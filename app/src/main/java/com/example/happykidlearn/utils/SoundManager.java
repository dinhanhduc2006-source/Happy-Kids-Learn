package com.example.happykidlearn.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    private static final String TAG = "SoundManager";
    private static volatile SoundManager instance;

    private final Context appContext;
    private SoundPool soundPool;
    private MediaPlayer bgmPlayer;

    private final Map<Integer, Integer> soundMap = new HashMap<>();
    private final Map<Integer, Boolean> loadedMap = new HashMap<>();

    private boolean isSfxMuted = false;
    private boolean isBgmMuted = false;
    private float bgmVolume = 0.5f;

    private SoundManager(Context context) {
        this.appContext = context.getApplicationContext();
        initSoundPool();
    }

    public static SoundManager getInstance(Context context) {
        if (instance == null) {
            synchronized (SoundManager.class) {
                if (instance == null) {
                    instance = new SoundManager(context);
                }
            }
        }
        return instance;
    }

    private void initSoundPool() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build();

        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            if (status == 0) {
                for (Map.Entry<Integer, Integer> entry : soundMap.entrySet()) {
                    if (entry.getValue() == sampleId) {
                        loadedMap.put(entry.getKey(), true);
                        break;
                    }
                }
            }
        });
    }


    public void preloadSound(int rawResId) {
        if (soundPool != null && !soundMap.containsKey(rawResId)) {
            try {
                int soundId = soundPool.load(appContext, rawResId, 1);
                soundMap.put(rawResId, soundId);
                loadedMap.put(rawResId, false);
            } catch (Exception e) {
                Log.e(TAG, "Lỗi nạp âm thanh res ID: " + rawResId, e);
            }
        }
    }


    public void playSfx(int rawResId) {
        if (isSfxMuted || soundPool == null) {
            return;
        }

        if (!soundMap.containsKey(rawResId)) {
            preloadSound(rawResId);
        }

        Integer soundId = soundMap.get(rawResId);
        Boolean isLoaded = loadedMap.get(rawResId);

        if (soundId != null && Boolean.TRUE.equals(isLoaded)) {
            soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
        } else if (soundId != null) {
            // Nếu chưa nạp xong, tự nạp và phát khi nạp xong
            try {
                MediaPlayer mp = MediaPlayer.create(appContext, rawResId);
                if (mp != null) {
                    mp.setOnCompletionListener(MediaPlayer::release);
                    mp.start();
                }
            } catch (Exception e) {
                Log.e(TAG, "Không thể phát âm thanh: " + rawResId, e);
            }
        }
    }

    public void playBgm(int rawResId) {
        if (isBgmMuted) {
            return;
        }

        try {
            stopBgm();
            bgmPlayer = MediaPlayer.create(appContext, rawResId);
            if (bgmPlayer != null) {
                bgmPlayer.setLooping(true);
                bgmPlayer.setVolume(bgmVolume, bgmVolume);
                bgmPlayer.start();
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi phát nhạc nền BGM", e);
        }
    }

    public void pauseBgm() {
        if (bgmPlayer != null && bgmPlayer.isPlaying()) {
            bgmPlayer.pause();
        }
    }

    public void resumeBgm() {
        if (bgmPlayer != null && !bgmPlayer.isPlaying() && !isBgmMuted) {
            bgmPlayer.start();
        }
    }


    public void stopBgm() {
        if (bgmPlayer != null) {
            if (bgmPlayer.isPlaying()) {
                bgmPlayer.stop();
            }
            bgmPlayer.release();
            bgmPlayer = null;
        }
    }

    public void setAllMuted(boolean isMuted) {
        setSfxMuted(isMuted);
        setBgmMuted(isMuted);
    }

    public void setSfxMuted(boolean isMuted) {
        this.isSfxMuted = isMuted;
    }

    public void setBgmMuted(boolean isMuted) {
        this.isBgmMuted = isMuted;
        if (isMuted) {
            pauseBgm();
        } else {
            resumeBgm();
        }
    }

    public boolean isSfxMuted() {
        return isSfxMuted;
    }

    public boolean isBgmMuted() {
        return isBgmMuted;
    }


    public void setBgmVolume(float volume) {
        this.bgmVolume = Math.max(0.0f, Math.min(1.0f, volume));
        if (bgmPlayer != null && bgmPlayer.isPlaying()) {
            bgmPlayer.setVolume(this.bgmVolume, this.bgmVolume);
        }
    }


    public void release() {
        stopBgm();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        soundMap.clear();
        loadedMap.clear();
        instance = null;
    }
}