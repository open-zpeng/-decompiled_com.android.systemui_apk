package com.xiaopeng.speech.jarvisproto;

import com.android.systemui.globalactions.GlobalActionsDialog;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class DMStart extends JarvisProto {
    public static final String EVENT = "jarvis.dm.start";
    public static final String REASON_API_START = "api.start";
    public static final String REASON_BOSS_START = "boss.start";
    public static final String REASON_CLICK_START = "click.start";
    public static final String REASON_MUSIC_SEARCH = "music";
    public static final String REASON_REAR_BOSS_START = "rear.boss.start";
    public static final String REASON_SEND_TEXT = "api.sendText";
    public static final String REASON_WAITING = "waiting";
    public static final String REASON_WAKEUP_CMD = "wakeup.command";
    public static final String REASON_WAKEUP_MAJOR = "wakeup.major";
    public static final String REASON_WHEEL_START = "wheel.start";
    public String reason;
    public String sessionId;
    public int soundArea;
    public WakeupResult wakeupResult;

    @Override // com.xiaopeng.speech.jarvisproto.JarvisProto
    public String getEvent() {
        return "jarvis.dm.start";
    }

    @Override // com.xiaopeng.speech.jarvisproto.JarvisProto
    public String getJsonData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(GlobalActionsDialog.SYSTEM_DIALOG_REASON_KEY, this.reason);
            jsonObject.put("soundArea", this.soundArea);
            if (this.wakeupResult != null) {
                jsonObject.put("wakeupResult", this.wakeupResult.getJsonData());
            } else {
                jsonObject.put("wakeupResult", (Object) null);
            }
            jsonObject.put("sessionId", this.sessionId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
