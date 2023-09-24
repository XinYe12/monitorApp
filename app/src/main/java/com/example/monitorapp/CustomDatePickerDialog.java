package com.example.monitorapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;

public class CustomDatePickerDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private boolean isSelectingStartDateTime;
    private Calendar selectedStartCalendar;
    private DatePickerDialog.OnDateSetListener onDateSetListener;

    public CustomDatePickerDialog(boolean isSelectingStartDateTime, Calendar selectedStartCalendar, DatePickerDialog.OnDateSetListener onDateSetListener) {
        this.isSelectingStartDateTime = isSelectingStartDateTime;
        this.selectedStartCalendar = selectedStartCalendar;
        this.onDateSetListener = onDateSetListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog with a custom OnDateSetListener
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), this, year, month, day);

        // Set a date limit to disable dates before the selected start datetime
        if (!isSelectingStartDateTime && selectedStartCalendar != null) {
            datePickerDialog.getDatePicker().setMinDate(selectedStartCalendar.getTimeInMillis());
        }

        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        onDateSetListener.onDateSet(view, year, monthOfYear, dayOfMonth);
    }
}
