package com.google.android.apps.auto.sdk.samples.uscitizenshipfilingplanner.trips;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
@Dao
public interface TripsDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(Trip trip);

    @Query("DELETE FROM trip_table")
    void deleteAll();

    @Delete
    public void deleteTrips(Trip... trips);

    @Query("SELECT * FROM trip_table ORDER BY start_date ASC")
    LiveData<List<Trip>> getTrips();

    @Update
    void update(Trip trip);
}
