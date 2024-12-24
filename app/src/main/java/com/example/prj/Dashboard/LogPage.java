package com.example.prj.Dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
    private LinearLayout filterContainer;
    private RelativeLayout lowButtonLayout, mediumButtonLayout, highButtonLayout;
    private AppCompatButton lowButton, mediumButton, highButton, filterButton;
    private int filterType = -1;

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

        filterContainer = findViewById(R.id.filter_container);
        filterButton = findViewById(R.id.filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!filterContainer.isShown()) {
                    filterContainer.setVisibility(View.VISIBLE);
                }
                else {
                    filterContainer.setVisibility(View.GONE);
                }
            }
        });

        lowButtonLayout = findViewById(R.id.low_button_layout);
        lowButton = findViewById(R.id.low_button);
        mediumButtonLayout = findViewById(R.id.medium_button_layout);
        mediumButton = findViewById(R.id.medium_button);
        highButtonLayout = findViewById(R.id.high_button_layout);
        highButton = findViewById(R.id.high_button);

        lowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filterType == -1) {
                    filterType = 0;
                    lowButtonLayout.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_choosen));
                    mediumButtonLayout.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                    highButtonLayout.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                }
                else if (filterType == 0) {
                    filterType = -1;
                    lowButtonLayout.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                }
                else {
                    filterType = 0;
                    lowButtonLayout.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_choosen));
                    mediumButtonLayout.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                    highButtonLayout.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                }
                populateSensorData();
            }
        });

        mediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filterType == -1) {
                    filterType = 1;
                    lowButtonLayout.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                    mediumButtonLayout.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_choosen));
                    highButtonLayout.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                }
                else if (filterType == 1) {
                    filterType = -1;
                    mediumButtonLayout.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                }
                else {
                    filterType = 1;
                    lowButtonLayout.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                    mediumButtonLayout.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_choosen));
                    highButtonLayout.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                }
                populateSensorData();
            }
        });

        highButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filterType == -1) {
                    filterType = 2;
                    lowButtonLayout.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                    mediumButtonLayout.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                    highButtonLayout.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_choosen));
                }
                else if (filterType == 2) {
                    filterType = -1;
                    highButtonLayout.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                }
                else {
                    filterType = 2;
                    lowButtonLayout.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                    mediumButtonLayout.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_not_choosen));
                    highButtonLayout.setBackground(getResources().getDrawable(R.drawable.driving_profile_button_background_choosen));
                }
                populateSensorData();
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
                            if (filterType == -1 ||
                                    (filterType == 0 && "Low".equals(severity)) ||
                                    (filterType == 1 && "Medium".equals(severity)) ||
                                    (filterType == 2 && "High".equals(severity))) {
                                if (!sensorDataList.contains(logModel)) {
                                    sensorDataList.add(logModel);
                                    adapter.notifyItemInserted(sensorDataList.size() - 1);
                                }
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