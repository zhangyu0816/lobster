package com.yimi.rentme.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.yimi.rentme.BR;
import com.yimi.rentme.EncryptionUtil;
import com.yimi.rentme.R;
import com.yimi.rentme.databinding.AcLoadingBinding;
import com.yimi.rentme.vm.LoadingViewModel;
import com.zb.lib_base.app.MineApp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import androidx.databinding.DataBindingUtil;

public class LoadingActivity extends RxAppCompatActivity {
    private LoadingViewModel viewModel;
    private AcLoadingBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MineApp.getApp().addActivity(this);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        mBinding = DataBindingUtil.setContentView(this, R.layout.ac_loading);
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewModel = new LoadingViewModel();
        mBinding.setVariable(BR.viewModel, viewModel);
        viewModel.setBinding(mBinding);
        EncryptionUtil.encryptionMD5(MineApp.sContext, getSign(MineApp.sContext, getPackageName()), 0);
        Log.e("androidSign", EncryptionUtil.encryptionMD5(MineApp.sContext, getSign(MineApp.sContext, getPackageName()), 0));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
        mBinding = null;
        viewModel = null;
    }

    /**
     * 获取应用签名
     *
     * @param context
     * @param packageName
     * @return
     */
    public static String getSign(Context context, String packageName) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            byte[] bytes = info.signatures[0].toByteArray();
            return byte2hex(bytes).toLowerCase(Locale.ROOT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取签名的MD5值
     *
     * @param context
     * @param packageName
     * @return
     */
    public static String getSignMD5(Context context, String packageName) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            byte[] bytes = info.signatures[0].toByteArray();
            return encryptionMD5(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String encryptionMD5(byte[] byteStr) {
        MessageDigest messageDigest = null;
        StringBuffer md5StrBuff = new StringBuffer();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(byteStr);
            byte[] byteArray = messageDigest.digest();
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5StrBuff.toString();
    }

    private static String byte2hex(byte[] bytes) {
        String hs = "";
        String tmp = "";
        for (int i = 0; i < bytes.length; i++) {
            tmp = (Integer.toHexString(bytes[i] & 0XFF));
            if (tmp.length() == 1)
                hs = hs + "0" + tmp;
            else
                hs = hs + tmp;
        }
        return hs.toUpperCase();
    }
}
