package com.example.prj.History;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prj.Dashboard.MainPage;
import com.example.prj.Map.MapPage;
import com.example.prj.Map.StorePotholes;
import com.example.prj.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryPage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private AppCompatButton homeButton;
    private List<PotholeModel> sensorDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history_page);

        recyclerView = findViewById(R.id.history_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new HistoryAdapter(sensorDataList);
        recyclerView.setAdapter(adapter);

        homeButton = findViewById(R.id.setting_home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistoryPage.this, MainPage.class);
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
        // Load data from local storage
        List<PotholeModel> loadedData = StorePotholes.loadPotholeData(this);
        for (PotholeModel model : loadedData) {
            if (!sensorDataList.contains(model)) {
                sensorDataList.add(model);
                adapter.notifyItemInserted(sensorDataList.size() - 1);
            }
        }
    }
}