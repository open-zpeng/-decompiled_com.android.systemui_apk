package com.xiaopeng.speech.protocol.node.dialog.bean;

import com.android.systemui.globalactions.GlobalActionsDialog;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class DialogEndReason {
    public String event;
    public String reason;
    public String sessionId;

    public DialogEndReason(String reason, String event, String sessionId) {
        this.reason = reason;
        this.event = event;
        this.sessionId = sessionId;
    }

    public static DialogEndReason fromJson(String data) {
        String reason = "normal";
        String event = "";
        String sessionId = "";
        try {
            JSONObject jsonObject = new JSONObject(data);
            reason = jsonObject.optString(GlobalActionsDialog.SYSTEM_DIALOG_REASON_KEY);
            event = jsonObject.optString("event");
            sessionId = jsonObject.optString("sessionId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new DialogEndReason(reason, event, sessionId);
    }
}
