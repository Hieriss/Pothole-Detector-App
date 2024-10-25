package com.example.prj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.Objects;
import java.util.UUID;

public class SignIn extends AppCompatActivity {

    // UI components
    EditText signinUsername, signinPassword;
    Button signinButton, usernameForm, qrcodeForm;
    ImageView qrCodeImageView;
    TextView switchToSignUpText;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("qrCodes");

        // UI Initialization
        signinUsername = findViewById(R.id.signin_username);
        signinPassword = findViewById(R.id.signin_password);
        signinButton = findViewById(R.id.signin_button);
        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        qrcodeForm = findViewById(R.id.qrcode_button);
        usernameForm = findViewById(R.id.username_button);
        switchToSignUpText = findViewById(R.id.signin_text3);

        // QR Code Button Handler (Generate QR Code)
        qrcodeForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrCodeImageView.setVisibility(View.VISIBLE);
                signinUsername.setVisibility(View.GONE);
                signinPassword.setVisibility(View.GONE);
                signinButton.setVisibility(View.GONE);

                // Change the background of the button
                qrcodeForm.setBackgroundResource(R.drawable.qr_username_choosen_background);
                usernameForm.setBackgroundResource(R.drawable.qr_username_not_choosen_background);

                // Generate a session ID
                String sessionId = UUID.randomUUID().toString();
                Bitmap qrCodeBitmap = generateQRCode(sessionId);
                qrCodeImageView.setImageBitmap(qrCodeBitmap);

                // Save the session ID to Firebase
                databaseReference.child(sessionId).setValue("waiting_for_login");

                // Listen for changes in the session ID status
                databaseReference.child(sessionId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String status = snapshot.child("status").getValue(String.class);
                        if ("logged_in".equals(status)) {
                            // Retrieve the user credentials
                            String usernameqr = snapshot.child("username").getValue(String.class);

                            // Save the credentials in SharedPreferences
                            /*SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("username", username);
                            editor.apply();*/

                            // Login successful
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
            }
        });

        // Username and Password Form Button Handler
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

        // Sign-in Button Handler (Username/Password)
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

        // Switch to Sign-Up
        switchToSignUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
            }
        });
    }

    // QR Code Generation Function
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

    // Username Validation
    private Boolean validateUsername() {
        String val = signinUsername.getText().toString().trim();
        if (val.isEmpty()) {
            signinUsername.setError("Username can't be empty");
            return false;
        } else {
            signinUsername.setError(null);
            return true;
        }
    }

    // Password Validation
    private Boolean validatePassword() {
        String val = signinPassword.getText().toString().trim();
        if (val.isEmpty()) {
            signinPassword.setError("Password can't be empty");
            return false;
        } else {
            signinPassword.setError(null);
            return true;
        }
    }

    // Check User Credentials in Firebase
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
                        signinUsername.setError(null);
                        Intent intent = new Intent(SignIn.this, MainPage.class);
                        intent.putExtra("USERNAME", userUsername);
                        startActivity(intent);
                    } else {
                        signinPassword.setError("Invalid Credentials");
                        signinPassword.requestFocus();
                    }
                } else {
                    signinUsername.setError("Username doesn't exist");
                    signinUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SignIn.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}