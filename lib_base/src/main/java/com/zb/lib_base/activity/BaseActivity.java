package com.zb.lib_base.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.umeng.analytics.MobclickAgent;
import com.zb.lib_base.R;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.databinding.PwsPerformBinding;
import com.zb.lib_base.utils.PreferenceUtil;

import java.io.File;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

public abstract class BaseActivity extends RxAppCompatActivity {

    public ViewDataBinding mBinding;
    public static RxAppCompatActivity activity;
    public static long userId = 0;
    public static String sessionId = "";

    public static long systemUserId = 10000;
    public static long dynUserId = 10002;
    public static long likeUserId = 1001;
    public static long bottleUserId = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MineApp.addActivity(this);
        super.onCreate(savedInstanceState);
        activity = this;
        mBinding = DataBindingUtil.setContentView(this, getRes());

        ARouter.getInstance().inject(this);
        update();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initUI();
    }

    public abstract int getRes();

    public abstract void initUI();

    public static void update() {
        userId = PreferenceUtil.readLongValue(activity, "userId");
        sessionId = PreferenceUtil.readStringValue(activity, "sessionId");
    }

    @Override
    protected void onDestroy() {
        if (mBinding != null)
            mBinding.unbind();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(activity);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(activity);
    }

    /**
     * 图片地址
     *
     * @return
     */
    public static File getImageFile() {
        File imagePath = new File(activity.getCacheDir(), "images");
        if (!imagePath.exists()) {
            imagePath.mkdirs();
        }
        File newFile = new File(imagePath, randomString(15) + ".jpg");
        return newFile;
    }

    /**
     * 视频文件
     *
     * @return
     */
    public static File getVideoFile() {
        File videoPath = new File(activity.getCacheDir(), "videos");
        if (!videoPath.exists()) {
            videoPath.mkdirs();
        }
        File newFile = new File(videoPath, randomString(15) + ".mp4");
        return newFile;
    }

    public static void createFfmpegFile() {
        File videoPath = new File(activity.getFilesDir(), "ffmpeg");
        if (!videoPath.exists()) {
            videoPath.mkdirs();
        }
    }

    /**
     * 语音文件
     *
     * @return
     */
    public static File getAudioFile() {
        File audioPath = new File(activity.getCacheDir(), "audios");
        if (!audioPath.exists()) {
            audioPath.mkdirs();
        }
        File newFile = new File(audioPath, randomString(15) + ".amr");
        return newFile;
    }

    /**
     * 下载文件
     *
     * @return
     */
    public static File getDownloadFile(String fileType) {
        File downPath = new File(activity.getCacheDir(), "downFiles");
        if (!downPath.exists()) {
            downPath.mkdirs();
        }
        File newFile = new File(downPath, randomString(15) + fileType);
        return newFile;
    }

    /**
     * 随机选取资源名称
     *
     * @param length
     * @return
     */
    public final static String randomString(int length) {
        Random randGen = null;
        char[] numbersAndLetters = null;
        if (length < 1) {
            return null;
        }
        if (randGen == null) {
            randGen = new Random();
            numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz"
                    + "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
        }
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
        }
        return new String(randBuffer);
    }

    public void fitComprehensiveScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(View.SYSTEM_UI_FLAG_FULLSCREEN);// 导致华为手机模糊
            getWindow().addFlags(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);// 导致华为手机黑屏
//            getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static NotificationCompat.Builder getNotificationBuilderByChannel(NotificationManager notificationManager, String channelId) {
        NotificationCompat.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();

            builder = new NotificationCompat.Builder(MineApp.getInstance(), channelId);
            NotificationChannel channel = new NotificationChannel(channelId,
                    "您有未读消息", NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true); //是否在桌面icon右上角展示小红点
            channel.setLightColor(Color.RED); //小红点颜色
            channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
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

    //**************** Android M Permission (Android 6.0权限控制代码封装)*****************************************************
    private int permissionRequestCode = 88;
    private PermissionCallback permissionRunnable;

    public interface PermissionCallback {
        void hasPermission();

        void noPermission();
    }

    /**
     * Android M运行时权限请求封装
     *
     * @param permissionDes 权限描述
     * @param runnable      请求权限回调
     * @param permissions   请求的权限（数组类型），直接从Manifest中读取相应的值，比如Manifest.permission.WRITE_CONTACTS
     */
    public void performCodeWithPermission(@NonNull String permissionDes, PermissionCallback
            runnable, @NonNull String... permissions) {
        if (permissions == null || permissions.length == 0) return;
        this.permissionRunnable = runnable;
        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || checkPermissionGranted(permissions)) {
            if (permissionRunnable != null) {
                permissionRunnable.hasPermission();
                permissionRunnable = null;
            }
        } else {
            //permission has not been granted.
            requestPermission(permissionDes, permissionRequestCode, permissions);
        }

    }

    private boolean checkPermissionGranted(String[] permissions) {
        boolean flag = true;
        for (String p : permissions) {
            if (ActivityCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    private boolean isClose = false;

    @SuppressLint("ClickableViewAccessibility")
    private void requestPermission(String permissionDes, final int requestCode,
                                   final String[] permissions) {
        if (shouldShowRequestPermissionRationale(permissions)) {
            //如果用户之前拒绝过此权限，再提示一次准备授权相关权限
            final AlertDialog build = new AlertDialog.Builder(this).create();
            //自定义布局
            PwsPerformBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.pws_perform, null, false);

            View view = binding.getRoot();
            build.setView(view, 0, 0, 0, 0);
            build.show();
            int width = getWindowManager().getDefaultDisplay().getWidth();
            WindowManager.LayoutParams params = build.getWindow().getAttributes();
            params.width = width - (width / 6);
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.CENTER;
            build.getWindow().setAttributes(params);

            binding.setContent(permissionDes);


            binding.tvSure.setOnClickListener(v -> {
                isClose = false;
                ActivityCompat.requestPermissions(BaseActivity.this, permissions, requestCode);
                build.dismiss();
            });
            binding.tvCancel.setOnClickListener(v -> {
                isClose = true;
                build.dismiss();
            });
            view.setOnTouchListener((v, event) -> {
                isClose = true;
                build.dismiss();
                return false;
            });
            build.setOnDismissListener(dialog -> {
                if (permissionRunnable != null) {
                    permissionRunnable.noPermission();
                    permissionRunnable = null;
                    isClose = false;
                }
            });
        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(BaseActivity.this, permissions, requestCode);
        }
    }

    private boolean shouldShowRequestPermissionRationale(String[] permissions) {
        boolean flag = false;
        for (String p : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, p)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == permissionRequestCode) {
            if (verifyPermissions(grantResults)) {
                if (permissionRunnable != null) {
                    permissionRunnable.hasPermission();
                    permissionRunnable = null;
                }
            } else {
                if (permissionRunnable != null) {
                    permissionRunnable.noPermission();
                    permissionRunnable = null;
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    public boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    //********************** END Android M Permission ****************************************
}
