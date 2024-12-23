package com.example.prj.Authen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prj.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChangePassword extends AppCompatActivity {
    EditText newPasswordET, confirmPasswordET;
    Button confirmButton;
    String newPassword, confirmPassword;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        newPasswordET = findViewById(R.id.new_password);
        confirmPasswordET = findViewById(R.id.confirm_new_password);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        confirmButton = findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validatePassword() | !validateConfirmPassword()) {
                    return;
                }
                newPassword = newPasswordET.getText().toString().trim();
                confirmPassword = confirmPasswordET.getText().toString().trim();
                updatePassword(username, newPassword);
            }
        });
    }

    private Boolean validatePassword() {
        String val = newPasswordET.getText().toString().trim();
        if (val.isEmpty()) {
            newPasswordET.setError(getString(R.string.empty_password));
            return false;
        } else if (val.length() < 8) {
            newPasswordET.setError(getString(R.string.rule_password));
            return false;
        } else {
            newPasswordET.setError(null);
            return true;
        }
    }

    private Boolean validateConfirmPassword() {
        String val = confirmPasswordET.getText().toString().trim();
        String password = newPasswordET.getText().toString().trim();
        if (val.isEmpty()) {
            confirmPasswordET.setError(getString(R.string.empty_confirm_pass));
            return false;
        } else if (!val.equals(password)) {
            confirmPasswordET.setError(getString(R.string.pass_didnt_match));
            return false;
        } else {
            confirmPasswordET.setError(null);
            return true;
        }
    }

    private void updatePassword(String username, String newPassword) {
        String hashedPassword = Encrypt.hashPassword(newPassword);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user");
        databaseReference.child(username).child("password").setValue(hashedPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(ChangePassword.this, SignIn.class);
                        startActivity(intent);
                        finish();
                    } else {
                    }
                });
    }
}