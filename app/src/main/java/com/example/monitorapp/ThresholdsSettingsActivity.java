package com.example.monitorapp;



import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Result;


public class ThresholdsSettingsActivity extends AppCompatActivity {
    private final static String TAG = "mysql-thresholdSetting";
    private static int deviceID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //data initialization
        String deviceInfo = getIntent().getStringExtra("deviceInfo");   //retrieve device info from intent extra
        try{
            deviceID = Integer.parseInt(deviceInfo);
        }catch(NumberFormatException e){
            Log.e(TAG, e.getMessage());
        }
        getSupportActionBar().setTitle("设备编号："+ deviceInfo);


        Log.d(TAG, deviceInfo);
    }




    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            //Log.d(TAG, "executing edittextpreferences");
            setupEditTextPreferences();
            setupResetButtonPreference();
        }


        private void setupEditTextPreferences() {
            String[] editTextKeys = {
                    "tempreture", "fault_temp_time", "fault_temp_num",
                    "warn_temp_time", "warn_temp_num",
                    "vibration", "fault_vibr_time", "fault_vibr_num",
                    "warn_vibr_time", "warn_vibr_num",
                    "warn_time", "fault_time", "gear_num"
            };

            for (String key : editTextKeys) {
                EditTextPreference editTextPreference = findPreference(key);
                restrictOnlyNumericpad(editTextPreference);
                if (editTextPreference != null) {
                    editTextPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                        new Thread(() ->{
                            updateAttributeValue(key, newValue.toString());

                        }).start();

                        return true;
                    });
                    new Thread(()->{
                        try {
                            ResultSet resultSet = DBConnection.getDataById(deviceID);
                            //Log.d(TAG,"result set: " + resultSet.toString());
                            if(resultSet.next()){
                                String columnValue = resultSet.getString(key); // Replace key with the actual column name
                                getActivity().runOnUiThread(() -> {
                                    editTextPreference.setText(columnValue);
                                });
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }).start();

                }
            }
        }
        private void restrictOnlyNumericpad(EditTextPreference editTextPreference){
            editTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                    String stringValue = (String) newValue;
                    try {
                        // Attempt to parse the input as an integer
                        int integerValue = Integer.parseInt(stringValue);
                        // Input is a valid integer
                        return true;
                    } catch (NumberFormatException e) {
                        // Input is not a valid integer, show an error message or reset the value
                        Toast.makeText(getActivity(), "Please enter a valid integer", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            });
        }
        private void setupResetButtonPreference() {
            Preference resetButtonPreference = findPreference("reset_button");
            if (resetButtonPreference != null) {
                resetButtonPreference.setOnPreferenceClickListener(preference -> {
                    new Thread(() -> {
                        //Log.d(TAG, "reset button clicked");
                        resetPreferencesToDefault();
                    }).start();
                    return true;
                });
            }
        }
        private void updateAttributeValue(String attributeName, String newValue) {
            int deviceId = deviceID; // Replace with actual device ID
            Map<String, String> attributeUpdates = new HashMap<>();
            attributeUpdates.put(attributeName, newValue);
            DBConnection.updateAttributeValueById(deviceId, attributeUpdates);
        }
        private void resetPreferencesToDefault() {
            try {
                ResultSet resultSet = DBConnection.getDefaultDataByID(deviceID);//default results list

                if (resultSet.next()) {
                    // Define an array of keys for the preferences you want to find
                    String[] preferenceKeys = {
                            "tempreture", "fault_temp_time", "fault_temp_num",
                            "warn_temp_time", "warn_temp_num",
                            "vibration", "fault_vibr_time", "fault_vibr_num",
                            "warn_vibr_time", "warn_vibr_num",
                            "warn_time", "fault_time", "gear_num"
                    };
                    Map<String, String> attributeUpdates = new HashMap<>();
                    for (String key : preferenceKeys) {

                        EditTextPreference preference = findPreference(key);
                        if (preference != null) {
                            String defaultVal = resultSet.getString(key);
                            preference.setText(defaultVal);
                            Log.d(TAG, "so far so good");

                            Log.d(TAG, "Testing what attributes: " + defaultVal);
                            attributeUpdates.put(key, defaultVal);

                            // You can work with the 'preference' object here as needed
                        }else{
                            Log.e(TAG, "cannot find designated preference");
                        }
                    }
                    DBConnection.updateAttributeValueById(deviceID, attributeUpdates);
                }
                resultSet.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}