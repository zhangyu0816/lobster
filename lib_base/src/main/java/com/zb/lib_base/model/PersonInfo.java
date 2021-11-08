package com.zb.lib_base.model;

public class PersonInfo {
    String wxNum;
    double totalRewards = 0.0;
    int isBindWxMiniAppAqxg;

    public String getWxNum() {
        return wxNum;
    }

    public void setWxNum(String wxNum) {
        this.wxNum = wxNum;
    }

    public double getTotalRewards() {
        return totalRewards;
    }

    public void setTotalRewards(double totalRewards) {
        this.totalRewards = totalRewards;
    }

    public int getIsBindWxMiniAppAqxg() {
        return isBindWxMiniAppAqxg;
    }

    public void setIsBindWxMiniAppAqxg(int isBindWxMiniAppAqxg) {
        this.isBindWxMiniAppAqxg = isBindWxMiniAppAqxg;
    }
}
