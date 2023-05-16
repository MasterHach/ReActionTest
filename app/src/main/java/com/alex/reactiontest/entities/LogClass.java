package com.alex.reactiontest.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;



import java.util.Date;

@Entity(tableName = "errors")
public class LogClass {

    @PrimaryKey(autoGenerate = true)
    public int log_id;

    @ColumnInfo(name = "user_uid")
    public String user_uid;

    @ColumnInfo(name = "log_text")
    public String log_text;

    @ColumnInfo(name = "log_date")
    public Date log_date;

    @ColumnInfo(name = "log_type")
    public String log_type;
}
