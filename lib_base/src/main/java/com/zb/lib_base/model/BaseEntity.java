package com.zb.lib_base.model;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpService;
import com.zb.lib_base.http.HttpTimeException;

import rx.Observable;
import rx.functions.Func1;

/**
 * 请求数据统一封装类
 * Created by WZG on 2016/7/16.
 */
public abstract class BaseEntity<T> implements Func1<BaseResultEntity<T>, T> {
    //    rx生命周期管理
    private RxAppCompatActivity rxAppCompatActivity;
    /*回调*/
    private HttpOnNextListener listener;
    /*是否显示加载框*/
    private boolean showProgress;

    private String dialogTitle;


    public BaseEntity(HttpOnNextListener listener, RxAppCompatActivity rxAppCompatActivity) {
        setListener(listener);
        setRxAppCompatActivity(rxAppCompatActivity);
        setShowProgress(true);
        setDialogTitle("正在加载中....");
    }

    /**
     * 设置参数
     *
     * @param methods
     * @return
     */
    public abstract Observable getObservable(HttpService methods);

    public void setRxAppCompatActivity(RxAppCompatActivity rxAppCompatActivity) {
        this.rxAppCompatActivity = rxAppCompatActivity;
    }

    public boolean isShowProgress() {
        return showProgress;
    }

    public void setShowProgress(boolean showProgress) {
        this.showProgress = showProgress;
    }

    public String getDialogTitle() {
        return dialogTitle;
    }

    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    public HttpOnNextListener getListener() {
        return listener;
    }

    public void setListener(HttpOnNextListener listener) {
        this.listener = listener;
    }

    /*
     * 获取当前rx生命周期
     * @return
     */
    public RxAppCompatActivity getRxAppCompatActivity() {
        return rxAppCompatActivity;
    }


    @Override
    public T call(BaseResultEntity<T> httpResult) {
        //未登录
        if (httpResult.getCode() == HttpTimeException.NOT_LOGIN) {
//            if (TextUtils.equals("", BaseActivity.sessionId) && rxAppCompatActivity != null) {
//                rxAppCompatActivity.sendBroadcast(new Intent("mdc_rule"));
//            } else {
//                Intent intent = new Intent("mdc_logout");
//                intent.putExtra("type", 2);
//                rxAppCompatActivity.sendBroadcast(intent);
//            }
            throw new HttpTimeException(httpResult.getCode());
        }

        if (httpResult.getCode() == 1) {
            return httpResult.getData();
        }  // 成功

        //请求失败
        if (httpResult.getCode() == HttpTimeException.ERROR) {
            if (httpResult.getMsg().isEmpty()) {
                return (T) httpResult.getMsg();
            } else
                throw new HttpTimeException(httpResult.getMsg());
        } else throw new HttpTimeException(httpResult.getCode());
    }
}
