package com.xiaopeng.speech.jarvisproto;

import com.android.systemui.globalactions.GlobalActionsDialog;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class DataAuthStateChanged extends JarvisProto {
    public static final String EVENT = "jarvis.data.auth.state.changed";
    public boolean state;

    @Override // com.xiaopeng.speech.jarvisproto.JarvisProto
    public String getEvent() {
        return EVENT;
    }

    @Override // com.xiaopeng.speech.jarvisproto.JarvisProto
    public String getJsonData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(GlobalActionsDialog.SYSTEM_DIALOG_REASON_KEY, this.state);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
