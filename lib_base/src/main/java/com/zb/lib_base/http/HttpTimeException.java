package com.zb.lib_base.http;

/**
 * 自定义错误信息，统一处理返回处理
 * Created by WZG on 2016/7/16.
 */
public class HttpTimeException extends RuntimeException {

    private int code;

    public static final int NO_DATA = -1;// 没有数据

    public static final int ERROR = 0;

    public static final int NOT_LOGIN = 2;// 没有登录

    public static final int SERVER_ERROR = 4;// 服务器连接失败

    public static final int SINGLE_IMAGE = 7;// 个人形象图

    public static final int NOT_BIND_PHONE = 8;// 手机未绑定

    public static final int OPENVIP = 10;// 开通会员

    public int getCode() {
        return code;
    }

    public HttpTimeException(int resultCode) {
        super(getApiExceptionMessage(resultCode));
        this.code = resultCode;
    }

    public HttpTimeException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * 转换错误数据
     *
     * @param code
     * @return
     */
    private static String getApiExceptionMessage(int code) {
        String message = "";
        switch (code) {
            case NO_DATA:
                message = "无数据";
                break;
            case NOT_LOGIN:
                message = "请先登录";
                break;
            case NOT_BIND_PHONE:
                message = "手机未绑定";
                break;
            default:
                message = "error";
                break;

        }
        return message;
    }
}

