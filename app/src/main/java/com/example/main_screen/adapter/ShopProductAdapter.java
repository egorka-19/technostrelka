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
import com.example.main_screen.model.ShopProduct;
import com.example.main_screen.shop.ShopProductDetailActivity;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShopProductAdapter extends RecyclerView.Adapter<ShopProductAdapter.Holder> {

    private final Context context;
    private List<ShopProduct> products;
    private final boolean vertical;
    private final String userEmail;

    public ShopProductAdapter(Context context, List<ShopProduct> products, boolean vertical, String userEmail) {
        this.context = context;
        this.products = products != null ? products : new ArrayList<>();
        this.vertical = vertical;
        this.userEmail = userEmail != null ? userEmail : "";
    }

    public void setProducts(List<ShopProduct> products) {
        this.products = products != null ? products : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = vertical ? R.layout.item_shop_product_card_vertical : R.layout.item_shop_product_card;
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        ShopProduct p = products.get(position);
        String title = p.getName().replace('\n', ' ').trim();
        holder.title.setText(title);
        int price = p.displayPriceRub();
        if (price > 0) {
            holder.price.setText(String.format(Locale.getDefault(), "%d ₽", price));
            holder.price.setVisibility(View.VISIBLE);
        } else {
            holder.price.setText("");
            holder.price.setVisibility(View.GONE);
        }
        String url = firstImage(p);
        if (!TextUtils.isEmpty(url)) {
            Glide.with(context).load(url).error(R.drawable.izo).into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.izo);
        }
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, ShopProductDetailActivity.class);
            i.putExtra(ShopProductDetailActivity.EXTRA_PRODUCT, p);
            i.putExtra(ShopProductDetailActivity.EXTRA_USER_EMAIL, userEmail);
            context.startActivity(i);
        });
    }

    private static String firstImage(ShopProduct p) {
        List<String> urls = p.getImageUrls();
        if (urls == null || urls.isEmpty()) {
            return null;
        }
        return urls.get(0);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final RoundedImageView image;
        final TextView title;
        final TextView price;

        Holder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.shop_card_image);
            title = itemView.findViewById(R.id.shop_card_title);
            price = itemView.findViewById(R.id.shop_card_price);
        }
    }
}
