package com.example.monitorapp.userInterfaces.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NotificationsViewModel extends ViewModel {
    private MutableLiveData<String> notificationData;

    public LiveData<String> getNotificationData() {
        if (notificationData == null) {
            notificationData = new MutableLiveData<>();
            // Load your data from the database or any other source here
            // For example: notificationData.setValue("Data from the database");
        }
        return notificationData;
    }

    // You can also provide a method to update the data if needed
    public void updateNotificationData(String newData) {
        notificationData.setValue(newData);
    }
}
