package com.zb.lib_base.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.db.ResFileDb;
import com.zb.lib_base.http.DownLoadRetrofitHelper;
import com.zb.lib_base.model.ResFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class DownLoad {

    private static RequestCallBack<File> back;

    public static void getFilePath(String fileUrl, String filePath, CallBack callBack) {
        if (ResFileDb.getInstance().isRead(fileUrl)) {
            File file = new File(ResFileDb.getInstance().getResFile(fileUrl).getFilePath());
            if (file.exists()) {
                callBack.success(ResFileDb.getInstance().getResFile(fileUrl).getFilePath(), null);
            } else {
                ResFileDb.getInstance().deleteResFile(fileUrl);
                downloadVideo(fileUrl, filePath, callBack);
            }
        } else {
            downloadVideo(fileUrl, filePath, callBack);
        }
    }

    private static void downloadVideo(String fileUrl, String filePath, CallBack callBack) {
        back = new RequestCallBack<File>() {

            @Override
            public void onSuccess(ResponseInfo<File> file) {
                ResFileDb.getInstance().saveResFile(new ResFile(fileUrl, file.result.getAbsolutePath()));
                callBack.success(file.result.getAbsolutePath(), null);
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // 下载失败则将本地的空文件删除
                File file = new File(filePath);
                file.deleteOnExit();
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                callBack.onLoading(total, current);
            }
        };

        DownLoadRetrofitHelper.httpClient.download(fileUrl, filePath, back);
    }

    public static void downloadLocation(String fileUrl, CallBack callBack) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath());
        if (!file.exists()) {
            file.mkdirs();
        }
        String filePath = file.getAbsolutePath() + "/Camera/xg_" + BaseActivity.randomString(15) + ".mp4";

        back = new RequestCallBack<File>() {

            @Override
            public void onSuccess(ResponseInfo<File> file) {
                callBack.success(file.result.getAbsolutePath(), null);
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // 下载失败则将本地的空文件删除
                File file = new File(filePath);
                file.deleteOnExit();
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                callBack.onLoading(total, current);
            }
        };

        DownLoadRetrofitHelper.httpClient.download(fileUrl, filePath, back);
    }


    public static void stop() {
        back.onCancelled();
    }


    public static void downImageFile(String fileUrl, CallBack callBack) {

        String filePath = BaseActivity.getImageFile().getAbsolutePath();
        back = new RequestCallBack<File>() {

            @Override
            public void onSuccess(ResponseInfo<File> file) {
                try {
                    InputStream is = new FileInputStream(filePath);
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inTempStorage = new byte[100 * 1024];
                    opts.inPreferredConfig = Bitmap.Config.RGB_565;
                    opts.inPurgeable = true;
                    opts.inSampleSize = 4;
                    opts.inInputShareable = true;
                    Bitmap bitmap = BitmapFactory.decodeStream(is, null, opts);
                    callBack.success(file.result.getAbsolutePath(), bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    callBack.success(file.result.getAbsolutePath(), null);
                }

            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // 下载失败则将本地的空文件删除
                File file = new File(filePath);
                file.deleteOnExit();
                if (callBack != null)
                    callBack.fail();
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                callBack.onLoading(total, current);
            }
        };
        DownLoadRetrofitHelper.httpClient.download(fileUrl, filePath, back);
    }

    @FunctionalInterface
    public interface CallBack {
        void success(String filePath, Bitmap bitmap);

        default void fail() {

        }

        default void onLoading(long total, long current) {
        }
    }
}
