package com.zb.lib_base.api;

import android.os.Build;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.LoginInfo;
import com.zb.lib_base.utils.PreferenceUtil;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

public class loginByUnionApi extends BaseEntity<LoginInfo> {
    String openId;
    String unionId;
    String unionNick;
    String unionImage;
    int unionSex;
    int unionType;

    String userName = "";           //用户名（手机号）  返回code = 301时传入
    String captcha;          //验证码 		返回code = 301时传入
    String moreImages;    //多图 【虾菇】  image1#image2#image3 这样的形式     返回code = 301时传入
    String nick;        //昵称    	返回code = 301时传入
    int sex;     //性别 		返回code = 301时传入
    String birthday;      //生日		返回code = 301时传入
    long provinceId;   //省份ID		返回code = 301时传入
    long cityId;       //城市ID		返回code = 301时传入
    long districtId;   //地区id			返回code = 301时传入

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

    public loginByUnionApi setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public loginByUnionApi setCaptcha(String captcha) {
        this.captcha = captcha;
        return this;
    }

    public loginByUnionApi setMoreImages(String moreImages) {
        this.moreImages = moreImages;
        return this;
    }

    public loginByUnionApi setNick(String nick) {
        this.nick = nick;
        return this;
    }

    public loginByUnionApi setSex(int sex) {
        this.sex = sex;
        return this;
    }

    public loginByUnionApi setBirthday(String birthday) {
        this.birthday = birthday;
        return this;
    }

    public loginByUnionApi setProvinceId(long provinceId) {
        this.provinceId = provinceId;
        return this;
    }

    public loginByUnionApi setCityId(long cityId) {
        this.cityId = cityId;
        return this;
    }

    public loginByUnionApi setDistrictId(long districtId) {
        this.districtId = districtId;
        return this;
    }

    public loginByUnionApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("提交登录信息");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        Map<String, String> map = new HashMap<>();
        map.put("openId", openId);
        map.put("unionId", unionId);
        map.put("unionNick", unionNick);
        map.put("unionImage", unionImage);
        map.put("unionSex", unionSex + "");
        map.put("unionType", unionType + "");
        map.put("device", "android");
        map.put("deviceSysVersion", Build.VERSION.RELEASE);
        map.put("deviceCode", PreferenceUtil.readStringValue(getRxAppCompatActivity(), "deviceCode"));
        map.put("channelId", PreferenceUtil.readStringValue(getRxAppCompatActivity(), "channelId"));
        map.put("usePl", "2");
        map.put("appVersion", MineApp.versionName);
//        map.put("deviceHardwareInfo", PreferenceUtil.readStringValue(getRxAppCompatActivity(), "deviceHardwareInfo"));

        if (!userName.isEmpty()) {
            map.put("userName", userName);
            map.put("captcha", captcha);
            map.put("moreImages", moreImages);
            map.put("nick", nick);
            map.put("sex", sex + "");
            map.put("birthday", birthday);
            map.put("provinceId", provinceId+"");
            map.put("cityId", cityId+"");
            map.put("districtId", districtId+"");
        }
        return methods.loginByUnion(map);
    }
}
