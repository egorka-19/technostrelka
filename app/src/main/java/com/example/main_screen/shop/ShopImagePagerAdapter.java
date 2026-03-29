package com.example.main_screen.shop;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.main_screen.R;

import java.util.ArrayList;
import java.util.List;

public class ShopImagePagerAdapter extends RecyclerView.Adapter<ShopImagePagerAdapter.Holder> {

    private final List<String> urls;

    public ShopImagePagerAdapter(List<String> urls) {
        this.urls = urls != null ? new ArrayList<>(urls) : new ArrayList<>();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_photo_page, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        if (urls.isEmpty()) {
            holder.photo.setImageResource(R.drawable.izo);
            return;
        }
        String u = urls.get(position);
        if (!TextUtils.isEmpty(u)) {
            Glide.with(holder.photo.getContext()).load(u).error(R.drawable.izo).into(holder.photo);
        } else {
            holder.photo.setImageResource(R.drawable.izo);
        }
    }

    @Override
    public int getItemCount() {
        return urls.isEmpty() ? 1 : urls.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final ImageView photo;

        Holder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.shop_photo_full);
        }
    }
}
