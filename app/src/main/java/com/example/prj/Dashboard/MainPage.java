package com.example.prj.Dashboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prj.Authen.SignIn;
import com.example.prj.Map.MapPage;
import com.example.prj.R;
import com.example.prj.Session.SessionManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainPage extends AppCompatActivity {

    SessionManager sessionManager;
    private Button logoutButton, scanButton, settingButton, menuButton, notificationButton, historyButton, profileButton, mapButton;
    private LineChart lineChart1, lineChart2;
    private List<String> xValues1, yValues1, xValues2, yValues2;
    public TextView nameTextView;
    public ImageView userImageView;

    public String username, usernameText;
    private boolean isImageSaved = false;

    // session
    private BroadcastReceiver logoutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_page);

        // Register the logout receiver
        IntentFilter filter = new IntentFilter("com.example.prj.LOGOUT");
        registerReceiver(logoutReceiver, filter);

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

        // Check if the digital otp is null
        /*DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(nameTextView.getText().toString());

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String otpFromDB = snapshot.child(nameTextView.getText().toString()).child("otp").getValue(String.class);
                    if (otpFromDB == null) {
                        setDigitalOTP();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainPage.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });*/

        // Linechart
        lineChart1 = findViewById(R.id.line_chart1);
        Description description1 = new Description();
        description1.setText("");
        lineChart1.setDescription(description1);
        lineChart1.getAxisRight().setDrawLabels(false);
        lineChart1.getAxisRight().setDrawGridLines(false);
        xValues1 = Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");

        XAxis xAxis1 = lineChart1.getXAxis();
        xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis1.setValueFormatter(new IndexAxisValueFormatter(xValues1));
        xAxis1.setDrawGridLines(false);
        xAxis1.setLabelCount(6);

        YAxis yAxis1 = lineChart1.getAxisLeft();
        yAxis1.setAxisMinimum(0f);
        yAxis1.setAxisMaximum(20f);
        yAxis1.setAxisLineWidth(1f);
        yAxis1.setDrawGridLines(false);
        yAxis1.setLabelCount(4);

        List<Entry> entries1 = new ArrayList<>();
        entries1.add(new Entry(0, 0f));
        entries1.add(new Entry(1, 7f));
        entries1.add(new Entry(2, 5f));
        entries1.add(new Entry(3, 12f));
        entries1.add(new Entry(4, 10f));
        entries1.add(new Entry(5, 3f));
        entries1.add(new Entry(6, 18f));

        LineDataSet dataSet1 = new LineDataSet(entries1, "Detected");
        dataSet1.setColor(getColor(R.color.red));
        dataSet1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataSet1.setDrawCircles(false);
        dataSet1.setLineWidth(3f);

        LineData lineData1 = new LineData(dataSet1);
        lineData1.setDrawValues(false);

        lineChart1.setData(lineData1);
        lineChart1.invalidate();
        lineChart1.setScaleEnabled(false);
        lineChart1.setPinchZoom(false);
        lineChart1.setDoubleTapToZoomEnabled(false);

        //-----------------------------------------

        lineChart2 = findViewById(R.id.line_chart2);
        Description description2 = new Description();
        description2.setText("");
        lineChart2.setDescription(description2);
        lineChart2.getAxisRight().setDrawLabels(false);
        lineChart2.getAxisRight().setDrawGridLines(false);
        xValues1 = Arrays.asList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun");

        XAxis xAxis2 = lineChart2.getXAxis();
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis2.setValueFormatter(new IndexAxisValueFormatter(xValues1));
        xAxis2.setDrawGridLines(false);
        xAxis2.setLabelCount(6);

        YAxis yAxis2 = lineChart2.getAxisLeft();
        yAxis2.setAxisMinimum(0f);
        yAxis2.setAxisMaximum(20f);
        yAxis2.setAxisLineWidth(1f);
        yAxis2.setDrawGridLines(false);
        yAxis2.setLabelCount(4);

        List<Entry> entries2 = new ArrayList<>();
        entries2.add(new Entry(0, 10f));
        entries2.add(new Entry(1, 2f));
        entries2.add(new Entry(2, 15f));
        entries2.add(new Entry(3, 1f));
        entries2.add(new Entry(4, 5f));
        entries2.add(new Entry(5, 20f));
        entries2.add(new Entry(6, 10f));

        LineDataSet dataSet2 = new LineDataSet(entries2, "Detected");
        dataSet2.setColor(getColor(R.color.cyan));
        dataSet2.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataSet2.setDrawCircles(false);
        dataSet2.setLineWidth(3f);

        LineData lineData2 = new LineData(dataSet2);
        lineData2.setDrawValues(false);

        lineChart2.setData(lineData2);
        lineChart2.invalidate();
        lineChart2.setScaleEnabled(false);
        lineChart2.setPinchZoom(false);
        lineChart2.setDoubleTapToZoomEnabled(false);

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

    /*private void setDigitalOTP() {
        Intent intent = new Intent(MainPage.this, SetDigital.class);
        intent.putExtra("USERNAME", nameTextView.getText().toString());
        startActivity(intent);
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the logout receiver
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
    public void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocale(language);
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

                            // Save to local storage
                            saveImageToLocalStorage(imageBitmap, usernameText + ".jpg");
                        }
                    }
                });
    }


    private void saveImageToLocalStorage(Bitmap bitmap, String fileName) {
        FileOutputStream fos = null;
        try {
            // Save the image in app-specific storage
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            Log.d("SaveImage", "Image saved at: " + file.getAbsolutePath());
        } catch (Exception e) {
            Log.e("SaveImage", "Error: ", e);
        } finally {
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
                Log.e("SaveImage", "Failed to close FileOutputStream", e);
            }
        }
    }
}