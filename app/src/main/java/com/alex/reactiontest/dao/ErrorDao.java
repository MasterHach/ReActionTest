package com.alex.reactiontest.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import androidx.lifecycle.LiveData;

import com.alex.reactiontest.entities.LogClass;

import java.util.List;

@Dao
public interface ErrorDao {
    @Query("SELECT * FROM errors WHERE user_uid = :user_uid")
    LiveData<List<LogClass>> getErrorsForUser(String user_uid);

    @Query("SELECT * FROM errors")
    List<LogClass> getAllErrors();

    @Query("SELECT * FROM errors WHERE log_id = :id")
    LogClass getErrorById(int id);

    @Insert
    void insert(LogClass error);

    @Update
    void update(LogClass error);

    @Delete
    void delete(LogClass error);
}
