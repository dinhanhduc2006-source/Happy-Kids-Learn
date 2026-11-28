package com.example.happykidlearn.member3.reflexes;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.happykidlearn.R;
import com.example.happykidlearn.base.BaseGameActivity;
import com.example.happykidlearn.member3.view.BubbleGameView;

public class QuickGameActivity extends BaseGameActivity {

    private BubbleGameView bubbleGameView;
    private TextView tvScore;
    private TextView tvTimer;
    private View layoutStartOverlay;
    private TextView tvOverlayTitle;
    private TextView tvOverlayScore;
    private Button btnStartGame;

    private CountDownTimer countDownTimer;
    private int score = 0;
    private final long GAME_DURATION_MS = 30000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m3_quick_game);

        setupHeader(getString(R.string.m3_game_reflexes_title));

        bubbleGameView = findViewById(R.id.view_m3_bubble_game);
        tvScore = findViewById(R.id.tv_m3_score);
        tvTimer = findViewById(R.id.tv_m3_timer);
        layoutStartOverlay = findViewById(R.id.layout_m3_start_overlay);
        tvOverlayTitle = findViewById(R.id.tv_m3_overlay_title);
        tvOverlayScore = findViewById(R.id.tv_m3_overlay_score);
        btnStartGame = findViewById(R.id.btn_m3_start_game);

        updateScoreText();
        tvTimer.setText(String.format(getString(R.string.m3_lbl_time_with_icon), 30));

        // Hide progress bar in header for free-play mode
        if (pbHeaderProgress != null) {
            View parent = (View) pbHeaderProgress.getParent();
            if (parent != null) {
                parent.setVisibility(View.INVISIBLE);
            }
        }

        btnStartGame.setOnClickListener(v -> {
            if (soundManager != null) soundManager.playSfx(R.raw.m2_sfx_tap);
            layoutStartOverlay.setVisibility(View.GONE);
            startPlaying();
        });

        bubbleGameView.setOnBubblePopListener(this::onBubblePopped);
    }

    private void startPlaying() {
        score = 0;
        updateScoreText();
        bubbleGameView.resetGame();
        bubbleGameView.startGame();

        countDownTimer = new CountDownTimer(GAME_DURATION_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long sec = millisUntilFinished / 1000;
                tvTimer.setText(String.format(getString(R.string.m3_lbl_time_with_icon), sec));
            }

            @Override
            public void onFinish() {
                tvTimer.setText(String.format(getString(R.string.m3_lbl_time_with_icon), 0));
                endGame();
            }
        }.start();
    }

    private void onBubblePopped() {
        score++;
        updateScoreText();

        if (soundManager != null) {
            if (score % 5 == 0) {
                soundManager.playSfx(R.raw.m2_sfx_correct);
            } else {
                soundManager.playSfx(R.raw.m2_sfx_tap);
            }
        }
    }

    private void updateScoreText() {
        tvScore.setText(String.format(getString(R.string.m3_lbl_score_with_icon), score));
    }

    private void endGame() {
        bubbleGameView.pauseGame();

        if (soundManager != null) {
            soundManager.playSfx(R.raw.m2_sfx_celebrate);
        }

        // Hiện kết quả trực tiếp ngay tại thẻ overlay
        tvOverlayTitle.setText(R.string.m3_lbl_time_out);
        tvOverlayScore.setText(String.format(getString(R.string.m3_lbl_final_score), score));
        tvOverlayScore.setVisibility(View.VISIBLE);
        btnStartGame.setText(R.string.m3_btn_play_again);

        layoutStartOverlay.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        bubbleGameView.pauseGame();
    }

    @Override
    protected void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        bubbleGameView.pauseGame();
        super.onDestroy();
    }
}
