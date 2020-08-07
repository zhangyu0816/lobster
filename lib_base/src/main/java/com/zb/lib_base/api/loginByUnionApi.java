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

public class loginByUnionApi extends BaseEntity<LoginInfo> {
    String openId;
    String unionId;
    String unionNick;
    String unionImage;
    int unionSex;
    int unionType;

    public loginByUnionApi setOpenId(String openId) {
        this.openId = openId;
        return this;
    }

    public loginByUnionApi setUnionId(String unionId) {
        this.unionId = unionId;
        return this;
    }

    public loginByUnionApi setUnionNick(String unionNick) {
        this.unionNick = unionNick;
        return this;
    }

    public loginByUnionApi setUnionImage(String unionImage) {
        this.unionImage = unionImage;
        return this;
    }

    public loginByUnionApi setUnionSex(int unionSex) {
        this.unionSex = unionSex;
        return this;
    }

    public loginByUnionApi setUnionType(int unionType) {
        this.unionType = unionType;
        return this;
    }

    public loginByUnionApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("提交登录信息");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.loginByUnion(openId, unionId, unionNick, unionImage, unionSex, unionType, "android",
                Build.VERSION.RELEASE, PreferenceUtil.readStringValue(getRxAppCompatActivity(), "deviceCode"),
                PreferenceUtil.readStringValue(getRxAppCompatActivity(), "channelId"), 2,
                MineApp.versionName, PreferenceUtil.readStringValue(getRxAppCompatActivity(), "deviceHardwareInfo"));
    }
}
