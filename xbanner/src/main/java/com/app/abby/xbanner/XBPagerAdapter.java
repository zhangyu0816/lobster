package com.app.abby.xbanner;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

/**
 * Created by DIY on 2017-12-18.
 */

public class XBPagerAdapter extends PagerAdapter {
    List<View> mData;
    BannerPageListener mBannerPageListner;
    int mImageCount;
    View view;

    public XBPagerAdapter(BannerPageListener listener, int imagecount) {
        mData = new ArrayList<>();
        mBannerPageListner = listener;
        mImageCount = imagecount;
    }

    /**
     * Add data,will not clear the data already exists
     *
     * @param data the ImageViews to be added
     */
    public void addData(List<View> data) {
        if (mData != null) {
            mData.addAll(data);
            notifyDataSetChanged();
        }
    }

    /**
     * Reset the data
     */
    public void setData(List<View> data) {

        if (mData != null) {
            mData.clear();
            mData.addAll(data);
            notifyDataSetChanged();
        }

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


    @Override
    public int getCount() {
        return mData.size();
    }


    @NonNull
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        try {
            container.addView(mData.get(position));
        } catch (IllegalStateException e) {
            if (mData.get(position).getParent() != null) {
                ((ViewGroup) mData.get(position).getParent()).removeView(mData.get(position));
            }
            container.addView(mData.get(position));
        }


        view = mData.get(position);
        view.setOnClickListener(v -> {
            if (mBannerPageListner != null) {
                mBannerPageListner.onBannerClick(getTruePos(position));
            }
        });
        return mData.get(position);
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

    }

    private int getTruePos(int pos) {
        //get the position of the indicator
        int truepos = (pos - 1) % mImageCount;
        if (truepos < 0) {
            truepos = mImageCount - 1;
        }
        return truepos;
    }

    public void releaseAdapter() {

        mBannerPageListner = null;
        if (mData != null && mData.size() > 0) {
            for (int i = 0; i < mData.size(); i++) {
                mData.get(i).setOnClickListener(null);
            }
            mData.clear();
        }

        view.setOnClickListener(null);
        view = null;
    }

    @FunctionalInterface
    public interface BannerPageListener {
        void onBannerClick(int item);

        default void onBannerDragging(int item) {
        }

        default void onBannerIdle(int item) {
        }
    }
}
