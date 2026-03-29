package com.example.main_screen.shop;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.main_screen.MainActivity;
import com.example.main_screen.R;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class ShopOrderSuccessActivity extends AppCompatActivity {

    public static final String EXTRA_USER_EMAIL = "shop_ok_email";
    public static final String EXTRA_PRODUCT_NAME = "shop_ok_product";
    public static final String EXTRA_AMOUNT_RUB = "shop_ok_amount";
    public static final String EXTRA_ADDRESS = "shop_ok_address";
    public static final String EXTRA_DELIVERY_TIME = "shop_ok_time";
    public static final String EXTRA_CARD_MASK = "shop_ok_card";

    private String email;
    private String body;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_order_success);

        email = getIntent().getStringExtra(EXTRA_USER_EMAIL);
        if (email == null) {
            email = "";
        }
        String productName = getIntent().getStringExtra(EXTRA_PRODUCT_NAME);
        if (productName == null) {
            productName = "";
        }
        int amount = getIntent().getIntExtra(EXTRA_AMOUNT_RUB, 0);
        String address = getIntent().getStringExtra(EXTRA_ADDRESS);
        if (address == null) {
            address = "";
        }
        String time = getIntent().getStringExtra(EXTRA_DELIVERY_TIME);
        if (time == null) {
            time = "";
        }
        String card = getIntent().getStringExtra(EXTRA_CARD_MASK);
        if (card == null) {
            card = "";
        }

        body = buildBody(productName, amount, address, time, card);

        TextView sub = findViewById(R.id.success_subtitle);
        if (!TextUtils.isEmpty(email)) {
            sub.setText("Заказ оформлен. Нажмите кнопку ниже, чтобы открыть почту и отправить подтверждение на " + email + ".");
        } else {
            sub.setText("Заказ оформлен (демо). Добавьте почту в профиле, чтобы отправить письмо с подтверждением.");
        }

        MaterialButton send = findViewById(R.id.btn_send_email);
        MaterialButton done = findViewById(R.id.btn_done);

        send.setOnClickListener(v -> sendEmail());
        done.setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            finish();
        });
    }

    private String buildBody(String productName, int amount, String address, String time, String card) {
        StringBuilder sb = new StringBuilder();
        sb.append("Заказ VisitUdmurtia Shop (учебный макет в приложении «Сердце Удмуртии»)\n\n");
        sb.append("Товар: ").append(productName).append('\n');
        if (amount > 0) {
            sb.append("Сумма: ").append(String.format(Locale.getDefault(), "%d ₽", amount)).append('\n');
        }
        sb.append("Адрес доставки: ").append(address).append('\n');
        sb.append("Время доставки: ").append(time).append('\n');
        if (!card.isEmpty()) {
            sb.append("Карта (маска): ").append(card).append('\n');
        }
        sb.append("\nСпасибо за покупку!\n");
        sb.append("Источник каталога: https://visitudmurtiashop.ru\n");
        return sb.toString();
    }

    private void sendEmail() {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Почта не указана в профиле", Toast.LENGTH_LONG).show();
            return;
        }
        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("message/rfc822");
        send.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        send.putExtra(Intent.EXTRA_SUBJECT, "Подтверждение заказа — VisitUdmurtia Shop (демо)");
        send.putExtra(Intent.EXTRA_TEXT, body);
        try {
            startActivity(Intent.createChooser(send, "Отправить письмо"));
        } catch (Exception e) {
            Toast.makeText(this, "Не удалось открыть почтовый клиент", Toast.LENGTH_SHORT).show();
        }
    }
}
