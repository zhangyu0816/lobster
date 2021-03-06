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
import android.widget.RelativeLayout;

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
import com.zb.lib_base.api.dynVisitApi;
import com.zb.lib_base.api.makeEvaluateApi;
import com.zb.lib_base.api.memberInfoConfApi;
import com.zb.lib_base.api.otherInfoApi;
import com.zb.lib_base.api.seeLikersApi;
import com.zb.lib_base.api.seeReviewsApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AttentionDb;
import com.zb.lib_base.db.GoodDb;
import com.zb.lib_base.db.LikeTypeDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.AttentionInfo;
import com.zb.lib_base.model.CollectID;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.Review;
import com.zb.lib_base.model.ShareInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DownLoad;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.water.WaterMark;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.FunctionPW;
import com.zb.lib_base.windows.GiftPW;
import com.zb.lib_base.windows.GiftPayPW;
import com.zb.lib_base.windows.SuperLikePW;
import com.zb.lib_base.windows.TextPW;
import com.zb.lib_base.windows.VipAdPW;
import com.zb.module_home.R;
import com.zb.module_home.adapter.HomeAdapter;
import com.zb.module_home.databinding.HomeVideoBinding;
import com.zb.module_home.iv.DiscoverVideoVMInterface;
import com.zb.module_home.windows.ReviewPW;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class DiscoverVideoViewModel extends BaseViewModel implements DiscoverVideoVMInterface {
    public long friendDynId;
    public HomeAdapter adapter;
    private HomeVideoBinding mBinding;
    public DiscoverInfo discoverInfo;
    private ObjectAnimator animator;
    private MemberInfo memberInfo;
    private BaseReceiver attentionReceiver;
    private int videoWidth, videoHeight;
    private List<Review> reviewList = new ArrayList<>();
    private int duration;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (HomeVideoBinding) binding;
        animator = ObjectAnimator.ofFloat(mBinding.ivProgress, "rotation", 0, 360).setDuration(700);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(Animation.INFINITE);
        animator.start();
        mBinding.setIsProgress(true);
        mBinding.setIsPlay(true);

        if (GoodDb.getInstance().hasGood(friendDynId)) {
            mBinding.ivLike.setVisibility(View.VISIBLE);
        } else {
            mBinding.ivUnLike.setVisibility(View.VISIBLE);
        }
        attentionReceiver = new BaseReceiver(activity, "lobster_attention") {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isAttention = intent.getBooleanExtra("isAttention", false);
                if (isAttention) {
                    mBinding.attentionLayout.setVisibility(View.INVISIBLE);
                } else {
                    mBinding.attentionLayout.setVisibility(View.VISIBLE);
                    mBinding.ivAttention.setBackgroundResource(R.drawable.attention_icon);
                }
            }
        };

        initGood(mBinding.viewClick, mBinding.ivGood, () -> videoPlay(null), () -> {
            if (!GoodDb.getInstance().hasGood(friendDynId)) {
                mBinding.ivUnLike.setVisibility(View.GONE);
                mBinding.ivLike.setVisibility(View.VISIBLE);
                GoodDb.getInstance().saveGood(new CollectID(friendDynId));
                dynDoLike();
            }
        });
        setAdapter();
        dynDetail();
    }

    @Override
    public void setAdapter() {
        adapter = new HomeAdapter<>(activity, R.layout.item_auto_review, reviewList, this);
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
        mBinding.reviewList.stop();
        activity.finish();
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
    public void videoPlay(View view) {
        if (mBinding.getIsPlay()) {
            mBinding.setIsPlay(false);
            mBinding.videoView.pause();
            mBinding.reviewList.stop();
        } else {
            if (discoverInfo == null) return;
            mBinding.setIsPlay(true);
            mBinding.videoView.start();
            mBinding.reviewList.start();
        }
    }

    @Override
    public void toReviews(View view) {
        new ReviewPW(mBinding.getRoot(), friendDynId, discoverInfo.getReviews(), () -> {
            discoverInfo.setReviews(discoverInfo.getReviews() + 1);
            mBinding.setDiscoverInfo(discoverInfo);
            reviewList.clear();
            adapter.notifyDataSetChanged();
            seeReviews(1);
        }).setMainId(memberInfo.getUserId());
    }

    @Override
    public void toGood(View view) {
        if (GoodDb.getInstance().hasGood(friendDynId)) {
            mBinding.ivUnLike.setVisibility(View.VISIBLE);
            mBinding.ivLike.setVisibility(View.GONE);
            likeOrNot(mBinding.ivUnLike);
            dynCancelLike();
        } else {
            mBinding.ivUnLike.setVisibility(View.GONE);
            mBinding.ivLike.setVisibility(View.VISIBLE);
            likeOrNot(mBinding.ivLike);
            GoodDb.getInstance().saveGood(new CollectID(friendDynId));
            dynDoLike();
        }
    }

    @Override
    public void toShare(View view) {
        memberInfoConfApi api = new memberInfoConfApi(new HttpOnNextListener<ShareInfo>() {
            @Override
            public void onNext(ShareInfo o) {
                if (discoverInfo == null) return;
                String sharedName = o.getText().replace("{userId}", discoverInfo.getUserId() + "").replace("{nick}", discoverInfo.getNick());
                String content = discoverInfo.getText().isEmpty() ? discoverInfo.getFriendTitle() : discoverInfo.getText();
                String sharedUrl = HttpManager.BASE_URL + "mobile/Dyn_dynDetail?friendDynId=" + friendDynId;
                new FunctionPW(mBinding.getRoot(), discoverInfo.getImage().replace("YM0000", "430X430"), sharedName, content, sharedUrl,
                        discoverInfo.getUserId() == BaseActivity.userId, true, true, false, new FunctionPW.CallBack() {
                    @Override
                    public void gift() {
                        mBinding.videoView.pause();
                        mBinding.reviewList.stop();
                        ActivityUtils.getHomeRewardList(friendDynId, 0);
                    }

                    @Override
                    public void delete() {
                        new TextPW(mBinding.getRoot(), "????????????", "?????????????????????????????????", "??????", () -> deleteDyn());
                    }

                    @Override
                    public void report() {
                        // ??????
                        mBinding.videoView.pause();
                        mBinding.reviewList.stop();
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
                                        "??????", false, true, DiscoverVideoViewModel.this::getPermissions);
                            } else {
                                SCToastUtil.showToast(activity, "??????????????????????????????????????????--??????--????????????--??????????????????", true);
                            }
                        }
                    }

                    @Override
                    public void like() {
                        // ????????????
                        if (MineApp.mineInfo.getMemberType() == 2) {
                            makeEvaluate();
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
    public void toMemberDetail(View view) {
        if (discoverInfo != null) {
            mBinding.videoView.pause();
            mBinding.reviewList.stop();
            ActivityUtils.getCardMemberDetail(discoverInfo.getUserId(), false);
        }
    }

    @Override
    public void doReward(View view) {
        new GiftPW(mBinding.getRoot(), giftInfo ->
                new GiftPayPW(mBinding.getRoot(), giftInfo, discoverInfo.getFriendDynId(), 0, () -> {
                }));
    }

    @Override
    public void visitMember(long userId) {
        super.visitMember(userId);
        if (userId != BaseActivity.userId) {
            mBinding.videoView.pause();
            mBinding.reviewList.stop();
            ActivityUtils.getCardMemberDetail(userId, false);
        }
    }

    @Override
    public void dynDetail() {
        dynDetailApi api = new dynDetailApi(new HttpOnNextListener<DiscoverInfo>() {
            @Override
            public void onNext(DiscoverInfo o) {
                discoverInfo = o;
                mBinding.setDiscoverInfo(discoverInfo);
                seeReviews(1);
                DownLoad.getFilePath(discoverInfo.getVideoUrl(), BaseActivity.getDownloadFile(".mp4").getAbsolutePath(), (filePath, bitmap) -> {
                    discoverInfo.setVideoPath(filePath);
                    mBinding.setIsProgress(false);
                    if (animator != null)
                        animator.cancel();
                    animator = null;
                    initVideo();
                });
                otherInfo();
                if (discoverInfo.getUserId() == BaseActivity.userId) {
                    mBinding.ivAttention.setVisibility(View.INVISIBLE);
                    mBinding.setIsMine(true);
                } else
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
            }
        }, activity).setOtherUserId(discoverInfo.getUserId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void attentionStatus() {
        attentionStatusApi api = new attentionStatusApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                if (o == null) {
                    mBinding.attentionLayout.setVisibility(View.INVISIBLE);
                    AttentionDb.getInstance().saveAttention(new AttentionInfo(discoverInfo.getUserId(), memberInfo.getNick(), memberInfo.getImage(), true, BaseActivity.userId));
                } else {
                    mBinding.attentionLayout.setVisibility(View.VISIBLE);
                    AttentionDb.getInstance().saveAttention(new AttentionInfo(discoverInfo.getUserId(), memberInfo.getNick(), memberInfo.getImage(), false, BaseActivity.userId));
                }
            }
        }, activity).setOtherUserId(discoverInfo.getUserId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void attentionOther() {
        attentionOtherApi api = new attentionOtherApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                isAttention(mBinding.attentionLayout, mBinding.ivAttention);
                AttentionDb.getInstance().saveAttention(new AttentionInfo(discoverInfo.getUserId(), memberInfo.getNick(), memberInfo.getImage(), true, BaseActivity.userId));
                Intent data = new Intent("lobster_attentionList");
                data.putExtra("isAdd", true);
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
                MineApp.getApp().getFixedThreadPool().execute(() -> {
                    SystemClock.sleep(1000);
                    activity.runOnUiThread(() -> {
                        Intent intent = new Intent("lobster_attention");
                        intent.putExtra("isAttention", true);
                        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(intent);
                    });
                });
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.ERROR) {
                    if (e.getMessage().equals("???????????????")) {
                        isAttention(mBinding.attentionLayout, mBinding.ivAttention);
                        AttentionDb.getInstance().saveAttention(new AttentionInfo(discoverInfo.getUserId(), memberInfo.getNick(), memberInfo.getImage(), true, BaseActivity.userId));
                        Intent data = new Intent("lobster_attentionList");
                        data.putExtra("isAdd", true);
                        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
                    }
                }
            }
        }, activity).setOtherUserId(discoverInfo.getUserId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void cancelAttention() {
        cancelAttentionApi api = new cancelAttentionApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                mBinding.attentionLayout.setVisibility(View.VISIBLE);
                mBinding.ivAttention.setBackgroundResource(R.drawable.attention_icon);
                AttentionDb.getInstance().saveAttention(new AttentionInfo(discoverInfo.getUserId(), memberInfo.getNick(), memberInfo.getImage(), false, BaseActivity.userId));
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_attentionList"));
                Intent intent = new Intent("lobster_attention");
                intent.putExtra("isAttention", false);
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(intent);
            }
        }, activity).setOtherUserId(discoverInfo.getUserId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void dynDoLike() {
        dynDoLikeApi api = new dynDoLikeApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                int goodNum = discoverInfo.getGoodNum() + 1;
                discoverInfo.setGoodNum(goodNum);
                mBinding.setDiscoverInfo(discoverInfo);

                Intent data = new Intent("lobster_doGood");
                data.putExtra("goodNum", goodNum);
                data.putExtra("friendDynId", friendDynId);
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
                reviewList.clear();
                adapter.notifyDataSetChanged();
                seeReviews(1);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == 0) {
                    if (TextUtils.equals(e.getMessage(), "???????????????")) {
                        Intent data = new Intent("lobster_doGood");
                        data.putExtra("goodNum", discoverInfo.getGoodNum());
                        data.putExtra("friendDynId", friendDynId);
                        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
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
                mBinding.setDiscoverInfo(discoverInfo);

                Intent data = new Intent("lobster_doGood");
                data.putExtra("goodNum", goodNum);
                data.putExtra("friendDynId", friendDynId);
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);

            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == 0) {
                    if (TextUtils.equals(e.getMessage(), "???????????????")) {
                        GoodDb.getInstance().deleteGood(friendDynId);
                        Intent data = new Intent("lobster_doGood");
                        data.putExtra("goodNum", discoverInfo.getGoodNum());
                        data.putExtra("friendDynId", friendDynId);
                        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(data);
                    }
                }
            }
        }, activity).setFriendDynId(friendDynId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void deleteDyn() {
        deleteDynApi api = new deleteDynApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_publish"));
                SCToastUtil.showToast(activity, "????????????", true);
                back(null);
            }
        }, activity).setFriendDynId(friendDynId);
        HttpManager.getInstance().doHttpDeal(api);

    }

    @Override
    public void makeEvaluate() {
        makeEvaluateApi api = new makeEvaluateApi(new HttpOnNextListener<Integer>() {
            @Override
            public void onNext(Integer o) {
                // 1???????????? 2???????????? 3??????????????????
                String myHead = MineApp.mineInfo.getImage();
                String otherHead = memberInfo.getImage();
                if (o == 1) {
                    LikeTypeDb.getInstance().setType(discoverInfo.getUserId(), 2);
                    new SuperLikePW(mBinding.getRoot(), myHead, otherHead, MineApp.mineInfo.getSex(), memberInfo.getSex());
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

    @Override
    public void seeReviews(int pageNo) {
        seeReviewsApi api = new seeReviewsApi(new HttpOnNextListener<List<Review>>() {
            @Override
            public void onNext(List<Review> o) {
                mBinding.reviewList.setVisibility(View.VISIBLE);
                int start = reviewList.size();
                for (Review item : o) {
                    item.setType(2);
                    reviewList.add(item);
                }
                adapter.notifyItemRangeChanged(start, reviewList.size());
                if (reviewList.size() > 2) {
                    mBinding.reviewList.setLayoutParams(new RelativeLayout.LayoutParams(-2, ObjectUtils.getViewSizeByWidthFromMax(400)));
                    mBinding.reviewList.start();
                } else {
                    mBinding.reviewList.setLayoutParams(new RelativeLayout.LayoutParams(-2, -2));
                }
                seeLikers(1);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    seeLikers(1);
                }
            }
        }, activity).setFriendDynId(friendDynId).setTimeSortType(1).setPageNo(pageNo).setRow(10);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void seeLikers(int pageNo) {
        seeLikersApi api = new seeLikersApi(new HttpOnNextListener<List<Review>>() {
            @Override
            public void onNext(List<Review> o) {
                mBinding.reviewList.setVisibility(View.VISIBLE);
                int start = reviewList.size();
                for (Review item : o) {
                    item.setType(1);
                    reviewList.add(item);
                }
                adapter.notifyItemRangeChanged(start, reviewList.size());
                if (reviewList.size() > 2) {
                    mBinding.reviewList.setLayoutParams(new RelativeLayout.LayoutParams(-2, ObjectUtils.getViewSizeByWidthFromMax(400)));
                    mBinding.reviewList.start();
                } else {
                    mBinding.reviewList.setLayoutParams(new RelativeLayout.LayoutParams(-2, -2));
                }
            }
        }, activity).setFriendDynId(friendDynId).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void initVideo() {
        //????????????
        mBinding.videoView.setOnErrorListener((mp, what, extra) -> {
            return true;//????????????????????????????????????true????????????false?????????false???????????????OnErrorListener??????????????????OnCompletionListener???
        });
        mBinding.videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);//?????????????????????
        });
        //????????????
        mBinding.videoView.setOnInfoListener((mp, what, extra) -> {
            if (what == MediaPlayer.MEDIA_INFO_UNKNOWN || what == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
                SCToastUtil.showToast(activity, "??????????????????", true);
                mBinding.videoView.stopPlayback();//??????????????????,????????????
                mBinding.videoView.suspend();//???????????????????????????????????????
                return true;
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                // ????????????,????????????????????????START?????????END,?????????????????????????????????????????????????????????????????????
                if (mp.isPlaying()) {
                    mBinding.setIsProgress(false);
                }
                return true;
            } else if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                changeVideoSize(mp);
                duration = mp.getDuration();
                mBinding.setIsProgress(false);
                mBinding.ivImage.setVisibility(View.GONE);
                mBinding.videoView.setBackgroundColor(Color.TRANSPARENT);
            }
            return false; //????????????????????????????????????true????????????????????????false?????????false???????????????OnInfoListener??????????????????????????????
        });
        mBinding.videoView.setVideoPath(discoverInfo.getVideoPath());
        mBinding.videoView.start();
    }

    /**
     * ????????????View?????????,?????????????????????
     */
    private void changeVideoSize(@NonNull MediaPlayer mMediaPlayer) {
        videoWidth = mMediaPlayer.getVideoWidth();
        videoHeight = mMediaPlayer.getVideoHeight();

        if (ObjectUtils.getViewSizeByHeight(1.0f) * videoWidth / videoHeight > MineApp.W) {
            AdapterBinding.viewSize(mBinding.videoView, MineApp.W, (MineApp.W * videoHeight / videoWidth));
        } else {
            AdapterBinding.viewSize(mBinding.videoView, (ObjectUtils.getViewSizeByHeight(1.0f) * videoWidth / videoHeight), ObjectUtils.getViewSizeByHeight(1.0f));
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
            WaterMark.getInstance().createWater(activity, filePath1, memberInfo.getUserId(), videoWidth, videoHeight, duration);
        });

    }
}
