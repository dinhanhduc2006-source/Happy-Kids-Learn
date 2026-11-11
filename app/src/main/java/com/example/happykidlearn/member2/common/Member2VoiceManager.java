package com.example.happykidlearn.member2.common;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.happykidlearn.R;
import com.example.happykidlearn.member2.english.EnglishContent;
import com.example.happykidlearn.utils.SoundManager;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Hệ thống trợ lý giọng nói thông minh "Sao Bông – Người bạn đồng hành của bé"
 * dùng chung cho toàn bộ phân hệ Thành viên 2 (Home Dashboard & 4 Mini-game).
 *
 * Đặc điểm thiết kế:
 * 1. Tái sử dụng thực thể TextToSpeech chung của {@link Member2SpeechHelper} (không tạo nhiều engine).
 * 2. Hỗ trợ cơ chế Prerecorded Audio Resolver: tự động kiểm tra file âm thanh thu sẵn trong
 *    {@code res/raw/m2_voice_<key>} trước khi fallback sang TTS, cho phép nâng cấp giọng thu âm thật
 *    bất kỳ lúc nào mà không cần sửa logic mini-game.
 * 3. Quản lý hàng đợi phát âm tuần tự có độ trễ giữa các bước (ví dụ: giải thích tiếng Việt ->
 *    đọc từ tiếng Anh -> nghỉ 550ms -> đọc lại từ tiếng Anh) không bao giờ chồng tiếng.
 * 4. Hệ thống 4 mức độ ưu tiên âm thanh (Knowledge/Question > Action Instruction > Feedback > Greeting/Idle).
 * 5. Quản lý cài đặt "Giọng nói hướng dẫn: BẬT / TẮT" lưu trong SharedPreferences.
 * 6. Cơ chế tự động nhắc nhở thông minh khi bé không thao tác sau 12 giây (tối đa 1 lần mỗi lượt).
 * 7. Đồng bộ hiệu ứng nhún nhẹ của linh vật Sao Bông và nút loa khi đang phát lời thoại.
 */
public final class Member2VoiceManager {

    private static final String TAG = Member2SoundEffects.TAG;
    private static final String PREF_NAME = "m2_member2_item_progress";
    public static final String KEY_VOICE_GUIDANCE_ENABLED = "voice_guidance_enabled";
    private static final String UTTERANCE_PREFIX = "m2_saobong_";

    /** Thời gian chờ không tương tác trước khi Sao Bông hỏi thăm (12 giây, nằm trong khoảng 10–15s). */
    public static final long IDLE_REMINDER_DELAY_MS = 12000L;
    private static final long STEP_WATCHDOG_BASE_MS = 2800L;
    private static final long STEP_WATCHDOG_PER_CHAR_MS = 115L;
    private static final long STEP_WATCHDOG_MAX_MS = 11000L;

    public static final Locale LOCALE_VI = Member2SpeechHelper.LOCALE_VIETNAMESE;
    public static final Locale LOCALE_EN = Member2SpeechHelper.LOCALE_ENGLISH_US;

    // =========================================================================
    // KỊCH BẢN LỜI THOẠI CHUẨN CỦA TRỢ LÝ SAO BÔNG CHO 4 MINI-GAME
    // =========================================================================

    // Mini-game 1: Alphabet (Bảng chữ cái tiếng Việt)
    public static final String ALPHABET_GREETING_1 =
            "Xin chào bé yêu! Sao Bông sẽ cùng bé khám phá những chữ cái thật thú vị nhé!";
    public static final String ALPHABET_GREETING_2 =
            "Bé hãy chạm vào từng chữ cái trên màn hình để nghe Sao Bông đọc nhé!";
    public static final String ALPHABET_LEARN_SCREEN_GUIDE =
            "Bé hãy chạm vào từng chữ cái trên màn hình để nghe Sao Bông đọc nhé!";
    public static final String ALPHABET_CARD_TAP_GUIDE =
            "Bé hãy chạm vào tấm thẻ chữ để nghe cách phát âm nha!";
    public static final String ALPHABET_ILLUSTRATION_GUIDE =
            "Bé hãy chạm vào hình ảnh để khám phá điều thú vị nhé!";
    public static final String ALPHABET_FLASHCARD_PROMPT =
            "Bé hãy chạm vào tấm thẻ để lật sang mặt còn lại nhé!";
    public static final String ALPHABET_FLASHCARD_FLIP_GUIDE =
            "Bé hãy chạm vào tấm thẻ để lật sang mặt còn lại nhé!";
    public static final String ALPHABET_FLASHCARD_FLIP_PRAISE =
            "Giỏi lắm! Bé vừa khám phá một chữ cái mới!";
    public static final String ALPHABET_CHALLENGE_INTRO =
            "Bây giờ mình cùng chơi trò nghe và tìm chữ nhé!";
    public static final String ALPHABET_CHALLENGE_PROMPT =
            "Bé hãy lắng nghe chữ Sao Bông đọc, sau đó chạm vào chữ cái đúng trên màn hình nhé!";
    public static final String ALPHABET_REPLAY_SPEAKER_GUIDE =
            "Bé có thể chạm vào nút hình chiếc loa để nghe lại nha!";
    public static final String ALPHABET_CHALLENGE_CORRECT =
            "Chính xác! Bé giỏi quá!";
    public static final String ALPHABET_CHALLENGE_WRONG =
            "Chưa đúng rồi, bé thử lại một lần nữa nhé!";

    // Mini-game 2: Spelling (Ghép vần tiếng Việt)
    public static final String SPELLING_GREETING_1 =
            "Chào bé! Hôm nay Sao Bông sẽ cùng bé ghép những chữ cái thành từ thật hay nhé!";
    public static final String SPELLING_GREETING_2 =
            "Bé hãy chạm vào các chữ cái ở phía dưới để ghép thành từ đúng nhé!";
    public static final String SPELLING_LOOK_TOP_GUIDE =
            "Bé hãy nhìn từ cần ghép ở phía trên nhé!";
    public static final String SPELLING_TAP_BOTTOM_GUIDE =
            "Bé hãy chạm vào các chữ cái ở phía dưới để ghép thành từ đúng nhé!";
    public static final String SPELLING_DRAG_SLOT_GUIDE =
            "Bé hãy kéo từng chữ cái vào ô trống để hoàn thành từ nhé!";
    public static final String SPELLING_NEXT_LETTER_SHORT_GUIDE =
            "Tiếp theo bé chọn chữ cái còn thiếu nhé!";
    public static final String SPELLING_HINT_BULB_GUIDE =
            "Bé hãy chạm vào hình bóng đèn để Sao Bông giúp nhé!";
    public static final String SPELLING_CORRECT_PRAISE =
            "Hoan hô! Bé đã ghép đúng rồi! Bây giờ mình cùng đọc từ này nhé!";
    public static final String SPELLING_COMPLETE_PRAISE =
            "Hoan hô! Bé đã ghép đúng rồi! Bây giờ mình cùng đọc từ này nhé!";
    public static final String SPELLING_WRONG_PROMPT =
            "Suýt đúng rồi! Bé hãy thử lại nhé!";

    // Mini-game 3: English (Tiếng Anh cho bé)
    public static final String ENGLISH_GREETING_1 =
            "Hello bé yêu! Hôm nay Sao Bông sẽ cùng bé học những từ tiếng Anh thật thú vị nhé!";
    public static final String ENGLISH_GREETING_2 =
            "Bé hãy nhìn hình, lắng nghe và chọn đáp án đúng nha!";
    public static final String ENGLISH_TOPIC_ANIMALS_GUIDE =
            "Bé hãy chạm vào hình con vật để nghe tên tiếng Anh của nó nhé!";
    public static final String ENGLISH_TOPIC_COLORS_GUIDE =
            "Bé hãy chạm vào từng màu để nghe Sao Bông đọc bằng tiếng Anh nhé!";
    public static final String ENGLISH_TOPIC_NUMBERS_GUIDE =
            "Bé hãy chạm vào các con số để nghe cách đọc tiếng Anh nha!";
    public static final String ENGLISH_TOPIC_FAMILY_GUIDE =
            "Bé hãy chạm vào hình các thành viên gia đình để nghe cách đọc tiếng Anh nhé!";
    public static final String ENGLISH_QUIZ_PROMPT =
            "Bé hãy nghe từ tiếng Anh, sau đó chạm vào hình ảnh đúng nhé!";
    public static final String ENGLISH_REPLAY_SPEAKER_GUIDE =
            "Nếu chưa nghe rõ, bé hãy chạm vào nút loa để nghe lại nhé!";
    public static final String ENGLISH_CORRECT_EN =
            "Wonderful!";
    public static final String ENGLISH_CORRECT_VI =
            "Bé làm đúng rồi!";
    public static final String ENGLISH_WRONG_VI =
            "Không sao đâu! Mình thử lại nhé!";

    // Mini-game 4: Storybook (Đọc truyện tương tác)
    public static final String STORYBOOK_GREETING_1 =
            "Xin chào bé! Sao Bông sẽ kể cho bé nghe những câu chuyện thật hay nhé!";
    public static final String STORYBOOK_GREETING_2 =
            "Bé hãy chạm vào bìa cuốn truyện mình thích để Sao Bông kể chuyện nhé!";
    public static final String STORYBOOK_LIBRARY_GUIDE =
            "Bé hãy chạm vào bìa cuốn truyện mình thích để Sao Bông kể chuyện nhé!";
    public static final String STORYBOOK_SELECT_STORY =
            "Bé hãy nhìn hình minh họa và cùng Sao Bông lắng nghe câu chuyện nha!";
    public static final String STORYBOOK_OPEN_STORY_GUIDE =
            "Bé hãy nhìn hình minh họa và cùng Sao Bông lắng nghe câu chuyện nha!";
    public static final String STORYBOOK_SPEAKER_BTN_GUIDE =
            "Bé hãy chạm vào nút hình chiếc loa để nghe kể chuyện nhé!";
    public static final String STORYBOOK_NEXT_PAGE_ARROW_GUIDE =
            "Bé hãy chạm vào mũi tên bên phải để xem trang tiếp theo nhé!";
    public static final String STORYBOOK_PREV_PAGE_ARROW_GUIDE =
            "Bé có thể chạm vào mũi tên bên trái để xem lại trang trước nha!";
    public static final String STORYBOOK_END_STORY =
            "Câu chuyện đến đây là hết rồi! Bé có thích câu chuyện này không?";

    // Nhắc nhở khi bé không thao tác
    public static final String IDLE_REMINDER_PHRASE =
            "Bé cần Sao Bông giúp không nào?";

    /**
     * Thứ tự ưu tiên âm thanh (Level 1 cao nhất -> Level 4 thấp nhất).
     */
    public enum Priority {
        /** 1. Âm thanh câu hỏi và phát âm kiến thức (Ưu tiên cao nhất). */
        KNOWLEDGE_AND_QUESTION(1),
        /** 2. Hướng dẫn cần thiết để thực hiện thao tác. */
        ACTION_INSTRUCTION(2),
        /** 3. Phản hồi đúng/sai. */
        FEEDBACK(3),
        /** 4. Lời chào và câu thoại phụ / nhắc nhở khi không thao tác. */
        GREETING_AND_IDLE(4);

        private final int level;

        Priority(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    }

    /**
     * Một bước phát âm trong chuỗi hướng dẫn đa bước của Sao Bông.
     */
    public static final class VoiceStep {
        private final String text;
        private final Locale locale;
        private final long preDelayMs;
        @Nullable
        private final String voiceKey;
        @Nullable
        private final Runnable onStepStart;

        public VoiceStep(@NonNull String text, @NonNull Locale locale) {
            this(text, locale, 0L, null, null);
        }

        public VoiceStep(@NonNull String text, @NonNull Locale locale, long preDelayMs) {
            this(text, locale, preDelayMs, null, null);
        }

        public VoiceStep(
                @NonNull String text,
                @NonNull Locale locale,
                long preDelayMs,
                @Nullable String voiceKey,
                @Nullable Runnable onStepStart
        ) {
            this.text = text;
            this.locale = locale;
            this.preDelayMs = Math.max(0L, preDelayMs);
            this.voiceKey = voiceKey;
            this.onStepStart = onStepStart;
        }

        @NonNull
        public String getText() {
            return text;
        }

        @NonNull
        public Locale getLocale() {
            return locale;
        }

        public long getPreDelayMs() {
            return preDelayMs;
        }

        @Nullable
        public String getVoiceKey() {
            return voiceKey;
        }

        @Nullable
        public Runnable getOnStepStart() {
            return onStepStart;
        }
    }

    private final Context appContext;
    private final SoundManager soundManager;
    private final Member2SpeechHelper speechHelper;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ArrayDeque<VoiceStep> pendingSteps = new ArrayDeque<>();
    private final List<VoiceStep> currentInstructionSteps = new ArrayList<>();

    @Nullable
    private Priority activePriority = null;
    @Nullable
    private String currentUtteranceId = null;
    @Nullable
    private Runnable onSequenceComplete = null;
    @Nullable
    private MediaPlayer prerecordedPlayer = null;
    private int utteranceCounter = 0;

    private boolean isResumed = false;
    private boolean isReleased = false;
    private boolean isPausedState = false;
    private boolean idleReminderSpokenInCurrentTurn = false;
    private final List<VoiceStep> pausedRemainingSteps = new ArrayList<>();

    @Nullable
    private ImageView boundMascotView;
    @Nullable
    private View boundReplaySpeakerButton;
    @Nullable
    private TextView boundGuidanceToggleView;
    @Nullable
    private ObjectAnimator mascotSpeakAnimator;

    private final Runnable stepWatchdogRunnable = () -> {
        if (currentUtteranceId != null) {
            Log.d(TAG, "SaoBong watchdog clearing interrupted utterance: " + currentUtteranceId);
            currentUtteranceId = null;
            speakNextStepInQueue();
        }
    };

    private final Runnable delayedNextStepRunnable = this::executeNextStepNow;

    private final Runnable idleReminderRunnable = new Runnable() {
        @Override
        public void run() {
            if (isReleased || !isResumed || idleReminderSpokenInCurrentTurn) {
                return;
            }
            if (!isGuidanceEnabled(appContext) || isMasterMuted()) {
                return;
            }
            if (isSpeaking() || Member2SpeechHelper.isEngineSpeaking()) {
                // Đang phát âm thanh khác -> dời lại 6 giây sau
                mainHandler.postDelayed(this, 6000L);
                return;
            }
            idleReminderSpokenInCurrentTurn = true;
            speakSequence(
                    Collections.singletonList(new VoiceStep(IDLE_REMINDER_PHRASE, LOCALE_VI, 0L, "idle_help", null)),
                    Priority.GREETING_AND_IDLE,
                    false,
                    null
            );
        }
    };

    public Member2VoiceManager(@NonNull Context context) {
        this.appContext = context.getApplicationContext();
        this.soundManager = SoundManager.getInstance(this.appContext);
        this.speechHelper = new Member2SpeechHelper(this.appContext, LOCALE_VI);
        this.speechHelper.setSpeechListener(new Member2SpeechHelper.SpeechListener() {
            @Override
            public void onSpeechStarted(String utteranceId) {
                mainHandler.post(() -> startMascotSpeakingEffect());
            }

            @Override
            public void onSpeechDone(String utteranceId) {
                mainHandler.post(() -> handleUtteranceDone(utteranceId));
            }

            @Override
            public void onSpeechUnavailable(String reason) {
                Log.w(TAG, "SaoBong TTS unavailable: " + reason);
            }
        });
    }

    // =========================================================================
    // CÁC HÀM TIỆN ÍCH THUẦN LOGIC (CÓ THỂ KIỂM THỬ UNIT TEST ĐỘC LẬP)
    // =========================================================================

    /**
     * Kiểm tra mức ưu tiên mới có được phép ngắt mức ưu tiên đang phát hay không.
     */
    public static boolean canPreempt(@Nullable Priority activePriority, @NonNull Priority incomingPriority) {
        if (activePriority == null) {
            return true;
        }
        return incomingPriority.getLevel() <= activePriority.getLevel();
    }

    @NonNull
    public static String buildAlphabetLetterIntro(@NonNull String upperLetter, @NonNull String spokenSound) {
        return "Đây là chữ " + upperLetter + ". Bé đọc cùng Sao Bông nào! " + spokenSound + "!";
    }

    @NonNull
    public static String buildSpellingQuestionPrompt(@NonNull String targetWord) {
        return "Bé hãy ghép các chữ cái để tạo thành từ "
                + targetWord.toUpperCase(LOCALE_VI) + " nào!";
    }

    @NonNull
    public static String buildSpellingHintPrompt(@NonNull String neededToken, int slotIndex) {
        String upper = neededToken.toUpperCase(LOCALE_VI);
        if (slotIndex <= 0) {
            return "Sao Bông gợi ý cho bé nhé! Mình bắt đầu bằng chữ " + upper + " nào!";
        }
        return "Sao Bông gợi ý cho bé nhé! Tiếp theo mình chọn chữ " + upper + " nào!";
    }

    @NonNull
    public static String buildEnglishVocabVietnameseIntro(
            @NonNull String topicId,
            @NonNull String rawVietnameseMeaning
    ) {
        String naturalMeaning = cleanVietnameseMeaningForSpeech(rawVietnameseMeaning);
        if (EnglishContent.TOPIC_COLORS.equalsIgnoreCase(topicId)
                || EnglishContent.TOPIC_NUMBERS.equalsIgnoreCase(topicId)) {
            return "Đây là " + naturalMeaning + "!";
        }
        return "Bé nhìn xem! Đây là " + naturalMeaning + "!";
    }

    @NonNull
    public static String cleanVietnameseMeaningForSpeech(@NonNull String rawMeaning) {
        String trimmed = rawMeaning.trim();
        switch (trimmed.toLowerCase(LOCALE_VI)) {
            case "số 1 (một)":
                return "số một";
            case "số 2 (hai)":
                return "số hai";
            case "số 3 (ba)":
                return "số ba";
            case "số 4 (bốn)":
                return "số bốn";
            case "số 5 (năm)":
                return "số năm";
            case "số 6 (sáu)":
                return "số sáu";
            case "bố / ba":
                return "bố";
            case "anh / em trai":
                return "anh trai";
            case "chị / em gái":
                return "chị gái";
            default:
                int parenIdx = trimmed.indexOf('(');
                if (parenIdx > 0) {
                    trimmed = trimmed.substring(0, parenIdx).trim();
                }
                int slashIdx = trimmed.indexOf('/');
                if (slashIdx > 0) {
                    trimmed = trimmed.substring(0, slashIdx).trim();
                }
                return trimmed.toLowerCase(LOCALE_VI);
        }
    }

    @NonNull
    public static List<VoiceStep> buildEnglishFlashcardSteps(@NonNull EnglishContent.VocabItem item) {
        String viIntro = buildEnglishVocabVietnameseIntro(item.getTopicId(), item.getVietnameseMeaning());
        String enWord = item.getEnglishWord() + "!";
        return Arrays.asList(
                new VoiceStep(viIntro, LOCALE_VI, 0L, "en_intro_" + item.getId(), null),
                new VoiceStep(enWord, LOCALE_EN, 320L, "en_word_" + item.getId(), null),
                new VoiceStep(enWord, LOCALE_EN, 550L, "en_word_repeat_" + item.getId(), null)
        );
    }

    /**
     * Tạo câu hướng dẫn thao tác cụ thể theo đúng chủ đề tiếng Anh hiện tại (Động vật, Màu sắc, Số đếm, Gia đình).
     */
    @NonNull
    public static String buildEnglishTopicActionGuide(@NonNull String topicId) {
        if (EnglishContent.TOPIC_ANIMALS.equalsIgnoreCase(topicId)) {
            return ENGLISH_TOPIC_ANIMALS_GUIDE;
        }
        if (EnglishContent.TOPIC_COLORS.equalsIgnoreCase(topicId)) {
            return ENGLISH_TOPIC_COLORS_GUIDE;
        }
        if (EnglishContent.TOPIC_NUMBERS.equalsIgnoreCase(topicId)) {
            return ENGLISH_TOPIC_NUMBERS_GUIDE;
        }
        if (EnglishContent.TOPIC_FAMILY.equalsIgnoreCase(topicId)) {
            return ENGLISH_TOPIC_FAMILY_GUIDE;
        }
        return ENGLISH_GREETING_2;
    }

    /**
     * Tạo câu hướng dẫn chạm trực tiếp vào hình minh họa cụ thể (ví dụ: "Bé hãy chạm vào hình con mèo để nghe nhé!").
     */
    @NonNull
    public static String buildEnglishItemTouchGuide(@NonNull EnglishContent.VocabItem item) {
        String naturalMeaning = cleanVietnameseMeaningForSpeech(item.getVietnameseMeaning());
        return "Bé hãy chạm vào hình " + naturalMeaning + " để nghe nhé!";
    }

    /**
     * Làm nổi bật nhẹ thành phần giao diện đang được Sao Bông hướng dẫn (phóng to nhẹ 2 nhịp, không gây lag,
     * không che khuất hình ảnh và tôn trọng cài đặt giảm chuyển động của hệ thống).
     */
    public static void highlightTargetView(@Nullable View target) {
        if (target == null || target.getVisibility() != View.VISIBLE) {
            return;
        }
        try {
            float scale = Settings.Global.getFloat(
                    target.getContext().getContentResolver(),
                    Settings.Global.ANIMATOR_DURATION_SCALE,
                    1.0f
            );
            if (scale <= 0.0f) {
                return;
            }
        } catch (Exception ignored) {
        }
        target.animate().cancel();
        target.animate()
                .scaleX(1.06f)
                .scaleY(1.06f)
                .setDuration(190L)
                .withEndAction(() -> target.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(190L)
                        .withEndAction(() -> target.animate()
                                .scaleX(1.05f)
                                .scaleY(1.05f)
                                .setDuration(170L)
                                .withEndAction(() -> target.animate()
                                        .scaleX(1.0f)
                                        .scaleY(1.0f)
                                        .setDuration(170L)
                                        .start())
                                .start())
                        .start())
                .start();
    }

    // =========================================================================
    // CÀI ĐẶT BẬT / TẮT GIỌNG NÓI HƯỚNG DẪN (SHARED PREFERENCES)
    // =========================================================================

    public static boolean isGuidanceEnabled(@NonNull Context context) {
        SharedPreferences prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_VOICE_GUIDANCE_ENABLED, true);
    }

    public static void setGuidanceEnabled(@NonNull Context context, boolean enabled) {
        SharedPreferences prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_VOICE_GUIDANCE_ENABLED, enabled).apply();
    }

    public boolean toggleGuidanceEnabled(@NonNull Context uiContext) {
        boolean nextState = !isGuidanceEnabled(appContext);
        setGuidanceEnabled(appContext, nextState);
        updateGuidanceToggleUi();
        if (!nextState) {
            stop();
            Toast.makeText(uiContext, R.string.m2_toast_voice_guidance_off, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(uiContext, R.string.m2_toast_voice_guidance_on, Toast.LENGTH_SHORT).show();
            replayCurrentInstruction();
        }
        return nextState;
    }

    // =========================================================================
    // LIÊN KẾT GIAO DIỆN SAO BÔNG & NÚT LOA NGHE LẠI
    // =========================================================================

    public void bindViews(
            @Nullable ImageView ivMascot,
            @Nullable View btnReplaySpeaker,
            @Nullable TextView tvGuidanceToggle
    ) {
        this.boundMascotView = ivMascot;
        this.boundReplaySpeakerButton = btnReplaySpeaker;
        this.boundGuidanceToggleView = tvGuidanceToggle;

        if (this.boundReplaySpeakerButton != null) {
            this.boundReplaySpeakerButton.setOnClickListener(v -> {
                Member2SoundEffects.playTap(appContext);
                onUserInteraction();
                replayCurrentInstruction();
            });
        }
        if (this.boundMascotView != null) {
            this.boundMascotView.setOnClickListener(v -> {
                Member2SoundEffects.playTap(appContext);
                onUserInteraction();
                replayCurrentInstruction();
            });
        }
        if (this.boundGuidanceToggleView != null) {
            updateGuidanceToggleUi();
            this.boundGuidanceToggleView.setOnClickListener(v -> {
                Member2SoundEffects.playTap(appContext);
                toggleGuidanceEnabled(v.getContext());
            });
        }
    }

    public void updateGuidanceToggleUi() {
        if (boundGuidanceToggleView == null) {
            return;
        }
        boolean enabled = isGuidanceEnabled(appContext);
        boundGuidanceToggleView.setText(
                enabled ? R.string.m2_voice_guidance_badge_on : R.string.m2_voice_guidance_badge_off
        );
        boundGuidanceToggleView.setAlpha(enabled ? 1.0f : 0.68f);
    }

    // =========================================================================
    // ĐIỀU KHIỂN PHÁT GIỌNG NÓI & HÀNG ĐỢI ƯU TIÊN
    // =========================================================================

    /**
     * Ghi nhận câu hướng dẫn của màn hình/câu hỏi hiện tại để phát lại khi bé bấm nút loa.
     */
    public void setCurrentInstruction(@NonNull List<VoiceStep> steps) {
        currentInstructionSteps.clear();
        currentInstructionSteps.addAll(steps);
    }

    public void setCurrentInstructionVietnamese(@NonNull String vietnameseText) {
        setCurrentInstruction(Collections.singletonList(new VoiceStep(vietnameseText, LOCALE_VI)));
    }

    /**
     * Phát lại đúng hướng dẫn của màn hình hoặc câu hỏi hiện tại khi bé nhấn nút loa.
     * Hoạt động ngay cả khi tự động hướng dẫn đang tắt, vì bé chủ động bấm nút loa.
     */
    public void replayCurrentInstruction() {
        if (isMasterMuted()) {
            Toast.makeText(appContext, R.string.m2_alphabet_toast_muted, Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentInstructionSteps.isEmpty()) {
            return;
        }
        speakSequence(new ArrayList<>(currentInstructionSteps), Priority.KNOWLEDGE_AND_QUESTION, true, null);
    }

    public void speakVietnameseGuidance(@NonNull String text, @NonNull Priority priority) {
        speakSequence(
                Collections.singletonList(new VoiceStep(text, LOCALE_VI)),
                priority,
                false,
                null
        );
    }

    public void speakKnowledgeVietnamese(@NonNull String text) {
        speakSequence(
                Collections.singletonList(new VoiceStep(text, LOCALE_VI)),
                Priority.KNOWLEDGE_AND_QUESTION,
                true,
                null
        );
    }

    public void speakKnowledgeEnglish(@NonNull String englishText) {
        speakSequence(
                Collections.singletonList(new VoiceStep(englishText, LOCALE_EN)),
                Priority.KNOWLEDGE_AND_QUESTION,
                true,
                null
        );
    }

    public void speakFeedbackSteps(@NonNull List<VoiceStep> steps, @Nullable Runnable onComplete) {
        speakSequence(steps, Priority.FEEDBACK, true, onComplete);
    }

    public void speakFeedbackVietnamese(@NonNull String vietnameseFeedback) {
        speakSequence(
                Collections.singletonList(new VoiceStep(vietnameseFeedback, LOCALE_VI)),
                Priority.FEEDBACK,
                true,
                null
        );
    }

    /**
     * Phát một chuỗi các bước giọng nói theo mức độ ưu tiên.
     *
     * @param steps              Danh sách các câu thoại tuần tự.
     * @param priority           Mức độ ưu tiên (1..4).
     * @param bypassGuidanceFlag True nếu là phát âm kiến thức, phản hồi đúng/sai hoặc do bé bấm loa.
     * @param onComplete         Callback thực thi sau khi đọc xong toàn bộ chuỗi.
     */
    public void speakSequence(
            @NonNull List<VoiceStep> steps,
            @NonNull Priority priority,
            boolean bypassGuidanceFlag,
            @Nullable Runnable onComplete
    ) {
        if (isReleased || steps.isEmpty() || isMasterMuted()) {
            return;
        }
        if (!bypassGuidanceFlag && !isGuidanceEnabled(appContext)) {
            if (priority == Priority.GREETING_AND_IDLE || priority == Priority.ACTION_INSTRUCTION) {
                Log.d(TAG, "SaoBong skipped auto-guidance because voice guidance setting is OFF");
                if (onComplete != null) {
                    mainHandler.post(onComplete);
                }
                return;
            }
        }
        if (isSpeaking() && !canPreempt(activePriority, priority)) {
            Log.d(TAG, "SaoBong skipped lower-priority speech (" + priority + " < " + activePriority + ")");
            return;
        }

        cancelActiveSpeechInternal(false);
        this.activePriority = priority;
        this.onSequenceComplete = onComplete;
        for (VoiceStep step : steps) {
            if (step != null && !step.getText().trim().isEmpty()) {
                pendingSteps.addLast(step);
            }
        }
        speakNextStepInQueue();
    }

    private void speakNextStepInQueue() {
        mainHandler.removeCallbacks(stepWatchdogRunnable);
        mainHandler.removeCallbacks(delayedNextStepRunnable);

        if (isReleased || isPausedState || isMasterMuted()) {
            cancelActiveSpeechInternal(false);
            return;
        }

        VoiceStep next = pendingSteps.peekFirst();
        if (next == null) {
            currentUtteranceId = null;
            activePriority = null;
            stopMascotSpeakingEffect();
            Runnable completeCallback = onSequenceComplete;
            onSequenceComplete = null;
            if (completeCallback != null) {
                mainHandler.post(completeCallback);
            }
            return;
        }

        if (next.getPreDelayMs() > 0L) {
            mainHandler.postDelayed(delayedNextStepRunnable, next.getPreDelayMs());
        } else {
            executeNextStepNow();
        }
    }

    private void executeNextStepNow() {
        if (isReleased || isPausedState || isMasterMuted()) {
            cancelActiveSpeechInternal(false);
            return;
        }
        VoiceStep step = pendingSteps.pollFirst();
        while (step != null && !Member2SpeechHelper.isLocaleSupported(step.getLocale())) {
            Log.w(TAG, "SaoBong skipping unsupported locale step: " + step.getLocale());
            step = pendingSteps.pollFirst();
        }
        if (step == null) {
            speakNextStepInQueue();
            return;
        }

        if (step.getOnStepStart() != null) {
            try {
                step.getOnStepStart().run();
            } catch (Exception ignored) {
            }
        }

        startMascotSpeakingEffect();
        String id = UTTERANCE_PREFIX + (++utteranceCounter);
        currentUtteranceId = id;

        // 1. Ưu tiên kiểm tra file âm thanh thu sẵn trong res/raw/m2_voice_<key> nếu có
        int rawResId = resolvePrerecordedRawId(appContext, step.getVoiceKey());
        if (rawResId != 0 && playPrerecordedStep(rawResId, id)) {
            return;
        }

        // 2. Phát qua TextToSpeech
        boolean started = speechHelper.speak(step.getText(), step.getLocale(), id);
        if (!started) {
            currentUtteranceId = null;
            speakNextStepInQueue();
            return;
        }

        long watchdogMs = Math.min(
                STEP_WATCHDOG_MAX_MS,
                STEP_WATCHDOG_BASE_MS + STEP_WATCHDOG_PER_CHAR_MS * step.getText().length()
        );
        mainHandler.postDelayed(stepWatchdogRunnable, watchdogMs);
    }

    private void handleUtteranceDone(@Nullable String utteranceId) {
        if (isReleased || utteranceId == null || !utteranceId.equals(currentUtteranceId)) {
            return;
        }
        currentUtteranceId = null;
        speakNextStepInQueue();
    }

    /**
     * Kiểm tra xem có file âm thanh thu sẵn {@code res/raw/m2_voice_<voiceKey>} hay không.
     */
    public static int resolvePrerecordedRawId(@NonNull Context context, @Nullable String voiceKey) {
        if (voiceKey == null || voiceKey.trim().isEmpty()) {
            return 0;
        }
        String sanitized = voiceKey.trim().toLowerCase(Locale.US).replaceAll("[^a-z0-9_]", "_");
        String resName = sanitized.startsWith("m2_voice_") ? sanitized : ("m2_voice_" + sanitized);
        try {
            return context.getResources().getIdentifier(resName, "raw", context.getPackageName());
        } catch (Exception e) {
            return 0;
        }
    }

    private boolean playPrerecordedStep(int rawResId, @NonNull String utteranceId) {
        try {
            releasePrerecordedPlayer();
            MediaPlayer mp = MediaPlayer.create(appContext, rawResId);
            if (mp == null) {
                return false;
            }
            prerecordedPlayer = mp;
            Member2SoundEffects.duckBgmForSpeech(appContext, true);
            mp.setOnCompletionListener(player -> {
                Member2SoundEffects.duckBgmForSpeech(appContext, false);
                releasePrerecordedPlayer();
                mainHandler.post(() -> handleUtteranceDone(utteranceId));
            });
            mp.setOnErrorListener((player, what, extra) -> {
                Member2SoundEffects.duckBgmForSpeech(appContext, false);
                releasePrerecordedPlayer();
                mainHandler.post(() -> handleUtteranceDone(utteranceId));
                return true;
            });
            mp.start();
            return true;
        } catch (Exception e) {
            releasePrerecordedPlayer();
            return false;
        }
    }

    private void releasePrerecordedPlayer() {
        if (prerecordedPlayer != null) {
            try {
                if (prerecordedPlayer.isPlaying()) {
                    prerecordedPlayer.stop();
                }
            } catch (Exception ignored) {
            }
            try {
                prerecordedPlayer.release();
            } catch (Exception ignored) {
            }
            prerecordedPlayer = null;
        }
    }

    public boolean isReady() {
        return Member2SpeechHelper.isReady();
    }

    public boolean isSpeaking() {
        return currentUtteranceId != null || !pendingSteps.isEmpty();
    }

    public void pause() {
        if (!isSpeaking()) {
            return;
        }
        isPausedState = true;
        pausedRemainingSteps.clear();
        pausedRemainingSteps.addAll(pendingSteps);
        mainHandler.removeCallbacks(stepWatchdogRunnable);
        mainHandler.removeCallbacks(delayedNextStepRunnable);
        releasePrerecordedPlayer();
        speechHelper.stop();
        stopMascotSpeakingEffect();
    }

    public void resume() {
        if (!isPausedState) {
            return;
        }
        isPausedState = false;
        if (!pausedRemainingSteps.isEmpty()) {
            pendingSteps.clear();
            pendingSteps.addAll(pausedRemainingSteps);
            pausedRemainingSteps.clear();
            speakNextStepInQueue();
        }
    }

    public void stop() {
        cancelActiveSpeechInternal(true);
    }

    private void cancelActiveSpeechInternal(boolean stopEngine) {
        isPausedState = false;
        pausedRemainingSteps.clear();
        mainHandler.removeCallbacks(stepWatchdogRunnable);
        mainHandler.removeCallbacks(delayedNextStepRunnable);
        pendingSteps.clear();
        currentUtteranceId = null;
        activePriority = null;
        onSequenceComplete = null;
        releasePrerecordedPlayer();
        if (stopEngine) {
            speechHelper.stop();
        }
        stopMascotSpeakingEffect();
    }

    // =========================================================================
    // CƠ CHẾ TỰ ĐỘNG NHẮC NHỞ KHI BÉ KHÔNG THAO TÁC (12 GIÂY)
    // =========================================================================

    /**
     * Bắt đầu một lượt câu hỏi/thẻ mới: cho phép nhắc nhở tối đa 1 lần trong lượt này.
     */
    public void startNewTurn() {
        idleReminderSpokenInCurrentTurn = false;
        scheduleIdleReminderIfAllowed();
    }

    /**
     * Ghi nhận bé vừa chạm hoặc tương tác: hủy bộ đếm cũ và đếm lại từ đầu.
     */
    public void onUserInteraction() {
        mainHandler.removeCallbacks(idleReminderRunnable);
        if (!idleReminderSpokenInCurrentTurn) {
            scheduleIdleReminderIfAllowed();
        }
    }

    public void cancelIdleReminder() {
        mainHandler.removeCallbacks(idleReminderRunnable);
    }

    private void scheduleIdleReminderIfAllowed() {
        mainHandler.removeCallbacks(idleReminderRunnable);
        if (isReleased || !isResumed || idleReminderSpokenInCurrentTurn) {
            return;
        }
        if (!isGuidanceEnabled(appContext) || isMasterMuted()) {
            return;
        }
        mainHandler.postDelayed(idleReminderRunnable, IDLE_REMINDER_DELAY_MS);
    }

    // =========================================================================
    // HIỆU ỨNG NHÚN NHẸ CỦA LINH VẬT SAO BÔNG KHI ĐANG NÓI
    // =========================================================================

    private void startMascotSpeakingEffect() {
        if (boundReplaySpeakerButton != null) {
            boundReplaySpeakerButton.setScaleX(1.08f);
            boundReplaySpeakerButton.setScaleY(1.08f);
        }
        if (boundMascotView == null || !areAnimationsEnabled()) {
            return;
        }
        if (mascotSpeakAnimator != null && mascotSpeakAnimator.isRunning()) {
            return;
        }
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.07f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.07f);
        PropertyValuesHolder transY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0f, -6f);
        mascotSpeakAnimator = ObjectAnimator.ofPropertyValuesHolder(boundMascotView, scaleX, scaleY, transY);
        mascotSpeakAnimator.setDuration(380L);
        mascotSpeakAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mascotSpeakAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mascotSpeakAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mascotSpeakAnimator.start();
    }

    private void stopMascotSpeakingEffect() {
        if (mascotSpeakAnimator != null) {
            mascotSpeakAnimator.cancel();
            mascotSpeakAnimator = null;
        }
        if (boundMascotView != null) {
            boundMascotView.setScaleX(1.0f);
            boundMascotView.setScaleY(1.0f);
            boundMascotView.setTranslationY(0f);
        }
        if (boundReplaySpeakerButton != null) {
            boundReplaySpeakerButton.setScaleX(1.0f);
            boundReplaySpeakerButton.setScaleY(1.0f);
        }
    }

    private boolean areAnimationsEnabled() {
        try {
            float scale = Settings.Global.getFloat(
                    appContext.getContentResolver(),
                    Settings.Global.ANIMATOR_DURATION_SCALE,
                    1.0f
            );
            return scale > 0.0f;
        } catch (Exception e) {
            return true;
        }
    }

    private boolean isMasterMuted() {
        return soundManager != null && soundManager.isSfxMuted();
    }

    // =========================================================================
    // QUẢN LÝ VÒNG ĐỜI ANDROID ACTIVITY
    // =========================================================================

    public void onResume() {
        isResumed = true;
        updateGuidanceToggleUi();
        scheduleIdleReminderIfAllowed();
    }

    public void onPause() {
        isResumed = false;
        cancelIdleReminder();
        stop();
    }

    public void onStop() {
        isResumed = false;
        cancelIdleReminder();
        stop();
    }

    public void release() {
        isReleased = true;
        isResumed = false;
        cancelIdleReminder();
        cancelActiveSpeechInternal(true);
        speechHelper.shutdown();
        boundMascotView = null;
        boundReplaySpeakerButton = null;
        boundGuidanceToggleView = null;
    }
}

