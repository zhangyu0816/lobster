package com.zb.module_home.vm;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.api.attentionOtherApi;
import com.zb.lib_base.api.attentionStatusApi;
import com.zb.lib_base.api.cancelAttentionApi;
import com.zb.lib_base.api.deleteDynApi;
import com.zb.lib_base.api.dynCancelLikeApi;
import com.zb.lib_base.api.dynDoLikeApi;
import com.zb.lib_base.api.dynPiazzaListApi;
import com.zb.lib_base.api.makeEvaluateApi;
import com.zb.lib_base.api.seeLikersApi;
import com.zb.lib_base.api.seeReviewsApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.http.CustomProgressDialog;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.AttentionInfo;
import com.zb.lib_base.model.CollectID;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.Review;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DataCleanManager;
import com.zb.lib_base.utils.DouYinLayoutManager;
import com.zb.lib_base.utils.DownLoad;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.OnViewPagerListener;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.ScrollSpeedLinearLayoutManger;
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

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.OrientationHelper;
import io.realm.Realm;

public class VideoListViewModel extends BaseViewModel implements VideoListVMInterface {
    public int position;
    public int pageNo;
    private HomeAdapter adapter;
    private DouYinLayoutManager douYinLayoutManager;
    private HomeVideoListBinding mBinding;
    private boolean isUp = false;
    private boolean isOver = false;
    private MineInfo mineInfo;
    private BaseReceiver attentionReceiver;
    private String downloadPath = "";
    private int videoWidth, videoHeight;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (HomeVideoListBinding) binding;
        areaDb = new AreaDb(Realm.getDefaultInstance());
        mineInfo = mineInfoDb.getMineInfo();
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
        attentionReceiver.unregisterReceiver();
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        adapter = new HomeAdapter<>(activity, R.layout.item_video_l2, MineApp.discoverInfoList, this);

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
                isUp = douYinLayoutManager.getDrift() >= 0;
                position = douYinLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (position == -1)
                    return;
                playVideo(view);
                if (!isOver && position == MineApp.discoverInfoList.size() - 1 && isUp) {
                    pageNo++;
                    dynPiazzaList();
                }
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
                if (isUp) {
                    // 上滑  底部加载更多
                    int start = MineApp.discoverInfoList.size();
                    MineApp.discoverInfoList.addAll(o);
                    adapter.notifyItemRangeChanged(start, MineApp.discoverInfoList.size());

                    for (DiscoverInfo item : o) {
                        DownLoad.getFilePath(item.getVideoUrl(), BaseActivity.getDownloadFile(".mp4").getAbsolutePath(), filePath -> {
                        });
                    }
                } else {
                    // 下滑  顶部加载更多
                    MineApp.discoverInfoList.addAll(0, o);
                    adapter.notifyItemRangeChanged(0, o.size());
                    mBinding.videoList.scrollToPosition(o.size());
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
                .setCityId(areaDb.getCityId(MineApp.cityName))
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
        new ReviewPW(activity, mBinding.getRoot(), discoverInfo.getFriendDynId(), discoverInfo.getReviews(), () -> {
            discoverInfo.setReviews(discoverInfo.getReviews() + 1);
            tvReviews.setText(ObjectUtils.count(discoverInfo.getReviews()));
            seeLikers(1);
        });
    }

    @Override
    public void doGood(int position) {
        DiscoverInfo discoverInfo = MineApp.discoverInfoList.get(position);
        if (goodDb.hasGood(discoverInfo.getFriendDynId())) {
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
        new GiftPW(activity, mBinding.getRoot(), giftInfo ->
                new GiftPayPW(activity, mBinding.getRoot(), giftInfo, discoverInfo.getFriendDynId(), () -> {
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
        String sharedName = discoverInfo.getNick();
        String content = discoverInfo.getText();
        String sharedUrl = HttpManager.BASE_URL + "mobile/Dyn_dynDetail?friendDynId=" + discoverInfo.getFriendDynId();
        new FunctionPW(activity, mBinding.getRoot(), discoverInfo.getImage().replace("YM0000", "430X430"), sharedName, content, sharedUrl,
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
                DownLoad.downloadLocation(discoverInfo.getVideoUrl(), filePath -> {
                    downloadPath = filePath;
                    createWater();
                });
            }

            @Override
            public void like() {
                // 超级喜欢
                if (mineInfo.getMemberType() == 2) {
                    makeEvaluate(discoverInfo);
                } else {
                    new VipAdPW(activity, mBinding.getRoot(), false, 3, discoverInfo.getImage());
                }
            }
        });
    }

    private void attentionStatus(DiscoverInfo discoverInfo) {
        attentionStatusApi api = new attentionStatusApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                if (o == null) {
                    ivAttention.setVisibility(View.INVISIBLE);
                    attentionDb.saveAttention(new AttentionInfo(discoverInfo.getUserId(), discoverInfo.getNick(), discoverInfo.getImage(), true, BaseActivity.userId));
                } else {
                    ivAttention.setVisibility(View.VISIBLE);
                    attentionDb.saveAttention(new AttentionInfo(discoverInfo.getUserId(), discoverInfo.getNick(), discoverInfo.getImage(), false, BaseActivity.userId));
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
                attentionDb.saveAttention(new AttentionInfo(discoverInfo.getUserId(), discoverInfo.getNick(), discoverInfo.getImage(), true, BaseActivity.userId));
                activity.sendBroadcast(new Intent("lobster_attentionList"));
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.ERROR) {
                    if (e.getMessage().equals("已经关注过")) {
                        ivAttention.setVisibility(View.INVISIBLE);
                        attentionDb.saveAttention(new AttentionInfo(discoverInfo.getUserId(), discoverInfo.getNick(), discoverInfo.getImage(), true, BaseActivity.userId));
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
                attentionDb.saveAttention(new AttentionInfo(discoverInfo.getUserId(), discoverInfo.getNick(), discoverInfo.getImage(), false, BaseActivity.userId));
                activity.sendBroadcast(new Intent("lobster_attentionList"));
            }
        }, activity).setOtherUserId(discoverInfo.getUserId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void deleteDyn(DiscoverInfo discoverInfo) {
        deleteDynApi api = new deleteDynApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                activity.sendBroadcast(new Intent("lobster_publish"));
                SCToastUtil.showToast(activity, "删除成功", true);
                adapter.notifyItemRemoved(position);
                MineApp.discoverInfoList.remove(position);
            }
        }, activity).setFriendDynId(discoverInfo.getFriendDynId());
        HttpManager.getInstance().doHttpDeal(api);

    }

    private void makeEvaluate(DiscoverInfo discoverInfo) {
        makeEvaluateApi api = new makeEvaluateApi(new HttpOnNextListener<Integer>() {
            @Override
            public void onNext(Integer o) {
                // 1喜欢成功 2匹配成功 3喜欢次数用尽
                String myHead = mineInfo.getImage();
                String otherHead = discoverInfo.getImage();
                if (o == 1) {
                    likeTypeDb.setType(discoverInfo.getUserId(), 2);
                    new SuperLikePW(activity, mBinding.getRoot(), myHead, otherHead, false, mineInfo.getSex(), mineInfo.getSex() == 1 ? 0 : 1, null);
                } else if (o == 4) {
                    SCToastUtil.showToast(activity, "今日超级喜欢次数已用完", true);
                } else {
                    likeTypeDb.setType(discoverInfo.getUserId(), 2);
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
                goodDb.saveGood(new CollectID(discoverInfo.getFriendDynId()));
                int goodNum = discoverInfo.getGoodNum() + 1;
                discoverInfo.setGoodNum(goodNum);
                tvGood.setText(discoverInfo.getGoodNumStr());

                Intent data = new Intent("lobster_doGood");
                data.putExtra("goodNum", goodNum);
                data.putExtra("friendDynId", discoverInfo.getFriendDynId());
                activity.sendBroadcast(data);
                seeLikers(1);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == 0) {
                    if (TextUtils.equals(e.getMessage(), "已经赞过了")) {
                        goodDb.saveGood(new CollectID(discoverInfo.getFriendDynId()));
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
                goodDb.deleteGood(discoverInfo.getFriendDynId());
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
                        goodDb.deleteGood(discoverInfo.getFriendDynId());
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
    private View viewClick;
    private ImageView ivGood;
    private AutoPollRecyclerView reviewListView;
    private HomeAdapter reviewAdapter;
    private List<Review> reviewList = new ArrayList<>();
    private List<Review> tempList = new ArrayList<>();
    private VideoView lastVideoView;

    private void playVideo(View view) {
        if (lastVideoView != null) {
            lastVideoView.stopPlayback();//停止播放视频,并且释放
            lastVideoView.suspend();//在任何状态下释放媒体播放器
            lastVideoView = null;
        }
        discoverInfo = MineApp.discoverInfoList.get(position);

        videoView = view.findViewById(R.id.video_view);
        ivProgress = view.findViewById(R.id.iv_progress);
        ivUnLike = view.findViewById(R.id.iv_unLike);
        ivLike = view.findViewById(R.id.iv_like);
        tvGood = view.findViewById(R.id.tv_good);
        tvReviews = view.findViewById(R.id.tv_reviews);
        ivAttention = view.findViewById(R.id.iv_attention);
        ivImage = view.findViewById(R.id.iv_image);
        ivPlay = view.findViewById(R.id.iv_play);
        viewClick = view.findViewById(R.id.view_click);
        ivGood = view.findViewById(R.id.iv_good);
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
        if (goodDb.hasGood(discoverInfo.getFriendDynId())) {
            ivLike.setVisibility(View.VISIBLE);
        } else {
            ivUnLike.setVisibility(View.VISIBLE);
        }
        ivPlay.setVisibility(View.GONE);
        if (reviewAdapter == null)
            reviewAdapter = new HomeAdapter<>(activity, R.layout.item_auto_review, reviewList, this);
        reviewList.clear();
        reviewAdapter.setMax(true);
        reviewAdapter.notifyDataSetChanged();
        reviewListView.setAdapter(reviewAdapter);
        ScrollSpeedLinearLayoutManger layoutManager1 = new ScrollSpeedLinearLayoutManger(view.getContext());
        layoutManager1.setSmoothScrollbarEnabled(true);
        layoutManager1.setAutoMeasureEnabled(true);
        reviewListView.setLayoutManager(layoutManager1);// 布局管理器。
        reviewListView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。


        DownLoad.getFilePath(discoverInfo.getVideoUrl(), BaseActivity.getDownloadFile(".mp4").getAbsolutePath(), filePath -> {
            discoverInfo.setVideoPath(filePath);
            ivProgress.setVisibility(View.GONE);
            if (animator != null)
                animator.cancel();
            initVideo();
            lastVideoView = videoView;
        });

        initGood(viewClick, ivGood, () -> videoPlay(discoverInfo), () -> {
            if (!goodDb.hasGood(discoverInfo.getFriendDynId())) {
                ivUnLike.setVisibility(View.GONE);
                ivLike.setVisibility(View.VISIBLE);
                dynDoLike(discoverInfo);
            }
        });
    }

    private void seeLikers(int pageNo) {
        seeLikersApi api = new seeLikersApi(new HttpOnNextListener<List<Review>>() {
            @Override
            public void onNext(List<Review> o) {
                for (Review item : o) {
                    item.setType(1);
                    tempList.add(item);
                }
                seeLikers(pageNo + 1);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    seeReviews(1);
                }
            }
        }, activity).setFriendDynId(discoverInfo.getFriendDynId()).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void seeReviews(int pageNo) {
        seeReviewsApi api = new seeReviewsApi(new HttpOnNextListener<List<Review>>() {
            @Override
            public void onNext(List<Review> o) {
                for (Review item : o) {
                    item.setType(2);
                    tempList.add(item);
                }
                seeReviews(pageNo + 1);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    Collections.sort(tempList, new CreateTimeComparator());
                    if (tempList.size() > 0) {
                        reviewList.clear();
                        reviewAdapter.notifyDataSetChanged();
                        reviewList.addAll(tempList);
                        tempList.clear();
                        reviewListView.setVisibility(View.VISIBLE);
                        if (reviewList.size() > 2) {
                            reviewListView.setLayoutParams(new RelativeLayout.LayoutParams(-2, ObjectUtils.getViewSizeByWidthFromMax(335)));
                            reviewListView.start();
                        } else {
                            reviewListView.setLayoutParams(new RelativeLayout.LayoutParams(-2, -2));
                        }
                        reviewAdapter.notifyDataSetChanged();
                    }
                }
            }
        }, activity).setFriendDynId(discoverInfo.getFriendDynId()).setTimeSortType(1).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    public void onResume() {
        videoView.setVideoPath(discoverInfo.getVideoPath());
        videoView.start();
        reviewListView.start();
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
                seeLikers(1);
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

    /*****************水印功能******************/

    /* 水印 */
    private String outPutUrl = "";
    private String imageUrl = "";

    /**
     * 文本转成Bitmap
     *
     * @param text 文本内容
     * @return 图片的bitmap
     */
    private Bitmap textToBitmap(String text) {

        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(videoWidth, videoHeight);
        layout.setLayoutParams(layoutParams);
        layout.setBackgroundColor(Color.TRANSPARENT);

        ImageView iv = new ImageView(activity);

        float ra = 0;
        if (videoHeight > videoWidth)
            ra = (float) videoWidth / (float) MineApp.W;
        else
            ra = (float) videoHeight / (float) MineApp.W;
        int w = (int) (87f * 2 * ra);
        int h = (int) (39f * 2 * ra);
        int size = (int) (7f * 2 * ra);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w, h);
        params.leftMargin = 0;
        params.rightMargin = MineApp.W;
        params.gravity = Gravity.START;
        iv.setLayoutParams(params);
        iv.setImageResource(R.mipmap.water_icon);
        layout.addView(iv);

        TextView tv = new TextView(activity);
        tv.setText(text);
        tv.setTextSize(size);
        tv.setTextColor(Color.WHITE);
        tv.setShadowLayer(1, 2f, 2f, R.color.black);
        tv.setBackgroundColor(Color.TRANSPARENT);
        layout.addView(tv);

        layout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        layout.layout(0, 0, layout.getMeasuredWidth(), layout.getMeasuredHeight());
        layout.buildDrawingCache();
        Bitmap bitmap = layout.getDrawingCache();
        return Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false);
    }

    private void getImage(Bitmap bitmap) {
        try {
            FileOutputStream os = new FileOutputStream(new File(imageUrl));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] addWaterMark(String imageUrl, String videoUrl, String outputUrl) {
        String content = "-i " + videoUrl +
                " -i " + imageUrl + " -filter_complex overlay=10:10" +
                " -y -strict -2 -vcodec libx264 -preset ultrafast -crf 10 -threads 2 -acodec aac -ar 44100 -ac 2 -b:a 32k " + outputUrl;
        //-crf  用于指定输出视频的质量，取值范围是0-51，默认值为23，数字越小输出视频的质量越高。
        // 这个选项会直接影响到输出视频的码率。一般来说，压制480p我会用20左右，压制720p我会用16-18
        return content.split(" ");
    }

    // 添加水印
    private void createWater() {
        CustomProgressDialog.showLoading(activity, "正在处理视频");
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());
        if (!file.exists()) {
            file.mkdirs();
        }
        outPutUrl = file.getAbsolutePath() + "/Camera/xg_" + BaseActivity.randomString(15) + ".mp4";
        imageUrl = BaseActivity.getImageFile().getAbsolutePath();
        Bitmap bitmap = textToBitmap("我的虾菇号：" + discoverInfo.getUserId());
        getImage(bitmap);

        String[] common = addWaterMark(imageUrl, downloadPath, outPutUrl);
        FFmpeg.getInstance(activity).execute(common, new FFmpegExecuteResponseHandler() {
            @Override
            public void onSuccess(String message) {
                handler.sendEmptyMessage(1);
            }

            @Override
            public void onProgress(String message) {
            }

            @Override
            public void onFailure(String message) {
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
            }
        });
    }

    private Handler handler = new Handler(msg -> {
        switch (msg.what) {
            case 0:
                SCToastUtil.showToast(activity, "视频下载失败", true);
                CustomProgressDialog.stopLoading();
                break;
            case 1:
                DataCleanManager.deleteFile(new File(downloadPath));
                // 最后通知图库更新
                MineApp.getInstance().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://" + outPutUrl)));
                SCToastUtil.showToast(activity, "下载成功", true);
                CustomProgressDialog.stopLoading();
                break;
        }
        return false;
    });
}
