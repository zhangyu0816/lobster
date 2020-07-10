package com.yimi.rentme.activity;

import android.os.Bundle;
import android.text.TextUtils;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.yimi.rentme.R;
import com.zb.lib_base.activity.BaseActivity;
import com.zb.lib_base.utils.ActivityUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class NotifivationActivity extends RxAppCompatActivity {

    private String activityContent = "";
    private long otherUserId = 0;
    private long discoverId = 0;
    private long driftBottleId = 0;
    private int dynType = 0;
    private int reviewType = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_notifivation);

        activityContent = getIntent().getStringExtra("activityContent");
        try {
            JSONObject object = new JSONObject(activityContent);
            Iterator<String> keys = object.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                if (key.indexOf("-") != -1) {
                    String[] finalKey = key.split("-");
                    String value = object.optString(key);
                    if ("Long".equals(finalKey[1])) {
                        if (TextUtils.equals(finalKey[0], "otherUserId")) {
                            otherUserId = Long.parseLong(value);
                        } else if (TextUtils.equals(finalKey[0], "discoverId")) {
                            discoverId = Long.parseLong(value);
                        } else if (TextUtils.equals(finalKey[0], "driftBottleId")) {
                            driftBottleId = Long.parseLong(value);
                        }
                    }
                    if ("Integer".equals(finalKey[1])) {
                        if (TextUtils.equals(finalKey[0], "dynType")) {
                            dynType = Integer.parseInt(value);
                        } else if (TextUtils.equals(finalKey[0], "reviewType")) {
                            reviewType = Integer.parseInt(value);
                        }
                    }
                }
            }
            String activity = object.optString("ActivityName");
            if (TextUtils.equals(activity, "MemberDetailActivity")) {
                ActivityUtils.getCardMemberDetail(otherUserId, false);
            } else if (TextUtils.equals(activity, "ChatActivity")) {
                if(otherUserId == BaseActivity.systemUserId){
                    ActivityUtils.getMineSystemMsg();
                }else{
                    ActivityUtils.getChatActivity(otherUserId);
                }
            } else if (TextUtils.equals(activity, "DiscoverDetailActivity")) {
                if (dynType == 1) {
                    ActivityUtils.getHomeDiscoverDetail(discoverId);
                } else if (dynType == 2) {
                    ActivityUtils.getHomeDiscoverVideo(discoverId);
                }
            } else if (TextUtils.equals(activity, "NewsListActivity")) {
                ActivityUtils.getMineNewsList(reviewType);
            } else if (TextUtils.equals(activity, "FCLActivity")) {
                ActivityUtils.getMineFCL(1);
            } else if (TextUtils.equals(activity, "BottleChatActivity")) {
                ActivityUtils.getBottleChat(driftBottleId,"");
            }
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
