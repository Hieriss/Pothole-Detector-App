package com.example.prj.Map;

import static android.content.ContentValues.TAG;
import static com.mapbox.maps.plugin.animation.CameraAnimationsUtils.getCamera;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.addOnMapClickListener;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.getGestures;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prj.History.PotholeModel;
import com.example.prj.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.EdgeInsets;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor;
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor;
import com.mapbox.maps.plugin.Plugin;
import com.mapbox.maps.plugin.animation.MapAnimationOptions;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.compass.CompassPlugin;
import com.mapbox.maps.plugin.gestures.OnMapClickListener;
import com.mapbox.maps.plugin.locationcomponent.generated.LocationComponentSettings;
import com.mapbox.maps.plugin.scalebar.ScaleBarPlugin;
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion;
import com.mapbox.search.ui.adapter.autocomplete.PlaceAutocompleteUiAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MapViewPothole extends AppCompatActivity {
    MapView mapView;
    AppCompatButton backButton, confirmButton, declineButton;
    TextView severity, timestamp;
    String severityText, timestampText;
    private DatabaseReference database;
    private PointAnnotationManager pointAnnotationManager;
    Point fromHistory;

    private void updateCamera(Point point, Double bearing) {
        MapAnimationOptions animationOptions = new MapAnimationOptions.Builder().duration(1500L).build();
        CameraOptions cameraOptions = new CameraOptions.Builder()
                .center(point)
                .zoom(18.0)
                .bearing(bearing)
                .pitch(0.0)
                .build();
        getCamera(mapView).easeTo(cameraOptions, animationOptions);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map_view_pothole);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        database = FirebaseDatabase.getInstance().getReference();

        severity = findViewById(R.id.severity_text);
        timestamp = findViewById(R.id.timestamp_text);
        severity.setText(severityText);
        timestamp.setText(timestampText);

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mapView = findViewById(R.id.mapView);
        AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
        pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);
        pointAnnotationManager.setIconAllowOverlap(false);

        mapView.getMapboxMap().loadStyleUri(Style.OUTDOORS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                CompassPlugin compassPlugin = (CompassPlugin) mapView.getPlugin(Plugin.MAPBOX_COMPASS_PLUGIN_ID);
                if (compassPlugin != null) {
                    compassPlugin.setVisibility(false);
                    compassPlugin.setEnabled(false);
                }
                ScaleBarPlugin scaleBarPlugin = (ScaleBarPlugin) mapView.getPlugin(Plugin.MAPBOX_SCALEBAR_PLUGIN_ID);
                if (scaleBarPlugin != null) {
                    scaleBarPlugin.setEnabled(false);
                }
                mapView.getMapboxMap().setCamera(new CameraOptions.Builder().zoom(20.0).build());
                if (pointAnnotationManager != null) {
                    pointAnnotationManager.deleteAll(); // Clear existing annotations if needed
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);
                    PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                            .withTextAnchor(TextAnchor.CENTER)
                            .withIconAnchor(IconAnchor.CENTER)
                            .withIconSize(1)
                            .withIconImage(bitmap)
                            .withIconOpacity(1.0)
                            .withPoint(fromHistory);
                    pointAnnotationManager.create(pointAnnotationOptions);
                }
                updateCamera();
            }
        });

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("LATITUDE") && intent.hasExtra("LONGITUDE")) {
            double latitude = intent.getDoubleExtra("LATITUDE", 0);
            double longitude = intent.getDoubleExtra("LONGITUDE", 0);
            severityText = getIntent().getStringExtra("SEVERITY");
            timestampText = getIntent().getStringExtra("TIMESTAMP");
            fromHistory = Point.fromLngLat(longitude, latitude);
            updateCamera(fromHistory, 0.0);
        }

        confirmButton = findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<PotholeModel> potholeDataList = StorePotholes.loadPotholeData(MapViewPothole.this);
                for (PotholeModel potholeModel : potholeDataList) {
                    SensorData sensorData = new SensorData(
                            potholeModel.getCurrentX(), potholeModel.getCurrentY(), potholeModel.getCurrentZ(),
                            potholeModel.getPitch(), potholeModel.getRoll(), potholeModel.getSpeedKmh(),
                            potholeModel.getLatitude(), potholeModel.getLongitude(), potholeModel.getPoint(),
                            potholeModel.getUsername(), potholeModel.getSeverity(), potholeModel.getTimestamp()
                    );
                    Log.d(TAG, "Pushing data to Firebase: " + sensorData.toString());
                    database.child("sensorData").push().setValue(sensorData)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Data pushed successfully");
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to push data", e);
                            });
                }
                finish();
            }
        });

        declineButton = findViewById(R.id.decline_button);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear the stored pothole data
                List<PotholeModel> potholeDataList = new ArrayList<>();
                StorePotholes.savePotholeData(MapViewPothole.this, potholeDataList);
                finish();
            }
        });
    }

    private void updateCamera() {
        if (mapView != null) {
            MapAnimationOptions animationOptions = new MapAnimationOptions.Builder().duration(1500L).build();
            CameraOptions cameraOptions = new CameraOptions.Builder().zoom(15.0).build();
            getCamera(mapView).easeTo(cameraOptions, animationOptions);
        } else {
            Log.e(TAG, "MapView is null");
        }
    }
}