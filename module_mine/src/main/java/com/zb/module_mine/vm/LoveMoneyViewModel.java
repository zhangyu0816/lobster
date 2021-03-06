package com.zb.module_mine.vm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMMin;
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
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.TextPW;
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
        mBinding.setTitle("????????????");
        mBinding.setRight("??????");
        mBinding.setNoData(true);
        setAdapter();
        statisticsRewardsCount();

        openVipReceiver = new BaseReceiver(activity, "lobster_openVip") {
            @Override
            public void onReceive(Context context, Intent intent) {
                new TextPW(activity, mBinding.getRoot(), MineApp.loveMineInfo.getMemberType() == 2 ? "????????????????????????" : "????????????????????????", "????????????????????????????????????????????????", "????????????", true, () -> toLoveShare(null));
                myInfo();
            }
        };
        mBinding.setOpenTime(MineApp.loveMineInfo.getMemberType() == 1 ? "???????????????" : DateUtil.strToStr(MineApp.loveMineInfo.getMemberExpireTime(), DateUtil.yyyy_MM_dd));
        mBinding.setOpenLove(MineApp.loveMineInfo.getMemberType() == 2 ? "??????>" : "?????????>");
        mBinding.setLoveBtn(MineApp.loveMineInfo.getMemberType() == 1 ? "????????????" : "???????????????");
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void right(View view) {
        super.right(view);
        ActivityUtils.getMineWeb("????????????????????????", HttpManager.BASE_URL + "mobile/aqmh/aqmh_get_help.html");
    }

    public void onResume() {
        myWeChatIsBind();
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
        req.userName = "gh_dd74e49d61df"; // ??????????????????id
        req.path = "pages/user/bindaqmh?userId=" + BaseActivity.userId + "&sessionId=" + BaseActivity.sessionId;                  //???????????????????????????????????????????????????????????????????????????
        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// ???????????? ?????????????????????????????????
        api.sendReq(req);
    }

    public void payVip(View view) {
        new OpenLovePW(mBinding.getRoot()).setVipInfoList(MineApp.loveInfoList).initUI();
    }

    public void openLove(View view) {
        if (MineApp.loveMineInfo.getMemberType() == 1)
            new OpenLovePW(mBinding.getRoot()).setVipInfoList(MineApp.loveInfoList).initUI();
        else
            toLoveShare(null);
    }

    private void statisticsRewardsCount() {
        statisticsRewardsCountApi api = new statisticsRewardsCountApi(new HttpOnNextListener<PersonInfo>() {
            @Override
            public void onNext(PersonInfo o) {
                mBinding.setMoney(o.getTotalRewards());
            }
        }, activity);
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
                mBinding.setOpenTime(MineApp.loveMineInfo.getMemberExpireTime());
            }

            @Override
            public void onError(Throwable e) {
                MineApp.pfAppType = "203";
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    public void toLoveShare(View view) {
        //??????????????????????????????
        UMMin umMin = new UMMin("https://xgapi.zuwo.la");
        UMImage umImage = new UMImage(activity, R.drawable.ic_min_logo);
        umImage.compressStyle = UMImage.CompressStyle.SCALE;//???????????????????????????????????????????????????????????????
        umImage.compressFormat = Bitmap.CompressFormat.PNG;
        // ???????????????????????????
        umMin.setThumb(umImage);
        // ???????????????title
        umMin.setTitle("????????????,????????????");
        // ?????????????????????
        umMin.setDescription("??????????????????ta");
        //?????????????????????
        umMin.setPath("pages/index/index?userId=" + BaseActivity.userId);
        // ???????????????id,?????????????????????
        umMin.setUserName("gh_dd74e49d61df");

        new ShareAction(activity)
                .withMedia(umMin)
                .setPlatform(SHARE_MEDIA.WEIXIN)
                .setCallback(umShareListener).share();
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            SCToastUtil.showToast(activity, "?????????????????????", true);
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            SCToastUtil.showToast(activity, "????????????????????????", true);
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            SCToastUtil.showToast(activity, "????????????????????????", true);
            if (t != null) {
                Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            SCToastUtil.showToast(activity, "????????????????????????", true);
        }
    };
}
