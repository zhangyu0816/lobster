package com.yimi.rentme.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.yimi.rentme.R;
import com.zb.lib_base.app.MineApp;

import androidx.core.app.NotificationCompat;

public class ForegroundLiveService extends Service {

    public static final int NOTIFICATION_ID = 0x11;

    public ForegroundLiveService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //API 18以下，直接发送Notification并将其置为前台
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            startForeground(NOTIFICATION_ID, new Notification());
        } else {
            //API 18以上，发送Notification并将其置为前台后，启动InnerService
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MineApp.NOTIFICATION_CHANNEL_ID);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setTicker(getString(R.string.app_name));
            builder.setContentTitle(getString(R.string.app_name_sub));
            builder.setContentText(getString(R.string.app_name_running));
            builder.setWhen(System.currentTimeMillis()); //发送时间
            startForeground(NOTIFICATION_ID, builder.build());
            startService(new Intent(this, InnerService.class));
        }
    }


    public static class InnerService extends Service {
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            Log.e("InnerService", "Service");
            //发送与KeepLiveService中ID相同的Notification，然后将其取消并取消自己的前台显示
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MineApp.NOTIFICATION_CHANNEL_ID);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setTicker(getString(R.string.app_name));
            builder.setContentTitle(getString(R.string.app_name_sub));
            builder.setContentText(getString(R.string.app_name_running));
            builder.setWhen(System.currentTimeMillis()); //发送时间
            startForeground(NOTIFICATION_ID, builder.build());
            MineApp.getApp().getFixedThreadPool().execute(() -> {
                SystemClock.sleep(100);
                stopForeground(true);
                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                manager.cancel(NOTIFICATION_ID);
                stopSelf();
            });
        }
    }
}