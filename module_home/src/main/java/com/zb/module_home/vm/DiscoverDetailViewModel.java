package com.zb.module_home.vm;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
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
import com.zb.lib_base.api.dynVisitApi;
import com.zb.lib_base.api.makeEvaluateApi;
import com.zb.lib_base.api.memberInfoConfApi;
import com.zb.lib_base.api.otherInfoApi;
import com.zb.lib_base.api.seeGiftRewardsApi;
import com.zb.lib_base.api.seeReviewsApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AttentionDb;
import com.zb.lib_base.db.GoodDb;
import com.zb.lib_base.db.LikeDb;
import com.zb.lib_base.db.LikeTypeDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.iv.SuperLikeInterface;
import com.zb.lib_base.model.Ads;
import com.zb.lib_base.model.AttentionInfo;
import com.zb.lib_base.model.CollectID;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.PairInfo;
import com.zb.lib_base.model.Review;
import com.zb.lib_base.model.Reward;
import com.zb.lib_base.model.ShareInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.MNImage;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.views.xbanner.XUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.FunctionPW;
import com.zb.lib_base.windows.GiftPW;
import com.zb.lib_base.windows.GiftPayPW;
import com.zb.lib_base.windows.SuperLikePW;
import com.zb.lib_base.windows.TextPW;
import com.zb.lib_base.windows.VipAdPW;
import com.zb.module_home.R;
import com.zb.module_home.adapter.HomeAdapter;
import com.zb.module_home.databinding.HomeDiscoverDetailBinding;
import com.zb.module_home.iv.DiscoverDetailVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

public class DiscoverDetailViewModel extends BaseViewModel implements DiscoverDetailVMInterface, OnRefreshListener, OnLoadMoreListener, SuperLikeInterface {
    private HomeDiscoverDetailBinding mBinding;
    public long friendDynId = 0;
    public DiscoverInfo discoverInfo;
    public HomeAdapter reviewAdapter;
    public HomeAdapter rewardAdapter;

    private List<Review> reviewList = new ArrayList<>();
    private int pageNo = 1;
    private List<Reward> rewardList = new ArrayList<>();
    private MemberInfo memberInfo;

    private long reviewId = 0;
    private BaseReceiver attentionReceiver;
    private boolean isFirst = true;
    private int bannerWidth = MineApp.W;
    private int bannerHeight = MineApp.W;
    private List<Ads> adsList = new ArrayList<>();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (HomeDiscoverDetailBinding) binding;
        mBinding.setContent("");
        mBinding.setName("");
        mBinding.setIsAttention(false);
        mBinding.setIsPlay(true);
        setAdapter();
        dynDetail();
        // 发送
        mBinding.edContent.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                dynDoReview();
            }
            return true;
        });

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
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                isFirst = true;
            }
            return false;
        });

        mBinding.recyclerView.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE && isSoftShowing() && isFirst) {
                isFirst = false;
                hintKeyBoard();
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                isFirst = true;
            }
            return false;
        });
    }

    @Override
    public void setAdapter() {
        // 评论
        reviewAdapter = new HomeAdapter<>(activity, R.layout.item_home_review, reviewList, this);
        // 打赏
        rewardAdapter = new HomeAdapter<>(activity, R.layout.item_home_reward, rewardList, this);
        dynVisit();
    }

    private void dynVisit() {
        dynVisitApi api = new dynVisitApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {

            }
        }, activity).setFriendDynId(friendDynId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void back(View view) {
        super.back(view);
        mBinding.banner.releaseBanner();
        try {
            attentionReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
        activity.finish();
    }

    @Override
    public void more(View view) {
        super.more(view);
        memberInfoConf();
    }

    private void memberInfoConf() {
        memberInfoConfApi api = new memberInfoConfApi(new HttpOnNextListener<ShareInfo>() {
            @Override
            public void onNext(ShareInfo o) {
                if (discoverInfo == null) return;
                String sharedName = o.getText().replace("{userId}", discoverInfo.getUserId() + "").replace("{nick}", discoverInfo.getNick());
                String content = discoverInfo.getText().isEmpty() ? discoverInfo.getFriendTitle() : discoverInfo.getText();
                String sharedUrl = HttpManager.BASE_URL + "mobile/Dyn_dynDetail?friendDynId=" + friendDynId;

                new FunctionPW(mBinding.getRoot(), discoverInfo.getImage().replace("YM0000", "430X430"), sharedName, content, sharedUrl,
                        discoverInfo.getUserId() == BaseActivity.userId, false, true, false, new FunctionPW.CallBack() {
                    @Override
                    public void gift() {
                        ActivityUtils.getHomeRewardList(friendDynId, 0);
                    }

                    @Override
                    public void delete() {
                        new TextPW(mBinding.getRoot(), "删除动态", "删除后，动态不可找回！", "删除", () -> deleteDyn());
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
                        if (MineApp.mineInfo.getMemberType() == 2) {
                            makeEvaluate(2);
                        } else {
                            new VipAdPW(mBinding.getRoot(), 3, discoverInfo.getImage());
                        }
                    }
                });
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
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
        if (discoverInfo.getUserId() == BaseActivity.userId) {
            ActivityUtils.getHomeRewardList(friendDynId, 0);
        } else
            new GiftPW(mBinding.getRoot(), giftInfo ->
                    new GiftPayPW(mBinding.getRoot(), giftInfo, friendDynId, 0, () -> {
                        rewardList.clear();
                        rewardAdapter.notifyDataSetChanged();
                        seeGiftRewards(1);
                    }));
    }

    @Override
    public void toRewardList(View view) {
        ActivityUtils.getHomeRewardList(friendDynId, 0);
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
                mBinding.setLikeType(LikeTypeDb.getInstance().getType(discoverInfo.getUserId()));
                mBinding.setViewModel(DiscoverDetailViewModel.this);
                Runnable ra = () -> {
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
                    activity.runOnUiThread(() -> {
                        int height = (int) (bannerHeight * 1.2f);
                        AdapterBinding.viewSize(mBinding.banner, bannerWidth, height);
                        XUtils.showBanner(mBinding.banner, adsList, 1,
                                (context, ads, image, position) ->
                                        AdapterBinding.loadImage(image, ads.getSmallImage(), 0, R.drawable.empty_bg, bannerWidth, height, false,
                                                true, 10, false, 0, false),
                                (position, imageList) ->
                                        MNImage.imageBrowser(activity, mBinding.getRoot(), imageList, position, discoverInfo,
                                                mBinding.getIsAttention(), GoodDb.getInstance().hasGood(friendDynId),
                                                new MNImage.OnDiscoverListener() {
                                                    @Override
                                                    public void attention() {
                                                        follow(null);
                                                    }

                                                    @Override
                                                    public void good() {
                                                        dynLike(null);
                                                    }

                                                    @Override
                                                    public void review() {
                                                        mBinding.edContent.setFocusable(true);
                                                        showImplicit(mBinding.edContent);
                                                    }

                                                    @Override
                                                    public void share() {
                                                        memberInfoConf();
                                                    }
                                                }), null);
                    });
                };

                MineApp.getApp().getFixedThreadPool().execute(ra);
                if (discoverInfo.getGoodNum() == 0 || discoverInfo.getReviews() == 0) {
                    mBinding.ivRemind.setVisibility(View.VISIBLE);
                    MineApp.getApp().getFixedThreadPool().execute(() -> {
                        SystemClock.sleep(500);
                        if (mBinding.ivRemind.getVisibility() == View.VISIBLE) {
                            SystemClock.sleep(2500);
                            activity.runOnUiThread(() -> mBinding.ivRemind.setVisibility(View.GONE));
                        }
                    });
                }
                otherInfo();
                seeGiftRewards(1);
            }
        }, activity).setFriendDynId(friendDynId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private String info = "";
    private String rewardInfo = "";
    private char[] temp;
    private int i = 0;
    private int rewardNum = 0;

    private void seeGiftRewards(int pageNo) {
        if (pageNo == 1) {
            rewardNum = 0;
        }
        seeGiftRewardsApi api = new seeGiftRewardsApi(new HttpOnNextListener<List<Reward>>() {
            @Override
            public void onNext(List<Reward> o) {
                rewardNum += o.size();
                if (rewardList.size() == 0)
                    for (int i = 0; i < Math.min(3, o.size()); i++) {
                        rewardList.add(o.get(i));
                    }
                seeGiftRewards(pageNo + 1);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.setRewardNum(rewardNum);
                    if (mBinding.getRewardNum() == 0) {
                        rewardInfo = "送朵玫瑰花，开始我们的邂逅";
                    } else {
                        if (mBinding.getRewardNum() == 1) {
                            rewardInfo = "成为CP候选人";
                        } else {
                            rewardInfo = "快来打榜";
                        }
                        rewardAdapter.notifyDataSetChanged();
                    }
                    temp = rewardInfo.toCharArray();
                    info = "";
                    i = 0;
                    mBinding.setRewardInfo(info);
                    mHandler.postDelayed(ra, 50);
                }
            }
        }, activity).setFriendDynId(friendDynId)
                .setRewardSortType(2)
                .setPageNo(pageNo)
                .setRow(10);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private Handler mHandler = new Handler();
    private Runnable ra = new Runnable() {
        @Override
        public void run() {
            if (i < temp.length) {
                info += temp[i];
                mBinding.setRewardInfo(info);
                i++;
                mHandler.postDelayed(ra, 50);
            } else {
                mHandler.removeCallbacks(ra);
            }
        }
    };

    @Override
    public void seeReviews() {
        seeReviewsApi api = new seeReviewsApi(new HttpOnNextListener<List<Review>>() {
            @Override
            public void onNext(List<Review> o) {
                int start = reviewList.size();
                if (start == 0) {
                    reviewList.add(new Review(MineApp.mineInfo.getImage(), "说句打动人心的表白，成功率高达99%", BaseActivity.userId));
                }
                for (Review item : o) {
                    item.setMainId(memberInfo.getUserId());
                }
                reviewList.addAll(o);
                if (start > 0)
                    start--;
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
                    if (reviewList.size() == 0) {
                        reviewList.add(new Review(MineApp.mineInfo.getImage(), "说句打动人心的表白，成功率高达99%", BaseActivity.userId));
                        reviewAdapter.notifyDataSetChanged();
                    }
                }
            }
        }, activity).setFriendDynId(friendDynId).setTimeSortType(1).setPageNo(pageNo).setRow(10);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void makeEvaluate(int likeOtherStatus) {
        //  likeOtherStatus  0 不喜欢  1 喜欢  2.超级喜欢 （非会员提示开通会员）
        makeEvaluateApi api = new makeEvaluateApi(new HttpOnNextListener<Integer>() {
            @Override
            public void onNext(Integer o) {
                String myHead = MineApp.mineInfo.getImage();
                String otherHead = memberInfo.getImage();
                // 1喜欢成功 2匹配成功 3喜欢次数用尽
                if (o == 1) {
                    // 不喜欢成功  喜欢成功  超级喜欢成功
                    if (likeOtherStatus == 0) {
                        activity.finish();
                    } else if (likeOtherStatus == 1) {
                        LikeDb.getInstance().saveLike(new CollectID(discoverInfo.getUserId()));
                        activity.sendBroadcast(new Intent("lobster_isLike"));
                        activity.sendBroadcast(new Intent("lobster_updateFCL"));
                        LikeTypeDb.getInstance().setType(discoverInfo.getUserId(), 1);
                        closeBtn(mBinding.likeLayout);
                        SCToastUtil.showToast(activity, "已喜欢成功", true);
                    } else if (likeOtherStatus == 2) {
                        closeBtn(mBinding.likeLayout);
                        LikeTypeDb.getInstance().setType(discoverInfo.getUserId(), 2);
                        activity.sendBroadcast(new Intent("lobster_updateFCL"));
                        new SuperLikePW(mBinding.getRoot(), myHead, otherHead, MineApp.mineInfo.getSex(), memberInfo.getSex());
                    }
                } else if (o == 2) {
                    // 匹配成功
                    LikeDb.getInstance().saveLike(new CollectID(discoverInfo.getUserId()));
                    new SuperLikePW(mBinding.getRoot(), myHead, otherHead, MineApp.mineInfo.getSex(), memberInfo.getSex(), memberInfo.getNick(),
                            () -> ActivityUtils.getChatActivity(discoverInfo.getUserId(), false));
                    activity.sendBroadcast(new Intent("lobster_pairList"));
                    activity.sendBroadcast(new Intent("lobster_isLike"));
                    activity.sendBroadcast(new Intent("lobster_updateFCL"));
                    closeBtn(mBinding.likeLayout);
                    LikeTypeDb.getInstance().setType(discoverInfo.getUserId(), 2);
                } else if (o == 3) {
                    // 喜欢次数用尽
                    new VipAdPW(mBinding.getRoot(), 6, "");
                    SCToastUtil.showToast(activity, "今日喜欢次数已用完", true);
                } else if (o == 4) {
                    // 超级喜欢时，非会员或超级喜欢次数用尽
                    if (MineApp.mineInfo.getMemberType() == 2) {
                        SCToastUtil.showToast(activity, "今日超级喜欢次数已用完", true);
                    } else {
                        new VipAdPW(mBinding.getRoot(), 3, otherHead);
                    }
                } else {
                    if (likeOtherStatus == 0) {
                        activity.finish();
                    } else if (likeOtherStatus == 1) {
                        LikeTypeDb.getInstance().setType(discoverInfo.getUserId(), 1);
                        closeBtn(mBinding.likeLayout);
                        SCToastUtil.showToast(activity, "已喜欢成功", true);
                    } else if (likeOtherStatus == 2) {
                        LikeTypeDb.getInstance().setType(discoverInfo.getUserId(), 2);
                        closeBtn(mBinding.likeLayout);
                        SCToastUtil.showToast(activity, "你已超级喜欢过对方", true);
                    }
                }
            }
        }, activity).setOtherUserId(discoverInfo.getUserId()).setLikeOtherStatus(likeOtherStatus);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private ObjectAnimator pvh, translateY;

    private void isLike(View view) {
        PropertyValuesHolder pvhSY = PropertyValuesHolder.ofFloat("scaleY", 1, 1.1f, 1, 1.2f, 1);
        PropertyValuesHolder pvhSX = PropertyValuesHolder.ofFloat("scaleX", 1, 1.1f, 1, 1.2f, 1);
        pvh = ObjectAnimator.ofPropertyValuesHolder(view, pvhSY, pvhSX).setDuration(500);
        pvh.start();
        MineApp.getApp().getFixedThreadPool().execute(() -> {
            SystemClock.sleep(500);
            activity.runOnUiThread(() -> {
                if (pvh != null)
                    pvh.cancel();
                pvh = null;
            });
        });
    }

    private void closeBtn(View view) {
        translateY = ObjectAnimator.ofFloat(view, "translationY", 0, 1000).setDuration(500);
        translateY.start();
        MineApp.getApp().getFixedThreadPool().execute(() -> {
            SystemClock.sleep(500);
            activity.runOnUiThread(() -> {
                translateY = null;
                mBinding.setLikeType(view == mBinding.ivSuperLike ? 2 : 1);
            });
        });
    }

    @Override
    public void otherInfo() {
        otherInfoApi api = new otherInfoApi(new HttpOnNextListener<MemberInfo>() {
            @Override
            public void onNext(MemberInfo o) {
                memberInfo = o;
                mBinding.setMemberInfo(memberInfo);
                mBinding.setViewModel(DiscoverDetailViewModel.this);
                attentionStatus();
                seeReviews();
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
                closeAt(null);
            }
        }, activity).setFriendDynId(friendDynId).setText(mBinding.getContent()).setReviewId(reviewId);
        api.setDialogTitle("发布评论");
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void dynLike(View view) {
        if (GoodDb.getInstance().hasGood(friendDynId)) {
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
                GoodDb.getInstance().saveGood(new CollectID(friendDynId));
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
                        GoodDb.getInstance().saveGood(new CollectID(friendDynId));
                        mBinding.setViewModel(DiscoverDetailViewModel.this);
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
    public void dynCancelLike() {
        dynCancelLikeApi api = new dynCancelLikeApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                GoodDb.getInstance().deleteGood(friendDynId);
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
                        GoodDb.getInstance().deleteGood(friendDynId);
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
    public void dislike(View view) {
        activity.finish();
    }

    @Override
    public void like(View view) {
        if (memberInfo == null) {
            SCToastUtil.showToast(activity, "网络异常，请检查网络是否链接", true);
            return;
        }
        isLike(mBinding.ivLike);
        if (PreferenceUtil.readIntValue(activity, "toLikeCount_" + BaseActivity.userId + "_" + DateUtil.getNow(DateUtil.yyyy_MM_dd), -1) == 0 && MineApp.mineInfo.getMemberType() == 1) {
            new VipAdPW(mBinding.getRoot(), 6, "");
            SCToastUtil.showToast(activity, "今日喜欢次数已用完", true);
            return;
        }
        makeEvaluate(1);
    }


    @Override
    public void selectReview(Review review) {
        reviewId = reviewId == review.getReviewId() ? 0 : review.getReviewId();

        mBinding.edContent.setHint(reviewId == 0 ? "表白一句，成功率超高～" : "评论 " + review.getNick());
    }

    @Override
    public void closeAt(View view) {
        reviewId = 0;
        mBinding.edContent.setHint("表白一句，成功率超高～");
    }

    @Override
    public void toReviewList(View view) {
        mBinding.appbar.setExpanded(false);
    }

    @Override
    public void toReviewMemberDetail(Review review) {
        if (review.getUserId() != BaseActivity.userId && review.getUserId() != 0)
            ActivityUtils.getCardMemberDetail(review.getUserId(), false);
    }

    @Override
    public void editReview(View view) {
        mBinding.edContent.setFocusable(true);
        showImplicit(mBinding.edContent);
    }

    @Override
    public void attentionStatus() {
        attentionStatusApi api = new attentionStatusApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                if (o == null) {
                    mBinding.setIsAttention(true);
                    AttentionDb.getInstance().saveAttention(new AttentionInfo(discoverInfo.getUserId(), memberInfo.getNick(), memberInfo.getImage(), true, BaseActivity.userId));
                } else {
                    AttentionDb.getInstance().saveAttention(new AttentionInfo(discoverInfo.getUserId(), memberInfo.getNick(), memberInfo.getImage(), false, BaseActivity.userId));
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
                if (memberInfo != null)
                    AttentionDb.getInstance().saveAttention(new AttentionInfo(discoverInfo.getUserId(), memberInfo.getNick(), memberInfo.getImage(), true, BaseActivity.userId));
                activity.sendBroadcast(new Intent("lobster_attentionList"));
                Intent intent = new Intent("lobster_attention");
                intent.putExtra("isAttention", true);
                activity.sendBroadcast(intent);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.ERROR) {
                    if (e.getMessage().equals("已经关注过")) {
                        mBinding.setIsAttention(true);
                        if (memberInfo != null)
                            AttentionDb.getInstance().saveAttention(new AttentionInfo(discoverInfo.getUserId(), memberInfo.getNick(), memberInfo.getImage(), true, BaseActivity.userId));
                        activity.sendBroadcast(new Intent("lobster_attentionList"));
                        Intent intent = new Intent("lobster_attention");
                        intent.putExtra("isAttention", true);
                        activity.sendBroadcast(intent);
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
                if (memberInfo != null)
                    AttentionDb.getInstance().saveAttention(new AttentionInfo(discoverInfo.getUserId(), memberInfo.getNick(), memberInfo.getImage(), false, BaseActivity.userId));
                activity.sendBroadcast(new Intent("lobster_attentionList"));
                Intent intent = new Intent("lobster_attention");
                intent.putExtra("isAttention", false);
                activity.sendBroadcast(intent);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.ERROR) {
                    if (e.getMessage().equals("你还没关注我啊")) {
                        mBinding.setIsAttention(false);
                        if (memberInfo != null)
                            AttentionDb.getInstance().saveAttention(new AttentionInfo(discoverInfo.getUserId(), memberInfo.getNick(), memberInfo.getImage(), false, BaseActivity.userId));
                        activity.sendBroadcast(new Intent("lobster_attentionList"));
                        Intent intent = new Intent("lobster_attention");
                        intent.putExtra("isAttention", false);
                        activity.sendBroadcast(intent);
                    }
                }
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

    @Override
    public void superLike(View view, PairInfo pairInfo) {
        if (MineApp.mineInfo.getMemberType() == 2) {
            makeEvaluate(2);
        } else {
            if (memberInfo != null)
                new VipAdPW(mBinding.getRoot(), 3, memberInfo.getImage());
        }
    }

    @Override
    public void returnBack() {

    }
}
