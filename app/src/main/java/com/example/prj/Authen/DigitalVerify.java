package com.example.prj.Authen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.prj.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class DigitalVerify extends AppCompatActivity {
    Button verifyButton, quitButton;
    String otp;
    String userUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_digital_verify);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText otpBox1 = findViewById(R.id.otpBox1);
        EditText otpBox2 = findViewById(R.id.otpBox2);
        EditText otpBox3 = findViewById(R.id.otpBox3);
        EditText otpBox4 = findViewById(R.id.otpBox4);
        EditText otpBox5 = findViewById(R.id.otpBox5);
        EditText otpBox6 = findViewById(R.id.otpBox6);

        EditText[] otpBoxes = {otpBox1, otpBox2, otpBox3, otpBox4, otpBox5, otpBox6};

        verifyButton = findViewById(R.id.verify_button);
        quitButton = findViewById(R.id.quit_button);

        Intent intent = getIntent();
        if (intent != null) {
            otp = intent.getStringExtra("otp");
            userUsername = intent.getStringExtra("userUsername");
        }

        // Add TextWatcher to each box
        for (int i = 0; i < otpBoxes.length; i++) {
            final int currentIndex = i;
            otpBoxes[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().isEmpty() && currentIndex < otpBoxes.length - 1) {
                        // Move to the next box
                        otpBoxes[currentIndex + 1].requestFocus();
                    } else if (s.toString().isEmpty() && currentIndex > 0) {
                        // Go back to the previous box
                        otpBoxes[currentIndex - 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Define the array of OTP boxes
                EditText[] otpBoxes = {
                        findViewById(R.id.otpBox1),
                        findViewById(R.id.otpBox2),
                        findViewById(R.id.otpBox3),
                        findViewById(R.id.otpBox4),
                        findViewById(R.id.otpBox5),
                        findViewById(R.id.otpBox6)
                };

                // Get the full OTP
                String otp = getOtp(otpBoxes);

                // Do something with the OTP (e.g., validate or send to server)
                if (otp.length() == 6) {
                    validateOtp(otpBoxes);
                } else {
                    Toast.makeText(DigitalVerify.this, getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getOtp(EditText[] otpBoxes) {
        StringBuilder insertedOTP = new StringBuilder();
        for (EditText box : otpBoxes) {
            insertedOTP.append(box.getText().toString());
        }
        return insertedOTP.toString();
    }

    private void validateOtp(EditText[] otpBoxes) {
        String insertedOTP = getOtp(otpBoxes);

        if (insertedOTP.equals(otp)) {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isChecked", true);
            editor.apply();

            Intent resultIntent = new Intent();
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
}