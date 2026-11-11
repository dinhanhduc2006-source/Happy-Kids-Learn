package com.example.happykidlearn.member2.english;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.happykidlearn.R;
import com.example.happykidlearn.base.BaseGameActivity;
import com.example.happykidlearn.member2.common.Member2ContentCatalog;
import com.example.happykidlearn.member2.common.Member2ProgressRepository;
import com.example.happykidlearn.member2.common.Member2SoundEffects;
import com.example.happykidlearn.member2.common.Member2SpeechHelper;
import com.example.happykidlearn.member2.common.Member2VoiceFeedback;
import com.example.happykidlearn.member2.common.Member2VoiceManager;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnglishActivity extends BaseGameActivity {

    private static final String STATE_TOPIC_INDEX = "state_en_topic_index";
    private static final String STATE_CARD_INDEX = "state_en_card_index";
    private static final String STATE_GAME_MODE = "state_en_game_mode";
    private static final String STATE_QUIZ_INDEX = "state_en_quiz_index";
    private static final String STATE_QUIZ_SCORE = "state_en_quiz_score";
    private static final String STATE_QUIZ_SEED = "state_en_quiz_seed";
    private static final String STATE_WELCOME_PLAYED = "state_en_welcome_played";

    private static final int MODE_FLASHCARD = 0;
    private static final int MODE_LISTEN = 1;
    private static final int MODE_QUIZ = 2;

    private ImageView ivMascot;
    private TextView tvBannerText;
    private LinearLayout llTopicsStrip;
    private Button btnModeFlashcard;
    private Button btnModeListen;
    private Button btnModeQuiz;

    // Flashcard views
    private View layoutFlashcardContainer;
    private MaterialCardView cardFlashcard;
    private TextView tvCardIndex;
    private TextView tvCardStatus;
    private ImageView ivCardIllust;
    private TextView tvCardWord;
    private TextView tvCardPhonetic;
    private TextView tvCardMeaning;
    private TextView tvCardSentence;
    private Button btnCardSpeak;
    private Button btnPrevWord;
    private Button btnNextWord;

    // Listen & Quiz stage views
    private View layoutQuizContainer;
    private TextView tvQuizProgress;
    private TextView tvQuizPromptTitle;
    private TextView tvQuizWord;
    private Button btnQuizSpeak;
    private final MaterialCardView[] optionCards = new MaterialCardView[4];
    private final ImageView[] optionImages = new ImageView[4];
    private final TextView[] optionLabels = new TextView[4];
    private TextView tvQuizFeedback;
    private Button btnQuizNext;

    private List<EnglishContent.Topic> topics;
    private Member2ProgressRepository progressRepository;
    private Member2SpeechHelper speechHelper;
    private Member2VoiceFeedback voiceFeedback;
    private Member2VoiceManager voiceManager;

    private final Set<String> learnedVocabIds = new HashSet<>();
    private int currentTopicIndex = 0;
    private int currentCardIndex = 0;
    private int currentMode = MODE_FLASHCARD;

    private List<EnglishQuestion> currentQuizQuestions = Collections.emptyList();
    private int currentQuizIndex = 0;
    private int currentQuizScore = 0;
    private long currentQuizSeed = 2026L;
    private boolean currentQuestionAnswered = false;
    private boolean hasPlayedWelcomeGreeting = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m2_activity_english);

        setupHeader(getString(R.string.m2_english_header_title));
        applyWindowInsets();

        topics = EnglishContent.getAllTopics();
        progressRepository = new Member2ProgressRepository(this);
        Member2SoundEffects.preloadAll(this);
        speechHelper = new Member2SpeechHelper(this, Member2SpeechHelper.LOCALE_ENGLISH_US);
        speechHelper.setSpeechListener(new Member2SpeechHelper.SpeechListener() {
            @Override
            public void onSpeechUnavailable(String reason) {
                runOnUiThread(() -> Toast.makeText(EnglishActivity.this, reason, Toast.LENGTH_SHORT).show());
            }
        });
        voiceFeedback = new Member2VoiceFeedback(this);
        voiceManager = new Member2VoiceManager(this);

        initViews();
        buildTopicChips();

        if (savedInstanceState != null) {
            currentTopicIndex = savedInstanceState.getInt(STATE_TOPIC_INDEX, 0);
            currentCardIndex = savedInstanceState.getInt(STATE_CARD_INDEX, 0);
            currentMode = savedInstanceState.getInt(STATE_GAME_MODE, MODE_FLASHCARD);
            currentQuizIndex = savedInstanceState.getInt(STATE_QUIZ_INDEX, 0);
            currentQuizScore = savedInstanceState.getInt(STATE_QUIZ_SCORE, 0);
            currentQuizSeed = savedInstanceState.getLong(STATE_QUIZ_SEED, 2026L);
            hasPlayedWelcomeGreeting = savedInstanceState.getBoolean(STATE_WELCOME_PLAYED, false);
        }

        updateTopicSelectionUi();
        if (currentMode == MODE_LISTEN || currentMode == MODE_QUIZ) {
            EnglishContent.Topic topic = topics.get(currentTopicIndex);
            currentQuizQuestions = EnglishQuizEngine.generateQuizForTopic(topic.getId(), currentQuizSeed);
            switchToInteractiveStageMode(currentMode, false);
        } else {
            switchToFlashcardMode(savedInstanceState == null);
        }

        loadSavedProgress();
    }

    private void applyWindowInsets() {
        View root = findViewById(R.id.m2_english_root);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                return insets;
            });
        }
    }

    private void initViews() {
        ivMascot = findViewById(R.id.iv_m2_english_mascot);
        tvBannerText = findViewById(R.id.tv_m2_english_banner_text);
        llTopicsStrip = findViewById(R.id.ll_m2_english_topics);
        btnModeFlashcard = findViewById(R.id.btn_m2_en_mode_flashcard);
        btnModeListen = findViewById(R.id.btn_m2_en_mode_listen);
        btnModeQuiz = findViewById(R.id.btn_m2_en_mode_quiz);

        layoutFlashcardContainer = findViewById(R.id.layout_m2_en_flashcard_container);
        cardFlashcard = findViewById(R.id.card_m2_en_flashcard);
        tvCardIndex = findViewById(R.id.tv_m2_en_card_index);
        tvCardStatus = findViewById(R.id.tv_m2_en_card_status);
        ivCardIllust = findViewById(R.id.iv_m2_en_card_illust);
        tvCardWord = findViewById(R.id.tv_m2_en_card_word);
        tvCardPhonetic = findViewById(R.id.tv_m2_en_card_phonetic);
        tvCardMeaning = findViewById(R.id.tv_m2_en_card_meaning);
        tvCardSentence = findViewById(R.id.tv_m2_en_card_sentence);
        btnCardSpeak = findViewById(R.id.btn_m2_en_card_speak);
        btnPrevWord = findViewById(R.id.btn_m2_en_prev_word);
        btnNextWord = findViewById(R.id.btn_m2_en_next_word);

        layoutQuizContainer = findViewById(R.id.layout_m2_en_quiz_container);
        tvQuizProgress = findViewById(R.id.tv_m2_en_quiz_progress);
        tvQuizPromptTitle = findViewById(R.id.tv_m2_en_quiz_prompt_title);
        tvQuizWord = findViewById(R.id.tv_m2_en_quiz_word);
        btnQuizSpeak = findViewById(R.id.btn_m2_en_quiz_speak);

        optionCards[0] = findViewById(R.id.card_m2_en_opt_0);
        optionCards[1] = findViewById(R.id.card_m2_en_opt_1);
        optionCards[2] = findViewById(R.id.card_m2_en_opt_2);
        optionCards[3] = findViewById(R.id.card_m2_en_opt_3);

        optionImages[0] = findViewById(R.id.iv_m2_en_opt_0);
        optionImages[1] = findViewById(R.id.iv_m2_en_opt_1);
        optionImages[2] = findViewById(R.id.iv_m2_en_opt_2);
        optionImages[3] = findViewById(R.id.iv_m2_en_opt_3);

        optionLabels[0] = findViewById(R.id.tv_m2_en_opt_0);
        optionLabels[1] = findViewById(R.id.tv_m2_en_opt_1);
        optionLabels[2] = findViewById(R.id.tv_m2_en_opt_2);
        optionLabels[3] = findViewById(R.id.tv_m2_en_opt_3);

        tvQuizFeedback = findViewById(R.id.tv_m2_en_quiz_feedback);
        btnQuizNext = findViewById(R.id.btn_m2_en_quiz_next);

        if (voiceManager != null) {
            voiceManager.bindViews(
                    ivMascot,
                    findViewById(R.id.btn_m2_voice_replay),
                    findViewById(R.id.tv_m2_voice_guidance_toggle)
            );
        }

        btnModeFlashcard.setOnClickListener(v -> {
            Member2SoundEffects.playTap(this);
            switchToFlashcardMode(true);
        });
        btnModeListen.setOnClickListener(v -> {
            Member2SoundEffects.playTap(this);
            startNewStageSession(MODE_LISTEN);
        });
        btnModeQuiz.setOnClickListener(v -> {
            Member2SoundEffects.playTap(this);
            startNewStageSession(MODE_QUIZ);
        });

        btnCardSpeak.setOnClickListener(v -> {
            Member2SoundEffects.playTap(this);
            EnglishContent.VocabItem item = getCurrentFlashcardItem();
            animateViewPop(ivCardIllust);
            speakBilingualFlashcardNow(item);
            markVocabLearned(item);
        });

        ivCardIllust.setOnClickListener(v -> {
            Member2SoundEffects.playTap(this);
            EnglishContent.VocabItem item = getCurrentFlashcardItem();
            animateViewPop(ivCardIllust);
            speakBilingualFlashcardNow(item);
            markVocabLearned(item);
        });

        btnPrevWord.setOnClickListener(v -> {
            if (currentCardIndex > 0) {
                Member2SoundEffects.playOpenBook(this);
                currentCardIndex--;
                renderCurrentFlashcard(true);
            }
        });

        btnNextWord.setOnClickListener(v -> {
            Member2SoundEffects.playOpenBook(this);
            EnglishContent.VocabItem currentItem = getCurrentFlashcardItem();
            markVocabLearned(currentItem);
            EnglishContent.Topic topic = topics.get(currentTopicIndex);
            if (currentCardIndex < topic.getItems().size() - 1) {
                currentCardIndex++;
                renderCurrentFlashcard(true);
            } else {
                startNewStageSession(MODE_LISTEN);
            }
        });

        btnQuizSpeak.setOnClickListener(v -> {
            Member2SoundEffects.playTap(this);
            if (!currentQuizQuestions.isEmpty() && currentQuizIndex < currentQuizQuestions.size()) {
                EnglishContent.VocabItem target = currentQuizQuestions.get(currentQuizIndex).getTargetItem();
                speakBilingualQuizPromptNow(target);
            }
        });

        for (int i = 0; i < 4; i++) {
            final int optionIdx = i;
            optionCards[i].setOnClickListener(v -> onQuizOptionClicked(optionIdx));
        }

        btnQuizNext.setOnClickListener(v -> {
            Member2SoundEffects.playTap(this);
            if (currentQuizIndex < currentQuizQuestions.size() - 1) {
                currentQuizIndex++;
                renderCurrentQuizQuestion(true);
            } else {
                showQuizResultDialog();
            }
        });
    }

    private void buildTopicChips() {
        llTopicsStrip.removeAllViews();
        String[] icons = new String[] { "🎨 ", "🐾 ", "🏡 ", "🔢 " };
        for (int i = 0; i < topics.size(); i++) {
            final int topicIdx = i;
            EnglishContent.Topic topic = topics.get(i);

            TextView chip = new TextView(this);
            String prefix = (i < icons.length) ? icons[i] : "";
            chip.setText(prefix + topic.getDisplayChipTitle());
            chip.setGravity(Gravity.CENTER);
            chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            chip.setTypeface(chip.getTypeface(), android.graphics.Typeface.BOLD);
            chip.setMinHeight(dpToPx(38));
            chip.setPadding(dpToPx(14), dpToPx(6), dpToPx(14), dpToPx(6));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMarginEnd(dpToPx(8));
            chip.setLayoutParams(params);

            chip.setOnClickListener(v -> {
                Member2SoundEffects.playOpenBook(this);
                currentTopicIndex = topicIdx;
                currentCardIndex = 0;
                updateTopicSelectionUi();
                if (currentMode == MODE_LISTEN || currentMode == MODE_QUIZ) {
                    startNewStageSession(currentMode);
                } else {
                    renderCurrentFlashcard(true);
                }
            });

            llTopicsStrip.addView(chip);
        }
    }

    private void updateTopicSelectionUi() {
        int count = llTopicsStrip.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = llTopicsStrip.getChildAt(i);
            if (child instanceof TextView) {
                TextView chip = (TextView) child;
                boolean selected = (i == currentTopicIndex);
                chip.setBackgroundResource(
                        selected ? R.drawable.m2_bg_chip_selected : R.drawable.m2_bg_chip_unselected);
                chip.setTextColor(ContextCompat.getColor(
                        this,
                        selected ? R.color.m2_text_on_accent : R.color.m2_text_primary));
            }
        }
    }

    private void updateModeButtonsUi() {
        styleModeButton(btnModeFlashcard, currentMode == MODE_FLASHCARD, R.color.m2_accent_purple);
        styleModeButton(btnModeListen, currentMode == MODE_LISTEN, R.color.m2_brand_teal);
        styleModeButton(btnModeQuiz, currentMode == MODE_QUIZ, R.color.m2_accent_green);
    }

    private void styleModeButton(Button btn, boolean active, int activeColorRes) {
        if (btn == null) {
            return;
        }
        int bgColor = ContextCompat.getColor(this, active ? activeColorRes : R.color.m2_chip_stroke);
        int textColor = ContextCompat.getColor(this, active ? R.color.m2_text_on_accent : R.color.m2_text_primary);
        btn.setBackgroundTintList(ColorStateList.valueOf(bgColor));
        btn.setTextColor(textColor);
    }

    private void switchToFlashcardMode(boolean autoSpeakCard) {
        currentMode = MODE_FLASHCARD;
        layoutFlashcardContainer.setVisibility(View.VISIBLE);
        layoutQuizContainer.setVisibility(View.GONE);
        updateModeButtonsUi();
        renderCurrentFlashcard(autoSpeakCard);
    }

    private void startNewStageSession(int mode) {
        EnglishContent.Topic topic = topics.get(currentTopicIndex);
        currentQuizSeed = System.currentTimeMillis();
        currentQuizQuestions = EnglishQuizEngine.generateQuizForTopic(topic.getId(), currentQuizSeed);
        currentQuizIndex = 0;
        currentQuizScore = 0;
        switchToInteractiveStageMode(mode, true);
    }

    private void switchToInteractiveStageMode(int mode, boolean speakFirstQuestion) {
        currentMode = mode;
        layoutFlashcardContainer.setVisibility(View.GONE);
        layoutQuizContainer.setVisibility(View.VISIBLE);
        updateModeButtonsUi();
        renderCurrentQuizQuestion(speakFirstQuestion);
    }

    private EnglishContent.VocabItem getCurrentFlashcardItem() {
        EnglishContent.Topic topic = topics.get(currentTopicIndex);
        List<EnglishContent.VocabItem> items = topic.getItems();
        int safeIdx = Math.max(0, Math.min(currentCardIndex, items.size() - 1));
        return items.get(safeIdx);
    }

    private void renderCurrentFlashcard() {
        renderCurrentFlashcard(false);
    }

    private void renderCurrentFlashcard(boolean autoSpeakCard) {
        EnglishContent.Topic topic = topics.get(currentTopicIndex);
        List<EnglishContent.VocabItem> items = topic.getItems();
        EnglishContent.VocabItem item = getCurrentFlashcardItem();

        int accentColor = ContextCompat.getColor(this, item.getAccentColorResId());
        if (ivMascot != null) {
            ivMascot.setImageResource(R.drawable.m2_mascot_saobong_welcome);
        }
        if (tvBannerText != null) {
            tvBannerText.setText(topic.getWorldSubtitle() + " • Chạm tranh nghe phát âm nhé!");
        }

        tvCardIndex.setText("Từ " + (currentCardIndex + 1) + "/" + items.size());
        boolean isLearned = learnedVocabIds.contains(item.getId());
        if (isLearned) {
            tvCardStatus.setText("Đã khám phá ★");
            tvCardStatus.setTextColor(ContextCompat.getColor(this, R.color.m2_badge_ready_text));
            tvCardStatus.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.m2_badge_ready_bg)));
        } else {
            tvCardStatus.setText("Từ mới ✨");
            tvCardStatus.setTextColor(ContextCompat.getColor(this, R.color.m2_badge_soon_text));
            tvCardStatus.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.m2_badge_soon_bg)));
        }

        ivCardIllust.setImageResource(item.getIllustrationResId());
        ivCardIllust.setContentDescription(item.getEnglishWord() + " - " + item.getVietnameseMeaning());
        tvCardWord.setText(item.getEnglishWord());
        tvCardWord.setTextColor(accentColor);
        tvCardPhonetic.setText(item.getPhonetic());
        tvCardMeaning.setText(item.getVietnameseMeaning());
        if (tvCardSentence != null) {
            tvCardSentence.setText(item.getExampleSentence());
        }

        btnPrevWord.setEnabled(currentCardIndex > 0);
        btnPrevWord.setAlpha(currentCardIndex > 0 ? 1.0f : 0.45f);

        if (currentCardIndex == items.size() - 1) {
            btnNextWord.setText(R.string.m2_english_btn_start_quiz);
        } else {
            btnNextWord.setText(R.string.m2_english_btn_next_word);
        }

        if (voiceManager != null) {
            String topicGuide = Member2VoiceManager.buildEnglishTopicActionGuide(topic.getId());
            List<Member2VoiceManager.VoiceStep> rawCardSteps = Member2VoiceManager.buildEnglishFlashcardSteps(item);
            List<Member2VoiceManager.VoiceStep> cardSteps = new ArrayList<>(rawCardSteps.size());
            for (int i = 0; i < rawCardSteps.size(); i++) {
                Member2VoiceManager.VoiceStep s = rawCardSteps.get(i);
                cardSteps.add(new Member2VoiceManager.VoiceStep(
                        s.getText(),
                        s.getLocale(),
                        s.getPreDelayMs(),
                        s.getVoiceKey(),
                        i == 0 ? () -> Member2VoiceManager.highlightTargetView(ivCardIllust) : s.getOnStepStart()
                ));
            }
            List<Member2VoiceManager.VoiceStep> replaySteps = new ArrayList<>();
            replaySteps.add(new Member2VoiceManager.VoiceStep(
                    topicGuide,
                    Member2VoiceManager.LOCALE_VI,
                    0L,
                    "en_topic_guide_" + topic.getId(),
                    () -> Member2VoiceManager.highlightTargetView(ivCardIllust)
            ));
            for (int i = 0; i < cardSteps.size(); i++) {
                Member2VoiceManager.VoiceStep s = cardSteps.get(i);
                replaySteps.add(new Member2VoiceManager.VoiceStep(
                        s.getText(),
                        s.getLocale(),
                        i == 0 ? 300L : s.getPreDelayMs(),
                        s.getVoiceKey(),
                        s.getOnStepStart()
                ));
            }
            voiceManager.setCurrentInstruction(replaySteps);
            voiceManager.startNewTurn();
            if (!hasPlayedWelcomeGreeting) {
                hasPlayedWelcomeGreeting = true;
                List<Member2VoiceManager.VoiceStep> welcomeAndCard = new ArrayList<>();
                welcomeAndCard.add(new Member2VoiceManager.VoiceStep(
                        Member2VoiceManager.ENGLISH_GREETING_1,
                        Member2VoiceManager.LOCALE_VI,
                        0L,
                        "en_greeting_1",
                        null
                ));
                welcomeAndCard.add(new Member2VoiceManager.VoiceStep(
                        topicGuide,
                        Member2VoiceManager.LOCALE_VI,
                        260L,
                        "en_topic_guide_" + topic.getId(),
                        () -> Member2VoiceManager.highlightTargetView(ivCardIllust)
                ));
                for (int i = 0; i < cardSteps.size(); i++) {
                    Member2VoiceManager.VoiceStep s = cardSteps.get(i);
                    welcomeAndCard.add(new Member2VoiceManager.VoiceStep(
                            s.getText(),
                            s.getLocale(),
                            i == 0 ? 320L : s.getPreDelayMs(),
                            s.getVoiceKey(),
                            s.getOnStepStart()
                    ));
                }
                voiceManager.speakSequence(
                        welcomeAndCard,
                        Member2VoiceManager.Priority.GREETING_AND_IDLE,
                        false,
                        null
                );
            } else if (autoSpeakCard) {
                voiceManager.onUserInteraction();
                if (currentCardIndex == 0) {
                    voiceManager.speakSequence(
                            replaySteps,
                            Member2VoiceManager.Priority.KNOWLEDGE_AND_QUESTION,
                            false,
                            null
                    );
                } else {
                    voiceManager.speakSequence(
                            cardSteps,
                            Member2VoiceManager.Priority.KNOWLEDGE_AND_QUESTION,
                            false,
                            null
                    );
                }
            }
        }
    }

    private void speakBilingualFlashcardNow(@NonNull EnglishContent.VocabItem item) {
        if (soundManager != null && soundManager.isSfxMuted()) {
            Toast.makeText(this, R.string.m2_alphabet_toast_muted, Toast.LENGTH_SHORT).show();
            return;
        }
        if (voiceManager != null) {
            voiceManager.onUserInteraction();
            List<Member2VoiceManager.VoiceStep> steps = Member2VoiceManager.buildEnglishFlashcardSteps(item);
            voiceManager.setCurrentInstruction(steps);
            voiceManager.speakSequence(
                    steps,
                    Member2VoiceManager.Priority.KNOWLEDGE_AND_QUESTION,
                    true,
                    null
            );
            return;
        }
        speakEnglishWord(item.getEnglishWord(), item.getId());
    }

    private void renderCurrentQuizQuestion(boolean autoSpeak) {
        if (currentQuizQuestions.isEmpty()) {
            return;
        }
        currentQuestionAnswered = false;
        btnQuizNext.setVisibility(View.GONE);

        EnglishQuestion question = currentQuizQuestions.get(currentQuizIndex);
        EnglishContent.VocabItem target = question.getTargetItem();
        EnglishContent.Topic topic = topics.get(currentTopicIndex);

        if (currentMode == MODE_LISTEN) {
            tvQuizProgress
                    .setText("🎧 Nghe & Chọn • Câu " + (currentQuizIndex + 1) + "/" + currentQuizQuestions.size());
            tvQuizPromptTitle.setText(R.string.m2_english_listen_prompt);
            tvQuizWord.setText(R.string.m2_english_listen_mystery);
            if (ivMascot != null) {
                ivMascot.setImageResource(R.drawable.m2_mascot_saobong_hint);
            }
            if (tvBannerText != null) {
                tvBannerText.setText(
                        "🎧 Thử Thách Nghe (" + topic.getEnglishName() + ") • Lắng tai nghe từ tiếng Anh nhé!");
            }
            tvQuizFeedback.setText(R.string.m2_english_listen_prompt);
        } else {
            tvQuizProgress.setText(getString(
                    R.string.m2_english_quiz_progress_format,
                    currentQuizIndex + 1,
                    currentQuizQuestions.size(),
                    currentQuizScore));
            tvQuizPromptTitle.setText(R.string.m2_english_quiz_prompt);
            tvQuizWord.setText(target.getEnglishWord() + "  " + target.getPhonetic());
            if (ivMascot != null) {
                ivMascot.setImageResource(R.drawable.m2_mascot_saobong_welcome);
            }
            if (tvBannerText != null) {
                tvBannerText
                        .setText("🏆 Đố Vui 5 Câu (" + topic.getEnglishName() + ") • Chọn hình đúng để nhận Sao Vàng!");
            }
            tvQuizFeedback.setText(R.string.m2_english_quiz_prompt);
        }

        tvQuizFeedback.setTextColor(ContextCompat.getColor(this, R.color.m2_text_primary));
        tvQuizFeedback.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.m2_badge_soon_bg)));

        List<EnglishContent.VocabItem> options = question.getOptions();
        for (int i = 0; i < 4; i++) {
            if (i < options.size()) {
                EnglishContent.VocabItem opt = options.get(i);
                optionCards[i].setVisibility(View.VISIBLE);
                optionCards[i].setStrokeColor(ContextCompat.getColor(this, R.color.m2_divider_soft));
                optionCards[i].setCardBackgroundColor(ContextCompat.getColor(this, R.color.m2_bg_card_surface));
                optionImages[i].setImageResource(opt.getIllustrationResId());
                optionImages[i].setContentDescription(opt.getVietnameseMeaning());
                optionLabels[i].setText(opt.getVietnameseMeaning());
            } else {
                optionCards[i].setVisibility(View.INVISIBLE);
            }
        }

        if (voiceManager != null) {
            List<Member2VoiceManager.VoiceStep> quizSteps = Arrays.asList(
                    new Member2VoiceManager.VoiceStep(
                            Member2VoiceManager.ENGLISH_QUIZ_PROMPT,
                            Member2VoiceManager.LOCALE_VI,
                            0L,
                            "en_quiz_prompt",
                            null
                    ),
                    new Member2VoiceManager.VoiceStep(
                            target.getEnglishWord() + "!",
                            Member2VoiceManager.LOCALE_EN,
                            300L,
                            "en_word_" + target.getId(),
                            null
                    )
            );
            voiceManager.setCurrentInstruction(quizSteps);
            voiceManager.startNewTurn();
        }

        if (autoSpeak) {
            speakBilingualQuizPromptNow(target);
        }
    }

    private void speakBilingualQuizPromptNow(@NonNull EnglishContent.VocabItem target) {
        if (soundManager != null && soundManager.isSfxMuted()) {
            Toast.makeText(this, R.string.m2_alphabet_toast_muted, Toast.LENGTH_SHORT).show();
            return;
        }
        if (voiceManager != null) {
            voiceManager.onUserInteraction();
            List<Member2VoiceManager.VoiceStep> quizSteps = Arrays.asList(
                    new Member2VoiceManager.VoiceStep(
                            Member2VoiceManager.ENGLISH_QUIZ_PROMPT,
                            Member2VoiceManager.LOCALE_VI,
                            0L,
                            "en_quiz_prompt",
                            null
                    ),
                    new Member2VoiceManager.VoiceStep(
                            target.getEnglishWord() + "!",
                            Member2VoiceManager.LOCALE_EN,
                            300L,
                            "en_word_" + target.getId(),
                            null
                    )
            );
            voiceManager.speakSequence(
                    quizSteps,
                    Member2VoiceManager.Priority.KNOWLEDGE_AND_QUESTION,
                    true,
                    null
            );
            return;
        }
        speakEnglishWord(target.getEnglishWord(), "stage_q_" + target.getId());
    }

    private void onQuizOptionClicked(int selectedIdx) {
        if (currentQuestionAnswered || currentQuizQuestions.isEmpty()) {
            return;
        }
        if (voiceManager != null) {
            voiceManager.onUserInteraction();
        }
        EnglishQuestion question = currentQuizQuestions.get(currentQuizIndex);
        EnglishContent.VocabItem target = question.getTargetItem();

        if (EnglishQuizEngine.isAnswerCorrect(question, selectedIdx)) {
            currentQuestionAnswered = true;
            currentQuizScore++;
            Member2SoundEffects.playCorrect(this);

            animateViewPop(optionCards[selectedIdx]);
            optionCards[selectedIdx].setStrokeColor(ContextCompat.getColor(this, R.color.m2_accent_green));
            optionCards[selectedIdx].setCardBackgroundColor(ContextCompat.getColor(this, R.color.m2_badge_ready_bg));

            tvQuizWord.setText(target.getEnglishWord() + "  " + target.getPhonetic());
            if (ivMascot != null) {
                ivMascot.setImageResource(R.drawable.m2_mascot_saobong_celebrate);
            }
            if (tvBannerText != null) {
                tvBannerText.setText("🌟 Tuyệt vời! " + target.getEnglishWord() + " nghĩa là "
                        + target.getVietnameseMeaning() + "!");
            }

            if (currentMode == MODE_QUIZ) {
                tvQuizProgress.setText(getString(
                        R.string.m2_english_quiz_progress_format,
                        currentQuizIndex + 1,
                        currentQuizQuestions.size(),
                        currentQuizScore));
            }
            tvQuizFeedback.setText(getString(
                    R.string.m2_english_quiz_correct_format,
                    target.getEnglishWord(),
                    target.getVietnameseMeaning()));
            tvQuizFeedback.setTextColor(ContextCompat.getColor(this, R.color.m2_badge_ready_text));
            tvQuizFeedback.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.m2_badge_ready_bg)));

            if (voiceManager != null) {
                voiceManager.speakFeedbackSteps(
                        Arrays.asList(
                                new Member2VoiceManager.VoiceStep(
                                        Member2VoiceManager.ENGLISH_CORRECT_EN,
                                        Member2VoiceManager.LOCALE_EN,
                                        0L,
                                        "en_correct_wonderful",
                                        null
                                ),
                                new Member2VoiceManager.VoiceStep(
                                        Member2VoiceManager.ENGLISH_CORRECT_VI,
                                        Member2VoiceManager.LOCALE_VI,
                                        240L,
                                        "en_correct_vi",
                                        null
                                ),
                                new Member2VoiceManager.VoiceStep(
                                        target.getEnglishWord() + "!",
                                        Member2VoiceManager.LOCALE_EN,
                                        280L,
                                        "en_word_" + target.getId(),
                                        null
                                )
                        ),
                        null
                );
            } else if (voiceFeedback != null) {
                voiceFeedback.speakTextThenCorrect(
                        target.getEnglishWord(),
                        Member2SpeechHelper.LOCALE_ENGLISH_US);
            }
            markVocabLearned(target);

            btnQuizNext.setText(
                    currentQuizIndex == currentQuizQuestions.size() - 1
                            ? R.string.m2_english_quiz_finish
                            : R.string.m2_english_quiz_next_question);
            btnQuizNext.setVisibility(View.VISIBLE);
        } else {
            Member2SoundEffects.playGentleTry(this);
            animateViewShake(optionCards[selectedIdx]);
            optionCards[selectedIdx].setStrokeColor(ContextCompat.getColor(this, R.color.accent_pink));
            optionCards[selectedIdx].setCardBackgroundColor(ContextCompat.getColor(this, R.color.m2_accent_pink_light));

            if (ivMascot != null) {
                ivMascot.setImageResource(R.drawable.m2_mascot_saobong_hint);
            }
            tvQuizFeedback.setText(getString(
                    R.string.m2_english_quiz_wrong_format,
                    target.getEnglishWord()));
            tvQuizFeedback.setTextColor(ContextCompat.getColor(this, R.color.m2_text_primary));
            tvQuizFeedback.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.m2_accent_pink_light)));
            if (voiceManager != null) {
                voiceManager.speakFeedbackSteps(
                        Arrays.asList(
                                new Member2VoiceManager.VoiceStep(
                                        Member2VoiceManager.ENGLISH_WRONG_VI,
                                        Member2VoiceManager.LOCALE_VI,
                                        0L,
                                        "en_wrong_vi",
                                        null
                                ),
                                new Member2VoiceManager.VoiceStep(
                                        Member2VoiceManager.ENGLISH_REPLAY_SPEAKER_GUIDE,
                                        Member2VoiceManager.LOCALE_VI,
                                        260L,
                                        "en_replay_speaker_guide",
                                        () -> Member2VoiceManager.highlightTargetView(btnQuizSpeak)
                                )
                        ),
                        null
                );
            } else if (voiceFeedback != null) {
                voiceFeedback.speakWrong();
            }
        }
    }

    private void showQuizResultDialog() {
        Member2SoundEffects.playCelebrate(this);
        if (voiceFeedback != null) {
            voiceFeedback.speakCompletionNow();
        }
        int stars = EnglishQuizEngine.calculateStars(currentQuizScore, currentQuizQuestions.size());
        showVictoryDialog(
                stars,
                () -> {
                    if (currentMode == MODE_LISTEN) {
                        startNewStageSession(MODE_QUIZ);
                    } else if (currentTopicIndex < topics.size() - 1) {
                        currentTopicIndex++;
                        currentCardIndex = 0;
                        updateTopicSelectionUi();
                        switchToFlashcardMode(true);
                    } else {
                        finish();
                    }
                },
                () -> startNewStageSession(currentMode));
    }

    private void loadSavedProgress() {
        int totalVocab = EnglishContent.getTotalVocabularyCount();
        setGameProgress(0, totalVocab);
        progressRepository.loadCompletedItemsForGame(
                Member2ContentCatalog.GAME_ID_ENGLISH,
                completedSet -> {
                    learnedVocabIds.clear();
                    learnedVocabIds.addAll(completedSet);
                    setGameProgress(learnedVocabIds.size(), totalVocab);
                    if (currentMode == MODE_FLASHCARD) {
                        renderCurrentFlashcard(false);
                    }
                });
    }

    private void markVocabLearned(EnglishContent.VocabItem item) {
        int totalVocab = EnglishContent.getTotalVocabularyCount();
        learnedVocabIds.add(item.getId());
        setGameProgress(learnedVocabIds.size(), totalVocab);
        if (currentMode == MODE_FLASHCARD) {
            renderCurrentFlashcard(false);
        }

        progressRepository.markItemCompleted(
                Member2ContentCatalog.GAME_ID_ENGLISH,
                item.getId(),
                totalVocab,
                1,
                updated -> setGameProgress(updated.getCompletedLevels(), totalVocab));
    }

    private void speakEnglishWord(String englishWord, String utteranceId) {
        if (soundManager != null && soundManager.isSfxMuted()) {
            Toast.makeText(this, R.string.m2_alphabet_toast_muted, Toast.LENGTH_SHORT).show();
            return;
        }
        speechHelper.speak(englishWord, Member2SpeechHelper.LOCALE_ENGLISH_US, utteranceId);
    }

    private void animateViewPop(View v) {
        if (v == null) {
            return;
        }
        v.animate()
                .scaleX(1.08f)
                .scaleY(1.08f)
                .setDuration(110)
                .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(110).start())
                .start();
    }

    private void animateViewShake(View v) {
        if (v == null) {
            return;
        }
        v.animate()
                .translationX(12f)
                .setDuration(55)
                .withEndAction(() -> v.animate()
                        .translationX(-12f)
                        .setDuration(55)
                        .withEndAction(() -> v.animate().translationX(0f).setDuration(55).start())
                        .start())
                .start();
    }

    @Override
    protected void onSoundToggled(boolean isMuted) {
        super.onSoundToggled(isMuted);
        if (isMuted) {
            Member2SoundEffects.stopOrMuteBgm(this);
            if (speechHelper != null) {
                speechHelper.stop();
            }
            if (voiceFeedback != null) {
                voiceFeedback.stop();
            }
            if (voiceManager != null) {
                voiceManager.stop();
            }
        } else {
            Member2SoundEffects.applyMiniGameBgmLevel(this);
        }
        if (progressRepository != null) {
            progressRepository.toggleSoundAndPersist(!isMuted, null);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_TOPIC_INDEX, currentTopicIndex);
        outState.putInt(STATE_CARD_INDEX, currentCardIndex);
        outState.putInt(STATE_GAME_MODE, currentMode);
        outState.putInt(STATE_QUIZ_INDEX, currentQuizIndex);
        outState.putInt(STATE_QUIZ_SCORE, currentQuizScore);
        outState.putLong(STATE_QUIZ_SEED, currentQuizSeed);
        outState.putBoolean(STATE_WELCOME_PLAYED, hasPlayedWelcomeGreeting);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Member2SoundEffects.applyMiniGameBgmLevel(this);
        if (voiceManager != null) {
            voiceManager.onResume();
        }
    }

    @Override
    protected void onStop() {
        if (speechHelper != null) {
            speechHelper.stop();
        }
        if (voiceFeedback != null) {
            voiceFeedback.stop();
        }
        if (voiceManager != null) {
            voiceManager.onStop();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (speechHelper != null) {
            speechHelper.shutdown();
        }
        if (voiceFeedback != null) {
            voiceFeedback.release();
        }
        if (voiceManager != null) {
            voiceManager.release();
        }
        super.onDestroy();
    }

    private int dpToPx(int dp) {
        return Math.round(
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        dp,
                        getResources().getDisplayMetrics()));
    }
}
