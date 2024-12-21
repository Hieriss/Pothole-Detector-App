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

        // Load existing pothole data
        String existingJson = sharedPreferences.getString(KEY_POTHOLE_DATA, null);
        Type type = new TypeToken<ArrayList<PotholeModel>>() {}.getType();
        List<PotholeModel> existingPotholeDataList = existingJson == null ? new ArrayList<>() : gson.fromJson(existingJson, type);

        // Add new potholes if they have latitude and longitude and are not already saved
        for (PotholeModel newPothole : potholeDataList) {
            if (newPothole.getLatitude() != 0 && newPothole.getLongitude() != 0 && !existingPotholeDataList.contains(newPothole)) {
                existingPotholeDataList.add(newPothole);
            }
        }

        // Save the updated list
        String json = gson.toJson(existingPotholeDataList);
        editor.putString(KEY_POTHOLE_DATA, json);
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
