package com.example.prj.Authen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.prj.Dashboard.MainPage;
import com.example.prj.R;
import com.example.prj.Session.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class SignIn extends AppCompatActivity {
    EditText signinUsername, signinPassword;
    Button signinButton, usernameForm, qrcodeForm;
    ImageView qrCodeImageView;
    TextView switchToSignUpText, forgotPasswordText;

    DatabaseReference databaseReference;

    SessionManager sessionManager;
    Handler handler = new Handler();
    String sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);

        sessionManager = new SessionManager(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("qrCodes");

        signinUsername = findViewById(R.id.signin_username);
        signinPassword = findViewById(R.id.signin_password);
        signinButton = findViewById(R.id.signin_button);
        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        qrcodeForm = findViewById(R.id.qrcode_button);
        usernameForm = findViewById(R.id.username_button);
        switchToSignUpText = findViewById(R.id.signin_text3);
        forgotPasswordText = findViewById(R.id.forgot_password);

        qrcodeForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrCodeImageView.setVisibility(View.VISIBLE);
                signinUsername.setVisibility(View.GONE);
                signinPassword.setVisibility(View.GONE);
                signinButton.setVisibility(View.GONE);

                qrcodeForm.setBackgroundResource(R.drawable.qr_username_choosen_background);
                usernameForm.setBackgroundResource(R.drawable.qr_username_not_choosen_background);

                if (sessionId == null) {
                    sessionId = UUID.randomUUID().toString();
                    long timestamp = System.currentTimeMillis();
                    databaseReference.child(sessionId).setValue(new Session("waiting_for_login", timestamp));
                }

                Bitmap qrCodeBitmap = generateQRCode(sessionId);
                qrCodeImageView.setImageBitmap(qrCodeBitmap);

                databaseReference.child(sessionId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String status = snapshot.child("status").getValue(String.class);
                        if ("logged_in".equals(status)) {
                            String usernameqr = snapshot.child("username").getValue(String.class);

                            Intent intent = new Intent(SignIn.this, MainPage.class);
                            intent.putExtra("USERNAMEQR", usernameqr);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SignIn.this, "Database Error", Toast.LENGTH_SHORT).show();
                    }
                });

                // Hide the QR code after 5 minutes
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        qrCodeImageView.setVisibility(View.GONE);
                        signinUsername.setVisibility(View.VISIBLE);
                        signinPassword.setVisibility(View.VISIBLE);
                        signinButton.setVisibility(View.VISIBLE);

                        usernameForm.setBackgroundResource(R.drawable.qr_username_choosen_background);
                        qrcodeForm.setBackgroundResource(R.drawable.qr_username_not_choosen_background);
                    }
                }, 300000); // 300,000 milliseconds = 5 minutes
            }
        });

        usernameForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrCodeImageView.setVisibility(View.GONE);
                signinUsername.setVisibility(View.VISIBLE);
                signinPassword.setVisibility(View.VISIBLE);
                signinButton.setVisibility(View.VISIBLE);

                usernameForm.setBackgroundResource(R.drawable.qr_username_choosen_background);
                qrcodeForm.setBackgroundResource(R.drawable.qr_username_not_choosen_background);
            }
        });

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUsername() | !validatePassword()) {
                    return;
                } else {
                    checkUser();
                }
            }
        });

        switchToSignUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
            }
        });

        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, ForgotPassword.class);
                startActivity(intent);
            }
        });
    }

    private Bitmap generateQRCode(String sessionId) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(sessionId, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Boolean validateUsername() {
        String val = signinUsername.getText().toString().trim();
        if (val.isEmpty()) {
            signinUsername.setError(getString(R.string.empty_username));
            return false;
        } else {
            signinUsername.setError(null);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = signinPassword.getText().toString().trim();
        if (val.isEmpty()) {
            signinPassword.setError(getString(R.string.empty_password));
            return false;
        }
        else {
            signinPassword.setError(null);
            return true;
        }
    }

    private void checkUser() {
        String userUsername = signinUsername.getText().toString().trim();
        String userPassword = signinPassword.getText().toString().trim();
        String hashedPassword = Encrypt.hashPassword(userPassword);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    signinUsername.setError(null);
                    String passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);

                    if (Objects.equals(passwordFromDB, hashedPassword)) {
                        sessionManager.createLoginSession(userUsername, userPassword);

                        signinUsername.setError(null);
                        Intent intent = new Intent(SignIn.this, MainPage.class);
                        intent.putExtra("USERNAME", userUsername);
                        startActivity(intent);
                        finish();
                    } else {
                        signinPassword.setError(getString(R.string.wrong_pass));
                        signinPassword.requestFocus();
                    }
                } else {
                    signinUsername.setError(getString(R.string.account_doesnt_exist));
                    signinUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SignIn.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class Session {
        public String status;
        public long timestamp;

        public Session(String status, long timestamp) {
            this.status = status;
            this.timestamp = timestamp;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SignIn.this, IntroPage.class);
        startActivity(intent);
    }
    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }
    private void setThemeMode(boolean isNightMode) {
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        SharedPreferences.Editor editor = getSharedPreferences("MODE", MODE_PRIVATE).edit();
        editor.putBoolean("night", isNightMode);
        editor.apply();
    }
    public void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        SharedPreferences sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        boolean nightMode = sharedPreferences.getBoolean("night", false);
        setLocale(language);
        setThemeMode(nightMode);
    }
}