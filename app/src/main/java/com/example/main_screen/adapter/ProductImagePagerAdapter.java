package com.example.main_screen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.main_screen.R;

import java.util.List;

/**
 * Адаптер для ViewPager2 с фотографиями места.
 * TODO: заменить source на список фотографий из Firebase.
 */
public class ProductImagePagerAdapter extends RecyclerView.Adapter<ProductImagePagerAdapter.Holder> {

    private final Context context;
    private final List<Object> images; // String (url) или Integer (resId)

    public ProductImagePagerAdapter(Context context, List<Object> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_photo, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Object item = images.get(position);
        if (item instanceof Integer) {
            holder.photo.setImageResource((Integer) item);
        } else if (item instanceof String) {
            Glide.with(context)
                    .load((String) item)
                    .placeholder(R.drawable.izo)
                    .error(R.drawable.izo)
                    .into(holder.photo);
        } else {
            holder.photo.setImageResource(R.drawable.izo);
        }
    }

    @Override
    public int getItemCount() {
        return images != null ? images.size() : 0;
    }

    static class Holder extends RecyclerView.ViewHolder {
        ImageView photo;

        Holder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.photo);
        }
    }
}

