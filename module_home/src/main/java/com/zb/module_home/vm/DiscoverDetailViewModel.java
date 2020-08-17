package com.zb.module_home.vm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;

import com.app.abby.xbanner.Ads;
import com.app.abby.xbanner.XBanner;
import com.google.android.material.appbar.AppBarLayout;
import com.maning.imagebrowserlibrary.MNImage;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.socialize.media.UMImage;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.api.attentionOtherApi;
import com.zb.lib_base.api.attentionStatusApi;
import com.zb.lib_base.api.cancelAttentionApi;
import com.zb.lib_base.api.deleteDynApi;
import com.zb.lib_base.api.dynCancelLikeApi;
import com.zb.lib_base.api.dynDetailApi;
import com.zb.lib_base.api.dynDoLikeApi;
import com.zb.lib_base.api.dynDoReviewApi;
import com.zb.lib_base.api.makeEvaluateApi;
import com.zb.lib_base.api.otherInfoApi;
import com.zb.lib_base.api.seeGiftRewardsApi;
import com.zb.lib_base.api.seeReviewsApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.LikeDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.AttentionInfo;
import com.zb.lib_base.model.CollectID;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.Review;
import com.zb.lib_base.model.Reward;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.CountUsedPW;
import com.zb.lib_base.windows.FunctionPW;
import com.zb.lib_base.windows.SuperLikePW;
import com.zb.lib_base.windows.TextPW;
import com.zb.lib_base.windows.VipAdPW;
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
import io.realm.Realm;

public class DiscoverDetailViewModel extends BaseViewModel implements DiscoverDetailVMInterface, OnRefreshListener, OnLoadMoreListener {
    private HomeDiscoverDetailBinding mBinding;
    public long friendDynId = 0;
    public DiscoverInfo discoverInfo;
    public HomeAdapter reviewAdapter;
    public HomeAdapter rewardAdapter;
    private LikeDb likeDb;

    private List<Review> reviewList = new ArrayList<>();
    private int pageNo = 1;
    private List<Reward> rewardList = new ArrayList<>();
    private MineInfo mineInfo;
    private MemberInfo memberInfo;

    private long reviewId = 0;
    private BaseReceiver finishRefreshReceiver;
    private BaseReceiver attentionReceiver;
    private boolean isFirst = true;
    private AppBarLayout.LayoutParams params;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (HomeDiscoverDetailBinding) binding;
        mineInfo = mineInfoDb.getMineInfo();
        likeDb = new LikeDb(Realm.getDefaultInstance());
        AdapterBinding.viewSize(mBinding.banner, MineApp.W, MineApp.W);
        mBinding.setContent("");
        mBinding.setName("");
        mBinding.setListNum(10);
        mBinding.setIsAttention(false);

        setAdapter();
        dynDetail();
        // 发送
        mBinding.edContent.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                dynDoReview();
            }
            return true;
        });
        finishRefreshReceiver = new BaseReceiver(activity, "lobster_finishRefresh") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();
            }
        };

        attentionReceiver = new BaseReceiver(activity, "lobster_attention") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.setIsAttention(intent.getBooleanExtra("isAttention", false));
            }
        };

        mBinding.coordinator.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE && isSoftShowing() && isFirst) {
                isFirst = false;
                hintKeyBoard();
                new Handler().postDelayed(() -> isFirst = true, 500);
            }
            return false;
        });

        mBinding.recyclerView.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE && isSoftShowing() && isFirst) {
                isFirst = false;
                hintKeyBoard();
                new Handler().postDelayed(() -> isFirst = true, 500);
            }
            return false;
        });
        params = (AppBarLayout.LayoutParams) mBinding.toolbar.getLayoutParams();
    }

    @Override
    public void setAdapter() {
        // 评论
        reviewAdapter = new HomeAdapter<>(activity, R.layout.item_home_review, reviewList, this);
        // 打赏
        rewardAdapter = new HomeAdapter<>(activity, R.layout.item_home_reward, rewardList, this);
    }

    @Override
    public void back(View view) {
        super.back(view);
        finishRefreshReceiver.unregisterReceiver();
        attentionReceiver.unregisterReceiver();
        activity.finish();
    }

    @Override
    public void more(View view) {
        super.more(view);
        if (discoverInfo == null) return;
        String sharedName = discoverInfo.getNick();
        String content = discoverInfo.getText();
        String sharedUrl = HttpManager.BASE_URL + "mobile/Dyn_dynDetail?friendDynId=" + friendDynId;
        UMImage umImage = new UMImage(activity, discoverInfo.getImage().replace("YM0000", "430X430"));

        new FunctionPW(activity, mBinding.getRoot(), umImage, sharedName, content, sharedUrl,
                discoverInfo.getUserId() == BaseActivity.userId, false, false, new FunctionPW.CallBack() {
            @Override
            public void gift() {

            }

            @Override
            public void delete() {
                new TextPW(activity, mBinding.getRoot(), "删除动态", "删除后，动态不可找回！", "删除", () -> deleteDyn());
            }

            @Override
            public void report() {
                ActivityUtils.getHomeReport(discoverInfo.getUserId());
            }

            @Override
            public void download() {

            }

            @Override
            public void like() {
                if (mineInfo.getMemberType() == 2) {
                    makeEvaluate();
                } else {
                    new VipAdPW(activity, mBinding.getRoot(), false, 3);
                }
            }
        });
    }

    @Override
    public void follow(View view) {
        super.follow(view);
        if (!mBinding.getIsAttention()) {
            attentionOther();
        } else {
            cancelAttention();
        }
    }

    @Override
    public void selectGift(View view) {
        hintKeyBoard();
        if (MineApp.walletInfo != null && MineApp.giftInfoList.size() > 0)
            new GiftPW(activity, mBinding.getRoot(), giftInfo ->
                    new GiftPayPW(activity, mBinding.getRoot(), giftInfo, friendDynId, () -> {
                        seeGiftRewards();
                    }));
    }

    @Override
    public void toRewardList(View view) {
        ActivityUtils.getHomeRewardList(friendDynId);
    }

    @Override
    public void toMemberDetail(View view) {
        ActivityUtils.getCardMemberDetail(discoverInfo.getUserId(), false);
    }

    @Override
    public void dynDetail() {
        dynDetailApi api = new dynDetailApi(new HttpOnNextListener<DiscoverInfo>() {
            @Override
            public void onNext(DiscoverInfo o) {
                discoverInfo = o;
                mBinding.setViewModel(DiscoverDetailViewModel.this);
                otherInfo();
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
                } else {
                    Ads ads = new Ads();
                    ads.setSmallImage(discoverInfo.getImage());
                    adsList.add(ads);
                }
                showBanner(adsList);
                seeGiftRewards();
                seeReviews();
            }
        }, activity).setFriendDynId(friendDynId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    public void seeGiftRewards() {
        seeGiftRewardsApi api = new seeGiftRewardsApi(new HttpOnNextListener<List<Reward>>() {
            @Override
            public void onNext(List<Reward> o) {
                rewardList.clear();
                rewardAdapter.notifyDataSetChanged();
                rewardList.addAll(o);
                rewardAdapter.notifyDataSetChanged();
                mBinding.setRewardNum(rewardList.size());
                int gridNum = rewardList.size() % 10;
                mBinding.setListNum(gridNum == 0 ? 10 : gridNum);
            }
        }, activity).setFriendDynId(friendDynId)
                .setRewardSortType(2)
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
                if (params.getScrollFlags() == 0) {
                    params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED | AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);//0不能伸缩
                    mBinding.toolbar.setLayoutParams(params);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.refresh.setEnableLoadMore(false);
                    mBinding.refresh.finishRefresh();
                    mBinding.refresh.finishLoadMore();
                    if (reviewList.size() == 0) {
                        params.setScrollFlags(0);//0不能伸缩
                        mBinding.toolbar.setLayoutParams(params);
                    }
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
                    new SuperLikePW(activity, mBinding.getRoot(), myHead, otherHead, false, mineInfo.getSex(), memberInfo.getSex(), null);
                } else if (o == 2) {
                    // 匹配成功
                    likeDb.saveLike(new CollectID(memberInfo.getUserId()));
                    new SuperLikePW(activity, mBinding.getRoot(), myHead, otherHead, true, mineInfo.getSex(), memberInfo.getSex(), () -> ActivityUtils.getChatActivity(memberInfo.getUserId()));
                    activity.sendBroadcast(new Intent("lobster_pairList"));
                } else if (o == 3) {
                    // 喜欢次数用尽
                    SCToastUtil.showToast(activity, "今日喜欢次数已用完", true);
                } else if (o == 4) {
                    new CountUsedPW(activity, mBinding.getRoot(), 2);
                } else {
                    SCToastUtil.showToast(activity, "你已超级喜欢过对方", true);
                }
            }
        }, activity).setOtherUserId(discoverInfo.getUserId()).setLikeOtherStatus(2);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void otherInfo() {
        otherInfoApi api = new otherInfoApi(new HttpOnNextListener<MemberInfo>() {
            @Override
            public void onNext(MemberInfo o) {
                memberInfo = o;
                mBinding.setViewModel(DiscoverDetailViewModel.this);
                attentionStatus();
            }
        }, activity).setOtherUserId(discoverInfo.getUserId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void deleteDyn() {
        deleteDynApi api = new deleteDynApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                activity.sendBroadcast(new Intent("lobster_publish"));
                SCToastUtil.showToast(activity, "删除成功", true);
                activity.finish();
            }
        }, activity).setFriendDynId(friendDynId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void dynDoReview() {
        if (mBinding.getContent().trim().isEmpty()) {
            SCToastUtil.showToast(activity, "请输入评论内容", true);
            return;
        }
        dynDoReviewApi api = new dynDoReviewApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToast(activity, "发布成功", true);
                discoverInfo.setReviews(discoverInfo.getReviews() + 1);
                mBinding.setViewModel(DiscoverDetailViewModel.this);
                mBinding.setContent("");
                // 下拉刷新
                onRefresh(mBinding.refresh);
            }
        }, activity).setFriendDynId(friendDynId).setText(mBinding.getContent()).setReviewId(reviewId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void dynLike(View view) {
        if (goodDb.hasGood(friendDynId)) {
            mBinding.goodView.playUnlike();
            dynCancelLike();
        } else {
            mBinding.goodView.playLike();
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
                mBinding.setViewModel(DiscoverDetailViewModel.this);

                Intent data = new Intent("lobster_doGood");
                data.putExtra("goodNum", goodNum);
                data.putExtra("friendDynId", friendDynId);
                activity.sendBroadcast(data);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == 0) {
                    if (TextUtils.equals(e.getMessage(), "已经赞过了")) {
                        goodDb.saveGood(new CollectID(friendDynId));
                        mBinding.setViewModel(DiscoverDetailViewModel.this);
                        Intent data = new Intent("lobster_doGood");
                        data.putExtra("friendDynId", friendDynId);
                        activity.sendBroadcast(data);
                    }
                }
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
                mBinding.setViewModel(DiscoverDetailViewModel.this);
                Intent data = new Intent("lobster_doGood");
                data.putExtra("goodNum", goodNum);
                data.putExtra("friendDynId", friendDynId);
                activity.sendBroadcast(data);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == 0) {
                    if (TextUtils.equals(e.getMessage(), "已经取消过")) {
                        goodDb.deleteGood(friendDynId);
                        Intent data = new Intent("lobster_doGood");
                        data.putExtra("goodNum", discoverInfo.getGoodNum());
                        data.putExtra("friendDynId", friendDynId);
                        activity.sendBroadcast(data);
                    }
                }
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
    public void closeAt(View view) {
        reviewId = 0;
        mBinding.setVariable(BR.name, "");
    }

    @Override
    public void toReviewList(View view) {
        mBinding.appbar.setExpanded(false);
    }

    @Override
    public void attentionStatus() {
        attentionStatusApi api = new attentionStatusApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                if (o == null) {
                    mBinding.setIsAttention(true);
                    attentionDb.saveAttention(new AttentionInfo(discoverInfo.getUserId(), memberInfo.getNick(), memberInfo.getImage(), true, BaseActivity.userId));
                } else {
                    attentionDb.saveAttention(new AttentionInfo(discoverInfo.getUserId(), memberInfo.getNick(), memberInfo.getImage(), false, BaseActivity.userId));
                }
            }
        }, activity).setOtherUserId(discoverInfo.getUserId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void attentionOther() {
        if (discoverInfo == null) return;
        attentionOtherApi api = new attentionOtherApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                mBinding.setIsAttention(true);
                attentionDb.saveAttention(new AttentionInfo(discoverInfo.getUserId(), memberInfo.getNick(), memberInfo.getImage(), true, BaseActivity.userId));
                activity.sendBroadcast(new Intent("lobster_attentionList"));
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.ERROR) {
                    if (e.getMessage().equals("已经关注过")) {
                        mBinding.setIsAttention(true);
                        attentionDb.saveAttention(new AttentionInfo(discoverInfo.getUserId(), memberInfo.getNick(), memberInfo.getImage(), true, BaseActivity.userId));
                        activity.sendBroadcast(new Intent("lobster_attentionList"));
                    }
                }
            }
        }, activity).setOtherUserId(discoverInfo.getUserId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void cancelAttention() {
        if (discoverInfo == null) return;
        cancelAttentionApi api = new cancelAttentionApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                mBinding.setIsAttention(false);
                attentionDb.saveAttention(new AttentionInfo(discoverInfo.getUserId(), memberInfo.getNick(), memberInfo.getImage(), false, BaseActivity.userId));
                activity.sendBroadcast(new Intent("lobster_attentionList"));
            }
        }, activity).setOtherUserId(discoverInfo.getUserId());
        HttpManager.getInstance().doHttpDeal(api);
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
        reviewAdapter.notifyDataSetChanged();
        seeReviews();
    }

    // 显示相册
    private void showBanner(List<Ads> adList) {
        ArrayList<String> imageList = new ArrayList<>();
        for (Ads item : adList) {
            imageList.add(item.getSmallImage());
        }
        mBinding.banner.setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setAds(adList)
                .setImageLoader((context, ads, image, position) -> AdapterBinding.loadImage(image, ads.getSmallImage(), 0,
                        ObjectUtils.getDefaultRes(), MineApp.W, MineApp.W,
                        false, false, 0, false, 0, false))
                .setBannerPageListener(item -> MNImage.imageBrowser(activity, mBinding.getRoot(), imageList, item, false, null))
                .setBannerTypes(XBanner.CIRCLE_INDICATOR_TITLE)
                .setIndicatorGravity(XBanner.INDICATOR_START)
                .setDelay(3000)
                .setUpIndicators(R.drawable.banner_circle_pressed, R.drawable.banner_circle_unpressed)
                .setUpIndicatorSize(20, 20)
                .isAutoPlay(false)
                .start();
    }
}
