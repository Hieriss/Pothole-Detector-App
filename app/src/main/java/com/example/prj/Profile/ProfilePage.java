package com.example.prj.Profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prj.Dashboard.MainPage;
import com.example.prj.R;
import com.example.prj.Session.SessionManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ProfilePage extends AppCompatActivity {
    TextView textUsername, textEmail, textPhone;
    ImageView profileUserImage;
    Button quitButton, editButton;
    public String username, email, phone, usernameText;

    SessionManager sessionManager;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new SessionManager(this);
        HashMap<String, String> userDetails = sessionManager.getUserDetails();
        usernameText = userDetails.get(SessionManager.KEY_NAME);

        username = getIntent().getStringExtra("USERNAME");
        textUsername = findViewById(R.id.profile_username);
        textUsername.setText(usernameText);

        textEmail = findViewById(R.id.profile_email);
        textPhone = findViewById(R.id.profile_phone);
        profileUserImage = findViewById(R.id.profile_user_image_rounded);

        quitButton = findViewById(R.id.setting_home_button);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfilePage.this, MainPage.class);
                startActivity(intent);
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user").child(usernameText);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    email = snapshot.child("email").getValue(String.class);
                    phone = snapshot.child("phone").getValue(String.class);

                    textEmail.setText(email);
                    textPhone.setText(phone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        editButton = findViewById(R.id.edit_image_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfilePage.this, EditImage.class);
                intent.putExtra("USERNAME", usernameText);
                startActivityForResult(intent, 1);
            }
        });

        downloadImage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            downloadImage();
        }
    }

    private void downloadImage() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(usernameText)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String encodedImage = documentSnapshot.getString("userImage");

                        if (encodedImage != null && !encodedImage.isEmpty()) {
                            byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
                            Bitmap imageBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

                            profileUserImage.setImageBitmap(imageBitmap);
                        }
                    }
                });
    }
}