package com.google.android.apps.auto.sdk.samples.uscitizenshipfilingplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.android.apps.auto.sdk.samples.uscitizenshipfilingplanner.databinding.RecyclerviewItemBinding;
import com.google.android.apps.auto.sdk.samples.uscitizenshipfilingplanner.trips.Trip;

import androidx.recyclerview.widget.RecyclerView;

public class TripsViewHolder extends RecyclerView.ViewHolder {
    private final TextView startDateItemView;
    private final TextView endDateItemView;
    private final TextView durationItemView;
    private final ImageButton editItemButton;
    private RecyclerviewItemBinding recyclerviewItemBinding;

    private TripsViewHolder(View itemView) {
        super(itemView);
        startDateItemView = itemView.findViewById(R.id.startDateTextView);
        endDateItemView = itemView.findViewById(R.id.endDateTextView);
        durationItemView = itemView.findViewById(R.id.durationTextView);
        editItemButton = itemView.findViewById(R.id.editButton);

    }

    public void bind(String startDate, String endDate, String duration, Trip trip, TripsListAdapter.OnTripClickListener onTripClickListener) {
        startDateItemView.setText(startDate);
        endDateItemView.setText(endDate);
        durationItemView.setText(duration);
        editItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTripClickListener.onTripClick(trip);
            }
        });
    }

    static TripsViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);
        return new TripsViewHolder(view);
    }
}
