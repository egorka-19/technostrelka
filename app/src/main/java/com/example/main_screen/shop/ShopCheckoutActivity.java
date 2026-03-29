package com.example.main_screen.shop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.main_screen.R;
import com.example.main_screen.model.ShopProduct;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class ShopCheckoutActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT = "shop_product";
    public static final String EXTRA_USER_EMAIL = "shop_user_email";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_checkout);

        ShopProduct product = (ShopProduct) getIntent().getSerializableExtra(EXTRA_PRODUCT);
        String emailRaw = getIntent().getStringExtra(EXTRA_USER_EMAIL);
        final String email = emailRaw != null ? emailRaw : "";
        if (product == null) {
            finish();
            return;
        }

        TextInputEditText address = findViewById(R.id.input_address);
        TextInputEditText time = findViewById(R.id.input_delivery_time);
        android.widget.TextView name = findViewById(R.id.checkout_product_name);
        android.widget.TextView linePrice = findViewById(R.id.checkout_product_price);
        android.widget.TextView total = findViewById(R.id.checkout_total);
        MaterialButton next = findViewById(R.id.btn_continue_payment);

        name.setText(product.getName().replace('\n', ' ').trim());
        int amount = product.displayPriceRub();
        if (amount > 0) {
            String p = String.format(Locale.getDefault(), "%d ₽", amount);
            linePrice.setText(p);
            total.setText("К оплате: " + p);
        } else {
            linePrice.setText("Цена по запросу");
            total.setText("К оплате: —");
        }

        next.setOnClickListener(v -> {
            String addr = address.getText() != null ? address.getText().toString().trim() : "";
            String t = time.getText() != null ? time.getText().toString().trim() : "";
            if (addr.isEmpty()) {
                Toast.makeText(this, "Укажите адрес доставки", Toast.LENGTH_SHORT).show();
                return;
            }
            if (t.isEmpty()) {
                Toast.makeText(this, "Укажите удобное время доставки", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(this, ShopPaymentFakeActivity.class);
            i.putExtra(ShopPaymentFakeActivity.EXTRA_USER_EMAIL, email);
            i.putExtra(ShopPaymentFakeActivity.EXTRA_ADDRESS, addr);
            i.putExtra(ShopPaymentFakeActivity.EXTRA_DELIVERY_TIME, t);
            i.putExtra(ShopPaymentFakeActivity.EXTRA_PRODUCT_NAME, product.getName().replace('\n', ' ').trim());
            i.putExtra(ShopPaymentFakeActivity.EXTRA_AMOUNT_RUB, amount);
            startActivity(i);
        });
    }
}
