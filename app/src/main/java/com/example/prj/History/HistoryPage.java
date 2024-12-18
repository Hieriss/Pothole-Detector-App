package com.example.prj.History;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prj.AccountSettingPage;
import com.example.prj.Dashboard.MainPage;
import com.example.prj.R;
import com.example.prj.SettingPage;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.example.prj.Map.LocationRetriever;



public class HistoryPage extends AppCompatActivity {

    private RecyclerView recyclerView;
    HistoryAdapter adapter; // Create Object of the Adapter class
    DatabaseReference mbase;
    AppCompatButton homeButton;
    DataSnapshot dataSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history_page);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);

        mbase = FirebaseDatabase.getInstance().getReference("sensorData");
        Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
        recyclerView = findViewById(R.id.history_recycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        homeButton = findViewById(R.id.setting_home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistoryPage.this, MainPage.class);
                startActivity(intent);
            }
        });
    }

    @Override protected void onStart()
    {
        super.onStart();
        FirebaseRecyclerOptions<PotholeModel> options
                = new FirebaseRecyclerOptions.Builder<PotholeModel>()
                .setQuery(mbase, PotholeModel.class)
                .build();

        System.out.println("DatabaseReference: " + options);

        adapter = new HistoryAdapter(options);
        // Connecting Adapter class with the Recycler view*/
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    // Function to tell the app to stop getting
    // data from database on stopping of the activity
    @Override protected void onStop()
    {
        super.onStop();
        adapter.stopListening();
    }

}