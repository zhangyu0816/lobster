package com.zb.lib_base.http;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.utils.SCToastUtil;

import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

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
    private WeakReference<RxAppCompatActivity> mActivity;

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
    public ProgressSubscriber(HttpOnNextListener mSubscriberOnNextListener, RxAppCompatActivity context, boolean showProgressAndCancel,
                              String dialogTitle, CallBack callBack) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.mActivity = new WeakReference<>(context);
        this.showProgressAndCancel = showProgressAndCancel;
        mCallBack = callBack;
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
        onCancelProgress();
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
        RxAppCompatActivity context = mActivity.get();
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
        if (mSubscriberOnNextListener != null) {
            mSubscriberOnNextListener.onNext(t);
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
}