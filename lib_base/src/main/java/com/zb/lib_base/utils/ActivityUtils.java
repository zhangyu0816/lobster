package com.zb.lib_base.utils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.model.Authentication;
import com.zb.lib_base.model.FeedbackInfo;
import com.zb.lib_base.model.Film;
import com.zb.lib_base.model.MemberInfo;

public class ActivityUtils {
    /*********************** app **************************/
    // 软件主页
    public static void getMainActivity() {
        ARouter.getInstance().build(RouteUtils.Main_MainActivity).navigation();
    }

    // 注册登录
    public static void getLoginActivity(int loginStep) {
        ARouter.getInstance().build(RouteUtils.Main_Login).withInt("loginStep", loginStep).navigation();
    }

    // 登录视频
    public static void getLoginVideoActivity() {
        ARouter.getInstance().build(RouteUtils.Main_Login_Video).navigation();
    }

    /*********************** 首页 **************************/
    // 图文动态
    public static void getHomePublishImage() {
        ARouter.getInstance().build(RouteUtils.Home_Publish_image).navigation();
    }

    // 动态详情
    public static void getHomeDiscoverDetail(long friendDynId) {//253510
        MineApp.getApp().removeActivity(MineApp.getApp().getActivityMap().get("DiscoverDetailActivity"));
        ARouter.getInstance().build(RouteUtils.Home_Discover_Detail).withLong("friendDynId", friendDynId).navigation();
    }

    // 礼物列表
    public static void getHomeRewardList(long friendDynId, long otherUserId) {
        ARouter.getInstance().build(RouteUtils.Home_Reward_List).withLong("friendDynId", friendDynId).withLong("otherUserId", otherUserId).navigation();
    }

    // 礼物列表
    public static void getHomeReport(long otherUserId) {
        ARouter.getInstance().build(RouteUtils.Home_Report).withLong("otherUserId", otherUserId).navigation();
    }

    // 礼物列表
    public static void getHomeDiscoverVideo(long friendDynId) {
        MineApp.getApp().removeActivity(MineApp.getApp().getActivityMap().get("DiscoverVideoActivity"));
        ARouter.getInstance().build(RouteUtils.Home_Discover_Video).withLong("friendDynId", friendDynId).navigation();
    }

    // 礼物列表
    public static void getHomeSearch() {
        ARouter.getInstance().build(RouteUtils.Home_Search).navigation();
    }

    // 礼物列表
    public static void getHomeVideoList(int position, int pageNo) {
        ARouter.getInstance().build(RouteUtils.Home_Video_List).withInt("position", position).withInt("pageNo", pageNo).navigation();
    }

    /*********************** 卡片 **************************/
    // 用户详情
    public static void getCardMemberDetail(long userId, boolean showLike) {//2321942
        MineApp.getApp().removeActivity(MineApp.getApp().getActivityMap().get("MemberDetailActivity"));
        ARouter.getInstance().build(RouteUtils.Card_Member_Detail).withLong("userId", userId).withBoolean("showLike", showLike).navigation();
    }

    // 用户详情
    public static void getCardDiscoverList(long userId, boolean isAttention, MemberInfo memberInfo) {
        MineApp.getApp().removeActivity(MineApp.getApp().getActivityMap().get("DiscoverListActivity"));
        ARouter.getInstance().build(RouteUtils.Card_Discover_List).withLong("userId", userId).withBoolean("isAttention", isAttention).withParcelable("memberInfo", memberInfo).navigation();
    }

    /*********************** 对话 **************************/
    // 对话页
    public static void getChatActivity(long otherUserId, boolean isNotice) {
        ARouter.getInstance().build(RouteUtils.Chat_Activity).withLong("otherUserId", otherUserId).withBoolean("isNotice", isNotice).navigation();
    }

    // 对话页
    public static void getFlashChatActivity(long otherUserId, long flashTalkId, boolean isNotice) {
        ARouter.getInstance().build(RouteUtils.Flash_Chat_Activity).withLong("otherUserId", otherUserId).withLong("flashTalkId", flashTalkId).withBoolean("isNotice", isNotice).navigation();
    }

    /*********************** 我的 **************************/
    // 会员
    public static void getMineOpenVip(boolean isFinish) {
        ARouter.getInstance().build(RouteUtils.Mine_Open_Vip).withBoolean("isFinish", isFinish).navigation();
    }

    // 消息
    public static void getMineNewsManager() {
        ARouter.getInstance().build(RouteUtils.Mine_News_Manager).navigation();
    }

    // 消息
    public static void getMineNewsList(int reviewType) {
        ARouter.getInstance().build(RouteUtils.Mine_News_list).withInt("reviewType", reviewType).navigation();
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
    public static void getMineLocation(boolean isDiscover) {
        ARouter.getInstance().build(RouteUtils.Mine_Location).withBoolean("isDiscover", isDiscover).navigation();
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

    // 反馈
    public static void getMineFeedbackDetail(FeedbackInfo feedbackInfo) {
        ARouter.getInstance().build(RouteUtils.Mine_Feedback_Detail).withParcelable("feedbackInfo", feedbackInfo).navigation();
    }

    // 反馈
    public static void getMineAddFeedback(FeedbackInfo feedbackInfo) {
        ARouter.getInstance().build(RouteUtils.Mine_Add_Feedback).withParcelable("feedbackInfo", feedbackInfo).navigation();
    }

    // 网页
    public static void getMineWeb(String title, String url) {
        ARouter.getInstance().build(RouteUtils.Mine_Web).withString("title", title).withString("url", url).navigation();
    }

    // 礼物收益
    public static void getMineGiftRecord() {
        ARouter.getInstance().build(RouteUtils.Mine_Gift_Record).navigation();
    }

    // 编辑个人信息
    public static void getMineEditMember() {
        ARouter.getInstance().build(RouteUtils.Mine_Edit_Member).navigation();
    }

    // 选择工作
    public static void getMineSelectJob(String job) {
        ARouter.getInstance().build(RouteUtils.Mine_Select_Job).withString("job", job).navigation();
    }

    // 编辑信息
    public static void getMineEditContent(int type, int lines, String title, String content, String hint) {
        ARouter.getInstance().build(RouteUtils.Mine_Edit_Content).withInt("type", type).withInt("lines", lines).withString("title", title).withString("content", content).withString("hint", hint).navigation();
    }

    // 选择标签
    public static void getMineSelectTag(String serviceTags) {
        ARouter.getInstance().build(RouteUtils.Mine_Select_Tag).withString("serviceTags", serviceTags).navigation();
    }

    // 粉丝、关注、被喜欢列表
    public static void getMineFCL(int position, long otherUserId) {
        ARouter.getInstance().build(RouteUtils.Mine_FCL).withInt("position", position).withLong("otherUserId", otherUserId).navigation();
    }

    // 收发礼物记录
    public static void getMineGRGift(int friendDynGiftType) {
        ARouter.getInstance().build(RouteUtils.Mine_GRGift).withInt("friendDynGiftType", friendDynGiftType).navigation();
    }

    // 交易记录
    public static void getMineTranRecord(int tranType) {
        ARouter.getInstance().build(RouteUtils.Mine_Tran_Record).withInt("tranType", tranType).navigation();
    }


    // 绑定银行卡
    public static void getMineBindingBank() {
        ARouter.getInstance().build(RouteUtils.Mine_Binding_Bank).navigation();
    }

    // 银行卡列表
    public static void getMineBankList(boolean isSelect) {
        ARouter.getInstance().build(RouteUtils.Mine_Bank_List).withBoolean("isSelect", isSelect).navigation();
    }

    // 系统消息
    public static void getMineSystemMsg() {
        ARouter.getInstance().build(RouteUtils.Mine_System_Msg).navigation();
    }

    // 提现
    public static void getMineWithdraw() {
        ARouter.getInstance().build(RouteUtils.Mine_Withdraw).navigation();
    }

    // 实名认证
    public static void getMineAuthentication(Authentication authentication) {
        ARouter.getInstance().build(RouteUtils.Mine_Authentication).withParcelable("authentication", authentication).navigation();
    }

    // 绑定手机号
    public static void getBindingPhoneActivity(RxAppCompatActivity activity, boolean isRegister, boolean isFinish) {
        ARouter.getInstance().build(RouteUtils.Mine_Binding_Phone).withBoolean("isRegister", isRegister).withBoolean("isFinish", isFinish).navigation(activity, 1002);
    }

    // 相册主页
    public static void getCameraMain(RxAppCompatActivity activity, boolean isMore, boolean showBottom, boolean showVideo) {
        ARouter.getInstance().build(RouteUtils.Camera_Main).withBoolean("isMore", isMore).withBoolean("showBottom", showBottom).withBoolean("showVideo", showVideo).navigation(activity, 1001);
    }

    // 拍视频
    public static void getCameraVideo(boolean showBottom) {
        ARouter.getInstance().build(RouteUtils.Camera_Video).withBoolean("showBottom", showBottom).navigation();
    }

    // 拍照
    public static void getCameraPhoto(boolean isMore, boolean showBottom, boolean showVideo) {
        ARouter.getInstance().build(RouteUtils.Camera_Photo).withBoolean("isMore", isMore).withBoolean("showBottom", showBottom).withBoolean("showVideo", showVideo).navigation();
    }

    // 选择视频
    public static void getCameraVideos(boolean showBottom) {
        ARouter.getInstance().build(RouteUtils.Camera_Videos).withBoolean("showBottom", showBottom).navigation();
    }


    // 视频播放
    public static void getCameraVideoPlay(String filePath, boolean isUpload, boolean isDelete) {
        ARouter.getInstance().build(RouteUtils.Camera_Video_Play).withString("filePath", filePath).withBoolean("isUpload", isUpload).withBoolean("isDelete", isDelete).navigation();
    }

    public static void getCameraPhotoStudio() {
        ARouter.getInstance().build(RouteUtils.Camera_Photo_Studio).navigation();
    }

    public static void getCameraPhotoWall(Film film, int surplusCount) {
        ARouter.getInstance().build(RouteUtils.Camera_Photo_Wall).withParcelable("film", film).withInt("surplusCount", surplusCount).navigation();
    }

    public static void getCameraPhotoGroup() {
        ARouter.getInstance().build(RouteUtils.Camera_Photo_Group).navigation();
    }

    public static void getCameraFilmDetail(Film film) {
        ARouter.getInstance().build(RouteUtils.Camera_Film_Detail).withParcelable("film", film).navigation();
    }


    /*********************** 漂流瓶 **************************/

    // 漂流瓶主页
    public static void getBottleThrow() {
        ARouter.getInstance().build(RouteUtils.Bottle_Throw).navigation();
    }

    // 漂流瓶主页
    public static void getBottleList() {
        ARouter.getInstance().build(RouteUtils.Bottle_List).navigation();
    }

    // 漂流瓶主页
    public static void getBottleChat(long driftBottleId, boolean isNotice) {
        ARouter.getInstance().build(RouteUtils.Bottle_Chat).withLong("driftBottleId", driftBottleId).withBoolean("isNotice", isNotice).navigation();
    }
}
