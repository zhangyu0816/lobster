package com.zb.lib_base.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.db.ResFileDb;
import com.zb.lib_base.http.DownLoadRetrofitHelper;
import com.zb.lib_base.model.ResFile;

import java.io.File;

import io.realm.Realm;


public class DownLoad {

    private static ResFileDb resFileDb;
    private static RequestCallBack<File> back;

    public static void getFilePath(String fileUrl, String filePath, CallBack callBack) {
        if (resFileDb == null)
            resFileDb = new ResFileDb(Realm.getDefaultInstance());
        if (resFileDb.isRead(fileUrl)) {
            File file = new File(resFileDb.getResFile(fileUrl).getFilePath());
            if (file.exists()) {
                callBack.success(resFileDb.getResFile(fileUrl).getFilePath());
            } else {
                resFileDb.deleteResFile(fileUrl);
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
                resFileDb.saveResFile(new ResFile(fileUrl, file.result.getAbsolutePath()));
                callBack.success(file.result.getAbsolutePath());
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // 下载失败则将本地的空文件删除
                File file = new File(filePath);
                file.deleteOnExit();
                file = null;
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
                callBack.success(file.result.getAbsolutePath());
                // 最后通知图库更新
                MineApp.getInstance().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://" + file.result.getAbsolutePath())));
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                // 下载失败则将本地的空文件删除
                File file = new File(filePath);
                file.deleteOnExit();
                file = null;
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

    @FunctionalInterface
    public interface CallBack {
        void success(String filePath);

        default void onLoading(long total, long current) {
        }
    }
}
