package com.example.prj.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prj.Authen.Encrypt;
import com.example.prj.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SetDigital extends AppCompatActivity {
    Button setButton;
    public String otp, hashedOTP;
    public String username;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_set_digital);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USERNAME")) {
            username = intent.getStringExtra("USERNAME");
            // Use the username as needed
        } else {
            Toast.makeText(this, "No username provided", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if no username is provided
        }

        EditText otpBox1 = findViewById(R.id.otpBox1);
        EditText otpBox2 = findViewById(R.id.otpBox2);
        EditText otpBox3 = findViewById(R.id.otpBox3);
        EditText otpBox4 = findViewById(R.id.otpBox4);
        EditText otpBox5 = findViewById(R.id.otpBox5);
        EditText otpBox6 = findViewById(R.id.otpBox6);

        EditText[] otpBoxes = {otpBox1, otpBox2, otpBox3, otpBox4, otpBox5, otpBox6};

        setButton = findViewById(R.id.set_button);

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

        setButton.setOnClickListener(new View.OnClickListener() {
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
                otp = getOtp(otpBoxes);

                // Do something with the OTP (e.g., validate or send to server)
                if (otp.length() == 6) {
                    pushDigitalOTP();
                }
            }
        });
    }
    private String getOtp(EditText[] otpBoxes) {
        StringBuilder otp = new StringBuilder();
        for (EditText box : otpBoxes) {
            otp.append(box.getText().toString());
        }
        return otp.toString();
    }

    public void pushDigitalOTP() {
        hashedOTP = Encrypt.hashPassword(otp);

        databaseReference = FirebaseDatabase.getInstance().getReference("user");
        databaseReference.child(username).child("otp").setValue(hashedOTP);

        Toast.makeText(SetDigital.this, "Digital OTP set successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}