package com.google.android.apps.auto.sdk.samples.uscitizenshipfilingplanner.trips;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class TripViewModel extends AndroidViewModel {

    private TripsRepository mRepository;

    private final LiveData<List<Trip>> mAllTrips;
    public TripViewModel(@NonNull Application application) {
        super(application);
        mRepository = new TripsRepository(application);
        mAllTrips = mRepository.getAllTrips();
    }

    public LiveData<List<Trip>> getAllTrips() { return mAllTrips; }

    public void insert(Trip trip) { mRepository.insert(trip); }
    public void update(Trip trip) { mRepository.update(trip); }
    public void deleteAll() { mRepository.deleteAll(); }
    public void delete(Trip... trips) { mRepository.delete(trips); }
}
