package com.zb.module_camera.utils;

import android.database.Cursor;
import android.os.Handler;
import android.provider.MediaStore;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.app.MineApp;
import com.zb.lib_base.model.VideoInfo;

public class GetVideo {

    /**
     * 获取本地所有的视频
     *
     * @return list
     */
    public static void getAllLocalVideos(RxAppCompatActivity context, Handler handler) {
        new Thread(() -> {
            String[] projection = {
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.DURATION,
                    MediaStore.Video.Media.SIZE
            };
            //全部图片
            String where = MediaStore.Video.Media.MIME_TYPE + "=?";
            String[] whereArgs = {"video/mp4"};
            Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    projection, where, whereArgs, MediaStore.Video.Media.DATE_ADDED + " DESC ");
            if (cursor == null) {
                return;
            }
            try {
                while (cursor.moveToNext()) {
                    long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)); // 大小
                    if (size < 20 * 1024 * 1024) {//<600M
                        String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)); // 路径
                        VideoInfo vi = new VideoInfo();
                        vi.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)));
                        vi.setPath(path);
                        MineApp.videoInfoList.add(vi);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
            if (MineApp.videoInfoList.size() > 0)
                handler.sendEmptyMessage(0);
        }).start();
    }
}
