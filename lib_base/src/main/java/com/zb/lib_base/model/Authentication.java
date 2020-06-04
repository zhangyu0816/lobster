package com.zb.lib_base.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class Authentication extends BaseObservable implements Parcelable {
    private long userId;
    private String realName = ""; // 身份证上的真实姓名
    private String identityNum = ""; // 身份证号
    private String personalImage = ""; // 持卡个人照
    private String idFrontImage = ""; // 身份证正面照片
    private String idBackImage = ""; // 身份证背面照片
    private Integer isChecked;// 是否通过审核 100 （未审核,属于机器人未审核）//0未审核(无法提交) //1通过(无法提交) 2未通过(显示不通过原因。备注)
    private String mark = ""; // 备注 ，退回的原因

    public Authentication() {
    }

    protected Authentication(Parcel in) {
        userId = in.readLong();
        realName = in.readString();
        identityNum = in.readString();
        personalImage = in.readString();
        idFrontImage = in.readString();
        idBackImage = in.readString();
        if (in.readByte() == 0) {
            isChecked = null;
        } else {
            isChecked = in.readInt();
        }
        mark = in.readString();
    }

    public static final Creator<Authentication> CREATOR = new Creator<Authentication>() {
        @Override
        public Authentication createFromParcel(Parcel in) {
            return new Authentication(in);
        }

        @Override
        public Authentication[] newArray(int size) {
            return new Authentication[size];
        }
    };

    @Bindable
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
        notifyPropertyChanged(BR.userId);
    }

    @Bindable public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
        notifyPropertyChanged(BR.realName);
    }

    @Bindable public String getIdentityNum() {
        return identityNum;
    }

    public void setIdentityNum(String identityNum) {
        this.identityNum = identityNum;
        notifyPropertyChanged(BR.identityNum);
    }

    @Bindable public String getPersonalImage() {
        return personalImage;
    }

    public void setPersonalImage(String personalImage) {
        this.personalImage = personalImage;
        notifyPropertyChanged(BR.personalImage);
    }

    @Bindable public String getIdFrontImage() {
        return idFrontImage;
    }

    public void setIdFrontImage(String idFrontImage) {
        this.idFrontImage = idFrontImage;
        notifyPropertyChanged(BR.idFrontImage);
    }

    @Bindable public String getIdBackImage() {
        return idBackImage;
    }

    public void setIdBackImage(String idBackImage) {
        this.idBackImage = idBackImage;
        notifyPropertyChanged(BR.idBackImage);
    }

    @Bindable public Integer getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(Integer isChecked) {
        this.isChecked = isChecked;
        notifyPropertyChanged(BR.isChecked);
    }

    @Bindable public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
        notifyPropertyChanged(BR.mark);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(userId);
        dest.writeString(realName);
        dest.writeString(identityNum);
        dest.writeString(personalImage);
        dest.writeString(idFrontImage);
        dest.writeString(idBackImage);
        if (isChecked == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(isChecked);
        }
        dest.writeString(mark);
    }
}
