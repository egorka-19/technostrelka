package com.example.main_screen.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.main_screen.R;
import com.example.main_screen.api.EventMapper;
import com.example.main_screen.api.dto.EventItemDto;
import com.example.main_screen.model.PopularModel;
import com.example.main_screen.product_card;
import com.example.main_screen.utils.MediaUrlUtils;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Избранные мероприятия в профиле — карточки как у маршрутов в поиске (полная ширина списка).
 */
public class ProfileFavoriteEventAdapter extends RecyclerView.Adapter<ProfileFavoriteEventAdapter.Holder> {

    private List<EventItemDto> items = new ArrayList<>();

    public ProfileFavoriteEventAdapter() {
    }

    public void setItems(List<EventItemDto> list) {
        this.items = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.route_card_item_vertical, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Context ctx = holder.itemView.getContext();
        EventItemDto e = items.get(position);
        String title = e != null && e.name != null ? e.name : "";
        holder.name.setText(title);
        String img = MediaUrlUtils.resolveForApiClient(EventMapper.pickCoverImage(e));
        if (!TextUtils.isEmpty(img)) {
            Glide.with(ctx).load(img).error(R.drawable.izo).into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.izo);
        }
        holder.itemView.setOnClickListener(v -> {
            PopularModel pm = EventMapper.toPopular(e);
            pm.setFavorite(true);
            Intent intent = new Intent(ctx, product_card.class);
            intent.putExtra("detail", pm);
            ctx.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final RoundedImageView image;
        final TextView name;

        Holder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.route_image);
            name = itemView.findViewById(R.id.route_name);
        }
    }
}
