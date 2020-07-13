package com.zb.lib_base.imcore;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.util.Log;

import com.alibaba.mobileim.IYWLoginService;
import com.alibaba.mobileim.IYWPushListener;
import com.alibaba.mobileim.YWAPI;
import com.alibaba.mobileim.YWIMCore;
import com.alibaba.mobileim.YWLoginParam;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.YWConversation;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.conversation.YWMessageBody;
import com.alibaba.mobileim.gingko.model.tribe.YWTribe;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.R;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.ActivityUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import androidx.core.app.NotificationCompat;

/**
 * Created by DIY on 2019-03-12.
 */

public class LoginSampleHelper {

    private static LoginSampleHelper sInstance = new LoginSampleHelper();

    public static LoginSampleHelper getInstance() {
        return sInstance;
    }

    private RxAppCompatActivity activity;
    public static String APP_KEY = "23484729";
    private YWIMCore imCore;
    private IYWConversationService conversationService;

    public YWIMCore getImCore() {
        return imCore;
    }

    public void setImCore(YWIMCore imCore) {
        this.imCore = imCore;
    }

    private void initYWIMCore(String userId) {
        imCore = YWAPI.createIMCore(userId, APP_KEY);
        addPushMessageListener();
    }

    public IYWConversationService getConversationService() {
        return conversationService;
    }

    /**
     * 登录操作
     *
     * @param userId   用户id
     * @param password 密码
     */
    //------------------请特别注意，OpenIMSDK会自动对所有输入的用户ID转成小写处理-------------------
    //所以开发者在注册用户信息时，尽量用小写
    public void login_Sample(RxAppCompatActivity activity, String userId, String password) {
        this.activity = activity;
        initYWIMCore(userId);

        YWLoginParam loginParam = YWLoginParam.createLoginParam(userId, password);
        // openIM SDK提供的登录服务
        IYWLoginService mLoginService = imCore.getLoginService();
        mLoginService.login(loginParam, new IWxCallback() {
            @Override
            public void onSuccess(Object... objects) {
                Log.i("onSuccess", "阿里登录成功");
            }

            @Override
            public void onError(int i, String s) {
                Log.e("onSuccess", "code == " + i + "   阿里登录失败:" + s);
            }

            @Override
            public void onProgress(int i) {

            }
        });
    }

    /**
     * 添加新消息到达监听，该监听应该在登录之前调用以保证登录后可以及时收到消息
     */
    private void addPushMessageListener() {
        conversationService = imCore.getConversationService();
        //添加单聊消息监听，先删除再添加，以免多次添加该监听
        conversationService.removePushListener(iywPushListener);
        conversationService.addPushListener(iywPushListener);
//        conversationService.removeP2PPushListener(mP2PListener);
//        conversationService.addP2PPushListener(mP2PListener);
    }

    private IYWPushListener iywPushListener = new IYWPushListener() {
        @Override
        public void onPushMessage(IYWContact contact, YWMessage ywMessage) {
            CustomMessageBody body = (CustomMessageBody) unpack(ywMessage.getContent());
            if (body.getMsgType() > 4)
                return;
            if (isBackground()) {
                sendMsg(ywMessage);
            } else {
                try {
                    JSONObject data = new JSONObject(
                            new JSONObject(ywMessage.getContent()).getString("customize"));
                    if (data.has("message")) { // 聊天信息
                        if (body.getFromId() != BaseActivity.userId)
                            appSound();
                        // 单聊消息
                        YWConversation conversation = conversationService.getConversationByConversationId(contact.getUserId());
//                        YWConversation conversation = conversationService.getConversationByUserId(contact.getUserId());
                        if (conversation != null) {
                            Intent intent = new Intent("lobster_newMsg");
                            intent.putExtra("unreadCount", conversation.getUnreadCount());
                            intent.putExtra("ywMessage", ywMessage);
                            MineApp.getInstance().sendBroadcast(intent);
                        }
                    }
//                    else if (data.has("roster")) { // 添加好友
//                        MineApplication.sContext.sendBroadcast(new Intent("updateFriendList"));
//                        Intent intent = new Intent("updateFriendStatus");
//                        intent.putExtra("ywMessage", ywMessage);
//                        MineApplication.sContext.sendBroadcast(intent);
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onPushMessage(YWTribe ywTribe, YWMessage ywMessage) {

        }
    };

//    private IYWP2PPushListener mP2PListener = new IYWP2PPushListener() {
//        @Override
//        public void onPushMessage(IYWContact contact, List<YWMessage> messages) {
//            for (YWMessage ywMessage : messages) {
//                CustomMessageBody body = (CustomMessageBody) unpack(ywMessage.getContent());
//                if (Long.parseLong(ywMessage.getAuthorUserId()) == BaseActivity.userId) {
//                    if (body.getMsgType() == 12 || body.getMsgType() == 102 || body.getMsgType() == 112)
//                        return;
//                }
//                if (isBackground()) {
//                    sendMsg(ywMessage);
//                } else {
//                    Log.i("addPushListener", this + "");
//                    try {
//                        JSONObject data = new JSONObject(
//                                new JSONObject(ywMessage.getContent()).getString("customize"));
//                        if (data.has("message")) { // 聊天信息
//                            appSound();
//                            // 单聊消息
//                            YWConversation conversation = conversationService.getConversationByConversationId(ywMessage.getAuthorUserId());
////                            YWConversation conversation = conversationService.getConversationByUserId(contact.getUserId());
//                            if (conversation != null) {
//                                Intent intent = new Intent("lobster_newMsg");
//                                intent.putExtra("unReadCount", conversation.getUnreadCount());
//                                intent.putExtra("ywMessage", ywMessage);
//                                MineApp.getInstance().sendBroadcast(intent);
//                            }
//                        } else if (data.has("roster")) { // 添加好友
//                            MineApp.getInstance().sendBroadcast(new Intent("lobster_updateFriendList"));
//                            Intent intent = new Intent("lobster_updateFriendStatus");
//                            intent.putExtra("ywMessage", ywMessage);
//                            MineApp.getInstance().sendBroadcast(intent);
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    };

    /**
     * 登出
     */
    public void loginOut_Sample() {
        if (imCore == null)
            return;
        // openIM SDK提供的登录服务
        IYWLoginService mLoginService = imCore.getLoginService();
        mLoginService.logout(new IWxCallback() {

            @Override
            public void onSuccess(Object... arg0) {

            }

            @Override
            public void onProgress(int arg0) {

            }

            @Override
            public void onError(int arg0, String arg1) {

            }
        });
    }

    private static YWMessage mywMessage;

    private void sendMsg(YWMessage ywMessage) {
        mywMessage = ywMessage;
        NotificationManager notificationManager = (NotificationManager) activity
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // 通知内容
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(activity);
        mBuilder.setContentTitle("虾菇通知").setContentText(ywMessage.getContent()).setTicker("您有未读消息")
                // 通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())
                // 通知产生的时间，会在通知信息里显示
                .setPriority(Notification.PRIORITY_DEFAULT)
                // 设置该通知优先级
                .setAutoCancel(true)
                // 设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)
                // ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_ALL)
                // 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                .setSmallIcon(R.mipmap.ic_launcher);


        MineApp.getInstance().registerReceiver(new NotificationReceiver(), new IntentFilter("lobster_send"));

        PendingIntent contentIntent = PendingIntent.getActivity(activity, 1, new Intent("lobster_send"), PendingIntent.FLAG_UPDATE_CURRENT);
        // 指定内容意图
        mBuilder.setContentIntent(contentIntent);
        notificationManager.notify(1, mBuilder.build());
    }

    static class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ActivityUtils.getChatActivity(Long.parseLong(mywMessage.getAuthorUserId()));
        }
    }

    private boolean isBackground() {
        ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(activity.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    Log.i("后台", appProcess.processName);
                    return true;
                } else {
                    Log.i("前台", appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 解析消息
     *
     * @param content
     * @return
     */
    public static YWMessageBody unpack(String content) {
        CustomMessageBody body = new CustomMessageBody();
        // 自定义消息的实现可以使用jsonObject实现，或者也可以根据自定义其他格式，只需要个pack中一致即可
        JSONObject object;
        try {
            object = new JSONObject(content);
            body.setSummary(object.getJSONObject("header").getString("summary"));

            String dataStr = object.getString("customize");
            JSONObject data = new JSONObject(dataStr);
            if (data.has("message")) {
                JSONObject message = data.getJSONObject("message");
                body.setFromId(message.getLong("fromId"));
                body.setToId(message.getLong("toId"));
                JSONObject valueBean = message.getJSONObject("valueBean");
                if (valueBean.has("msgType"))
                    body.setMsgType(valueBean.getInt("msgType"));
                if (valueBean.has("stanza"))
                    body.setStanza(valueBean.getString("stanza"));
                if (valueBean.has("resLink"))
                    body.setResLink(valueBean.getString("resLink"));
                if (valueBean.has("resTime"))
                    body.setResTime(valueBean.getInt("resTime"));
                if (valueBean.has("driftBottleId"))
                    body.setDriftBottleId(valueBean.getLong("driftBottleId"));
                if (valueBean.has("msgChannelType"))
                    body.setMsgChannelType(valueBean.getInt("msgChannelType"));
            } else if (data.has("rosterApply")) {
                JSONObject rosterApply = data.getJSONObject("rosterApply");
                body.setFromId(rosterApply.getLong("fromId"));
                body.setToId(rosterApply.getLong("toId"));
                JSONObject valueBean = rosterApply.getJSONObject("valueBean");
                body.setStatus(valueBean.getInt("status"));
                body.setAskMark(valueBean.getString("askMark"));
            } else if (data.has("roster")) {
                JSONObject roster = data.getJSONObject("roster");
                body.setFromId(roster.getLong("fromId"));
                body.setToId(roster.getLong("toId"));
                JSONObject valueBean = roster.getJSONObject("valueBean");
                body.setStatus(valueBean.getInt("status"));
                body.setAskMark(valueBean.getString("askMark"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return body;
    }

    /**
     * 封装消息
     *
     * @param body
     * @return
     */
    public static String pack(YWMessageBody body) {
        // 自定义消息的实现可以使用jsonObject实现，或者也可以根据自定义其他格式，只需要个unpack中一致即可
        JSONObject data = new JSONObject();
        try {
            JSONObject message = new JSONObject();
            JSONObject valueBean = new JSONObject();
            valueBean.put("msgType", ((CustomMessageBody) body).getMsgType());
            valueBean.put("stanza", ((CustomMessageBody) body).getStanza());
            valueBean.put("resLink", ((CustomMessageBody) body).getResLink());
            valueBean.put("resTime", ((CustomMessageBody) body).getResTime());
            valueBean.put("driftBottleId", ((CustomMessageBody) body).getDriftBottleId());
            valueBean.put("msgChannelType", ((CustomMessageBody) body).getMsgChannelType());
            message.put("fromId", ((CustomMessageBody) body).getFromId());
            message.put("toId", ((CustomMessageBody) body).getToId());
            message.put("valueBean", valueBean);
            data.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data.toString();
    }

    public void appSound() {
        // 播放声音
        MediaPlayer mPlayer = MediaPlayer.create(activity, R.raw.msn);
        try {
            if (mPlayer != null) {
                mPlayer.stop();
            }
            mPlayer.prepare();
            mPlayer.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
