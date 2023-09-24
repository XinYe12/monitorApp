package com.example.monitorapp.userInterfaces.ui.errorAlert;

public class errorAlertDataModel {
    private boolean isChecked;
    private String deviceName;
    private String datetime;
    private String issues;

    public errorAlertDataModel(boolean isChecked, String deviceName, String datetime, String issues) {
        this.isChecked = isChecked;
        this.deviceName = deviceName;
        this.datetime = datetime;
        this.issues = issues;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getIssues() {
        return issues;
    }

    public void setIssues(String issues) {
        this.issues = issues;
    }
}
