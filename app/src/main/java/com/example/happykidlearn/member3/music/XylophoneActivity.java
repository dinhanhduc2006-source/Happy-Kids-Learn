package com.example.happykidlearn.member3.music;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.happykidlearn.R;
import com.example.happykidlearn.base.BaseGameActivity;

import java.util.Arrays;
import java.util.Random;

public class XylophoneActivity extends BaseGameActivity {

    private XylophoneSoundEngine soundEngine;

    private final View[] keys = new View[7];
    private final TextView[] lights = new TextView[7];
    private final int[] lastPointerKey = new int[20];

    private FrameLayout layoutBoard;
    private View layoutKeysContainer;
    private View layoutSongControls;
    private TextView tvCongrats;

    private Button btnModeFree;
    private Button btnModeLight;
    private Button btnSelectSong;
    private Button btnPlayMelody;

    private boolean isFollowLightMode = false;
    private boolean isPlayingMelody = false;

    // 3 Bài hát ngắn cho trẻ mầm non
    private final int[][] songs = {
            {0, 0, 4, 4, 5, 5, 4, 3, 3, 2, 2, 1, 1, 0}, // Ngôi sao lấp lánh
            {2, 2, 2, 2, 2, 2, 2, 4, 0, 1, 2},          // Chuông ngân vang
            {2, 1, 0, 1, 2, 2, 2, 1, 1, 1, 2, 4, 4}          // Cừu con đi học
    };

    private final String[] songTitles = {
            "⭐ Ngôi sao lấp lánh",
            "🔔 Chuông ngân vang",
            "🐑 Cừu con đi học"
    };

    private int selectedSongIndex = 0;
    private int songNoteIndex = 0;
    private ObjectAnimator pulseAnimator;

    private final Random random = new Random();
    private final String[] noteSymbols = {"♪", "♫", "★", "♩", "♬"};

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable melodyRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isPlayingMelody) return;

            int[] currentSong = songs[selectedSongIndex];
            if (songNoteIndex < currentSong.length) {
                int note = currentSong[songNoteIndex];
                if (note >= 0 && note < 7) {
                    onKeyTriggered(note);
                }
                songNoteIndex++;
                handler.postDelayed(this, 500);
            } else {
                stopMelody();
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

        // Ẩn thanh tiến trình trên Header cho chế độ chơi tự do
        if (pbHeaderProgress != null) {
            View parent = (View) pbHeaderProgress.getParent();
            if (parent != null) {
                parent.setVisibility(View.INVISIBLE);
            }
        }

        initViews();
        setupGlissandoTouch();
        setupControls();
    }

    private void initViews() {
        layoutBoard = findViewById(R.id.layout_m3_xylo_board);
        layoutKeysContainer = findViewById(R.id.layout_m3_keys_container);
        layoutSongControls = findViewById(R.id.layout_m3_song_controls);
        tvCongrats = findViewById(R.id.tv_m3_xylo_congrats);

        btnModeFree = findViewById(R.id.btn_m3_mode_free);
        btnModeLight = findViewById(R.id.btn_m3_mode_light);
        btnSelectSong = findViewById(R.id.btn_m3_select_song);
        btnPlayMelody = findViewById(R.id.btn_m3_play_melody);

        int[] keyIds = {
                R.id.m3_key_0, R.id.m3_key_1, R.id.m3_key_2,
                R.id.m3_key_3, R.id.m3_key_4, R.id.m3_key_5, R.id.m3_key_6
        };

        int[] lightIds = {
                R.id.tv_light_0, R.id.tv_light_1, R.id.tv_light_2,
                R.id.tv_light_3, R.id.tv_light_4, R.id.tv_light_5, R.id.tv_light_6
        };

        for (int i = 0; i < 7; i++) {
            keys[i] = findViewById(keyIds[i]);
            lights[i] = findViewById(lightIds[i]);
        }

        Arrays.fill(lastPointerKey, -1);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupGlissandoTouch() {
        layoutKeysContainer.setOnTouchListener((v, event) -> {
            int action = event.getActionMasked();
            int pointerIndex = event.getActionIndex();
            int pointerId = event.getPointerId(pointerIndex);

            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN || action == MotionEvent.ACTION_MOVE) {
                stopMelody();
                for (int i = 0; i < event.getPointerCount(); i++) {
                    int pId = event.getPointerId(i);
                    if (pId >= 0 && pId < lastPointerKey.length) {
                        float x = event.getX(i);
                        float y = event.getY(i);
                        int keyIdx = findKeyIndexAt(x, y);

                        if (keyIdx != -1 && keyIdx != lastPointerKey[pId]) {
                            lastPointerKey[pId] = keyIdx;
                            onKeyTriggered(keyIdx);
                        } else if (keyIdx == -1) {
                            lastPointerKey[pId] = -1;
                        }
                    }
                }
            }

            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_CANCEL) {
                if (pointerId >= 0 && pointerId < lastPointerKey.length) {
                    lastPointerKey[pointerId] = -1;
                }
                if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    Arrays.fill(lastPointerKey, -1);
                }
            }
            return true;
        });
    }

    private int findKeyIndexAt(float x, float y) {
        for (int i = 0; i < 7; i++) {
            View k = keys[i];
            if (k != null) {
                if (x >= k.getLeft() && x <= k.getRight() && y >= k.getTop() && y <= k.getBottom()) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void setupControls() {
        btnModeFree.setOnClickListener(v -> {
            playSoundTap();
            switchToFreeMode();
        });

        btnModeLight.setOnClickListener(v -> {
            playSoundTap();
            switchToLightMode();
        });

        btnSelectSong.setOnClickListener(v -> {
            playSoundTap();
            selectedSongIndex = (selectedSongIndex + 1) % songs.length;
            btnSelectSong.setText(songTitles[selectedSongIndex]);
            stopMelody();
            songNoteIndex = 0;
            updateTargetLight();
        });

        btnPlayMelody.setOnClickListener(v -> {
            playSoundTap();
            if (isPlayingMelody) {
                stopMelody();
            } else {
                startMelody();
            }
        });
    }

    private void switchToFreeMode() {
        isFollowLightMode = false;
        btnModeFree.setBackgroundResource(R.drawable.m3_btn_pill_green);
        btnModeFree.setTextColor(Color.WHITE);
        btnModeLight.setBackgroundResource(R.drawable.m3_bg_item_tile);
        btnModeLight.setTextColor(ContextCompat.getColor(this, R.color.m3_text_main));

        layoutSongControls.setVisibility(View.GONE);
        stopMelody();
        clearTargetLight();
    }

    private void switchToLightMode() {
        isFollowLightMode = true;
        btnModeLight.setBackgroundResource(R.drawable.m3_btn_pill_green);
        btnModeLight.setTextColor(Color.WHITE);
        btnModeFree.setBackgroundResource(R.drawable.m3_bg_item_tile);
        btnModeFree.setTextColor(ContextCompat.getColor(this, R.color.m3_text_main));

        layoutSongControls.setVisibility(View.VISIBLE);
        songNoteIndex = 0;
        updateTargetLight();
    }

    private void onKeyTriggered(int noteIndex) {
        if (soundManager != null && !soundManager.isSfxMuted()) {
            soundEngine.playNote(noteIndex);
        }

        View key = keys[noteIndex];
        key.setScaleX(1.15f);
        key.setScaleY(1.15f);
        key.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(250)
                .setInterpolator(new OvershootInterpolator())
                .start();

        showFlyingNoteAnimation(key);

        // Xử lý chế độ "Chơi theo đèn"
        if (isFollowLightMode && !isPlayingMelody) {
            int[] currentSong = songs[selectedSongIndex];
            if (songNoteIndex < currentSong.length) {
                int targetKey = currentSong[songNoteIndex];
                if (noteIndex == targetKey) {
                    songNoteIndex++;
                    if (songNoteIndex < currentSong.length) {
                        updateTargetLight();
                    } else {
                        // Hoàn thành bài hát!
                        playWaveCelebration();
                    }
                }
            }
        }
    }

    private void updateTargetLight() {
        clearTargetLight();
        if (!isFollowLightMode) return;

        int[] currentSong = songs[selectedSongIndex];
        if (songNoteIndex >= 0 && songNoteIndex < currentSong.length) {
            int targetKey = currentSong[songNoteIndex];
            if (targetKey >= 0 && targetKey < 7) {
                TextView lightTv = lights[targetKey];
                lightTv.setVisibility(View.VISIBLE);

                pulseAnimator = ObjectAnimator.ofFloat(lightTv, "alpha", 0.3f, 1.0f);
                pulseAnimator.setDuration(400);
                pulseAnimator.setRepeatCount(ObjectAnimator.INFINITE);
                pulseAnimator.setRepeatMode(ObjectAnimator.REVERSE);
                pulseAnimator.start();
            }
        }
    }

    private void clearTargetLight() {
        if (pulseAnimator != null) {
            pulseAnimator.cancel();
            pulseAnimator = null;
        }
        for (int i = 0; i < 7; i++) {
            if (lights[i] != null) {
                lights[i].setVisibility(View.INVISIBLE);
                lights[i].setAlpha(1.0f);
            }
        }
    }

    private void playWaveCelebration() {
        clearTargetLight();
        if (soundManager != null) {
            soundManager.playSfx(R.raw.m2_sfx_celebrate);
        }

        tvCongrats.setVisibility(View.VISIBLE);

        for (int i = 0; i < 7; i++) {
            final View key = keys[i];
            final int delay = i * 70;
            handler.postDelayed(() -> {
                key.setScaleX(1.2f);
                key.setScaleY(1.2f);
                key.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(300)
                        .setInterpolator(new OvershootInterpolator())
                        .start();
            }, delay);
        }

        handler.postDelayed(() -> {
            tvCongrats.setVisibility(View.GONE);
            songNoteIndex = 0;
            updateTargetLight();
        }, 2500);
    }

    private void showFlyingNoteAnimation(View key) {
        TextView tvNote = new TextView(this);
        String symbol = noteSymbols[random.nextInt(noteSymbols.length)];
        tvNote.setText(symbol);
        tvNote.setTextSize(32f);
        tvNote.setTextColor(Color.parseColor("#52B788"));
        tvNote.setTypeface(null, Typeface.BOLD);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

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
                .translationY(startY - 160)
                .alpha(0f)
                .setDuration(800)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> layoutBoard.removeView(tvNote))
                .start();
    }

    private void startMelody() {
        isPlayingMelody = true;
        songNoteIndex = 0;
        btnPlayMelody.setText("⏹ Dừng");
        handler.post(melodyRunnable);
    }

    private void stopMelody() {
        isPlayingMelody = false;
        handler.removeCallbacks(melodyRunnable);
        if (btnPlayMelody != null) {
            btnPlayMelody.setText("▶️ Nghe mẫu");
        }
    }

    private void playSoundTap() {
        if (soundManager != null) {
            soundManager.playSfx(R.raw.m2_sfx_tap);
        }
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
        clearTargetLight();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onDestroy() {
        stopMelody();
        clearTargetLight();
        handler.removeCallbacksAndMessages(null);
        if (soundEngine != null) {
            soundEngine.release();
        }
        super.onDestroy();
    }
}
