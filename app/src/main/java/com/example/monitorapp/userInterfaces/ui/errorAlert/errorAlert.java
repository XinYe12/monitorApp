package com.example.monitorapp.userInterfaces.ui.errorAlert;

import android.os.Bundle;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.monitorapp.DBConnection;
import com.example.monitorapp.R;
import com.example.monitorapp.userInterfaces.ui.dashboard.DashboardFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class errorAlert extends Fragment {
    private final static String TAG = "mysql-error";
    private ListView listView;
    private CustomListAdapter adapter; // Updated adapter name
    private List<Map<String, String>> alertsDataList;
    private List<Integer> selectedItems = new ArrayList<>();


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_error_alert, container, false);

        // Initialize the ListView
        listView = view.findViewById(R.id.listView_errorAlerts);

        // Check if there's saved instance state
        if (savedInstanceState != null) {
            // Restore the alertsDataList from the saved state
            alertsDataList = (List<Map<String, String>>) savedInstanceState.getSerializable("alertsDataList");
        } else {
            // If no saved state, initialize the list
            alertsDataList = new ArrayList<>();
        }

        // Create the adapter and set it to the ListView
        adapter = new CustomListAdapter(requireContext(), alertsDataList);
        listView.setAdapter(adapter);

        // Retrieve alertsLiveData using your method
        LiveData<List<Map<String, String>>> alertsLiveData = DBConnection.retrieveAlertsLiveData(DashboardFragment.company);

        // Observe the LiveData to update the UI when data changes
        alertsLiveData.observe(getViewLifecycleOwner(), new Observer<List<Map<String, String>>>() {
            @Override
            public void onChanged(List<Map<String, String>> newData) {
                // Update the adapter with the new data
                alertsDataList.clear();
                alertsDataList.addAll(newData);
                adapter.notifyDataSetChanged();
            }
        });

        // Add an OnClickListener to the "删除警报" button
        Button btnDeleteAlert = view.findViewById(R.id.btnDeleteAlert);
        btnDeleteAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedItems = adapter.getSelectedItems();
                // Delete selected items
                deleteSelectedItems();
            }
        });

        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the alertsDataList to the instance state
        outState.putSerializable("alertsDataList", (Serializable) alertsDataList);
    }
    // Method to handle deletion of selected items
    private void deleteSelectedItems() {
        Log.d(TAG, "clicked deletion");
        if (!selectedItems.isEmpty()) {

            // Create a copy of the alertsDataList to avoid ConcurrentModificationException
            List<Map<String, String>> copyList = new ArrayList<>(alertsDataList);
            Log.d(TAG, "checked: " + copyList);
            // Create a list to store the error codes of selected items
            List<String> errorCodesToDelete = new ArrayList<>();

            // Remove selected items from the copyList and collect their error codes
            Collections.sort(selectedItems, Collections.reverseOrder()); // Sort in reverse order to avoid index issues
            // Collect error codes first
            for (int position : selectedItems) {
                if (position >= 0 && position < copyList.size()) {
                    errorCodesToDelete.add(copyList.get(position).get("id"));
                }
            }

// Remove items from copyList
            for (int position : selectedItems) {
                if (position >= 0 && position < copyList.size()) {
                    copyList.remove(position);
                }
            }


            selectedItems.clear();

            // Update the adapter with the modified data
            adapter.clear();
            adapter.addAll(copyList);
            adapter.notifyDataSetChanged();

            // Delete selected items from the database
            new Thread(()->{
                DBConnection.deleteItemsFromDatabase(errorCodesToDelete);
            }).start();

        }else{
            Log.e(TAG, "no checked item");
        }
    }


}

