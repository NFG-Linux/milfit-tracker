package com.example.milfittracker.room;

import android.content.Context;
import androidx.room.RoomDatabase;
import androidx.room.Room;
import androidx.room.Database;

@Database(entities = {SetGoal.class, Scores.class, User.class}, version = 4, exportSchema = false)
public abstract class MilFitDB extends RoomDatabase {
    private static volatile MilFitDB INSTANCE;

    public abstract SetGoalDAO setGoalDAO();
    public abstract ScoreDAO scoreDAO();
    public abstract UserDAO userDAO();

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
