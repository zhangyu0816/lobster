package com.zb.lib_base.emojj;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;

class EmojiSpan extends DynamicDrawableSpan {
	private final Context mContext;
	private final int mResourceId;
	private Drawable mDrawable;
	private int mSize = 0;

	public EmojiSpan(Context context, int resourceId, int size) {
		super();
		mContext = context;
		mResourceId = resourceId;
		mSize = size;
	}

	@SuppressLint("UseCompatLoadingForDrawables")
	public Drawable getDrawable() {
		if (mDrawable == null) {
			try {
				mDrawable = mContext.getResources().getDrawable(mResourceId);
				int size = mSize;
				mDrawable.setBounds(0, 0, size, size);
			} catch (Exception ignored) {
			}
		}
		return mDrawable;
	}
}