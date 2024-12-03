package com.example.prj.Authen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prj.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends VerifyStatus {

    Button signupButton;
    TextView switchtosigninText;
    EditText signupUsername, signupPassword, signupConfirmpassword, signupEmail, signupPhone;
    FirebaseDatabase database;
    DatabaseReference reference;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        signupConfirmpassword = findViewById(R.id.signup_confirmpassword);
        signupEmail = findViewById(R.id.signup_email);
        signupPhone = findViewById(R.id.signup_phone);

        signupButton = findViewById(R.id.signup_button);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUsername() | !validatePassword() | !validateConfirmPassword() | !validateEmail() | !validatePhone()) {
                    return;
                } else {
                    createAccount();
                }
            }
        });

        switchtosigninText = findViewById(R.id.signup_text3);
        switchtosigninText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SignIn.class);
                view.getContext().startActivity(intent);
            }
        });
    }

    private Boolean validateUsername() {
        String val = signupUsername.getText().toString().trim();
        if (val.isEmpty()) {
            signupUsername.setError("Username can't be empty");
            return false;
        } else {
            signupUsername.setError(null);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = signupPassword.getText().toString().trim();
        if (val.isEmpty()) {
            signupPassword.setError("Password can't be empty");
            return false;
        } else if (val.length() < 8) {
            signupPassword.setError("Password must be at least 8 characters long");
            return false;
        } else {
            signupPassword.setError(null);
            return true;
        }
    }

    private Boolean validateConfirmPassword() {
        String val = signupConfirmpassword.getText().toString().trim();
        String password = signupPassword.getText().toString().trim();
        if (val.isEmpty()) {
            signupConfirmpassword.setError("Confirm password can't be empty");
            return false;
        } else if (!val.equals(password)) {
            signupConfirmpassword.setError("Password didn't match");
            return false;
        } else {
            signupConfirmpassword.setError(null);
            return true;
        }
    }

    private Boolean validateEmail() {
        String val = signupEmail.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (val.isEmpty()) {
            signupEmail.setError("Email can't be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            signupEmail.setError("Invalid email address");
            return false;
        } else {
            signupEmail.setError(null);
            return true;
        }
    }

    private Boolean validatePhone() {
        String val = signupPhone.getText().toString().trim();
        String phonePattern = "[0-9]{10}";
        if (val.isEmpty()) {
            signupPhone.setError("Phone Number can't be empty");
            return false;
        } else if (!val.matches(phonePattern)) {
            signupPhone.setError("Phone Number must be 10 digits long");
            return false;
        } else {
            signupPhone.setError(null);
            return true;
        }
    }

    private void createAccount() {
        final String email = signupEmail.getText().toString();
        final String password = signupPassword.getText().toString();
        final String username = signupUsername.getText().toString();
        final String phone = signupPhone.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    FirebaseUser user = mAuth.getCurrentUser();

                    if (user != null) {
                        user.sendEmailVerification()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(SignUp.this, "Verification email sent.", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(SignUp.this, VerifySignUp.class);
                                        intent.putExtra("username", username);
                                        intent.putExtra("password", password);
                                        intent.putExtra("email", email);
                                        intent.putExtra("phone", phone);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(SignUp.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(SignUp.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}