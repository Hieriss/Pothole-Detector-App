package com.example.prj.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

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
    private List<Quadruple<Double, Double, String, String>> locationList;
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
                    String timestamp = snapshot.child("timestamp").getValue(String.class);
                    String id = snapshot.getKey();
                    if (latitude != null && longitude != null && timestamp != null && id != null) {
                        locationList.add(new Quadruple<>(latitude, longitude, timestamp, id));
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

    private void saveLocationsToLocalStorage(List<Quadruple<Double, Double, String, String>> locations) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        StringBuilder sb = new StringBuilder();
        for (Quadruple<Double, Double, String, String> location : locations) {
            sb.append(location.first).append(",")
                    .append(location.second).append(",")
                    .append(location.third).append(",")
                    .append(location.fourth).append(";");
        }
        editor.putString(LOCATIONS_KEY, sb.toString());
        editor.apply();
    }

    public List<Quadruple<Double, Double, String, String>> getLocationsFromLocalStorage() {
        List<Quadruple<Double, Double, String, String>> locations = new ArrayList<>();
        String savedLocations = sharedPreferences.getString(LOCATIONS_KEY, "");
        if (!savedLocations.isEmpty()) {
            String[] locationPairs = savedLocations.split(";");
            for (String pair : locationPairs) {
                String[] data = pair.split(",");
                if (data.length == 4) {
                    Double latitude = Double.parseDouble(data[0]);
                    Double longitude = Double.parseDouble(data[1]);
                    String timestamp = data[2];
                    String id = data[3];
                    locations.add(new Quadruple<>(latitude, longitude, timestamp, id));
                }
            }
        }
        return locations;
    }

    public void logStoredLocations() {
        List<Quadruple<Double, Double, String, String>> locations = getLocationsFromLocalStorage();
        for (Quadruple<Double, Double, String, String> location : locations) {
            Log.d("LocationRetriever", "Latitude: " + location.first + ", Longitude: " + location.second + ", Timestamp: " + location.third + ", ID: " + location.fourth);
        }
    }

    public interface LocationCallback {
        void onLocationsRetrieved(List<Quadruple<Double, Double, String, String>> locations);
        void onError(Exception e);
    }
}