package com.example.happykidlearn.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "game_progress")
public class GameProgress {

    @PrimaryKey
    @NonNull
    private String gameId;
    private int completedLevels;
    private int totalLevels;
    private int starsEarned;
    private boolean isUnlocked;

    public GameProgress(@NonNull String gameId, int completedLevels, int totalLevels, int starsEarned, boolean isUnlocked) {
        this.gameId = gameId;
        this.completedLevels = completedLevels;
        this.totalLevels = totalLevels;
        this.starsEarned = starsEarned;
        this.isUnlocked = isUnlocked;
    }

    @NonNull
    public String getGameId() {
        return gameId;
    }

    public void setGameId(@NonNull String gameId) {
        this.gameId = gameId;
    }

    public int getCompletedLevels() {
        return completedLevels;
    }

    public void setCompletedLevels(int completedLevels) {
        this.completedLevels = completedLevels;
    }

    public int getTotalLevels() {
        return totalLevels;
    }

    public void setTotalLevels(int totalLevels) {
        this.totalLevels = totalLevels;
    }

    public int getStarsEarned() {
        return starsEarned;
    }

    public void setStarsEarned(int starsEarned) {
        this.starsEarned = starsEarned;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }
}