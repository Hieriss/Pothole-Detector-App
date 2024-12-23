package com.example.prj.Setting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prj.Authen.ForgotPassword;
import com.example.prj.Dashboard.MainPage;
import com.example.prj.Profile.ProfilePage;
import com.example.prj.R;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.mapbox.geojson.Point;

import java.util.Locale;

public class SettingPage extends AppCompatActivity {
    Button homeButton, accountButton, logoutButton, deleteAccountButton, supportButton, termButton;
    TextInputLayout warnNotificationLayout, languageLayout, themeLayout;
    AutoCompleteTextView warnNotificationText, languageText, themeText;
    String[] warnNotification = {"1 km", "5 km", "10 km"};
    String[] language = {"English", "Tiếng Việt"};
    String[] theme = {"Light", "Dark"};
    ArrayAdapter<String> warnNotificationAdapter;
    ArrayAdapter<String> languageAdapter;
    ArrayAdapter<String> themeAdapter;
    private BroadcastReceiver closeReceiver;

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

        closeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("CLOSE_SETTING_PAGE".equals(intent.getAction())) {
                    finish();
                }
            }
        };
        registerReceiver(closeReceiver, new IntentFilter("CLOSE_SETTING_PAGE"));

        loadLocale();

        warnNotificationLayout = findViewById(R.id.setting_warn_notification_text);
        warnNotificationText = findViewById(R.id.setting_warn_notification_text_input);
        warnNotificationAdapter = new ArrayAdapter<>(this, R.layout.list_item, warnNotification);
        warnNotificationText.setAdapter(warnNotificationAdapter);
        warnNotificationText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
            }
        });

        languageLayout = findViewById(R.id.setting_languague_text);
        languageText = findViewById(R.id.setting_language_text_input);
        languageAdapter = new ArrayAdapter<>(this, R.layout.list_item, language);
        languageText.setAdapter(languageAdapter);
        languageText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                if (item.equals("English")) {
                    setLocale("en");
                } else {
                    setLocale("vi");
                }
                recreate();
            }
        });

        themeLayout = findViewById(R.id.setting_theme_text);
        themeText = findViewById(R.id.setting_theme_text_input);
        themeAdapter = new ArrayAdapter<>(this, R.layout.list_item, theme);
        themeText.setAdapter(themeAdapter);
        themeText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                if (item.equals("Dark")) {
                    setTheme(true);
                } else {
                    setTheme(false);
                }
                recreate();
            }
        });

        homeButton = findViewById(R.id.setting_home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        deleteAccountButton = findViewById(R.id.setting_delete_button);
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingPage.this, DeleteAccountRequest.class);
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
                getSharedPreferences("PotholeDataPrefs", MODE_PRIVATE).edit().clear().apply();
                Intent intent = new Intent(SettingPage.this, LogoutRequest.class);
                startActivity(intent);
            }
        });
    }

    public void setTheme(boolean isNightMode) {
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        SharedPreferences.Editor editor = getSharedPreferences("MODE", MODE_PRIVATE).edit();
        editor.putBoolean("night", isNightMode);
        editor.apply();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the broadcast receiver
        if (closeReceiver != null) {
            unregisterReceiver(closeReceiver);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        homeButton.performClick();
    }
}