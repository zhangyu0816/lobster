package com.zb.lib_base.http;


import com.zb.lib_base.model.BaseEntity;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
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
public class HttpChatUploadManager {
    /*基础url*/
//    public static final String BASE_URL = "http://img01.weishangagent.com/";
    public static final String BASE_URL = "http://cimg.zuwo.la/";

    /*超时设置*/
    private static final int DEFAULT_TIMEOUT = 60;
    private HttpService httpService;
    private volatile static HttpChatUploadManager INSTANCE;

    //构造方法私有
    private HttpChatUploadManager() {
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.addInterceptor(new CommonInterceptor());
        builder.addInterceptor(new LoggingInterceptor());
        Retrofit retrofit = new Retrofit.Builder().client(builder.build()).addConverterFactory(GsonConverterFactory
                .create()).addCallAdapterFactory(RxJavaCallAdapterFactory.create()).baseUrl(BASE_URL).build();
        httpService = retrofit.create(HttpService.class);
    }

    //获取单例
    public static HttpChatUploadManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HttpChatUploadManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpChatUploadManager();
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
        ProgressSubscriber subscriber = new ProgressSubscriber(basePar.getListener(), basePar.getRxAppCompatActivity(),
                basePar.isShowProgress(), basePar.getDialogTitle());
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
