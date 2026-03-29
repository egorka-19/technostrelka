package com.example.main_screen.shop;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.main_screen.R;
import com.example.main_screen.adapter.ShopProductAdapter;
import com.example.main_screen.data.shop.ShopCatalogAssetLoader;
import com.example.main_screen.model.ShopProduct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ShopActivity extends AppCompatActivity {

    public static final String EXTRA_USER_EMAIL = "shop_user_email";

    private EditText searchBox;
    private ImageButton filterButton;
    private ImageButton backButton;
    private RecyclerView searchResultsRecycler;
    private ScrollView shopScrollView;
    private LinearLayout categoriesContainer;
    private TextView shopEmpty;

    private final Map<String, RecyclerView> categoryRecyclers = new HashMap<>();
    private final Map<String, ShopProductAdapter> categoryAdapters = new HashMap<>();
    private final Map<String, View> categorySectionRoots = new HashMap<>();
    private final Map<String, List<ShopProduct>> categoryProducts = new HashMap<>();

    private List<ShopProduct> allProducts = new ArrayList<>();
    private ShopProductAdapter searchAdapter;
    private String userEmail = "";

    private Set<String> allowedCategories;

    private ActivityResultLauncher<Intent> filterLauncher;

    private static final Comparator<String> CATEGORY_ORDER = (a, b) -> {
        int ia = shopCategoryRank(a);
        int ib = shopCategoryRank(b);
        if (ia != ib) {
            return Integer.compare(ia, ib);
        }
        return a.compareToIgnoreCase(b);
    };

    private static int shopCategoryRank(String name) {
        if (name == null) {
            return 100;
        }
        switch (name) {
            case ShopFilterActivity.CATEGORY_TSHIRTS:
                return 0;
            case ShopFilterActivity.CATEGORY_HOODIES:
                return 1;
            case ShopFilterActivity.CATEGORY_SOUVENIRS:
                return 2;
            default:
                return 20;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        userEmail = getIntent().getStringExtra(EXTRA_USER_EMAIL);
        if (userEmail == null) {
            userEmail = "";
        }

        filterLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        ArrayList<String> sel = result.getData().getStringArrayListExtra(
                                ShopFilterActivity.EXTRA_SELECTED_CATEGORIES);
                        if (sel == null || sel.isEmpty()) {
                            allowedCategories = null;
                        } else {
                            allowedCategories = new HashSet<>(sel);
                        }
                        rebuildCatalog();
                    }
                });

        searchBox = findViewById(R.id.search_box);
        filterButton = findViewById(R.id.filter_button);
        backButton = findViewById(R.id.shop_back);
        searchResultsRecycler = findViewById(R.id.search_results_recycler);
        shopScrollView = findViewById(R.id.shop_scroll_view);
        categoriesContainer = findViewById(R.id.shop_categories_container);
        shopEmpty = findViewById(R.id.shop_empty);

        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        searchResultsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        searchAdapter = new ShopProductAdapter(this, new ArrayList<>(), true, userEmail);
        searchResultsRecycler.setAdapter(searchAdapter);

        filterButton.setOnClickListener(v -> {
            Intent i = new Intent(this, ShopFilterActivity.class);
            if (allowedCategories != null && !allowedCategories.isEmpty()) {
                i.putStringArrayListExtra(ShopFilterActivity.EXTRA_INITIAL_SELECTION,
                        new ArrayList<>(allowedCategories));
            }
            filterLauncher.launch(i);
        });

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String q = s.toString().toLowerCase(Locale.getDefault()).trim();
                if (q.isEmpty()) {
                    searchResultsRecycler.setVisibility(View.GONE);
                    shopScrollView.setVisibility(View.VISIBLE);
                } else {
                    searchAdapter.setProducts(filterForSearch(q));
                    searchResultsRecycler.setVisibility(View.VISIBLE);
                    shopScrollView.setVisibility(View.GONE);
                }
            }
        });

        loadProducts();
    }

    private void loadProducts() {
        allProducts = ShopCatalogAssetLoader.load(getApplicationContext());
        allowedCategories = null;
        rebuildCatalog();
    }

    private List<ShopProduct> visibleProducts() {
        if (allowedCategories == null || allowedCategories.isEmpty()) {
            return new ArrayList<>(allProducts);
        }
        List<ShopProduct> out = new ArrayList<>();
        for (ShopProduct p : allProducts) {
            String cat = p.getShopCategory();
            if (allowedCategories.contains(cat)) {
                out.add(p);
            }
        }
        return out;
    }

    private List<ShopProduct> filterForSearch(String q) {
        List<ShopProduct> base = visibleProducts();
        List<ShopProduct> out = new ArrayList<>();
        for (ShopProduct p : base) {
            String n = p.getName() != null ? p.getName().toLowerCase(Locale.getDefault()) : "";
            String d = p.getDescription() != null ? p.getDescription().toLowerCase(Locale.getDefault()) : "";
            if (n.contains(q) || d.contains(q)) {
                out.add(p);
            }
        }
        return out;
    }

    private void rebuildCatalog() {
        categoryProducts.clear();
        for (ShopProduct p : visibleProducts()) {
            String cat = p.getShopCategory();
            if (cat == null || cat.isEmpty()) {
                cat = ShopFilterActivity.CATEGORY_SOUVENIRS;
            }
            categoryProducts.computeIfAbsent(cat, k -> new ArrayList<>()).add(p);
        }
        setupCategorySections();
        updateEmptyState();
        String q = searchBox.getText() != null ? searchBox.getText().toString().toLowerCase(Locale.getDefault()).trim() : "";
        if (!q.isEmpty()) {
            searchAdapter.setProducts(filterForSearch(q));
        }
    }

    private void setupCategorySections() {
        categoryRecyclers.clear();
        categoryAdapters.clear();
        categorySectionRoots.clear();
        categoriesContainer.removeAllViews();

        List<String> ordered = new ArrayList<>(categoryProducts.keySet());
        Collections.sort(ordered, CATEGORY_ORDER);

        LayoutInflater inflater = LayoutInflater.from(this);
        for (String cat : ordered) {
            List<ShopProduct> list = categoryProducts.get(cat);
            if (list == null || list.isEmpty()) {
                continue;
            }
            View section = inflater.inflate(R.layout.item_route_category_section, categoriesContainer, false);
            TextView title = section.findViewById(R.id.section_title);
            RecyclerView rv = section.findViewById(R.id.section_recycler);
            title.setText(cat);
            rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            ShopProductAdapter adapter = new ShopProductAdapter(this, new ArrayList<>(list), false, userEmail);
            rv.setAdapter(adapter);
            categoryRecyclers.put(cat, rv);
            categoryAdapters.put(cat, adapter);
            categorySectionRoots.put(cat, section);
            categoriesContainer.addView(section);
        }
    }

    private void updateEmptyState() {
        boolean any = categoriesContainer.getChildCount() > 0;
        shopEmpty.setVisibility(any ? View.GONE : View.VISIBLE);
    }
}
