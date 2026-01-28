package com.example.main_screen.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.main_screen.R;
import com.example.main_screen.model.EventReviewModel;

import java.util.List;

public class EventReviewsAdapter extends RecyclerView.Adapter<EventReviewsAdapter.Holder> {

    private final Context context;
    private List<EventReviewModel> items;

    public EventReviewsAdapter(Context context, List<EventReviewModel> items) {
        this.context = context;
        this.items = items;
    }

    public void setItems(List<EventReviewModel> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_review, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        EventReviewModel r = items.get(position);

        holder.userName.setText(r.getUserName());
        holder.date.setText(r.getDate());
        holder.rating.setText(String.format(java.util.Locale.US, "%.1f", r.getRating()));
        holder.text.setText(r.getText());

        if (!TextUtils.isEmpty(r.getUserAvatarUrl())) {
            Glide.with(context)
                    .load(r.getUserAvatarUrl())
                    .placeholder(R.drawable.ava_reviews)
                    .error(R.drawable.ava_reviews)
                    .into(holder.avatar);
        } else {
            holder.avatar.setImageResource(R.drawable.ava_reviews);
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class Holder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView userName;
        TextView date;
        TextView rating;
        TextView text;

        Holder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.review_avatar);
            userName = itemView.findViewById(R.id.review_username);
            date = itemView.findViewById(R.id.review_date);
            rating = itemView.findViewById(R.id.review_rating);
            text = itemView.findViewById(R.id.review_text);
        }
    }
}

