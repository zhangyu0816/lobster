package com.zb.lib_base.utils;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.db.ResFileDb;
import com.zb.lib_base.http.DownLoadRetrofitHelper;
import com.zb.lib_base.model.ResFile;

import java.io.File;

import io.realm.Realm;


public class DownLoad {

    private static ResFileDb resFileDb;

    public static void getFilePath(String fileUrl, CallBack callBack) {
        if (resFileDb == null)
            resFileDb = new ResFileDb(Realm.getDefaultInstance());
        if (resFileDb.isRead(fileUrl)) {
            File file = new File(resFileDb.getResFile(fileUrl).getFilePath());
            if (file.exists()) {
                callBack.success(resFileDb.getResFile(fileUrl).getFilePath());
            } else {
                resFileDb.deleteResFile(fileUrl);
                downloadVideo(fileUrl, callBack);
            }
        } else {
            downloadVideo(fileUrl, callBack);
        }
    }

    private static void downloadVideo(String fileUrl, CallBack callBack) {
        String filePath = BaseActivity.getDownloadFile(".mp4").getAbsolutePath();
        DownLoadRetrofitHelper.httpClient.download(fileUrl, filePath, new RequestCallBack<File>() {

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
        });
    }

    @FunctionalInterface
    public interface CallBack {
        void success(String filePath);

        default void onLoading(long total, long current) {
        }
    }
}
