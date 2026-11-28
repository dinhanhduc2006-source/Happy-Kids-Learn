package com.example.happykidlearn.member3.music;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class XylophoneSoundEngine {

    private static final String TAG = "XylophoneSoundEngine";
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private SoundPool soundPool;
    private final int[] soundIds = new int[7];

    // Tần số 7 nốt C5->B5: Đô, Rê, Mi, Pha, Sol, La, Si
    private final double[] freqs = {523.25, 587.33, 659.25, 698.46, 783.99, 880.00, 987.77};
    private final int sampleRate = 44100;

    public XylophoneSoundEngine(Context context) {
        initSoundPool();
        Context appContext = context.getApplicationContext();

        executor.execute(() -> {
            Random random = new Random();
            for (int i = 0; i < 7; i++) {
                File wavFile = new File(appContext.getCacheDir(), "m3_xylo_" + i + ".wav");
                try {
                    // Sinh âm thanh thanh gõ vật lý và lưu thành WAV
                    generateAndSaveWav(wavFile, freqs[i], i, random);
                    final int index = i;
                    if (soundPool != null) {
                        int soundId = soundPool.load(wavFile.getAbsolutePath(), 1);
                        soundIds[index] = soundId;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Lỗi tạo file âm thanh nốt " + i, e);
                }
            }
        });
    }

    private void initSoundPool() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(audioAttributes)
                .build();
    }

    private void generateAndSaveWav(File wavFile, double freq, int noteIndex, Random random) throws IOException {
        // Thời gian ngân từ 1.15s (C5) giảm dần xuống 0.75s (B5)
        double durationSec = 1.15 - (noteIndex * (0.40 / 6.0));
        int numSamples = (int) (durationSec * sampleRate);
        double[] samples = new double[numSamples];

        double prevNoise = 0.0;
        double maxAbs = 0.0;

        for (int i = 0; i < numSamples; i++) {
            double t = i / (double) sampleRate;

            // 1. Tần số gốc f
            double fundamental = 0.72 * Math.sin(2 * Math.PI * freq * t) * Math.exp(-t / (durationSec / 4.5));

            // 2. Hòa âm thanh gõ đặc trưng 2.756 * f
            double harmonic2 = 0.20 * Math.sin(2 * Math.PI * (2.756 * freq) * t) * Math.exp(-t / 0.18);

            // 3. Hòa âm gõ cao 5.404 * f
            double harmonic3 = 0.08 * Math.sin(2 * Math.PI * (5.404 * freq) * t) * Math.exp(-t / 0.045);

            // 4. Quãng 8 ấm nhẹ 2.0 * f
            double octave = 0.04 * Math.sin(2 * Math.PI * (2.0 * freq) * t) * Math.exp(-t / (durationSec / 3.0));

            // 5. Tiếng dùi gõ (Mallet strike transient) ở 8ms đầu
            double mallet = 0.0;
            if (t < 0.008) {
                double rawNoise = random.nextDouble() * 2.0 - 1.0;
                double filteredNoise = (rawNoise + prevNoise) * 0.5; // Low-pass filter
                prevNoise = filteredNoise;
                mallet = 0.12 * filteredNoise * Math.exp(-600.0 * t);
            }

            double val = fundamental + harmonic2 + harmonic3 + octave + mallet;

            // Soft attack 2.5ms để triệt tiêu tiếng click đầu nốt
            if (t < 0.0025) {
                val *= (t / 0.0025);
            }

            // Fade-out 20ms ở đuôi mẫu để triệt tiêu tiếng bụp ở cuối
            double remainingTime = durationSec - t;
            if (remainingTime < 0.020) {
                val *= Math.max(0.0, remainingTime / 0.020);
            }

            samples[i] = val;
            if (Math.abs(val) > maxAbs) {
                maxAbs = Math.abs(val);
            }
        }

        // Chuẩn hóa biên độ (Peak Normalization về 0.85)
        double scale = maxAbs > 0 ? (0.85 / maxAbs) : 1.0;
        short[] pcmData = new short[numSamples];
        for (int i = 0; i < numSamples; i++) {
            pcmData[i] = (short) (samples[i] * scale * 32767);
        }

        // Ghi ra file WAV 44-byte header + PCM data
        try (FileOutputStream fos = new FileOutputStream(wavFile)) {
            int dataSize = numSamples * 2;
            writeWavHeader(fos, dataSize);
            for (short sample : pcmData) {
                fos.write(sample & 0xFF);
                fos.write((sample >> 8) & 0xFF);
            }
        }
    }

    private void writeWavHeader(OutputStream out, int dataSize) throws IOException {
        int totalSize = dataSize + 36;
        out.write(new byte[]{'R', 'I', 'F', 'F'});
        writeInt(out, totalSize);
        out.write(new byte[]{'W', 'A', 'V', 'E'});
        out.write(new byte[]{'f', 'm', 't', ' '});
        writeInt(out, 16); // Subchunk1Size (PCM)
        writeShort(out, (short) 1); // AudioFormat 1 = PCM
        writeShort(out, (short) 1); // NumChannels = 1
        writeInt(out, sampleRate);
        writeInt(out, sampleRate * 2); // ByteRate
        writeShort(out, (short) 2); // BlockAlign
        writeShort(out, (short) 16); // BitsPerSample
        out.write(new byte[]{'d', 'a', 't', 'a'});
        writeInt(out, dataSize);
    }

    private void writeInt(OutputStream out, int value) throws IOException {
        out.write(value & 0xFF);
        out.write((value >> 8) & 0xFF);
        out.write((value >> 16) & 0xFF);
        out.write((value >> 24) & 0xFF);
    }

    private void writeShort(OutputStream out, short value) throws IOException {
        out.write(value & 0xFF);
        out.write((value >> 8) & 0xFF);
    }

    public void playNote(int index) {
        if (index < 0 || index > 6) return;

        int soundId = soundIds[index];
        if (soundPool != null && soundId != 0) {
            soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    public void release() {
        executor.shutdownNow();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}
