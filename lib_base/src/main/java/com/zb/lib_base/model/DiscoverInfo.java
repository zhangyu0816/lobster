package com.zb.lib_base.model;

import com.zb.lib_base.BR;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class DiscoverInfo extends BaseObservable {
    long discoverId = 0;
    String image = "";
    String content = "";
    String logo = "";
    int likeNum = 0;

    @Bindable
    public long getDiscoverId() {
        return discoverId;
    }

    public void setDiscoverId(long discoverId) {
        this.discoverId = discoverId;
        notifyPropertyChanged(BR.discoverId);
    }

    @Bindable public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
        notifyPropertyChanged(BR.image);
    }

    @Bindable public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
        notifyPropertyChanged(BR.content);
    }

    @Bindable public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
        notifyPropertyChanged(BR.logo);
    }

    @Bindable public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
        notifyPropertyChanged(BR.likeNum);
    }
}
