package com.xiaopeng.speech.protocol.query.speech;

import android.text.TextUtils;
import com.xiaopeng.speech.SpeechQuery;
import com.xiaopeng.speech.annotation.QueryAnnotation;
import com.xiaopeng.speech.common.util.LogUtils;
import com.xiaopeng.speech.protocol.event.query.speech.SpeechQueryEvent;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class SpeechSensorQuery extends SpeechQuery<ISpeechQueryCaller> {
    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechQueryEvent.SOUND_LOCATION)
    public int getSoundLocation(String event, String data) {
        return ((ISpeechQueryCaller) this.mQueryCaller).getSoundLocation();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechQueryEvent.IS_APP_FOREGROUND)
    public boolean isAppForeground(String event, String data) {
        String packageName = "";
        try {
            JSONObject jsonObject = new JSONObject(data);
            packageName = jsonObject.optString("package");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ((ISpeechQueryCaller) this.mQueryCaller).isAppForeground(packageName);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechQueryEvent.IS_ACCOUNT_LOGIN)
    public boolean isAccountLogin(String event, String data) {
        return ((ISpeechQueryCaller) this.mQueryCaller).isAccountLogin();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechQueryEvent.IS_WAKEUP_ENABLE)
    public boolean isEnableGlobalWakeup(String event, String data) {
        return ((ISpeechQueryCaller) this.mQueryCaller).isEnableGlobalWakeup();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechQueryEvent.GET_CURRENT_MODE)
    public int getCurrentMode(String event, String data) {
        return ((ISpeechQueryCaller) this.mQueryCaller).getCurrentMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechQueryEvent.GET_CAR_PLATFORM)
    public String getCarPlatform(String event, String data) {
        return ((ISpeechQueryCaller) this.mQueryCaller).getCarPlatForm();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechQueryEvent.GET_SCENE_SWITCH_STATUS)
    public int getVuiSceneSwitchStatus(String event, String data) {
        return ((ISpeechQueryCaller) this.mQueryCaller).getVuiSceneSwitchStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechQueryEvent.GET_FIRST_SPEECH_STATUS)
    public int getFirstSpeechState(String event, String data) {
        return ((ISpeechQueryCaller) this.mQueryCaller).getFirstSpeechState();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechQueryEvent.CURRENT_TTS_ENGINE)
    public int getCurrentTtsEngine(String event, String data) {
        return ((ISpeechQueryCaller) this.mQueryCaller).getCurrentTtsEngine();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechQueryEvent.APP_IS_INSTALLED)
    public boolean appIsInstalled(String event, String data) {
        LogUtils.d("SpeechUiQuery", "enter appIsInstalled , event = " + event + ", data = " + data);
        try {
            if (!TextUtils.isEmpty(data)) {
                JSONObject json = new JSONObject(data);
                String packageName = json.optString(VuiConstants.SCENE_PACKAGE_NAME, "");
                return ((ISpeechQueryCaller) this.mQueryCaller).appIsInstalled(packageName);
            }
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechQueryEvent.IS_USEREXPRESSION_OPENED)
    public boolean isUserExpressionOpened(String event, String data) {
        return ((ISpeechQueryCaller) this.mQueryCaller).isUserExpressionOpened();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechQueryEvent.IS_NAPA_TOP)
    public boolean isNapaTop(String event, String data) {
        int display_id = -1;
        try {
            if (!TextUtils.isEmpty(data)) {
                JSONObject json = new JSONObject(data);
                display_id = json.optInt("display_location");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ((ISpeechQueryCaller) this.mQueryCaller).isNapaTop(display_id);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechQueryEvent.IS_NAVI_TOP)
    public boolean isNaviTop(String event, String data) {
        int display_location = -1;
        try {
            if (!TextUtils.isEmpty(data)) {
                JSONObject json = new JSONObject(data);
                display_location = json.optInt("display_location");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ((ISpeechQueryCaller) this.mQueryCaller).isNaviTop(display_location);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechQueryEvent.IS_SCREEN_ON)
    public boolean is(String event, String data) {
        int display_location = -1;
        try {
            if (!TextUtils.isEmpty(data)) {
                JSONObject json = new JSONObject(data);
                display_location = json.optInt("display_location");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ((ISpeechQueryCaller) this.mQueryCaller).isScreenOn(display_location);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechQueryEvent.GET_CAR_LEVEL)
    public int getCfcVehicleLevel() {
        return ((ISpeechQueryCaller) this.mQueryCaller).getCfcVehicleLevel();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechQueryEvent.SUPPORT_FAST_SPEECH)
    public int ifSupportFastSpeech() {
        return ((ISpeechQueryCaller) this.mQueryCaller).ifSupportFastSpeech();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechQueryEvent.SUPPORT_WAIT_AWAKE)
    public int ifSupportWaitAwake() {
        return ((ISpeechQueryCaller) this.mQueryCaller).ifSupportWaitAwake();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechQueryEvent.SUPPORT_MULTI_SPEECH)
    public int ifSupportMulti() {
        return ((ISpeechQueryCaller) this.mQueryCaller).ifSupportMulti();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechQueryEvent.IS_SUPPROT_XSPORT)
    public boolean isSupportXSport() {
        return ((ISpeechQueryCaller) this.mQueryCaller).isSupportXSport();
    }
}
