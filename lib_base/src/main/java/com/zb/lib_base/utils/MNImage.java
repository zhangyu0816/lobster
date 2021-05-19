package com.zb.lib_base.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.maning.imagebrowserlibrary.Discover;
import com.maning.imagebrowserlibrary.model.ImageBrowserConfig;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zb.lib_base.activity.MNImageBrowser;
import com.zb.lib_base.model.DiscoverInfo;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MNImage {
    public static void imageBrowser(final RxAppCompatActivity mContext, final View view, long otherUserId, ArrayList<String> sourceImageList, int index, boolean showDelete, final CallBack callBack) {
        final int W = mContext.getResources().getDisplayMetrics().widthPixels;
        final int H = mContext.getResources().getDisplayMetrics().heightPixels;
        MNImageBrowser.with(mContext)
                .setTransformType(ImageBrowserConfig.TransformType.Transform_DepthPage)
                .setIndicatorType(ImageBrowserConfig.IndicatorType.Indicator_Number)
                .setCurrentPosition(index)
                .setImageEngine((context, url, imageView) -> {
                    RequestOptions cropOptions = new RequestOptions().fitCenter();
                    Glide.with(context).asBitmap().load(url).apply(cropOptions).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            int width = resource.getWidth();
                            int height = resource.getHeight();
                            ViewGroup.LayoutParams para = imageView.getLayoutParams();
                            if (width >= height) {
                                para.width = W;
                                para.height = (int) ((float) resource.getHeight() * W / (float) resource.getWidth());
                            } else {
                                para.width = (int) ((float) resource.getWidth() * H / (float) resource.getHeight());
                                para.height = H;
                            }
                            imageView.setLayoutParams(para);
                            imageView.setImageBitmap(resource);
                        }
                    });
                })
                .setOnDeleteImageListener(position -> {
                    if (callBack != null)
                        callBack.deleteImage(position);
                })
                .setImageList(sourceImageList)
                .setShowDelete(showDelete)
                .setOtherUserId(otherUserId)
                .show(view);
    }

    public static void imageBrowser(final RxAppCompatActivity mContext, final View view, ArrayList<String> sourceImageList, int index, DiscoverInfo discoverInfo, boolean isAttention, boolean isGood, final OnDiscoverListener onDiscoverListener) {
        final int W = mContext.getResources().getDisplayMetrics().widthPixels;
        final int H = mContext.getResources().getDisplayMetrics().heightPixels;
        Discover discover = new Discover(discoverInfo.getFriendDynId(), discoverInfo.getOtherUserId(), discoverInfo.getUserId(),
                discoverInfo.getNick(), discoverInfo.getImage(), discoverInfo.getText(), discoverInfo.getCreateTime(),
                discoverInfo.getGoodNum(), discoverInfo.getReviews());
        MNImageBrowser.with(mContext)
                .setTransformType(ImageBrowserConfig.TransformType.Transform_DepthPage)
                .setIndicatorType(ImageBrowserConfig.IndicatorType.Indicator_Number)
                .setCurrentPosition(index)
                .setImageEngine((context, url, imageView) -> {
                    RequestOptions cropOptions = new RequestOptions().fitCenter();
                    Glide.with(context).asBitmap().load(url).apply(cropOptions).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            int width = resource.getWidth();
                            int height = resource.getHeight();
                            ViewGroup.LayoutParams para = imageView.getLayoutParams();
                            if (width >= height) {
                                para.width = W;
                                para.height = (int) ((float) resource.getHeight() * W / (float) resource.getWidth());
                            } else {
                                para.width = (int) ((float) resource.getWidth() * H / (float) resource.getHeight());
                                para.height = H;
                            }
                            imageView.setLayoutParams(para);
                            imageView.setImageBitmap(resource);
                        }
                    });
                })
                .setImageList(sourceImageList)
                .setDiscover(discover)
                .isAttention(isAttention)
                .isGood(isGood)
                .setOnDiscoverListener(new com.maning.imagebrowserlibrary.listeners.OnDiscoverListener() {
                    @Override
                    public void attention() {
                        onDiscoverListener.attention();
                    }

                    @Override
                    public void good() {
                        onDiscoverListener.good();
                    }

                    @Override
                    public void review() {
                        onDiscoverListener.review();
                    }

                    @Override
                    public void share() {
                        onDiscoverListener.share();
                    }
                })
                .setOtherUserId(discoverInfo.getUserId())
                .show(view);
    }

    public interface CallBack {
        void deleteImage(int position);
    }

    public interface OnDiscoverListener {

        void attention();

        void good();

        void review();

        void share();
    }
}
