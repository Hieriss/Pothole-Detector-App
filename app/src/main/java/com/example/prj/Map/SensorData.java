package com.example.prj.Map;

public class SensorData {
    public float currentX;
    public float currentY;
    public float currentZ;
    public double pitch;
    public double roll;
    public float speedKmh;
    public double latitude;
    public double longitude;
    public double point;
    public String username;
    public String timestamp;

    public SensorData() {
        // Default constructor required for calls to DataSnapshot.getValue(SensorData.class)
    }

    public SensorData(float currentX, float currentY, float currentZ, double pitch, double roll, float speedKmh, double latitude, double longitude, double point, String username, String timestamp) {
        this.currentX = currentX;
        this.currentY = currentY;
        this.currentZ = currentZ;
        this.pitch = pitch;
        this.roll = roll;
        this.speedKmh = speedKmh;
        this.latitude = latitude;
        this.longitude = longitude;
        this.point = point;
        this.username = username;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "SensorData{" +
                "currentX=" + currentX +
                ", currentY=" + currentY +
                ", currentZ=" + currentZ +
                ", pitch=" + pitch +
                ", roll=" + roll +
                ", speedKmh=" + speedKmh +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", point=" + point +
                ", username=" + username +
                ", timestamp=" + timestamp +
                '}';
    }
}