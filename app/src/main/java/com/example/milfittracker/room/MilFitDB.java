package com.example.milfittracker.room;

import android.content.Context;

import androidx.room.RoomDatabase;
import androidx.room.Room;
import androidx.room.Database;
import androidx.room.TypeConverters;

import com.example.milfittracker.room.SetGoalDAO;
import com.example.milfittracker.room.SetGoal;

@Database(entities = {SetGoal.class}, version = 1, exportSchema = false)
public abstract class MilFitDB extends RoomDatabase {
    private static volatile MilFitDB INSTANCE;

    public abstract SetGoalDAO setGoalDAO();

    public static MilFitDB getInstance(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    MilFitDB.class, "milfit_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
