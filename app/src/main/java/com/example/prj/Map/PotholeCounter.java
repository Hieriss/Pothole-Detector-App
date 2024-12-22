package com.example.prj.Map;

public class PotholeCounter {
    String timestamp, severity;

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

    public PotholeCounter(String timestamp, String severity) {
        this.timestamp = timestamp;
        this.severity = severity;
    }

    public PotholeCounter() {
    }
}
