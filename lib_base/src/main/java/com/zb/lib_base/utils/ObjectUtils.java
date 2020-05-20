package com.zb.lib_base.utils;

import android.graphics.drawable.Drawable;

import com.zb.lib_base.R;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AttentionDb;
import com.zb.lib_base.db.LikeDb;

import java.util.Map;

public class ObjectUtils {

    // 根据屏幕宽获取尺寸
    public static int getViewSizeByWidth(float scale) {
        return (int) (MineApp.W * scale);
    }

    // 根据屏幕高获取尺寸
    public static int getViewSizeByHeight(float scale) {
        return (int) (MineApp.H * scale);
    }

    // 根据屏幕宽获取尺寸
    public static int getViewSizeByWidthFromMax(int width) {
        return (int) (MineApp.W * (float) width / 1080f);
    }

    // 根据屏幕高获取尺寸
    public static int getViewSizeByHeightFromMax(int height) {
        return (int) (MineApp.H * (float) height / 1920f);
    }

    // 根据屏幕宽获取尺寸
    public static int getViewSizeByWidthFromMax750(int width) {
        return (int) (MineApp.W * width / 750f);
    }

    // 根据屏幕高获取尺寸
    public static int getViewSizeByHeightFromMax1334(int width, int height) {
        return (int) (getViewSizeByWidthFromMax750(width) * (float) height / (float) width);
    }

    public static int getLogoHeight(float scale) {
        return (int) (getViewSizeByWidth(scale) * 510f / 345f);
    }

    public static int getLogoWidth(float scale) {
        return (int) (getViewSizeByHeight(scale) * 345f / 510f);
    }

    public static int getVipBgHeight(float scale) {
        return (int) (getViewSizeByWidth(scale) * 254f / 1035f);
    }

    public static int getVipIntroHeight() {
        return (int) (MineApp.W * 2111f / 1125f);
    }

    public static int getVipIntroBgHeight(float scale) {
        return (int) (getViewSizeByWidth(scale) * 458f / 1035f);
    }

    // 默认图片
    public static int getDefaultRes() {
        return R.mipmap.empty_icon;
    }

    // 超级喜欢
    public static int getSuperLikeRes() {
        return R.mipmap.super_like_icon;
    }

    // 线条颜色
    public static int getLineColor() {
        return R.color.black_efe;
    }

    // 获取选中时显示的数量
    public static String getSelectCount(Map map, String s) {
        return map.containsKey(s) ? map.get(s) + "" : "";
    }

    // 最大输入字数
    public static String editMax(String content, int max) {
        return content.length() + "/" + max;
    }

    // 是不是视频
    public static boolean isVideo(String filePath) {
        return filePath.contains(".mp4");
    }


    public static Drawable getRanking(int position) {
        if (position == 0) {
            return MineApp.getInstance().getResources().getDrawable(R.mipmap.reward_ranking_1);
        } else if (position == 1) {
            return MineApp.getInstance().getResources().getDrawable(R.mipmap.reward_ranking_2);
        } else {
            return MineApp.getInstance().getResources().getDrawable(R.mipmap.reward_ranking_3);
        }
    }

    public static Drawable getNoData(int position) {
        if (position == 0) {
            return MineApp.getInstance().getResources().getDrawable(R.mipmap.no_anth_data);
        } else if (position == 1) {
            return MineApp.getInstance().getResources().getDrawable(R.mipmap.no_fan_data);
        } else {
            return MineApp.getInstance().getResources().getDrawable(R.mipmap.no_belike_data);
        }
    }

    public static String getStanza(String stanza, int msgType) {
        //消息类型 1：文字 2：图片 3：语音 4：视频
        if (msgType == 1)
            return stanza;
        else if (msgType == 2)
            return "[图片]";
        else if (msgType == 3)
            return "[语音]";
        else if (msgType == 4)
            return "[视频]";
        else
            return "[暂不支付该类型消息]";
    }

    public static String getCheckResult(int checkStatus) {
        //0未审核(无法提交)  //1通过(无法提交)  2未通过(显示不通过原因。备注)
        if (checkStatus == 0)
            return "审核中";
        else if (checkStatus == 1)
            return "已通过";
        else
            return "立即验证";
    }

    public static int textColor(LikeDb likeDb, AttentionDb attentionDb, long collectId) {
        if (likeDb.hasLike(collectId) || attentionDb.hasAttention(collectId)) {
            return R.color.black_827;
        } else {
            return R.color.purple_7a4;
        }
    }

    public static String textName(LikeDb likeDb, AttentionDb attentionDb, long collectId, int position) {
        if (position == 2) {
            return likeDb.hasLike(collectId) ? "已喜欢" : "喜欢Ta";
        } else if (position == 1) {
            return attentionDb.hasAttention(collectId) ? "取消关注" : "回粉";
        } else {
            return attentionDb.hasAttention(collectId) ? "取消关注" : "关注Ta";
        }
    }

    public static String count(int count) {
        if (count < 99)
            return count + "";
        else
            return "···";
    }
}
