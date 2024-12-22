package com.example.prj.Notification;

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
}
