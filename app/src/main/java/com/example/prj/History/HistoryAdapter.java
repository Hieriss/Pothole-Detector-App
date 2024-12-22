package com.example.prj.History;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prj.Map.MapViewPothole;
import com.example.prj.R;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private final List<PotholeModel> potholeList;

    public HistoryAdapter(List<PotholeModel> potholeList) {
        this.potholeList = potholeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PotholeModel model = potholeList.get(position);
        holder.currentX.setText(String.valueOf(model.getCurrentX()));
        holder.currentY.setText(String.valueOf(model.getCurrentY()));
        holder.currentZ.setText(String.valueOf(model.getCurrentZ()));
        holder.pitch.setText(String.valueOf(model.getPitch()));
        holder.roll.setText(String.valueOf(model.getRoll()));
        holder.speedKmh.setText(String.valueOf(model.getSpeedKmh()));
        holder.point.setText(String.valueOf(model.getPoint()));
        holder.username.setText(model.getUsername());
        holder.severity.setText(model.getSeverity());
        holder.latitude.setText(String.valueOf(model.getLatitude()));
        holder.longitude.setText(String.valueOf(model.getLongitude()));
        holder.timestamp.setText(model.getTimestamp());
        holder.viewOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MapViewPothole.class);
                intent.putExtra("LATITUDE", model.getLatitude());
                intent.putExtra("LONGITUDE", model.getLongitude());
                intent.putExtra("SEVERITY", model.getSeverity());
                intent.putExtra("TIMESTAMP", model.getTimestamp());
                intent.putExtra("POSITION", holder.getAdapterPosition());
                ((Activity) v.getContext()).startActivityForResult(intent, 1);
            }
        });
        if (model.getSeverity().equals("Low")) {
            holder.severity.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.green));
        } else if (model.getSeverity().equals("Medium")) {
            holder.severity.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.yellow));
        } else if (model.getSeverity().equals("High")) {
            holder.severity.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.red));
        } else {
            holder.severity.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.cyan)); // Default color
        }
    }

    @Override
    public int getItemCount() {
        return potholeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView currentX;
        public TextView currentY;
        public TextView currentZ;
        public TextView pitch;
        public TextView roll;
        public TextView speedKmh;
        public TextView point;
        public TextView username;
        public TextView severity;
        public TextView latitude;
        public TextView longitude;
        public TextView timestamp;
        public AppCompatButton viewOnMap;

        public ViewHolder(View view) {
            super(view);
            currentX = view.findViewById(R.id.currentX);
            currentY = view.findViewById(R.id.currentY);
            currentZ = view.findViewById(R.id.currentZ);
            pitch = view.findViewById(R.id.pitch);
            roll = view.findViewById(R.id.roll);
            speedKmh = view.findViewById(R.id.speedKmh);
            point = view.findViewById(R.id.point);
            username = view.findViewById(R.id.username);
            severity = view.findViewById(R.id.severity);
            latitude = view.findViewById(R.id.latitude);
            longitude = view.findViewById(R.id.longitude);
            timestamp = view.findViewById(R.id.timestamp);
            viewOnMap = view.findViewById(R.id.item_button);

        }
    }
}
