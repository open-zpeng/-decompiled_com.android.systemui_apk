package com.xiaopeng.speech.jarvisproto;

import com.android.systemui.globalactions.GlobalActionsDialog;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public abstract class WakeupResultBase extends JarvisProto {
    public int angle;
    public boolean autoListen;
    public boolean canWakeup = true;
    public int channel;
    public String command;
    public String param;
    public String reason;
    public String script;
    public String word;

    @Override // com.xiaopeng.speech.jarvisproto.JarvisProto
    public String getJsonData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(GlobalActionsDialog.SYSTEM_DIALOG_REASON_KEY, this.reason);
            jsonObject.put("word", this.word);
            jsonObject.put("command", this.command);
            jsonObject.put("angle", this.angle);
            jsonObject.put("channel", this.channel);
            jsonObject.put("canWakeup", this.canWakeup);
            jsonObject.put("param", this.param);
            jsonObject.put("script", this.script);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
