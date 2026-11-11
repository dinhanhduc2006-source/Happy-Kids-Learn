package com.example.happykidlearn.member2.alphabet;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
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
import androidx.viewpager2.widget.ViewPager2;

import com.example.happykidlearn.R;
import com.example.happykidlearn.base.BaseGameActivity;
import com.example.happykidlearn.member2.common.Member2ContentCatalog;
import com.example.happykidlearn.member2.common.Member2ProgressRepository;
import com.example.happykidlearn.member2.common.Member2SoundEffects;
import com.example.happykidlearn.member2.common.Member2SpeechHelper;
import com.example.happykidlearn.member2.common.Member2VoiceFeedback;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlphabetActivity extends BaseGameActivity implements AlphabetCardAdapter.OnAlphabetActionListener {

    private static final String STATE_CURRENT_INDEX = "state_current_alphabet_index";
    private static final String STATE_IS_CHALLENGE_MODE = "state_alpha_is_challenge_mode";
    private static final String STATE_CHALLENGE_TARGET = "state_alpha_challenge_target";
    private static final String STATE_CHALLENGE_SEED = "state_alpha_challenge_seed";

    private ImageView ivMascot;
    private TextView tvInstruction;
    private TextView tvMilestoneBanner;
    private Button btnModeLearn;
    private Button btnModeChallenge;
    private HorizontalScrollView hsvSelector;
    private LinearLayout llSelector;

    // Learn Mode views
    private View layoutLearnContainer;
    private ViewPager2 vpCards;
    private Button btnPrev;
    private Button btnNext;

    // Listening Challenge Mode views
    private View layoutChallengeContainer;
    private Button btnChallengeReplay;
    private final Button[] btnChallengeChoices = new Button[3];
    private TextView tvChallengeFeedback;
    private Button btnChallengeNext;

    private List<AlphabetContent.AlphabetCardItem> cards;
    private AlphabetCardAdapter cardAdapter;
    private Member2ProgressRepository progressRepository;
    private Member2SpeechHelper speechHelper;
    private Member2VoiceFeedback voiceFeedback;

    private final Set<String> learnedUpperLetters = new HashSet<>();
    private int currentCardIndex = 0;
    private boolean isChallengeMode = false;
    private int challengeTargetIndex = 0;
    private long challengeSeed = 3001L;
    private List<AlphabetContent.AlphabetCardItem> currentChallengeChoices = Collections.emptyList();
    private boolean challengeAnsweredCorrectly = false;
    private boolean suppressPageTurnSound = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m2_activity_alphabet);

        setupHeader(getString(R.string.m2_alphabet_header_title));
        applyWindowInsets();

        if (savedInstanceState != null) {
            currentCardIndex = savedInstanceState.getInt(STATE_CURRENT_INDEX, 0);
            isChallengeMode = savedInstanceState.getBoolean(STATE_IS_CHALLENGE_MODE, false);
            challengeTargetIndex = savedInstanceState.getInt(STATE_CHALLENGE_TARGET, currentCardIndex);
            challengeSeed = savedInstanceState.getLong(STATE_CHALLENGE_SEED, 3001L);
        }

        cards = AlphabetContent.getAllCards();
        progressRepository = new Member2ProgressRepository(this);
        Member2SoundEffects.preloadAll(this);
        speechHelper = new Member2SpeechHelper(this, Member2SpeechHelper.LOCALE_VIETNAMESE);
        speechHelper.setSpeechListener(new Member2SpeechHelper.SpeechListener() {
            @Override
            public void onSpeechUnavailable(String reason) {
                runOnUiThread(() -> Toast.makeText(AlphabetActivity.this, reason, Toast.LENGTH_SHORT).show());
            }
        });
        voiceFeedback = new Member2VoiceFeedback(this);

        initViews();
        buildLetterSelectorStrip();
        setupViewPager();
        updateModeUi(false);
        loadSavedProgress();
    }

    private void applyWindowInsets() {
        View root = findViewById(R.id.m2_alphabet_root);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                return insets;
            });
        }
    }

    private void initViews() {
        ivMascot = findViewById(R.id.iv_m2_alphabet_mascot);
        tvInstruction = findViewById(R.id.tv_m2_alphabet_instruction);
        tvMilestoneBanner = findViewById(R.id.tv_m2_alphabet_milestone_banner);
        btnModeLearn = findViewById(R.id.btn_m2_alpha_mode_learn);
        btnModeChallenge = findViewById(R.id.btn_m2_alpha_mode_challenge);
        hsvSelector = findViewById(R.id.hsv_m2_alphabet_selector);
        llSelector = findViewById(R.id.ll_m2_alphabet_selector);

        layoutLearnContainer = findViewById(R.id.layout_m2_alpha_learn_container);
        vpCards = findViewById(R.id.vp_m2_alphabet_cards);
        btnPrev = findViewById(R.id.btn_m2_alpha_prev);
        btnNext = findViewById(R.id.btn_m2_alpha_next);

        layoutChallengeContainer = findViewById(R.id.layout_m2_alpha_challenge);
        btnChallengeReplay = findViewById(R.id.btn_m2_alpha_challenge_replay);
        btnChallengeChoices[0] = findViewById(R.id.btn_m2_alpha_choice_0);
        btnChallengeChoices[1] = findViewById(R.id.btn_m2_alpha_choice_1);
        btnChallengeChoices[2] = findViewById(R.id.btn_m2_alpha_choice_2);
        tvChallengeFeedback = findViewById(R.id.tv_m2_alpha_challenge_feedback);
        btnChallengeNext = findViewById(R.id.btn_m2_alpha_challenge_next);

        btnModeLearn.setOnClickListener(v -> {
            if (isChallengeMode) {
                Member2SoundEffects.playTap(this);
                isChallengeMode = false;
                updateModeUi(true);
            }
        });

        btnModeChallenge.setOnClickListener(v -> {
            if (!isChallengeMode) {
                Member2SoundEffects.playOpenBook(this);
                isChallengeMode = true;
                challengeTargetIndex = currentCardIndex;
                challengeSeed = 3001L + currentCardIndex * 37L;
                updateModeUi(true);
            }
        });

        btnPrev.setOnClickListener(v -> {
            if (currentCardIndex > 0) {
                Member2SoundEffects.playTap(this);
                vpCards.setCurrentItem(currentCardIndex - 1, true);
            }
        });

        btnNext.setOnClickListener(v -> {
            Member2SoundEffects.playTap(this);
            AlphabetContent.AlphabetCardItem currentItem = cards.get(currentCardIndex);
            markLetterCompleted(currentItem, false);

            if (currentCardIndex < cards.size() - 1) {
                vpCards.setCurrentItem(currentCardIndex + 1, true);
            } else {
                showCompletionCelebration();
            }
        });

        btnChallengeReplay.setOnClickListener(v -> speakCurrentChallengePrompt());

        for (int i = 0; i < btnChallengeChoices.length; i++) {
            final int choiceIdx = i;
            btnChallengeChoices[i].setOnClickListener(v -> onChallengeChoiceSelected(choiceIdx));
        }

        btnChallengeNext.setOnClickListener(v -> {
            Member2SoundEffects.playTap(this);
            challengeTargetIndex = (challengeTargetIndex + 1) % cards.size();
            challengeSeed += 53L;
            setupListeningChallengeQuestion(true);
        });
    }

    private void setupViewPager() {
        cardAdapter = new AlphabetCardAdapter(cards, this);
        vpCards.setAdapter(cardAdapter);
        vpCards.setOffscreenPageLimit(1);

        // Hiệu ứng lật trang sách 2.5D mượt mà
        vpCards.setPageTransformer((page, position) -> {
            if (!areAnimationsEnabled()) {
                page.setRotationY(0f);
                page.setAlpha(1f);
                page.setScaleX(1f);
                page.setScaleY(1f);
                return;
            }
            float absPos = Math.abs(position);
            page.setCameraDistance(8000f * getResources().getDisplayMetrics().density);
            page.setPivotX(position < 0f ? page.getWidth() : 0f);
            page.setPivotY(page.getHeight() * 0.5f);
            if (absPos <= 1f) {
                page.setRotationY(-18f * position);
                float scale = 1f - (0.08f * absPos);
                page.setScaleX(scale);
                page.setScaleY(scale);
                page.setAlpha(1f - (0.25f * absPos));
            } else {
                page.setRotationY(0f);
                page.setAlpha(0f);
            }
        });

        vpCards.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentCardIndex = position;
                if (speechHelper != null) {
                    speechHelper.stop();
                }
                if (!suppressPageTurnSound) {
                    Member2SoundEffects.playOpenBook(AlphabetActivity.this);
                }
                updateNavigationButtons(position);
                updateLetterSelectorStrip();
                scrollSelectorToPosition(position);

                AlphabetContent.AlphabetCardItem item = cards.get(position);
                tvInstruction.setText("Chữ " + item.getUpperLetter() + " (" + item.getExampleWord()
                        + ") • Chạm chữ nghe âm hoặc bấm Lật thẻ nhé!");
            }
        });

        vpCards.setCurrentItem(currentCardIndex, false);
        updateNavigationButtons(currentCardIndex);
        vpCards.post(() -> suppressPageTurnSound = false);
    }

    private void updateModeUi(boolean autoSpeakChallenge) {
        if (isChallengeMode) {
            layoutLearnContainer.setVisibility(View.GONE);
            layoutChallengeContainer.setVisibility(View.VISIBLE);
            btnModeChallenge.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.m2_accent_purple)
            ));
            btnModeChallenge.setTextColor(ContextCompat.getColor(this, R.color.m2_text_on_accent));
            btnModeLearn.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.m2_chip_stroke)
            ));
            btnModeLearn.setTextColor(ContextCompat.getColor(this, R.color.m2_text_primary));
            ivMascot.setImageResource(R.drawable.m2_mascot_saobong_hint);
            tvInstruction.setText("Sao Bông đố bé: Nghe âm thanh và chạm đúng thẻ chữ cái nhé!");
            setupListeningChallengeQuestion(autoSpeakChallenge);
        } else {
            layoutChallengeContainer.setVisibility(View.GONE);
            layoutLearnContainer.setVisibility(View.VISIBLE);
            btnModeLearn.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.m2_brand_teal)
            ));
            btnModeLearn.setTextColor(ContextCompat.getColor(this, R.color.m2_text_on_accent));
            btnModeChallenge.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.m2_chip_stroke)
            ));
            btnModeChallenge.setTextColor(ContextCompat.getColor(this, R.color.m2_text_primary));
            ivMascot.setImageResource(R.drawable.m2_mascot_saobong_welcome);
            tvInstruction.setText(R.string.m2_alphabet_instruction);
        }
    }

    private void setupListeningChallengeQuestion(boolean autoSpeak) {
        challengeAnsweredCorrectly = false;
        currentChallengeChoices = AlphabetContent.buildListeningChoices(challengeTargetIndex, challengeSeed);
        AlphabetContent.AlphabetCardItem targetItem = cards.get(challengeTargetIndex);

        for (int i = 0; i < btnChallengeChoices.length && i < currentChallengeChoices.size(); i++) {
            AlphabetContent.AlphabetCardItem option = currentChallengeChoices.get(i);
            Button btn = btnChallengeChoices[i];
            btn.setText(option.getUpperLetter() + " " + option.getLowerLetter());
            btn.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.m2_accent_yellow_light)
            ));
            btn.setTextColor(ContextCompat.getColor(this, R.color.m2_text_primary));
            btn.setScaleX(1f);
            btn.setScaleY(1f);
        }

        tvChallengeFeedback.setText("🔊 Đố bé chữ nào phát âm là \"" + targetItem.getSpokenLetterName() + "\"?");
        if (autoSpeak) {
            speakCurrentChallengePrompt();
        }
    }

    private void speakCurrentChallengePrompt() {
        AlphabetContent.AlphabetCardItem targetItem = cards.get(challengeTargetIndex);
        if (soundManager != null && soundManager.isSfxMuted()) {
            Toast.makeText(this, R.string.m2_alphabet_toast_muted, Toast.LENGTH_SHORT).show();
            return;
        }
        speechHelper.speak(
                targetItem.getSpokenLetterName(),
                Member2SpeechHelper.LOCALE_VIETNAMESE,
                "alpha_challenge_" + targetItem.getIndex()
        );
    }

    private void onChallengeChoiceSelected(int choiceIdx) {
        if (choiceIdx < 0 || choiceIdx >= currentChallengeChoices.size()) {
            return;
        }
        AlphabetContent.AlphabetCardItem chosen = currentChallengeChoices.get(choiceIdx);
        AlphabetContent.AlphabetCardItem target = cards.get(challengeTargetIndex);
        Button clickedBtn = btnChallengeChoices[choiceIdx];

        if (chosen.getIndex() == target.getIndex()) {
            challengeAnsweredCorrectly = true;
            Member2SoundEffects.playCorrect(this);
            clickedBtn.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.m2_accent_green)
            ));
            clickedBtn.setTextColor(ContextCompat.getColor(this, R.color.m2_text_on_accent));
            ivMascot.setImageResource(R.drawable.m2_mascot_saobong_celebrate);
            tvChallengeFeedback.setText(getString(
                    R.string.m2_alphabet_challenge_correct_format,
                    target.getUpperLetter(),
                    target.getSpokenLetterName(),
                    target.getExampleWord()
            ));
            if (voiceFeedback != null) {
                voiceFeedback.speakTextThenCorrect(
                        target.getSpokenLetterName() + ". " + target.getExampleWord(),
                        Member2SpeechHelper.LOCALE_VIETNAMESE
                );
            }
            markLetterCompleted(target, true);
        } else if (!challengeAnsweredCorrectly) {
            Member2SoundEffects.playGentleTry(this);
            clickedBtn.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.m2_accent_pink_light)
            ));
            ivMascot.setImageResource(R.drawable.m2_mascot_saobong_hint);
            tvChallengeFeedback.setText(R.string.m2_alphabet_challenge_wrong_format);
            if (voiceFeedback != null) {
                voiceFeedback.speakWrong();
            }
            if (areAnimationsEnabled()) {
                clickedBtn.animate()
                        .translationX(12f)
                        .setDuration(60)
                        .withEndAction(() -> clickedBtn.animate()
                                .translationX(-12f)
                                .setDuration(60)
                                .withEndAction(() -> clickedBtn.animate().translationX(0f).setDuration(60).start())
                                .start())
                        .start();
            }
        }
    }

    private void buildLetterSelectorStrip() {
        llSelector.removeAllViews();
        int sizePx = dpToPx(42);
        int marginPx = dpToPx(6);

        for (int i = 0; i < cards.size(); i++) {
            final int index = i;
            AlphabetContent.AlphabetCardItem item = cards.get(i);

            TextView chip = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(sizePx, sizePx);
            if (i > 0) {
                params.setMarginStart(marginPx);
            }
            chip.setLayoutParams(params);
            chip.setGravity(Gravity.CENTER);
            chip.setText(item.getUpperLetter());
            chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            chip.setTypeface(chip.getTypeface(), android.graphics.Typeface.BOLD);

            chip.setOnClickListener(v -> {
                Member2SoundEffects.playTap(this);
                if (isChallengeMode) {
                    challengeTargetIndex = index;
                    challengeSeed += 19L;
                    setupListeningChallengeQuestion(true);
                    updateLetterSelectorStrip();
                } else {
                    vpCards.setCurrentItem(index, true);
                }
            });
            llSelector.addView(chip);
        }
        updateLetterSelectorStrip();
    }

    private void updateLetterSelectorStrip() {
        int activeIndex = isChallengeMode ? challengeTargetIndex : currentCardIndex;
        int childCount = llSelector.getChildCount();
        for (int i = 0; i < childCount && i < cards.size(); i++) {
            View view = llSelector.getChildAt(i);
            if (!(view instanceof TextView)) {
                continue;
            }
            TextView chip = (TextView) view;
            AlphabetContent.AlphabetCardItem item = cards.get(i);
            boolean isSelected = (i == activeIndex);
            boolean isLearned = learnedUpperLetters.contains(item.getUpperLetter());

            if (isSelected) {
                chip.setBackgroundResource(R.drawable.m2_bg_chip_selected);
                chip.setTextColor(ContextCompat.getColor(this, R.color.m2_text_on_accent));
            } else if (isLearned) {
                chip.setBackgroundResource(R.drawable.m2_bg_alphabet_seed_learned);
                chip.setTextColor(ContextCompat.getColor(this, R.color.m2_badge_ready_text));
            } else {
                chip.setBackgroundResource(R.drawable.m2_bg_chip_unselected);
                chip.setTextColor(ContextCompat.getColor(this, R.color.m2_text_primary));
            }
        }
    }

    private void scrollSelectorToPosition(int index) {
        if (index < 0 || index >= llSelector.getChildCount()) {
            return;
        }
        View child = llSelector.getChildAt(index);
        if (child != null) {
            hsvSelector.post(() -> {
                int scrollX = child.getLeft() - (hsvSelector.getWidth() - child.getWidth()) / 2;
                hsvSelector.smoothScrollTo(Math.max(0, scrollX), 0);
            });
        }
    }

    private void updateNavigationButtons(int position) {
        btnPrev.setEnabled(position > 0);
        btnPrev.setAlpha(position > 0 ? 1.0f : 0.45f);

        if (position == cards.size() - 1) {
            btnNext.setText(R.string.m2_alphabet_btn_finish);
        } else {
            btnNext.setText(R.string.m2_alphabet_btn_next);
        }
    }

    private void loadSavedProgress() {
        setGameProgress(0, AlphabetContent.EXPECTED_LETTER_COUNT);
        progressRepository.loadCompletedItemsForGame(
                Member2ContentCatalog.GAME_ID_ALPHABET,
                completedSet -> {
                    learnedUpperLetters.clear();
                    learnedUpperLetters.addAll(completedSet);
                    setGameProgress(learnedUpperLetters.size(), AlphabetContent.EXPECTED_LETTER_COUNT);
                    cardAdapter.updateLearnedLetters(learnedUpperLetters);
                    updateLetterSelectorStrip();
                }
        );
    }

    @Override
    public void onSpeakLetterClicked(AlphabetContent.AlphabetCardItem item) {
        ivMascot.setImageResource(R.drawable.m2_mascot_saobong_happy);
        tvInstruction.setText("Sao Bông phát âm: \"" + item.getSpokenLetterName() + "\"");
        if (soundManager != null && soundManager.isSfxMuted()) {
            Toast.makeText(this, R.string.m2_alphabet_toast_muted, Toast.LENGTH_SHORT).show();
        } else {
            speechHelper.speak(
                    item.getSpokenLetterName(),
                    Member2SpeechHelper.LOCALE_VIETNAMESE,
                    "alpha_letter_" + item.getIndex()
            );
        }
        markLetterCompleted(item, true);
    }

    @Override
    public void onSpeakWordClicked(AlphabetContent.AlphabetCardItem item) {
        ivMascot.setImageResource(R.drawable.m2_mascot_saobong_happy);
        tvInstruction.setText("Từ ví dụ của chữ " + item.getUpperLetter() + ": \"" + item.getExampleWord() + "\"");
        if (soundManager != null && soundManager.isSfxMuted()) {
            Toast.makeText(this, R.string.m2_alphabet_toast_muted, Toast.LENGTH_SHORT).show();
        } else {
            speechHelper.speak(
                    item.getExampleWord(),
                    Member2SpeechHelper.LOCALE_VIETNAMESE,
                    "alpha_word_" + item.getIndex()
            );
        }
        markLetterCompleted(item, true);
    }

    @Override
    public void onSpeakBothClicked(AlphabetContent.AlphabetCardItem item) {
        ivMascot.setImageResource(R.drawable.m2_mascot_saobong_celebrate);
        if (soundManager != null && soundManager.isSfxMuted()) {
            Toast.makeText(this, R.string.m2_alphabet_toast_muted, Toast.LENGTH_SHORT).show();
        } else {
            speechHelper.speak(
                    item.getSpokenLetterName() + ". " + item.getExampleWord(),
                    Member2SpeechHelper.LOCALE_VIETNAMESE,
                    "alpha_both_" + item.getIndex()
            );
        }
        markLetterCompleted(item, true);
    }

    @Override
    public void onCardFlipped(AlphabetContent.AlphabetCardItem item, boolean isBackVisible) {
        Member2SoundEffects.playOpenBook(this);
        if (isBackVisible) {
            ivMascot.setImageResource(R.drawable.m2_mascot_saobong_happy);
            tvInstruction.setText(item.getDiscoverySentence());
            if (soundManager == null || !soundManager.isSfxMuted()) {
                speechHelper.speak(
                        item.getSpokenLetterName() + ". " + item.getExampleWord(),
                        Member2SpeechHelper.LOCALE_VIETNAMESE,
                        "alpha_flip_" + item.getIndex()
                );
            }
            markLetterCompleted(item, true);
        } else {
            ivMascot.setImageResource(R.drawable.m2_mascot_saobong_welcome);
            tvInstruction.setText(R.string.m2_alphabet_instruction);
        }
    }

    private void markLetterCompleted(AlphabetContent.AlphabetCardItem item, boolean celebrateIfAllDone) {
        boolean wasAlreadyLearned = learnedUpperLetters.contains(item.getUpperLetter());
        if (!wasAlreadyLearned) {
            learnedUpperLetters.add(item.getUpperLetter());
        }

        int learnedCount = learnedUpperLetters.size();
        setGameProgress(learnedCount, AlphabetContent.EXPECTED_LETTER_COUNT);
        cardAdapter.updateLearnedLetters(learnedUpperLetters);
        updateLetterSelectorStrip();

        progressRepository.markItemCompleted(
                Member2ContentCatalog.GAME_ID_ALPHABET,
                item.getUpperLetter(),
                AlphabetContent.EXPECTED_LETTER_COUNT,
                1,
                null
        );

        if (!wasAlreadyLearned) {
            if (learnedCount % AlphabetContent.GROUP_MILESTONE_SIZE == 0
                    && learnedCount < AlphabetContent.EXPECTED_LETTER_COUNT) {
                showGroupMilestoneCelebration(learnedCount);
            }
        }

        if (celebrateIfAllDone && !wasAlreadyLearned && learnedCount == AlphabetContent.EXPECTED_LETTER_COUNT) {
            showCompletionCelebration();
        }
    }

    private void showGroupMilestoneCelebration(int learnedCount) {
        Member2SoundEffects.playCelebrate(this);
        if (voiceFeedback != null) {
            voiceFeedback.speakCompletion();
        }
        ivMascot.setImageResource(R.drawable.m2_mascot_saobong_celebrate);
        if (tvMilestoneBanner != null) {
            tvMilestoneBanner.setText(getString(R.string.m2_alphabet_milestone_banner_format, learnedCount));
            tvMilestoneBanner.setVisibility(View.VISIBLE);
            if (areAnimationsEnabled()) {
                tvMilestoneBanner.setScaleX(0.85f);
                tvMilestoneBanner.setScaleY(0.85f);
                tvMilestoneBanner.animate().scaleX(1f).scaleY(1f).setDuration(220).start();
            }
        }
    }

    private void showCompletionCelebration() {
        Member2SoundEffects.playCelebrate(this);
        if (voiceFeedback != null) {
            voiceFeedback.speakCompletionNow();
        }
        ivMascot.setImageResource(R.drawable.m2_mascot_saobong_celebrate);
        int learnedCount = learnedUpperLetters.size();
        int stars = learnedCount >= 24 ? 3 : (learnedCount >= 12 ? 2 : 1);
        showVictoryDialog(
                stars,
                this::finish,
                () -> {
                    isChallengeMode = false;
                    updateModeUi(false);
                    vpCards.setCurrentItem(0, true);
                }
        );
    }

    private boolean areAnimationsEnabled() {
        try {
            float scale = Settings.Global.getFloat(
                    getContentResolver(),
                    Settings.Global.ANIMATOR_DURATION_SCALE,
                    1.0f
            );
            return scale > 0.0f;
        } catch (Exception e) {
            return true;
        }
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
        outState.putInt(STATE_CURRENT_INDEX, currentCardIndex);
        outState.putBoolean(STATE_IS_CHALLENGE_MODE, isChallengeMode);
        outState.putInt(STATE_CHALLENGE_TARGET, challengeTargetIndex);
        outState.putLong(STATE_CHALLENGE_SEED, challengeSeed);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Member2SoundEffects.applyMiniGameBgmLevel(this);
    }

    @Override
    protected void onStop() {
        if (speechHelper != null) {
            speechHelper.stop();
        }
        if (voiceFeedback != null) {
            voiceFeedback.stop();
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
        super.onDestroy();
    }

    private int dpToPx(int dp) {
        return Math.round(
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        dp,
                        getResources().getDisplayMetrics()
                )
        );
    }
}
