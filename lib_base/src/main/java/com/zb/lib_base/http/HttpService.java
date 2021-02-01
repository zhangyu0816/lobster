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
import com.zb.lib_base.model.ContactNum;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.FaceStatus;
import com.zb.lib_base.model.FeedbackInfo;
import com.zb.lib_base.model.FlashInfo;
import com.zb.lib_base.model.FlashUser;
import com.zb.lib_base.model.GiftInfo;
import com.zb.lib_base.model.GiftRecord;
import com.zb.lib_base.model.ImAccount;
import com.zb.lib_base.model.ImageCaptcha;
import com.zb.lib_base.model.LikeMe;
import com.zb.lib_base.model.LoginInfo;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.MineBank;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.MineNews;
import com.zb.lib_base.model.MineNewsCount;
import com.zb.lib_base.model.OrderNumber;
import com.zb.lib_base.model.OrderTran;
import com.zb.lib_base.model.PairInfo;
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
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
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
    @GET("api/Login_regist")
    Observable<BaseResultEntity<LoginInfo>> register(@Query("userName") String userName, @Query("captcha") String captcha,
                                                     @Query("moreImages") String moreImages, @Query("nick") String nick,
                                                     @Query("sex") int sex, @Query("birthday") String birthday, @Query("provinceId") long provinceId, @Query("cityId") long cityId,
                                                     @Query("districtId") long districtId, @Query("device") String device,
                                                     @Query("deviceSysVersion") String deviceSysVersion, @Query("deviceCode") String deviceCode,
                                                     @Query("channelId") String channelId, @Query("usePl") int usePl, @Query("appVersion") String appVersion,
                                                     @Query("deviceHardwareInfo") String deviceHardwareInfo);

    // 根据密码登录
    @GET("api/Login_login")
    Observable<BaseResultEntity<LoginInfo>> loginByPass(@Query("userName") String userName, @Query("passWord") String passWord, @Query("device") String device,
                                                        @Query("deviceSysVersion") String deviceSysVersion, @Query("deviceCode") String deviceCode,
                                                        @Query("channelId") String channelId, @Query("usePl") int usePl, @Query("appVersion") String appVersion,
                                                        @Query("deviceHardwareInfo") String deviceHardwareInfo);

    // 根据验证码登录
    @GET("api/Login_captchaLogin")
    Observable<BaseResultEntity<LoginInfo>> loginByCaptcha(@Query("userName") String userName, @Query("captcha") String captcha, @Query("device") String device,
                                                           @Query("deviceSysVersion") String deviceSysVersion, @Query("deviceCode") String deviceCode,
                                                           @Query("channelId") String channelId, @Query("usePl") int usePl, @Query("appVersion") String appVersion,
                                                           @Query("deviceHardwareInfo") String deviceHardwareInfo);

    // 快捷登录
    @FormUrlEncoded
    @POST("api/Union_loginByUnionV2")
    Observable<BaseResultEntity<LoginInfo>> loginByUnion(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("api/Union_bindingPhone")
    Observable<BaseResultEntity> bindingPhone(@Field("userName") String userName, @Field("captcha") String captcha);

    @GET("api/Union_banderCaptcha")
    Observable<BaseResultEntity> banderCaptcha(@Query("userName") String userName, @Query("imageCaptchaToken") String imageCaptchaToken,
                                               @Query("imageCaptchaCode") String imageCaptchaCode);

    // 用户注册验证码
    @GET("api/Login_registCaptcha")
    Observable<BaseResultEntity> registerCaptcha(@Query("userName") String userName);

    // 用户登录验证码
    @GET("api/Login_loginCaptcha")
    Observable<BaseResultEntity> loginCaptcha(@Query("userName") String userName);

    // 退出登录
    @GET("api/Login_loginOut")
    Observable<BaseResultEntity> loginOut();

    // 验证 手机号是否注册
    @GET("api/Login_checkUserName")
    Observable<BaseResultEntity<CheckUser>> checkUserName(@Query("userName") String userName);

    // 验证 验证码
    @GET("api/Login_verifyCaptcha")
    Observable<BaseResultEntity> verifyCaptcha(@Query("userName") String userName, @Query("captcha") String captcha);

    // 图片验证码
    @GET("api/ImageCaptca_findImageCaptcha")
    Observable<BaseResultEntity<ImageCaptcha>> findImageCaptcha();

    // 找回密码1.获取验证码 --- 输入用户名,找回密码(获取验证码)
    @GET("api/Login_findPassCaptcha")
    Observable<BaseResultEntity> findPassCaptcha(@Query("userName") String userName, @Query("imageCaptchaToken") String imageCaptchaToken,
                                                 @Query("imageCaptchaCode") String imageCaptchaCode);

    // 找回密码2-修改密码
    @GET("api/Login_findPassWord")
    Observable<BaseResultEntity> findPassWord(@Query("userName") String userName, @Query("passWord") String passWord,
                                              @Query("captcha") String captcha);

    // 我的信息
    @GET("api/Member_myInfo")
    Observable<BaseResultEntity<MineInfo>> myInfo();

    // 获取他人信息
    @GET("api/Contact_otherInfo")
    Observable<BaseResultEntity<MemberInfo>> otherInfo(@Query("otherUserId") long otherUserId);

    // 钱包和受欢迎信息
    @GET("api/Member_walletAndPop")
    Observable<BaseResultEntity<WalletInfo>> walletAndPop();

    // 充钱到我的钱包
    @GET("api/Tran_rechargeWallet")
    Observable<BaseResultEntity<OrderTran>> rechargeWallet(@Query("money") double money, @Query("moneyDiscountId") long moneyDiscountId);

    // 举报类型
    @GET("api/Complain_comType")
    Observable<BaseResultEntity<List<Report>>> comType();

    // 举报
    @GET("api/Complain_comsub")
    Observable<BaseResultEntity> comsub(@Query("complainTypeId") long complainTypeId, @Query("comUserId") long comUserId, @Query("comText") String comText,
                                        @Query("images") String images);

    // 实名认证信息
    @GET("api/Verify_realNameVerifyStatus")
    Observable<BaseResultEntity<Authentication>> realNameVerify();

    // 提交实名信息
    @GET("api/Verify_upRealNameInfo")
    Observable<BaseResultEntity> upRealNameInfo(@Query("realName") String realName, @Query("identityNum") String identityNum,
                                                @Query("personalImage") String personalImage, @Query("idFrontImage") String idFrontImage,
                                                @Query("idBackImage") String idBackImage, @Query("verifyMethodType") int verifyMethodType);

    // 分享
    @GET("api/Share_memberInfoConf")
    Observable<BaseResultEntity<ShareInfo>> memberInfoConf();

    // 成为合伙人
    @GET("api/Share_openMarkePartner")
    Observable<BaseResultEntity<OrderNumber>> openMakePartner(@Query("markeProductId") long markeProductId);

    // 获取营销产品
    @GET("api/Share_markeProductList")
    Observable<BaseResultEntity<List<ShareProduct>>> markProductList();

    // 获取交易订单号
    @GET("api/Share_payOrderForTran")
    Observable<BaseResultEntity<OrderTran>> payOrderForTranShare(@Query("orderNumber") String orderNumber);

    // 活动金额 提现
    @GET("api/Share_changeCash")
    Observable<BaseResultEntity> shareChangeCash(@Query("money") double money, @Query("bankAccountId") long bankAccountId);

    /******************************* app **********************************/

    // 会员价格
    @GET("api/MemberOrder_openedMemberPriceList")
    Observable<BaseResultEntity<List<VipInfo>>> openedMemberPriceList();

    // 提交VIP订单 - 需登录
    @GET("api/MemberOrder_submitOpenedMemberOrder")
    Observable<BaseResultEntity<OrderNumber>> submitOpenedMemberOrder(@Query("memberOfOpenedProductId") long memberOfOpenedProductId, @Query("productCount") int productCount);

    // 首充
    @GET("api/MemberOrder_isFirstOpenMember")
    Observable<BaseResultEntity<Integer>> firstOpenMemberPage();

    // 获取交易订单号
    @GET("api/MemberOrder_payOrderForTran")
    Observable<BaseResultEntity<OrderTran>> payOrderForTran(@Query("orderNumber") String orderNumber);

    // 用钱包支付交易
    @GET("api/Pay_walletPayTran")
    Observable<BaseResultEntity> walletPayTran(@Query("tranOrderId") String tranOrderId);

    // 用支付宝支付交易
    @GET("api/Pay_alipayFastPayTran")
    Observable<BaseResultEntity<AliPay>> alipayFastPayTran(@Query("tranOrderId") String tranOrderId);

    // 用微信支付交易
    @GET("api/Pay_wxpayAppPayTran")
    Observable<BaseResultEntity<WXPay>> wxpayAppPayTran(@Query("tranOrderId") String tranOrderId);

    // 三合一接口 （返回关注数量、粉丝数量、喜欢数量、被喜欢数量）
    @GET("api/Collect_contactNum")
    Observable<BaseResultEntity<ContactNum>> contactNum(@Query("otherUserId") long otherUserId);

    @GET("api/SimpleRent_otherInfo")
    Observable<BaseResultEntity<RentInfo>> otherRentInfo(@Query("otherUserId") long otherUserId);

    @GET("api/Interactive_visitorBySeeMeList")
    Observable<BaseResultEntity<List<MemberInfo>>> visitorBySeeMeList(@Query("pageNo") int pageNo);

    @GET("api/Interactive_visitorBySeeMeCountV2")
    Observable<BaseResultEntity<VisitorCount>> visitorBySeeMeCount();

    /******************************* 首页 **********************************/

    // 我关注的人的动态列表
    @GET("api/Interactive_attentionDyn")
    Observable<BaseResultEntity<List<DiscoverInfo>>> attentionDyn(@Query("pageNo") int pageNo, @Query("timeSortType") int timeSortType);

    // 动态广场
    @GET("api/Interactive_dynPiazzaList")
    Observable<BaseResultEntity<List<DiscoverInfo>>> dynPiazzaList(@QueryMap Map<String, String> map);

    // 个人动态
    @GET("api/Interactive_personOtherDyn")
    Observable<BaseResultEntity<List<DiscoverInfo>>> personOtherDyn(@Query("otherUserId") long otherUserId, @Query("pageNo") int pageNo,
                                                                    @Query("timeSortType") int timeSortType, @Query("dycRootType") int dynType);

    // 动态访问
    @GET("api/Interactive_dynVisit")
    Observable<BaseResultEntity> dynVisit(@Query("friendDynId") long friendDynId);

    // 发布动态
    @GET("api/Interactive_publishDyn")
    Observable<BaseResultEntity> publishDyn(@Query("text") String text, @Query("images") String images,
                                            @Query("videoUrl") String videoUrl, @Query("resTime") int resTime,
                                            @Query("isSyncPiazza") int isSyncPiazza, @Query("isPrivate") int isPrivate,
                                            @Query("isAppearance") int isAppearance, @Query("addressInfo") String addressInfo,
                                            @Query("friendTitle") String friendTitle);

    // 动态详情
    @GET("api/Interactive_dynDetail")
    Observable<BaseResultEntity<DiscoverInfo>> dynDetail(@Query("friendDynId") long friendDynId);

    // 打赏礼物
    @GET("api/Gift_giftList")
    Observable<BaseResultEntity<List<GiftInfo>>> giftList();

    // 创建订单
    @GET("api/Gift_submitOrder")
    Observable<BaseResultEntity<OrderNumber>> submitOrder(@Query("friendDynId") long friendDynId, @Query("giftId") long giftId,
                                                          @Query("giftNum") int giftNum);

    // 打赏列表
    @GET("api/Interactive_seeGiftRewards")
    Observable<BaseResultEntity<List<Reward>>> seeGiftRewards(@Query("friendDynId") long friendDynId, @Query("rewardSortType") int rewardSortType,
                                                              @Query("pageNo") int pageNo, @Query("row") int row);

    // 查看评论
    @GET("api/Interactive_seeReviews")
    Observable<BaseResultEntity<List<Review>>> seeReviews(@Query("friendDynId") long friendDynId, @Query("timeSortType") int timeSortType,
                                                          @Query("pageNo") int pageNo, @Query("row") int row);

    // 删除动态
    @GET("api/Interactive_deleteDyn")
    Observable<BaseResultEntity> deleteDyn(@Query("friendDynId") long friendDynId);

    // 给动态评论
    @GET("api/Interactive_dynDoReview")
    Observable<BaseResultEntity> dynDoReview(@QueryMap Map<String, String> map);

    // 给动态点赞
    @GET("api/Interactive_dynDoLike")
    Observable<BaseResultEntity> dynDoLike(@Query("friendDynId") long friendDynId);

    // 给动态点赞
    @GET("api/Interactive_dynCancelLike")
    Observable<BaseResultEntity> dynCancelLike(@Query("friendDynId") long friendDynId);

    // 关注状态
    @GET("api/Collect_attentionStatus")
    Observable<BaseResultEntity> attentionStatus(@Query("otherUserId") long otherUserId);

    // 关注他人
    @GET("api/Collect_attentionOther")
    Observable<BaseResultEntity> attentionOther(@Query("otherUserId") long otherUserId);

    // 取消关注
    @GET("api/Collect_cancelAttention")
    Observable<BaseResultEntity> cancelAttention(@Query("otherUserId") long otherUserId);

    // 搜索
    @GET("api/SimpleRent_search")
    Observable<BaseResultEntity<List<MemberInfo>>> search(@QueryMap Map<String, String> map);

    /******************************* 卡片 **********************************/
    // 加入匹配池 (提交当前位置)
    @GET("api/Pair_joinPairPool")
    Observable<BaseResultEntity> joinPairPool(@Query("longitude") String longitude, @Query("latitude") String latitude,
                                              @Query("provinceId") long provinceId, @Query("cityId") long cityId,
                                              @Query("districtId") long districtId);

    // 更新(提交当前位置)
    @GET("api/Pair_updatePairPool")
    Observable<BaseResultEntity> updatePairPool(@Query("longitude") String longitude, @Query("latitude") String latitude,
                                                @Query("provinceId") long provinceId, @Query("cityId") long cityId,
                                                @Query("districtId") long districtId);

    // 预匹配列表
    @GET("api/Pair_prePairList")
    Observable<BaseResultEntity<List<PairInfo>>> prePairList(@QueryMap Map<String, String> map);

    // 匹配列表
    @GET("api/Pair_pairList")
    Observable<BaseResultEntity<List<LikeMe>>> pairList(@Query("pageNo") int pageNo);

    // 评估
    @GET("api/Pair_makeEvaluate")
    Observable<BaseResultEntity<Integer>> makeEvaluate(@Query("otherUserId") long otherUserId, @Query("likeOtherStatus") int likeOtherStatus);

    // 评估
    @GET("api/Pair_relievePair")
    Observable<BaseResultEntity> relievePair(@Query("otherUserId") long otherUserId);

    // 超级曝光
    @GET("api/Pair_superExposure")
    Observable<BaseResultEntity> superExposure();

    // 超级曝光
    @GET("api/UserPopularityDetail_recommendRankingList")
    Observable<BaseResultEntity<List<RecommendInfo>>> recommendRankingList(@Query("cityId") long cityId, @Query("sex") int sex);

    // 自动喜欢
    @GET("api/Pair_pushGoodUser")
    Observable<BaseResultEntity<Integer>> pushGoodUser();

    // 访问他人
    @GET("api/Interactive_otherUserInfoVisit")
    Observable<BaseResultEntity> otherUserInfoVisit(@Query("otherUserId") long otherUserId);

    /******************************* 对话 **********************************/

    // 未读会话列表
    @GET("api/Contact_thirdChatList")
    Observable<BaseResultEntity<List<ChatList>>> chatList(@Query("pageNo") int pageNo, @Query("pageSize") int pageSize,
                                                          @Query("isPublicAccount") int isPublicAccount);

    // 获取阿里百川登录账号
    @GET("api/Contact_myImAccountInfo")
    Observable<BaseResultEntity<ImAccount>> myImAccountInfo(@Query("imPlatformType") int imPlatformType);

    // 获取别人阿里百川账号
    @GET("api/Contact_otherImAccountInfo")
    Observable<BaseResultEntity<ImAccount>> otherImAccountInfo(@Query("otherUserId") long otherUserId, @Query("imPlatformType") int imPlatformType);

    // 获取历史消息
    @GET("api/Contact_historyMsgList")
    Observable<BaseResultEntity<List<PrivateMsg>>> historyMsgList(@Query("otherUserId") long otherUserId, @Query("pageNo") int pageNo);

    // 清空用户消息
    @GET("api/Contact_readOverHistoryMsg")
    Observable<BaseResultEntity> readOverHistoryMsg(@Query("otherUserId") long otherUserId, @Query("messageId") long messageId);

    // 第三方消息
    @GET("api/Contact_thirdHistoryMsgList")
    Observable<BaseResultEntity<List<PrivateMsg>>> thirdHistoryMsgList(@Query("otherUserId") long otherUserId, @Query("pageNo") int pageNo,
                                                                       @Query("imPlatformType") int imPlatformType);

    // 清除第三方未读会话
    @GET("api/Contact_thirdReadChat")
    Observable<BaseResultEntity> thirdReadChat(@Query("otherUserId") long otherUserId);

    // 清除与这个人的消息
    @GET("api/Contact_clearHistoryMsg")
    Observable<BaseResultEntity> clearAllHistoryMsg(@Query("otherUserId") long otherUserId);

    // 推荐闪聊用户列表
    @GET("api/FlashTalk_userList")
    Observable<BaseResultEntity<List<FlashInfo>>> flashUserList(@QueryMap Map<String, String> map);

    // 开启闪聊
    @GET("api/FlashTalk_saveTalk")
    Observable<BaseResultEntity<FlashUser>> saveTalk(@Query("otherUserId") long otherUserId);

    // 未读会话列表
    @GET("api/FlashTalk_chatList")
    Observable<BaseResultEntity<List<ChatList>>> flashChatList(@Query("pageNo") int pageNo, @Query("pageSize") int pageSize,
                                                               @Query("isPublicAccount") int isPublicAccount);

    // 获取闪聊历史消息
    @GET("api/FlashTalk_historyMsgList")
    Observable<BaseResultEntity<List<PrivateMsg>>> flashHistoryMsgList(@Query("otherUserId") long otherUserId, @Query("flashTalkId") long flashTalkId, @Query("pageNo") int pageNo);

    // 清除与这个人的消息
    @GET("api/FlashTalk_clearHistoryMsg")
    Observable<BaseResultEntity> flashClearHistoryMsg(@Query("otherUserId") long otherUserId, @Query("flashTalkId") long flashTalkId);

    // 历史消息读取完。 将最大的消息id 传上来
    @GET("api/FlashTalk_readOverHistoryMsg")
    Observable<BaseResultEntity> flashReadOverHistoryMsg(@Query("otherUserId") long otherUserId, @Query("flashTalkId") long flashTalkId, @Query("messageId") long messageId);

    /******************************* 漂流瓶 **********************************/

    // 投掷漂流瓶
    @GET("api/DriftBottle_castBottle")
    Observable<BaseResultEntity> castBottle(@Query("text") String text);

    // 我的漂流瓶列表
    @GET("api/DriftBottle_myBottleList")
    Observable<BaseResultEntity<List<BottleInfo>>> myBottleList(@Query("pageNo") int pageNo);

    // 我的漂流瓶
    @GET("api/DriftBottle_myBottle")
    Observable<BaseResultEntity<BottleInfo>> myBottle(@Query("driftBottleId") long driftBottleId);

    // 寻找漂流瓶
    @GET("api/DriftBottle_findBottle")
    Observable<BaseResultEntity<BottleInfo>> findBottle(@QueryMap Map<String, String> map);

    // 拾取漂流瓶(包括销毁漂流瓶)
    @GET("api/DriftBottle_pcikBottle")
    Observable<BaseResultEntity> pickBottle(@Query("driftBottleId") long driftBottleId, @Query("driftBottleType") int driftBottleType);

    // 回复漂流瓶
    @GET("api/DriftBottle_replyBottle")
    Observable<BaseResultEntity<BottleMsg>> replyBottle(@Query("driftBottleId") long driftBottleId, @Query("text") String text);

    // 漂流瓶未读数量
    @GET("api/DriftBottle_noReadBottleNum")
    Observable<BaseResultEntity<Integer>> noReadBottleNum();

    // 漂流瓶历史记录
    @GET("api/DriftBottle_historyMsgList")
    Observable<BaseResultEntity<List<PrivateMsg>>> bottleHistoryMsgList(@Query("otherUserId") long otherUserId, @Query("driftBottleId") long driftBottleId,
                                                                        @Query("pageNo") int pageNo);

    // 漂流瓶未读会话列表
    @GET("api/DriftBottle_chatList")
    Observable<BaseResultEntity<List<BottleCache>>> driftBottleChatList(@Query("isPublicAccount") int isPublicAccount, @Query("pageNo") int pageNo);

    // 清除与这个人的消息
    @GET("api/DriftBottle_clearHistoryMsg")
    Observable<BaseResultEntity> clearAllDriftBottleHistoryMsg(@Query("otherUserId") long otherUserId, @Query("driftBottleId") long driftBottleId);

    // 清空用户消息
    @GET("api/DriftBottle_readOverHistoryMsg")
    Observable<BaseResultEntity> readOverDriftBottleHistoryMsg(@Query("otherUserId") long otherUserId, @Query("driftBottleId") long driftBottleId,
                                                               @Query("messageId") long messageId);

    // 随机获取一条最新动态
    @GET("api/Interactive_randomNewDyn")
    Observable<BaseResultEntity<DiscoverInfo>> randomNewDyn(@QueryMap Map<String, String> map);

    /******************************* 我的 **********************************/

    // 修改个人信息
    @GET("api/Member_modifyMemberInfo")
    Observable<BaseResultEntity> modifyMemberInfo(@Query("nick") String nick, @Query("image") String image, @Query("moreImages") String moreImages,
                                                  @Query("personalitySign") String personalitySign, @Query("birthday") String birthday,
                                                  @Query("age") int age, @Query("sex") int sex, @Query("constellation") int constellation,
                                                  @Query("job") String job, @Query("provinceId") long provinceId, @Query("cityId") long cityId,
                                                  @Query("districtId") long districtId, @Query("singleImage") String singleImage,
                                                  @Query("serviceTags") String serviceTags);

    // 修改密码
    @GET("api/Member_modifyPass")
    Observable<BaseResultEntity> modifyPass(@Query("oldPassWord") String oldPassWord, @Query("newPassWord") String newPassWord);

    // 人脸认证状态
    @GET("api/Verify_humanFaceStatus")
    Observable<BaseResultEntity<FaceStatus>> humanFaceStatus();

    // 人脸认证
    @GET("api/Verify_humanFace")
    Observable<BaseResultEntity> humanFace(@Query("faceVerifyType") int faceVerifyType, @Query("faceImage") String faceImage,
                                           @Query("faceVideo") String faceVideo, @Query("verifyMethodType") int verifyMethodType);

    // 我关注的
    @GET("api/Collect_myConcerns")
    Observable<BaseResultEntity<List<MemberInfo>>> myConcerns(@Query("pageNo") int pageNo);

    // 我的粉丝
    @GET("api/Collect_myFans")
    Observable<BaseResultEntity<List<MemberInfo>>> myFans(@Query("pageNo") int pageNo);

    // 喜欢我的人列表
    @GET("api/Pair_likeMeList")
    Observable<BaseResultEntity<List<LikeMe>>> likeMeList(@QueryMap Map<String, String> map);

    // 新消息列表
    @GET("api/Interactive_dynNewMsgListV2")
    Observable<BaseResultEntity<List<MineNews>>> dynNewMsgList(@Query("pageNo") int pageNo, @Query("reviewType") int reviewType);

    // 新消息读完后，请调用这个接口
    @GET("api/Interactive_readOverMyDynNewMsg")
    Observable<BaseResultEntity> readOverMyDynNewMsg();

    // 我的新消息数量(礼物、评论、点赞)
    @GET("api/Interactive_newDynMsgAllNum")
    Observable<BaseResultEntity<MineNewsCount>> newDynMsgAllNum();

    // 清除全部未读消息
    @GET("api/Interactive_readNewDynMsgAll")
    Observable<BaseResultEntity> readNewDynMsgAll(@QueryMap Map<String, String> map);

    // 礼物记录列表
    @GET("api/Gift_giveOrReceiveList")
    Observable<BaseResultEntity<List<GiftRecord>>> giveOrReceiveList(@Query("pageNo") int pageNo, @Query("friendDynGiftType") int friendDynGiftType);

    // 交易记录
    @GET("api/Tran_tranRecords")
    Observable<BaseResultEntity<List<TranRecord>>> tranRecords(@QueryMap Map<String, String> map);

    // 交易记录
    @GET("api/Tran_tranSingleRecord")
    Observable<BaseResultEntity<TranRecord>> tranSingleRecord(@Query("tranOrderId") String tranOrderId);

    // 所有的银行..包括支付宝
    @GET("api/Tran_bankInfoList")
    Observable<BaseResultEntity<List<BankInfo>>> bankInfoList();

    // 绑定银行卡
    @GET("api/Tran_bindBankCard")
    Observable<BaseResultEntity> bindBankCard(@Query("bankId") long bankId, @Query("accountNo") String accountNo,
                                              @Query("openAccountLocation") String openAccountLocation);

    // 我的银行卡
    @GET("api/Tran_myBankCards")
    Observable<BaseResultEntity<List<MineBank>>> myBankCards();

    // 删除银行卡
    @GET("api/Tran_removeBankCard")
    Observable<BaseResultEntity> removeBankCard(@Query("bankAccountId") long bankAccountId);

    // 充值
    @GET("api/Tran_rechargeDiscountList")
    Observable<BaseResultEntity<List<RechargeInfo>>> rechargeDiscountList(@Query("pageNo") int pageNo);

    // 提现
    @GET("api/Tran_changeCash")
    Observable<BaseResultEntity<TranRecord>> changeCash(@Query("money") String money, @Query("bankAccountId") long bankAccountId);

    // 反馈列表
    @GET("api/FeedBack_selfFeedBack")
    Observable<BaseResultEntity<List<FeedbackInfo>>> selfFeedBack(@Query("pageNumber") int pageNumber);

    // 提交反馈
    @GET("api/FeedBack_addFeedBack")
    Observable<BaseResultEntity> addFeedBack(@Query("title") String title, @Query("content") String content, @Query("images") String images);

    // 系统消息未读
    @GET("api/SystemMsg_chat")
    Observable<BaseResultEntity<SystemMsg>> systemChat();

    // 系统消息
    @GET("api/SystemMsg_historyMsgList")
    Observable<BaseResultEntity<List<SystemMsg>>> systemHistoryMsgList(@Query("pageNo") int pageNo);

    // 清除系统消息
    @GET("api/SystemMsg_clearHistoryMsg")
    Observable<BaseResultEntity> clearHistoryMsg(@Query("messageId") long messageId);

    @GET("api/Interactive_seeLikers")
    Observable<BaseResultEntity<List<Review>>> seeLikers(@Query("friendDynId") long friendDynId, @Query("pageNo") int pageNo, @Query("pageSize") int pageSize);
}
