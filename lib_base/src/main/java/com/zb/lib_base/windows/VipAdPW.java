package com.zb.lib_base.windows;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zb.lib_base.BR;
import com.zb.lib_base.R;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.adapter.BaseAdapter;
import com.zb.lib_base.api.openedMemberPriceListApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.databinding.ItemAdBinding;
import com.zb.lib_base.databinding.PwsVipAdBinding;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.Ads;
import com.zb.lib_base.model.VipInfo;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.views.xbanner.ImageLoader;
import com.zb.lib_base.views.xbanner.XBanner;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.DataBindingUtil;

public class VipAdPW extends BasePopupWindow {
    private List<Ads> adsList = new ArrayList<>();
    private BaseAdapter adapter;
    private int preIndex = -1;
    private int type;
    private int sex;
    private String otherImage;
    private PwsVipAdBinding binding;

    public VipAdPW(View parentView, int type, String otherImage) {
        super(parentView, false);
        sex = MineApp.mineInfo.getSex();
        this.type = type;
        this.otherImage = otherImage;
        if (MineApp.isFirstOpen) {
            Ads ads = new Ads();
            ads.setView(adView(0));
            adsList.add(ads);
        } else {
            if (type == 0) {
                for (int i = 1; i < 8; i++) {
                    Ads ads = new Ads();
                    ads.setView(adView(i));
                    adsList.add(ads);
                }
            } else {
                Ads ads = new Ads(adView(type));
                adsList.add(ads);
            }
        }

        if (MineApp.vipInfoList.size() == 0)
            openedMemberPriceList();
        else
            initUI();
    }

    private View adView(int type) {
        ItemAdBinding binding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.item_ad, null, false);
        binding.setType(type);
        binding.setSex(sex);
        if (MineApp.isFirstOpen) {
            binding.setTitle("");
            binding.setContent("");
            binding.setMyHead("empty_icon");
            binding.setOtherHead("empty_icon");
            binding.ivBigBg.setBackgroundResource(R.mipmap.icon_first_ad);
            AdapterBinding.viewSize(binding.ivBigBg, ObjectUtils.getViewSizeByWidthFromMax(1000), ObjectUtils.getVipExposureHeight(1000));
        } else {
            if (type == 1) {
                binding.setTitle("超级曝光");
                binding.setContent("增加10倍曝光度，让更多人先发现你");
                binding.setMyHead(MineApp.mineInfo.getImage().replace("YM0000", "240X240"));
                binding.setOtherHead("empty_icon");
                binding.ivVipBg.setBackgroundResource(R.drawable.empty_bg);
            } else if (type == 2) {
                binding.setTitle("划错反悔");
                binding.setContent("手滑了？立即找回不要错过任何一个缘分！");
                binding.setMyHead("empty_icon");
                binding.setOtherHead("empty_icon");
                binding.ivVipBg.setBackgroundResource(sex == 0 ? R.mipmap.vip_ad_2_male : R.mipmap.vip_ad_2);
            } else if (type == 3) {
                binding.setTitle("超级喜欢");
                binding.setContent("每天10个超级喜欢，开通专属私信通道");
                binding.setMyHead(MineApp.mineInfo.getImage().replace("YM0000", "240X240"));
                binding.setOtherHead(otherImage.isEmpty() ? (sex == 0 ? "vip_ad_3_logo_male" : "vip_ad_3_logo") : otherImage);
                binding.ivVipBg.setBackgroundResource(R.drawable.empty_bg);
            } else if (type == 4) {
                binding.setTitle("立即查看谁喜欢我？");
                binding.setContent("第一时间查看喜欢你的人！立即匹配哦～");
                binding.setMyHead(MineApp.mineInfo.getImage().replace("YM0000", "240X240"));
                binding.setOtherHead("empty_icon");
                binding.ivVipBg.setBackgroundResource(sex == 0 ? R.mipmap.vip_ad_4_male : R.mipmap.vip_ad_4);
            } else if (type == 5) {
                binding.setTitle("位置漫游");
                binding.setContent("让你随时随地认识全世界的朋友！");
                binding.setMyHead(MineApp.mineInfo.getImage().replace("YM0000", "240X240"));
                binding.setOtherHead("empty_icon");
                binding.ivVipBg.setBackgroundResource(sex == 0 ? R.mipmap.vip_ad_5_male : R.mipmap.vip_ad_5);
            } else if (type == 6) {
                binding.setTitle("无限次数喜欢");
                binding.setContent("左滑喜欢不限次数，不要错过每个机会");
                binding.setMyHead("empty_icon");
                binding.setOtherHead("empty_icon");
                binding.ivVipBg.setBackgroundResource(sex == 0 ? R.mipmap.vip_ad_6_male : R.mipmap.vip_ad_6);
            } else if (type == 7) {
                binding.setTitle("立即匹配闪聊");
                binding.setContent("随机匹配，无需互相喜欢，直接在线闪聊");
                binding.setMyHead(MineApp.mineInfo.getImage().replace("YM0000", "240X240"));
                binding.setOtherHead("empty_icon");
                binding.ivVipBg.setBackgroundResource(R.drawable.empty_bg);
            }
        }
        return binding.getRoot();
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

        binding = (PwsVipAdBinding) mBinding;

        setBtn();

        AdapterBinding.viewSize(binding.banner, ObjectUtils.getViewSizeByWidthFromMax(1000), ObjectUtils.getVipExposureHeight(1000));

        binding.banner.setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setAds(adsList)
                .setImageLoader(new ImageLoader() {
                    @Override
                    public void loadImages(Context context, Ads ads, ImageView image, int position) {

                    }

                    @Override
                    public void loadView(LinearLayout linearLayout, View adView) {
                        if (adView.getParent() != null) {
                            ((ViewGroup) adView.getParent()).removeView(adView);
                        }
                        linearLayout.addView(adView);
                        AdapterBinding.viewSize(linearLayout, ObjectUtils.getViewSizeByWidthFromMax(1000), ObjectUtils.getVipExposureHeight(1000));
                    }
                })
                .setBannerTypes(XBanner.CIRCLE_INDICATOR_TITLE)
                .setIndicatorGravity(XBanner.INDICATOR_START)
                .setDelay(5000)
                .setUpIndicators(R.drawable.vip_circle_pressed, R.drawable.vip_circle_unpressed)
                .isAutoPlay(false)
                .setShowBg(true)
                .setType(MineApp.isFirstOpen ? 0 : type)
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
            setBtn();
        }
    }

    private void setBtn() {
        mBinding.setVariable(BR.vipInfo, MineApp.vipInfoList.get(preIndex));
        VipInfo vipInfo = binding.getVipInfo();
        if (MineApp.isFirstOpen) {
            binding.tvBtn.setText(activity.getResources().getString(R.string.open_btn_month, (vipInfo.getDayCount() / 30) * 2, vipInfo.getPrice()));
        } else if (MineApp.mineInfo.getMemberType() == 2) {
            binding.tvBtn.setText("立即续费VIP特权");
        } else {
            binding.tvBtn.setText(activity.getResources().getString(R.string.open_btn, vipInfo.getPrice()));
        }
    }

    @Override
    public void sure(View view) {
        super.sure(view);
        if (preIndex == -1) {
            SCToastUtil.showToast(activity, "请选择VIP套餐", true);
            return;
        }
        submitOpenedMemberOrder(MineApp.vipInfoList.get(preIndex).getMemberOfOpenedProductId(), () -> binding.banner.releaseBanner());
    }

    @Override
    public void cancel(View view) {
        super.cancel(view);
        binding.banner.releaseBanner();
        dismiss();
        if (type == 7) {
            activity.sendBroadcast(new Intent("lobster_flashChat"));
        }
    }

    private void openedMemberPriceList() {
        openedMemberPriceListApi api = new openedMemberPriceListApi(new HttpOnNextListener<List<VipInfo>>() {
            @Override
            public void onNext(List<VipInfo> o) {
                VipInfo vipInfo = o.get(0);
                o.get(0).setOriginalPrice(vipInfo.getPrice());
                o.get(1).setOriginalPrice(vipInfo.getPrice() * 3);
                o.get(2).setOriginalPrice(vipInfo.getPrice() * 12);
                MineApp.vipInfoList.clear();
                MineApp.vipInfoList.addAll(o);
                initUI();
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
