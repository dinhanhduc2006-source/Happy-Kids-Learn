package com.example.happykidlearn.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.happykidlearn.R;
import com.example.happykidlearn.base.BaseGameActivity;
import com.example.happykidlearn.db.AppDatabase;
import com.example.happykidlearn.db.AppSettings;
import com.example.happykidlearn.db.GameProgress;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.List;
import java.util.concurrent.Executors;

public class ParentalSettingsActivity extends BaseGameActivity {

    private MaterialSwitch swSoundEffect;
    private MaterialSwitch swBgm;
    private EditText etTimeLimit;
    private TextView tvTotalCompleted;
    private TextView tvTotalStars;
    private Button btnSaveSettings;
    private Button btnResetProgress;

    private AppDatabase db;
    private AppSettings currentSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parental_settings);
        setupHeader("Cài Đặt Phụ Huynh");

        db = AppDatabase.getInstance(this);

        initViews();
        loadDataFromDatabase();
    }

    private void initViews() {
        swSoundEffect = findViewById(R.id.sw_sound_effect);
        swBgm = findViewById(R.id.sw_bgm);
        etTimeLimit = findViewById(R.id.et_time_limit);
        tvTotalCompleted = findViewById(R.id.tv_total_completed);
        tvTotalStars = findViewById(R.id.tv_total_stars);
        btnSaveSettings = findViewById(R.id.btn_save_settings);
        btnResetProgress = findViewById(R.id.btn_reset_progress);

        btnSaveSettings.setOnClickListener(v -> saveSettings());
        btnResetProgress.setOnClickListener(v -> resetProgress());
    }

    private void loadDataFromDatabase() {
        Executors.newSingleThreadExecutor().execute(() -> {
            AppSettings settings = db.appSettingsDao().getSettings();
            if (settings == null) {
                settings = new AppSettings();
            }
            currentSettings = settings;

            List<GameProgress> progressList = db.gameProgressDao().getAllProgress();

            int completedCount = 0;
            int totalStars = 0;
            if (progressList != null) {
                for (GameProgress p : progressList) {
                    completedCount += p.getCompletedLevels();
                    totalStars += p.getStarsEarned();
                }
            }

            final int finalCompleted = completedCount;
            final int finalStars = totalStars;

            runOnUiThread(() -> {
                swSoundEffect.setChecked(currentSettings.isSoundEnabled());
                swBgm.setChecked(currentSettings.isBgmEnabled());
                etTimeLimit.setText(String.valueOf(currentSettings.getScreenTimeLimitMinutes()));

                tvTotalCompleted.setText("Bài học đã hoàn thành: " + finalCompleted + " bài");
                tvTotalStars.setText("Tổng số sao đạt được: " + finalStars + " ⭐");
            });
        });
    }

    private void saveSettings() {
        String inputTime = etTimeLimit.getText().toString().trim();
        if (inputTime.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập thời gian giới hạn!", Toast.LENGTH_SHORT).show();
            return;
        }

        int minutes = Integer.parseInt(inputTime);
        if (currentSettings == null) {
            currentSettings = new AppSettings();
        }

        currentSettings.setSoundEnabled(swSoundEffect.isChecked());
        currentSettings.setBgmEnabled(swBgm.isChecked());
        currentSettings.setScreenTimeLimitMinutes(minutes);

        soundManager.setSfxMuted(!swSoundEffect.isChecked());
        soundManager.setBgmMuted(!swBgm.isChecked());

        Executors.newSingleThreadExecutor().execute(() -> {
            db.appSettingsDao().saveSettings(currentSettings);
            runOnUiThread(() -> Toast.makeText(ParentalSettingsActivity.this, "Đã lưu cài đặt!", Toast.LENGTH_SHORT).show());
        });
    }

    private void resetProgress() {
        Executors.newSingleThreadExecutor().execute(() -> {
            db.gameProgressDao().deleteAll();
            runOnUiThread(() -> {
                Toast.makeText(ParentalSettingsActivity.this, "Đã đặt lại tiến độ học!", Toast.LENGTH_SHORT).show();
                loadDataFromDatabase();
            });
        });
    }
}