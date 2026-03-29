package com.example.main_screen.shop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.main_screen.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class ShopPaymentFakeActivity extends AppCompatActivity {

    public static final String EXTRA_USER_EMAIL = "shop_pay_email";
    public static final String EXTRA_ADDRESS = "shop_pay_address";
    public static final String EXTRA_DELIVERY_TIME = "shop_pay_time";
    public static final String EXTRA_PRODUCT_NAME = "shop_pay_product";
    public static final String EXTRA_AMOUNT_RUB = "shop_pay_amount";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_payment_fake);

        String emailRaw = getIntent().getStringExtra(EXTRA_USER_EMAIL);
        String addressRaw = getIntent().getStringExtra(EXTRA_ADDRESS);
        String timeRaw = getIntent().getStringExtra(EXTRA_DELIVERY_TIME);
        String productNameRaw = getIntent().getStringExtra(EXTRA_PRODUCT_NAME);
        int amount = getIntent().getIntExtra(EXTRA_AMOUNT_RUB, 0);
        final String email = emailRaw != null ? emailRaw : "";
        final String address = addressRaw != null ? addressRaw : "";
        final String time = timeRaw != null ? timeRaw : "";
        final String productName = productNameRaw != null ? productNameRaw : "";

        android.widget.TextView amountLabel = findViewById(R.id.payment_amount_label);
        if (amount > 0) {
            amountLabel.setText(String.format(Locale.getDefault(), "К оплате: %d ₽", amount));
        } else {
            amountLabel.setText("К оплате: —");
        }

        TextInputEditText card = findViewById(R.id.input_card);
        TextInputEditText expiry = findViewById(R.id.input_expiry);
        TextInputEditText cvc = findViewById(R.id.input_cvc);
        MaterialButton pay = findViewById(R.id.btn_pay_fake);

        pay.setOnClickListener(v -> {
            String c = card.getText() != null ? card.getText().toString().trim() : "";
            if (c.length() < 12) {
                Toast.makeText(this, "Введите номер карты (демо, можно выдуманный)", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(this, ShopOrderSuccessActivity.class);
            i.putExtra(ShopOrderSuccessActivity.EXTRA_USER_EMAIL, email);
            i.putExtra(ShopOrderSuccessActivity.EXTRA_PRODUCT_NAME, productName);
            i.putExtra(ShopOrderSuccessActivity.EXTRA_AMOUNT_RUB, amount);
            i.putExtra(ShopOrderSuccessActivity.EXTRA_ADDRESS, address);
            i.putExtra(ShopOrderSuccessActivity.EXTRA_DELIVERY_TIME, time);
            i.putExtra(ShopOrderSuccessActivity.EXTRA_CARD_MASK, maskCard(c));
            startActivity(i);
            finish();
        });
    }

    private static String maskCard(String digits) {
        if (digits.length() <= 4) {
            return "****";
        }
        return "**** **** **** " + digits.substring(digits.length() - 4);
    }
}
