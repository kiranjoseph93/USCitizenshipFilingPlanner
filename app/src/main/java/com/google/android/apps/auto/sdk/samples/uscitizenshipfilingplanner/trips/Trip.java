package com.google.android.apps.auto.sdk.samples.uscitizenshipfilingplanner.trips;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Calendar;

@SuppressLint("ParcelCreator")
@Entity(tableName = "trip_table")
public class Trip implements Parcelable {
    @NonNull
    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(@NonNull Calendar startDate) {
        this.startDate = startDate;
    }

    public int getStartDateIndex() {
        return startDateIndex;
    }

    public void setStartDateIndex(int startDateIndex) {
        this.startDateIndex = startDateIndex;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public int getEndDateIndex() {
        return endDateIndex;
    }

    public void setEndDateIndex(int endDateIndex) {
        this.endDateIndex = endDateIndex;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    @PrimaryKey(autoGenerate = true)
    private long tripId;
    @NonNull
    @ColumnInfo(name = "start_date")
    private Calendar startDate;
    @ColumnInfo(name = "start_date_index")
    private int startDateIndex;
    @ColumnInfo(name = "end_date")
    private Calendar endDate;
    @ColumnInfo(name = "end_date_index")
    private int endDateIndex;
    @ColumnInfo(name = "duration_in_days")
    private int duration;
    @ColumnInfo(name = "destination")
    private String destination;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(tripId);
        dest.writeSerializable(startDate);
        dest.writeSerializable(endDate);
        dest.writeInt(startDateIndex);
        dest.writeInt(endDateIndex);
        dest.writeInt(duration);
        dest.writeString(destination);
    }
}
