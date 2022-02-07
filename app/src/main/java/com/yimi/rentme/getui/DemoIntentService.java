package com.yimi.rentme.getui;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.yimi.rentme.R;
import com.yimi.rentme.activity.LoadingActivity;
import com.yimi.rentme.activity.MainActivity;
import com.zb.lib_base.activity.NotifivationActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.PreferenceUtil;

import org.json.JSONObject;

import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class DemoIntentService extends GTIntentService {

    public DemoIntentService() {

    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
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

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        byte[] payload = msg.getPayload();
        if (payload != null) {
            String data = new String(payload);

            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationManagerCompat nmc = NotificationManagerCompat.from(context);
            NotificationCompat.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder = new NotificationCompat.Builder(this, getChannelId(notificationManager));
            } else {
                builder = new NotificationCompat.Builder(this, MineApp.NOTIFICATION_CHANNEL_ID);
            }

            JSONObject object;
            try {
                object = new JSONObject(data);
                JSONObject customContent = object.optJSONObject("custom_content");
                int openType = object.optInt("open_type");
                String description = object.optString("description");
                String title = object.optString("title");
                int notificationBasicStyle = object.optInt("notification_basic_style");
                // 通知内容

                RemoteViews mRemoteViews = new RemoteViews("com.yimi.rentme", R.layout.remoteview_layout);
                mRemoteViews.setTextViewText(R.id.tv_title, title);
                mRemoteViews.setViewVisibility(R.id.tv_title, title.isEmpty() ? View.GONE : View.VISIBLE);
                mRemoteViews.setTextViewText(R.id.tv_content, description);

                builder.setContent(mRemoteViews);
                builder.setOngoing(false);
                builder.setAutoCancel(true);// 设置这个标志当用户单击面板就可以让通知将自动取消
                builder.setDefaults(Notification.DEFAULT_ALL);
                builder.setSmallIcon(R.mipmap.ic_launcher); // 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：

                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
                boolean isAppRunning = false;
                String MY_PKG_NAME = "com.yimi.rentme";
                for (ActivityManager.RunningTaskInfo info : list) {
                    if (info.topActivity.getPackageName().equals(MY_PKG_NAME)
                            || info.baseActivity.getPackageName().equals(MY_PKG_NAME)) {
                        isAppRunning = true;
                        break;
                    }
                }

                if (openType == 1) {
                    Intent[] intents = new Intent[2];
                    intents[0] = Intent.makeRestartActivityTask(new ComponentName(context, LoadingActivity.class));
                    intents[1] = new Intent(Intent.ACTION_VIEW, Uri.parse(object.optString("url")));
                    intents[1].addCategory(Intent.CATEGORY_BROWSABLE);
                    intents[1].setComponent(null);
                    intents[1].setSelector(null);
                    PendingIntent contentIntent = PendingIntent.getActivities(context, 1, intents,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    // 指定内容意图
                    builder.setContentIntent(contentIntent);
                    nmc.notify(null, 1, builder.build());
                } else if (customContent == null) {
                    Intent intentMain = new Intent(Intent.ACTION_MAIN);
                    intentMain.addCategory(Intent.CATEGORY_LAUNCHER);
                    intentMain.setClass(context, LoadingActivity.class);
                    intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    PendingIntent contextIntent = PendingIntent.getActivity(context, 0, intentMain, 0);
                    builder.setContentIntent(contextIntent);
                    nmc.notify(null, 2, builder.build());
                    if (notificationBasicStyle == 7) {
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("lobster_updateChat"));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("lobster_newDynMsgAllNum"));
                    }
                } else {
                    long userId = object.optJSONObject("custom_content").optLong("userId");
                    JSONObject activityContent = object.optJSONObject("custom_content").optJSONObject("Activity");
                    if (userId != PreferenceUtil.readLongValue(context, "userId")) {
                        return;
                    }
                    String activity = activityContent.optString("ActivityName");
                    if(TextUtils.equals(activity,"FilmResourceDetailActivity")){
                        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("lobster_addFilmMsgCount"));
                    }
                    if (isAppRunning) {
                        Log.i("isAppRunning", "isAppRunning == true");
                        Intent intent1 = new Intent(context, NotifivationActivity.class);
                        intent1.putExtra("activityContent", activityContent.toString());
                        PendingIntent contentIntent = PendingIntent.getActivity(context, 3, intent1,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        // 指定内容意图
                        builder.setContentIntent(contentIntent);
                    } else {
                        Log.i("isAppRunning", "isAppRunning == false");
                        Intent[] intents = new Intent[2];
                        intents[0] = Intent.makeRestartActivityTask(new ComponentName(context, MainActivity.class));
                        intents[1] = new Intent(context, NotifivationActivity.class);
                        intents[1].putExtra("activityContent", activityContent.toString());
                        PendingIntent contentIntent = PendingIntent.getActivities(context, 3, intents,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                        // 指定内容意图
                        builder.setContentIntent(contentIntent);
                    }
                    nmc.notify(null, 3, builder.build());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        PreferenceUtil.saveStringValue(context, "channelId", clientid);
        Log.i("channelId", clientid);
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
    }

    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage gtNotificationMessage) {

    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage gtNotificationMessage) {

    }
}
