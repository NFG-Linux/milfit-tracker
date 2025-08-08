package com.example.milfittracker.room;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "scores")
public class Scores {

    @PrimaryKey(autoGenerate = true)
    private long id;

    public String branch;
    public String event;
    public String gender;
    public String ageInt;
    public int eventValue;
    public String unit;
    public String date;

    //constructor for room
    public Scores() {}

    //getter and setter so id can stay private
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Ignore
    public Scores(String branch, String event, String gender, String ageInt, int eventValue, String unit, String date) {
        this.branch = branch;
        this.event = event;
        this.gender = gender;
        this.ageInt = ageInt;
        this.eventValue = eventValue;
        this.unit = unit;
        this.date = date;
    }
}
