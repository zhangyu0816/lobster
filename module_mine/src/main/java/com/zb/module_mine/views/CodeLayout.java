package com.zb.module_mine.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.model.WebShare;
import com.zb.lib_base.utils.SCToastUtil;
import com.zb.lib_base.utils.ShareUtil;
import com.zb.module_mine.R;
import com.zb.module_mine.databinding.MinePosterBinding;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Hashtable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class CodeLayout extends RelativeLayout {
    private MinePosterBinding mBinding;

    public CodeLayout(Context context) {
        super(context);
        init(context);
    }

    public CodeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CodeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.mine_poster, null, false);
        addView(mBinding.getRoot());
    }

    public void setData(RxAppCompatActivity activity, WebShare webShare) {
        Bitmap qrCode = createImage(webShare.getUrl(), webShare.getQrCodeW(), webShare.getQrCodeH(), null);
        Glide.with(mBinding.getRoot().getContext()).asBitmap().load(webShare.getImgUrl()).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                Bitmap bitmap = mergeWithCrop(resource, qrCode, webShare.getCoorX(), webShare.getCoorY());
                File myCaptureFile = getFile();
                if (webShare.getShareType() == 5) {
                    Canvas canvas = new Canvas(bitmap);
                    mBinding.ivQrCode.draw(canvas);
                    try {
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
                        bos.flush();
                        bos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    saveFile(activity, myCaptureFile);
                } else {
                    // 1.QQ邀请  2.微信邀请  3.朋友圈邀请  4.微信群邀请  5.海报分享
                    String type = "";
                    switch (webShare.getShareType()) {
                        case 1:
                            type = "qqshare";
                            break;
                        case 2:
                        case 4:
                            type = "wxshare";
                            break;
                        case 3:
                            type = "wxfriend";
                            break;
                    }
                    ShareUtil.share( activity,  bitmap,"邀请好友",webShare.getUrl(),  type);
                }
            }
        });
    }

    private File getFile() {
        File dirFile = new File(Environment.getExternalStorageDirectory(), "DCIM/Camera");
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        String fileName = BaseActivity.randomString(15) + ".jpg";
        return new File(dirFile, fileName);
    }

    /**
     * 保存本地图片
     */
    private void saveFile(RxAppCompatActivity activity, File myCaptureFile) {
        SCToastUtil.showToast(activity, "保存成功", true);
        // 最后通知图库更新
        LocalBroadcastManager.getInstance(MineApp.sContext).sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://" + myCaptureFile.getAbsolutePath())));
    }

    /**
     * 合成图片
     *
     * @param big   底图
     * @param small 小图
     * @return
     */
    public static Bitmap mergeWithCrop(Bitmap big, Bitmap small, int x, int y) {
        int bigW = big.getWidth();
        int bigH = big.getHeight();

        Bitmap newBmp = Bitmap.createBitmap(bigW, bigH, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newBmp);
        //在0，0坐标开始画入bg
        cv.drawBitmap(big, 0, 0, null);
        // 开始画入fg，可以从任意位置画入，具体位置自己计算
        //设置镶嵌图片的位置
        Rect sRect = new Rect(x, y, x + small.getWidth(), y + small.getHeight());
        //将小图镶嵌到背景图指定位置
        cv.drawBitmap(small, null, sRect, null);
        return newBmp;
    }

    /**
     * 生成二维码图片
     *
     * @param text
     * @param w
     * @param h
     * @param logo
     * @return
     */
    public static Bitmap createImage(String text, int w, int h, Bitmap logo) {
        if (TextUtils.isEmpty(text)) {
            return null;
        }
        try {
            Bitmap scaleLogo = null;
            int offsetX = w / 2;
            int offsetY = h / 2;
            int scaleWidth = 0;
            int scaleHeight = 0;
            Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //容错级别
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            //设置空白边距的宽度
            hints.put(EncodeHintType.MARGIN, 0);
            BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, w, h, hints);
            int[] pixels = new int[w * h];
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (x >= offsetX && x < offsetX + scaleWidth && y >= offsetY && y < offsetY + scaleHeight) {
                        int pixel = scaleLogo.getPixel(x - offsetX, y - offsetY);
                        if (pixel == 0) {
                            if (bitMatrix.get(x, y)) {
                                pixel = 0xff000000;
                            } else {
                                pixel = 0xffffffff;
                            }
                        }
                        pixels[y * w + x] = pixel;
                    } else {
                        if (bitMatrix.get(x, y)) {
                            pixels[y * w + x] = 0xff000000;
                        } else {
                            pixels[y * w + x] = 0xffffffff;
                        }
                    }
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(w, h,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
