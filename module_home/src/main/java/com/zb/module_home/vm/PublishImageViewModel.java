package com.zb.module_home.vm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.maning.imagebrowserlibrary.MNImage;
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
import com.zb.lib_base.utils.ActivityUtils;
import com.zb.lib_base.utils.DataCleanManager;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.uploadImage.PhotoManager;
import com.zb.lib_base.vm.BaseViewModel;
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

import androidx.databinding.ViewDataBinding;

public class PublishImageViewModel extends BaseViewModel implements PublishImageVMInterface {
    public HomeAdapter adapter;
    public ArrayList<String> images = new ArrayList<>();
    public long videoTime = 0;
    public String videoUrl = "";
    private File videoImageFile;
    public int cameraType = 0;
    private HomePublicImageBinding publicImageBinding;
    private PhotoManager photoManager;
    private OnlyCompressOverBean onlyCompressOverBean;
    private BaseReceiver locationReceiver;
    private BaseReceiver deleteVideoReceiver;

    @Override
    public void back(View view) {
        locationReceiver.unregisterReceiver();
        deleteVideoReceiver.unregisterReceiver();
        MineApp.selectMap.clear();
        MineApp.selectPathMap.clear();
        MineApp.cutImageViewMap.clear();
        DataCleanManager.deleteFile(new File(activity.getCacheDir(), "videos"));
        DataCleanManager.deleteFile(new File(activity.getCacheDir(), "images"));
        activity.finish();
    }

    @Override
    public void setBinding(ViewDataBinding binding) {
        super.setBinding(binding);
        publicImageBinding = (HomePublicImageBinding) binding;
        photoManager = new PhotoManager(activity, () -> {
            publishDyn(photoManager.jointWebUrl(","));
            photoManager.deleteAllFile();
        });
        File videoPath = new File(activity.getCacheDir(), "videos");
        if (!videoPath.exists()) {
            videoPath.mkdirs();
        }
        JianXiCamera.setVideoCachePath(videoPath.getPath() + "/");

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

    @Override
    public void setAdapter() {
        images.add("add_image_icon");
        adapter = new HomeAdapter<>(activity, R.layout.item_home_image, images, this);
    }

    @Override
    public void previewImage(int position) {
        if (cameraType == 1) {
            if (TextUtils.equals(images.get(0), "add_image_icon")) {
                getPermissions();
            } else {
                ActivityUtils.getCameraVideoPlay(images.get(0), false, true);
            }
        } else {
            if (position == images.size() - 1) {
                getPermissions();
            } else {
                ArrayList<String> imageList = new ArrayList<>();
                for (int i = 0; i < images.size() - 1; i++) {
                    imageList.add(images.get(i));
                }
                MNImage.imageBrowser(activity, mBinding.getRoot(), imageList, position, true, position12 -> {
                    try {
                        int count = MineApp.selectMap.remove(MineApp.selectPathMap.get(images.get(position12)));
                        MineApp.cutImageViewMap.remove(MineApp.selectPathMap.get(images.get(position12)));
                        MineApp.selectPathMap.remove(images.get(position12));
                        for (Map.Entry<String, Integer> entry : MineApp.selectMap.entrySet()) {
                            if (entry.getValue() > count) {
                                MineApp.selectMap.put(entry.getKey(), entry.getValue() - 1);
                            }
                        }
                    } catch (Exception e) {

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
        ActivityUtils.getMineLocation(true);
    }

    @Override
    public void publish(View view) {
        if (publicImageBinding.getTitle().isEmpty()) {
            SCToastUtil.showToast(activity, "请填写动态标题", true);
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
            photoManager.addFiles(imageList, () -> photoManager.reUploadByUnSuccess());
        } else {
            CustomProgressDialog.showLoading(activity, "正在压缩视频", true);
            if (isChinese(videoUrl)) {
                SCToastUtil.showToast(activity, "视频链接含中文路径，压缩失败", true);
                CustomProgressDialog.stopLoading();
            } else {
                File file = new File(videoUrl);
                if (file.length() > 3 * 1024 * 1024) {
                    compress(videoUrl);
                } else {
                    uploadVideo();
//                    saveBitmapFile(ThumbnailUtils.createVideoThumbnail(videoUrl, MediaStore.Video.Thumbnails.MINI_KIND));
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
        new Thread(() -> {
            try {  // 选择本地视频压缩
                final LocalMediaConfig config = new LocalMediaConfig.Buidler()
                        .setVideoPath(filePath)
                        .captureThumbnailsTime(1)
                        .doH264Compress(new AutoVBRMode(34))
                        .setFramerate(10)
                        .build();

                onlyCompressOverBean = new LocalMediaCompress(config).startCompress();


                File file = new File(onlyCompressOverBean.getVideoPath());
                if (file.length() > 3 * 1024 * 1024) {
                    handler.sendEmptyMessage(0);
                } else {
                    videoUrl = onlyCompressOverBean.getVideoPath();
                    videoImageFile = new File(onlyCompressOverBean.getPicPath());
                    handler.sendEmptyMessage(1);
                }
            } catch (Exception e) {
                handler.sendEmptyMessage(0);
            }

        }).start();
    }

    private void saveBitmapFile(Bitmap bitmap) {
        videoImageFile = BaseActivity.getImageFile();//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(videoImageFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            handler.sendEmptyMessage(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler(msg -> {
        Log.e("msg", "" + msg.what);
        CustomProgressDialog.stopLoading();
        switch (msg.what) {
            case 0:
                SCToastUtil.showToast(activity, "压缩视频失败", true);
                break;
            case 1:
                uploadVideo();
                break;
        }
        return false;
    });

    private void uploadVideo() {
        uploadVideoApi api = new uploadVideoApi(new HttpOnNextListener<ResourceUrl>() {
            @Override
            public void onNext(ResourceUrl o) {
                videoUrl = o.getUrl();
                publishDyn("");
            }
        }, activity).setFile(new File(videoUrl));
        HttpUploadManager.getInstance().doHttpDeal(api);
    }

    private void publishDyn(String images) {
        publishDynApi api = new publishDynApi(new HttpOnNextListener() {
            @Override
            public void onNext(Object o) {
                activity.sendBroadcast(new Intent("lobster_publish"));
                SCToastUtil.showToast(activity, "发布成功", true);
                MineApp.toPublish = false;
                MineApp.cameraType = 0;
                MineApp.isMore = false;
                MineApp.filePath = "";
                MineApp.time = 0;
                back(null);
            }
        }, activity)
                .setText(publicImageBinding.getContent())
                .setFriendTitle(publicImageBinding.getTitle())
                .setImages(images)
                .setResTime((int) videoTime / 1000)
                .setVideoUrl(videoUrl)
                .setAddressInfo(publicImageBinding.getCityName());
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
    }
}
