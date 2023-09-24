package com.example.monitorapp.userInterfaces.ui.home;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<List<HashMap<String, Object>>> dataList;

    public HomeViewModel() {
        dataList = new MutableLiveData<>();
    }

    public LiveData<List<HashMap<String, Object>>> getDataList() {
        return dataList;
    }

    public void updateDataList(List<HashMap<String, Object>> newDataList) {
        dataList.postValue(newDataList);
    }
}
