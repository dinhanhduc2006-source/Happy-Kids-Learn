package com.example.happykidlearn.member2.common;

import android.animation.ValueAnimator;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;

import com.example.happykidlearn.R;
import com.example.happykidlearn.utils.SoundManager;

/**
 * Điều phối toàn bộ Sound Effects (SFX) và Background Music (BGM) cho phân hệ Thành viên 2.
 *
 * Bản nhạc nền phong cách "Magical Storybook — Gentle Music Box & Soft Piano":
 * - Nhẹ nhàng, trong trẻo, êm ái cho trẻ 4–8 tuổi (72 BPM, không trống mạnh, không chói tai).
 * - Âm lượng nhạc nền giữ ở mức 10–20% so với giọng đọc (Home: 18%, Mini-game: 10%).
 * - Tự động giảm xuống mức thì thầm (2.5%) khi có giọng phát âm chữ cái, từ vựng hoặc kể chuyện.
 * - Sau khi đọc xong, hồi phục từ từ qua hiệu ứng Fade In nhẹ nhàng (500ms) có debounce chống giật.
 * - Chuyển màn hình mượt mà qua Fade In / Fade Out, không restart hoặc phát chồng 2 bài.
 * - Dừng ngay lập tức khi tắt tiếng (Mute) hoặc khi thoát ứng dụng.
 */
public final class Member2SoundEffects {

    public static final String TAG = "M2Audio";
    private static final long MIN_SFX_INTERVAL_MS = 70L;

    // Tỷ lệ âm lượng nhạc nền: 10–20% so với giọng đọc (TTS đọc ở mức 100%)
    public static final float HOME_BGM_VOLUME = 0.18f;
    public static final float MINIGAME_BGM_VOLUME = 0.10f;
    public static final float SPEECH_DUCKED_BGM_VOLUME = 0.025f;

    private static final long FADE_DUCK_DOWN_MS = 120L;
    private static final long FADE_RESTORE_UP_MS = 500L;
    private static final long FADE_RESTORE_DEBOUNCE_MS = 120L;
    private static final long FADE_IN_START_MS = 500L;
    private static final long FADE_CROSS_SCENE_MS = 400L;

    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());
    private static ValueAnimator bgmFader;
    private static float currentBgmVolume = HOME_BGM_VOLUME;
    private static float activeBaseBgmVolume = HOME_BGM_VOLUME;
    private static boolean isBgmStarted = false;
    private static boolean isPreloaded = false;
    private static long lastSfxTimestampMs = 0L;
    private static Context cachedAppContext;

    private static final Runnable restoreDuckingRunnable = () -> {
        if (cachedAppContext != null) {
            Log.d(TAG, "Gradual BGM restore starting -> fading up to " + activeBaseBgmVolume + " over " + FADE_RESTORE_UP_MS + "ms");
            fadeBgmVolumeInternal(cachedAppContext, activeBaseBgmVolume, FADE_RESTORE_UP_MS);
        }
    };

    private Member2SoundEffects() {
    }

    public static synchronized void preloadAll(@NonNull Context context) {
        cachedAppContext = context.getApplicationContext();
        SoundManager sm = SoundManager.getInstance(cachedAppContext);
        if (!isPreloaded) {
            Log.i(TAG, "Preloading Member2 SFX assets into SoundManager...");
            sm.preloadSound(R.raw.m2_sfx_tap);
            sm.preloadSound(R.raw.m2_sfx_open_book);
            sm.preloadSound(R.raw.m2_sfx_correct);
            sm.preloadSound(R.raw.m2_sfx_gentle_try);
            sm.preloadSound(R.raw.m2_sfx_celebrate);
            sm.setBgmVolume(HOME_BGM_VOLUME);
            currentBgmVolume = HOME_BGM_VOLUME;
            isPreloaded = true;
        }
        ensureAudibleSystemStream(cachedAppContext, sm);
    }

    public static void ensureAudibleSystemStream(@NonNull Context context, @NonNull SoundManager sm) {
        try {
            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (am == null) {
                return;
            }
            int currentVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            int maxVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int minComfortableVol = Math.max(1, (int) Math.round(maxVol * 0.60));
            Log.d(TAG, "System STREAM_MUSIC volume=" + currentVol + "/" + maxVol
                    + ", sfxMuted=" + sm.isSfxMuted() + ", bgmMuted=" + sm.isBgmMuted());

            if (!sm.isSfxMuted() && currentVol < minComfortableVol && maxVol > 0) {
                int targetVol = Math.max(1, (int) Math.round(maxVol * 0.80));
                am.setStreamVolume(AudioManager.STREAM_MUSIC, targetVol, 0);
                Log.w(TAG, "STREAM_MUSIC was low (" + currentVol + "/" + maxVol
                        + ") while app sound enabled; raised to " + targetVol + "/" + maxVol);
            }
        } catch (Exception e) {
            Log.w(TAG, "Unable to inspect/adjust AudioManager STREAM_MUSIC: " + e.getMessage());
        }
    }

    /**
     * Khởi động hoặc nối tiếp phát nhạc nền êm dịu trên Home Dashboard.
     * Sử dụng hiệu ứng Fade In nhẹ nhàng và không khởi động lại nếu đang phát.
     */
    public static void ensureGentleBgmPlaying(@NonNull Context context) {
        cachedAppContext = context.getApplicationContext();
        SoundManager sm = SoundManager.getInstance(cachedAppContext);
        preloadAll(cachedAppContext);
        activeBaseBgmVolume = HOME_BGM_VOLUME;

        if (sm.isBgmMuted()) {
            Log.d(TAG, "ensureGentleBgmPlaying skipped because isBgmMuted=true");
            return;
        }

        MAIN_HANDLER.post(() -> {
            if (!isBgmStarted) {
                sm.setBgmVolume(0.0f);
                currentBgmVolume = 0.0f;
                sm.playBgm(R.raw.m2_bgm_magic_book);
                isBgmStarted = true;
                fadeBgmVolumeInternal(cachedAppContext, HOME_BGM_VOLUME, FADE_IN_START_MS);
                Log.i(TAG, "Started Magical Storybook BGM with gentle fade-in to " + HOME_BGM_VOLUME);
            } else {
                sm.resumeBgm();
                fadeBgmVolumeInternal(cachedAppContext, HOME_BGM_VOLUME, FADE_CROSS_SCENE_MS);
                Log.i(TAG, "Resumed Magical Storybook BGM with gentle cross-scene fade to " + HOME_BGM_VOLUME);
            }
        });
    }

    /**
     * Đặt âm lượng nhạc nền nhẹ nhàng cho mini-game (10% so với giọng đọc).
     * Chuyển mượt mà qua Fade In / Fade Out, không cắt ngang hay giật âm lượng.
     */
    public static void applyMiniGameBgmLevel(@NonNull Context context) {
        cachedAppContext = context.getApplicationContext();
        SoundManager sm = SoundManager.getInstance(cachedAppContext);
        preloadAll(cachedAppContext);
        activeBaseBgmVolume = MINIGAME_BGM_VOLUME;

        if (sm.isBgmMuted()) {
            return;
        }

        MAIN_HANDLER.post(() -> {
            if (!isBgmStarted) {
                sm.setBgmVolume(0.0f);
                currentBgmVolume = 0.0f;
                sm.playBgm(R.raw.m2_bgm_magic_book);
                isBgmStarted = true;
                fadeBgmVolumeInternal(cachedAppContext, MINIGAME_BGM_VOLUME, FADE_IN_START_MS);
            } else {
                sm.resumeBgm();
                fadeBgmVolumeInternal(cachedAppContext, MINIGAME_BGM_VOLUME, FADE_CROSS_SCENE_MS);
            }
            Log.d(TAG, "Applied mini-game BGM level with gentle fade to " + MINIGAME_BGM_VOLUME);
        });
    }

    /**
     * Tự động giảm nhạc nền xuống mức rất nhỏ khi phát âm và tăng trở lại từ từ khi kết thúc.
     * Có cơ chế debounce 120ms để xử lý mượt mà các chuỗi đọc liên tiếp (ví dụ: tiếng Anh xong khen tiếng Việt).
     */
    public static void duckBgmForSpeech(@NonNull Context context, boolean duck) {
        cachedAppContext = context.getApplicationContext();
        MAIN_HANDLER.post(() -> {
            MAIN_HANDLER.removeCallbacks(restoreDuckingRunnable);
            SoundManager sm = SoundManager.getInstance(cachedAppContext);
            if (sm.isBgmMuted()) {
                return;
            }
            if (duck) {
                // Giảm nhanh và êm xuống mức thì thầm (2.5%)
                fadeBgmVolumeInternal(cachedAppContext, SPEECH_DUCKED_BGM_VOLUME, FADE_DUCK_DOWN_MS);
                Log.d(TAG, "BGM ducking started -> fading to whisper level " + SPEECH_DUCKED_BGM_VOLUME);
            } else {
                // Đợi 120ms debounce chống giật âm lượng nếu có câu đọc nối tiếp, sau đó tăng dần qua 500ms
                MAIN_HANDLER.postDelayed(restoreDuckingRunnable, FADE_RESTORE_DEBOUNCE_MS);
            }
        });
    }

    /**
     * Dừng hoặc tắt tiếng nhạc nền ngay lập tức khi người dùng bấm Mute.
     */
    public static void stopOrMuteBgm(@NonNull Context context) {
        cachedAppContext = context.getApplicationContext();
        MAIN_HANDLER.post(() -> {
            MAIN_HANDLER.removeCallbacks(restoreDuckingRunnable);
            if (bgmFader != null && bgmFader.isRunning()) {
                bgmFader.cancel();
            }
            currentBgmVolume = 0.0f;
            SoundManager sm = SoundManager.getInstance(cachedAppContext);
            sm.setBgmVolume(0.0f);
            sm.pauseBgm();
            Log.d(TAG, "BGM stopped/muted immediately");
        });
    }

    /**
     * Điều chỉnh âm lượng nhạc nền qua hiệu ứng Fade In / Fade Out mượt mà.
     */
    public static void fadeBgmVolume(@NonNull Context context, float targetVolume, long durationMs) {
        cachedAppContext = context.getApplicationContext();
        MAIN_HANDLER.post(() -> fadeBgmVolumeInternal(cachedAppContext, targetVolume, durationMs));
    }

    private static void fadeBgmVolumeInternal(@NonNull Context context, float targetVolume, long durationMs) {
        SoundManager sm = SoundManager.getInstance(context);
        if (sm.isBgmMuted()) {
            if (bgmFader != null && bgmFader.isRunning()) {
                bgmFader.cancel();
            }
            currentBgmVolume = 0.0f;
            sm.setBgmVolume(0.0f);
            return;
        }

        if (bgmFader != null && bgmFader.isRunning()) {
            bgmFader.cancel();
        }

        if (durationMs <= 0) {
            currentBgmVolume = targetVolume;
            sm.setBgmVolume(targetVolume);
            return;
        }

        float startVol = currentBgmVolume;
        bgmFader = ValueAnimator.ofFloat(startVol, targetVolume);
        bgmFader.setDuration(durationMs);
        bgmFader.setInterpolator(new AccelerateDecelerateInterpolator());
        bgmFader.addUpdateListener(anim -> {
            float val = (Float) anim.getAnimatedValue();
            currentBgmVolume = val;
            sm.setBgmVolume(val);
        });
        bgmFader.start();
    }

    public static void playTap(@NonNull Context context) {
        playWithCooldown(context, R.raw.m2_sfx_tap, "m2_sfx_tap");
    }

    public static void playOpenBook(@NonNull Context context) {
        playWithCooldown(context, R.raw.m2_sfx_open_book, "m2_sfx_open_book");
    }

    public static void playCorrect(@NonNull Context context) {
        playWithCooldown(context, R.raw.m2_sfx_correct, "m2_sfx_correct");
    }

    public static void playGentleTry(@NonNull Context context) {
        playWithCooldown(context, R.raw.m2_sfx_gentle_try, "m2_sfx_gentle_try");
    }

    public static void playCelebrate(@NonNull Context context) {
        playWithCooldown(context, R.raw.m2_sfx_celebrate, "m2_sfx_celebrate");
    }

    private static void playWithCooldown(@NonNull Context context, int rawResId, @NonNull String name) {
        long now = SystemClock.elapsedRealtime();
        if (now - lastSfxTimestampMs < MIN_SFX_INTERVAL_MS) {
            return;
        }
        lastSfxTimestampMs = now;
        Context appContext = context.getApplicationContext();
        SoundManager sm = SoundManager.getInstance(appContext);
        preloadAll(appContext);
        if (sm.isSfxMuted()) {
            Log.d(TAG, "SFX " + name + " skipped because sfxMuted=true");
            return;
        }
        Log.d(TAG, "Playing SFX: " + name + " (resId=" + rawResId + ")");
        sm.playSfx(rawResId);
    }
}
