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
        mBinding.setTitle("我的收益");
        mBinding.setRight("规则");
        mBinding.setNoData(true);
        setAdapter();
        statisticsRewardsCount();
        openVipReceiver = new BaseReceiver(activity, "lobster_openVip") {
            @Override
            public void onReceive(Context context, Intent intent) {
                new TextPW(activity, mBinding.getRoot(), MineApp.loveMineInfo.getMemberType() == 2 ? "恭喜您成功续费！" : "恭喜您成功入驻！", "您已成为地摊主，分享盲盒赚分佣！", "分享盲盒", true, () -> toLoveShare(null));
                myInfo();
            }
        };
        mBinding.setOpenTime(MineApp.loveMineInfo.getMemberExpireTime());
        mBinding.setOpenBtn(MineApp.loveMineInfo.getMemberType() == 2 ? "续费权限" : "开通权限");
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void right(View view) {
        super.right(view);
        new TextPW(activity, mBinding.getRoot(), "爱情盲盒摊主规则", "一，玩法规则：\n" +
                "1，花费一元把你的微信号存入盲盒中，等待异性用户拆盲盒。\n" +
                "2，花费一元取出一个异性微信号盲盒。\n" +
                "\n" +
                "二，地摊主赚钱：\n" +
                "1，开通地摊主功能，拥有自己的摊位，\n" +
                "2，所有通过你分享的小程序，二维码，链接等的用户存取微信号，你都可以抽取税前7成的佣金，佣金可以直接提现到微信账号。\n" +
                "3，地摊主到期后请及时续费。否则将不再享受分佣抽成。", "确认", true, null);
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
        //兼容低版本的网页链接
        UMMin umMin = new UMMin("https://xgapi.zuwo.la");
        UMImage umImage = new UMImage(activity, R.drawable.ic_min_logo);
        umImage.compressStyle = UMImage.CompressStyle.SCALE;//大小压缩，默认为大小压缩，适合普通很大的图
        umImage.compressFormat = Bitmap.CompressFormat.PNG;
        // 小程序消息封面图片
        umMin.setThumb(umImage);
        // 小程序消息title
        umMin.setTitle("爱情盲盒,缘分来了");
        // 小程序消息描述
        umMin.setDescription("邂逅最真实的ta");
        //小程序页面路径
        umMin.setPath("pages/index/index?userId=" + BaseActivity.userId);
        // 小程序原始id,在微信平台查询
        umMin.setUserName("gh_dd74e49d61df");

        new ShareAction(activity)
                .withMedia(umMin)
                .setPlatform(SHARE_MEDIA.WEIXIN)
                .setCallback(umShareListener).share();
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            SCToastUtil.showToast(activity, "小程序开始分享", true);
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            SCToastUtil.showToast(activity, "小程序分享成功啦", true);
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            SCToastUtil.showToast(activity, "小程序分享失败啦", true);
            if (t != null) {
                Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            SCToastUtil.showToast(activity, "小程序分享取消了", true);
        }
    };
}
