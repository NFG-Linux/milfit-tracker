package com.example.milfittracker.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.lifecycle.LiveData;
import java.util.List;

@Dao
public interface ScoreDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Scores scores);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Scores> scores);

    @Query("SELECT * FROM scores ORDER BY date ASC")
    List<Scores> getAll();

    @Query("SELECT * FROM scores WHERE branch = :branch AND event = :event ORDER BY date DESC")
    List<Scores> getForBranchEvent(String branch, String event);

    @Query("SELECT * FROM scores WHERE branch = :branch AND event = :event AND date BETWEEN :fromDate AND :toDate ORDER BY date ASC")
    List<Scores> getForBranchEventBetween(String branch, String event, String fromDate, String toDate);

    @Query("SELECT * FROM scores ORDER BY date ASC")
    LiveData<List<Scores>> observeAll();

    @Query("SELECT * FROM scores ORDER BY date DESC")
    LiveData<List<Scores>> getAllLive();

    @Query("SELECT * FROM scores WHERE event = :event ORDER BY date ASC")
    LiveData<List<Scores>> observeByEvent(String event);

    @Query("SELECT * FROM scores WHERE sID = :sID ORDER BY date ASC")
    List<Scores> getBySID(String sID);

    @Query("SELECT DISTINCT sID FROM scores WHERE sID IS NOT NULL ORDER BY date DESC")
    List<String> getAllSID();

    @Query("SELECT * FROM scores WHERE sID = :sID ORDER BY date ASC")
    LiveData<List<Scores>> observeScoresBySID(String sID);

    @Query("SELECT * FROM scores WHERE event LIKE '%Run%' ORDER BY date DESC")
    LiveData<List<Scores>> observeAllRuns();

    @Query("DELETE FROM scores")
    void clearAll();
}
