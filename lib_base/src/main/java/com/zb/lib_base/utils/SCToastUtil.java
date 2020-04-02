package com.zb.lib_base.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * [�?要描述]: 自定义Toast提示框显�? [详细描述]:
 * 
 * @author [Jarry]
 * @date [Created 2014-2-24]
 * @see [SCToastUtil]
 * @package [com.easycity.manager.utils]
 * @since [EasyCityManager]
 */
public class SCToastUtil {

	/**
	 * @param context
	 * @param text
	 * @param duration
	 * @param type
	 *            参�?�本类静态变�? TOAST_TYPE
	 * @return
	 */
	private static Toast toast = null;

	public static void showToast(Context context, CharSequence text) {
		if (toast == null) {
			toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		} else {
			toast.setText(text);
		}
		toast.show();
	}

}
