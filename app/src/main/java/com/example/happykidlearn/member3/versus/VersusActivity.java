package com.example.happykidlearn.member3.versus;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.happykidlearn.R;
import com.example.happykidlearn.base.BaseGameActivity;

public class VersusActivity extends BaseGameActivity {

    private final int MAX_SCORE = 25;

    private int p1Score = 0;
    private int p2Score = 0;
    private boolean isPlaying = false;

    private View layoutP1, layoutP2;
    private TextView tvP1Score, tvP2Score;
    private TextView tvP1Result, tvP2Result;
    private ProgressBar pbP1, pbP2;
    private Button btnStart;

    private final Handler delayHandler = new Handler(Looper.getMainLooper());

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m3_versus);

        setupHeader(getString(R.string.m3_game_versus_title));

        // Luôn hiển thị tiến độ 0/25 ở Header ban đầu
        setGameProgress(0, MAX_SCORE);

        layoutP1 = findViewById(R.id.layout_m3_p1);
        layoutP2 = findViewById(R.id.layout_m3_p2);

        tvP1Score = findViewById(R.id.tv_m3_p1_score);
        tvP2Score = findViewById(R.id.tv_m3_p2_score);

        tvP1Result = findViewById(R.id.tv_m3_p1_result);
        tvP2Result = findViewById(R.id.tv_m3_p2_result);

        pbP1 = findViewById(R.id.pb_m3_p1);
        pbP2 = findViewById(R.id.pb_m3_p2);

        btnStart = findViewById(R.id.btn_m3_start_versus);

        btnStart.setOnClickListener(v -> {
            if (soundManager != null) soundManager.playSfx(R.raw.m2_sfx_tap);
            startGame();
        });

        // Player 1 (Top) Touch Listener
        layoutP1.setOnTouchListener((v, event) -> {
            if (isPlaying && event.getAction() == MotionEvent.ACTION_DOWN) {
                handleTap(1, v);
                return true;
            }
            return false;
        });

        // Player 2 (Bottom) Touch Listener
        layoutP2.setOnTouchListener((v, event) -> {
            if (isPlaying && event.getAction() == MotionEvent.ACTION_DOWN) {
                handleTap(2, v);
                return true;
            }
            return false;
        });
    }

    private void startGame() {
        p1Score = 0;
        p2Score = 0;
        isPlaying = true;

        updateUI();
        tvP1Result.setVisibility(View.GONE);
        tvP2Result.setVisibility(View.GONE);
        btnStart.setVisibility(View.GONE);
    }

    private void handleTap(int playerIndex, View layoutView) {
        if (soundManager != null) {
            soundManager.playSfx(R.raw.m2_sfx_tap);
        }

        // Bounce effect cho cảm giác nảy
        layoutView.animate()
                .scaleX(0.97f).scaleY(0.97f)
                .setDuration(40)
                .withEndAction(() -> {
                    layoutView.animate()
                            .scaleX(1.0f).scaleY(1.0f)
                            .setDuration(100)
                            .setInterpolator(new OvershootInterpolator())
                            .start();
                }).start();

        if (playerIndex == 1) {
            p1Score++;
        } else {
            p2Score++;
        }

        updateUI();

        if (p1Score >= MAX_SCORE || p2Score >= MAX_SCORE) {
            finishMatch(p1Score >= MAX_SCORE ? 1 : 2);
        }
    }

    private void updateUI() {
        tvP1Score.setText(p1Score + " / " + MAX_SCORE);
        tvP2Score.setText(p2Score + " / " + MAX_SCORE);

        pbP1.setProgress(p1Score);
        pbP2.setProgress(p2Score);

        int highestScore = Math.max(p1Score, p2Score);
        setGameProgress(highestScore, MAX_SCORE);
    }

    private void finishMatch(int winnerIndex) {
        isPlaying = false;

        tvP1Result.setVisibility(View.VISIBLE);
        tvP2Result.setVisibility(View.VISIBLE);

        if (winnerIndex == 1) {
            tvP1Result.setText(getString(R.string.m3_msg_win));
            tvP2Result.setText(getString(R.string.m3_msg_lose));
        } else {
            tvP1Result.setText(getString(R.string.m3_msg_lose));
            tvP2Result.setText(getString(R.string.m3_msg_win));
        }

        if (soundManager != null) {
            soundManager.playSfx(R.raw.m2_sfx_celebrate);
        }

        saveGameProgress("versus", MAX_SCORE, MAX_SCORE, 3);

        // Trì hoãn 800ms để 2 người chơi xem Banner kết quả, sau đó mới hiện Dialog
        delayHandler.postDelayed(() -> {
            showVictoryDialog(3, this::finish, this::resetMatch);
        }, 800);
    }

    private void resetMatch() {
        p1Score = 0;
        p2Score = 0;
        updateUI();
        tvP1Result.setVisibility(View.GONE);
        tvP2Result.setVisibility(View.GONE);
        btnStart.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        // Hủy bỏ callback chờ dialog để tránh rò rỉ nếu người dùng bấm Back sớm
        delayHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
