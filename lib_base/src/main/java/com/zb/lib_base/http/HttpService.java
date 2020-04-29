package com.zb.lib_base.http;


import com.zb.lib_base.model.BaseResultEntity;
import com.zb.lib_base.model.LoginInfo;
import com.zb.lib_base.model.ResourceUrl;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
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


}
