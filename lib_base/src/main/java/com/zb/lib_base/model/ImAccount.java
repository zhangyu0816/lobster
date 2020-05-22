package com.zb.lib_base.model;

public class ImAccount {
    private long userId;
    private int imPlatformType; // 1.zuwoIM 2.阿里(openIM)
    private String imUserId = ""; // im用户名
    private String imPassWord = "";// im密码

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getImPlatformType() {
        return imPlatformType;
    }

    public void setImPlatformType(int imPlatformType) {
        this.imPlatformType = imPlatformType;
    }

    public String getImUserId() {
        return imUserId;
    }

    public void setImUserId(String imUserId) {
        this.imUserId = imUserId;
    }

    public String getImPassWord() {
        return imPassWord;
    }

    public void setImPassWord(String imPassWord) {
        this.imPassWord = imPassWord;
    }
}
