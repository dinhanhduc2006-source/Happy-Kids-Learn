package com.example.happykidlearn.member2.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.happykidlearn.MainActivity;
import com.example.happykidlearn.R;
import com.example.happykidlearn.member2.common.Member2ContentCatalog;
import com.example.happykidlearn.models.GameModel;

public final class Member2GameRouter {

    private static final String CLASS_ALPHABET = "com.example.happykidlearn.member2.alphabet.AlphabetActivity";
    private static final String CLASS_SPELLING = "com.example.happykidlearn.member2.spelling.SpellingActivity";
    private static final String CLASS_STORYBOOK = "com.example.happykidlearn.member2.storybook.StorybookActivity";
    private static final String CLASS_ENGLISH = "com.example.happykidlearn.member2.english.EnglishActivity";

    private Member2GameRouter() {
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static Class<? extends Activity> resolveTargetActivity(@Nullable GameModel gameModel) {
        if (gameModel == null) {
            return null;
        }

        Class<? extends Activity> registeredClass = gameModel.getActivityClass();
        if (registeredClass != null && !MainActivity.class.equals(registeredClass)) {
            return registeredClass;
        }

        String id = gameModel.getId() != null ? gameModel.getId().toLowerCase() : "";
        String candidateClassName = null;
        switch (id) {
            case Member2ContentCatalog.GAME_ID_ALPHABET:
                candidateClassName = CLASS_ALPHABET;
                break;
            case Member2ContentCatalog.GAME_ID_SPELLING:
                candidateClassName = CLASS_SPELLING;
                break;
            case Member2ContentCatalog.GAME_ID_STORYBOOK:
                candidateClassName = CLASS_STORYBOOK;
                break;
            case Member2ContentCatalog.GAME_ID_ENGLISH:
                candidateClassName = CLASS_ENGLISH;
                break;
            default:
                break;
        }

        if (candidateClassName != null) {
            try {
                Class<?> clazz = Class.forName(candidateClassName);
                if (Activity.class.isAssignableFrom(clazz)) {
                    return (Class<? extends Activity>) clazz;
                }
            } catch (ClassNotFoundException ignored) {
            }
        }
        return null;
    }

    public static boolean isGameAvailable(@NonNull Context context, @Nullable GameModel gameModel) {
        Class<? extends Activity> targetClass = resolveTargetActivity(gameModel);
        if (targetClass == null || MainActivity.class.equals(targetClass)) {
            return false;
        }
        return isActivityDeclaredInManifest(context, targetClass) || Member2ContentCatalog.isMember2OwnedGame(
                gameModel != null ? gameModel.getId() : null
        );
    }

    public static boolean isActivityDeclaredInManifest(@NonNull Context context, @NonNull Class<? extends Activity> activityClass) {
        Intent intent = new Intent(context, activityClass);
        PackageManager pm = context.getPackageManager();
        ResolveInfo info = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return info != null;
    }

    public static void navigateToGame(@NonNull Context context, @Nullable GameModel gameModel) {
        if (gameModel == null) {
            return;
        }

        Class<? extends Activity> targetClass = resolveTargetActivity(gameModel);
        if (targetClass == null || MainActivity.class.equals(targetClass)) {
            Toast.makeText(
                    context,
                    context.getString(R.string.m2_toast_game_coming_soon, gameModel.getTitle()),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        Intent intent = new Intent(context, targetClass);
        intent.putExtra("EXTRA_GAME_ID", gameModel.getId());
        intent.putExtra("EXTRA_GAME_TITLE", gameModel.getTitle());

        if (isActivityDeclaredInManifest(context, targetClass)) {
            context.startActivity(intent);
        } else {
            Toast.makeText(
                    context,
                    "Cần đăng ký " + targetClass.getSimpleName() + " trong AndroidManifest.xml (xem M2_INTEGRATION_REQUEST.md)",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}

