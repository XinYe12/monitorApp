package com.example.monitorapp;

import android.util.Log;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CustomXAxisValueFormatter extends ValueFormatter {
    private SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
    private SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.US);

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        try {
            // Debug logging to see the input value
            Log.d("CustomXAxisValueFormatter", "Input Value: " + value);

            // Parse the long float to a Date
            Date dateTimeValue = new Date((long) value);

            // Create a SimpleDateFormat with your desired datetime format ("MM-dd HH:mm")
            SimpleDateFormat outputDateTimeFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.US);

            // Format the parsed date to the desired output datetime format
            String formattedDateTime = outputDateTimeFormat.format(dateTimeValue);

            // Debug logging to see the formatted datetime
            Log.d("CustomXAxisValueFormatter", "Formatted DateTime: " + formattedDateTime);

            // Return the formatted datetime
            return formattedDateTime;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return the original value as a fallback if there's an error
        return String.valueOf(value);
    }




}

