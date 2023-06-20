package com.google.android.apps.auto.sdk.samples.uscitizenshipfilingplanner;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.apps.auto.sdk.samples.uscitizenshipfilingplanner.databinding.FragmentFirstBinding;
import com.google.android.apps.auto.sdk.samples.uscitizenshipfilingplanner.trips.Trip;
import com.google.android.apps.auto.sdk.samples.uscitizenshipfilingplanner.trips.TripViewModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    private TripViewModel tripViewModel;
    private DatePickerDialog datePickerDialog;
    int[] totalDates;
    LiveData<List<Trip>> tripListLiveData;
    private final Calendar initialCalendar;

    public FirstFragment() {
        this.initialCalendar = Calendar.getInstance();
        this.initialCalendar.set(2017, 03, 19);;
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //get trips livedata
        tripViewModel = new ViewModelProvider(requireActivity()).get(TripViewModel.class);
        tripListLiveData = tripViewModel.getAllTrips();
        //array for dates and fill with 1
        totalDates = new int[365 * 12];
        datePickerDialog = new DatePickerDialog(getContext());
        //add trips button
        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToSecondFragment();
            }
        });

        binding.checkBestDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBestDate();
            }
        });
        binding.checkDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = requireContext().getApplicationContext();
                WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                binding.bestDate.setText(ip);
                datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, dayOfMonth);
                    Calendar startCalendar = (Calendar) initialCalendar.clone();
                    startCalendar.add(Calendar.YEAR, 5);
                    if (calendar.compareTo(startCalendar) < 0) {
                        Toast.makeText(getContext(), "Please select a date after April 19, 2022", Toast.LENGTH_LONG).show();
                        return;
                    }
                    binding.days.setText(""+checkDaysInArray(totalDates, initialCalendar, calendar,Calendar.YEAR, -5)+ " days");
                    binding.checkDays.setText(new SimpleDateFormat("MMM d, yyyy").format(calendar.getTime()));
                });
                datePickerDialog.show();
            }
        });
        // Trip list recyclerview adapter
        final TripsListAdapter adapter = new TripsListAdapter(new TripsListAdapter.TripsDiff(), trip -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("selected_trip", trip);
            getParentFragmentManager().setFragmentResult("requestKey", bundle);
            navigateToSecondFragment();
        });
        binding.recyclerview.setAdapter(adapter);
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        //Livedata observer
        tripListLiveData.observe(getViewLifecycleOwner(), trips -> {
            // Update the cached copy of the trips in the adapter.
            adapter.submitList(trips);
            loadTripsToArray();
        });
        //delete all trips
        binding.deleteAllTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show dialog before deleting all trips
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.Confirm);
                builder.setMessage(R.string.dialog_delete_all_trips);
                builder.setPositiveButton(R.string.delete, (dialog, which) -> {
                    tripViewModel.deleteAll();
                });
                builder.setNegativeButton(R.string.cancel, (dialog, which) -> {
                    dialog.dismiss();
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
    //fun to load trips to array
    private void loadTripsToArray() {
        Arrays.fill(totalDates, 1);
        List<Trip> tripsList = tripListLiveData.getValue();
        for (Trip trip :
                tripsList) {
            int startIndex = trip.getStartDateIndex();
            int endIndex = trip.getEndDateIndex();
            if (totalDates.length <= endIndex)
                return;
            Arrays.fill(totalDates, startIndex + 1, endIndex, 0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    // fun to go to 2nd fragment
    public void navigateToSecondFragment() {
        NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment);
    }

    private void checkBestDate()
    {
        boolean bestDateFound = false;
        Calendar bestDate;

        Calendar startCalendar = Calendar.getInstance();
        //Calendar to start calculation

        //Calendar after 5 years
        startCalendar.set( initialCalendar.get(Calendar.YEAR) + 5, initialCalendar.get(Calendar.MONTH), initialCalendar.get(Calendar.DAY_OF_MONTH));
        // starting index of the array to start counting backwards
        int bestDateStartIndex = checkDaysInBetween(initialCalendar,startCalendar);
        for (int i = bestDateStartIndex; i < totalDates.length; i++) {
            //Converting array back to calendar to calculate the index 5 years before
            //create a calendar by cloning startCalendar (where calculation starts)
            Calendar currentIndexCalendar = (Calendar) startCalendar.clone();
            //Calendar of the corresponding index
            //currentIndexCalendar is the calendar currently checking for being the best date
            currentIndexCalendar.add(Calendar.DAY_OF_YEAR, i- bestDateStartIndex);
            //Cloning the calendar of the corresponding index and subtracting 5 years to get the calendar before 5 years

            if (checkDaysInArray(totalDates,initialCalendar,currentIndexCalendar, Calendar.YEAR, -5) >= 913)
            {
                bestDateFound = true;
                bestDate = currentIndexCalendar;
                DateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
                binding.bestDate.setText(dateFormat.format(bestDate.getTime()));
                break;
            }


        }
    }
//stand alone function which takes in an array of dates (1, 0), an initial calendar to denote the starting date in the array, start calendar, calendar fields and amount to add to get the new calendar
    private int checkDaysInArray(int[] totalDates, Calendar initialCalendar ,Calendar startCalendar, int field, int amount)
    {
        Calendar newCalendar = (Calendar) startCalendar.clone();
        newCalendar.add(field,amount);
        //finding the index of calendars
        int startCalendarIndex = checkDaysInBetween(initialCalendar,startCalendar);
        int newCalendarIndex = checkDaysInBetween(initialCalendar,newCalendar);
        int daysCount =0;
        for (int j = Math.min(startCalendarIndex,newCalendarIndex); j < Math.max(startCalendarIndex,newCalendarIndex); j++) {
            if (totalDates[j]!= 0)
                daysCount++;
        }
        return daysCount;
    }

    protected static int checkDaysInBetween(@NonNull Calendar startCalendar, @NonNull Calendar endCalendar)
    {
        int daysInBetween = 0;
        Calendar tempCalendar = endCalendar;
        final int toYear = endCalendar.get(Calendar.YEAR);
        daysInBetween += endCalendar.get(Calendar.DAY_OF_YEAR);

        tempCalendar = (Calendar) startCalendar.clone();
        daysInBetween -= startCalendar.get(Calendar.DAY_OF_YEAR);

        while (tempCalendar.get(Calendar.YEAR) < toYear) {
            daysInBetween += tempCalendar.getActualMaximum(Calendar.DAY_OF_YEAR);
            tempCalendar.add(Calendar.YEAR, 1);
        }
        return  daysInBetween;
    }
}