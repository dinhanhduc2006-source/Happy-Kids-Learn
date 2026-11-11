package com.example.happykidlearn.member2.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.happykidlearn.db.AppDatabase;
import com.example.happykidlearn.db.AppSettings;
import com.example.happykidlearn.db.GameProgress;
import com.example.happykidlearn.utils.SoundManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Member2ProgressRepository {

    private static final String PREF_NAME = "m2_member2_item_progress";
    private static final String KEY_PREFIX_ITEMS = "completed_items_";

    public interface Callback<T> {
        void onResult(T result);
    }

    public static class DashboardSnapshot {
        private final Map<String, GameProgress> progressByGameId;
        private final AppSettings appSettings;
        private final int totalCompletedLevels;
        private final int totalStarsEarned;

        public DashboardSnapshot(
                Map<String, GameProgress> progressByGameId,
                AppSettings appSettings,
                int totalCompletedLevels,
                int totalStarsEarned
        ) {
            this.progressByGameId = progressByGameId;
            this.appSettings = appSettings;
            this.totalCompletedLevels = totalCompletedLevels;
            this.totalStarsEarned = totalStarsEarned;
        }

        public Map<String, GameProgress> getProgressByGameId() {
            return progressByGameId;
        }

        public AppSettings getAppSettings() {
            return appSettings;
        }

        public int getTotalCompletedLevels() {
            return totalCompletedLevels;
        }

        public int getTotalStarsEarned() {
            return totalStarsEarned;
        }
    }

    private final Context appContext;
    private final AppDatabase database;
    private final SharedPreferences prefs;
    private final ExecutorService executor;
    private final Handler mainHandler;

    public Member2ProgressRepository(@NonNull Context context) {
        this.appContext = context.getApplicationContext();
        this.database = AppDatabase.getInstance(this.appContext);
        this.prefs = this.appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void loadDashboardSnapshot(@NonNull Callback<DashboardSnapshot> callback) {
        executor.execute(() -> {
            AppSettings settings = database.appSettingsDao().getSettings();
            if (settings == null) {
                settings = new AppSettings();
            }

            List<GameProgress> allProgress = database.gameProgressDao().getAllProgress();
            Map<String, GameProgress> progressMap = new HashMap<>();
            int totalCompleted = 0;
            int totalStars = 0;

            if (allProgress != null) {
                for (GameProgress item : allProgress) {
                    if (item != null && item.getGameId() != null) {
                        progressMap.put(item.getGameId(), item);
                        totalCompleted += Math.max(0, item.getCompletedLevels());
                        totalStars += Math.max(0, item.getStarsEarned());
                    }
                }
            }

            syncLocalPrefsWithRoomReset(progressMap);

            SoundManager soundManager = SoundManager.getInstance(appContext);
            soundManager.setSfxMuted(!settings.isSoundEnabled());
            soundManager.setBgmMuted(!settings.isBgmEnabled());

            final DashboardSnapshot snapshot = new DashboardSnapshot(
                    Collections.unmodifiableMap(progressMap),
                    settings,
                    totalCompleted,
                    totalStars
            );

            mainHandler.post(() -> callback.onResult(snapshot));
        });
    }

    public void toggleSoundAndPersist(boolean enableAllSound, @Nullable Callback<AppSettings> callback) {
        SoundManager soundManager = SoundManager.getInstance(appContext);
        soundManager.setSfxMuted(!enableAllSound);
        soundManager.setBgmMuted(!enableAllSound);

        executor.execute(() -> {
            AppSettings settings = database.appSettingsDao().getSettings();
            if (settings == null) {
                settings = new AppSettings();
            }
            settings.setSoundEnabled(enableAllSound);
            settings.setBgmEnabled(enableAllSound);
            database.appSettingsDao().saveSettings(settings);

            final AppSettings saved = settings;
            if (callback != null) {
                mainHandler.post(() -> callback.onResult(saved));
            }
        });
    }

    public void markItemCompleted(
            @NonNull String gameId,
            @NonNull String itemKey,
            int totalLevels,
            int starsForNewCompletion,
            @Nullable Callback<GameProgress> callback
    ) {
        executor.execute(() -> {
            GameProgress current = database.gameProgressDao().getProgressByGameId(gameId);
            String prefKey = KEY_PREFIX_ITEMS + gameId;
            Set<String> storedItems = new HashSet<>(prefs.getStringSet(prefKey, Collections.emptySet()));

            if (current == null || current.getCompletedLevels() == 0) {
                storedItems.clear();
            }

            boolean isNewlyCompleted = storedItems.add(itemKey);
            prefs.edit().putStringSet(prefKey, storedItems).apply();

            int resolvedTotal = Math.max(totalLevels, Member2ContentCatalog.getDefaultTotalLevels(gameId));
            int completedCount = Math.min(resolvedTotal > 0 ? resolvedTotal : storedItems.size(), storedItems.size());
            int existingStars = current != null ? Math.max(0, current.getStarsEarned()) : 0;
            int updatedStars = isNewlyCompleted
                    ? existingStars + Math.max(0, starsForNewCompletion)
                    : existingStars;

            GameProgress updated = new GameProgress(
                    gameId,
                    completedCount,
                    resolvedTotal,
                    updatedStars,
                    true
            );
            database.gameProgressDao().insertOrUpdate(updated);

            if (callback != null) {
                mainHandler.post(() -> callback.onResult(updated));
            }
        });
    }

    public void loadCompletedItemsForGame(@NonNull String gameId, @NonNull Callback<Set<String>> callback) {
        executor.execute(() -> {
            GameProgress current = database.gameProgressDao().getProgressByGameId(gameId);
            String prefKey = KEY_PREFIX_ITEMS + gameId;
            if (current == null || current.getCompletedLevels() == 0) {
                prefs.edit().remove(prefKey).apply();
                mainHandler.post(() -> callback.onResult(new HashSet<>()));
                return;
            }
            Set<String> items = new HashSet<>(prefs.getStringSet(prefKey, Collections.emptySet()));
            mainHandler.post(() -> callback.onResult(items));
        });
    }

    private void syncLocalPrefsWithRoomReset(Map<String, GameProgress> roomProgressMap) {
        String[] member2Ids = {
                Member2ContentCatalog.GAME_ID_ALPHABET,
                Member2ContentCatalog.GAME_ID_SPELLING,
                Member2ContentCatalog.GAME_ID_STORYBOOK,
                Member2ContentCatalog.GAME_ID_ENGLISH
        };
        SharedPreferences.Editor editor = prefs.edit();
        boolean changed = false;
        for (String id : member2Ids) {
            GameProgress gp = roomProgressMap.get(id);
            if (gp == null || gp.getCompletedLevels() == 0) {
                String key = KEY_PREFIX_ITEMS + id;
                if (prefs.contains(key)) {
                    editor.remove(key);
                    changed = true;
                }
            }
        }
        if (changed) {
            editor.apply();
        }
    }
}

