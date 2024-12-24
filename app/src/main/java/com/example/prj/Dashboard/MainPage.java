package com.example.prj.Dashboard;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prj.Map.MapPage;
import com.example.prj.Notification.NotificationPage;
import com.example.prj.R;
import com.example.prj.Session.SessionManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainPage extends AppCompatActivity {

    SessionManager sessionManager;
    private Button logoutButton, scanButton, menuButton, mapButton;
    private LineChart lineChart;
    private BarChart barChart;
    private List<String> lineXValues;
    private List<Entry> lineEntries = new ArrayList<>();
    ArrayList<BarEntry> barEntries = new ArrayList<>();
    public TextView nameTextView;
    public ImageView userImageView;

    public String username, usernameText;
    private boolean isImageSaved = false;

    private List<String> barXValues = Arrays.asList("Low", "Medium", "High");

    // session
    private BroadcastReceiver logoutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_page);

        IntentFilter filter_close = new IntentFilter("CLOSE_MAIN_PAGE");
        registerReceiver(closeReceiver, filter_close, Context.RECEIVER_NOT_EXPORTED);

        // Register the logout receiver
        IntentFilter filter = new IntentFilter("com.example.prj.LOGOUT");
        registerReceiver(logoutReceiver, filter, Context.RECEIVER_NOT_EXPORTED);

        nameTextView = findViewById(R.id.name_text);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        HashMap<String, String> userDetails = sessionManager.getUserDetails();
        usernameText = userDetails.get(SessionManager.KEY_NAME);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (usernameText != null) {
            // Set the username to the name_text TextView
            nameTextView.setText(usernameText);
        } else {
            // Set the username to the name_text TextView
            usernameText = getIntent().getStringExtra("USERNAMEQR"); // Ensure username is set
            nameTextView.setText(usernameText);
        }

        // bar chart
        barChart = findViewById(R.id.bar_chart);
        Description barDescription = new Description();
        barDescription.setText("");
        barChart.setDescription(barDescription);
        barChart.getAxisRight().setDrawLabels(false);
        barChart.getAxisRight().setDrawGridLines(false);

        XAxis barXAxis = barChart.getXAxis();
        barXAxis.setDrawGridLines(false);
        barXAxis.setLabelCount(3);

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(20f);
        yAxis.setAxisLineWidth(2f);
        yAxis.setDrawGridLines(false);
        yAxis.setAxisLineColor(getColor(R.color.black));
        yAxis.setLabelCount(4);

        fetchDataForBarChart();

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(barXValues));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getXAxis().setGranularityEnabled(true);
        barChart.setScaleEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDoubleTapToZoomEnabled(false);

        // line chart
        lineChart = findViewById(R.id.line_chart);
        Description lineDescription = new Description();
        lineDescription.setText("");
        lineChart.setDescription(lineDescription);
        lineChart.getAxisRight().setDrawLabels(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineXValues = Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");

        XAxis lineXAxis = lineChart.getXAxis();
        lineXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        lineXAxis.setValueFormatter(new IndexAxisValueFormatter(lineXValues));
        lineXAxis.setDrawGridLines(false);
        lineXAxis.setLabelCount(6);

        YAxis lineYAxis = lineChart.getAxisLeft();
        lineYAxis.setAxisMinimum(0f);
        lineYAxis.setAxisMaximum(20f);
        lineYAxis.setAxisLineWidth(1f);
        lineYAxis.setDrawGridLines(false);
        lineYAxis.setLabelCount(4);

        fetchDataForLineChart();

        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDoubleTapToZoomEnabled(false);

        scanButton = findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiateQRScan();  // Start QR code scanning when the scan button is clicked
            }
        });

        menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainPage.this, MenuPage.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
            }
        });

        mapButton = findViewById(R.id.map_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainPage.this, MapPage.class);
                startActivity(intent);
            }
        });

        userImageView = findViewById(R.id.main_user_image_rounded);

        if (usernameText != null) {
            downloadImage();
        } else {
            Toast.makeText(this, "Username is null", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchDataForLineChart() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user").child(usernameText).child("counter");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Map<String, Integer> weekdayCount = new HashMap<>();
                    for (String day : lineXValues) {
                        weekdayCount.put(day, 0);
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_YEAR, -7);
                    Date oneWeekAgo = calendar.getTime();

                    for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                        String dateString = dateSnapshot.getKey();
                        try {
                            Date date = sdf.parse(dateString);
                            if (date != null && date.after(oneWeekAgo)) {
                                calendar.setTime(date);
                                String weekday = new SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.getTime());
                                int itemCount = 0;
                                for (DataSnapshot itemSnapshot : dateSnapshot.getChildren()) {
                                    itemCount++;
                                }
                                weekdayCount.put(weekday, weekdayCount.get(weekday) + itemCount);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    lineEntries.clear(); // Clear previous entries
                    for (int i = 0; i < lineXValues.size(); i++) {
                        String day = lineXValues.get(i);
                        lineEntries.add(new Entry(i, weekdayCount.get(day)));
                    }

                    if (lineEntries.isEmpty()) {
                        for (int i = 0; i < lineXValues.size(); i++) {
                            String day = lineXValues.get(i);
                            lineEntries.add(new Entry(i, 0));
                        }
                        Log.d("LineChart", "No data available for the chart.");
                    } else {
                        Log.d("LineChart", "Data available for the chart: " + lineEntries.toString());
                    }

                    LineDataSet dataSet = new LineDataSet(lineEntries, "Number of Potholes");
                    dataSet.setColor(getColor(R.color.red));
                    dataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
                    dataSet.setDrawCircles(false);
                    dataSet.setLineWidth(3f);

                    LineData lineData = new LineData(dataSet);
                    lineData.setDrawValues(false);

                    lineChart.setData(lineData);
                    lineChart.invalidate();

                    lineChart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainPage.this, LogPage.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                } else {
                    Log.d("LineChart", "No snapshot data available.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LineChart", "Database error: " + error.getMessage());
            }
        });
    }

    private void fetchDataForBarChart() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user").child(usernameText).child("counter");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Map<String, Integer> severityCount = new HashMap<>();
                    severityCount.put("Low", 0);
                    severityCount.put("Medium", 0);
                    severityCount.put("High", 0);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_YEAR, -7);
                    Date oneWeekAgo = calendar.getTime();

                    for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                        String dateString = dateSnapshot.getKey();
                        try {
                            Date date = sdf.parse(dateString);
                            if (date != null && date.after(oneWeekAgo)) {
                                for (DataSnapshot timeSnapshot : dateSnapshot.getChildren()) {
                                    String severity = timeSnapshot.getValue(String.class);
                                    if (severity != null) {
                                        severityCount.put(severity, severityCount.get(severity) + 1);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    barEntries.clear();
                    barEntries.add(new BarEntry(0, severityCount.get("Low")));
                    barEntries.add(new BarEntry(1, severityCount.get("Medium")));
                    barEntries.add(new BarEntry(2, severityCount.get("High")));

                    if (barEntries.isEmpty()) {
                        for (int i = 0; i < 3; i++) {
                            barEntries.add(new BarEntry(i, 0));
                        }
                        Log.d("BarChart", "No data available for the chart.");
                    } else {
                        Log.d("BarChart", "Data available for the chart: " + barEntries.toString());
                    }

                    BarDataSet dataSet = new BarDataSet(barEntries, "Pothole Types");
                    dataSet.setColors(new int[]{ColorTemplate.MATERIAL_COLORS[0], ColorTemplate.MATERIAL_COLORS[1], ColorTemplate.MATERIAL_COLORS[2]});

                    BarData barData = new BarData(dataSet);
                    barData.setDrawValues(false);

                    barChart.setData(barData);
                    barChart.invalidate();
                } else {
                    Log.d("BarChart", "No snapshot data available.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BarChart", "Database error: " + error.getMessage());
            }
        });
    }

    // QR Code Scanning Method
    private void initiateQRScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);  // Only QR codes
        integrator.setPrompt(getString(R.string.scan_qr));
        integrator.setCameraId(0);  // Use a specific camera, or you can remove this to let it choose automatically
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(true);  // Lock orientation
        integrator.initiateScan();  // Start scanning
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, getString(R.string.scan_cancelled), Toast.LENGTH_LONG).show();
            } else {
                String scannedSessionId = result.getContents();
                verifyScannedSession(scannedSessionId);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void verifyScannedSession(String sessionId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("qrCodes");
        ref.child(sessionId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                String status = task.getResult().getValue(String.class);
                if ("waiting_for_login".equals(status)) {
                    // Update the session ID with user credentials
                    username = getIntent().getStringExtra("USERNAME");
                    ref.child(sessionId).child("username").setValue(username);
                    ref.child(sessionId).child("status").setValue("logged_in");

                    Toast.makeText(MainPage.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainPage.this, getString(R.string.invalid_qrcode), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainPage.this, getString(R.string.session_id_not_found), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private BroadcastReceiver closeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the logout receiver
        unregisterReceiver(closeReceiver);
        unregisterReceiver(logoutReceiver);
    }
    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }
    private void setThemeMode(boolean isNightMode) {
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        SharedPreferences.Editor editor = getSharedPreferences("MODE", MODE_PRIVATE).edit();
        editor.putBoolean("night", isNightMode);
        editor.apply();
    }
    public void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        SharedPreferences sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        boolean nightMode = sharedPreferences.getBoolean("night", false);
        setLocale(language);
        setThemeMode(nightMode);
    }

    private void downloadImage() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(usernameText)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String encodedImage = documentSnapshot.getString("userImage");

                        if (encodedImage != null && !encodedImage.isEmpty()) {
                            // Decode Base64 string to Bitmap
                            byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
                            Bitmap imageBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

                            // Set the image to userImageView
                            userImageView.setImageBitmap(imageBitmap);
                        }
                    }
                });
    }
}