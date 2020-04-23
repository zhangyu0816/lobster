package com.zb.lib_base.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Preferences value read and write
 *
 * @author jarry
 */
public class PreferenceUtil {

    /**
     * read String value from Preferences
     *
     * @param context Context
     * @param key     value key
     * @return
     */
    public static String readStringValue(Context context, String key) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("test", Context.MODE_MULTI_PROCESS);
        return prefs.getString(key, "");
    }

    /**
     * [�?��描述]: read String value from Preferences [详细描述]:
     *
     * @param context
     * @param key
     * @param defaultStr
     * @return
     * @throws
     * @author [Jarry]
     * @date [Modified 2013-8-29]
     * @method [readStringValue]
     * @retruntype [String]
     */
    public static String readStringValue(Context context, String key,
                                         String defaultStr) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("test", Context.MODE_MULTI_PROCESS);
        return prefs.getString(key, defaultStr);
    }

    /**
     * [�?��描述]: read Int value from Preferences [详细描述]:
     *
     * @param context
     * @param key
     * @return
     * @throws
     * @author [Jarry]
     * @date [Modified 2013-8-29]
     * @method [readIntValue]
     * @retruntype [int]
     */
    public static int readIntValue(Context context, String key) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("test", Context.MODE_MULTI_PROCESS);
        return prefs.getInt(key, 0);
    }

    public static int readIntValue(Context context, String key, int def) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("test", Context.MODE_MULTI_PROCESS);
        return prefs.getInt(key, def);
    }

    /**
     * [�?��描述]: read Long value from Preferences [详细描述]:
     *
     * @param context
     * @param key
     * @return
     * @throws
     * @author [Jarry]
     * @date [Modified 2013-8-29]
     * @method [readLongValue]
     * @retruntype [Long]
     */
    public static Long readLongValue(Context context, String key) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("test", Context.MODE_MULTI_PROCESS);
        return prefs.getLong(key, 0);
    }

    /**
     * [�?��描述]: save Int value from Preferences [详细描述]:
     *
     * @param context
     * @param key
     * @param value
     * @throws
     * @author [Jarry]
     * @date [Modified 2013-8-29]
     * @method [saveIntValue]
     * @retruntype [void]
     */
    public static void saveIntValue(Context context, String key, int value) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("test", Context.MODE_MULTI_PROCESS);
        Editor editor = prefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * save string value to Preferences
     *
     * @param context Context
     * @param key
     * @param value
     */
    public static void saveStringValue(Context context, String key, String value) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("test", Context.MODE_MULTI_PROCESS);
        Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * save Long value to Preferences
     *
     * @param context Context
     * @param key
     * @param value
     */
    public static void saveLongValue(Context context, String key, Long value) {
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("test", Context.MODE_MULTI_PROCESS);
        Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.commit();
    }
}
