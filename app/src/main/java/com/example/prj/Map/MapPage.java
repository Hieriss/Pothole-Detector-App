// 489 driving options
package com.example.prj.Map;

import static com.google.android.gms.common.util.CollectionUtils.listOf;
import static com.mapbox.maps.plugin.animation.CameraAnimationsUtils.getCamera;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.addOnMapClickListener;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.getGestures;
import static com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils.getLocationComponent;
import static com.mapbox.navigation.base.extensions.RouteOptionsExtensions.applyDefaultNavigationOptions;

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
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prj.History.HistoryAdapter;
import com.example.prj.History.HistoryPage;
import com.example.prj.History.PotholeModel;
import com.example.prj.Notification.NotificationPage;
import com.example.prj.R;
import com.example.prj.Session.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.Bearing;
import com.mapbox.api.directions.v5.models.RouteOptions;
import com.mapbox.api.directions.v5.models.VoiceInstructions;
import com.mapbox.bindgen.Expected;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.EdgeInsets;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.extension.observable.eventdata.CameraChangedEventData;
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor;
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor;
import com.mapbox.maps.plugin.LocationPuck3D;
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
import com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;
import com.mapbox.maps.plugin.locationcomponent.generated.LocationComponentSettings;
import com.mapbox.maps.plugin.scalebar.ScaleBarPlugin;
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
import com.mapbox.navigation.ui.maps.internal.ui.LocationComponent;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.text.SimpleDateFormat;
import java.util.Date;

import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.functions.Function1;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.LocationListener;

public class MapPage extends AppCompatActivity implements SensorEventListener, LocationListener {
    // view && boolean
    static MapView mapView;
    MaterialButton setRoute;
    MaterialButton navigateBtn;
    FloatingActionButton focusLocationBtn;
    FloatingActionButton addPotholeBtn;
    FloatingActionButton filterBtn;
    private Style mapStyle;
    private boolean isRouteActive = false;
    private boolean isOnNavigation = true;
    private boolean isVoiceInstructionsMuted = false;
    private Point searchedPoint;
    private boolean manualAddActive = false;
    Bitmap bitmap;
    private double iconSize = 1.0;
    private Handler handler = new Handler();
    private Runnable debounceRunnable = null;
    private Runnable retrieveLocationsRunnable = null;
    int routeType = 2;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private Uri photoUri;
    double thresholdDistanceToNoti = 0.07; // 70 meters
    boolean lowFilter = true, mediumFilter = true, highFilter = true;

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
    private CompassViewImpl compassView;
    private RelativeLayout containterView;
    private RelativeLayout walkingView;
    private RelativeLayout cyclingView;
    private RelativeLayout drivingView;
    private RelativeLayout backBtnLayout;
    private RelativeLayout lowLayout;
    private RelativeLayout mediumLayout;
    private RelativeLayout highLayout;
    private LinearLayout searchLayout;
    private LinearLayout filterLayout;
    private AppCompatButton walkingBtn;
    private AppCompatButton cyclingBtn;
    private AppCompatButton drivingBtn;
    private AppCompatButton backBtn;
    private AppCompatButton quitBtn;
    private AppCompatButton uploadImage;
    private AppCompatButton captureImage;
    private AppCompatButton lowBtn;
    private AppCompatButton mediumBtn;
    private AppCompatButton highBtn;
    MapboxSoundButton soundButton;

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
    SessionManager sessionManager;
    private String username;
    double rielZ;
    private Runnable pushDataRunnable;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private List<Penaldo<Double, Double, String, String, String>> potholeLocations = new ArrayList<>();
    private List<Penaldo<Double, Double, String, String, String>> potholeTracking = new ArrayList<>();
    private static final float SPEED_THRESHOLD = 15f;
    private static final float DELTA_Z_THRESHOLD = 100.0f;
    public List<PotholeModel> potholeDataList = new ArrayList<>();

    //--------------------------Navigation Register--------------------------------

    private final OnIndicatorPositionChangedListener onPositionChangedListener = new OnIndicatorPositionChangedListener() {
        @Override
        public void onIndicatorPositionChanged(@NonNull Point point) {
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
            if (isOnNavigation) {
                updateCamera(Point.fromLngLat(location.getLongitude(), location.getLatitude()), (double) location.getBearing());
            }

            // Check if the current location is close to the destination
            if (isRouteActive && selectedRoute != null) {
                LineString lineString = LineString.fromPolyline(selectedRoute.getDirectionsRoute().geometry(), 6);
                Point destination = lineString.coordinates().get(lineString.coordinates().size() - 1);
                double distanceToDestination = TurfMeasurement.distance(Point.fromLngLat(location.getLongitude(), location.getLatitude()), destination);
                double arrivalThreshold = 0.005; // 5 meters

                if (distanceToDestination < arrivalThreshold) {
                    QuitRouting();
                    Toast.makeText(MapPage.this, "You have arrived at your destination", Toast.LENGTH_SHORT).show();
                }
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
                    if (isOnNavigation) {
                        maneuverView.setVisibility(View.VISIBLE);
                        maneuverView.renderManeuvers(maneuverApi.getManeuvers(routeProgress));
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) soundButton.getLayoutParams();
                        params.addRule(RelativeLayout.BELOW, R.id.maneuverView);
                        soundButton.setLayoutParams(params);
                    }
                    else {
                        maneuverView.setVisibility(View.GONE);
                    }
                    return new Object();
                }
            });
        }
    };

    //---------------------------------------------------------------------------------
    private void fetchRoadName(Point point, final RoadNameCallback callback) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(point.latitude(), point.longitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String roadName = extractMostAccurateRoadName(address);
                callback.onRoadNameFetched(roadName != null ? roadName : "Unknown road");
            } else {
                callback.onRoadNameFetched("Unknown road");
            }
        } catch (IOException e) {
            callback.onRoadNameFetched("Unknown road");
        }
    }

    private String extractMostAccurateRoadName(Address address) {
        // Check for the most accurate fields in priority order
        String roadName = address.getThoroughfare(); // Main road name
        if (roadName == null) {
            roadName = address.getSubThoroughfare(); // House number + road
        }
        if (roadName == null) {
            roadName = address.getFeatureName().toString(); // Landmark or feature name
        }
        if (roadName == null) {
            roadName = address.getSubLocality(); // Sub-locality
        }
        if (roadName == null) {
            roadName = address.getLocality(); // Locality (city, town, etc.)
        }
        return roadName;
    }

    interface RoadNameCallback {
        void onRoadNameFetched(String roadName);
    }

    private void showNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "pothole_channel";
        Uri soundUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.pothole_notification);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Pothole Notification", NotificationManager.IMPORTANCE_HIGH);
            channel.setSound(soundUri, null);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, NotificationPage.class);
        long timestamp = System.currentTimeMillis();
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String formattedDate = sdf.format(date);
        intent.putExtra("TIME", formattedDate);
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

    private Point nearestPointOfRoute(Point point, NavigationRoute route) {
        LineString routeLineString = LineString.fromPolyline(route.getDirectionsRoute().geometry(), 6);
        Point nearestPoint = (Point) TurfMisc.nearestPointOnLine(point, routeLineString.coordinates()).geometry();
        return nearestPoint;
    }
    private boolean isPointOnRoute(Point point, NavigationRoute route) {
        Point nearestPoint = nearestPointOfRoute(point, route);
        double distance = TurfMeasurement.distance(point, nearestPoint);
        double thresholdDistance = 0.003; // 3 meters
        return distance < thresholdDistance;
    }

    /* private boolean isPointOnRoute(Point point, NavigationRoute route) {
        LineString routeLineString = LineString.fromPolyline(route.getDirectionsRoute().geometry(), 6);
        List<Point> routePoints = routeLineString.coordinates();
        for (int i = 0; i < routePoints.size() - 1; i++) {
            Point start = routePoints.get(i);
            Point end = routePoints.get(i + 1);
            // Check if the point is exactly at the start or end of the segment
            if (TurfMeasurement.distance(point, start) == 0 || TurfMeasurement.distance(point, end) == 0) {
                return true;
            }
            // Check if the point is collinear with the segment and within the bounds
            if (isPointOnSegment(point, start, end)) {
                return true;
            }
        }
        return false;
    }
    private boolean isPointOnSegment(Point point, Point start, Point end) {
        double epsilon = 0.000027; // Approximately 3 meters in degrees
        double crossProduct = (point.latitude() - start.latitude()) * (end.longitude() - start.longitude()) -
                (point.longitude() - start.longitude()) * (end.latitude() - start.latitude());
        if (Math.abs(crossProduct) > epsilon) {
            return false; // Not collinear
        }
        double dotProduct = (point.longitude() - start.longitude()) * (end.longitude() - start.longitude()) +
                (point.latitude() - start.latitude()) * (end.latitude() - start.latitude());
        if (dotProduct < 0) {
            return false; // Point is behind the start point
        }
        double squaredLengthBA = (end.longitude() - start.longitude()) * (end.longitude() - start.longitude()) +
                (end.latitude() - start.latitude()) * (end.latitude() - start.latitude());
        if (dotProduct > squaredLengthBA) {
            return false; // Point is beyond the end point
        }
        return true; // Point is on the segment
    } */

    private void updateCamera(Point point, Double bearing) {
        MapAnimationOptions animationOptions = new MapAnimationOptions.Builder().duration(1500L).build();
        CameraOptions cameraOptions;
        if (isRouteActive) {
            cameraOptions = new CameraOptions.Builder()
                    .center(point)
                    .zoom(18.0)
                    .bearing(bearing)
                    .pitch(45.0) // Adjust the pitch to see ahead
                    .build();
        }
        else {
            cameraOptions = new CameraOptions.Builder()
                    .center(point)
                    .zoom(18.0)
                    .bearing(bearing)
                    .pitch(0.0)
                    .build();
        }
        getCamera(mapView).easeTo(cameraOptions, animationOptions);
    }

    private final OnMoveListener onMoveListener = new OnMoveListener() {
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
            if (isOnNavigation) {
                isOnNavigation = false;
                navigateBtn.setBackgroundColor(getResources().getColor(R.color.light_purple));
                navigateBtn.setEnabled(true);
            }
            if (!isOnNavigation) focusLocationBtn.show();
            if (!potholeLocations.isEmpty()) {
                reloadPotholePoint();
            }
            getGestures(mapView).removeOnMoveListener(this);
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

    private final MapboxNavigationConsumer<Expected<SpeechError, SpeechValue>> speechCallback = new MapboxNavigationConsumer<Expected<SpeechError, SpeechValue>>() {
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

        sessionManager = new SessionManager(this);
        HashMap<String, String> userDetails = sessionManager.getUserDetails();
        username = userDetails.get(SessionManager.KEY_NAME);

        // Initialize Firebase
        if (database == null) {
            FirebaseDatabase databaseInstance = FirebaseDatabase.getInstance();
            //databaseInstance.setPersistenceEnabled(true);
            database = databaseInstance.getReference();
            Log.d(TAG, "Firebase initialized");
        }

        quitBtn = findViewById(R.id.quit_button);
        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                containterView.setVisibility(View.GONE);
                QuitRouting();
            }
        });

        // Initialize LocationRetriever and retrieve locations from Firebase
        retrieveLocationsRunnable = new Runnable() {
            @Override
            public void run() {
                LocationRetriever locationRetriever = new LocationRetriever(MapPage.this);
                locationRetriever.retrieveLocations(new LocationRetriever.LocationCallback() {
                    @Override
                    public void onLocationsRetrieved(List<Penaldo<Double, Double, String, String, String>>locations) {
                        // Log the retrieved locations
                        if (locations.isEmpty()) {
                            Log.d(TAG, "No locations retrieved from local storage.");
                        } else {
                            Log.d(TAG, "Retrieved " + locations.size() + " locations from local storage.");
                            for (Penaldo<Double, Double, String, String, String> location : locations) {
                                Log.d(TAG, "Latitude: " + location.first + ", Longitude: " + location.second + ", Severity: " + location.fifth);
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
                potholeTracking = potholeLocations;
                reloadPotholePoint();

                // Schedule the next update after 1 minute
                handler.postDelayed(this, 60000);
            }
        };
        handler.post(retrieveLocationsRunnable);

        // Compass
        compassView = findViewById(R.id.compass_icon);
        compassView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOnNavigation = false;
                resetMapBearing();
            }
        });

        // Initialize addPotholeBtn
        filterLayout = findViewById(R.id.filter_layout);
        lowBtn = findViewById(R.id.low_button);
        mediumBtn = findViewById(R.id.medium_button);
        highBtn = findViewById(R.id.high_button);
        lowLayout = findViewById(R.id.low_button_layout);
        mediumLayout = findViewById(R.id.medium_button_layout);
        highLayout = findViewById(R.id.high_button_layout);
        addPotholeBtn = findViewById(R.id.debug_detail_point);
        addPotholeBtn.setVisibility(View.GONE);

        // Navigate button
        navigateBtn = findViewById(R.id.navigate_button);
        containterView = findViewById(R.id.container);
        walkingBtn = findViewById(R.id.walking_button);
        cyclingBtn = findViewById(R.id.cycling_button);
        drivingBtn = findViewById(R.id.driving_button);
        walkingView = findViewById(R.id.walking_button_layout);
        cyclingView = findViewById(R.id.cycling_button_layout);
        drivingView = findViewById(R.id.driving_button_layout);
        searchLayout = findViewById(R.id.search_bar_layout);
        backBtnLayout = findViewById(R.id.back_button_layout);

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
        filterBtn = findViewById(R.id.filter_button);
        setRoute = findViewById(R.id.route_button);

        // route line color resources
        RouteLineColorResources routeLineColorResources = new RouteLineColorResources.Builder()
//                .routeDefaultColor(getResources().getColor(R.color.light_purple))
//                .routeCasingColor(getResources().getColor(R.color.magenta))
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
        soundButton = findViewById(R.id.soundButton);
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

        // search
        placeAutocomplete = PlaceAutocomplete.create(getString(R.string.mapbox_access_token));
        searchET = findViewById(R.id.search_bar_text);
        searchET.setHint(R.string.search_option);
        backBtn = findViewById(R.id.back_button);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mapboxNavigation != null) {
                    mapboxNavigation.onDestroy();
                    mapboxNavigation = null;
                }
                finish();
            }
        });
        searchResultsView = findViewById(R.id.search_results_view);
        searchResultsView.initialize(new SearchResultsView.Configuration(new CommonSearchViewConfiguration()));
        placeAutocompleteUiAdapter = new PlaceAutocompleteUiAdapter(searchResultsView, placeAutocomplete, LocationEngineProvider.getBestLocationEngine(MapPage.this));

        View rootView = findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is used to determine if the keyboard is shown
                    backBtnLayout.setVisibility(View.GONE);
                    searchET.setCursorVisible(true);
                } else {
                    searchET.setCursorVisible(false);
                    backBtnLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

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
            public void afterTextChanged(Editable editable) {}
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
                                        .withIconOpacity(1)
                                        .withIconImage(bitmap)
                                        .withPoint(point);
                                pointAnnotationManager.create(pointAnnotationOptions);
                            }
                            else {
                                mapboxNavigation.setNavigationRoutes(Collections.emptyList());
                                for (PointAnnotation annotation : pointAnnotationManager.getAnnotations()) {
                                    if (annotation.getIconOpacity().equals(0.95)) {
                                        pointAnnotationManager.delete(annotation);
                                    }
                                }
                                bitmap =  BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);
                                PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                                        .withTextAnchor(TextAnchor.CENTER)
                                        .withIconAnchor(IconAnchor.CENTER)
                                        .withIconSize(1)
                                        .withIconOpacity(0.95)
                                        .withIconImage(bitmap)
                                        .withPoint(point);
                                pointAnnotationManager.create(pointAnnotationOptions);

                                containterView.setVisibility(View.VISIBLE);
                                navigateBtn.setEnabled(false);
                                navigateBtn.setBackgroundColor(getResources().getColor(R.color.light_gray));

                                walkingBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        walkingView.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_choosen));
                                        cyclingView.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                                        drivingView.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                                        routeType = 0;
                                        walkingBtn.setEnabled(false);
                                        cyclingBtn.setEnabled(true);
                                        drivingBtn.setEnabled(true);
                                    }
                                });

                                cyclingBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        cyclingView.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_choosen));
                                        walkingView.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                                        drivingView.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                                        routeType = 1;
                                        cyclingBtn.setEnabled(false);
                                        walkingBtn.setEnabled(true);
                                        drivingBtn.setEnabled(true);
                                    }
                                });

                                drivingBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        drivingView.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_choosen));
                                        walkingView.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                                        cyclingView.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                                        routeType = 2;
                                        drivingBtn.setEnabled(false);
                                        walkingBtn.setEnabled(true);
                                        cyclingBtn.setEnabled(true);
                                    }
                                });
                            }
                        }

                        // view point detail
                        pointAnnotationManager.addClickListener(new OnPointAnnotationClickListener() {
                            @Override
                            public boolean onAnnotationClick(@NonNull PointAnnotation pointAnnotation) {
                                if (pointAnnotation.getIconOpacity() != 0.95) {
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
                                    QuitRouting();
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
                        else {
                            Toast.makeText(MapPage.this, "A new pothole has been detected!", Toast.LENGTH_SHORT).show();
                            long timestamp = System.currentTimeMillis();
                            Date date = new Date(timestamp);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            String formattedDate = sdf.format(date);
                            String severity = "Debug";
                            PotholeModel potholeModel = new PotholeModel(deltaX, deltaY, (float) rielZ, pitch, roll, speedKmh, point, username, severity, latitude, longitude, formattedDate);
                            potholeDataList = StorePotholes.loadPotholeData(MapPage.this);
                            potholeDataList.add(potholeModel);
                            StorePotholes.savePotholeData(MapPage.this, potholeDataList);
                        }
                    }
                });

                // filter button
                filterBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (filterLayout.isShown()) {
                            filterLayout.setVisibility(View.GONE);
                        } else {
                            filterLayout.setVisibility(View.VISIBLE);
                        }
                    }
                });

                lowBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (lowFilter) {
                            lowFilter = false;
                            lowLayout.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                        }
                        else {
                            lowFilter = true;
                            lowLayout.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_choosen));
                        }
                        reloadPotholePoint();
                    }
                });

                mediumBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mediumFilter) {
                            mediumFilter = false;
                            mediumLayout.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                        }
                        else {
                            mediumFilter = true;
                            mediumLayout.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_choosen));
                        }
                        reloadPotholePoint();
                    }
                });

                highBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (highFilter) {
                            highFilter = false;
                            highLayout.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                        }
                        else {
                            highFilter = true;
                            highLayout.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_choosen));
                        }
                        reloadPotholePoint();
                    }
                });

                // navigate button
                navigateBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isOnNavigation = true;
                        searchLayout.setVisibility(View.GONE);
                        walkingView.setVisibility(View.GONE);
                        cyclingView.setVisibility(View.GONE);
                        drivingView.setVisibility(View.GONE);

                        getGestures(mapView).addOnMoveListener(onMoveListener);
                        focusLocationBtn.hide();
                        navigateBtn.setEnabled(false);
                        navigateBtn.setBackgroundColor(getResources().getColor(R.color.light_gray));
                    }
                });

                // focus button
                focusLocationBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        navigateBtn.performClick();
                        searchLayout.setVisibility(View.VISIBLE);
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
                        isOnNavigation = false;
                        searchET.setText(placeAutocompleteSuggestion.getName());
                        searchResultsView.setVisibility(View.GONE);
                        searchedPoint = placeAutocompleteSuggestion.getCoordinate();
                        containterView.setVisibility(View.VISIBLE);
                        navigateBtn.setEnabled(false);
                        navigateBtn.setBackgroundColor(getResources().getColor(R.color.light_gray));

                        List<PointAnnotation> annotations = pointAnnotationManager.getAnnotations();
                        for (PointAnnotation annotation : annotations) {
                            if (annotation.getIconOpacity() == 0.95) {
                                pointAnnotationManager.delete(annotation);
                            }
                        }

                        // Add new point on map
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.placeholder);
                        PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                                .withTextAnchor(TextAnchor.CENTER)
                                .withIconAnchor(IconAnchor.CENTER)
                                .withIconSize(1)
                                .withIconOpacity(0.95)
                                .withIconImage(bitmap)
                                .withPoint(searchedPoint);
                        pointAnnotationManager.create(pointAnnotationOptions);

                        updateCamera(searchedPoint, 0.0);

                        walkingBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                walkingView.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_choosen));
                                cyclingView.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                                drivingView.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                                routeType = 0;
                                walkingBtn.setEnabled(false);
                                cyclingBtn.setEnabled(true);
                                drivingBtn.setEnabled(true);
                            }
                        });

                        cyclingBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cyclingView.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_choosen));
                                walkingView.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                                drivingView.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                                routeType = 1;
                                cyclingBtn.setEnabled(false);
                                walkingBtn.setEnabled(true);
                                drivingBtn.setEnabled(true);
                            }
                        });

                        drivingBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                drivingView.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_choosen));
                                walkingView.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                                cyclingView.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                                routeType = 2;
                                drivingBtn.setEnabled(false);
                                walkingBtn.setEnabled(true);
                                cyclingBtn.setEnabled(true);
                            }
                        });

                        setRoute.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                searchLayout.setVisibility(View.GONE);
                                searchResultsView.setVisibility(View.GONE);
                                if (!isRouteActive)
                                    fetchRoute(searchedPoint);
                                else {
                                    QuitRouting();
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
                        float iconSize = 1.0f;
                        //Toast.makeText(MapPage.this, "Zoom level: " + zoomLevel, Toast.LENGTH_SHORT).show();
                        if (zoomLevel > 15 && zoomLevel < 17 && iconSize != 0.6f) {
                            iconSize = 0.6f;
                            changeIconSize(iconSize);
                        } else if (zoomLevel <= 15 && iconSize != 0.3f) {
                            iconSize = 0.3f;
                            changeIconSize(iconSize);
                        } else if (zoomLevel > 20 && iconSize != 1.5f) {
                            iconSize = 1.5f;
                            changeIconSize(iconSize);
                        } else if (iconSize != 1.0f){
                            iconSize = 1.0f;
                            changeIconSize(iconSize);
                        }
                    }
                };
                camHandler.postDelayed(debounceRunnable, 200); // Adjust the delay as needed
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
                searchLayout.setVisibility(View.GONE);
                filterLayout.setVisibility(View.GONE);
                focusLocationBtn.setVisibility(View.GONE);
                compassView.setVisibility(View.GONE);
                containterView.setVisibility(View.GONE);
                soundButton.setVisibility(View.GONE);

                Button closeBtn = dialog.findViewById(R.id.quit_button);
                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        searchLayout.setVisibility(View.VISIBLE);
                        filterLayout.setVisibility(View.VISIBLE);
                        focusLocationBtn.setVisibility(View.VISIBLE);
                        compassView.setVisibility(View.VISIBLE);
                        containterView.setVisibility(View.VISIBLE);
                        soundButton.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                });

                TextView roadNameTextView = dialog.findViewById(R.id.road_name_text_view);
                if (roadName.equals("Tô Vĩnh Diện")) {
                    roadName = "Đường Mạc Đĩnh Chi";
                } else if (roadName.equals("Hiệp Bình") || roadName.equals("VQJV+2F9") || roadName.equals("33, 99, 53") || roadName.equals("VQJW+5FH")) {
                    roadName = "Đường Nguyễn Du";
                } else if (roadName.equals("VRH2+74C") || roadName.equals("VRH2+27J") || roadName.equals("VRG2+G3M")) {
                    roadName = "Đường William Shakespeare";
                } else if (roadName.equals("VRG2+CFW") || roadName.equals("VRG3+PC8")) {
                    roadName = "Đường Lưu Hữu Phước";
                } else if (roadName.equals("VRF3+46J ") || roadName.equals("VRC3+V7G")) {
                    roadName = "Đường Hàn Thuyên";
                } else if (roadName.equals("A2 / A1")) {
                    roadName = "Đường Lê Quý Đôn";
                } else if (roadName.equals("VQJP+R76")) {
                    roadName = "Đường Hồ Xuân Hương";
                }
                roadNameTextView.setText(roadName);

                Penaldo<Double, Double, String, String, String>location = findQuadrupleByPoint(point);
                if (location == null) {
                    Log.e(TAG, "Location not found for the given point.");
                    return;
                }
                String timestamp = location.third;
                String id = location.fourth;
                String severity = location.fifth;
                //Toast.makeText(MapPage.this, "ID: " + id, Toast.LENGTH_SHORT).show();

                TextView timeTextView = dialog.findViewById(R.id.time_text_view);
                timeTextView.setText(timestamp);
                ImageView potholeImage = dialog.findViewById(R.id.pothole_image);
                TextView severityTextView = dialog.findViewById(R.id.severity_text_view);
                severityTextView.setText("Severity: " + severity);

                // download image
                downloadImage(id, potholeImage);

                // upload image
                uploadImage = dialog.findViewById(R.id.upload_image_button);
                uploadImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFileChooser(id, potholeImage);
                    }
                });

                // capture image
                captureImage = dialog.findViewById(R.id.capture_image_button);
                captureImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ContextCompat.checkSelfPermission(MapPage.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MapPage.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                        } else {
                            dispatchTakePictureIntent(id, potholeImage);
                        }
                    }
                });

                dialog.show();
            }
        });
    }

    private String potholeID;
    private ImageView selectedPotholeImage;

    private void openFileChooser(String id, ImageView potholeImage) {
        potholeID = id;
        selectedPotholeImage = potholeImage;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadImage(potholeID, selectedPotholeImage);
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageUri = photoUri;
            uploadImage(potholeID, selectedPotholeImage);
        }
    }

    private void uploadImage(String id, ImageView potholeImage) {
        if (id == null || id.isEmpty()) {
            Toast.makeText(this, "Invalid pothole ID", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null) {
            try {
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                storeImageInFirestore(encodedImage, id, potholeImage);
                potholeImage.setImageBitmap(selectedImage);
            } catch (Exception e) {
                Toast.makeText(this, "Failed to encode image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("UploadImage", "Error: ", e);
            }
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void storeImageInFirestore(String encodedImage, String id, ImageView potholeImage) {
        if (id == null || id.isEmpty()) {
            Toast.makeText(this, "Invalid pothole ID", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put("image", encodedImage);
        db.collection("potholes").document(id)
                .set(user)
                .addOnSuccessListener(aVoid -> Toast.makeText(MapPage.this, "Image stored successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(MapPage.this, "Failed to store image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        downloadImage(id, potholeImage);
    }

    private void downloadImage(String id, ImageView potholeImage) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("potholes").document(id)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String encodedImage = documentSnapshot.getString("image");

                        if (encodedImage != null && !encodedImage.isEmpty()) {
                            // Decode Base64 string to Bitmap
                            byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
                            Bitmap imageBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

                            // Set the image to userImageView
                            potholeImage.setImageBitmap(imageBitmap);
                        }
                    }
                });
    }

    private void dispatchTakePictureIntent(String id, ImageView potholeImage) {
        selectedPotholeImage = potholeImage;
        potholeID = id;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Handle error
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, "com.example.prj.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void resetMapBearing() {
        MapAnimationOptions animationOptions = new MapAnimationOptions.Builder().duration(1500L).build();
        CameraOptions cameraOptions = new CameraOptions.Builder().bearing(0.0).build();
        getCamera(mapView).easeTo(cameraOptions, animationOptions);
    }

    private void changeIconSize(float iconSize) {
        if (pointAnnotationManager != null) {
            List<PointAnnotation> annotations = pointAnnotationManager.getAnnotations();
            for (PointAnnotation annotation : annotations) {
                if (annotation.getIconOpacity() != 0.95) { // Check if it's a pothole point
                    annotation.setIconSize((double) iconSize);
                    pointAnnotationManager.update(annotation);
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
                navigateBtn.setEnabled(true);
                navigateBtn.setText("Navigate");
                navigateBtn.setBackgroundColor(getResources().getColor(R.color.light_purple));
                setRoute.setText("Routing...");
                potholeTracking = potholeLocations;
                RouteOptions.Builder builder = RouteOptions.builder();
                Point origin = Point.fromLngLat(Objects.requireNonNull(location).getLongitude(), location.getLatitude());
                builder.coordinatesList(Arrays.asList(origin, point));
                builder.alternatives(true); // alternative

                if (routeType == 0) {
                    builder.profile(DirectionsCriteria.PROFILE_WALKING); // walking options ()
                }
                else if (routeType == 1) {
                    builder.profile(DirectionsCriteria.PROFILE_CYCLING); // cycling options)
                }
                else {
                    builder.profile(DirectionsCriteria.PROFILE_DRIVING); // driving options
                }

                builder.bearingsList(Arrays.asList(Bearing.builder().angle(location.getBearing()).degrees(45.0).build(), null));
                applyDefaultNavigationOptions(builder);

                mapboxNavigation.requestRoutes(builder.build(), new NavigationRouterCallback() {
                    @Override
                    public void onRoutesReady(@NonNull List<NavigationRoute> list, @NonNull RouterOrigin routerOrigin) {
                        mapboxNavigation.setNavigationRoutes(list);
                        selectedRoute = list.get(0);
                        setRoute.setEnabled(true);
                        setRoute.setText("Stop route");
                        searchLayout.setVisibility(View.GONE);
                        isRouteActive = true;

                        // Set camera to fit the bounding box
                        List<Point> routePoints = LineString.fromPolyline(selectedRoute.getDirectionsRoute().geometry(), 6).coordinates();
                        CameraOptions cameraOptions = mapView.getMapboxMap().cameraForCoordinates(routePoints, new EdgeInsets(100.0, 100.0, 100.0, 100.0), 0.0, 0.0);
                        MapAnimationOptions animationOptions = new MapAnimationOptions.Builder().duration(2000L).build();
                        getCamera(mapView).easeTo(cameraOptions, animationOptions);

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

                        // change route type
                        walkingBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                walkingView.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_choosen));
                                cyclingView.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                                drivingView.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                                builder.profile(DirectionsCriteria.PROFILE_WALKING);
                                walkingBtn.setEnabled(false);
                                cyclingBtn.setEnabled(true);
                                drivingBtn.setEnabled(true);
                                fetchRoute(point);
                            }
                        });

                        cyclingBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cyclingView.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_choosen));
                                walkingView.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                                drivingView.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                                routeType = 1;
                                cyclingBtn.setEnabled(false);
                                walkingBtn.setEnabled(true);
                                drivingBtn.setEnabled(true);
                                fetchRoute(point);
                            }
                        });

                        drivingBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                drivingView.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_choosen));
                                walkingView.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                                cyclingView.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                                routeType = 2;
                                drivingBtn.setEnabled(false);
                                walkingBtn.setEnabled(true);
                                cyclingBtn.setEnabled(true);
                                fetchRoute(point);
                            }
                        });

                        // set route btn
                        setRoute.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (isRouteActive) {
                                    QuitRouting();
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
        if (sensorManager != null) {
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            }
            if (rotationVectorSensor != null) {
                sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this, accelerometer);
            sensorManager.unregisterListener(this, rotationVectorSensor);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mapboxNavigation != null){
            mapboxNavigation.onDestroy();
            mapboxNavigation = null;
        }
        handler.removeCallbacks(pushDataRunnable);
        if (mapView != null) {
            LocationComponentPlugin locationComponentPlugin = getLocationComponent(mapView);
            if (locationComponentPlugin != null) {
                locationComponentPlugin.removeOnIndicatorPositionChangedListener(onPositionChangedListener);
            }
        }
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
        if (isRouteActive) {
            if (potholeTracking != null) {
                for (Penaldo<Double, Double, String, String, String> pLocation : potholeTracking) {
                    Point p = Point.fromLngLat(pLocation.second, pLocation.first);
                    Double distance = TurfMeasurement.distance(Point.fromLngLat(location.getLongitude(), location.getLatitude()), p);
                    if (distance > 1) continue;
                    if (pLocation.fifth.equals("Low") && !lowFilter) continue;
                    else if (pLocation.fifth.equals("Medium") && !mediumFilter) continue;
                    else if (pLocation.fifth.equals("High") && !highFilter) continue;
                    if (distance < thresholdDistanceToNoti) {
                        if (isPointOnRoute(p, selectedRoute)) {
                            showNotification("Pothole On Route", "There are a pothole ahead!");
                            potholeTracking.remove(pLocation);
                            break;
                        }
                    }
                }
            }
            Point nearestP = nearestPointOfRoute(Point.fromLngLat(location.getLongitude(), location.getLatitude()), selectedRoute);
            latitude = nearestP.latitude();
            longitude = nearestP.longitude();
        }
        // Get the speed in meters/second
        float speed = location.getSpeed();
        // Convert to km/h
        speedKmh = speed * 3.6f;
    }

    private void QuitRouting() {
        isRouteActive = false;
        isOnNavigation = false;
        searchLayout.setVisibility(View.VISIBLE);
        containterView.setVisibility(View.GONE);
        walkingView.setVisibility(View.VISIBLE);
        cyclingView.setVisibility(View.VISIBLE);
        drivingView.setVisibility(View.VISIBLE);
        searchResultsView.setVisibility(View.GONE);
        searchET.setText("");
        navigateBtn.setEnabled(false);
        navigateBtn.setBackgroundColor(getResources().getColor(R.color.light_gray));
        maneuverView.setVisibility(View.GONE);
        searchET.setVisibility(View.VISIBLE);
        mapboxNavigation.setNavigationRoutes(Collections.emptyList());
        ArrowVisibilityChangeValue tmp = routeArrowApi.hideManeuverArrow();
        routeArrowView.render(mapStyle, tmp);
        setRoute.setText("Set route");
        mapboxNavigation.setNavigationRoutes(Collections.emptyList());
        for (PointAnnotation annotation : pointAnnotationManager.getAnnotations()) {
            if (annotation.getIconOpacity() == 0.95) {
                pointAnnotationManager.delete(annotation);
            }
        }
        potholeTracking = potholeLocations;
        reloadPotholePoint();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) soundButton.getLayoutParams();
        params.addRule(RelativeLayout.BELOW, R.id.search_bar_layout);
        soundButton.setLayoutParams(params);
        @SuppressLint("MissingPermission") Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            MapAnimationOptions animationOptions = new MapAnimationOptions.Builder().duration(1500L).build();
            CameraOptions cameraOptions = new CameraOptions.Builder()
                    .center(Point.fromLngLat(lastKnownLocation.getLongitude(), lastKnownLocation.getLatitude()))
                    .zoom(18.0) // Default zoom level
                    .bearing(0.0) // Default bearing
                    .pitch(0.0) // Default pitch
                    .build();
            getCamera(mapView).easeTo(cameraOptions, animationOptions);
        }
    }

    private void pushData() {
        calcPoint();
        if (point != 0 && speedKmh > SPEED_THRESHOLD && rielZ > DELTA_Z_THRESHOLD) {
            Toast.makeText(this, "A new pothole has been detected!", Toast.LENGTH_SHORT).show();

            long timestamp = System.currentTimeMillis();
            Date date = new Date(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String formattedDate = sdf.format(date);
            String severity;

            if (rielZ > 100 && rielZ < 120) {
                severity = "Low";
            } else if (rielZ > 120 && rielZ < 140) {
                severity = "Medium";
            } else {
                severity = "High";
            }

            PotholeModel potholeModel = new PotholeModel(deltaX, deltaY, (float) rielZ, pitch, roll, speedKmh, point, username, severity, latitude, longitude, formattedDate);
            potholeDataList = StorePotholes.loadPotholeData(this);
            potholeDataList.add(potholeModel);
            StorePotholes.savePotholeData(this, potholeDataList);
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
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage.performClick();
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Camera permission is required to take pictures", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void vibrate() {
        if ((deltaX > vibrateThreshold) || (deltaY > vibrateThreshold) || (deltaZ > vibrateThreshold)) {
            v.vibrate(50);
        }
    }

    public void calcPoint(){
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

    public void reloadPotholePoint() {
        if (pointAnnotationManager != null) {
            for (PointAnnotation annotation : pointAnnotationManager.getAnnotations()) {
                if (annotation.getIconOpacity() != 0.95) {
                    pointAnnotationManager.delete(annotation);
                }
            }
            for (Penaldo<Double, Double, String, String, String> location : potholeLocations) {
                Point point = Point.fromLngLat(location.second, location.first);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pothole_on_map);
                String severity = location.fifth;
                if (severity.equals("Low")) {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.low_severity_pothole);
                }
                else if (severity.equals("Medium")) {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.medium_severity_pothole);
                }
                else if (severity.equals("High")) {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.high_severity_pothole);
                }
                if (!lowFilter) {
                    if (severity.equals("Low")) continue;
                }
                if (!mediumFilter) {
                    if (severity.equals("Medium")) continue;
                }
                if (!highFilter) {
                    if (severity.equals("High")) continue;
                }
                PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions()
                        .withTextAnchor(TextAnchor.CENTER)
                        .withIconAnchor(IconAnchor.CENTER)
                        .withIconSize(iconSize)
                        .withIconOpacity(1)
                        .withIconImage(bitmap)
                        .withPoint(point);
                pointAnnotationManager.create(pointAnnotationOptions);
            }
        }
    }

    private Penaldo<Double, Double, String, String, String> findQuadrupleByPoint(Point point) {
        for (Penaldo<Double, Double, String, String, String> location : potholeLocations) {
            if (location.first.equals(point.latitude()) && location.second.equals(point.longitude())) {
                return location;
            }
        }
        return null;
    }
}