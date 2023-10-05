package com.example.monitorapp.userInterfaces.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DashboardViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private LiveData<String> companyNameLiveData;

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
    public LiveData<String> getCompanyNameLiveData() {
        return companyNameLiveData;
    }

    private void retrieveCompanyName() {
        // Asynchronously retrieve the company name
        // Update companyNameLiveData when you have the data
    }

}