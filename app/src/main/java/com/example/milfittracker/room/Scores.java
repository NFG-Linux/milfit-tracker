package com.example.milfittracker.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "scores")
public class Scores {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String branch;
    private String event;
    private String gender;
    private int age;
    private int eventValue;
    private String unit;
    private String date;
    private String sID;

    //constructor for room
    public Scores() {}

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @NonNull public String getBranch() {
        return branch;
    }
    public void setBranch(@NonNull String branch) {
        this.branch = branch;
    }

    @NonNull public String getEvent() {
        return event;
    }
    public void setEvent(@NonNull String event) {
        this.event = event;
    }

    @NonNull public String getGender() {
        return gender;
    }
    public void setGender(@NonNull String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }

    public int getEventValue() {
        return eventValue;
    }
    public void setEventValue(int eventValue) {
        this.eventValue = eventValue;
    }

    @NonNull public String getUnit() {
        return unit;
    }
    public void setUnit(@NonNull String unit) {
        this.unit = unit;
    }

    @NonNull public String getDate() {
        return date;
    }
    public void setDate(@NonNull String date) {
        this.date = date;
    }

    public String getSID() {
        return sID;
    }
    public void setSID(String sID) {
        this.sID = sID;
    }

    @Ignore
    public Scores(String branch, String event, String gender, int age, int eventValue, String unit, String date) {
        this.branch = branch;
        this.event = event;
        this.gender = gender;
        this.age = age;
        this.eventValue = eventValue;
        this.unit = unit;
        this.date = date;
    }

    @Ignore
    public Scores(String branch, String event, String gender, int age, int eventValue, String unit, String date, String sID) {
        this.branch = branch;
        this.event = event;
        this.gender = gender;
        this.age = age;
        this.eventValue = eventValue;
        this.unit = unit;
        this.date = date;
        this.sID = sID;
    }
}
