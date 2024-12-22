package com.example.prj.Authen;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.example.prj.Session.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class VerifySignUp extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SessionManager sessionManager;
    Button verifyButton, quitButton;
    String otp;
    Long timeoutSeconds = 60L;
    TextView resendOtpTextView;
    String verificationCode;

    PhoneAuthProvider.ForceResendingToken  resendingToken;

    String username, password, email, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_verify);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(this);
        verifyButton = findViewById(R.id.verify_button);
        quitButton = findViewById(R.id.quit_button);
        resendOtpTextView = findViewById(R.id.resend_otp);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        email = intent.getStringExtra("email");
        phone = intent.getStringExtra("phone");

        if (phone == null || phone.isEmpty()) {
            Toast.makeText(this, "Phone number is missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sendOtp(phone, false);

        EditText otpBox1 = findViewById(R.id.otpBox1);
        EditText otpBox2 = findViewById(R.id.otpBox2);
        EditText otpBox3 = findViewById(R.id.otpBox3);
        EditText otpBox4 = findViewById(R.id.otpBox4);
        EditText otpBox5 = findViewById(R.id.otpBox5);
        EditText otpBox6 = findViewById(R.id.otpBox6);

        EditText[] otpBoxes = {otpBox1, otpBox2, otpBox3, otpBox4, otpBox5, otpBox6};

        // Add TextWatcher to each box
        for (int i = 0; i < otpBoxes.length; i++) {
            final int currentIndex = i;
            otpBoxes[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().isEmpty() && currentIndex < otpBoxes.length - 1) {
                        otpBoxes[currentIndex + 1].requestFocus();
                    } else if (s.toString().isEmpty() && currentIndex > 0) {
                        otpBoxes[currentIndex - 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText[] otpBoxes = {
                        findViewById(R.id.otpBox1),
                        findViewById(R.id.otpBox2),
                        findViewById(R.id.otpBox3),
                        findViewById(R.id.otpBox4),
                        findViewById(R.id.otpBox5),
                        findViewById(R.id.otpBox6)
                };
                otp = getOtp(otpBoxes);

                if (otp.length() == 6) {
                    String enteredOtp  = otp;
                    PhoneAuthCredential credential =  PhoneAuthProvider.getCredential(verificationCode,enteredOtp);
                    signUp(credential);
                }
            }
        });

        resendOtpTextView.setOnClickListener((v)->{
            sendOtp(phone,true);
        });
    }

    private String getOtp(EditText[] otpBoxes) {
        StringBuilder otp = new StringBuilder();
        for (EditText box : otpBoxes) {
            otp.append(box.getText().toString());
        }
        return otp.toString();
    }

    void sendOtp(String phoneNumber, boolean isResend) {
        Log.d("VerifySignUp", "Sending OTP to: " + phoneNumber);
        startResendTimer();
        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                Log.d("VerifySignUp", "Verification completed");
                                signUp(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Log.e("VerifySignUp", "Verification failed", e);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(s, forceResendingToken);
                                Log.d("VerifySignUp", "Code sent: " + s);
                                verificationCode = s;
                                resendingToken = forceResendingToken;
                            }
                        });
        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    void signUp(PhoneAuthCredential phoneAuthCredential){
        //login and go to next activity
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(VerifySignUp.this,SignIn.class);
                    pushInfo(username, password, email, phone);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void startResendTimer() {
        resendOtpTextView.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeoutSeconds--;
                runOnUiThread(() -> resendOtpTextView.setText("Resend OTP in " + timeoutSeconds + " seconds"));
                if (timeoutSeconds <= 0) {
                    timeoutSeconds = 60L;
                    timer.cancel();
                    runOnUiThread(() -> resendOtpTextView.setEnabled(true));
                }
            }
        }, 0, 1000);
    }

    private void pushInfo(String username, String password, String email, String phone) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("user");

        String hashedPassword = Encrypt.hashPassword(password);
        UserData userData = new UserData(username, hashedPassword, email, phone);
        reference.child(username).setValue(userData);

        Toast.makeText(VerifySignUp.this, getString(R.string.sign_up_success), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(VerifySignUp.this, SignIn.class);
        startActivity(intent);
    }
}