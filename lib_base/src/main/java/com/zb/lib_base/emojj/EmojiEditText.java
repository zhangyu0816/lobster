
package com.zb.lib_base.emojj;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.zb.lib_base.R;

import java.util.Objects;

import androidx.appcompat.widget.AppCompatEditText;


public class EmojiEditText extends AppCompatEditText {
    private int mEmojiconSize;

    public EmojiEditText(Context context) {
        super(context);
        mEmojiconSize = (int) getTextSize();

    }

    public EmojiEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EmojiEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Emoji);
        mEmojiconSize = (int) a.getDimension(R.styleable.Emoji_emojiSize, getTextSize());
        a.recycle();
        setText(getText());
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        EmojiHandler.addEmojis(getContext(), Objects.requireNonNull(getText()), mEmojiconSize);
    }

    /**
     * Set the size of emojicon in pixels.
     */
    public void setEmojiconSize(int pixels) {
        mEmojiconSize = pixels;
    }
}
