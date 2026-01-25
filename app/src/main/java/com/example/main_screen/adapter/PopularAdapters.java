package com.example.main_screen.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.main_screen.model.PopularModel;
import com.example.main_screen.R;
import com.example.main_screen.product_card;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class PopularAdapters extends RecyclerView.Adapter<PopularAdapters.ViewHolder> {

    private final Context context;
    private List<PopularModel> popularModelList;
    private OnFavoriteChangeListener favoriteChangeListener;
    private OnShowNotificationListener notificationListener;

    public interface OnFavoriteChangeListener {
        void onFavoriteChanged();
    }

    public interface OnShowNotificationListener {
        void showFavoriteNotification();
    }

    public void setOnFavoriteChangeListener(OnFavoriteChangeListener listener) {
        this.favoriteChangeListener = listener;
    }

    public void setOnShowNotificationListener(OnShowNotificationListener listener) {
        this.notificationListener = listener;
    }

    public PopularAdapters(Context context, List<PopularModel> popularModelList) {
        this.context = context;
        this.popularModelList = popularModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.event_card_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int currentPosition = holder.getAdapterPosition();

        if (currentPosition != RecyclerView.NO_POSITION) {
            PopularModel currentItem = popularModelList.get(currentPosition);

            // Загрузка изображения через Glide
            Glide.with(context)
                    .load(currentItem.getImg_url())
                    .error(R.drawable.izo)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("GlideError", "Error loading image", e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(holder.eventImage);

            // Название события
            holder.eventName.setText(currentItem.getName());

            // Рейтинг
            String rating = currentItem.getRating();
            if (!TextUtils.isEmpty(rating)) {
                holder.eventRating.setText(rating);
                holder.starIcon.setVisibility(View.VISIBLE);
                holder.eventRating.setVisibility(View.VISIBLE);
            } else {
                holder.starIcon.setVisibility(View.GONE);
                holder.eventRating.setVisibility(View.GONE);
            }

            // Возрастные ограничения
            String age = currentItem.getAge();
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
            String schedule = currentItem.getSchedule();
            String status = currentItem.getStatus();
            
            if (!TextUtils.isEmpty(status)) {
                // Если есть статус, показываем его
                holder.eventSchedule.setVisibility(View.GONE);
                holder.eventStatus.setVisibility(View.VISIBLE);
                holder.eventStatus.setText(status);
                
                if (status.toLowerCase().contains("закрыт") || status.toLowerCase().contains("closed")) {
                    holder.eventStatus.setTextColor(context.getResources().getColor(R.color.red_pink));
                } else {
                    holder.eventStatus.setTextColor(context.getResources().getColor(R.color.blue));
                }
            } else if (!TextUtils.isEmpty(schedule)) {
                // Если есть расписание, показываем его
                holder.eventSchedule.setVisibility(View.VISIBLE);
                holder.eventStatus.setVisibility(View.GONE);
                holder.eventSchedule.setText(schedule);
            } else {
                // Если нет ни того, ни другого, показываем data
                String data = currentItem.getData();
                if (!TextUtils.isEmpty(data)) {
                    holder.eventSchedule.setVisibility(View.VISIBLE);
                    holder.eventStatus.setVisibility(View.GONE);
                    holder.eventSchedule.setText(data);
                } else {
                    holder.eventSchedule.setVisibility(View.GONE);
                    holder.eventStatus.setVisibility(View.GONE);
                }
            }

            // Адрес
            String place = currentItem.getPlace();
            if (!TextUtils.isEmpty(place)) {
                holder.eventAddress.setText(place);
            } else {
                holder.eventAddress.setText("Адрес не указан");
            }

            // Проверка состояния избранного из Firebase
            checkFavoriteStatus(currentItem, holder);

            // Обработчик клика на карточку
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, product_card.class);
                intent.putExtra("detail", currentItem);
                context.startActivity(intent);
            });

            // Обработчик клика на кнопку "Подробнее"
            holder.detailsButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, product_card.class);
                intent.putExtra("detail", currentItem);
                context.startActivity(intent);
            });

            // Обработчик клика на избранное
            holder.favoriteIcon.setOnClickListener(v -> {
                toggleFavorite(currentItem, holder);
            });
        }
    }

    /**
     * Проверка состояния избранного из Firebase
     */
    private void checkFavoriteStatus(PopularModel item, ViewHolder holder) {
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
    private void toggleFavorite(PopularModel item, ViewHolder holder) {
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
                    
                    // Показываем анимацию уведомления
                    if (notificationListener != null) {
                        notificationListener.showFavoriteNotification();
                    }
                } else {
                    // Удалено из избранного - убираем заливку
                    setFavoriteIconState(holder, false);
                }

                // Уведомляем слушателя об изменении
                if (favoriteChangeListener != null) {
                    favoriteChangeListener.onFavoriteChanged();
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
        return popularModelList.size();
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
