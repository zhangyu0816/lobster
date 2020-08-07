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

public class registerByUnionApi extends BaseEntity<LoginInfo> {
    String openId;
    String unionId;
    String unionNick;
    String unionImage;
    int unionSex;
    int unionType;
    String userName;
    String captcha;

    public registerByUnionApi setOpenId(String openId) {
        this.openId = openId;
        return this;
    }

    public registerByUnionApi setUnionId(String unionId) {
        this.unionId = unionId;
        return this;
    }

    public registerByUnionApi setUnionNick(String unionNick) {
        this.unionNick = unionNick;
        return this;
    }

    public registerByUnionApi setUnionImage(String unionImage) {
        this.unionImage = unionImage;
        return this;
    }

    public registerByUnionApi setUnionSex(int unionSex) {
        this.unionSex = unionSex;
        return this;
    }

    public registerByUnionApi setUnionType(int unionType) {
        this.unionType = unionType;
        return this;
    }

    public registerByUnionApi setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public registerByUnionApi setCaptcha(String captcha) {
        this.captcha = captcha;
        return this;
    }

    public registerByUnionApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("提交登录信息");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.registerByUnion(openId, unionId, unionNick, unionImage, unionSex, unionType, "android",
                Build.VERSION.RELEASE, PreferenceUtil.readStringValue(getRxAppCompatActivity(), "deviceCode"),
                PreferenceUtil.readStringValue(getRxAppCompatActivity(), "channelId"), 2,
                MineApp.versionName, PreferenceUtil.readStringValue(getRxAppCompatActivity(), "deviceHardwareInfo"), userName, captcha);
    }
}
