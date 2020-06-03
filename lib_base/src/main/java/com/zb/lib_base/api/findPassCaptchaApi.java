package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class findPassCaptchaApi extends BaseEntity<BaseResultEntity> {
    String userName;   //手机号 用户   唯一键
    String imageCaptchaToken;//图片验证令牌
    String imageCaptchaCode; //图片验证值

    public findPassCaptchaApi setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public findPassCaptchaApi setImageCaptchaToken(String imageCaptchaToken) {
        this.imageCaptchaToken = imageCaptchaToken;
        return this;
    }

    public findPassCaptchaApi setImageCaptchaCode(String imageCaptchaCode) {
        this.imageCaptchaCode = imageCaptchaCode;
        return this;
    }

    public findPassCaptchaApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("发送短信验证码");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.findPassCaptcha(userName, imageCaptchaToken, imageCaptchaCode);
    }
}
