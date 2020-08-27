package com.yimi.rentme.activity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.SystemClock;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.yimi.rentme.BR;
import com.yimi.rentme.R;
import com.yimi.rentme.vm.MainViewModel;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.JobInfoDb;
import com.zb.lib_base.db.TagDb;
import com.zb.lib_base.model.JobInfo;
import com.zb.lib_base.model.MineInfo;
import com.zb.lib_base.model.RecommendInfo;
import com.zb.lib_base.model.Tag;
import com.zb.lib_base.utils.DateUtil;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.SimulateNetAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import io.realm.Realm;

@Route(path = RouteUtils.Main_MainActivity)
public class MainActivity extends AppBaseActivity {
    private TagDb tagDb;
    private JobInfoDb jobInfoDb;
    private MainViewModel viewModel;

    @Override
    public int getRes() {
        return R.layout.ac_main;
    }

    @Override
    public void initUI() {
        tagDb = new TagDb(Realm.getDefaultInstance());
        jobInfoDb = new JobInfoDb(Realm.getDefaultInstance());
        viewModel = new MainViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        if (tagDb.get().size() == 0) {
            String data = SimulateNetAPI.getOriginalFundData(activity, "tag.json");
            try {
                JSONArray array = new JSONArray(data);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.optJSONObject(i);
                    Tag tag = new Tag();
                    tag.setName(object.optString("name"));
                    tag.setTags(object.optString("tags"));
                    tagDb.saveTag(tag);
                }
            } catch (Exception e) {
            }
        }
        if (jobInfoDb.getJobList("").size() == 0) {
            String data = SimulateNetAPI.getOriginalFundData(activity, "job.json");
            try {
                JSONArray array = new JSONArray(data);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.optJSONObject(i);
                    JSONArray jobArray = object.optJSONArray("job");
                    assert jobArray != null;
                    for (int j = 0; j < jobArray.length(); j++) {
                        JSONObject jobObject = jobArray.optJSONObject(j);
                        JobInfo jobInfo = new JobInfo();
                        jobInfo.setJobTitle(object.optString("jobTitle"));
                        jobInfo.setJob(jobObject.optString("name"));
                        jobInfoDb.saveJobInfo(jobInfo);
                    }
                }
            } catch (Exception e) {
            }
        }
        setNotificationChannel();
        mineInfo = viewModel.mineInfoDb.getMineInfo();
        sender = PendingIntent.getBroadcast(activity, 0, new Intent(activity, alarmReceiver.class), 0);
        sender1 = PendingIntent.getBroadcast(activity, 0, new Intent(activity, alarmReceiver1.class), 0);
        sender2 = PendingIntent.getBroadcast(activity, 0, new Intent(activity, alarmReceiver2.class), 0);
        am = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        am1 = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        am2 = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
    }

    private static int id;
    private PendingIntent sender;
    private PendingIntent sender1;
    private PendingIntent sender2;
    private AlarmManager am;
    private AlarmManager am1;
    private AlarmManager am2;
    private static Random ra = new Random();
    private static MineInfo mineInfo = null;
    private static Map<Integer, String> noticeMap = new HashMap<>();

    //注意：receiver记得在manifest.xml注册
    public static class alarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            setNotice(context, 0);
        }
    }

    //注意：receiver记得在manifest.xml注册
    public static class alarmReceiver1 extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            setNotice(context, 1);
        }
    }

    //注意：receiver记得在manifest.xml注册
    public static class alarmReceiver2 extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            setNotice(context, 2);
        }
    }

    private static void setNotice(Context context, int type) {
        PreferenceUtil.saveIntValue(context, BaseActivity.userId + "_notice_" + type + "_" + DateUtil.getNow(DateUtil.yyyy_MM_dd), 1);
        noticeMap.put(100, "我不计较你的过去#但我想知道你的历史～");
        noticeMap.put(101, "给我一首歌的时间#我想让你看到一整年的快乐");
        noticeMap.put(102, "你知道嘛#有人喜欢你很久了");
        noticeMap.put(103, "你知道孤独是什么感觉吗#手机亮了却只有推送");
        noticeMap.put(104, "你和" + (mineInfo == null ? "Ta" : (mineInfo.getSex() == 0 ? "他" : "她")) + "们擦肩而过#快来看看是谁");
        noticeMap.put(105, "虾菇#亲，你不在的这段时间里，有人多次访问了你的资料");
        noticeMap.put(106, "甜甜的恋爱是真的#不信？你滑我试试？");
        noticeMap.put(107, "聊天选我#超会聊天，还秒回");
        noticeMap.put(108, "虾菇#▇▇▇▇▇▇←刮开看看有谁喜欢你了！");
        noticeMap.put(109, "就爱难舍，新欢难得，我觉得我可以做你的新欢+旧爱#别说话，打开我");
        noticeMap.put(110, "虾菇#你知道孤独是什么感觉吗，手机亮了却只有推送");
        noticeMap.put(111, "虾菇#你未必出类拔萃，但你一定与众不同");
        noticeMap.put(112, "虾菇#摸着你的良心说话，还不来回复，是不是去打游戏了");
        noticeMap.put(113, "宝贝～总有人在角落偷偷爱你#比如：我");
        noticeMap.put(114, "来自同城的{name}访问了你#你们都在同一个城市哦，周末约出来玩玩~");
        noticeMap.put(115, "滴～滴~，喜欢的人已送到#请进入右滑模式");
        noticeMap.put(116, "有人在角落里偷偷看了你！#快来找到" + (mineInfo == null ? "Ta" : (mineInfo.getSex() == 0 ? "他" : "她")));
        noticeMap.put(117, (mineInfo == null ? "Ta" : (mineInfo.getSex() == 0 ? "他" : "她")) + "反复查看了你的动态#快来右滑");

        id = ra.nextInt(18) + 100;
        String[] temp = noticeMap.get(id).split("#");
        RecommendInfo recommendInfo = null;
        if (MineApp.recommendInfoList.size() > 0 && id > 113) {
            recommendInfo = MineApp.recommendInfoList.get(0);
        }
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationManagerCompat nmc = NotificationManagerCompat.from(context);
        NotificationCompat.Builder builder = BaseActivity.getNotificationBuilderByChannel(notificationManager, MineApp.NOTIFICATION_CHANNEL_ID);
        // 通知内容
        builder.setContentText(temp[1]);
        builder.setContentTitle(temp[0].replace("{name}", recommendInfo == null ? (mineInfo == null ? "Ta" : (mineInfo.getSex() == 0 ? "他" : "她")) : recommendInfo.getUserNick()));
        builder.setOngoing(false);
        builder.setAutoCancel(true);// 设置这个标志当用户单击面板就可以让通知将自动取消
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setSmallIcon(R.mipmap.ic_launcher); // 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
        try {
            if (recommendInfo != null)
                builder.setLargeIcon(Glide.with(activity).asBitmap().load(recommendInfo.getSingleImage()).into(380, 380).get());
        } catch (Exception e) {

        }

        Intent intentMain = new Intent(Intent.ACTION_MAIN);
        intentMain.addCategory(Intent.CATEGORY_LAUNCHER);
        intentMain.setClass(context, LoadingActivity.class);
        intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        PendingIntent contextIntent = PendingIntent.getActivity(context, 0, intentMain, 0);
        builder.setContentIntent(contextIntent);
        nmc.notify(null, id, builder.build());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
        viewModel.stopAnimator();
    }

    /**
     * 适配8.0通知栏
     */
    private void setNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = MineApp.NOTIFICATION_CHANNEL_ID;
            String channelName = "虾菇消息通知";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationChannel.enableLights(true); //是否在桌面icon右上角展示小红点
            notificationChannel.setLightColor(Color.RED); //小红点颜色
            notificationChannel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    // 监听程序退出
    private long exitTime = 0;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                SCToastUtil.showToast(activity, "再按一次退出程序", true);
                exitTime = System.currentTimeMillis();
            } else {
                if (PreferenceUtil.readIntValue(activity, BaseActivity.userId + "_notice_" + 0 + "_" + DateUtil.getNow(DateUtil.yyyy_MM_dd)) == 0)
                    am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 30 * 60 * 1000, sender);
                if (PreferenceUtil.readIntValue(activity, BaseActivity.userId + "_notice_" + 1 + "_" + DateUtil.getNow(DateUtil.yyyy_MM_dd)) == 0)
                    am1.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 150 * 60 * 1000, sender1);
                if (PreferenceUtil.readIntValue(activity, BaseActivity.userId + "_notice_" + 2 + "_" + DateUtil.getNow(DateUtil.yyyy_MM_dd)) == 0)
                    am2.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 330 * 60 * 1000, sender2);
                System.exit(0);
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        am.cancel(sender);
        am1.cancel(sender1);
        am2.cancel(sender2);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (PreferenceUtil.readIntValue(activity, BaseActivity.userId + "_notice_" + 0 + "_" + DateUtil.getNow(DateUtil.yyyy_MM_dd)) == 0)
            am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 30 * 60 * 1000, sender);
        if (PreferenceUtil.readIntValue(activity, BaseActivity.userId + "_notice_" + 1 + "_" + DateUtil.getNow(DateUtil.yyyy_MM_dd)) == 0)
            am1.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 150 * 60 * 1000, sender1);
        if (PreferenceUtil.readIntValue(activity, BaseActivity.userId + "_notice_" + 2 + "_" + DateUtil.getNow(DateUtil.yyyy_MM_dd)) == 0)
            am2.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 330 * 60 * 1000, sender2);
    }

}
