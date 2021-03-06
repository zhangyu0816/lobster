package com.zb.module_home.vm;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.api.attentionOtherApi;
import com.zb.lib_base.api.attentionStatusApi;
import com.zb.lib_base.api.cancelAttentionApi;
import com.zb.lib_base.api.dynCancelLikeApi;
import com.zb.lib_base.api.dynDoLikeApi;
import com.zb.lib_base.api.dynPiazzaListApi;
import com.zb.lib_base.api.dynVisitApi;
import com.zb.lib_base.api.makeEvaluateApi;
import com.zb.lib_base.api.memberInfoConfApi;
import com.zb.lib_base.api.seeLikersApi;
import com.zb.lib_base.api.seeReviewsApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.db.AttentionDb;
import com.zb.lib_base.db.GoodDb;
import com.zb.lib_base.db.LikeTypeDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.AttentionInfo;
import com.zb.lib_base.model.CollectID;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.Review;
import com.zb.lib_base.model.ShareInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DownLoad;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.glide.DouYinLayoutManager;
import com.zb.lib_base.utils.glide.OnViewPagerListener;
import com.zb.lib_base.utils.glide.ScrollSpeedLinearLayoutManger;
import com.zb.lib_base.utils.water.WaterMark;
import com.zb.lib_base.views.AutoPollRecyclerView;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.FunctionPW;
import com.zb.lib_base.windows.GiftPW;
import com.zb.lib_base.windows.GiftPayPW;
import com.zb.lib_base.windows.SuperLikePW;
import com.zb.lib_base.windows.TextPW;
import com.zb.lib_base.windows.VipAdPW;
import com.zb.module_home.R;
import com.zb.module_home.adapter.HomeAdapter;
import com.zb.module_home.databinding.HomeVideoListBinding;
import com.zb.module_home.iv.VideoListVMInterface;
import com.zb.module_home.windows.ReviewPW;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.OrientationHelper;

public class VideoListViewModel extends BaseViewModel implements VideoListVMInterface {
    public int position = -1;
    public int pageNo;
    private int showPosition = -1;
    private HomeAdapter adapter;
    private DouYinLayoutManager douYinLayoutManager;
    private HomeVideoListBinding mBinding;
    private boolean isUp = false;
    private boolean isOver = false;
    private BaseReceiver attentionReceiver;
    private int videoWidth, videoHeight;
    private boolean canUpdate = false;
    private int duration;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (HomeVideoListBinding) binding;
        setAdapter();

        attentionReceiver = new BaseReceiver(activity, "lobster_attention") {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isAttention = intent.getBooleanExtra("isAttention", false);
                if (isAttention) {
                    layout.setVisibility(View.INVISIBLE);
                } else {
                    layout.setVisibility(View.VISIBLE);
                    ivAttention.setBackgroundResource(R.drawable.attention_icon);
                }
            }
        };
    }

    public void onDestroy() {
        super.onDestroy();
        try {
            attentionReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        adapter = new HomeAdapter<>(activity, R.layout.item_video, MineApp.discoverInfoList, this);

        douYinLayoutManager = new DouYinLayoutManager(activity, OrientationHelper.VERTICAL, false);
        mBinding.videoList.setLayoutManager(douYinLayoutManager);
        mBinding.videoList.setAdapter(adapter);
        mBinding.videoList.scrollToPosition(position);
        adapter.notifyDataSetChanged();
        douYinLayoutManager.setOnViewPagerListener(new OnViewPagerListener() {
            @Override
            public void onPageRelease(boolean isNest, View view) {
                VideoView videoView = view.findViewById(R.id.video_view);
                ImageView ivImage = view.findViewById(R.id.iv_image);
                AutoPollRecyclerView reviewList = view.findViewById(R.id.review_list);
                videoView.stopPlayback();//??????????????????,????????????
                videoView.suspend();//???????????????????????????????????????
                ivImage.setVisibility(View.VISIBLE);
                videoView.setBackgroundColor(activity.getResources().getColor(R.color.black_252));
                reviewList.stop();
                reviewList.setVisibility(View.GONE);
            }

            @Override
            public void onPageSelected(boolean isButton, View view) {
                setScroll(false);
                isUp = douYinLayoutManager.getDrift() >= 0;
                position = douYinLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (position == -1)
                    return;
                if (showPosition == position)
                    return;
                showPosition = position;

                canUpdate = false;
                playVideo(view);
                if (!isOver && position == MineApp.discoverInfoList.size() - 1 && isUp) {
                    pageNo++;
                    dynPiazzaList();
                }
            }

            @Override
            public void onScroll() {
                setScroll(true);
            }
        });
    }

    @Override
    public void videoPlay(DiscoverInfo discoverInfo) {
        if (videoView.isPlaying()) {
            ivPlay.setVisibility(View.VISIBLE);
            videoView.pause();
            reviewListView.stop();
        } else {
            ivPlay.setVisibility(View.GONE);
//            videoView.setVideoPath(discoverInfo.getVideoUrl());
            videoView.start();
            reviewListView.start();
        }
    }

    @Override
    public void dynPiazzaList() {
        dynPiazzaListApi api = new dynPiazzaListApi(new HttpOnNextListener<List<DiscoverInfo>>() {
            @Override
            public void onNext(List<DiscoverInfo> o) {
                // ??????  ??????????????????
                int start = MineApp.discoverInfoList.size();
                MineApp.discoverInfoList.addAll(o);
                adapter.notifyItemRangeChanged(start, MineApp.discoverInfoList.size());
                for (DiscoverInfo item : o) {
                    DownLoad.getFilePath(item.getVideoUrl(), BaseActivity.getDownloadFile(".mp4").getAbsolutePath(), (filePath, bitmap) -> {
                    });
                }
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    SCToastUtil.showToast(activity, "??????????????????", true);
                    isOver = true;
                }
            }
        }, activity)
                .setCityId(AreaDb.getInstance().getCityId(MineApp.cityName))
                .setDynType(2)
                .setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void toMemberDetail(DiscoverInfo discoverInfo) {
        videoView.pause();
        reviewListView.stop();
        ActivityUtils.getCardMemberDetail(discoverInfo.getUserId(), false);
    }

    @Override
    public void visitMember(long userId) {
        super.visitMember(userId);
        if (userId != BaseActivity.userId) {
            videoView.pause();
            reviewListView.stop();
            ActivityUtils.getCardMemberDetail(userId, false);
        }
    }

    @Override
    public void toReviews(int position) {
        DiscoverInfo discoverInfo = MineApp.discoverInfoList.get(position);
        new ReviewPW(mBinding.getRoot(), discoverInfo.getFriendDynId(), discoverInfo.getReviews(), () -> {
            discoverInfo.setReviews(discoverInfo.getReviews() + 1);
            tvReviews.setText(ObjectUtils.count(discoverInfo.getReviews()));
            reviewList.clear();
            reviewAdapter.notifyDataSetChanged();
            seeReviews(1);
        }).setMainId(discoverInfo.getUserId());
    }

    @Override
    public void doGood(int position) {
        DiscoverInfo discoverInfo = MineApp.discoverInfoList.get(position);
        if (GoodDb.getInstance().hasGood(discoverInfo.getFriendDynId())) {
            ivUnLike.setVisibility(View.VISIBLE);
            ivLike.setVisibility(View.GONE);
            likeOrNot(ivUnLike);
            dynCancelLike(discoverInfo);
        } else {
            ivUnLike.setVisibility(View.GONE);
            ivLike.setVisibility(View.VISIBLE);
            likeOrNot(ivLike);
            GoodDb.getInstance().saveGood(new CollectID(discoverInfo.getFriendDynId()));
            dynDoLike(discoverInfo);
        }
    }

    @Override
    public void doReward(DiscoverInfo discoverInfo) {
        new GiftPW(mBinding.getRoot(), giftInfo ->
                new GiftPayPW(mBinding.getRoot(), giftInfo, discoverInfo.getFriendDynId(), 0, () -> {
                }));
    }

    @Override
    public void follow(DiscoverInfo discoverInfo) {
        if (layout.getVisibility() == View.INVISIBLE) {
            attentionOther(discoverInfo);
        } else {
            cancelAttention(discoverInfo);
        }
    }

    @Override
    public void more(DiscoverInfo discoverInfo) {
        memberInfoConfApi api = new memberInfoConfApi(new HttpOnNextListener<ShareInfo>() {
            @Override
            public void onNext(ShareInfo o) {
                String sharedName = o.getText().replace("{userId}", discoverInfo.getUserId() + "").replace("{nick}", discoverInfo.getNick());
                String content = discoverInfo.getText().isEmpty() ? discoverInfo.getFriendTitle() : discoverInfo.getText();
                String sharedUrl = HttpManager.BASE_URL + "mobile/Dyn_dynDetail?friendDynId=" + discoverInfo.getFriendDynId();
                new FunctionPW(mBinding.getRoot(), discoverInfo.getImage().replace("YM0000", "430X430"), sharedName, content, sharedUrl,
                        discoverInfo.getUserId() == BaseActivity.userId, true, true, true, new FunctionPW.CallBack() {
                    @Override
                    public void gift() {
                        // ????????????
                        videoView.pause();
                        reviewListView.stop();
                        ActivityUtils.getHomeRewardList(discoverInfo.getFriendDynId(), 0);
                    }

                    @Override
                    public void delete() {
                    }

                    @Override
                    public void report() {
                        // ??????
                        videoView.pause();
                        reviewListView.stop();
                        ActivityUtils.getHomeReport(discoverInfo.getUserId());
                    }

                    @Override
                    public void download() {
                        if (checkPermissionGranted(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            setPermissions();
                        } else {
                            if (PreferenceUtil.readIntValue(activity, "downPermission") == 0) {
                                PreferenceUtil.saveIntValue(activity, "downPermission", 1);
                                new TextPW(activity, mBinding.getRoot(), "????????????",
                                        "?????????????????????????????????????????????????????????" +
                                                "\n 1?????????????????????--???????????????????????????" +
                                                "\n 2??????????????????????????????????????????????????????????????????????????????????????????????????????" +
                                                "\n 3????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????/?????????" +
                                                "\n 4????????????????????????????????????--??????--??????--????????????app????????????--??????--????????????--????????????????????????????????????????????????",
                                        "??????", false, true, VideoListViewModel.this::getPermissions);
                            } else {
                                SCToastUtil.showToast(activity, "??????????????????????????????????????????--??????--????????????--??????????????????", true);
                            }
                        }
                    }

                    @Override
                    public void like() {
                        // ????????????
                        if (MineApp.mineInfo.getMemberType() == 2) {
                            makeEvaluate(discoverInfo);
                        } else {
                            new VipAdPW(mBinding.getRoot(), 3, discoverInfo.getImage());
                        }
                    }
                });
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void attentionStatus(DiscoverInfo discoverInfo) {
        attentionStatusApi api = new attentionStatusApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                if (o == null) {
                    layout.setVisibility(View.INVISIBLE);
                    AttentionDb.getInstance().saveAttention(new AttentionInfo(discoverInfo.getUserId(), discoverInfo.getNick(), discoverInfo.getImage(), true, BaseActivity.userId));
                } else {
                    layout.setVisibility(View.VISIBLE);
                    AttentionDb.getInstance().saveAttention(new AttentionInfo(discoverInfo.getUserId(), discoverInfo.getNick(), discoverInfo.getImage(), false, BaseActivity.userId));
                }
            }
        }, activity).setOtherUserId(discoverInfo.getUserId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    public void attentionOther(DiscoverInfo discoverInfo) {
        if (discoverInfo == null) return;
        attentionOtherApi api = new attentionOtherApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                isAttention(layout, ivAttention);
                AttentionDb.getInstance().saveAttention(new AttentionInfo(discoverInfo.getUserId(), discoverInfo.getNick(), discoverInfo.getImage(), true, BaseActivity.userId));
                Intent data = new Intent("lobster_attentionList");
                data.putExtra("isAdd", true);
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.ERROR) {
                    if (e.getMessage().equals("???????????????")) {
                        isAttention(layout, ivAttention);
                        AttentionDb.getInstance().saveAttention(new AttentionInfo(discoverInfo.getUserId(), discoverInfo.getNick(), discoverInfo.getImage(), true, BaseActivity.userId));
                        Intent data = new Intent("lobster_attentionList");
                        data.putExtra("isAdd", true);
                        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
                    }
                }
            }
        }, activity).setOtherUserId(discoverInfo.getUserId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void cancelAttention(DiscoverInfo discoverInfo) {
        if (discoverInfo == null) return;
        cancelAttentionApi api = new cancelAttentionApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                layout.setVisibility(View.VISIBLE);
                ivAttention.setBackgroundResource(R.drawable.attention_icon);
                AttentionDb.getInstance().saveAttention(new AttentionInfo(discoverInfo.getUserId(), discoverInfo.getNick(), discoverInfo.getImage(), false, BaseActivity.userId));
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_attentionList"));
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.ERROR) {
                    if (e.getMessage().equals("?????????????????????")) {
                        layout.setVisibility(View.VISIBLE);
                        ivAttention.setBackgroundResource(R.drawable.attention_icon);
                        AttentionDb.getInstance().saveAttention(new AttentionInfo(discoverInfo.getUserId(), discoverInfo.getNick(), discoverInfo.getImage(), false, BaseActivity.userId));
                        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_attentionList"));
                    }
                }
            }
        }, activity).setOtherUserId(discoverInfo.getUserId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void makeEvaluate(DiscoverInfo discoverInfo) {
        makeEvaluateApi api = new makeEvaluateApi(new HttpOnNextListener<Integer>() {
            @Override
            public void onNext(Integer o) {
                // 1???????????? 2???????????? 3??????????????????
                String myHead = MineApp.mineInfo.getImage();
                String otherHead = discoverInfo.getImage();
                if (o == 1) {
                    LikeTypeDb.getInstance().setType(discoverInfo.getUserId(), 2);
                    new SuperLikePW(mBinding.getRoot(), myHead, otherHead, MineApp.mineInfo.getSex(), MineApp.mineInfo.getSex() == 1 ? 0 : 1);
                } else if (o == 4) {
                    SCToastUtil.showToast(activity, "?????????????????????????????????", true);
                } else {
                    LikeTypeDb.getInstance().setType(discoverInfo.getUserId(), 2);
                    SCToastUtil.showToast(activity, "???????????????????????????", true);
                }
            }
        }, activity).setOtherUserId(discoverInfo.getUserId()).setLikeOtherStatus(2);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void dynDoLike(DiscoverInfo discoverInfo) {
        dynDoLikeApi api = new dynDoLikeApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                int goodNum = discoverInfo.getGoodNum() + 1;
                discoverInfo.setGoodNum(goodNum);
                tvGood.setText(discoverInfo.getGoodNumStr());

                Intent data = new Intent("lobster_doGood");
                data.putExtra("goodNum", goodNum);
                data.putExtra("friendDynId", discoverInfo.getFriendDynId());
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
                reviewList.clear();
                reviewAdapter.notifyDataSetChanged();
                seeReviews(1);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == 0) {
                    if (TextUtils.equals(e.getMessage(), "???????????????")) {
                        Intent data = new Intent("lobster_doGood");
                        data.putExtra("goodNum", discoverInfo.getGoodNum());
                        data.putExtra("friendDynId", discoverInfo.getFriendDynId());
                        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
                    }
                }
            }
        }, activity).setFriendDynId(discoverInfo.getFriendDynId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void dynCancelLike(DiscoverInfo discoverInfo) {
        dynCancelLikeApi api = new dynCancelLikeApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                GoodDb.getInstance().deleteGood(discoverInfo.getFriendDynId());
                int goodNum = discoverInfo.getGoodNum() - 1;
                discoverInfo.setGoodNum(goodNum);
                tvGood.setText(discoverInfo.getGoodNumStr());

                Intent data = new Intent("lobster_doGood");
                data.putExtra("goodNum", goodNum);
                data.putExtra("friendDynId", discoverInfo.getFriendDynId());
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == 0) {
                    if (TextUtils.equals(e.getMessage(), "???????????????")) {
                        GoodDb.getInstance().deleteGood(discoverInfo.getFriendDynId());
                        Intent data = new Intent("lobster_doGood");
                        data.putExtra("goodNum", discoverInfo.getGoodNum());
                        data.putExtra("friendDynId", discoverInfo.getFriendDynId());
                        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
                    }
                }
            }

        }, activity).setFriendDynId(discoverInfo.getFriendDynId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    private VideoView videoView;
    private ImageView ivProgress;
    private ObjectAnimator animator;
    private DiscoverInfo discoverInfo;
    private ImageView ivUnLike, ivLike;
    private TextView tvGood;
    private TextView tvReviews;
    private ImageView ivAttention;
    private ImageView ivImage;
    private ImageView ivPlay;
    private AutoPollRecyclerView reviewListView;
    private HomeAdapter reviewAdapter;
    private List<Review> reviewList = new ArrayList<>();
    private VideoView lastVideoView;
    private RelativeLayout layout;

    private void playVideo(View view) {
        if (lastVideoView != null) {
            lastVideoView.stopPlayback();//??????????????????,????????????
            lastVideoView.suspend();//???????????????????????????????????????
            lastVideoView = null;
        }
        discoverInfo = MineApp.discoverInfoList.get(position);
        dynVisit(discoverInfo.getFriendDynId());
        videoView = view.findViewById(R.id.video_view);
        ivProgress = view.findViewById(R.id.iv_progress);
        ivUnLike = view.findViewById(R.id.iv_unLike);
        ivLike = view.findViewById(R.id.iv_like);
        tvGood = view.findViewById(R.id.tv_good);
        tvReviews = view.findViewById(R.id.tv_reviews);
        layout = view.findViewById(R.id.attention_layout);
        ivAttention = view.findViewById(R.id.iv_attention);
        ivImage = view.findViewById(R.id.iv_image);
        ivPlay = view.findViewById(R.id.iv_play);
        View viewClick = view.findViewById(R.id.view_click);
        ImageView ivGood = view.findViewById(R.id.iv_good);
        reviewListView = view.findViewById(R.id.review_list);

        attentionStatus(discoverInfo);
        animator = ObjectAnimator.ofFloat(ivProgress, "rotation", 0, 360).setDuration(700);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(Animation.INFINITE);
        animator.start();
        ivProgress.setVisibility(View.VISIBLE);
        ivImage.setVisibility(View.VISIBLE);
        videoView.setBackgroundColor(activity.getResources().getColor(R.color.black_252));
        ivLike.setVisibility(View.GONE);
        ivUnLike.setVisibility(View.GONE);
        if (GoodDb.getInstance().hasGood(discoverInfo.getFriendDynId())) {
            ivLike.setVisibility(View.VISIBLE);
        } else {
            ivUnLike.setVisibility(View.VISIBLE);
        }
        ivPlay.setVisibility(View.GONE);
        if (reviewAdapter == null)
            reviewAdapter = new HomeAdapter<>(activity, R.layout.item_auto_review, reviewList, this);
        reviewAdapter.setMax(true);
        reviewAdapter.notifyDataSetChanged();
        reviewListView.setAdapter(reviewAdapter);
        ScrollSpeedLinearLayoutManger layoutManager1 = new ScrollSpeedLinearLayoutManger(view.getContext());
        layoutManager1.setSmoothScrollbarEnabled(true);
        layoutManager1.setAutoMeasureEnabled(true);
        reviewListView.setLayoutManager(layoutManager1);// ??????????????????
        reviewListView.setHasFixedSize(true);// ??????Item???????????????????????????????????????FixSize??????????????????

        DownLoad.getFilePath(discoverInfo.getVideoUrl(), BaseActivity.getDownloadFile(".mp4").getAbsolutePath(), (filePath, bitmap) -> {
            discoverInfo.setVideoPath(filePath);
            ivProgress.setVisibility(View.GONE);
            if (animator != null)
                animator.cancel();
            animator = null;
            initVideo();
            lastVideoView = videoView;
        });

        initGood(viewClick, ivGood, () -> videoPlay(discoverInfo), () -> {
            if (!GoodDb.getInstance().hasGood(discoverInfo.getFriendDynId())) {
                GoodDb.getInstance().saveGood(new CollectID(discoverInfo.getFriendDynId()));
                ivUnLike.setVisibility(View.GONE);
                ivLike.setVisibility(View.VISIBLE);
                dynDoLike(discoverInfo);
            }
        });
    }

    private void dynVisit(long friendDynId) {
        dynVisitApi api = new dynVisitApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {

            }
        }, activity).setFriendDynId(friendDynId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void seeReviews(int pageNo) {
        seeReviewsApi api = new seeReviewsApi(new HttpOnNextListener<List<Review>>() {
            @Override
            public void onNext(List<Review> o) {
                if (canUpdate) {
                    reviewListView.setVisibility(View.VISIBLE);
                    int start = reviewList.size();
                    for (Review item : o) {
                        item.setType(2);
                        reviewList.add(item);
                    }
                    reviewAdapter.notifyItemRangeChanged(start, reviewList.size());
                    if (reviewList.size() > 2) {
                        reviewListView.setLayoutParams(new RelativeLayout.LayoutParams(-2, ObjectUtils.getViewSizeByWidthFromMax(400)));
                    } else {
                        reviewListView.setLayoutParams(new RelativeLayout.LayoutParams(-2, -2));
                    }
                    seeLikers(1);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    seeLikers(1);
                }
            }
        }, activity).setFriendDynId(discoverInfo.getFriendDynId()).setTimeSortType(1).setPageNo(pageNo).setRow(10);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void seeLikers(int pageNo) {
        seeLikersApi api = new seeLikersApi(new HttpOnNextListener<List<Review>>() {
            @Override
            public void onNext(List<Review> o) {
                if (canUpdate) {
                    reviewListView.setVisibility(View.VISIBLE);
                    int start = reviewList.size();
                    for (Review item : o) {
                        item.setType(1);
                        reviewList.add(item);
                    }
                    reviewAdapter.notifyItemRangeChanged(start, reviewList.size());
                    if (reviewList.size() > 2) {
                        reviewListView.setLayoutParams(new RelativeLayout.LayoutParams(-2, ObjectUtils.getViewSizeByWidthFromMax(400)));
                        reviewListView.start();
                    } else {
                        reviewListView.setLayoutParams(new RelativeLayout.LayoutParams(-2, -2));
                    }
                }
            }
        }, activity).setFriendDynId(discoverInfo.getFriendDynId()).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    public void onResume() {
        if (discoverInfo != null) {
//            videoView.setVideoPath(discoverInfo.getVideoPath());
            videoView.start();
        }
        reviewListView.start();
        ivPlay.setVisibility(View.GONE);
    }

    private void initVideo() {
        //??????????????????????????????
        videoView.setOnCompletionListener(mp -> {
            videoView.stopPlayback();//??????????????????,????????????
            videoView.suspend();//???????????????????????????????????????
        });
        //????????????
        videoView.setOnErrorListener((mp, what, extra) -> {
            return true;//????????????????????????????????????true????????????false?????????false???????????????OnErrorListener??????????????????OnCompletionListener???
        });
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);//?????????????????????
        });
        //????????????
        videoView.setOnInfoListener((mp, what, extra) -> {
            if (what == MediaPlayer.MEDIA_INFO_UNKNOWN || what == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
                SCToastUtil.showToast(activity, "??????????????????", true);
                videoView.stopPlayback();//??????????????????,????????????
                videoView.suspend();//???????????????????????????????????????
                return true;
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                // ????????????,????????????????????????START?????????END,?????????????????????????????????????????????????????????????????????
                if (mp.isPlaying()) {
                    ivProgress.setVisibility(View.GONE);
                }
                return true;
            } else if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                changeVideoSize(mp);
                duration = mp.getDuration();
                ivProgress.setVisibility(View.GONE);
                ivImage.setVisibility(View.GONE);
                videoView.setBackgroundColor(Color.TRANSPARENT);
                reviewList.clear();
                reviewAdapter.notifyDataSetChanged();
                canUpdate = true;
                seeReviews(1);
            }
            return false; //????????????????????????????????????true????????????????????????false?????????false???????????????OnInfoListener??????????????????????????????
        });
        MineApp.getApp().getFixedThreadPool().execute(() -> {
            SystemClock.sleep(200);
            activity.runOnUiThread(() -> {
                ivPlay.setVisibility(View.GONE);
                videoView.setVideoPath(discoverInfo.getVideoPath());
                videoView.start();
            });
        });
    }

    /**
     * ????????????View?????????,?????????????????????
     */
    private void changeVideoSize(@NonNull MediaPlayer mMediaPlayer) {
        videoWidth = mMediaPlayer.getVideoWidth();
        videoHeight = mMediaPlayer.getVideoHeight();

        if (ObjectUtils.getViewSizeByHeight(1.0f) * videoWidth / videoHeight > MineApp.W) {
            AdapterBinding.viewSize(videoView, MineApp.W, (MineApp.W * videoHeight / videoWidth));
        } else {
            AdapterBinding.viewSize(videoView, (ObjectUtils.getViewSizeByHeight(1.0f) * videoWidth / videoHeight), ObjectUtils.getViewSizeByHeight(1.0f));
        }
    }

    /**
     * ??????
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("??????????????????????????????", new BaseActivity.PermissionCallback() {
                @Override
                public void hasPermission() {
                    setPermissions();
                }

                @Override
                public void noPermission() {
                    PreferenceUtil.saveIntValue(activity, "downPermission", 1);
                }
            }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            setPermissions();
        }
    }


    private void setPermissions() {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());
        if (!file.exists()) {
            file.mkdirs();
        }
        String filePath = file.getAbsolutePath() + "/Camera/xg_" + BaseActivity.randomString(15) + ".mp4";
        DownLoad.downloadVideoByLocation(discoverInfo.getVideoUrl(), filePath, (filePath1, bitmap) -> {
            WaterMark.getInstance().createWater(activity, filePath1, discoverInfo.getUserId(), videoWidth, videoHeight, duration);
        });

    }
}
