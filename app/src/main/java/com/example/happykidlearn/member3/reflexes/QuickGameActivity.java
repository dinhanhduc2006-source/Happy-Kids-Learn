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
    private Button btnStartGame;

    private CountDownTimer countDownTimer;
    private int score = 0;
    private final int MAX_SCORE_GOAL = 20;
    private final long GAME_DURATION_MS = 30000; // 30 giây

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m3_quick_game);

        setupHeader(getString(R.string.m3_game_reflexes_title));

        bubbleGameView = findViewById(R.id.view_m3_bubble_game);
        tvScore = findViewById(R.id.tv_m3_score);
        tvTimer = findViewById(R.id.tv_m3_timer);
        layoutStartOverlay = findViewById(R.id.layout_m3_start_overlay);
        btnStartGame = findViewById(R.id.btn_m3_start_game);

        updateScoreText();
        tvTimer.setText(getString(R.string.m3_lbl_time, 30));
        setGameProgress(0, MAX_SCORE_GOAL);

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
        setGameProgress(0, MAX_SCORE_GOAL);
        bubbleGameView.resetGame();
        bubbleGameView.startGame();

        countDownTimer = new CountDownTimer(GAME_DURATION_MS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long sec = millisUntilFinished / 1000;
                tvTimer.setText(getString(R.string.m3_lbl_time, sec));
            }

            @Override
            public void onFinish() {
                tvTimer.setText(getString(R.string.m3_lbl_time, 0));
                endGame();
            }
        }.start();
    }

    private void onBubblePopped() {
        score++;
        updateScoreText();
        setGameProgress(Math.min(score, MAX_SCORE_GOAL), MAX_SCORE_GOAL);

        if (soundManager != null) {
            if (score % 5 == 0) {
                soundManager.playSfx(R.raw.m2_sfx_correct);
            } else {
                soundManager.playSfx(R.raw.m2_sfx_tap);
            }
        }
    }

    private void updateScoreText() {
        tvScore.setText(getString(R.string.m3_lbl_score, score));
    }

    private void endGame() {
        bubbleGameView.pauseGame();

        int stars = 1;
        if (score >= 20) {
            stars = 3;
        } else if (score >= 10) {
            stars = 2;
        }

        saveGameProgress("reflexes", Math.min(score, MAX_SCORE_GOAL), MAX_SCORE_GOAL, stars);

        if (soundManager != null) {
            soundManager.playSfx(R.raw.m2_sfx_celebrate);
        }

        showVictoryDialog(stars, this::finish, this::restartGame);
    }

    private void restartGame() {
        layoutStartOverlay.setVisibility(View.VISIBLE);
        tvTimer.setText(getString(R.string.m3_lbl_time, 30));
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
