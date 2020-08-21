package com.app.abby.xbanner;

import android.view.View;

/**
 * Created by DIY on 2017-10-11.
 */

public class Ads {

    private Long adId;         //id
    private String smallImage = ""; //小图地址  //app的图片
    private String bigImage = "";  //大图地址
    private String webImage = "";  //网页端图片
    private String outLink = "";    //广告链接的地址
    private String adTitle = "";   //广告标题
    private int adRes = 0;
    private View view;

    @Override
    public String toString() {
        return "Ads{" +
                "adId=" + adId +
                ", smallImage='" + smallImage + '\'' +
                ", bigImage='" + bigImage + '\'' +
                ", webImage='" + webImage + '\'' +
                ", outLink='" + outLink + '\'' +
                ", adTitle='" + adTitle + '\'' +
                '}';
    }

    public Ads() {
    }

    public Ads(int adRes) {
        this.adRes = adRes;
    }

    public Ads(View view) {
        this.view = view;
    }

    public Ads(Long adId, String smallImage, String bigImage, String webImage, String outLink, String adTitle) {
        this.adId = adId;
        this.smallImage = smallImage;
        this.bigImage = bigImage;
        this.webImage = webImage;
        this.outLink = outLink;
        this.adTitle = adTitle;
    }

    public Long getAdId() {
        return adId;
    }

    public void setAdId(Long adId) {
        this.adId = adId;
    }

    public String getSmallImage() {
        return smallImage;
    }

    public void setSmallImage(String smallImage) {
        this.smallImage = smallImage;
    }

    public String getBigImage() {
        return bigImage;
    }

    public void setBigImage(String bigImage) {
        this.bigImage = bigImage;
    }

    public String getWebImage() {
        return webImage;
    }

    public void setWebImage(String webImage) {
        this.webImage = webImage;
    }

    public String getOutLink() {
        return outLink;
    }

    public void setOutLink(String outLink) {
        this.outLink = outLink;
    }

    public String getAdTitle() {
        return adTitle;
    }

    public void setAdTitle(String adTitle) {
        this.adTitle = adTitle;
    }

    public int getAdRes() {
        return adRes;
    }

    public void setAdRes(int adRes) {
        this.adRes = adRes;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }
}
