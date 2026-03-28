package com.example.main_screen.api;

import android.content.Context;

import com.example.main_screen.BuildConfig;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {
    private static volatile Retrofit retrofit;
    private static volatile ApiService apiService;

    private ApiClient() {
    }

    public static ApiService get(Context context) {
        if (apiService == null) {
            synchronized (ApiClient.class) {
                if (apiService == null) {
                    OkHttpClient ok = new OkHttpClient.Builder()
                            .addInterceptor(new AuthInterceptor(context))
                            .build();
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
        }
    }
}
