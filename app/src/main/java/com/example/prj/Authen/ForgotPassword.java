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
import com.example.prj.Setting.SettingPage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ForgotPassword extends AppCompatActivity {
    Button back_button, verify_button;
    EditText forgot_password_phone, forgot_password_username;
    TextView forgot_password_description;

    private FirebaseAuth mAuth;

    public String userUsername, userPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        back_button = findViewById(R.id.back_button);
        verify_button = findViewById(R.id.verify_button);
        forgot_password_username = findViewById(R.id.forgot_password_username);
        forgot_password_phone = findViewById(R.id.forgot_password_phone);
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
                Intent intent = new Intent(ForgotPassword.this, SettingPage.class);
                startActivity(intent);
                finish();
            }
        });

        verify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUsername() | !validatePhone()) {
                    return;
                } else {
                    sendOTP();
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

    private Boolean validatePhone() {
        String val = forgot_password_phone.getText().toString().trim();
        String phonePattern = "[0-9]{10}";
        if (val.isEmpty()) {
            forgot_password_phone.setError(getString(R.string.empty_phonenumber));
            return false;
        } else if (!val.matches(phonePattern)) {
            forgot_password_phone.setError(getString(R.string.rule_phone));
            return false;
        } else {
            forgot_password_phone.setError(null);
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

    private void sendOTP() {
        String userUsername = forgot_password_username.getText().toString().trim();
        String userPhone = formatPhoneNumber(forgot_password_phone.getText().toString().trim());

        Intent intent = new Intent(ForgotPassword.this, ForgotPasswordVerify.class);
        intent.putExtra("username", userUsername);
        intent.putExtra("phone", userPhone);
        startActivity(intent);
    }

    /*private void updatePassword(String userUsername, String newPassword) {
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
    }*/
}