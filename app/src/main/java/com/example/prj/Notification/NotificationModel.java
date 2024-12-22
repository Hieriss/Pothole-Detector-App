package com.example.prj.Notification;

public class NotificationModel {
        public NotificationModel() {}

        public NotificationModel(String timestamp, String severity) {
            this.timestamp = timestamp;
            this.severity = severity;
        }

        private String timestamp;
        private String severity;

        public String getTimestamp() { return timestamp; }

        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

        public String getSeverity() { return severity; }

        public void setSeverity(String severity) { this.severity = severity; }
}
