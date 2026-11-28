package com.example.happykidlearn.member3.music;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.example.happykidlearn.R;
import com.example.happykidlearn.base.BaseGameActivity;

import java.util.HashSet;
import java.util.Set;

public class XylophoneActivity extends BaseGameActivity {

    private XylophoneSoundEngine soundEngine;
    private View[] keys = new View[7];
    private Set<Integer> playedNotes = new HashSet<>();
    private boolean isVictoryShown = false;

    // Bài hát mẫu: Twinkle Twinkle Little Star (Đô Đô Sol Sol La La Sol, Pha Pha Mi Mi Rê Rê Đô)
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
                melodyHandler.postDelayed(this, 500); // Khoảng cách nửa giây
            } else {
                isPlayingMelody = false;
                Button btnMelody = findViewById(R.id.btn_m3_play_melody);
                if (btnMelody != null) btnMelody.setText("Nghe giai điệu mẫu");
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

        int[] keyIds = {
            R.id.m3_key_0, R.id.m3_key_1, R.id.m3_key_2,
            R.id.m3_key_3, R.id.m3_key_4, R.id.m3_key_5, R.id.m3_key_6
        };

        for (int i = 0; i < 7; i++) {
            keys[i] = findViewById(keyIds[i]);
            final int noteIndex = i;

            // Dùng ACTION_DOWN để phản hồi mượt mà không cần đợi thả tay
            keys[i].setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Dừng giai điệu mẫu nếu đang tự gõ
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

        setGameProgress(0, 7);
    }

    private void animateAndPlayNote(int noteIndex) {
        // Phát tiếng
        if (soundManager != null && !soundManager.isSfxMuted()) {
            soundEngine.playNote(noteIndex);
        }

        // Cập nhật tiến trình game
        playedNotes.add(noteIndex);
        setGameProgress(playedNotes.size(), 7);

        if (playedNotes.size() == 7 && !isVictoryShown) {
            isVictoryShown = true;
            saveGameProgress("music", 7, 7, 3);
            showVictoryDialog(3, this::finish, () -> {
                playedNotes.clear();
                isVictoryShown = false;
                setGameProgress(0, 7);
            });
        }

        // Hiệu ứng nảy mượt mà
        View key = keys[noteIndex];
        key.setScaleX(1.15f);
        key.setScaleY(1.15f);
        key.setAlpha(0.7f); // Sáng phím

        key.animate()
           .scaleX(1.0f)
           .scaleY(1.0f)
           .alpha(1.0f)
           .setDuration(300)
           .setInterpolator(new OvershootInterpolator())
           .start();
    }

    private void startMelody() {
        isPlayingMelody = true;
        melodyIndex = 0;
        Button btnMelody = findViewById(R.id.btn_m3_play_melody);
        if (btnMelody != null) btnMelody.setText("Dừng nghe");
        melodyHandler.post(melodyRunnable);
    }

    private void stopMelody() {
        isPlayingMelody = false;
        melodyHandler.removeCallbacks(melodyRunnable);
        Button btnMelody = findViewById(R.id.btn_m3_play_melody);
        if (btnMelody != null) btnMelody.setText("Nghe giai điệu mẫu");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tắt nhạc nền để tránh lẫn tiếng gõ đàn
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
