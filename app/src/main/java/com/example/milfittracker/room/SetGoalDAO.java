package com.example.milfittracker.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface SetGoalDAO {
    @Insert
    void Insert(SetGoal goal);

    @Update
    void update(SetGoal goal);

    @Delete
    void delete(SetGoal goal);

    @Query("SELECT * FROM set_goals")
    List<SetGoal> getAllGoals();

    @Query("SELECT * FROM set_goals WHERE ID = :id")
    SetGoal getGoalById(int id);
}
