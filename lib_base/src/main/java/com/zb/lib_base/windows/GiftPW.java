package com.zb.lib_base.windows;

import android.view.View;

import com.zb.lib_base.BR;
import com.zb.lib_base.R;
import com.zb.lib_base.adapter.BaseAdapter;
import com.zb.lib_base.api.giftListApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.GiftInfo;
import com.zb.lib_base.utils.SCToastUtil;

import java.util.ArrayList;
import java.util.List;

public class GiftPW extends BasePopupWindow {

    private BaseAdapter adapter;
    private int preIndex = -1;
    private CallBack mCallBack;
    private List<GiftInfo> giftInfoList = new ArrayList<>();

    public GiftPW(View parentView, CallBack callBack) {
        super(parentView, true);
        mCallBack = callBack;
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_home_gift;
    }

    @Override
    public void initUI() {
        adapter = new BaseAdapter<>(activity, R.layout.item_home_pws_gift, giftInfoList, this);
        mBinding.setVariable(BR.pw, this);
        mBinding.setVariable(BR.walletInfo, MineApp.walletInfo);
        mBinding.setVariable(BR.adapter, adapter);
        giftList();
    }

    @Override
    public void cancel(View view) {
        super.cancel(view);
        dismiss();
    }

    @Override
    public void recharge(View view) {
        super.recharge(view);
        dismiss();
        new RechargePW(mBinding.getRoot());
    }

    @Override
    public void selectIndex(int position) {
        super.selectIndex(position);
        if (preIndex != position) {
            adapter.setSelectIndex(position);
            if (preIndex != -1) {
                adapter.notifyItemChanged(preIndex);
            }
            adapter.notifyItemChanged(position);
            preIndex = position;
        }
    }

    @Override
    public void payGift(View view) {
        super.payGift(view);
        if (MineApp.walletInfo.getWallet() < giftInfoList.get(preIndex).getPayMoney()) {
            SCToastUtil.showToast(activity, "钱包余额不足，请先充值", true);
            return;
        }
        mCallBack.selectGiftInfo(giftInfoList.get(preIndex));
        dismiss();
    }

    public interface CallBack {
        void selectGiftInfo(GiftInfo giftInfo);
    }

    private void giftList() {
        giftListApi api = new giftListApi(new HttpOnNextListener<List<GiftInfo>>() {
            @Override
            public void onNext(List<GiftInfo> o) {
                giftInfoList.addAll(o);
                adapter.notifyDataSetChanged();
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
