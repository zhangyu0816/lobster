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

public class registerApi extends BaseEntity<LoginInfo> {
    String userName;             //用户名（手机号）
    String captcha;           //验证码
    String moreImages;   //多图 【虾菇】  image1#image2#image3 这样的形式
    String nick;        //昵称
    Integer sex;        //性别
    String birthday;     //生日
    long provinceId;    //省份ID
    long cityId;       //城市ID
    long districtId;       //城市ID

    public registerApi setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public registerApi setCaptcha(String captcha) {
        this.captcha = captcha;
        return this;
    }

    public registerApi setMoreImages(String moreImages) {
        this.moreImages = moreImages;
        return this;
    }

    public registerApi setNick(String nick) {
        this.nick = nick;
        return this;
    }

    public registerApi setSex(Integer sex) {
        this.sex = sex;
        return this;
    }

    public registerApi setBirthday(String birthday) {
        this.birthday = birthday;
        return this;
    }

    public registerApi setProvinceId(long provinceId) {
        this.provinceId = provinceId;
        return this;
    }

    public registerApi setCityId(long cityId) {
        this.cityId = cityId;
        return this;
    }

    public registerApi setDistrictId(long districtId) {
        this.districtId = districtId;
        return this;
    }

    public registerApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("正在提交注册信息");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.register(userName, captcha, moreImages, nick, sex, birthday, provinceId, cityId, districtId,
                "Android", Build.VERSION.RELEASE, PreferenceUtil.readStringValue(getRxAppCompatActivity(), "deviceCode"), PreferenceUtil.readStringValue(getRxAppCompatActivity(), "channelId"),
                2, MineApp.versionName, PreferenceUtil.readStringValue(getRxAppCompatActivity(), "deviceHardwareInfo"));
    }
}
