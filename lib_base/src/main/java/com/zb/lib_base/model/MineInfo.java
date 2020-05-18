package com.zb.lib_base.model;

import io.realm.RealmObject;

public class MineInfo extends RealmObject {
    long userId;      //userId
    String nick = "";      //昵称
    String image = "";     //头像
    String moreImages = "";   // 多图
    String personalitySign = "";//个性签名
    String birthday = "";    //生日
    int age;     //年龄
    int sex;  //性别  0女  1男
    int height;   //身高 CM
    int constellation; //星座
    String job = "";        //职业
    int idAttest;  //实名认证 V  0未认证  1认证
    int memberType;  //1免费用户   2 .会员
    String memberExpireTime = "";//会员到期时间
    long provinceId;//省份ID
    long cityId;    //城市ID
    long districtId;   //地区id
    String serviceTags = "";

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMoreImages() {
        return moreImages;
    }

    public void setMoreImages(String moreImages) {
        this.moreImages = moreImages;
    }

    public String getPersonalitySign() {
        return personalitySign;
    }

    public void setPersonalitySign(String personalitySign) {
        this.personalitySign = personalitySign;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getConstellation() {
        return constellation;
    }

    public void setConstellation(int constellation) {
        this.constellation = constellation;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public int getIdAttest() {
        return idAttest;
    }

    public void setIdAttest(int idAttest) {
        this.idAttest = idAttest;
    }

    public int getMemberType() {
        return memberType;
    }

    public void setMemberType(int memberType) {
        this.memberType = memberType;
    }

    public String getMemberExpireTime() {
        return memberExpireTime;
    }

    public void setMemberExpireTime(String memberExpireTime) {
        this.memberExpireTime = memberExpireTime;
    }

    public long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(long provinceId) {
        this.provinceId = provinceId;
    }

    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }

    public long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(long districtId) {
        this.districtId = districtId;
    }

    public String getServiceTags() {
        return serviceTags;
    }

    public void setServiceTags(String serviceTags) {
        this.serviceTags = serviceTags;
    }
}
