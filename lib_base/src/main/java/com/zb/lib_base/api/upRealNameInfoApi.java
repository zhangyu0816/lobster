package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class upRealNameInfoApi extends BaseEntity<BaseResultEntity> {
    String realName;
    String identityNum;
    String personalImage;
    String idFrontImage;
    String idBackImage;
    int verifyMethodType;

    public upRealNameInfoApi setRealName(String realName) {
        this.realName = realName;
        return this;
    }

    public upRealNameInfoApi setIdentityNum(String identityNum) {
        this.identityNum = identityNum;
        return this;
    }

    public upRealNameInfoApi setPersonalImage(String personalImage) {
        this.personalImage = personalImage;
        return this;
    }

    public upRealNameInfoApi setIdFrontImage(String idFrontImage) {
        this.idFrontImage = idFrontImage;
        return this;
    }

    public upRealNameInfoApi setIdBackImage(String idBackImage) {
        this.idBackImage = idBackImage;
        return this;
    }

    public upRealNameInfoApi setVerifyMethodType(int verifyMethodType) {
        this.verifyMethodType = verifyMethodType;
        return this;
    }

    public upRealNameInfoApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("提交实名认证信息");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.upRealNameInfo(realName, identityNum, personalImage, idFrontImage, idBackImage, verifyMethodType);
    }
}
