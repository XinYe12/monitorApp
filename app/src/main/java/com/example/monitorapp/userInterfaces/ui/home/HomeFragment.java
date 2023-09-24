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

public class HomeFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "mysql-homefragment";
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private ListView listView;
    private List<HashMap<String, Object>> listIns = new ArrayList<HashMap<String, Object>>();
    private ArrayList<String> dataList = new ArrayList<>();
    private CustomListAdapter customListAdapter;
    private reloadingFragment reloadingFragment = new reloadingFragment(); // Declare the reloadingFragment
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.add.setOnClickListener(this);
        binding.delete.setOnClickListener(this);
        binding.lookupQuery.setOnClickListener(this);
        binding.update.setOnClickListener(this);



        listView = root.findViewById(R.id.listview_devices);

        if (savedInstanceState != null) {
            // Restore data from saved instance state
            dataList = savedInstanceState.getStringArrayList("dataList");
        }

        // Set up the adapter and list data

        homeViewModel.getDataList().observe(getViewLifecycleOwner(), newDataList -> {
            if(newDataList != null){
                CustomListAdapter customListAdapter1 = new CustomListAdapter(requireContext(), newDataList);
                listView.setAdapter(customListAdapter1);
            }
        });

        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("dataList", dataList);
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

        @SuppressLint("HandlerLeak")
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                if (msg.what == 1) {
                    List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) msg.obj;
                    customListAdapter = new CustomListAdapter(requireContext(), list);
                    listView.setAdapter(customListAdapter);
                }
            }
        };

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    listIns = DBConnection.getInfo("testing");
                    Log.d(TAG, listIns.toString());
                    homeViewModel.updateDataList(listIns);
                } catch (SQLException e) {
                    Log.e(TAG, e.getMessage());
                }

                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = listIns;
                handler.sendMessage(msg);


            }
        });
        thread.start();
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
                                    DBConnection.insert(ID, deviceId, plcAddress);
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
        Log.d(TAG, "checked items: "+checkedItemPositions.toString());
        if (checkedItemPositions != null) {
            List<HashMap<String, Object>> listCopy = new ArrayList<>(listIns);  // Create a copy
            for (int i = listCopy.size() - 1; i >= 0; i--) {
                if (checkedItemPositions.get(i)) {
                    Log.d(TAG, "Item at position " + i + " is checked for deletion.");

                    HashMap<String, Object> selectedItem = listCopy.get(i);
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

            // Remove selected items from the listCopy
            for (int index : itemsToRemove) {
                listCopy.remove(index);
            }

            // Update the original list after removing items
            listIns.clear();
            listIns.addAll(listCopy);
        } else {
            Log.e(TAG, "CheckedItemPositions is null.");
        }

        // Clear the checked positions
        listView.clearChoices();

    }

}
