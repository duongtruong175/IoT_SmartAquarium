package com.example.smartaquarium.models;

import java.io.Serializable;

public class AquariumDataModel implements Serializable {
    int id;
    String deviceId;
    double temperature;
    double waterLevel;
    double tds;
    long timeSend;

    public AquariumDataModel(int id, String deviceId, double temperature, double waterLevel, double tds, long timeSend) {
        this.id = id;
        this.deviceId = deviceId;
        this.temperature = temperature;
        this.waterLevel = waterLevel;
        this.tds = tds;
        this.timeSend = timeSend;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(double waterLevel) {
        this.waterLevel = waterLevel;
    }

    public double getTds() {
        return tds;
    }

    public void setTds(double tds) {
        this.tds = tds;
    }

    public long getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(long timeSend) {
        this.timeSend = timeSend;
    }
}
