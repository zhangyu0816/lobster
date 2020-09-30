package com.zb.lib_base.utils;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;

import com.zb.lib_base.R;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AttentionDb;
import com.zb.lib_base.db.LikeDb;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
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

    public static int getBottleBgHeight(float height) {
        return (int) (height * MineApp.W / 1095f);
    }

    public static int getLogoHeight(float scale) {
        return (int) (getViewSizeByWidth(scale) * 510f / 345f);
    }

    public static int getImageHeight(float scale, int width, int height) {
        return (int) (getViewSizeByWidth(scale) * (float) height / (float) width);
    }

    public static int getVipBgHeight(float scale) {
        return (int) (getViewSizeByWidth(scale) * 149f / 501f);
    }

    public static int getVipIntroHeight() {
        return (int) (MineApp.W * 2196 / 1125f);
    }

    public static int getVipIntroBgHeight(float scale) {
        return (int) (getViewSizeByWidth(scale) * 458f / 1035f);
    }

    public static int getVipExposureHeight(int width) {
        return (int) (getViewSizeByWidthFromMax(width) * 900f / 1035f);
    }

    // 默认图片
    public static int getDefaultRes() {
        return R.mipmap.empty_icon;
    }

    // 超级喜欢
    public static int getSuperLikeRes(boolean isPair) {
        return isPair ? R.mipmap.like_tag_icon : R.mipmap.super_like_small_icon;
    }

    // 客服头像
    public static int getSystemRes() {
        return R.mipmap.system_tag_icon;
    }

    // 动态
    public static int getDiscoverRes() {
        return R.mipmap.discover_icon;
    }

    // 客服头像
    public static int getEmojiDeleteRes() {
        return R.mipmap.emoji_delete;
    }

    // 漂流瓶顶部背景图
    public static int getBottleTopRes() {
        return R.mipmap.bottle_top_icon;
    }

    // 漂流瓶顶部背景图
    public static int getHaidi1Res() {
        return R.mipmap.haidi1;
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


    @SuppressLint("UseCompatLoadingForDrawables")
    public static Drawable getRanking(int position) {
        if (position == 0) {
            return MineApp.getInstance().getResources().getDrawable(R.mipmap.reward_ranking_1);
        } else if (position == 1) {
            return MineApp.getInstance().getResources().getDrawable(R.mipmap.reward_ranking_2);
        } else {
            return MineApp.getInstance().getResources().getDrawable(R.mipmap.reward_ranking_3);
        }
    }

    private static Map<String, Integer> constellationMap = new HashMap<>();

    static {
        constellationMap.put("摩羯座", R.drawable.btn_bg_moxie_radius2);
        constellationMap.put("水瓶座", R.drawable.btn_bg_shuiping_radius2);
        constellationMap.put("双鱼座", R.drawable.btn_bg_shuangyu_radius2);
        constellationMap.put("白羊座", R.drawable.btn_bg_baiyang_radius2);
        constellationMap.put("金牛座", R.drawable.btn_bg_jinniu_radius2);
        constellationMap.put("双子座", R.drawable.btn_bg_shuangzi_radius2);
        constellationMap.put("巨蟹座", R.drawable.btn_bg_juxie_radius2);
        constellationMap.put("狮子座", R.drawable.btn_bg_shizi_radius2);
        constellationMap.put("处女座", R.drawable.btn_bg_chunv_radius2);
        constellationMap.put("天秤座", R.drawable.btn_bg_tianping_radius2);
        constellationMap.put("天蝎座", R.drawable.btn_bg_tianxie_radius2);
        constellationMap.put("射手座", R.drawable.btn_bg_sheshou_radius2);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static Drawable getConstellationBg(String constellation) {
        if (constellation.isEmpty())
            return MineApp.getInstance().getResources().getDrawable(R.mipmap.empty_icon);
        else
            return MineApp.getInstance().getResources().getDrawable(constellationMap.get(constellation));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static Drawable getRealCheck(int isChecked) {
        if (isChecked == -1) {
            return MineApp.getInstance().getResources().getDrawable(R.mipmap.real_un_icon);
        } else if (isChecked == 0) {
            return MineApp.getInstance().getResources().getDrawable(R.mipmap.real_checking_icon);
        } else if (isChecked == 1) {
            return MineApp.getInstance().getResources().getDrawable(R.mipmap.real_icon);
        } else {
            return MineApp.getInstance().getResources().getDrawable(R.mipmap.real_fail_icon);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static Drawable getNoData(int position) {
        if (position == 0) {
            return MineApp.getInstance().getResources().getDrawable(R.mipmap.no_anth_data);
        } else if (position == 1) {
            return MineApp.getInstance().getResources().getDrawable(R.mipmap.no_fan_data);
        } else if (position == 2) {
            return MineApp.getInstance().getResources().getDrawable(R.mipmap.no_belike_data);
        } else {
            return MineApp.getInstance().getResources().getDrawable(R.mipmap.no_visitor_data);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static Drawable getNewsNoData(int reviewType) {
        if (reviewType == 1) {
            return MineApp.getInstance().getResources().getDrawable(R.mipmap.no_review_icon);
        } else if (reviewType == 2) {
            return MineApp.getInstance().getResources().getDrawable(R.mipmap.no_good_icon);
        } else {
            return MineApp.getInstance().getResources().getDrawable(R.mipmap.no_gift_icon);
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
        else if (msgType == 112) {
            JSONObject object = null;
            try {
                object = new JSONObject(stanza);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            assert object != null;
            return object.has("content") ? object.optString("content") : "[你有新的动态消息]";
        } else
            return "[暂不支付该类型消息]";
    }

    public static String getSystemStanza(String stanza, int msgType) {
        //消息类型 1：文字 2：图片 3：语音 4：视频
        if (msgType == 0) {
            return "这是官方消息哦~";
        } else if (msgType == 1)
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
        else if (checkStatus == 2)
            return "继续验证";
        else
            return "立即验证";
    }

    public static int textColor(long otherUserId, int position) {
        if (position == 2) {
            if (LikeDb.getInstance().hasLike(otherUserId)) {
                return MineApp.getInstance().getResources().getColor(R.color.black_827);
            } else {
                return MineApp.getInstance().getResources().getColor(R.color.purple_7a4);
            }
        } else {
            if (AttentionDb.getInstance().isAttention(otherUserId)) {
                return MineApp.getInstance().getResources().getColor(R.color.black_827);
            } else {
                return MineApp.getInstance().getResources().getColor(R.color.purple_7a4);
            }
        }

    }

    public static String textName(long otherUserId, int position) {
        if (position == 2) {
            return LikeDb.getInstance().hasLike(otherUserId) ? "已喜欢" : "喜欢Ta";
        } else if (position == 1) {
            return AttentionDb.getInstance().isAttention(otherUserId) ? "已关注" : "回粉";
        } else {
            return AttentionDb.getInstance().isAttention(otherUserId) ? "已关注" : "关注TA";
        }
    }

    public static String count(int count) {
        if (count < 99)
            return count + "";
        else
            return "···";
    }

    public static String count99(int count) {
        if (count < 99)
            return count + "";
        else
            return "99+";
    }

    public static String count0_99(int count) {
        if (count == 0) {
            return "0";
        } else if (count < 99)
            return count + "";
        else
            return "99+";
    }

    public static String feedbackText(int replyState) {
        if (replyState == 1) {
            return "查看回复";
        } else {
            return "待处理";
        }
    }

    public static String feedbackDetailText(int replyState) {
        if (replyState == 1) {
            return "已回复";
        } else {
            return "待处理";
        }
    }

    public static String idCardText(int position) {
        if (position == 0) {
            return "身份证正面";
        } else if (position == 1) {
            return "身份证反面";
        } else {
            return "手持身份证";
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static Drawable getTag(int position) {
        if (position == 0) {
            return MineApp.getInstance().getResources().getDrawable(R.drawable.btn_bg_red_ffe_radius40);
        } else if (position == 1) {
            return MineApp.getInstance().getResources().getDrawable(R.drawable.btn_bg_yellow_fff_radius40);
        } else if (position == 2) {
            return MineApp.getInstance().getResources().getDrawable(R.drawable.btn_bg_blue_e4f_radius40);
        } else if (position == 3) {
            return MineApp.getInstance().getResources().getDrawable(R.drawable.btn_bg_purple_e8d_radius40);
        } else {
            return MineApp.getInstance().getResources().getDrawable(R.drawable.btn_bg_green_e1f_radius40);
        }
    }

    public static int getTagColor(int position) {
        if (position == 0) {
            return MineApp.getInstance().getResources().getColor(R.color.red_ff3);
        } else if (position == 1) {
            return MineApp.getInstance().getResources().getColor(R.color.yellow_e89);
        } else if (position == 2) {
            return MineApp.getInstance().getResources().getColor(R.color.blue_37a);
        } else if (position == 3) {
            return MineApp.getInstance().getResources().getColor(R.color.purple_7a4);
        } else {
            return MineApp.getInstance().getResources().getColor(R.color.green_34c);
        }
    }
}
