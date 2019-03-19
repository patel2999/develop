package com.example.kissanhub.user.storage.local;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.kissanhub.user.storage.local.dao.WeatherDao;
import com.example.kissanhub.user.storage.models.WeatherDataEntity;

@Database(entities = {WeatherDataEntity.class}, version = 2, exportSchema = false)
public abstract class WeatherDatabase extends RoomDatabase {
    public abstract WeatherDao weatherDao();

    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    private static WeatherDatabase INSTANCE;

    public static WeatherDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), WeatherDatabase.class,
                            DBConstant.DB_NAME)
                            .allowMainThreadQueries()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}
