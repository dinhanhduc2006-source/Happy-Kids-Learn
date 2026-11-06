package com.example.happykidlearn.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "app_settings")
public class AppSettings {

    @PrimaryKey
    private int id = 1;
    private boolean soundEnabled = true;
    private boolean bgmEnabled = true;
    private int screenTimeLimitMinutes = 30;
    private String parentalPin = "1234";

    public AppSettings() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public boolean isBgmEnabled() {
        return bgmEnabled;
    }

    public void setBgmEnabled(boolean bgmEnabled) {
        this.bgmEnabled = bgmEnabled;
    }

    public int getScreenTimeLimitMinutes() {
        return screenTimeLimitMinutes;
    }

    public void setScreenTimeLimitMinutes(int screenTimeLimitMinutes) {
        this.screenTimeLimitMinutes = screenTimeLimitMinutes;
    }

    public String getParentalPin() {
        return parentalPin;
    }

    public void setParentalPin(String parentalPin) {
        this.parentalPin = parentalPin;
    }
}