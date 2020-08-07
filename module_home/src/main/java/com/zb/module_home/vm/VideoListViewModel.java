package com.zb.module_home.vm;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.umeng.socialize.media.UMImage;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.api.attentionOtherApi;
import com.zb.lib_base.api.attentionStatusApi;
import com.zb.lib_base.api.cancelAttentionApi;
import com.zb.lib_base.api.deleteDynApi;
import com.zb.lib_base.api.dynCancelLikeApi;
import com.zb.lib_base.api.dynDoLikeApi;
import com.zb.lib_base.api.dynPiazzaListApi;
import com.zb.lib_base.api.makeEvaluateApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.AreaDb;
import com.zb.lib_base.db.LikeDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.AttentionInfo;
import com.zb.lib_base.model.CollectID;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DouYinLayoutManager;
import com.zb.lib_base.utils.DownLoad;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.OnViewPagerListener;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.CountUsedPW;
import com.zb.lib_base.windows.SelectorPW;
import com.zb.lib_base.windows.SharePW;
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
import io.realm.Realm;

public class VideoListViewModel extends BaseViewModel implements VideoListVMInterface {
    public int position;
    public int pageNo;
    private HomeAdapter adapter;
    private DouYinLayoutManager douYinLayoutManager;
    private HomeVideoListBinding mBinding;
    private AreaDb areaDb;
    private boolean isUp = false;
    private boolean isOver = false;
    private List<String> selectorList = new ArrayList<>();
    private MineInfo mineInfo;
    private LikeDb likeDb;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (HomeVideoListBinding) binding;
        areaDb = new AreaDb(Realm.getDefaultInstance());
        likeDb = new LikeDb(Realm.getDefaultInstance());
        mineInfo = mineInfoDb.getMineInfo();
        animator = ObjectAnimator.ofFloat(ivProgress, "rotation", 0, 360).setDuration(700);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(Animation.INFINITE);
        setAdapter();
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
                videoView.stopPlayback();//停止播放视频,并且释放
                videoView.suspend();//在任何状态下释放媒体播放器
            }

            @Override
            public void onPageSelected(boolean isButton, View view) {
                isUp = douYinLayoutManager.getDrift() >= 0;
                position = douYinLayoutManager.findFirstCompletelyVisibleItemPosition();
                playVideo(view);
                if (!isOver && position == MineApp.discoverInfoList.size() - 1 && isUp) {
                    pageNo++;
                    dynPiazzaList();
                }
            }
        });
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
        ActivityUtils.getCardMemberDetail(discoverInfo.getUserId(), false);
    }

    @Override
    public void toReviews(int position) {
        DiscoverInfo discoverInfo = MineApp.discoverInfoList.get(position);
        new ReviewPW(activity, mBinding.getRoot(), discoverInfo.getFriendDynId(), discoverInfo.getReviews(), () -> {
            discoverInfo.setReviews(discoverInfo.getReviews() + 1);
            tvReviews.setText(ObjectUtils.count(discoverInfo.getReviews()));
        });
    }

    @Override
    public void doGood(int position) {
        DiscoverInfo discoverInfo = MineApp.discoverInfoList.get(position);
        if (goodDb.hasGood(discoverInfo.getFriendDynId())) {
            dynCancelLike(discoverInfo, position);
        } else {
            dynDoLike(discoverInfo, position);
        }
    }

    @Override
    public void doShare(DiscoverInfo discoverInfo) {
        String sharedName = discoverInfo.getNick();
        String content = discoverInfo.getText();
        String sharedUrl = HttpManager.BASE_URL + "mobile/Dyn_dynDetail?friendDynId=" + discoverInfo.getFriendDynId();
        UMImage umImage = new UMImage(activity, discoverInfo.getImage().replace("YM0000", "430X430"));
        new SharePW(activity, mBinding.getRoot(), umImage, sharedName, content, sharedUrl);
    }

    @Override
    public void doReward(DiscoverInfo discoverInfo) {
        new GiftPW(activity, mBinding.getRoot(), giftInfo ->
                new GiftPayPW(activity, mBinding.getRoot(), giftInfo, discoverInfo.getFriendDynId(), () -> {
                }));
    }

    @Override
    public void follow(DiscoverInfo discoverInfo) {
        if (tvFollow.getText().toString().equals("关注")) {
            attentionOther(discoverInfo);
        } else {
            cancelAttention(discoverInfo);
        }
    }

    @Override
    public void more(DiscoverInfo discoverInfo) {
        selectorList.clear();
        if (discoverInfo.getUserId() == BaseActivity.userId) {
            selectorList.add("查看礼物");
//            selectorList.add("删除动态");
        } else {
            selectorList.add("超级喜欢");
            selectorList.add("举报");
            selectorList.add("下载视频");
        }
        new SelectorPW(activity, mBinding.getRoot(), selectorList, position -> {
            if (discoverInfo.getUserId() == BaseActivity.userId) {
                if (position == 0) {
                    // 查看礼物
                    videoView.pause();
                    ActivityUtils.getHomeRewardList(discoverInfo.getFriendDynId());
                }
//                else {
//                    new TextPW(activity, mBinding.getRoot(), "删除动态", "删除后，动态不可找回！", "删除", () -> deleteDyn(discoverInfo));
//                }
            } else {
                if (position == 0) {
                    // 超级喜欢
                    if (mineInfo.getMemberType() == 2) {
                        makeEvaluate(discoverInfo);
                    } else {
                        new VipAdPW(activity, mBinding.getRoot(), false, 3);
                    }
                } else if (position == 1) {
                    // 举报
                    videoView.pause();
                    ActivityUtils.getHomeReport(discoverInfo.getUserId());
                } else {
                    DownLoad.downloadLocation(discoverInfo.getVideoUrl(), filePath -> SCToastUtil.showToast(activity, "下载成功", true));
                }
            }
        });
    }

    private void attentionStatus(DiscoverInfo discoverInfo) {
        attentionStatusApi api = new attentionStatusApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                if (o == null) {
                    tvFollow.setText("取消关注");
                    tvFollow.setTextColor(activity.getResources().getColor(R.color.black_827));
                    attentionDb.saveAttention(new AttentionInfo(discoverInfo.getUserId(), discoverInfo.getNick(), discoverInfo.getImage(), true, BaseActivity.userId));
                } else {
                    attentionDb.saveAttention(new AttentionInfo(discoverInfo.getUserId(), discoverInfo.getNick(), discoverInfo.getImage(), false, BaseActivity.userId));
                }
            }
        }, activity).setOtherUserId(discoverInfo.getUserId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void attentionOther(DiscoverInfo discoverInfo) {
        if (discoverInfo == null) return;
        attentionOtherApi api = new attentionOtherApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                tvFollow.setText("取消关注");
                tvFollow.setTextColor(activity.getResources().getColor(R.color.black_827));
                attentionDb.saveAttention(new AttentionInfo(discoverInfo.getUserId(), discoverInfo.getNick(), discoverInfo.getImage(), true, BaseActivity.userId));
                activity.sendBroadcast(new Intent("lobster_attentionList"));
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.ERROR) {
                    if (e.getMessage().equals("已经关注过")) {
                        tvFollow.setText("取消关注");
                        tvFollow.setTextColor(activity.getResources().getColor(R.color.black_827));
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
                tvFollow.setText("关注");
                tvFollow.setTextColor(activity.getResources().getColor(R.color.black_4d4));
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
                    new SuperLikePW(activity, mBinding.getRoot(), myHead, otherHead, false, mineInfo.getSex(), mineInfo.getSex() == 1 ? 0 : 1, null);
                } else if (o == 2) {
                    // 匹配成功
                    likeDb.saveLike(new CollectID(discoverInfo.getUserId()));
                    new SuperLikePW(activity, mBinding.getRoot(), myHead, otherHead, true, mineInfo.getSex(), mineInfo.getSex() == 1 ? 0 : 1, () -> ActivityUtils.getChatActivity(discoverInfo.getUserId()));
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

    private void dynDoLike(DiscoverInfo discoverInfo, int position) {
        dynDoLikeApi api = new dynDoLikeApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                goodDb.saveGood(new CollectID(discoverInfo.getFriendDynId()));
                ivGood.setImageBitmap(BitmapFactory.decodeResource(activity.getResources(), R.drawable.video_play_good_pressed));
                Intent data = new Intent("lobster_doGood");
                data.putExtra("goodNum", discoverInfo.getGoodNum() + 1);
                data.putExtra("friendDynId", discoverInfo.getFriendDynId());
                activity.sendBroadcast(data);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == 0) {
                    if (TextUtils.equals(e.getMessage(), "已经赞过了")) {
                        goodDb.saveGood(new CollectID(discoverInfo.getFriendDynId()));
                        ivGood.setImageBitmap(BitmapFactory.decodeResource(activity.getResources(), R.drawable.video_play_good_pressed));
                        Intent data = new Intent("lobster_doGood");
                        data.putExtra("friendDynId", discoverInfo.getFriendDynId());
                        activity.sendBroadcast(data);
                    }
                }
            }
        }, activity).setFriendDynId(discoverInfo.getFriendDynId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void dynCancelLike(DiscoverInfo discoverInfo, int position) {
        dynCancelLikeApi api = new dynCancelLikeApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                goodDb.deleteGood(discoverInfo.getFriendDynId());
                ivGood.setImageBitmap(BitmapFactory.decodeResource(activity.getResources(), R.drawable.video_play_good_unpressed));
                Intent data = new Intent("lobster_doGood");
                data.putExtra("goodNum", discoverInfo.getGoodNum() - 1);
                data.putExtra("friendDynId", discoverInfo.getFriendDynId());
                activity.sendBroadcast(data);
            }
        }, activity).setFriendDynId(discoverInfo.getFriendDynId());
        HttpManager.getInstance().doHttpDeal(api);
    }

    private VideoView videoView;
    private ImageView ivProgress;
    private ObjectAnimator animator;
    private DiscoverInfo discoverInfo;
    private ImageView ivGood;
    private TextView tvReviews;
    private TextView tvFollow;

    private void playVideo(View view) {
        discoverInfo = MineApp.discoverInfoList.get(position);

        videoView = view.findViewById(R.id.video_view);
        ivProgress = view.findViewById(R.id.iv_progress);
        ivGood = view.findViewById(R.id.iv_good);
        tvReviews = view.findViewById(R.id.tv_reviews);
        tvFollow = view.findViewById(R.id.tv_follow);

        attentionStatus(discoverInfo);

        animator.start();
        ivProgress.setVisibility(View.VISIBLE);
        DownLoad.getFilePath(discoverInfo.getVideoUrl(), BaseActivity.getDownloadFile(".mp4").getAbsolutePath(), filePath -> {
            discoverInfo.setVideoPath(filePath);
            ivProgress.setVisibility(View.GONE);
            if (animator != null)
                animator.cancel();
            initVideo();
        });
    }

    public void onResume() {
        videoView.setVideoPath(discoverInfo.getVideoPath());
        videoView.start();
    }

    private void initVideo() {
        //视频加载完成,准备好播放视频的回调
        videoView.setOnPreparedListener(mp -> {
            //尺寸变化回调
            mp.setOnVideoSizeChangedListener((mp1, width, height) -> changeVideoSize(mp1));
        });
        //视频播放完成后的回调
        videoView.setOnCompletionListener(mp -> {
            videoView.stopPlayback();//停止播放视频,并且释放
            videoView.suspend();//在任何状态下释放媒体播放器
            videoView.setVideoPath(discoverInfo.getVideoPath());
            videoView.start();
        });
        //异常回调
        videoView.setOnErrorListener((mp, what, extra) -> {
            return true;//如果方法处理了错误，则为true；否则为false。返回false或根本没有OnErrorListener，将导致调用OnCompletionListener。
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
                ivProgress.setVisibility(View.GONE);
            }
            return false; //如果方法处理了信息，则为true；如果没有，则为false。返回false或根本没有OnInfoListener，将导致丢弃该信息。
        });
        videoView.setVideoPath(discoverInfo.getVideoPath());
        videoView.start();
    }

    /**
     * 修改预览View的大小,以用来适配屏幕
     */
    private void changeVideoSize(@NonNull MediaPlayer mMediaPlayer) {
        int width = mMediaPlayer.getVideoWidth();
        int height = mMediaPlayer.getVideoHeight();

        if (ObjectUtils.getViewSizeByHeight(1.0f) * width / height > MineApp.W) {
            AdapterBinding.viewSize(videoView, MineApp.W, (MineApp.W * height / width));
        } else {
            AdapterBinding.viewSize(videoView, (ObjectUtils.getViewSizeByHeight(1.0f) * width / height), ObjectUtils.getViewSizeByHeight(1.0f));
        }
    }
}
