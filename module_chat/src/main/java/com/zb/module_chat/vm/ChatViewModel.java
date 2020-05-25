package com.zb.module_chat.vm;

import android.graphics.drawable.AnimationDrawable;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.contact.YWContactFactory;
import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.YWConversation;
import com.maning.imagebrowserlibrary.MNImage;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zb.lib_base.api.historyMsgListApi;
import com.zb.lib_base.api.myImAccountInfoApi;
import com.zb.lib_base.api.otherImAccountInfoApi;
import com.zb.lib_base.api.otherInfoApi;
import com.zb.lib_base.api.readOverHistoryMsgApi;
import com.zb.lib_base.api.thirdHistoryMsgListApi;
import com.zb.lib_base.api.thirdReadChatApi;
import com.zb.lib_base.db.HistoryMsgDb;
import com.zb.lib_base.db.ResFileDb;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.imcore.LoginSampleHelper;
import com.zb.lib_base.model.HistoryMsg;
import com.zb.lib_base.model.ImAccount;
import com.zb.lib_base.model.MemberInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_chat.BR;
import com.zb.module_chat.R;
import com.zb.module_chat.adapter.ChatAdapter;
import com.zb.module_chat.databinding.ChatChatBinding;
import com.zb.module_chat.iv.ChatVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import io.realm.Realm;

public class ChatViewModel extends BaseViewModel implements ChatVMInterface, OnRefreshListener {
    public long otherUserId;
    public MemberInfo memberInfo;
    public ChatAdapter adapter;
    public MineInfo mineInfo;
    public ResFileDb resFileDb;
    private ChatChatBinding mBinding;
    private List<HistoryMsg> historyMsgList = new ArrayList<>();
    private int pageNo = 0;
    private int pageSize = 20;
    private HistoryMsgDb historyMsgDb;
    private LoginSampleHelper loginHelper;
    // 阿里百川
    private String otherIMUserId;
    private YWConversation conversation;
    private int timeOut = 10 * 1000;
    private IYWConversationService mConversationService;
    private long historyMsgId = 0;
    private boolean updateAll = false;
    private AnimationDrawable drawable; // 语音播放
    private CountDownTimer timer;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (ChatChatBinding) binding;
        resFileDb = new ResFileDb(Realm.getDefaultInstance());
        historyMsgDb = new HistoryMsgDb(Realm.getDefaultInstance());
        mineInfo = mineInfoDb.getMineInfo();
        loginHelper = LoginSampleHelper.getInstance();
        setAdapter();
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        historyMsgList.addAll(historyMsgDb.getLimitList(historyMsgDb.getRealmResults(), pageNo * pageSize, pageSize));
        adapter = new ChatAdapter<>(activity, R.layout.item_chat, historyMsgList, this);
        otherInfo();
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (updateAll) {
            mBinding.refresh.finishRefresh();
            return;
        }
        pageNo++;
        List<HistoryMsg> tempList = historyMsgDb.getLimitList(historyMsgDb.getRealmResults(), pageNo * pageSize, pageSize);
        historyMsgList.addAll(0, tempList);
        adapter.notifyItemRangeChanged(0, tempList.size());
        updateAll = tempList.size() == 0;
        mBinding.refresh.finishRefresh();
    }

    @Override
    public void otherInfo() {
        otherInfoApi api = new otherInfoApi(new HttpOnNextListener<MemberInfo>() {
            @Override
            public void onNext(MemberInfo o) {
                memberInfo = o;
                mBinding.setVariable(BR.viewModel, this);

                if (loginHelper.getImCore() == null) {
                    myImAccountInfoApi();
                } else {
                    otherImAccountInfoApi();
                }
                thirdReadChat();
                new Thread(() -> historyMsgList(0)).start();
            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void historyMsgList(int pageNo) {
        historyMsgListApi api = new historyMsgListApi(new HttpOnNextListener<List<HistoryMsg>>() {
            @Override
            public void onNext(List<HistoryMsg> o) {
                for (HistoryMsg historyMsg : o) {
                    historyMsgDb.saveHistoryMsg(historyMsg);
                }
                historyMsgId = o.get(o.size() - 1).getId();
                historyMsgList(pageNo + 1);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    if (historyMsgId > 0) {
                        readOverHistoryMsg();
                    } else {
                        thirdHistoryMsgList(0);
                    }
                }
            }
        }, activity).setOtherUserId(otherUserId).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void thirdHistoryMsgList(int pageNo) {
        thirdHistoryMsgListApi api = new thirdHistoryMsgListApi(new HttpOnNextListener<List<HistoryMsg>>() {
            @Override
            public void onNext(List<HistoryMsg> o) {
                for (HistoryMsg historyMsg : o) {
                    historyMsgDb.saveHistoryMsg(historyMsg);
                }
                thirdHistoryMsgList(pageNo + 1);
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.NO_DATA) {
                    historyMsgList.clear();
                    historyMsgList.addAll(historyMsgDb.getLimitList(historyMsgDb.getRealmResults(), pageNo * pageSize, pageSize));
                    adapter.notifyDataSetChanged();
                }
            }
        }, activity).setOtherUserId(otherUserId).setPageNo(pageNo);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void toDetail(View view) {
        ActivityUtils.getCardMemberDetail(otherUserId);
    }

    @Override
    public void toImageVideo(HistoryMsg historyMsg) {
        if (historyMsg.getMsgType() == 2) {
            ArrayList<String> imageList = new ArrayList<>();
            imageList.add(historyMsg.getResLink());
            MNImage.imageBrowser(activity, mBinding.getRoot(), imageList, 0);
        } else {
            ActivityUtils.getCameraVideoPlay(resFileDb.getResFile(historyMsg.getResLink()).getFilePath());
        }
    }

    private ImageView preImageView;
    private int preDirection;

    @Override
    public void toVoice(ImageView view, HistoryMsg historyMsg, int direction) {
        // direction 0 左  1右
        if (drawable != null) {
            preImageView.setImageResource(preDirection == 0 ? R.mipmap.icon_voice_3_left : R.mipmap.icon_voice_3_right);
            timer.cancel();
            drawable.stop();
            drawable = null;
            timer = null;
        }
        preImageView = view;
        preDirection = direction;

        view.setImageResource(direction == 0 ? R.drawable.voice_chat_anim_left : R.drawable.voice_chat_anim_right);
        drawable = (AnimationDrawable) view.getDrawable();
        drawable.start();
        timer = new CountDownTimer(historyMsg.getResTime(), 100) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                view.setImageResource(direction == 0 ? R.mipmap.icon_voice_3_left : R.mipmap.icon_voice_3_right);
                timer.cancel();
                drawable.stop();
                drawable = null;
                timer = null;
            }
        };
        timer.start();
    }

    /**
     * 清空用户消息
     */
    private void readOverHistoryMsg() {
        readOverHistoryMsgApi api = new readOverHistoryMsgApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                thirdHistoryMsgList(0);
            }
        }, activity).setOtherUserId(otherUserId).setMessageId(historyMsgId);
        HttpManager.getInstance().doHttpDeal(api);
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
        api.setOtherUserId(otherUserId);
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

    /**
     * 清除未读数量
     */
    private void thirdReadChat() {
        thirdReadChatApi api = new thirdReadChatApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {

            }
        }, activity).setOtherUserId(otherUserId);
        HttpManager.getInstance().doHttpDeal(api);
    }
}
