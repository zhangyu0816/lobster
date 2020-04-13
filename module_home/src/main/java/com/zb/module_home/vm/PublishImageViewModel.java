package com.zb.module_home.vm;

import android.Manifest;
import android.os.Build;
import android.view.View;

import com.maning.imagebrowserlibrary.MNImage;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DataCleanManager;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.module_home.HomeAdapter;
import com.zb.module_home.R;
import com.zb.module_home.iv.PublishImageVMInterface;

import java.util.ArrayList;

public class PublishImageViewModel extends BaseViewModel implements PublishImageVMInterface {
    public HomeAdapter adapter;
    public ArrayList<String> images = new ArrayList<>();

    @Override
    public void setAdapter() {
        images.add("add_image_icon");
        adapter = new HomeAdapter<>(activity, R.layout.item_image, images, this);
    }

    @Override
    public void previewImage(int position) {
        if (position == images.size() - 1) {
            getPermissions();
        } else {
            ArrayList<String> imageList = new ArrayList<>();
            for (int i = 0; i < images.size() - 1; i++) {
                imageList.add(images.get(i));
            }
            MNImage.imageBrowser(activity, mBinding.getRoot(), imageList, position);
        }
    }

    @Override
    public void publish(View view) {
        MineApp.selectMap.clear();
        MineApp.cutImageViewMap.clear();
        DataCleanManager.deleteFile(activity.getCacheDir());
        activity.finish();
    }

    @Override
    public void cancel(View view) {
        activity.finish();
    }

    /**
     * 权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("虾菇需要访问读外部存储权限及相机权限", new BaseActivity.PermissionCallback() {
                @Override
                public void hasPermission() {
                    setPermissions();
                }

                @Override
                public void noPermission() {
                }
            }, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA);
        } else {
            setPermissions();
        }
    }

    private void setPermissions() {
        ActivityUtils.getCameraMain(activity, true);
    }
}
