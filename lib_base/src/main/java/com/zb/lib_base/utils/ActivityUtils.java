package com.zb.lib_base.utils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.model.Authentication;
import com.zb.lib_base.model.FeedbackInfo;
import com.zb.lib_base.model.MemberInfo;

public class ActivityUtils {
    /*********************** app **************************/
    // 软件主页
    public static RxAppCompatActivity getMainActivity() {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Main_MainActivity).navigation();
        return activity;
    }

    // 注册登录
    public static RxAppCompatActivity getLoginActivity(int loginStep) {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Main_Login).withInt("loginStep", loginStep).navigation();
        return activity;
    }

    // 绑定手机号
    public static void getBindingPhoneActivity(RxAppCompatActivity activity) {
        ARouter.getInstance().build(RouteUtils.Main_Binding_Phone).navigation(activity, 1002);
    }

    /*********************** 首页 **************************/
    // 图文动态
    public static RxAppCompatActivity getHomePublishImage() {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Home_Publish_image).navigation();
        return activity;
    }

    // 动态详情
    public static RxAppCompatActivity getHomeDiscoverDetail(long friendDynId) {//253510
        MineApp.removeActivity(MineApp.activityMap.get("DiscoverDetailActivity"));
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Home_Discover_Detail).withLong("friendDynId", friendDynId).navigation();
        return activity;
    }

    // 礼物列表
    public static RxAppCompatActivity getHomeRewardList(long friendDynId) {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Home_Reward_List).withLong("friendDynId", friendDynId).navigation();
        return activity;
    }

    // 礼物列表
    public static RxAppCompatActivity getHomeReport(long otherUserId) {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Home_Report).withLong("otherUserId", otherUserId).navigation();
        return activity;
    }

    // 礼物列表
    public static RxAppCompatActivity getHomeDiscoverVideoL2(long friendDynId) {
        MineApp.removeActivity(MineApp.activityMap.get("DiscoverVideoL2Activity"));
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Home_Discover_Video_L2).withLong("friendDynId", friendDynId).navigation();
        return activity;
    }

    // 礼物列表
    public static RxAppCompatActivity getHomeSearch() {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Home_Search).navigation();
        return activity;
    }

    // 礼物列表
    public static RxAppCompatActivity getHomeVideoList(int position, int pageNo) {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Home_Video_List).withInt("position", position).withInt("pageNo", pageNo).navigation();
        return activity;
    }

    /*********************** 卡片 **************************/
    // 用户详情
    public static RxAppCompatActivity getCardMemberDetail(long userId, boolean showLike) {//2321942
        MineApp.removeActivity(MineApp.activityMap.get("MemberDetailActivity"));
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Card_Member_Detail).withLong("userId", userId).withBoolean("showLike", showLike).navigation();
        return activity;
    }

    // 用户详情
    public static RxAppCompatActivity getCardDiscoverList(long userId, boolean isAttention, MemberInfo memberInfo) {
        MineApp.removeActivity(MineApp.activityMap.get("DiscoverListActivity"));
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Card_Discover_List).withLong("userId", userId).withBoolean("isAttention", isAttention).withParcelable("memberInfo", memberInfo).navigation();
        return activity;
    }

    /*********************** 对话 **************************/
    // 对话页
    public static RxAppCompatActivity getChatActivity(long otherUserId) {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Chat_Activity).withLong("otherUserId", otherUserId).navigation();
        return activity;
    }

    /*********************** 我的 **************************/
    // 会员
    public static RxAppCompatActivity getMineOpenVip() {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Mine_Open_Vip).navigation();
        return activity;
    }

    // 消息
    public static RxAppCompatActivity getMineNewsManager() {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Mine_News_Manager).navigation();
        return activity;
    }

    // 消息
    public static RxAppCompatActivity getMineNewsList(int reviewType) {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Mine_News_list).withInt("reviewType", reviewType).navigation();
        return activity;
    }

    // 设置
    public static RxAppCompatActivity getMineSetting() {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Mine_Setting).navigation();
        return activity;
    }

    // 实名认证
    public static RxAppCompatActivity getMineRealName() {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Mine_Real_Name).navigation();
        return activity;
    }

    // 我的钱包
    public static RxAppCompatActivity getMineWallet() {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Mine_Wallet).navigation();
        return activity;
    }

    // 定位
    public static RxAppCompatActivity getMineLocation(boolean isDiscover) {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Mine_Location).withBoolean("isDiscover", isDiscover).navigation();
        return activity;
    }

    // 修改密码
    public static RxAppCompatActivity getMineModifyPass() {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Mine_Modify_Pass).navigation();
        return activity;
    }

    // 通知
    public static RxAppCompatActivity getMineNotice() {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Mine_Notice).navigation();
        return activity;
    }

    // 反馈
    public static RxAppCompatActivity getMineFeedback() {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Mine_Feedback).navigation();
        return activity;
    }

    // 反馈
    public static RxAppCompatActivity getMineFeedbackDetail(FeedbackInfo feedbackInfo) {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Mine_Feedback_Detail).withParcelable("feedbackInfo", feedbackInfo).navigation();
        return activity;
    }

    // 反馈
    public static RxAppCompatActivity getMineAddFeedback(FeedbackInfo feedbackInfo) {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Mine_Add_Feedback).withParcelable("feedbackInfo", feedbackInfo).navigation();
        return activity;
    }

    // 网页
    public static RxAppCompatActivity getMineWeb(String title, String url) {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Mine_Web).withString("title", title).withString("url", url).navigation();
        return activity;
    }

    // 礼物收益
    public static RxAppCompatActivity getMineGiftRecord() {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Mine_Gift_Record).navigation();
        return activity;
    }

    // 编辑个人信息
    public static RxAppCompatActivity getMineEditMember() {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Mine_Edit_Member).navigation();
        return activity;
    }

    // 选择工作
    public static RxAppCompatActivity getMineSelectJob(String job) {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Mine_Select_Job).withString("job", job).navigation();
        return activity;
    }

    // 编辑信息
    public static RxAppCompatActivity getMineEditContent(int type, int lines, String title, String content, String hint) {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Mine_Edit_Content).withInt("type", type).withInt("lines", lines).withString("title", title).withString("content", content).withString("hint", hint).navigation();
        return activity;
    }

    // 选择标签
    public static RxAppCompatActivity getMineSelectTag(String serviceTags) {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Mine_Select_Tag).withString("serviceTags", serviceTags).navigation();
        return activity;
    }

    // 粉丝、关注、被喜欢列表
    public static RxAppCompatActivity getMineFCL(int position) {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Mine_FCL).withInt("position", position).navigation();
        return activity;
    }

    // 收发礼物记录
    public static RxAppCompatActivity getMineGRGift(int friendDynGiftType) {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Mine_GRGift).withInt("friendDynGiftType", friendDynGiftType).navigation();
        return activity;
    }

    // 交易记录
    public static RxAppCompatActivity getMineTranRecord(int tranType) {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Mine_Tran_Record).withInt("tranType", tranType).navigation();
        return activity;
    }


    // 绑定银行卡
    public static RxAppCompatActivity getMineBindingBank() {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Mine_Binding_Bank).navigation();
        return activity;
    }

    // 银行卡列表
    public static RxAppCompatActivity getMineBankList(boolean isSelect) {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Mine_Bank_List).withBoolean("isSelect", isSelect).navigation();
        return activity;
    }

    // 系统消息
    public static RxAppCompatActivity getMineSystemMsg() {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Mine_System_Msg).navigation();
        return activity;
    }

    // 提现
    public static RxAppCompatActivity getMineWithdraw() {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Mine_Withdraw).navigation();
        return activity;
    }

    // 实名认证
    public static RxAppCompatActivity getMineAuthentication(Authentication authentication) {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Mine_Authentication).withParcelable("authentication", authentication).navigation();
        return activity;
    }

    /*********************** 注册 **************************/

//    // 注册主页
//    public static RxAppCompatActivity getRegisterMain() {
//        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Register_Main).navigation();
//        return activity;
//    }
//
//    // 注册--登录
//    public static RxAppCompatActivity getRegisterLogin() {
//        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Register_Login).navigation();
//        return activity;
//    }
//
//    // 注册--昵称
//    public static RxAppCompatActivity getRegisterNick() {
//        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Register_Nick).navigation();
//        return activity;
//    }
//
//    // 注册--生日
//    public static RxAppCompatActivity getRegisterBirthday() {
//        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Register_Birthday).navigation();
//        return activity;
//    }
//
//    // 注册--手机号
//    public static RxAppCompatActivity getRegisterPhone(boolean isLogin) {
//        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Register_Phone).withBoolean("isLogin", isLogin).navigation();
//        return activity;
//    }
//
//    // 注册--验证码
//    public static RxAppCompatActivity getRegisterCode(boolean isLogin) {
//        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Register_Code).withBoolean("isLogin", isLogin).navigation();
//        return activity;
//    }
//
//    // 注册--头像
//    public static RxAppCompatActivity getRegisterLogo() {
//        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Register_Logo).navigation();
//        return activity;
//    }
//
//    // 注册--多图
//    public static RxAppCompatActivity getRegisterImages() {
//        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Register_Images).navigation();
//        return activity;
//    }
//
//    // 相册主页
//    public static void getBindingPhone(RxAppCompatActivity activity) {
//        ARouter.getInstance().build(RouteUtils.Register_Binding_Phone).navigation(activity, 1002);
//    }

    /*********************** 相册 **************************/

    // 相册主页
    public static void getCameraMain(RxAppCompatActivity activity, boolean isMore, boolean showBottom, boolean showVideo) {
        ARouter.getInstance().build(RouteUtils.Camera_Main).withBoolean("isMore", isMore).withBoolean("showBottom", showBottom).withBoolean("showVideo", showVideo).navigation(activity, 1001);
    }

    // 拍视频
    public static RxAppCompatActivity getCameraVideo(boolean showBottom) {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Camera_Video).withBoolean("showBottom", showBottom).navigation();
        return activity;
    }

    // 拍照
    public static RxAppCompatActivity getCameraPhoto(boolean isMore, boolean showBottom, boolean showVideo) {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Camera_Photo).withBoolean("isMore", isMore).withBoolean("showBottom", showBottom).withBoolean("showVideo", showVideo).navigation();
        return activity;
    }

    // 选择视频
    public static RxAppCompatActivity getCameraVideos(boolean showBottom) {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Camera_Videos).withBoolean("showBottom", showBottom).navigation();
        return activity;
    }


    // 视频播放
    public static RxAppCompatActivity getCameraVideoPlay(String filePath, boolean isUpload, boolean isDelete) {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Camera_Video_Play).withString("filePath", filePath).withBoolean("isUpload", isUpload).withBoolean("isDelete", isDelete).navigation();
        return activity;
    }

    /*********************** 漂流瓶 **************************/

    // 漂流瓶主页
    public static RxAppCompatActivity getBottleThrow() {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Bottle_Throw).navigation();
        return activity;
    }

    // 漂流瓶主页
    public static RxAppCompatActivity getBottleList() {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Bottle_List).navigation();
        return activity;
    }

    // 漂流瓶主页
    public static RxAppCompatActivity getBottleChat(long driftBottleId) {
        RxAppCompatActivity activity = (RxAppCompatActivity) ARouter.getInstance().build(RouteUtils.Bottle_Chat).withLong("driftBottleId", driftBottleId).navigation();
        return activity;
    }
}
