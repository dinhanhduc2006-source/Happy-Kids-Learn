package com.example.happykidlearn.member3.handwriting;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.happykidlearn.R;
import com.example.happykidlearn.base.BaseGameActivity;

public class HandwritingActivity extends BaseGameActivity {

    private static class Lesson {
        String letter;
        String emoji;
        String label;

        Lesson(String letter, String emoji, String label) {
            this.letter = letter;
            this.emoji = emoji;
            this.label = label;
        }
    }

    private HandwritingBoardView boardView;
    private TextView tvBanner;
    private LinearLayout layoutLessonContainer;
    private View layoutBoardContainer;
    private TextView tvEncourageToast;

    private final Lesson[] lessons = {
            new Lesson("A", "🍎", "Quả táo"),
            new Lesson("B", "⚽", "Quả bóng"),
            new Lesson("C", "🐟", "Con cá"),
            new Lesson("D", "🍉", "Dưa hấu"),
            new Lesson("E", "🐘", "Con voi"),
            new Lesson("G", "🐥", "Con gà"),
            new Lesson("H", "🐯", "Con hổ"),
            new Lesson("I", "🍦", "Kem ốc quế"),
            new Lesson("K", "🍬", "Kẹo ngọt"),
            new Lesson("L", "🎈", "Bóng bay")
    };

    private final boolean[] lessonCompleted = new boolean[10];
    private int currentIndex = 0;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m3_handwriting);

        setupHeader(getString(R.string.m3_game_handwriting_title));

        boardView = findViewById(R.id.view_m3_handwriting_board);
        tvBanner = findViewById(R.id.tv_m3_banner);
        layoutLessonContainer = findViewById(R.id.layout_m3_lesson_container);
        layoutBoardContainer = findViewById(R.id.layout_m3_board_container);
        tvEncourageToast = findViewById(R.id.tv_m3_encourage_toast);

        setupControls();
        loadLesson(currentIndex);
    }

    private void setupControls() {
        ImageButton btnPrev = findViewById(R.id.btn_m3_prev);
        ImageButton btnNext = findViewById(R.id.btn_m3_next);
        Button btnFinish = findViewById(R.id.btn_m3_finish_letter);

        Button btnUndo = findViewById(R.id.btn_m3_undo);
        Button btnClear = findViewById(R.id.btn_m3_clear);

        btnPrev.setOnClickListener(v -> {
            playSoundTap();
            if (currentIndex > 0) {
                currentIndex--;
                loadLesson(currentIndex);
            }
        });

        btnNext.setOnClickListener(v -> {
            playSoundTap();
            if (currentIndex < lessons.length - 1) {
                currentIndex++;
                loadLesson(currentIndex);
            }
        });

        btnUndo.setOnClickListener(v -> {
            playSoundTap();
            boardView.undo();
        });

        btnClear.setOnClickListener(v -> {
            playSoundTap();
            boardView.clearCanvas();
        });

        btnFinish.setOnClickListener(v -> checkAndFinishLesson());

        setupColorPalette();
    }

    private void setupColorPalette() {
        int[] colorIds = {
                R.id.color_red, R.id.color_orange, R.id.color_green,
                R.id.color_blue, R.id.color_purple, R.id.color_black
        };
        int[] colors = {
                ContextCompat.getColor(this, R.color.m3_color_red),
                ContextCompat.getColor(this, R.color.m3_color_orange),
                ContextCompat.getColor(this, R.color.m3_color_green),
                ContextCompat.getColor(this, R.color.m3_color_blue),
                ContextCompat.getColor(this, R.color.m3_color_purple),
                ContextCompat.getColor(this, R.color.m3_color_black)
        };

        for (int i = 0; i < colorIds.length; i++) {
            final int c = colors[i];
            final View colorView = findViewById(colorIds[i]);

            colorView.setOnClickListener(v -> {
                playSoundTap();

                for (int id : colorIds) {
                    findViewById(id).setScaleX(1.0f);
                    findViewById(id).setScaleY(1.0f);
                }

                colorView.setScaleX(1.3f);
                colorView.setScaleY(1.3f);

                boardView.setStrokeColor(c);
            });
        }
    }

    private void setupLessonSelector() {
        layoutLessonContainer.removeAllViews();
        float density = getResources().getDisplayMetrics().density;

        for (int i = 0; i < lessons.length; i++) {
            final int index = i;
            Lesson lesson = lessons[i];

            TextView tvTile = new TextView(this);
            String titleText = lesson.letter + (lessonCompleted[i] ? " ✓" : "");
            tvTile.setText(titleText);
            tvTile.setTextSize(16f);
            tvTile.setTypeface(null, android.graphics.Typeface.BOLD);
            tvTile.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int) (52 * density), (int) (48 * density));
            params.setMargins((int) (4 * density), 0, (int) (4 * density), 0);
            tvTile.setLayoutParams(params);

            if (i == currentIndex) {
                tvTile.setBackgroundResource(R.drawable.m3_bg_item_tile_selected);
                tvTile.setTextColor(ContextCompat.getColor(this, R.color.m3_btn_green));
            } else if (lessonCompleted[i]) {
                tvTile.setBackgroundResource(R.drawable.m3_bg_item_tile);
                tvTile.setTextColor(ContextCompat.getColor(this, R.color.m3_btn_green));
            } else {
                tvTile.setBackgroundResource(R.drawable.m3_bg_item_tile);
                tvTile.setTextColor(ContextCompat.getColor(this, R.color.m3_text_main));
            }

            tvTile.setOnClickListener(v -> {
                playSoundTap();
                currentIndex = index;
                loadLesson(currentIndex);
            });

            layoutLessonContainer.addView(tvTile);
        }
    }

    private void loadLesson(int index) {
        Lesson lesson = lessons[index];
        boardView.setGuideText(lesson.letter);

        if (tvBanner != null) {
            tvBanner.setText(String.format("✏️ Bé tô nét chữ %s — %s %s", lesson.letter, lesson.emoji, lesson.label));
        }

        setupLessonSelector();
        setGameProgress(countCompleted(), lessons.length);
    }

    private void checkAndFinishLesson() {
        if (boardView.isEmpty()) {
            showEncouragement("Bé hãy tập viết nét chữ trước nhé!");
            shakeBoard();
            return;
        }

        float coverage = boardView.calculateCoveragePercentage();

        if (coverage < 40f) {
            showEncouragement("Bé hãy tô thêm cho kín nét chữ nhé!");
            shakeBoard();
        } else {
            // Tô ĐÚNG (Coverage >= 40%)
            if (soundManager != null) {
                soundManager.playSfx(R.raw.m2_sfx_correct);
            }

            lessonCompleted[currentIndex] = true;
            int completedCount = countCompleted();
            int stars = (completedCount >= lessons.length) ? 3 : (completedCount >= 5 ? 2 : 1);

            setGameProgress(completedCount, lessons.length);
            saveGameProgress("handwriting", completedCount, lessons.length, stars);

            showEncouragement("🌟 Giỏi quá!");

            handler.postDelayed(() -> {
                tvEncourageToast.setVisibility(View.GONE);
                if (completedCount >= lessons.length) {
                    showVictoryDialog(3, this::finish, this::resetGame);
                } else {
                    // Chuyển sang bài tiếp theo chưa hoàn thành
                    int nextUncompleted = findNextUncompletedIndex();
                    if (nextUncompleted != -1) {
                        currentIndex = nextUncompleted;
                    } else if (currentIndex < lessons.length - 1) {
                        currentIndex++;
                    }
                    loadLesson(currentIndex);
                }
            }, 900);
        }
    }

    private int findNextUncompletedIndex() {
        for (int i = 0; i < lessons.length; i++) {
            int idx = (currentIndex + i + 1) % lessons.length;
            if (!lessonCompleted[idx]) {
                return idx;
            }
        }
        return -1;
    }

    private int countCompleted() {
        int count = 0;
        for (boolean c : lessonCompleted) {
            if (c) count++;
        }
        return count;
    }

    private void shakeBoard() {
        ObjectAnimator shakeAnim = ObjectAnimator.ofFloat(
                layoutBoardContainer, "translationX", 0, 15, -15, 10, -10, 0);
        shakeAnim.setDuration(400);
        shakeAnim.start();
    }

    private void showEncouragement(String message) {
        tvEncourageToast.setText(message);
        tvEncourageToast.setVisibility(View.VISIBLE);

        handler.removeCallbacks(hideToastRunnable);
        handler.postDelayed(hideToastRunnable, 2000);
    }

    private final Runnable hideToastRunnable = () -> tvEncourageToast.setVisibility(View.GONE);

    private void playSoundTap() {
        if (soundManager != null) {
            soundManager.playSfx(R.raw.m2_sfx_tap);
        }
    }

    private void resetGame() {
        for (int i = 0; i < lessonCompleted.length; i++) {
            lessonCompleted[i] = false;
        }
        currentIndex = 0;
        loadLesson(currentIndex);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
