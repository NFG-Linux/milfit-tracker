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

    @Query("SELECT COUNT(*) FROM users")
    int count();

    @Query("UPDATE users SET branch = :branch WHERE id = :id")
    void updateBranch(long id, String branch);

    @Query("UPDATE users SET altitude = :altitude WHERE id = :id")
    void updateAltitude(long id, String altitude);

    @Query("DELETE FROM users")
    void clear();
}
