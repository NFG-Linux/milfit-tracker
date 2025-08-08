package com.example.milfittracker.room;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey(autoGenerate = true)
    private long id;

    public String name;
    public String bDate;
    public String gender;

    //prob move this to AF/Space Force since they are the only ones to use multiple altitudes. Navy and Marines use < or >5000ft
    public String altGroup;

    public User() {}

    @Ignore
    public User(long id, String name, String bDate, String gender, String altGroup) {
        this.id = id;
        this.name = name;
        this.bDate = bDate;
        this.gender = gender;
        this.altGroup = altGroup;
    }

    //getter/setter for id
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}