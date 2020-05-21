package com.zb.module_card.windows;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.app.abby.xbanner.Ads;
import com.app.abby.xbanner.ImageLoader;
import com.app.abby.xbanner.XBanner;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.model.VipAd;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.windows.BasePopupWindow;
import com.zb.module_card.BR;
import com.zb.module_card.R;
import com.zb.module_card.adapter.CardAdapter;
import com.zb.module_card.databinding.PwsVipAdBinding;

import java.util.ArrayList;
import java.util.List;

public class VipAdPW extends BasePopupWindow {
    private List<VipAd> vipAdList = new ArrayList<>();
    private List<Ads> adsList = new ArrayList<>();
    private CardAdapter adapter;
    private int preIndex = -1;
    private PwsVipAdBinding binding;

    public VipAdPW(RxAppCompatActivity activity, View parentView) {
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
        adapter = new CardAdapter<>(activity, R.layout.item_vip_ad, MineApp.vipInfoList, this);
        mBinding.setVariable(BR.pw, this);

        mBinding.setVariable(BR.adapter, adapter);

        binding = (PwsVipAdBinding) mBinding;

        AdapterBinding.viewSize(binding.bannerLinear, ObjectUtils.getViewSizeByWidthFromMax(900), (int) (ObjectUtils.getViewSizeByWidthFromMax(900) * 579f / 810f));
        AdapterBinding.viewSize(binding.banner, ObjectUtils.getViewSizeByWidthFromMax(900), (int) (ObjectUtils.getViewSizeByWidthFromMax(900) * 579f / 810f));

        binding.banner.setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setAds(adsList)
                .setImageLoader(new ImageLoader() {
                    @Override
                    public void loadImages(Context context, Ads ads, ImageView image, int position) {
                        AdapterBinding.loadImage(image, "", ads.getAdRes(),
                                ObjectUtils.getDefaultRes(), ObjectUtils.getViewSizeByWidthFromMax(900), (int) (ObjectUtils.getViewSizeByWidthFromMax(900) * 579f / 810f),
                                false, false, 0, false, 0,false);
                    }

                    @Override
                    public void getPosition(int position) {
                        if (position > vipAdList.size() - 1)
                            return;
                        mBinding.setVariable(BR.vipAd, vipAdList.get(position));
                    }
                })
                .setBannerTypes(XBanner.CIRCLE_INDICATOR_TITLE)
                .setIndicatorGravity(XBanner.INDICATOR_START)
                .setDelay(5000)
                .setUpIndicators(R.drawable.banner_circle_pressed, R.drawable.banner_circle_unpressed)
                .setUpIndicatorSize(20, 20)
                .isAutoPlay(true)
                .setShowBg(true)
                .start();
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
    public void sure(View view) {
        super.sure(view);
        if (preIndex == -1) {
            SCToastUtil.showToastBlack(activity, "请选择VIP套餐");
            return;
        }
        submitOpenedMemberOrder(MineApp.vipInfoList.get(preIndex).getMemberOfOpenedProductId());
    }
}
