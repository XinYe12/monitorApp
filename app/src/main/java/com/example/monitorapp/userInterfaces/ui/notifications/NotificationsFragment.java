package com.example.monitorapp.userInterfaces.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.monitorapp.DBConnection;
import com.example.monitorapp.R;
import com.example.monitorapp.databinding.FragmentNotificationsBinding;
import com.example.monitorapp.scatterPlotGraph;
import com.example.monitorapp.userInterfaces.ui.dashboard.DashboardFragment;


import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;
    private final static String TAG = "notificationFragment";
    public  static String username;
    public  static String companyName;

    private List<HashMap<String, Object>> savedData = new ArrayList<>(); // Initialize to an empty list

    private void fetchDataAndUpdateUI(String companyName) {
        try {
            //Log.d(TAG, "company name: " + companyName);
            List<HashMap<String, Object>> data = DBConnection.getStateInfo(companyName);//this companyName is currently not parameter
            if (data != null) {
                savedData = data; // Update savedData with new data
                if (binding != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Update your ListView with the retrieved data
                            Log.d(TAG, "processing listViewDataMonitor");
                            ListView listView = binding.listViewdataMonitor;
                            customListAdapterDataMonitor adapter = new customListAdapterDataMonitor(requireContext(), data);
                            listView.setAdapter(adapter);
                            // Inside your activity's onCreate

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Object clickedID = view.getTag();

                                    Log.d(TAG, "Clicked device ID: " + clickedID);
                                    if(clickedID != null) {

                                        //start the scatterplot activity with the given id
                                        Intent intent = new Intent(getActivity(), scatterPlotGraph.class);
                                        intent.putExtra("deviceID", clickedID.toString());
                                        startActivity(intent);
                                    }else{
                                        Log.e(TAG, "no id");
                                    }
                                }
                            });

                        }
                    });
                } else {
                    Log.e(TAG, "binding null");
                }
            } else {
                // Handle the case where data is null
                Log.e(TAG, "null data");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);
        // Retrieve the username from the arguments bundle
        Bundle args = getArguments();

        username = DashboardFragment.user;
        companyName = DashboardFragment.company;
        // Now you have the username, and you can use it as needed
        //Log.d(TAG, "Received username: " + username + companyName);

        if (savedInstanceState != null) {
            // Restore saved data if available
            savedData = (List<HashMap<String, Object>>) savedInstanceState.getSerializable("savedData");
            updateUIWithData(savedData);
        } else {
            // No saved data, fetch and display fresh data
            Log.d(TAG, "no saved data");
            new Thread(() -> {
                fetchDataAndUpdateUI(companyName);
            }).start();
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("savedData", (Serializable) savedData);
    }

    private void updateUIWithData(List<HashMap<String, Object>> data) {
        if (binding != null) {
            // Update your ListView with the retrieved data
            ListView listView = binding.listViewdataMonitor;
            customListAdapterDataMonitor adapter = new customListAdapterDataMonitor(requireContext(), data);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Object clickedID = view.getTag();
                    Log.d(TAG, "Clicked device ID: " + clickedID);

                    if(clickedID != null) {

                        // Example: Start a new activity
                        Intent intent = new Intent(getActivity(), scatterPlotGraph.class);
                        intent.putExtra("deviceID", clickedID.toString());
                        startActivity(intent);
                    }else{
                        Log.e(TAG, "no id");
                    }
                }
            });
        }
    }
}

