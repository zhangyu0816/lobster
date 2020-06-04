package com.zb.module_mine.vm;

import android.Manifest;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.api.upRealNameInfoApi;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.Authentication;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.uploadImage.PhotoFile;
import com.zb.lib_base.utils.uploadImage.PhotoManager;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.iv.AuthenticationVMInterface;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.ViewDataBinding;

public class AuthenticationViewModel extends BaseViewModel implements AuthenticationVMInterface {
    public Authentication authentication;
    public MineAdapter adapter;
    public List<String> imageList = new ArrayList<>();
    private PhotoManager photoManager;
    public int _position = -1;

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        setAdapter();
        photoManager = new PhotoManager(activity, () -> {
            for (PhotoFile photoFile : photoManager.getPhotoFiles()) {
                int i = imageList.indexOf(photoFile.getSrcFilePath());
                imageList.set(i, photoFile.getWebUrl());
            }
            authentication.setIdFrontImage(imageList.get(0));
            authentication.setIdBackImage(imageList.get(1));
            authentication.setPersonalImage(imageList.get(2));
            upRealNameInfo();
        });
    }

    @Override
    public void back(View view) {
        super.back(view);
        activity.finish();
    }

    @Override
    public void setAdapter() {
        imageList.add(authentication.getIdFrontImage().isEmpty() ? "add_image_icon" : authentication.getIdFrontImage());
        imageList.add(authentication.getIdBackImage().isEmpty() ? "add_image_icon" : authentication.getIdBackImage());
        imageList.add(authentication.getPersonalImage().isEmpty() ? "add_image_icon" : authentication.getPersonalImage());

        adapter = new MineAdapter<>(activity, R.layout.item_authentication_image, imageList, this);
    }

    @Override
    public void selectImage(int position) {
        _position = position;
        getPermissions();
    }

    @Override
    public void submit(View view) {
        if (TextUtils.equals(imageList.get(0), "add_image_icon")) {
            SCToastUtil.showToastBlack(activity, "请上传身份证正面照片");
            return;
        }
        if (TextUtils.equals(imageList.get(1), "add_image_icon")) {
            SCToastUtil.showToastBlack(activity, "请上传身份证正面照片");
            return;
        }
        if (TextUtils.equals(imageList.get(2), "add_image_icon")) {
            SCToastUtil.showToastBlack(activity, "请上传身份证正面照片");
            return;
        }

        if (authentication.getRealName().isEmpty()) {
            SCToastUtil.showToastBlack(activity, "请上传真实姓名");
            return;
        }

        if (authentication.getIdentityNum().isEmpty()) {
            SCToastUtil.showToastBlack(activity, "请上传身份证号");
            return;
        }

        List<String> images = new ArrayList<>();
        for (String item : imageList) {
            if (!item.contains("http://") && !item.contains("https://")) {
                images.add(item);
            }
        }
        if (images.size() == 0) {
            upRealNameInfo();
        } else {
            photoManager.addFiles(images, () -> photoManager.reUploadByUnSuccess());
        }
    }

    @Override
    public void upRealNameInfo() {
        upRealNameInfoApi api = new upRealNameInfoApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                SCToastUtil.showToastBlack(activity, "认证信息已提交，请等待审核结果，需1-3个工作日！");
                activity.finish();
            }
        }, activity)
                .setRealName(authentication.getRealName())
                .setIdentityNum(authentication.getIdentityNum())
                .setIdFrontImage(authentication.getIdFrontImage())
                .setIdBackImage(authentication.getIdBackImage())
                .setPersonalImage(authentication.getPersonalImage())
                .setVerifyMethodType(2);// 1：人工审核  2：机器审核
        HttpManager.getInstance().doHttpDeal(api);
    }

    /**
     * 权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("虾菇需要访问读写外部存储权限及相机权限", new BaseActivity.PermissionCallback() {
                        @Override
                        public void hasPermission() {
                            setPermissions();
                        }

                        @Override
                        public void noPermission() {
                        }
                    }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO);
        } else {
            setPermissions();
        }
    }

    private void setPermissions() {
        ActivityUtils.getCameraMain(activity, false, false);
    }
}
