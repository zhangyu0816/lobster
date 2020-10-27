package com.zb.lib_base.utils;

import android.util.Log;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.util.HashMap;
import java.util.Map;

public class ShareUtil {
    private static RxAppCompatActivity mActivity;
    private Map<SHARE_MEDIA, String> mediaMap = new HashMap<>();
    private static UMImage umImage;
    private static String sharedName;
    private static String content;
    private String sharedUrl;
    private UMWeb web;
    private SHARE_MEDIA media;

    {
        mediaMap.put(SHARE_MEDIA.WEIXIN, "微信");
        mediaMap.put(SHARE_MEDIA.WEIXIN_CIRCLE, "朋友圈");
        mediaMap.put(SHARE_MEDIA.QQ, "QQ");
        mediaMap.put(SHARE_MEDIA.QZONE, "QQ空间");
    }

    public static void share(RxAppCompatActivity activity, String logo, String sharedName, String content, String sharedUrl, SHARE_MEDIA media) {
        mActivity = activity;
        umImage = new UMImage(activity, logo);
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            SCToastUtil.showToast(mActivity, mediaMap.get(platform) + " 开始分享", true);
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            SCToastUtil.showToast(mActivity, mediaMap.get(platform) + " 分享成功啦", true);
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            SCToastUtil.showToast(mActivity, mediaMap.get(platform) + " 分享失败啦", true);
            if (t != null) {
                Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            SCToastUtil.showToast(mActivity, platform + " 分享取消了", true);
        }
    };
}
