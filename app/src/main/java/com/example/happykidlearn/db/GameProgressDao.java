package com.example.happykidlearn.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GameProgressDao {

    @Query("SELECT * FROM game_progress")
    List<GameProgress> getAllProgress();

    @Query("SELECT * FROM game_progress WHERE gameId = :gameId LIMIT 1")
    GameProgress getProgressByGameId(String gameId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(GameProgress progress);

    @Query("DELETE FROM game_progress")
    void deleteAll();
}