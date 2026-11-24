package com.example.happykidlearn.member4.memory;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
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

public class MemoryActivity extends BaseGameActivity {

    private TextView tvQuestionTitle;
    private TextView tvMatchedPairs;
    private GridLayout glCardsContainer;
    private Button btnRestartLevel;

    private List<MemoryCard> cards;
    private List<View> cardViews;

    private int currentLevel = 0; // 0: Dễ (2x3), 1: Vừa (3x4), 2: Khó (4x4)
    private int matchedPairsCount = 0;
    private int totalPairsInCurrentLevel = 0;
    private int firstFlippedIndex = -1;
    private boolean isBusy = false;
    private int totalStarsEarned = 0;

    // Kho ảnh hoạt hình phong phú cho các thẻ bài
    private final int[] availableIcons = {
            R.drawable.m4_ic_memory_cat,
            R.drawable.m4_ic_memory_dog,
            R.drawable.m4_ic_memory_fish,
            R.drawable.m4_ic_counting_apple,
            R.drawable.m4_ic_counting_star,
            R.drawable.m4_ic_counting_rabbit,
            R.drawable.m4_ic_fun_math_car,
            R.drawable.m4_ic_fun_math_candy
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m4_activity_memory);

        // Khởi tạo Header chuẩn từ BaseGameActivity
        setupHeader("Game Trí Nhớ");

        initViews();
        loadLevel(currentLevel);
    }

    private void initViews() {
        tvQuestionTitle = findViewById(R.id.tv_question_title);
        tvMatchedPairs = findViewById(R.id.tv_matched_pairs);
        glCardsContainer = findViewById(R.id.gl_cards_container);
        btnRestartLevel = findViewById(R.id.btn_restart_level);

        btnRestartLevel.setOnClickListener(v -> loadLevel(currentLevel));
    }

    private void loadLevel(int level) {
        matchedPairsCount = 0;
        firstFlippedIndex = -1;
        isBusy = false;

        // Cập nhật tiến độ trên Header (Level 1/3, 2/3, 3/3)
        setGameProgress(level + 1, 3);

        int pairsCount;
        int columnCount;

        if (level == 0) {
            pairsCount = 3; // 6 thẻ (2x3)
            columnCount = 3;
            tvQuestionTitle.setText("🧠 Màn 1: Lật tìm 3 cặp thẻ tương ứng!");
        } else if (level == 1) {
            pairsCount = 6; // 12 thẻ (3x4)
            columnCount = 4;
            tvQuestionTitle.setText("🧠 Màn 2: Lật tìm 6 cặp thẻ tương ứng!");
        } else {
            pairsCount = 8; // 16 thẻ (4x4)
            columnCount = 4;
            tvQuestionTitle.setText("🧠 Màn 3: Thử thách tìm 8 cặp thẻ bài!");
        }

        totalPairsInCurrentLevel = pairsCount;
        updatePairsText();

        // Tạo danh sách cặp thẻ ngẫu nhiên
        cards = new ArrayList<>();
        List<Integer> selectedIcons = new ArrayList<>();
        for (int i = 0; i < pairsCount; i++) {
            selectedIcons.add(availableIcons[i % availableIcons.length]);
        }

        int cardId = 0;
        for (int iconRes : selectedIcons) {
            cards.add(new MemoryCard(cardId++, iconRes));
            cards.add(new MemoryCard(cardId++, iconRes));
        }
        // Xáo trộn vị trí thẻ bài
        Collections.shuffle(cards);

        // Render lên GridLayout
        glCardsContainer.removeAllViews();
        glCardsContainer.setColumnCount(columnCount);
        cardViews = new ArrayList<>();

        LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < cards.size(); i++) {
            View cardView = inflater.inflate(R.layout.m4_item_memory_card, glCardsContainer, false);
            ImageView imgContent = cardView.findViewById(R.id.img_card_content);
            View flBack = cardView.findViewById(R.id.fl_card_back);
            View flFront = cardView.findViewById(R.id.fl_card_front);

            imgContent.setImageResource(cards.get(i).getImageResId());
            flBack.setVisibility(View.VISIBLE);
            flFront.setVisibility(View.GONE);

            final int index = i;
            cardView.setOnClickListener(v -> onCardClicked(index));

            cardViews.add(cardView);
            glCardsContainer.addView(cardView);
        }
    }

    private void onCardClicked(int index) {
        if (isBusy || index == firstFlippedIndex) {
            return;
        }

        MemoryCard card = cards.get(index);
        if (card.isFaceUp() || card.isMatched()) {
            return;
        }

        // Phát âm thanh lật thẻ
        if (soundManager != null) {
            soundManager.playSfx(R.raw.m2_sfx_celebrate);
        }

        // Lật ngửa thẻ bài với hiệu ứng xoay 3D
        flipCard(cardViews.get(index), true, () -> {
            card.setFaceUp(true);

            if (firstFlippedIndex == -1) {
                // Thẻ thứ 1 trong cặp
                firstFlippedIndex = index;
            } else {
                // Thẻ thứ 2 trong cặp -> Tiến hành so sánh
                isBusy = true;
                int secondFlippedIndex = index;
                checkMatch(firstFlippedIndex, secondFlippedIndex);
            }
        });
    }

    private void checkMatch(int index1, int index2) {
        MemoryCard card1 = cards.get(index1);
        MemoryCard card2 = cards.get(index2);

        if (card1.getImageResId() == card2.getImageResId()) {
            // Trùng khớp cặp hình!
            card1.setMatched(true);
            card2.setMatched(true);
            matchedPairsCount++;
            updatePairsText();

            Toast.makeText(this, "🎉 Giỏi quá! Tìm thấy 1 cặp hình!", Toast.LENGTH_SHORT).show();

            // Hiệu ứng nảy sáng nhẹ cho 2 thẻ bài đúng
            animateMatchedCard(cardViews.get(index1));
            animateMatchedCard(cardViews.get(index2));

            firstFlippedIndex = -1;
            isBusy = false;

            // Kiểm tra hoàn thành màn chơi
            if (matchedPairsCount == totalPairsInCurrentLevel) {
                totalStarsEarned++;
                saveGameProgress("memory", currentLevel + 1, 3, totalStarsEarned);
                glCardsContainer.postDelayed(() -> {
                    if (currentLevel < 2) {
                        Toast.makeText(this, "🌟 Xuất sắc! Chuẩn bị sang màn tiếp theo...", Toast.LENGTH_SHORT).show();
                        currentLevel++;
                        loadLevel(currentLevel);
                    } else {
                        finishGame();
                    }
                }, 1000);
            }
        } else {
            // Không trùng khớp -> Tự động úp lại sau 1 giây
            glCardsContainer.postDelayed(() -> {
                flipCard(cardViews.get(index1), false, null);
                flipCard(cardViews.get(index2), false, () -> {
                    card1.setFaceUp(false);
                    card2.setFaceUp(false);
                    firstFlippedIndex = -1;
                    isBusy = false;
                });
            }, 1000);
        }
    }

    // Hiệu ứng xoay Lật Thẻ Bài 3D sinh động
    private void flipCard(View cardView, boolean showFront, @Nullable Runnable onEnd) {
        View flBack = cardView.findViewById(R.id.fl_card_back);
        View flFront = cardView.findViewById(R.id.fl_card_front);

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(cardView, "rotationY", 0f, 90f);
        anim1.setDuration(150);
        anim1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (showFront) {
                    flBack.setVisibility(View.GONE);
                    flFront.setVisibility(View.VISIBLE);
                } else {
                    flBack.setVisibility(View.VISIBLE);
                    flFront.setVisibility(View.GONE);
                }
                ObjectAnimator anim2 = ObjectAnimator.ofFloat(cardView, "rotationY", -90f, 0f);
                anim2.setDuration(150);
                anim2.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (onEnd != null) {
                            onEnd.run();
                        }
                    }
                });
                anim2.start();
            }
        });
        anim1.start();
    }

    private void animateMatchedCard(View cardView) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(cardView, "scaleX", 1.0f, 1.15f, 1.0f);
        anim.setDuration(300);
        anim.start();
    }

    private void updatePairsText() {
        tvMatchedPairs.setText("Cặp tìm được: " + matchedPairsCount + " / " + totalPairsInCurrentLevel);
    }

    private void finishGame() {
        int finalStars = totalStarsEarned >= 3 ? 3 : (totalStarsEarned >= 2 ? 2 : 1);
        saveGameProgress("memory", 3, 3, finalStars);
        showVictoryDialog(finalStars,
                this::finish,
                () -> {
                    currentLevel = 0;
                    totalStarsEarned = 0;
                    loadLevel(currentLevel);
                }
        );
    }
}
