package com.zb.module_home.vm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
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
import com.zb.module_camera.utils.Compressor;
import com.zb.module_camera.utils.InitListener;
import com.zb.module_home.BR;
import com.zb.module_home.R;
import com.zb.module_home.adapter.HomeAdapter;
import com.zb.module_home.databinding.HomePublicImageBinding;
import com.zb.module_home.iv.PublishImageVMInterface;
import com.zero.smallvideorecord.LocalMediaCompress;
import com.zero.smallvideorecord.model.AutoVBRMode;
import com.zero.smallvideorecord.model.LocalMediaConfig;
import com.zero.smallvideorecord.model.OnlyCompressOverBean;

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

    /* 水印 */
    private String outPutUrl = "";
    private String imageUrl = "";
    private Compressor mCompressor;

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

        BaseActivity.createJianXiCameraFile();
        BaseActivity.createFfmpegFile();
        outPutUrl = BaseActivity.getVideoFile().getAbsolutePath();
        imageUrl = BaseActivity.getImageFile().getAbsolutePath();

        photoManager = new PhotoManager(activity, () -> {
            publishDyn(photoManager.jointWebUrl(","));
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

        mCompressor = new Compressor(activity);
        mCompressor.loadBinary(new InitListener() {
            @Override
            public void onLoadSuccess() {
            }

            @Override
            public void onLoadFail(String reason) {
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
            photoManager.addFiles(imageList, () -> photoManager.reUploadByUnSuccess());
        } else {
            CustomProgressDialog.showLoading(activity, "正在处理视频", true);
            if (isChinese(videoUrl)) {
                handler.sendEmptyMessage(4);
            } else {
                File file = new File(videoUrl);
                if (file.length() > 3 * 1024 * 1024) {
                    compress(videoUrl);
                } else {
                    createWater();
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

    private Handler handler = new Handler(msg -> {
        switch (msg.what) {
            case 0:
                SCToastUtil.showToast(activity, "视频处理失败", true);
                CustomProgressDialog.stopLoading();
                break;
            case 1:
                createWater();
                break;
            case 3:
                uploadVideo();
                break;
            case 4:
                SCToastUtil.showToast(activity, "视频链接含中文路径，处理失败", true);
                CustomProgressDialog.stopLoading();
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
                MineApp.toPublish = false;
                MineApp.cameraType = 0;
                MineApp.isMore = false;
                MineApp.filePath = "";
                MineApp.time = 0;
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

    private void uploadVideo() {
        uploadVideoApi api = new uploadVideoApi(new HttpOnNextListener<ResourceUrl>() {
            @Override
            public void onNext(ResourceUrl o) {
                videoUrl = o.getUrl();
                publishDyn("");
            }
        }, activity).setFile(new File(outPutUrl));
        HttpUploadManager.getInstance().doHttpDeal(api);
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

    /*****************水印功能******************/
    /**
     * 文本转成Bitmap
     *
     * @param text 文本内容
     * @return 图片的bitmap
     */
    private Bitmap textToBitmap(String text) {

        TextView tv = new TextView(activity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(layoutParams);
        tv.setText(text);
        tv.setTextSize(14);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setDrawingCacheEnabled(true);
        tv.setTextColor(Color.WHITE);
        tv.setBackgroundColor(Color.TRANSPARENT);
        tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());
        tv.buildDrawingCache();
        Bitmap bitmap = tv.getDrawingCache();
        return Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false);
    }

    private void getImage(Bitmap bitmap) {
        try {
            FileOutputStream os = new FileOutputStream(new File(imageUrl));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] addWaterMark(String imageUrl, String videoUrl, String outputUrl) {
//        String content = "-i " + videoUrl + " -i " + imageUrl +
//                " -filter_complex overlay=20:20 -y -strict -2 -vcodec libx264 -preset ultrafast" +
//                " -crf 29 -threads 2 -acodec aac -ar 44100 -ac 2 -b:a 32k " + outputUrl;
        String[] commands = new String[26];
        commands[0] = "-i";
        commands[1] = videoUrl;
        commands[2] = "-i";
        commands[3] = imageUrl;
        commands[4] = "-filter_complex";
        commands[5] = "overlay=20:20";
        commands[6] = "-y";
        commands[7] = "-strict";
        commands[8] = "-2";
        commands[9] = "-vcodec";
        commands[10] = "libx264";
        commands[11] = "-preset";
        commands[12] = "ultrafast";
        //-crf  用于指定输出视频的质量，取值范围是0-51，默认值为23，数字越小输出视频的质量越高。
        // 这个选项会直接影响到输出视频的码率。一般来说，压制480p我会用20左右，压制720p我会用16-18
        commands[13] = "-crf";
        commands[14] = "10";
        commands[15] = "-threads";
        commands[16] = "2";
        commands[17] = "-acodec";
        commands[18] = "aac";
        commands[19] = "-ar";
        commands[20] = "44100";
        commands[21] = "-ac";
        commands[22] = "2";
        commands[23] = "-b:a";
        commands[24] = "32k";
        commands[25] = outputUrl;
        return commands;
    }

    // 添加水印
    private void createWater() {
        Bitmap bitmap = textToBitmap("虾菇号：" + BaseActivity.userId);
        getImage(bitmap);

        String[] common = addWaterMark(imageUrl, videoUrl, outPutUrl);
        FFmpeg.getInstance(activity).execute(common, new FFmpegExecuteResponseHandler() {
            @Override
            public void onSuccess(String message) {
                handler.sendEmptyMessage(3);
            }

            @Override
            public void onProgress(String message) {
            }

            @Override
            public void onFailure(String message) {
                handler.sendEmptyMessage(0);
            }

            @Override
            public void onStart() {
            }

            @Override
            public void onFinish() {
            }
        });
    }
}
