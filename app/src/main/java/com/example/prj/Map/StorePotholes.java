package com.example.prj.Map;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.prj.History.PotholeModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StorePotholes {
    private static final String PREFS_NAME = "PotholeDataPrefs";
    private static final String KEY_POTHOLE_DATA = "PotholeData";

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
}