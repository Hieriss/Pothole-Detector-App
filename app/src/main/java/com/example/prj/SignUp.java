package com.example.prj;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ThemedSpinnerAdapter;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {

    Button signupButton;
    TextView switchtosigninText;
    EditText signupUsername, signupPassword, signupConfirmpassword, signupEmail, signupPhone;
    FirebaseDatabase database;
    DatabaseReference reference;

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
                    checkInfo();
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

    private void checkInfo() {
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("user");

        String username = signupUsername.getText().toString();
        String password = signupPassword.getText().toString();
        String confirmpassword = signupConfirmpassword.getText().toString();
        String email = signupEmail.getText().toString();
        String phone = signupPhone.getText().toString();

        String hashedPassword = Encrypt.hashPassword(password);
        HelperClass helperClass = new HelperClass(username, hashedPassword, email, phone);
        reference.child(username).setValue(helperClass);

        Toast.makeText(SignUp.this, "Signed up successfully!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SignUp.this, SignIn.class);
        startActivity(intent);
    }
}