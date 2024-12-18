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

import com.example.prj.Authen.SignIn;
import com.example.prj.R;
import com.example.prj.Session.SessionManager;

public class LogoutRequest extends AppCompatActivity {
    Button backButton, confirmButton, declineButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_logout_request);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backButton = findViewById(R.id.quit_button);
        backButton.setOnClickListener(view -> finish());

        confirmButton = findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionManager sessionManager = new SessionManager(LogoutRequest.this);
                sessionManager.logoutUser();

                // Send broadcast to destroy MainPage activity
                Intent broadcastIntent = new Intent("com.example.prj.LOGOUT");
                sendBroadcast(broadcastIntent);

                // Navigate back to the login screen
                Intent intent = new Intent(LogoutRequest.this, SignIn.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

                Intent closeSettingPageIntent = new Intent("CLOSE_SETTING_PAGE");
                sendBroadcast(closeSettingPageIntent);
            }
        });

        declineButton = findViewById(R.id.decline_button);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}