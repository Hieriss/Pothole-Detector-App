// 489 driving options
package com.example.prj.Map;

import static com.google.android.gms.common.util.CollectionUtils.listOf;
import static com.mapbox.maps.plugin.animation.CameraAnimationsUtils.getCamera;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.addOnMapClickListener;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.getGestures;
import static com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils.getLocationComponent;
import static com.mapbox.navigation.base.extensions.RouteOptionsExtensions.applyDefaultNavigationOptions;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prj.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.Bearing;
import com.mapbox.api.directions.v5.models.RouteOptions;
import com.mapbox.api.directions.v5.models.VoiceInstructions;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.bindgen.Expected;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.EdgeInsets;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.ViewAnnotationOptions;
import com.mapbox.maps.extension.observable.eventdata.CameraChangedEventData;
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
import com.mapbox.maps.plugin.compass.CompassViewImpl;
import com.mapbox.maps.plugin.delegates.listeners.OnCameraChangeListener;
import com.mapbox.maps.plugin.gestures.OnMapClickListener;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentConstants;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;
import com.mapbox.maps.plugin.locationcomponent.generated.LocationComponentSettings;
import com.mapbox.maps.plugin.scalebar.ScaleBarPlugin;
import com.mapbox.maps.viewannotation.ViewAnnotationManager;
import com.mapbox.navigation.base.formatter.DistanceFormatterOptions;
import com.mapbox.navigation.base.options.NavigationOptions;
import com.mapbox.navigation.base.route.NavigationRoute;
import com.mapbox.navigation.base.route.NavigationRouterCallback;
import com.mapbox.navigation.base.route.RouterFailure;
import com.mapbox.navigation.base.route.RouterOrigin;
import com.mapbox.navigation.base.trip.model.RouteProgress;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.navigation.core.directions.session.RoutesObserver;
import com.mapbox.navigation.core.directions.session.RoutesUpdatedResult;
import com.mapbox.navigation.core.formatter.MapboxDistanceFormatter;
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp;
import com.mapbox.navigation.core.trip.session.LocationMatcherResult;
import com.mapbox.navigation.core.trip.session.LocationObserver;
import com.mapbox.navigation.core.trip.session.RouteProgressObserver;
import com.mapbox.navigation.core.trip.session.VoiceInstructionsObserver;
import com.mapbox.navigation.ui.base.util.MapboxNavigationConsumer;
import com.mapbox.navigation.ui.maneuver.api.MapboxManeuverApi;
import com.mapbox.navigation.ui.maneuver.model.Maneuver;
import com.mapbox.navigation.ui.maneuver.model.ManeuverError;
import com.mapbox.navigation.ui.maneuver.view.MapboxManeuverView;
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider;
import com.mapbox.navigation.ui.maps.route.RouteLayerConstants;
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowApi;
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowView;
import com.mapbox.navigation.ui.maps.route.arrow.model.ArrowVisibilityChangeValue;
import com.mapbox.navigation.ui.maps.route.arrow.model.InvalidPointError;
import com.mapbox.navigation.ui.maps.route.arrow.model.RouteArrowOptions;
import com.mapbox.navigation.ui.maps.route.arrow.model.UpdateManeuverArrowValue;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView;
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineColorResources;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineError;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineUpdateValue;
import com.mapbox.navigation.ui.maps.route.line.model.RouteSetValue;
import com.mapbox.navigation.ui.voice.api.MapboxSpeechApi;
import com.mapbox.navigation.ui.voice.api.MapboxVoiceInstructionsPlayer;
import com.mapbox.navigation.ui.voice.model.SpeechAnnouncement;
import com.mapbox.navigation.ui.voice.model.SpeechError;
import com.mapbox.navigation.ui.voice.model.SpeechValue;
import com.mapbox.navigation.ui.voice.model.SpeechVolume;
import com.mapbox.navigation.ui.voice.view.MapboxSoundButton;
import com.mapbox.search.autocomplete.PlaceAutocomplete;
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion;
import com.mapbox.search.ui.adapter.autocomplete.PlaceAutocompleteUiAdapter;
import com.mapbox.search.ui.view.CommonSearchViewConfiguration;
import com.mapbox.search.ui.view.SearchResultsView;
import com.mapbox.turf.TurfMeasurement;
import com.mapbox.turf.TurfMisc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.text.SimpleDateFormat;
import java.util.Date;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.functions.Function1;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.LocationListener;

public class MapPage extends AppCompatActivity implements SensorEventListener, LocationListener {
    // view && boolean
    static MapView mapView;
    MaterialButton setRoute;
    FloatingActionButton focusLocationBtn;
    FloatingActionButton addPotholeBtn;
    private Style mapStyle;
    boolean focusLocation = true;
    private boolean isRouteActive = false;
    private boolean isVoiceInstructionsMuted = false;
    private Point searchedPoint;
    private boolean manualAddActive = false;
    Bitmap bitmap;
    private double iconSize = 1.1;
    private Handler handler = new Handler();
    private Runnable debounceRunnable = null;

    // map component
    private MapboxNavigation mapboxNavigation;
    private final NavigationLocationProvider navigationLocationProvider = new NavigationLocationProvider();
    private MapboxRouteLineView routeLineView;
    private MapboxRouteLineApi routeLineApi;
    private MapboxRouteArrowApi routeArrowApi;
    private MapboxRouteArrowView routeArrowView;
    private MapboxSpeechApi speechApi;
    private MapboxVoiceInstructionsPlayer mapboxVoiceInstructionsPlayer;
    private NavigationRoute alternativeRoute;
    private NavigationRoute selectedRoute;
    private PointAnnotationManager pointAnnotationManager;
    private MapboxManeuverView maneuverView;
    private MapboxManeuverApi maneuverApi;
    CompassViewImpl compassView;

    // search variables
    private PlaceAutocomplete placeAutocomplete;
    private SearchResultsView searchResultsView;
    private PlaceAutocompleteUiAdapter placeAutocompleteUiAdapter;
    private TextInputEditText searchET;
    private boolean ignoreNextQueryUpdate = false;

    // sensors definition
    private static final String TAG = "btb";
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor rotationVectorSensor;

    // variables for accelerometer
    private float lastX, lastY, lastZ;
    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;
    private float vibrateThreshold = 0;
    public Vibrator v;

    // variables for rotation vector sensor
    private float[] rotationMatrix = new float[9];
    private float[] orientationAngles = new float[3];
    private double pitch;
    private double roll;
    private double latitude;
    private double longitude;

    // variables for speed
    private LocationManager locationManager;
    private float speedKmh;
    private double point;

    // other sensors' variables
    private DatabaseReference database;
    private Handler camHandler = new Handler();
    private Runnable pushDataRunnable;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private List<Pair<Double, Double>> potholeLocations;
    private static final float SPEED_THRESHOLD = 0f;
    private static final float DELTA_Z_THRESHOLD = 7.0f;

    //--------------------------Navigation Register--------------------------------

    private final OnIndicatorPositionChangedListener onPositionChangedListener = new OnIndicatorPositionChangedListener() {
        @Override
        public void onIndicatorPositionChanged(Point point) {
            // Only update if a route exists
            if (isRouteActive) {
                Expected<RouteLineError, RouteLineUpdateValue> result =
                        routeLineApi.updateTraveledRouteLine(point);
                if (mapStyle != null) {
                    routeLineView.renderRouteLineUpdate(mapStyle, result);
                }
            }
        }
    };

    private final LocationObserver locationObserver = new LocationObserver() {
        @Override
        public void onNewRawLocation(@NonNull Location location) {

        }

        @Override
        public void onNewLocationMatcherResult(@NonNull LocationMatcherResult locationMatcherResult) {
            Location location = locationMatcherResult.getEnhancedLocation();
            navigationLocationProvider.changePosition(location, locationMatcherResult.getKeyPoints(), null, null);
            if (focusLocation) {
                updateCamera(Point.fromLngLat(location.getLongitude(), location.getLatitude()), (double) location.getBearing());
            }
        }
    };

    private final RoutesObserver routesObserver = new RoutesObserver() {
        @Override
        public void onRoutesChanged(@NonNull RoutesUpdatedResult routesUpdatedResult) {
            routeLineApi.setNavigationRoutes(routesUpdatedResult.getNavigationRoutes(), new MapboxNavigationConsumer<Expected<RouteLineError, RouteSetValue>>() {
                @Override
                public void accept(Expected<RouteLineError, RouteSetValue> routeLineErrorRouteSetValueExpected) {
                    if (mapStyle != null) {
                        routeLineView.renderRouteDrawData(mapStyle, routeLineErrorRouteSetValueExpected);
                    }
                }
            });

        }
    };

    private final RouteProgressObserver routeProgressObserver = new RouteProgressObserver() {
        @Override
        public void onRouteProgressChanged(@NonNull RouteProgress routeProgress) {
            routeLineApi.updateWithRouteProgress(routeProgress, new MapboxNavigationConsumer<Expected<RouteLineError, RouteLineUpdateValue>>() {
                @Override
                public void accept(Expected<RouteLineError, RouteLineUpdateValue> result) {
                    if (mapStyle != null) {
                        routeLineView.renderRouteLineUpdate(mapStyle, result);
                    }
                }
            });
            Expected<InvalidPointError, UpdateManeuverArrowValue> updatedManeuverArrow = routeArrowApi.addUpcomingManeuverArrow(routeProgress);
            routeArrowView.renderManeuverUpdate(mapStyle, updatedManeuverArrow);

            // maneuver
            maneuverApi.getManeuvers(routeProgress).fold(new Expected.Transformer<ManeuverError, Object>() {
                @NonNull
                @Override
                public Object invoke(@NonNull ManeuverError input) {
                    return new Object();
                }
            }, new Expected.Transformer<List<Maneuver>, Object>() {
                @NonNull
                @Override
                public Object invoke(@NonNull List<Maneuver> input) {
                    maneuverView.setVisibility(View.VISIBLE);
                    maneuverView.renderManeuvers(maneuverApi.getManeuvers(routeProgress));
                    return new Object();
                }
            });
        }
    };

    //---------------------------------------------------------------------------------
    private void fetchRoadName(Point point, final RoadNameCallback callback) {
        MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder()
                .accessToken(getString(R.string.mapbox_access_token))
                .query(Point.fromLngLat(point.longitude(), point.latitude()))
                .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                .build();

        mapboxGeocoding.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                if (response.body() != null) {
                    Log.d("GeocodingResponse", "Response: " + response.body().toString());
                    if (!response.body().features().isEmpty()) {
                        String roadName = response.body().features().get(0).placeName();
                        callback.onRoadNameFetched(roadName);
                    } else {
                        callback.onRoadNameFetched("Unknown road");
                    }
                } else {
                    Log.d("GeocodingResponse", "Response body is null");
                    callback.onRoadNameFetched("Unknown road");
                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable t) {
                Log.e("GeocodingResponse", "Error: " + t.getMessage());
                callback.onRoadNameFetched("Unknown road");
            }
        });
    }

    interface RoadNameCallback {
        void onRoadNameFetched(String roadName);
    }

    private void showNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "nearby_annotation_channel";
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Nearby Annotation", NotificationManager.IMPORTANCE_HIGH);
            channel.setSound(soundUri, null);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, MapPage.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.pothole_on_map)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(soundUri);

        notificationManager.notify(1, builder.build());
    }

    private boolean isPointOnRoute(Point point, NavigationRoute route) {
        LineString routeLineString = LineString.fromPolyline(route.getDirectionsRoute().geometry(), 6);
        Point nearestPoint = (Point) TurfMisc.nearestPointOnLine(point, routeLineString.coordinates()).geometry();
        double distance = TurfMeasurement.distance(point, nearestPoint);
        // Define a threshold distance (in kilometers) to consider the point as being on the route
        double thresholdDistance = 0.01; // 10 meters
        return distance < thresholdDistance;
    }

    private void updateCamera(Point point, Double bearing) {
        MapAnimationOptions animationOptions = new MapAnimationOptions.Builder().duration(1500L).build();
        CameraOptions cameraOptions = new CameraOptions.Builder().center(point).zoom(18.0).bearing(bearing).pitch(0.0)
                .padding(new EdgeInsets(1000.0, 0.0, 0.0, 0.0)).build();

        getCamera(mapView).easeTo(cameraOptions, animationOptions);
    }
    private final OnMoveListener onMoveListener = new OnMoveListener() {
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
            focusLocation = false;
            getGestures(mapView).removeOnMoveListener(this);
            focusLocationBtn.show();
            for (Pair<Double, Double> location : potholeLocations) {
                Point point = Point.fromLngLat(location.second, location.first);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pothole_on_map);
                PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                        .withTextAnchor(TextAnchor.CENTER)
                        .withIconAnchor(IconAnchor.CENTER)
                        .withIconSize(iconSize)
                        .withIconOpacity(0.95)
                        .withIconImage(bitmap)
                        .withPoint(point);
                pointAnnotationManager.create(pointAnnotationOptions);
            }
        }

        @Override
        public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
            return false;
        }

        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {

        }
    };
    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }
    });

    private MapboxNavigationConsumer<Expected<SpeechError, SpeechValue>> speechCallback = new MapboxNavigationConsumer<Expected<SpeechError, SpeechValue>>() {
        @Override
        public void accept(Expected<SpeechError, SpeechValue> speechErrorSpeechValueExpected) {
            speechErrorSpeechValueExpected.fold(new Expected.Transformer<SpeechError, Unit>() {
                @NonNull
                @Override
                public Unit invoke(@NonNull SpeechError input) {
                    mapboxVoiceInstructionsPlayer.play(input.getFallback(), voiceInstructionsPlayerCallback);
                    return Unit.INSTANCE;
                }
            }, new Expected.Transformer<SpeechValue, Unit>() {
                @NonNull
                @Override
                public Unit invoke(@NonNull SpeechValue input) {
                    mapboxVoiceInstructionsPlayer.play(input.getAnnouncement(), voiceInstructionsPlayerCallback);
                    return Unit.INSTANCE;
                }
            });
        }
    };

    private MapboxNavigationConsumer<SpeechAnnouncement> voiceInstructionsPlayerCallback = new MapboxNavigationConsumer<SpeechAnnouncement>() {
        @Override
        public void accept(SpeechAnnouncement speechAnnouncement) {
            speechApi.clean(speechAnnouncement);
        }
    };

    VoiceInstructionsObserver voiceInstructionsObserver = new VoiceInstructionsObserver() {
        @Override
        public void onNewVoiceInstructions(@NonNull VoiceInstructions voiceInstructions) {
            speechApi.generate(voiceInstructions, speechCallback);
        }
    };

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

        // Initialize Firebase
        if (database == null) {
            FirebaseDatabase databaseInstance = FirebaseDatabase.getInstance();
            //databaseInstance.setPersistenceEnabled(true);
            database = databaseInstance.getReference();
            Log.d(TAG, "Firebase initialized");
        }

        // Initialize potholeLocations
        potholeLocations = new ArrayList<>();

        // Initialize LocationRetriever and retrieve locations from Firebase
        LocationRetriever locationRetriever = new LocationRetriever(this);
        locationRetriever.retrieveLocations(new LocationRetriever.LocationCallback() {
            @Override
            public void onLocationsRetrieved(List<Pair<Double, Double>> locations) {
                // Log the retrieved locations
                if (locations.isEmpty()) {
                    Log.d(TAG, "No locations retrieved from local storage.");
                } else {
                    Log.d(TAG, "Retrieved " + locations.size() + " locations from local storage.");
                    for (Pair<Double, Double> location : locations) {
                        Log.d(TAG, "Latitude: " + location.first + ", Longitude: " + location.second);
                    }
                }

                // get locations
                potholeLocations = locations;
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Failed to retrieve locations", e);
            }
        });

        // Compass
        compassView = findViewById(R.id.compass_icon);
        compassView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetMapBearing();
            }
        });

        // Initialize addPotholeBtn
        addPotholeBtn = findViewById(R.id.debug_detail_point);
        addPotholeBtn.setVisibility(View.GONE);

        // Initialize LocationManager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }

        // Request location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500L, 0.5F, this);

        // sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            Log.d(TAG, "Success! we have an accelerometer");
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            vibrateThreshold = accelerometer.getMaximumRange() / 2;
        } else {
            Log.e(TAG, "Failed. Unfortunately we do not have an accelerometer");
        }

        // Initialize vibration
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        // Initialize rotation vector sensor
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null) {
            rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Log.e(TAG, "Failed. Unfortunately we do not have a rotation vector sensor");
        }

        // Initialize the Runnable to push data
        pushDataRunnable = new Runnable() {
            @Override
            public void run() {
                pushData();
                // Schedule the next update after 1 second
                handler.postDelayed(this, 1000);
            }
        };

        // Start the periodic data push
        handler.post(pushDataRunnable);

        // route line
        mapView = findViewById(R.id.mapView);
        focusLocationBtn = findViewById(R.id.focus_location_button);
        setRoute = findViewById(R.id.route_button);
        //setRoute.setVisibility(View.GONE);

        // route line color resources
        RouteLineColorResources routeLineColorResources = new RouteLineColorResources.Builder()
//                .routeDefaultColor(Color.TRANSPARENT)
//                .routeCasingColor(Color.TRANSPARENT)
                .routeLineTraveledColor(Color.GRAY)
                .routeLineTraveledCasingColor(Color.DKGRAY)  //mau vien
                .routeUnknownCongestionColor(Color.TRANSPARENT)
                .routeLowCongestionColor(Color.TRANSPARENT)
                .routeModerateCongestionColor(Color.TRANSPARENT)
                .routeSevereCongestionColor(Color.TRANSPARENT)
                .build();

        // route line resources
        RouteLineResources routeLineResources = new RouteLineResources.Builder()
                .routeLineColorResources(routeLineColorResources)
                .build();

        // route line options
        MapboxRouteLineOptions lineOptions = new MapboxRouteLineOptions.Builder(this)
                .withVanishingRouteLineEnabled(true)
                .withRouteLineResources(routeLineResources)
                .withRouteLineBelowLayerId(LocationComponentConstants.LOCATION_INDICATOR_LAYER)
                .build();
        routeLineView = new MapboxRouteLineView(lineOptions);
        routeLineApi = new MapboxRouteLineApi(lineOptions);

        // arrow on route line
        routeArrowApi = new MapboxRouteArrowApi();
        RouteArrowOptions arrowOptions = new RouteArrowOptions.Builder(this)
                .withAboveLayerId(RouteLayerConstants.TOP_LEVEL_ROUTE_LINE_LAYER_ID)
                .build();
        routeArrowView = new MapboxRouteArrowView(arrowOptions);

        // speech
        speechApi = new MapboxSpeechApi(MapPage.this, getString(R.string.mapbox_access_token), Locale.US.toLanguageTag());
        mapboxVoiceInstructionsPlayer = new MapboxVoiceInstructionsPlayer(MapPage.this, Locale.US.toLanguageTag());

        // register
        NavigationOptions navigationOptions = new NavigationOptions.Builder(this).accessToken(getString(R.string.mapbox_access_token)).build();
        MapboxNavigationApp.setup(navigationOptions);
        mapboxNavigation = new MapboxNavigation(navigationOptions);
        mapboxNavigation.registerRoutesObserver(routesObserver);
        mapboxNavigation.registerRouteProgressObserver(routeProgressObserver);
        mapboxNavigation.registerLocationObserver(locationObserver);
        mapboxNavigation.registerVoiceInstructionsObserver(voiceInstructionsObserver);

        // sound button
        MapboxSoundButton soundButton = findViewById(R.id.soundButton);
        soundButton.unmute();
        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isVoiceInstructionsMuted = !isVoiceInstructionsMuted;
                if (isVoiceInstructionsMuted) {
                    soundButton.muteAndExtend(1500L);
                    mapboxVoiceInstructionsPlayer.volume(new SpeechVolume(0f));
                } else {
                    soundButton.unmuteAndExtend(1500L);
                    mapboxVoiceInstructionsPlayer.volume(new SpeechVolume(1f));
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(MapPage.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                activityResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        if (ActivityCompat.checkSelfPermission(MapPage.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MapPage.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            activityResultLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
        } else {
            mapboxNavigation.startTripSession();
        }

        focusLocationBtn.hide();
        // location plugin
        LocationComponentPlugin locationComponentPlugin = getLocationComponent(mapView);
        locationComponentPlugin.addOnIndicatorPositionChangedListener(onPositionChangedListener);
        getGestures(mapView).addOnMoveListener(onMoveListener);

        setRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MapPage.this, "Please select a location in map", Toast.LENGTH_SHORT).show();
            }
        });

        // search
        placeAutocomplete = PlaceAutocomplete.create(getString(R.string.mapbox_access_token));
        searchET = findViewById(R.id.search_bar_text);
        searchResultsView = findViewById(R.id.search_results_view);
        searchResultsView.initialize(new SearchResultsView.Configuration(new CommonSearchViewConfiguration()));
        placeAutocompleteUiAdapter = new PlaceAutocompleteUiAdapter(searchResultsView, placeAutocomplete, LocationEngineProvider.getBestLocationEngine(MapPage.this));

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (ignoreNextQueryUpdate) {
                    ignoreNextQueryUpdate = false;
                } else {
                    placeAutocompleteUiAdapter.search(charSequence.toString(), new Continuation<Unit>() {
                        @NonNull
                        @Override
                        public CoroutineContext getContext() {
                            return EmptyCoroutineContext.INSTANCE;
                        }

                        @Override
                        public void resumeWith(@NonNull Object o) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    searchResultsView.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mapView.getMapboxMap().loadStyleUri(Style.OUTDOORS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                mapStyle = style;
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
                locationComponentPlugin.setEnabled(true);
                locationComponentPlugin.setLocationProvider(navigationLocationProvider);
                getGestures(mapView).addOnMoveListener(onMoveListener);
                locationComponentPlugin.updateSettings(new Function1<LocationComponentSettings, Unit>() {
                    @Override
                    public Unit invoke(LocationComponentSettings locationComponentSettings) {
                        locationComponentSettings.setEnabled(true);
                        locationComponentSettings.setPulsingEnabled(true);
                        return null;
                    }
                });
                AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
                pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);
                pointAnnotationManager.setIconAllowOverlap(false);

                for (Pair<Double, Double> location : potholeLocations) {
                    Point point = Point.fromLngLat(location.second, location.first);
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pothole_on_map);
                    PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                            .withTextAnchor(TextAnchor.CENTER)
                            .withIconAnchor(IconAnchor.CENTER)
                            .withIconSize(iconSize)
                            .withIconOpacity(0.95)
                            .withIconImage(bitmap)
                            .withPoint(point);
                    pointAnnotationManager.create(pointAnnotationOptions);
                }

                addOnMapClickListener(mapView.getMapboxMap(), new OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull Point point) {
                        if (!isRouteActive) {
                            if (manualAddActive) {
                                bitmap =  BitmapFactory.decodeResource(getResources(), R.drawable.pothole_on_map);
                                PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                                        .withTextAnchor(TextAnchor.CENTER)
                                        .withIconAnchor(IconAnchor.CENTER)
                                        .withIconSize(iconSize)  // change later
                                        .withIconOpacity(0.95)
                                        .withIconImage(bitmap)
                                        .withPoint(point);
                                pointAnnotationManager.create(pointAnnotationOptions);
                            }
                            else {
                                mapboxNavigation.setNavigationRoutes(Collections.emptyList());
                                List<PointAnnotation> annotations = pointAnnotationManager.getAnnotations();
                                for (PointAnnotation annotation : annotations) {
                                    if (annotation.getIconOpacity() != 0.95) {
                                        pointAnnotationManager.delete(annotation);
                                    }
                                }
                                bitmap =  BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);
                                PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                                        .withTextAnchor(TextAnchor.CENTER)
                                        .withIconAnchor(IconAnchor.CENTER)
                                        .withIconSize(1)
                                        .withIconImage(bitmap)
                                        .withIconOpacity(1.0)
                                        .withDraggable(true)
                                        .withPoint(point);
                                pointAnnotationManager.create(pointAnnotationOptions);
                            }
                        }

                        // view point detail
                        pointAnnotationManager.addClickListener(new OnPointAnnotationClickListener() {
                            @Override
                            public boolean onAnnotationClick(@NonNull PointAnnotation pointAnnotation) {
                                if (pointAnnotation.getIconOpacity() == 0.95) {
                                    showModalPopup(pointAnnotation.getPoint());
                                    return true;
                                }
                                return false;
                            }
                        });

                        // set route button
                        setRoute.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!isRouteActive) {
                                    if (!manualAddActive) {
                                        fetchRoute(point);
                                    } else {
                                        Toast.makeText(MapPage.this, "Turn off debug add pothole", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    isRouteActive = false;
                                    maneuverView.setVisibility(View.GONE);
                                    searchET.setVisibility(View.VISIBLE);
                                    mapboxNavigation.setNavigationRoutes(Collections.emptyList());
                                    ArrowVisibilityChangeValue tmp = routeArrowApi.hideManeuverArrow();
                                    routeArrowView.render(mapStyle, tmp);
                                    setRoute.setText("Set route");
                                }
                            }
                        });
                        return false;
                    }
                });
                // add pothole button (use to change boolean)
                addPotholeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isRouteActive) {
                            if (!manualAddActive) {
                                pointAnnotationManager.deleteAll();
                                manualAddActive = true;
                            } else {
                                //pointAnnotationManager.deleteAll();
                                manualAddActive = false;
                            }
                        }
                    }
                });

                // focus button
                focusLocationBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        focusLocation = true;
                        getGestures(mapView).addOnMoveListener(onMoveListener);
                        focusLocationBtn.hide();
                    }
                });

                // search
                placeAutocompleteUiAdapter.addSearchListener(new PlaceAutocompleteUiAdapter.SearchListener() {
                    @Override
                    public void onSuggestionsShown(@NonNull List<PlaceAutocompleteSuggestion> list) {

                    }

                    @Override
                    public void onSuggestionSelected(@NonNull PlaceAutocompleteSuggestion placeAutocompleteSuggestion) {
                        ignoreNextQueryUpdate = true;
                        focusLocation = false;
                        searchET.setText(placeAutocompleteSuggestion.getName());
                        searchResultsView.setVisibility(View.GONE);
                        searchedPoint = placeAutocompleteSuggestion.getCoordinate();

                        List<PointAnnotation> annotations = pointAnnotationManager.getAnnotations();
                        for (PointAnnotation annotation : annotations) {
                            if (annotation.getIconOpacity() != 0.95) {
                                pointAnnotationManager.delete(annotation);
                            }
                        }

                        // Add new point on map
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);
                        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                                .withTextAnchor(TextAnchor.CENTER)
                                .withIconAnchor(IconAnchor.CENTER)
                                .withIconSize(1)
                                .withIconImage(bitmap)
                                .withIconOpacity(1.0)
                                .withPoint(searchedPoint);
                        pointAnnotationManager.create(pointAnnotationOptions);

                        updateCamera(searchedPoint, 0.0);

                        setRoute.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!isRouteActive)
                                    fetchRoute(searchedPoint);
                                else {
                                    isRouteActive = false;
                                    maneuverView.setVisibility(View.GONE);
                                    searchET.setVisibility(View.VISIBLE);
                                    mapboxNavigation.setNavigationRoutes(Collections.emptyList());
                                    ArrowVisibilityChangeValue tmp = routeArrowApi.hideManeuverArrow();
                                    routeArrowView.render(mapStyle, tmp);
                                    setRoute.setText("Set route");
                                }
                            }
                        });
                    }

                    @Override
                    public void onPopulateQueryClick(@NonNull PlaceAutocompleteSuggestion placeAutocompleteSuggestion) {
                        //queryEditText.setText(placeAutocompleteSuggestion.getName());
                    }

                    @Override
                    public void onError(@NonNull Exception e) {

                    }
                });


            }
        });



        mapView.getMapboxMap().addOnCameraChangeListener(new OnCameraChangeListener() {
            @Override
            public void onCameraChanged(@NonNull CameraChangedEventData cameraChangedEventData) {
                if (debounceRunnable != null) {
                    camHandler.removeCallbacks(debounceRunnable);
                }
                debounceRunnable = new Runnable() {
                    @Override
                    public void run() {
                        double zoomLevel = mapView.getMapboxMap().getCameraState().getZoom();
                        if (zoomLevel > 15 && zoomLevel < 17) {
                            changeIconSize(0.6f);
                        } else if (zoomLevel <= 15) {
                            changeIconSize(0.3f);
                        } else {
                            changeIconSize(1.1f);
                        }
                    }
                };
                camHandler.postDelayed(debounceRunnable, 300); // Adjust the delay as needed
            }
        });

        // maneuver
        maneuverView = findViewById(R.id.maneuverView);
        maneuverApi = new MapboxManeuverApi(new MapboxDistanceFormatter(new DistanceFormatterOptions.Builder(MapPage.this).build()));
        routeArrowView = new MapboxRouteArrowView(new RouteArrowOptions.Builder(MapPage.this).build());
    }
    private void showModalPopup(Point point) {
        fetchRoadName(point, new RoadNameCallback() {
            @Override
            public void onRoadNameFetched(String roadName) {
                Dialog dialog = new Dialog(MapPage.this);
                dialog.setContentView(R.layout.modal_popup);
                dialog.setCancelable(true);

                TextView roadNameTextView = dialog.findViewById(R.id.road_name_text_view);
                roadNameTextView.setText(roadName);

                dialog.show();
            }
        });
    }
    private void resetMapBearing() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }

        LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(this);
        locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult result) {
                Location location = result.getLastLocation();
                if (location != null) {
                    Point currentLocation = Point.fromLngLat(location.getLongitude(), location.getLatitude());
                    updateCamera(currentLocation, 0.0);
                }
            }

            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e(TAG, "Failed to get current location", exception);
            }
        });
    }
    private void animateIconSizeChange(final PointAnnotation annotation, final float startSize, final float endSize) {
        ValueAnimator animator = ValueAnimator.ofFloat((float) startSize, (float) endSize);
        animator.setDuration(300); // Duration of the animation in milliseconds
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                annotation.setIconSize((double) animatedValue);
                pointAnnotationManager.update(annotation);
            }
        });
        animator.start();
    }
    private void changeIconSize(float iconSize) {
        if (pointAnnotationManager != null) {
            List<PointAnnotation> annotations = pointAnnotationManager.getAnnotations();
            for (PointAnnotation annotation : annotations) {
                if (annotation.getIconOpacity() == 0.95) { // Check if it's a pothole point
                    double currentSize = annotation.getIconSize();
                    animateIconSizeChange(annotation, (float) currentSize, iconSize);
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void fetchRoute(Point point) {
        LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(MapPage.this);
        locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult result) {
                Location location = result.getLastLocation();
                setRoute.setEnabled(false);
                setRoute.setText("Fetching route...");
                RouteOptions.Builder builder = RouteOptions.builder();
                Point origin = Point.fromLngLat(Objects.requireNonNull(location).getLongitude(), location.getLatitude());
                builder.coordinatesList(Arrays.asList(origin, point));
                builder.alternatives(true); // alternative
                builder.profile(DirectionsCriteria.PROFILE_DRIVING); // driving options
                builder.bearingsList(Arrays.asList(Bearing.builder().angle(location.getBearing()).degrees(45.0).build(), null));
                applyDefaultNavigationOptions(builder);

                mapboxNavigation.requestRoutes(builder.build(), new NavigationRouterCallback() {
                    @Override
                    public void onRoutesReady(@NonNull List<NavigationRoute> list, @NonNull RouterOrigin routerOrigin) {
                        mapboxNavigation.setNavigationRoutes(list);
                        selectedRoute = list.get(0);
                        focusLocationBtn.performClick();
                        setRoute.setEnabled(true);
                        setRoute.setText("Stop route");
                        searchET.setVisibility(View.GONE);
                        isRouteActive = true;

                        // click on map to change to alternative route
                        addOnMapClickListener(mapView.getMapboxMap(), new OnMapClickListener() {
                            @Override
                            public boolean onMapClick(@NonNull Point point) {
                                if (isRouteActive) {
                                    int index=1;
                                    if (list.size() > 1) {
                                        for (int i = 1; i < list.size(); i++) {
                                            alternativeRoute = list.get(i);
                                            if (isPointOnRoute(point, alternativeRoute)) {
                                                index = i;
                                                break;
                                            }
                                        }
                                        list.remove(index);
                                        list.add(0, alternativeRoute);
                                        selectedRoute = list.get(0);
                                    }
                                    mapboxNavigation.setNavigationRoutes(list);
                                    return false;
                                }
                                return true;
                            }
                        });

                        // set route btn
                        setRoute.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (isRouteActive) {
                                    mapboxNavigation.setNavigationRoutes(Collections.emptyList());
                                    ArrowVisibilityChangeValue tmp = routeArrowApi.hideManeuverArrow();
                                    routeArrowView.render(mapStyle, tmp);
                                    setRoute.setText("Set route");
                                    isRouteActive = false;
                                    maneuverView.setVisibility(View.GONE);
                                    searchET.setVisibility(View.VISIBLE);
                                } else {
                                    fetchRoute(searchedPoint);
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NonNull List<RouterFailure> list, @NonNull RouteOptions routeOptions) {
                        setRoute.setEnabled(true);
                        setRoute.setText("Set route");
                        Toast.makeText(MapPage.this, "Route request failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCanceled(@NonNull RouteOptions routeOptions, @NonNull RouterOrigin routerOrigin) {

                    }
                });
            }

            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, accelerometer);
        sensorManager.unregisterListener(this, rotationVectorSensor);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mapboxNavigation != null){
            mapboxNavigation.onDestroy();
            mapboxNavigation = null;
        }

        handler.removeCallbacks(pushDataRunnable);
        LocationComponentPlugin locationComponentPlugin = getLocationComponent(mapView);
        locationComponentPlugin.removeOnIndicatorPositionChangedListener(onPositionChangedListener);
    }

    // sensors
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Handle accelerometer data
            deltaX = Math.abs(lastX - event.values[0]);
            deltaY = Math.abs(lastY - event.values[1]);
            deltaZ = Math.abs(lastZ - event.values[2]);

            if (deltaX < 2) deltaX = 0;
            if (deltaY < 2) deltaY = 0;
            if (deltaZ < 2) deltaZ = 0;

            lastX = event.values[0];
            lastY = event.values[1];
            lastZ = event.values[2];

            vibrate();
        } else if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // Handle rotation vector data
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            SensorManager.getOrientation(rotationMatrix, orientationAngles);

            pitch = (double) Math.toDegrees(orientationAngles[1]);
            roll = (double) Math.toDegrees(orientationAngles[2]);

            // Convert pitch
            if (pitch < 0) {
                pitch += 360;
                pitch = Math.abs(pitch - 360);
            }

            // Convert roll
            if (roll < 0) {
                roll += 360;
                roll = Math.abs(roll -360);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (pointAnnotationManager == null) {
            Log.e(TAG, "PointAnnotationManager is not initialized");
            return;
        }

        // Get the speed in meters/second
        float speed = location.getSpeed();
        // Convert to km/h
        speedKmh = speed * 3.6f;

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        // Check distance to annotation point
        List<PointAnnotation> annotations = pointAnnotationManager.getAnnotations();
        double thresholdDistance = 0.01; // 10 meters
        if (annotations != null) {
            for (PointAnnotation annotation : annotations) {
                Point annotationPoint = annotation.getPoint();
                if (annotationPoint != null) {
                    if (annotation.getIconOpacity() == 0.95) {
                        Double distance = TurfMeasurement.distance(Point.fromLngLat(location.getLongitude(), location.getLatitude()), annotationPoint);
                        if (isRouteActive) {
                            if (isPointOnRoute(annotationPoint, selectedRoute)) {
                                if (distance < thresholdDistance) {
                                    showNotification("Pothole On Route", "There are a pothole ahead!.");
                                    break;
                                }
                            }
                        } else {
                            if (distance < thresholdDistance) {
                                showNotification("Pothole Nearby", "You are near a pothole!.");
                                break;
                            }
                        }
                    }
                }

            }
        }
    }

    private void pushData() {
        calcPoint();

        if (point != 0 && speedKmh > SPEED_THRESHOLD && deltaZ > DELTA_Z_THRESHOLD) {
            long timestamp = System.currentTimeMillis();
            Date date = new Date(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String formattedDate = sdf.format(date);

            SensorData sensorData = new SensorData(deltaX, deltaY, deltaZ, pitch, roll, speedKmh, latitude, longitude, point, formattedDate);
            Log.d(TAG, "Pushing data to Firebase: " + sensorData.toString());
            database.child("sensorData").push().setValue(sensorData)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Data pushed successfully");
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to push data", e);
                    });
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500L, 0.5F, this);
                }
            } else {
                // Permission denied, handle accordingly
                Log.e(TAG, "Location permission denied");
            }
        }
    }

    public void vibrate() {
        if ((deltaX > vibrateThreshold) || (deltaY > vibrateThreshold) || (deltaZ > vibrateThreshold)) {
            v.vibrate(50);
        }
    }

    public void calcPoint(){
        double rielZ;

        if (deltaZ != 0){
            rielZ = deltaZ * (1 / Math.cos(Math.toRadians(pitch))) * (1 / Math.cos(Math.toRadians(roll)));

            if (speedKmh <= 7){
                point = 0d;
            }
            else {
                point = speedKmh / rielZ;
            }
        } else {
            point = 0d;
        }
    }
}

// force commit