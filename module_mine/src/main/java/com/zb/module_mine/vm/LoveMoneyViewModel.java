package com.zb.module_mine.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.api.myWeChatIsBindApi;
import com.zb.lib_base.api.rewardsOrderListApi;
import com.zb.lib_base.api.statisticsRewardsCountApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.LoveMoney;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.PersonInfo;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.databinding.AcLoveMoneyBinding;
import com.zb.module_mine.windows.OpenLovePW;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

public class LoveMoneyViewModel extends BaseViewModel implements OnRefreshListener, OnLoadMoreListener {
    private AcLoveMoneyBinding mBinding;
    public MineAdapter<LoveMoney> adapter;
    private int pageNo = 1;
    private List<LoveMoney> loveMoneyList = new ArrayList<>();
    private BaseReceiver openVipReceiver;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (AcLoveMoneyBinding) binding;
        mBinding.setTitle("我的收益");
        mBinding.setNoData(true);
        setAdapter();
        statisticsRewardsCount();
        openVipReceiver = new BaseReceiver(activity, "lobster_openVip") {
            @Override
            public void onReceive(Context context, Intent intent) {
                myInfo();
            }
        };
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            openVipReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onResume() {
        myWeChatIsBind();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        pageNo++;
        rewardsOrderList();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mBinding.refresh.setEnableLoadMore(true);
        pageNo = 1;
        loveMoneyList.clear();
        adapter.notifyDataSetChanged();
        rewardsOrderList();
    }

    @Override
    public void setAdapter() {
        adapter = new MineAdapter<>(activity, R.layout.item_love_money_reward, loveMoneyList, this);
        rewardsOrderList();
    }

    public void withdraw(View view) {
        IWXAPI api = WXAPIFactory.createWXAPI(activity, "wx95d8f6b30546af30");
        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = "gh_dd74e49d61df"; // 填小程序原始id
        req.path = "pages/user/bindaqmh?userId=" + BaseActivity.userId + "&sessionId=" + BaseActivity.sessionId;                  //拉起小程序页面的可带参路径，不填默认拉起小程序首页
        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
        api.sendReq(req);
    }

    public void openLove(View view) {
        new OpenLovePW(mBinding.getRoot()).setVipInfoList(MineApp.loveInfoList).initUI();
    }

    private void statisticsRewardsCount() {
        statisticsRewardsCountApi api = new statisticsRewardsCountApi(new HttpOnNextListener<PersonInfo>() {
            @Override
            public void onNext(PersonInfo o) {
                mBinding.setMoney(o.getTotalRewards());
            }
        }, activity).setTranStatusType(200);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void rewardsOrderList() {
        rewardsOrderListApi api = new rewardsOrderListApi(new HttpOnNextListener<List<LoveMoney>>() {
            @Override
            public void onNext(List<LoveMoney> o) {
                int start = loveMoneyList.size();
                loveMoneyList.addAll(o);
                adapter.notifyItemRangeChanged(start, loveMoneyList.size());
                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();
                mBinding.setNoData(false);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.refresh.setEnableLoadMore(false);
                    mBinding.refresh.finishRefresh();
                    mBinding.refresh.finishLoadMore();
                }
                mBinding.setNoData(loveMoneyList.size() == 0);
            }
        }, activity).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void myWeChatIsBind() {
        myWeChatIsBindApi api = new myWeChatIsBindApi(new HttpOnNextListener<PersonInfo>() {
            @Override
            public void onNext(PersonInfo o) {
                mBinding.tvWx.setVisibility(o.getIsBindWxMiniAppAqxg() == 0 ? View.VISIBLE : View.GONE);
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void myInfo() {
        MineApp.pfAppType = "205";
        myInfoApi api = new myInfoApi(new HttpOnNextListener<MineInfo>() {
            @Override
            public void onNext(MineInfo o) {
                MineApp.loveMineInfo = o;
                MineApp.pfAppType = "203";
            }

            @Override
            public void onError(Throwable e) {
                MineApp.pfAppType = "203";
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
