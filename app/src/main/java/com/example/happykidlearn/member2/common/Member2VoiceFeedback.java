package com.example.happykidlearn.member2.common;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.happykidlearn.utils.SoundManager;

import java.util.ArrayDeque;
import java.util.Locale;
import java.util.Random;

/**
 * Phản hồi bằng giọng nói tiếng Việt (khen / động viên / chúc mừng) cho 4 mini-game Thành viên 2.
 *
 * Nguyên tắc:
 * - Dùng chung engine TextToSpeech của {@link Member2SpeechHelper} (không tạo hệ thống âm thanh thứ hai).
 * - Không phát khi người dùng đã tắt âm thanh ({@code SoundManager.isSfxMuted()}).
 * - Chuỗi phát tuần tự (ví dụ: đọc từ tiếng Anh xong mới khen tiếng Việt) dựa trên callback onDone,
 *   nên không bao giờ chồng tiếng.
 * - Chống phát lặp khi bấm liên tục (cooldown theo nhóm câu).
 * - Bỏ qua câu tiếng Việt an toàn nếu thiết bị không có giọng vi-VN (không crash, không đọc sai giọng).
 * - Activity phải gọi {@link #stop()} ở onStop và {@link #release()} ở onDestroy.
 */
public final class Member2VoiceFeedback {

    private static final String TAG = Member2SoundEffects.TAG;
    private static final String UTTERANCE_PREFIX = "m2_voice_";
    private static final long COMPLETION_DELAY_MS = 1300L;
    private static final long STEP_WATCHDOG_BASE_MS = 2500L;
    private static final long STEP_WATCHDOG_PER_CHAR_MS = 110L;
    private static final long STEP_WATCHDOG_MAX_MS = 10000L;

    private static final class Step {
        final String text;
        final Locale locale;

        Step(@NonNull String text, @NonNull Locale locale) {
            this.text = text;
            this.locale = locale;
        }
    }

    private final SoundManager soundManager;
    private final Member2SpeechHelper speechHelper;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();
    private final int[] lastPhraseIndex = new int[]{-1, -1, -1};
    private final ArrayDeque<Step> pendingSteps = new ArrayDeque<>();

    @Nullable
    private String currentUtteranceId;
    private int utteranceCounter = 0;
    @Nullable
    private Member2VoicePhrases.Category lastCategory;
    private long lastCategoryAtMs = 0L;
    private boolean released = false;

    private final Runnable delayedCompletionRunnable = this::speakCompletionNow;
    private final Runnable stepWatchdogRunnable = () -> {
        // Nếu utterance bị một giọng đọc khác ngắt (QUEUE_FLUSH) thì không có onDone;
        // tự giải phóng trạng thái để không kẹt chuỗi phản hồi.
        if (currentUtteranceId != null) {
            Log.d(TAG, "Voice feedback watchdog: clearing interrupted utterance " + currentUtteranceId);
            currentUtteranceId = null;
            pendingSteps.clear();
        }
    };

    public Member2VoiceFeedback(@NonNull Context context) {
        Context appContext = context.getApplicationContext();
        this.soundManager = SoundManager.getInstance(appContext);
        this.speechHelper = new Member2SpeechHelper(appContext, Member2SpeechHelper.LOCALE_VIETNAMESE);
        this.speechHelper.setSpeechListener(new Member2SpeechHelper.SpeechListener() {
            @Override
            public void onSpeechDone(String utteranceId) {
                mainHandler.post(() -> handleUtteranceDone(utteranceId));
            }

            @Override
            public void onSpeechUnavailable(String reason) {
                Log.w(TAG, "Voice feedback: speech unavailable -> " + reason);
            }
        });
    }

    /** Khen ngợi khi trẻ trả lời đúng. */
    public void speakCorrect() {
        startCategory(Member2VoicePhrases.Category.CORRECT, null, null);
    }

    /** Động viên nhẹ nhàng khi trẻ trả lời chưa đúng. */
    public void speakWrong() {
        startCategory(Member2VoicePhrases.Category.WRONG, null, null);
    }

    /**
     * Đọc nội dung chính trước (ví dụ: từ vừa ghép, hoặc từ tiếng Anh với locale en-US),
     * sau khi đọc xong mới phát lời khen tiếng Việt. Không bao giờ chồng tiếng.
     */
    public void speakTextThenCorrect(@NonNull String leadText, @NonNull Locale leadLocale) {
        startCategory(Member2VoicePhrases.Category.CORRECT, leadText, leadLocale);
    }

    /**
     * Chúc mừng hoàn thành bài học.
     * Nếu đang phát một chuỗi phản hồi, câu chúc mừng được nối vào cuối chuỗi;
     * nếu không, phát sau một nhịp ngắn để không cắt ngang âm đọc vừa bắt đầu.
     */
    public void speakCompletion() {
        if (!canSpeak()) {
            return;
        }
        if (currentUtteranceId != null) {
            if (!passCooldown(Member2VoicePhrases.Category.COMPLETE)) {
                return;
            }
            String phrase = nextPhrase(Member2VoicePhrases.Category.COMPLETE);
            pendingSteps.addLast(new Step(phrase, Member2SpeechHelper.LOCALE_VIETNAMESE));
            Log.i(TAG, "Voice feedback: appended COMPLETE \"" + phrase + "\" to active sequence");
            return;
        }
        mainHandler.removeCallbacks(delayedCompletionRunnable);
        mainHandler.postDelayed(delayedCompletionRunnable, COMPLETION_DELAY_MS);
    }

    /** Chúc mừng ngay lập tức (dùng khi chắc chắn không có giọng đọc nào khác đang phát). */
    public void speakCompletionNow() {
        startCategory(Member2VoicePhrases.Category.COMPLETE, null, null);
    }

    /** Dừng mọi phản hồi đang/chuẩn bị phát (gọi khi rời màn hình, đổi trang, tắt tiếng). */
    public void stop() {
        cancelPending();
        speechHelper.stop();
    }

    /** Giải phóng tài nguyên, gọi ở onDestroy. */
    public void release() {
        stop();
        released = true;
        speechHelper.shutdown();
    }

    private void startCategory(
            @NonNull Member2VoicePhrases.Category category,
            @Nullable String leadText,
            @Nullable Locale leadLocale
    ) {
        if (!canSpeak()) {
            Log.d(TAG, "Voice feedback " + category + " skipped (muted or released)");
            return;
        }
        if (!passCooldown(category)) {
            Log.d(TAG, "Voice feedback " + category + " skipped by cooldown (anti-spam)");
            return;
        }
        cancelPending();
        if (leadText != null && leadLocale != null && !leadText.trim().isEmpty()) {
            pendingSteps.addLast(new Step(leadText, leadLocale));
        }
        String phrase = nextPhrase(category);
        pendingSteps.addLast(new Step(phrase, Member2SpeechHelper.LOCALE_VIETNAMESE));
        Log.i(TAG, "Voice feedback " + category + " -> \"" + phrase + "\""
                + (leadText != null ? " (after lead \"" + leadText + "\")" : ""));
        speakNextStep();
    }

    private boolean passCooldown(@NonNull Member2VoicePhrases.Category category) {
        long now = SystemClock.elapsedRealtime();
        if (!Member2VoicePhrases.passesCooldown(category, now, lastCategory, lastCategoryAtMs)) {
            return false;
        }
        lastCategory = category;
        lastCategoryAtMs = now;
        return true;
    }

    @NonNull
    private String nextPhrase(@NonNull Member2VoicePhrases.Category category) {
        java.util.List<String> phrases = Member2VoicePhrases.forCategory(category);
        int slot = category.ordinal();
        int idx = Member2VoicePhrases.pickIndex(phrases.size(), lastPhraseIndex[slot], random);
        lastPhraseIndex[slot] = idx;
        return phrases.get(idx);
    }

    private boolean canSpeak() {
        return !released && (soundManager == null || !soundManager.isSfxMuted());
    }

    private void speakNextStep() {
        mainHandler.removeCallbacks(stepWatchdogRunnable);
        if (!canSpeak()) {
            cancelPending();
            return;
        }
        Step step = pendingSteps.pollFirst();
        while (step != null && !Member2SpeechHelper.isLocaleSupported(step.locale)) {
            Log.w(TAG, "Voice feedback: locale " + step.locale
                    + " not supported on this device; skipping \"" + step.text + "\" safely");
            step = pendingSteps.pollFirst();
        }
        if (step == null) {
            currentUtteranceId = null;
            return;
        }
        String id = UTTERANCE_PREFIX + (++utteranceCounter);
        currentUtteranceId = id;
        boolean started = speechHelper.speak(step.text, step.locale, id);
        if (!started) {
            currentUtteranceId = null;
            pendingSteps.clear();
            return;
        }
        long watchdog = Math.min(
                STEP_WATCHDOG_MAX_MS,
                STEP_WATCHDOG_BASE_MS + STEP_WATCHDOG_PER_CHAR_MS * step.text.length()
        );
        mainHandler.postDelayed(stepWatchdogRunnable, watchdog);
    }

    private void handleUtteranceDone(@Nullable String utteranceId) {
        if (released || utteranceId == null || !utteranceId.equals(currentUtteranceId)) {
            return;
        }
        speakNextStep();
    }

    private void cancelPending() {
        mainHandler.removeCallbacks(delayedCompletionRunnable);
        mainHandler.removeCallbacks(stepWatchdogRunnable);
        pendingSteps.clear();
        currentUtteranceId = null;
    }
}

