package com.zb.lib_base.http;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class UploadImageHelper {

    private static UploadImageHelper instance;
    public OkHttpClient.Builder builder;

    private UploadImageHelper() {
        builder = new OkHttpClient.Builder();
        builder.readTimeout(60L, TimeUnit.SECONDS);
        builder.writeTimeout(60L, TimeUnit.SECONDS);
        builder.connectTimeout(60L, TimeUnit.SECONDS);
        builder.addInterceptor(new CommonInterceptor());
        builder.addInterceptor(new LoggingInterceptor());
    }

    public static UploadImageHelper getInstance() {
        if (instance == null) {
            instance = new UploadImageHelper();
        }
        return instance;
    }
}
