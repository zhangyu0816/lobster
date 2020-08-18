package com.zb.lib_base.windows;

import android.view.View;
import android.widget.ImageView;

import com.app.abby.xbanner.Ads;
import com.app.abby.xbanner.XBanner;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.BR;
import com.zb.lib_base.R;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.adapter.BaseAdapter;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.databinding.PwsVipAdBinding;
import com.zb.lib_base.db.MineInfoDb;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.SCToastUtil;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class VipAdPW extends BasePopupWindow {
    private List<Ads> adsList = new ArrayList<>();
    private BaseAdapter adapter;
    private int preIndex = -1;
    private PwsVipAdBinding binding;
    private boolean isAutoPlay;
    private int type = 0;
    private MineInfoDb mineInfoDb;
    private MineInfo mineInfo;
    private int sex;

    public VipAdPW(RxAppCompatActivity activity, View parentView, boolean isAutoPlay, int type) {
        super(activity, parentView, false);
        mineInfoDb = new MineInfoDb(Realm.getDefaultInstance());
        mineInfo = mineInfoDb.getMineInfo();
        sex = mineInfo.getSex();
        this.isAutoPlay = isAutoPlay;
        this.type = type;
        if (type == 0) {
            for (int i = 0; i < 5; i++) {
                Ads ads = new Ads();
                if (i == 0) {
                    ads.setAdRes(sex == 0 ? R.mipmap.vip_ad_1 : R.mipmap.vip_ad_1_male);
                } else if (i == 1) {
                    ads.setAdRes(sex == 1 ? R.mipmap.vip_ad_2 : R.mipmap.vip_ad_2_male);
                } else if (i == 2) {
                    ads.setAdRes(R.mipmap.vip_ad_3);
                } else if (i == 3) {
                    ads.setAdRes(sex == 0 ? R.mipmap.vip_ad_4 : R.mipmap.vip_ad_4_male);
                } else {
                    ads.setAdRes(R.mipmap.vip_ad_5);
                }
                adsList.add(ads);
            }
        } else if (type == 5) {
            Ads ads = new Ads(R.mipmap.vip_ad_5);
            adsList.add(ads);
        } else if (type == 4) {
            Ads ads = new Ads(sex == 0 ? R.mipmap.vip_ad_4 : R.mipmap.vip_ad_4_male);
            adsList.add(ads);
        } else if (type == 3) {
            Ads ads = new Ads(R.mipmap.vip_ad_3);
            adsList.add(ads);
        } else if (type == 2) {
            Ads ads = new Ads(sex == 1 ? R.mipmap.vip_ad_2 : R.mipmap.vip_ad_2_male);
            adsList.add(ads);
        } else if (type == 1) {
            Ads ads = new Ads(sex == 0 ? R.mipmap.vip_ad_1 : R.mipmap.vip_ad_1_male);
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
        if (MineApp.vipInfoList.size() < 2) {
            preIndex = MineApp.vipInfoList.size() - 1;
        } else {
            preIndex = 1;
        }

        if (preIndex >= 0) {
            adapter.setSelectIndex(preIndex);
            adapter.notifyItemChanged(preIndex);
        }

        mBinding.setVariable(BR.pw, this);
        mBinding.setVariable(BR.adapter, adapter);
        mBinding.setVariable(BR.mineInfo,mineInfo);
        mBinding.setVariable(BR.vipInfo,MineApp.vipInfoList.get(preIndex));

        binding = (PwsVipAdBinding) mBinding;

        AdapterBinding.viewSize(binding.banner, ObjectUtils.getViewSizeByWidthFromMax(1000), ObjectUtils.getVipExposureHeight(1000));

        binding.banner.setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setAds(adsList)
                .setImageLoader((context, ads, image, position) -> AdapterBinding.loadImage(image, "", ads.getAdRes(),
                        ObjectUtils.getDefaultRes(), ObjectUtils.getViewSizeByWidthFromMax(1000), ObjectUtils.getVipExposureHeight(1000),
                        false, false, 0, false, 0, false))
                .setBannerTypes(XBanner.CIRCLE_INDICATOR_TITLE)
                .setIndicatorGravity(XBanner.INDICATOR_START)
                .setDelay(5000)
                .setUpIndicators(R.drawable.vip_circle_pressed, R.drawable.vip_circle_unpressed)
                .setUpIndicatorSize(20, 20)
                .isAutoPlay(isAutoPlay)
                .setShowBg(true)
                .setType(type)
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
            mBinding.setVariable(BR.vipInfo,MineApp.vipInfoList.get(preIndex));
        }
    }

    @Override
    public void sure(View view) {
        super.sure(view);
        if (preIndex == -1) {
            SCToastUtil.showToast(activity, "请选择VIP套餐", true);
            return;
        }
        submitOpenedMemberOrder(MineApp.vipInfoList.get(preIndex).getMemberOfOpenedProductId());
    }

    @Override
    public void cancel(View view) {
        super.cancel(view);
        dismiss();
    }

}
