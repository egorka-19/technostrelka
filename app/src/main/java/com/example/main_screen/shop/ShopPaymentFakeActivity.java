package com.example.main_screen.shop;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.TransformationMethod;
import android.view.View;
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

        // TYPE_CLASS_NUMBER не допускает пробел и «/» — маска обрезается. Разрешаем символы маски явно.
        card.setKeyListener(DigitsKeyListener.getInstance("0123456789 "));
        card.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        card.addTextChangedListener(new CardNumberTextWatcher(card));

        expiry.setKeyListener(DigitsKeyListener.getInstance("0123456789/"));
        expiry.setRawInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        expiry.addTextChangedListener(new ExpiryTextWatcher(expiry));

        cvc.setInputType(InputType.TYPE_CLASS_NUMBER);
        cvc.setTransformationMethod(StarMaskTransformationMethod.INSTANCE);
        cvc.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
        cvc.addTextChangedListener(new DigitsOnlyMaxWatcher(cvc, 3));

        pay.setOnClickListener(v -> {
            String cardDigits = digitsOnly(card.getText());
            String expDigits = digitsOnly(expiry.getText());
            String cvcDigits = digitsOnly(cvc.getText());

            if (cardDigits.length() < 16) {
                Toast.makeText(this, "Введите 16 цифр номера карты", Toast.LENGTH_SHORT).show();
                return;
            }
            if (cardDigits.length() > 16) {
                Toast.makeText(this, "Номер карты: 16 цифр", Toast.LENGTH_SHORT).show();
                return;
            }
            if (expDigits.length() != 4) {
                Toast.makeText(this, "Укажите срок ММ/ГГ (4 цифры)", Toast.LENGTH_SHORT).show();
                return;
            }
            int mm = Integer.parseInt(expDigits.substring(0, 2));
            if (mm < 1 || mm > 12) {
                Toast.makeText(this, "Некорректный месяц в сроке действия", Toast.LENGTH_SHORT).show();
                return;
            }
            if (cvcDigits.length() != 3) {
                Toast.makeText(this, "Введите CVC (3 цифры)", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent i = new Intent(this, ShopOrderSuccessActivity.class);
            i.putExtra(ShopOrderSuccessActivity.EXTRA_USER_EMAIL, email);
            i.putExtra(ShopOrderSuccessActivity.EXTRA_PRODUCT_NAME, productName);
            i.putExtra(ShopOrderSuccessActivity.EXTRA_AMOUNT_RUB, amount);
            i.putExtra(ShopOrderSuccessActivity.EXTRA_ADDRESS, address);
            i.putExtra(ShopOrderSuccessActivity.EXTRA_DELIVERY_TIME, time);
            i.putExtra(ShopOrderSuccessActivity.EXTRA_CARD_MASK, maskCard(cardDigits));
            startActivity(i);
            finish();
        });
    }

    private static String digitsOnly(CharSequence s) {
        if (s == null) {
            return "";
        }
        StringBuilder b = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= '0' && c <= '9') {
                b.append(c);
            }
        }
        return b.toString();
    }

    private static String maskCard(String sixteenDigits) {
        if (sixteenDigits.length() <= 4) {
            return "****";
        }
        return "**** **** **** " + sixteenDigits.substring(sixteenDigits.length() - 4);
    }

    /** Номер карты: «1111 1111 1111 1111». */
    private static final class CardNumberTextWatcher implements TextWatcher {
        private final TextInputEditText editText;
        private boolean selfChange;

        CardNumberTextWatcher(TextInputEditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (selfChange) {
                return;
            }
            int selStart = editText.getSelectionStart();
            if (selStart < 0) {
                selStart = 0;
            }
            int digitsBefore = asciiDigitsBefore(s, selStart);

            String raw = digitsOnly(s);
            if (raw.length() > 16) {
                raw = raw.substring(0, 16);
            }
            digitsBefore = Math.min(digitsBefore, raw.length());
            String formatted = formatCardGroups(raw);

            selfChange = true;
            s.replace(0, s.length(), formatted);
            int newPos = positionAfterNthAsciiDigit(formatted, digitsBefore);
            newPos = Math.max(0, Math.min(newPos, editText.length()));
            editText.setSelection(newPos);
            selfChange = false;
        }

        private static String formatCardGroups(String d) {
            StringBuilder out = new StringBuilder();
            for (int i = 0; i < d.length(); i++) {
                if (i > 0 && i % 4 == 0) {
                    out.append(' ');
                }
                out.append(d.charAt(i));
            }
            return out.toString();
        }
    }

    /** Срок: «12/26». */
    private static final class ExpiryTextWatcher implements TextWatcher {
        private final TextInputEditText editText;
        private boolean selfChange;

        ExpiryTextWatcher(TextInputEditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (selfChange) {
                return;
            }
            int selStart = editText.getSelectionStart();
            if (selStart < 0) {
                selStart = 0;
            }
            int digitsBefore = asciiDigitsBefore(s, selStart);

            String raw = digitsOnly(s);
            if (raw.length() > 4) {
                raw = raw.substring(0, 4);
            }
            digitsBefore = Math.min(digitsBefore, raw.length());
            String formatted;
            if (raw.length() <= 2) {
                formatted = raw;
            } else {
                formatted = raw.substring(0, 2) + "/" + raw.substring(2);
            }

            selfChange = true;
            s.replace(0, s.length(), formatted);
            int newPos = expiryCursorPosAscii(formatted, digitsBefore);
            newPos = Math.max(0, Math.min(newPos, editText.length()));
            editText.setSelection(newPos);
            selfChange = false;
        }

        private static int expiryCursorPosAscii(String formatted, int digitsBefore) {
            if (digitsBefore <= 0) {
                return 0;
            }
            int digits = 0;
            for (int i = 0; i < formatted.length(); i++) {
                char c = formatted.charAt(i);
                if (c >= '0' && c <= '9') {
                    digits++;
                    if (digits >= digitsBefore) {
                        return i + 1;
                    }
                }
            }
            return formatted.length();
        }
    }

    /** Только цифры, ограничение длины (для CVC). */
    private static final class DigitsOnlyMaxWatcher implements TextWatcher {
        private final TextInputEditText editText;
        private final int maxDigits;
        private boolean selfChange;

        DigitsOnlyMaxWatcher(TextInputEditText editText, int maxDigits) {
            this.editText = editText;
            this.maxDigits = maxDigits;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (selfChange) {
                return;
            }
            String raw = digitsOnly(s);
            if (raw.length() <= maxDigits) {
                return;
            }
            raw = raw.substring(0, maxDigits);
            selfChange = true;
            s.replace(0, s.length(), raw);
            editText.setSelection(s.length());
            selfChange = false;
        }
    }

    /** Сколько ASCII-цифр 0–9 стоит в тексте до позиции {@code end} (как в {@link #digitsOnly}). */
    private static int asciiDigitsBefore(CharSequence s, int end) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        int e = Math.min(Math.max(end, 0), s.length());
        int n = 0;
        for (int i = 0; i < e; i++) {
            char c = s.charAt(i);
            if (c >= '0' && c <= '9') {
                n++;
            }
        }
        return n;
    }

    /** Позиция курсора сразу после n-й ASCII-цифры в отформатированной строке. */
    private static int positionAfterNthAsciiDigit(String formatted, int n) {
        if (n <= 0 || formatted == null) {
            return 0;
        }
        int digits = 0;
        for (int i = 0; i < formatted.length(); i++) {
            char c = formatted.charAt(i);
            if (c >= '0' && c <= '9') {
                digits++;
                if (digits >= n) {
                    return Math.min(i + 1, formatted.length());
                }
            }
        }
        return formatted.length();
    }

    /** Отображение CVC звёздочками; в буфере остаются цифры. */
    private static final class StarMaskTransformationMethod implements TransformationMethod {
        static final StarMaskTransformationMethod INSTANCE = new StarMaskTransformationMethod();

        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return new StarCharSequence(source);
        }

        @Override
        public void onFocusChanged(View view, CharSequence sourceText, boolean focused,
                int direction, Rect previouslyFocusedRect) {
        }

        private static final class StarCharSequence implements CharSequence {
            private final CharSequence source;

            StarCharSequence(CharSequence source) {
                this.source = source;
            }

            @Override
            public int length() {
                return source.length();
            }

            @Override
            public char charAt(int index) {
                return '*';
            }

            @Override
            public CharSequence subSequence(int start, int end) {
                SpannableStringBuilder b = new SpannableStringBuilder();
                for (int i = start; i < end; i++) {
                    b.append('*');
                }
                return b;
            }
        }
    }
}
