package com.example.prj.Authen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prj.R;
import com.example.prj.Session.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VerifySignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SessionManager sessionManager;
    Button verifyButton, quitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(this);
        verifyButton = findViewById(R.id.verify_button);
        quitButton = findViewById(R.id.quit_button);

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        verifyButton.setOnClickListener(view -> {
            if (mAuth.getCurrentUser() != null) {
                mAuth.getCurrentUser().reload().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (mAuth.getCurrentUser().isEmailVerified()) {
                            Toast.makeText(VerifySignUp.this, getString(R.string.email_verify_success), Toast.LENGTH_SHORT).show();

                            Intent intent = getIntent();
                            String username = intent.getStringExtra("username");
                            String password = intent.getStringExtra("password");
                            String email = intent.getStringExtra("email");
                            String phone = intent.getStringExtra("phone");

                            pushInfo(username, password, email, phone);

                            // Set intro passed status
                            sessionManager.setIntroPassed(true);
                        } else {
                            Toast.makeText(VerifySignUp.this, getString(R.string.email_verify_warning), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(VerifySignUp.this, getString(R.string.fail_reload_user), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(VerifySignUp.this, getString(R.string.currently_logged), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pushInfo(String username, String password, String email, String phone) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("user");

        String hashedPassword = Encrypt.hashPassword(password);
        UserData userData = new UserData(username, hashedPassword, email, phone);
        reference.child(username).setValue(userData);

        Toast.makeText(VerifySignUp.this, getString(R.string.sign_up_success), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(VerifySignUp.this, SignIn.class);
        startActivity(intent);
    }
}