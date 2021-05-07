package com.yimi.rentme.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.igexin.sdk.PushManager;
import com.xiaomi.mimc.logger.Logger;
import com.xiaomi.mimc.logger.MIMCLog;
import com.yimi.rentme.BR;
import com.yimi.rentme.R;
import com.yimi.rentme.getui.DemoIntentService;
import com.yimi.rentme.utils.AlarmUtils;
import com.yimi.rentme.vm.MainViewModel;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.iv.DemoPushService;
import com.zb.lib_base.utils.DataCleanManager;
import com.zb.lib_base.utils.RouteUtils;
import com.zb.lib_base.utils.SCToastUtil;

import java.io.File;

@Route(path = RouteUtils.Main_MainActivity)
public class MainActivity extends AppBaseActivity {
    private MainViewModel viewModel;
    private AlarmUtils alarmUtils;

    @Override
    public int getRes() {
        return R.layout.ac_main;
    }

    @Override
    public void initUI() {
        // 个推注册
        MineApp.getApp().getFixedThreadPool().execute(() -> {
            PushManager.getInstance().initialize(this.getApplicationContext(), DemoPushService.class);
            PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), DemoIntentService.class);
            setNotificationChannel();
        });

        fitComprehensiveScreen();
        viewModel = new MainViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        alarmUtils = new AlarmUtils(activity, MineApp.mineInfo);
        MIMCLog.setLogger(new Logger() {
            @Override
            public void d(String tag, String msg) {
                Log.d(tag, msg);
            }

            @Override
            public void d(String tag, String msg, Throwable th) {
                Log.d(tag, msg, th);
            }

            @Override
            public void i(String tag, String msg) {
                Log.i(tag, msg);
            }

            @Override
            public void i(String tag, String msg, Throwable th) {
                Log.i(tag, msg, th);
            }

            @Override
            public void w(String tag, String msg) {
                Log.w(tag, msg);
            }

            @Override
            public void w(String tag, String msg, Throwable th) {
                Log.w(tag, msg, th);
            }

            @Override
            public void e(String tag, String msg) {
                Log.e(tag, msg);
            }

            @Override
            public void e(String tag, String msg, Throwable th) {
                Log.e(tag, msg, th);
            }
        });
        MIMCLog.setLogPrintLevel(MIMCLog.DEBUG);
        MIMCLog.setLogSaveLevel(MIMCLog.DEBUG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MineApp.getApp().exit();
        if (viewModel != null) {
            viewModel.onDestroy();
            viewModel.stopAnimator();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (viewModel != null) {
            viewModel.onStart();
        }
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
                DataCleanManager.deleteFile(new File(activity.getCacheDir(), "videos"));
                DataCleanManager.deleteFile(new File(activity.getCacheDir(), "images"));
                alarmUtils.startAlarm();
                MineApp.sMIMCUser.logout();
                MineApp.sMIMCUser.destroy();
                MineApp.getApp().exit();
                System.exit(0);
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MineApp.getApp().getActivityList().get(0) instanceof MainActivity) {
            alarmUtils.cancelAlarm();
            if (viewModel != null)
                viewModel.onResume();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        DataCleanManager.deleteFile(new File(activity.getCacheDir(), "videos"));
        DataCleanManager.deleteFile(new File(activity.getCacheDir(), "images"));
        alarmUtils.startAlarm();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (viewModel != null)
            viewModel.onPause();
    }
}
