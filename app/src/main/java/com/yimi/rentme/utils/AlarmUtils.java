package com.yimi.rentme.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.SystemClock;
import android.view.View;
import android.widget.RemoteViews;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.yimi.rentme.R;
import com.yimi.rentme.activity.LoadingActivity;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.RecommendInfo;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.DownLoad;
import com.zb.lib_base.utils.PreferenceUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.content.Context.ALARM_SERVICE;

public class AlarmUtils {
    private RxAppCompatActivity activity;
    private static int id;
    private PendingIntent sender;
    private PendingIntent sender1;
    private PendingIntent sender2;
    private PendingIntent senderShort;
    private PendingIntent senderShort1;
    private PendingIntent senderShort2;
    private PendingIntent senderShort3;
    private PendingIntent senderShort4;
    private AlarmManager am;
    private AlarmManager am1;
    private AlarmManager am2;
    private AlarmManager amShort;
    private AlarmManager amShort1;
    private AlarmManager amShort2;
    private AlarmManager amShort3;
    private AlarmManager amShort4;
    private static Random ra = new Random();
    private static MineInfo mineInfo = null;
    private static Map<Integer, String> noticeMap = new HashMap<>();
    private static Map<Integer, String> noticeShortMap = new HashMap<>();

    public AlarmUtils(RxAppCompatActivity activity, MineInfo mineInfo) {
        AlarmUtils.mineInfo = mineInfo;
        this.activity = activity;
        Intent intent1 = new Intent(activity, alarmReceiver.class);
        Intent intent2 = new Intent(activity, alarmReceiver1.class);
        Intent intent3 = new Intent(activity, alarmReceiver2.class);

        sender = PendingIntent.getBroadcast(activity, 0, intent1, 0);
        sender1 = PendingIntent.getBroadcast(activity, 0, intent2, 0);
        sender2 = PendingIntent.getBroadcast(activity, 0, intent3, 0);

        Intent intent4 = new Intent(activity, alarmShortReceiver.class);
        Intent intent5 = new Intent(activity, alarmShortReceiver1.class);
        Intent intent6 = new Intent(activity, alarmShortReceiver2.class);
        Intent intent7 = new Intent(activity, alarmShortReceiver3.class);
        Intent intent8 = new Intent(activity, alarmShortReceiver4.class);

        senderShort = PendingIntent.getBroadcast(activity, 0, intent4, 0);
        senderShort1 = PendingIntent.getBroadcast(activity, 0, intent5, 0);
        senderShort2 = PendingIntent.getBroadcast(activity, 0, intent6, 0);
        senderShort3 = PendingIntent.getBroadcast(activity, 0, intent7, 0);
        senderShort4 = PendingIntent.getBroadcast(activity, 0, intent8, 0);

        am = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        am1 = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        am2 = (AlarmManager) activity.getSystemService(ALARM_SERVICE);

        amShort = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        amShort1 = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        amShort2 = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        amShort3 = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        amShort4 = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
    }

    public void startAlarm() {
        if (PreferenceUtil.readIntValue(activity, BaseActivity.userId + "_notice_" + 0 + "_" + DateUtil.getNow(DateUtil.yyyy_MM_dd)) == 0)
            am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 120 * 60 * 1000, sender);
        if (PreferenceUtil.readIntValue(activity, BaseActivity.userId + "_notice_" + 1 + "_" + DateUtil.getNow(DateUtil.yyyy_MM_dd)) == 0)
            am1.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 240 * 60 * 1000, sender1);
        if (PreferenceUtil.readIntValue(activity, BaseActivity.userId + "_notice_" + 2 + "_" + DateUtil.getNow(DateUtil.yyyy_MM_dd)) == 0)
            am2.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 300 * 60 * 1000, sender2);

        if (PreferenceUtil.readIntValue(activity, BaseActivity.userId + "_noticeShort_" + 0 + "_" + DateUtil.getNow(DateUtil.yyyy_MM_dd)) == 0)
            amShort.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 60 * 1000, senderShort);
        if (PreferenceUtil.readIntValue(activity, BaseActivity.userId + "_noticeShort_" + 1 + "_" + DateUtil.getNow(DateUtil.yyyy_MM_dd)) == 0)
            amShort1.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 3 * 60 * 1000, senderShort1);
        if (PreferenceUtil.readIntValue(activity, BaseActivity.userId + "_noticeShort_" + 2 + "_" + DateUtil.getNow(DateUtil.yyyy_MM_dd)) == 0)
            amShort2.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 5 * 60 * 1000, senderShort2);
        if (PreferenceUtil.readIntValue(activity, BaseActivity.userId + "_noticeShort_" + 3 + "_" + DateUtil.getNow(DateUtil.yyyy_MM_dd)) == 0)
            amShort3.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 7 * 60 * 1000, senderShort3);
        if (PreferenceUtil.readIntValue(activity, BaseActivity.userId + "_noticeShort_" + 4 + "_" + DateUtil.getNow(DateUtil.yyyy_MM_dd)) == 0)
            amShort4.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 9 * 60 * 1000, senderShort4);
    }

    public void cancelAlarm() {
        am.cancel(sender);
        am1.cancel(sender1);
        am2.cancel(sender2);

        amShort.cancel(senderShort);
        amShort1.cancel(senderShort1);
        amShort2.cancel(senderShort2);
        amShort3.cancel(senderShort3);
        amShort4.cancel(senderShort4);
    }

    //?????????receiver?????????manifest.xml??????
    public static class alarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            setNotice(context, 0);
        }
    }

    //?????????receiver?????????manifest.xml??????
    public static class alarmReceiver1 extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            setNotice(context, 1);
        }
    }

    //?????????receiver?????????manifest.xml??????
    public static class alarmReceiver2 extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            setNotice(context, 2);
        }
    }

    //?????????receiver?????????manifest.xml??????
    public static class alarmShortReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            setNoticeShort(context, 0);
        }
    }

    //?????????receiver?????????manifest.xml??????
    public static class alarmShortReceiver1 extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            setNoticeShort(context, 1);
        }
    }

    //?????????receiver?????????manifest.xml??????
    public static class alarmShortReceiver2 extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            setNoticeShort(context, 2);
        }
    }

    //?????????receiver?????????manifest.xml??????
    public static class alarmShortReceiver3 extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            setNoticeShort(context, 3);
        }
    }

    //?????????receiver?????????manifest.xml??????
    public static class alarmShortReceiver4 extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            setNoticeShort(context, 4);
        }
    }

    private static void setNotice(Context context, int type) {
        PreferenceUtil.saveIntValue(context, BaseActivity.userId + "_notice_" + type + "_" + DateUtil.getNow(DateUtil.yyyy_MM_dd), 1);
        noticeMap.put(100, "????????????????????????#??????????????????????????????");
        noticeMap.put(101, "????????????????????????#????????????????????????????????????");
        noticeMap.put(102, "????????????#????????????????????????");
        noticeMap.put(103, "?????????????????????????????????#???????????????????????????");
        noticeMap.put(104, "??????" + (mineInfo == null ? "TA" : (mineInfo.getSex() == 0 ? "???" : "???")) + "???????????????#??????????????????");
        noticeMap.put(105, "#?????????????????????????????????????????????????????????????????????");
        noticeMap.put(106, "????????????????????????#???????????????????????????");
        noticeMap.put(107, "????????????#????????????????????????");
        noticeMap.put(108, "#??????????????????????????????????????????????????????");
        noticeMap.put(109, "???????????????????????????????????????????????????????????????+??????#?????????????????????");
        noticeMap.put(110, "#???????????????????????????????????????????????????????????????");
        noticeMap.put(111, "#????????????????????????????????????????????????");
        noticeMap.put(112, "#?????????????????????????????????????????????????????????????????????");
        noticeMap.put(113, "???????????????????????????????????????#????????????");
        noticeMap.put(114, "???????????????<name>????????????#??????????????????????????????????????????????????????~");
        noticeMap.put(115, "?????????~????????????????????????#?????????????????????");
        noticeMap.put(116, "????????????????????????????????????#????????????" + (mineInfo == null ? "TA" : (mineInfo.getSex() == 0 ? "???" : "???")));
        noticeMap.put(117, (mineInfo == null ? "TA" : (mineInfo.getSex() == 0 ? "???" : "???")) + "???????????????????????????#????????????");

        id = ra.nextInt(18) + 100;
        String[] temp = Objects.requireNonNull(noticeMap.get(id)).split("#");
        RecommendInfo recommendInfo = null;
        if (MineApp.recommendInfoList.size() > 0 && id > 113) {
            recommendInfo = MineApp.recommendInfoList.get(new Random().nextInt(MineApp.recommendInfoList.size()));
        }
        setNoticeManager(context, temp[0].replace("name", recommendInfo == null ? (mineInfo == null ? "TA" : (mineInfo.getSex() == 0 ? "???" : "???")) : recommendInfo.getUserNick()), temp[1], recommendInfo);
    }

    private static void setNoticeShort(Context context, int type) {
        PreferenceUtil.saveIntValue(context, BaseActivity.userId + "_noticeShort_" + type + "_" + DateUtil.getNow(DateUtil.yyyy_MM_dd), 1);
        noticeShortMap.put(118, "#<name>???????????????");
        noticeShortMap.put(119, "#<name>???????????????");
        noticeShortMap.put(120, "????????????????????????????????????#???????????????");
        noticeShortMap.put(121, (mineInfo == null ? "TA" : (mineInfo.getSex() == 0 ? "???" : "???")) + "???????????????????????????#????????????");

        id = ra.nextInt(4) + 118;
        String[] temp = Objects.requireNonNull(noticeShortMap.get(id)).split("#");
        RecommendInfo recommendInfo = null;
        if (MineApp.recommendInfoList.size() > 0 && id < 120) {
            recommendInfo = MineApp.recommendInfoList.get(new Random().nextInt(MineApp.recommendInfoList.size()));
        }

        setNoticeManager(context, temp[0], temp[1].replace("name", recommendInfo == null ? (mineInfo == null ? "TA" : (mineInfo.getSex() == 0 ? "???" : "???")) : recommendInfo.getUserNick()), recommendInfo);
    }

    private static void setNoticeManager(Context context, String title, String content, RecommendInfo recommendInfo) {

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationManagerCompat nmc = NotificationManagerCompat.from(context);
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(context, getChannelId(notificationManager));
        } else {
            builder = new NotificationCompat.Builder(context, MineApp.NOTIFICATION_CHANNEL_ID);
        }

        RemoteViews mRemoteViews = new RemoteViews("com.yimi.rentme", R.layout.remoteview_layout);
        mRemoteViews.setTextViewText(R.id.tv_title, title);
        mRemoteViews.setViewVisibility(R.id.tv_title, title.isEmpty() ? View.GONE : View.VISIBLE);
        mRemoteViews.setTextViewText(R.id.tv_content, content);

        builder.setOngoing(false);
        builder.setAutoCancel(true);// ????????????????????????????????????????????????????????????????????????
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        try {
            if (recommendInfo != null) {
                DownLoad.downImageFile(recommendInfo.getSingleImage().replace("YM0000", "240X240"), new DownLoad.CallBack() {
                    @Override
                    public void success(String filePath, Bitmap bitmap) {
                        if (bitmap != null) {
                            mRemoteViews.setImageViewBitmap(R.id.iv_logo, bitmap);
                            mRemoteViews.setViewVisibility(R.id.iv_logo, View.VISIBLE);
                        }
                        builder(context, builder, mRemoteViews, nmc);
                    }

                    @Override
                    public void fail() {
                        builder(context, builder, mRemoteViews, nmc);
                    }
                });
            } else {
                builder(context, builder, mRemoteViews, nmc);
            }
        } catch (Exception e) {
            builder(context, builder, mRemoteViews, nmc);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getChannelId(NotificationManager notificationManager) {
        NotificationChannel channel = new NotificationChannel(MineApp.NOTIFICATION_CHANNEL_ID, "????????????", NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);//??????????????????
        channel.setLightColor(Color.RED);
        channel.setShowBadge(true);
        notificationManager.createNotificationChannel(channel);
        return channel.getId();
    }

    private static void builder(Context context, NotificationCompat.Builder builder, RemoteViews mRemoteViews, NotificationManagerCompat nmc) {
        builder.setContent(mRemoteViews);
        Intent intentMain = new Intent(Intent.ACTION_MAIN);
        intentMain.addCategory(Intent.CATEGORY_LAUNCHER);
        intentMain.setClass(context, LoadingActivity.class);
        intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        PendingIntent contextIntent = PendingIntent.getActivity(context, 0, intentMain, 0);
        builder.setContentIntent(contextIntent);
        nmc.notify(null, id, builder.build());
    }
}
