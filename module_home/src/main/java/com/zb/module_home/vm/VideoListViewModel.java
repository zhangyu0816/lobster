package com.zb.module_home.vm;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
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
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.glide.DouYinLayoutManager;
import com.zb.lib_base.utils.glide.OnViewPagerListener;
import com.zb.lib_base.utils.glide.ScrollSpeedLinearLayoutManger;
import com.zb.lib_base.utils.water.WaterMark;
import com.zb.lib_base.views.AutoPollRecyclerView;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.FunctionPW;
import com.zb.lib_base.windows.SuperLikePW;
import com.zb.lib_base.windows.VipAdPW;
import com.zb.module_home.R;
import com.zb.module_home.adapter.HomeAdapter;
import com.zb.module_home.databinding.HomeVideoListBinding;
import com.zb.module_home.iv.VideoListVMInterface;
import com.zb.module_home.windows.GiftPW;
import com.zb.module_home.windows.GiftPayPW;
import com.zb.module_home.windows.ReviewPW;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
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
    private String downloadPath = "";
    private int videoWidth, videoHeight;
    private boolean canUpdate = false;

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
                    ivAttention.setVisibility(View.INVISIBLE);
                } else {
                    ivAttention.setVisibility(View.VISIBLE);
                }
            }
        };
    }

    public void onDestroy() {
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
                videoView.stopPlayback();//停止播放视频,并且释放
                videoView.suspend();//在任何状态下释放媒体播放器
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
            videoView.setVideoPath(discoverInfo.getVideoUrl());
            videoView.start();
            reviewListView.start();
        }
    }

    @Override
    public void dynPiazzaList() {
        dynPiazzaListApi api = new dynPiazzaListApi(new HttpOnNextListener<List<DiscoverInfo>>() {
            @Override
            public void onNext(List<DiscoverInfo> o) {
                // 上滑  底部加载更多
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
                    SCToastUtil.showToast(activity, "视频已加载完", true);
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
        });
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
            dynDoLike(discoverInfo);
        }
    }

    @Override
    public void doReward(DiscoverInfo discoverInfo) {
        new GiftPW(mBinding.getRoot(), giftInfo ->
                new GiftPayPW(mBinding.getRoot(), giftInfo, discoverInfo.getFriendDynId(), () -> {
                }));
    }

    @Override
    public void follow(DiscoverInfo discoverInfo) {
        if (ivAttention.getVisibility() == View.INVISIBLE) {
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
                        // 查看礼物
                        videoView.pause();
                        reviewListView.stop();
                        ActivityUtils.getHomeRewardList(discoverInfo.getFriendDynId());
                    }

                    @Override
                    public void delete() {
                    }

                    @Override
                    public void report() {
                        // 举报
                        videoView.pause();
                        reviewListView.stop();
                        ActivityUtils.getHomeReport(discoverInfo.getUserId());
                    }

                    @Override
                    public void download() {
                        DownLoad.downloadLocation(discoverInfo.getVideoUrl(), (filePath, bitmap) -> {
                            downloadPath = filePath;
                            getPermissions();
                        });
                    }

                    @Override
                    public void like() {
                        // 超级喜欢
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
                    ivAttention.setVisibility(View.INVISIBLE);
                    AttentionDb.getInstance().saveAttention(new AttentionInfo(discoverInfo.getUserId(), discoverInfo.getNick(), discoverInfo.getImage(), true, BaseActivity.userId));
                } else {
                    ivAttention.setVisibility(View.VISIBLE);
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
                ivAttention.setVisibility(View.INVISIBLE);
                AttentionDb.getInstance().saveAttention(new AttentionInfo(discoverInfo.getUserId(), discoverInfo.getNick(), discoverInfo.getImage(), true, BaseActivity.userId));
                activity.sendBroadcast(new Intent("lobster_attentionList"));
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.ERROR) {
                    if (e.getMessage().equals("已经关注过")) {
                        ivAttention.setVisibility(View.INVISIBLE);
                        AttentionDb.getInstance().saveAttention(new AttentionInfo(discoverInfo.getUserId(), discoverInfo.getNick(), discoverInfo.getImage(), true, BaseActivity.userId));
                        activity.sendBroadcast(new Intent("lobster_attentionList"));
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
                ivAttention.setVisibility(View.VISIBLE);
                AttentionDb.getInstance().saveAttention(new AttentionInfo(discoverInfo.getUserId(), discoverInfo.getNick(), discoverInfo.getImage(), false, BaseActivity.userId));
                activity.sendBroadcast(new Intent("lobster_attentionList"));
            }
        }, activity).setOtherUserId(discoverInfo.getUserId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void makeEvaluate(DiscoverInfo discoverInfo) {
        makeEvaluateApi api = new makeEvaluateApi(new HttpOnNextListener<Integer>() {
            @Override
            public void onNext(Integer o) {
                // 1喜欢成功 2匹配成功 3喜欢次数用尽
                String myHead = MineApp.mineInfo.getImage();
                String otherHead = discoverInfo.getImage();
                if (o == 1) {
                    LikeTypeDb.getInstance().setType(discoverInfo.getUserId(), 2);
                    new SuperLikePW(mBinding.getRoot(), myHead, otherHead, MineApp.mineInfo.getSex(), MineApp.mineInfo.getSex() == 1 ? 0 : 1);
                } else if (o == 4) {
                    SCToastUtil.showToast(activity, "今日超级喜欢次数已用完", true);
                } else {
                    LikeTypeDb.getInstance().setType(discoverInfo.getUserId(), 2);
                    SCToastUtil.showToast(activity, "你已超级喜欢过对方", true);
                }
            }
        }, activity).setOtherUserId(discoverInfo.getUserId()).setLikeOtherStatus(2);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void dynDoLike(DiscoverInfo discoverInfo) {
        dynDoLikeApi api = new dynDoLikeApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                GoodDb.getInstance().saveGood(new CollectID(discoverInfo.getFriendDynId()));
                int goodNum = discoverInfo.getGoodNum() + 1;
                discoverInfo.setGoodNum(goodNum);
                tvGood.setText(discoverInfo.getGoodNumStr());

                Intent data = new Intent("lobster_doGood");
                data.putExtra("goodNum", goodNum);
                data.putExtra("friendDynId", discoverInfo.getFriendDynId());
                activity.sendBroadcast(data);
                reviewList.clear();
                reviewAdapter.notifyDataSetChanged();
                seeReviews(1);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == 0) {
                    if (TextUtils.equals(e.getMessage(), "已经赞过了")) {
                        GoodDb.getInstance().saveGood(new CollectID(discoverInfo.getFriendDynId()));
                        Intent data = new Intent("lobster_doGood");
                        data.putExtra("goodNum", discoverInfo.getGoodNum());
                        data.putExtra("friendDynId", discoverInfo.getFriendDynId());
                        activity.sendBroadcast(data);
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
                activity.sendBroadcast(data);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == 0) {
                    if (TextUtils.equals(e.getMessage(), "已经取消过")) {
                        GoodDb.getInstance().deleteGood(discoverInfo.getFriendDynId());
                        Intent data = new Intent("lobster_doGood");
                        data.putExtra("goodNum", discoverInfo.getGoodNum());
                        data.putExtra("friendDynId", discoverInfo.getFriendDynId());
                        activity.sendBroadcast(data);
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

    private void playVideo(View view) {
        if (lastVideoView != null) {
            lastVideoView.stopPlayback();//停止播放视频,并且释放
            lastVideoView.suspend();//在任何状态下释放媒体播放器
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
        reviewListView.setLayoutManager(layoutManager1);// 布局管理器。
        reviewListView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。

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
                        reviewListView.setLayoutParams(new RelativeLayout.LayoutParams(-2, ObjectUtils.getViewSizeByWidthFromMax(335)));
                        reviewListView.start();
                    } else {
                        reviewListView.setLayoutParams(new RelativeLayout.LayoutParams(-2, -2));
                    }
                    seeReviews(pageNo + 1);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    seeLikers(1);
                }
            }
        }, activity).setFriendDynId(discoverInfo.getFriendDynId()).setTimeSortType(1).setPageNo(pageNo);
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
                        reviewListView.setLayoutParams(new RelativeLayout.LayoutParams(-2, ObjectUtils.getViewSizeByWidthFromMax(335)));
                        reviewListView.start();
                    } else {
                        reviewListView.setLayoutParams(new RelativeLayout.LayoutParams(-2, -2));
                    }
                    seeLikers(pageNo + 1);
                }
            }
        }, activity).setFriendDynId(discoverInfo.getFriendDynId()).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    public void onResume() {
        if (discoverInfo != null) {
            videoView.setVideoPath(discoverInfo.getVideoPath());
            videoView.start();
        }
        reviewListView.start();
        ivPlay.setVisibility(View.GONE);
    }

    private void initVideo() {
        //视频播放完成后的回调
        videoView.setOnCompletionListener(mp -> {
            videoView.stopPlayback();//停止播放视频,并且释放
            videoView.suspend();//在任何状态下释放媒体播放器
        });
        //异常回调
        videoView.setOnErrorListener((mp, what, extra) -> {
            return true;//如果方法处理了错误，则为true；否则为false。返回false或根本没有OnErrorListener，将导致调用OnCompletionListener。
        });
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);//让电影循环播放
        });
        //信息回调
        videoView.setOnInfoListener((mp, what, extra) -> {
            if (what == MediaPlayer.MEDIA_INFO_UNKNOWN || what == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
                SCToastUtil.showToast(activity, "视频播放失败", true);
                videoView.stopPlayback();//停止播放视频,并且释放
                videoView.suspend();//在任何状态下释放媒体播放器
                return true;
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                // 缓冲结束,此接口每次回调完START就回调END,若不加上判断就会出现缓冲图标一闪一闪的卡顿现象
                if (mp.isPlaying()) {
                    ivProgress.setVisibility(View.GONE);
                }
                return true;
            } else if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                changeVideoSize(mp);
                ivProgress.setVisibility(View.GONE);
                ivImage.setVisibility(View.GONE);
                videoView.setBackgroundColor(Color.TRANSPARENT);
                reviewList.clear();
                reviewAdapter.notifyDataSetChanged();
                canUpdate = true;
                seeReviews(1);
            }
            return false; //如果方法处理了信息，则为true；如果没有，则为false。返回false或根本没有OnInfoListener，将导致丢弃该信息。
        });
        new Handler().postDelayed(() -> {
            ivPlay.setVisibility(View.GONE);
            videoView.setVideoPath(discoverInfo.getVideoPath());
            videoView.start();
        }, 200);

    }

    /**
     * 修改预览View的大小,以用来适配屏幕
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
     * 权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("虾菇需要访问读写外部存储权限", new BaseActivity.PermissionCallback() {
                @Override
                public void hasPermission() {
                    setPermissions();
                }

                @Override
                public void noPermission() {
                }
            }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            setPermissions();
        }
    }


    private void setPermissions() {
        WaterMark.getInstance().createWater(activity, downloadPath, discoverInfo.getUserId(), videoWidth, videoHeight);
    }
}
