package com.zb.module_home.vm;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.zb.lib_base.api.dynDetailApi;
import com.zb.lib_base.api.dynDoLikeApi;
import com.zb.lib_base.api.makeEvaluateApi;
import com.zb.lib_base.api.otherInfoApi;
import com.zb.lib_base.api.seeLikersApi;
import com.zb.lib_base.api.seeReviewsApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.CustomProgressDialog;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.AttentionInfo;
import com.zb.lib_base.model.CollectID;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.Review;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DataCleanManager;
import com.zb.lib_base.utils.DownLoad;
import com.zb.lib_base.utils.ObjectUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.FunctionPW;
import com.zb.lib_base.windows.SuperLikePW;
import com.zb.lib_base.windows.TextPW;
import com.zb.lib_base.windows.VipAdPW;
import com.zb.module_home.R;
import com.zb.module_home.adapter.HomeAdapter;
import com.zb.module_home.databinding.HomeVideoL2Binding;
import com.zb.module_home.iv.DiscoverVideoL2VMInterface;
import com.zb.module_home.utils.Compressor;
import com.zb.module_home.utils.InitListener;
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

public class DiscoverVideoL2ViewModel extends BaseViewModel implements DiscoverVideoL2VMInterface {
    public long friendDynId;
    public HomeAdapter adapter;
    private HomeVideoL2Binding mBinding;
    public DiscoverInfo discoverInfo;
    private ObjectAnimator animator;
    private MemberInfo memberInfo;
    private BaseReceiver attentionReceiver;
    private String downloadPath = "";
    private int videoWidth, videoHeight;
    private List<Review> reviewList = new ArrayList<>();
    private List<Review> tempList = new ArrayList<>();

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (HomeVideoL2Binding) binding;
        animator = ObjectAnimator.ofFloat(mBinding.ivProgress, "rotation", 0, 360).setDuration(700);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(Animation.INFINITE);
        animator.start();
        mBinding.setIsProgress(true);
        mBinding.setGoodDb(goodDb);
        mBinding.setIsPlay(true);

        if (goodDb.hasGood(friendDynId)) {
            mBinding.ivLike.setVisibility(View.VISIBLE);
        } else {
            mBinding.ivUnLike.setVisibility(View.VISIBLE);
        }
        attentionReceiver = new BaseReceiver(activity, "lobster_attention") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.setIsAttention(intent.getBooleanExtra("isAttention", false));
            }
        };

        initGood(mBinding.viewClick, mBinding.ivGood, () -> videoPlay(null), () -> {
            if (!goodDb.hasGood(friendDynId)) {
                mBinding.ivUnLike.setVisibility(View.GONE);
                mBinding.ivLike.setVisibility(View.VISIBLE);
                dynDoLike();
            }
        });
        setAdapter();
        dynDetail();
    }

    @Override
    public void setAdapter() {
        adapter = new HomeAdapter<>(activity, R.layout.item_auto_review, reviewList, this);
    }

    @Override
    public void back(View view) {
        super.back(view);
        mBinding.reviewList.stop();
        activity.finish();
    }

    public void onDestroy() {
        attentionReceiver.unregisterReceiver();
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
            mBinding.videoView.setVideoPath(discoverInfo.getVideoPath());
            mBinding.videoView.start();
            mBinding.reviewList.start();
        }
    }

    @Override
    public void toReviews(View view) {
        new ReviewPW(activity, mBinding.getRoot(), friendDynId, discoverInfo.getReviews(), () -> {
            discoverInfo.setReviews(discoverInfo.getReviews() + 1);
            mBinding.setDiscoverInfo(discoverInfo);
            seeLikers(1);
        });
    }

    @Override
    public void toGood(View view) {
        if (goodDb.hasGood(friendDynId)) {
            mBinding.ivUnLike.setVisibility(View.VISIBLE);
            mBinding.ivLike.setVisibility(View.GONE);
            likeOrNot(mBinding.ivUnLike);
            dynCancelLike();
        } else {
            mBinding.ivUnLike.setVisibility(View.GONE);
            mBinding.ivLike.setVisibility(View.VISIBLE);
            likeOrNot(mBinding.ivLike);
            dynDoLike();
        }
    }

    @Override
    public void toShare(View view) {
        if (discoverInfo == null) return;
        String sharedName = discoverInfo.getNick();
        String content = discoverInfo.getText();
        String sharedUrl = HttpManager.BASE_URL + "mobile/Dyn_dynDetail?friendDynId=" + friendDynId;
        new FunctionPW(activity, mBinding.getRoot(), discoverInfo.getImage().replace("YM0000", "430X430"), sharedName, content, sharedUrl,
                discoverInfo.getUserId() == BaseActivity.userId, true, true, false, new FunctionPW.CallBack() {
            @Override
            public void gift() {
                mBinding.videoView.pause();
                mBinding.reviewList.stop();
                ActivityUtils.getHomeRewardList(friendDynId);
            }

            @Override
            public void delete() {
                new TextPW(activity, mBinding.getRoot(), "删除动态", "删除后，动态不可找回！", "删除", () -> deleteDyn());
            }

            @Override
            public void report() {
                // 举报
                mBinding.videoView.pause();
                mBinding.reviewList.stop();
                ActivityUtils.getHomeReport(discoverInfo.getUserId());
            }

            @Override
            public void download() {
                DownLoad.downloadLocation(discoverInfo.getVideoUrl(), filePath -> {
                    downloadPath = filePath;
                    getPermissions();
                });
            }

            @Override
            public void like() {
                // 超级喜欢
                if (MineApp.mineInfo.getMemberType() == 2) {
                    makeEvaluate();
                } else {
                    new VipAdPW(activity, mBinding.getRoot(), false, 3, discoverInfo.getImage());
                }
            }
        });
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
        new GiftPW(activity, mBinding.getRoot(), giftInfo ->
                new GiftPayPW(activity, mBinding.getRoot(), giftInfo, discoverInfo.getFriendDynId(), () -> {
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
                seeLikers(1);
                DownLoad.getFilePath(discoverInfo.getVideoUrl(), BaseActivity.getDownloadFile(".mp4").getAbsolutePath(), filePath -> {
                    discoverInfo.setVideoPath(filePath);
                    mBinding.setIsProgress(false);
                    if (animator != null)
                        animator.cancel();
                    animator = null;
                    initVideo();
                });
                otherInfo();
                if (discoverInfo.getUserId() == BaseActivity.userId) {
                    mBinding.setIsAttention(true);
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
                mBinding.setMemberInfo(memberInfo);
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
        attentionOtherApi api = new attentionOtherApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                mBinding.setIsAttention(true);
                attentionDb.saveAttention(new AttentionInfo(discoverInfo.getUserId(), memberInfo.getNick(), memberInfo.getImage(), true, BaseActivity.userId));
                activity.sendBroadcast(new Intent("lobster_attentionList"));
                Intent intent = new Intent("lobster_attention");
                intent.putExtra("isAttention", mBinding.getIsAttention());
                activity.sendBroadcast(intent);
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
        cancelAttentionApi api = new cancelAttentionApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                mBinding.setIsAttention(false);
                attentionDb.saveAttention(new AttentionInfo(discoverInfo.getUserId(), memberInfo.getNick(), memberInfo.getImage(), false, BaseActivity.userId));
                activity.sendBroadcast(new Intent("lobster_attentionList"));
                Intent intent = new Intent("lobster_attention");
                intent.putExtra("isAttention", mBinding.getIsAttention());
                activity.sendBroadcast(intent);
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
                discoverInfo.setGoodNum(goodNum);
                mBinding.setDiscoverInfo(discoverInfo);
                mBinding.setGoodDb(goodDb);

                Intent data = new Intent("lobster_doGood");
                data.putExtra("goodNum", goodNum);
                data.putExtra("friendDynId", friendDynId);
                activity.sendBroadcast(data);
                seeLikers(1);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == 0) {
                    if (TextUtils.equals(e.getMessage(), "已经赞过了")) {
                        goodDb.saveGood(new CollectID(friendDynId));
                        mBinding.setGoodDb(goodDb);
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
                goodDb.deleteGood(friendDynId);
                mBinding.setGoodDb(goodDb);

                int goodNum = discoverInfo.getGoodNum() - 1;
                discoverInfo.setGoodNum(goodNum);
                mBinding.setDiscoverInfo(discoverInfo);

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
                        mBinding.setGoodDb(goodDb);
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
    public void makeEvaluate() {
        makeEvaluateApi api = new makeEvaluateApi(new HttpOnNextListener<Integer>() {
            @Override
            public void onNext(Integer o) {
                // 1喜欢成功 2匹配成功 3喜欢次数用尽
                String myHead = MineApp.mineInfo.getImage();
                String otherHead = memberInfo.getImage();
                if (o == 1) {
                    likeTypeDb.setType(discoverInfo.getUserId(), 2);
                    new SuperLikePW(activity, mBinding.getRoot(), myHead, otherHead, false, MineApp.mineInfo.getSex(), memberInfo.getSex(), null);
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

    @Override
    public void seeLikers(int pageNo) {
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
        }, activity).setFriendDynId(friendDynId).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void seeReviews(int pageNo) {
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
                        adapter.notifyDataSetChanged();
                        reviewList.addAll(tempList);
                        tempList.clear();
                        mBinding.reviewList.setVisibility(View.VISIBLE);
                        if (reviewList.size() > 2) {
                            mBinding.reviewList.setLayoutParams(new RelativeLayout.LayoutParams(-2, ObjectUtils.getViewSizeByWidthFromMax(335)));
                            mBinding.reviewList.start();
                        } else {
                            mBinding.reviewList.setLayoutParams(new RelativeLayout.LayoutParams(-2, -2));
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }, activity).setFriendDynId(friendDynId).setTimeSortType(1).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void initVideo() {
        //异常回调
        mBinding.videoView.setOnErrorListener((mp, what, extra) -> {
            return true;//如果方法处理了错误，则为true；否则为false。返回false或根本没有OnErrorListener，将导致调用OnCompletionListener。
        });
        mBinding.videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);//让电影循环播放
        });
        //信息回调
        mBinding.videoView.setOnInfoListener((mp, what, extra) -> {
            if (what == MediaPlayer.MEDIA_INFO_UNKNOWN || what == MediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {
                SCToastUtil.showToast(activity, "视频播放失败", true);
                mBinding.videoView.stopPlayback();//停止播放视频,并且释放
                mBinding.videoView.suspend();//在任何状态下释放媒体播放器
                return true;
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                // 缓冲结束,此接口每次回调完START就回调END,若不加上判断就会出现缓冲图标一闪一闪的卡顿现象
                if (mp.isPlaying()) {
                    mBinding.setIsProgress(false);
                }
                return true;
            } else if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                changeVideoSize(mp);
                mBinding.setIsProgress(false);
                mBinding.ivImage.setVisibility(View.GONE);
                mBinding.videoView.setBackgroundColor(Color.TRANSPARENT);
            }
            return false; //如果方法处理了信息，则为true；如果没有，则为false。返回false或根本没有OnInfoListener，将导致丢弃该信息。
        });
        mBinding.videoView.setVideoPath(discoverInfo.getVideoPath());
        mBinding.videoView.start();
    }

    /**
     * 修改预览View的大小,以用来适配屏幕
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
        Bitmap bitmap = textToBitmap("我的虾菇号：" + memberInfo.getUserId());
        getImage(bitmap);

        String[] common = addWaterMark(imageUrl, downloadPath, outPutUrl);
        FFmpeg.getInstance(activity).execute(common, new FFmpegExecuteResponseHandler() {
            @Override
            public void onSuccess(String message) {
                handler.sendEmptyMessage(1);
            }

            @Override
            public void onProgress(String message) {
                Log.e("onProgress", "111111111111111 = = = = = = = " + message);
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

    private Compressor mCompressor;

    private void setPermissions() {
        BaseActivity.createFfmpegFile();
        if (mCompressor == null) {
            mCompressor = new Compressor(activity);
            mCompressor.loadBinary(new InitListener() {
                @Override
                public void onLoadSuccess() {
                }

                @Override
                public void onLoadFail(String reason) {
                }
            });
        }
        createWater();
    }
}
