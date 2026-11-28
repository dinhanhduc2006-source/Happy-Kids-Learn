package com.example.happykidlearn.member3.handwriting;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.happykidlearn.R;
import com.example.happykidlearn.base.BaseGameActivity;

import java.util.Locale;

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

    private TextToSpeech tts;
    private boolean isTtsReady = false;

    // 10 Lesson CHỮ THƯỜNG viết bằng 1 nét
    private final Lesson[] lessons = {
            new Lesson("o", "🐝", "Con ong"),
            new Lesson("c", "🐟", "Con cá"),
            new Lesson("e", "👶", "Em bé"),
            new Lesson("l", "🍃", "Chiếc lá"),
            new Lesson("s", "🦁", "Sư tử"),
            new Lesson("n", "🍄", "Cây nấm"),
            new Lesson("m", "🐱", "Con mèo"),
            new Lesson("v", "🦆", "Con vịt"),
            new Lesson("r", "🐢", "Con rùa"),
            new Lesson("u", "🐷", "Ủn ỉn")
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

        initTts();
        setupControls();
        loadLesson(currentIndex);
    }

    private void initTts() {
        try {
            tts = new TextToSpeech(this, status -> {
                if (status == TextToSpeech.SUCCESS && tts != null) {
                    int result = tts.setLanguage(new Locale("vi", "VN"));
                    if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                        isTtsReady = true;
                        speakCurrentLesson();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void speakCurrentLesson() {
        if (soundManager != null && soundManager.isSfxMuted()) return;
        if (isTtsReady && tts != null) {
            Lesson lesson = lessons[currentIndex];
            String speech = "Chữ " + lesson.letter + ", " + lesson.label;
            tts.speak(speech, TextToSpeech.QUEUE_FLUSH, null, "HandwritingTTS");
        }
    }

    private void speakText(String text) {
        if (soundManager != null && soundManager.isSfxMuted()) return;
        if (isTtsReady && tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "HandwritingTTS");
        }
    }

    private void setupControls() {
        ImageButton btnPrev = findViewById(R.id.btn_m3_prev);
        ImageButton btnNext = findViewById(R.id.btn_m3_next);
        ImageButton btnSpeak = findViewById(R.id.btn_m3_speak);

        Button btnClear = findViewById(R.id.btn_m3_clear);
        Button btnFinish = findViewById(R.id.btn_m3_finish_letter);

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

        btnSpeak.setOnClickListener(v -> {
            playSoundTap();
            speakCurrentLesson();
        });

        btnClear.setOnClickListener(v -> {
            playSoundTap();
            boardView.clearCanvas();
        });

        btnFinish.setOnClickListener(v -> checkAndFinishLesson());

        boardView.setOnStrokeFinishListener(this::playSoundTap);

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
            tvTile.setTextSize(18f);
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
            tvBanner.setText(String.format("✏️ Bé tô 1 nét chữ %s — %s %s", lesson.letter, lesson.emoji, lesson.label));
        }

        setupLessonSelector();
        setGameProgress(countCompleted(), lessons.length);
        speakCurrentLesson();
    }

    private void checkAndFinishLesson() {
        if (boardView.isEmpty()) {
            if (soundManager != null) {
                soundManager.playSfx(R.raw.m2_sfx_gentle_try);
            }
            showEncouragement("Bé hãy tô liền 1 nét cho kín chữ nhé!");
            shakeBoard();
            return;
        }

        float coverage = boardView.calculateCoveragePercentage();

        if (coverage < 40f) {
            if (soundManager != null) {
                soundManager.playSfx(R.raw.m2_sfx_gentle_try);
            }
            showEncouragement("Bé hãy tô liền 1 nét cho kín chữ nhé!");
            shakeBoard();
        } else {
            // Tô ĐÚNG (Coverage >= 40%)
            if (soundManager != null) {
                soundManager.playSfx(R.raw.m2_sfx_correct);
            }
            speakText("Giỏi quá!");

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
                    int nextUncompleted = findNextUncompletedIndex();
                    if (nextUncompleted != -1) {
                        currentIndex = nextUncompleted;
                    } else if (currentIndex < lessons.length - 1) {
                        currentIndex++;
                    }
                    loadLesson(currentIndex);
                }
            }, 1000);
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
        java.util.Arrays.fill(lessonCompleted, false);
        currentIndex = 0;
        loadLesson(currentIndex);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if (tts != null) {
            try {
                tts.stop();
                tts.shutdown();
            } catch (Exception ignored) {
            }
            tts = null;
        }
        super.onDestroy();
    }
}
