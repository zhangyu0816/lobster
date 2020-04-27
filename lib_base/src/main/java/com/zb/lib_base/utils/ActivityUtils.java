package com.zb.lib_base.utils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.zb.lib_base.model.MemberInfo;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityUtils {
    /*********************** 首页 **************************/
    // 图文动态
    public static void getHomePublishImage() {
        ARouter.getInstance().build(RouteUtils.Home_Publish_image).navigation();
    }

    // 动态详情
    public static void getHomeDiscoverDetail(long friendDynId) {
        ARouter.getInstance().build(RouteUtils.Home_Discover_Detail).withLong("friendDynId", friendDynId).navigation();
    }

    // 礼物列表
    public static void getHomeRewardList(long friendDynId) {
        ARouter.getInstance().build(RouteUtils.Home_Reward_List).withLong("friendDynId", friendDynId).navigation();
    }

    /*********************** 卡片 **************************/
    // 用户详情
    public static void getCardMemberDetail(long userId) {
        ARouter.getInstance().build(RouteUtils.Card_Member_Detail).withLong("userId", userId).navigation();
    }
    /*********************** 对话 **************************/
    /*********************** 我的 **************************/
    // 会员
    public static void getMineOpenVip(MemberInfo memberInfo) {
        ARouter.getInstance().build(RouteUtils.Mine_Open_Vip).withParcelable("memberInfo", memberInfo).navigation();
    }

    // 消息
    public static void getMineNewsManager() {
        ARouter.getInstance().build(RouteUtils.Mine_News_Manager).navigation();
    }

    // 消息
    public static void getMineGiftNews() {
        ARouter.getInstance().build(RouteUtils.Mine_Gift_News).navigation();
    }

    // 设置
    public static void getMineSetting() {
        ARouter.getInstance().build(RouteUtils.Mine_Setting).navigation();
    }

    // 实名认证
    public static void getMineRealName() {
        ARouter.getInstance().build(RouteUtils.Mine_Real_Name).navigation();
    }

    // 我的钱包
    public static void getMineWallet() {
        ARouter.getInstance().build(RouteUtils.Mine_Wallet).navigation();
    }

    // 定位
    public static void getMineLocation() {
        ARouter.getInstance().build(RouteUtils.Mine_Location).navigation();
    }

    // 修改密码
    public static void getMineModifyPass() {
        ARouter.getInstance().build(RouteUtils.Mine_Modify_Pass).navigation();
    }

    // 通知
    public static void getMineNotice() {
        ARouter.getInstance().build(RouteUtils.Mine_Notice).navigation();
    }

    // 反馈
    public static void getMineFeedback() {
        ARouter.getInstance().build(RouteUtils.Mine_Feedback).navigation();
    }

    // 网页
    public static void getMineWeb(String title, String url) {
        ARouter.getInstance().build(RouteUtils.Mine_Web).withString("title", title).withString("url", url).navigation();
    }

    // 礼物收益
    public static void getMineGiftRecord() {
        ARouter.getInstance().build(RouteUtils.Mine_Gift_Record).navigation();
    }


    /*********************** 注册 **************************/

    // 注册主页
    public static void getRegisterMain() {
        ARouter.getInstance().build(RouteUtils.Register_Main).navigation();
    }

    // 注册--登录
    public static void getRegisterLogin() {
        ARouter.getInstance().build(RouteUtils.Register_Login).navigation();
    }

    // 注册--昵称
    public static void getRegisterNick() {
        ARouter.getInstance().build(RouteUtils.Register_Nick).navigation();
    }

    // 注册--生日
    public static void getRegisterBirthday() {
        ARouter.getInstance().build(RouteUtils.Register_Birthday).navigation();
    }

    // 注册--手机号
    public static void getRegisterPhone(boolean isLogin) {
        ARouter.getInstance().build(RouteUtils.Register_Phone).withBoolean("isLogin", isLogin).navigation();
    }

    // 注册--验证码
    public static void getRegisterCode(boolean isLogin) {
        ARouter.getInstance().build(RouteUtils.Register_Code).withBoolean("isLogin", isLogin).navigation();
    }

    // 注册--头像
    public static void getRegisterLogo() {
        ARouter.getInstance().build(RouteUtils.Register_Logo).navigation();
    }

    // 注册--多图
    public static void getRegisterImages() {
        ARouter.getInstance().build(RouteUtils.Register_Images).navigation();
    }

    /*********************** 相册 **************************/

    // 相册主页
    public static void getCameraMain(AppCompatActivity activity, boolean isMore) {
        ARouter.getInstance().build(RouteUtils.Camera_Main).withBoolean("isMore", isMore).navigation(activity, 1001);
    }

    // 拍视频
    public static void getCameraVideo() {
        ARouter.getInstance().build(RouteUtils.Camera_Video).navigation();
    }

    // 拍照
    public static void getCameraPhoto() {
        ARouter.getInstance().build(RouteUtils.Camera_Photo).navigation();
    }

    // 选择视频
    public static void getCameraVideos() {
        ARouter.getInstance().build(RouteUtils.Camera_Videos).navigation();
    }

    // 视频播放
    public static void getCameraVideoPlay(String filePath) {
        ARouter.getInstance().build(RouteUtils.Camera_Video_Play).withString("filePath", filePath).navigation();
    }

    /*********************** 漂流瓶 **************************/

    // 漂流瓶主页
    public static void getBottleMain() {
        ARouter.getInstance().build(RouteUtils.Bottle_Main).navigation();
    }

    // 漂流瓶主页
    public static void getBottleThrow() {
        ARouter.getInstance().build(RouteUtils.Bottle_Throw).navigation();
    }

    // 漂流瓶主页
    public static void getBottleList() {
        ARouter.getInstance().build(RouteUtils.Bottle_List).navigation();
    }
}
