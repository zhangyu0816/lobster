package com.yimi.rentme.getui;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.yimi.rentme.R;
import com.yimi.rentme.activity.MainActivity;
import com.yimi.rentme.activity.NotifivationActivity;
import com.zb.lib_base.utils.PreferenceUtil;

import org.json.JSONObject;

import java.util.List;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class DemoIntentService extends GTIntentService {
    public static StringBuilder payloadData = new StringBuilder();

    public DemoIntentService() {

    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        byte[] payload = msg.getPayload();
        if (payload != null) {
            String data = new String(payload);

            Log.d("GetuiSdkDemo", "receiver payload : " + data);

            payloadData.append(data);
            payloadData.append("\n");
            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationManagerCompat nmc = NotificationManagerCompat.from(context);
            NotificationCompat.Builder builder = getNotificationBuilderByChannel(notificationManager, "com.yimi.mdcm");
            JSONObject object;
            try {
                object = new JSONObject(data);
                JSONObject customContent = object.optJSONObject("custom_content");
                int openType = object.optInt("open_type");
                String description = object.optString("description");
                String title = object.optString("title");
                int notificationBasicStyle = object.optInt("notification_basic_style");
                // 通知内容
                builder.setContentText(description);
                builder.setContentTitle(title);
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
                    intents[0] = Intent.makeRestartActivityTask(new ComponentName(context, MainActivity.class));
                    intents[1] = new Intent(Intent.ACTION_VIEW, Uri.parse(object.optString("url")));
                    PendingIntent contentIntent = PendingIntent.getActivities(context, 1, intents,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    // 指定内容意图
                    builder.setContentIntent(contentIntent);
                    nmc.notify(null, 1, builder.build());
                    appSound(context);
                } else if (customContent == null) {
                    Intent intentMain = new Intent(Intent.ACTION_MAIN);
                    intentMain.addCategory(Intent.CATEGORY_LAUNCHER);
                    intentMain.setClass(context, MainActivity.class);
                    intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    PendingIntent contextIntent = PendingIntent.getActivity(context, 0, intentMain, 0);
                    builder.setContentIntent(contextIntent);
                    nmc.notify(null, 2, builder.build());
                    if (notificationBasicStyle == 7) {
                        context.sendBroadcast(new Intent("lobster_updateChat"));
                        context.sendBroadcast(new Intent("lobster_newDynMsgAllNum"));
                    }
                    appSound(context);
                } else {
                    long userId = object.optJSONObject("custom_content").optLong("userId");
                    JSONObject activityContent = object.optJSONObject("custom_content").optJSONObject("Activity");
                    if (userId != PreferenceUtil.readLongValue(context, "userId")) {
                        return;
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
                        appSound(context);
                    }
                    nmc.notify(null, 3, builder.build());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private void appSound(Context context) {
        // 播放声音
        MediaPlayer mPlayer = MediaPlayer.create(context, R.raw.msn);
        try {
            if (mPlayer != null) {
                mPlayer.stop();
            }
            mPlayer.prepare();
            mPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private NotificationCompat.Builder getNotificationBuilderByChannel(NotificationManager notificationManager, String channelId) {
        NotificationCompat.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();

            builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
            NotificationChannel channel = new NotificationChannel(channelId,
                    "您有未读消息", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//8.0以下 && 7.0及以上 设置优先级
                builder.setPriority(NotificationManager.IMPORTANCE_HIGH);
            } else {
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            }
        }
        return builder;
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
