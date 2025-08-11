package com.example.milfittracker.room;

import androidx.room.Entity;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    @ColumnInfo(name = "name")
    private String name = "";

    // Store as ISO date string (yyyy-MM-dd)
    @ColumnInfo(name = "birthday")
    private String bDay;

    @ColumnInfo(name = "gender")
    private String gender;

    @ColumnInfo(name = "altitude_group")
    private String altiGrp;

    // --- Canonical getters/setters (Room uses these/fields) ---
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    @NonNull public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }

    public String getBDay() { return bDay; }
    public void setBDay(String bDay) { this.bDay = bDay; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAltiGrp() { return altiGrp; }
    public void setAltiGrp(String altiGrp) { this.altiGrp = altiGrp; }
}