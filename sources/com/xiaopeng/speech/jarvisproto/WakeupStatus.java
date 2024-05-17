package com.xiaopeng.speech.jarvisproto;

import com.xiaopeng.speech.vui.constants.VuiConstants;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class WakeupStatus extends JarvisProto {
    public static final String EVENT = "jarvis.wakeup.status";
    public static final int WAKEUP_DISABLED = 1;
    public static final int WAKEUP_ENABLED = 0;
    public static final int WAKEUP_STATUS_NOTIFY_TYPE_INFOFLOW = 1;
    public static final int WAKEUP_STATUS_NOTIFY_TYPE_NONE = 0;
    public static final int WAKEUP_STATUS_NOTIFY_TYPE_TOAST = 2;
    public static final int WAKEUP_STATUS_NOTIFY_TYPE_TTS = 4;
    public static final int WAKEUP_TYPE_ALL = 1;
    public static final int WAKEUP_TYPE_INTERRUPT_WAKEUP_WORD = 8;
    public static final int WAKEUP_TYPE_MAIN_WAKEUP_WORD = 2;
    public static final int WAKEUP_TYPE_WHEEL_WAKEUP = 4;
    public String mInfo;
    public int mStatus;
    public int mType;

    public WakeupStatus(int status, int type, String info) {
        this.mStatus = -1;
        this.mType = -1;
        this.mInfo = null;
        this.mStatus = status;
        this.mType = type;
        this.mInfo = info;
    }

    @Override // com.xiaopeng.speech.jarvisproto.JarvisProto
    public String getJsonData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("status", this.mStatus);
            jsonObject.put(VuiConstants.ELEMENT_TYPE, this.mType);
            jsonObject.put("info", this.mInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static WakeupStatus fromJson(String data) {
        int status = -1;
        int type = -1;
        String info = null;
        try {
            JSONObject jsonObject = new JSONObject(data);
            status = jsonObject.optInt("status");
            type = jsonObject.optInt(VuiConstants.ELEMENT_TYPE);
            info = jsonObject.optString("info");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new WakeupStatus(status, type, info);
    }

    @Override // com.xiaopeng.speech.jarvisproto.JarvisProto
    public String getEvent() {
        return EVENT;
    }
}
