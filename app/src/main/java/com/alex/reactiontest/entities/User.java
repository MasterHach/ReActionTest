package com.alex.reactiontest.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;


@Entity(tableName = "users")
public class User {
    @NonNull
    @PrimaryKey
    public String uid = "0";
    @ColumnInfo(name = "nickname")
    public String nickname;
    @ColumnInfo(name = "email")
    public String email;
    @ColumnInfo(name = "positive_games")
    public int positive_games;
    @ColumnInfo(name = "total_games")
    public int total_games;
    @ColumnInfo(name = "best_score")
    public int best_score;

}
