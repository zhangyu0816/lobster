package com.zb.lib_base.http;


import com.zb.lib_base.model.AliPay;
import com.zb.lib_base.model.Authentication;
import com.zb.lib_base.model.BankInfo;
import com.zb.lib_base.model.BaseResultEntity;
import com.zb.lib_base.model.BottleCache;
import com.zb.lib_base.model.BottleInfo;
import com.zb.lib_base.model.BottleMsg;
import com.zb.lib_base.model.ChatList;
import com.zb.lib_base.model.CheckUser;
import com.zb.lib_base.model.CommonSwitch;
import com.zb.lib_base.model.ContactNum;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.FaceStatus;
import com.zb.lib_base.model.FeedbackInfo;
import com.zb.lib_base.model.Film;
import com.zb.lib_base.model.FilmComment;
import com.zb.lib_base.model.FilmInfo;
import com.zb.lib_base.model.FilmMsg;
import com.zb.lib_base.model.FlashInfo;
import com.zb.lib_base.model.FlashUser;
import com.zb.lib_base.model.GiftInfo;
import com.zb.lib_base.model.GiftRecord;
import com.zb.lib_base.model.ImAccount;
import com.zb.lib_base.model.ImageCaptcha;
import com.zb.lib_base.model.LikeMe;
import com.zb.lib_base.model.LoginInfo;
import com.zb.lib_base.model.LoveMoney;
import com.zb.lib_base.model.LoveNumber;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.MineBank;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.MineNews;
import com.zb.lib_base.model.MineNewsCount;
import com.zb.lib_base.model.OrderNumber;
import com.zb.lib_base.model.OrderTran;
import com.zb.lib_base.model.PairInfo;
import com.zb.lib_base.model.PersonInfo;
import com.zb.lib_base.model.PrivateMsg;
import com.zb.lib_base.model.RechargeInfo;
import com.zb.lib_base.model.RecommendInfo;
import com.zb.lib_base.model.RentInfo;
import com.zb.lib_base.model.Report;
import com.zb.lib_base.model.ResourceUrl;
import com.zb.lib_base.model.Review;
import com.zb.lib_base.model.Reward;
import com.zb.lib_base.model.ShareInfo;
import com.zb.lib_base.model.ShareProduct;
import com.zb.lib_base.model.SystemMsg;
import com.zb.lib_base.model.TranRecord;
import com.zb.lib_base.model.VipInfo;
import com.zb.lib_base.model.VisitorCount;
import com.zb.lib_base.model.WXPay;
import com.zb.lib_base.model.WalletInfo;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * service统一接口数据
 * Created by WZG on 2016/7/16.
 */
public interface HttpService {

    // 上传图片
    @Multipart
    @POST("YmUpload_image")
    Observable<BaseResultEntity<ResourceUrl>> uploadImages(@Part("isCompre") RequestBody isCompre, @Part("isCutImage") RequestBody isCutImage,
                                                           @Part("fileFileName") RequestBody fileName, @Part("fileContentType") RequestBody fileContentType,
                                                           @Part MultipartBody.Part file);

    // 上传视频
    @Multipart
    @POST("YmUpload_videoFile")
    Observable<BaseResultEntity<ResourceUrl>> uploadVideo(@Part("fileFileName") RequestBody fileName, @Part MultipartBody.Part file);

    // 上传语音
    @Multipart
    @POST("YmUpload_soundFile")
    Observable<BaseResultEntity<ResourceUrl>> uploadSound(@Part("fileFileName") RequestBody fileName, @Part MultipartBody.Part file);

    // 用户注册
    @FormUrlEncoded
    @POST("api/Login_regist")
    Observable<BaseResultEntity<LoginInfo>> register(@Field("userName") String userName, @Field("captcha") String captcha,
                                                     @Field("moreImages") String moreImages, @Field("nick") String nick,
                                                     @Field("sex") int sex, @Field("birthday") String birthday, @Field("provinceId") long provinceId, @Field("cityId") long cityId,
                                                     @Field("districtId") long districtId, @Field("device") String device,
                                                     @Field("deviceSysVersion") String deviceSysVersion, @Field("deviceCode") String deviceCode,
                                                     @Field("channelId") String channelId, @Field("usePl") int usePl, @Field("appVersion") String appVersion,
                                                     @Field("deviceHardwareInfo") String deviceHardwareInfo);

    // 根据密码登录
    @FormUrlEncoded
    @POST("api/Login_login")
    Observable<BaseResultEntity<LoginInfo>> loginByPass(@Field("userName") String userName, @Field("passWord") String passWord, @Field("device") String device,
                                                        @Field("deviceSysVersion") String deviceSysVersion, @Field("deviceCode") String deviceCode,
                                                        @Field("channelId") String channelId, @Field("usePl") int usePl, @Field("appVersion") String appVersion,
                                                        @Field("deviceHardwareInfo") String deviceHardwareInfo);

    // 根据验证码登录
    @FormUrlEncoded
    @POST("api/Login_captchaLogin")
    Observable<BaseResultEntity<LoginInfo>> loginByCaptcha(@Field("userName") String userName, @Field("captcha") String captcha, @Field("device") String device,
                                                           @Field("deviceSysVersion") String deviceSysVersion, @Field("deviceCode") String deviceCode,
                                                           @Field("channelId") String channelId, @Field("usePl") int usePl, @Field("appVersion") String appVersion,
                                                           @Field("deviceHardwareInfo") String deviceHardwareInfo);

    // 快捷登录
    @FormUrlEncoded
    @POST("api/Union_loginByUnionV2")
    Observable<BaseResultEntity<LoginInfo>> loginByUnion(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("api/Union_bindingPhone")
    Observable<BaseResultEntity> bindingPhone(@Field("userName") String userName, @Field("captcha") String captcha);

    @FormUrlEncoded
    @POST("api/Union_banderCaptcha")
    Observable<BaseResultEntity> banderCaptcha(@Field("userName") String userName, @Field("imageCaptchaToken") String imageCaptchaToken,
                                               @Field("imageCaptchaCode") String imageCaptchaCode);

    // 用户注册验证码
    @FormUrlEncoded
    @POST("api/Login_registCaptcha")
    Observable<BaseResultEntity> registerCaptcha(@Field("userName") String userName);

    // 用户登录验证码
    @FormUrlEncoded
    @POST("api/Login_loginCaptcha")
    Observable<BaseResultEntity> loginCaptcha(@Field("userName") String userName);

    // 退出登录
    @GET("api/Login_loginOut")
    Observable<BaseResultEntity> loginOut();

    // 退出登录
    @GET("api/Login_deleteUser")
    Observable<BaseResultEntity<String>> deleteUser();

    // 验证 手机号是否注册
    @FormUrlEncoded
    @POST("api/Login_checkUserName")
    Observable<BaseResultEntity<CheckUser>> checkUserName(@Field("userName") String userName);

    // 验证 验证码
    @FormUrlEncoded
    @POST("api/Login_verifyCaptcha")
    Observable<BaseResultEntity> verifyCaptcha(@Field("userName") String userName, @Field("captcha") String captcha);

    // 图片验证码
    @GET("api/ImageCaptca_findImageCaptcha")
    Observable<BaseResultEntity<ImageCaptcha>> findImageCaptcha();

    // 找回密码1.获取验证码 --- 输入用户名,找回密码(获取验证码)
    @FormUrlEncoded
    @POST("api/Login_findPassCaptcha")
    Observable<BaseResultEntity> findPassCaptcha(@Field("userName") String userName, @Field("imageCaptchaToken") String imageCaptchaToken,
                                                 @Field("imageCaptchaCode") String imageCaptchaCode);

    // 找回密码2-修改密码
    @FormUrlEncoded
    @POST("api/Login_findPassWord")
    Observable<BaseResultEntity> findPassWord(@Field("userName") String userName, @Field("passWord") String passWord,
                                              @Field("captcha") String captcha);

    // 我的信息
    @GET("api/Member_myInfo")
    Observable<BaseResultEntity<MineInfo>> myInfo();

    // 获取他人信息
    @FormUrlEncoded
    @POST("api/Contact_otherInfo")
    Observable<BaseResultEntity<MemberInfo>> otherInfo(@Field("otherUserId") long otherUserId);

    // 钱包和受欢迎信息
    @GET("api/Member_walletAndPop")
    Observable<BaseResultEntity<WalletInfo>> walletAndPop();

    // 充钱到我的钱包
    @FormUrlEncoded
    @POST("api/Tran_rechargeWallet")
    Observable<BaseResultEntity<OrderTran>> rechargeWallet(@Field("money") double money, @Field("moneyDiscountId") long moneyDiscountId);

    // 举报类型
    @GET("api/Complain_comType")
    Observable<BaseResultEntity<List<Report>>> comType();

    // 举报
    @FormUrlEncoded
    @POST("api/Complain_comsub")
    Observable<BaseResultEntity> comsub(@Field("complainTypeId") long complainTypeId, @Field("comUserId") long comUserId, @Field("comText") String comText,
                                        @Field("images") String images);

    // 实名认证信息
    @GET("api/Verify_realNameVerifyStatus")
    Observable<BaseResultEntity<Authentication>> realNameVerify();

    // 提交实名信息
    @FormUrlEncoded
    @POST("api/Verify_upRealNameInfo")
    Observable<BaseResultEntity> upRealNameInfo(@Field("realName") String realName, @Field("identityNum") String identityNum,
                                                @Field("personalImage") String personalImage, @Field("idFrontImage") String idFrontImage,
                                                @Field("idBackImage") String idBackImage, @Field("verifyMethodType") int verifyMethodType);

    // 分享
    @GET("api/Share_memberInfoConf")
    Observable<BaseResultEntity<ShareInfo>> memberInfoConf();

    // 成为合伙人
    @FormUrlEncoded
    @POST("api/Share_openMarkePartner")
    Observable<BaseResultEntity<OrderNumber>> openMakePartner(@Field("markeProductId") long markeProductId);

    // 获取营销产品
    @GET("api/Share_markeProductList")
    Observable<BaseResultEntity<List<ShareProduct>>> markProductList();

    // 获取交易订单号
    @FormUrlEncoded
    @POST("api/Share_payOrderForTran")
    Observable<BaseResultEntity<OrderTran>> payOrderForTranShare(@Field("orderNumber") String orderNumber);

    // 活动金额 提现
    @FormUrlEncoded
    @POST("api/Share_changeCash")
    Observable<BaseResultEntity> shareChangeCash(@Field("money") double money, @Field("bankAccountId") long bankAccountId);

    /******************************* app **********************************/

    // 会员价格
    @GET("api/MemberOrder_openedMemberPriceList")
    Observable<BaseResultEntity<List<VipInfo>>> openedMemberPriceList();

    // 提交VIP订单 - 需登录
    @FormUrlEncoded
    @POST("api/MemberOrder_submitOpenedMemberOrder")
    Observable<BaseResultEntity<OrderNumber>> submitOpenedMemberOrder(@Field("memberOfOpenedProductId") long memberOfOpenedProductId, @Field("productCount") int productCount);

    // 首充
    @GET("api/MemberOrder_isFirstOpenMember")
    Observable<BaseResultEntity<Integer>> firstOpenMemberPage();

    // 获取交易订单号
    @FormUrlEncoded
    @POST("api/MemberOrder_payOrderForTran")
    Observable<BaseResultEntity<OrderTran>> payOrderForTran(@Field("orderNumber") String orderNumber);

    // 用钱包支付交易
    @FormUrlEncoded
    @POST("api/Pay_walletPayTran")
    Observable<BaseResultEntity> walletPayTran(@Field("tranOrderId") String tranOrderId);

    // 用支付宝支付交易
    @FormUrlEncoded
    @POST("api/Pay_alipayFastPayTran")
    Observable<BaseResultEntity<AliPay>> alipayFastPayTran(@Field("tranOrderId") String tranOrderId);

    // 用微信支付交易
    @FormUrlEncoded
    @POST("api/Pay_wxpayAppPayTran")
    Observable<BaseResultEntity<WXPay>> wxpayAppPayTran(@Field("tranOrderId") String tranOrderId);

    // 三合一接口 （返回关注数量、粉丝数量、喜欢数量、被喜欢数量）
    @FormUrlEncoded
    @POST("api/Collect_contactNum")
    Observable<BaseResultEntity<ContactNum>> contactNum(@Field("otherUserId") long otherUserId);

    @FormUrlEncoded
    @POST("api/SimpleRent_otherInfo")
    Observable<BaseResultEntity<RentInfo>> otherRentInfo(@Field("otherUserId") long otherUserId);

    @FormUrlEncoded
    @POST("api/Interactive_visitorBySeeMeList")
    Observable<BaseResultEntity<List<MemberInfo>>> visitorBySeeMeList(@Field("pageNo") int pageNo);

    @GET("api/Interactive_visitorBySeeMeCountV2")
    Observable<BaseResultEntity<VisitorCount>> visitorBySeeMeCount();

    /******************************* 首页 **********************************/

    // 我关注的人的动态列表
    @FormUrlEncoded
    @POST("api/Interactive_attentionDyn")
    Observable<BaseResultEntity<List<DiscoverInfo>>> attentionDyn(@Field("pageNo") int pageNo, @Field("timeSortType") int timeSortType);

    // 动态广场
    @FormUrlEncoded
    @POST("api/Interactive_dynPiazzaList")
    Observable<BaseResultEntity<List<DiscoverInfo>>> dynPiazzaList(@FieldMap Map<String, String> map);

    // 个人动态
    @FormUrlEncoded
    @POST("api/Interactive_personOtherDyn")
    Observable<BaseResultEntity<List<DiscoverInfo>>> personOtherDyn(@Field("otherUserId") long otherUserId, @Field("pageNo") int pageNo,
                                                                    @Field("timeSortType") int timeSortType, @Field("dycRootType") int dynType);

    // 动态访问
    @FormUrlEncoded
    @POST("api/Interactive_dynVisit")
    Observable<BaseResultEntity> dynVisit(@Field("friendDynId") long friendDynId);

    // 发布动态
    @FormUrlEncoded
    @POST("api/Interactive_publishDyn")
    Observable<BaseResultEntity> publishDyn(@Field("text") String text, @Field("images") String images,
                                            @Field("videoUrl") String videoUrl, @Field("resTime") int resTime,
                                            @Field("isSyncPiazza") int isSyncPiazza, @Field("isPrivate") int isPrivate,
                                            @Field("isAppearance") int isAppearance, @Field("addressInfo") String addressInfo,
                                            @Field("friendTitle") String friendTitle);

    // 动态详情
    @FormUrlEncoded
    @POST("api/Interactive_dynDetail")
    Observable<BaseResultEntity<DiscoverInfo>> dynDetail(@Field("friendDynId") long friendDynId);

    // 打赏礼物
    @GET("api/Gift_giftList")
    Observable<BaseResultEntity<List<GiftInfo>>> giftList();

    // 创建订单
    @FormUrlEncoded
    @POST("api/Gift_submitOrder")
    Observable<BaseResultEntity<OrderNumber>> submitOrder(@Field("friendDynId") long friendDynId, @Field("giftId") long giftId,
                                                          @Field("giftNum") int giftNum);

    // 创建订单
    @FormUrlEncoded
    @POST("api/Gift_submitUserOrder")
    Observable<BaseResultEntity<OrderNumber>> submitUserOrder(@Field("otherUserId") long otherUserId, @Field("giftId") long giftId,
                                                              @Field("giftNum") int giftNum);

    // 打赏列表
    @FormUrlEncoded
    @POST("api/Interactive_seeGiftRewards")
    Observable<BaseResultEntity<List<Reward>>> seeGiftRewards(@Field("friendDynId") long friendDynId, @Field("rewardSortType") int rewardSortType,
                                                              @Field("pageNo") int pageNo, @Field("row") int row);

    // 打赏列表
    @FormUrlEncoded
    @POST("api/Interactive_seeUserGiftRewards")
    Observable<BaseResultEntity<List<Reward>>> seeUserGiftRewards(@Field("otherUserId") long otherUserId, @Field("rewardSortType") int rewardSortType,
                                                                  @Field("pageNo") int pageNo, @Field("row") int row);

    // 查看评论
    @FormUrlEncoded
    @POST("api/Interactive_seeReviews")
    Observable<BaseResultEntity<List<Review>>> seeReviews(@Field("friendDynId") long friendDynId, @Field("timeSortType") int timeSortType,
                                                          @Field("pageNo") int pageNo, @Field("row") int row);

    // 删除动态
    @FormUrlEncoded
    @POST("api/Interactive_deleteDyn")
    Observable<BaseResultEntity> deleteDyn(@Field("friendDynId") long friendDynId);

    // 给动态评论
    @FormUrlEncoded
    @POST("api/Interactive_dynDoReview")
    Observable<BaseResultEntity> dynDoReview(@FieldMap Map<String, String> map);

    // 给动态点赞
    @FormUrlEncoded
    @POST("api/Interactive_dynDoLike")
    Observable<BaseResultEntity> dynDoLike(@Field("friendDynId") long friendDynId);

    // 给动态点赞
    @FormUrlEncoded
    @POST("api/Interactive_dynCancelLike")
    Observable<BaseResultEntity> dynCancelLike(@Field("friendDynId") long friendDynId);

    // 关注状态
    @FormUrlEncoded
    @POST("api/Collect_attentionStatus")
    Observable<BaseResultEntity> attentionStatus(@Field("otherUserId") long otherUserId);

    // 关注他人
    @FormUrlEncoded
    @POST("api/Collect_attentionOther")
    Observable<BaseResultEntity> attentionOther(@Field("otherUserId") long otherUserId);

    // 取消关注
    @FormUrlEncoded
    @POST("api/Collect_cancelAttention")
    Observable<BaseResultEntity> cancelAttention(@Field("otherUserId") long otherUserId);

    // 搜索
    @FormUrlEncoded
    @POST("api/SimpleRent_search")
    Observable<BaseResultEntity<List<MemberInfo>>> search(@FieldMap Map<String, String> map);

    /******************************* 卡片 **********************************/
    // 加入匹配池 (提交当前位置)
    @FormUrlEncoded
    @POST("api/Pair_joinPairPool")
    Observable<BaseResultEntity> joinPairPool(@Field("longitude") String longitude, @Field("latitude") String latitude,
                                              @Field("provinceId") long provinceId, @Field("cityId") long cityId,
                                              @Field("districtId") long districtId);

    // 更新(提交当前位置)
    @FormUrlEncoded
    @POST("api/Pair_updatePairPool")
    Observable<BaseResultEntity> updatePairPool(@Field("longitude") String longitude, @Field("latitude") String latitude,
                                                @Field("provinceId") long provinceId, @Field("cityId") long cityId,
                                                @Field("districtId") long districtId);

    // 预匹配列表
    @FormUrlEncoded
    @POST("api/Pair_prePairList")
    Observable<BaseResultEntity<List<PairInfo>>> prePairList(@FieldMap Map<String, String> map);

    // 匹配列表
    @FormUrlEncoded
    @POST("api/Pair_pairList")
    Observable<BaseResultEntity<List<LikeMe>>> pairList(@Field("pageNo") int pageNo);

    // 评估
    @FormUrlEncoded
    @POST("api/Pair_makeEvaluate")
    Observable<BaseResultEntity<Integer>> makeEvaluate(@Field("otherUserId") long otherUserId, @Field("likeOtherStatus") int likeOtherStatus);

    // 评估
    @FormUrlEncoded
    @POST("api/Pair_relievePair")
    Observable<BaseResultEntity> relievePair(@Field("otherUserId") long otherUserId);

    // 超级曝光
    @GET("api/Pair_superExposure")
    Observable<BaseResultEntity> superExposure();

    // 超级曝光
    @FormUrlEncoded
    @POST("api/UserPopularityDetail_recommendRankingList")
    Observable<BaseResultEntity<List<RecommendInfo>>> recommendRankingList(@Field("cityId") long cityId, @Field("sex") int sex);

    // 自动喜欢
    @GET("api/Pair_pushGoodUser")
    Observable<BaseResultEntity<Integer>> pushGoodUser();

    // 访问他人
    @FormUrlEncoded
    @POST("api/Interactive_otherUserInfoVisit")
    Observable<BaseResultEntity> otherUserInfoVisit(@Field("otherUserId") long otherUserId);

    /******************************* 对话 **********************************/

    // 未读会话列表
    @FormUrlEncoded
    @POST("api/Contact_thirdChatList")
    Observable<BaseResultEntity<List<ChatList>>> chatList(@Field("pageNo") int pageNo, @Field("pageSize") int pageSize,
                                                          @Field("isPublicAccount") int isPublicAccount);

    // 获取阿里百川登录账号
    @FormUrlEncoded
    @POST("api/Contact_myImAccountInfo")
    Observable<BaseResultEntity<ImAccount>> myImAccountInfo(@Field("imPlatformType") int imPlatformType);

    // 获取别人阿里百川账号
    @FormUrlEncoded
    @POST("api/Contact_otherImAccountInfo")
    Observable<BaseResultEntity<ImAccount>> otherImAccountInfo(@Field("otherUserId") long otherUserId, @Field("imPlatformType") int imPlatformType);

    // 获取历史消息
    @FormUrlEncoded
    @POST("api/Contact_historyMsgList")
    Observable<BaseResultEntity<List<PrivateMsg>>> historyMsgList(@Field("otherUserId") long otherUserId, @Field("pageNo") int pageNo);

    // 清空用户消息
    @FormUrlEncoded
    @POST("api/Contact_readOverHistoryMsg")
    Observable<BaseResultEntity> readOverHistoryMsg(@Field("otherUserId") long otherUserId, @Field("messageId") long messageId);

    // 第三方消息
    @FormUrlEncoded
    @POST("api/Contact_thirdHistoryMsgList")
    Observable<BaseResultEntity<List<PrivateMsg>>> thirdHistoryMsgList(@Field("otherUserId") long otherUserId, @Field("pageNo") int pageNo,
                                                                       @Field("imPlatformType") int imPlatformType);

    // 清除第三方未读会话
    @FormUrlEncoded
    @POST("api/Contact_thirdReadChat")
    Observable<BaseResultEntity> thirdReadChat(@Field("otherUserId") long otherUserId);

    // 清除与这个人的消息
    @FormUrlEncoded
    @POST("api/Contact_clearHistoryMsg")
    Observable<BaseResultEntity> clearAllHistoryMsg(@Field("otherUserId") long otherUserId);

    // 推荐闪聊用户列表
    @FormUrlEncoded
    @POST("api/FlashTalk_userList")
    Observable<BaseResultEntity<List<FlashInfo>>> flashUserList(@FieldMap Map<String, String> map);

    // 开启闪聊
    @FormUrlEncoded
    @POST("api/FlashTalk_saveTalk")
    Observable<BaseResultEntity<FlashUser>> saveTalk(@Field("otherUserId") long otherUserId);

    // 未读会话列表
    @FormUrlEncoded
    @POST("api/FlashTalk_chatList")
    Observable<BaseResultEntity<List<ChatList>>> flashChatList(@Field("pageNo") int pageNo, @Field("pageSize") int pageSize,
                                                               @Field("isPublicAccount") int isPublicAccount);

    // 获取闪聊历史消息
    @FormUrlEncoded
    @POST("api/FlashTalk_historyMsgList")
    Observable<BaseResultEntity<List<PrivateMsg>>> flashHistoryMsgList(@Field("otherUserId") long otherUserId, @Field("flashTalkId") long flashTalkId, @Field("pageNo") int pageNo);

    // 清除与这个人的消息
    @FormUrlEncoded
    @POST("api/FlashTalk_clearHistoryMsg")
    Observable<BaseResultEntity> flashClearHistoryMsg(@Field("otherUserId") long otherUserId, @Field("flashTalkId") long flashTalkId);

    // 历史消息读取完。 将最大的消息id 传上来
    @FormUrlEncoded
    @POST("api/FlashTalk_readOverHistoryMsg")
    Observable<BaseResultEntity> flashReadOverHistoryMsg(@Field("otherUserId") long otherUserId, @Field("flashTalkId") long flashTalkId, @Field("messageId") long messageId);

    /******************************* 漂流瓶 **********************************/

    // 投掷漂流瓶
    @FormUrlEncoded
    @POST("api/DriftBottle_castBottle")
    Observable<BaseResultEntity> castBottle(@Field("text") String text);

    // 我的漂流瓶列表
    @FormUrlEncoded
    @POST("api/DriftBottle_myBottleList")
    Observable<BaseResultEntity<List<BottleInfo>>> myBottleList(@Field("pageNo") int pageNo);

    // 我的漂流瓶
    @FormUrlEncoded
    @POST("api/DriftBottle_myBottle")
    Observable<BaseResultEntity<BottleInfo>> myBottle(@Field("driftBottleId") long driftBottleId);

    // 寻找漂流瓶
    @FormUrlEncoded
    @POST("api/DriftBottle_findBottle")
    Observable<BaseResultEntity<BottleInfo>> findBottle(@FieldMap Map<String, String> map);

    // 拾取漂流瓶(包括销毁漂流瓶)
    @FormUrlEncoded
    @POST("api/DriftBottle_pcikBottle")
    Observable<BaseResultEntity> pickBottle(@Field("driftBottleId") long driftBottleId, @Field("driftBottleType") int driftBottleType);

    // 回复漂流瓶
    @FormUrlEncoded
    @POST("api/DriftBottle_replyBottle")
    Observable<BaseResultEntity<BottleMsg>> replyBottle(@Field("driftBottleId") long driftBottleId, @Field("text") String text);

    // 漂流瓶未读数量
    @GET("api/DriftBottle_noReadBottleNum")
    Observable<BaseResultEntity<Integer>> noReadBottleNum();

    // 漂流瓶历史记录
    @FormUrlEncoded
    @POST("api/DriftBottle_historyMsgList")
    Observable<BaseResultEntity<List<PrivateMsg>>> bottleHistoryMsgList(@Field("otherUserId") long otherUserId, @Field("driftBottleId") long driftBottleId,
                                                                        @Field("pageNo") int pageNo);

    // 漂流瓶未读会话列表
    @FormUrlEncoded
    @POST("api/DriftBottle_chatList")
    Observable<BaseResultEntity<List<BottleCache>>> driftBottleChatList(@Field("isPublicAccount") int isPublicAccount, @Field("pageNo") int pageNo);

    // 清除与这个人的消息
    @FormUrlEncoded
    @POST("api/DriftBottle_clearHistoryMsg")
    Observable<BaseResultEntity> clearAllDriftBottleHistoryMsg(@Field("otherUserId") long otherUserId, @Field("driftBottleId") long driftBottleId);

    // 清空用户消息
    @FormUrlEncoded
    @POST("api/DriftBottle_readOverHistoryMsg")
    Observable<BaseResultEntity> readOverDriftBottleHistoryMsg(@Field("otherUserId") long otherUserId, @Field("driftBottleId") long driftBottleId,
                                                               @Field("messageId") long messageId);

    // 随机获取一条最新动态
    @FormUrlEncoded
    @POST("api/Interactive_randomNewDyn")
    Observable<BaseResultEntity<DiscoverInfo>> randomNewDyn(@FieldMap Map<String, String> map);

    /******************************* 我的 **********************************/

    // 修改个人信息
    @FormUrlEncoded
    @POST("api/Member_modifyMemberInfo")
    Observable<BaseResultEntity> modifyMemberInfo(@FieldMap Map<String, Object> map);

    // 修改个人信息--不审核
    @FormUrlEncoded
    @POST("api/Member_modifyMemberInfoForNoVerify")
    Observable<BaseResultEntity> modifyMemberInfoForNoVerify(@FieldMap Map<String, Object> map);

    // 修改密码
    @FormUrlEncoded
    @POST("api/Member_modifyPass")
    Observable<BaseResultEntity> modifyPass(@Field("oldPassWord") String oldPassWord, @Field("newPassWord") String newPassWord);

    // 人脸认证状态
    @GET("api/Verify_humanFaceStatus")
    Observable<BaseResultEntity<FaceStatus>> humanFaceStatus();

    // 人脸认证
    @FormUrlEncoded
    @POST("api/Verify_humanFace")
    Observable<BaseResultEntity> humanFace(@Field("faceVerifyType") int faceVerifyType, @Field("faceImage") String faceImage,
                                           @Field("faceVideo") String faceVideo, @Field("verifyMethodType") int verifyMethodType);

    // 我关注的
    @FormUrlEncoded
    @POST("api/Collect_myConcerns")
    Observable<BaseResultEntity<List<MemberInfo>>> myConcerns(@Field("pageNo") int pageNo);

    // 我的粉丝
    @FormUrlEncoded
    @POST("api/Collect_myFans")
    Observable<BaseResultEntity<List<MemberInfo>>> myFans(@Field("pageNo") int pageNo);

    // 别人的关注
    @FormUrlEncoded
    @POST("api/Collect_otherConcerns")
    Observable<BaseResultEntity<List<MemberInfo>>> otherConcerns(@Field("pageNo") int pageNo, @Field("otherUserId") long otherUserId);

    // 别人的粉丝
    @FormUrlEncoded
    @POST("api/Collect_otherFans")
    Observable<BaseResultEntity<List<MemberInfo>>> otherFans(@Field("pageNo") int pageNo, @Field("otherUserId") long otherUserId);

    // 喜欢我的人列表
    @FormUrlEncoded
    @POST("api/Pair_likeMeList")
    Observable<BaseResultEntity<List<LikeMe>>> likeMeList(@FieldMap Map<String, String> map);

    // 新消息列表
    @FormUrlEncoded
    @POST("api/Interactive_dynNewMsgListV2")
    Observable<BaseResultEntity<List<MineNews>>> dynNewMsgList(@Field("pageNo") int pageNo, @Field("reviewType") int reviewType);

    // 新消息读完后，请调用这个接口
    @GET("api/Interactive_readOverMyDynNewMsg")
    Observable<BaseResultEntity> readOverMyDynNewMsg();

    // 我的新消息数量(礼物、评论、点赞)
    @GET("api/Interactive_newDynMsgAllNum")
    Observable<BaseResultEntity<MineNewsCount>> newDynMsgAllNum();

    // 清除全部未读消息
    @FormUrlEncoded
    @POST("api/Interactive_readNewDynMsgAll")
    Observable<BaseResultEntity> readNewDynMsgAll(@FieldMap Map<String, String> map);

    // 礼物记录列表
    @FormUrlEncoded
    @POST("api/Gift_giveOrReceiveList")
    Observable<BaseResultEntity<List<GiftRecord>>> giveOrReceiveList(@Field("pageNo") int pageNo, @Field("friendDynGiftType") int friendDynGiftType);

    // 交易记录
    @FormUrlEncoded
    @POST("api/Tran_tranRecords")
    Observable<BaseResultEntity<List<TranRecord>>> tranRecords(@FieldMap Map<String, String> map);

    // 交易记录
    @FormUrlEncoded
    @POST("api/Tran_tranSingleRecord")
    Observable<BaseResultEntity<TranRecord>> tranSingleRecord(@Field("tranOrderId") String tranOrderId);

    // 所有的银行..包括支付宝
    @GET("api/Tran_bankInfoList")
    Observable<BaseResultEntity<List<BankInfo>>> bankInfoList();

    // 绑定银行卡
    @FormUrlEncoded
    @POST("api/Tran_bindBankCard")
    Observable<BaseResultEntity> bindBankCard(@Field("bankId") long bankId, @Field("accountNo") String accountNo,
                                              @Field("openAccountLocation") String openAccountLocation);

    // 我的银行卡
    @GET("api/Tran_myBankCards")
    Observable<BaseResultEntity<List<MineBank>>> myBankCards();

    // 删除银行卡
    @FormUrlEncoded
    @POST("api/Tran_removeBankCard")
    Observable<BaseResultEntity> removeBankCard(@Field("bankAccountId") long bankAccountId);

    // 充值
    @FormUrlEncoded
    @POST("api/Tran_rechargeDiscountList")
    Observable<BaseResultEntity<List<RechargeInfo>>> rechargeDiscountList(@Field("pageNo") int pageNo);

    // 提现
    @FormUrlEncoded
    @POST("api/Tran_changeCash")
    Observable<BaseResultEntity<TranRecord>> changeCash(@Field("money") String money, @Field("bankAccountId") long bankAccountId);

    // 反馈列表
    @FormUrlEncoded
    @POST("api/FeedBack_selfFeedBack")
    Observable<BaseResultEntity<List<FeedbackInfo>>> selfFeedBack(@Field("pageNumber") int pageNumber);

    // 提交反馈
    @FormUrlEncoded
    @POST("api/FeedBack_addFeedBack")
    Observable<BaseResultEntity> addFeedBack(@Field("title") String title, @Field("content") String content, @Field("images") String images);

    // 系统消息未读
    @GET("api/SystemMsg_chat")
    Observable<BaseResultEntity<SystemMsg>> systemChat();

    // 系统消息
    @FormUrlEncoded
    @POST("api/SystemMsg_historyMsgList")
    Observable<BaseResultEntity<List<SystemMsg>>> systemHistoryMsgList(@Field("pageNo") int pageNo);

    // 清除系统消息
    @FormUrlEncoded
    @POST("api/SystemMsg_clearHistoryMsg")
    Observable<BaseResultEntity> clearHistoryMsg(@Field("messageId") long messageId);

    @FormUrlEncoded
    @POST("api/Interactive_seeLikers")
    Observable<BaseResultEntity<List<Review>>> seeLikers(@Field("friendDynId") long friendDynId, @Field("pageNo") int pageNo, @Field("pageSize") int pageSize);

    @FormUrlEncoded
    @POST("api/Share_setSendMessage")
    Observable<BaseResultEntity<Integer>> setSendMessage(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("api/Verify_checkFace")
    Observable<BaseResultEntity> checkFace(@Field("faceImage") String faceImage);

    @FormUrlEncoded
    @POST("api/Camera_findCameraFilms")
    Observable<BaseResultEntity<List<Film>>> findCameraFilms(@Field("isEnable") int isEnable, @Field("pageNo") int pageNo);

    @FormUrlEncoded
    @POST("api/Camera_findCameraFilmsForAll")
    Observable<BaseResultEntity<List<Film>>> findCameraFilmsForAll(@Field("authority") int authority, @Field("washType") int washType, @Field("pageNo") int pageNo);

    @FormUrlEncoded
    @POST("api/Camera_saveCameraFilm")
    Observable<BaseResultEntity<Film>> saveCameraFilm(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("api/Camera_saveCameraFilmResource")
    Observable<BaseResultEntity> saveCameraFilmResource(@Field("cameraFilmId") long cameraFilmId, @Field("image") String image);

    @FormUrlEncoded
    @POST("api/Camera_saveCameraFilmResourceForImages")
    Observable<BaseResultEntity> saveCameraFilmResourceForImages(@Field("cameraFilmId") long cameraFilmId, @Field("images") String images);

    @FormUrlEncoded
    @POST("api/Camera_washResource")
    Observable<BaseResultEntity> washResource(@Field("cameraFilmId") long cameraFilmId);

    @FormUrlEncoded
    @POST("api/Camera_findCameraFilmsResourceList")
    Observable<BaseResultEntity<List<FilmInfo>>> findCameraFilmsResourceList(@Field("cameraFilmId") long cameraFilmId);

    @FormUrlEncoded
    @POST("api/Camera_cameraFilmMsCount")
    Observable<BaseResultEntity<Integer>> cameraFilmMsCount(@Field("isRead") int isRead);

    @FormUrlEncoded
    @POST("api/Camera_cameraFilmMsgList")
    Observable<BaseResultEntity<List<FilmMsg>>> cameraFilmMsgList(@Field("pageNo") int pageNo, @Field("row") int row);

    @FormUrlEncoded
    @POST("api/Camera_findCameraFilmsResource")
    Observable<BaseResultEntity<FilmInfo>> findCameraFilmsResource(@Field("cameraFilmResourceId") long cameraFilmResourceId);

    @FormUrlEncoded
    @POST("api/Camera_seeLikers")
    Observable<BaseResultEntity<List<FilmComment>>> cameraSeeLikers(@Field("cameraFilmResourceId") long cameraFilmResourceId, @Field("pageNo") int pageNo);

    @FormUrlEncoded
    @POST("api/Camera_seeReviews")
    Observable<BaseResultEntity<List<FilmComment>>> cameraSeeReviews(@Field("cameraFilmResourceId") long cameraFilmResourceId, @Field("timeSortType") int timeSortType, @Field("pageNo") int pageNo, @Field("row") int row);

    @FormUrlEncoded
    @POST("api/Camera_review")
    Observable<BaseResultEntity> cameraReview(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("api/Camera_doLike")
    Observable<BaseResultEntity> cameraDoLike(@Field("cameraFilmResourceId") long cameraFilmResourceId);

    @FormUrlEncoded
    @POST("api/Camera_cancelLike")
    Observable<BaseResultEntity> cameraCancelLike(@Field("cameraFilmResourceId") long cameraFilmResourceId);

    @FormUrlEncoded
    @POST("api/Camera_findCameraFilmsInfo")
    Observable<BaseResultEntity<Film>> findCameraFilmsInfo(@Field("cameraFilmId") long cameraFilmId);

    @FormUrlEncoded
    @POST("api/Camera_readCameraFilmMsg")
    Observable<BaseResultEntity> readCameraFilmMsg(@Field("cameraFilmResourceReviewMsgId") long cameraFilmResourceReviewMsgId);

    @FormUrlEncoded
    @POST("api/Camera_deleteCameraFilmMsg")
    Observable<BaseResultEntity> deleteCameraFilmMsg(@Field("cameraFilmResourceReviewMsgId") long cameraFilmResourceReviewMsgId);

    @GET("api/Camera_readCameraFilmMsgForAllRead")
    Observable<BaseResultEntity> readCameraFilmMsgForAllRead();

    @GET("api/AppCommon_functionSwitch")
    Observable<BaseResultEntity<CommonSwitch>> functionSwitch();

    // 保存 爱情盲盒
    @FormUrlEncoded
    @POST("api/BlackBox_saveBlackBoxPersonInfo")
    Observable<BaseResultEntity<Long>> saveBlackBoxPersonInfo(@Field("age") int age, @Field("wxNum") String wxNum,
                                                              @Field("sourceType") int sourceType, @Field("provinceId") long provinceId,
                                                              @Field("cityId") long cityId, @Field("sex") int sex);

    // 创建爱情盲盒 存或取 订单
    @FormUrlEncoded
    @POST("api/BlackBox_submitBlackBoxOrderForTran")
    Observable<BaseResultEntity<LoveNumber>> submitBlackBoxOrderForTran(@FieldMap Map<String, Object> map);

    // 支付订单
    @FormUrlEncoded
    @POST("api/Interactive_payOrderForTran")
    Observable<BaseResultEntity<OrderTran>> payOrderForTranLove(@Field("number") String number);

    // 获取爱情盲盒信息
    @FormUrlEncoded
    @POST("api/BlackBox_getBlackBoxPersonInfo")
    Observable<BaseResultEntity<PersonInfo>> getBlackBoxPersonInfo(@Field("number") String number);

    // 盲盒奖励 收益总额
    @FormUrlEncoded
    @POST("api/BlackBox_statisticsRewardsCount")
    Observable<BaseResultEntity<PersonInfo>> statisticsRewardsCount(@Field("tranStatusType") int tranStatusType);

    // 盲盒奖励订单列表
    @FormUrlEncoded
    @POST("api/BlackBox_rewardsOrderList")
    Observable<BaseResultEntity<List<LoveMoney>>> rewardsOrderList(@Field("tranStatusType") int tranStatusType, @Field("pageNo") int pageNo,
                                                                   @Field("pageSize") int pageSize);

    // 是否绑定微信钱包
    @GET("api/Tran_myWeChatIsBind")
    Observable<BaseResultEntity<PersonInfo>> myWeChatIsBind();
}
