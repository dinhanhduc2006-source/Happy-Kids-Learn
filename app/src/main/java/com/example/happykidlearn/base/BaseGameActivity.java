package com.example.happykidlearn.base;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.happykidlearn.R;


public abstract class BaseGameActivity extends AppCompatActivity {

    protected ImageButton btnHeaderBack;
    protected ImageButton btnHeaderSound;
    protected TextView tvHeaderTitle;
    protected ProgressBar pbHeaderProgress;
    protected TextView tvHeaderProgressText;

    protected boolean isSoundMuted = false;
    protected Dialog victoryDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    protected void setupHeader(String gameTitle) {
        btnHeaderBack = findViewById(R.id.btn_header_back);
        btnHeaderSound = findViewById(R.id.btn_header_sound);
        tvHeaderTitle = findViewById(R.id.tv_header_title);
        pbHeaderProgress = findViewById(R.id.pb_header_progress);
        tvHeaderProgressText = findViewById(R.id.tv_header_progress_text);

        if (tvHeaderTitle != null && gameTitle != null) {
            tvHeaderTitle.setText(gameTitle);
        }

        if (btnHeaderBack != null) {
            btnHeaderBack.setOnClickListener(v -> finish());
        }

        if (btnHeaderSound != null) {
            btnHeaderSound.setOnClickListener(v -> toggleSound());
        }
    }

    protected void setGameProgress(int current, int total) {
        if (pbHeaderProgress != null && total > 0) {
            int progressPercent = (int) (((float) current / total) * 100);
            pbHeaderProgress.setProgress(progressPercent);
        }
        if (tvHeaderProgressText != null) {
            tvHeaderProgressText.setText(current + "/" + total);
        }
    }


    protected void setGameProgressPercent(int percent) {
        if (pbHeaderProgress != null) {
            pbHeaderProgress.setProgress(percent);
        }
        if (tvHeaderProgressText != null) {
            tvHeaderProgressText.setText(percent + "%");
        }
    }


    protected void toggleSound() {
        isSoundMuted = !isSoundMuted;
        if (btnHeaderSound != null) {
            btnHeaderSound.setImageResource(isSoundMuted ? R.drawable.ic_sound_off : R.drawable.ic_sound_on);
        }
        onSoundToggled(isSoundMuted);
    }


    protected void onSoundToggled(boolean isMuted) {
        // Mặc định trống, các Activity con hoặc SoundManager sẽ lắng nghe
    }

    /**
     * Hiển thị Popup/Dialog Chúc mừng chiến thắng.
     *
     * @param stars     Số sao đạt được (từ 1 đến 3 sao).
     * @param onNext    Sự kiện khi bấm nút "Tiếp tục".
     * @param onReplay  Sự kiện khi bấm nút "Chơi lại".
     */
    protected void showVictoryDialog(int stars, @Nullable Runnable onNext, @Nullable Runnable onReplay) {
        dismissVictoryDialog();

        victoryDialog = new Dialog(this);
        victoryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        victoryDialog.setContentView(R.layout.dialog_victory);
        victoryDialog.setCancelable(false);

        if (victoryDialog.getWindow() != null) {
            victoryDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ImageView imgStar1 = victoryDialog.findViewById(R.id.img_star1);
        ImageView imgStar2 = victoryDialog.findViewById(R.id.img_star2);
        ImageView imgStar3 = victoryDialog.findViewById(R.id.img_star3);

        int filledStar = R.drawable.ic_star_filled;
        int emptyStar = R.drawable.ic_star_empty;

        if (imgStar1 != null && imgStar2 != null && imgStar3 != null) {
            imgStar1.setImageResource(stars >= 1 ? filledStar : emptyStar);
            imgStar2.setImageResource(stars >= 2 ? filledStar : emptyStar);
            imgStar3.setImageResource(stars >= 3 ? filledStar : emptyStar);
        }

        Button btnReplay = victoryDialog.findViewById(R.id.btn_dialog_replay);
        Button btnNext = victoryDialog.findViewById(R.id.btn_dialog_next);

        if (btnReplay != null) {
            btnReplay.setOnClickListener(v -> {
                victoryDialog.dismiss();
                if (onReplay != null) {
                    onReplay.run();
                }
            });
        }

        if (btnNext != null) {
            btnNext.setOnClickListener(v -> {
                victoryDialog.dismiss();
                if (onNext != null) {
                    onNext.run();
                }
            });
        }

        victoryDialog.show();
    }

    protected void dismissVictoryDialog() {
        if (victoryDialog != null && victoryDialog.isShowing()) {
            victoryDialog.dismiss();
            victoryDialog = null;
        }
    }

    @Override
    protected void onDestroy() {
        dismissVictoryDialog();
        super.onDestroy();
    }
}