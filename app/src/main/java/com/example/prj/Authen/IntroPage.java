package com.example.prj.Authen;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prj.Dashboard.MainPage;
import com.example.prj.R;
import com.example.prj.Session.SessionManager;

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
            } else {
                // User is not logged in, redirect to SignIn
                Intent intent = new Intent(IntroPage.this, SignIn.class);
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

}