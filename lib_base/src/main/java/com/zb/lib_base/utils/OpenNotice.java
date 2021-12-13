package com.zb.lib_base.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.View;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.windows.TextPW;

import androidx.core.app.NotificationManagerCompat;

import static android.provider.Settings.EXTRA_APP_PACKAGE;
import static android.provider.Settings.EXTRA_CHANNEL_ID;

public class OpenNotice {

    public OpenNotice(RxAppCompatActivity activity, View view) {
        if (PreferenceUtil.readIntValue(activity, "isNotificationEnabled") == 1) {
            if (isNotNotification(activity)) {
                MineApp.getApp().getFixedThreadPool().execute(() -> {
                    SystemClock.sleep(1000);
                    activity.runOnUiThread(() -> {
                        PreferenceUtil.saveIntValue(activity, "isNotificationEnabled", 1);
                        new TextPW(activity, view, "应用通知",
                                "在您使用虾菇服务时，为了您能及时收到系统消息、聊天内容、关注、点赞等交互信息，虾菇集成了第三方推送服务（个推），此服务需要开通应用通知功能。" +
                                        "若您点击“同意”按钮，将跳转到应用通知管理页面，选择开启通知及通知显示方式。" +
                                        "若您点击“拒绝”按钮，我们将不再主动弹出该提示，您也无法收到通知信息，不影响使用其他的虾菇功能/服务。" +
                                        "您也可以通过“手机设置--应用--虾菇--通知”或app内“我的--设置--通知管理”，手动开启或关闭通知。",
                                "同意", false, true, () -> gotoSet(activity));
                    });
                });
            }
        }
    }

    public static boolean isNotNotification(RxAppCompatActivity activity) {
        boolean isOpened = false;
        try {
            isOpened = NotificationManagerCompat.from(activity).areNotificationsEnabled();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return !isOpened;
    }

    public static void gotoSet(RxAppCompatActivity activity) {

        if (Build.VERSION.SDK_INT >= 26) {
            // android 8.0引导
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(EXTRA_APP_PACKAGE, activity.getPackageName());
            intent.putExtra(EXTRA_CHANNEL_ID, activity.getApplicationInfo().uid);
            activity.startActivity(intent);
        } else if (Build.VERSION.SDK_INT >= 21) {
            // android 5.0-7.0
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra("app_package", activity.getPackageName());
            intent.putExtra("app_uid", activity.getApplicationInfo().uid);
            activity.startActivity(intent);
        } else {
            // 其他
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
            intent.setData(uri);
            activity.startActivity(intent);
        }
    }
}
