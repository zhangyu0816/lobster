package com.zb.lib_base.windows;

import android.annotation.SuppressLint;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.api.payOrderForTranApi;
import com.zb.lib_base.api.submitOpenedMemberOrderApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.OrderNumber;
import com.zb.lib_base.model.OrderTran;
import com.zb.lib_base.utils.ActivityUtils;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public abstract class BasePopupWindow extends PopupWindow {

    public RxAppCompatActivity activity;
    public ViewDataBinding mBinding;

    @SuppressLint("ClickableViewAccessibility")
    public BasePopupWindow(View parentView, boolean canClick) {
        this.activity = (RxAppCompatActivity) parentView.getContext();
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), getRes(), null, false);
        View view = mBinding.getRoot();
        setWidth(LinearLayout.LayoutParams.FILL_PARENT);
        setHeight(LinearLayout.LayoutParams.FILL_PARENT);
        setBackgroundDrawable(new BitmapDrawable());
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(view);
        showAtLocation(parentView, Gravity.CENTER, 0, 0);
        update();
        if (canClick)
            view.setOnTouchListener((v, event) -> {
                if (isShowing()) {
                    dismiss();
                }
                return false;
            });
    }

    @SuppressLint("ClickableViewAccessibility")
    public BasePopupWindow(RxAppCompatActivity activity, View parentView, boolean canClick) {
        this.activity = activity;
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), getRes(), null, false);
        View view = mBinding.getRoot();
        setWidth(LinearLayout.LayoutParams.FILL_PARENT);
        setHeight(LinearLayout.LayoutParams.FILL_PARENT);
        setBackgroundDrawable(new BitmapDrawable());
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(view);
        showAtLocation(parentView, Gravity.CENTER, 0, 0);
        update();
        if (canClick)
            view.setOnTouchListener((v, event) -> {
                if (isShowing()) {
                    dismiss();
                }
                return false;
            });
    }

    public abstract int getRes();

    public abstract void initUI();

    /**
     * ??????
     */
    public void cancel(View view) {
    }

    /**
     * ??????
     */
    public void sure(View view) {
    }

    /**
     * ??????
     */
    public void selectIndex(int position) {
    }

    /**
     * ??????
     */
    public void recharge(View view) {
    }

    /**
     * ????????????
     */
    public void payGift(View view) {
    }

    /**
     * ????????????
     */
    public void showRule(View view) {
        ActivityUtils.getMineWeb("??????????????????", HttpManager.BASE_URL + "mobile/xiagu_recharge_protocol.html");
    }

    public void changeImage(View view) {
    }

    /**
     * ??????VIP??????
     */
    public void submitOpenedMemberOrder(long memberOfOpenedProductId, CallBack callBack) {
        submitOpenedMemberOrderApi api = new submitOpenedMemberOrderApi(new HttpOnNextListener<OrderNumber>() {
            @Override
            public void onNext(OrderNumber o) {
                MineApp.pfAppType = "203";
                payOrderForTran(o.getOrderNumber(), 1, callBack);
            }

            @Override
            public void onError(Throwable e) {
                MineApp.pfAppType = "203";
            }
        }, activity)
                .setMemberOfOpenedProductId(memberOfOpenedProductId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    /**
     * ?????????????????????
     */
    public void payOrderForTran(String orderNumber, int payType, CallBack callBack) {
        payOrderForTranApi api = new payOrderForTranApi(new HttpOnNextListener<OrderTran>() {
            @Override
            public void onNext(OrderTran o) {
                dismiss();
                if (callBack != null)
                    callBack.canDismiss();
                new PaymentPW(mBinding.getRoot(), o, payType);
            }
        }, activity).setOrderNumber(orderNumber);
        HttpManager.getInstance().doHttpDeal(api);
    }

    public interface CallBack {
        void canDismiss();
    }

}
