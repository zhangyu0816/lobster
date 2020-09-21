package com.zb.lib_base.imcore;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

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
import com.alibaba.mobileim.gingko.model.tribe.YWTribe;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.R;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.NotifivationActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.model.LikeMe;
import com.zb.lib_base.utils.SCToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
    public void login_Sample(RxAppCompatActivity activity, String userId, String password, CallBack callBack) {
        this.activity = activity;
        initYWIMCore(userId);

        YWLoginParam loginParam = YWLoginParam.createLoginParam(userId, password);
        // openIM SDK提供的登录服务
        IYWLoginService mLoginService = imCore.getLoginService();
        mLoginService.login(loginParam, new IWxCallback() {
            @Override
            public void onSuccess(Object... objects) {
                Log.i("onSuccess", "阿里登录成功");
                callBack.registerSuccess();
            }

            @Override
            public void onError(int i, String s) {
                SCToastUtil.showToast(activity, "聊天异常：" + s + ",请联系客服解决", true);
            }

            @Override
            public void onProgress(int i) {

            }
        });
    }

    public interface CallBack {
        void registerSuccess();
    }

    /**
     * 添加新消息到达监听，该监听应该在登录之前调用以保证登录后可以及时收到消息
     */
    private void addPushMessageListener() {
        conversationService = imCore.getConversationService();
        //添加单聊消息监听，先删除再添加，以免多次添加该监听
        conversationService.removePushListener(iywPushListener);
        conversationService.addPushListener(iywPushListener);
    }

    private IYWPushListener iywPushListener = new IYWPushListener() {
        @Override
        public void onPushMessage(IYWContact contact, YWMessage ywMessage) {
            CustomMessageBody body = unpack(ywMessage.getContent());
            long otherUserId = body.getFromId();
            if (body.getDriftBottleId() == 0) {
                if (otherUserId != BaseActivity.dynUserId) {
                    boolean hasOtherUserId = false;
                    for (LikeMe item : MineApp.pairList) {
                        if (item.getOtherUserId() == otherUserId) {
                            hasOtherUserId = true;
                            break;
                        }
                    }
                    if (!hasOtherUserId) {
                        return;
                    }
                }
            }
            try {
                JSONObject data = new JSONObject(new JSONObject(ywMessage.getContent()).getString("customize"));
                if (data.has("message")) { // 聊天信息
                    if (body.getFromId() != BaseActivity.userId)
                        appSound();
                    // 单聊消息
                    YWConversation conversation = conversationService.getConversationByUserId(contact.getUserId());
                    if (conversation != null) {
                        Intent intent = new Intent("lobster_newMsg");
                        intent.putExtra("unreadCount", conversation.getUnreadCount());
                        intent.putExtra("customMessageBody", body);
                        intent.putExtra("msgId", ywMessage.getMsgId() + "");
                        MineApp.getInstance().sendBroadcast(intent);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (isBackground()) {
                sendMsg(ywMessage);
            }

        }

        @Override
        public void onPushMessage(YWTribe ywTribe, YWMessage ywMessage) {

        }
    };

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
                imCore = null;
            }

            @Override
            public void onProgress(int arg0) {

            }

            @Override
            public void onError(int arg0, String arg1) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getChannelId(NotificationManager notificationManager) {
        NotificationChannel channel = new NotificationChannel(MineApp.NOTIFICATION_CHANNEL_ID, "虾菇推送", NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);//显示桌面红点
        channel.setLightColor(Color.RED);
        channel.setShowBadge(true);
        notificationManager.createNotificationChannel(channel);
        return channel.getId();
    }

    private void sendMsg(YWMessage ywMessage) {
        CustomMessageBody body = unpack(ywMessage.getContent());
        long otherUserId = body.getFromId() == BaseActivity.userId ? body.getToId() : body.getFromId();

        NotificationManager notificationManager = (NotificationManager) activity
                .getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationManagerCompat nmc = NotificationManagerCompat.from(activity);
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(activity, getChannelId(notificationManager));
        } else {
            builder = new NotificationCompat.Builder(activity, null);
        }

        RemoteViews mRemoteViews = new RemoteViews("com.yimi.rentme", R.layout.remote_layout);
        mRemoteViews.setTextViewText(R.id.tv_content, body.getStanza());

        builder.setOngoing(false);
        builder.setAutoCancel(true);// 设置这个标志当用户单击面板就可以让通知将自动取消
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContent(mRemoteViews);

        Intent intentMain = new Intent(Intent.ACTION_MAIN);
        intentMain.addCategory(Intent.CATEGORY_LAUNCHER);
        intentMain.setClass(activity, NotifivationActivity.class);
        intentMain.putExtra("activityContent", "ChatActivity");
        intentMain.putExtra("otherUserId", otherUserId);
        intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        PendingIntent contextIntent = PendingIntent.getActivity(activity, 0, intentMain, 0);
        builder.setContentIntent(contextIntent);
        nmc.notify(null, (int) otherUserId, builder.build());
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
    private CustomMessageBody unpack(String content) {
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
    public String pack(CustomMessageBody body) {
        // 自定义消息的实现可以使用jsonObject实现，或者也可以根据自定义其他格式，只需要个unpack中一致即可
        JSONObject data = new JSONObject();
        try {
            JSONObject message = new JSONObject();
            JSONObject valueBean = new JSONObject();
            valueBean.put("msgType", body.getMsgType());
            valueBean.put("stanza", body.getStanza());
            valueBean.put("resLink", body.getResLink());
            valueBean.put("resTime", body.getResTime());
            if (body.getDriftBottleId() != 0)
                valueBean.put("driftBottleId", body.getDriftBottleId());
            if (body.getMsgChannelType() == 2)
                valueBean.put("msgChannelType", body.getMsgChannelType());
            message.put("fromId", body.getFromId());
            message.put("toId", body.getToId());
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
                mPlayer.prepare();
                mPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Handler().postDelayed(() -> {
            if (mPlayer != null) {
                mPlayer.stop();
                mPlayer.release();//释放资源
            }
        }, 500);
    }
}
