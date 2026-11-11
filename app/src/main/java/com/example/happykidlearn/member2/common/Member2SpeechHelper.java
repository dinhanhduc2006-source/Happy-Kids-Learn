package com.example.happykidlearn.member2.common;

import android.content.Context;
import android.media.AudioAttributes;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.happykidlearn.utils.SoundManager;

import java.util.Locale;

/**
 * Trình điều khiển Text-to-Speech (TTS) cho phân hệ Thành viên 2.
 * Quản lý một thực thể TextToSpeech dùng chung ở cấp Application Context để:
 * - Không tạo nhiều TTS engine trùng lặp gây rò rỉ hoặc trễ 3.7 giây khi mở từng mini-game.
 * - Cho phép warm-up ngay từ Member2HomeActivity.
 * - Hỗ trợ hàng đợi phát âm (Pending Utterance Queue) nếu người dùng chạm trước khi TTS kịp sẵn sàng.
 * - Tự động ducking nhạc nền khi đang đọc và khôi phục khi đọc xong.
 */
public class Member2SpeechHelper {

    private static final String TAG = Member2SoundEffects.TAG;

    public interface SpeechListener {
        default void onSpeechStarted(String utteranceId) {
        }

        default void onSpeechRange(String utteranceId, int start, int end) {
        }

        default void onSpeechDone(String utteranceId) {
        }

        default void onSpeechUnavailable(String reason) {
        }
    }

    public static final Locale LOCALE_VIETNAMESE = Locale.forLanguageTag("vi-VN");
    public static final Locale LOCALE_VIETNAMESE_SHORT = Locale.forLanguageTag("vi");
    public static final Locale LOCALE_ENGLISH_US = Locale.US;

    private static final Object LOCK = new Object();
    @Nullable
    private static TextToSpeech sharedTts;
    private static volatile boolean sharedInitialized = false;
    private static volatile boolean sharedInitializing = false;
    @Nullable
    private static SpeechListener activeListener;
    @Nullable
    private static String pendingText;
    @Nullable
    private static Locale pendingLocale;
    @Nullable
    private static String pendingUtteranceId;

    private final Context appContext;
    private final SoundManager soundManager;
    private final Locale defaultLocale;
    @Nullable
    private SpeechListener listener;
    private boolean hasNotifiedMissingLocale = false;

    public Member2SpeechHelper(@NonNull Context context, @NonNull Locale defaultLocale) {
        this.appContext = context.getApplicationContext();
        this.soundManager = SoundManager.getInstance(this.appContext);
        this.defaultLocale = defaultLocale;
        ensureEngineInitialized(this.appContext, defaultLocale);
    }

    public static void warmUp(@NonNull Context context) {
        ensureEngineInitialized(context.getApplicationContext(), LOCALE_VIETNAMESE);
    }

    private static void ensureEngineInitialized(@NonNull Context appContext, @NonNull Locale initialLocale) {
        synchronized (LOCK) {
            if (sharedInitialized && sharedTts != null) {
                return;
            }
            if (sharedInitializing && sharedTts != null) {
                return;
            }
            sharedInitializing = true;
            Log.i(TAG, "Initializing shared TextToSpeech engine (initialLocale=" + initialLocale + ")...");
            try {
                sharedTts = new TextToSpeech(appContext, status -> {
                    synchronized (LOCK) {
                        sharedInitializing = false;
                        if (status == TextToSpeech.SUCCESS && sharedTts != null) {
                            configureAudioAttributes(sharedTts);
                            sharedTts.setSpeechRate(0.92f);
                            sharedTts.setPitch(1.04f);

                            int viStatus = safeCheckLanguage(sharedTts, LOCALE_VIETNAMESE);
                            int enStatus = safeCheckLanguage(sharedTts, LOCALE_ENGLISH_US);
                            Log.i(TAG, "Shared TTS initialized SUCCESS. engine=" + sharedTts.getDefaultEngine()
                                    + ", vi-VN availability=" + langStatusToString(viStatus)
                                    + ", en-US availability=" + langStatusToString(enStatus));

                            applyLocaleWithFallback(sharedTts, initialLocale);
                            attachSharedProgressListener(appContext, sharedTts);
                            sharedInitialized = true;

                            if (pendingText != null && pendingLocale != null && pendingUtteranceId != null) {
                                String textToSpeak = pendingText;
                                Locale localeToSpeak = pendingLocale;
                                String idToSpeak = pendingUtteranceId;
                                clearPendingUtterance();
                                Log.i(TAG, "Speaking queued pending utterance after shared TTS init: id=" + idToSpeak);
                                speakInternal(appContext, textToSpeak, localeToSpeak, idToSpeak);
                            }
                        } else {
                            sharedInitialized = false;
                            Log.e(TAG, "Shared TTS initialization FAILED with status=" + status);
                            SpeechListener current = activeListener;
                            if (current != null) {
                                current.onSpeechUnavailable("Thiết bị chưa sẵn sàng bộ đọc giọng nói (TTS status=" + status + ").");
                            }
                        }
                    }
                });
            } catch (Exception e) {
                sharedInitializing = false;
                sharedInitialized = false;
                Log.e(TAG, "Exception while creating shared TextToSpeech", e);
            }
        }
    }

    private static void configureAudioAttributes(@NonNull TextToSpeech tts) {
        try {
            AudioAttributes attrs = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();
            tts.setAudioAttributes(attrs);
        } catch (Exception e) {
            Log.w(TAG, "Unable to set TTS AudioAttributes: " + e.getMessage());
        }
    }

    private static void attachSharedProgressListener(@NonNull Context appContext, @NonNull TextToSpeech tts) {
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                Log.d(TAG, "TTS onStart: " + utteranceId);
                Member2SoundEffects.duckBgmForSpeech(appContext, true);
                SpeechListener current = activeListener;
                if (current != null) {
                    current.onSpeechStarted(utteranceId);
                }
            }

            @Override
            public void onRangeStart(String utteranceId, int start, int end, int frame) {
                SpeechListener current = activeListener;
                if (current != null) {
                    current.onSpeechRange(utteranceId, start, end);
                }
            }

            @Override
            public void onDone(String utteranceId) {
                Log.d(TAG, "TTS onDone: " + utteranceId);
                Member2SoundEffects.duckBgmForSpeech(appContext, false);
                SpeechListener current = activeListener;
                if (current != null) {
                    current.onSpeechDone(utteranceId);
                }
            }

            @Override
            public void onStop(String utteranceId, boolean interrupted) {
                // Utterance bị ngắt bởi stop()/QUEUE_FLUSH: khôi phục nhạc nền, không báo onDone.
                Log.d(TAG, "TTS onStop: " + utteranceId + ", interrupted=" + interrupted);
                Member2SoundEffects.duckBgmForSpeech(appContext, false);
            }

            @Override
            @SuppressWarnings("deprecation")
            public void onError(String utteranceId) {
                Log.e(TAG, "TTS onError (deprecated callback): " + utteranceId);
                Member2SoundEffects.duckBgmForSpeech(appContext, false);
                SpeechListener current = activeListener;
                if (current != null) {
                    current.onSpeechDone(utteranceId);
                }
            }

            @Override
            public void onError(String utteranceId, int errorCode) {
                Log.e(TAG, "TTS onError: " + utteranceId + ", errorCode=" + errorCode);
                Member2SoundEffects.duckBgmForSpeech(appContext, false);
                SpeechListener current = activeListener;
                if (current != null) {
                    current.onSpeechDone(utteranceId);
                }
            }
        });
    }

    public void setSpeechListener(@Nullable SpeechListener listener) {
        this.listener = listener;
        synchronized (LOCK) {
            activeListener = listener;
        }
    }

    public boolean speak(@NonNull String text, @NonNull Locale locale, @NonNull String utteranceId) {
        synchronized (LOCK) {
            activeListener = this.listener;
        }
        if (soundManager.isSfxMuted()) {
            Log.d(TAG, "TTS speak skipped because soundManager.isSfxMuted()=true");
            return false;
        }
        Member2SoundEffects.ensureAudibleSystemStream(appContext, soundManager);
        ensureEngineInitialized(appContext, locale);

        synchronized (LOCK) {
            if (!sharedInitialized || sharedTts == null) {
                Log.i(TAG, "TTS still initializing; queueing utteranceId=" + utteranceId + " text=\"" + text + "\"");
                pendingText = text;
                pendingLocale = locale;
                pendingUtteranceId = utteranceId;
                return true;
            }
            int langResult = applyLocaleWithFallback(sharedTts, locale);
            if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.w(TAG, "TTS locale " + locale + " missing/unsupported (code=" + langResult
                        + "). Using fallback TTS voice.");
                if (!hasNotifiedMissingLocale && listener != null) {
                    hasNotifiedMissingLocale = true;
                    listener.onSpeechUnavailable(
                            "Thiết bị thiếu gói giọng đọc " + locale.getDisplayLanguage()
                                    + " — đang dùng giọng mặc định."
                    );
                }
            }
            return speakInternal(appContext, text, locale, utteranceId);
        }
    }

    private static boolean speakInternal(
            @NonNull Context appContext,
            @NonNull String text,
            @NonNull Locale locale,
            @NonNull String utteranceId
    ) {
        if (sharedTts == null) {
            return false;
        }
        applyLocaleWithFallback(sharedTts, locale);
        Bundle params = new Bundle();
        params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f);
        int speakRes = sharedTts.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId);
        Log.i(TAG, "TTS speak invoked: id=" + utteranceId + ", locale=" + locale
                + ", text=\"" + text + "\", result=" + (speakRes == TextToSpeech.SUCCESS ? "SUCCESS" : "ERROR(" + speakRes + ")"));
        return speakRes == TextToSpeech.SUCCESS;
    }

    private static int applyLocaleWithFallback(@NonNull TextToSpeech tts, @NonNull Locale requestedLocale) {
        try {
            int res = tts.setLanguage(requestedLocale);
            if (res != TextToSpeech.LANG_MISSING_DATA && res != TextToSpeech.LANG_NOT_SUPPORTED) {
                return res;
            }
            if ("vi".equalsIgnoreCase(requestedLocale.getLanguage())) {
                int shortViRes = tts.setLanguage(LOCALE_VIETNAMESE_SHORT);
                if (shortViRes != TextToSpeech.LANG_MISSING_DATA && shortViRes != TextToSpeech.LANG_NOT_SUPPORTED) {
                    return shortViRes;
                }
            }
            Locale sysDefault = Locale.getDefault();
            int defRes = tts.setLanguage(sysDefault);
            if (defRes != TextToSpeech.LANG_MISSING_DATA && defRes != TextToSpeech.LANG_NOT_SUPPORTED) {
                return res;
            }
            tts.setLanguage(LOCALE_ENGLISH_US);
            return res;
        } catch (Exception e) {
            Log.w(TAG, "applyLocaleWithFallback exception: " + e.getMessage());
            return TextToSpeech.LANG_NOT_SUPPORTED;
        }
    }

    /**
     * Kiểm tra thiết bị có giọng đọc cho {@code locale} hay không.
     * Trả về true khi engine chưa khởi tạo xong (utterance sẽ được xếp hàng và kiểm tra lại khi phát).
     */
    public static boolean isLocaleSupported(@NonNull Locale locale) {
        synchronized (LOCK) {
            if (!sharedInitialized || sharedTts == null) {
                return true;
            }
            int res = safeCheckLanguage(sharedTts, locale);
            if ((res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED)
                    && "vi".equalsIgnoreCase(locale.getLanguage())) {
                res = safeCheckLanguage(sharedTts, LOCALE_VIETNAMESE_SHORT);
            }
            return res != TextToSpeech.LANG_MISSING_DATA && res != TextToSpeech.LANG_NOT_SUPPORTED;
        }
    }

    private static int safeCheckLanguage(@NonNull TextToSpeech tts, @NonNull Locale locale) {
        try {
            return tts.isLanguageAvailable(locale);
        } catch (Exception e) {
            return TextToSpeech.LANG_NOT_SUPPORTED;
        }
    }

    private static String langStatusToString(int code) {
        switch (code) {
            case TextToSpeech.LANG_AVAILABLE:
                return "LANG_AVAILABLE";
            case TextToSpeech.LANG_COUNTRY_AVAILABLE:
                return "LANG_COUNTRY_AVAILABLE";
            case TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE:
                return "LANG_COUNTRY_VAR_AVAILABLE";
            case TextToSpeech.LANG_MISSING_DATA:
                return "LANG_MISSING_DATA";
            case TextToSpeech.LANG_NOT_SUPPORTED:
                return "LANG_NOT_SUPPORTED";
            default:
                return "CODE(" + code + ")";
        }
    }

    private static void clearPendingUtterance() {
        pendingText = null;
        pendingLocale = null;
        pendingUtteranceId = null;
    }

    public void stop() {
        synchronized (LOCK) {
            clearPendingUtterance();
            Member2SoundEffects.duckBgmForSpeech(appContext, false);
            if (sharedTts != null && sharedInitialized) {
                try {
                    sharedTts.stop();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void shutdown() {
        stop();
        synchronized (LOCK) {
            if (activeListener == this.listener) {
                activeListener = null;
            }
            this.listener = null;
        }
    }
}
