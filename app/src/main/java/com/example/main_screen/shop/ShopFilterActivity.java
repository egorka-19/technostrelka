package com.example.main_screen.shop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.main_screen.R;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;

public class ShopFilterActivity extends AppCompatActivity {

    public static final String EXTRA_SELECTED_CATEGORIES = "shop_selected_categories";
    public static final String EXTRA_INITIAL_SELECTION = "shop_initial_selection";

    public static final String CATEGORY_TSHIRTS = "Футболки";
    public static final String CATEGORY_HOODIES = "Худи и свитшоты";
    public static final String CATEGORY_SOUVENIRS = "Сувениры";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_filter);

        MaterialCheckBox cbT = findViewById(R.id.cat_tshirts);
        MaterialCheckBox cbH = findViewById(R.id.cat_hoodies);
        MaterialCheckBox cbS = findViewById(R.id.cat_souvenirs);
        Button apply = findViewById(R.id.btn_apply_filter);
        Button reset = findViewById(R.id.btn_reset_filter);

        ArrayList<String> initial = getIntent().getStringArrayListExtra(EXTRA_INITIAL_SELECTION);
        if (initial != null) {
            cbT.setChecked(initial.contains(CATEGORY_TSHIRTS));
            cbH.setChecked(initial.contains(CATEGORY_HOODIES));
            cbS.setChecked(initial.contains(CATEGORY_SOUVENIRS));
        }

        apply.setOnClickListener(v -> {
            ArrayList<String> out = new ArrayList<>();
            if (cbT.isChecked()) {
                out.add(CATEGORY_TSHIRTS);
            }
            if (cbH.isChecked()) {
                out.add(CATEGORY_HOODIES);
            }
            if (cbS.isChecked()) {
                out.add(CATEGORY_SOUVENIRS);
            }
            Intent data = new Intent();
            data.putStringArrayListExtra(EXTRA_SELECTED_CATEGORIES, out);
            setResult(RESULT_OK, data);
            finish();
        });

        reset.setOnClickListener(v -> {
            cbT.setChecked(false);
            cbH.setChecked(false);
            cbS.setChecked(false);
        });
    }
}
