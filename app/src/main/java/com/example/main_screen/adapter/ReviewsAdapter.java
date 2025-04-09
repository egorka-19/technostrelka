package com.example.main_screen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.main_screen.R;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {
    Context context;
    List<Reviews1> reviews;

    public ReviewsAdapter(Context context, List<Reviews1> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View reviewsItems = LayoutInflater.from(context).inflate(R.layout.reviews_forrecycle, parent, false);
        return new ReviewsViewHolder(reviewsItems);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewsViewHolder holder, int position) {
        int imageId = context.getResources().getIdentifier("rev_" + reviews.get(position).getAva(), "drawable", context.getPackageName());
        holder.ava.setImageResource(imageId);
        holder.nickname.setText(reviews.get(position).getNickname());
        holder.mainText.setText(reviews.get(position).getMainText());

    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static final class ReviewsViewHolder extends RecyclerView.ViewHolder {
        TextView nickname, mainText;
        ImageView ava;
        public ReviewsViewHolder(@NonNull View itemView) {
            super(itemView);

            nickname = itemView.findViewById(R.id.nickname);
            mainText = itemView.findViewById(R.id.textmain);
            ava = itemView.findViewById(R.id.ava);
        }
    }
}
