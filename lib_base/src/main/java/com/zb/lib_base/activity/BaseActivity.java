package com.zb.lib_base.activity;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.R;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.databinding.PwsPerformBinding;
import com.zb.lib_base.mimc.UserManager;
import com.zb.lib_base.utils.DebuggerUtils;
import com.zb.lib_base.utils.PreferenceUtil;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
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
        MineApp.getApp().addActivity(this);
        super.onCreate(savedInstanceState);
        if (!DebuggerUtils.isDebuggable(MineApp.sContext))
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        activity = this;
        mBinding = DataBindingUtil.setContentView(this, getRes());

        ARouter.getInstance().inject(this);
        update();
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if (MineApp.sMIMCUser != null && isAppOnForeground() && !MineApp.isLogin) {
            MineApp.sMIMCUser = UserManager.getInstance().newMIMCUser(MineApp.imUserId);
            MineApp.sMIMCUser.login();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isAppOnForeground()) {
            if (MineApp.sMIMCUser != null) {
                MineApp.sMIMCUser.logout();
                MineApp.sMIMCUser.destroy();
            }
        }
    }

    private boolean isAppOnForeground() {
        ActivityManager am = (ActivityManager) activity.getSystemService(ACTIVITY_SERVICE);
        String myPackageName = activity.getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
        if (appProcesses.isEmpty()) {
            return false;
        }

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            String processName = appProcess.processName;
            boolean isName = processName.equals(myPackageName);
            boolean isImportance = appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
            if (isName && isImportance) {
                return true;
            }
        }
        return false;
    }

    /**
     * 图片地址
     */
    public static File getImageFile() {
        File imagePath = new File(activity.getCacheDir(), "images");
        if (!imagePath.exists()) {
            imagePath.mkdirs();
        }
        return new File(imagePath, randomString(15) + ".jpg");
    }

    /**
     * 图片地址
     */
    public static File getFilmImageFile() {
        File imagePath = new File(MineApp.activity.getCacheDir(), "filmImages");
        if (!imagePath.exists()) {
            imagePath.mkdirs();
        }
        return new File(imagePath, randomString(15) + ".jpg");
    }

    /**
     * 视频文件
     */
    public static File getVideoFile() {
        File videoPath = new File(activity.getCacheDir(), "videos");
        if (!videoPath.exists()) {
            videoPath.mkdirs();
        }
        return new File(videoPath, randomString(15) + ".mp4");
    }

    /**
     * 小米聊天
     */
    public static String getLogCachePath() {
        File file = new File(activity.getCacheDir(), "logCachePath");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getPath();
    }

    /**
     * 小米聊天
     */
    public static String getTokenCachePath() {
        File file = new File(activity.getCacheDir(), "tokenCachePath");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getPath();
    }

    /**
     * 语音文件
     */
    public static File getAudioFile() {
        File audioPath = new File(activity.getCacheDir(), "audios");
        if (!audioPath.exists()) {
            audioPath.mkdirs();
        }
        return new File(audioPath, randomString(15) + ".amr");
    }

    /**
     * 下载文件
     */
    public static File getDownloadFile(String fileType) {
        File downPath = new File(activity.getCacheDir(), "downFiles");
        if (!downPath.exists()) {
            downPath.mkdirs();
        }
        return new File(downPath, randomString(15) + fileType);
    }

    /**
     * 随机选取资源名称
     */
    public static String randomString(int length) {
        if (length < 1) {
            return null;
        }
        Random randGen = new Random();
        char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz"
                + "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
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
            getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
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
        if (permissions.length == 0) return;
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
            WindowManager.LayoutParams params = Objects.requireNonNull(build.getWindow()).getAttributes();
            params.width = width - (width / 6);
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.CENTER;
            build.getWindow().setAttributes(params);

            binding.setContent(permissionDes);


            binding.tvSure.setOnClickListener(v -> {
                ActivityCompat.requestPermissions(BaseActivity.this, permissions, requestCode);
                build.dismiss();
            });
            binding.tvCancel.setOnClickListener(v -> build.dismiss());
            view.setOnTouchListener((v, event) -> {
                build.dismiss();
                return false;
            });
            build.setOnDismissListener(dialog -> {
                if (permissionRunnable != null) {
                    permissionRunnable.noPermission();
                    permissionRunnable = null;
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
