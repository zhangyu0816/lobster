package com.zb.lib_base.api;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.model.BaseEntity;
import com.zb.lib_base.model.BaseResultEntity;
import com.zb.lib_base.utils.DateUtil;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

public class modifyMemberInfoForNoVerifyApi extends BaseEntity<BaseResultEntity> {
    String nick = "";          //昵称
    String image = "";    //头像
    String moreImages = "";    //多图 【虾菇】
    String personalitySign = "";//个性签名
    String birthday = "";    //生日
    int sex = -1;         //性别  0女  1男
    String job = "";         //职业
    long provinceId;    //省份ID
    long cityId;       //城市ID
    long districtId;       //城市ID
    String serviceTags = "";
    int height;

    public modifyMemberInfoForNoVerifyApi setNick(String nick) {
        this.nick = nick;
        return this;
    }

    public modifyMemberInfoForNoVerifyApi setImage(String image) {
        this.image = image;
        return this;
    }

    public modifyMemberInfoForNoVerifyApi setMoreImages(String moreImages) {
        this.moreImages = moreImages;
        return this;
    }

    public modifyMemberInfoForNoVerifyApi setPersonalitySign(String personalitySign) {
        this.personalitySign = personalitySign;
        return this;
    }

    public modifyMemberInfoForNoVerifyApi setBirthday(String birthday) {
        this.birthday = birthday;
        return this;
    }

    public modifyMemberInfoForNoVerifyApi setSex(int sex) {
        this.sex = sex;
        return this;
    }

    public modifyMemberInfoForNoVerifyApi setJob(String job) {
        this.job = job;
        return this;
    }

    public modifyMemberInfoForNoVerifyApi setProvinceId(long provinceId) {
        this.provinceId = provinceId;
        return this;
    }

    public modifyMemberInfoForNoVerifyApi setCityId(long cityId) {
        this.cityId = cityId;
        return this;
    }

    public modifyMemberInfoForNoVerifyApi setDistrictId(long districtId) {
        this.districtId = districtId;
        return this;
    }

    public modifyMemberInfoForNoVerifyApi setServiceTags(String serviceTags) {
        this.serviceTags = serviceTags;
        return this;
    }

    public modifyMemberInfoForNoVerifyApi setHeight(int height) {
        this.height = height;
        return this;
    }

    public modifyMemberInfoForNoVerifyApi(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        super(listener, rxAppCompatActivity);
        setShowProgress(false);
    }

    @Override
    public Observable getObservable(HttpService methods) {
        Map<String, Object> map = new HashMap<>();
        if (!nick.isEmpty())
            map.put("nick", nick);
        if (!image.isEmpty())
            map.put("image", image);
        if (!moreImages.isEmpty())
            map.put("moreImages", moreImages);
        if (!personalitySign.isEmpty())
            map.put("personalitySign", personalitySign);
        if (!serviceTags.isEmpty())
            map.put("serviceTags", serviceTags);
        if (!birthday.isEmpty()) {
            map.put("birthday", birthday);
            map.put("age", DateUtil.getAge(birthday, 31));
        }
        if (sex != -1)
            map.put("sex", sex);
        map.put("constellation", 0);
        if (!job.isEmpty())
            map.put("job", job);
        if (provinceId != 0)
            map.put("provinceId", provinceId);
        if (cityId != 0)
            map.put("cityId", cityId);
        if (districtId != 0)
            map.put("districtId", districtId);
        if (height != 0)
            map.put("height", height);
        map.put("singleImage", "");

        return methods.modifyMemberInfoForNoVerify(map);
    }
}
