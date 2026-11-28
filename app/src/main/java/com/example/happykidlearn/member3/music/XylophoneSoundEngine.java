package com.example.happykidlearn.member3.music;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class XylophoneSoundEngine {

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final byte[][] noteBuffers = new byte[7][];

    // Tần số 7 nốt C5->B5: Đô, Rê, Mi, Pha, Sol, La, Si
    private final double[] freqs = {523.25, 587.33, 659.25, 698.46, 783.99, 880.00, 987.77};
    private final int sampleRate = 44100;

    public XylophoneSoundEngine() {
        // Sinh buffer âm thanh PCM 16-bit tổng hợp hòa âm cho 7 nốt lúc khởi tạo
        for (int i = 0; i < 7; i++) {
            noteBuffers[i] = generateHarmonicTone(freqs[i], 1.2);
        }
    }

    private byte[] generateHarmonicTone(double freq, double durationSec) {
        int numSamples = (int) (durationSec * sampleRate);
        double[] sample = new double[numSamples];
        byte[] generatedSnd = new byte[2 * numSamples];

        for (int i = 0; i < numSamples; ++i) {
            double time = i / (double) sampleRate;

            // Pha trộn tần số cơ bản + hòa âm bậc 2 + hòa âm bậc 3 có độ tắt nhanh hơn
            double fundamental = 0.78 * Math.sin(2 * Math.PI * freq * time) * Math.exp(-5.0 * time);
            double harmonic2   = 0.16 * Math.sin(2 * Math.PI * (2 * freq) * time) * Math.exp(-9.0 * time);
            double harmonic3   = 0.06 * Math.sin(2 * Math.PI * (3 * freq) * time) * Math.exp(-14.0 * time);

            sample[i] = fundamental + harmonic2 + harmonic3;
        }

        int idx = 0;
        for (final double dVal : sample) {
            // Giới hạn chống vỡ tiếng (clipping) và chuyển sang PCM 16-bit
            double clamped = Math.max(-1.0, Math.min(1.0, dVal));
            final short val = (short) (clamped * 32767);
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }
        return generatedSnd;
    }

    public void playNote(int index) {
        if (index < 0 || index > 6) return;

        executor.execute(() -> {
            try {
                byte[] soundData = noteBuffers[index];
                AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC,
                        sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, soundData.length,
                        AudioTrack.MODE_STREAM);

                track.play();
                track.write(soundData, 0, soundData.length);
                track.stop();
                track.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void release() {
        executor.shutdownNow();
    }
}
