package com.zb.lib_base.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class FeedbackInfo extends BaseObservable implements Parcelable {
    private long id;
    private long userId; // 用户id
    private String title = ""; // 标题
    private String content = ""; // 反馈内容
    private String createTime = ""; // 提交时间
    private int replyState; // 回复状态 1已处理 0未处理
    private String replyContent = ""; // 回复内容
    private String replyTime = ""; // 回复时间
    private int isDelete; // 删除状态（0：正常 1：删除）
    private String images = "";//图片
    private int imageSize; //图片数量

    public FeedbackInfo() {
    }

    public FeedbackInfo(Parcel in) {
        id = in.readLong();
        userId = in.readLong();
        title = in.readString();
        content = in.readString();
        createTime = in.readString();
        replyState = in.readInt();
        replyContent = in.readString();
        replyTime = in.readString();
        isDelete = in.readInt();
        images = in.readString();
        imageSize = in.readInt();
    }

    public static final Creator<FeedbackInfo> CREATOR = new Creator<FeedbackInfo>() {
        @Override
        public FeedbackInfo createFromParcel(Parcel in) {
            return new FeedbackInfo(in);
        }

        @Override
        public FeedbackInfo[] newArray(int size) {
            return new FeedbackInfo[size];
        }
    };

    @Bindable
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
        notifyPropertyChanged(BR.userId);
    }

    @Bindable public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    @Bindable public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        notifyPropertyChanged(BR.content);
    }

    @Bindable public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
        notifyPropertyChanged(BR.createTime);
    }

    @Bindable public int getReplyState() {
        return replyState;
    }

    public void setReplyState(int replyState) {
        this.replyState = replyState;
        notifyPropertyChanged(BR.replyState);
    }

    @Bindable public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
        notifyPropertyChanged(BR.replyContent);
    }

    @Bindable public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
        notifyPropertyChanged(BR.replyTime);
    }

    @Bindable public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
        notifyPropertyChanged(BR.isDelete);
    }

    @Bindable public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
        notifyPropertyChanged(BR.images);
    }

    @Bindable public int getImageSize() {
        return imageSize;
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
        notifyPropertyChanged(BR.imageSize);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(userId);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(createTime);
        dest.writeInt(replyState);
        dest.writeString(replyContent);
        dest.writeString(replyTime);
        dest.writeInt(isDelete);
        dest.writeString(images);
        dest.writeInt(imageSize);
    }
}
