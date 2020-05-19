package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class modifyPassApi extends BaseEntity<BaseResultEntity> {
    String oldPassWord;    //密码
    String newPassWord;    //新密码

    public modifyPassApi setOldPassWord(String oldPassWord) {
        this.oldPassWord = oldPassWord;
        return this;
    }

    public modifyPassApi setNewPassWord(String newPassWord) {
        this.newPassWord = newPassWord;
        return this;
    }

    public modifyPassApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("提交新密码");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.modifyPass(oldPassWord, newPassWord);
    }
}
