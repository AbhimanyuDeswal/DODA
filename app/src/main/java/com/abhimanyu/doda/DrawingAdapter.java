package com.abhimanyu.doda;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DrawingAdapter extends RecyclerView.Adapter<DrawingAdapter.ViewHolder> {
    private List<Drawing> drawingList;
    private OnItemClickListener listener;

    public DrawingAdapter(List<Drawing> drawingList) {
        this.drawingList = this.drawingList;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public ImageView thumbnailImageView;
        public TextView additionTimeTextView;
        public TextView markerCountTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textview_drawing_name);
            thumbnailImageView = itemView.findViewById(R.id.imageview_thumbnail);
            additionTimeTextView = itemView.findViewById(R.id.textview_addition_time);
            markerCountTextView = itemView.findViewById(R.id.textview_marker_count);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_drawing, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Drawing drawing = drawingList.get(position);

        holder.nameTextView.setText(drawing.getName());
        // Set the thumbnail image using Glide or any other image loading library
        holder.additionTimeTextView.setText(drawing.getAdditionTime());
        holder.markerCountTextView.setText(String.valueOf(drawing.getMarkerCount()));
    }

    @Override
    public int getItemCount() {
        return drawingList.size();
    }
}

