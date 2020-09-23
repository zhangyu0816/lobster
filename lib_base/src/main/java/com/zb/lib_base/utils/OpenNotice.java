package com.zb.lib_base.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.windows.TextPW;

import androidx.core.app.NotificationManagerCompat;

import static android.provider.Settings.EXTRA_APP_PACKAGE;
import static android.provider.Settings.EXTRA_CHANNEL_ID;

public class OpenNotice {

    public OpenNotice(RxAppCompatActivity activity, View view) {
        if (PreferenceUtil.readIntValue(activity, "isNotificationEnabled") == 0) {
            if (!isNotificationEnabled(activity)) {
                new Handler().postDelayed(() -> {
                    PreferenceUtil.saveIntValue(activity, "isNotificationEnabled", 1);
                    new TextPW(view, "应用通知", "为了及时收到虾菇通知，请开启通知", "去开启", () -> gotoSet(activity));
                }, 1000);
            }
        }
    }

    public static boolean isNotificationEnabled(RxAppCompatActivity activity) {
        boolean isOpened = false;
        try {
            isOpened = NotificationManagerCompat.from(activity).areNotificationsEnabled();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isOpened;
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
