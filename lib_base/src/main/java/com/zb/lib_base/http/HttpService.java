package com.zb.lib_base.http;


import com.zb.lib_base.model.AliPay;
import com.zb.lib_base.model.BaseResultEntity;
import com.zb.lib_base.model.ContactNum;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.LoginInfo;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.OrderTran;
import com.zb.lib_base.model.PairInfo;
import com.zb.lib_base.model.ResourceUrl;
import com.zb.lib_base.model.VipInfo;
import com.zb.lib_base.model.VipOrder;
import com.zb.lib_base.model.WXPay;

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

    // 用户注册
    @FormUrlEncoded
    @POST("api/Login_regist")
    Observable<BaseResultEntity<LoginInfo>> register(@Field("userName") String userName, @Field("captcha") String captcha,
                                                     @Field("moreImages") String moreImages, @Field("nick") String nick,
                                                     @Field("sex") int sex, @Field("birthday") String birthday, @Field("device") String device,
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

    /******************************* app **********************************/

    // 会员价格
    @GET(" api/MemberOrder_openedMemberPriceList")
    Observable<BaseResultEntity<List<VipInfo>>> openedMemberPriceList();

    // 提交VIP订单 - 需登录
    @FormUrlEncoded
    @POST("api/MemberOrder_submitOpenedMemberOrder")
    Observable<BaseResultEntity<VipOrder>> submitOpenedMemberOrder(@Field("memberOfOpenedProductId") long memberOfOpenedProductId, @Field("productCount") int productCount);

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

    /******************************* 卡片 **********************************/
}
