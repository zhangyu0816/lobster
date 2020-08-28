package com.zb.module_mine.vm;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.api.clearHistoryMsgApi;
import com.zb.lib_base.api.dynDetailApi;
import com.zb.lib_base.api.myImAccountInfoApi;
import com.zb.lib_base.api.otherImAccountInfoApi;
import com.zb.lib_base.api.systemHistoryMsgListApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.ResFileDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.imcore.LoginSampleHelper;
import com.zb.lib_base.model.DiscoverInfo;
import com.zb.lib_base.model.ImAccount;
import com.zb.lib_base.model.StanzaInfo;
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

import androidx.databinding.ViewDataBinding;
import io.realm.Realm;

public class SystemMsgViewModel extends BaseViewModel implements SystemMsgVMInterface {
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
        loginHelper = LoginSampleHelper.getInstance();
        if (loginHelper.getImCore() == null) {
            myImAccountInfoApi();
        } else {
            otherImAccountInfoApi();
        }
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
    public void systemHistoryMsgList() {
        systemHistoryMsgListApi api = new systemHistoryMsgListApi(new HttpOnNextListener<List<SystemMsg>>() {
            @Override
            public void onNext(List<SystemMsg> o) {
                int start = systemMsgList.size();
                systemMsgList.addAll(o);
                adapter.notifyItemRangeChanged(start, systemMsgList.size());
                pageNo++;
                systemHistoryMsgList();
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
                    ActivityUtils.getHomeDiscoverVideoL2(discoverId);
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
