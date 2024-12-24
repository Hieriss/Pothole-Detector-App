package com.example.prj.Authen;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prj.Dashboard.MainPage;
import com.example.prj.R;
import com.example.prj.Session.SessionManager;

import java.util.Locale;

public class IntroPage extends AppCompatActivity {
    SessionManager sessionManager;
    TextView textView;
    final String[] texts = {"Give you real-time pothole warnings", "Detect potholes with high accuracy", "Earn rewards for reporting potholes", "Find the best route to your destination"};
    int currentIndex = 0;
    final Handler handler = new Handler(Looper.getMainLooper());
    Button introButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        loadLocale();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intro_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new SessionManager(this);
        if (sessionManager.PassedIntro()) {
            // Intro has been passed, check login status
            if (sessionManager.isLoggedIn()) {
                // User is logged in, redirect to MainPage
                Intent intent = new Intent(IntroPage.this, MainPage.class);
                startActivity(intent);
                finish();
            }
        }

        introButton = findViewById(R.id.intro_button);
        introButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.setIntroPassed(true);
                Intent intent = new Intent(view.getContext(), SignIn.class);
                view.getContext().startActivity(intent);
                finish();
            }
        });

        textView = findViewById(R.id.loop_text);
        startTextLoop();
    }

    private void startTextLoop() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                // Fade out animation
                ObjectAnimator fadeOut = ObjectAnimator.ofFloat(textView, "alpha", 1f, 0f);
                fadeOut.setDuration(500); // duration in milliseconds

                // Fade in animation
                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f);
                fadeIn.setDuration(500); // duration in milliseconds

                fadeOut.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        // Change the text when fade out ends
                        textView.setText(texts[currentIndex]);
                        currentIndex = (currentIndex + 1) % texts.length;
                        // Start fade in animation
                        fadeIn.start();
                    }
                });

                // Start fade out animation
                fadeOut.start();

                // Schedule the next text change
                handler.postDelayed(this, 4000); // delay in milliseconds
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
//    private void setThemeMode(boolean isNightMode) {
//        if (isNightMode) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        }
//        SharedPreferences.Editor editor = getSharedPreferences("MODE", MODE_PRIVATE).edit();
//        editor.putBoolean("night", isNightMode);
//        editor.apply();
//    }
//    public void loadLocale() {
//        SharedPreferences sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
//        boolean nightMode = sharedPreferences.getBoolean("night", false);
//        if (isFirstTime()) {
//            setThemeMode(false);
//        } else {
//            setThemeMode(nightMode);
//        }
//    }
    private boolean isFirstTime() {
        SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean firstTime = preferences.getBoolean("isFirstTime", true);
        if (firstTime) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isFirstTime", false);
            editor.apply();
        }
        return firstTime;
    }

}