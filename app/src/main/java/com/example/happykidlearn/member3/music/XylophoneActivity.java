package com.example.happykidlearn.member3.music;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.happykidlearn.R;
import com.example.happykidlearn.base.BaseGameActivity;

import java.util.HashSet;
import java.util.Set;

public class XylophoneActivity extends BaseGameActivity {

    private XylophoneSoundEngine soundEngine;
    private final View[] keys = new View[7];
    private final Set<Integer> playedNotes = new HashSet<>();
    private boolean isVictoryShown = false;

    private TextView tvProgress;
    private FrameLayout layoutBoard;

    // Bài hát mẫu: Twinkle Twinkle Little Star
    private final int[] melodyNotes = {0, 0, 4, 4, 5, 5, 4, -1, 3, 3, 2, 2, 1, 1, 0};
    private int melodyIndex = 0;
    private boolean isPlayingMelody = false;
    private final Handler melodyHandler = new Handler();
    private final Runnable melodyRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isPlayingMelody) return;

            int note = melodyNotes[melodyIndex];
            if (note != -1) {
                animateAndPlayNote(note);
            }

            melodyIndex++;
            if (melodyIndex < melodyNotes.length) {
                melodyHandler.postDelayed(this, 500);
            } else {
                isPlayingMelody = false;
                Button btnMelody = findViewById(R.id.btn_m3_play_melody);
                if (btnMelody != null) btnMelody.setText(R.string.m3_btn_play_melody);
            }
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m3_xylophone);

        setupHeader(getString(R.string.m3_game_music_title));
        soundEngine = new XylophoneSoundEngine();

        layoutBoard = findViewById(R.id.layout_m3_xylo_board);
        tvProgress = findViewById(R.id.tv_m3_xylo_progress);

        int[] keyIds = {
            R.id.m3_key_0, R.id.m3_key_1, R.id.m3_key_2,
            R.id.m3_key_3, R.id.m3_key_4, R.id.m3_key_5, R.id.m3_key_6
        };

        for (int i = 0; i < 7; i++) {
            keys[i] = findViewById(keyIds[i]);
            final int noteIndex = i;

            keys[i].setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    stopMelody();
                    animateAndPlayNote(noteIndex);
                }
                return true;
            });
        }

        Button btnMelody = findViewById(R.id.btn_m3_play_melody);
        btnMelody.setOnClickListener(v -> {
            if (isPlayingMelody) {
                stopMelody();
            } else {
                startMelody();
            }
        });

        updateProgressUI();
    }

    private void animateAndPlayNote(int noteIndex) {
        if (soundManager != null && !soundManager.isSfxMuted()) {
            soundEngine.playNote(noteIndex);
        }

        playedNotes.add(noteIndex);
        updateProgressUI();

        if (playedNotes.size() == 7 && !isVictoryShown) {
            isVictoryShown = true;
            saveGameProgress("music", 7, 7, 3);
            showVictoryDialog(3, this::finish, () -> {
                playedNotes.clear();
                isVictoryShown = false;
                updateProgressUI();
            });
        }

        View key = keys[noteIndex];
        key.setScaleX(1.15f);
        key.setScaleY(1.15f);
        key.setAlpha(0.7f);

        key.animate()
           .scaleX(1.0f)
           .scaleY(1.0f)
           .alpha(1.0f)
           .setDuration(300)
           .setInterpolator(new OvershootInterpolator())
           .start();

        showFlyingNoteAnimation(key);
    }

    private void showFlyingNoteAnimation(View key) {
        TextView tvNote = new TextView(this);
        tvNote.setText("♪");
        tvNote.setTextSize(32f); // Chỉnh lại theo float
        tvNote.setTextColor(Color.parseColor("#52B788"));
        tvNote.setTypeface(null, Typeface.BOLD);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Calculate center of the key relative to the board
        int[] boardLoc = new int[2];
        layoutBoard.getLocationOnScreen(boardLoc);

        int[] keyLoc = new int[2];
        key.getLocationOnScreen(keyLoc);

        float startX = keyLoc[0] - boardLoc[0] + key.getWidth() / 2f - 24;
        float startY = keyLoc[1] - boardLoc[1] + key.getHeight() / 2f;

        tvNote.setTranslationX(startX);
        tvNote.setTranslationY(startY);

        layoutBoard.addView(tvNote, params);

        tvNote.animate()
                .translationY(startY - 150)
                .alpha(0f)
                .setDuration(800)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> layoutBoard.removeView(tvNote))
                .start();
    }

    private void updateProgressUI() {
        setGameProgress(playedNotes.size(), 7);
        tvProgress.setText(String.format(getString(R.string.m3_lbl_notes_discovered), playedNotes.size()));
    }

    private void startMelody() {
        isPlayingMelody = true;
        melodyIndex = 0;
        Button btnMelody = findViewById(R.id.btn_m3_play_melody);
        if (btnMelody != null) btnMelody.setText(R.string.m3_btn_stop_melody);
        melodyHandler.post(melodyRunnable);
    }

    private void stopMelody() {
        isPlayingMelody = false;
        melodyHandler.removeCallbacks(melodyRunnable);
        Button btnMelody = findViewById(R.id.btn_m3_play_melody);
        if (btnMelody != null) btnMelody.setText(R.string.m3_btn_play_melody);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (soundManager != null) {
            soundManager.pauseBgm();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMelody();
    }

    @Override
    protected void onDestroy() {
        stopMelody();
        if (soundEngine != null) {
            soundEngine.release();
        }
        super.onDestroy();
    }
}
