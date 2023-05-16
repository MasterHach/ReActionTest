package com.alex.reactiontest.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.alex.reactiontest.entities.User;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users WHERE uid = :user_uid")
    User getUserByUid(String user_uid);

    @Query("UPDATE users SET nickname = :new_nick WHERE uid = :user_uid")
    void updateNick(String user_uid, String new_nick);

    @Query("SELECT * FROM users")
    List<User> getAllUsers();
    @Insert
    void insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);
}
