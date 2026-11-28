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
    private ProgressBar pbP1, pbP2;
    private Button btnStart;
    private Button btnP1Tap, btnP2Tap;
    private TextView tvVsBadge, tvCountdown;

    private View layoutResultOverlay;
    private TextView tvWinnerText;

    private final Handler delayHandler = new Handler(Looper.getMainLooper());
    private int countdown = 3;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m3_versus);

        setupHeader(getString(R.string.m3_game_versus_title));

        // Hide progress bar in header for free-play mode
        if (pbHeaderProgress != null) {
            View parent = (View) pbHeaderProgress.getParent();
            if (parent != null) {
                parent.setVisibility(View.INVISIBLE);
            }
        }

        layoutP1 = findViewById(R.id.layout_m3_p1);
        layoutP2 = findViewById(R.id.layout_m3_p2);

        tvP1Score = findViewById(R.id.tv_m3_p1_score);
        tvP2Score = findViewById(R.id.tv_m3_p2_score);

        pbP1 = findViewById(R.id.pb_m3_p1);
        pbP2 = findViewById(R.id.pb_m3_p2);

        btnStart = findViewById(R.id.btn_m3_start_versus);
        btnP1Tap = findViewById(R.id.btn_m3_p1_tap);
        btnP2Tap = findViewById(R.id.btn_m3_p2_tap);

        tvVsBadge = findViewById(R.id.tv_m3_vs_badge);
        tvCountdown = findViewById(R.id.tv_m3_countdown);

        layoutResultOverlay = findViewById(R.id.layout_m3_versus_result_overlay);
        tvWinnerText = findViewById(R.id.tv_m3_versus_winnerText);
        Button btnReplayVersus = findViewById(R.id.btn_m3_versus_replay);

        btnStart.setOnClickListener(v -> {
            if (soundManager != null) soundManager.playSfx(R.raw.m2_sfx_tap);
            startCountdown();
        });

        btnReplayVersus.setOnClickListener(v -> {
            if (soundManager != null) soundManager.playSfx(R.raw.m2_sfx_tap);
            layoutResultOverlay.setVisibility(View.GONE);
            startCountdown();
        });

        // Vô hiệu hóa nút Bấm Nhanh khi chưa chơi
        btnP1Tap.setClickable(false);
        btnP2Tap.setClickable(false);

        // Player 1 (Top) Touch Listener - chạm vào bất kỳ đâu trên phần thẻ của P1
        layoutP1.setOnTouchListener((v, event) -> {
            if (isPlaying && event.getAction() == MotionEvent.ACTION_DOWN) {
                handleTap(1, btnP1Tap); // Hiệu ứng nảy vào nút chứ không phải toàn màn hình
                return true;
            }
            return false;
        });

        // Player 2 (Bottom) Touch Listener
        layoutP2.setOnTouchListener((v, event) -> {
            if (isPlaying && event.getAction() == MotionEvent.ACTION_DOWN) {
                handleTap(2, btnP2Tap);
                return true;
            }
            return false;
        });
    }

    private void startCountdown() {
        btnStart.setVisibility(View.GONE);
        tvVsBadge.setVisibility(View.GONE);
        tvCountdown.setVisibility(View.VISIBLE);
        layoutResultOverlay.setVisibility(View.GONE);

        p1Score = 0;
        p2Score = 0;
        updateUI();

        countdown = 3;
        runCountdownTick();
    }

    private void runCountdownTick() {
        if (countdown > 0) {
            if (soundManager != null) soundManager.playSfx(R.raw.m2_sfx_tap);
            tvCountdown.setText(String.valueOf(countdown));
            countdown--;
            delayHandler.postDelayed(this::runCountdownTick, 800);
        } else if (countdown == 0) {
            if (soundManager != null) soundManager.playSfx(R.raw.m2_sfx_correct);
            tvCountdown.setText("BẮT ĐẦU!");
            countdown--;
            delayHandler.postDelayed(this::runCountdownTick, 800);
        } else {
            tvCountdown.setVisibility(View.GONE);
            tvVsBadge.setVisibility(View.VISIBLE);
            isPlaying = true;
        }
    }

    private void handleTap(int playerIndex, View btnView) {
        if (soundManager != null) {
            soundManager.playSfx(R.raw.m2_sfx_tap);
        }

        btnView.animate()
                .scaleX(0.9f).scaleY(0.9f)
                .setDuration(40)
                .withEndAction(() -> {
                    btnView.animate()
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
        tvP1Score.setText(String.format("%d / %d", p1Score, MAX_SCORE));
        tvP2Score.setText(String.format("%d / %d", p2Score, MAX_SCORE));

        pbP1.setProgress(p1Score);
        pbP2.setProgress(p2Score);
    }

    private void finishMatch(int winnerIndex) {
        isPlaying = false;
        delayHandler.removeCallbacksAndMessages(null);

        if (winnerIndex == 1) {
            tvWinnerText.setText(R.string.m3_msg_p1_win);
        } else {
            tvWinnerText.setText(R.string.m3_msg_p2_win);
        }

        if (soundManager != null) {
            soundManager.playSfx(R.raw.m2_sfx_celebrate);
        }

        layoutResultOverlay.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        delayHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
