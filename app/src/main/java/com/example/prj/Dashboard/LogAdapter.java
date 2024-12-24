package com.example.prj.Dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prj.Notification.NotificationAdapter;
import com.example.prj.Notification.NotificationModel;
import com.example.prj.R;

import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> {
    private final List<LogModel> potholeList;

    public LogAdapter(List<LogModel> potholeList) {
        this.potholeList = potholeList;
    }

    @NonNull
    @Override
    public LogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_log_adapter, parent, false);
        return new LogAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogAdapter.ViewHolder holder, int position) {
        LogModel model = potholeList.get(position);
        holder.severity.setText(model.getSeverity());
        holder.timestamp.setText(model.getTimestamp());
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

        public ViewHolder(View view) {
            super(view);
            severity = view.findViewById(R.id.severity);
            timestamp = view.findViewById(R.id.timestamp);
        }
    }

    public void removeItem(int position) {
        potholeList.remove(position);
        notifyItemRemoved(position);
    }
}