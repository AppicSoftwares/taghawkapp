package com.taghawk.model;

import com.google.gson.annotations.SerializedName;

public class CommonResponseData {

    @SerializedName("type")
    private String type;
    @SerializedName("deviceType")
    private int deviceType;
    @SerializedName("isCurrentVersion")
    private boolean isCurrentVersion;
    @SerializedName("status")
    private int status;
    @SerializedName("versionName")
    private String versionName;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public boolean isCurrentVersion() {
        return isCurrentVersion;
    }

    public void setCurrentVersion(boolean currentVersion) {
        isCurrentVersion = currentVersion;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
}
