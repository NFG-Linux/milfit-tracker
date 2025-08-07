package com.example.milfittracker.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "set_goals")
public class SetGoal {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String branch;
    public String event;
    public int goalVal;
    public String goalUnit;
    public String goalDate;

    public SetGoal(String branch, String event, int goalVal, String goalUnit, String goalDate) {
        this.branch = branch;
        this.event = event;
        this.goalVal = goalVal;
        this.goalUnit = goalUnit;
        this.goalDate = goalDate;
    }
}
