package com.google.android.apps.auto.sdk.samples.uscitizenshipfilingplanner.trips;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Trip.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class TripsRoomDatabase extends RoomDatabase {
    public abstract TripsDao tripDao();

    private static volatile TripsRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static TripsRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TripsRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TripsRoomDatabase.class, "trip_database").fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
