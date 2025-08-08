package com.example.milfittracker.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ScoreDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Scores scores);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Scores> scores);

    @Query("SELECT * FROM scores ORDER BY date DESC")
    List<Scores> getAll();

    @Query("SELECT * FROM scores WHERE branch = :branch AND event = :event ORDER BY date DESC")
    List<Scores> getForBranchEvent(String branch, String event);

    @Query("SELECT * FROM scores WHERE branch = :branch AND event = :event AND date BETWEEN :fromDate AND :toDate ORDER BY date ASC")
    List<Scores> getForBranchEventBetween(String branch, String event, String fromDate, String toDate);

    @Query("DELETE FROM scores")
    void clearAll();
}
