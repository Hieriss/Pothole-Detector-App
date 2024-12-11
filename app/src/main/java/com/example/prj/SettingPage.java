package com.example.prj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prj.Authen.SignIn;
import com.example.prj.Dashboard.MainPage;
import com.example.prj.Session.SessionManager;

public class SettingPage extends AppCompatActivity {

    Button homeButton, accountButton, securityButton, notificationButton, displayButton, activityCenterButton, reportBugsButton, supportButton, termButton, switchAccountButton, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setting_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        homeButton = findViewById(R.id.setting_home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingPage.this, MainPage.class);
                startActivity(intent);
            }
        });

        accountButton = findViewById(R.id.setting_account_button);
        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingPage.this, AccountSettingPage.class);
                startActivity(intent);
            }
        });

        securityButton = findViewById(R.id.setting_security_button);
        securityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingPage.this, SecuritySettingPage.class);
                startActivity(intent);
            }
        });

        notificationButton = findViewById(R.id.setting_notification_button);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingPage.this, NotificationSettingPage.class);
                startActivity(intent);
            }
        });

        displayButton = findViewById(R.id.setting_display_button);
        displayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingPage.this, DisplaySettingPage.class);
                startActivity(intent);
            }
        });

        activityCenterButton = findViewById(R.id.setting_activity_center_button);
        activityCenterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingPage.this, ActivitycenterSettingPage.class);
                startActivity(intent);
            }
        });

        reportBugsButton = findViewById(R.id.setting_report_bugs_button);
        reportBugsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingPage.this, ReportbugsSettingPage.class);
                startActivity(intent);
            }
        });

        supportButton = findViewById(R.id.setting_support_button);
        supportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingPage.this, SupportPage.class);
                startActivity(intent);
            }
        });

        termButton = findViewById(R.id.setting_term_button);
        termButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingPage.this, TermsAndPoliciesPage.class);
                startActivity(intent);
            }
        });

        logoutButton = findViewById(R.id.setting_logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionManager sessionManager = new SessionManager(SettingPage.this);
                sessionManager.logoutUser();

                // Send broadcast to destroy MainPage activity
                Intent broadcastIntent = new Intent("com.example.prj.LOGOUT");
                sendBroadcast(broadcastIntent);

                // Navigate back to the login screen
                Intent intent = new Intent(SettingPage.this, SignIn.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}