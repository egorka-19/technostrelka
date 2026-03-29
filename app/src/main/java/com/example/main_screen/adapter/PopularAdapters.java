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
import com.example.main_screen.api.ApiClient;
import com.example.main_screen.api.TokenStore;
import com.example.main_screen.model.PopularModel;
import com.example.main_screen.R;
import com.example.main_screen.product_card;
import com.example.main_screen.utils.MediaUrlUtils;

import java.util.List;
import java.util.concurrent.Executors;

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

            String imageUrl = MediaUrlUtils.resolveForApiClient(currentItem.getImg_url());
            if (TextUtils.isEmpty(imageUrl)) {
                holder.eventImage.setImageResource(R.drawable.izo);
            } else {
                Glide.with(context)
                        .load(imageUrl)
                        .error(R.drawable.izo)
                        .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Log.e("GlideError", "Error loading image url=" + model, e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(holder.eventImage);
            }

            // Название события
            holder.eventName.setText(currentItem.getName());

            applyRatingFromModel(currentItem, holder);

            // Возрастные ограничения
            // TODO: Загружать из Firebase, временно значение по умолчанию
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

            // Часы работы
            // TODO: Загружать из Firebase, временно значение по умолчанию
            String schedule = currentItem.getSchedule();
            
            if (!TextUtils.isEmpty(schedule)) {
                // Если есть расписание, показываем его
                holder.eventSchedule.setVisibility(View.VISIBLE);
                holder.eventSchedule.setText(schedule);
            } else {
                // Если нет расписания, показываем data или значение по умолчанию
                String data = currentItem.getData();
                if (!TextUtils.isEmpty(data)) {
                    holder.eventSchedule.setVisibility(View.VISIBLE);
                    holder.eventSchedule.setText(data);
                } else {
                    // Значение по умолчанию
                    holder.eventSchedule.setVisibility(View.VISIBLE);
                    holder.eventSchedule.setText("пн-чт 06:30–23:00; пт 06:30–00:00; сб 08:00–00:00; вс 08:00–23:00");
                }
            }

            // Адрес
            // TODO: Загружать из Firebase, временно значение по умолчанию
            String place = currentItem.getPlace();
            if (!TextUtils.isEmpty(place)) {
                holder.eventAddress.setText(place);
            } else {
                // Значение по умолчанию
                holder.eventAddress.setText("Ижевск, ул. Милиционная, 5");
            }

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

    private void checkFavoriteStatus(PopularModel item, ViewHolder holder) {
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

    /**
     * Переключение состояния избранного
     */
    private void toggleFavorite(PopularModel item, ViewHolder holder) {
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
                        if (newFav && notificationListener != null) {
                            notificationListener.showFavoriteNotification();
                        }
                        if (favoriteChangeListener != null) {
                            favoriteChangeListener.onFavoriteChanged();
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("Api", "favorite toggle", e);
            }
        });
    }

    private void applyRatingFromModel(PopularModel item, ViewHolder holder) {
        String r = item.getRating();
        if (r == null || r.isEmpty()) {
            r = "0";
        }
        holder.eventRating.setText(r);
        holder.starIcon.setVisibility(View.VISIBLE);
        holder.eventRating.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return popularModelList.size();
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
