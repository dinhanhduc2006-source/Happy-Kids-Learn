package com.example.happykidlearn.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface AppSettingsDao {

    @Query("SELECT * FROM app_settings WHERE id = 1 LIMIT 1")
    AppSettings getSettings();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveSettings(AppSettings settings);
}