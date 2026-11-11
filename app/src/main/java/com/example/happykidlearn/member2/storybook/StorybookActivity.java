package com.example.happykidlearn.member2.storybook;

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

import androidx.activity.OnBackPressedCallback;
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
import com.example.happykidlearn.member2.common.Member2VoiceFeedback;
import com.example.happykidlearn.member2.common.Member2VoiceManager;
import com.google.android.material.card.MaterialCardView;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StorybookActivity extends BaseGameActivity implements NarrationController.Listener {

    private static final String STATE_IN_READER = "state_story_in_reader";
    private static final String STATE_STORY_INDEX = "state_story_index";
    private static final String STATE_PAGE_INDEX = "state_story_page_index";
    private static final String STATE_WELCOME_PLAYED = "state_story_welcome_played";

    private ImageView ivMascot;
    private TextView tvBannerText;

    private View layoutLibrary;
    private LinearLayout llStoryList;

    private View layoutReader;
    private Button btnBackLibrary;
    private TextView tvCurrentStoryTitle;
    private TextView tvPageIndicator;
    private ViewPager2 vpPages;
    private Button btnPlayPause;
    private Button btnReplay;
    private Button btnPrevPage;
    private Button btnNextPage;

    private List<StoryContent.StoryBook> stories;
    private Member2ProgressRepository progressRepository;
    private NarrationController narrationController;
    private StoryPageAdapter pageAdapter;
    private Member2VoiceFeedback voiceFeedback;
    private Member2VoiceManager voiceManager;

    private final Set<String> completedStoryIds = new HashSet<>();
    private boolean isInReaderMode = false;
    private int currentStoryIndex = 0;
    private int currentPageIndex = 0;
    private boolean hasPlayedWelcomeGreeting = false;
    private boolean suppressPageSelectedAutoNarrate = false;
    private boolean hasGuidedPageTurnInCurrentStory = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m2_activity_storybook);

        setupHeader(getString(R.string.m2_storybook_header_title));
        applyWindowInsets();

        stories = StoryContent.getAllStories();
        progressRepository = new Member2ProgressRepository(this);
        Member2SoundEffects.preloadAll(this);
        narrationController = new NarrationController(this);
        narrationController.setListener(this);
        voiceFeedback = new Member2VoiceFeedback(this);
        voiceManager = new Member2VoiceManager(this);

        initViews();
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isInReaderMode) {
                    showLibraryScreen(true);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        if (savedInstanceState != null) {
            isInReaderMode = savedInstanceState.getBoolean(STATE_IN_READER, false);
            currentStoryIndex = savedInstanceState.getInt(STATE_STORY_INDEX, 0);
            currentPageIndex = savedInstanceState.getInt(STATE_PAGE_INDEX, 0);
            hasPlayedWelcomeGreeting = savedInstanceState.getBoolean(STATE_WELCOME_PLAYED, false);
        }

        renderStoryLibraryCards();
        if (isInReaderMode) {
            openStoryAtPage(currentStoryIndex, currentPageIndex, false);
        } else {
            showLibraryScreen(false);
        }

        loadSavedProgress();
    }

    private void applyWindowInsets() {
        View root = findViewById(R.id.m2_storybook_root);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                return insets;
            });
        }
    }

    private void initViews() {
        ivMascot = findViewById(R.id.iv_m2_storybook_mascot);
        tvBannerText = findViewById(R.id.tv_m2_storybook_banner_text);

        layoutLibrary = findViewById(R.id.layout_m2_story_library);
        llStoryList = findViewById(R.id.ll_m2_story_list);

        layoutReader = findViewById(R.id.layout_m2_story_reader);
        btnBackLibrary = findViewById(R.id.btn_m2_story_back_library);
        tvCurrentStoryTitle = findViewById(R.id.tv_m2_story_current_title);
        tvPageIndicator = findViewById(R.id.tv_m2_story_page_indicator);
        vpPages = findViewById(R.id.vp_m2_story_pages);
        btnPlayPause = findViewById(R.id.btn_m2_story_play_pause);
        btnReplay = findViewById(R.id.btn_m2_story_replay);
        btnPrevPage = findViewById(R.id.btn_m2_story_prev_page);
        btnNextPage = findViewById(R.id.btn_m2_story_next_page);

        View btnBannerVoiceReplay = findViewById(R.id.btn_m2_voice_replay);
        TextView tvGuidanceToggle = findViewById(R.id.tv_m2_voice_guidance_toggle);
        if (voiceManager != null) {
            voiceManager.bindViews(ivMascot, btnBannerVoiceReplay, tvGuidanceToggle);
        }
        View.OnClickListener smartReplayListener = v -> {
            Member2SoundEffects.playTap(this);
            if (voiceManager != null) {
                voiceManager.onUserInteraction();
            }
            if (soundManager != null && soundManager.isSfxMuted()) {
                Toast.makeText(this, R.string.m2_alphabet_toast_muted, Toast.LENGTH_SHORT).show();
                return;
            }
            if (isInReaderMode) {
                if (voiceManager != null) {
                    voiceManager.stop();
                }
                StoryPage page = getCurrentPage();
                narrationController.startFromBeginning(currentPageIndex, page.getPageText());
            } else if (voiceManager != null) {
                voiceManager.replayCurrentInstruction();
            }
        };
        if (btnBannerVoiceReplay != null) {
            btnBannerVoiceReplay.setOnClickListener(smartReplayListener);
        }
        if (ivMascot != null) {
            ivMascot.setOnClickListener(smartReplayListener);
        }

        btnBackLibrary.setOnClickListener(v -> showLibraryScreen(true));

        btnPlayPause.setOnClickListener(v -> {
            if (voiceManager != null) {
                voiceManager.onUserInteraction();
                voiceManager.stop();
            }
            Member2SoundEffects.playTap(this);
            if (soundManager != null && soundManager.isSfxMuted()) {
                Toast.makeText(this, R.string.m2_alphabet_toast_muted, Toast.LENGTH_SHORT).show();
                return;
            }
            StoryPage page = getCurrentPage();
            narrationController.togglePlayPause(currentPageIndex, page.getPageText());
        });

        btnReplay.setOnClickListener(v -> {
            if (voiceManager != null) {
                voiceManager.onUserInteraction();
                voiceManager.stop();
            }
            Member2SoundEffects.playTap(this);
            if (soundManager != null && soundManager.isSfxMuted()) {
                Toast.makeText(this, R.string.m2_alphabet_toast_muted, Toast.LENGTH_SHORT).show();
                return;
            }
            StoryPage page = getCurrentPage();
            narrationController.startFromBeginning(currentPageIndex, page.getPageText());
        });

        btnPrevPage.setOnClickListener(v -> {
            if (currentPageIndex > 0) {
                Member2SoundEffects.playOpenBook(this);
                vpPages.setCurrentItem(currentPageIndex - 1, true);
            }
        });

        btnNextPage.setOnClickListener(v -> {
            StoryContent.StoryBook currentStory = stories.get(currentStoryIndex);
            if (currentPageIndex < currentStory.getPages().size() - 1) {
                Member2SoundEffects.playOpenBook(this);
                vpPages.setCurrentItem(currentPageIndex + 1, true);
            } else {
                completeCurrentStory(currentStory);
            }
        });

        // Hiệu ứng lật trang sách 2.5D mềm mại
        vpPages.setPageTransformer((page, position) -> {
            float absPos = Math.abs(position);
            page.setAlpha(1.0f - (absPos * 0.28f));
            page.setScaleY(1.0f - (absPos * 0.08f));
            page.setRotationY(position * -12f);
        });

        vpPages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Dừng ngay phát âm của trang trước để không bao giờ phát chồng tiếng
                narrationController.stop();
                if (voiceManager != null) {
                    voiceManager.stop();
                    voiceManager.onUserInteraction();
                }
                currentPageIndex = position;
                updateReaderPageControls();
                if (!suppressPageSelectedAutoNarrate && isInReaderMode) {
                    if (soundManager == null || !soundManager.isSfxMuted()) {
                        StoryPage page = getCurrentPage();
                        narrationController.startFromBeginning(currentPageIndex, page.getPageText());
                    }
                }
            }
        });
    }

    private void renderStoryLibraryCards() {
        llStoryList.removeAllViews();
        for (int i = 0; i < stories.size(); i++) {
            final int storyIdx = i;
            StoryContent.StoryBook story = stories.get(i);
            boolean isRead = completedStoryIds.contains(story.getId());
            int accentColor = ContextCompat.getColor(this, story.getAccentColorResId());

            MaterialCardView card = new MaterialCardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardParams.setMargins(0, dpToPx(6), 0, dpToPx(8));
            card.setLayoutParams(cardParams);
            card.setRadius(dpToPx(24));
            card.setCardElevation(0f);
            card.setStrokeWidth(0);
            card.setCardBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            card.setClickable(true);
            card.setFocusable(true);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setBackgroundResource(R.drawable.m2_bg_alphabet_book_card);
            row.setPadding(dpToPx(24), dpToPx(14), dpToPx(16), dpToPx(16));

            ImageView ivCover = new ImageView(this);
            LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(dpToPx(78), dpToPx(78));
            ivCover.setLayoutParams(ivParams);
            ivCover.setBackgroundResource(R.drawable.m2_bg_alphabet_illust_frame);
            int coverPad = dpToPx(8);
            ivCover.setPadding(coverPad, coverPad, coverPad, coverPad);
            ivCover.setImageResource(story.getCoverIllustrationResId());
            ivCover.setContentDescription(story.getTitle());
            row.addView(ivCover);

            LinearLayout textCol = new LinearLayout(this);
            textCol.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams colParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            colParams.setMarginStart(dpToPx(14));
            textCol.setLayoutParams(colParams);

            TextView tvTitle = new TextView(this);
            tvTitle.setText(story.getTitle());
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            tvTitle.setTypeface(tvTitle.getTypeface(), android.graphics.Typeface.BOLD);
            tvTitle.setTextColor(accentColor);
            textCol.addView(tvTitle);

            TextView tvDesc = new TextView(this);
            tvDesc.setText(story.getShortDescription());
            tvDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            tvDesc.setTextColor(ContextCompat.getColor(this, R.color.m2_text_secondary));
            tvDesc.setPadding(0, dpToPx(4), 0, dpToPx(8));
            textCol.addView(tvDesc);

            LinearLayout badgeRow = new LinearLayout(this);
            badgeRow.setOrientation(LinearLayout.HORIZONTAL);
            badgeRow.setGravity(Gravity.CENTER_VERTICAL);

            TextView tvPageCount = new TextView(this);
            tvPageCount.setText("📖 " + getString(R.string.m2_storybook_page_count_format, story.getPages().size()));
            tvPageCount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            tvPageCount.setTypeface(tvPageCount.getTypeface(), android.graphics.Typeface.BOLD);
            tvPageCount.setBackgroundResource(R.drawable.m2_bg_star_pill);
            tvPageCount.setPadding(dpToPx(10), dpToPx(3), dpToPx(10), dpToPx(4));
            tvPageCount.setTextColor(ContextCompat.getColor(this, R.color.m2_text_primary));
            badgeRow.addView(tvPageCount);

            TextView tvStatus = new TextView(this);
            LinearLayout.LayoutParams statusParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            statusParams.setMarginStart(dpToPx(8));
            tvStatus.setLayoutParams(statusParams);
            String statusText = isRead
                    ? getString(R.string.m2_storybook_status_read)
                    : getString(R.string.m2_storybook_status_unread);
            tvStatus.setText(statusText);
            tvStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            tvStatus.setTypeface(tvStatus.getTypeface(), android.graphics.Typeface.BOLD);
            tvStatus.setBackgroundResource(R.drawable.m2_bg_badge_pill);
            tvStatus.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(
                    this,
                    isRead ? R.color.m2_badge_ready_bg : R.color.m2_badge_soon_bg
            )));
            tvStatus.setPadding(dpToPx(10), dpToPx(3), dpToPx(10), dpToPx(4));
            tvStatus.setTextColor(ContextCompat.getColor(
                    this,
                    isRead ? R.color.m2_badge_ready_text : R.color.m2_badge_soon_text
            ));
            badgeRow.addView(tvStatus);

            textCol.addView(badgeRow);
            row.addView(textCol);
            card.addView(row);

            card.setOnClickListener(v -> {
                Member2SoundEffects.playOpenBook(this);
                openStoryAtPage(storyIdx, 0, true);
            });
            llStoryList.addView(card);
        }
    }

    private void showLibraryScreen() {
        showLibraryScreen(true);
    }

    private void showLibraryScreen(boolean fromUserBackAction) {
        if (fromUserBackAction) {
            Member2SoundEffects.playTap(this);
        }
        narrationController.stop();
        if (voiceManager != null) {
            voiceManager.stop();
        }
        isInReaderMode = false;
        layoutLibrary.setVisibility(View.VISIBLE);
        layoutReader.setVisibility(View.GONE);
        if (ivMascot != null) {
            ivMascot.setImageResource(R.drawable.m2_mascot_saobong_welcome);
        }
        if (tvBannerText != null) {
            tvBannerText.setText(R.string.m2_storybook_library_subtitle);
        }
        renderStoryLibraryCards();

        if (voiceManager != null) {
            View firstStoryCard = llStoryList.getChildCount() > 0 ? llStoryList.getChildAt(0) : null;
            List<Member2VoiceManager.VoiceStep> libInstruction = Arrays.asList(
                    new Member2VoiceManager.VoiceStep(
                            Member2VoiceManager.STORYBOOK_GREETING_1,
                            Member2VoiceManager.LOCALE_VI,
                            0L,
                            "story_greeting_1",
                            null
                    ),
                    new Member2VoiceManager.VoiceStep(
                            Member2VoiceManager.STORYBOOK_LIBRARY_GUIDE,
                            Member2VoiceManager.LOCALE_VI,
                            260L,
                            "story_greeting_2",
                            () -> Member2VoiceManager.highlightTargetView(firstStoryCard)
                    )
            );
            voiceManager.setCurrentInstruction(libInstruction);
            voiceManager.startNewTurn();
            if (!hasPlayedWelcomeGreeting) {
                hasPlayedWelcomeGreeting = true;
                voiceManager.speakSequence(
                        libInstruction,
                        Member2VoiceManager.Priority.GREETING_AND_IDLE,
                        false,
                        null
                );
            } else if (fromUserBackAction) {
                voiceManager.onUserInteraction();
                voiceManager.speakSequence(
                        Collections.singletonList(new Member2VoiceManager.VoiceStep(
                                Member2VoiceManager.STORYBOOK_LIBRARY_GUIDE,
                                Member2VoiceManager.LOCALE_VI,
                                0L,
                                "story_greeting_2",
                                () -> Member2VoiceManager.highlightTargetView(firstStoryCard)
                        )),
                        Member2VoiceManager.Priority.ACTION_INSTRUCTION,
                        false,
                        null
                );
            }
        }
    }

    private void openStoryAtPage(int storyIndex, int pageIndex, boolean autoIntroduceAndNarrate) {
        narrationController.stop();
        if (voiceManager != null) {
            voiceManager.stop();
            voiceManager.onUserInteraction();
        }
        isInReaderMode = true;
        hasGuidedPageTurnInCurrentStory = false;
        currentStoryIndex = Math.max(0, Math.min(storyIndex, stories.size() - 1));
        StoryContent.StoryBook story = stories.get(currentStoryIndex);
        currentPageIndex = Math.max(0, Math.min(pageIndex, story.getPages().size() - 1));

        layoutLibrary.setVisibility(View.GONE);
        layoutReader.setVisibility(View.VISIBLE);

        tvCurrentStoryTitle.setText(story.getTitle());
        pageAdapter = new StoryPageAdapter(
                story,
                (pIdx, page) -> {
                    if (voiceManager != null) {
                        voiceManager.onUserInteraction();
                        voiceManager.stop();
                    }
                    if (soundManager != null && soundManager.isSfxMuted()) {
                        Toast.makeText(this, R.string.m2_alphabet_toast_muted, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    narrationController.startFromBeginning(pIdx, page.getPageText());
                },
                isCorrect -> {
                    if (voiceManager != null) {
                        voiceManager.onUserInteraction();
                        voiceManager.stop();
                    }
                    if (voiceFeedback != null) {
                        if (isCorrect) {
                            voiceFeedback.speakCorrect();
                        } else {
                            voiceFeedback.speakWrong();
                        }
                    }
                }
        );
        suppressPageSelectedAutoNarrate = true;
        vpPages.setAdapter(pageAdapter);
        vpPages.setCurrentItem(currentPageIndex, false);
        vpPages.post(() -> suppressPageSelectedAutoNarrate = false);
        updateReaderPageControls();

        if (autoIntroduceAndNarrate && (soundManager == null || !soundManager.isSfxMuted())) {
            final int openedStoryIdx = currentStoryIndex;
            final int openedPageIdx = currentPageIndex;
            if (voiceManager != null) {
                voiceManager.speakSequence(
                        Collections.singletonList(new Member2VoiceManager.VoiceStep(
                                Member2VoiceManager.STORYBOOK_OPEN_STORY_GUIDE,
                                Member2VoiceManager.LOCALE_VI,
                                0L,
                                "story_select_intro",
                                () -> Member2VoiceManager.highlightTargetView(vpPages)
                        )),
                        Member2VoiceManager.Priority.ACTION_INSTRUCTION,
                        false,
                        () -> {
                            if (isInReaderMode
                                    && currentStoryIndex == openedStoryIdx
                                    && currentPageIndex == openedPageIdx
                                    && (soundManager == null || !soundManager.isSfxMuted())) {
                                StoryPage page = getCurrentPage();
                                narrationController.startFromBeginning(currentPageIndex, page.getPageText());
                            }
                        }
                );
            } else {
                StoryPage page = getCurrentPage();
                narrationController.startFromBeginning(currentPageIndex, page.getPageText());
            }
        }
    }

    private StoryPage getCurrentPage() {
        StoryContent.StoryBook story = stories.get(currentStoryIndex);
        return story.getPages().get(currentPageIndex);
    }

    private void updateReaderPageControls() {
        StoryContent.StoryBook story = stories.get(currentStoryIndex);
        int totalPages = story.getPages().size();

        tvPageIndicator.setText(getString(
                R.string.m2_storybook_page_indicator_format,
                currentPageIndex + 1,
                totalPages
        ));

        btnPrevPage.setEnabled(currentPageIndex > 0);
        btnPrevPage.setAlpha(currentPageIndex > 0 ? 1.0f : 0.45f);

        boolean isLastPage = (currentPageIndex == totalPages - 1);
        if (isLastPage) {
            btnNextPage.setText(R.string.m2_storybook_btn_finish_story);
            if (ivMascot != null) {
                ivMascot.setImageResource(R.drawable.m2_mascot_saobong_celebrate);
            }
            if (tvBannerText != null) {
                tvBannerText.setText("🌟 Trang cuối truyện \"" + story.getTitle() + "\" • Cùng trả lời câu đố của Sao Bông nhé!");
            }
        } else {
            btnNextPage.setText(R.string.m2_storybook_btn_next_page);
            if (ivMascot != null) {
                ivMascot.setImageResource(R.drawable.m2_mascot_saobong_welcome);
            }
            if (tvBannerText != null) {
                tvBannerText.setText("📖 " + story.getTitle() + " (Trang " + (currentPageIndex + 1) + "/" + totalPages + ") • Bấm loa để nghe kể chuyện!");
            }
        }

        if (voiceManager != null) {
            StoryPage page = getCurrentPage();
            voiceManager.setCurrentInstructionVietnamese(page.getPageText());
        }
    }

    private void completeCurrentStory(StoryContent.StoryBook story) {
        narrationController.stop();
        if (voiceManager != null) {
            voiceManager.stop();
            voiceManager.onUserInteraction();
        }
        Member2SoundEffects.playCelebrate(this);
        if (voiceManager != null) {
            voiceManager.speakFeedbackSteps(
                    Collections.singletonList(new Member2VoiceManager.VoiceStep(
                            Member2VoiceManager.STORYBOOK_END_STORY,
                            Member2VoiceManager.LOCALE_VI,
                            0L,
                            "story_end_question",
                            null
                    )),
                    null
            );
        } else if (voiceFeedback != null) {
            voiceFeedback.speakCompletionNow();
        }
        completedStoryIds.add(story.getId());
        setGameProgress(completedStoryIds.size(), stories.size());
        renderStoryLibraryCards();

        progressRepository.markItemCompleted(
                Member2ContentCatalog.GAME_ID_STORYBOOK,
                story.getId(),
                stories.size(),
                3,
                updated -> setGameProgress(updated.getCompletedLevels(), stories.size())
        );

        showVictoryDialog(
                3,
                this::showLibraryScreen,
                () -> vpPages.setCurrentItem(0, true)
        );
    }

    private void loadSavedProgress() {
        setGameProgress(0, stories.size());
        progressRepository.loadCompletedItemsForGame(
                Member2ContentCatalog.GAME_ID_STORYBOOK,
                completedSet -> {
                    completedStoryIds.clear();
                    completedStoryIds.addAll(completedSet);
                    setGameProgress(completedStoryIds.size(), stories.size());
                    renderStoryLibraryCards();
                }
        );
    }

    @Override
    public void onNarrationStateChanged(boolean isPlaying, boolean isPaused) {
        if (isPlaying) {
            btnPlayPause.setText(R.string.m2_storybook_btn_pause);
            if (ivMascot != null) {
                ivMascot.setImageResource(R.drawable.m2_mascot_saobong_happy);
            }
            if (tvBannerText != null) {
                tvBannerText.setText("🎙️ Sao Bông đang đọc từng câu và tô sáng chữ cho bé...");
            }
        } else if (isPaused) {
            btnPlayPause.setText(R.string.m2_storybook_btn_resume);
            if (ivMascot != null) {
                ivMascot.setImageResource(R.drawable.m2_mascot_saobong_hint);
            }
            if (tvBannerText != null) {
                tvBannerText.setText("⏸ Đã tạm dừng lời kể • Bấm Kể tiếp để nghe tiếp nhé!");
            }
        } else {
            btnPlayPause.setText(R.string.m2_storybook_btn_play);
        }
    }

    @Override
    public void onHighlightChanged(int pageIndex, int startChar, int endChar) {
        if (pageAdapter != null) {
            pageAdapter.updateHighlight(pageIndex, startChar, endChar);
        }
    }

    @Override
    public void onPageNarrationCompleted(int pageIndex) {
        if (pageAdapter != null) {
            pageAdapter.updateHighlight(pageIndex, -1, -1);
        }
        StoryContent.StoryBook story = stories.get(currentStoryIndex);
        boolean isLastPage = (pageIndex == story.getPages().size() - 1);
        if (isLastPage) {
            if (tvBannerText != null) {
                tvBannerText.setText("🌟 Câu chuyện kết thúc rồi! Bé hãy nghe câu đố của Sao Bông nhé!");
            }
            if (voiceManager != null && (soundManager == null || !soundManager.isSfxMuted())) {
                voiceManager.speakSequence(
                        Arrays.asList(
                                new Member2VoiceManager.VoiceStep(
                                        Member2VoiceManager.STORYBOOK_END_STORY,
                                        Member2VoiceManager.LOCALE_VI,
                                        220L,
                                        "story_end_question",
                                        null
                                ),
                                new Member2VoiceManager.VoiceStep(
                                        story.getMiniQuestion(),
                                        Member2VoiceManager.LOCALE_VI,
                                        320L,
                                        "story_quiz_" + story.getId(),
                                        null
                                )
                        ),
                        Member2VoiceManager.Priority.ACTION_INSTRUCTION,
                        false,
                        null
                );
            }
        } else {
            if (tvBannerText != null) {
                tvBannerText.setText("✨ Sao Bông đã đọc xong trang này! Bé chạm mũi tên bên phải để sang trang tiếp nhé!");
            }
            Member2VoiceManager.highlightTargetView(btnNextPage);
            if (!hasGuidedPageTurnInCurrentStory
                    && voiceManager != null
                    && (soundManager == null || !soundManager.isSfxMuted())) {
                hasGuidedPageTurnInCurrentStory = true;
                voiceManager.speakSequence(
                        Collections.singletonList(new Member2VoiceManager.VoiceStep(
                                Member2VoiceManager.STORYBOOK_NEXT_PAGE_ARROW_GUIDE,
                                Member2VoiceManager.LOCALE_VI,
                                320L,
                                "story_next_page_arrow_guide",
                                () -> Member2VoiceManager.highlightTargetView(btnNextPage)
                        )),
                        Member2VoiceManager.Priority.ACTION_INSTRUCTION,
                        false,
                        null
                );
            }
        }
    }

    @Override
    public void onNarrationUnavailable(String reason) {
        Toast.makeText(this, reason, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSoundToggled(boolean isMuted) {
        super.onSoundToggled(isMuted);
        if (isMuted) {
            Member2SoundEffects.stopOrMuteBgm(this);
            if (narrationController != null) {
                narrationController.stop();
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
        outState.putBoolean(STATE_IN_READER, isInReaderMode);
        outState.putInt(STATE_STORY_INDEX, currentStoryIndex);
        outState.putInt(STATE_PAGE_INDEX, currentPageIndex);
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
    protected void onPause() {
        if (narrationController != null) {
            narrationController.stop();
        }
        if (voiceFeedback != null) {
            voiceFeedback.stop();
        }
        if (voiceManager != null) {
            voiceManager.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
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
        if (narrationController != null) {
            narrationController.shutdown();
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
