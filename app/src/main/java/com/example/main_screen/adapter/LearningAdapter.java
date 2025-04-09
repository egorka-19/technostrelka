package com.example.main_screen.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.main_screen.R;
import com.example.main_screen.data.LocalLearningData;
import com.example.main_screen.model.LearningItem;

import java.util.List;

public class LearningAdapter extends RecyclerView.Adapter<LearningAdapter.ViewHolder> {
    private static final String TAG = "LearningAdapter";
    private List<LearningItem> items;
    private Context context;

    public LearningAdapter(String type) {
        Log.d(TAG, "Creating adapter for type: " + type);
        switch (type) {
            case "game":
                this.items = LocalLearningData.getGames();
                break;
            case "video":
                this.items = LocalLearningData.getVideos();
                break;
            case "article":
                this.items = LocalLearningData.getArticles();
                break;
            default:
                this.items = LocalLearningData.getGames();
        }
        Log.d(TAG, "Items size: " + (items != null ? items.size() : 0));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder called");
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_learning, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder called for position: " + position);
        if (items != null && !items.isEmpty() && position < items.size()) {
            LearningItem item = items.get(position);
            Log.d(TAG, "Binding item: " + item.getTitle());
            
            holder.titleText.setText(item.getTitle());
            holder.descriptionText.setText(item.getDescription());
            
            // Установка изображения из ресурсов
            holder.imageView.setImageResource(item.getImageResourceId());
            
            holder.itemView.setOnClickListener(v -> {
                try {
                    Class<?> activityClass = Class.forName(item.getContentUrl());
                    Intent intent = new Intent(context, activityClass);
                    intent.putExtra("item_id", item.getId());
                    intent.putExtra("item_title", item.getTitle());
                    context.startActivity(intent);
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, "Activity not found: " + item.getContentUrl(), e);
                }
            });
        } else {
            Log.e(TAG, "Invalid position or empty items list. Position: " + position + 
                    ", Items size: " + (items != null ? items.size() : 0));
        }
    }

    @Override
    public int getItemCount() {
        int count = items != null ? items.size() : 0;
        Log.d(TAG, "getItemCount: " + count);
        return count;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleText;
        TextView descriptionText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("LearningAdapter", "ViewHolder constructor");
            imageView = itemView.findViewById(R.id.item_image);
            titleText = itemView.findViewById(R.id.item_title);
            descriptionText = itemView.findViewById(R.id.item_description);
            
            if (imageView == null || titleText == null || descriptionText == null) {
                Log.e("LearningAdapter", "Some views are null in ViewHolder");
            }
        }
    }
} 