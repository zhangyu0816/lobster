package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class MemberInfo extends BaseObservable {
    private long userId;      //userId
    private String nick = "";        //昵称
    private String image = "";       //头像
    private String moreImages = "";   // 多图
    private String personalitySign = "";//个性签名
    private String shopUrl = "";    //网店地址
    private String birthday = "";    //生日
    private int sex;       //性别  0女  1男
    private int height;       //身高 CM
    private int weight;      //体重 公斤
    private int constellation; //星座
    private int bloodType;      //AB0血型系统 A B O AB 其他
    private String job = "";     //职业
    private int education;//学历
    private long provinceId;   //省份ID
    private long cityId;     //地区ID
    private long districtId;   //地区id
    private int rstatus;     //1：好友  2：非好友
    private String remark = "";      //备注
    private int attentionQuantity; //关注数量
    private int fansQuantity;//粉丝数量
    private int popularity;  //人气(浏览量)
    private int pollQuantity;//赞数
    private int rentQuantity;// 订单量（出租多少次 ）
    private int beLikeQuantity;//被喜欢数
    private int memberType;  //1免费用户   2 .会员
    private String serviceTags = "";

    @Bindable
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
        notifyPropertyChanged(BR.userId);
    }

    @Bindable
    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
        notifyPropertyChanged(BR.nick);
    }

    @Bindable
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
        notifyPropertyChanged(BR.image);
    }

    @Bindable
    public String getMoreImages() {
        return moreImages;
    }

    public void setMoreImages(String moreImages) {
        this.moreImages = moreImages;
        notifyPropertyChanged(BR.moreImages);
    }

    @Bindable
    public String getPersonalitySign() {
        return personalitySign;
    }

    public void setPersonalitySign(String personalitySign) {
        this.personalitySign = personalitySign;
        notifyPropertyChanged(BR.personalitySign);
    }

    @Bindable
    public String getShopUrl() {
        return shopUrl;
    }

    public void setShopUrl(String shopUrl) {
        this.shopUrl = shopUrl;
        notifyPropertyChanged(BR.shopUrl);
    }

    @Bindable
    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
        notifyPropertyChanged(BR.birthday);
    }

    @Bindable
    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
        notifyPropertyChanged(BR.sex);
    }

    @Bindable
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        notifyPropertyChanged(BR.height);
    }

    @Bindable
    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
        notifyPropertyChanged(BR.weight);
    }

    @Bindable
    public int getConstellation() {
        return constellation;
    }

    public void setConstellation(int constellation) {
        this.constellation = constellation;
        notifyPropertyChanged(BR.constellation);
    }

    @Bindable
    public int getBloodType() {
        return bloodType;
    }

    public void setBloodType(int bloodType) {
        this.bloodType = bloodType;
        notifyPropertyChanged(BR.bloodType);
    }

    @Bindable
    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
        notifyPropertyChanged(BR.job);
    }

    @Bindable
    public int getEducation() {
        return education;
    }

    public void setEducation(int education) {
        this.education = education;
        notifyPropertyChanged(BR.education);
    }

    @Bindable
    public long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(long provinceId) {
        this.provinceId = provinceId;
        notifyPropertyChanged(BR.provinceId);
    }

    @Bindable
    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
        notifyPropertyChanged(BR.cityId);
    }

    @Bindable
    public long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(long districtId) {
        this.districtId = districtId;
        notifyPropertyChanged(BR.districtId);
    }

    @Bindable
    public int getRstatus() {
        return rstatus;
    }

    public void setRstatus(int rstatus) {
        this.rstatus = rstatus;
        notifyPropertyChanged(BR.rstatus);
    }

    @Bindable
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
        notifyPropertyChanged(BR.remark);
    }

    @Bindable
    public int getAttentionQuantity() {
        return attentionQuantity;
    }

    public void setAttentionQuantity(int attentionQuantity) {
        this.attentionQuantity = attentionQuantity;
        notifyPropertyChanged(BR.attentionQuantity);
    }

    @Bindable
    public int getFansQuantity() {
        return fansQuantity;
    }

    public void setFansQuantity(int fansQuantity) {
        this.fansQuantity = fansQuantity;
        notifyPropertyChanged(BR.fansQuantity);
    }

    @Bindable
    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
        notifyPropertyChanged(BR.popularity);
    }

    @Bindable
    public int getPollQuantity() {
        return pollQuantity;
    }

    public void setPollQuantity(int pollQuantity) {
        this.pollQuantity = pollQuantity;
        notifyPropertyChanged(BR.pollQuantity);
    }

    @Bindable
    public int getRentQuantity() {
        return rentQuantity;
    }

    public void setRentQuantity(int rentQuantity) {
        this.rentQuantity = rentQuantity;
        notifyPropertyChanged(BR.rentQuantity);
    }

    @Bindable
    public int getBeLikeQuantity() {
        return beLikeQuantity;
    }

    public void setBeLikeQuantity(int beLikeQuantity) {
        this.beLikeQuantity = beLikeQuantity;
        notifyPropertyChanged(BR.beLikeQuantity);
    }

    @Bindable
    public int getMemberType() {
        return memberType;
    }

    public void setMemberType(int memberType) {
        this.memberType = memberType;
        notifyPropertyChanged(BR.memberType);
    }

    @Bindable
    public String getServiceTags() {
        return serviceTags;
    }

    public void setServiceTags(String serviceTags) {
        this.serviceTags = serviceTags;
        notifyPropertyChanged(BR.serviceTags);
    }
}
