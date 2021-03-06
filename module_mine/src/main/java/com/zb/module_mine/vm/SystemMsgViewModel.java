package com.zb.module_mine.vm;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.api.clearHistoryMsgApi;
import com.zb.lib_base.api.dynDetailApi;
import com.zb.lib_base.api.systemHistoryMsgListApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.SystemMsgDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.StanzaInfo;
import com.zb.lib_base.model.SystemMsg;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.DownLoad;
import com.zb.lib_base.utils.MNImage;
import com.zb.lib_base.views.SoundView;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.BR;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.databinding.MineSystemMsgBinding;
import com.zb.module_mine.iv.SystemMsgVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ViewDataBinding;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class SystemMsgViewModel extends BaseViewModel implements SystemMsgVMInterface {
    public MineAdapter adapter;
    private List<SystemMsg> systemMsgList = new ArrayList<>();
    private int pageNo = 1;
    private MineSystemMsgBinding mBinding;
    private AnimationDrawable drawable; // 语音播放
    private ImageView preImageView;
    private int preDirection;
    private SoundView soundView;
    private ObjectAnimator animator;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (MineSystemMsgBinding) binding;
        setAdapter();
        soundView = new SoundView(activity, view -> stopVoiceDrawable());
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        systemMsgList.addAll(SystemMsgDb.getInstance().getSystemMsgList());
        adapter = new MineAdapter<>(activity, R.layout.item_system_msg, systemMsgList, this);
        if (systemMsgList.size() > 0)
            updateTime();
        systemHistoryMsgList();
    }

    @Override
    public void systemHistoryMsgList() {
        systemHistoryMsgListApi api = new systemHistoryMsgListApi(new HttpOnNextListener<List<SystemMsg>>() {
            @Override
            public void onNext(List<SystemMsg> o) {
                mBinding.setVariable(BR.noData, false);
                for (SystemMsg item : o) {
                    item.setMainUserId(BaseActivity.userId);
                    SystemMsgDb.getInstance().saveSystemMsg(item);
                }
                pageNo++;
                systemHistoryMsgList();
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    systemMsgList.clear();
                    adapter.notifyDataSetChanged();
                    systemMsgList.addAll(SystemMsgDb.getInstance().getSystemMsgList());
                    if (systemMsgList.size() > 0) {
                        clearHistoryMsg(systemMsgList.get(0).getId());
                        updateTime();
                    } else
                        mBinding.setVariable(BR.noData, true);
                }
            }
        }, activity).setPageNo(pageNo);
        api.setShowProgress(systemMsgList.size() == 0);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void updateTime() {
        String time;
        SystemMsgDb.getInstance().setShowTime(systemMsgList.get(0).getId(), true);
        time = systemMsgList.get(0).getCreationDate();
        for (int i = 1; i < systemMsgList.size(); i++) {
            int data = DateUtil.getDateCount(time, systemMsgList.get(i).getCreationDate(), DateUtil.yyyy_MM_dd_HH_mm_ss, 1000f * 60f);
            if (data > 3) {
                time = systemMsgList.get(i).getCreationDate();
                SystemMsgDb.getInstance().setShowTime(systemMsgList.get(i).getId(), true);
            } else {
                SystemMsgDb.getInstance().setShowTime(systemMsgList.get(i).getId(), false);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void clearHistoryMsg(long messageId) {
        clearHistoryMsgApi api = new clearHistoryMsgApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                MineApp.mineNewsCount.setMsgType(0);
                MineApp.mineNewsCount.setSystemNewsNum(0);
                LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent("lobster_newsCount"));
            }
        }, activity).setMessageId(messageId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void toImageVideo(View view, SystemMsg systemMsg) {
        if (systemMsg.getMsgType() == 2) {
            ArrayList<String> imageList = new ArrayList<>();
            imageList.add(systemMsg.getResLink());
            MNImage.imageBrowser(activity, mBinding.getRoot(),0, imageList, 0, false, null);
        } else {
            ImageView ivPlay = view.findViewById(R.id.iv_play);
            ImageView ivProgress = view.findViewById(R.id.iv_progress);
            ivPlay.setVisibility(View.GONE);
            DownLoad.getFilePath(systemMsg.getResLink(), BaseActivity.getDownloadFile(".mp4").getAbsolutePath(), new DownLoad.CallBack() {
                @Override
                public void success(String filePath, Bitmap bitmap) {
                    ivPlay.setVisibility(View.VISIBLE);
                    ivProgress.setVisibility(View.GONE);
                    if (animator != null)
                        animator.cancel();
                    ActivityUtils.getCameraVideoPlay(filePath, false, false);
                }

                @Override
                public void onLoading(long total, long current) {
                    animator = ObjectAnimator.ofFloat(ivProgress, "rotation", 0, 360).setDuration(700);
                    animator.setRepeatMode(ValueAnimator.RESTART);
                    animator.setRepeatCount(Animation.INFINITE);
                    animator.start();
                    ivPlay.setVisibility(View.GONE);
                    ivProgress.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    public void toVoice(View view, SystemMsg systemMsg, int direction) {
        DownLoad.getFilePath(systemMsg.getResLink(), BaseActivity.getDownloadFile(".amr").getAbsolutePath(), (filePath, bitmap) -> {
            // direction 0 左  1右
            stopVoiceDrawable();
            preImageView = (ImageView) view;
            preDirection = direction;

            preImageView.setImageResource(direction == 0 ? R.drawable.voice_chat_anim_left : R.drawable.voice_chat_anim_right);
            drawable = (AnimationDrawable) preImageView.getDrawable();
            drawable.start();

            soundView.soundPlayer(filePath, view);
        });
    }

    @Override
    public void check(StanzaInfo stanzaInfo) {
        if (stanzaInfo.getLink().contains("person_detail")) {
            ActivityUtils.getCardMemberDetail(Long.parseLong(stanzaInfo.getLink().replace("zw://appview/person_detail?userId=", "")), false);
        } else {
            dynDetail(Long.parseLong(stanzaInfo.getLink().replace("zw://appview/dynamic_detail?friendDynId=", "")));
        }
    }

    private void dynDetail(long discoverId) {
        dynDetailApi api = new dynDetailApi(new HttpOnNextListener<DiscoverInfo>() {
            @Override
            public void onNext(DiscoverInfo o) {
                if (o.getVideoUrl().isEmpty()) {
                    ActivityUtils.getHomeDiscoverDetail(discoverId);
                } else {
                    ActivityUtils.getHomeDiscoverVideo(discoverId);
                }
            }
        }, activity).setFriendDynId(discoverId);
        api.setShowProgress(false);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void stopVoiceDrawable() {
        if (drawable != null) {
            soundView.stopPlayer();
            preImageView.setImageResource(preDirection == 0 ? R.mipmap.icon_voice_3_left : R.mipmap.icon_voice_3_right);
            drawable.stop();
            drawable = null;
        }
    }
}
