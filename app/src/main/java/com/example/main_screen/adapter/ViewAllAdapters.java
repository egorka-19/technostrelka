package com.example.main_screen.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.main_screen.api.ApiClient;
import com.example.main_screen.api.TokenStore;
import com.example.main_screen.model.ViewAllModel;
import com.example.main_screen.R;
import com.example.main_screen.product_card;

import java.util.List;
import java.util.concurrent.Executors;

public class ViewAllAdapters extends RecyclerView.Adapter<ViewAllAdapters.ViewHolder> {
    Context context;
    List<ViewAllModel> list;

    public ViewAllAdapters(Context context, List<ViewAllModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewAllModel item = list.get(position);

        // Загрузка изображения
        if (item.getImg_url() != null && !item.getImg_url().isEmpty()) {
            Glide.with(context)
                    .load(item.getImg_url())
                    .error(R.drawable.izo)
                    .into(holder.eventImage);
        } else {
            holder.eventImage.setImageResource(R.drawable.izo);
        }

        // Название события
        holder.eventName.setText(item.getName());

        applyRatingFromModel(item, holder);

        // Возрастные ограничения
        String age = item.getAge();
        if (!TextUtils.isEmpty(age)) {
            if (age.equals("0+") || age.equals("Нет ограничений")) {
                holder.eventAge.setText("Нет ограничений по возрасту");
            } else {
                holder.eventAge.setText("Возраст: " + age);
            }
        } else {
            holder.eventAge.setText("Нет ограничений по возрасту");
        }

        // Часы работы
        String data = item.getData();
        if (!TextUtils.isEmpty(data)) {
            holder.eventSchedule.setVisibility(View.VISIBLE);
            holder.eventSchedule.setText(data);
        } else {
            holder.eventSchedule.setVisibility(View.GONE);
        }

        // Адрес
        String place = item.getPlace();
        if (!TextUtils.isEmpty(place)) {
            holder.eventAddress.setText(place);
        } else {
            holder.eventAddress.setText("Адрес не указан");
        }

        checkFavoriteStatus(item, holder);

        // Обработчик клика на карточку
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, product_card.class);
            intent.putExtra("detail", item);
            context.startActivity(intent);
        });

        // Обработчик клика на кнопку "Подробнее"
        holder.detailsButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, product_card.class);
            intent.putExtra("detail", item);
            context.startActivity(intent);
        });

        // Обработчик клика на избранное
        holder.favoriteIcon.setOnClickListener(v -> {
            toggleFavorite(item, holder);
        });
    }

    private void checkFavoriteStatus(ViewAllModel item, ViewHolder holder) {
        if (!TokenStore.get(context).hasAccessToken()) {
            setFavoriteIconState(holder, false);
            return;
        }
        setFavoriteIconState(holder, item.isFavorite());
    }

    /**
     * Установка состояния иконки избранного с заливкой красным цветом
     */
    private void setFavoriteIconState(ViewHolder holder, boolean isFavorite) {
        if (isFavorite) {
            // Сердечко по контуру, залитое красным цветом FF0033
            holder.favoriteIcon.setImageResource(R.drawable.health);
            holder.favoriteIcon.setColorFilter(Color.parseColor("#FF0033"), PorterDuff.Mode.SRC_IN);
            holder.favoriteIcon.setBackground(null);
            holder.favoriteIcon.setTag("favorite");
        } else {
            // Обычное состояние - контур без заливки
            holder.favoriteIcon.setImageResource(R.drawable.health);
            holder.favoriteIcon.setColorFilter(null);
            holder.favoriteIcon.setBackground(null);
            holder.favoriteIcon.setTag(null);
        }
    }

    private void applyRatingFromModel(ViewAllModel item, ViewHolder holder) {
        String r = item.getRating();
        if (r == null || r.isEmpty()) {
            r = "0";
        }
        holder.eventRating.setText(r);
        holder.starIcon.setVisibility(View.VISIBLE);
        holder.eventRating.setVisibility(View.VISIBLE);
    }

    private void toggleFavorite(ViewAllModel item, ViewHolder holder) {
        if (item.getServerId() == null || item.getServerId().isEmpty()) {
            return;
        }
        if (!TokenStore.get(context).hasAccessToken()) {
            return;
        }

        boolean isCurrentlyFavorite = holder.favoriteIcon.getTag() != null && holder.favoriteIcon.getTag().equals("favorite");

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                if (isCurrentlyFavorite) {
                    ApiClient.get(context).removeFavorite(item.getServerId()).execute();
                } else {
                    ApiClient.get(context).addFavorite(item.getServerId()).execute();
                }
                if (context instanceof AppCompatActivity) {
                    ((AppCompatActivity) context).runOnUiThread(() -> {
                        boolean newFav = !isCurrentlyFavorite;
                        setFavoriteIconState(holder, newFav);
                        item.setFavorite(newFav);
                    });
                }
            } catch (Exception e) {
                android.util.Log.e("Api", "favorite toggle", e);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        com.makeramen.roundedimageview.RoundedImageView eventImage;
        ImageView favoriteIcon, starIcon;
        TextView eventName, eventRating, eventAge, eventSchedule, eventAddress, detailsButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.event_image);
            favoriteIcon = itemView.findViewById(R.id.favorite_icon);
            eventName = itemView.findViewById(R.id.event_name);
            starIcon = itemView.findViewById(R.id.star_icon);
            eventRating = itemView.findViewById(R.id.event_rating);
            eventAge = itemView.findViewById(R.id.event_age);
            eventSchedule = itemView.findViewById(R.id.event_schedule);
            eventAddress = itemView.findViewById(R.id.event_address);
            detailsButton = itemView.findViewById(R.id.details_button);
        }
    }
}
