package com.zb.module_mine.vm;

import android.Manifest;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.api.upRealNameInfoApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.Authentication;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DataCleanManager;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.uploadImage.PhotoFile;
import com.zb.lib_base.utils.uploadImage.PhotoManager;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.TextPW;
import com.zb.module_mine.R;
import com.zb.module_mine.adapter.MineAdapter;
import com.zb.module_mine.iv.AuthenticationVMInterface;

import java.io.File;
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
        DataCleanManager.deleteFile(new File(activity.getCacheDir(), "images"));
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
        if (checkPermissionGranted(activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            setPermissions();
        } else {
            if (PreferenceUtil.readIntValue(activity, "photoPermission") == 0) {
                PreferenceUtil.saveIntValue(activity, "photoPermission", 1);
                new TextPW(activity, mBinding.getRoot(), "权限说明",
                        "认证身份证信息时需要使用上传图片功能，我们将会申请相机、存储权限：" +
                                "\n 1、申请相机权限--上传图片时获取拍摄照片功能，" +
                                "\n 2、申请存储权限--上传图片时获取保存和读取图片功能，" +
                                "\n 3、若您点击“同意”按钮，我们方可正式申请上述权限，以便拍摄照片及选取照片，上传身份证正反面照片及本人头像照片，" +
                                "\n 4、若您点击“拒绝”按钮，我们将不再主动弹出该提示，您也无法完成身份证认证，不影响使用其他的虾菇功能/服务，" +
                                "\n 5、您也可以通过“手机设置--应用--虾菇--权限”或app内“我的--设置--权限管理--权限”，手动开启或关闭相机、存储权限。",
                        "同意", false, true, this::getPermissions);
            } else {
                if (!checkPermissionGranted(activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    SCToastUtil.showToast(activity, "你未开启存储权限，请前往我的--设置--权限管理--权限进行设置", true);
                } else if (!checkPermissionGranted(activity, Manifest.permission.CAMERA)) {
                    SCToastUtil.showToast(activity, "你未开启相机权限，请前往我的--设置--权限管理--权限进行设置", true);
                }
            }
        }
    }

    @Override
    public void submit(View view) {
        if (TextUtils.equals(imageList.get(0), "add_image_icon")) {
            SCToastUtil.showToast(activity, "请上传身份证正面照片", true);
            return;
        }
        if (TextUtils.equals(imageList.get(1), "add_image_icon")) {
            SCToastUtil.showToast(activity, "请上传身份证反面照片", true);
            return;
        }
        if (TextUtils.equals(imageList.get(2), "add_image_icon")) {
            SCToastUtil.showToast(activity, "请上传手持身份证照片", true);
            return;
        }

        if (authentication.getRealName().isEmpty()) {
            SCToastUtil.showToast(activity, "请上传真实姓名", true);
            return;
        }

        if (authentication.getIdentityNum().isEmpty()) {
            SCToastUtil.showToast(activity, "请上传身份证号", true);
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
                SCToastUtil.showToast(activity, "认证信息已提交，请等待审核结果，需1-3个工作日！", true);
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
            performCodeWithPermission("虾菇需要访问存储权限及相机权限", new BaseActivity.PermissionCallback() {
                        @Override
                        public void hasPermission() {
                            setPermissions();
                        }

                        @Override
                        public void noPermission() {
                            PreferenceUtil.saveIntValue(activity, "photoPermission", 1);
                        }
                    }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            setPermissions();
        }
    }

    private void setPermissions() {
        MineApp.toPublish = false;
        ActivityUtils.getCameraMain(activity, false, true, false);
    }
}
