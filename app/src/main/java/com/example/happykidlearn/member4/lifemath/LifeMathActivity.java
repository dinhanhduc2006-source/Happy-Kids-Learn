package com.example.happykidlearn.member4.lifemath;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.happykidlearn.R;
import com.example.happykidlearn.base.BaseGameActivity;

import java.util.ArrayList;
import java.util.List;

public class LifeMathActivity extends BaseGameActivity {

    private TextView tvQuestionTitle;
    private TextView tvCartCount;
    private LinearLayout llCartContainer;
    private GridLayout glCartItems;
    private GridLayout glShelfItems;
    private Button btnResetCart;
    private Button btnSubmitCart;

    private List<LifeMathQuestion> questions;
    private int currentQuestionIndex = 0;
    private int currentCartCount = 0;
    private int totalStarsEarned = 0;
    private boolean[] shelfItemPickedState;
    private List<View> shelfViews;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m4_activity_life_math);

        // Khởi tạo thanh tiêu đề dùng chung từ BaseGameActivity
        setupHeader("Toán Đời Sống");

        initViews();
        initQuestionData();
        loadQuestion(currentQuestionIndex);
    }

    private void initViews() {
        tvQuestionTitle = findViewById(R.id.tv_question_title);
        tvCartCount = findViewById(R.id.tv_cart_count);
        llCartContainer = findViewById(R.id.ll_cart_container);
        glCartItems = findViewById(R.id.gl_cart_items);
        glShelfItems = findViewById(R.id.gl_shelf_items);
        btnResetCart = findViewById(R.id.btn_reset_cart);
        btnSubmitCart = findViewById(R.id.btn_submit_cart);

        btnResetCart.setOnClickListener(v -> resetCartForCurrentQuestion());
        btnSubmitCart.setOnClickListener(v -> checkAnswer());
    }

    private void initQuestionData() {
        questions = new ArrayList<>();
        questions.add(new LifeMathQuestion("🛒 Bé hãy chọn đúng 3 hộp sữa bỏ vào giỏ hàng!", "hộp sữa", 3, R.drawable.m4_ic_life_milk, 6));
        questions.add(new LifeMathQuestion("🍎 Bé hãy mua giúp mẹ 5 quả táo chín nhé!", "quả táo", 5, R.drawable.m4_ic_counting_apple, 8));
        questions.add(new LifeMathQuestion("🍞 Bé hãy chọn 4 ổ bánh mì bỏ vào giỏ hàng!", "ổ bánh mì", 4, R.drawable.m4_ic_life_bread, 7));
        questions.add(new LifeMathQuestion("🍬 Bé hãy lấy 6 viên kẹo ngọt cho các bạn nhé!", "viên kẹo", 6, R.drawable.m4_ic_fun_math_candy, 8));
        questions.add(new LifeMathQuestion("🧸 Bé hãy chọn 4 gấu bông làm quà tặng nhé!", "gấu bông", 4, R.drawable.m4_ic_life_toy, 6));
    }

    private void loadQuestion(int index) {
        if (index < 0 || index >= questions.size()) {
            finishGame();
            return;
        }

        LifeMathQuestion currentQ = questions.get(index);
        currentCartCount = 0;
        shelfItemPickedState = new boolean[currentQ.getShelfQuantity()];
        shelfViews = new ArrayList<>();

        // Cập nhật tiến độ header (1/5, 2/5...)
        setGameProgress(index + 1, questions.size());

        // Hiển thị tiêu đề bài toán đi siêu thị
        tvQuestionTitle.setText(currentQ.getInstruction());
        updateCartCountText();

        // Xóa sạch giỏ hàng & kệ hàng cũ
        glCartItems.removeAllViews();
        glShelfItems.removeAllViews();

        // Render danh sách các đồ vật trên kệ hàng siêu thị
        int shelfTotal = currentQ.getShelfQuantity();
        int columns = Math.min(shelfTotal, 4);
        if (columns < 2) columns = 2;
        glShelfItems.setColumnCount(columns);

        int cardSize = (int) (64 * getResources().getDisplayMetrics().density);
        int margin = (int) (6 * getResources().getDisplayMetrics().density);

        for (int i = 0; i < shelfTotal; i++) {
            FrameLayout frame = new FrameLayout(this);
            frame.setBackgroundResource(R.drawable.m4_bg_counting_item);

            ImageView img = new ImageView(this);
            img.setImageResource(currentQ.getIconResId());
            int imgSize = (int) (44 * getResources().getDisplayMetrics().density);
            FrameLayout.LayoutParams imgParams = new FrameLayout.LayoutParams(imgSize, imgSize);
            imgParams.gravity = android.view.Gravity.CENTER;
            img.setLayoutParams(imgParams);

            frame.addView(img);

            GridLayout.LayoutParams gridParams = new GridLayout.LayoutParams();
            gridParams.width = cardSize;
            gridParams.height = cardSize;
            gridParams.setMargins(margin, margin, margin, margin);
            frame.setLayoutParams(gridParams);

            final int itemIndex = i;
            frame.setOnClickListener(v -> onItemPickedFromShelf(frame, itemIndex));

            shelfViews.add(frame);
            glShelfItems.addView(frame);
        }

        enableControls(true);
    }

    private void onItemPickedFromShelf(View shelfItemView, int itemIndex) {
        if (shelfItemPickedState[itemIndex]) {
            return;
        }

        shelfItemPickedState[itemIndex] = true;
        currentCartCount++;

        // Hiệu ứng biến mất nhẹ trên kệ hàng
        shelfItemView.animate().alpha(0.2f).scaleX(0.8f).scaleY(0.8f).setDuration(250).start();
        shelfItemView.setEnabled(false);

        // Thêm biểu tượng món đồ vào trong Giỏ Hàng
        LifeMathQuestion currentQ = questions.get(currentQuestionIndex);
        ImageView cartImg = new ImageView(this);
        cartImg.setImageResource(currentQ.getIconResId());
        int cartImgSize = (int) (40 * getResources().getDisplayMetrics().density);
        int margin = (int) (4 * getResources().getDisplayMetrics().density);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = cartImgSize;
        params.height = cartImgSize;
        params.setMargins(margin, margin, margin, margin);
        cartImg.setLayoutParams(params);

        glCartItems.addView(cartImg);

        // Hiệu ứng nảy biểu tượng trong giỏ hàng
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(
                cartImg,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 0.3f, 1.2f, 1.0f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.3f, 1.2f, 1.0f)
        );
        anim.setDuration(300);
        anim.start();

        // Phát âm thanh chọn đồ
        if (soundManager != null) {
            soundManager.playSfx(R.raw.m2_sfx_celebrate);
        }

        updateCartCountText();
    }

    private void resetCartForCurrentQuestion() {
        LifeMathQuestion currentQ = questions.get(currentQuestionIndex);
        currentCartCount = 0;
        shelfItemPickedState = new boolean[currentQ.getShelfQuantity()];

        // Xóa hình trong giỏ
        glCartItems.removeAllViews();

        // Khôi phục hiển thị trên kệ hàng
        for (View shelfView : shelfViews) {
            shelfView.setAlpha(1.0f);
            shelfView.setScaleX(1.0f);
            shelfView.setScaleY(1.0f);
            shelfView.setEnabled(true);
        }

        updateCartCountText();
        Toast.makeText(this, "🧹 Đã đặt lại giỏ hàng!", Toast.LENGTH_SHORT).show();
    }

    private void checkAnswer() {
        LifeMathQuestion currentQ = questions.get(currentQuestionIndex);
        if (currentCartCount == currentQ.getTargetQuantity()) {
            // Trả lời ĐÚNG
            totalStarsEarned++;
            saveGameProgress("life_math", currentQuestionIndex + 1, questions.size(), totalStarsEarned);
            Toast.makeText(this, "🎉 Xuất sắc! Bé đã chọn đúng đủ " + currentCartCount + " " + currentQ.getItemName() + "!", Toast.LENGTH_SHORT).show();
            enableControls(false);

            if (soundManager != null) {
                soundManager.playSfx(R.raw.m2_sfx_celebrate);
            }

            // Chuyển sang câu tiếp theo sau 800ms
            glCartItems.postDelayed(() -> {
                currentQuestionIndex++;
                if (currentQuestionIndex < questions.size()) {
                    loadQuestion(currentQuestionIndex);
                } else {
                    finishGame();
                }
            }, 800);
        } else {
            // Trả lời SAI
            String msg;
            if (currentCartCount < currentQ.getTargetQuantity()) {
                msg = "💡 Bé mới lấy " + currentCartCount + " món thôi, cần đúng " + currentQ.getTargetQuantity() + " món cơ!";
            } else {
                msg = "💡 Bé lấy thừa rồi (" + currentCartCount + " món). Hãy bấm Đặt lại để chọn lại nhé!";
            }
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();

            // Hiệu ứng rung khung giỏ hàng
            ObjectAnimator shake = ObjectAnimator.ofFloat(llCartContainer, View.TRANSLATION_X, 0, 15, -15, 10, -10, 0);
            shake.setDuration(400);
            shake.start();
        }
    }

    private void updateCartCountText() {
        LifeMathQuestion currentQ = questions.get(currentQuestionIndex);
        tvCartCount.setText("Giỏ hàng: " + currentCartCount + " / " + currentQ.getTargetQuantity() + " " + currentQ.getItemName());
    }

    private void enableControls(boolean enable) {
        btnResetCart.setEnabled(enable);
        btnSubmitCart.setEnabled(enable);
    }

    private void finishGame() {
        int finalStars = totalStarsEarned >= 4 ? 3 : (totalStarsEarned >= 2 ? 2 : 1);
        saveGameProgress("life_math", questions.size(), questions.size(), finalStars);
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
