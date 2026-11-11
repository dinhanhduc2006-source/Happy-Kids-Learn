package com.example.happykidlearn.member2;

import static org.junit.Assert.assertTrue;

import com.example.happykidlearn.member2.common.Member2SoundEffects;

import org.junit.Test;

public class Member2SoundEffectsTest {

    @Test
    public void bgmVolumes_satisfySecondaryBackgroundRatio() {
        // Yêu cầu: "Âm lượng nhạc nền khoảng 10–20% so với giọng đọc (TTS 1.0f)"
        assertTrue("Âm lượng Home BGM phải nằm trong khoảng 10–20% (0.10f - 0.20f)",
                Member2SoundEffects.HOME_BGM_VOLUME >= 0.10f && Member2SoundEffects.HOME_BGM_VOLUME <= 0.20f);

        assertTrue("Âm lượng Mini-game BGM phải nằm trong khoảng 0.08f - 0.15f",
                Member2SoundEffects.MINIGAME_BGM_VOLUME >= 0.08f && Member2SoundEffects.MINIGAME_BGM_VOLUME <= 0.15f);

        assertTrue("Âm lượng khi đọc TTS (ducking) phải cực nhỏ dưới 0.05f",
                Member2SoundEffects.SPEECH_DUCKED_BGM_VOLUME < 0.05f && Member2SoundEffects.SPEECH_DUCKED_BGM_VOLUME > 0.0f);

        // Thứ tự ưu tiên âm lượng
        assertTrue("Âm lượng ducking < Mini-game BGM < Home BGM",
                Member2SoundEffects.SPEECH_DUCKED_BGM_VOLUME < Member2SoundEffects.MINIGAME_BGM_VOLUME
                        && Member2SoundEffects.MINIGAME_BGM_VOLUME < Member2SoundEffects.HOME_BGM_VOLUME);
    }
}

