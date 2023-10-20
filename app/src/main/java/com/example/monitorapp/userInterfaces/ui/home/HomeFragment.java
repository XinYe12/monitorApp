package com.example.monitorapp.userInterfaces.ui.home;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.monitorapp.DBConnection;
import com.example.monitorapp.R;
import com.example.monitorapp.databinding.FragmentHomeBinding;
import com.example.monitorapp.reloadingFragment;

import com.example.monitorapp.userInterfaces.ui.CustomListAdapter;
import com.example.monitorapp.userInterfaces.ui.dashboard.DashboardFragment;
import com.example.monitorapp.userInterfaces.ui.dashboard.DashboardViewModel;

public class HomeFragment extends Fragment implements View.OnClickListener, DashboardFragment.CompanyNameCallback{
    private static final String TAG = "mysql-homefragment";
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private ListView listView;
    private List<HashMap<String, Object>> listIns = new ArrayList<HashMap<String, Object>>();
    private List<HashMap<String, Object>> devicesList = new ArrayList<HashMap<String, Object>>();
    private boolean dataLoaded = false;
    private CustomListAdapter customListAdapter;

    private reloadingFragment reloadingFragment = new reloadingFragment(); // Declare the reloadingFragment
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.add.setOnClickListener(this);
        binding.delete.setOnClickListener(this);
        binding.lookupQuery.setOnClickListener(this);
        listView = root.findViewById(R.id.listview_devices);

        // Check if data was loaded before
        if (dataLoaded && devicesList != null) {
            // Data was previously loaded, use it to set up the adapter
            CustomListAdapter customListAdapter1 = new CustomListAdapter(requireContext(), devicesList);
            listView.setAdapter(customListAdapter1);
        } else {
            // Data was not loaded, load it
            // Create an instance of your DashboardFragment
            DashboardFragment dashboardFragment = new DashboardFragment();
            Bundle args = new Bundle();
            args.putString("username", DashboardFragment.user);
            dashboardFragment.setArguments(args);

            // Call the getCompanyName method and pass this class as the callback
            dashboardFragment.getCompanyName(this);

        }


        // Set up the adapter and list data (assuming you want to set it up after companyName is loaded)
        homeViewModel.getDataList().observe(getViewLifecycleOwner(), newDataList -> {
            if (newDataList != null) {
                CustomListAdapter customListAdapter1 = new CustomListAdapter(requireContext(), newDataList);
                listView.setAdapter(customListAdapter1);
            }
        });

        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Store the data if it has been loaded
        if (dataLoaded && devicesList != null) {
            // Convert devicesList to a Serializable format (e.g., ArrayList)
            ArrayList<HashMap<String, Object>> serializableList = new ArrayList<>(devicesList);
            outState.putSerializable("dataList", serializableList);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add){
            showAddDialog();
            Log.d(TAG, "add dialog started");

        }else if(v.getId() == R.id.delete){
            delete();
            Log.d(TAG, "delete method started");

        }else if(v.getId() == R.id.lookup_query){
            Log.d(TAG,"lookup query method started");
            lookup();
        }else{
            Log.d(TAG, "update event started");
        }
    }


    /**
     * this is the query method for user to refresh the page to see content
     */
    private void lookup(){
        Log.d(TAG, "oncompanynameloadded called");
        new Thread(()->{
            try {
                devicesList = DBConnection.getInfo(DashboardFragment.company);
                listIns = devicesList;
                requireActivity().runOnUiThread(()->{
                    CustomListAdapter adapter = new CustomListAdapter(requireContext(), devicesList);
                    customListAdapter = new CustomListAdapter(requireContext(), devicesList);
                    listView.setAdapter(adapter);
                    Log.d(TAG, "testing adapter" + devicesList);});
                dataLoaded = true;

            } catch (SQLException e) {
                Log.e(TAG, "catched exception: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }).start();
    }
    @Override
    public void onCompanyNameLoaded(String companyName, String username) {
        // Process the loaded company name here
        Log.d(TAG, "Company Name: " + companyName);
        lookup();
    }


    /**
     * this is method popup the add-item dialog window
     */
    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_update, null);
        builder.setView(dialogView)
                .setTitle("添加设备")
                .setPositiveButton("添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the update action here
                        EditText editTextID = dialogView.findViewById(R.id.editTextID);
                        EditText editTextDeviceId = dialogView.findViewById(R.id.editTextDeviceId);
                        EditText editTextPlcAddress = dialogView.findViewById(R.id.editTextPlcAddress);

                        String ID = editTextID.getText().toString().trim();
                        String deviceId = editTextDeviceId.getText().toString().trim();
                        String plcAddress = editTextPlcAddress.getText().toString().trim();
                        @SuppressLint("HandlerLeak")
                        final Handler handler = new Handler() {
                            @Override
                            public void handleMessage(@NonNull Message msg) {
                                super.handleMessage(msg);

                                if (msg.what == 1) {
                                    List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) msg.obj;
                                    CustomListAdapter adapter = new CustomListAdapter(requireContext(), list);
                                    listView.setAdapter(adapter);
                                }
                            }
                        };
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try{
                                    DBConnection.insert(ID, deviceId, plcAddress, DashboardFragment.company);
                                    //Toast.makeText(adminDashboards.this, "Data added successfully", Toast.LENGTH_SHORT).show();
                                }catch(SQLException e){
                                    Log.e(TAG, e.getMessage());
                                    //Toast.makeText(adminDashboards.this, "Error adding data", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                        thread.start();

                        // Perform the update action using the input data
                        // You can pass this data to your update method or perform the desired action
                        // For example: updateData(companyName, deviceId, plcAddress);
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }
    /**
     * this is the delete method for user to refresh the page to see content
     */
    private void delete() {
        @SuppressLint("HandlerLeak")
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    Log.d(TAG, "handler recieved msg: "+ msg.obj.toString());
                    List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) msg.obj;
                    CustomListAdapter adapter = new CustomListAdapter(requireContext(), list);
                    listView.setAdapter(adapter);
                }
            }
        };
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    deleteCheckedRecords();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = listIns;
                handler.sendMessage(msg);

            }
        });
        thread.start();

    }

    private void deleteCheckedRecords() throws SQLException {
        SparseBooleanArray checkedItemPositions = customListAdapter.getCheckedItems();
        List<Integer> itemsToRemove = new ArrayList<>();
        Log.d(TAG, "checked items: " + checkedItemPositions.toString());

        if (checkedItemPositions != null) {
            for (int i = 0; i < listIns.size(); i++) {
                if (checkedItemPositions.get(i)) {
                    Log.d(TAG, "Item at position " + i + " is checked for deletion.");

                    HashMap<String, Object> selectedItem = listIns.get(i);
                    String id = (String) selectedItem.get("id");
                    Log.d(TAG, "Deleting record with ID: " + id);

                    try {
                        DBConnection.delete(id);
                        itemsToRemove.add(i);
                    } catch (SQLException e) {
                        Log.e(TAG, "Error deleting record with ID: " + id);
                        e.printStackTrace();
                    }
                } else {
                    Log.d(TAG, "unchecked at: " + i);
                }
            }

            // Remove selected items from the listIns
            for (int i = itemsToRemove.size() - 1; i >= 0; i--) {
                int index = itemsToRemove.get(i);
                listIns.remove(index);
            }
        } else {
            Log.e(TAG, "CheckedItemPositions is null.");
        }

        // Clear the checked positions
        customListAdapter.getCheckedItems().clear();
        customListAdapter.notifyDataSetChanged();
    }


}
