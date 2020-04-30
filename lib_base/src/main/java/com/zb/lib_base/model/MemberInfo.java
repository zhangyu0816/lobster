package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class MemberInfo extends BaseObservable {
    private long otherUserId;   //用户id
    private long userId; // userId
    private String nick = ""; // 昵称
    private String remark = ""; // 备注
    private String image = ""; // 头像
    private String moreImages = "";     // 多图
    private String personalitySign = "";// 个性签名
    private String birthday = ""; // 生日
    private int sex; // 性别 0女 1男
    private int height; // 身高 CM
    private int constellation = 1;// 星座
    private int age;// 年龄
    private String job = ""; // 职业
    private long provinceId; // 省份ID
    private long cityId; // 城市ID
    private long districtId = 0;
    private int idAttest; // 实名认证 V 0未认证 1认证
    private String serviceTags = "";
    private String singleImage = ""; // 单张形象图
    private int rstatus; // 1：好友 2：非好友
    private int popularity; // 人气(浏览量)
    private int rentQuantity;// 订单量（出租多少次 ）
    private int memberType; // 1免费用户 2 .会员
    private String memberExpireTime = ""; // 会员到期时间
    private int totalOrderNumber;// 总下单次数 // 1000000(MAX) -等于大于1000000 就是无限
    private int surplusOrderNumber;// 剩余下单次数// 1000000(MAX) -等于大于1000000 就是无限
    private int totalLookPrivateNumber; // 总查看专享相册次数 // 1000000(MAX)
    private int surplusLookPrivateNumber; // 剩余查看专享次数// 1000000(MAX)
    private String publicTag = "";// 标签:租我官方
    private int authType;// 权限类型 1.无限制 2.以下限制 3.无输入框
    private int onlineStatus = 1;//  0未在线  1在线
    private int attentionQuantity;//关注数量
    private int fansQuantity;// 粉丝数量
    private int beLikeQuantity;// 粉丝数量
    private String lastJoinTime = "";
    private int newDycType; // 最新的动态类型
    // 未知 0
    // 文字 1
    // 图片 2
    // 图文 3
    // 视频 4
    // 视频_文字 5
    // 视频_图片 6
    // 视频_图片_文字 7
    // 专享 8 (未使用)
    // 专享文字 9 (未使用)
    // 专享图片 10
    // 专享图文 11
    // 专享视频 12 (未使用)
    // 专享视频文字 13 (未使用)
    // 专享视频图片 14 (未使用)
    // 专享视频图文 15 (未使用)
    private String newDycCreateTime = ""; // 最新创建时间


    @Bindable
    public long getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
        notifyPropertyChanged(BR.otherUserId);
    }

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
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
        notifyPropertyChanged(BR.remark);
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
    public int getConstellation() {
        return constellation;
    }

    public void setConstellation(int constellation) {
        this.constellation = constellation;
        notifyPropertyChanged(BR.constellation);
    }

    @Bindable
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
        notifyPropertyChanged(BR.age);
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
    public int getIdAttest() {
        return idAttest;
    }

    public void setIdAttest(int idAttest) {
        this.idAttest = idAttest;
        notifyPropertyChanged(BR.idAttest);
    }

    @Bindable
    public String getServiceTags() {
        return serviceTags;
    }

    public void setServiceTags(String serviceTags) {
        this.serviceTags = serviceTags;
        notifyPropertyChanged(BR.serviceTags);
    }

    @Bindable
    public String getSingleImage() {
        return singleImage;
    }

    public void setSingleImage(String singleImage) {
        this.singleImage = singleImage;
        notifyPropertyChanged(BR.singleImage);
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
    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
        notifyPropertyChanged(BR.popularity);
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
    public int getMemberType() {
        return memberType;
    }

    public void setMemberType(int memberType) {
        this.memberType = memberType;
        notifyPropertyChanged(BR.memberType);
    }

    @Bindable
    public String getMemberExpireTime() {
        return memberExpireTime;
    }

    public void setMemberExpireTime(String memberExpireTime) {
        this.memberExpireTime = memberExpireTime;
        notifyPropertyChanged(BR.memberExpireTime);
    }

    @Bindable
    public int getTotalOrderNumber() {
        return totalOrderNumber;
    }

    public void setTotalOrderNumber(int totalOrderNumber) {
        this.totalOrderNumber = totalOrderNumber;
        notifyPropertyChanged(BR.totalOrderNumber);
    }

    @Bindable
    public int getSurplusOrderNumber() {
        return surplusOrderNumber;
    }

    public void setSurplusOrderNumber(int surplusOrderNumber) {
        this.surplusOrderNumber = surplusOrderNumber;
        notifyPropertyChanged(BR.surplusOrderNumber);
    }

    @Bindable
    public int getTotalLookPrivateNumber() {
        return totalLookPrivateNumber;
    }

    public void setTotalLookPrivateNumber(int totalLookPrivateNumber) {
        this.totalLookPrivateNumber = totalLookPrivateNumber;
        notifyPropertyChanged(BR.totalLookPrivateNumber);
    }

    @Bindable
    public int getSurplusLookPrivateNumber() {
        return surplusLookPrivateNumber;
    }

    public void setSurplusLookPrivateNumber(int surplusLookPrivateNumber) {
        this.surplusLookPrivateNumber = surplusLookPrivateNumber;
        notifyPropertyChanged(BR.surplusLookPrivateNumber);
    }

    @Bindable
    public String getPublicTag() {
        return publicTag;
    }

    public void setPublicTag(String publicTag) {
        this.publicTag = publicTag;
        notifyPropertyChanged(BR.publicTag);
    }

    @Bindable
    public int getAuthType() {
        return authType;
    }

    public void setAuthType(int authType) {
        this.authType = authType;
        notifyPropertyChanged(BR.authType);
    }

    @Bindable
    public int getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(int onlineStatus) {
        this.onlineStatus = onlineStatus;
        notifyPropertyChanged(BR.onlineStatus);
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
    public String getLastJoinTime() {
        return lastJoinTime;
    }

    public void setLastJoinTime(String lastJoinTime) {
        this.lastJoinTime = lastJoinTime;
        notifyPropertyChanged(BR.lastJoinTime);
    }

    @Bindable
    public int getNewDycType() {
        return newDycType;
    }

    public void setNewDycType(int newDycType) {
        this.newDycType = newDycType;
        notifyPropertyChanged(BR.newDycType);
    }

    @Bindable
    public String getNewDycCreateTime() {
        return newDycCreateTime;
    }

    public void setNewDycCreateTime(String newDycCreateTime) {
        this.newDycCreateTime = newDycCreateTime;
        notifyPropertyChanged(BR.newDycCreateTime);
    }

    @Bindable
    public int getBeLikeQuantity() {
        return beLikeQuantity;
    }

    public void setBeLikeQuantity(int beLikeQuantity) {
        this.beLikeQuantity = beLikeQuantity;
        notifyPropertyChanged(BR.beLikeQuantity);
    }

}
