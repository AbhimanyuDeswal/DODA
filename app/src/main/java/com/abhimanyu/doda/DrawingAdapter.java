package com.abhimanyu.doda;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DrawingAdapter extends RecyclerView.Adapter<DrawingAdapter.DrawingViewHolder> {

    private List<Drawing> drawings = new ArrayList<>();

    public void setDrawings(List<Drawing> drawings) {
        this.drawings = drawings;
        notifyDataSetChanged();
    }
    public void addDrawing(Drawing drawing) {
        drawings.add(0, drawing);
        notifyItemInserted(0);
    }
    @NonNull
    @Override
    public DrawingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drawing, parent, false);
        return new DrawingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DrawingViewHolder holder, int position) {
        Drawing drawing = drawings.get(position);

        // Set the drawing name, thumbnail, addition time, and marker count
        holder.drawingNameTextView.setText(drawing.getName());
        holder.additionTimeTextView.setText(formatTime(drawing.getAdditionTime()));
        holder.markerCountTextView.setText(String.valueOf(drawing.getMarkerCount()));

        // Set a click listener to handle item clicks
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click event, e.g., navigate to drawing details activity
            }
        });
    }

    @Override
    public int getItemCount() {
        return drawings.size();
    }

    public static class DrawingViewHolder extends RecyclerView.ViewHolder {
        TextView drawingNameTextView;
        TextView additionTimeTextView;
        TextView markerCountTextView;

        public DrawingViewHolder(@NonNull View itemView) {
            super(itemView);
            drawingNameTextView = itemView.findViewById(R.id.textview_drawing_name);
            additionTimeTextView = itemView.findViewById(R.id.textview_addition_time);
            markerCountTextView = itemView.findViewById(R.id.textview_marker_count);
        }
    }

    private String formatTime(String timestamp) {
        // Implement the logic to format the timestamp in a social format
        // Example: "few minutes ago", "an hour ago", "at 6 PM", "a day ago", etc.
        // You can use a library like DateUtils or implement your own custom logic.
        // Return the formatted time string.
        return "";
    }
}
