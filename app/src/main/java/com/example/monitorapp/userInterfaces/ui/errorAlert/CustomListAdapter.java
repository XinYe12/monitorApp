package com.example.monitorapp.userInterfaces.ui.errorAlert;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.monitorapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomListAdapter extends ArrayAdapter<Map<String, String>> {
    private Context context;
    private static final String TAG = "mysql-checkbox";
    public static List<Integer> selectedItems = new ArrayList<>(); // To keep track of selected items

    public CustomListAdapter(Context context, List<Map<String, String>> data) {
        super(context, R.layout.error_list_item, R.id.textView1_errorDeviceID, data);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.error_list_item, parent, false);
        }

        // Bind data to your UI elements
        final Map<String, String> itemData = getItem(position);
        CheckBox checkBox = convertView.findViewById(R.id.checkbox);
        TextView deviceNameTextView = convertView.findViewById(R.id.textView1_errorDeviceID);
        TextView datetimeTextView = convertView.findViewById(R.id.textView2_errorInfo_datetime);
        TextView issuesTextView = convertView.findViewById(R.id.textView2_errorInfo_issues);

        // Populate UI elements with data
        checkBox.setChecked(false); // You can set your checkbox state here

        if (itemData != null) {
            deviceNameTextView.setText(itemData.get("device_name"));
            datetimeTextView.setText("时间：" + itemData.get("datetime"));
            issuesTextView.setText("问题：" + itemData.get("issues"));
        }

        // Handle CheckBox clicks
        // Handle CheckBox clicks
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the current checked state of the CheckBox
                boolean isChecked = ((CheckBox) v).isChecked();
                // Update the selected items list
                updateSelectedItems(position, isChecked);
                Log.d(TAG, "checked");
            }
        });

        return convertView;
    }

    // Update the selected items list when a CheckBox is checked
    private void updateSelectedItems(int position, boolean isChecked) {
        if (isChecked) {
            selectedItems.add(position);
        } else {
            selectedItems.remove(Integer.valueOf(position));
        }
    }

    // Get the list of selected items
    public List<Integer> getSelectedItems() {
        return selectedItems;
    }
}