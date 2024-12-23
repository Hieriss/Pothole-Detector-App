package com.example.prj.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prj.History.HistoryPage;
import com.example.prj.Map.MapViewPothole;
import com.example.prj.Notification.NotificationPage;
import com.example.prj.Profile.ProfilePage;
import com.example.prj.R;
import com.example.prj.Setting.SettingPage;

public class MenuPage extends AppCompatActivity {
    Button quitButton, settingButton, profileButton, notificationButton, historyButton;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FrameLayout mainLayout = findViewById(R.id.main);
        Animation slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        mainLayout.startAnimation(slideIn);

        username = getIntent().getStringExtra("USERNAME");

        quitButton = findViewById(R.id.quit_button);
        quitButton.setOnClickListener(v -> {
            Animation slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
            slideOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // Do nothing
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    finish();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // Do nothing
                }
            });
            mainLayout.startAnimation(slideOut);
        });

        settingButton = findViewById(R.id.setting_button);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuPage.this, SettingPage.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
                finish();

                Intent closeMainPageIntent = new Intent("CLOSE_MAIN_PAGE");
                sendBroadcast(closeMainPageIntent);
            }
        });

        profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuPage.this, ProfilePage.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
                finish();

                Intent closeMainPageIntent = new Intent("CLOSE_MAIN_PAGE");
                sendBroadcast(closeMainPageIntent);
            }
        });

        notificationButton = findViewById(R.id.notification_button);
        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuPage.this, NotificationPage.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
                finish();

                Intent closeMainPageIntent = new Intent("CLOSE_MAIN_PAGE");
                sendBroadcast(closeMainPageIntent);
            }
        });

        historyButton = findViewById(R.id.history_button);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuPage.this, HistoryPage.class);
                intent.putExtra("USERNAME", username);
                startActivity(intent);
                finish();

                Intent closeMainPageIntent = new Intent("CLOSE_MAIN_PAGE");
                sendBroadcast(closeMainPageIntent);
            }
        });
    }
}