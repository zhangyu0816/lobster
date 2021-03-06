package com.zb.lib_base.utils.water;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.R;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.http.CustomProgressDialog;
import com.zb.lib_base.utils.DataCleanManager;
import com.zb.lib_base.utils.DisplayUtils;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.water.helper.MagicFilterType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WaterMark {
    public volatile static WaterMark INSTANCE;
    private RxAppCompatActivity activity;
    private String downloadPath = "";
    private String outPutUrl = "";
    private String imageUrl = "";
    private int videoWidth = 0;
    private int videoHeight = 0;
    public static Bitmap waterBitmap;

    private Handler handler = new Handler(msg -> {
        switch (msg.what) {
            case 0:
                SCToastUtil.showToast(activity, "视频下载失败", true);
                CustomProgressDialog.stopLoading();
                break;
            case 1:
                DataCleanManager.deleteFile(new File(downloadPath));
                // 最后通知图库更新
                Intent intent = new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://" + outPutUrl)
                );
                // 最后通知图库更新
                activity.sendBroadcast(intent);
                SCToastUtil.showToast(activity, "下载成功", true);
                CustomProgressDialog.stopLoading();
                break;
        }
        return false;
    });

    public WaterMark() {
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

    public void createWater(RxAppCompatActivity activity, String downloadPath, long otherUserId, int videoWidth, int videoHeight, int duration) {
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
        waterBitmap = textToBitmap("虾菇号：" + otherUserId, null);

        VideoClipper clipper = new VideoClipper();
        clipper.setInputVideoPath(downloadPath);
        clipper.setFilterType(MagicFilterType.WARM);
        clipper.setOutputVideoPath(outPutUrl);
        clipper.setOnVideoCutFinishListener(() -> handler.sendEmptyMessage(1));
        try {
            clipper.clipVideo(0, duration * 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveImage(RxAppCompatActivity activity, long otherUserId, String downloadPath) {
        this.activity = activity;
        this.downloadPath = downloadPath;
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());
        if (!file.exists()) {
            file.mkdirs();
        }
        outPutUrl = file.getAbsolutePath() + "/Camera/xg_" + BaseActivity.randomString(15) + ".jpg";

        Glide.with(activity).asBitmap().load(downloadPath).apply(new RequestOptions().centerCrop()).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                videoWidth = resource.getWidth();
                videoHeight = resource.getHeight();
                activity.runOnUiThread(() -> saveFile(textToBitmap("虾菇号：" + otherUserId, resource)));
            }
        });

    }

    private void saveFile(Bitmap bitmap) {
        File file = new File(outPutUrl);
        try {
            FileOutputStream os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        CustomProgressDialog.stopLoading();
        SCToastUtil.showToast(activity, "保存成功", true);
        Intent intent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + file.getAbsolutePath())
        );
        // 最后通知图库更新
        activity.sendBroadcast(intent);
    }

    /**
     * 文本转成Bitmap
     *
     * @param text 文本内容
     * @return 图片的bitmap
     */
    private Bitmap textToBitmap(String text, Bitmap resource) {
        float ra;
        if (videoHeight > videoWidth)
            ra = (float) videoWidth / (float) MineApp.W;
        else
            ra = (float) videoHeight / (float) MineApp.W;
        int w = (int) (87f * 1.8f * ra);
        int h = (int) (39f * 1.8f * ra);
        int size = (int) (7f * 2 * ra);

        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(videoWidth, videoHeight);

        layout.setLayoutParams(layoutParams);
        if (resource == null)
            layout.setBackgroundColor(Color.TRANSPARENT);
        else {
            layout.setBackgroundDrawable(new BitmapDrawable(resource));
        }

        ImageView iv = new ImageView(activity);
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

        if (resource != null) {
            int width = videoWidth - DisplayUtils.dip2px(size) * text.length();
            int height = videoHeight - 2 * h;
            layout.setPadding(width, height, 0, 0);
        }

        layout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        if (resource == null) {
            layout.layout(0, 0, layout.getMeasuredWidth(), layout.getMeasuredHeight());
            layout.buildDrawingCache();
            Bitmap bitmap = layout.getDrawingCache();
            return Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false);
        } else {
            layout.layout(0, 0, videoWidth, videoHeight);
            Bitmap bitmap = Bitmap.createBitmap(videoWidth, videoHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            layout.draw(canvas);
            return bitmap;
        }
    }
}
