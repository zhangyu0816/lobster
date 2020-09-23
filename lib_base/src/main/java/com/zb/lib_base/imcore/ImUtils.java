package com.zb.lib_base.imcore;

import android.os.Handler;
import android.util.Log;

import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.contact.YWContactFactory;
import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.YWConversation;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.conversation.YWMessageBody;
import com.alibaba.mobileim.conversation.YWMessageChannel;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.api.myImAccountInfoApi;
import com.zb.lib_base.api.otherImAccountInfoApi;
import com.zb.lib_base.http.CustomProgressDialog;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpTimeException;
import com.zb.lib_base.model.ImAccount;
import com.zb.lib_base.utils.SCToastUtil;

public class ImUtils {
    public volatile static ImUtils INSTANCE;
    private CallBackForMsg callBackForMsg;
    private CallBackForLogin callBackForLogin;
    private YWConversation conversation;
    private IYWConversationService mConversationService;
    private int timeOut = 10 * 1000;

    private LoginSampleHelper loginHelper;
    private String otherIMUserId;
    private long otherUserId;
    private boolean needLink = false;
    private boolean isDelete = false;
    private boolean isChat = true;

    public ImUtils() {
        loginHelper = LoginSampleHelper.getInstance();
    }

    //获取单例
    public static ImUtils getInstance() {
        if (INSTANCE == null) {
            synchronized (ImUtils.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ImUtils();
                }
            }
        }
        return INSTANCE;
    }

    public void setOtherUserId(long otherUserId) {
        this.otherUserId = otherUserId;
    }

    public void setCallBackForMsg(CallBackForMsg callBackForMsg) {
        this.callBackForMsg = callBackForMsg;
    }

    public void setCallBackForLogin(CallBackForLogin callBackForLogin) {
        this.callBackForLogin = callBackForLogin;
    }

    public interface CallBackForMsg {
        void updateMsg(String msgId, CustomMessageBody body);
    }

    public interface CallBackForLogin {
        void success();
    }

    /**
     * 删除聊天记录
     */
    public void setDelete(boolean delete, RxAppCompatActivity activity) {
        isDelete = delete;
        if (delete)
            otherImAccountInfoApi(activity);
    }

    /**
     * 建立连接
     */
    public void setChat(boolean chat, RxAppCompatActivity activity) {
        isChat = chat;
        myImAccountInfoApi(activity);
    }

    /**
     * 发送消息
     */
    private CustomMessageBody body;
    private YWMessage message;

    public void sendChatMessage(RxAppCompatActivity activity, final int msgType, final String stanza, final String resLink, final int resTime, final String summary, long driftBottleId, int msgChannelType) {
        YWMessageBody mainBody = new YWMessageBody();
        body = new CustomMessageBody(msgType, stanza, resLink, resTime, BaseActivity.userId, otherUserId, summary, driftBottleId, msgChannelType);
        mainBody.setSummary(body.getSummary());
        mainBody.setContent(loginHelper.pack(body));
        message = YWMessageChannel.createCustomMessage(mainBody);

        if (mConversationService == null) {
            needLink = true;
            CustomProgressDialog.showLoading(activity, "已断开连接，正在重新连接...");
            myImAccountInfoApi(activity);
        } else {
            if (conversation == null) { // 这里必须判空
                IYWContact contact = YWContactFactory.createAPPContact(otherIMUserId, LoginSampleHelper.APP_KEY);
                conversation = mConversationService.getConversationCreater().createConversationIfNotExist(contact);
            }
            conversation.getMessageSender().sendMessage(message, timeOut, new IWxCallback() {

                @Override
                public void onSuccess(Object... arg0) {
                    if (callBackForMsg != null)
                        callBackForMsg.updateMsg(message.getMsgId() + "", body);
                }

                @Override
                public void onProgress(int arg0) {

                }

                @Override
                public void onError(int arg0, String arg1) {
                    Log.e("onError", arg1);
                }
            });
        }
    }

    /**
     * 清除角标
     */
    public void markRead() {
        try {
            mConversationService.markReaded(conversation);
        } catch (Exception ignored) {
        }
    }

    /**
     * 清除记录
     */
    public void deleteAllMsg() {
        conversation.getMessageLoader().deleteAllMessage();
    }

    public void loginOutIM() {
        loginHelper.loginOut_Sample();
    }


    /**
     * 阿里百川登录账号
     */
    private int requestCount = 1;

    private void myImAccountInfoApi(RxAppCompatActivity activity) {
        if (loginHelper.getImCore() == null) {
            requestCount = 1;
            myImAccountInfo(activity);
        } else {
            checkChat(activity);
        }
    }

    private void myImAccountInfo(RxAppCompatActivity activity) {
        myImAccountInfoApi api = new myImAccountInfoApi(new HttpOnNextListener<ImAccount>() {
            @Override
            public void onNext(ImAccount o) {
                loginOutIM();
                loginHelper.login_Sample(activity, o.getImUserId(), o.getImPassWord(), () -> checkChat(activity));
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof HttpTimeException && ((HttpTimeException) e).getCode() == HttpTimeException.ERROR) {
                    requestCount++;
                    if (requestCount < 6)
                        new Handler().postDelayed(() -> myImAccountInfo(activity), 3000);
                    else {
                        loginOutIM();
                        SCToastUtil.showToast(activity, "当前聊天网络拥挤，请稍后再试", true);
                    }
                }
            }
        }, activity);
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void checkChat(RxAppCompatActivity activity) {
        if (isChat)
            otherImAccountInfoApi(activity);
        else {
            if (callBackForLogin != null) {
                callBackForLogin.success();
            }
        }
    }

    /**
     * 对方的阿里百川账号
     */
    private void otherImAccountInfoApi(RxAppCompatActivity activity) {
        otherImAccountInfoApi api = new otherImAccountInfoApi(new HttpOnNextListener<ImAccount>() {
            @Override
            public void onNext(ImAccount o) {
                otherIMUserId = o.getImUserId();
                mConversationService = loginHelper.getConversationService();
                conversation = mConversationService.getConversationByUserId(otherIMUserId, LoginSampleHelper.APP_KEY);
                checkConversation();
                if (needLink) {
                    needLink = false;
                    CustomProgressDialog.stopLoading();
                    SCToastUtil.showToast(activity, "连接成功", true);
                    conversation.getMessageSender().sendMessage(message, timeOut, new IWxCallback() {

                        @Override
                        public void onSuccess(Object... arg0) {
                            if (callBackForMsg != null)
                                callBackForMsg.updateMsg(message.getMsgId() + "", body);
                        }

                        @Override
                        public void onProgress(int arg0) {

                        }

                        @Override
                        public void onError(int arg0, String arg1) {
                            Log.e("onError", arg1);
                        }
                    });
                }

                if (isDelete) {
                    deleteAllMsg();
                }
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


}
