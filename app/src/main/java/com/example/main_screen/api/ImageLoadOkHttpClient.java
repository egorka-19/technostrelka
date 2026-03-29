package com.example.main_screen.api;

import android.content.Context;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

/**
 * Отдельный клиент только для загрузки картинок (Glide). Некоторые внешние сайты
 * (например региональные порталы) отдают HTTPS с цепочкой, которую Android не принимает
 * как доверенную — тогда строгая проверка падает с {@code CertPathValidatorException}.
 * К запросам API этот клиент не применяется — там остаётся обычный {@link ApiClient}.
 */
public final class ImageLoadOkHttpClient {

    private static volatile OkHttpClient instance;

    private ImageLoadOkHttpClient() {
    }

    public static OkHttpClient get(Context context) {
        if (instance == null) {
            synchronized (ImageLoadOkHttpClient.class) {
                if (instance == null) {
                    Context app = context.getApplicationContext();
                    instance = build(app);
                }
            }
        }
        return instance;
    }

    private static OkHttpClient build(Context appContext) {
        try {
            TrustManager[] trustAll = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAll, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            X509TrustManager trustManager = (X509TrustManager) trustAll[0];

            return new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .sslSocketFactory(sslSocketFactory, trustManager)
                    .hostnameVerifier((hostname, session) -> true)
                    .addInterceptor(new AuthInterceptor(appContext))
                    .build();
        } catch (Exception e) {
            return new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(new AuthInterceptor(appContext))
                    .build();
        }
    }
}
