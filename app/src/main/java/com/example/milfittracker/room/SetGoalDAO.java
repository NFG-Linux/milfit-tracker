package com.example.milfittracker.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.OnConflictStrategy;

import java.util.List;

@Dao
public interface SetGoalDAO {
    @Insert
    long insert(SetGoal goal);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long upsert(SetGoal goal);

    @Update
    int update(SetGoal goal);

    @Delete
    int delete(SetGoal goal);

    @Query("DELETE FROM set_goals")
    int clear();

    @Query("SELECT * FROM set_goals")
    List<SetGoal> getAllGoals();

    @Query("SELECT * FROM set_goals ORDER BY id DESC")
    LiveData<List<SetGoal>> getAllLive();

    @Query("SELECT * FROM set_goals WHERE ID = :id")
    SetGoal getGoalById(int id);

    @Query("SELECT * FROM set_goals WHERE branch = :branch AND event = :event LIMIT 1")
    SetGoal getForBranchEvent(String branch, String event);

    @Query("SELECT * FROM set_goals WHERE branch = :branch ORDER BY event ASC")
    List<SetGoal> getForBranch(String branch);
}
