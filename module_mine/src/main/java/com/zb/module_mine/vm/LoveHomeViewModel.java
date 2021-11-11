package com.zb.module_mine.vm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMMin;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.myInfoApi;
import com.zb.lib_base.api.openedMemberPriceListApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.VipInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.TextPW;
import com.zb.module_mine.R;
import com.zb.module_mine.databinding.AcLoveHomeBinding;
import com.zb.module_mine.windows.OpenLovePW;

import java.util.List;

import androidx.databinding.ViewDataBinding;

public class LoveHomeViewModel extends BaseViewModel {
    private AcLoveHomeBinding mBinding;
    private BaseReceiver openVipReceiver;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (AcLoveHomeBinding) binding;
        goAnimator(mBinding.ivLogoWen, 0.8f, 1.0f, 800L);
        goAnimator(mBinding.ivLogoXin, 0.8f, 1.0f, 800L);
        openVipReceiver = new BaseReceiver(activity, "lobster_openVip") {
            @Override
            public void onReceive(Context context, Intent intent) {
                new TextPW(activity, mBinding.getRoot(), MineApp.loveMineInfo.getMemberType() == 2 ? "恭喜您成功续费！" : "恭喜您成功入驻！", "您已成为地摊主，分享盲盒赚分佣！", "分享盲盒", true, () -> toLoveShare(null));
                myInfo();
            }
        };
        myInfo();
        openedMemberPriceList();
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

    public void toLoveMoney(View view) {
        if (MineApp.loveMineInfo.getMemberType() == 1) {
            SCToastUtil.showToast(activity, "成为地摊主后才有收益哦", true);
            return;
        }
        ActivityUtils.getLoveMoney();
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

    public void onSave(View view) {
        ActivityUtils.getLoveSave();
    }

    public void onGet(View view) {
        ActivityUtils.getLoveGet();
    }

    public void onOpen(View view) {
        new OpenLovePW(mBinding.getRoot()).setVipInfoList(MineApp.loveInfoList).initUI();
    }

    private void openedMemberPriceList() {
        MineApp.pfAppType = "205";
        openedMemberPriceListApi api = new openedMemberPriceListApi(new HttpOnNextListener<List<VipInfo>>() {
            @Override
            public void onNext(List<VipInfo> o) {
                MineApp.loveInfoList.clear();
                MineApp.loveInfoList = o;
                MineApp.pfAppType = "203";
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                MineApp.pfAppType = "203";
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
