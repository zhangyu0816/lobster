package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class registerCaptchaApi extends BaseEntity<BaseResultEntity> {
    String userName;

    public registerCaptchaApi setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public registerCaptchaApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("正在获取短信验证码");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.registerCaptcha(userName);
    }
}
