package com.zb.lib_base.http;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.model.BaseResultEntity;
import com.zb.lib_base.utils.SCToastUtil;

import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import androidx.appcompat.app.AppCompatActivity;
import rx.Subscriber;

/**
 * 用于在Http请求开始时，自动显示一个ProgressDialog
 * 在Http请求结束是，关闭ProgressDialog
 * 调用者自己对请求数据进行处理
 * Created by WZG on 2016/7/16.
 */
public class ProgressSubscriber<T> extends Subscriber<T> {
    /*是否弹框*/
    private boolean showProgressAndCancel = true;
    //    回调接口
    private HttpOnNextListener mSubscriberOnNextListener;
    //    弱引用反正内存泄露
    private WeakReference<AppCompatActivity> mActivity;

    private String dialogTitle = "正在加载中";
    private CallBack mCallBack;

    public interface CallBack {
        void error();
    }

    /**
     * 初始化
     *
     * @param mSubscriberOnNextListener
     * @param context
     * @param showProgressAndCancel     是否需要加载框,是否能取消加载框
     */
    public ProgressSubscriber(HttpOnNextListener mSubscriberOnNextListener, AppCompatActivity context, boolean showProgressAndCancel,
                              String dialogTitle, CallBack callBack) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.mActivity = new WeakReference<>(context);
        this.showProgressAndCancel = showProgressAndCancel;
        mCallBack = callBack;
        setShowProgressAndCancel(showProgressAndCancel);
        setDialogTitle(dialogTitle);
    }

    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }


    /**
     * 隐藏
     */
    private void dismissProgressDialog() {
        CustomProgressDialog.stopLoading();
    }

    /**
     * 订阅开始时调用
     * 显示ProgressDialog
     */
    @Override
    public void onStart() {
        CustomProgressDialog.showLoading(mActivity.get(), dialogTitle, showProgressAndCancel);
    }

    /**
     * 完成，隐藏ProgressDialog
     */
    @Override
    public void onCompleted() {
        dismissProgressDialog();
    }

    /**
     * 对错误进行统一处理
     * 隐藏ProgressDialog
     *
     * @param e
     */
    @Override
    public void onError(Throwable e) {
        AppCompatActivity context = mActivity.get();
        if (context == null) {
            return;
        }
        if (e instanceof SocketTimeoutException) {
            if (mCallBack != null)
                mCallBack.error();
        } else if (e instanceof ConnectException) {
            if (mCallBack != null)
                mCallBack.error();
        } else if (e instanceof HttpTimeException) {
            HttpTimeException exception = (HttpTimeException) e;
            switch (exception.getCode()) {
                case HttpTimeException.ERROR:
                    SCToastUtil.showToast(context, e.getMessage());
                    break;
                default:
                    break;
            }
        }

        dismissProgressDialog();
        if (mSubscriberOnNextListener != null) {
            mSubscriberOnNextListener.onError(e);
        }
    }

    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param t 创建Subscriber时的泛型类型
     */
    @Override
    public void onNext(T t) {
        BaseResultEntity<T> httpResult = (BaseResultEntity<T>) t;
        Context context = mActivity.get();
        //未登录
        if (httpResult.getCode() == HttpTimeException.NOT_LOGIN) {

            try {
                if (TextUtils.equals("", BaseActivity.sessionId)) {
                    Intent intent = new Intent(context, Class.forName("com.yimi.rentme.ui.activity.login_register.LoginActivity"));
                    ((Activity) context).startActivityForResult(intent, HttpTimeException.NOT_LOGIN);
                } else {
                    Intent data = new Intent("rentme_logout");
                    data.putExtra("logoutType", 2);
                    context.sendBroadcast(data);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            throw new HttpTimeException(httpResult.getCode());
        }

        //请求失败
        if (httpResult.getCode() == HttpTimeException.ERROR ||
                httpResult.getCode() == HttpTimeException.NOT_BIND_PHONE ||
                httpResult.getCode() == HttpTimeException.SINGLE_IMAGE ||
                httpResult.getCode() == HttpTimeException.OPENVIP) {
            throw new HttpTimeException(httpResult.getMsg());
        }

        if (httpResult.getCode() == 1) {
            if (mSubscriberOnNextListener != null) {
                mSubscriberOnNextListener.onNext(httpResult.getData());
            }
        } else {
            throw new HttpTimeException(httpResult.getCode());
        }
    }

    /**
     * 取消ProgressDialog的时候，取消对observable的订阅，同时也取消了http请求
     */
    public void onCancelProgress() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }

    public boolean isShowProgressAndCancel() {
        return showProgressAndCancel;
    }

    public void setShowProgressAndCancel(boolean showProgressAndCancel) {
        this.showProgressAndCancel = showProgressAndCancel;
    }
}