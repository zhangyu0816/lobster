package com.zb.lib_base.emojj;

import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.EditText;

public class EmojiUtil {

    public static void setProhibitEmoji(EditText et) {
        InputFilter[] filters = {getInputFilterProhibitEmoji()};
        et.setFilters(filters);
    }

    private static InputFilter getInputFilterProhibitEmoji() {
        return (source, start, end, dest, dstart, dend) -> {
            StringBuffer buffer = new StringBuffer();
            for (int i = start; i < end; i++) {
                char codePoint = source.charAt(i);
                if (!getIsEmoji(codePoint)) {
                    buffer.append(codePoint);
                } else {
                    i++;
                }
            }
            if (source instanceof Spanned) {
                SpannableString sp = new SpannableString(buffer);
                TextUtils.copySpansFrom((Spanned) source, start, end, null, sp, 0);
                return sp;
            } else {
                return buffer;
            }
        };
    }

    private static boolean getIsEmoji(char codePoint) {
        return codePoint != 0x0 && codePoint != 0x9 && codePoint != 0xA && codePoint != 0xD && (codePoint < 0x20 || codePoint > 0xD7FF) && (codePoint < 0xE000 || codePoint > 0xFFFD);
    }

}
