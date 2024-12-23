package com.example.prj.Authen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prj.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;

public class SignUp extends AppCompatActivity {
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

        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
        );

        mAuth = FirebaseAuth.getInstance();

        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        signupConfirmpassword = findViewById(R.id.signup_confirmpassword);
        signupEmail = findViewById(R.id.signup_email);
        signupPhone = findViewById(R.id.signup_phone);

        String email = signupEmail.getText().toString();
        String password = signupPassword.getText().toString();
        String username = signupUsername.getText().toString();
        String phone = signupPhone.getText().toString();

        signupButton = findViewById(R.id.signup_button);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUsername() | !validatePassword() | !validateConfirmPassword() | !validateEmail() | !validatePhone()) {
                    return;
                } else {
                    String username = signupUsername.getText().toString().trim();
                    String password = signupPassword.getText().toString().trim();
                    String email = signupEmail.getText().toString().trim();
                    String phone = formatPhoneNumber(signupPhone.getText().toString().trim());

                    Intent intent = new Intent(SignUp.this, SignUpVerify.class);
                    intent.putExtra("username", username);
                    intent.putExtra("password", password);
                    intent.putExtra("email", email);
                    intent.putExtra("phone", phone);
                    startActivity(intent);
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
            signupUsername.setError(getString(R.string.empty_username));
            return false;
        } else {
            signupUsername.setError(null);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = signupPassword.getText().toString().trim();
        if (val.isEmpty()) {
            signupPassword.setError(getString(R.string.empty_password));
            return false;
        } else if (val.length() < 8) {
            signupPassword.setError(getString(R.string.rule_password));
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
            signupConfirmpassword.setError(getString(R.string.empty_confirm_pass));
            return false;
        } else if (!val.equals(password)) {
            signupConfirmpassword.setError(getString(R.string.pass_didnt_match));
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
            signupEmail.setError(getString(R.string.empty_email));
            return false;
        } else if (!val.matches(emailPattern)) {
            signupEmail.setError(getString(R.string.invalid_email));
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
            signupPhone.setError(getString(R.string.empty_phonenumber));
            return false;
        } else if (!val.matches(phonePattern)) {
            signupPhone.setError(getString(R.string.rule_phone));
            return false;
        } else {
            signupPhone.setError(null);
            return true;
        }
    }

    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("0")) {
            //return "+84" + phoneNumber;
            return "+84" + phoneNumber.substring(1);
        }
        return phoneNumber;
    }
}