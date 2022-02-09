package com.example.smartaquarium.models;

import java.io.Serializable;

public class AquariumModel implements Serializable {
    int id;
    String deviceType;
    String deviceId;

    public AquariumModel(int id, String deviceType, String deviceId) {
        this.id = id;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
