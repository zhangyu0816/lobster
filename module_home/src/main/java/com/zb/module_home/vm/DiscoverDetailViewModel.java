package com.zb.module_home.vm;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;

import com.app.abby.xbanner.Ads;
import com.app.abby.xbanner.XBanner;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.api.deleteDynApi;
import com.zb.lib_base.api.dynCancelLikeApi;
import com.zb.lib_base.api.dynDetailApi;
import com.zb.lib_base.api.dynDoLikeApi;
import com.zb.lib_base.api.dynDoReviewApi;
import com.zb.lib_base.api.giftListApi;
import com.zb.lib_base.api.makeEvaluateApi;
import com.zb.lib_base.api.otherInfoApi;
import com.zb.lib_base.api.seeGiftRewardsApi;
import com.zb.lib_base.api.seeReviewsApi;
import com.zb.lib_base.api.walletAndPopApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.CollectID;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.GiftInfo;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.Review;
import com.zb.lib_base.model.Reward;
import com.zb.lib_base.model.WalletInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.CountUsedPW;
import com.zb.lib_base.windows.SelectorPW;
import com.zb.lib_base.windows.SuperLikePW;
import com.zb.lib_base.windows.TextPW;
import com.zb.module_home.BR;
import com.zb.module_home.R;
import com.zb.module_home.adapter.HomeAdapter;
import com.zb.module_home.databinding.HomeDiscoverDetailBinding;
import com.zb.module_home.iv.DiscoverDetailVMInterface;
import com.zb.module_home.windows.GiftPW;
import com.zb.module_home.windows.GiftPayPW;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

public class DiscoverDetailViewModel extends BaseViewModel implements DiscoverDetailVMInterface, OnRefreshListener, OnLoadMoreListener {
    private HomeDiscoverDetailBinding mBinding;
    public long friendDynId = 0;
    public DiscoverInfo discoverInfo;
    public HomeAdapter reviewAdapter;
    public HomeAdapter rewardAdapter;

    private List<String> selectorList = new ArrayList<>();
    private List<Review> reviewList = new ArrayList<>();
    private int pageNo = 1;
    private List<Reward> rewardList = new ArrayList<>();
    private WalletInfo walletInfo;
    private List<GiftInfo> giftInfoList = new ArrayList<>();
    private MineInfo mineInfo;
    private MemberInfo memberInfo;
    private BaseReceiver rechargeReceiver;
    private long reviewId = 0;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (HomeDiscoverDetailBinding) binding;
        mineInfo = mineInfoDb.getMineInfo();
        AdapterBinding.viewSize(mBinding.bannerLayout.banner, MineApp.W, ObjectUtils.getLogoHeight(1.0f));
        mBinding.setVariable(BR.content, "");
        mBinding.setVariable(BR.name, "");
        setAdapter();
        dynDetail();
        giftList();

        rechargeReceiver = new BaseReceiver(activity, "lobster_recharge") {
            @Override
            public void onReceive(Context context, Intent intent) {
                walletAndPop();
            }
        };

        // 发送
        mBinding.edContent.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                dynDoReview();
            }
            return false;
        });
    }

    @Override
    public void setAdapter() {
        // 评论
        reviewAdapter = new HomeAdapter<>(activity, R.layout.item_home_review, reviewList, this);
        seeReviews();
        // 打赏
        rewardAdapter = new HomeAdapter<>(activity, R.layout.item_home_reward, rewardList, this);
        seeGiftRewards();
    }

    @Override
    public void back(View view) {
        super.back(view);
        rechargeReceiver.unregisterReceiver();
        activity.finish();
    }

    @Override
    public void more(View view) {
        super.more(view);
        new SelectorPW(activity, mBinding.getRoot(), selectorList, position -> {
            if (discoverInfo.getOtherUserId() == BaseActivity.userId) {
                if (position == 0) {

                } else if (position == 1) {
                    new TextPW(activity, mBinding.getRoot(), "删除动态", "删除后，动态不可找回！", this::deleteDyn);
                }
            } else {
                if (position == 0) {
                    makeEvaluate();
                } else if (position == 1) {
                    ActivityUtils.getHomeReport(discoverInfo.getOtherUserId());
                } else if (position == 2) {

                }
            }
        });
    }

    @Override
    public void follow(View view) {
        super.follow(view);
        if (mBinding.bannerLayout.tvFollow.getText().toString().equals("关注")) {
            mBinding.bannerLayout.tvFollow.setText("取消关注");
            mBinding.bannerLayout.tvFollow.setTextColor(activity.getResources().getColor(R.color.black_827));
        } else {
            mBinding.bannerLayout.tvFollow.setText("关注");
            mBinding.bannerLayout.tvFollow.setTextColor(activity.getResources().getColor(R.color.black_4d4));
        }
    }

    @Override
    public void selectGift(View view) {
        if (walletInfo != null && giftInfoList.size() > 0)
            new GiftPW(activity, mBinding.getRoot(), walletInfo, giftInfoList, giftInfo ->
                    new GiftPayPW(activity, mBinding.getRoot(), giftInfo, walletInfo, friendDynId));
    }

    @Override
    public void toRewardList(View view) {
        ActivityUtils.getHomeRewardList(discoverInfo.getFriendDynId());
    }

    @Override
    public void dynDetail() {
        dynDetailApi api = new dynDetailApi(new HttpOnNextListener<DiscoverInfo>() {
            @Override
            public void onNext(DiscoverInfo o) {
                discoverInfo = o;
                if (discoverInfo.getOtherUserId() == BaseActivity.userId) {
                    selectorList.add("分享");
                    selectorList.add("删除");
                } else {
                    selectorList.add("超级喜欢");
                    selectorList.add("举报");
                    selectorList.add("分享");
                }
                List<Ads> adsList = new ArrayList<>();
                if (!discoverInfo.getImages().isEmpty()) {
                    String[] images = discoverInfo.getImages().split(",");
                    for (String image : images) {
                        if (!image.isEmpty()) {
                            Ads ads = new Ads();
                            ads.setSmallImage(image);
                            adsList.add(ads);
                        }
                    }
                }
                showBanner(adsList);
                otherInfo();
                walletAndPop();
                mBinding.setVariable(BR.viewModel, DiscoverDetailViewModel.this);
            }
        }, activity).setFriendDynId(friendDynId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void giftList() {
        giftListApi api = new giftListApi(new HttpOnNextListener<List<GiftInfo>>() {
            @Override
            public void onNext(List<GiftInfo> o) {
                giftInfoList.addAll(o);
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void seeGiftRewards() {
        seeGiftRewardsApi api = new seeGiftRewardsApi(new HttpOnNextListener<List<Reward>>() {
            @Override
            public void onNext(List<Reward> o) {
                rewardList.addAll(o);
                rewardAdapter.notifyDataSetChanged();
            }
        }, activity).setFriendDynId(friendDynId)
                .setRewardSortType(1)
                .setPageNo(1);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void seeReviews() {
        seeReviewsApi api = new seeReviewsApi(new HttpOnNextListener<List<Review>>() {
            @Override
            public void onNext(List<Review> o) {
                int start = reviewList.size();
                reviewList.addAll(o);
                reviewAdapter.notifyItemRangeChanged(start, reviewList.size());
                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.refresh.setEnableLoadMore(false);
                    mBinding.refresh.finishRefresh();
                    mBinding.refresh.finishLoadMore();
                }
            }
        }, activity).setFriendDynId(friendDynId).setTimeSortType(1).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void makeEvaluate() {
        makeEvaluateApi api = new makeEvaluateApi(new HttpOnNextListener<Integer>() {
            @Override
            public void onNext(Integer o) {
                // 1喜欢成功 2匹配成功 3喜欢次数用尽
                String myHead = mineInfo.getImage();
                String otherHead = memberInfo.getMoreImages().split("#")[0];
                if (o == 1) {
                    new SuperLikePW(activity, mBinding.getRoot(), myHead, otherHead, false, mineInfo.getSex(), memberInfo.getSex());
                } else if (o == 4) {
                    new CountUsedPW(activity, mBinding.getRoot(), 2);
                }
            }
        }, activity).setOtherUserId(discoverInfo.getOtherUserId()).setLikeOtherStatus(2);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void otherInfo() {
        otherInfoApi api = new otherInfoApi(new HttpOnNextListener<MemberInfo>() {
            @Override
            public void onNext(MemberInfo o) {
                memberInfo = o;
            }
        }, activity).setOtherUserId(discoverInfo.getOtherUserId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void deleteDyn() {
        deleteDynApi api = new deleteDynApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                activity.sendBroadcast(new Intent("lobster_publish"));
                SCToastUtil.showToastBlack(activity, "删除成功");
                activity.finish();
            }
        }, activity).setFriendDynId(friendDynId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void walletAndPop() {
        walletAndPopApi api = new walletAndPopApi(new HttpOnNextListener<WalletInfo>() {
            @Override
            public void onNext(WalletInfo o) {
                walletInfo = o;
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void dynDoReview() {
        if (mBinding.getContent().isEmpty()) {
            SCToastUtil.showToastBlack(activity, "请输入评论内容");
            return;
        }
        dynDoReviewApi api = new dynDoReviewApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToastBlack(activity, "发布成功");
                // 下拉刷新
                onRefresh(mBinding.refresh);
            }
        }, activity).setFriendDynId(friendDynId).setText(mBinding.getContent()).setReviewId(reviewId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void dynLike(View view) {
        if (goodDb.hasGood(friendDynId)) {
            dynCancelLike();
        } else {
            dynDoLike();
        }
    }

    @Override
    public void dynDoLike() {
        dynDoLikeApi api = new dynDoLikeApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                goodDb.saveGood(new CollectID(friendDynId));
                int goodNum = discoverInfo.getGoodNum() + 1;
                discoverInfo.setGoodNum(goodNum);
            }
        }, activity).setFriendDynId(friendDynId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void dynCancelLike() {
        dynCancelLikeApi api = new dynCancelLikeApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                goodDb.deleteGood(friendDynId);
                int goodNum = discoverInfo.getGoodNum() - 1;
                discoverInfo.setGoodNum(goodNum);
            }
        }, activity).setFriendDynId(friendDynId);
        HttpManager.getInstance().doHttpDeal(api);
    }


    @Override
    public void selectReview(Review review) {
        reviewId = reviewId == review.getReviewId() ? 0 : review.getReviewId();
        mBinding.setVariable(BR.name, reviewId == 0 ? "" : review.getNick());
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        // 上拉加载更多
        pageNo++;
        seeReviews();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        // 下拉刷新
        mBinding.refresh.setEnableLoadMore(true);
        pageNo = 1;
        reviewList.clear();
        seeReviews();
    }

    // 显示相册
    private void showBanner(List<Ads> adList) {
        mBinding.bannerLayout.banner.setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setAds(adList)
                .setImageLoader((context, ads, image, position) -> AdapterBinding.loadImage(image, ads.getSmallImage(), 0,
                        ObjectUtils.getDefaultRes(), MineApp.W, ObjectUtils.getLogoHeight(1.0f),
                        false, false, 0, false, 0, false))
                .setBannerTypes(XBanner.CIRCLE_INDICATOR_TITLE)
                .setIndicatorGravity(XBanner.INDICATOR_START)
                .setDelay(3000)
                .setUpIndicators(R.drawable.banner_circle_pressed, R.drawable.banner_circle_unpressed)
                .setUpIndicatorSize(20, 20)
                .isAutoPlay(true)
                .start();
    }
}
