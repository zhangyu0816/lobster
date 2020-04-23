package com.zb.lib_base.windows;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.app.abby.xbanner.Ads;
import com.app.abby.xbanner.XBanner;
import com.zb.lib_base.BR;
import com.zb.lib_base.R;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.adapter.BaseAdapter;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.databinding.PwsVipAdBinding;
import com.zb.lib_base.model.VipAd;
import com.zb.lib_base.utils.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class VipAdPW extends BasePopupWindow {
    private List<VipAd> vipAdList = new ArrayList<>();
    private List<Ads> adsList = new ArrayList<>();
    private BaseAdapter adapter;
    private int preIndex = -1;
    private PwsVipAdBinding binding;

    public VipAdPW(AppCompatActivity activity, View parentView) {
        super(activity, parentView, true);
        for (int i = 0; i < 4; i++) {
            VipAd vipAd = new VipAd();
            Ads ads = new Ads();
            if (i == 0) {
                vipAd.setTopRes(R.mipmap.vip_ad_1);
                vipAd.setTitle("超级曝光");
                vipAd.setContent("增加10倍曝光度，让更多人先发现你");
                ads.setAdRes(R.mipmap.vip_ad_1);
            } else if (i == 1) {
                vipAd.setTopRes(R.mipmap.vip_ad_2);
                vipAd.setTitle("划错反悔");
                vipAd.setContent("手滑了？不要错过任何一个缘分");
                ads.setAdRes(R.mipmap.vip_ad_2);
            } else if (i == 2) {
                vipAd.setTopRes(R.mipmap.vip_ad_3);
                vipAd.setTitle("超级喜欢");
                vipAd.setContent("每天10个超级喜欢，开通专属私信通道");
                ads.setAdRes(R.mipmap.vip_ad_3);
            } else {
                vipAd.setTopRes(R.mipmap.vip_ad_4);
                vipAd.setTitle("谁喜欢我");
                vipAd.setContent("第一时间查看喜欢你的人！立即匹配哦~");
                ads.setAdRes(R.mipmap.vip_ad_4);
            }
            vipAdList.add(vipAd);
            adsList.add(ads);
        }
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_vip_ad;
    }

    @Override
    public void initUI() {
        adapter = new BaseAdapter<>(activity, R.layout.item_vip_ad, MineApp.vipInfoList, this);
        mBinding.setVariable(BR.pw, this);

        mBinding.setVariable(BR.adapter, adapter);

        binding = (PwsVipAdBinding) mBinding;

        AdapterBinding.viewSize(binding.bannerLinear, ObjectUtils.getViewSizeByWidthFromMax(900), (int) (ObjectUtils.getViewSizeByWidthFromMax(900) * 579f / 810f));
        AdapterBinding.viewSize(binding.banner, ObjectUtils.getViewSizeByWidthFromMax(900), (int) (ObjectUtils.getViewSizeByWidthFromMax(900) * 579f / 810f));

        binding.banner.setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setAds(adsList)
                .setImageLoader((context, ads, image, position) -> AdapterBinding.loadImage(image, "", ads.getAdRes(),
                        ObjectUtils.getDefaultRes(), ObjectUtils.getViewSizeByWidthFromMax(900), (int) (ObjectUtils.getViewSizeByWidthFromMax(900) * 579f / 810f),
                        false, false, 0, false, 0))
                .setBannerTypes(XBanner.CIRCLE_INDICATOR_TITLE)
                .setIndicatorGravity(XBanner.INDICATOR_START)
                .setDelay(5000)
                .setUpIndicators(R.drawable.banner_circle_pressed, R.drawable.banner_circle_unpressed)
                .setUpIndicatorSize(20, 20)
                .isAutoPlay(true)
                .start();

        handler.sendEmptyMessage(1);
    }

    private int position = 0;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                mBinding.setVariable(BR.vipAd, vipAdList.get(position % 4));
                binding.banner.setBannerBg(position % 4);
                position++;
                binding.banner.postDelayed(() -> handler.sendEmptyMessage(1), 5000);
            }
            return false;
        }
    });

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
    public void sure(View view) {
        super.sure(view);
        dismiss();
    }
}
