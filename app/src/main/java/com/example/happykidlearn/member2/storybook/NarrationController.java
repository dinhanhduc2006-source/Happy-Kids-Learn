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

/**
 * Bộ điều khiển kể chuyện tự động cho Storybook ("Cuốn Sách Kỳ Diệu").
 *
 * Đặc điểm cải tiến cho trẻ 4–8 tuổi:
 * 1. Tốc độ đọc chậm rãi, truyền cảm: {@link #NARRATION_SPEECH_RATE} = 0.76f (nằm trong khoảng 0.75–0.80),
 *    chỉ áp dụng riêng cho phần kể nội dung truyện, không làm chậm các mini-game khác.
 * 2. Ngắt nghỉ tự nhiên như kể chuyện trước giờ đi ngủ (sử dụng {@link Handler#postDelayed}, tuyệt đối
 *    không dùng Thread.sleep trên UI thread):
 *    - Sau dấu phẩy (',' / ';'): nghỉ {@link #COMMA_PAUSE_MS} = 280 ms (200–350 ms).
 *    - Sau dấu kết câu ('.' / '!' / '?'): nghỉ {@link #SENTENCE_PAUSE_MS} = 500 ms (400–600 ms).
 *    - Khi chuyển đoạn ('\n'): nghỉ {@link #PARAGRAPH_PAUSE_MS} = 700 ms (600–900 ms).
 * 3. Đồng bộ tô sáng (highlight) chính xác cụm từ/câu đang được đọc trên trang sách.
 * 4. Hủy ngay lập tức mọi hàng đợi và callback khi chuyển trang, tạm dừng hoặc thoát truyện.
 */
public class NarrationController {

    /** Khoảng nghỉ tự nhiên sau dấu phẩy (200–350 ms). */
    public static final long COMMA_PAUSE_MS = 280L;
    /** Khoảng nghỉ tự nhiên sau dấu chấm kết câu (400–600 ms). */
    public static final long SENTENCE_PAUSE_MS = 500L;
    /** Khoảng nghỉ tự nhiên khi chuyển đoạn văn (600–900 ms). */
    public static final long PARAGRAPH_PAUSE_MS = 700L;

    /** Tốc độ đọc truyện chậm rãi, dễ theo dõi cho trẻ 4–8 tuổi (0.75–0.80). */
    public static final float NARRATION_SPEECH_RATE = Member2SpeechHelper.STORY_NARRATION_SPEECH_RATE;
    /** Cao độ giọng kể chuyện ấm áp, dịu dàng. */
    public static final float NARRATION_PITCH = Member2SpeechHelper.STORY_NARRATION_PITCH;

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

    /**
     * Phân đoạn cụm từ/vế câu phục vụ đọc chậm có ngắt nghỉ tự nhiên sau dấu phẩy và dấu chấm.
     */
    public static final class ClauseSegment {
        private final int clauseIndex;
        private final String clauseText;
        private final int startCharIndex;
        private final int endCharIndex;
        private final long pauseAfterMs;

        public ClauseSegment(
                int clauseIndex,
                @NonNull String clauseText,
                int startCharIndex,
                int endCharIndex,
                long pauseAfterMs
        ) {
            this.clauseIndex = clauseIndex;
            this.clauseText = clauseText;
            this.startCharIndex = startCharIndex;
            this.endCharIndex = endCharIndex;
            this.pauseAfterMs = Math.max(0L, pauseAfterMs);
        }

        public int getClauseIndex() {
            return clauseIndex;
        }

        @NonNull
        public String getClauseText() {
            return clauseText;
        }

        public int getStartCharIndex() {
            return startCharIndex;
        }

        public int getEndCharIndex() {
            return endCharIndex;
        }

        public long getPauseAfterMs() {
            return pauseAfterMs;
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
    private List<ClauseSegment> activeClauses = Collections.emptyList();
    private int currentClauseIndex = 0;
    private boolean isPlaying = false;
    private boolean isPaused = false;

    private final Runnable nextClauseRunnable = () -> {
        if (!isPlaying || isPaused) {
            return;
        }
        speakCurrentClause();
    };

    public NarrationController(@NonNull Context context) {
        this.speechHelper = new Member2SpeechHelper(context, Member2SpeechHelper.LOCALE_VIETNAMESE);
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.speechHelper.setSpeechListener(new Member2SpeechHelper.SpeechListener() {
            @Override
            public void onSpeechStarted(String utteranceId) {
                int clauseIdx = parseClauseIndex(utteranceId);
                if (clauseIdx >= 0 && clauseIdx < activeClauses.size()) {
                    ClauseSegment seg = activeClauses.get(clauseIdx);
                    mainHandler.post(() -> {
                        if (listener != null && isPlaying) {
                            listener.onHighlightChanged(activePageIndex, seg.getStartCharIndex(), seg.getEndCharIndex());
                        }
                    });
                }
            }

            @Override
            public void onSpeechRange(String utteranceId, int start, int end) {
                int clauseIdx = parseClauseIndex(utteranceId);
                if (clauseIdx >= 0 && clauseIdx < activeClauses.size()) {
                    ClauseSegment seg = activeClauses.get(clauseIdx);
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
                int clauseIdx = parseClauseIndex(utteranceId);
                mainHandler.post(() -> {
                    if (!isPlaying || isPaused || clauseIdx != currentClauseIndex) {
                        return;
                    }
                    if (currentClauseIndex < activeClauses.size() - 1) {
                        long pauseMs = activeClauses.get(currentClauseIndex).getPauseAfterMs();
                        currentClauseIndex++;
                        mainHandler.removeCallbacks(nextClauseRunnable);
                        if (pauseMs > 0L) {
                            mainHandler.postDelayed(nextClauseRunnable, pauseMs);
                        } else {
                            speakCurrentClause();
                        }
                    } else {
                        int finishedPage = activePageIndex;
                        isPlaying = false;
                        isPaused = false;
                        currentClauseIndex = 0;
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
                    mainHandler.removeCallbacks(nextClauseRunnable);
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

    /**
     * Tách văn bản trang truyện thành danh sách câu hoàn chỉnh (dựa trên '.', '!', '?').
     */
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

    /**
     * Tách văn bản trang truyện thành các cụm từ/vế câu có khoảng nghỉ tự nhiên:
     * - Sau dấu phẩy (',' / ';'): nghỉ {@link #COMMA_PAUSE_MS} (280 ms).
     * - Sau dấu chấm kết câu ('.' / '!' / '?'): nghỉ {@link #SENTENCE_PAUSE_MS} (500 ms).
     * - Khi xuống dòng chuyển đoạn ('\n'): nghỉ {@link #PARAGRAPH_PAUSE_MS} (700 ms).
     *
     * Ví dụ:
     * "Ngày xửa ngày xưa, trong một khu rừng xanh tươi, có một chú thỏ nhỏ rất đáng yêu."
     * -> ["Ngày xửa ngày xưa," (280ms), "trong một khu rừng xanh tươi," (280ms), "có một chú thỏ nhỏ rất đáng yêu." (500ms)]
     */
    public static List<ClauseSegment> splitIntoNarrationClauses(@NonNull String pageText) {
        List<ClauseSegment> clauses = new ArrayList<>();
        int length = pageText.length();
        int segStart = 0;

        while (segStart < length && Character.isWhitespace(pageText.charAt(segStart))) {
            segStart++;
        }

        int i = segStart;
        while (i < length) {
            char ch = pageText.charAt(i);
            boolean isCommaBreak = (ch == ',' || ch == ';');
            boolean isSentenceBreak = (ch == '.' || ch == '!' || ch == '?');
            boolean isLineBreak = (ch == '\n');

            if (isCommaBreak || isSentenceBreak || isLineBreak) {
                int segEnd = isLineBreak ? i : (i + 1);
                String sub = pageText.substring(segStart, segEnd).trim();

                boolean followedByNewline = isLineBreak;
                int nextIdx = i + 1;
                while (nextIdx < length && Character.isWhitespace(pageText.charAt(nextIdx))) {
                    if (pageText.charAt(nextIdx) == '\n') {
                        followedByNewline = true;
                    }
                    nextIdx++;
                }

                long pauseMs;
                if (followedByNewline) {
                    pauseMs = PARAGRAPH_PAUSE_MS;
                } else if (isSentenceBreak) {
                    pauseMs = SENTENCE_PAUSE_MS;
                } else {
                    pauseMs = COMMA_PAUSE_MS;
                }

                if (!sub.isEmpty()) {
                    clauses.add(new ClauseSegment(clauses.size(), sub, segStart, segEnd, pauseMs));
                }
                segStart = nextIdx;
                i = segStart;
            } else {
                i++;
            }
        }

        if (segStart < length) {
            String remainder = pageText.substring(segStart).trim();
            if (!remainder.isEmpty()) {
                clauses.add(new ClauseSegment(clauses.size(), remainder, segStart, length, SENTENCE_PAUSE_MS));
            }
        }

        return Collections.unmodifiableList(clauses);
    }

    public void togglePlayPause(int pageIndex, @NonNull String pageText) {
        if (activePageIndex != pageIndex || activeClauses.isEmpty()) {
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
        mainHandler.removeCallbacks(nextClauseRunnable);
        speechHelper.stop();
        activePageIndex = pageIndex;
        activeClauses = splitIntoNarrationClauses(pageText);
        currentClauseIndex = 0;

        if (activeClauses.isEmpty()) {
            return;
        }

        isPlaying = true;
        isPaused = false;
        if (listener != null) {
            listener.onNarrationStateChanged(true, false);
        }
        speakCurrentClause();
    }

    public void pause() {
        if (!isPlaying) {
            return;
        }
        mainHandler.removeCallbacks(nextClauseRunnable);
        isPlaying = false;
        isPaused = true;
        speechHelper.stop();
        if (listener != null) {
            listener.onNarrationStateChanged(false, true);
        }
    }

    public void resume() {
        if (!isPaused || activeClauses.isEmpty()) {
            return;
        }
        mainHandler.removeCallbacks(nextClauseRunnable);
        isPlaying = true;
        isPaused = false;
        if (listener != null) {
            listener.onNarrationStateChanged(true, false);
        }
        speakCurrentClause();
    }

    public void stop() {
        mainHandler.removeCallbacks(nextClauseRunnable);
        boolean wasActive = isPlaying || isPaused;
        int prevPage = activePageIndex;
        isPlaying = false;
        isPaused = false;
        currentClauseIndex = 0;
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

    private void speakCurrentClause() {
        if (currentClauseIndex < 0 || currentClauseIndex >= activeClauses.size()) {
            return;
        }
        ClauseSegment seg = activeClauses.get(currentClauseIndex);
        if (listener != null) {
            listener.onHighlightChanged(activePageIndex, seg.getStartCharIndex(), seg.getEndCharIndex());
        }
        boolean started = speechHelper.speak(
                seg.getClauseText(),
                Member2SpeechHelper.LOCALE_VIETNAMESE,
                UTTERANCE_PREFIX + currentClauseIndex,
                NARRATION_SPEECH_RATE,
                NARRATION_PITCH
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

    private int parseClauseIndex(String utteranceId) {
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
