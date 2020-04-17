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
import com.zb.lib_base.windows.SelectorPW;
import com.zb.module_home.R;
import com.zb.module_home.adapter.HomeAdapter;
import com.zb.module_home.iv.PublishImageVMInterface;

import java.util.ArrayList;
import java.util.List;

public class PublishImageViewModel extends BaseViewModel implements PublishImageVMInterface {
    public HomeAdapter adapter;
    public ArrayList<String> images = new ArrayList<>();
    public long videoTime = 0;
    public int cameraType = 0;
    private List<String> selectorList = new ArrayList<>();

    public PublishImageViewModel() {
        selectorList.add("预览");
        selectorList.add("删除");
    }

    @Override
    public void setAdapter() {
        images.add("add_image_icon");
        adapter = new HomeAdapter<>(activity, R.layout.item_home_image, images, this);
    }

    @Override
    public void previewImage(int position) {
        if (cameraType == 1) {
            new SelectorPW(activity, mBinding.getRoot(), selectorList, position1 -> {
                if (position1 == 0) {
                    ActivityUtils.getCameraVideoPlay(images.get(0));
                } else {
                    cameraType = 0;
                    images.clear();
                    images.add("add_image_icon");
                    adapter.notifyDataSetChanged();
                }
            });
        } else {
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
    }

    @Override
    public void publish(View view) {
        MineApp.selectMap.clear();
        MineApp.cutImageViewMap.clear();
        DataCleanManager.deleteFile(activity.getCacheDir());
        activity.finish();
    }

    @Override
    public void back(View view) {
        activity.finish();
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
        ActivityUtils.getCameraMain(activity, true);
    }
}
