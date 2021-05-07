package com.zb.lib_base.mimc;

import com.xiaomi.mimc.MIMCGroupMessage;
import com.xiaomi.mimc.MIMCMessage;
import com.xiaomi.mimc.MIMCMessageHandler;
import com.xiaomi.mimc.MIMCOnlineMessageAck;
import com.xiaomi.mimc.MIMCOnlineStatusListener;
import com.xiaomi.mimc.MIMCRtsCallHandler;
import com.xiaomi.mimc.MIMCServerAck;
import com.xiaomi.mimc.MIMCTokenFetcher;
import com.xiaomi.mimc.MIMCUnlimitedGroupHandler;
import com.xiaomi.mimc.MIMCUser;
import com.xiaomi.mimc.common.MIMCConstant;
import com.xiaomi.mimc.data.LaunchedResponse;
import com.xiaomi.mimc.data.RtsChannelType;
import com.xiaomi.mimc.data.RtsDataType;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;

import org.json.JSONObject;

import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserManager {
    // online
    private long appId = 2882303761519880104L;
    private String appKey = "5251988015104";
    private String appSecret = "lIuFyZdIHxdrdZ7H4lJMkQ==";

    // 用户登录APP的帐号
    private MIMCUser mimcUser;
    private MIMCConstant.OnlineStatus mStatus;
    private final static UserManager instance = new UserManager();
    private OnHandleMIMCMsgListener onHandleMIMCMsgListener;
    public static final String TEXT = "TEXT";
    public static final String PIC_FILE = "PIC_FILE";

    // 设置消息监听
    public void setHandleMIMCMsgListener(OnHandleMIMCMsgListener onHandleMIMCMsgListener) {
        this.onHandleMIMCMsgListener = onHandleMIMCMsgListener;
    }

    @FunctionalInterface
    public interface OnHandleMIMCMsgListener {

        void onHandleMessage(CustomMessageBody customMessageBody, String thirdMessageId);

        default void onHandleStatusChanged(MIMCConstant.OnlineStatus status) {
        }

        default void onHandleServerAck(MIMCServerAck serverAck) {
        }
    }

    public static UserManager getInstance() {
        return instance;
    }

    /**
     * 获取用户帐号
     *
     * @return 成功返回用户帐号，失败返回""
     */
    public String getAccount() {
        return getMIMCUser() != null ? getMIMCUser().getAppAccount() : BaseActivity.userId + "";
    }

    /**
     * 获取用户
     *
     * @return 返回已创建用户
     */
    public MIMCUser getMIMCUser() {
        return mimcUser;
    }

    /**
     * 创建用户
     *
     * @param appAccount APP自己维护的用户帐号，不能为null
     * @return 返回新创建的用户
     */
    public MIMCUser newMIMCUser(String appAccount) {
        if (appAccount == null || appAccount.isEmpty()) return null;

        // 若是新用户，先释放老用户资源
        if (mimcUser != null) {
            mimcUser.logout();
            mimcUser.destroy();
        }

        // cachePath必须传入，用于缓存文件读写，否则返回null
        mimcUser = MIMCUser.newInstance(appId, appAccount, BaseActivity.getLogCachePath(), BaseActivity.getTokenCachePath());
        // 注册相关监听，必须
        mimcUser.registerTokenFetcher(new TokenFetcher());
        mimcUser.registerMessageHandler(new MessageHandler());
        mimcUser.registerOnlineStatusListener(new OnlineStatusListener());
        mimcUser.registerRtsCallHandler(new RTSHandler());
        mimcUser.registerUnlimitedGroupHandler(new UnlimitedGroupHandler());
        return mimcUser;
    }

    /**
     * 获取用户在线状态
     *
     * @return STATUS_LOGIN_SUCCESS 在线，STATUS_LOGOUT 下线，STATUS_LOGIN_FAIL 登录失败
     */
    public MIMCConstant.OnlineStatus getStatus() {
        return mStatus;
    }

    public void addMsg(CustomMessageBody customMessageBody, String thirdMessageId) {
        onHandleMIMCMsgListener.onHandleMessage(customMessageBody, thirdMessageId);
    }

    public void sendMsg(String toAppAccount, String bizType, int msgType, String stanza, String resLink,
                        int resTime, String summary, long driftBottleId, long flashTalkId, int msgChannelType) {
        CustomMessageBody body = new CustomMessageBody();
        body.setFromId(Long.parseLong(getAccount()));
        body.setToId(Long.parseLong(toAppAccount));
        body.setMsgType(msgType);
        body.setStanza(stanza);
        body.setResLink(resLink);
        body.setResTime(resTime);
        body.setSummary(summary);
        body.setDriftBottleId(driftBottleId);
        body.setFlashTalkId(flashTalkId);
        body.setMsgChannelType(msgChannelType);

        String json = MessageUtil.pack(body);
        mimcUser.sendMessage(toAppAccount, json.getBytes(), bizType, true);
        addMsg(body, "");
    }

    class MessageHandler implements MIMCMessageHandler {
        /**
         * 接收单聊消息
         * MIMCMessage类
         * String packetId 消息ID
         * long sequence 序列号
         * String fromAccount 发送方帐号
         * String toAccount 接收方帐号
         * byte[] payload 消息体
         * long timestamp 时间戳
         */
        @Override
        public boolean handleMessage(List<MIMCMessage> packets) {
            for (int i = 0; i < packets.size(); ++i) {
                MIMCMessage message = packets.get(i);
                try {
                    addMsg(MessageUtil.unpack(new String(message.getPayload())), message.getPacketId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        /**
         * 接收群聊消息
         * MIMCGroupMessage类
         * String packetId 消息ID
         * long groupId 群ID
         * long sequence 序列号
         * String fromAccount 发送方帐号
         * byte[] payload 消息体
         * long timestamp 时间戳
         */
        @Override
        public boolean handleGroupMessage(List<MIMCGroupMessage> packets) {
            return true;
        }

        /**
         * 接收服务端已收到发送消息确认
         * MIMCServerAck类
         * String packetId 消息ID
         * long sequence 序列号
         * long timestamp 时间戳
         */
        @Override
        public void handleServerAck(MIMCServerAck serverAck) {
            onHandleMIMCMsgListener.onHandleServerAck(serverAck);
        }

        /**
         * 接收单聊超时消息
         *
         * @param message 单聊消息类
         */
        @Override
        public void handleSendMessageTimeout(MIMCMessage message) {
            try {
                addMsg(MessageUtil.unpack(new String(message.getPayload())), message.getPacketId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * 接收发送群聊超时消息
         *
         * @param groupMessage 群聊消息类
         */
        @Override
        public void handleSendGroupMessageTimeout(MIMCGroupMessage groupMessage) {
        }

        @Override
        public void handleSendUnlimitedGroupMessageTimeout(MIMCGroupMessage mimcGroupMessage) {

        }

        @Override
        public boolean handleUnlimitedGroupMessage(List<MIMCGroupMessage> packets) {
            return true;
        }

        @Override
        public boolean onPullNotification(long minSequence, long maxSequence) {
            return true;
        }

        @Override
        public void handleOnlineMessage(MIMCMessage message) {
            try {
                addMsg(MessageUtil.unpack(new String(message.getPayload())), message.getPacketId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void handleOnlineMessageAck(MIMCOnlineMessageAck onlineMessageAck) {
        }
    }

    class TokenFetcher implements MIMCTokenFetcher {
        @Override
        public String fetchToken() {

            String url = HttpManager.BASE_URL + "myapi/Return_getToken?appAccountId=" + getAccount() +
                    "&userId=" + BaseActivity.userId +
                    "&sessionId=" + BaseActivity.sessionId +
                    "&pfDevice=Android&pfAppType=203&pfAppVersion=" + MineApp.versionName;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request
                    .Builder()
                    .url(url)
                    .build();
            Call call = client.newCall(request);
            JSONObject data = null;
            try {
                Response response = call.execute();
                data = new JSONObject(response.body().string());
                int code = data.getInt("code");
                if (code != 1) {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject returnData =data.optJSONObject("data");
            return returnData != null ? returnData.toString() : null;
        }
    }


    class OnlineStatusListener implements MIMCOnlineStatusListener {
        @Override
        public void statusChange(MIMCConstant.OnlineStatus status, String type, String reason, String desc) {
            mStatus = status;
            onHandleMIMCMsgListener.onHandleStatusChanged(status);
        }
    }


    class RTSHandler implements MIMCRtsCallHandler {
        @Override
        public LaunchedResponse onLaunched(String fromAccount, String fromResource, long callId, byte[] appContent) {
            return new LaunchedResponse(false, "");
        }

        @Override
        public void onAnswered(long callId, boolean accepted, String errMsg) {
        }

        @Override
        public void onData(long callId, String fromAccount, String resource, byte[] data, RtsDataType dataType, RtsChannelType channelType) {
        }

        @Override
        public void onClosed(long callId, String errMsg) {
        }

        @Override
        public void onSendDataSuccess(long callId, int dataId, Object context) {

        }

        @Override
        public void onSendDataFailure(long callId, int dataId, Object context) {

        }
    }

    class UnlimitedGroupHandler implements MIMCUnlimitedGroupHandler {
        @Override
        public void handleCreateUnlimitedGroup(long topicId, String topicName, int code, String desc, Object obj) {
        }

        @Override
        public void handleJoinUnlimitedGroup(long topicId, int code, String errMsg, Object obj) {
        }

        @Override
        public void handleQuitUnlimitedGroup(long topicId, int code, String errMsg, Object obj) {
        }

        @Override
        public void handleDismissUnlimitedGroup(long topicId, int code, String errMsg, Object obj) {
        }

        @Override
        public void handleDismissUnlimitedGroup(long topicId) {

        }
    }
}