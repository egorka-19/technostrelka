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
import com.example.main_screen.model.RouteModel;
import com.example.main_screen.RoutePreviewActivity;
import com.example.main_screen.utils.MediaUrlUtils;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.ViewHolder> {

    private final Context context;
    private List<RouteModel> routeList;
    private boolean isVertical; // true для вертикального списка, false для горизонтального

    public RouteAdapter(Context context, List<RouteModel> routeList) {
        this(context, routeList, false);
    }

    public RouteAdapter(Context context, List<RouteModel> routeList, boolean isVertical) {
        this.context = context;
        this.routeList = routeList;
        this.isVertical = isVertical;
    }

    public void setRoutes(List<RouteModel> routes) {
        this.routeList = routes != null ? routes : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = isVertical ? R.layout.route_card_item_vertical : R.layout.route_card_item;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RouteModel route = routeList.get(position);

        // Загрузка изображения
        String resolved = MediaUrlUtils.resolveForApiClient(route.getImageUrl());
        if (!TextUtils.isEmpty(resolved)) {
            Glide.with(context)
                    .load(resolved)
                    .error(R.drawable.izo)
                    .into(holder.routeImage);
        } else {
            holder.routeImage.setImageResource(R.drawable.izo);
        }

        // Название маршрута
        holder.routeName.setText(route.getName());

        // Обработчик клика на карточку
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RoutePreviewActivity.class);
            intent.putExtra(RoutePreviewActivity.EXTRA_ROUTE, route);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return routeList != null ? routeList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView routeImage;
        TextView routeName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            routeImage = itemView.findViewById(R.id.route_image);
            routeName = itemView.findViewById(R.id.route_name);
        }
    }
}
