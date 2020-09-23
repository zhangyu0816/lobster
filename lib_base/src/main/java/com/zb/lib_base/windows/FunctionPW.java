package com.zb.lib_base.windows;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

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

public class FunctionPW extends BasePopupWindow {
    public BaseAdapter topAdapter;
    public BaseAdapter bottomAdapter;

    private List<ShareItem> shareItemList = new ArrayList<>();
    private List<ShareItem> shareItemBottomList = new ArrayList<>();
    private boolean isMine = false;
    public boolean isVideo = false;
    private boolean isList = false;
    private UMImage umImage;
    private String sharedName;
    private String content;
    private String sharedUrl;
    private UMWeb web;
    private SHARE_MEDIA media;
    private Map<SHARE_MEDIA, String> mediaMap = new HashMap<>();
    private CallBack callBack;
    private boolean isDiscover;

    public FunctionPW(View parentView, String logo, String sharedName, String content, String sharedUrl,
                      boolean isMine, boolean isVideo, boolean isDiscover, boolean isList, CallBack callBack) {
        super(parentView, true);
        this.umImage = new UMImage(activity, logo);
        this.sharedName = sharedName;
        this.content = content.isEmpty() ? "这里藏着喜欢你的人" : content;
        this.sharedUrl = sharedUrl;
        this.isMine = isMine;
        this.isVideo = isVideo;
        this.isList = isList;
        this.isDiscover = isDiscover;
        this.callBack = callBack;
        initUI();
    }

    @Override
    public int getRes() {
        return R.layout.pws_function;
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
        topAdapter = new BaseAdapter<>(activity, R.layout.item_share, shareItemList, this);

        if (isMine) {
            if (isDiscover) {
                shareItemBottomList.add(new ShareItem(isVideo ? R.mipmap.share_gift_ico : R.mipmap.share_gift_white_ico, "查看礼物"));
                if (!isList)
                    shareItemBottomList.add(new ShareItem(isVideo ? R.mipmap.share_delete_ico : R.mipmap.share_delete_white_ico, "删除动态"));
            }
            shareItemBottomList.add(new ShareItem(isVideo ? R.mipmap.share_copy_ico : R.mipmap.share_copy_white_ico, "复制链接"));
        } else {
            shareItemBottomList.add(new ShareItem(isVideo ? R.mipmap.share_report_ico : R.mipmap.share_report_white_ico, "举报"));
            if (isVideo)
                shareItemBottomList.add(new ShareItem(R.mipmap.share_download_ico, "保存至相册"));
            shareItemBottomList.add(new ShareItem(isVideo ? R.mipmap.share_copy_ico : R.mipmap.share_copy_white_ico, "复制链接"));
            shareItemBottomList.add(new ShareItem(isVideo ? R.mipmap.share_like_ico : R.mipmap.share_like_white_ico, "超级喜欢"));
        }

        bottomAdapter = new BaseAdapter<>(activity, R.layout.item_share_bottom, shareItemBottomList, this);
        mBinding.setVariable(BR.isVideo, isVideo);
        mBinding.setVariable(BR.pw, this);
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
        }
        // 微信
        new ShareAction(activity).setPlatform(media)
                .withMedia(web)
                .setCallback(umShareListener)
                .share();
    }

    public void selectBottom(ShareItem shareItem) {
        dismiss();
        if (TextUtils.equals(shareItem.getShareName(), "查看礼物")) {
            callBack.gift();
        } else if (TextUtils.equals(shareItem.getShareName(), "删除动态")) {
            callBack.delete();
        } else if (TextUtils.equals(shareItem.getShareName(), "举报")) {
            callBack.report();
        } else if (TextUtils.equals(shareItem.getShareName(), "保存至相册")) {
            callBack.download();
        } else if (TextUtils.equals(shareItem.getShareName(), "复制链接")) {
            copy(sharedUrl);
        } else if (TextUtils.equals(shareItem.getShareName(), "超级喜欢")) {
            callBack.like();
        }
    }

    public interface CallBack {
        void gift();

        void delete();

        void report();

        void download();

        void like();
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
