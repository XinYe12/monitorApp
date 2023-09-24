package com.example.monitorapp.userInterfaces.ui.errorAlert;



import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.monitorapp.DBConnection;
import com.example.monitorapp.userInterfaces.ui.dashboard.DashboardFragment;

import java.util.List;
import java.util.Map;

public class errorAlertViewModel extends ViewModel {

    private LiveData<List<Map<String, String>>> alertsLiveData;

    public LiveData<List<Map<String, String>>> getAlertsLiveData(String company) {
        if (alertsLiveData == null) {
            alertsLiveData = DBConnection.retrieveAlertsLiveData(DashboardFragment.company);
        }
        return alertsLiveData;
    }
}