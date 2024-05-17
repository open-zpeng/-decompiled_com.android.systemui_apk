package com.xiaopeng.speech.protocol.query.scenedemo;

import android.text.TextUtils;
import com.xiaopeng.speech.SpeechQuery;
import com.xiaopeng.speech.annotation.QueryAnnotation;
import com.xiaopeng.speech.protocol.event.query.speech.SceneDemoSpeechVuiEvent;
import com.xiaopeng.speech.protocol.query.speech.vui.ISpeechVuiElementStateQueryCaller;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class DemoSpeechVuiQuery extends SpeechQuery<ISpeechVuiElementStateQueryCaller> {
    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SceneDemoSpeechVuiEvent.DEMO_VUI_XSWITCH_CHECKED)
    public boolean isSwitchChecked(String event, String data) {
        try {
            JSONObject json = new JSONObject(data);
            String elementId = json.optString("elementId", "");
            String sceneId = json.optString(VuiConstants.SCENE_ID, "");
            String state = ((ISpeechVuiElementStateQueryCaller) this.mQueryCaller).getVuiElementState(sceneId, elementId);
            if (!TextUtils.isEmpty(state)) {
                JSONObject stateJson = new JSONObject(state);
                if (stateJson.has(VuiConstants.ELEMENT_VALUE)) {
                    boolean isChecked = stateJson.optJSONObject(VuiConstants.ELEMENT_VALUE).optJSONObject("SetCheck").optBoolean(VuiConstants.ELEMENT_VALUE, false);
                    return isChecked;
                } else if (!stateJson.has("values")) {
                    return false;
                } else {
                    boolean isChecked2 = stateJson.optJSONObject("values").optJSONObject("SetCheck").optBoolean(VuiConstants.ELEMENT_VALUE, false);
                    return isChecked2;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SceneDemoSpeechVuiEvent.DEMO_VUI_XTABLAYOUT_SELECTED)
    public boolean isTableLayoutSelected(String event, String data) {
        try {
            JSONObject dataJson = new JSONObject(data);
            int index = dataJson.optInt("index", 0);
            String elementId = dataJson.optString("elementId", "");
            String sceneId = dataJson.optString(VuiConstants.SCENE_ID, "");
            String state = ((ISpeechVuiElementStateQueryCaller) this.mQueryCaller).getVuiElementState(sceneId, elementId);
            if (!TextUtils.isEmpty(state)) {
                JSONObject json = new JSONObject(state);
                JSONObject valueObj = null;
                if (json.has(VuiConstants.ELEMENT_VALUE)) {
                    valueObj = json.optJSONObject(VuiConstants.ELEMENT_VALUE);
                } else if (json.has("values")) {
                    valueObj = json.optJSONObject("values");
                }
                if (valueObj != null) {
                    int value = 0;
                    if (valueObj.has("SetValue")) {
                        value = valueObj.optJSONObject("SetValue").optInt(VuiConstants.ELEMENT_VALUE, 0);
                    } else if (valueObj.has("SelectTab")) {
                        value = valueObj.optJSONObject("SelectTab").optInt(VuiConstants.ELEMENT_VALUE, 0);
                    }
                    return value == index;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SceneDemoSpeechVuiEvent.DEMO_VUI_XSLIDER_SETVALUE)
    public int getXSliderIndex(String event, String data) {
        try {
            JSONObject dataJson = new JSONObject(data);
            String elementId = dataJson.optString("elementId", "");
            String sceneId = dataJson.optString(VuiConstants.SCENE_ID, "");
            String state = ((ISpeechVuiElementStateQueryCaller) this.mQueryCaller).getVuiElementState(sceneId, elementId);
            if (!TextUtils.isEmpty(state)) {
                JSONObject json = new JSONObject(state);
                double value = 0.0d;
                if (json.has(VuiConstants.ELEMENT_VALUE)) {
                    value = json.optJSONObject(VuiConstants.ELEMENT_VALUE).optJSONObject("SetValue").optDouble(VuiConstants.ELEMENT_VALUE, 0.0d);
                } else if (json.has("values")) {
                    value = json.optJSONObject("values").optJSONObject("SetValue").optDouble(VuiConstants.ELEMENT_VALUE, 0.0d);
                }
                return (int) value;
            }
            return 0;
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SceneDemoSpeechVuiEvent.DEMO_VUI_STATEFULBUTTON_CHECKED)
    public boolean isStatefulButtonChecked(String event, String data) {
        try {
            JSONObject dataJson = new JSONObject(data);
            String elementId = dataJson.optString("elementId", "");
            String sceneId = dataJson.optString(VuiConstants.SCENE_ID, "");
            String state = ((ISpeechVuiElementStateQueryCaller) this.mQueryCaller).getVuiElementState(sceneId, elementId);
            if (!TextUtils.isEmpty(state)) {
                JSONObject json = new JSONObject(state);
                if (json.has(VuiConstants.ELEMENT_VALUE)) {
                    return json.optJSONObject(VuiConstants.ELEMENT_VALUE).optJSONObject("SetCheck").optBoolean(VuiConstants.ELEMENT_VALUE, false);
                }
                if (json.has("values")) {
                    return json.optJSONObject("values").optJSONObject("SetCheck").optBoolean(VuiConstants.ELEMENT_VALUE, false);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SceneDemoSpeechVuiEvent.DEMO_VUI_STATEFULBUTTON_SETVALUE)
    public String getStatefulButtonValue(String event, String data) {
        try {
            JSONObject dataJson = new JSONObject(data);
            String elementId = dataJson.optString("elementId", "");
            String sceneId = dataJson.optString(VuiConstants.SCENE_ID, "");
            String state = ((ISpeechVuiElementStateQueryCaller) this.mQueryCaller).getVuiElementState(sceneId, elementId);
            if (!TextUtils.isEmpty(state)) {
                JSONObject json = new JSONObject(state);
                if (json.has(VuiConstants.ELEMENT_VALUE)) {
                    String value = json.optJSONObject(VuiConstants.ELEMENT_VALUE).optJSONObject("SetValue").optString(VuiConstants.ELEMENT_VALUE, null);
                    return value;
                } else if (!json.has("values")) {
                    return null;
                } else {
                    String value2 = json.optJSONObject("values").optJSONObject("SetValue").optString(VuiConstants.ELEMENT_VALUE, null);
                    return value2;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SceneDemoSpeechVuiEvent.DEMO_VUI_ELEMENT_ENABLED)
    public boolean isElementEnabled(String event, String data) {
        try {
            JSONObject dataJson = new JSONObject(data);
            String elementId = dataJson.optString("elementId", "");
            String sceneId = dataJson.optString(VuiConstants.SCENE_ID, "");
            String state = ((ISpeechVuiElementStateQueryCaller) this.mQueryCaller).getVuiElementState(sceneId, elementId);
            if (!TextUtils.isEmpty(state)) {
                JSONObject json = new JSONObject(state);
                boolean value = json.optBoolean(VuiConstants.ELEMENT_ENABLED, true);
                return value;
            }
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Removed duplicated region for block: B:31:0x0078  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x008b  */
    @com.xiaopeng.speech.annotation.QueryAnnotation(event = com.xiaopeng.speech.protocol.event.query.speech.SceneDemoSpeechVuiEvent.DEMO_VUI_ELEMENT_SCROLL_STATE)
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean isElementCanScrolled(java.lang.String r17, java.lang.String r18) {
        /*
            r16 = this;
            java.lang.String r0 = ""
            r1 = 0
            org.json.JSONObject r2 = new org.json.JSONObject     // Catch: org.json.JSONException -> L9b
            r3 = r18
            r2.<init>(r3)     // Catch: org.json.JSONException -> L97
            java.lang.String r4 = "elementId"
            java.lang.String r4 = r2.optString(r4, r0)     // Catch: org.json.JSONException -> L97
            java.lang.String r5 = "sceneId"
            java.lang.String r5 = r2.optString(r5, r0)     // Catch: org.json.JSONException -> L97
            java.lang.String r6 = "direction"
            java.lang.String r0 = r2.optString(r6, r0)     // Catch: org.json.JSONException -> L97
            r6 = r16
            T r7 = r6.mQueryCaller     // Catch: org.json.JSONException -> L95
            com.xiaopeng.speech.protocol.query.speech.vui.ISpeechVuiElementStateQueryCaller r7 = (com.xiaopeng.speech.protocol.query.speech.vui.ISpeechVuiElementStateQueryCaller) r7     // Catch: org.json.JSONException -> L95
            java.lang.String r7 = r7.getVuiElementState(r5, r4)     // Catch: org.json.JSONException -> L95
            boolean r8 = android.text.TextUtils.isEmpty(r7)     // Catch: org.json.JSONException -> L95
            if (r8 != 0) goto L94
            org.json.JSONObject r8 = new org.json.JSONObject     // Catch: org.json.JSONException -> L95
            r8.<init>(r7)     // Catch: org.json.JSONException -> L95
            r9 = 0
            r10 = -1
            int r11 = r0.hashCode()     // Catch: org.json.JSONException -> L95
            r12 = 3739(0xe9b, float:5.24E-42)
            r13 = 3
            r14 = 2
            r15 = 1
            if (r11 == r12) goto L6d
            r12 = 3089570(0x2f24a2, float:4.32941E-39)
            if (r11 == r12) goto L63
            r12 = 3317767(0x32a007, float:4.649182E-39)
            if (r11 == r12) goto L59
            r12 = 108511772(0x677c21c, float:4.6598146E-35)
            if (r11 == r12) goto L4f
        L4e:
            goto L76
        L4f:
            java.lang.String r11 = "right"
            boolean r11 = r0.equals(r11)     // Catch: org.json.JSONException -> L95
            if (r11 == 0) goto L4e
            r10 = r14
            goto L76
        L59:
            java.lang.String r11 = "left"
            boolean r11 = r0.equals(r11)     // Catch: org.json.JSONException -> L95
            if (r11 == 0) goto L4e
            r10 = r1
            goto L76
        L63:
            java.lang.String r11 = "down"
            boolean r11 = r0.equals(r11)     // Catch: org.json.JSONException -> L95
            if (r11 == 0) goto L4e
            r10 = r13
            goto L76
        L6d:
            java.lang.String r11 = "up"
            boolean r11 = r0.equals(r11)     // Catch: org.json.JSONException -> L95
            if (r11 == 0) goto L4e
            r10 = r15
        L76:
            if (r10 == 0) goto L8b
            if (r10 == r15) goto L87
            if (r10 == r14) goto L83
            if (r10 == r13) goto L7f
            goto L8f
        L7f:
            java.lang.String r10 = "canScrollDown"
            r9 = r10
            goto L8f
        L83:
            java.lang.String r10 = "canScrollRight"
            r9 = r10
            goto L8f
        L87:
            java.lang.String r10 = "canScrollUp"
            r9 = r10
            goto L8f
        L8b:
            java.lang.String r10 = "canScrollLeft"
            r9 = r10
        L8f:
            boolean r1 = r8.optBoolean(r9, r15)     // Catch: org.json.JSONException -> L95
            return r1
        L94:
            goto La3
        L95:
            r0 = move-exception
            goto La0
        L97:
            r0 = move-exception
            r6 = r16
            goto La0
        L9b:
            r0 = move-exception
            r6 = r16
            r3 = r18
        La0:
            r0.printStackTrace()
        La3:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.speech.protocol.query.scenedemo.DemoSpeechVuiQuery.isElementCanScrolled(java.lang.String, java.lang.String):boolean");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SceneDemoSpeechVuiEvent.DEMO_VUI_ELEMENT_CHECKBOX_CHECKED)
    public boolean isCheckBoxChecked(String event, String data) {
        try {
            JSONObject dataJson = new JSONObject(data);
            String elementId = dataJson.optString("elementId", "");
            String sceneId = dataJson.optString(VuiConstants.SCENE_ID, "");
            String state = ((ISpeechVuiElementStateQueryCaller) this.mQueryCaller).getVuiElementState(sceneId, elementId);
            if (!TextUtils.isEmpty(state)) {
                JSONObject json = new JSONObject(state);
                if (json.has(VuiConstants.ELEMENT_VALUE)) {
                    return json.optJSONObject(VuiConstants.ELEMENT_VALUE).optJSONObject("SetCheck").optBoolean(VuiConstants.ELEMENT_VALUE, false);
                }
                if (json.has("values")) {
                    return json.optJSONObject("values").optJSONObject("SetCheck").optBoolean(VuiConstants.ELEMENT_VALUE, false);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SceneDemoSpeechVuiEvent.DEMO_VUI_ELEMENT_RADIOBUTTON_CHECKED)
    public boolean isRadiobuttonChecked(String event, String data) {
        try {
            JSONObject dataJson = new JSONObject(data);
            String elementId = dataJson.optString("elementId", "");
            String sceneId = dataJson.optString(VuiConstants.SCENE_ID, "");
            String state = ((ISpeechVuiElementStateQueryCaller) this.mQueryCaller).getVuiElementState(sceneId, elementId);
            if (!TextUtils.isEmpty(state)) {
                JSONObject json = new JSONObject(state);
                if (json.has(VuiConstants.ELEMENT_VALUE)) {
                    return json.optJSONObject(VuiConstants.ELEMENT_VALUE).optJSONObject("SetCheck").optBoolean(VuiConstants.ELEMENT_VALUE, false);
                }
                if (json.has("values")) {
                    return json.optJSONObject("values").optJSONObject("SetCheck").optBoolean(VuiConstants.ELEMENT_VALUE, false);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
