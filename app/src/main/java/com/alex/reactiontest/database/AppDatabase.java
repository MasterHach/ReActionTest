package com.alex.reactiontest.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.alex.reactiontest.dao.ErrorDao;
import com.alex.reactiontest.dao.UserDao;
import com.alex.reactiontest.entities.LogClass;
import com.alex.reactiontest.entities.User;

@Database(entities = {User.class, LogClass.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract ErrorDao errorDao();

    private static final String DB_NAME = "reaction.db";
    private static volatile AppDatabase INSTANCE = null;

    synchronized static AppDatabase get(Context context) {
        if (INSTANCE == null) {
            INSTANCE = create(context, false);
        }
        return(INSTANCE);
    }
    public static AppDatabase create(Context context, boolean memoryOnly) {
        RoomDatabase.Builder<AppDatabase> bd;
        if (memoryOnly) {
            bd = Room.inMemoryDatabaseBuilder(context.getApplicationContext(), AppDatabase.class);
        } else {
            bd = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DB_NAME);
        }
        return(bd.build());
    }

}
