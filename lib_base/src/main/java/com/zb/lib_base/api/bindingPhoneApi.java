package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class bindingPhoneApi extends BaseEntity<BaseResultEntity> {
    String userName;
    String captcha;

    public bindingPhoneApi setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public bindingPhoneApi setCaptcha(String captcha) {
        this.captcha = captcha;
        return this;
    }

    public bindingPhoneApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("提交手机号");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.bindingPhone(userName, captcha);
    }
}
