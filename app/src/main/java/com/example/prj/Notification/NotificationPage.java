package com.example.prj.Notification;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prj.Dashboard.MainPage;
import com.example.prj.History.HistoryAdapter;
import com.example.prj.History.HistoryPage;
import com.example.prj.History.PotholeModel;
import com.example.prj.Map.StorePotholes;
import com.example.prj.R;
import com.mapbox.geojson.Point;

import java.util.ArrayList;
import java.util.List;

public class NotificationPage extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private AppCompatButton homeButton;
    private List<NotificationModel> sensorDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.notification_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NotificationAdapter(sensorDataList);
        recyclerView.setAdapter(adapter);

        homeButton = findViewById(R.id.setting_home_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotificationPage.this, MainPage.class);
                startActivity(intent);
                finish();
            }
        });

        populateSensorData();

        // Inside onCreate method
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                NotificationModel model = sensorDataList.get(position);
                sensorDataList.remove(position);
                adapter.notifyItemRemoved(position);
                StorePotholes.deleteNotificationData(NotificationPage.this, model);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
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

        // Load data from local storage
        List<NotificationModel> loadedData = StorePotholes.loadNotificationData(this);
        for (NotificationModel model : loadedData) {
            if (!sensorDataList.contains(model)) {
                sensorDataList.add(model);
                adapter.notifyItemInserted(sensorDataList.size() - 1);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        homeButton.performClick();
    }
}