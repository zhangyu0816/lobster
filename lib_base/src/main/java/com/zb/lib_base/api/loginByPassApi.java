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

public class loginByPassApi extends BaseEntity<LoginInfo> {
    String userName;            //用户名
    String passWord;

    public loginByPassApi setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public loginByPassApi setPassWord(String passWord) {
        this.passWord = passWord;
        return this;
    }

    public loginByPassApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("获取登录信息");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.loginByPass(userName, passWord, "Android", Build.VERSION.RELEASE, PreferenceUtil.readStringValue(getRxAppCompatActivity(), "deviceCode"), PreferenceUtil.readStringValue(getRxAppCompatActivity(), "channelId"),
                2, MineApp.versionName, PreferenceUtil.readStringValue(getRxAppCompatActivity(), "deviceHardwareInfo"));
    }
}
