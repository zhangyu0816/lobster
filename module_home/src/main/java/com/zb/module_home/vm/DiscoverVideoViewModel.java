package com.zb.module_home.vm;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;

import com.umeng.socialize.media.UMImage;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.api.attentionOtherApi;
import com.zb.lib_base.api.attentionStatusApi;
import com.zb.lib_base.api.cancelAttentionApi;
import com.zb.lib_base.api.deleteDynApi;
import com.zb.lib_base.api.dynCancelLikeApi;
import com.zb.lib_base.api.dynDetailApi;
import com.zb.lib_base.api.dynDoLikeApi;
import com.zb.lib_base.api.makeEvaluateApi;
import com.zb.lib_base.api.otherInfoApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.GoodDb;
import com.zb.lib_base.db.LikeDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.AttentionInfo;
import com.zb.lib_base.model.CollectID;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DownLoad;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.CountUsedPW;
import com.zb.lib_base.windows.SelectorPW;
import com.zb.lib_base.windows.SharePW;
import com.zb.lib_base.windows.SuperLikePW;
import com.zb.lib_base.windows.TextPW;
import com.zb.lib_base.windows.VipAdPW;
import com.zb.module_home.BR;
import com.zb.module_home.R;
import com.zb.module_home.databinding.HomeDiscoverVideoBinding;
import com.zb.module_home.iv.DiscoverVideoVMInterface;
import com.zb.module_home.windows.GiftPW;
import com.zb.module_home.windows.GiftPayPW;
import com.zb.module_home.windows.ReviewPW;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import io.realm.Realm;

public class DiscoverVideoViewModel extends BaseViewModel implements DiscoverVideoVMInterface {
    public long friendDynId;
    public DiscoverInfo discoverInfo;
    public MemberInfo memberInfo;
    public GoodDb goodDb;
    private HomeDiscoverVideoBinding mBinding;
    private ObjectAnimator animator;
    private List<String> selectorList = new ArrayList<>();
    private MineInfo mineInfo;
    private LikeDb likeDb;
    private String downloadFileUrl = "";

    @Override
    public void back(View view) {
        super.back(view);
        mBinding.videoView.stopPlayback();//停止播放视频,并且释放
        mBinding.videoView.suspend();//在任何状态下释放媒体播放器
        activity.finish();
    }

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (HomeDiscoverVideoBinding) binding;
        mineInfo = mineInfoDb.getMineInfo();
        likeDb = new LikeDb(Realm.getDefaultInstance());
        goodDb = new GoodDb(Realm.getDefaultInstance());
        mBinding.setIsPlay(true);
        mBinding.setIsProgress(false);
//        animator = ObjectAnimator.ofFloat(mBinding.ivProgress, "rotation", 0, 360).setDuration(700);
//        animator.setRepeatMode(ValueAnimator.RESTART);
//        animator.setRepeatCount(Animation.INFINITE);
//        animator.start();
        dynDetail();
    }

    @Override
    public void follow(View view) {
        super.follow(view);
        if (mBinding.tvFollow.getText().toString().equals("关注")) {
            attentionOther();
        } else {
            cancelAttention();
        }
    }

    @Override
    public void videoPlay(View view) {
        if (mBinding.getIsPlay()) {
            mBinding.setIsPlay(false);
            mBinding.videoView.pause();
        } else {
            if (discoverInfo == null) return;
            mBinding.videoView.setVideoPath(discoverInfo.getVideoUrl());
            mBinding.videoView.start();
        }
    }

    @Override
    public void toMemberDetail(View view) {
        if (discoverInfo != null) {
            mBinding.setIsPlay(false);
            mBinding.videoView.pause();
            ActivityUtils.getCardMemberDetail(discoverInfo.getUserId(), false);
        }
    }

    @Override
    public void toReviews(View view) {
        new ReviewPW(activity, mBinding.getRoot(), friendDynId, discoverInfo.getReviews(), () -> {
            discoverInfo.setReviews(discoverInfo.getReviews() + 1);
            mBinding.setViewModel(DiscoverVideoViewModel.this);
        });
    }

    @Override
    public void doGood(View view) {
        if (goodDb.hasGood(friendDynId)) {
            dynCancelLike();
        } else {
            dynDoLike();
        }
    }

    @Override
    public void doShare(View view) {
        if (discoverInfo == null) return;
        String sharedName = discoverInfo.getNick();
        String content = discoverInfo.getText();
        String sharedUrl = HttpManager.BASE_URL + "mobile/Dyn_dynDetail?friendDynId=" + friendDynId;
        UMImage umImage = new UMImage(activity, discoverInfo.getImage().replace("YM0000", "430X430"));
        new SharePW(activity, mBinding.getRoot(), umImage, sharedName, content, sharedUrl);
    }

    @Override
    public void doReward(View view) {
        new GiftPW(activity, mBinding.getRoot(), giftInfo ->
                new GiftPayPW(activity, mBinding.getRoot(), giftInfo, friendDynId, () -> {
                }));
    }

    @Override
    public void toRewards(View view) {
        mBinding.setIsPlay(false);
        mBinding.videoView.pause();
        ActivityUtils.getHomeRewardList(friendDynId);
    }

    @Override
    public void toDelete(View view) {
        new TextPW(activity, mBinding.getRoot(), "删除动态", "删除后，动态不可找回！", "删除", this::deleteDyn);
    }

    @Override
    public void more(View view) {
        super.more(view);
        new SelectorPW(activity, mBinding.getRoot(), selectorList, position -> {
            if (discoverInfo.getUserId() == BaseActivity.userId) {
                if (position == 0) {
                    // 查看礼物
                    toRewards(null);
                } else {
                    toDelete(null);
                }
            } else {
                if (position == 0) {
                    // 超级喜欢
                    if (mineInfo.getMemberType() == 2) {
                        makeEvaluate();
                    } else {
                        new VipAdPW(activity, mBinding.getRoot(), false, 3);
                    }
                } else if (position == 1) {
                    // 举报
                    mBinding.setIsPlay(false);
                    mBinding.videoView.pause();
                    ActivityUtils.getHomeReport(discoverInfo.getUserId());
                } else {
                    DownLoad.downloadLocation(downloadFileUrl, filePath -> SCToastUtil.showToast(activity, "下载成功", true));
                }
            }
        });
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
    public void deleteDyn() {
        deleteDynApi api = new deleteDynApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                activity.sendBroadcast(new Intent("lobster_publish"));
                SCToastUtil.showToast(activity, "删除成功", true);
                back(null);
            }
        }, activity).setFriendDynId(friendDynId);
        HttpManager.getInstance().doHttpDeal(api);

    }

    @Override
    public void dynDetail() {
        dynDetailApi api = new dynDetailApi(new HttpOnNextListener<DiscoverInfo>() {
            @Override
            public void onNext(DiscoverInfo o) {
                discoverInfo = o;
                downloadFileUrl = discoverInfo.getVideoUrl();
                if (discoverInfo.getUserId() == BaseActivity.userId) {
                    selectorList.add("查看礼物");
                    selectorList.add("删除动态");
                } else {
                    selectorList.add("超级喜欢");
                    selectorList.add("举报");
                    selectorList.add("下载视频");
                }
//                initVideo();
                DownLoad.getFilePath(discoverInfo.getVideoUrl(), BaseActivity.getDownloadFile(".mp4").getAbsolutePath(), new DownLoad.CallBack() {
                    @Override
                    public void success(String filePath) {
                        discoverInfo.setVideoUrl(filePath);
                        mBinding.setIsProgress(false);
                        if (animator != null)
                            animator.cancel();
                        initVideo();
                    }

                    @Override
                    public void onLoading(long total, long current) {
                        animator = ObjectAnimator.ofFloat(mBinding.ivProgress, "rotation", 0, 360).setDuration(700);
                        animator.setRepeatMode(ValueAnimator.RESTART);
                        animator.setRepeatCount(Animation.INFINITE);
                        animator.start();
                        mBinding.setIsProgress(true);
                        mBinding.setIsPlay(true);
                    }
                });
                otherInfo();
                attentionStatus();
            }
        }, activity).setFriendDynId(friendDynId);
        api.setShowProgress(false);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void otherInfo() {
        otherInfoApi api = new otherInfoApi(new HttpOnNextListener<MemberInfo>() {
            @Override
            public void onNext(MemberInfo o) {
                memberInfo = o;
                mBinding.setVariable(BR.viewModel, DiscoverVideoViewModel.this);
            }
        }, activity).setOtherUserId(discoverInfo.getUserId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void attentionStatus() {
        if (discoverInfo == null) return;
        attentionStatusApi api = new attentionStatusApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                if (o == null) {
                    mBinding.tvFollow.setText("取消关注");
                    mBinding.tvFollow.setTextColor(activity.getResources().getColor(R.color.black_827));
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
                mBinding.tvFollow.setText("取消关注");
                mBinding.tvFollow.setTextColor(activity.getResources().getColor(R.color.black_827));
                attentionDb.saveAttention(new AttentionInfo(discoverInfo.getUserId(), memberInfo.getNick(), memberInfo.getImage(), true, BaseActivity.userId));
                activity.sendBroadcast(new Intent("lobster_attentionList"));
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.ERROR) {
                    if (e.getMessage().equals("已经关注过")) {
                        mBinding.tvFollow.setText("取消关注");
                        mBinding.tvFollow.setTextColor(activity.getResources().getColor(R.color.black_827));
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
                mBinding.tvFollow.setText("关注");
                mBinding.tvFollow.setTextColor(activity.getResources().getColor(R.color.black_4d4));
                attentionDb.saveAttention(new AttentionInfo(discoverInfo.getUserId(), memberInfo.getNick(), memberInfo.getImage(), false, BaseActivity.userId));
                activity.sendBroadcast(new Intent("lobster_attentionList"));
            }
        }, activity).setOtherUserId(discoverInfo.getUserId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void dynDoLike() {
        dynDoLikeApi api = new dynDoLikeApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                goodDb.saveGood(new CollectID(friendDynId));
                int goodNum = discoverInfo.getGoodNum() + 1;
                mBinding.setViewModel(DiscoverVideoViewModel.this);
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
                        mBinding.setViewModel(DiscoverVideoViewModel.this);
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
                mBinding.setViewModel(DiscoverVideoViewModel.this);
                Intent data = new Intent("lobster_doGood");
                data.putExtra("goodNum", goodNum);
                data.putExtra("friendDynId", friendDynId);
                activity.sendBroadcast(data);
            }
        }, activity).setFriendDynId(friendDynId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void initVideo() {
        //视频加载完成,准备好播放视频的回调
        mBinding.videoView.setOnPreparedListener(mp -> {
//            mBinding.setIsPlay(false);
            //尺寸变化回调
            mp.setOnVideoSizeChangedListener((mp1, width, height) -> changeVideoSize(mp1));
        });
        //视频播放完成后的回调
        mBinding.videoView.setOnCompletionListener(mp -> {
//            mBinding.setIsPlay(false);
            mBinding.videoView.stopPlayback();//停止播放视频,并且释放
            mBinding.videoView.suspend();//在任何状态下释放媒体播放器
            mBinding.videoView.setVideoPath(discoverInfo.getVideoUrl());
            mBinding.videoView.start();
        });
        //异常回调
        mBinding.videoView.setOnErrorListener((mp, what, extra) -> {
            return true;//如果方法处理了错误，则为true；否则为false。返回false或根本没有OnErrorListener，将导致调用OnCompletionListener。
        });

        //信息回调
        mBinding.videoView.setOnInfoListener((mp, what, extra) -> {
            if (what == MediaPlayer.MEDIA_INFO_UNKNOWN || what == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
                SCToastUtil.showToast(activity, "视频播放失败", true);
                mBinding.setIsPlay(false);
                mBinding.videoView.stopPlayback();//停止播放视频,并且释放
                mBinding.videoView.suspend();//在任何状态下释放媒体播放器
                return true;
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
//                // 缓冲开始
//                mBinding.setIsPlay(true);
//                mBinding.setIsProgress(true);

                return true;
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                // 缓冲结束,此接口每次回调完START就回调END,若不加上判断就会出现缓冲图标一闪一闪的卡顿现象
                if (mp.isPlaying()) {
                    mBinding.setIsPlay(true);
                    mBinding.setIsProgress(false);
                }
                return true;
            } else if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                mBinding.setIsPlay(true);
                mBinding.setIsProgress(false);
//                if (animator != null)
//                    animator.cancel();
            }
            return false; //如果方法处理了信息，则为true；如果没有，则为false。返回false或根本没有OnInfoListener，将导致丢弃该信息。
        });
        mBinding.videoView.setVideoPath(discoverInfo.getVideoUrl());
        mBinding.videoView.start();
    }

    /**
     * 修改预览View的大小,以用来适配屏幕
     */
    private void changeVideoSize(@NonNull MediaPlayer mMediaPlayer) {
        int width = mMediaPlayer.getVideoWidth();
        int height = mMediaPlayer.getVideoHeight();

        if (ObjectUtils.getViewSizeByHeight(1.0f) * width / height > MineApp.W) {
            AdapterBinding.viewSize(mBinding.videoView, MineApp.W, (MineApp.W * height / width));
//            AdapterBinding.loadImage(mBinding.ivVideo, discoverInfo.getVideoUrl(), 0, ObjectUtils.getDefaultRes(), MineApp.W, (MineApp.W * height / width),
//                    false, false, 0, false, 0, false);
        } else {
            AdapterBinding.viewSize(mBinding.videoView, (ObjectUtils.getViewSizeByHeight(1.0f) * width / height), ObjectUtils.getViewSizeByHeight(1.0f));
//            AdapterBinding.loadImage(mBinding.ivVideo, discoverInfo.getVideoUrl(), 0,
//                    ObjectUtils.getDefaultRes(), (ObjectUtils.getViewSizeByHeight(1.0f) * width / height), ObjectUtils.getViewSizeByHeight(1.0f),
//                    false, false, 0, false, 0, false);
        }
    }
}
