package com.example.myapplication;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(version =6, entities = {locationEntity.class}, exportSchema = false)
public abstract class locationDatabase extends RoomDatabase {
    // call db instance : Room.databaseBuilder() or Room.inMemoryDatabaseBuilder()
    private static locationDatabase INSTANCE;
    public abstract locationDao locationDao();

    public static locationDatabase getAppDatabase(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context, locationDatabase.class, "locationDatabase")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public static void destroyDatabaseInstance(){
        INSTANCE = null;
    }
}
