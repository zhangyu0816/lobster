package com.zb.lib_base.utils;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.CustomProgressDialog;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ThreeLogin {
    private RxAppCompatActivity activity;
    private String openId;
    private String unionId;
    private String unionNick;
    private String unionImage;
    private int unionSex;
    private int unionType;
    private UMShareAPI umShareAPI;
    private CallBack callBack;

    public interface CallBack {
        void success();
    }

    public ThreeLogin(RxAppCompatActivity activity, CallBack callBack) {
        this.activity = activity;
        this.callBack = callBack;
        umShareAPI = UMShareAPI.get(activity);
        UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(true);
        umShareAPI.setShareConfig(config);
    }

    public void selectUnionType(int unionType) {
        this.unionType = unionType;
        if (unionType == 1) { // 微信登录
            CustomProgressDialog.showLoading(activity, "正在微信登录申请权限");
            umShareAPI.getPlatformInfo(activity, SHARE_MEDIA.WEIXIN, umAuthListener);
        } else { // QQ登录
            CustomProgressDialog.showLoading(activity, "正在QQ登录申请权限");
            umShareAPI.getPlatformInfo(activity, SHARE_MEDIA.QQ, umAuthListener);
        }
    }

    private UMAuthListener umAuthListener = new UMAuthListener() {

        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int status, Map<String, String> info) {
            CustomProgressDialog.showLoading(activity, "授权成功，正在登录中...");
            if (status == 2) {
                unionNick = "";
                unionImage = "";
                unionSex = 0;
                openId = "";
                unionId = "";
                if ("QQ".equals(platform.name())) {
                    unionNick = stringFilter(info.get("screen_name"));
                    unionImage = info.get("profile_image_url");
                    String sexName = info.get("gender");
                    if ("女".equals(sexName)) {
                        unionSex = 0;
                    } else {
                        unionSex = 1;
                    }
                    openId = info.get("openid");
                    unionId = "";
                } else if ("WEIXIN".equals(platform.name())) {
                    unionNick = stringFilter(info.get("screen_name"));
                    unionImage = info.get("profile_image_url");
                    String sexName = info.get("gender");
                    if ("女".equals(sexName)) {
                        unionSex = 0;
                    } else {
                        unionSex = 1;
                    }
                    openId = info.get("openid");
                    unionId = info.get("unionid");
                }

                try {
                    MineApp.registerInfo.setOpenId(openId);
                    MineApp.registerInfo.setUnionId(unionId);
                    MineApp.registerInfo.setUnionType(unionType);
                    MineApp.registerInfo.setName(unionNick);
                    MineApp.registerInfo.setSex(unionSex);
                    MineApp.registerInfo.setUnionImage(unionImage);
                    MineApp.registerInfo.setPhone("");
                    callBack.success();
                } catch (Exception e) {
                    CustomProgressDialog.stopLoading();
                    SCToastUtil.showToast(activity, "获取用户信息失败", true);
                }
            } else {
                CustomProgressDialog.stopLoading();
                SCToastUtil.showToast(activity, "获取用户信息失败", true);
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            CustomProgressDialog.stopLoading();
            SCToastUtil.showToast(activity, "获取用户信息失败", true);
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            CustomProgressDialog.stopLoading();
        }
    };

    private String stringFilter(String str) throws PatternSyntaxException {
        String regEx = ".*\\p{So}.*";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("");
    }
}
