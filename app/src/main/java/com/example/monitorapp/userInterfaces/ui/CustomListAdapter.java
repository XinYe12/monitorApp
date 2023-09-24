package com.example.monitorapp.userInterfaces.ui;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;



import com.example.monitorapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CustomListAdapter extends BaseAdapter {
    private Context context;
    private final String TAG = "mysql-getCheckedItems";
    private List<HashMap<String, Object>> dataList;
    private static SparseBooleanArray checkedItems;


    static class ViewHolder {
        TextView companyNameText;
        TextView deviceItemsText;
        TextView createTimeText;
        CheckBox checkBox;
    }

    public CustomListAdapter(Context context, List<HashMap<String, Object>> dataList) {
        this.context = context;
        this.dataList = dataList;

        this.checkedItems = new SparseBooleanArray(dataList.size());

    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.companyNameText = convertView.findViewById(R.id.company_name_text);
            viewHolder.deviceItemsText = convertView.findViewById(R.id.device_items_text);
            viewHolder.createTimeText = convertView.findViewById(R.id.create_time_text);
            viewHolder.checkBox = convertView.findViewById(R.id.checkBox);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }


        HashMap<String, Object> data = dataList.get(position);
        viewHolder.companyNameText.setText((String) data.get("id"));
        viewHolder.deviceItemsText.setText((String) data.get("device id"));
        viewHolder.createTimeText.setText((String) data.get("PLC address"));

        viewHolder.checkBox.setChecked(isItemChecked(position));

        // Set the OnCheckedChangeListener for the CheckBox
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setItemChecked(position, isChecked);
                Log.d(TAG, "position: " + position + isChecked);

            }
        });

        return convertView;
    }
    public void setItemChecked(int position, boolean isChecked){
        checkedItems.put(position, isChecked);
        notifyDataSetChanged();
    }
    public boolean isItemChecked(int position){

        return checkedItems.get(position);
    }


    // Method to get checked item positions
    public SparseBooleanArray getCheckedItems() {
        Log.d(TAG, "checked items: " +checkedItems);
        return checkedItems.clone();
    }



}
