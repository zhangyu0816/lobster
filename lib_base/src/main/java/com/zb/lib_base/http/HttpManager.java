package com.zb.lib_base.http;


import com.google.gson.Gson;
import com.zb.lib_base.log.HttpLogger;
import com.zb.lib_base.model.BaseEntity;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * http交互处理类
 * Created by WZG on 2016/7/16.
 */
public class HttpManager {
    /* 平台服务器 */
    public static String BASE_URL = "https://xgapi.zuwo.la/";
//        public static String BASE_URL = "http://192.168.1.88:8090/";//敏耀
//    public static String BASE_URL = "http://317a598y11.wicp.vip/";//敏耀
    /*超时设置*/
    private static final int DEFAULT_TIMEOUT = 6;
    private HttpService httpService;
    public volatile static HttpManager INSTANCE;

    //构造方法私有
    private HttpManager() {
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.addInterceptor(new CommonInterceptor());

        // http log
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addNetworkInterceptor(logInterceptor);

        Retrofit retrofit = new Retrofit.Builder().client(builder.build()).addConverterFactory(GsonConverterFactory
                .create(new Gson())).addCallAdapterFactory(RxJavaCallAdapterFactory.create()).baseUrl(BASE_URL).build();
        httpService = retrofit.create(HttpService.class);
    }

    //获取单例
    public static HttpManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HttpManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 处理http请求
     *
     * @param basePar 封装的请求数据
     */
    public void doHttpDeal(BaseEntity basePar) {
        ProgressSubscriber subscriber = new ProgressSubscriber(basePar.getListener(), basePar.getRxAppCompatActivity
                (), basePar.isShowProgress(), basePar.getDialogTitle(), basePar.getPosition());
        Observable observable = basePar.getObservable(httpService)
                /*失败后的retry配置*/.retryWhen(new RetryWhenNetworkException());
        if (basePar.getRxAppCompatActivity() != null) {
            /*生命周期管理*/
            observable = observable.compose(basePar.getRxAppCompatActivity().bindToLifecycle());
        }
        /*http请求线程*/
        observable = observable.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io())
                /*回调线程*/.observeOn(AndroidSchedulers.mainThread())
                /*结果判断*/.map(basePar);
        /*数据回调*/
        observable.subscribe(subscriber);
    }

}
