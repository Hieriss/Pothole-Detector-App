package com.example.prj.Notification;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prj.History.HistoryAdapter;
import com.example.prj.History.PotholeModel;
import com.example.prj.Map.MapViewPothole;
import com.example.prj.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private final List<NotificationModel> potholeList;

    public NotificationAdapter(List<NotificationModel> potholeList) {
        this.potholeList = potholeList;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_recycle_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        NotificationModel model = potholeList.get(position);
        holder.severity.setText(model.getSeverity());
        holder.timestamp.setText(model.getTimestamp());
        holder.roadName.setText(model.getRoadName());
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
        public TextView severity;
        public TextView timestamp;
        public TextView roadName;

        public ViewHolder(View view) {
            super(view);
            severity = view.findViewById(R.id.severity);
            timestamp = view.findViewById(R.id.timestamp);
            roadName = view.findViewById(R.id.roadname);
        }
    }
}
