package com.example.main_screen.api;

import android.content.Context;

import com.example.main_screen.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {
    private static volatile Retrofit retrofit;
    private static volatile ApiService apiService;
    private static volatile OkHttpClient okHttpClient;

    private ApiClient() {
    }

    private static OkHttpClient createOkHttpClient(Context appContext) {
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new AuthInterceptor(appContext))
                .build();
    }

    public static ApiService get(Context context) {
        if (apiService == null) {
            synchronized (ApiClient.class) {
                if (apiService == null) {
                    Context app = context.getApplicationContext();
                    OkHttpClient ok = createOkHttpClient(app);
                    okHttpClient = ok;
                    retrofit = new Retrofit.Builder()
                            .baseUrl(BuildConfig.API_BASE_URL)
                            .client(ok)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    apiService = retrofit.create(ApiService.class);
                }
            }
        }
        return apiService;
    }

    /** Call after logout to pick up new interceptors if needed. */
    public static void reset() {
        synchronized (ApiClient.class) {
            retrofit = null;
            apiService = null;
            okHttpClient = null;
        }
    }
}
