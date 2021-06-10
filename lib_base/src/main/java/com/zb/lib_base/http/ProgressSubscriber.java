package com.zb.lib_base.http;

import android.text.TextUtils;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.utils.SCToastUtil;

import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import rx.Subscriber;

/**
 * 用于在Http请求开始时，自动显示一个ProgressDialog
 * 在Http请求结束是，关闭ProgressDialog
 * 调用者自己对请求数据进行处理
 * Created by WZG on 2016/7/16.
 */
public class ProgressSubscriber<T> extends Subscriber<T> {
    /*是否弹框*/
    private boolean showProgressAndCancel;
    //    回调接口
    private HttpOnNextListener mSubscriberOnNextListener;
    //    弱引用反正内存泄露
    private WeakReference<RxAppCompatActivity> mActivity;

    private String dialogTitle = "正在加载中";
    private int position = 0;

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
                              String dialogTitle) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.mActivity = new WeakReference<>(context);
        this.showProgressAndCancel = showProgressAndCancel;
        setDialogTitle(dialogTitle);
    }

    public ProgressSubscriber(HttpOnNextListener mSubscriberOnNextListener, RxAppCompatActivity context, boolean showProgressAndCancel,
                              String dialogTitle, int position) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.mActivity = new WeakReference<>(context);
        this.showProgressAndCancel = showProgressAndCancel;
        this.position = position;
        setDialogTitle(dialogTitle);
    }

    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }


    /**
     * 隐藏
     */
    private void dismissProgressDialog() {
        if (showProgressAndCancel) {
            CustomProgressDialog.stopLoading();
            onCancelProgress();
        }
    }

    /**
     * 订阅开始时调用
     * 显示ProgressDialog
     */
    @Override
    public void onStart() {
        if (showProgressAndCancel)
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
        if (e instanceof SocketTimeoutException || e instanceof ConnectException || e instanceof UnknownHostException) {
            SCToastUtil.showToast(context, "网络异常，请检查网络是否链接", position == 0);
        } else if (e instanceof HttpTimeException) {
            HttpTimeException exception = (HttpTimeException) e;
            switch (exception.getCode()) {
                case HttpTimeException.ERROR:
                case HttpTimeException.OPEN_VIP:
                    if (!TextUtils.equals(exception.getMessage(), "已经赞过了")
                            && !TextUtils.equals("已经取消过", exception.getMessage())
                            && !TextUtils.equals(exception.getMessage(), "暂时无法拾取漂流瓶哟!")
                            && !TextUtils.equals(exception.getMessage(), "获取失败")
                            && !TextUtils.equals(exception.getMessage(), "已到闪聊上限")
                            && !TextUtils.equals(exception.getMessage(), "账号尚未注册")
                            && !TextUtils.equals(exception.getMessage(), "你还没关注我啊")
                            && !TextUtils.equals(exception.getMessage(), "账号生成失败")) {
                        SCToastUtil.showToast(context, exception.getMessage(), position == 0);
                    }
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