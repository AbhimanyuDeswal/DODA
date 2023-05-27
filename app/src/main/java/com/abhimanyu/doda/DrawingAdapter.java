package com.abhimanyu.doda;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DrawingAdapter extends RecyclerView.Adapter<DrawingAdapter.DrawingViewHolder> {

    private Context context;
    private List<Drawing> drawings = new ArrayList<>();
    private static final long MINUTE_MILLIS = 60 * 1000;
    private static final long HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final long DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final long WEEK_MILLIS = 7 * DAY_MILLIS;

    public DrawingAdapter(Context context) {
        this.context = context;
    }


    public void setDrawings(List<Drawing> drawings) {
        this.drawings = drawings;
        notifyDataSetChanged();
    }
//    public void addDrawing(Drawing drawing) {
//        drawings.add(0, drawing);
//        notifyItemInserted(0);
//    }
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
        holder.markerCountTextView.setText(String.valueOf(drawing.getMarkerCount()));
        holder.additionTimeTextView.setText(formatTime(drawing.getAdditionTime()));

        // Set a click listener to handle item clicks
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click event, e.g., navigate to drawing details activity
                // Launch the DrawingDetailsActivity and pass the drawing ID
                Intent intent = new Intent(v.getContext(), DrawingDetailsActivity.class);
                intent.putExtra("imageURL", drawing.getImageUrl());
                v.getContext().startActivity(intent);
            }
        });
        Glide.with(context)
                .load(drawing.getImageUrl())
                .into(holder.thumbnailImageView);
    }

    @Override
    public int getItemCount() {
        return drawings.size();
    }

    public static class DrawingViewHolder extends RecyclerView.ViewHolder {
        TextView drawingNameTextView;
        TextView additionTimeTextView;
        TextView markerCountTextView;
        ImageView thumbnailImageView;

        public DrawingViewHolder(@NonNull View itemView) {
            super(itemView);
            drawingNameTextView = itemView.findViewById(R.id.textview_drawing_name);
            additionTimeTextView = itemView.findViewById(R.id.textview_addition_time);
            markerCountTextView = itemView.findViewById(R.id.textview_marker_count);
            thumbnailImageView = itemView.findViewById(R.id.imageview_thumbnail);
        }
    }

    private String formatTime(String timestamp) {
        long timeInMillis = Long.parseLong(timestamp);
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - timeInMillis;

        if (elapsedTime < MINUTE_MILLIS) {
            return "Just now";
        } else if (elapsedTime < HOUR_MILLIS) {
            int minutes = (int) (elapsedTime / MINUTE_MILLIS);
            return minutes + " minutes ago";
        } else if (elapsedTime < DAY_MILLIS) {
            int hours = (int) (elapsedTime / HOUR_MILLIS);
            return hours + " hours ago";
        } else if (elapsedTime < WEEK_MILLIS) {
            int days = (int) (elapsedTime / DAY_MILLIS);
            return days + " days ago";
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            return sdf.format(new Date(timeInMillis));
        }
    }

}
