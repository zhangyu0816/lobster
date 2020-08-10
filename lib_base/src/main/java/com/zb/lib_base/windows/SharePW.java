package com.zb.lib_base.windows;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.View;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.zb.lib_base.BR;
import com.zb.lib_base.R;
import com.zb.lib_base.adapter.BaseAdapter;
import com.zb.lib_base.model.ShareItem;
import com.zb.lib_base.utils.SCToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SharePW extends BasePopupWindow {
    public BaseAdapter adapter;
    private List<ShareItem> shareItemList = new ArrayList<>();
    private UMImage umImage;
    private String sharedName;
    private String content;
    private String sharedUrl;
    private UMWeb web;
    private SHARE_MEDIA media;
    private Map<SHARE_MEDIA, String> mediaMap = new HashMap<>();

    public SharePW(RxAppCompatActivity activity, View parentView, UMImage umImage, String sharedName, String content, String sharedUrl) {
        super(activity, parentView, true);
        this.umImage = umImage;
        this.sharedName = sharedName;
        this.content = content;
        this.sharedUrl = sharedUrl;
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_share;
    }

    @Override
    public void initUI() {
        mediaMap.put(SHARE_MEDIA.WEIXIN, "微信");
        mediaMap.put(SHARE_MEDIA.WEIXIN_CIRCLE, "朋友圈");
        mediaMap.put(SHARE_MEDIA.QQ, "QQ");
        mediaMap.put(SHARE_MEDIA.QZONE, "QQ空间");
        shareItemList.add(new ShareItem(R.mipmap.share_wx_ico, "微信好友"));
        shareItemList.add(new ShareItem(R.mipmap.share_wxcircle_ico, "朋友圈"));
        shareItemList.add(new ShareItem(R.mipmap.share_qq_ico, "QQ好友"));
        shareItemList.add(new ShareItem(R.mipmap.share_qqzore_ico, "QQ空间"));
        shareItemList.add(new ShareItem(R.mipmap.share_copy_ico, "复制链接"));
        adapter = new BaseAdapter<>(activity, R.layout.item_share, shareItemList, this);
        mBinding.setVariable(BR.pw, SharePW.this);

        umImage.compressStyle = UMImage.CompressStyle.SCALE;//大小压缩，默认为大小压缩，适合普通很大的图
        umImage.compressFormat = Bitmap.CompressFormat.PNG;
        web = new UMWeb(sharedUrl);
        web.setThumb(umImage);
    }

    @Override
    public void cancel(View view) {
        super.cancel(view);
        dismiss();
    }

    @Override
    public void selectIndex(int position) {
        super.selectIndex(position);
        dismiss();
        switch (position) {
            case 0:
                web.setTitle(sharedName);//标题
                web.setDescription(content);//描述
                media = SHARE_MEDIA.WEIXIN;
                break;
            case 1:
                web.setTitle(sharedName + "\n" + content);//标题
                web.setDescription(sharedName + "\n" + content);//描述
                media = SHARE_MEDIA.WEIXIN_CIRCLE;
                break;
            case 2:
                web.setTitle(sharedName);//标题
                web.setDescription(content);//描述
                media = SHARE_MEDIA.QQ;
                break;
            case 3:
                web.setTitle(sharedName);//标题
                web.setDescription(content);//描述
                media = SHARE_MEDIA.QZONE;
                break;
            case 4:
                copy(sharedUrl);
                break;
        }
        // 微信
        new ShareAction(activity).setPlatform(media)
                .withMedia(web)
                .setCallback(umShareListener)
                .share();
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            SCToastUtil.showToast(activity, mediaMap.get(platform) + " 开始分享", true);
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            SCToastUtil.showToast(activity, mediaMap.get(platform) + " 分享成功啦", true);
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            SCToastUtil.showToast(activity, mediaMap.get(platform) + " 分享失败啦", true);
            if (t != null) {
                Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            SCToastUtil.showToast(activity, platform + " 分享取消了", true);
        }
    };

    /**
     * 实现文本复制功能
     *
     * @param content 要复制的内容
     */

    private void copy(String content) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) activity
                .getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
        SCToastUtil.showToast(activity, "复制成功.", true);
    }
}
