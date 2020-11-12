package com.zb.lib_base.model;

public class WebShare {

    String imgUrl = "";
    String url = "";
    String title = "";
    String shareTitle = "";
    String desc = "";
    int contentType;//1.文字 2.图片 3.图文
    int coorX;
    int coorY;
    int qrCodeW;
    int qrCodeH;
    int shareType;// 1.QQ邀请  2.微信邀请  3.朋友圈邀请  4.微信群邀请  5.海报分享

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public int getCoorX() {
        return coorX;
    }

    public void setCoorX(int coorX) {
        this.coorX = coorX;
    }

    public int getCoorY() {
        return coorY;
    }

    public void setCoorY(int coorY) {
        this.coorY = coorY;
    }

    public int getQrCodeW() {
        return qrCodeW;
    }

    public void setQrCodeW(int qrCodeW) {
        this.qrCodeW = qrCodeW;
    }

    public int getQrCodeH() {
        return qrCodeH;
    }

    public void setQrCodeH(int qrCodeH) {
        this.qrCodeH = qrCodeH;
    }

    public int getShareType() {
        return shareType;
    }

    public void setShareType(int shareType) {
        this.shareType = shareType;
    }
}
