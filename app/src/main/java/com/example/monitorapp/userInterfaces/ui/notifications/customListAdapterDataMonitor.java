package com.example.monitorapp.userInterfaces.ui.notifications;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.monitorapp.R;
import com.example.monitorapp.scatterPlotGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class customListAdapterDataMonitor extends BaseAdapter {
    private Context context;
    private List<HashMap<String, Object>> dataList;
    private final static String TAG = "notificationfragment-adapter";

    // Define an interface for item click callbacks
    public interface OnItemClickListener {
        void onItemClick(String deviceId);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    static class ViewHolder {
        TextView textView1;
        TextView textView2;
        TextView textView3;
        ListView myListView;
    }

    public customListAdapterDataMonitor(Context context, List<HashMap<String, Object>> dataList) {
        this.context = context;
        if (dataList != null) {
            this.dataList = dataList;
        } else {
            this.dataList = new ArrayList<>(); // Initialize with an empty list if dataList is null
        }
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
    public int getViewTypeCount() {
        return 2; // We have two types of views: default row and data row
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 0 : 1; // First item is the default row, others are data rows
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        int viewType = getItemViewType(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.fragment_item, parent, false);
            viewHolder.textView1 = convertView.findViewById(R.id.deviceNum);
            viewHolder.textView2 = convertView.findViewById(R.id.status);
            viewHolder.textView3 = convertView.findViewById(R.id.datetime);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        HashMap<String, Object> data = dataList.get(position);
        /*
         *Log.d(TAG, data.get("runorstop").toString());
         *the data inside the data.get("") is prob null,
         * */

        // Handle data row data

        if(data.get("id") != null){
            viewHolder.textView1.setText((String) data.get("id"));
            Log.d(TAG, data.get("id").toString());
            convertView.setTag(data.get("id"));
        }else{
            viewHolder.textView1.setText("N/A");
        }
        if(data.get("runorstop") != null){
            viewHolder.textView2.setText((String) data.get("runorstop"));
            Log.d(TAG, data.get("runorstop").toString());
        }else{
            Log.e(TAG, "runorstop null");
            viewHolder.textView2.setText("无数据");
        }

        if(data.get("time_device") != null){
            viewHolder.textView3.setText((String) data.get("time_device"));
            Log.d(TAG, data.get("time_device").toString());
        }else{
            viewHolder.textView3.setText("N/A");
        }




        return convertView;
    }

}
