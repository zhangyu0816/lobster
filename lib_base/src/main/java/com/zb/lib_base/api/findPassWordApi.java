package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;

import rx.Observable;

public class findPassWordApi extends BaseEntity<BaseResultEntity> {
    String userName;           //用户名
    String passWord;           //密码
    String captcha;          //验证码

    public findPassWordApi setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public findPassWordApi setPassWord(String passWord) {
        this.passWord = passWord;
        return this;
    }

    public findPassWordApi setCaptcha(String captcha) {
        this.captcha = captcha;
        return this;
    }

    public findPassWordApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("提交新密码");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.findPassWord(userName, passWord, captcha);
    }
}
