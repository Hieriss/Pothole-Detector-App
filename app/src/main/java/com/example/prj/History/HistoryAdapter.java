package com.example.prj.History;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prj.R;
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

        public ViewHolder(View view) {
            super(view);
            latitude = view.findViewById(R.id.latitude);
            longitude = view.findViewById(R.id.longitude);
            timestamp = view.findViewById(R.id.timestamp);
            id = view.findViewById(R.id.id);
        }
    }


}
