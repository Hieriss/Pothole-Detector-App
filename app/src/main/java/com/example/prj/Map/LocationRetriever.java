package com.example.prj.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LocationRetriever {
    private static final String PREFS_NAME = "LocationPrefs";
    private static final String LOCATIONS_KEY = "locations";
    private DatabaseReference databaseReference;
    private List<Pair<Double, Double>> locationList;
    private SharedPreferences sharedPreferences;

    public LocationRetriever(Context context) {
        databaseReference = FirebaseDatabase.getInstance().getReference("sensorData");
        locationList = new ArrayList<>();
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void retrieveLocations(final LocationCallback callback) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                locationList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Double latitude = snapshot.child("latitude").getValue(Double.class);
                    Double longitude = snapshot.child("longitude").getValue(Double.class);
                    Long timestamp = snapshot.child("timestamp").getValue(Long.class);
                    String id = snapshot.getKey();
                    if (latitude != null && longitude != null) {
                        locationList.add(new Pair<>(latitude, longitude));
                        Log.d("LocationRetriever", "ID: " + id + ", Timestamp: " + timestamp);
                    }
                }

                saveLocationsToLocalStorage(locationList);
                callback.onLocationsRetrieved(locationList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    private void saveLocationsToLocalStorage(List<Pair<Double, Double>> locations) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        StringBuilder sb = new StringBuilder();
        for (Pair<Double, Double> location : locations) {
            sb.append(location.first).append(",").append(location.second).append(";");
        }
        editor.putString(LOCATIONS_KEY, sb.toString());
        editor.apply();
    }

    public List<Pair<Double, Double>> getLocationsFromLocalStorage() {
        List<Pair<Double, Double>> locations = new ArrayList<>();
        String savedLocations = sharedPreferences.getString(LOCATIONS_KEY, "");
        if (!savedLocations.isEmpty()) {
            String[] locationPairs = savedLocations.split(";");
            for (String pair : locationPairs) {
                String[] latLng = pair.split(",");
                if (latLng.length == 2) {
                    Double latitude = Double.parseDouble(latLng[0]);
                    Double longitude = Double.parseDouble(latLng[1]);
                    locations.add(new Pair<>(latitude, longitude));
                }
            }
        }
        return locations;
    }

    public void logStoredLocations() {
        List<Pair<Double, Double>> locations = getLocationsFromLocalStorage();
        for (Pair<Double, Double> location : locations) {
            Log.d("LocationRetriever", "Latitude: " + location.first + ", Longitude: " + location.second);
        }
    }

    public interface LocationCallback {
        void onLocationsRetrieved(List<Pair<Double, Double>> locations);
        void onError(Exception e);
    }
}