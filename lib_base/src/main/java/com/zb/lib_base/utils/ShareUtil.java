package com.zb.lib_base.utils;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.util.HashMap;
import java.util.Map;

public class ShareUtil {
    private static RxAppCompatActivity mActivity;
    private static Map<SHARE_MEDIA, String> mediaMap = new HashMap<>();

    static {
        mediaMap.put(SHARE_MEDIA.WEIXIN, "微信");
        mediaMap.put(SHARE_MEDIA.WEIXIN_CIRCLE, "朋友圈");
        mediaMap.put(SHARE_MEDIA.QQ, "QQ");
        mediaMap.put(SHARE_MEDIA.QZONE, "QQ空间");
    }

    public static void share(RxAppCompatActivity activity, String logo, String sharedName, String content, String sharedUrl, String type) {
        mActivity = activity;
        SHARE_MEDIA media;
        UMImage umImage;
        UMWeb web = new UMWeb(sharedUrl);
        if (!logo.isEmpty()) {
            umImage = new UMImage(activity, logo);
            umImage.compressStyle = UMImage.CompressStyle.SCALE;//大小压缩，默认为大小压缩，适合普通很大的图
            umImage.compressFormat = Bitmap.CompressFormat.PNG;
            web.setThumb(umImage);
        }

        if (TextUtils.equals("wxfriend", type)) {
            media = SHARE_MEDIA.WEIXIN_CIRCLE;
            if (!sharedName.isEmpty() || !content.isEmpty()) {
                web.setTitle(sharedName + "\n" + content);//标题
                web.setDescription(sharedName + "\n" + content);//描述
            }
        } else {
            if (TextUtils.equals("qqshare", type)) {
                media = SHARE_MEDIA.QQ;
            } else if (TextUtils.equals("wxshare", type)) {
                media = SHARE_MEDIA.WEIXIN;
            } else {
                media = SHARE_MEDIA.QZONE;
            }
            if (!sharedName.isEmpty())
                web.setTitle(sharedName);//标题
            if (!content.isEmpty())
                web.setDescription(content);//描述
        }

        // 微信
        new ShareAction(activity).setPlatform(media)
                .withMedia(web)
                .setCallback(umShareListener)
                .share();
    }

    private static UMShareListener umShareListener = new UMShareListener() {
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
