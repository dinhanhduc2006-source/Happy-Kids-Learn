package com.example.happykidlearn.member4.counting;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.view.LayoutInflater;
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

public class CountingActivity extends BaseGameActivity {

    private TextView tvQuestionTitle;
    private TextView tvCountFeedback;
    private GridLayout glObjectsContainer;
    private Button btnOption1, btnOption2, btnOption3, btnOption4;

    private List<CountingQuestion> questions;
    private int currentQuestionIndex = 0;
    private int currentTapCount = 0;
    private int totalStarsEarned = 0;
    private boolean[] itemTappedState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m4_activity_counting);

        // Khởi tạo thanh tiêu đề dùng chung từ BaseGameActivity
        setupHeader("Game Đếm Số");

        initViews();
        initQuestionData();
        loadQuestion(currentQuestionIndex);
    }

    private void initViews() {
        tvQuestionTitle = findViewById(R.id.tv_question_title);
        tvCountFeedback = findViewById(R.id.tv_count_feedback);
        glObjectsContainer = findViewById(R.id.gl_objects_container);
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
        questions.add(new CountingQuestion("quả táo", R.drawable.m4_ic_counting_apple, 3, new int[]{2, 3, 4, 5}));
        questions.add(new CountingQuestion("ngôi sao", R.drawable.m4_ic_counting_star, 5, new int[]{4, 5, 6, 7}));
        questions.add(new CountingQuestion("chú thỏ", R.drawable.m4_ic_counting_rabbit, 4, new int[]{3, 4, 6, 8}));
        questions.add(new CountingQuestion("quả cam", R.drawable.m4_ic_counting_orange, 6, new int[]{5, 6, 7, 8}));
        questions.add(new CountingQuestion("ngôi sao", R.drawable.m4_ic_counting_star, 8, new int[]{6, 7, 8, 9}));
    }

    private void loadQuestion(int index) {
        if (index < 0 || index >= questions.size()) {
            finishGame();
            return;
        }

        CountingQuestion currentQ = questions.get(index);
        currentTapCount = 0;
        itemTappedState = new boolean[currentQ.getCount()];

        // Cập nhật thanh tiến độ header
        setGameProgress(index + 1, questions.size());

        // Tiêu đề câu hỏi
        tvQuestionTitle.setText("Bé hãy đếm xem có bao nhiêu " + currentQ.getItemName() + "?");
        tvCountFeedback.setText("Chạm vào từng hình để đếm (Đã đếm: 0)");

        // Render các đồ vật vào lưới
        glObjectsContainer.removeAllViews();
        int totalObjects = currentQ.getCount();

        // Tính toán số cột tự động phù hợp (tối đa 5 cột)
        int columns = Math.min(totalObjects, 5);
        if (columns < 2) columns = 2;
        glObjectsContainer.setColumnCount(columns);

        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < totalObjects; i++) {
            View itemView = inflater.inflate(R.layout.m4_item_counting_object, glObjectsContainer, false);
            ImageView imgObject = itemView.findViewById(R.id.img_object);
            TextView tvBadge = itemView.findViewById(R.id.tv_count_badge);

            imgObject.setImageResource(currentQ.getImageResId());
            tvBadge.setVisibility(View.GONE);

            final int itemIndex = i;
            itemView.setOnClickListener(v -> onItemTapped(itemView, tvBadge, itemIndex));

            glObjectsContainer.addView(itemView);
        }

        // Cấu hình danh sách đáp án
        int[] opts = currentQ.getOptions();
        List<Integer> optionList = new ArrayList<>();
        for (int opt : opts) {
            optionList.add(opt);
        }
        // Trộn đáp án ngẫu nhiên
        Collections.shuffle(optionList);

        btnOption1.setText(String.valueOf(optionList.get(0)));
        btnOption2.setText(String.valueOf(optionList.get(1)));
        btnOption3.setText(String.valueOf(optionList.get(2)));
        btnOption4.setText(String.valueOf(optionList.get(3)));

        enableOptions(true);
    }

    private void onItemTapped(View itemView, TextView tvBadge, int itemIndex) {
        if (itemTappedState[itemIndex]) {
            // Đã đếm rồi -> không tính thêm
            return;
        }

        itemTappedState[itemIndex] = true;
        currentTapCount++;

        // Hiển thị nhãn số thứ tự
        tvBadge.setText(String.valueOf(currentTapCount));
        tvBadge.setVisibility(View.VISIBLE);

        // Hiệu ứng nảy đồ vật khi chạm
        ObjectAnimator scaleAnim = ObjectAnimator.ofPropertyValuesHolder(
                itemView,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 0.7f, 1.2f, 1.0f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.7f, 1.2f, 1.0f)
        );
        scaleAnim.setDuration(300);
        scaleAnim.start();

        // Phát âm thanh nếu có SoundManager
        if (soundManager != null) {
            soundManager.playSfx(R.raw.m2_sfx_celebrate);
        }

        // Cập nhật text phản hồi
        tvCountFeedback.setText("Bé đã đếm được: " + currentTapCount + " / " + questions.get(currentQuestionIndex).getCount());
    }

    private void checkAnswer(int chosenValue, Button clickedBtn) {
        CountingQuestion currentQ = questions.get(currentQuestionIndex);
        if (chosenValue == currentQ.getCount()) {
            // Đáp án ĐÚNG
            totalStarsEarned++;
            saveGameProgress("counting", currentQuestionIndex + 1, questions.size(), totalStarsEarned);
            Toast.makeText(this, "🎉 Hoan hô! Bé trả lời đúng rồi!", Toast.LENGTH_SHORT).show();
            enableOptions(false);

            // Chuyển sang câu tiếp theo sau 800ms
            glObjectsContainer.postDelayed(() -> {
                currentQuestionIndex++;
                if (currentQuestionIndex < questions.size()) {
                    loadQuestion(currentQuestionIndex);
                } else {
                    finishGame();
                }
            }, 800);
        } else {
            // Đáp án SAI
            Toast.makeText(this, "💡 Chưa chính xác, bé hãy đếm kỹ lại xem nhé!", Toast.LENGTH_SHORT).show();

            // Hiệu ứng rung nhẹ nút bấm sai
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
        saveGameProgress("counting", questions.size(), questions.size(), finalStars);
        showVictoryDialog(finalStars,
                // Nút Tiếp tục (Finish activity)
                this::finish,
                // Nút Chơi lại (Reset game)
                () -> {
                    currentQuestionIndex = 0;
                    totalStarsEarned = 0;
                    loadQuestion(currentQuestionIndex);
                }
        );
    }
}
