package com.zb.lib_base.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class MemberInfo extends BaseObservable implements Parcelable {
    private long userId;      //userId
    private String nick = "";        //昵称
    private String image = "";       //头像
    private String moreImages = "";   // 多图
    private String personalitySign = "";//个性签名
    private String shopUrl = "";    //网店地址
    private String birthday = "";    //生日
    private int age;        //年龄
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
    private String newDycCreateTime = "";  //最新创建时间   3天内的属于新增动态
    private int idAttest;  //实名认证 V  0未认证  1认证
    private int faceAttest;  //人脸 V  0未认证  1认证
    private int newDycType; // 最新的动态类型       配上对应的文字
    private int attentionStatus; //关注关系  1 关注  0 未关注
    private String distance = "";
    private String singleImage = "";

    public MemberInfo() {
    }

    protected MemberInfo(Parcel in) {
        userId = in.readLong();
        nick = in.readString();
        image = in.readString();
        moreImages = in.readString();
        personalitySign = in.readString();
        shopUrl = in.readString();
        birthday = in.readString();
        age = in.readInt();
        sex = in.readInt();
        height = in.readInt();
        weight = in.readInt();
        constellation = in.readInt();
        bloodType = in.readInt();
        job = in.readString();
        education = in.readInt();
        provinceId = in.readLong();
        cityId = in.readLong();
        districtId = in.readLong();
        rstatus = in.readInt();
        remark = in.readString();
        attentionQuantity = in.readInt();
        fansQuantity = in.readInt();
        popularity = in.readInt();
        pollQuantity = in.readInt();
        rentQuantity = in.readInt();
        beLikeQuantity = in.readInt();
        memberType = in.readInt();
        serviceTags = in.readString();
        newDycCreateTime = in.readString();
        idAttest = in.readInt();
        faceAttest = in.readInt();
        newDycType = in.readInt();
        attentionStatus = in.readInt();
        distance = in.readString();
        singleImage = in.readString();
    }

    public static final Creator<MemberInfo> CREATOR = new Creator<MemberInfo>() {
        @Override
        public MemberInfo createFromParcel(Parcel in) {
            return new MemberInfo(in);
        }

        @Override
        public MemberInfo[] newArray(int size) {
            return new MemberInfo[size];
        }
    };

    @Bindable
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
        notifyPropertyChanged(BR.age);
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

    @Bindable
    public String getNewDycCreateTime() {
        return newDycCreateTime;
    }

    public void setNewDycCreateTime(String newDycCreateTime) {
        this.newDycCreateTime = newDycCreateTime;
        notifyPropertyChanged(BR.newDycCreateTime);
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
    public int getNewDycType() {
        return newDycType;
    }

    public void setNewDycType(int newDycType) {
        this.newDycType = newDycType;
        notifyPropertyChanged(BR.newDycType);
    }

    @Bindable
    public int getAttentionStatus() {
        return attentionStatus;
    }

    public void setAttentionStatus(int attentionStatus) {
        this.attentionStatus = attentionStatus;
        notifyPropertyChanged(BR.attentionStatus);
    }

    @Bindable
    public int getFaceAttest() {
        return faceAttest;
    }

    public void setFaceAttest(int faceAttest) {
        this.faceAttest = faceAttest;
        notifyPropertyChanged(BR.faceAttest);
    }

    @Bindable
    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
        notifyPropertyChanged(BR.distance);
    }

    @Bindable
    public String getSingleImage() {
        return singleImage;
    }

    public void setSingleImage(String singleImage) {
        this.singleImage = singleImage;
        notifyPropertyChanged(BR.singleImage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(userId);
        parcel.writeString(nick);
        parcel.writeString(image);
        parcel.writeString(moreImages);
        parcel.writeString(personalitySign);
        parcel.writeString(shopUrl);
        parcel.writeString(birthday);
        parcel.writeInt(age);
        parcel.writeInt(sex);
        parcel.writeInt(height);
        parcel.writeInt(weight);
        parcel.writeInt(constellation);
        parcel.writeInt(bloodType);
        parcel.writeString(job);
        parcel.writeInt(education);
        parcel.writeLong(provinceId);
        parcel.writeLong(cityId);
        parcel.writeLong(districtId);
        parcel.writeInt(rstatus);
        parcel.writeString(remark);
        parcel.writeInt(attentionQuantity);
        parcel.writeInt(fansQuantity);
        parcel.writeInt(popularity);
        parcel.writeInt(pollQuantity);
        parcel.writeInt(rentQuantity);
        parcel.writeInt(beLikeQuantity);
        parcel.writeInt(memberType);
        parcel.writeString(serviceTags);
        parcel.writeString(newDycCreateTime);
        parcel.writeInt(idAttest);
        parcel.writeInt(faceAttest);
        parcel.writeInt(newDycType);
        parcel.writeInt(attentionStatus);
        parcel.writeString(distance);
        parcel.writeString(singleImage);
    }
}
