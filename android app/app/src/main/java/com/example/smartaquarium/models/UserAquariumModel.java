package com.example.smartaquarium.models;

import java.io.Serializable;

public class UserAquariumModel implements Serializable {
    int id;
    String aquariumName;
    int userId;
    String deviceId;

    public UserAquariumModel(int id, String aquariumName, int userId, String deviceId) {
        this.id = id;
        this.aquariumName = aquariumName;
        this.userId = userId;
        this.deviceId = deviceId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAquariumName() {
        return aquariumName;
    }

    public void setAquariumName(String aquariumName) {
        this.aquariumName = aquariumName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
