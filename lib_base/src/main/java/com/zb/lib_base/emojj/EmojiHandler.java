package com.zb.lib_base.emojj;

import android.content.Context;
import android.text.Spannable;

import com.zb.lib_base.R;
import com.zb.lib_base.app.MineApp;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class EmojiHandler {
    private EmojiHandler() {

    }

    public static int maxEmojiCount = 88;
    public static final Map<Integer, Integer> sCustomizeEmojisMap = new HashMap<>();

    private static int getPic(String pid) {
        Field f;
        try {
            f = R.mipmap.class.getField(pid);
            return f.getInt(null);
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return 0;
    }

    static {
        // Customize
        for (int i = 1; i < maxEmojiCount; i++) {
            sCustomizeEmojisMap.put(i, getPic("emoji_" + i));
        }
    }

    @SuppressWarnings("rawtypes")
    public static int getCusEmojisResource(Integer index) {
        int key = 0;
        Set set = ((Map) sCustomizeEmojisMap).entrySet();
        for (Object o : set) {
            Map.Entry entry = (Map.Entry) o;
            if (entry.getValue().equals(index)) {
                key = (Integer) entry.getKey();
            }
        }
        return key;
    }

    private static int cusEmojisResource(int key) {
        return sCustomizeEmojisMap.get(key);
    }

    /**
     * Convert emoji characters of the given Spannable to the according
     * emojicon.
     *
     * @param context
     * @param text
     * @param emojiSize
     */
    public static void addEmojis(Context context, Spannable text, int emojiSize) {
        int length = text.length();
        EmojiSpan[] oldSpans = text.getSpans(0, length, EmojiSpan.class);
        for (EmojiSpan oldSpan : oldSpans) {
            text.removeSpan(oldSpan);
        }

        String m = "\\{f:\\d+\\}";
        Matcher match = Pattern.compile(m).matcher(text.toString());
        while (match.find()) {
            String mSr = match.group();
            int start = mSr.indexOf("{f:");
            int end = mSr.indexOf("}");
            String content;
            if (start > -1) {
                content = mSr.substring(3, end);
                int icon = cusEmojisResource(Integer.parseInt(content));
                if (icon > 0) {
                    text.setSpan(new EmojiSpan(context, icon,
                                    (int) (MineApp.W * (60f / 1080f))), match
                                    .start(), match.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }

    }
}
