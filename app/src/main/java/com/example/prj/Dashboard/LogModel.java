package com.example.prj.Dashboard;

import java.util.Objects;

public class LogModel {
    private String timestamp;
    private String severity;

    public LogModel() {}

    public LogModel(String timestamp, String severity) {
        this.timestamp = timestamp;
        this.severity = severity;
    }

    public String getTimestamp() { return timestamp; }

    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getSeverity() { return severity; }

    public void setSeverity(String severity) { this.severity = severity; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogModel that = (LogModel) o;
        return Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp);
    }
}
