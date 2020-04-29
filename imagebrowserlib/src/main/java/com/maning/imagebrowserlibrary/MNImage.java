package com.maning.imagebrowserlibrary;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.maning.imagebrowserlibrary.model.ImageBrowserConfig;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.ArrayList;

public class MNImage {
    public static void imageBrowser(final RxAppCompatActivity mContext, final View view, ArrayList<String> sourceImageList, int index) {
        MNImageBrowser.with(mContext)
                .setTransformType(ImageBrowserConfig.TransformType.Transform_DepthPage)
                .setIndicatorType(ImageBrowserConfig.IndicatorType.Indicator_Circle)
                .setCurrentPosition(index)
                .setImageEngine(new ImageEngine() {
                    @Override
                    public void loadImage(Context context, String url, ImageView imageView) {
                        RequestOptions cropOptions = new RequestOptions().fitCenter();
                        Glide.with(context).asBitmap().apply(cropOptions).load(url).into(imageView);
                    }
                })
                .setImageList(sourceImageList)
                .show(view);
    }
}
