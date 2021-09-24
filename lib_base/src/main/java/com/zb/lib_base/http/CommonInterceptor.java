package com.zb.lib_base.http;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.yimi.lib.EncryptionUtil;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import androidx.annotation.NonNull;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ZhengQunWei
 * on 2017/7/6
 */
public class CommonInterceptor implements Interceptor {


    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        // 添加新的参数
        HttpUrl.Builder authorizedUrlBuilder = oldRequest.url().newBuilder().scheme(oldRequest.url().scheme()).host
                (oldRequest.url().host())
                .addQueryParameter("pfDevice", "Android")
                .addQueryParameter("pfAppType", "203")
                .addQueryParameter("pfAppVersion", MineApp.versionName)
                .addQueryParameter("fullVersion", MineApp.versionName + ".1")
                .addQueryParameter("userId", BaseActivity.userId + "")
                .addQueryParameter("sessionId", BaseActivity.sessionId)
                .addQueryParameter("androidSign",EncryptionUtil.encryptionMD5(MineApp.sContext, getSign(MineApp.sContext, MineApp.sContext.getPackageName()), 0));

        // 新的请求
        Request newRequest = oldRequest.newBuilder().method(oldRequest.method(), oldRequest.body()).url
                (authorizedUrlBuilder.build()).build();

        EncryptionUtil.encryptionMD5(MineApp.sContext, getSign(MineApp.sContext, MineApp.sContext.getPackageName()), 0);
        Log.e("CommonInterceptor",newRequest.body().toString());
        return chain.proceed(newRequest);
    }

    /**
     * 获取应用签名
     *
     * @param context
     * @param packageName
     * @return
     */
    public static String getSign(Context context, String packageName) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            byte[] bytes = info.signatures[0].toByteArray();
            return byte2hex(bytes).toLowerCase(Locale.ROOT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取签名的MD5值
     *
     * @param context
     * @param packageName
     * @return
     */
    public static String getSignMD5(Context context, String packageName) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            byte[] bytes = info.signatures[0].toByteArray();
            return encryptionMD5(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String encryptionMD5(byte[] byteStr) {
        MessageDigest messageDigest = null;
        StringBuffer md5StrBuff = new StringBuffer();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(byteStr);
            byte[] byteArray = messageDigest.digest();
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5StrBuff.toString();
    }

    private static String byte2hex(byte[] bytes) {
        String hs = "";
        String tmp = "";
        for (int i = 0; i < bytes.length; i++) {
            tmp = (Integer.toHexString(bytes[i] & 0XFF));
            if (tmp.length() == 1)
                hs = hs + "0" + tmp;
            else
                hs = hs + tmp;
        }
        return hs.toUpperCase();
    }
}
