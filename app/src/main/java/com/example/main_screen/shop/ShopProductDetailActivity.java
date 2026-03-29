package com.example.main_screen.shop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.main_screen.R;
import com.example.main_screen.model.ShopProduct;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class ShopProductDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT = "shop_product";
    public static final String EXTRA_USER_EMAIL = "shop_user_email";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_product_detail);

        ShopProduct product = (ShopProduct) getIntent().getSerializableExtra(EXTRA_PRODUCT);
        String emailRaw = getIntent().getStringExtra(EXTRA_USER_EMAIL);
        final String email = emailRaw != null ? emailRaw : "";
        if (product == null) {
            finish();
            return;
        }

        ImageButton back = findViewById(R.id.detail_back);
        ViewPager2 pager = findViewById(R.id.photo_pager);
        TextView title = findViewById(R.id.detail_title);
        TextView place = findViewById(R.id.detail_place);
        TextView description = findViewById(R.id.detail_description);
        TextView priceTv = findViewById(R.id.detail_price);
        MaterialButton buy = findViewById(R.id.btn_buy);

        back.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        title.setText(product.getName().trim());
        place.setText(product.getPlace());
        description.setText(product.getDescription().trim());
        int pr = product.displayPriceRub();
        if (pr > 0) {
            priceTv.setText(String.format(Locale.getDefault(), "%d ₽", pr));
            priceTv.setVisibility(android.view.View.VISIBLE);
        } else {
            priceTv.setVisibility(android.view.View.GONE);
        }

        pager.setAdapter(new ShopImagePagerAdapter(product.getImageUrls()));

        buy.setOnClickListener(v -> {
            Intent i = new Intent(this, ShopCheckoutActivity.class);
            i.putExtra(ShopCheckoutActivity.EXTRA_PRODUCT, product);
            i.putExtra(ShopCheckoutActivity.EXTRA_USER_EMAIL, email);
            startActivity(i);
        });
    }
}
