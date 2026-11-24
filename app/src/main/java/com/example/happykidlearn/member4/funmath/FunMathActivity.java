package com.example.happykidlearn.member4.funmath;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.happykidlearn.R;
import com.example.happykidlearn.base.BaseGameActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FunMathActivity extends BaseGameActivity {

    private TextView tvQuestionTitle;
    private TextView tvFormulaText;
    private TextView tvOperator;
    private TextView tvResultPlaceholder;
    private GridLayout glGroup1;
    private GridLayout glGroup2;
    private Button btnOption1, btnOption2, btnOption3, btnOption4;

    private List<FunMathQuestion> questions;
    private int currentQuestionIndex = 0;
    private int totalStarsEarned = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m4_activity_fun_math);

        // Khởi tạo thanh tiêu đề dùng chung từ BaseGameActivity
        setupHeader("Game Toán Vui");

        initViews();
        initQuestionData();
        loadQuestion(currentQuestionIndex);
    }

    private void initViews() {
        tvQuestionTitle = findViewById(R.id.tv_question_title);
        tvFormulaText = findViewById(R.id.tv_formula_text);
        tvOperator = findViewById(R.id.tv_operator);
        tvResultPlaceholder = findViewById(R.id.tv_result_placeholder);
        glGroup1 = findViewById(R.id.gl_group1);
        glGroup2 = findViewById(R.id.gl_group2);

        btnOption1 = findViewById(R.id.btn_option_1);
        btnOption2 = findViewById(R.id.btn_option_2);
        btnOption3 = findViewById(R.id.btn_option_3);
        btnOption4 = findViewById(R.id.btn_option_4);

        View.OnClickListener optionClickListener = v -> {
            Button btn = (Button) v;
            int chosenValue = Integer.parseInt(btn.getText().toString());
            checkAnswer(chosenValue, btn);
        };

        btnOption1.setOnClickListener(optionClickListener);
        btnOption2.setOnClickListener(optionClickListener);
        btnOption3.setOnClickListener(optionClickListener);
        btnOption4.setOnClickListener(optionClickListener);
    }

    private void initQuestionData() {
        questions = new ArrayList<>();
        questions.add(new FunMathQuestion(3, 2, "+", "chiếc xe", R.drawable.m4_ic_fun_math_car, new int[]{4, 5, 6, 7}));
        questions.add(new FunMathQuestion(5, 2, "-", "quả táo", R.drawable.m4_ic_counting_apple, new int[]{2, 3, 4, 5}));
        questions.add(new FunMathQuestion(4, 4, "+", "viên kẹo", R.drawable.m4_ic_fun_math_candy, new int[]{6, 7, 8, 9}));
        questions.add(new FunMathQuestion(8, 3, "-", "ngôi sao", R.drawable.m4_ic_counting_star, new int[]{4, 5, 6, 7}));
        questions.add(new FunMathQuestion(6, 4, "+", "chú thỏ", R.drawable.m4_ic_counting_rabbit, new int[]{8, 9, 10, 11}));
    }

    private void loadQuestion(int index) {
        if (index < 0 || index >= questions.size()) {
            finishGame();
            return;
        }

        FunMathQuestion currentQ = questions.get(index);

        // Cập nhật thanh tiến độ header (1/5, 2/5...)
        setGameProgress(index + 1, questions.size());

        // Hiển thị công thức chữ & phép tính
        tvQuestionTitle.setText("Bé hãy thực hiện phép tính " + currentQ.getItemName() + ":");
        tvFormulaText.setText(currentQ.getNum1() + " " + currentQ.getOperator() + " " + currentQ.getNum2() + " = ?");
        tvOperator.setText(currentQ.getOperator());
        tvResultPlaceholder.setText("?");

        // Render biểu tượng nhóm 1
        renderItemIcons(glGroup1, currentQ.getNum1(), currentQ.getIconResId());

        // Render biểu tượng nhóm 2
        renderItemIcons(glGroup2, currentQ.getNum2(), currentQ.getIconResId());

        // Cấu hình đáp án bong bóng
        int[] opts = currentQ.getOptions();
        List<Integer> optionList = new ArrayList<>();
        for (int opt : opts) {
            optionList.add(opt);
        }
        Collections.shuffle(optionList);

        btnOption1.setText(String.valueOf(optionList.get(0)));
        btnOption2.setText(String.valueOf(optionList.get(1)));
        btnOption3.setText(String.valueOf(optionList.get(2)));
        btnOption4.setText(String.valueOf(optionList.get(3)));

        enableOptions(true);
    }

    private void renderItemIcons(GridLayout container, int count, int iconResId) {
        container.removeAllViews();

        int columns = Math.min(count, 3);
        if (columns < 1) columns = 1;
        container.setColumnCount(columns);

        int iconSize = (int) (32 * getResources().getDisplayMetrics().density);
        int margin = (int) (2 * getResources().getDisplayMetrics().density);

        for (int i = 0; i < count; i++) {
            ImageView img = new ImageView(this);
            img.setImageResource(iconResId);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = iconSize;
            params.height = iconSize;
            params.setMargins(margin, margin, margin, margin);
            container.addView(img);
        }
    }

    private void checkAnswer(int chosenValue, Button clickedBtn) {
        FunMathQuestion currentQ = questions.get(currentQuestionIndex);
        if (chosenValue == currentQ.getCorrectAnswer()) {
            // Đáp án ĐÚNG
            totalStarsEarned++;
            saveGameProgress("fun_math", currentQuestionIndex + 1, questions.size(), totalStarsEarned);
            tvResultPlaceholder.setText(String.valueOf(chosenValue));
            Toast.makeText(this, "🎉 Bé thật giỏi! Phép tính chính xác!", Toast.LENGTH_SHORT).show();
            enableOptions(false);

            if (soundManager != null) {
                soundManager.playSfx(R.raw.m2_sfx_celebrate);
            }

            // Tự động qua câu tiếp theo sau 900ms
            glGroup1.postDelayed(() -> {
                currentQuestionIndex++;
                if (currentQuestionIndex < questions.size()) {
                    loadQuestion(currentQuestionIndex);
                } else {
                    finishGame();
                }
            }, 900);
        } else {
            // Đáp án SAI
            Toast.makeText(this, "💡 Bé hãy thử tính lại nhé!", Toast.LENGTH_SHORT).show();

            // Hiệu ứng rung lắc nút sai
            ObjectAnimator shakeAnim = ObjectAnimator.ofFloat(clickedBtn, View.TRANSLATION_X, 0, 15, -15, 10, -10, 0);
            shakeAnim.setDuration(400);
            shakeAnim.start();
        }
    }

    private void enableOptions(boolean enable) {
        btnOption1.setEnabled(enable);
        btnOption2.setEnabled(enable);
        btnOption3.setEnabled(enable);
        btnOption4.setEnabled(enable);
    }

    private void finishGame() {
        int finalStars = totalStarsEarned >= 4 ? 3 : (totalStarsEarned >= 2 ? 2 : 1);
        saveGameProgress("fun_math", questions.size(), questions.size(), finalStars);
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
