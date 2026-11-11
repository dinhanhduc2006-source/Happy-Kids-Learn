package com.example.happykidlearn.member2.storybook;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.happykidlearn.member2.common.Member2SpeechHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NarrationController {

    public static final class SentenceSegment {
        private final int sentenceIndex;
        private final String sentenceText;
        private final int startCharIndex;
        private final int endCharIndex;

        public SentenceSegment(int sentenceIndex, @NonNull String sentenceText, int startCharIndex, int endCharIndex) {
            this.sentenceIndex = sentenceIndex;
            this.sentenceText = sentenceText;
            this.startCharIndex = startCharIndex;
            this.endCharIndex = endCharIndex;
        }

        public int getSentenceIndex() {
            return sentenceIndex;
        }

        @NonNull
        public String getSentenceText() {
            return sentenceText;
        }

        public int getStartCharIndex() {
            return startCharIndex;
        }

        public int getEndCharIndex() {
            return endCharIndex;
        }
    }

    public interface Listener {
        void onNarrationStateChanged(boolean isPlaying, boolean isPaused);

        void onHighlightChanged(int pageIndex, int startChar, int endChar);

        void onPageNarrationCompleted(int pageIndex);

        void onNarrationUnavailable(String reason);
    }

    private static final String UTTERANCE_PREFIX = "story_sent_";

    private final Member2SpeechHelper speechHelper;
    private final Handler mainHandler;
    private Listener listener;

    private int activePageIndex = -1;
    private List<SentenceSegment> activeSentences = Collections.emptyList();
    private int currentSentenceIndex = 0;
    private boolean isPlaying = false;
    private boolean isPaused = false;

    public NarrationController(@NonNull Context context) {
        this.speechHelper = new Member2SpeechHelper(context, Member2SpeechHelper.LOCALE_VIETNAMESE);
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.speechHelper.setSpeechListener(new Member2SpeechHelper.SpeechListener() {
            @Override
            public void onSpeechStarted(String utteranceId) {
                int sentIdx = parseSentenceIndex(utteranceId);
                if (sentIdx >= 0 && sentIdx < activeSentences.size()) {
                    SentenceSegment seg = activeSentences.get(sentIdx);
                    mainHandler.post(() -> {
                        if (listener != null && isPlaying) {
                            listener.onHighlightChanged(activePageIndex, seg.getStartCharIndex(), seg.getEndCharIndex());
                        }
                    });
                }
            }

            @Override
            public void onSpeechRange(String utteranceId, int start, int end) {
                int sentIdx = parseSentenceIndex(utteranceId);
                if (sentIdx >= 0 && sentIdx < activeSentences.size()) {
                    SentenceSegment seg = activeSentences.get(sentIdx);
                    final int absStart = Math.max(seg.getStartCharIndex(), seg.getStartCharIndex() + start);
                    final int absEnd = Math.min(seg.getEndCharIndex(), seg.getStartCharIndex() + end);
                    if (absEnd > absStart) {
                        mainHandler.post(() -> {
                            if (listener != null && isPlaying) {
                                listener.onHighlightChanged(activePageIndex, absStart, absEnd);
                            }
                        });
                    }
                }
            }

            @Override
            public void onSpeechDone(String utteranceId) {
                int sentIdx = parseSentenceIndex(utteranceId);
                mainHandler.post(() -> {
                    if (!isPlaying || isPaused || sentIdx != currentSentenceIndex) {
                        return;
                    }
                    if (currentSentenceIndex < activeSentences.size() - 1) {
                        currentSentenceIndex++;
                        speakCurrentSentence();
                    } else {
                        int finishedPage = activePageIndex;
                        isPlaying = false;
                        isPaused = false;
                        currentSentenceIndex = 0;
                        if (listener != null) {
                            listener.onHighlightChanged(finishedPage, -1, -1);
                            listener.onNarrationStateChanged(false, false);
                            listener.onPageNarrationCompleted(finishedPage);
                        }
                    }
                });
            }

            @Override
            public void onSpeechUnavailable(String reason) {
                mainHandler.post(() -> {
                    isPlaying = false;
                    isPaused = false;
                    if (listener != null) {
                        listener.onNarrationStateChanged(false, false);
                        listener.onNarrationUnavailable(reason);
                    }
                });
            }
        });
    }

    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
    }

    public static List<SentenceSegment> splitIntoSentences(@NonNull String pageText) {
        List<SentenceSegment> segments = new ArrayList<>();
        int length = pageText.length();
        int segStart = 0;

        while (segStart < length && Character.isWhitespace(pageText.charAt(segStart))) {
            segStart++;
        }

        int i = segStart;
        while (i < length) {
            char ch = pageText.charAt(i);
            boolean isSentenceBreak = (ch == '.' || ch == '!' || ch == '?');
            if (isSentenceBreak) {
                int segEnd = i + 1;
                String sub = pageText.substring(segStart, segEnd).trim();
                if (!sub.isEmpty()) {
                    segments.add(new SentenceSegment(segments.size(), sub, segStart, segEnd));
                }
                segStart = segEnd;
                while (segStart < length && Character.isWhitespace(pageText.charAt(segStart))) {
                    segStart++;
                }
                i = segStart;
            } else {
                i++;
            }
        }

        if (segStart < length) {
            String remainder = pageText.substring(segStart).trim();
            if (!remainder.isEmpty()) {
                segments.add(new SentenceSegment(segments.size(), remainder, segStart, length));
            }
        }

        return Collections.unmodifiableList(segments);
    }

    public void togglePlayPause(int pageIndex, @NonNull String pageText) {
        if (activePageIndex != pageIndex || activeSentences.isEmpty()) {
            startFromBeginning(pageIndex, pageText);
            return;
        }

        if (isPlaying) {
            pause();
        } else if (isPaused) {
            resume();
        } else {
            startFromBeginning(pageIndex, pageText);
        }
    }

    public void startFromBeginning(int pageIndex, @NonNull String pageText) {
        speechHelper.stop();
        activePageIndex = pageIndex;
        activeSentences = splitIntoSentences(pageText);
        currentSentenceIndex = 0;

        if (activeSentences.isEmpty()) {
            return;
        }

        isPlaying = true;
        isPaused = false;
        if (listener != null) {
            listener.onNarrationStateChanged(true, false);
        }
        speakCurrentSentence();
    }

    public void pause() {
        if (!isPlaying) {
            return;
        }
        isPlaying = false;
        isPaused = true;
        speechHelper.stop();
        if (listener != null) {
            listener.onNarrationStateChanged(false, true);
        }
    }

    public void resume() {
        if (!isPaused || activeSentences.isEmpty()) {
            return;
        }
        isPlaying = true;
        isPaused = false;
        if (listener != null) {
            listener.onNarrationStateChanged(true, false);
        }
        speakCurrentSentence();
    }

    public void stop() {
        boolean wasActive = isPlaying || isPaused;
        int prevPage = activePageIndex;
        isPlaying = false;
        isPaused = false;
        currentSentenceIndex = 0;
        speechHelper.stop();
        if (wasActive && listener != null) {
            listener.onHighlightChanged(prevPage, -1, -1);
            listener.onNarrationStateChanged(false, false);
        }
    }

    public void shutdown() {
        stop();
        speechHelper.shutdown();
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isPaused() {
        return isPaused;
    }

    private void speakCurrentSentence() {
        if (currentSentenceIndex < 0 || currentSentenceIndex >= activeSentences.size()) {
            return;
        }
        SentenceSegment seg = activeSentences.get(currentSentenceIndex);
        if (listener != null) {
            listener.onHighlightChanged(activePageIndex, seg.getStartCharIndex(), seg.getEndCharIndex());
        }
        boolean started = speechHelper.speak(
                seg.getSentenceText(),
                Member2SpeechHelper.LOCALE_VIETNAMESE,
                UTTERANCE_PREFIX + currentSentenceIndex
        );
        if (!started) {
            isPlaying = false;
            isPaused = false;
            if (listener != null) {
                listener.onHighlightChanged(activePageIndex, -1, -1);
                listener.onNarrationStateChanged(false, false);
            }
        }
    }

    private int parseSentenceIndex(String utteranceId) {
        if (utteranceId == null || !utteranceId.startsWith(UTTERANCE_PREFIX)) {
            return -1;
        }
        try {
            return Integer.parseInt(utteranceId.substring(UTTERANCE_PREFIX.length()));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}

