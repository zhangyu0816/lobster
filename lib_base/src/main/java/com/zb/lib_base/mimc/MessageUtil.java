package com.zb.lib_base.mimc;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageUtil {

    public static CustomMessageBody unpack(String content) {
        CustomMessageBody body = new CustomMessageBody();
        try {
            JSONObject message = new JSONObject(content);
            body.setFromId(message.optLong("fromId"));
            body.setToId(message.optLong("toId"));
            JSONObject valueBean = message.optJSONObject("valueBean");
            if (valueBean.has("msgType"))
                body.setMsgType(valueBean.optInt("msgType"));
            if (valueBean.has("stanza"))
                body.setStanza(valueBean.optString("stanza"));
            if (valueBean.has("resLink"))
                body.setResLink(valueBean.optString("resLink"));
            if (valueBean.has("resTime"))
                body.setResTime(valueBean.optInt("resTime"));
            if (valueBean.has("driftBottleId"))
                body.setDriftBottleId(valueBean.optLong("driftBottleId"));
            if (valueBean.has("flashTalkId"))
                body.setFlashTalkId(valueBean.optLong("flashTalkId"));
            if (valueBean.has("msgChannelType"))
                body.setMsgChannelType(valueBean.optInt("msgChannelType"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return body;
    }

    /**
     * 封装消息
     */
    public static String pack(CustomMessageBody body) {
        // 自定义消息的实现可以使用jsonObject实现，或者也可以根据自定义其他格式，只需要个unpack中一致即可
        JSONObject message = new JSONObject();
        try {
            JSONObject valueBean = new JSONObject();
            valueBean.put("msgType", body.getMsgType());
            valueBean.put("stanza", body.getStanza());
            valueBean.put("resLink", body.getResLink());
            valueBean.put("resTime", body.getResTime());
            if (body.getDriftBottleId() != 0)
                valueBean.put("driftBottleId", body.getDriftBottleId());
            if (body.getFlashTalkId() != 0)
                valueBean.put("flashTalkId", body.getFlashTalkId());
            valueBean.put("msgChannelType", body.getMsgChannelType());
            message.put("fromId", body.getFromId());
            message.put("toId", body.getToId());
            message.put("valueBean", valueBean);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message.toString();
    }
}
