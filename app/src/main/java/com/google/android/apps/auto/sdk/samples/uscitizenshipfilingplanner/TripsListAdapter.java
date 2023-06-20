package com.google.android.apps.auto.sdk.samples.uscitizenshipfilingplanner;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.google.android.apps.auto.sdk.samples.uscitizenshipfilingplanner.trips.Trip;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class TripsListAdapter extends ListAdapter<Trip, TripsViewHolder> {
    public interface OnTripClickListener {
        void onTripClick(Trip trip);
    }

    private final OnTripClickListener onTripClickListener;
    public TripsListAdapter(@NonNull DiffUtil.ItemCallback<Trip> diffCallback, OnTripClickListener onTripClickListener) {
        super(diffCallback);
        this.onTripClickListener = onTripClickListener;
    }

    @Override
    public TripsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return TripsViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(TripsViewHolder holder, int position) {
        Trip current = getItem(position);
        DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
        holder.bind("From: "+dateFormat.format(current.getStartDate().getTime()),"To: "+dateFormat.format(current.getEndDate().getTime()), " - "+current.getDuration()+ " days", current, onTripClickListener);
    }

    static class TripsDiff extends DiffUtil.ItemCallback<Trip> {

        @Override
        public boolean areItemsTheSame(@NonNull Trip oldItem, @NonNull Trip newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Trip oldItem, @NonNull Trip newItem) {
            return oldItem.getStartDate().equals(newItem.getStartDate());
        }
    }
}
