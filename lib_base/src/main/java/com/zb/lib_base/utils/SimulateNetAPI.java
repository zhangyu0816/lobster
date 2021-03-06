package com.zb.lib_base.utils;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class SimulateNetAPI {
    /**
     * 获取去最原始的数据信息
     *
     * @return json data
     */
    public static String getOriginalFundData(RxAppCompatActivity activity, String fileName) {
        InputStream input;
        try {
            input = activity.getAssets().open(fileName);
            return convertStreamToString(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * input 流转换为字符串
     *
     * @param is
     * @return
     */
    private static String convertStreamToString(java.io.InputStream is) {
        String s = null;
        try {
            Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
            if (scanner.hasNext()) {
                s = scanner.next();
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }
}
