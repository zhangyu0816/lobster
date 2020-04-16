package com.zb.module_home.vm;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.app.abby.xbanner.Ads;
import com.app.abby.xbanner.ImageLoader;
import com.app.abby.xbanner.XBanner;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.Review;
import com.zb.lib_base.model.Reward;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_home.BR;
import com.zb.module_home.HomeAdapter;
import com.zb.module_home.R;
import com.zb.module_home.databinding.HomeDiscoverDetailBinding;
import com.zb.module_home.iv.DiscoverDetailVMInterface;
import com.zb.module_home.windows.SelectorPW;

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

    public DiscoverDetailViewModel() {
        selectorList.add("超级喜欢");
        selectorList.add("举报");
        selectorList.add("分享");

        discoverInfo = new DiscoverInfo();
        discoverInfo.setRewardNum(2);

        for (int i = 0; i < 10; i++) {
            reviewList.add(new Review());
            rewardList.add(new Reward());
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
            if (position == 0) {

            } else if (position == 1) {

            } else if (position == 2) {

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
                .setImageLoader(new ImageLoader() {
                    @Override
                    public void loadImages(Context context, final Ads ads, ImageView image) {
                        AdapterBinding.loadImage(image, ads.getSmallImage(), 0,
                                ObjectUtils.getDefaultRes(), MineApp.W, ObjectUtils.getLogoHeight(1.0f),
                                false, false, 0, false, 0, 0);
                    }

                    @Override
                    public void loadVideoViews(Context context, Ads ads, VideoView videoView) {
//                        videoView.setMediaController(controller);
//                        videoView.setVideoURI(Uri.parse(ads.getSmallImage()));
//                        videoView.start();
                    }
                })
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
    public void clickEd(View view) {
        SCToastUtil.showToast(activity,"111111111");
    }

}
