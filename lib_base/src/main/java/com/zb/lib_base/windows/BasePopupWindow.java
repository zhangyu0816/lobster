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
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.OrderNumber;
import com.zb.lib_base.model.OrderTran;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public abstract class BasePopupWindow extends PopupWindow {

    public RxAppCompatActivity activity;
    public ViewDataBinding mBinding;

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
     * 取消
     *
     * @param view
     */
    public void cancel(View view) {
    }

    /**
     * 确认
     *
     * @param view
     */
    public void sure(View view) {
    }

    /**
     * 选中
     *
     * @param position
     */
    public void selectIndex(int position) {
    }

    /**
     * 充值
     *
     * @param view
     */
    public void recharge(View view) {
    }

    /**
     * 支付礼物
     *
     * @param view
     */
    public void payGift(View view) {
    }

    /**
     * 充值协议
     *
     * @param view
     */
    public void showRule(View view) {
    }

    /**
     * 提交VIP订单
     *
     * @param memberOfOpenedProductId
     */
    public void submitOpenedMemberOrder(long memberOfOpenedProductId) {
        submitOpenedMemberOrderApi api = new submitOpenedMemberOrderApi(new HttpOnNextListener<OrderNumber>() {
            @Override
            public void onNext(OrderNumber o) {
                payOrderForTran(o.getOrderNumber(), 1);
            }
        }, activity)
                .setMemberOfOpenedProductId(memberOfOpenedProductId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    /**
     * 获取交易订单号
     *
     * @param orderNumber
     */
    public void payOrderForTran(String orderNumber, int payType) {
        payOrderForTranApi api = new payOrderForTranApi(new HttpOnNextListener<OrderTran>() {
            @Override
            public void onNext(OrderTran o) {
                dismiss();
                new PaymentPW(activity, mBinding.getRoot(), o, payType);
            }
        }, activity).setOrderNumber(orderNumber);
        HttpManager.getInstance().doHttpDeal(api);
    }

}
