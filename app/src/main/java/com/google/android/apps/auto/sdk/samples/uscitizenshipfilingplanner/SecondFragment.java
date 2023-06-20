package com.google.android.apps.auto.sdk.samples.uscitizenshipfilingplanner;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.apps.auto.sdk.samples.uscitizenshipfilingplanner.databinding.FragmentSecondBinding;
import com.google.android.apps.auto.sdk.samples.uscitizenshipfilingplanner.trips.Trip;
import com.google.android.apps.auto.sdk.samples.uscitizenshipfilingplanner.trips.TripViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    DatePickerDialog datePickerDialog;
    int startDateIndex = -1;
    int endDateIndex = -1;
    private Calendar initialCalendar;
    private Calendar startDate;
    private Calendar endDate;
    private TripViewModel tripViewModel;
    private boolean isEditingTrip = false;
    LiveData<List<Trip>> tripListLiveData;
    List<Trip> tripsList;
    Trip selectedTrip;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialCalendar = Calendar.getInstance();
        initialCalendar.set(2017, 03, 19);

        datePickerDialog = new DatePickerDialog(getContext());
//        binding.buttonSave.setOnClickListener(view1 -> NavHostFragment.findNavController(SecondFragment.this)
//                .navigate(R.id.action_SecondFragment_to_FirstFragment));
        binding.buttonSave.setOnClickListener(saveClickListener);


        binding.startDateValue.setOnClickListener(onDateClickListener);
        binding.endDateValue.setOnClickListener(onDateClickListener);
        getParentFragmentManager().setFragmentResultListener("requestKey", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported

                selectedTrip = bundle.getParcelable("selected_trip");
                // Do something with the result
                startDateIndex = selectedTrip.getStartDateIndex();
                endDateIndex = selectedTrip.getEndDateIndex();
                startDate = selectedTrip.getStartDate();
                endDate = selectedTrip.getEndDate();
                isEditingTrip = true;
                binding.buttonSave.setEnabled(true);
                binding.buttonDelete.setVisibility(View.VISIBLE);
                binding.buttonDelete.setEnabled(true);
                binding.buttonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tripViewModel.delete(selectedTrip);
                        NavHostFragment.findNavController(SecondFragment.this)
                                .navigate(R.id.action_SecondFragment_to_FirstFragment);
                    }
                });
                binding.startDateValue.setText(new SimpleDateFormat("MMM d, yyyy").format(startDate.getTime()));
                binding.endDateValue.setText(new SimpleDateFormat("MMM d, yyyy").format(endDate.getTime()));
            }
        });

        tripViewModel = new ViewModelProvider(requireActivity()).get(TripViewModel.class);
        tripListLiveData = tripViewModel.getAllTrips();
        tripListLiveData.observe(getViewLifecycleOwner(), new Observer<List<Trip>>() {
            @Override
            public void onChanged(List<Trip> trips) {
                // Update the cached copy of the trips in the adapter.
//            adapter.submitList(trips);
                tripsList = trips;
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    View.OnClickListener onDateClickListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View v) {
//                binding.datePicker.setVisibility(View.VISIBLE);
            if (isEditingTrip) {
                if (v.equals(binding.startDateValue))
                    datePickerDialog.updateDate(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));
                if (v.equals(binding.endDateValue))
                    datePickerDialog.updateDate(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH));
            }
            datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);

                if (calendar.compareTo(initialCalendar) < 0) {
                    Toast.makeText(getContext(), "Please select a date after April 19, 2017", Toast.LENGTH_LONG).show();
                    return;
                }

                int daysInBetween = FirstFragment.checkDaysInBetween(initialCalendar,calendar);


                if (v.equals(binding.startDateValue)) {
                    startDateIndex = daysInBetween;
                    startDate = calendar;
                } else if (v.equals(binding.endDateValue)) {
                    endDateIndex = daysInBetween;
                    endDate = calendar;
                }
                if (endDateIndex != -1 && startDateIndex != -1) {
                    if (endDateIndex < startDateIndex) {
                        Toast.makeText(getContext(), "Start date has to be before end date", Toast.LENGTH_LONG).show();
                        return;
                    } else if (endDateIndex == startDateIndex) {
                        Toast.makeText(getContext(), "Please add trips that are longer than 24 hours", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        binding.buttonSave.setEnabled(true);
                    }
                }
                ((Button) v).setText(new SimpleDateFormat("MMM d, yyyy").format(calendar.getTime()));
            });
            datePickerDialog.show();
        }
    };
    View.OnClickListener saveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for (Trip trip :
                    tripsList) {
                if (isEditingTrip && trip.equals(selectedTrip))
                    continue;
                if (((startDate.compareTo(trip.getStartDate()) >= 0) && (startDate.compareTo(trip.getEndDate()) <= 0)) || (endDate.compareTo(trip.getStartDate()) >= 0) && (endDate.compareTo(trip.getEndDate()) <= 0)) {
                    Toast.makeText(getContext(), "This trip overlaps with an already existing trip, Please select other dates", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            Trip trip;
            if (!isEditingTrip)
                trip = new Trip();
            else
                trip = selectedTrip;
            trip.setStartDate(startDate);
            trip.setEndDate(endDate);
            trip.setStartDateIndex(startDateIndex);
            trip.setEndDateIndex(endDateIndex);
            trip.setDuration(endDateIndex - startDateIndex);
            if (isEditingTrip)
                tripViewModel.update(trip);
            else
                tripViewModel.insert(trip);

            NavHostFragment.findNavController(SecondFragment.this)
                    .navigate(R.id.action_SecondFragment_to_FirstFragment);
        }
    };

}