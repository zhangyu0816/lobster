package com.zb.lib_base.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.zb.lib_base.R;

public class CutImageView extends androidx.appcompat.widget.AppCompatImageView {
    /**
     * 图片的大小
     */
    private int mBitmapWidth;
    private int mBitmapHeight;
    /**
     * 控件的大小     *     * @param context
     */
    private int mWidth;
    private int mHeight;
    /**
     * 图片在控件上的放大倍数
     */
    private double mScaleX;
    private double mScaleY;
    /**
     * 用来处理截图框的变换
     */
    private RectF mFloatRect;
    /**
     * 截图框的初始数值
     */
    private RectF mDefaultRect;
    /**
     * 截图框外部的默认颜色
     */
    private final int mDefaultColor = Color.parseColor("#30000000");
    /**
     * 点击和移动的点
     */
    private float mTouchX;
    private float mTouchY;
    private float mMoveX;
    private float mMoveY;
    /**
     * 一些常量
     */
    private static final int TOPLEFT = 0x0001;
    private static final int TOPRIGHT = 0x0002;
    private static final int BOTTOMLEFT = 0x0003;
    private static final int BOTTOMRIGHT = 0x0004;
    private static final int BOTHSIDE = 0x0005;
    //  private static final int ONESIDE = 0x0006;
    private static final int MOVE = 0x0006;
    /**
     * 判断截图框的改变方式是移动还是缩放
     */
    private int mChangeType;
    /**
     * 判断点击的四个角
     */
    private int mTouch;
    /**
     * xml属性
     */
    private int mFloatColor;

    public CutImageView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public CutImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CutImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CutView);
        if (null != typedArray) {
            mFloatColor = typedArray.getColor(R.styleable.CutView_floatColor, mDefaultColor);
            typedArray.recycle();
        }
    }

    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mBitmapWidth = bm.getWidth();
        mBitmapHeight = bm.getHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 计算控件的大小就不用说了
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == View.MeasureSpec.EXACTLY && heightMode == View.MeasureSpec.EXACTLY) {
            mWidth = View.MeasureSpec.getSize(widthMeasureSpec);
            mHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        } else {
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
            mWidth = getMeasuredWidth();
            mHeight = getMeasuredHeight();
        }
        if (0 != mBitmapWidth && 0 != mWidth) {
            setScale();
        }
    }

    /**
     * 计算bitmap在控件上的放大倍数目前只写了FIT_CENTER，     * 其它的可以自己写，不知有没有其它直接的方法获取
     */
    private void setScale() {
        if (ImageView.ScaleType.FIT_CENTER == getScaleType()) {
            double widthScale = (double) mWidth / (double) mBitmapWidth;
            double heightScale = (double) mHeight / (double) mBitmapHeight;
            if (mBitmapWidth <= mWidth && mBitmapHeight <= mHeight) {
                mScaleX = mScaleY = (widthScale > heightScale ? heightScale : widthScale);
            } else if (mBitmapWidth <= mWidth && mBitmapHeight > mHeight) {
                mScaleX = mScaleY = heightScale;
            } else if (mBitmapWidth > mWidth && mBitmapHeight <= mHeight) {
                mScaleX = mScaleY = widthScale;
            } else if (mBitmapWidth > mWidth && mBitmapHeight > mHeight) {
                mScaleX = mScaleY = (widthScale > heightScale ? heightScale : widthScale);
            }
            setFloatRect();
        }
    }

    /**
     * 计算截图框的大小
     */
    private void setFloatRect() {
        float width = mBitmapWidth * (float) mScaleX;
        float height = mBitmapHeight * (float) mScaleY;
        float newWidth = height * 345f / 510f;
        float newHeight = width * 510f / 345f;

        if(newWidth>width){
            newWidth = width;
        }else if(newHeight>height){
            newHeight = height;
        }


        float leftDefault = (mWidth - width) / 2;
        float topDefault = (mHeight - height) / 2;
        float rightDefault = leftDefault + width;
        float bottomDefault = topDefault + height;

        float left = (mWidth - newWidth) / 2;
        float top = (mHeight - newHeight) / 2;
        float right = left + newWidth;
        float bottom = top + newHeight;


        mFloatRect = new RectF(left, top, right, bottom);
        mDefaultRect = new RectF(leftDefault, topDefault, rightDefault, bottomDefault);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null != mFloatRect) {
            Paint paint = new Paint();
            paint.setColor(mFloatColor);
            // 绘制截图框外部部分
            canvas.save();
            // Region.Op.XOR 是异并集，这里是获取截图框外部部分
            canvas.clipRect(mFloatRect, Region.Op.XOR);
            canvas.drawColor(mFloatColor);
            canvas.restore();
            canvas.save();
            //Region.Op.INTERSECT 是交集
            canvas.clipRect(mFloatRect, Region.Op.INTERSECT);
            // 绘制截图框部分
            drawFloatRect(canvas);
            canvas.restore();
        }
    }

    /**
     * 绘制截图框部分     *     * @param canvas
     */
    private void drawFloatRect(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#ffffffff"));
        paint.setStyle(Paint.Style.FILL);

        int corWidth = 5;
        int corLength = 80;
        int corWidthSmall = 1;
        int corLengthSmall = mHeight;
        // 左上角
        canvas.drawRect(mFloatRect.left, mFloatRect.top, mFloatRect.left + corWidth, mFloatRect.top
                + corLength, paint);
        canvas.drawRect(mFloatRect.left, mFloatRect.top, mFloatRect.left
                + corLength, mFloatRect.top + corWidth, paint);
        // 右上角
        canvas.drawRect(mFloatRect.right - corWidth, mFloatRect.top, mFloatRect.right,
                mFloatRect.top + corLength, paint);
        canvas.drawRect(mFloatRect.right - corLength, mFloatRect.top,
                mFloatRect.right, mFloatRect.top + corWidth, paint);
        // 左下角
        canvas.drawRect(mFloatRect.left, mFloatRect.bottom - corLength,
                mFloatRect.left + corWidth, mFloatRect.bottom, paint);
        canvas.drawRect(mFloatRect.left, mFloatRect.bottom - corWidth, mFloatRect.left
                + corLength, mFloatRect.bottom, paint);
        // 右下角
        canvas.drawRect(mFloatRect.right - corWidth, mFloatRect.bottom - corLength,
                mFloatRect.right, mFloatRect.bottom, paint);
        canvas.drawRect(mFloatRect.right - corLength, mFloatRect.bottom - corWidth,
                mFloatRect.right, mFloatRect.bottom, paint);

        // 细线
        // 左上角
        canvas.drawRect(mFloatRect.left, mFloatRect.top, mFloatRect.left + corWidthSmall, mFloatRect.top
                + corLengthSmall, paint);
        canvas.drawRect(mFloatRect.left, mFloatRect.top, mFloatRect.left
                + corLengthSmall, mFloatRect.top + corWidthSmall, paint);
        // 右上角
        canvas.drawRect(mFloatRect.right - corWidthSmall, mFloatRect.top, mFloatRect.right,
                mFloatRect.top + corLengthSmall, paint);
        canvas.drawRect(mFloatRect.right - corLengthSmall, mFloatRect.top,
                mFloatRect.right, mFloatRect.top + corWidthSmall, paint);
        // 左下角
        canvas.drawRect(mFloatRect.left, mFloatRect.bottom - corLengthSmall,
                mFloatRect.left + corWidthSmall, mFloatRect.bottom, paint);
        canvas.drawRect(mFloatRect.left, mFloatRect.bottom - corWidthSmall, mFloatRect.left
                + corLengthSmall, mFloatRect.bottom, paint);
        // 右下角
        canvas.drawRect(mFloatRect.right - corWidthSmall, mFloatRect.bottom - corLengthSmall,
                mFloatRect.right, mFloatRect.bottom, paint);
        canvas.drawRect(mFloatRect.right - corLengthSmall, mFloatRect.bottom - corWidthSmall,
                mFloatRect.right, mFloatRect.bottom, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchX = event.getX();
                mTouchY = event.getY();
                jugmentChangeType();
                break;
            case MotionEvent.ACTION_UP:
                mChangeType = 0;
                mTouch = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveX = event.getX();
                mMoveY = event.getY();
                setFloatRectChange();
                mTouchX = mMoveX;
                mTouchY = mMoveY;
                break;
        }
        return true;
    }

    /**
     * 判断截图框的改变方式，若是缩放需要判断点击的是哪个角
     */
    private void jugmentChangeType() {
        RectF rectF1 = new RectF(mFloatRect.left + 50, mFloatRect.top, mFloatRect.right - 50, mFloatRect.bottom);
        RectF rectF2 = new RectF(mFloatRect.left, mFloatRect.top + 50, mFloatRect.right, mFloatRect.bottom - 50);
        if (rectF1.contains(mTouchX, mTouchY) || rectF2.contains(mTouchX, mTouchY)) {
            mChangeType = MOVE;
        }
//        else {
//            mChangeType = BOTHSIDE;
//            RectF rectFLeftTop = new RectF(mFloatRect.left - 50, mFloatRect.top - 50, mFloatRect.left + 50, mFloatRect.top + 50);
//            RectF rectFRightTop = new RectF(mFloatRect.right - 50, mFloatRect.top - 50, mFloatRect.right + 50, mFloatRect.top + 50);
//            RectF rectFLeftBottom = new RectF(mFloatRect.left - 50, mFloatRect.bottom - 50, mFloatRect.left + 50, mFloatRect.bottom + 50);
//            RectF rectFRightBottom = new RectF(mFloatRect.right - 50, mFloatRect.bottom - 50, mFloatRect.right + 50, mFloatRect.bottom + 50);
//            if (rectFLeftTop.contains(mTouchX, mTouchY)) {
//                mTouch = TOPLEFT;
//            } else if (rectFRightTop.contains(mTouchX, mTouchY)) {
//                mTouch = TOPRIGHT;
//            } else if (rectFLeftBottom.contains(mTouchX, mTouchY)) {
//                mTouch = BOTTOMLEFT;
//            } else if (rectFRightBottom.contains(mTouchX, mTouchY)) {
//                mTouch = BOTTOMRIGHT;
//            }
//        }
    }

    /**
     * 截图框的数值变换
     */
    private void setFloatRectChange() {
        if (mChangeType == MOVE) {
            float x = mMoveX - mTouchX;
            float y = mMoveY - mTouchY;
            if (mDefaultRect.left <= mFloatRect.left + x && mDefaultRect.right >= mFloatRect.right + x) {
                mFloatRect.left += x;
                mFloatRect.right += x;
            } else if (mDefaultRect.left > mFloatRect.left + x && mDefaultRect.right >= mFloatRect.right + x) {
                float offx = mDefaultRect.left - mFloatRect.left;
                mFloatRect.left = mDefaultRect.left;
                mFloatRect.right += offx;
            } else if (mDefaultRect.left <= mFloatRect.left + x && mDefaultRect.right < mFloatRect.right + x) {
                float offx = mDefaultRect.right - mFloatRect.right;
                mFloatRect.right = mDefaultRect.right;
                mFloatRect.left += offx;
            }
            if (mDefaultRect.top <= mFloatRect.top + y && mDefaultRect.bottom >= mFloatRect.bottom + y) {
                mFloatRect.bottom += y;
                mFloatRect.top += y;
            } else if (mDefaultRect.top > mFloatRect.top + y && mDefaultRect.bottom >= mFloatRect.bottom + y) {
                float offy = mDefaultRect.top - mFloatRect.top;
                mFloatRect.top = mDefaultRect.top;
                mFloatRect.bottom += offy;
            } else if (mDefaultRect.top <= mFloatRect.top + y && mDefaultRect.bottom < mFloatRect.bottom + y) {
                float offy = mDefaultRect.bottom - mFloatRect.bottom;
                mFloatRect.bottom = mDefaultRect.bottom;
                mFloatRect.top += offy;
            }
        } else if (mChangeType == BOTHSIDE) {
            float x = mMoveX - mTouchX;
            float y = mMoveY - mTouchY;
            if (mTouch == TOPLEFT) {
                if (mDefaultRect.left <= mFloatRect.left + x && mFloatRect.right >= mFloatRect.left + x)
                    mFloatRect.left += x;
                if (mDefaultRect.top <= mFloatRect.top + y && mFloatRect.bottom >= mFloatRect.top + y)
                    mFloatRect.top += y;
            } else if (mTouch == TOPRIGHT) {
                if (mDefaultRect.right >= mFloatRect.right + x && mFloatRect.left <= mFloatRect.right + x)
                    mFloatRect.right += x;
                if (mDefaultRect.top <= mFloatRect.top + y && mFloatRect.bottom >= mFloatRect.top + y)
                    mFloatRect.top += y;
            } else if (mTouch == BOTTOMLEFT) {
                if (mDefaultRect.left <= mFloatRect.left + x && mFloatRect.right >= mFloatRect.left + x)
                    mFloatRect.left += x;
                if (mDefaultRect.bottom >= mFloatRect.bottom + y && mFloatRect.top <= mFloatRect.bottom + y)
                    mFloatRect.bottom += y;
            } else if (mTouch == BOTTOMRIGHT) {
                if (mDefaultRect.right >= mFloatRect.right + x && mFloatRect.left <= mFloatRect.right + x)
                    mFloatRect.right += x;
                if (mDefaultRect.bottom >= mFloatRect.bottom + y && mFloatRect.top <= mFloatRect.bottom + y)
                    mFloatRect.bottom += y;
            }
        }
        invalidate();
    }

    /**
     * drawable转化为bitmap     *     * @param drawable     * @return
     */
    public Bitmap creatBitmapFromDrawable(Drawable drawable) {
        if (null == drawable) return null;
        if (drawable instanceof BitmapDrawable) return ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicHeight(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 获取截图的bitmap 这个方法是在图片的原图上处理的，也可以通过截图的方式，不过截图的方式     * 获取的图片大小是有偏差的。     *     * @return
     */
    public Bitmap getCutBitmap() {
        Drawable drawable = getDrawable();
        if (null == drawable) return null;
        Bitmap bitmap = creatBitmapFromDrawable(drawable);
        // 计算截图框在原图片所在的位置大小，通过比例可以算出
        float left = (mFloatRect.left - mDefaultRect.left) / (float) mScaleX;
        float top = (mFloatRect.top - mDefaultRect.top) / (float) mScaleY;
        float width = mFloatRect.width() / (float) mScaleX;
        float height = mFloatRect.height() / (float) mScaleY;
        Bitmap dstBitmap = Bitmap.createBitmap(bitmap, (int) left, (int) top, (int) width, (int) height);
        return dstBitmap;
    }
}

