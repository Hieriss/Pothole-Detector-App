package com.example.prj.Authen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prj.R;
import com.google.firebase.auth.FirebaseAuth;
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
    TextView forgot_password_description;

    private FirebaseAuth mAuth;

    public String userUsername, userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        back_button = findViewById(R.id.back_button);
        verify_button = findViewById(R.id.verify_button);
        forgot_password_username = findViewById(R.id.forgot_password_username);
        forgot_password_email = findViewById(R.id.forgot_password_email);
        forgot_password_description = findViewById(R.id.forgot_password_description);

        mAuth = FirebaseAuth.getInstance();

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
                    sendEmail();
                }
            }
        });
    }

    private Boolean validateUsername() {
        String val = forgot_password_username.getText().toString().trim();
        if (val.isEmpty()) {
            forgot_password_username.setError(getString(R.string.empty_username));
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
            forgot_password_email.setError(getString(R.string.empty_email));
            return false;
        } else if (!val.matches(emailPattern)) {
            forgot_password_email.setError(getString(R.string.invalid_email));
            return false;
        } else {
            forgot_password_email.setError(null);
            return true;
        }
    }

    private void sendEmail() {
        String userUsername = forgot_password_username.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    forgot_password_username.setError(null);
                    String emailFromDB = snapshot.child(userUsername).child("email").getValue(String.class);

                    mAuth.sendPasswordResetEmail(emailFromDB)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgotPassword.this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ForgotPassword.this, "Failed to send reset link", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    forgot_password_username.setError(getString(R.string.account_doesnt_exist));
                    forgot_password_username.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ForgotPassword.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void authenticateUserWithUsername(String username, String newPassword) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        // Find email from the database using the username
        database.child("user").child(username).child("email").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        String email = task.getResult().getValue(String.class);

                        // Sign in with email and new password
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.signInWithEmailAndPassword(email, newPassword)
                                .addOnCompleteListener(authTask -> {
                                    if (authTask.isSuccessful()) {
                                        updatePassword(username, newPassword); // Proceed to update password
                                    } else {
                                        Toast.makeText(this, "Login failed: " + authTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Username not found.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updatePassword(String userUsername, String newPassword) {
        String hashedPassword = Encrypt.hashPassword(newPassword);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user");
        databaseReference.child(userUsername).child("password").setValue(hashedPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPassword.this, getString(R.string.update_pass_success), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgotPassword.this, SignIn.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ForgotPassword.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}