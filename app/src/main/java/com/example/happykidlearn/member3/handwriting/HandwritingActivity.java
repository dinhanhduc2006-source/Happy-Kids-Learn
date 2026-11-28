package com.example.happykidlearn.member3.handwriting;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.happykidlearn.R;
import com.example.happykidlearn.base.BaseGameActivity;
import com.example.happykidlearn.member3.view.BaseCanvasView;

public class HandwritingActivity extends BaseGameActivity {

    private BaseCanvasView canvasView;
    private TextView tvLetterGuide;

    private final String[] letters = {"A", "B", "C", "D", "E", "G", "H", "I", "K", "L"};
    private int currentIndex = 0;
    private int completedCount = 0;

    private TextView tvBanner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m3_handwriting);

        setupHeader(getString(R.string.m3_game_handwriting_title));

        canvasView = findViewById(R.id.view_m3_canvas);
        tvLetterGuide = findViewById(R.id.tv_m3_letter_guide);
        tvBanner = findViewById(R.id.tv_m3_banner);

        setupControls();
        loadLetter(currentIndex);
    }

    private void setupControls() {
        Button btnPrev = findViewById(R.id.btn_m3_prev);
        Button btnNext = findViewById(R.id.btn_m3_next);
        Button btnFinish = findViewById(R.id.btn_m3_finish_letter);

        Button btnEraser = findViewById(R.id.btn_m3_eraser);
        Button btnUndo = findViewById(R.id.btn_m3_undo);
        Button btnClear = findViewById(R.id.btn_m3_clear);

        btnPrev.setOnClickListener(v -> {
            playSoundTap();
            if (currentIndex > 0) {
                currentIndex--;
                loadLetter(currentIndex);
            }
        });

        btnNext.setOnClickListener(v -> {
            playSoundTap();
            if (currentIndex < letters.length - 1) {
                currentIndex++;
                loadLetter(currentIndex);
            }
        });

        btnFinish.setOnClickListener(v -> {
            if (canvasView.isEmpty()) {
                Toast.makeText(this, "Bé hãy tập viết nét chữ trước nhé!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (soundManager != null) {
                soundManager.playSfx(R.raw.m2_sfx_correct);
            }
            completedCount++;
            setGameProgress(completedCount, letters.length);

            if (completedCount >= letters.length) {
                saveGameProgress("handwriting", letters.length, letters.length, 3);
                showVictoryDialog(3, this::finish, this::resetGame);
            } else {
                if (currentIndex < letters.length - 1) {
                    currentIndex++;
                    loadLetter(currentIndex);
                } else {
                    saveGameProgress("handwriting", letters.length, letters.length, 3);
                    showVictoryDialog(3, this::finish, this::resetGame);
                }
            }
        });

        btnEraser.setOnClickListener(v -> {
            playSoundTap();
            canvasView.setEraserMode(true);
        });

        btnUndo.setOnClickListener(v -> {
            playSoundTap();
            canvasView.undo();
        });

        btnClear.setOnClickListener(v -> {
            playSoundTap();
            canvasView.clearCanvas();
        });

        int[] colorIds = {R.id.color_red, R.id.color_orange, R.id.color_yellow, R.id.color_green, R.id.color_blue, R.id.color_purple};
        int[] colors = {
                ContextCompat.getColor(this, R.color.m3_color_red),
                ContextCompat.getColor(this, R.color.m3_color_orange),
                ContextCompat.getColor(this, R.color.m3_color_yellow),
                ContextCompat.getColor(this, R.color.m3_color_green),
                ContextCompat.getColor(this, R.color.m3_color_blue),
                ContextCompat.getColor(this, R.color.m3_color_purple)
        };

        for (int i = 0; i < colorIds.length; i++) {
            final int c = colors[i];
            findViewById(colorIds[i]).setOnClickListener(v -> {
                playSoundTap();
                canvasView.setStrokeColor(c);
            });
        }
    }

    private void loadLetter(int index) {
        String letter = letters[index];
        tvLetterGuide.setText(letter);
        if (tvBanner != null) {
            tvBanner.setText(String.format(getString(R.string.m3_banner_handwriting), letter));
        }
        canvasView.clearCanvas();
        setGameProgress(completedCount, letters.length);
    }

    private void playSoundTap() {
        if (soundManager != null) {
            soundManager.playSfx(R.raw.m2_sfx_tap);
        }
    }

    private void resetGame() {
        currentIndex = 0;
        completedCount = 0;
        loadLetter(currentIndex);
    }
}