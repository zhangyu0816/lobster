package com.zb.module_mine.vm;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.contact.YWContactFactory;
import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.YWConversation;
import com.maning.imagebrowserlibrary.MNImage;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.clearHistoryMsgApi;
import com.zb.lib_base.api.myImAccountInfoApi;
import com.zb.lib_base.api.otherImAccountInfoApi;
import com.zb.lib_base.api.systemHistoryMsgListApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.ResFileDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.imcore.LoginSampleHelper;
import com.zb.lib_base.model.ImAccount;
import com.zb.lib_base.model.SystemMsg;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DownLoad;
import com.zb.lib_base.views.SoundView;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.databinding.MineSystemMsgBinding;
import com.zb.module_mine.iv.SystemMsgVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import io.realm.Realm;

public class SystemMsgViewModel extends BaseViewModel implements SystemMsgVMInterface, OnRefreshListener, OnLoadMoreListener {
    public MineAdapter adapter;
    public ResFileDb resFileDb;
    private List<SystemMsg> systemMsgList = new ArrayList<>();
    private int pageNo = 1;
    private MineSystemMsgBinding mBinding;
    private AnimationDrawable drawable; // 语音播放
    private ImageView preImageView;
    private int preDirection;
    private SoundView soundView;
    private ObjectAnimator animator;
    private BaseReceiver finishRefreshReceiver;
    private LoginSampleHelper loginHelper;
    private String otherIMUserId;
    private YWConversation conversation;
    private IYWConversationService mConversationService;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (MineSystemMsgBinding) binding;
        resFileDb = new ResFileDb(Realm.getDefaultInstance());
        setAdapter();
        soundView = new SoundView(activity, view -> stopVoiceDrawable());
        finishRefreshReceiver = new BaseReceiver(activity, "lobster_finishRefresh") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();
            }
        };
        loginHelper = LoginSampleHelper.getInstance();
        if (loginHelper.getImCore() == null) {
            myImAccountInfoApi();
        } else {
            otherImAccountInfoApi();
        }
    }

    public void onDestroy() {
        finishRefreshReceiver.unregisterReceiver();
    }

    @Override
    public void back(View view) {
        super.back(view);
        try {
            mConversationService.markReaded(conversation);
        } catch (Exception e) {
        }
        activity.finish();
    }

    @Override
    public void setAdapter() {
        adapter = new MineAdapter<>(activity, R.layout.item_system_msg, systemMsgList, this);
        systemHistoryMsgList();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        pageNo++;
        systemHistoryMsgList();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        // 下拉刷新
        mBinding.refresh.setEnableLoadMore(true);
        pageNo = 1;
        systemMsgList.clear();
        adapter.notifyDataSetChanged();
        systemHistoryMsgList();
    }

    @Override
    public void systemHistoryMsgList() {
        systemHistoryMsgListApi api = new systemHistoryMsgListApi(new HttpOnNextListener<List<SystemMsg>>() {
            @Override
            public void onNext(List<SystemMsg> o) {
                int start = systemMsgList.size();
                systemMsgList.addAll(o);
                adapter.notifyItemRangeChanged(start, systemMsgList.size());
                mBinding.refresh.finishRefresh();
                mBinding.refresh.finishLoadMore();
                if (pageNo == 1)
                    clearHistoryMsg(o.get(0).getId());
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    mBinding.refresh.setEnableLoadMore(false);
                    mBinding.refresh.finishRefresh();
                    mBinding.refresh.finishLoadMore();
                }
            }
        }, activity).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void clearHistoryMsg(long messageId) {
        clearHistoryMsgApi api = new clearHistoryMsgApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                MineApp.mineNewsCount.setMsgType(0);
                MineApp.mineNewsCount.setSystemNewsNum(0);
                activity.sendBroadcast(new Intent("lobster_newsCount"));
            }
        }, activity).setMessageId(messageId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void toImageVideo(View view, SystemMsg systemMsg) {
        if (systemMsg.getMsgType() == 2) {
            ArrayList<String> imageList = new ArrayList<>();
            imageList.add(systemMsg.getResLink());
            MNImage.imageBrowser(activity, mBinding.getRoot(), imageList, 0, false, null);
        } else {
            ImageView ivPlay = view.findViewById(R.id.iv_play);
            ImageView ivProgress = view.findViewById(R.id.iv_progress);
            ivPlay.setVisibility(View.GONE);
            DownLoad.getFilePath(systemMsg.getResLink(), BaseActivity.getDownloadFile(".mp4").getAbsolutePath(), new DownLoad.CallBack() {
                @Override
                public void success(String filePath) {
                    ivPlay.setVisibility(View.VISIBLE);
                    ivProgress.setVisibility(View.GONE);
                    if (animator != null)
                        animator.cancel();
                    ActivityUtils.getCameraVideoPlay(filePath);
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
        DownLoad.getFilePath(systemMsg.getResLink(), BaseActivity.getDownloadFile(".amr").getAbsolutePath(), filePath -> {
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

    private void stopVoiceDrawable() {
        if (drawable != null) {
            soundView.stopPlayer();
            preImageView.setImageResource(preDirection == 0 ? R.mipmap.icon_voice_3_left : R.mipmap.icon_voice_3_right);
            drawable.stop();
            drawable = null;
        }
    }

    /**
     * 阿里百川登录账号
     */
    private void myImAccountInfoApi() {
        myImAccountInfoApi api = new myImAccountInfoApi(new HttpOnNextListener<ImAccount>() {
            @Override
            public void onNext(ImAccount o) {
                loginHelper.loginOut_Sample();
                loginHelper.login_Sample(activity, o.getImUserId(), o.getImPassWord());
                otherImAccountInfoApi();
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    /**
     * 对方的阿里百川账号
     */
    private void otherImAccountInfoApi() {
        otherImAccountInfoApi api = new otherImAccountInfoApi(new HttpOnNextListener<ImAccount>() {
            @Override
            public void onNext(ImAccount o) {
                otherIMUserId = o.getImUserId();
                mConversationService = loginHelper.getConversationService();
                conversation = mConversationService.getConversationByUserId(otherIMUserId, LoginSampleHelper.APP_KEY);
                checkConversation();
            }
        }, activity);
        api.setOtherUserId(BaseActivity.systemUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    /**
     * 检查 conversation
     */
    private void checkConversation() {
        if (conversation == null) { // 这里必须判空
            IYWContact contact = YWContactFactory.createAPPContact(otherIMUserId, LoginSampleHelper.APP_KEY);
            conversation = mConversationService.getConversationCreater().createConversationIfNotExist(contact);
        }
    }
}
