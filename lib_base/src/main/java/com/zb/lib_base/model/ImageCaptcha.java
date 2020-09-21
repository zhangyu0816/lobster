package com.zb.lib_base.model;

import androidx.annotation.NonNull;

/**
 * Created by DIY on 2018-02-06.
 */

public class ImageCaptcha {
    private String imageCaptchaToken = "";//图片验证令牌
    private String imageCaptchaUrl = "";  //验证码图片

    @NonNull
    @Override
    public String toString() {
        return "ImageCaptcha{" +
                "imageCaptchaToken='" + imageCaptchaToken + '\'' +
                ", imageCaptchaUrl='" + imageCaptchaUrl + '\'' +
                '}';
    }

    public String getImageCaptchaToken() {
        return imageCaptchaToken;
    }

    public void setImageCaptchaToken(String imageCaptchaToken) {
        this.imageCaptchaToken = imageCaptchaToken;
    }

    public String getImageCaptchaUrl() {
        return imageCaptchaUrl;
    }

    public void setImageCaptchaUrl(String imageCaptchaUrl) {
        this.imageCaptchaUrl = imageCaptchaUrl;
    }
}
