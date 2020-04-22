package com.zb.lib_base.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

public class RoundImageView extends androidx.appcompat.widget.AppCompatImageView {

    float width, height;
    int roundSize = 28;

    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (Build.VERSION.SDK_INT < 18) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (width > roundSize && height > roundSize) {
            Path path = new Path();
            path.moveTo(roundSize, 0);
            path.lineTo(width - roundSize, 0);
            path.quadTo(width, 0, width, roundSize);
            path.lineTo(width, height - roundSize);
            path.quadTo(width, height, width - roundSize, height);
            path.lineTo(roundSize, height);
            path.quadTo(0, height, 0, height - roundSize);
            path.lineTo(0, roundSize);
            path.quadTo(0, 0, roundSize, 0);
            canvas.clipPath(path);
        }

        super.onDraw(canvas);
    }
}