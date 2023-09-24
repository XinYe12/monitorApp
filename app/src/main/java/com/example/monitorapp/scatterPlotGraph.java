package com.example.monitorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.monitorapp.userInterfaces.ui.dashboard.DashboardFragment;
import com.example.monitorapp.userInterfaces.ui.notifications.NotificationsFragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class scatterPlotGraph extends AppCompatActivity {
    private static final String TAG = "mysql-scatterPlot";

    private static String deviceID = null;
    private static int INVALID;

    private static String selectedPlaceID = null, selectedID = null;

    private LineChart lineChart1_rotation;
    private LineChart linechart2_temperature;
    private LineChart linechart3_vibration;
    private Button datePickerButtonStart;
    private Button timePickerButtonEnd;
    private String startTime, startDate,endDate,endTime;

    private boolean bothDatetimesSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scatter_plot_graph);
        setupDropdowns();
        setupDateTimePicker();

    }
    private Calendar startCalendar; // Declare start datetime calendar
    private Calendar endCalendar;   // Declare end datetime calendar
    private boolean isSelectingStartDateTime = true; // Flag to track if selecting start datetime
    private Button selectedButton; // Button that triggers the datetime picker

    private void setupDateTimePicker() {
        Button startButton = findViewById(R.id.datetimePickerButtonStart);
        Button endButton = findViewById(R.id.datetimePickerButtonEnd);

        startButton.setOnClickListener(v -> {
            // Set the flag to indicate selecting the start datetime
            isSelectingStartDateTime = true;

            // Store the button that triggered the datetime picker
            selectedButton = startButton;

            // Show the date picker dialog
            showDatePicker();
        });

        endButton.setOnClickListener(v -> {
            // Set the flag to indicate selecting the end datetime
            isSelectingStartDateTime = false;

            // Store the button that triggered the datetime picker
            selectedButton = endButton;

            // Show the date picker dialog
            showDatePicker();
        });
    }

    private void showDatePicker() {
        // Get the current date and time
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create and show the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                scatterPlotGraph.this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    // Handle the selected date here
                    // year1, monthOfYear, and dayOfMonth contain the selected date

                    // Format the selected date as desired (e.g., "yyyy MMM dd")
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy MMM dd", Locale.US);
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(year1, monthOfYear, dayOfMonth);

                    // Create and show the TimePickerDialog after selecting the date
                    showTimePickerDialog(selectedCalendar);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void showTimePickerDialog(Calendar selectedCalendar) {
        int hour = selectedCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = selectedCalendar.get(Calendar.MINUTE);

        // Create and show the TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                scatterPlotGraph.this,
                (view, hourOfDay, minuteOfDay) -> {
                    // Handle the selected time here
                    // hourOfDay and minuteOfDay contain the selected time

                    // Format the selected time as desired (e.g., "hh:mm a")
                    String formattedDateAndTime = String.format(Locale.US, "%02d-%02d %02d:%02d",
                            selectedCalendar.get(Calendar.MONTH) + 1, // Adding 1 to month because it's zero-based
                            selectedCalendar.get(Calendar.DAY_OF_MONTH),
                            hourOfDay, minuteOfDay
                    );

                    // Set the selected date and time as the button text
                    selectedButton.setText(formattedDateAndTime);


                    // Update the appropriate calendar (start or end datetime)
                    if (isSelectingStartDateTime) {
                        startCalendar = selectedCalendar;
                    } else {
                        endCalendar = selectedCalendar;
                    }

                    // Check if both startCalendar and endCalendar are not null
                    if (startCalendar != null && endCalendar != null) {
                        bothDatetimesSelected = true; // Set the flag to true
                    }

                    // Execute the code for drawing the line chart only if both datetimes are selected
                    if (bothDatetimesSelected) {
                        Intent intent = getIntent();
                        if (intent != null) {
                            String deviceNum = intent.getStringExtra("deviceID");
                            deviceID = deviceNum;
                            new Thread(() -> {
                                try {
                                    String combinedID = DBConnection.getCombinedValueForId(deviceNum);
                                    //retrieve the vibration and temperature
                                    Map<String, List<String>> columnData = DBConnection.retrieveDataFromCombinedID(combinedID);
                                    drawLinechart(columnData);
                                    Log.d(TAG, "column Data: " + columnData);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }).start();
                            if (deviceNum != null) {
                                // Log the deviceNum value
                                Log.d(TAG, "Received deviceNum: " + deviceNum);
                            } else {
                                Log.e(TAG, "deviceNum is null");
                            }
                        } else {
                            Log.e(TAG, "Intent is null");
                        }
                    }
                },
                hour, minute, true // true for 24-hour format, false for 12-hour format
        );
        timePickerDialog.show();
    }


    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    private void setupDropdowns() {
        new Thread(() -> {
            Map<String, Integer> idMap = DBConnection.getIdsByCompany(DashboardFragment.company);//use dashboard company b'c it is fosure going to be loaded
            Log.d(TAG, "idmap: " + idMap.size());
            // Convert the list of Integer values to an array of String values
            String[] dropdownItems2 = new String[idMap.size() + 1]; // +1 for the title
            dropdownItems2[0] = "设备编号"; // Set the title as the first item
            Integer[] sortedIDs = new Integer[idMap.size()];
            String[] dropdownItems1 = new String[idMap.size() + 1]; // +1 for the title
            dropdownItems1[0] = "设备位号"; // Set the title as the first ite
            int index = 1; // Start from index 1 for the data items

            for (Map.Entry<String, Integer> entry : idMap.entrySet()) {
                String combinedKey = entry.getKey();
                int id = entry.getValue();

                dropdownItems1[index] = combinedKey;
                dropdownItems2[index] = String.valueOf(id);

                index++; // Increment the index
            }
            // Custom bubble sort to sort dropdownItems2 while preserving the title
            for (int i = 2; i < dropdownItems2.length; i++) { // Start from index 2 to skip the title at index 1
                for (int j = 1; j < dropdownItems2.length - i + 1; j++) {
                    if (Integer.parseInt(dropdownItems2[j]) > Integer.parseInt(dropdownItems2[j + 1])) {
                        // Swap IDs
                        String temp = dropdownItems2[j];
                        dropdownItems2[j] = dropdownItems2[j + 1];
                        dropdownItems2[j + 1] = temp;

                        // Swap combined keys (dropdownItems1)
                        temp = dropdownItems1[j];
                        dropdownItems1[j] = dropdownItems1[j + 1];
                        dropdownItems1[j + 1] = temp;
                    }
                }
            }
            // Post an update to the UI thread using the Handler
            uiHandler.post(() -> updateUI(dropdownItems1, dropdownItems2));
        }).start();
    }

    private void updateUI(String[] dropdownItems1, String[] dropdownItems2) {
        Spinner dropdown1 = findViewById(R.id.dropdown1);
        Spinner dropdown2 = findViewById(R.id.dropdown2);

        // Create an ArrayAdapter for each Spinner and set it with default values
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dropdownItems1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dropdownItems2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dropdown1.setAdapter(adapter1);
        dropdown2.setAdapter(adapter2);

        // Set an OnItemSelectedListener for each Spinner
        dropdown1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle user selection in dropdown1
                String selectedValue = dropdown1.getSelectedItem().toString();
                selectedPlaceID = selectedValue;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle the case where nothing is selected (optional)
            }
        });

        dropdown2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle user selection in dropdown2
                String selectedValue = dropdown2.getSelectedItem().toString();
                //selectedID = selectedValue;
                if (bothDatetimesSelected) {
                    new Thread(() -> {
                        try {
                            String combinedID = DBConnection.getCombinedValueForId(selectedValue);
                            Map<String, List<String>> columnData = DBConnection.retrieveDataFromCombinedID(combinedID);
                            drawLinechart(columnData);
                            Log.d(TAG, "column Data: " + columnData);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }).start();
                    // You can perform actions based on the selected value here
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle the case where nothing is selected (optional)
            }
        });
    }


    private void drawLinechart(Map<String, List<String>> columnData) throws ParseException {
        lineChart1_rotation = (LineChart) findViewById(R.id.linechart1_rotation);
        linechart2_temperature = (LineChart) findViewById(R.id.linechart2_temperature);
        linechart3_vibration = (LineChart) findViewById(R.id.linechart3_vibration);

        //the List<String> yaxis is to retrieve from database directly without formatting
        List<String> yaxis_rotationFrontList = new ArrayList<>();
        yaxis_rotationFrontList = columnData.get("rpm");

        //since list will require to be entry type in order to be drawn, therefore, we need another list<Entry>
        List<Entry> combinedRotationList = new ArrayList<>();
        List<Entry> rotationStandardList = new ArrayList<>();

        List<String> temperatureFrontList = new ArrayList<>();
        temperatureFrontList = columnData.get("temp1");
        List<Entry> combinedTempListFront = new ArrayList<>();
        List<Entry> combinedTempStdList = new ArrayList<>();

        List<String> temperatureBackList = new ArrayList<>();
        List<String> StdTempAndVibr = new ArrayList<>();
        StdTempAndVibr = DBConnection.selectTemperatureAndVibrationById(Integer.parseInt(deviceID));
        // Check if the list contains values
        String temperature_stdStr = null, vibration_stdStr = null;
        if (StdTempAndVibr.size() >= 2) {
            // Retrieve the temperature and vibration values
            temperature_stdStr = StdTempAndVibr.get(0);
            vibration_stdStr = StdTempAndVibr.get(1);

            // Now you have the values and can use them as needed
        } else {
            Log.e(TAG, "no standards temp and vibr");
        }

        temperatureBackList = columnData.get("temp2");
        List<Entry> combinedTempListBack = new ArrayList<>();




        List<String> vibrationFrontList = new ArrayList<>();
        vibrationFrontList = columnData.get("vibr1");
        List<Entry> combinedVibrFront = new ArrayList<>();


        List<String> vibrationBackList = new ArrayList<>();
        vibrationBackList = columnData.get("vibr2");
        List<Entry> combinedVibrBack = new ArrayList<>();

        List<Entry> combinedVibrStdList = new ArrayList<>();

        // Assuming these are your desired attribute names
        String[] attributeNames = {"temp1", "temp2", "vibr1", "vibr2", "rpm"};
        // Assuming columnData.get("date_time") and value have the same size
        List<String> dates = columnData.get("date_time");

        for (int i = 0; i < dates.size(); i++) {
            String date = dates.get(i);//x value

            String value = yaxis_rotationFrontList.get(i);//y value for rotation
            String value_temperature_front_str = temperatureFrontList.get(i);//y value for temperature
            String yValue_tempBack_Str = temperatureBackList.get(i);
            String yValue_vibrFront_str = vibrationFrontList.get(i);
            String yValue_vibrBack_str = vibrationBackList.get(i);

            //x value formatting
            SimpleDateFormat dateFormat = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            }
            SimpleDateFormat floatFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            //


            float yValue, yValue_temperature, yValue_tempBack, yValue_tempStd, yValue_vibrStd, yValue_vibrFront, yValue_vibrBack;

            //each try block for one individual yvalue
            try{
                yValue = Float.parseFloat(value);
                Log.d(TAG, "parsed successfully; yValueRpm: " +  yValue);
            }catch(Exception e){
                Log.e(TAG, "parse failed for rpm");
                yValue = INVALID;
            }
            try{
                yValue_temperature = Float.parseFloat(value_temperature_front_str);
                Log.d(TAG, "parsed successfully; yValueTemp: " + yValue_temperature);
            }catch(Exception e){
                Log.e(TAG, "parse failed for yValTemp");
                yValue_temperature = INVALID;
            }
            try{
                yValue_tempBack = Float.parseFloat(yValue_tempBack_Str);
                Log.d(TAG, "parsed successfully; yValueTempBack: " + yValue_tempBack);
            }catch(Exception e){
                Log.e(TAG, "parse failed for yValTempBack");
                yValue_tempBack = INVALID;
            }

            try{
                yValue_tempStd = Float.parseFloat(temperature_stdStr);
                Log.d(TAG, "parsed successfully; yValueTempStd: " + yValue_tempStd);
            }catch(Exception e){
                Log.e(TAG, "parse failed for yValTempStd");
                yValue_tempStd = INVALID;
            }
            try{
                yValue_vibrStd = Float.parseFloat(vibration_stdStr);
                Log.d(TAG, "parsed successfully; yValueVibrStd: " + yValue_vibrStd);
            }catch(Exception e){
                Log.e(TAG, "parse failed for yValueVibrStd");
                yValue_vibrStd = INVALID;
            }
            try{
                yValue_vibrBack = Float.parseFloat(yValue_vibrBack_str);
                Log.d(TAG, "parsed successfully; yValueVibrBack: " + yValue_vibrBack);
            }catch(Exception e){
                Log.e(TAG, "parse failed for yValueVibrBack");
                yValue_vibrBack = INVALID;
            }
            try{
                yValue_vibrFront = Float.parseFloat(yValue_vibrFront_str);
                Log.d(TAG, "parsed successfully; yValue_vibrFront: " + yValue_vibrFront);
            }catch(Exception e){
                Log.e(TAG, "parse failed for yValueVibrFront");
                yValue_vibrFront = INVALID;
            }




            Date parsedDate = dateFormat.parse(date);

            //the xValue is default set to be nonnull
            float xValue = Float.parseFloat(floatFormat.format(parsedDate));

            if (value != null && date != null ) {
                Log.d(TAG, "value:" + value);
                Log.d(TAG, "xValue: " + xValue);
                combinedRotationList.add(new Entry(xValue, yValue));//
                rotationStandardList.add(new Entry(xValue, 100f));//setting up data for std list rpm

            } else {
                long timestamp = parsedDate.getTime();
                xValue = (float) timestamp;
                combinedRotationList.add(new Entry(xValue, yValue));
                rotationStandardList.add(new Entry(xValue, 100f));
                Log.e(TAG, "Null value at index " + i + xValue);
            }

            //adding formatted Entry to combined datalists
            combinedTempListFront.add(new Entry(xValue,yValue_temperature ));
            combinedTempListBack.add(new Entry(xValue, yValue_tempBack));
            combinedTempStdList.add(new Entry(xValue, yValue_tempStd));
            combinedVibrStdList.add(new Entry(xValue, yValue_vibrStd));
            combinedVibrFront.add(new Entry(xValue,  yValue_vibrFront));
            combinedVibrBack.add(new Entry(xValue, yValue_vibrBack));
        }
        LineDataSet dataSet = new LineDataSet(combinedRotationList, "转速"); // add entries to dataset

        LineDataSet dataSet_tempFront = new LineDataSet(combinedTempListFront, "前端温度");
        LineDataSet dataSet_tempBack = new LineDataSet(combinedTempListBack, "后端温度");
        LineDataSet dataSet_tempStd = new LineDataSet(combinedTempStdList, "标准温度");
        LineDataSet dataSet_VibrFront = new LineDataSet(combinedVibrFront, "前端振动");
        LineDataSet dataSet_VibrBack = new LineDataSet(combinedVibrBack, "后端振动");
        LineDataSet dataSet_vibrStd = new LineDataSet(combinedVibrStdList, "标准振动");


        dataSet.setColor(Color.BLACK);

        dataSet_tempFront.setColor(Color.RED);
        dataSet_tempBack.setColor(Color.BLUE);
        dataSet_tempStd.setColor(Color.BLACK);
        dataSet_vibrStd.setColor(Color.BLACK);
        dataSet_VibrFront.setColor(Color.RED);
        dataSet_VibrBack.setColor(Color.BLUE);

        //these are LineData collections for first chart
        LineData lineData = new LineData();
        lineData.addDataSet(dataSet);



        //these are LineData collections for the second chart
        LineData lineData_2 = new LineData();
        lineData_2.addDataSet(dataSet_tempFront);
        lineData_2.addDataSet(dataSet_tempBack);
        lineData_2.addDataSet(dataSet_tempStd);


        //LINEDATA for THIRD chart
        LineData lineData_3 = new LineData();
        lineData_3.addDataSet(dataSet_vibrStd);
        lineData_3.addDataSet(dataSet_VibrFront);
        lineData_3.addDataSet(dataSet_VibrBack);

        //setup data in those charts
        lineChart1_rotation.setData(lineData);
        linechart2_temperature.setData(lineData_2);
        linechart3_vibration.setData(lineData_3);


        // Assuming startCalendar and endCalendar are already set with the selected datetimes
        // Convert startCalendar and endCalendar to timestamps (in milliseconds)
        long startTimeStamp = startCalendar.getTimeInMillis();
        long endTimeStamp = endCalendar.getTimeInMillis();
        Log.d(TAG, "start and end: " + startTimeStamp + endTimeStamp);

        // Set the LineChart's X-axis minimum and maximum values
        XAxis xAxis = lineChart1_rotation.getXAxis();
        XAxis xAxis_2 = linechart2_temperature.getXAxis();
        XAxis xAxis_3 = linechart3_vibration.getXAxis();

        xAxis.setAxisMinimum(startTimeStamp);   // Set the minimum value to the timestamp of the start datetime
        xAxis.setAxisMaximum(endTimeStamp);     // Set the maximum value to the timestamp of the end datetime
        xAxis_2.setAxisMinimum(startTimeStamp);
        xAxis_2.setAxisMaximum(endTimeStamp);
        xAxis_3.setAxisMaximum(endTimeStamp);
        xAxis_3.setAxisMinimum(startTimeStamp);



        // Create an instance of the custom XAxis formatter
        CustomXAxisValueFormatter customXAxisValueFormatter = new CustomXAxisValueFormatter();

        // Set the custom XAxis formatter to the XAxis of lineChart1_rotation
        xAxis.setValueFormatter(customXAxisValueFormatter);
        xAxis_2.setValueFormatter(customXAxisValueFormatter);
        xAxis_3.setValueFormatter(customXAxisValueFormatter);
        // Optionally, you can set other properties of the xAxis as needed
        // For example, to control the number of labels displayed, you can use setLabelCount:
        // xAxis.setLabelCount(5, true); // Adjust the label count as per your preference



        // Optionally, you can set other properties of the xAxis as needed
        // For example, to control the number of labels displayed, you can use setLabelCount:
        xAxis.setLabelCount(5, true); // Adjust the label count as per your preference
        xAxis_2.setLabelCount(5, true);
        xAxis_3.setLabelCount(5, true);
        // Finally, invalidate the chart to apply the changes
        lineChart1_rotation.invalidate();
        linechart2_temperature.invalidate();
        linechart3_vibration.invalidate();
    }

}
