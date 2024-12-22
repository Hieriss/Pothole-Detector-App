package com.example.prj.Map;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.prj.History.PotholeModel;
import com.example.prj.Notification.NotificationModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StorePotholes {
    private static final String PREFS_NAME = "PotholeDataPrefs";
    private static final String KEY_POTHOLE_DATA = "PotholeData";
    private static final String KEY_NOTIFICATION_DATA = "NotificationData";

    public static void savePotholeData(Context context, List<PotholeModel> potholeDataList) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        // Load existing data
        String json = sharedPreferences.getString(KEY_POTHOLE_DATA, null);
        Type type = new TypeToken<ArrayList<PotholeModel>>() {}.getType();
        List<PotholeModel> existingPotholeDataList = json == null ? new ArrayList<>() : gson.fromJson(json, type);

        // Add new data if it does not already exist
        for (PotholeModel newPothole : potholeDataList) {
            boolean exists = false;
            for (PotholeModel existingPothole : existingPotholeDataList) {
                if (existingPothole.getLatitude() == newPothole.getLatitude() &&
                        existingPothole.getLongitude() == newPothole.getLongitude()) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                existingPotholeDataList.add(newPothole);
            }
        }

        // Save updated data
        String updatedJson = gson.toJson(existingPotholeDataList);
        editor.putString(KEY_POTHOLE_DATA, updatedJson);
        editor.apply();
    }

    public static List<PotholeModel> loadPotholeData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(KEY_POTHOLE_DATA, null);
        Type type = new TypeToken<ArrayList<PotholeModel>>() {}.getType();
        return json == null ? new ArrayList<>() : gson.fromJson(json, type);
    }

    public static void saveNotificationData(Context context, List<NotificationModel> notificationDataList) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        // Load existing data
        String json = sharedPreferences.getString(KEY_NOTIFICATION_DATA, null);
        Type type = new TypeToken<ArrayList<NotificationModel>>() {}.getType();
        List<NotificationModel> existingNotificationDataList = json == null ? new ArrayList<>() : gson.fromJson(json, type);

        // Add new data if it does not already exist
        for (NotificationModel newNotification : notificationDataList) {
            boolean exists = false;
            for (NotificationModel existingNotification : existingNotificationDataList) {
                if (existingNotification.getTimestamp().equals(newNotification.getTimestamp())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                existingNotificationDataList.add(newNotification);
            }
        }

        // Save updated data
        String updatedJson = gson.toJson(existingNotificationDataList);
        editor.putString(KEY_NOTIFICATION_DATA, updatedJson);
        editor.apply();
    }

    public static List<NotificationModel> loadNotificationData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(KEY_NOTIFICATION_DATA, null);
        Type type = new TypeToken<ArrayList<NotificationModel>>() {}.getType();
        return json == null ? new ArrayList<>() : gson.fromJson(json, type);
    }
}