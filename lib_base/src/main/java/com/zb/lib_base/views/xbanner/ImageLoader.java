package com.zb.lib_base.views.xbanner;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.zb.lib_base.model.Ads;

/**
 * Created by Abby on 9/22/2017.
 */

@FunctionalInterface
public interface ImageLoader {
    void loadImages(Context context, Ads ads, ImageView image, int position);

    default void loadVideoViews(Context context, Ads ads, VideoView videoView) {

    }

    default void loadView(LinearLayout linearLayout, View adView) {

    }
}
