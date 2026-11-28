package com.example.happykidlearn.member3.coloring;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.happykidlearn.R;
import com.example.happykidlearn.base.BaseGameActivity;
import com.example.happykidlearn.member3.view.BaseCanvasView;
import com.example.happykidlearn.member3.view.FloodFillView;

public class ColoringActivity extends BaseGameActivity {

    private FloodFillView floodFillView;
    private BaseCanvasView freehandCanvas;
    private ImageView imgOutline;
    private View viewWhiteBg;
    private Button btnSwitchMode;
    private Button btnEraser;

    private boolean isFreehandMode = false;
    private int currentPictureIndex = 0;
    private int completedPictures = 0;

    private final int[] pictures = {
            R.drawable.m3_coloring_house,
            R.drawable.m3_coloring_fish,
            R.drawable.m3_coloring_flower
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m3_coloring);

        setupHeader(getString(R.string.m3_game_coloring_title));

        floodFillView = findViewById(R.id.view_m3_flood_fill);
        freehandCanvas = findViewById(R.id.view_m3_freehand_canvas);
        imgOutline = findViewById(R.id.img_m3_coloring_outline);
        viewWhiteBg = findViewById(R.id.view_m3_white_bg);

        setupControls();
        loadPicture(currentPictureIndex);
    }

    private void setupControls() {
        Button btnChangePic = findViewById(R.id.btn_m3_change_pic);
        btnSwitchMode = findViewById(R.id.btn_m3_switch_mode);
        Button btnFinishPic = findViewById(R.id.btn_m3_finish_pic);
        Button btnClear = findViewById(R.id.btn_m3_clear);
        btnEraser = findViewById(R.id.btn_m3_eraser);

        btnChangePic.setOnClickListener(v -> {
            playSoundTap();
            currentPictureIndex = (currentPictureIndex + 1) % pictures.length;
            loadPicture(currentPictureIndex);
        });

        btnSwitchMode.setOnClickListener(v -> {
            playSoundTap();
            isFreehandMode = !isFreehandMode;
            updateMode();
        });

        btnFinishPic.setOnClickListener(v -> {
            if (soundManager != null) {
                soundManager.playSfx(R.raw.m2_sfx_correct);
            }
            completedPictures++;
            setGameProgress(completedPictures, pictures.length);

            if (completedPictures >= pictures.length) {
                saveGameProgress("coloring", pictures.length, pictures.length, 3);
                showVictoryDialog(3, this::finish, this::resetGame);
            } else {
                currentPictureIndex = (currentPictureIndex + 1) % pictures.length;
                loadPicture(currentPictureIndex);
            }
        });

        btnClear.setOnClickListener(v -> {
            playSoundTap();
            if (isFreehandMode) {
                freehandCanvas.clearCanvas();
            } else {
                floodFillView.resetCanvas();
            }
        });

        btnEraser.setOnClickListener(v -> {
            playSoundTap();
            if (isFreehandMode) {
                freehandCanvas.setEraserMode(true);
            }
        });

        floodFillView.setOnFillListener(() -> {
            if (soundManager != null) {
                soundManager.playSfx(R.raw.m2_sfx_tap);
            }
        });

        freehandCanvas.setOnStrokeListener(() -> {
            // Có thể thêm âm thanh vẽ nếu cần
        });

        setupColorPalette();
    }

    private void setupColorPalette() {
        int[] colorIds = {
                R.id.color_red, R.id.color_orange, R.id.color_yellow,
                R.id.color_green, R.id.color_blue, R.id.color_purple,
                R.id.color_pink, R.id.color_brown
        };
        int[] colors = {
                ContextCompat.getColor(this, R.color.m3_color_red),
                ContextCompat.getColor(this, R.color.m3_color_orange),
                ContextCompat.getColor(this, R.color.m3_color_yellow),
                ContextCompat.getColor(this, R.color.m3_color_green),
                ContextCompat.getColor(this, R.color.m3_color_blue),
                ContextCompat.getColor(this, R.color.m3_color_purple),
                ContextCompat.getColor(this, R.color.m3_color_pink),
                ContextCompat.getColor(this, R.color.m3_color_brown)
        };

        for (int i = 0; i < colorIds.length; i++) {
            final int c = colors[i];
            final View colorView = findViewById(colorIds[i]);

            colorView.setOnClickListener(v -> {
                playSoundTap();

                // Thu nhỏ tất cả các màu
                for (int id : colorIds) {
                    findViewById(id).setScaleX(1.0f);
                    findViewById(id).setScaleY(1.0f);
                }

                // Phóng to màu đang chọn
                colorView.setScaleX(1.3f);
                colorView.setScaleY(1.3f);

                if (isFreehandMode) {
                    freehandCanvas.setStrokeColor(c);
                } else {
                    floodFillView.setFillColor(c);
                }
            });
        }
    }

    private void updateMode() {
        if (isFreehandMode) {
            btnSwitchMode.setText("Chế độ: Tô tự do");
            floodFillView.setVisibility(View.GONE);
            viewWhiteBg.setVisibility(View.VISIBLE);
            freehandCanvas.setVisibility(View.VISIBLE);
            imgOutline.setVisibility(View.VISIBLE);
            btnEraser.setVisibility(View.VISIBLE);
        } else {
            btnSwitchMode.setText("Chế độ: Đổ màu");
            floodFillView.setVisibility(View.VISIBLE);
            viewWhiteBg.setVisibility(View.GONE);
            freehandCanvas.setVisibility(View.GONE);
            imgOutline.setVisibility(View.GONE);
            btnEraser.setVisibility(View.GONE);
        }
    }

    private void loadPicture(int index) {
        int resId = pictures[index];

        // Load cho chế độ Đổ màu
        floodFillView.setDrawableRes(resId);

        // Load cho chế độ Tô tự do
        freehandCanvas.clearCanvas();
        imgOutline.setImageResource(resId);

        setGameProgress(completedPictures, pictures.length);
    }

    private void playSoundTap() {
        if (soundManager != null) {
            soundManager.playSfx(R.raw.m2_sfx_tap);
        }
    }

    private void resetGame() {
        currentPictureIndex = 0;
        completedPictures = 0;
        loadPicture(currentPictureIndex);
    }
}
