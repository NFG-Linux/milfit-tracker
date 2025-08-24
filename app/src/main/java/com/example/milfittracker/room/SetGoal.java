package com.example.milfittracker.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Index;

@Entity(tableName = "set_goals", indices = @Index(value = {"branch","event"}, unique = true))
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

    public String getBranch() {
        return branch;
    }
    public String getEvent() {
        return event;
    }
    public int getValue() {
        return goalVal;
    }
    public String getUnit() {
        return goalUnit;
    }
    public String getDate() {
        return goalDate;
    }
}
