package com.example.prj;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ForgotPassword extends AppCompatActivity {
    Button back_button, verify_button;
    EditText forgot_password_email, forgot_password_username;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        back_button = findViewById(R.id.back_button);
        verify_button = findViewById(R.id.verify_button);
        forgot_password_username = findViewById(R.id.forgot_password_username);
        forgot_password_email = findViewById(R.id.forgot_password_email);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPassword.this, SignIn.class);
                startActivity(intent);
            }
        });

        verify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUsername() | !validateEmail()) {
                    return;
                } else {
                    checkInfo();
                }
            }
        });

    }

    private Boolean validateUsername() {
        String val = forgot_password_username.getText().toString().trim();
        if (val.isEmpty()) {
            forgot_password_username.setError("Username can't be empty");
            return false;
        } else {
            forgot_password_username.setError(null);
            return true;
        }
    }

    private Boolean validateEmail() {
        String val = forgot_password_email.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (val.isEmpty()) {
            forgot_password_email.setError("Email can't be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            forgot_password_email.setError("Invalid email address");
            return false;
        } else {
            forgot_password_email.setError(null);
            return true;
        }
    }

    private void checkInfo() {
        String userUsername = forgot_password_username.getText().toString().trim();
        String userEmail = forgot_password_email.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    forgot_password_username.setError(null);
                    String emailFromDB = snapshot.child(userUsername).child("email").getValue(String.class);

                    if (Objects.equals(emailFromDB, userEmail)) {
                        forgot_password_username.setError(null);
                        Intent intent = new Intent(ForgotPassword.this, MainPage.class);
                        startActivity(intent);
                    } else {
                        forgot_password_email.setError("Wrong email address");
                        forgot_password_email.requestFocus();
                    }
                } else {
                    forgot_password_username.setError("Username doesn't exist");
                    forgot_password_username.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ForgotPassword.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

}