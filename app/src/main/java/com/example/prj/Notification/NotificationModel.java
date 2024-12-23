package com.example.prj.Notification;

import java.util.Objects;

public class NotificationModel {
    private String timestamp;
    private String severity;
    private String roadName;

    public NotificationModel() {}

    public NotificationModel(String timestamp, String severity, String roadName) {
        this.timestamp = timestamp;
        this.severity = severity;
        this.roadName = roadName;
    }

    public String getTimestamp() { return timestamp; }

    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getSeverity() { return severity; }

    public void setSeverity(String severity) { this.severity = severity; }

    public String getRoadName() { return roadName; }

    public void setRoadName(String roadName) { this.roadName = roadName; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationModel that = (NotificationModel) o;
        return Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp);
    }
}
