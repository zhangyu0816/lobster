package com.zb.lib_base.emojj;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.widget.TextView;

import com.zb.lib_base.R;

import androidx.appcompat.widget.AppCompatTextView;


public class EmojiTextView extends AppCompatTextView {
    private int mEmojiconSize;

    public EmojiTextView(Context context) {
        super(context);
        init(null);
    }

    public EmojiTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EmojiTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs == null) {
            mEmojiconSize = (int) getTextSize();
        } else {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Emoji);
            mEmojiconSize = (int) a.getDimension(R.styleable.Emoji_emojiSize, getTextSize());
            a.recycle();
        }
        setText(getText());
    }

    @Override
    public void setText(CharSequence text, TextView.BufferType type) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        EmojiHandler.addEmojis(getContext(), builder, mEmojiconSize);
        super.setText(builder, type);
    }

    /**
     * Set the size of emojicon in pixels.
     */
    public void setEmojiconSize(int pixels) {
        mEmojiconSize = pixels;
    }
}
