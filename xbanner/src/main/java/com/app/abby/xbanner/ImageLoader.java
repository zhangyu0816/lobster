package com.app.abby.xbanner;

import android.content.Context;
import android.widget.ImageView;
import android.widget.VideoView;

/**
 * Created by Abby on 9/22/2017.
 */

@FunctionalInterface
public interface ImageLoader {
    void loadImages(Context context, Ads ads, ImageView image, int position);

    default void loadVideoViews(Context context, Ads ads, VideoView videoView) {

    }

    default void getPosition(int position) {

    }
}
