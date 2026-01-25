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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.main_screen.model.ViewAllModel;
import com.example.main_screen.R;
import com.example.main_screen.product_card;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

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

        // Рейтинг (по умолчанию 5.0)
        holder.eventRating.setText("5.0");
        holder.starIcon.setVisibility(View.VISIBLE);
        holder.eventRating.setVisibility(View.VISIBLE);

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

        // Часы работы / Статус
        String data = item.getData();
        if (!TextUtils.isEmpty(data)) {
            holder.eventSchedule.setVisibility(View.VISIBLE);
            holder.eventStatus.setVisibility(View.GONE);
            holder.eventSchedule.setText(data);
        } else {
            holder.eventSchedule.setVisibility(View.GONE);
            holder.eventStatus.setVisibility(View.GONE);
        }

        // Адрес
        String place = item.getPlace();
        if (!TextUtils.isEmpty(place)) {
            holder.eventAddress.setText(place);
        } else {
            holder.eventAddress.setText("Адрес не указан");
        }

        // Проверка состояния избранного из Firebase
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

    /**
     * Проверка состояния избранного из Firebase
     */
    private void checkFavoriteStatus(ViewAllModel item, ViewHolder holder) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            setFavoriteIconState(holder, false);
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String eventName = item.getName();
        String categoryType = getCategoryTypeForFirebase(item.getType());

        DatabaseReference favoriteRef = FirebaseDatabase.getInstance()
                .getReference("Reviews")
                .child(categoryType)
                .child(eventName)
                .child(userId)
                .child("lovest");

        favoriteRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Integer lovestValue = snapshot.getValue(Integer.class);
                    boolean isFavorite = lovestValue != null && lovestValue == 1;
                    setFavoriteIconState(holder, isFavorite);
                } else {
                    setFavoriteIconState(holder, false);
                }
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
                setFavoriteIconState(holder, false);
            }
        });
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

    /**
     * Переключение состояния избранного
     */
    private void toggleFavorite(ViewAllModel item, ViewHolder holder) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return;
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String eventName = item.getName();
        String categoryType = getCategoryTypeForFirebase(item.getType());

        DatabaseReference favoriteRef = FirebaseDatabase.getInstance()
                .getReference("Reviews")
                .child(categoryType)
                .child(eventName)
                .child(userId)
                .child("lovest");

        boolean isCurrentlyFavorite = holder.favoriteIcon.getTag() != null && holder.favoriteIcon.getTag().equals("favorite");
        int newValue = isCurrentlyFavorite ? 0 : 1;

        favoriteRef.setValue(newValue).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (newValue == 1) {
                    // Добавлено в избранное - заливаем красным цветом FF0033
                    setFavoriteIconState(holder, true);
                } else {
                    // Удалено из избранного - убираем заливку
                    setFavoriteIconState(holder, false);
                }
            }
        });
    }

    /**
     * Преобразование типа категории для Firebase
     */
    private String getCategoryTypeForFirebase(String type) {
        if (type == null) return "Other";
        
        switch (type) {
            case "Кино":
            case "Cinema":
                return "Cinema";
            case "Театр":
            case "Theater":
                return "Theater";
            case "Парк":
            case "Park":
                return "Park";
            case "Ресторан":
            case "Restaurant":
                return "Restaraunt";
            case "Музей":
            case "Museum":
                return "Museum";
            default:
                return "Other";
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        com.makeramen.roundedimageview.RoundedImageView eventImage;
        ImageView favoriteIcon, starIcon;
        TextView eventName, eventRating, eventAge, eventSchedule, eventStatus, eventAddress;
        Button detailsButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.event_image);
            favoriteIcon = itemView.findViewById(R.id.favorite_icon);
            eventName = itemView.findViewById(R.id.event_name);
            starIcon = itemView.findViewById(R.id.star_icon);
            eventRating = itemView.findViewById(R.id.event_rating);
            eventAge = itemView.findViewById(R.id.event_age);
            eventSchedule = itemView.findViewById(R.id.event_schedule);
            eventStatus = itemView.findViewById(R.id.event_status);
            eventAddress = itemView.findViewById(R.id.event_address);
            detailsButton = itemView.findViewById(R.id.details_button);
        }
    }
}
