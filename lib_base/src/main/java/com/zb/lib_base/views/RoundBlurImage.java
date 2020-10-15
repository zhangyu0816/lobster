package com.zb.lib_base.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.zb.lib_base.R;
import com.zb.lib_base.adapter.AdapterBinding;
import com.zb.lib_base.databinding.RoundBlurImageBinding;
import com.zb.lib_base.utils.glide.BlurTransformation;
import com.zb.lib_base.utils.glide.GlideRoundTransform;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

public class RoundBlurImage extends RelativeLayout {
    private static RoundBlurImageBinding mBinding;

    public RoundBlurImage(Context context) {
        super(context);
        init(context);
    }

    public RoundBlurImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RoundBlurImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.round_blur_image, null, false);
        addView(mBinding.getRoot());
    }

    @BindingAdapter(value = {"rbUrl", "isBlur", "roundSize", "viewWidthSize", "viewHeightSize"}, requireAll = false)
    public static void roundBlurImage(RoundBlurImage view, String rbUrl, boolean isBlur, int roundSize, int viewWidthSize, int viewHeightSize) {
        AdapterBinding.viewSize(view, viewWidthSize, viewHeightSize);
        AdapterBinding.viewSize(mBinding.ivRoundBlur, viewWidthSize, viewHeightSize);
        RequestOptions cropOptions = new RequestOptions();
        MultiTransformation<Bitmap> multiTransformation;
        if (isBlur) {
            multiTransformation = new MultiTransformation<>(new CenterCrop(), new BlurTransformation(), new GlideRoundTransform(roundSize, 0));
        } else {
            multiTransformation = new MultiTransformation<>(new CenterCrop(), new GlideRoundTransform(roundSize, 0));
        }
        cropOptions.transform(multiTransformation);
        Glide.with(view.getContext()).asBitmap().load(rbUrl).apply(cropOptions).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                mBinding.ivRoundBlur.setImageBitmap(resource);
            }
        });
    }
}
