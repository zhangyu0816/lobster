package com.yimi.rentme.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;
import android.view.KeyEvent;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.igexin.sdk.PushManager;
import com.yimi.rentme.BR;
import com.yimi.rentme.R;
import com.yimi.rentme.getui.DemoIntentService;
import com.yimi.rentme.utils.AlarmUtils;
import com.yimi.rentme.vm.MainViewModel;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.imcore.ImUtils;
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
        PushManager.getInstance().initialize(this.getApplicationContext(), DemoPushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), DemoIntentService.class);

        viewModel = new MainViewModel();
        viewModel.setBinding(mBinding);
        mBinding.setVariable(BR.viewModel, viewModel);
        setNotificationChannel();

        alarmUtils = new AlarmUtils(activity, MineApp.mineInfo);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MineApp.exit();
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
                DataCleanManager.deleteFile(new File(activity.getCacheDir(), "videos"));
                DataCleanManager.deleteFile(new File(activity.getCacheDir(), "images"));
                alarmUtils.startAlarm();
                ImUtils.getInstance(activity).loginOutIM();
                MineApp.exit();
                System.exit(0);
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        alarmUtils.cancelAlarm();
        ImUtils.getInstance(activity).setChat(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        DataCleanManager.deleteFile(new File(activity.getCacheDir(), "videos"));
        DataCleanManager.deleteFile(new File(activity.getCacheDir(), "images"));
        alarmUtils.startAlarm();
        ImUtils.getInstance(activity).loginOutIM();
    }
}
