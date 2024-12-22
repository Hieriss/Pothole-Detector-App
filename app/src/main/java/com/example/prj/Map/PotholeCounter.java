package com.example.prj.Map;

public class PotholeCounter {
    String day, timestamp, severity;

    public String getDay() {
        return day;
    }

    public void setDay(String username) {
        this.day = username;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String password) {
        this.timestamp = password;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String email) {
        this.severity = email;
    }

    public PotholeCounter(String day, String timestamp, String severity) {
        this.day = day;
        this.timestamp = timestamp;
        this.severity = severity;
    }

    public PotholeCounter() {
    }
}
