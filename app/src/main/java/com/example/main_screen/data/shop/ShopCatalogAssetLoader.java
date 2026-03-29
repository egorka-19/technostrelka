package com.example.main_screen.data.shop;

import android.content.Context;

import com.example.main_screen.model.ShopProduct;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ShopCatalogAssetLoader {

    private static final String ASSET = "visit_udmurtia_shop.json";

    private ShopCatalogAssetLoader() {
    }

    public static List<ShopProduct> load(Context context) {
        if (context == null) {
            return Collections.emptyList();
        }
        try (InputStream is = context.getAssets().open(ASSET);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            VisitUdmurtiaShopJsonDto root = new Gson().fromJson(reader, VisitUdmurtiaShopJsonDto.class);
            if (root == null || root.products == null) {
                return Collections.emptyList();
            }
            List<ShopProduct> out = new ArrayList<>();
            for (VisitUdmurtiaShopItemDto dto : root.products) {
                if (dto == null) {
                    continue;
                }
                ShopProduct p = new ShopProduct();
                p.setId(dto.id != null ? dto.id : "");
                p.setLid(dto.lid != null ? dto.lid : "");
                p.setCollection(dto.collection != null ? dto.collection : "");
                p.setShopCategory(dto.shopCategory != null ? dto.shopCategory : "Сувениры");
                p.setName(dto.name != null ? dto.name : "");
                p.setDescription(dto.description != null ? dto.description : "");
                p.setPriceRub(dto.priceRub);
                p.setPriceOldRub(dto.priceOldRub);
                p.setPlace(dto.place != null ? dto.place : "Ижевск");
                p.setProductUrl(dto.productUrl != null ? dto.productUrl : "");
                if (dto.imageUrls != null) {
                    p.setImageUrls(new ArrayList<>(dto.imageUrls));
                }
                out.add(p);
            }
            return out;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
