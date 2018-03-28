package com.lordsantanna.vento.utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import java.util.Calendar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

public class TimePickerFragment extends DialogFragment {

    private TimePickerDialog.OnTimeSetListener listener;
    private static java.util.Calendar date;

    public static TimePickerFragment newInstance(TimePickerDialog.OnTimeSetListener listener, Calendar c) {
        date = c;
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setListener(listener);
        return fragment;
    }

    public void setListener(TimePickerDialog.OnTimeSetListener listener) {
        this.listener = listener;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        int hourOfDay = date.get(Calendar.HOUR_OF_DAY);
        int minute = date.get(Calendar.MINUTE);
        boolean is24HourView = true;

        // Create a new instance of DatePickerDialog and return it
        return new TimePickerDialog(getActivity(), listener, hourOfDay, minute, is24HourView);
    }

}