package com.xiaopeng.speech.protocol.query.speech.combo;

import android.text.TextUtils;
import com.xiaopeng.speech.SpeechQuery;
import com.xiaopeng.speech.annotation.QueryAnnotation;
import com.xiaopeng.speech.protocol.event.query.speech.ComboQueryEvent;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class ComboQuery extends SpeechQuery<IComboQueryCaller> {
    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = ComboQueryEvent.COMBO_ENTER_USERMODE)
    public String enterMode(String event, String data) {
        String modeType = getModeFromJson(data);
        return ((IComboQueryCaller) this.mQueryCaller).enterUserMode(modeType);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = ComboQueryEvent.COMBO_EXIT_USERMODE)
    public String exitMode(String event, String data) {
        String modeType = getModeFromJson(data);
        return ((IComboQueryCaller) this.mQueryCaller).exitUserMode(modeType);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = ComboQueryEvent.COMBO_CHECK_ENTER_USERMODE)
    public String checkEnterUserMode(String event, String data) {
        String modeType = getModeFromJson(data);
        return ((IComboQueryCaller) this.mQueryCaller).checkEnterUserMode(modeType);
    }

    private String getModeFromJson(String data) {
        if (TextUtils.isEmpty(data)) {
            return "";
        }
        try {
            JSONObject modeJson = new JSONObject(data);
            return modeJson.has("mode") ? modeJson.optString("mode") : "";
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = ComboQueryEvent.COMBO_GET_CURRENT_USERMODE)
    public String getCurrentUserMode(String event, String data) {
        return ((IComboQueryCaller) this.mQueryCaller).getCurrentUserMode();
    }
}
