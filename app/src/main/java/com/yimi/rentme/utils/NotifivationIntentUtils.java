package com.yimi.rentme.utils;

import android.content.Context;
import android.content.Intent;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class NotifivationIntentUtils {

    private static Map<String, String> acMap = new HashMap<>();

    static {
        acMap.put("com.yimi.rentme.activity.PersonDetailsActivity", "com.yimi.rentme.ui.activity.person.PersonDetailsActivity");
        acMap.put("com.yimi.rentme.activity.ChatForFriendActivity", "com.yimi.rentme.ui.activity.chat.ChatForFriendActivity");
        acMap.put("com.yimi.rentme.activity.DynDetailsActivity", "com.yimi.rentme.ui.activity.person.DynDetailsActivity");
        acMap.put("com.yimi.rentme.activity.NewOrderDetailActivity", "com.yimi.rentme.ui.activity.order.NewOrderDetailActivity");
    }

    public static Intent getIntent(Context context, JSONObject object)
            throws Exception {
        String activity = object.optString("ActivityName");
        Intent intent = new Intent(context.getApplicationContext(),
                Class.forName(Objects.requireNonNull(acMap.get(activity))));
        Iterator<String> keys = object.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            if (key.contains("-")) {
                String[] finalKey = key.split("-");
                String value = object.optString(key);
                if ("Long".equals(finalKey[1])) {
                    Long longValue = Long.valueOf(value);
                    intent.putExtra(finalKey[0], longValue);
                }
                if ("Integer".equals(finalKey[1])) {
                    Integer longValue = Integer.valueOf(value);
                    intent.putExtra(finalKey[0], longValue);
                }
                if ("String".equals(finalKey[1])) {
                    intent.putExtra(finalKey[0], value);
                }
            }
        }
        return intent;
    }

}
