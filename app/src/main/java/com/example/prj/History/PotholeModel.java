package com.example.prj.History;

public class PotholeModel {

    public PotholeModel() {}

    public PotholeModel(float CurrentX, float CurrentY, float CurrentZ, double Pitch, double Roll, float SpeedKmh, double Point, String Username, String Severity, double Latitude, double Longitude, String Timestamp) {
        currentX = CurrentX;
        currentY = CurrentY;
        currentZ = CurrentZ;
        pitch = Pitch;
        roll = Roll;
        speedKmh = SpeedKmh;
        point = Point;
        username = Username;
        severity = Severity;
        latitude = Latitude;
        longitude = Longitude;
        timestamp = Timestamp;
    }

    private float currentX;
    private float currentY;
    private float currentZ;
    private double pitch;
    private double roll;
    private float speedKmh;
    private double point;
    private String username;
    private String severity;
    private double latitude;
    private double longitude;
    private String timestamp;

    public float getCurrentX() { return currentX; }

    public void setCurrentX(float currentX) { this.currentX = currentX; }

    public float getCurrentY() { return currentY; }

    public void setCurrentY(float currentY) { this.currentY = currentY; }

    public float getCurrentZ() { return currentZ; }

    public void setCurrentZ(float currentZ) { this.currentZ = currentZ; }

    public double getPitch() { return pitch; }

    public void setPitch(float pitch) { this.pitch = pitch; }

    public double getRoll() { return roll; }

    public void setRoll(float roll) { this.roll = roll; }

    public float getSpeedKmh() { return speedKmh; }

    public void setSpeedKmh(float speedKmh) { this.speedKmh = speedKmh; }

    public double getPoint() { return point; }

    public void setPoint(float point) { this.point = point; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getSeverity() { return severity; }

    public void setSeverity(String severity) { this.severity = severity; }

    public double getLatitude() { return latitude; }

    public void setLatitude(double latitude) {this.latitude = latitude;}

    public double getLongitude() { return longitude; }

    public void setLongitude(double longitude) {this.longitude = longitude; }

    public String getTimestamp() { return timestamp; }

    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}