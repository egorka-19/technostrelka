package com.example.main_screen.adapter;

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
import com.example.main_screen.ArticleActivity;
import com.example.main_screen.R;
import com.example.main_screen.model.LearningItem;

import java.util.List;

public class LearningAdapter extends RecyclerView.Adapter<LearningAdapter.LearningViewHolder> {
    private List<LearningItem> items;
    private String type;
    private Context context;

    public LearningAdapter(String type) {
        this.type = type;
    }

    public void setItems(List<LearningItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LearningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_learning, parent, false);
        return new LearningViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LearningViewHolder holder, int position) {
        LearningItem item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.description.setText(item.getDescription());
        
        Glide.with(context)
                .load(item.getImageResourceId())
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            if (type.equals("article")) {
                Intent intent = new Intent(context, ArticleActivity.class);
                intent.putExtra("article", item);
                context.startActivity(intent);
            }
            // Здесь можно добавить обработку других типов (игры, видео)
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class LearningViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView description;

        LearningViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            title = itemView.findViewById(R.id.item_title);
            description = itemView.findViewById(R.id.item_description);
        }
    }
} 