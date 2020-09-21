package com.zb.lib_base.http;

import com.lidroid.xutils.HttpUtils;

/**
 * Created by DIY on 2018-01-20.
 */

public class DownLoadRetrofitHelper {

    /**
     * 服务器交互客户端
     */
    public static HttpUtils httpClient;

    static {
        // 设置请求超时时间为20秒
        httpClient = new HttpUtils(1000 * 10);
        httpClient.configSoTimeout(1000 * 20);
        httpClient.configResponseTextCharset("UTF-8");
    }

}
