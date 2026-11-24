package com.example.happykidlearn.member4.logic;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.happykidlearn.R;
import com.example.happykidlearn.base.BaseGameActivity;

import java.util.ArrayList;
import java.util.List;

public class LogicActivity extends BaseGameActivity {

    private TextView tvQuestionTitle;
    private TextView tvPuzzlePrompt;
    private LinearLayout llSequenceContainer;
    private ImageView imgShadowPuzzle;

    private FrameLayout flOption1, flOption2, flOption3, flOption4;
    private ImageView imgOption1, imgOption2, imgOption3, imgOption4;

    private List<LogicQuestion> questions;
    private int currentQuestionIndex = 0;
    private int totalStarsEarned = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m4_activity_logic);

        // Khởi tạo Header dùng chung từ BaseGameActivity
        setupHeader("Game Tư Duy Logic");

        initViews();
        initQuestionData();
        loadQuestion(currentQuestionIndex);
    }

    private void initViews() {
        tvQuestionTitle = findViewById(R.id.tv_question_title);
        tvPuzzlePrompt = findViewById(R.id.tv_puzzle_prompt);
        llSequenceContainer = findViewById(R.id.ll_sequence_container);
        imgShadowPuzzle = findViewById(R.id.img_shadow_puzzle);

        flOption1 = findViewById(R.id.fl_option_1);
        flOption2 = findViewById(R.id.fl_option_2);
        flOption3 = findViewById(R.id.fl_option_3);
        flOption4 = findViewById(R.id.fl_option_4);

        imgOption1 = findViewById(R.id.img_option_1);
        imgOption2 = findViewById(R.id.img_option_2);
        imgOption3 = findViewById(R.id.img_option_3);
        imgOption4 = findViewById(R.id.img_option_4);

        flOption1.setOnClickListener(v -> checkAnswer(0, flOption1));
        flOption2.setOnClickListener(v -> checkAnswer(1, flOption2));
        flOption3.setOnClickListener(v -> checkAnswer(2, flOption3));
        flOption4.setOnClickListener(v -> checkAnswer(3, flOption4));
    }

    private void initQuestionData() {
        questions = new ArrayList<>();

        // Câu 1: Quy luật chuỗi hình (Tam giác - Vuông - Tam giác - Vuông - ?)
        questions.add(new LogicQuestion(
                "🧩 Bé hãy tìm hình tiếp theo trong quy luật bên dưới!",
                "Quy luật chuỗi hình:",
                LogicQuestion.PuzzleType.SEQUENCE,
                new int[]{R.drawable.m4_ic_logic_triangle, R.drawable.m4_ic_logic_square, R.drawable.m4_ic_logic_triangle, R.drawable.m4_ic_logic_square},
                0,
                new int[]{R.drawable.m4_ic_logic_triangle, R.drawable.m4_ic_logic_square, R.drawable.m4_ic_logic_circle, R.drawable.m4_ic_logic_plane},
                null, 0
        ));

        // Câu 2: Ghép bóng đen (Shadow Matching)
        questions.add(new LogicQuestion(
                "👤 Bé hãy tìm hình ảnh động vật tương ứng đúng với bóng đen!",
                "Bóng đen bí ẩn:",
                LogicQuestion.PuzzleType.SHADOW,
                null,
                R.drawable.m4_ic_logic_cat_shadow,
                new int[]{R.drawable.m4_ic_memory_cat, R.drawable.m4_ic_memory_dog, R.drawable.m4_ic_memory_fish, R.drawable.m4_ic_counting_rabbit},
                null, 0
        ));

        // Câu 3: Phân loại đồ vật
        questions.add(new LogicQuestion(
                "🌊 Trong các động vật bên dưới, loài nào sống dưới nước?",
                "Chọn đáp án đúng:",
                LogicQuestion.PuzzleType.CLASSIFICATION,
                null, 0,
                new int[]{R.drawable.m4_ic_memory_cat, R.drawable.m4_ic_memory_dog, R.drawable.m4_ic_memory_fish, R.drawable.m4_ic_counting_rabbit},
                null, 2
        ));

        // Câu 4: Quy luật hình trái cây
        questions.add(new LogicQuestion(
                "🍎 Bé hãy chọn quả tiếp theo hoàn thành quy luật:",
                "Quy luật hoa quả:",
                LogicQuestion.PuzzleType.SEQUENCE,
                new int[]{R.drawable.m4_ic_counting_apple, R.drawable.m4_ic_counting_orange, R.drawable.m4_ic_counting_apple, R.drawable.m4_ic_counting_orange},
                0,
                new int[]{R.drawable.m4_ic_counting_apple, R.drawable.m4_ic_counting_orange, R.drawable.m4_ic_fun_math_candy, R.drawable.m4_ic_counting_star},
                null, 0
        ));

        // Câu 5: Phân loại phương tiện giao thông
        questions.add(new LogicQuestion(
                "✈️ Đâu là phương tiện giao thông biết bay trên bầu trời?",
                "Chọn đáp án đúng:",
                LogicQuestion.PuzzleType.CLASSIFICATION,
                null, 0,
                new int[]{R.drawable.m4_ic_logic_plane, R.drawable.m4_ic_fun_math_car, R.drawable.m4_ic_memory_fish, R.drawable.m4_ic_fun_math_candy},
                null, 0
        ));
    }

    private void loadQuestion(int index) {
        if (index < 0 || index >= questions.size()) {
            finishGame();
            return;
        }

        LogicQuestion currentQ = questions.get(index);

        // Cập nhật tiến độ Header (1/5, 2/5...)
        setGameProgress(index + 1, questions.size());

        tvQuestionTitle.setText(currentQ.getTitle());
        tvPuzzlePrompt.setText(currentQ.getPromptText());

        // Xử lý hiển thị dựa trên dạng câu hỏi
        if (currentQ.getType() == LogicQuestion.PuzzleType.SEQUENCE) {
            llSequenceContainer.setVisibility(View.VISIBLE);
            imgShadowPuzzle.setVisibility(View.GONE);

            llSequenceContainer.removeAllViews();
            int iconSize = (int) (48 * getResources().getDisplayMetrics().density);
            int margin = (int) (6 * getResources().getDisplayMetrics().density);

            for (int resId : currentQ.getSequenceIconResIds()) {
                ImageView img = new ImageView(this);
                img.setImageResource(resId);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(iconSize, iconSize);
                params.setMargins(margin, margin, margin, margin);
                img.setLayoutParams(params);
                llSequenceContainer.addView(img);
            }

            // Thêm ký tự ô hỏi chấm '?' ở cuối chuỗi
            TextView tvQuestionMark = new TextView(this);
            tvQuestionMark.setText("?");
            tvQuestionMark.setTextSize(28.0f);
            tvQuestionMark.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.accent_pink));
            tvQuestionMark.setTypeface(null, android.graphics.Typeface.BOLD);
            LinearLayout.LayoutParams qParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            qParams.setMargins(margin * 2, 0, margin, 0);
            tvQuestionMark.setLayoutParams(qParams);
            llSequenceContainer.addView(tvQuestionMark);

        } else if (currentQ.getType() == LogicQuestion.PuzzleType.SHADOW) {
            tvPuzzlePrompt.setVisibility(View.VISIBLE);
            llSequenceContainer.setVisibility(View.GONE);
            imgShadowPuzzle.setVisibility(View.VISIBLE);
            imgShadowPuzzle.setImageResource(currentQ.getShadowIconResId());
        } else {
            tvPuzzlePrompt.setVisibility(View.GONE);
            llSequenceContainer.setVisibility(View.GONE);
            imgShadowPuzzle.setVisibility(View.GONE);
        }

        // Đổ dữ liệu vào 4 lựa chọn đáp án
        int[] opts = currentQ.getOptionIconResIds();
        imgOption1.setImageResource(opts[0]);
        imgOption2.setImageResource(opts[1]);
        imgOption3.setImageResource(opts[2]);
        imgOption4.setImageResource(opts[3]);

        enableOptions(true);
    }

    private void checkAnswer(int chosenIndex, View clickedView) {
        LogicQuestion currentQ = questions.get(currentQuestionIndex);
        if (chosenIndex == currentQ.getCorrectOptionIndex()) {
            // Trả lời ĐÚNG
            totalStarsEarned++;
            saveGameProgress("logic", currentQuestionIndex + 1, questions.size(), totalStarsEarned);
            Toast.makeText(this, "🎉 Bé suy luận rất chính xác!", Toast.LENGTH_SHORT).show();
            enableOptions(false);

            if (soundManager != null) {
                soundManager.playSfx(R.raw.m2_sfx_celebrate);
            }

            // Chuyển sang câu hỏi tiếp theo sau 800ms
            llSequenceContainer.postDelayed(() -> {
                currentQuestionIndex++;
                if (currentQuestionIndex < questions.size()) {
                    loadQuestion(currentQuestionIndex);
                } else {
                    finishGame();
                }
            }, 800);
        } else {
            // Trả lời SAI
            Toast.makeText(this, "💡 Chưa chính xác, bé hãy quan sát lại kỹ nhé!", Toast.LENGTH_SHORT).show();

            // Hiệu ứng rung lắc nút chọn sai
            ObjectAnimator shake = ObjectAnimator.ofFloat(clickedView, View.TRANSLATION_X, 0, 15, -15, 10, -10, 0);
            shake.setDuration(400);
            shake.start();
        }
    }

    private void enableOptions(boolean enable) {
        flOption1.setEnabled(enable);
        flOption2.setEnabled(enable);
        flOption3.setEnabled(enable);
        flOption4.setEnabled(enable);
    }

    private void finishGame() {
        int finalStars = totalStarsEarned >= 4 ? 3 : (totalStarsEarned >= 2 ? 2 : 1);
        saveGameProgress("logic", questions.size(), questions.size(), finalStars);
        showVictoryDialog(finalStars,
                this::finish,
                () -> {
                    currentQuestionIndex = 0;
                    totalStarsEarned = 0;
                    loadQuestion(currentQuestionIndex);
                }
        );
    }
}
