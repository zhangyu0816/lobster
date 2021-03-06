package com.zb.lib_base.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class
BottleInfo extends BaseObservable implements Parcelable {
    long driftBottleId;//漂流瓶id
    long userId;
    String text = ""; //内容
    int driftBottleType; //漂流瓶状态 .1.漂流中  2.被拾起  3.销毁
    int noReadNum;//未读数量
    String otherHeadImage = "";//头像
    String otherNick = "";  //昵称
    long otherUserId; //
    String modifyTime = "";
    String createTime = "";
    int destroyType;  // 0未销毁  1 单方销毁 所属人  2 单方销毁 拾起人  3 双方销毁

    public BottleInfo() {
    }

    protected BottleInfo(Parcel in) {
        driftBottleId = in.readLong();
        userId = in.readLong();
        text = in.readString();
        driftBottleType = in.readInt();
        noReadNum = in.readInt();
        otherHeadImage = in.readString();
        otherNick = in.readString();
        otherUserId = in.readLong();
        modifyTime = in.readString();
        createTime = in.readString();
        destroyType = in.readInt();
    }

    public static final Creator<BottleInfo> CREATOR = new Creator<BottleInfo>() {
        @Override
        public BottleInfo createFromParcel(Parcel in) {
            return new BottleInfo(in);
        }

        @Override
        public BottleInfo[] newArray(int size) {
            return new BottleInfo[size];
        }
    };

    @Bindable
    public long getDriftBottleId() {
        return driftBottleId;
    }

    public void setDriftBottleId(long driftBottleId) {
        this.driftBottleId = driftBottleId;
        notifyPropertyChanged(BR.driftBottleId);
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
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        notifyPropertyChanged(BR.text);
    }

    @Bindable
    public int getDriftBottleType() {
        return driftBottleType;
    }

    public void setDriftBottleType(int driftBottleType) {
        this.driftBottleType = driftBottleType;
        notifyPropertyChanged(BR.driftBottleType);
    }

    @Bindable
    public long getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
        notifyPropertyChanged(BR.otherUserId);
    }

    @Bindable
    public int getNoReadNum() {
        return noReadNum;
    }

    public void setNoReadNum(int noReadNum) {
        this.noReadNum = noReadNum;
        notifyPropertyChanged(BR.noReadNum);
    }

    @Bindable
    public String getOtherHeadImage() {
        return otherHeadImage;
    }

    public void setOtherHeadImage(String otherHeadImage) {
        this.otherHeadImage = otherHeadImage;
        notifyPropertyChanged(BR.otherHeadImage);
    }

    @Bindable
    public String getOtherNick() {
        return otherNick;
    }

    public void setOtherNick(String otherNick) {
        this.otherNick = otherNick;
        notifyPropertyChanged(BR.otherNick);
    }

    @Bindable
    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
        notifyPropertyChanged(BR.modifyTime);
    }

    @Bindable
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
        notifyPropertyChanged(BR.createTime);
    }

    @Bindable
    public int getDestroyType() {
        return destroyType;
    }

    public void setDestroyType(int destroyType) {
        this.destroyType = destroyType;
        notifyPropertyChanged(BR.destroyType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(driftBottleId);
        parcel.writeLong(userId);
        parcel.writeString(text);
        parcel.writeInt(driftBottleType);
        parcel.writeInt(noReadNum);
        parcel.writeString(otherHeadImage);
        parcel.writeString(otherNick);
        parcel.writeLong(otherUserId);
        parcel.writeString(modifyTime);
        parcel.writeString(createTime);
        parcel.writeInt(destroyType);
    }
}
