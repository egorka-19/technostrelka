package com.example.main_screen;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.example.main_screen.api.ImageLoadOkHttpClient;

import java.io.InputStream;

/**
 * Картинки грузим через OkHttp. Для проблемных HTTPS (нестандартная цепочка сертификатов)
 * используется {@link ImageLoadOkHttpClient}, не клиент Retrofit.
 */
@GlideModule
public final class MainAppGlideModule extends AppGlideModule {

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        registry.replace(
                GlideUrl.class,
                InputStream.class,
                new OkHttpUrlLoader.Factory(ImageLoadOkHttpClient.get(context.getApplicationContext())));
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
