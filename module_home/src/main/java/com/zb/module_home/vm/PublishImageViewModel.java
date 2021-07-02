package com.zb.module_home.vm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;

import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.activity.BaseReceiver;
import com.zb.lib_base.api.publishDynApi;
import com.zb.lib_base.api.uploadVideoApi;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.CustomProgressDialog;
import com.zb.lib_base.http.HttpManager;
import com.zb.lib_base.http.HttpOnNextListener;
import com.zb.lib_base.http.HttpUploadManager;
import com.zb.lib_base.model.ResourceUrl;
import com.zb.lib_base.utils.AMapLocation;
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DataCleanManager;
import com.zb.lib_base.utils.MNImage;
import com.zb.lib_base.utils.PreferenceUtil;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.uploadImage.PhotoManager;
import com.zb.lib_base.vm.BaseViewModel;
import com.zb.lib_base.windows.TextPW;
import com.zb.module_home.BR;
import com.zb.module_home.R;
import com.zb.module_home.adapter.HomeAdapter;
import com.zb.module_home.databinding.HomePublicImageBinding;
import com.zb.module_home.iv.PublishImageVMInterface;
import com.zero.smallvideorecord.JianXiCamera;
import com.zero.smallvideorecord.LocalMediaCompress;
import com.zero.smallvideorecord.model.AutoVBRMode;
import com.zero.smallvideorecord.model.LocalMediaConfig;
import com.zero.smallvideorecord.model.OnlyCompressOverBean;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.core.widget.ContentLoadingProgressBar;
import androidx.databinding.ViewDataBinding;

public class PublishImageViewModel extends BaseViewModel implements PublishImageVMInterface {
    public HomeAdapter adapter;
    public ArrayList<String> images = new ArrayList<>();
    public long videoTime = 0;
    public String videoUrl = "";
    public int cameraType = 0;
    private HomePublicImageBinding publicImageBinding;
    private PhotoManager photoManager;
    private OnlyCompressOverBean onlyCompressOverBean;
    private BaseReceiver locationReceiver;
    private BaseReceiver deleteVideoReceiver;
    private File videoImageFile;
    private AMapLocation aMapLocation;

    @Override
    public void back(View view) {
        MineApp.toPublish = false;
        MineApp.cameraType = 0;
        MineApp.isMore = false;
        MineApp.filePath = "";
        MineApp.time = 0;
        try {
            locationReceiver.unregisterReceiver();
            deleteVideoReceiver.unregisterReceiver();
        } catch (Exception e) {
            e.printStackTrace();
        }
        MineApp.selectMap.clear();
        DataCleanManager.deleteFile(new File(activity.getCacheDir(), "videos"));
        DataCleanManager.deleteFile(new File(activity.getCacheDir(), "images"));
        activity.finish();
    }

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        aMapLocation = new AMapLocation(activity);
        publicImageBinding = (HomePublicImageBinding) binding;

        createJianXiCameraFile();
        photoManager = new PhotoManager(activity, () -> {

            if (videoUrl.isEmpty()) {
                publishDyn(photoManager.jointWebUrl(","));
            } else {
                uploadVideo(photoManager.jointWebUrl(","));
            }
            photoManager.deleteAllFile();
        });

        locationReceiver = new BaseReceiver(activity, "lobster_location") {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBinding.setVariable(BR.cityName, intent.getStringExtra("cityName"));
            }
        };
        deleteVideoReceiver = new BaseReceiver(activity, "lobster_deleteVideo") {
            @Override
            public void onReceive(Context context, Intent intent) {
                MineApp.filePath = "";
                images.clear();
                images.add("add_image_icon");
                adapter.notifyDataSetChanged();
            }
        };
        setAdapter();
    }

    private void createJianXiCameraFile() {
        File file = new File(activity.getCacheDir(), "videos");
        if (!file.exists()) {
            file.mkdirs();
        }
        JianXiCamera.setVideoCachePath(file.getPath() + "/");
    }

    @Override
    public void setAdapter() {
        images.add("add_image_icon");
        adapter = new HomeAdapter<>(activity, R.layout.item_home_image, images, this);
    }

    @Override
    public void previewImage(int position) {
        if (cameraType == 1) {
            if (TextUtils.equals(images.get(0), "add_image_icon")) {
                getPermissions(1);
            } else {
                ActivityUtils.getCameraVideoPlay(images.get(0), false, true);
            }
        } else {
            if (position == images.size() - 1) {
                getPermissions(1);
            } else {
                ArrayList<String> imageList = new ArrayList<>();
                for (int i = 0; i < images.size() - 1; i++) {
                    imageList.add(images.get(i));
                }
                MNImage.imageBrowser(activity, mBinding.getRoot(),0, imageList, position, true, position12 -> {
                    try {
                        int count = MineApp.selectMap.remove(images.get(position12));
                        for (Map.Entry<String, Integer> entry : MineApp.selectMap.entrySet()) {
                            if (entry.getValue() > count) {
                                MineApp.selectMap.put(entry.getKey(), entry.getValue() - 1);
                            }
                        }
                    } catch (Exception ignored) {

                    }
                    adapter.notifyItemRemoved(position12);
                    images.remove(position12);
                    adapter.notifyDataSetChanged();
                });
            }
        }
    }

    @Override
    public void selectCity(View view) {
        if (PreferenceUtil.readStringValue(activity, "latitude").isEmpty()) {
            new TextPW(mBinding.getRoot(), "定位失败", "定位失败，无法选取地址，请重新定位", "重新定位", () -> getPermissions(2));
        } else {
            ActivityUtils.getMineLocation(true);
        }
    }

    @Override
    public void publish(View view) {
        if (publicImageBinding.getContent().isEmpty()) {
            SCToastUtil.showToast(activity, "请填写动态内容", true);
            return;
        }

        if (videoUrl.isEmpty()) {
            if (images.size() == 1) {
                SCToastUtil.showToast(activity, "请上传照片或视频", true);
                return;
            }
            List<String> imageList = new ArrayList<>();
            for (String url : images) {
                if (!TextUtils.equals(url, "add_image_icon")) {
                    imageList.add(url);
                }
            }
            photoManager.setNeedProgress(true);
            photoManager.addFiles(imageList, () -> photoManager.reUploadByUnSuccess());
        } else {
            CustomProgressDialog.showLoading(activity, "正在处理视频", true);
            if (isChinese(videoUrl)) {
                handler.sendEmptyMessage(1);
            } else {
                File file = new File(videoUrl);
                if (file.length() > 10 * 1024 * 1024) {
                    compress(videoUrl);
                } else {
                    saveBitmapFile(ThumbnailUtils.createVideoThumbnail(videoUrl, MediaStore.Video.Thumbnails.MINI_KIND));
                }
            }
        }
    }

    // 判断一个字符是否是中文
    public boolean isChinese(char c) {
        return c >= 0x4E00 && c <= 0x9FA5;// 根据字节码判断
    }

    // 判断一个字符串是否含有中文
    public boolean isChinese(String str) {
        if (str == null)
            return false;
        for (char c : str.toCharArray()) {
            if (isChinese(c))
                return true;// 有一个中文字符就返回
        }
        return false;
    }

    private void compress(final String filePath) {

        Runnable ra = () -> {
            try {  // 选择本地视频压缩
                final LocalMediaConfig config = new LocalMediaConfig.Buidler()
                        .setVideoPath(filePath)
                        .captureThumbnailsTime(1)
                        .doH264Compress(new AutoVBRMode(34))
                        .setFramerate(10)
                        .build();

                onlyCompressOverBean = new LocalMediaCompress(config).startCompress();
                File file = new File(onlyCompressOverBean.getVideoPath());
                if (file.length() > 10 * 1024 * 1024) {
                    handler.sendEmptyMessage(0);
                } else {
                    videoUrl = onlyCompressOverBean.getVideoPath();
                    videoImageFile = new File(onlyCompressOverBean.getPicPath());
                    handler.sendEmptyMessage(2);
                }
            } catch (Exception e) {
                handler.sendEmptyMessage(0);
            }
        };
        MineApp.getApp().getFixedThreadPool().execute(ra);
    }

    private void saveBitmapFile(Bitmap bitmap) {
        videoImageFile = BaseActivity.getImageFile();//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(videoImageFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            handler.sendEmptyMessage(2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler(msg -> {
        switch (msg.what) {
            case 0:
                SCToastUtil.showToast(activity, "视频压缩失败", true);
                CustomProgressDialog.stopLoading();
                break;
            case 1:
                SCToastUtil.showToast(activity, "链接中含中文，处理失败", true);
                CustomProgressDialog.stopLoading();
                break;
            case 2:
                photoManager.setNeedProgress(false);
                photoManager.addFileUpload(0, videoImageFile);
                break;
        }
        return false;
    });

    private void publishDyn(String images) {
        publishDynApi api = new publishDynApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                CustomProgressDialog.stopLoading();
                activity.sendBroadcast(new Intent("lobster_publish"));
                SCToastUtil.showToast(activity, "发布成功", true);
                back(null);
            }
        }, activity)
                .setText(publicImageBinding.getContent())
                .setFriendTitle("")
                .setImages(images)
                .setResTime((int) videoTime / 1000)
                .setVideoUrl(videoUrl)
                .setAddressInfo(publicImageBinding.getCityName());
        HttpManager.getInstance().doHttpDeal(api);
    }

    private void uploadVideo(String image) {
        View view = Objects.requireNonNull(publicImageBinding.recyclerView.getLayoutManager()).findViewByPosition(0);
        assert view != null;
        ContentLoadingProgressBar progressBar = view.findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);

        uploadVideoApi api = new uploadVideoApi(new HttpOnNextListener<ResourceUrl>() {
            @Override
            public void onNext(ResourceUrl o) {
                videoUrl = o.getUrl();
                publishDyn(image);
            }

            @Override
            public void onLoading(long contentLength, long bytesWritten) {
                super.onLoading(contentLength, bytesWritten);
                progressBar.setMax((int) contentLength);
                progressBar.setProgress((int) bytesWritten);
            }
        }, activity).setFile(new File(videoUrl));
        HttpUploadManager.getInstance().doHttpDeal(api);
    }

    /**
     * 权限
     */
    private void getPermissions(int type) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            performCodeWithPermission("虾菇需要访问读写外部存储权限及相机权限", new BaseActivity.PermissionCallback() {
                        @Override
                        public void hasPermission() {
                            setPermissions(type);
                        }

                        @Override
                        public void noPermission() {
                        }
                    }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            setPermissions(type);
        }
    }

    private void setPermissions(int type) {
        if (type == 1) {
            if (MineApp.toPublish) {
                MineApp.toContinue = true;
                if (cameraType == 1) {
                    ActivityUtils.getCameraVideo(false);
                } else {
                    ActivityUtils.getCameraMain(activity, true, true, false);
                }
            } else {
                ActivityUtils.getCameraMain(activity, true, true, true);
            }
        } else {
            aMapLocation.start(activity, () ->
                    ActivityUtils.getMineLocation(true));
        }
    }
}
