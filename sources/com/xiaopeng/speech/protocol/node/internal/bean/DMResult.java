package com.xiaopeng.speech.protocol.node.internal.bean;

import android.text.TextUtils;
import com.xiaopeng.speech.common.SpeechEvent;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class DMResult {
    private String event;
    private String intent;
    private boolean isNative;
    private String nlg;
    private JSONObject param;
    private String rawData;
    private String runSequence;
    private String sessionId;
    private boolean shouldEndSession;
    private String task;
    private JSONObject widget;

    public static DMResult fromJson(String data) {
        DMResult dmResult = new DMResult();
        dmResult.rawData = data;
        try {
            JSONObject jsonObject = new JSONObject(data);
            dmResult.sessionId = jsonObject.optString("sessionId");
            JSONObject dm = jsonObject.optJSONObject("dm");
            String dataFrom = dm.optString("dataFrom", "");
            if (dataFrom.equals("native")) {
                String api = dm.optString(SpeechWidget.DATA_SOURCE_API);
                if (!TextUtils.isEmpty(api)) {
                    dmResult.event = SpeechEvent.NATIVE_API_SCHEME + api;
                }
            }
            JSONObject commandObj = dm.optJSONObject("command");
            if (commandObj != null) {
                String api2 = commandObj.optString(SpeechWidget.DATA_SOURCE_API);
                if (!TextUtils.isEmpty(api2)) {
                    dmResult.event = SpeechEvent.COMMAND_SCHEME + api2;
                }
            }
            dmResult.intent = dm.optString("intentName");
            dmResult.runSequence = dm.optString("runSequence");
            dmResult.nlg = dm.optString("nlg");
            dmResult.task = dm.optString("task");
            dmResult.shouldEndSession = dm.optBoolean("shouldEndSession");
            dmResult.isNative = !TextUtils.isEmpty(dm.optString("dataFrom"));
            dmResult.widget = dm.optJSONObject("widget");
            dmResult.param = dm.optJSONObject("param");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dmResult;
    }

    public String getRawData() {
        return this.rawData;
    }

    public JSONObject getParam() {
        return this.param;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public String getIntent() {
        return this.intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getRunSequence() {
        return this.runSequence;
    }

    public void setRunSequence(String runSequence) {
        this.runSequence = runSequence;
    }

    public String getEvent() {
        return this.event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getNlg() {
        return this.nlg;
    }

    public void setNlg(String nlg) {
        this.nlg = nlg;
    }

    public String getTask() {
        return this.task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public boolean isShouldEndSession() {
        return this.shouldEndSession;
    }

    public void setShouldEndSession(boolean shouldEndSession) {
        this.shouldEndSession = shouldEndSession;
    }

    public boolean isNative() {
        return this.isNative;
    }

    public void setNative(boolean aNative) {
        this.isNative = aNative;
    }

    public boolean isListWidget() {
        return this.widget.optString(VuiConstants.ELEMENT_TYPE).equals(SpeechWidget.TYPE_LIST);
    }
}
