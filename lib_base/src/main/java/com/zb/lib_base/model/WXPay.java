package com.zb.lib_base.model;

import androidx.annotation.NonNull;

/**
 * Created by DIY on 2017-10-24.
 */

public class WXPay {

    private String timestamp = ""; // 时间戳，为1970年1月1日00:00到请求发起时间的秒数
    private String sign = "";
    private String partnerid = "";
    private String noncestr = ""; // 32位内的随机串，防重发
    private String appid = ""; // 应用唯一标识，在微信开放平台提交应用审核通过后获得
    private String prepayid = "";

    @NonNull
    @Override
    public String toString() {
        return "WXPay{" +
                "appid='" + appid + '\'' +
                ", noncestr='" + noncestr + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", partnerid='" + partnerid + '\'' +
                ", sign='" + sign + '\'' +
                ", prepayid='" + prepayid + '\'' +
                '}';
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }
}
