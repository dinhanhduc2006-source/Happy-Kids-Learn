package com.example.happykidlearn.member2.dashboard;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happykidlearn.R;
import com.example.happykidlearn.activities.ParentalSettingsActivity;
import com.example.happykidlearn.db.GameProgress;
import com.example.happykidlearn.member2.common.Member2ContentCatalog;
import com.example.happykidlearn.member2.common.Member2ProgressRepository;
import com.example.happykidlearn.member2.common.Member2SoundEffects;
import com.example.happykidlearn.member2.common.Member2SpeechHelper;
import com.example.happykidlearn.models.GameModel;
import com.example.happykidlearn.registry.GameRegistry;
import com.example.happykidlearn.utils.ParentalGateDialog;
import com.example.happykidlearn.utils.SoundManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Member2HomeActivity extends AppCompatActivity {

    private static final String STATE_SELECTED_CATEGORY = "state_selected_category";
    private static final String CATEGORY_ALL = "ALL";

    private ImageButton btnSound;
    private ImageButton btnParent;
    private View llParentContainer;
    private TextView tvTotalCompleted;
    private TextView tvTotalStars;
    private ImageView ivHeroStar1;
    private ImageView ivHeroStar2;
    private ImageView ivHeroStar3;
    private ImageView ivMascot;
    private View llMascotBadge;
    private View btnHeroExplore;
    private LinearLayout llCategoryChips;
    private View llContinueCard;
    private ImageView ivContinueMascot;
    private TextView tvContinueTitle;
    private TextView tvContinueSubtitle;
    private TextView btnContinueAction;
    private RecyclerView rvGames;
    private View layoutEmptyState;
    private Button btnEmptyReset;

    private GameGridAdapter gridAdapter;
    private Member2ProgressRepository progressRepository;
    private SoundManager soundManager;
    @Nullable
    private ObjectAnimator mascotPulseAnimator;
    @Nullable
    private GameModel recommendedGame;

    private final List<GameModel> registryGames = new ArrayList<>();
    private Map<String, GameProgress> latestProgressMap = Collections.emptyMap();
    private String selectedCategory = CATEGORY_ALL;
    private boolean isSoundEnabled = true;
    private boolean isResumedState = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m2_activity_home);

        if (savedInstanceState != null) {
            selectedCategory = savedInstanceState.getString(STATE_SELECTED_CATEGORY, CATEGORY_ALL);
        }

        soundManager = SoundManager.getInstance(this);
        progressRepository = new Member2ProgressRepository(this);
        Member2SoundEffects.preloadAll(this);
        Member2SpeechHelper.warmUp(this);

        initViews();
        applyWindowInsets();
        loadRegistryGames();
        buildCategoryChips();
        setupRecyclerView();
        rebuildGridModels();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResumedState = true;
        refreshProgressAndSettings();
        startMascotGentleAnimationIfAllowed();
    }

    @Override
    protected void onPause() {
        isResumedState = false;
        stopMascotGentleAnimation();
        soundManager.pauseBgm();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        stopMascotGentleAnimation();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_SELECTED_CATEGORY, selectedCategory);
    }

    private void initViews() {
        btnSound = findViewById(R.id.btn_m2_home_sound);
        btnParent = findViewById(R.id.btn_m2_home_parent);
        llParentContainer = findViewById(R.id.ll_m2_home_parent_container);
        tvTotalCompleted = findViewById(R.id.tv_m2_total_completed);
        tvTotalStars = findViewById(R.id.tv_m2_total_stars);
        ivHeroStar1 = findViewById(R.id.iv_m2_hero_star1);
        ivHeroStar2 = findViewById(R.id.iv_m2_hero_star2);
        ivHeroStar3 = findViewById(R.id.iv_m2_hero_star3);
        ivMascot = findViewById(R.id.iv_m2_mascot);
        llMascotBadge = findViewById(R.id.ll_m2_mascot_badge);
        btnHeroExplore = findViewById(R.id.btn_m2_hero_explore);
        llCategoryChips = findViewById(R.id.ll_m2_category_chips);
        llContinueCard = findViewById(R.id.ll_m2_continue_card);
        ivContinueMascot = findViewById(R.id.iv_m2_continue_mascot);
        tvContinueTitle = findViewById(R.id.tv_m2_continue_title);
        tvContinueSubtitle = findViewById(R.id.tv_m2_continue_subtitle);
        btnContinueAction = findViewById(R.id.btn_m2_continue_action);
        rvGames = findViewById(R.id.rv_m2_games);
        layoutEmptyState = findViewById(R.id.layout_m2_empty_state);
        btnEmptyReset = findViewById(R.id.btn_m2_empty_reset);

        btnSound.setOnClickListener(v -> onSoundButtonClicked());
        if (btnParent != null) {
            btnParent.setOnClickListener(v -> onParentButtonClicked());
        }
        if (llParentContainer != null) {
            llParentContainer.setOnClickListener(v -> onParentButtonClicked());
        }

        if (btnHeroExplore != null) {
            btnHeroExplore.setOnClickListener(v -> {
                Member2SoundEffects.playOpenBook(this);
                animateButtonPress(v, this::openRecommendedOrFirstPlayableGame);
            });
        }

        if (llContinueCard != null) {
            llContinueCard.setOnClickListener(v -> {
                Member2SoundEffects.playOpenBook(this);
                animateButtonPress(v, this::openRecommendedOrFirstPlayableGame);
            });
        }

        if (llMascotBadge != null) {
            llMascotBadge.setOnClickListener(v -> {
                Member2SoundEffects.playCelebrate(this);
                if (ivMascot != null) {
                    ivMascot.setImageResource(R.drawable.m2_mascot_saobong_happy);
                }
            });
        }

        btnEmptyReset.setOnClickListener(v -> {
            Member2SoundEffects.playTap(this);
            selectedCategory = CATEGORY_ALL;
            updateCategoryChipsSelection();
            rebuildGridModels();
        });
    }

    private void applyWindowInsets() {
        View root = findViewById(R.id.m2_home_root);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                return insets;
            });
        }
    }

    private void loadRegistryGames() {
        registryGames.clear();
        List<GameModel> fromRegistry = GameRegistry.getInstance().getGames();
        if (fromRegistry != null) {
            registryGames.addAll(fromRegistry);
        }
    }

    private void setupRecyclerView() {
        int spanCount = calculateResponsiveSpanCount();
        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        rvGames.setLayoutManager(layoutManager);
        rvGames.setHasFixedSize(false);
        gridAdapter = new GameGridAdapter(item ->
                Member2GameRouter.navigateToGame(this, item.getGameModel())
        );
        rvGames.setAdapter(gridAdapter);
    }

    private int calculateResponsiveSpanCount() {
        Configuration config = getResources().getConfiguration();
        boolean isWideScreen = config.screenWidthDp >= 600
                || config.orientation == Configuration.ORIENTATION_LANDSCAPE;
        return isWideScreen ? 3 : 2;
    }

    private void buildCategoryChips() {
        llCategoryChips.removeAllViews();

        Set<String> dynamicCategories = new LinkedHashSet<>();
        for (GameModel game : registryGames) {
            if (game.getCategory() != null && !game.getCategory().trim().isEmpty()) {
                dynamicCategories.add(game.getCategory().trim());
            }
        }

        for (String category : dynamicCategories) {
            addCategoryChip(category, Member2ContentCatalog.resolveCategoryDisplayLabel(category));
        }
        addCategoryChip(
                CATEGORY_ALL,
                getString(R.string.m2_home_category_all, registryGames.size())
        );
        updateCategoryChipsSelection();
    }

    private void addCategoryChip(@NonNull String categoryKey, @NonNull String label) {
        LinearLayout pill = new LinearLayout(this);
        pill.setTag(categoryKey);
        pill.setOrientation(LinearLayout.HORIZONTAL);
        pill.setGravity(Gravity.CENTER_VERTICAL);
        pill.setMinimumHeight(dpToPx(42));
        pill.setPadding(dpToPx(12), dpToPx(6), dpToPx(14), dpToPx(8));
        pill.setBackgroundResource(Member2ContentCatalog.resolveCategoryPillBackground(categoryKey));

        ImageView icon = new ImageView(this);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dpToPx(20), dpToPx(20));
        iconParams.setMarginEnd(dpToPx(6));
        icon.setLayoutParams(iconParams);
        icon.setImageResource(Member2ContentCatalog.resolveCategoryIcon(categoryKey));
        icon.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);

        TextView text = new TextView(this);
        text.setText(label);
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13.5f);
        text.setTypeface(Typeface.create("sans-serif-black", Typeface.BOLD));
        text.setTextColor(ContextCompat.getColor(this, R.color.m2_text_primary));

        pill.addView(icon);
        pill.addView(text);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMarginEnd(dpToPx(8));
        pill.setLayoutParams(params);

        pill.setOnClickListener(v -> {
            Member2SoundEffects.playTap(this);
            if ( categoryKey.equals(selectedCategory) && !CATEGORY_ALL.equals(categoryKey)) {
                selectedCategory = CATEGORY_ALL;
            } else {
                selectedCategory = categoryKey;
            }
            animateButtonPress(v, () -> {
                updateCategoryChipsSelection();
                rebuildGridModels();
            });
        });

        llCategoryChips.addView(pill);
    }

    private void updateCategoryChipsSelection() {
        int count = llCategoryChips.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = llCategoryChips.getChildAt(i);
            Object tag = child.getTag();
            boolean isSelected = selectedCategory.equals(tag);
            child.setAlpha(isSelected || CATEGORY_ALL.equals(selectedCategory) ? 1.0f : 0.72f);
            child.setScaleX(isSelected ? 1.04f : 1.0f);
            child.setScaleY(isSelected ? 1.04f : 1.0f);
            child.setElevation(isSelected ? dpToPx(3) : 0f);
        }
    }

    private void refreshProgressAndSettings() {
        progressRepository.loadDashboardSnapshot(snapshot -> {
            latestProgressMap = snapshot.getProgressByGameId();

            boolean soundOn = snapshot.getAppSettings().isSoundEnabled()
                    || snapshot.getAppSettings().isBgmEnabled();
            isSoundEnabled = soundOn;
            updateSoundButtonIcon();
            if (isSoundEnabled && isResumedState) {
                Member2SoundEffects.ensureGentleBgmPlaying(this);
            } else {
                Member2SoundEffects.stopOrMuteBgm(this);
            }

            int totalCompleted = snapshot.getTotalCompletedLevels();
            int totalStars = snapshot.getTotalStarsEarned();

            tvTotalCompleted.setText(getString(
                    R.string.m2_home_summary_lessons_format,
                    totalCompleted
            ));
            tvTotalStars.setText(getString(
                    R.string.m2_home_summary_stars_format,
                    totalStars
            ));

            updateHeroStarsAndMascot(totalCompleted, totalStars);
            updateContinueLearningSection();
            rebuildGridModels();
        });
    }

    private void updateHeroStarsAndMascot(int totalCompleted, int totalStars) {
        int litStars;
        if (totalCompleted >= 10 || totalStars >= 10) {
            litStars = 3;
        } else if (totalCompleted >= 3 || totalStars >= 3) {
            litStars = 2;
        } else if (totalCompleted >= 1 || totalStars >= 1) {
            litStars = 1;
        } else {
            litStars = 0;
        }

        if (ivHeroStar1 != null) {
            ivHeroStar1.setImageResource(litStars >= 1 ? R.drawable.ic_star_filled : R.drawable.ic_star_empty);
        }
        if (ivHeroStar2 != null) {
            ivHeroStar2.setImageResource(litStars >= 2 ? R.drawable.ic_star_filled : R.drawable.ic_star_empty);
        }
        if (ivHeroStar3 != null) {
            ivHeroStar3.setImageResource(litStars >= 3 ? R.drawable.ic_star_filled : R.drawable.ic_star_empty);
        }

        if (ivMascot != null) {
            if (litStars >= 3) {
                ivMascot.setImageResource(R.drawable.m2_mascot_saobong_celebrate);
            } else if (litStars >= 1) {
                ivMascot.setImageResource(R.drawable.m2_mascot_saobong_happy);
            } else {
                ivMascot.setImageResource(R.drawable.m2_mascot_saobong_welcome);
            }
        }
    }

    private void updateContinueLearningSection() {
        GameModel activeInProgressGame = null;
        int activeCompleted = 0;
        int activeTotal = 0;
        int activeStars = 0;

        GameModel firstPlayableFallback = null;

        for (GameModel game : registryGames) {
            boolean isReady = Member2GameRouter.resolveTargetActivity(game) != null;
            if (!isReady) {
                continue;
            }
            if (firstPlayableFallback == null) {
                firstPlayableFallback = game;
            }

            GameProgress gp = latestProgressMap.get(game.getId());
            int completed = gp != null ? gp.getCompletedLevels() : 0;
            int total = gp != null && gp.getTotalLevels() > 0
                    ? gp.getTotalLevels()
                    : Member2ContentCatalog.getDefaultTotalLevels(game.getId());
            int stars = gp != null ? gp.getStarsEarned() : 0;

            if (completed > 0 && (activeInProgressGame == null || completed < total)) {
                activeInProgressGame = game;
                activeCompleted = completed;
                activeTotal = total;
                activeStars = stars;
                if (completed < total) {
                    break;
                }
            }
        }

        if (activeInProgressGame != null) {
            recommendedGame = activeInProgressGame;
            if (tvContinueTitle != null) {
                tvContinueTitle.setText(R.string.m2_home_continue_header_active);
            }
            if (tvContinueSubtitle != null) {
                tvContinueSubtitle.setText(getString(
                        R.string.m2_home_continue_subtitle_active,
                        activeInProgressGame.getTitle(),
                        activeCompleted,
                        activeTotal,
                        activeStars
                ));
            }
            if (btnContinueAction != null) {
                btnContinueAction.setText(R.string.m2_home_continue_btn_active);
            }
            if (ivContinueMascot != null) {
                ivContinueMascot.setImageResource(R.drawable.m2_mascot_saobong_happy);
            }
        } else {
            recommendedGame = firstPlayableFallback;
            String starterTitle = firstPlayableFallback != null ? firstPlayableFallback.getTitle() : "Chữ Cái";
            if (tvContinueTitle != null) {
                tvContinueTitle.setText(R.string.m2_home_continue_header_starter);
            }
            if (tvContinueSubtitle != null) {
                tvContinueSubtitle.setText(getString(R.string.m2_home_continue_subtitle_starter, starterTitle));
            }
            if (btnContinueAction != null) {
                btnContinueAction.setText(R.string.m2_home_continue_btn_starter);
            }
            if (ivContinueMascot != null) {
                ivContinueMascot.setImageResource(R.drawable.m2_mascot_saobong_hint);
            }
        }
    }

    private void openRecommendedOrFirstPlayableGame() {
        if (recommendedGame != null) {
            Member2GameRouter.navigateToGame(this, recommendedGame);
            return;
        }
        for (GameModel game : registryGames) {
            if (Member2GameRouter.resolveTargetActivity(game) != null) {
                Member2GameRouter.navigateToGame(this, game);
                return;
            }
        }
    }

    private void rebuildGridModels() {
        List<HomeGameUiModel> readyModels = new ArrayList<>();
        List<HomeGameUiModel> upcomingModels = new ArrayList<>();

        for (GameModel game : registryGames) {
            if (!CATEGORY_ALL.equals(selectedCategory)) {
                String cat = game.getCategory() != null ? game.getCategory().trim() : "";
                if (!cat.equalsIgnoreCase(selectedCategory)) {
                    continue;
                }
            }

            GameProgress progress = latestProgressMap.get(game.getId());
            int completed = progress != null ? progress.getCompletedLevels() : 0;
            int stars = progress != null ? progress.getStarsEarned() : 0;
            int roomTotal = progress != null ? progress.getTotalLevels() : 0;
            int catalogTotal = Member2ContentCatalog.getDefaultTotalLevels(game.getId());
            int resolvedTotal = roomTotal > 0 ? roomTotal : catalogTotal;
            boolean hasKnownTotal = resolvedTotal > 0;

            int iconRes = Member2ContentCatalog.resolveGameIcon(game);
            int bookCoverColor = Member2ContentCatalog.resolveBookCoverColor(game);
            int softBgColor = Member2ContentCatalog.resolveSoftBackgroundTint(bookCoverColor);
            boolean isReady = Member2GameRouter.resolveTargetActivity(game) != null;

            HomeGameUiModel model = new HomeGameUiModel(
                    game,
                    iconRes,
                    bookCoverColor,
                    softBgColor,
                    completed,
                    resolvedTotal,
                    stars,
                    hasKnownTotal,
                    isReady
            );

            if (isReady) {
                readyModels.add(model);
            } else {
                upcomingModels.add(model);
            }
        }

        // Sắp xếp 4 sách trò chơi đã hoàn thiện của Thành viên 2 theo đúng thứ tự:
        // Chữ Cái -> Ghép Vần -> Tiếng Anh -> Đọc Truyện, tiếp theo là 11 sách Sắp ra mắt
        readyModels.sort((a, b) -> Integer.compare(
                getFeaturedBookOrder(a.getGameModel().getId()),
                getFeaturedBookOrder(b.getGameModel().getId())
        ));

        List<HomeGameUiModel> uiModels = new ArrayList<>(readyModels.size() + upcomingModels.size());
        uiModels.addAll(readyModels);
        uiModels.addAll(upcomingModels);

        if (gridAdapter != null) {
            gridAdapter.submitList(uiModels);
        }
        boolean isEmpty = uiModels.isEmpty();
        rvGames.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        layoutEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private int getFeaturedBookOrder(@Nullable String gameId) {
        if (gameId == null) {
            return 99;
        }
        switch (gameId.toLowerCase()) {
            case Member2ContentCatalog.GAME_ID_ALPHABET:
                return 1;
            case Member2ContentCatalog.GAME_ID_SPELLING:
                return 2;
            case Member2ContentCatalog.GAME_ID_ENGLISH:
                return 3;
            case Member2ContentCatalog.GAME_ID_STORYBOOK:
                return 4;
            default:
                return 50;
        }
    }

    private void onSoundButtonClicked() {
        isSoundEnabled = !isSoundEnabled;
        updateSoundButtonIcon();
        progressRepository.toggleSoundAndPersist(isSoundEnabled, saved -> {
            if (isSoundEnabled) {
                Member2SoundEffects.playTap(this);
                Member2SoundEffects.ensureGentleBgmPlaying(this);
            } else {
                Member2SoundEffects.stopOrMuteBgm(this);
            }
            Toast.makeText(
                    this,
                    isSoundEnabled ? R.string.m2_toast_sound_on : R.string.m2_toast_sound_off,
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    private void updateSoundButtonIcon() {
        if (btnSound != null) {
            btnSound.setImageResource(isSoundEnabled ? R.drawable.ic_sound_on : R.drawable.ic_sound_off);
        }
    }

    private void onParentButtonClicked() {
        Member2SoundEffects.playTap(this);
        ParentalGateDialog gateDialog = new ParentalGateDialog(this, () -> {
            Intent intent = new Intent(Member2HomeActivity.this, ParentalSettingsActivity.class);
            startActivity(intent);
        });
        gateDialog.show();
    }

    private void startMascotGentleAnimationIfAllowed() {
        stopMascotGentleAnimation();
        if (llMascotBadge == null || isReducedMotionEnabled()) {
            return;
        }
        PropertyValuesHolder translateY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 0f, -dpToPx(4));
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 1.03f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 1.03f);
        mascotPulseAnimator = ObjectAnimator.ofPropertyValuesHolder(llMascotBadge, translateY, scaleX, scaleY);
        mascotPulseAnimator.setDuration(1600L);
        mascotPulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mascotPulseAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mascotPulseAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mascotPulseAnimator.start();
    }

    private void stopMascotGentleAnimation() {
        if (mascotPulseAnimator != null) {
            mascotPulseAnimator.cancel();
            mascotPulseAnimator = null;
        }
        if (llMascotBadge != null) {
            llMascotBadge.setTranslationY(0f);
            llMascotBadge.setScaleX(1f);
            llMascotBadge.setScaleY(1f);
        }
    }

    private boolean isReducedMotionEnabled() {
        try {
            float scale = Settings.Global.getFloat(
                    getContentResolver(),
                    Settings.Global.ANIMATOR_DURATION_SCALE,
                    1.0f
            );
            return scale == 0.0f;
        } catch (Exception ignored) {
            return false;
        }
    }

    private void animateButtonPress(@NonNull View view, @NonNull Runnable endAction) {
        view.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(70)
                .withEndAction(() -> view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(80)
                        .withEndAction(endAction)
                        .start())
                .start();
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
