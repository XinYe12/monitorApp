package com.example.monitorapp.userInterfaces.ui.dashboard;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ThresholdsViewModel extends ViewModel {
    private final MutableLiveData<List<String>> thresholdsLiveData = new MutableLiveData<>();
    private List<String> thresholds = new ArrayList<>();

    public MutableLiveData<List<String>> getThresholdsLiveData() {
        return thresholdsLiveData;
    }

    public void setThresholds(List<String> fetchedThresholds) {
        thresholds = fetchedThresholds;
        thresholdsLiveData.postValue(thresholds);
    }

    public List<String> getThresholds() {
        return thresholds;
    }
}
