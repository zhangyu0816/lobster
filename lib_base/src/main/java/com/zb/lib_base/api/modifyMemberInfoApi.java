package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;
import com.zb.lib_base.utils.DateUtil;

import rx.Observable;

public class modifyMemberInfoApi extends BaseEntity<BaseResultEntity> {
    String nick;          //昵称
    String image;    //头像
    String moreImages;    //多图 【虾菇】
    String personalitySign;//个性签名
    String birthday;    //生日
    int sex;         //性别  0女  1男
    String job;         //职业
    long provinceId;    //省份ID
    long cityId;       //城市ID
    long districtId;       //城市ID
    String serviceTags;

    public modifyMemberInfoApi setNick(String nick) {
        this.nick = nick;
        return this;
    }

    public modifyMemberInfoApi setImage(String image) {
        this.image = image;
        return this;
    }

    public modifyMemberInfoApi setMoreImages(String moreImages) {
        this.moreImages = moreImages;
        return this;
    }

    public modifyMemberInfoApi setPersonalitySign(String personalitySign) {
        this.personalitySign = personalitySign;
        return this;
    }

    public modifyMemberInfoApi setBirthday(String birthday) {
        this.birthday = birthday;
        return this;
    }

    public modifyMemberInfoApi setSex(int sex) {
        this.sex = sex;
        return this;
    }

    public modifyMemberInfoApi setJob(String job) {
        this.job = job;
        return this;
    }

    public modifyMemberInfoApi setProvinceId(long provinceId) {
        this.provinceId = provinceId;
        return this;
    }

    public modifyMemberInfoApi setCityId(long cityId) {
        this.cityId = cityId;
        return this;
    }

    public modifyMemberInfoApi setDistrictId(long districtId) {
        this.districtId = districtId;
        return this;
    }

    public modifyMemberInfoApi setServiceTags(String serviceTags) {
        this.serviceTags = serviceTags;
        return this;
    }

    public modifyMemberInfoApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setDialogTitle("上传个人信息");
    }

    @Override
    public Observable getObservable(HttpService methods) {
        return methods.modifyMemberInfo(nick, image, moreImages, personalitySign, birthday, DateUtil.getAge(birthday, 31), sex, 0, job, provinceId, cityId, districtId, "", serviceTags);
    }
}
