package com.example.prj.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prj.Authen.ForgotPassword;
import com.example.prj.Dashboard.MainPage;
import com.example.prj.Profile.ProfilePage;
import com.example.prj.R;

public class SettingPage extends AppCompatActivity {

    Button homeButton, accountButton, resetPasswordButton, anonymousUserButton, logoutButton, deleteAccountButton, detectNotificationButton, warnNotificationButton, languageButton, themeButton, supportButton, termButton;

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
                finish();
            }
        });

        accountButton = findViewById(R.id.setting_account_button);
        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingPage.this, ProfilePage.class);
                startActivity(intent);
                finish();
            }
        });

        resetPasswordButton = findViewById(R.id.setting_resetpw_button);
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingPage.this, ForgotPassword.class);
                startActivity(intent);
                finish();
            }
        });

        anonymousUserButton = findViewById(R.id.setting_anonymous_button);
        anonymousUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        deleteAccountButton = findViewById(R.id.setting_delete_button);
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingPage.this, DeleteAccountRequest.class);
                startActivity(intent);
                finish();
            }
        });

        detectNotificationButton = findViewById(R.id.setting_detect_notification_button);
        detectNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        warnNotificationButton = findViewById(R.id.setting_warn_notification_button);
        warnNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        languageButton = findViewById(R.id.setting_language_button);
        languageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingPage.this, DisplaySettingPage.class);
                startActivity(intent);
                finish();
            }
        });

        themeButton = findViewById(R.id.setting_theme_button);
        themeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingPage.this, ThemeSettingPage.class);
                startActivity(intent);
                finish();
            }
        });

        supportButton = findViewById(R.id.setting_support_button);
        supportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingPage.this, SupportPage.class);
                startActivity(intent);
                finish();
            }
        });

        termButton = findViewById(R.id.setting_term_button);
        termButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingPage.this, TermsAndPoliciesPage.class);
                startActivity(intent);
                finish();
            }
        });

        logoutButton = findViewById(R.id.setting_logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingPage.this, LogoutRequest.class);
                startActivity(intent);
                finish();
            }
        });
    }
}