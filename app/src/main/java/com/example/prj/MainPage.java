package com.example.prj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainPage extends AppCompatActivity {

    private Button logoutButton, scanButton, settingButton, achievementButton, notificationButton, historyButton, profileButton, mapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve the username from the Intent
        String username = getIntent().getStringExtra("USERNAME");

        if (username != null ) {
            // Set the username to the name_text TextView
            TextView nameTextView = findViewById(R.id.name_text);
            nameTextView.setText(username);
        } else {
            // Set the username to the name_text TextView
            String usernameqr = getIntent().getStringExtra("USERNAMEQR");
            TextView nameTextView = findViewById(R.id.name_text);
            nameTextView.setText(usernameqr);
        }


        // Initialize buttons and listeners
        logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });

        scanButton = findViewById(R.id.scan_button);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initiateQRScan();  // Start QR code scanning when the scan button is clicked
            }
        });

        settingButton = findViewById(R.id.setting_button);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainPage.this, SettingPage.class);
                startActivity(intent);
            }
        });

        achievementButton = findViewById(R.id.achievement_button);
        achievementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainPage.this, AchievementPage.class);
                startActivity(intent);
            }
        });

        notificationButton = findViewById(R.id.notification_button);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainPage.this, NotificationPage.class);
                startActivity(intent);
            }
        });

        historyButton = findViewById(R.id.history_button);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainPage.this, HistoryPage.class);
                startActivity(intent);
            }
        });

        profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainPage.this, ProfilePage.class);
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
    }

    // Function to handle user logout
    public void logoutUser() {
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();  // Clear session data
        editor.apply();

        // Navigate back to the login screen
        Intent intent = new Intent(MainPage.this, SignIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    // QR Code Scanning Method
    private void initiateQRScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);  // Only QR codes
        integrator.setPrompt("Scan QR Code");
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
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_LONG).show();
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
                    // Retrieve the current user's credentials
                    // SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
                    // String username = preferences.getString("username", null);

                    // Update the session ID with user credentials
                    String username = getIntent().getStringExtra("USERNAME");
                    ref.child(sessionId).child("username").setValue(username);
                    ref.child(sessionId).child("status").setValue("logged_in");

                    Toast.makeText(MainPage.this, "Login successful!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainPage.this, "Invalid or already used QR Code", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainPage.this, "Session ID not found", Toast.LENGTH_SHORT).show();
            }
        });
    }
}