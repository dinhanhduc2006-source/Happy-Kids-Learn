package com.example.happykidlearn.member2.dashboard;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.example.happykidlearn.models.GameModel;

public class HomeGameUiModel {

    private final GameModel gameModel;
    @DrawableRes
    private final int resolvedIconResId;
    @ColorRes
    private final int accentColorResId;
    @ColorRes
    private final int softBgColorResId;
    private final int completedLevels;
    private final int totalLevels;
    private final int starsEarned;
    private final boolean hasKnownTotal;
    private final boolean isReadyToPlay;

    public HomeGameUiModel(
            @NonNull GameModel gameModel,
            @DrawableRes int resolvedIconResId,
            @ColorRes int accentColorResId,
            @ColorRes int softBgColorResId,
            int completedLevels,
            int totalLevels,
            int starsEarned,
            boolean hasKnownTotal,
            boolean isReadyToPlay
    ) {
        this.gameModel = gameModel;
        this.resolvedIconResId = resolvedIconResId;
        this.accentColorResId = accentColorResId;
        this.softBgColorResId = softBgColorResId;
        this.completedLevels = Math.max(0, completedLevels);
        this.totalLevels = Math.max(0, totalLevels);
        this.starsEarned = Math.max(0, starsEarned);
        this.hasKnownTotal = hasKnownTotal && this.totalLevels > 0;
        this.isReadyToPlay = isReadyToPlay;
    }

    @NonNull
    public GameModel getGameModel() {
        return gameModel;
    }

    @DrawableRes
    public int getResolvedIconResId() {
        return resolvedIconResId;
    }

    @ColorRes
    public int getAccentColorResId() {
        return accentColorResId;
    }

    @ColorRes
    public int getSoftBgColorResId() {
        return softBgColorResId;
    }

    public int getCompletedLevels() {
        return completedLevels;
    }

    public int getTotalLevels() {
        return totalLevels;
    }

    public int getStarsEarned() {
        return starsEarned;
    }

    public boolean hasKnownTotal() {
        return hasKnownTotal;
    }

    public boolean isReadyToPlay() {
        return isReadyToPlay;
    }

    public int getProgressPercent() {
        if (!hasKnownTotal || totalLevels <= 0) {
            return 0;
        }
        int clamped = Math.min(completedLevels, totalLevels);
        return Math.round((clamped * 100f) / totalLevels);
    }
}

