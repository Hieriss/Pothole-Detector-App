package com.example.prj;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.lang.reflect.Field;

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
    }
}