package com.example.happykidlearn.member3.music;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class XylophoneSoundEngine {

    // Thread pool tự động mở rộng để hỗ trợ gõ nhiều nốt nhạc liên tục không bị kẹt
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final byte[][] noteBuffers = new byte[7][];

    // Tần số 7 nốt: Đô, Rê, Mi, Pha, Sol, La, Si
    private final double[] freqs = {523.25, 587.33, 659.25, 698.46, 783.99, 880.00, 987.77};
    private final int sampleRate = 44100;

    public XylophoneSoundEngine() {
        // Sinh buffer âm thanh PCM 16-bit cho 7 nốt lúc khởi tạo
        for (int i = 0; i < 7; i++) {
            noteBuffers[i] = generateTone(freqs[i], 1.0); // Ngân vang 1 giây
        }
    }

    private byte[] generateTone(double freq, double durationSec) {
        int numSamples = (int) (durationSec * sampleRate);
        double[] sample = new double[numSamples];
        byte[] generatedSnd = new byte[2 * numSamples];

        for (int i = 0; i < numSamples; ++i) {
            double time = i / (double) sampleRate;
            double sine = Math.sin(2 * Math.PI * freq * time);
            // Exponential decay: e^(-kt) tạo hiệu ứng gõ vang rồi tắt dần mượt mà
            double envelope = Math.exp(-5.0 * time);
            sample[i] = sine * envelope;
        }

        int idx = 0;
        for (final double dVal : sample) {
            // Chuẩn hóa PCM 16-bit (Little Endian)
            final short val = (short) ((dVal * 32767));
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
                // write sẽ block luồng nền này cho đến khi ghi xong dữ liệu (tương đương phát xong)
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
