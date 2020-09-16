package com.zb.lib_base.utils.water;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.R;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.CustomProgressDialog;
import com.zb.lib_base.utils.DataCleanManager;
import com.zb.lib_base.utils.SCToastUtil;

import java.io.File;
import java.io.FileOutputStream;

public class WaterMark {
    public volatile static WaterMark INSTANCE;
    private RxAppCompatActivity activity;
    private Compressor mCompressor;
    private String downloadPath = "";
    private String outPutUrl = "";
    private String imageUrl = "";
    private int videoWidth = 0;
    private int videoHeight = 0;
    private Handler handler = new Handler(msg -> {
        switch (msg.what) {
            case 0:
                SCToastUtil.showToast(activity, "视频下载失败", true);
                CustomProgressDialog.stopLoading();
                break;
            case 1:
                DataCleanManager.deleteFile(new File(downloadPath));
                // 最后通知图库更新
                MineApp.getInstance().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://" + outPutUrl)));
                SCToastUtil.showToast(activity, "下载成功", true);
                CustomProgressDialog.stopLoading();
                break;
        }
        return false;
    });

    public WaterMark() {
        mCompressor = new Compressor(BaseActivity.activity);
        mCompressor.loadBinary(new InitListener() {
            @Override
            public void onLoadSuccess() {
            }

            @Override
            public void onLoadFail(String reason) {
            }
        });
    }

    //获取单例
    public static WaterMark getInstance() {
        if (INSTANCE == null) {
            synchronized (WaterMark.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WaterMark();
                }
            }
        }
        return INSTANCE;
    }

    public void createWater(RxAppCompatActivity activity, String downloadPath, long otherUserId, int videoWidth, int videoHeight) {
        this.activity = activity;
        this.downloadPath = downloadPath;
        this.videoWidth = videoWidth;
        this.videoHeight = videoHeight;

        CustomProgressDialog.showLoading(activity, "正在处理视频");
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());
        if (!file.exists()) {
            file.mkdirs();
        }
        outPutUrl = file.getAbsolutePath() + "/Camera/xg_" + BaseActivity.randomString(15) + ".mp4";
        imageUrl = BaseActivity.getImageFile().getAbsolutePath();
        Bitmap bitmap = textToBitmap("我的虾菇号：" + otherUserId);
        getImage(bitmap);

        String[] common = addWaterMark(imageUrl, downloadPath, outPutUrl);
        FFmpeg.getInstance(activity).execute(common, new FFmpegExecuteResponseHandler() {
            @Override
            public void onSuccess(String message) {
                handler.sendEmptyMessage(1);
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

    /**
     * 文本转成Bitmap
     *
     * @param text 文本内容
     * @return 图片的bitmap
     */
    private Bitmap textToBitmap(String text) {

        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(videoWidth, videoHeight);
        layout.setLayoutParams(layoutParams);
        layout.setBackgroundColor(Color.TRANSPARENT);

        ImageView iv = new ImageView(activity);
        float ra = 0;
        if (videoHeight > videoWidth)
            ra = (float) videoWidth / (float) MineApp.W;
        else
            ra = (float) videoHeight / (float) MineApp.W;
        int w = (int) (87f * 1.8f * ra);
        int h = (int) (39f * 1.8f * ra);
        int size = (int) (7f * 2 * ra);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w, h);
        params.leftMargin = 0;
        params.rightMargin = MineApp.W;
        params.gravity = Gravity.START;
        iv.setLayoutParams(params);
        iv.setImageResource(R.mipmap.water_icon);
        layout.addView(iv);

        TextView tv = new TextView(activity);
        tv.setText(text);
        tv.setTextSize(size);
        tv.setTextColor(Color.WHITE);
        tv.setShadowLayer(1, 2f, 2f, R.color.black);
        tv.setBackgroundColor(Color.TRANSPARENT);
        layout.addView(tv);

        layout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        layout.layout(0, 0, layout.getMeasuredWidth(), layout.getMeasuredHeight());
        layout.buildDrawingCache();
        Bitmap bitmap = layout.getDrawingCache();
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
        String content = "-i " + videoUrl +
                " -i " + imageUrl + " -filter_complex overlay=10:10" +
                " -y -strict -2 -vcodec libx264 -preset ultrafast -crf 10 -threads 2 -acodec aac -ar 44100 -ac 2 -b:a 32k " + outputUrl;
        //-crf  用于指定输出视频的质量，取值范围是0-51，默认值为23，数字越小输出视频的质量越高。
        // 这个选项会直接影响到输出视频的码率。一般来说，压制480p我会用20左右，压制720p我会用16-18
        return content.split(" ");
    }

}
