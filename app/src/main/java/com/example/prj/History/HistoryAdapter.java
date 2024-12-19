package com.example.prj.History;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prj.Dashboard.MenuPage;
import com.example.prj.Map.MapPage;
import com.example.prj.R;
import com.example.prj.Setting.SettingPage;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class HistoryAdapter extends FirebaseRecyclerAdapter<PotholeModel, HistoryAdapter.ViewHolder> {

    public HistoryAdapter(@NonNull FirebaseRecyclerOptions<PotholeModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull PotholeModel model) {
        holder.latitude.setText(String.valueOf(model.getLatitude()));
        holder.longitude.setText(String.valueOf(model.getLongitude()));
        holder.timestamp.setText(model.getTimestamp());
        holder.id.setText(model.getId());
        holder.viewOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MapPage.class);
                intent.putExtra("LATITUDE", model.getLatitude());
                intent.putExtra("LONGITUDE", model.getLongitude());
                v.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item, parent, false);

        return new ViewHolder(view);
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView latitude;
        public TextView longitude;
        public TextView timestamp;
        public TextView id;
        public AppCompatButton viewOnMap;

        public ViewHolder(View view) {
            super(view);
            latitude = view.findViewById(R.id.latitude);
            longitude = view.findViewById(R.id.longitude);
            timestamp = view.findViewById(R.id.timestamp);
            id = view.findViewById(R.id.id);
            viewOnMap = view.findViewById(R.id.item_button);
        }
    }


}
