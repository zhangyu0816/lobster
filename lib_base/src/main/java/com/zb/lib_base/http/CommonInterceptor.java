package com.zb.lib_base.http;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ZhengQunWei
 * on 2017/7/6
 */
public class CommonInterceptor implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();

        // 添加新的参数
        HttpUrl.Builder authorizedUrlBuilder = oldRequest.url().newBuilder().scheme(oldRequest.url().scheme()).host
                (oldRequest.url().host()).addQueryParameter("pfDevice", "Android").addQueryParameter("pfAppType", "203").addQueryParameter("pfAppVersion",
                MineApp.versionName).addQueryParameter("userId", BaseActivity.userId + "").addQueryParameter("sessionId", BaseActivity.sessionId);

        // 新的请求
        Request newRequest = oldRequest.newBuilder().method(oldRequest.method(), oldRequest.body()).url
                (authorizedUrlBuilder.build()).build();

        return chain.proceed(newRequest);
    }
}
