package com.zb.module_mine.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.contactNumApi;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.ContactNum;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.BR;
import com.zb.module_mine.iv.MineVMInterface;

import androidx.databinding.ViewDataBinding;

public class MineViewModel extends BaseViewModel implements MineVMInterface {

    public MineInfo mineInfo;
    public ContactNum contactNum;
    private BaseReceiver updateMineInfoReceiver;
    private BaseReceiver newsCountReceiver;
    private BaseReceiver openVipReceiver;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mineInfo = mineInfoDb.getMineInfo();

        updateMineInfoReceiver = new BaseReceiver(activity, "lobster_updateMineInfo") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mineInfo = mineInfoDb.getMineInfo();
                mBinding.setVariable(BR.viewModel, MineViewModel.this);
            }
        };
        newsCountReceiver = new BaseReceiver(activity, "lobster_newsCount") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.setVariable(BR.mineNewsCount, MineApp.mineNewsCount);
            }
        };

        // 开通会员
        openVipReceiver = new BaseReceiver(activity, "lobster_openVip") {
            @Override
            public void onReceive(Context context, Intent intent) {
                myInfo();
            }
        };
    }

    public void onDestroy() {
        updateMineInfoReceiver.unregisterReceiver();
        newsCountReceiver.unregisterReceiver();
        openVipReceiver.unregisterReceiver();
    }

    @Override
    public void publishDiscover(View view) {
        ActivityUtils.getHomePublishImage();
    }

    @Override
    public void openVip(View view) {
        ActivityUtils.getMineOpenVip();
    }

    @Override
    public void toEditMember(View view) {
        ActivityUtils.getMineEditMember();
    }

    @Override
    public void toNews(View view) {
        ActivityUtils.getMineNewsManager();
    }

    @Override
    public void toSetting(View view) {
        ActivityUtils.getMineSetting();
    }

    @Override
    public void contactNumDetail(int position) {
        if (position == 2) {
            if (mineInfo.getMemberType() == 2) {
                ActivityUtils.getMineFCL(2);
                return;
            }
            SCToastUtil.showToastBlack(activity, "查看被喜欢的人为VIP用户专享功能");
        } else {
            ActivityUtils.getMineFCL(position);
        }
    }

    @Override
    public void contactNum() {
        contactNumApi api = new contactNumApi(new HttpOnNextListener<ContactNum>() {
            @Override
            public void onNext(ContactNum o) {
                contactNum = o;
                mBinding.setVariable(BR.hasNewBeLike, PreferenceUtil.readIntValue(activity, "beLikeCount") > o.getBeLikeCount());
                PreferenceUtil.saveIntValue(activity, "beLikeCount", o.getBeLikeCount());
                mBinding.setVariable(BR.viewModel, MineViewModel.this);

            }
        }, activity).setOtherUserId(mineInfo.getUserId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void myInfo() {
        myInfoApi api = new myInfoApi(new HttpOnNextListener<MineInfo>() {
            @Override
            public void onNext(MineInfo o) {
                mineInfo = o;
                mineInfoDb.saveMineInfo(o);
                mBinding.setVariable(BR.viewModel, MineViewModel.this);
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
