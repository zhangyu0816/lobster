package com.zb.lib_base.http;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.gson.Gson;
import com.zb.lib.EncryptionUtil;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import androidx.annotation.NonNull;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * Created by ZhengQunWei
 * on 2017/7/6
 */
public class CommonInterceptor implements Interceptor {


    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        String method = oldRequest.method();
        // 签名参数
        TreeMap<String, String> treeMap = new TreeMap<>();
        String params = "";
        // 【请求参数】
        if ("POST".equalsIgnoreCase(method)) {
            if (oldRequest.body() instanceof FormBody) {// Form 提交
                FormBody body = (FormBody) oldRequest.body();
                // 数组参数
                Map<String, ArrayList<Object>> arrays = new HashMap<>();
                Map<String, Object> map = new HashMap<>();
                // 解析参数
                for (int i = 0; i < body.size(); i++) {
                    String key = body.name(i);
                    String value = body.value(i);
                    if (key.endsWith("[]")) {// 参数是数组
                        String realKey = key.substring(0, key.length() - 2);
                        ArrayList<Object> list;
                        if (arrays.containsKey(realKey)) {
                            list = arrays.get(realKey);
                        } else {
                            list = new ArrayList<>();
                            arrays.put(realKey, list);
                        }
                        list.add(value);
                    } else {// 其他
                        params += (params.isEmpty() ? "" : ", ") + key + " = " + value;
                        treeMap.put(key, value);
                        map.put(key, value);
                    }
                }
                // 如果参数包含数组
                if (!arrays.isEmpty()) {
                    for (String arrayKey : arrays.keySet()) {
                        String arrayParams = "";
                        for (Object value : arrays.get(arrayKey)) {
                            arrayParams += (arrayParams.isEmpty() ? "" : ", ") + value;
                        }
                        params += (params.isEmpty() ? "" : ", ") + arrayKey + " = [" + arrayParams + "]";
                        map.put(arrayKey, arrays.get(arrayKey));
                        treeMap.put(arrayKey, new Gson().toJson(arrays.get(arrayKey)));
                    }
                }
                params = new JSONObject(map).toString();
            } else if (oldRequest.body() instanceof MultipartBody) {
                MultipartBody body = (MultipartBody) oldRequest.body();
                List<MultipartBody.Part> parts = body.parts();
            } else {// 其他提交
                RequestBody body = oldRequest.body();
                Buffer buffer = new Buffer();
                body.writeTo(buffer);

                Charset charset = StandardCharsets.UTF_8;
                MediaType contentType = body.contentType();
                if (contentType != null) {
                    charset = contentType.charset(StandardCharsets.UTF_8);
                }
                params = buffer.readString(charset);
            }
        }

        treeMap.put("pfDevice", "Android");
        treeMap.put("pfAppType", MineApp.pfAppType);
        treeMap.put("pfAppVersion", MineApp.versionName);
        treeMap.put("fullVersion", MineApp.versionName + ".1");
        treeMap.put("userId", BaseActivity.userId + "");
        treeMap.put("sessionId", BaseActivity.sessionId);

        params = new JSONObject(treeMap).toString().replace("\\","");
        Log.e("CommonInterceptor", params);

        // 添加新的参数
        HttpUrl.Builder authorizedUrlBuilder = oldRequest.url().newBuilder().scheme(oldRequest.url().scheme()).host
                (oldRequest.url().host())
                .addQueryParameter("pfDevice", "Android")
                .addQueryParameter("pfAppType", MineApp.pfAppType)
                .addQueryParameter("pfAppVersion", MineApp.versionName)
                .addQueryParameter("fullVersion", MineApp.versionName + ".1")
                .addQueryParameter("userId", BaseActivity.userId + "")
                .addQueryParameter("sessionId", BaseActivity.sessionId)
                .addQueryParameter("androidSign", EncryptionUtil.encryptionMD5(MineApp.sContext, params, 0));

        // 新的请求
        Request newRequest = oldRequest.newBuilder().method(oldRequest.method(), oldRequest.body()).url
                (authorizedUrlBuilder.build()).build();
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
