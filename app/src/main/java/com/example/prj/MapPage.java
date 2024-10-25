package com.example.prj;

import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prj.PlaceAutoSuggestAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.IOException;
import java.util.List;

public class MapPage extends AppCompatActivity implements OnMapReadyCallback {
    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap gMap;
    private Marker currentLocationMarker;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }
        PlacesClient placesClient = Places.createClient(this);

        AutoCompleteTextView searchAddress = findViewById(R.id.search_address);
        Button searchButton = findViewById(R.id.search_button);

        searchAddress.setAdapter(new PlaceAutoSuggestAdapter(this, android.R.layout.simple_list_item_1));
        searchAddress.setOnItemClickListener((parent, view, position, id) -> {
            String address = (String) parent.getItemAtPosition(position);
            searchLocation(address);
        });

        searchAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String address = searchAddress.getText().toString();
                    searchLocation(address);
                    return true;
                }
                return false;
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = searchAddress.getText().toString();
                searchLocation(address);
            }
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.id_map);
        mapFragment.getMapAsync(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    updateMapLocation(location);
                }
            }
        };

        requestLocationUpdates();
    }
    private void searchLocation(String address) {
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addressList = geocoder.getFromLocationName(address, 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address location = addressList.get(0);
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                gMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(address)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.search_marker)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_PERMISSION_CODE);
            return;
        }
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(500); // 5 seconds
        locationRequest.setFastestInterval(500); // 1 second
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void updateMapLocation(Location location) {
        if (gMap == null) {
            return;
        }
        LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        float bearing = location.getBearing();
        if (currentLocationMarker == null) {
            currentLocationMarker = gMap.addMarker(new MarkerOptions()
                    .position(newLatLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location))
                    .title("My Location")
                    .rotation(bearing)
                    .anchor(0.5f, 0.5f));
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLatLng, 15));
        } else {
            animateMarkerTo(currentLocationMarker, newLatLng, bearing);
        }
    }

    private void animateMarkerTo(final Marker marker, final LatLng newLatLng, final float bearing) {
        final LatLng startLatLng = marker.getPosition();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(1000); // 1 second
        valueAnimator.addUpdateListener(animation -> {
            float v = animation.getAnimatedFraction();
            double lng = v * newLatLng.longitude + (1 - v) * startLatLng.longitude;
            double lat = v * newLatLng.latitude + (1 - v) * startLatLng.latitude;
            marker.setPosition(new LatLng(lat, lng));
            marker.setRotation(bearing); // Update rotation
        });
        valueAnimator.start();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates();
            } else {
                Toast.makeText(this, "Permission Denied, Please Allow Permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
}