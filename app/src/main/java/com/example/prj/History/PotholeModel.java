package com.example.prj.History;

public class PotholeModel {

    public PotholeModel() {}  // Needed for Firebase

    public PotholeModel(double Latitude, double Longitude, String Timestamp, String Id) {
        latitude = Latitude;
        longitude = Longitude;
        timestamp = Timestamp;
        id = Id;
    }

    private double latitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private double longitude;
    private String timestamp;
    private String id;


}
