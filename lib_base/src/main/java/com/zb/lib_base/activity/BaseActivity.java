package com.zb.lib_base.activity;

import android.app.AlertDialog;
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
import com.zb.lib_base.databinding.PwsPerformBinding;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;

import java.io.File;
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
    public static long applyUserId = 10001;
    public static long dynUserId = 10002;
    public static long questionUserId = 10003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        mBinding = DataBindingUtil.setContentView(this, getRes());

        ARouter.getInstance().inject(this);
        update();
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
            getWindow().addFlags(View.SYSTEM_UI_FLAG_FULLSCREEN);
            getWindow().addFlags(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            getWindow().addFlags(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
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
    public void performCodeWithPermission(@NonNull String permissionDes, PermissionCallback runnable, @NonNull String... permissions) {
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

    private void requestPermission(String permissionDes, final int requestCode, final String[] permissions) {
        if (shouldShowRequestPermissionRationale(permissions)) {
            //如果用户之前拒绝过此权限，再提示一次准备授权相关权限
            final AlertDialog build = new AlertDialog.Builder(this).create();
            //自定义布局
            PwsPerformBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.pws_perform, null, false);

            View view = binding.getRoot();
            //把自定义的布局设置到dialog中，注意，布局设置一定要在show之前。从第二个参数分别填充内容与边框之间左、上、右、下、的像素
            build.setView(view, 0, 0, 0, 0);
            //一定要先show出来再设置dialog的参数，不然就不会改变dialog的大小了
            build.show();
            //得到当前显示设备的宽度，单位是像素
            int width = getWindowManager().getDefaultDisplay().getWidth();
            //得到这个dialog界面的参数对象
            WindowManager.LayoutParams params = build.getWindow().getAttributes();
            //设置dialog的界面宽度
            params.width = width - (width / 6);
            //设置dialog高度为包裹内容
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            //设置dialog的重心
            params.gravity = Gravity.CENTER;
            //dialog.getWindow().setLayout(width-(width/6), LayoutParams.WRAP_CONTENT);
            //用这个方法设置dialog大小也可以，但是这个方法不能设置重心之类的参数，推荐用Attributes设置
            //最后把这个参数对象设置进去，即与dialog绑定
            build.getWindow().setAttributes(params);

            binding.setContent(permissionDes);

            binding.sure.setOnClickListener(v -> {
                ActivityCompat.requestPermissions(BaseActivity.this, permissions, requestCode);
                build.dismiss();
            });
            binding.cancel.setOnClickListener(v -> {
                if (permissionRunnable != null) {
                    permissionRunnable.noPermission();
                    permissionRunnable = null;
                }
                build.dismiss();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == permissionRequestCode) {
            if (verifyPermissions(grantResults)) {
                if (permissionRunnable != null) {
                    permissionRunnable.hasPermission();
                    permissionRunnable = null;
                }
            } else {
                SCToastUtil.showToastBlack(activity, "暂无权限执行相关操作！");
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
