package com.example.happykidlearn.member2.spelling;

import android.content.ClipData;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
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

import com.example.happykidlearn.R;
import com.example.happykidlearn.base.BaseGameActivity;
import com.example.happykidlearn.member2.common.Member2ContentCatalog;
import com.example.happykidlearn.member2.common.Member2ProgressRepository;
import com.example.happykidlearn.member2.common.Member2SoundEffects;
import com.example.happykidlearn.member2.common.Member2SpeechHelper;
import com.example.happykidlearn.member2.common.Member2VoiceFeedback;
import com.example.happykidlearn.member2.common.Member2VoiceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SpellingActivity extends BaseGameActivity {

    private static final String STATE_EXERCISE_INDEX = "state_spelling_exercise_index";
    private static final String STATE_POOL_SEED = "state_spelling_pool_seed";
    private static final String STATE_SLOT_TILE_INDICES = "state_spelling_slot_tile_indices";
    private static final String STATE_WELCOME_PLAYED = "state_spelling_welcome_played";

    private ImageView ivMascot;
    private TextView tvMascotSpeech;
    private HorizontalScrollView hsvSelector;
    private LinearLayout llSelector;
    private TextView tvExerciseIndex;
    private TextView tvStatusBadge;
    private ImageView ivIllustration;
    private TextView tvContextPhrase;
    private TextView tvHintText;
    private Button btnListen;
    private LinearLayout llSlotsContainer;
    private TextView tvFeedback;
    private LinearLayout llPoolContainer;
    private Button btnHint;
    private Button btnClear;
    private Button btnCheck;
    private Button btnPrev;
    private Button btnNext;

    private List<SpellingExercise> exercises;
    private Member2ProgressRepository progressRepository;
    private Member2SpeechHelper speechHelper;
    private Member2VoiceFeedback voiceFeedback;
    private Member2VoiceManager voiceManager;

    private final Set<String> completedExerciseIds = new HashSet<>();
    private int currentExerciseIndex = 0;
    private long currentPoolSeed = 1001L;
    private List<String> currentPoolTokens = new ArrayList<>();
    private int[] slotTileIndices = new int[0];
    private boolean isCurrentAttemptVerifiedCorrect = false;
    private boolean hasPlayedWelcomeGreeting = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m2_activity_spelling);

        setupHeader(getString(R.string.m2_spelling_header_title));
        applyWindowInsets();

        exercises = SpellingEngine.getAllExercises();
        progressRepository = new Member2ProgressRepository(this);
        Member2SoundEffects.preloadAll(this);
        speechHelper = new Member2SpeechHelper(this, Member2SpeechHelper.LOCALE_VIETNAMESE);
        speechHelper.setSpeechListener(new Member2SpeechHelper.SpeechListener() {
            @Override
            public void onSpeechUnavailable(String reason) {
                runOnUiThread(() -> Toast.makeText(SpellingActivity.this, reason, Toast.LENGTH_SHORT).show());
            }
        });
        voiceFeedback = new Member2VoiceFeedback(this);
        voiceManager = new Member2VoiceManager(this);

        initViews();
        buildExerciseSelectorStrip();

        if (savedInstanceState != null) {
            currentExerciseIndex = savedInstanceState.getInt(STATE_EXERCISE_INDEX, 0);
            currentPoolSeed = savedInstanceState.getLong(STATE_POOL_SEED, 1001L + currentExerciseIndex);
            hasPlayedWelcomeGreeting = savedInstanceState.getBoolean(STATE_WELCOME_PLAYED, false);
            int[] restoredSlots = savedInstanceState.getIntArray(STATE_SLOT_TILE_INDICES);
            loadExercise(currentExerciseIndex, currentPoolSeed, restoredSlots, false);
        } else {
            loadExercise(0, 1001L, null, true);
        }

        loadSavedProgress();
    }

    private void applyWindowInsets() {
        View root = findViewById(R.id.m2_spelling_root);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                return insets;
            });
        }
    }

    private void initViews() {
        ivMascot = findViewById(R.id.iv_m2_spelling_mascot);
        tvMascotSpeech = findViewById(R.id.tv_m2_spelling_mascot_speech);
        hsvSelector = findViewById(R.id.hsv_m2_spelling_selector);
        llSelector = findViewById(R.id.ll_m2_spelling_selector);
        tvExerciseIndex = findViewById(R.id.tv_m2_spelling_index);
        tvStatusBadge = findViewById(R.id.tv_m2_spelling_status);
        ivIllustration = findViewById(R.id.iv_m2_spelling_illust);
        tvContextPhrase = findViewById(R.id.tv_m2_spelling_context);
        tvHintText = findViewById(R.id.tv_m2_spelling_hint);
        btnListen = findViewById(R.id.btn_m2_spelling_listen);
        llSlotsContainer = findViewById(R.id.ll_m2_spelling_slots);
        tvFeedback = findViewById(R.id.tv_m2_spelling_feedback);
        llPoolContainer = findViewById(R.id.ll_m2_spelling_pool);
        btnHint = findViewById(R.id.btn_m2_spelling_hint);
        btnClear = findViewById(R.id.btn_m2_spelling_clear);
        btnCheck = findViewById(R.id.btn_m2_spelling_check);
        btnPrev = findViewById(R.id.btn_m2_spelling_prev);
        btnNext = findViewById(R.id.btn_m2_spelling_next);

        if (voiceManager != null) {
            voiceManager.bindViews(
                    ivMascot,
                    findViewById(R.id.btn_m2_voice_replay),
                    findViewById(R.id.tv_m2_voice_guidance_toggle)
            );
        }

        btnListen.setOnClickListener(v -> speakCurrentTargetWord());
        ivIllustration.setOnClickListener(v -> speakCurrentTargetWord());
        btnClear.setOnClickListener(v -> {
            if (voiceManager != null) {
                voiceManager.onUserInteraction();
            }
            Member2SoundEffects.playTap(this);
            clearAllSlots();
        });
        btnHint.setOnClickListener(v -> {
            if (voiceManager != null) {
                voiceManager.onUserInteraction();
            }
            Member2SoundEffects.playTap(this);
            applySmartHint();
        });
        btnCheck.setOnClickListener(v -> {
            if (voiceManager != null) {
                voiceManager.onUserInteraction();
            }
            checkCurrentAnswer();
        });

        btnPrev.setOnClickListener(v -> {
            if (currentExerciseIndex > 0) {
                Member2SoundEffects.playOpenBook(this);
                int prevIdx = currentExerciseIndex - 1;
                loadExercise(prevIdx, 1001L + prevIdx * 37L, null);
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentExerciseIndex < exercises.size() - 1) {
                Member2SoundEffects.playOpenBook(this);
                int nextIdx = currentExerciseIndex + 1;
                loadExercise(nextIdx, 1001L + nextIdx * 37L, null);
            } else {
                showAllExercisesVictoryDialog();
            }
        });
    }

    private void buildExerciseSelectorStrip() {
        if (llSelector == null) {
            return;
        }
        llSelector.removeAllViews();
        int heightPx = dpToPx(36);
        int padH = dpToPx(12);
        int marginPx = dpToPx(6);

        for (int i = 0; i < exercises.size(); i++) {
            final int index = i;
            SpellingExercise ex = exercises.get(i);

            TextView chip = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    heightPx
            );
            if (i > 0) {
                params.setMarginStart(marginPx);
            }
            chip.setLayoutParams(params);
            chip.setPadding(padH, 0, padH, 0);
            chip.setGravity(Gravity.CENTER);
            chip.setText((i + 1) + ". " + ex.getTargetWord());
            chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            chip.setTypeface(chip.getTypeface(), android.graphics.Typeface.BOLD);

            chip.setOnClickListener(v -> {
                if (currentExerciseIndex != index) {
                    Member2SoundEffects.playOpenBook(this);
                    loadExercise(index, 1001L + index * 37L, null);
                }
            });
            llSelector.addView(chip);
        }
        updateExerciseSelectorStrip();
    }

    private void updateExerciseSelectorStrip() {
        if (llSelector == null) {
            return;
        }
        int childCount = llSelector.getChildCount();
        for (int i = 0; i < childCount && i < exercises.size(); i++) {
            View v = llSelector.getChildAt(i);
            if (!(v instanceof TextView)) {
                continue;
            }
            TextView chip = (TextView) v;
            SpellingExercise ex = exercises.get(i);
            boolean isSelected = (i == currentExerciseIndex);
            boolean isDone = completedExerciseIds.contains(ex.getId());

            if (isSelected) {
                chip.setBackgroundResource(R.drawable.m2_bg_chip_selected);
                chip.setTextColor(ContextCompat.getColor(this, R.color.m2_text_on_accent));
            } else if (isDone) {
                chip.setBackgroundResource(R.drawable.m2_bg_alphabet_seed_learned);
                chip.setTextColor(ContextCompat.getColor(this, R.color.m2_badge_ready_text));
            } else {
                chip.setBackgroundResource(R.drawable.m2_bg_chip_unselected);
                chip.setTextColor(ContextCompat.getColor(this, R.color.m2_text_primary));
            }
        }

        if (hsvSelector != null && currentExerciseIndex >= 0 && currentExerciseIndex < llSelector.getChildCount()) {
            View activeChild = llSelector.getChildAt(currentExerciseIndex);
            if (activeChild != null) {
                hsvSelector.post(() -> {
                    int scrollX = activeChild.getLeft() - (hsvSelector.getWidth() - activeChild.getWidth()) / 2;
                    hsvSelector.smoothScrollTo(Math.max(0, scrollX), 0);
                });
            }
        }
    }

    private void loadSavedProgress() {
        setGameProgress(0, exercises.size());
        progressRepository.loadCompletedItemsForGame(
                Member2ContentCatalog.GAME_ID_SPELLING,
                completedSet -> {
                    completedExerciseIds.clear();
                    completedExerciseIds.addAll(completedSet);
                    setGameProgress(completedExerciseIds.size(), exercises.size());
                    updateExerciseStatusBadge();
                    updateExerciseSelectorStrip();
                }
        );
    }

    private void loadExercise(int index, long seed, @Nullable int[] restoredSlotIndices) {
        loadExercise(index, seed, restoredSlotIndices, true);
    }

    private void loadExercise(
            int index,
            long seed,
            @Nullable int[] restoredSlotIndices,
            boolean autoSpeakQuestion
    ) {
        if (speechHelper != null) {
            speechHelper.stop();
        }
        currentExerciseIndex = Math.max(0, Math.min(index, exercises.size() - 1));
        currentPoolSeed = seed;
        isCurrentAttemptVerifiedCorrect = false;
        SpellingExercise exercise = exercises.get(currentExerciseIndex);

        currentPoolTokens = SpellingEngine.buildTilePool(exercise, currentPoolSeed);
        if (restoredSlotIndices != null && restoredSlotIndices.length == exercise.getSlotCount()) {
            slotTileIndices = Arrays.copyOf(restoredSlotIndices, restoredSlotIndices.length);
        } else {
            slotTileIndices = new int[exercise.getSlotCount()];
            Arrays.fill(slotTileIndices, -1);
        }

        tvExerciseIndex.setText(getString(
                R.string.m2_spelling_exercise_index_format,
                currentExerciseIndex + 1,
                exercises.size()
        ));
        ivIllustration.setImageResource(exercise.getIllustrationResId());
        ivIllustration.setContentDescription(getString(
                R.string.m2_cd_alphabet_illustration_format,
                exercise.getContextPhrase()
        ));
        tvContextPhrase.setText(exercise.getContextPhrase());
        tvHintText.setText("Cần lắp " + exercise.getSlotCount() + " khối gỗ để tạo từ");

        if (ivMascot != null) {
            ivMascot.setImageResource(R.drawable.m2_mascot_saobong_welcome);
        }
        if (tvMascotSpeech != null) {
            tvMascotSpeech.setText("Bản vẽ số " + (currentExerciseIndex + 1) + ": \""
                    + exercise.getContextPhrase() + "\" • Bấm loa nghe từ rồi lắp chữ nhé!");
        }

        setFeedbackState(
                getString(R.string.m2_spelling_feedback_prompt),
                R.color.m2_badge_soon_bg,
                R.color.m2_text_primary
        );

        updateExerciseStatusBadge();
        updateExerciseSelectorStrip();
        updateNavigationButtons();
        renderSlotsAndPool();
        announceExerciseOrWelcome(exercise, autoSpeakQuestion);
    }

    private void announceExerciseOrWelcome(@NonNull SpellingExercise exercise, boolean autoSpeakQuestion) {
        if (voiceManager == null) {
            return;
        }
        String questionPrompt = Member2VoiceManager.buildSpellingQuestionPrompt(exercise.getTargetWord());
        List<Member2VoiceManager.VoiceStep> questionSteps = Arrays.asList(
                new Member2VoiceManager.VoiceStep(
                        questionPrompt,
                        Member2VoiceManager.LOCALE_VI,
                        0L,
                        "spelling_q_" + exercise.getId(),
                        () -> Member2VoiceManager.highlightTargetView(ivIllustration)
                ),
                new Member2VoiceManager.VoiceStep(
                        exercise.getTargetWord() + "! " + exercise.getContextPhrase(),
                        Member2VoiceManager.LOCALE_VI,
                        280L,
                        "spelling_word_" + exercise.getId(),
                        null
                )
        );
        List<Member2VoiceManager.VoiceStep> replayInstructionSteps = Arrays.asList(
                new Member2VoiceManager.VoiceStep(
                        Member2VoiceManager.SPELLING_LOOK_TOP_GUIDE,
                        Member2VoiceManager.LOCALE_VI,
                        0L,
                        "spelling_look_top",
                        () -> Member2VoiceManager.highlightTargetView(ivIllustration)
                ),
                new Member2VoiceManager.VoiceStep(
                        Member2VoiceManager.SPELLING_TAP_BOTTOM_GUIDE,
                        Member2VoiceManager.LOCALE_VI,
                        250L,
                        "spelling_tap_bottom",
                        () -> Member2VoiceManager.highlightTargetView(llPoolContainer)
                ),
                new Member2VoiceManager.VoiceStep(
                        questionPrompt,
                        Member2VoiceManager.LOCALE_VI,
                        280L,
                        "spelling_q_" + exercise.getId(),
                        null
                ),
                new Member2VoiceManager.VoiceStep(
                        exercise.getTargetWord() + "! " + exercise.getContextPhrase(),
                        Member2VoiceManager.LOCALE_VI,
                        280L,
                        "spelling_word_" + exercise.getId(),
                        null
                )
        );
        voiceManager.setCurrentInstruction(replayInstructionSteps);
        voiceManager.startNewTurn();

        if (!hasPlayedWelcomeGreeting) {
            hasPlayedWelcomeGreeting = true;
            voiceManager.speakSequence(
                    Arrays.asList(
                            new Member2VoiceManager.VoiceStep(
                                    Member2VoiceManager.SPELLING_GREETING_1,
                                    Member2VoiceManager.LOCALE_VI,
                                    0L,
                                    "spelling_greeting_1",
                                    null
                            ),
                            new Member2VoiceManager.VoiceStep(
                                    Member2VoiceManager.SPELLING_LOOK_TOP_GUIDE,
                                    Member2VoiceManager.LOCALE_VI,
                                    260L,
                                    "spelling_look_top",
                                    () -> Member2VoiceManager.highlightTargetView(ivIllustration)
                            ),
                            new Member2VoiceManager.VoiceStep(
                                    Member2VoiceManager.SPELLING_TAP_BOTTOM_GUIDE,
                                    Member2VoiceManager.LOCALE_VI,
                                    260L,
                                    "spelling_tap_bottom",
                                    () -> Member2VoiceManager.highlightTargetView(llPoolContainer)
                            ),
                            new Member2VoiceManager.VoiceStep(
                                    questionPrompt,
                                    Member2VoiceManager.LOCALE_VI,
                                    320L,
                                    "spelling_q_" + exercise.getId(),
                                    null
                            ),
                            new Member2VoiceManager.VoiceStep(
                                    exercise.getTargetWord() + "! " + exercise.getContextPhrase(),
                                    Member2VoiceManager.LOCALE_VI,
                                    280L,
                                    "spelling_word_" + exercise.getId(),
                                    null
                            )
                    ),
                    Member2VoiceManager.Priority.GREETING_AND_IDLE,
                    false,
                    null
            );
            return;
        }

        if (autoSpeakQuestion) {
            voiceManager.onUserInteraction();
            voiceManager.speakSequence(
                    questionSteps,
                    Member2VoiceManager.Priority.KNOWLEDGE_AND_QUESTION,
                    false,
                    null
            );
        }
    }

    private void updateExerciseStatusBadge() {
        SpellingExercise exercise = exercises.get(currentExerciseIndex);
        boolean isDone = completedExerciseIds.contains(exercise.getId());
        if (isDone) {
            tvStatusBadge.setText(R.string.m2_spelling_status_done);
            tvStatusBadge.setTextColor(ContextCompat.getColor(this, R.color.m2_badge_ready_text));
            tvStatusBadge.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.m2_badge_ready_bg)
            ));
        } else {
            tvStatusBadge.setText(R.string.m2_spelling_status_new);
            tvStatusBadge.setTextColor(ContextCompat.getColor(this, R.color.m2_badge_soon_text));
            tvStatusBadge.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.m2_badge_soon_bg)
            ));
        }
    }

    private void updateNavigationButtons() {
        btnPrev.setEnabled(currentExerciseIndex > 0);
        btnPrev.setAlpha(currentExerciseIndex > 0 ? 1.0f : 0.45f);
        if (currentExerciseIndex == exercises.size() - 1) {
            btnNext.setText(R.string.m2_alphabet_btn_finish);
        } else {
            btnNext.setText(R.string.m2_spelling_btn_next);
        }
    }

    private void renderSlotsAndPool() {
        llSlotsContainer.removeAllViews();
        llPoolContainer.removeAllViews();

        Set<Integer> usedPoolIndices = new HashSet<>();
        for (int tileIdx : slotTileIndices) {
            if (tileIdx >= 0) {
                usedPoolIndices.add(tileIdx);
            }
        }

        // 1. Render các khe gỗ trên Bàn Chế Tạo (Drop Targets + Chạm để lấy chữ ra)
        for (int s = 0; s < slotTileIndices.length; s++) {
            final int slotIndex = s;
            final int placedTileIdx = slotTileIndices[s];

            TextView slotView = new TextView(this);
            LinearLayout.LayoutParams slotParams = new LinearLayout.LayoutParams(dpToPx(68), dpToPx(68));
            slotParams.setMargins(dpToPx(6), dpToPx(2), dpToPx(6), dpToPx(2));
            slotView.setLayoutParams(slotParams);
            slotView.setGravity(Gravity.CENTER);
            slotView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
            slotView.setTypeface(slotView.getTypeface(), android.graphics.Typeface.BOLD);

            if (placedTileIdx >= 0 && placedTileIdx < currentPoolTokens.size()) {
                String token = currentPoolTokens.get(placedTileIdx);
                slotView.setText(token);
                if (isCurrentAttemptVerifiedCorrect) {
                    slotView.setTextColor(ContextCompat.getColor(this, R.color.m2_badge_ready_text));
                    slotView.setBackgroundResource(R.drawable.m2_bg_spelling_slot_correct);
                } else {
                    slotView.setTextColor(ContextCompat.getColor(this, R.color.m2_brand_teal_dark));
                    slotView.setBackgroundResource(R.drawable.m2_bg_spelling_slot_filled);
                }
                slotView.setContentDescription("Ô " + (slotIndex + 1) + " đang có chữ " + token);
                slotView.setOnClickListener(v -> {
                    Member2SoundEffects.playTap(this);
                    isCurrentAttemptVerifiedCorrect = false;
                    slotTileIndices[slotIndex] = -1;
                    renderSlotsAndPool();
                });
            } else {
                slotView.setText("?");
                slotView.setTextColor(ContextCompat.getColor(this, R.color.m2_text_muted));
                slotView.setBackgroundResource(R.drawable.m2_bg_spelling_slot_empty);
                slotView.setContentDescription("Ô trống số " + (slotIndex + 1));
            }

            slotView.setOnDragListener((v, event) -> handleSlotDragEvent(slotIndex, (TextView) v, event));
            llSlotsContainer.addView(slotView);
        }

        // 2. Render các khối chữ gỗ đồ chơi trong khay (Kéo thả + Chạm để lắp)
        for (int i = 0; i < currentPoolTokens.size(); i++) {
            final int poolIndex = i;
            final String token = currentPoolTokens.get(i);
            final boolean isUsed = usedPoolIndices.contains(poolIndex);

            TextView tileView = new TextView(this);
            LinearLayout.LayoutParams tileParams = new LinearLayout.LayoutParams(dpToPx(64), dpToPx(64));
            tileParams.setMargins(dpToPx(6), dpToPx(2), dpToPx(6), dpToPx(2));
            tileView.setLayoutParams(tileParams);
            tileView.setGravity(Gravity.CENTER);
            tileView.setText(token);
            tileView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            tileView.setTypeface(tileView.getTypeface(), android.graphics.Typeface.BOLD);
            tileView.setTextColor(ContextCompat.getColor(this, R.color.m2_text_book_title));
            tileView.setBackgroundResource(R.drawable.m2_bg_spelling_tile);
            tileView.setContentDescription("Khối chữ " + token);

            if (isUsed) {
                tileView.setAlpha(0.28f);
                tileView.setEnabled(false);
            } else {
                tileView.setAlpha(1.0f);
                tileView.setEnabled(true);

                // Hỗ trợ chạm trực tiếp để lắp vào ô trống đầu tiên
                tileView.setOnClickListener(v -> placeTileInFirstEmptySlot(poolIndex));

                // Hỗ trợ kéo thả (Drag and Drop)
                tileView.setOnTouchListener((v, event) -> {
                    if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
                        if (v.getParent() != null) {
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        ClipData clipData = ClipData.newPlainText("spelling_tile_index", String.valueOf(poolIndex));
                        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                        v.startDragAndDrop(clipData, shadowBuilder, poolIndex, 0);
                        return true;
                    }
                    return false;
                });
            }

            llPoolContainer.addView(tileView);
        }
    }

    private boolean handleSlotDragEvent(int slotIndex, TextView slotView, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return event.getLocalState() instanceof Integer;

            case DragEvent.ACTION_DRAG_ENTERED:
                slotView.setBackgroundResource(R.drawable.m2_bg_spelling_slot_hover);
                return true;

            case DragEvent.ACTION_DRAG_EXITED:
            case DragEvent.ACTION_DRAG_ENDED:
                boolean hasTile = slotTileIndices[slotIndex] >= 0;
                if (isCurrentAttemptVerifiedCorrect && hasTile) {
                    slotView.setBackgroundResource(R.drawable.m2_bg_spelling_slot_correct);
                } else {
                    slotView.setBackgroundResource(
                            hasTile ? R.drawable.m2_bg_spelling_slot_filled : R.drawable.m2_bg_spelling_slot_empty
                    );
                }
                return true;

            case DragEvent.ACTION_DROP:
                Object localState = event.getLocalState();
                if (localState instanceof Integer) {
                    int draggedPoolIdx = (Integer) localState;
                    placeTileAtSlot(draggedPoolIdx, slotIndex);
                    return true;
                }
                return false;

            default:
                return true;
        }
    }

    private void placeTileInFirstEmptySlot(int poolIndex) {
        for (int i = 0; i < slotTileIndices.length; i++) {
            if (slotTileIndices[i] == -1) {
                placeTileAtSlot(poolIndex, i);
                return;
            }
        }
    }

    private void placeTileAtSlot(int poolIndex, int targetSlotIndex) {
        placeTileAtSlot(poolIndex, targetSlotIndex, true);
    }

    private void placeTileAtSlot(int poolIndex, int targetSlotIndex, boolean speakSelectedTokenIfPartial) {
        if (poolIndex < 0 || poolIndex >= currentPoolTokens.size()) {
            return;
        }
        if (targetSlotIndex < 0 || targetSlotIndex >= slotTileIndices.length) {
            return;
        }
        if (voiceManager != null) {
            voiceManager.onUserInteraction();
        }

        isCurrentAttemptVerifiedCorrect = false;

        // Đảm bảo một khối gỗ không nằm ở 2 ô cùng lúc
        for (int i = 0; i < slotTileIndices.length; i++) {
            if (slotTileIndices[i] == poolIndex) {
                slotTileIndices[i] = -1;
            }
        }

        slotTileIndices[targetSlotIndex] = poolIndex;
        Member2SoundEffects.playTap(this);
        renderSlotsAndPool();
        animateSinglePlacedSlot(targetSlotIndex);

        // Nếu đã lắp đủ tất cả các ô trên bàn chế tạo thì tự động kiểm tra
        if (areAllSlotsFilled()) {
            checkCurrentAnswer();
        } else if (speakSelectedTokenIfPartial && voiceManager != null) {
            String placedToken = currentPoolTokens.get(poolIndex);
            int filledCount = 0;
            for (int idx : slotTileIndices) {
                if (idx >= 0) {
                    filledCount++;
                }
            }
            if (filledCount == 1 && slotTileIndices.length > 1) {
                voiceManager.speakSequence(
                        Arrays.asList(
                                new Member2VoiceManager.VoiceStep(
                                        "chữ " + placedToken,
                                        Member2VoiceManager.LOCALE_VI,
                                        0L,
                                        null,
                                        null
                                ),
                                new Member2VoiceManager.VoiceStep(
                                        Member2VoiceManager.SPELLING_NEXT_LETTER_SHORT_GUIDE,
                                        Member2VoiceManager.LOCALE_VI,
                                        240L,
                                        "spelling_next_letter_guide",
                                        () -> Member2VoiceManager.highlightTargetView(llPoolContainer)
                                )
                        ),
                        Member2VoiceManager.Priority.KNOWLEDGE_AND_QUESTION,
                        true,
                        null
                );
            } else {
                voiceManager.speakKnowledgeVietnamese("chữ " + placedToken);
            }
        }
    }

    private void animateSinglePlacedSlot(int slotIndex) {
        if (!areAnimationsEnabled() || slotIndex < 0 || slotIndex >= llSlotsContainer.getChildCount()) {
            return;
        }
        View slotView = llSlotsContainer.getChildAt(slotIndex);
        if (slotView != null) {
            slotView.setScaleX(0.84f);
            slotView.setScaleY(0.84f);
            slotView.animate()
                    .scaleX(1.08f)
                    .scaleY(1.08f)
                    .setDuration(110)
                    .withEndAction(() -> slotView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(90).start())
                    .start();
        }
    }

    private boolean areAllSlotsFilled() {
        for (int idx : slotTileIndices) {
            if (idx < 0) {
                return false;
            }
        }
        return true;
    }

    private List<String> getPlacedTokens() {
        List<String> list = new ArrayList<>(slotTileIndices.length);
        for (int idx : slotTileIndices) {
            if (idx >= 0 && idx < currentPoolTokens.size()) {
                list.add(currentPoolTokens.get(idx));
            } else {
                list.add(null);
            }
        }
        return list;
    }

    private void clearAllSlots() {
        isCurrentAttemptVerifiedCorrect = false;
        Arrays.fill(slotTileIndices, -1);
        if (ivMascot != null) {
            ivMascot.setImageResource(R.drawable.m2_mascot_saobong_welcome);
        }
        setFeedbackState(
                getString(R.string.m2_spelling_feedback_prompt),
                R.color.m2_badge_soon_bg,
                R.color.m2_text_primary
        );
        renderSlotsAndPool();
    }

    private void applySmartHint() {
        SpellingExercise exercise = exercises.get(currentExerciseIndex);
        tvHintText.setText(exercise.getHintText());
        if (ivMascot != null) {
            ivMascot.setImageResource(R.drawable.m2_mascot_saobong_hint);
        }

        List<String> currentTokens = getPlacedTokens();
        int helpSlot = SpellingEngine.findFirstSlotNeedingHelp(exercise, currentTokens);
        if (helpSlot < 0) {
            checkCurrentAnswer();
            return;
        }

        String rawNeededToken = exercise.getTargetTokens().get(helpSlot);
        String neededToken = SpellingEngine.normalizeUnicode(rawNeededToken);
        String spokenHint = Member2VoiceManager.buildSpellingHintPrompt(rawNeededToken, helpSlot);
        if (tvMascotSpeech != null) {
            tvMascotSpeech.setText(spokenHint);
        }

        // Chỉ hỗ trợ đúng 1 bước tại ô đầu tiên đang trống hoặc sai
        for (int p = 0; p < currentPoolTokens.size(); p++) {
            String candidate = SpellingEngine.normalizeUnicode(currentPoolTokens.get(p));
            if (neededToken.equals(candidate)) {
                boolean usedInEarlierCorrectSlot = false;
                for (int s = 0; s < helpSlot; s++) {
                    if (slotTileIndices[s] == p) {
                        usedInEarlierCorrectSlot = true;
                        break;
                    }
                }
                if (!usedInEarlierCorrectSlot) {
                    boolean willCompleteAllSlots = countFilledSlotsAfterPlacement(p, helpSlot) == slotTileIndices.length;
                    placeTileAtSlot(p, helpSlot, false);
                    if (!willCompleteAllSlots && voiceManager != null) {
                        voiceManager.speakSequence(
                                Collections.singletonList(new Member2VoiceManager.VoiceStep(
                                        spokenHint,
                                        Member2VoiceManager.LOCALE_VI,
                                        0L,
                                        "spelling_hint_" + exercise.getId() + "_" + helpSlot,
                                        null
                                )),
                                Member2VoiceManager.Priority.ACTION_INSTRUCTION,
                                true,
                                null
                        );
                    }
                    return;
                }
            }
        }
    }

    private int countFilledSlotsAfterPlacement(int poolIndex, int targetSlotIndex) {
        int filled = 0;
        for (int i = 0; i < slotTileIndices.length; i++) {
            int val = slotTileIndices[i];
            if (i == targetSlotIndex) {
                val = poolIndex;
            } else if (val == poolIndex) {
                val = -1;
            }
            if (val >= 0) {
                filled++;
            }
        }
        return filled;
    }

    @NonNull
    private static String buildCelebrationWordSpeech(@NonNull SpellingExercise exercise) {
        Locale vi = Member2VoiceManager.LOCALE_VI;
        String upperWord = exercise.getTargetWord().toUpperCase(vi);
        if ("mèo".equalsIgnoreCase(exercise.getTargetWord())) {
            return "MÈO! Con mèo kêu meo meo!";
        }
        return upperWord + "! " + exercise.getContextPhrase() + "!";
    }

    private void checkCurrentAnswer() {
        SpellingExercise exercise = exercises.get(currentExerciseIndex);
        List<String> placedTokens = getPlacedTokens();

        if (!SpellingEngine.isAttemptComplete(exercise, placedTokens)) {
            setFeedbackState(
                    getString(R.string.m2_spelling_feedback_prompt),
                    R.color.m2_accent_yellow_light,
                    R.color.m2_text_primary
            );
            return;
        }

        if (SpellingEngine.isAnswerCorrect(exercise, placedTokens)) {
            isCurrentAttemptVerifiedCorrect = true;
            Member2SoundEffects.playCorrect(this);
            if (ivMascot != null) {
                ivMascot.setImageResource(R.drawable.m2_mascot_saobong_celebrate);
            }
            if (tvMascotSpeech != null) {
                tvMascotSpeech.setText("Hoan hô! Bé đã chế tạo xong từ \"" + exercise.getTargetWord()
                        + "\" (" + exercise.getContextPhrase() + ")!");
            }
            setFeedbackState(
                    getString(R.string.m2_spelling_feedback_correct_format, exercise.getTargetWord()),
                    R.color.m2_badge_ready_bg,
                    R.color.m2_badge_ready_text
            );
            renderSlotsAndPool();
            animateCorrectSlotsAndIllustration();
            if (voiceManager != null) {
                voiceManager.speakFeedbackSteps(
                        Arrays.asList(
                                new Member2VoiceManager.VoiceStep(
                                        Member2VoiceManager.SPELLING_CORRECT_PRAISE,
                                        Member2VoiceManager.LOCALE_VI,
                                        0L,
                                        "spelling_correct_praise",
                                        null
                                ),
                                new Member2VoiceManager.VoiceStep(
                                        buildCelebrationWordSpeech(exercise),
                                        Member2VoiceManager.LOCALE_VI,
                                        260L,
                                        "spelling_word_celebrate_" + exercise.getId(),
                                        null
                                )
                        ),
                        null
                );
            } else if (voiceFeedback != null) {
                voiceFeedback.speakTextThenCorrect(
                        exercise.getTargetWord() + ". " + exercise.getContextPhrase(),
                        Member2SpeechHelper.LOCALE_VIETNAMESE
                );
            }
            markExerciseCompleted(exercise);
        } else {
            isCurrentAttemptVerifiedCorrect = false;
            Member2SoundEffects.playGentleTry(this);
            if (ivMascot != null) {
                ivMascot.setImageResource(R.drawable.m2_mascot_saobong_hint);
            }
            if (tvMascotSpeech != null) {
                tvMascotSpeech.setText("Chưa khớp rồi! Bé bấm vào khối chữ trên bàn để đổi lại hoặc dùng Gợi ý 1 bước nhé!");
            }
            setFeedbackState(
                    getString(R.string.m2_spelling_feedback_wrong),
                    R.color.m2_accent_pink_light,
                    R.color.m2_text_primary
            );
            shakeSlotsGently();
            if (voiceManager != null) {
                voiceManager.speakFeedbackSteps(
                        Arrays.asList(
                                new Member2VoiceManager.VoiceStep(
                                        Member2VoiceManager.SPELLING_WRONG_PROMPT,
                                        Member2VoiceManager.LOCALE_VI,
                                        0L,
                                        "spelling_wrong_prompt",
                                        null
                                ),
                                new Member2VoiceManager.VoiceStep(
                                        Member2VoiceManager.SPELLING_HINT_BULB_GUIDE,
                                        Member2VoiceManager.LOCALE_VI,
                                        260L,
                                        "spelling_hint_bulb_guide",
                                        () -> Member2VoiceManager.highlightTargetView(btnHint)
                                )
                        ),
                        null
                );
            } else if (voiceFeedback != null) {
                voiceFeedback.speakWrong();
            }
        }
    }

    private void animateCorrectSlotsAndIllustration() {
        if (!areAnimationsEnabled()) {
            return;
        }
        // Các khối chữ sáng lên và nảy nhẹ
        int count = llSlotsContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            View slotView = llSlotsContainer.getChildAt(i);
            if (slotView != null) {
                slotView.animate()
                        .scaleX(1.12f)
                        .scaleY(1.12f)
                        .setStartDelay(i * 55L)
                        .setDuration(130)
                        .withEndAction(() -> slotView.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(130)
                                .start())
                        .start();
            }
        }
        // Hình minh họa chuyển động nhẹ chúc mừng
        if (ivIllustration != null) {
            ivIllustration.animate()
                    .scaleX(1.14f)
                    .scaleY(1.14f)
                    .rotation(-6f)
                    .setDuration(150)
                    .withEndAction(() -> ivIllustration.animate()
                            .rotation(6f)
                            .setDuration(150)
                            .withEndAction(() -> ivIllustration.animate()
                                    .scaleX(1.0f)
                                    .scaleY(1.0f)
                                    .rotation(0f)
                                    .setDuration(150)
                                    .start())
                            .start())
                    .start();
        }
    }

    private void markExerciseCompleted(SpellingExercise exercise) {
        completedExerciseIds.add(exercise.getId());
        setGameProgress(completedExerciseIds.size(), exercises.size());
        updateExerciseStatusBadge();
        updateExerciseSelectorStrip();

        progressRepository.markItemCompleted(
                Member2ContentCatalog.GAME_ID_SPELLING,
                exercise.getId(),
                exercises.size(),
                2,
                updated -> setGameProgress(updated.getCompletedLevels(), exercises.size())
        );
    }

    private void speakCurrentTargetWord() {
        SpellingExercise exercise = exercises.get(currentExerciseIndex);
        if (soundManager != null && soundManager.isSfxMuted()) {
            Toast.makeText(this, R.string.m2_alphabet_toast_muted, Toast.LENGTH_SHORT).show();
            return;
        }
        if (voiceManager != null) {
            voiceManager.onUserInteraction();
            voiceManager.speakSequence(
                    Arrays.asList(
                            new Member2VoiceManager.VoiceStep(
                                    Member2VoiceManager.buildSpellingQuestionPrompt(exercise.getTargetWord()),
                                    Member2VoiceManager.LOCALE_VI,
                                    0L,
                                    "spelling_q_" + exercise.getId(),
                                    null
                            ),
                            new Member2VoiceManager.VoiceStep(
                                    exercise.getTargetWord() + "! " + exercise.getContextPhrase(),
                                    Member2VoiceManager.LOCALE_VI,
                                    280L,
                                    "spelling_word_" + exercise.getId(),
                                    null
                            )
                    ),
                    Member2VoiceManager.Priority.KNOWLEDGE_AND_QUESTION,
                    true,
                    null
            );
            return;
        }
        speechHelper.speak(
                exercise.getTargetWord() + ". " + exercise.getContextPhrase(),
                Member2SpeechHelper.LOCALE_VIETNAMESE,
                "spelling_" + exercise.getId()
        );
    }

    private void setFeedbackState(String message, int bgColorRes, int textColorRes) {
        tvFeedback.setText(message);
        tvFeedback.setTextColor(ContextCompat.getColor(this, textColorRes));
        tvFeedback.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(this, bgColorRes)
        ));
    }

    private void shakeSlotsGently() {
        if (!areAnimationsEnabled()) {
            return;
        }
        llSlotsContainer.animate()
                .translationX(12f)
                .setDuration(60)
                .withEndAction(() -> llSlotsContainer.animate()
                        .translationX(-12f)
                        .setDuration(60)
                        .withEndAction(() -> llSlotsContainer.animate()
                                .translationX(0f)
                                .setDuration(60)
                                .start())
                        .start())
                .start();
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

    private void showAllExercisesVictoryDialog() {
        Member2SoundEffects.playCelebrate(this);
        if (voiceFeedback != null) {
            voiceFeedback.speakCompletionNow();
        }
        int stars = completedExerciseIds.size() >= exercises.size()
                ? 3
                : (completedExerciseIds.size() >= 8 ? 2 : 1);
        showVictoryDialog(
                stars,
                this::finish,
                () -> loadExercise(0, 1001L, null)
        );
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
        outState.putInt(STATE_EXERCISE_INDEX, currentExerciseIndex);
        outState.putLong(STATE_POOL_SEED, currentPoolSeed);
        outState.putIntArray(STATE_SLOT_TILE_INDICES, slotTileIndices);
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
                        getResources().getDisplayMetrics()
                )
        );
    }
}
