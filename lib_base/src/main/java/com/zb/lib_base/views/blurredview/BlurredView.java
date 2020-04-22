package com.zb.lib_base.views.blurredview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zb.lib_base.R;


/**
 * @author Qiushui
 */

public class BlurredView extends RelativeLayout {

    /**
     * 模糊最大化值
     */
    private static final int ALPHA_MAX_VALUE = 255;

    /**
     * Context
     */
    private Context mContext;

    /**
     * 模糊后的ImageView
     */
    private ImageView mBlurredImg;

//    /**
//     * 原图ImageView
//     */
//    private ImageView mOriginImg;
    /**
     * 原图Bitmap
     */
    private Bitmap mOriginBitmap;

    /**
     * 模糊后的Bitmap
     */
    private Bitmap mBlurredBitmap;

    /**
     * 是否移动背景图片
     */
    private boolean isMove;

    public BlurredView(Context context) {
        super(context);
        init(context);
    }

    public BlurredView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        initAttr(context, attrs);
    }

    public BlurredView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        initAttr(context, attrs);
    }

    private void init(Context context) {
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.blurredview, this);
        mBlurredImg = (ImageView) findViewById(R.id.blurredview_blurred_img);
    }

    private void initAttr(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BlurredView);
        Drawable drawable = typedArray.getDrawable(R.styleable.BlurredView_src);
        isMove = typedArray.getBoolean(R.styleable.BlurredView_move, false);

        typedArray.recycle();

        // blur image
        setBlurredImg(drawable);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setImageView();
    }

    /**
     * 填充ImageView.
     */
    private void setImageView() {
        mBlurredImg.setImageBitmap(mBlurredBitmap);
    }

    /**
     * 以代码的方式添加待模糊的图片
     *
     * @param blurredBitmap 待模糊的图片
     */
    public void setBlurredImg(Bitmap blurredBitmap) {
        if (null != blurredBitmap) {
            mOriginBitmap = blurredBitmap;
            mBlurredBitmap = BlurBitmap.blur(mContext, blurredBitmap);
            setImageView();
        }
    }

    /**
     * 以代码的方式添加待模糊的图片
     *
     * @param blurDrawable 待模糊的图片
     */
    public void setBlurredImg(Drawable blurDrawable) {
        if (null != blurDrawable) {
            mOriginBitmap = drawableToBitmap(blurDrawable);
            mBlurredBitmap = BlurBitmap.blur(mContext, mOriginBitmap);
            setImageView();
        }
    }

    /**
     * 将Drawable对象转化为Bitmap对象
     *
     * @param drawable  Drawable对象
     * @return          对应的Bitmap对象
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1,
                    Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}
