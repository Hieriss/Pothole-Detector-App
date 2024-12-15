package com.example.prj.Authen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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

public class ForgotPassword extends DigitalVerify {
    Button back_button, verify_button, change_password_button;
    EditText forgot_password_email, forgot_password_username, forgot_password_newpw, forgot_password_confirmpw;
    TextView forgot_password_description;

    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    public String userUsername, userEmail;
    private static final int REQUEST_CODE_DIGITAL_VERIFY = 1;

    private static final String PREFS_NAME = "MyPrefs";
    private static final String IS_VERIFIED_KEY = "isVerified";
    private boolean isVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        back_button = findViewById(R.id.back_button);
        verify_button = findViewById(R.id.verify_button);
        change_password_button = findViewById(R.id.change_password_button);
        forgot_password_username = findViewById(R.id.forgot_password_username);
        forgot_password_email = findViewById(R.id.forgot_password_email);
        forgot_password_newpw = findViewById(R.id.forgot_password_newpw);
        forgot_password_confirmpw = findViewById(R.id.forgot_password_confirmpw);
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
                    checkInfo();
                }
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        isVerified = sharedPreferences.getBoolean(IS_VERIFIED_KEY, false);
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

    private void checkInfo() {
        userUsername = forgot_password_username.getText().toString().trim();
        userEmail = forgot_password_email.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    forgot_password_username.setError(null);
                    String emailFromDB = snapshot.child(userUsername).child("email").getValue(String.class);
                    String otpFromDB = snapshot.child(userUsername).child("otp").getValue(String.class);
                    if (Objects.equals(emailFromDB, userEmail)) {
                        forgot_password_username.setError(null);
                        if (otpFromDB != null) {
                            checkDigitalOTP();
                        } else {
                            Toast.makeText(ForgotPassword.this, getString(R.string.didnt_set_otp), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        forgot_password_email.setError(getString(R.string.email_doesnt_match));
                        forgot_password_email.requestFocus();
                    }
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

    public void checkDigitalOTP() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isChecked", false);
        editor.apply();

        Intent intent = new Intent(ForgotPassword.this, DigitalVerify.class);
        intent.putExtra("userUsername", forgot_password_username.getText().toString().trim());
        startActivityForResult(intent, REQUEST_CODE_DIGITAL_VERIFY);
    }

    public void changePassword() {
        forgot_password_username.setVisibility(View.GONE);
        forgot_password_email.setVisibility(View.GONE);
        verify_button.setVisibility(View.GONE);

        forgot_password_newpw.setVisibility(View.VISIBLE);
        forgot_password_confirmpw.setVisibility(View.VISIBLE);
        change_password_button.setVisibility(View.VISIBLE);

        forgot_password_description.setText(getString(R.string.reset_pass_success));

        change_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateNewPassword() | !validateConfirmPassword()) {
                    return;
                } else {
                    updatePassword();
                }
            }
        });
    }

    private Boolean validateNewPassword() {
        String val = forgot_password_newpw.getText().toString().trim();
        if (val.isEmpty()) {
            forgot_password_newpw.setError(getString(R.string.empty_password));
            return false;
        } else if (val.length() < 8) {
            forgot_password_newpw.setError(getString(R.string.rule_password));
            return false;
        } else {
            forgot_password_newpw.setError(null);
            return true;
        }
    }

    private Boolean validateConfirmPassword() {
        String val = forgot_password_confirmpw.getText().toString().trim();
        String password = forgot_password_newpw.getText().toString().trim();
        if (val.isEmpty()) {
            forgot_password_confirmpw.setError(getString(R.string.empty_confirm_pass));
            return false;
        } else if (!val.equals(password)) {
            forgot_password_confirmpw.setError(getString(R.string.pass_didnt_match));
            return false;
        } else {
            forgot_password_confirmpw.setError(null);
            return true;
        }
    }

    private void updatePassword() {
        String userUsername = forgot_password_username.getText().toString().trim();
        String userPassword = forgot_password_newpw.getText().toString().trim();
        String hashedPassword = Encrypt.hashPassword(userPassword);

        databaseReference = FirebaseDatabase.getInstance().getReference("user");
        databaseReference.child(userUsername).child("password").setValue(hashedPassword);

        Toast.makeText(ForgotPassword.this, getString(R.string.update_pass_success), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ForgotPassword.this, SignIn.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DIGITAL_VERIFY) {
            if (resultCode == RESULT_OK) {
                isVerified = true;
                SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(IS_VERIFIED_KEY, isVerified);
                editor.apply();
                changePassword();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isVerified = false;
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(IS_VERIFIED_KEY, isVerified);
        editor.apply();
    }
}