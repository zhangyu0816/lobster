package com.zb.module_bottle.vm;

import android.view.View;

import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.contact.YWContactFactory;
import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.YWConversation;
import com.zb.lib_base.api.myBottleApi;
import com.zb.lib_base.api.myImAccountInfoApi;
import com.zb.lib_base.api.otherImAccountInfoApi;
import com.zb.lib_base.api.replyBottleApi;
import com.zb.lib_base.api.thirdReadChatApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.imcore.LoginSampleHelper;
import com.zb.lib_base.model.BottleInfo;
import com.zb.lib_base.model.BottleMsg;
import com.zb.lib_base.model.ImAccount;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_bottle.BR;
import com.zb.module_bottle.R;
import com.zb.module_bottle.adapter.BottleAdapter;
import com.zb.module_bottle.databinding.BottleChatBinding;
import com.zb.module_bottle.iv.BottleChatVMInterface;
import com.zb.module_bottle.windows.BottleVipPW;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ViewDataBinding;

public class BottleChatViewModel extends BaseViewModel implements BottleChatVMInterface {
    public long driftBottleId;
    public BottleAdapter adapter;
    private List<BottleMsg> bottleMsgList = new ArrayList<>();
    private BottleChatBinding mBinding;
    public MineInfo mineInfo;
    public BottleInfo bottleInfo;

    // 阿里百川
    private LoginSampleHelper loginHelper;
    private String otherIMUserId;
    private YWConversation conversation;
    private int timeOut = 10 * 1000;
    private IYWConversationService mConversationService;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        mBinding = (BottleChatBinding) binding;
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
        adapter = new BottleAdapter<>(activity, R.layout.item_bottle_chat, bottleMsgList, this);
        myBottle();
    }

    @Override
    public void myBottle() {
        myBottleApi api = new myBottleApi(new HttpOnNextListener<BottleInfo>() {
            @Override
            public void onNext(BottleInfo o) {
                bottleInfo = o;
                bottleMsgList.addAll(o.getMessageList());
                adapter.notifyDataSetChanged();
                mBinding.chatList.scrollToPosition(adapter.getItemCount() - 1);
                mBinding.setVariable(BR.nick, bottleInfo.getOtherNick());
                if (loginHelper.getImCore() == null) {
                    myImAccountInfoApi();
                } else {
                    otherImAccountInfoApi();
                }
                thirdReadChat();
            }
        }, activity).setDriftBottleId(driftBottleId);
        HttpManager.getInstance().doHttpDeal(api);
    }

    @Override
    public void toMemberDetail(View view) {
        if (mineInfo.getMemberType() == 2) {
            ActivityUtils.getCardMemberDetail(bottleInfo.getUserId());
            return;
        }
        new BottleVipPW(activity, mBinding.getRoot());
    }

    @Override
    public void sendBottle(View view) {
        if (mBinding.getContent().isEmpty()) {
            SCToastUtil.showToastBlack(activity, "请输入回复内容");
            return;
        }
        replyBottleApi api = new replyBottleApi(new HttpOnNextListener<BottleMsg>() {
            @Override
            public void onNext(BottleMsg o) {
                if (o == null) {
                    SCToastUtil.showToastBlack(activity, "此漂流瓶已被销毁");
                } else {
                    bottleMsgList.add(o);
                    adapter.notifyDataSetChanged();
                    mBinding.chatList.scrollToPosition(adapter.getItemCount() - 1);
                }
            }
        }, activity).setDriftBottleId(driftBottleId).setText(mBinding.getContent());
        HttpManager.getInstance().doHttpDeal(api);
    }

    /**
     * 清除未读数量
     */
    private void thirdReadChat() {
        thirdReadChatApi api = new thirdReadChatApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {

            }
        }, activity).setOtherUserId(bottleInfo.getOtherUserId());
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
        api.setOtherUserId(bottleInfo.getOtherUserId());
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
