package com.zb.module_home.vm;

import android.view.View;
import android.widget.ImageView;

import com.app.abby.xbanner.Ads;
import com.app.abby.xbanner.XBanner;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.GiftInfo;
import com.zb.lib_base.model.Review;
import com.zb.lib_base.model.Reward;
import com.zb.lib_base.model.WalletInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.SelectorPW;
import com.zb.module_home.BR;
import com.zb.module_home.R;
import com.zb.module_home.adapter.HomeAdapter;
import com.zb.module_home.databinding.HomeDiscoverDetailBinding;
import com.zb.module_home.iv.DiscoverDetailVMInterface;
import com.zb.module_home.windows.GiftPW;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

public class DiscoverDetailViewModel extends BaseViewModel implements DiscoverDetailVMInterface, OnRefreshListener, OnLoadMoreListener {
    private HomeDiscoverDetailBinding discoverDetailBinding;
    public long friendDynId = 0;
    private List<String> selectorList = new ArrayList<>();
    public DiscoverInfo discoverInfo;
    private List<Review> reviewList = new ArrayList<>();
    public HomeAdapter reviewAdapter;
    private List<Reward> rewardList = new ArrayList<>();
    public HomeAdapter rewardAdapter;
    private WalletInfo walletInfo = new WalletInfo();
    private List<GiftInfo> giftInfoList = new ArrayList<>();

    public DiscoverDetailViewModel() {


        discoverInfo = new DiscoverInfo();
        discoverInfo.setRewardNum(2);
        discoverInfo.setUserId(123456l);

        for (int i = 0; i < 6; i++) {
            reviewList.add(new Review());
            rewardList.add(new Reward());
            GiftInfo giftInfo = new GiftInfo();
            giftInfo.setGiftName("礼物" + i);
            giftInfoList.add(giftInfo);
        }
        if (discoverInfo.getUserId() == BaseActivity.userId) {
            selectorList.add("分享");
            selectorList.add("删除");
        } else {
            selectorList.add("超级喜欢");
            selectorList.add("举报");
            selectorList.add("分享");
        }

    }

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        discoverDetailBinding = (HomeDiscoverDetailBinding) binding;
        AdapterBinding.viewSize(discoverDetailBinding.bannerLayout.banner, MineApp.W, ObjectUtils.getLogoHeight(1.0f));
        mBinding.setVariable(BR.content, "");
        setAdapter();
    }

    @Override
    public void setAdapter() {
        reviewAdapter = new HomeAdapter<>(activity, R.layout.item_home_review, reviewList, this);
        rewardAdapter = new HomeAdapter<>(activity, R.layout.item_home_reward, rewardList, this);
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void more(View view) {
        super.more(view);
        new SelectorPW(activity, mBinding.getRoot(), selectorList, position -> {
            if (discoverInfo.getUserId() == BaseActivity.userId) {
                if (position == 0) {

                } else if (position == 1) {

                }
            } else {
                if (position == 0) {

                } else if (position == 1) {

                } else if (position == 2) {

                }
            }
        });
    }

    @Override
    public void follow(View view) {
        super.follow(view);
        if (discoverDetailBinding.bannerLayout.tvFollow.getText().toString().equals("关注")) {
            discoverDetailBinding.bannerLayout.tvFollow.setText("取消关注");
            discoverDetailBinding.bannerLayout.tvFollow.setTextColor(activity.getResources().getColor(R.color.black_827));
        } else {
            discoverDetailBinding.bannerLayout.tvFollow.setText("关注");
            discoverDetailBinding.bannerLayout.tvFollow.setTextColor(activity.getResources().getColor(R.color.black_4d4));
        }

    }

    // 显示相册
    private void showBanner(List<Ads> adList) {
        discoverDetailBinding.bannerLayout.banner.setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setAds(adList)
                .setImageLoader((context, ads, image, position) -> AdapterBinding.loadImage(image, ads.getSmallImage(), 0,
                        ObjectUtils.getDefaultRes(), MineApp.W, ObjectUtils.getLogoHeight(1.0f),
                        false, false, 0, false, 0))
                .setBannerTypes(XBanner.CIRCLE_INDICATOR_TITLE)
                .setIndicatorGravity(XBanner.INDICATOR_START)
                .setDelay(3000)
                .setUpIndicators(R.drawable.banner_circle_pressed, R.drawable.banner_circle_unpressed)
                .setUpIndicatorSize(20, 20)
                .isAutoPlay(true)
                .start();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        // 上拉加载更多
//            int start = list.size();
//            list.addAll(newItems);
//            adapter.notifyItemInserted(start, list.size());
//        discoverInfoList.add(new DiscoverInfo());
//        refreshLayout.finishLoadMore();
//        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {


        // 下拉刷新
//            list.clear();
//            list.addAll(newList);
//            adapter.notifyItemRangeChanged(0, list.size());
//        refreshLayout.finishRefresh();
    }

    @Override
    public void selectGift(View view) {
        new GiftPW(activity, mBinding.getRoot(), walletInfo, giftInfoList);
    }

    @Override
    public void toRewardList(View view) {
        ActivityUtils.getHomeRewardList(discoverInfo.getFriendDynId());
    }
}
