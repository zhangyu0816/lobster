package com.zb.lib_base.http;


import com.zb.lib_base.model.AliPay;
import com.zb.lib_base.model.BaseResultEntity;
import com.zb.lib_base.model.BottleInfo;
import com.zb.lib_base.model.BottleMsg;
import com.zb.lib_base.model.ChatList;
import com.zb.lib_base.model.ContactNum;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.FaceStatus;
import com.zb.lib_base.model.GiftInfo;
import com.zb.lib_base.model.GiftRecord;
import com.zb.lib_base.model.LoginInfo;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.MineNews;
import com.zb.lib_base.model.MineNewsCount;
import com.zb.lib_base.model.OrderNumber;
import com.zb.lib_base.model.OrderTran;
import com.zb.lib_base.model.PairInfo;
import com.zb.lib_base.model.Report;
import com.zb.lib_base.model.ResourceUrl;
import com.zb.lib_base.model.Review;
import com.zb.lib_base.model.Reward;
import com.zb.lib_base.model.TranRecord;
import com.zb.lib_base.model.VipInfo;
import com.zb.lib_base.model.WXPay;
import com.zb.lib_base.model.WalletInfo;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Field;
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

    // 用户注册验证码
    @GET("api/Login_registCaptcha")
    Observable<BaseResultEntity> registerCaptcha(@Query("userName") String userName);

    // 用户注册验证码
    @GET("api/Login_loginCaptcha")
    Observable<BaseResultEntity> loginCaptcha(@Query("userName") String userName);

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
    Observable<BaseResultEntity<OrderTran>> rechargeWallet(@Query("money") double money);


    @GET("api/Complain_comType")
    Observable<BaseResultEntity<List<Report>>> comType();

    @FormUrlEncoded
    @POST("api/Complain_comsub")
    Observable<BaseResultEntity> comsub(@Field("complainTypeId") long complainTypeId, @Field("comUserId") long comUserId, @Field("comText") String comText,
                                        @Field("images") String images);

    /******************************* app **********************************/

    // 会员价格
    @GET(" api/MemberOrder_openedMemberPriceList")
    Observable<BaseResultEntity<List<VipInfo>>> openedMemberPriceList();

    // 提交VIP订单 - 需登录
    @FormUrlEncoded
    @POST("api/MemberOrder_submitOpenedMemberOrder")
    Observable<BaseResultEntity<OrderNumber>> submitOpenedMemberOrder(@Field("memberOfOpenedProductId") long memberOfOpenedProductId, @Field("productCount") int productCount);

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

    /******************************* 首页 **********************************/

    // 我关注的人的动态列表
    @GET("api/Interactive_attentionDyn")
    Observable<BaseResultEntity<List<DiscoverInfo>>> attentionDyn(@Query("pageNo") int pageNo, @Query("timeSortType") int timeSortType);

    // 动态广场
    @GET("api/Interactive_dynPiazzaList")
    Observable<BaseResultEntity<List<DiscoverInfo>>> dynPiazzaList(@QueryMap Map<String, String> map);

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
                                                              @Query("pageNo") int pageNo);

    // 查看评论
    @GET("api/Interactive_seeReviews")
    Observable<BaseResultEntity<List<Review>>> seeReviews(@Query("friendDynId") long friendDynId, @Query("timeSortType") int timeSortType,
                                                          @Query("pageNo") int pageNo);

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

    /******************************* 卡片 **********************************/
    // 加入匹配池 (提交当前位置)
    @FormUrlEncoded
    @POST("api/Pair_joinPairPool")
    Observable<BaseResultEntity> joinPairPool(@Field("longitude") String longitude, @Field("latitude") String latitude);

    // 预匹配列表
    @GET("api/Pair_prePairList")
    Observable<BaseResultEntity<List<PairInfo>>> prePairList(@Query("sex") int sex, @Query("minAge") int minAge, @Query("maxAge") int maxAge);

    // 评估
    @FormUrlEncoded
    @POST("api/Pair_makeEvaluate")
    Observable<BaseResultEntity<Integer>> makeEvaluate(@Field("otherUserId") long otherUserId, @Field("likeOtherStatus") int likeOtherStatus);

    // 超级曝光
    @GET("api/Pair_superExposure")
    Observable<BaseResultEntity> superExposure();

    /******************************* 对话 **********************************/

    // 未读会话列表
    @GET("api/Pair_pairList")
    Observable<BaseResultEntity<List<ChatList>>> pairList(@Query("pageNo") int pageNo, @Query("pageSize") int pageSize, @Query("isPublicAccount") int isPublicAccount);

    // 未读会话列表
    @GET("api/Contact_thirdChatList")
    Observable<BaseResultEntity<List<ChatList>>> chatList(@Query("pageNo") int pageNo, @Query("pageSize") int pageSize, @Query("isPublicAccount") int isPublicAccount);


    /******************************* 漂流瓶 **********************************/

    // 投掷漂流瓶
    @FormUrlEncoded
    @POST("api/DriftBottle_castBottle")
    Observable<BaseResultEntity> castBottle(@Field("text") String text);

    // 我的漂流瓶列表
    @GET("api/DriftBottle_myBottleList")
    Observable<BaseResultEntity<List<BottleInfo>>> myBottleList(@Query("pageNo") int pageNo);

    // 我的漂流瓶列表
    @GET("api/DriftBottle_myBottle")
    Observable<BaseResultEntity<BottleInfo>> myBottle(@Query("driftBottleId") long driftBottleId);

    // 寻找漂流瓶
    @GET("api/DriftBottle_findBottle")
    Observable<BaseResultEntity<BottleInfo>> findBottle();

    // 拾取漂流瓶(包括销毁漂流瓶)
    @GET("api/DriftBottle_pcikBottle")
    Observable<BaseResultEntity> pickBottle(@Query("driftBottleId") long driftBottleId, @Query("driftBottleType") int driftBottleType,
                                            @Query("otherUserId") long otherUserId);

    // 回复漂流瓶
    @GET("api/DriftBottle_replyBottle")
    Observable<BaseResultEntity<BottleMsg>> replyBottle(@Query("driftBottleId") long driftBottleId, @Query("text") String text);

    /******************************* 我的 **********************************/

    // 修改个人信息
    @FormUrlEncoded
    @POST("api/Member_modifyMemberInfo")
    Observable<BaseResultEntity> modifyMemberInfo(@Field("nick") String nick, @Field("image") String image, @Field("moreImages") String moreImages,
                                                  @Field("personalitySign") String personalitySign, @Field("birthday") String birthday,
                                                  @Field("age") int age, @Field("sex") int sex, @Field("constellation") int constellation,
                                                  @Field("job") String job, @Field("provinceId") long provinceId, @Field("cityId") long cityId,
                                                  @Field("districtId") long districtId, @Field("singleImage") String singleImage,
                                                  @Field("serviceTags") String serviceTags);

    // 修改密码
    @FormUrlEncoded
    @POST("api/Member_modifyPass")
    Observable<BaseResultEntity> modifyPass(@Field("oldPassWord") String oldPassWord, @Field("newPassWord") String newPassWord);

    // 人脸认证状态
    @GET("api/Verify_humanFaceStatus")
    Observable<BaseResultEntity<FaceStatus>> humanFaceStatus();

    // 人脸认证
    @GET("api/Verify_humanFace")
    Observable<BaseResultEntity> humanFace(@Query("faceVerifyType") int faceVerifyType, @Query("faceImage") String faceImage,
                                           @Query("faceVideo") String faceVideo);

    // 我关注的
    @GET("api/Collect_myConcerns")
    Observable<BaseResultEntity<List<MemberInfo>>> myConcerns(@Query("pageNo") int pageNo);

    // 我的粉丝
    @GET("api/Collect_myFans")
    Observable<BaseResultEntity<List<MemberInfo>>> myFans(@Query("pageNo") int pageNo);

    // 喜欢我的人列表
    @GET("api/Pair_likeMeList")
    Observable<BaseResultEntity<List<MemberInfo>>> likeMeList(@Query("pageNo") int pageNo);

    // 新消息列表
    @GET("api/Interactive_dynNewMsgList")
    Observable<BaseResultEntity<List<MineNews>>> dynNewMsgList(@Query("pageNo") int pageNo, @Query("reviewType") int reviewType);

    // 新消息读完后，请调用这个接口
    @GET("api/Interactive_readOverMyDynNewMsg")
    Observable<BaseResultEntity> readOverMyDynNewMsg();

    // 我的新消息数量(礼物、评论、点赞)
    @GET("api/Interactive_newDynMsgAllNum")
    Observable<BaseResultEntity<MineNewsCount>> newDynMsgAllNum();

    // 清除全部未读消息
    @GET("api/Interactive_readNewDynMsgAll")
    Observable<BaseResultEntity> readNewDynMsgAll();

    // 礼物记录列表
    @GET("api/Gift_giveOrReceiveList")
    Observable<BaseResultEntity<List<GiftRecord>>> giveOrReceiveList(@Query("pageNo") int pageNo, @Query("friendDynGiftType") int friendDynGiftType);

    // 交易记录
    @GET("api/Tran_tranRecords")
    Observable<BaseResultEntity<List<TranRecord>>> tranRecords(@QueryMap Map<String, String> map);

    // 交易记录
    @GET("api/Tran_tranSingleRecord")
    Observable<BaseResultEntity<TranRecord>> tranSingleRecord(@Query("tranOrderId") String tranOrderId);
}
