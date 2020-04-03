package com.zb.lib_base.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.util.DisplayMetrics;

/**
 * [简要描述]: 屏幕显示相关 帮助类 [详细描述]:
 * 
 * @author [Jarry]
 * @date [Created 2014-2-24]
 * @see [DisplayUtils]
 * @package [com.easycity.manager.utils]
 * @since [EasyCityManager]
 */
public class DisplayUtils {

	public static DisplayMetrics metrics;

	/**
	 * App启动时初始化
	 */
	public static void init(Context context) {
		metrics = context.getResources().getDisplayMetrics();
	}

	/**
	 * [简要描述]: Display Density [详细描述]:
	 * 
	 * @author [Jarry]
	 * @date [Modified 2013-8-13]
	 * @method [getDensity]
	 * @return
	 * @retruntype [float]
	 * @exception
	 */
	public static float getDensity() {
		return metrics.density;
	}

	/**
	 * [简要描述]: 获取屏幕宽度（像素值） [详细描述]:
	 * 
	 * @author [Jarry]
	 * @date [Modified 2013-8-13]
	 * @method [getScreenWidth]
	 * @return
	 * @retruntype [int]
	 * @exception
	 */
	public static int getScreenWidth() {
		return metrics.widthPixels;
	}

	/**
	 * [简要描述]: 获取屏幕宽度（Dip值） [详细描述]:
	 * 
	 * @author [Jarry]
	 * @date [Modified 2013-8-13]
	 * @method [getWidthDip]
	 * @return
	 * @retruntype [float]
	 * @exception
	 */
	public static float getWidthDip() {
		return (getScreenWidth() / getDensity());
	}

	/**
	 * [简要描述]: 获取屏幕高度（像素值） [详细描述]:
	 * 
	 * @author [Jarry]
	 * @date [Modified 2013-8-13]
	 * @method [getScreenHeight]
	 * @return
	 * @retruntype [int]
	 * @exception
	 */
	public static int getScreenHeight() {
		return metrics.heightPixels;
	}

	/**
	 * [简要描述]: 获取屏幕高度（Dip值） [详细描述]:
	 * 
	 * @author [Jarry]
	 * @date [Modified 2013-8-13]
	 * @method [getHeightDip]
	 * @return
	 * @retruntype [float]
	 * @exception
	 */
	public static float getHeightDip() {
		return (getScreenHeight() / getDensity());
	}

	/**
	 * [简要描述]: 获取Dip值 [Int] [详细描述]:
	 * 
	 * @author [Jarry]
	 * @date [Modified 2013-8-13]
	 * @method [getIntDip]
	 * @param value
	 * @return
	 * @retruntype [int]
	 * @exception
	 */
	public static int getIntDip(int value) {
		return (int) (metrics.density * value);
	}

	/**
	 * [简要描述]: 获取Dip值 [Float] [详细描述]:
	 * 
	 * @author [Jarry]
	 * @date [Modified 2013-8-13]
	 * @method [getFloatDip]
	 * @param i
	 * @return
	 * @retruntype [float]
	 * @exception
	 */
	public static float getFloatDip(float i) {
		return (metrics.density * i);
	}

	/**
	 * [简要描述]: 显示文本 [详细描述]:
	 * 
	 * @author [Jarry]
	 * @date [Modified 2013-8-13]
	 * @method [drawText]
	 * @param canvas
	 * @param text
	 * @param left
	 * @param right
	 * @param top
	 * @param bottom
	 * @param paint
	 * @retruntype [void]
	 * @exception
	 */
	public static void drawText(Canvas canvas, String text, int left,
                                int right, int top, int bottom, Paint paint) {
		FontMetrics fm = paint.getFontMetrics();
		int y = top + (int) (bottom - top - fm.ascent) / 2;
		if (bottom <= top) {
			int h = (int) (Math.ceil(fm.descent - fm.top) + 2);
			y = top + (int) (h - fm.ascent) / 2;
		}

		Paint.Align align = paint.getTextAlign();
		if (align == Paint.Align.LEFT) {
			canvas.drawText(text, left, y, paint);
		} else if (align == Paint.Align.RIGHT) {
			canvas.drawText(text, right, y, paint);
		} else if (align == Paint.Align.CENTER) {
			canvas.drawText(text, (left + right) >> 1, y, paint);
		}
	}

	/**
	 * [简要描述]: 显示文本 [详细描述]:
	 * 
	 * @author [Jarry]
	 * @date [Modified 2013-8-13]
	 * @method [drawText]
	 * @param canvas
	 * @param text
	 * @param rect
	 * @param paint
	 * @retruntype [void]
	 * @exception
	 */
	public static void drawText(Canvas canvas, String text, Rect rect,
                                Paint paint) {
		FontMetrics fm = paint.getFontMetrics();
		int y = rect.top + (int) (rect.bottom - rect.top - fm.ascent) / 2;

		Paint.Align align = paint.getTextAlign();
		if (align == Paint.Align.LEFT) {
			canvas.drawText(text, rect.left, y, paint);
		} else if (align == Paint.Align.RIGHT) {
			canvas.drawText(text, rect.right, y, paint);
		} else if (align == Paint.Align.CENTER) {
			canvas.drawText(text, (rect.left + rect.right) >> 1, y, paint);
		}
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
