package com.xiaopeng.speech.protocol.node.dialog.bean;

import com.android.systemui.globalactions.GlobalActionsDialog;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class DialogExitReason {
    public String reason;
    public int soundArea;

    public DialogExitReason(String reason, int soundArea) {
        this.soundArea = -1;
        this.reason = reason;
        this.soundArea = soundArea;
    }

    public static DialogExitReason fromJson(String data) {
        String reason = "normal";
        int soundArea = -1;
        try {
            JSONObject jsonObject = new JSONObject(data);
            reason = jsonObject.optString(GlobalActionsDialog.SYSTEM_DIALOG_REASON_KEY);
            soundArea = jsonObject.optInt("soundArea");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new DialogExitReason(reason, soundArea);
    }
}
