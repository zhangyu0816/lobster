package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class verifyCaptchaApi extends BaseEntity<BaseResultEntity> {
    String userName;
    String captcha;

    public verifyCaptchaApi setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public verifyCaptchaApi setCaptcha(String captcha) {
        this.captcha = captcha;
        return this;
    }

    public verifyCaptchaApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.verifyCaptcha(userName, captcha);
    }
}
