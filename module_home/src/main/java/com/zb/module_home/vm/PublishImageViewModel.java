package com.zb.module_home.vm;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import com.maning.imagebrowserlibrary.MNImage;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.api.publishDynApi;
import com.zb.lib_base.api.uploadVideoApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.model.ResourceUrl;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DataCleanManager;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.uploadImage.PhotoFile;
import com.zb.lib_base.utils.uploadImage.PhotoManager;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.SelectorPW;
import com.zb.module_home.R;
import com.zb.module_home.adapter.HomeAdapter;
import com.zb.module_home.databinding.HomePublicImageBinding;
import com.zb.module_home.iv.PublishImageVMInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.databinding.ViewDataBinding;

public class PublishImageViewModel extends BaseViewModel implements PublishImageVMInterface {
    public HomeAdapter adapter;
    public ArrayList<String> images = new ArrayList<>();
    public long videoTime = 0;
    public String videoUrl = "";
    public int cameraType = 0;
    private List<String> selectorList = new ArrayList<>();
    private HomePublicImageBinding publicImageBinding;
    private PhotoManager photoManager;

    public PublishImageViewModel() {
        selectorList.add("预览");
        selectorList.add("删除");
    }

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        publicImageBinding = (HomePublicImageBinding) binding;
        photoManager = new PhotoManager(activity, new PhotoManager.OnUpLoadImageListener() {
            @Override
            public void onSuccess() {
                publishDyn(photoManager.jointWebUrl(","));
                photoManager.deleteAllFile();
            }

            @Override
            public void onError(PhotoFile file) {

            }
        });
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
    public void selectCity(View view) {
        ActivityUtils.getMineLocation();
    }

    @Override
    public void publish(View view) {
        if (publicImageBinding.getTitle().isEmpty()) {
            SCToastUtil.showToast(activity, "请填写动态标题");
            return;
        }
        if (publicImageBinding.getContent().isEmpty()) {
            SCToastUtil.showToast(activity, "请填写动态内容");
            return;
        }
        if (videoUrl.isEmpty()) {
            if (images.size() == 1) {
                SCToastUtil.showToast(activity, "请上传照片或视频");
                return;
            }
            List<String> imageList = new ArrayList<>();
            for (String url : images) {
                if (!TextUtils.equals(url, "add_image_icon")) {
                    imageList.add(url);
                }
            }
            photoManager.addFiles(imageList, () -> photoManager.reUploadByUnSuccess());
        } else {
            uploadVideoApi api = new uploadVideoApi(new HttpOnNextListener<ResourceUrl>() {
                @Override
                public void onNext(ResourceUrl o) {
                    videoUrl = o.getUrl();
                    publishDyn("");
                }
            }, activity).setFile(new File(videoUrl));
            HttpManager.getInstance().doHttpDeal(api);
        }
    }

    private void publishDyn(String images) {
        publishDynApi api = new publishDynApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                MineApp.selectMap.clear();
                MineApp.cutImageViewMap.clear();
                DataCleanManager.deleteFile(activity.getCacheDir());
                activity.sendBroadcast(new Intent("lobster_publish"));
                SCToastUtil.showToast(activity, "发布成功");
                activity.finish();
            }
        }, activity)
                .setText(publicImageBinding.getContent())
                .setFriendTitle(publicImageBinding.getTitle())
                .setImages(images)
                .setResTime((int) videoTime / 1000)
                .setVideoUrl(videoUrl)
                .setAddressInfo(PreferenceUtil.readStringValue(activity, "address"));
        HttpManager.getInstance().doHttpDeal(api);
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
