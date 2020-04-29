package com.zb.lib_base.api;

import android.os.Build;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.LoginInfo;
import com.zb.lib_base.utils.PreferenceUtil;

import rx.Observable;

public class loginByCaptchaApi extends BaseEntity<LoginInfo> {
    String userName;             //手机号
    String captcha;             //验证码

    public loginByCaptchaApi setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public loginByCaptchaApi setCaptcha(String captcha) {
        this.captcha = captcha;
        return this;
    }

    public loginByCaptchaApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("获取登录信息");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.loginByCaptcha(userName, captcha, "Android", Build.VERSION.RELEASE, PreferenceUtil.readStringValue(getRxAppCompatActivity(), "deviceCode"), PreferenceUtil.readStringValue(getRxAppCompatActivity(), "channelId"),
                2, MineApp.versionName, PreferenceUtil.readStringValue(getRxAppCompatActivity(), "deviceHardwareInfo"));
    }
}
