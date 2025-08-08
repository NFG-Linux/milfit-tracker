package com.example.milfittracker.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(User user);

    @Update
    int update(User user);

    @Query("SELECT * FROM users LIMIT 1")
    User getUser();

    @Query("DELETE FROM users")
    void clear();
}
