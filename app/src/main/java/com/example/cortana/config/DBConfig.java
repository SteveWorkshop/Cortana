package com.example.cortana.config;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.cortana.dao.MessageDao;
import com.example.cortana.entity.Message;

@Database(version = 1,entities = {Message.class},exportSchema = false)
public abstract class DBConfig extends RoomDatabase {
    public static final String DB_NAME="message.db";
    private static volatile DBConfig instance;

    public static synchronized DBConfig getInstance(Context context)
    {
        if(instance==null)
        {
            instance=create(context.getApplicationContext());
        }
        return instance;
    }

    private static DBConfig create(final Context context)
    {
        return Room.databaseBuilder(context, DBConfig.class,DB_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    public abstract MessageDao getMessageDao();
}
