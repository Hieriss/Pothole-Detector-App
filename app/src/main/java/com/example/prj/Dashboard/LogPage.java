package com.example.prj.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prj.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LogPage extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LogAdapter adapter;
    private AppCompatButton homeButton;
    private List<LogModel> sensorDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.log_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new LogAdapter(sensorDataList);
        recyclerView.setAdapter(adapter);

        homeButton = findViewById(R.id.setting_home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogPage.this, MainPage.class);
                startActivity(intent);
                finish();
            }
        });

        populateSensorData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            int position = data.getIntExtra("POSITION", -1);
            if (position != -1) {
                sensorDataList.remove(position);
                adapter.notifyItemRemoved(position);
            }
        }
    }

    private void populateSensorData() {
        // Clear the existing data
        sensorDataList.clear();
        adapter.notifyDataSetChanged();

        // Reference to Firebase Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot counterSnapshot : userSnapshot.child("counter").getChildren()) {
                        for (DataSnapshot dateSnapshot : counterSnapshot.getChildren()) {
                            String formattedDate = dateSnapshot.getKey();
                            String severity = dateSnapshot.getValue(String.class);
                            LogModel logModel = new LogModel(formattedDate, severity);
                            if (!sensorDataList.contains(logModel)) {
                                sensorDataList.add(logModel);
                                adapter.notifyItemInserted(sensorDataList.size() - 1);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        homeButton.performClick();
    }
}