package com.xiaopeng.speech.protocol.query.speech.vui;

import com.xiaopeng.speech.SpeechQuery;
import com.xiaopeng.speech.annotation.QueryAnnotation;
import com.xiaopeng.speech.protocol.event.query.speech.SpeechVuiEvent;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class SpeechVuiQuery extends SpeechQuery<ISpeechVuiQueryCaller> {
    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechVuiEvent.VUI_XSWITCH_CHECKED)
    public boolean isSwitchChecked(String event, String data) {
        try {
            JSONObject json = new JSONObject(data);
            String elementId = json.optString("elementId", "");
            String sceneId = json.optString(VuiConstants.SCENE_ID, "");
            String packageName = json.optString(VuiConstants.SCENE_PACKAGE_NAME, "");
            String packageVersion = json.optString("packageVersion", "");
            return ((ISpeechVuiQueryCaller) this.mQueryCaller).isSwitchChecked(elementId, sceneId, packageName, packageVersion);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechVuiEvent.VUI_XTABLAYOUT_SELECTED)
    public boolean isTableLayoutSelected(String event, String data) {
        try {
            JSONObject json = new JSONObject(data);
            int index = json.optInt("index", 0);
            String elementId = json.optString("elementId", "");
            String sceneId = json.optString(VuiConstants.SCENE_ID, "");
            String packageName = json.optString(VuiConstants.SCENE_PACKAGE_NAME, "");
            String packageVersion = json.optString("packageVersion", "");
            return ((ISpeechVuiQueryCaller) this.mQueryCaller).isTableLayoutSelected(index, elementId, sceneId, packageName, packageVersion);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechVuiEvent.VUI_XSLIDER_SETVALUE)
    public int getXSliderIndex(String event, String data) {
        try {
            JSONObject json = new JSONObject(data);
            String elementId = json.optString("elementId", "");
            String sceneId = json.optString(VuiConstants.SCENE_ID, "");
            String packageName = json.optString(VuiConstants.SCENE_PACKAGE_NAME, "");
            String packageVersion = json.optString("packageVersion", "");
            return ((ISpeechVuiQueryCaller) this.mQueryCaller).getXSliderIndex(elementId, sceneId, packageName, packageVersion);
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechVuiEvent.VUI_STATEFULBUTTON_CHECKED)
    public boolean isStatefulButtonChecked(String event, String data) {
        try {
            JSONObject json = new JSONObject(data);
            String elementId = json.optString("elementId", "");
            String sceneId = json.optString(VuiConstants.SCENE_ID, "");
            String packageName = json.optString(VuiConstants.SCENE_PACKAGE_NAME, "");
            String packageVersion = json.optString("packageVersion", "");
            return ((ISpeechVuiQueryCaller) this.mQueryCaller).isStatefulButtonChecked(elementId, sceneId, packageName, packageVersion);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechVuiEvent.VUI_STATEFULBUTTON_SETVALUE)
    public String getStatefulButtonValue(String event, String data) {
        try {
            JSONObject json = new JSONObject(data);
            String elementId = json.optString("elementId", "");
            String sceneId = json.optString(VuiConstants.SCENE_ID, "");
            String packageName = json.optString(VuiConstants.SCENE_PACKAGE_NAME, "");
            String packageVersion = json.optString("packageVersion", "");
            return ((ISpeechVuiQueryCaller) this.mQueryCaller).getStatefulButtonValue(elementId, sceneId, packageName, packageVersion);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechVuiEvent.VUI_ELEMENT_ENABLED)
    public boolean isElementEnabled(String event, String data) {
        try {
            JSONObject json = new JSONObject(data);
            String elementId = json.optString("elementId", "");
            String sceneId = json.optString(VuiConstants.SCENE_ID, "");
            String packageName = json.optString(VuiConstants.SCENE_PACKAGE_NAME, "");
            String packageVersion = json.optString("packageVersion", "");
            return ((ISpeechVuiQueryCaller) this.mQueryCaller).isElementEnabled(elementId, sceneId, packageName, packageVersion);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechVuiEvent.VUI_ELEMENT_SCROLL_STATE)
    public boolean isElementCanScrolled(String event, String data) {
        try {
            JSONObject json = new JSONObject(data);
            String direction = json.optString(VuiConstants.EVENT_VALUE_DIRECTION, "");
            String elementId = json.optString("elementId", "");
            String sceneId = json.optString(VuiConstants.SCENE_ID, "");
            String packageName = json.optString(VuiConstants.SCENE_PACKAGE_NAME, "");
            String packageVersion = json.optString("packageVersion", "");
            return ((ISpeechVuiQueryCaller) this.mQueryCaller).isElementCanScrolled(direction, elementId, sceneId, packageName, packageVersion);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechVuiEvent.VUI_SWITCH_OPENED)
    public boolean isVuiSwitchOpened() {
        return ((ISpeechVuiQueryCaller) this.mQueryCaller).isVuiSwitchOpened();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechVuiEvent.VUI_ELEMENT_CHECKBOX_CHECKED)
    public boolean isCheckBoxChecked(String event, String data) {
        try {
            JSONObject json = new JSONObject(data);
            String elementId = json.optString("elementId", "");
            String sceneId = json.optString(VuiConstants.SCENE_ID, "");
            String packageName = json.optString(VuiConstants.SCENE_PACKAGE_NAME, "");
            String packageVersion = json.optString("packageVersion", "");
            return ((ISpeechVuiQueryCaller) this.mQueryCaller).isCheckBoxChecked(elementId, sceneId, packageName, packageVersion);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechVuiEvent.VUI_ELEMENT_RADIOBUTTON_CHECKED)
    public boolean isRadiobuttonChecked(String event, String data) {
        try {
            JSONObject json = new JSONObject(data);
            String elementId = json.optString("elementId", "");
            String sceneId = json.optString(VuiConstants.SCENE_ID, "");
            String packageName = json.optString(VuiConstants.SCENE_PACKAGE_NAME, "");
            String packageVersion = json.optString("packageVersion", "");
            return ((ISpeechVuiQueryCaller) this.mQueryCaller).isRadiobuttonChecked(elementId, sceneId, packageName, packageVersion);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechVuiEvent.VUI_SCROLLVIEW_CHILD_VIEW_VISIBLE)
    public String isViewVisibleByScrollView(String event, String data) {
        try {
            JSONObject json = new JSONObject(data);
            JSONArray elementIds = json.optJSONArray(VuiConstants.SCENE_ELEMENTS);
            String sceneId = json.optString(VuiConstants.SCENE_ID, "");
            String packageName = json.optString(VuiConstants.SCENE_PACKAGE_NAME, "");
            String packageVersion = json.optString(VuiConstants.SCENE_APP_VERSION, "");
            String msgId = json.optString("msgId");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msgId", msgId);
            jsonObject.put(VuiConstants.SCENE_ID, sceneId);
            jsonObject.put(VuiConstants.SCENE_ELEMENTS, elementIds);
            return ((ISpeechVuiQueryCaller) this.mQueryCaller).isViewVisibleByScrollView(packageName, sceneId, String.valueOf(jsonObject), packageVersion);
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechVuiEvent.VUI_REBUILD_SCENE)
    public boolean rebuildScene(String event, String data) {
        try {
            JSONObject json = new JSONObject(data);
            JSONArray sceneIds = json.optJSONArray("sceneIds");
            String[] arraySceneId = null;
            if (sceneIds != null) {
                arraySceneId = new String[sceneIds.length()];
                for (int i = 0; i < sceneIds.length(); i++) {
                    arraySceneId[i] = sceneIds.optString(i);
                }
            }
            return ((ISpeechVuiQueryCaller) this.mQueryCaller).reBuildScene(arraySceneId);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
}
