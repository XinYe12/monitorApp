package com.example.monitorapp.userInterfaces.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.monitorapp.DBConnection;
import com.example.monitorapp.R;
import com.example.monitorapp.ThresholdsSettingsActivity;
import com.example.monitorapp.databinding.FragmentDashboardBinding;
import com.example.monitorapp.reloadingFragment;

import java.util.ArrayList;
import java.util.List;


public class DashboardFragment extends Fragment {
    private final static String TAG = "mysql-DashboardFragment";
    private static boolean oneTimeUsername = true;
    public static String company;
    public static String user;
    private static ConstraintLayout deviceContainer ;
    private View rootView;
    private FragmentDashboardBinding binding;
    private List<String> thresholdIDs;
    private boolean dataLoaded = false; // Flag to track if data has been loaded
    private ThresholdsViewModel thresholdsViewModel;
    private reloadingFragment reloadingFragment;
    private final MutableLiveData<List<String>> thresholdsLiveData = new MutableLiveData<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        thresholdsViewModel = new ViewModelProvider(this).get(ThresholdsViewModel.class);
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        if(oneTimeUsername) {
            Log.d(TAG, "oneTimeUSernaem");
            getCompanyName(new CompanyNameCallback() {
                @Override
                public void onCompanyNameLoaded(String companyName, String username) {
                    company = companyName;
                    user = username;

                    // Now that you have the company and user information, get the devices
                    getDevicesID(new ThresholdsCallback() {
                        @Override
                        public void onThresholdsLoaded(List<String> thresholds) {
                            // Process the thresholds data here
                            Log.d(TAG, "thresholds: "+thresholds.toString());

                            // Update UI elements using thresholds data
                            populateDevices(thresholds);
                        }
                    });
                }
            });
            oneTimeUsername = false;
        } else {

            Log.d(TAG, "see this msg only once");
            loadThresholds();


        }

        deviceContainer = rootView.findViewById(R.id.deviceContainer);
        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void loadThresholds() {
        if (thresholdsViewModel.getThresholds().isEmpty()) {
            Log.d(TAG, "empty");
            new Thread(() -> {
                List<String> thresholds = DBConnection.getThresholdData(company, user);
                if(getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        thresholdsViewModel.setThresholds(thresholds);
                        Log.d(TAG, "devices: "+ thresholds.toString());
                        populateDevices(thresholds);
                    });
                }else{
                    Log.e(TAG, "getActivity() null");
                }
            }).start();
        } else{
            Log.d(TAG, "not empty");
            getActivity().runOnUiThread(() -> {
                Log.d(TAG, "devices: "+ thresholdsViewModel.getThresholds().toString());
                populateDevices(thresholdsViewModel.getThresholds());
            });
        }
    }
    private void populateDevices(List<String> devices) {
        Log.d(TAG, "Devices:" + devices);

        Button previousButton = null;
        for (String device : devices) {
            Button button = new Button(getContext());
            button.setId(View.generateViewId());
            button.setText(device);
            button.setTextSize(20);
            button.setTag(device);
            // Apply the custom background drawable
            button.setBackgroundResource(R.drawable.button_background);

            // Set text color
            button.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
            );

            layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            layoutParams.setMargins(0, 40, 0, 0); // Add top margin

            if (previousButton != null) {
                layoutParams.topToBottom = previousButton.getId();
            } else {
                layoutParams.topToBottom = R.id.randomTextView;
            }

            button.setLayoutParams(layoutParams);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String deviceInfo = (String) view.getTag();
                    Intent intent = new Intent(getActivity(), ThresholdsSettingsActivity.class);
                    intent.putExtra("deviceInfo", deviceInfo);
                    startActivity(intent);
                }
            });

            deviceContainer.addView(button);
            previousButton = button;
        }
        Log.d(TAG, "List populated successfully!");
    }


    public interface ThresholdsCallback{
        void onThresholdsLoaded(List<String> thresholds);
    }
    private ThresholdsCallback thresholdsCallback = new ThresholdsCallback() {
        @Override
        public void onThresholdsLoaded(List<String> thresholds) {
            // Update UI elements using thresholds data
            //Log.d(TAG, "see this msg only once"); this is not the problem
            populateDevices(thresholds);
        }
    };

    public void getDevicesID(ThresholdsCallback callback) {
        new Thread(() -> {
            List<String> thresholds = DBConnection.getThresholdData(company, user);

            // Check if the fragment is attached to an activity
            if (getActivity() != null) {

                getActivity().runOnUiThread(() -> {
                    //not here checked
                    thresholdsCallback.onThresholdsLoaded(thresholds);
                });
            }
        }).start();
    }

    public interface CompanyNameCallback {
        void onCompanyNameLoaded(String companyName, String username);
    }

    public void getCompanyName(CompanyNameCallback callback) {
        try {
            String username = getArguments().getString("username");
            Log.d(TAG, "username: " + username);
            oneTimeUsername = false;
            new Thread(() -> {
                try {
                    String companyName = DBConnection.getCompanyName(username);
                    Log.d(TAG, companyName);
                    company = companyName;
                    user = username;

                    // Call the callback with the company name and username
                    if(getActivity()!= null) {
                        getActivity().runOnUiThread(() -> {
                            //Log.d(TAG, "is it here"); not here
                            callback.onCompanyNameLoaded(companyName, username);
                        });
                    }else{
                        Log.e(TAG, "Fragment's activity is null");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "failed get companyname: " + e.getMessage());
                }
            }).start();
        } catch (NullPointerException e) {
            Log.e(TAG, "username null" + e.getMessage());
        }
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //not here
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("thresholds", new ArrayList<>(thresholdsViewModel.getThresholds()));
    }
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            ArrayList<String> savedThresholds = savedInstanceState.getStringArrayList("thresholds");
            if (savedThresholds != null) {
                thresholdsViewModel.setThresholds(savedThresholds);
                //Log.d(TAG, "populate invoked here");
                populateDevices(thresholdsViewModel.getThresholds());
            }
        }
    }


}