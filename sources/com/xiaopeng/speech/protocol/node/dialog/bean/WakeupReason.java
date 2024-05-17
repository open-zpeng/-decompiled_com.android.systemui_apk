package com.xiaopeng.speech.protocol.node.dialog.bean;

import com.android.systemui.globalactions.GlobalActionsDialog;
import com.xiaopeng.speech.jarvisproto.DMStart;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class WakeupReason {
    public static final int REASON_BOSS = 7;
    public static final int REASON_FAST_WAKEUP = 2;
    public static final int REASON_MAJOR = 1;
    public static final int REASON_MANUALLY = 3;
    public static final int REASON_MUSIC = 6;
    public static final int REASON_REAR_BOSS = 8;
    public static final int REASON_SEND_TEXT = 4;
    public static final int REASON_TRIGGER_INTENT = 5;
    public int reason;
    public String sessionId;
    public int soundArea;

    public WakeupReason(int reason, String sessionId) {
        this.soundArea = -1;
        this.reason = reason;
        this.sessionId = sessionId;
    }

    public WakeupReason(int reason, String sessionId, int soundArea) {
        this.soundArea = -1;
        this.reason = reason;
        this.sessionId = sessionId;
        this.soundArea = soundArea;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static WakeupReason fromJson(String jsonStr) {
        int reason = 1;
        String sessionId = "";
        int soundArea = -1;
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            String optString = jsonObject.optString(GlobalActionsDialog.SYSTEM_DIALOG_REASON_KEY);
            char c = 65535;
            switch (optString.hashCode()) {
                case -1990314378:
                    if (optString.equals("api.startDialog")) {
                        c = 3;
                        break;
                    }
                    break;
                case -1958887862:
                    if (optString.equals(DMStart.REASON_WAKEUP_MAJOR)) {
                        c = 0;
                        break;
                    }
                    break;
                case -1872304479:
                    if (optString.equals(DMStart.REASON_BOSS_START)) {
                        c = '\n';
                        break;
                    }
                    break;
                case -1792656369:
                    if (optString.equals(DMStart.REASON_WHEEL_START)) {
                        c = 7;
                        break;
                    }
                    break;
                case -1177155684:
                    if (optString.equals(DMStart.REASON_WAKEUP_CMD)) {
                        c = 1;
                        break;
                    }
                    break;
                case 104263205:
                    if (optString.equals("music")) {
                        c = 2;
                        break;
                    }
                    break;
                case 282538108:
                    if (optString.equals(DMStart.REASON_CLICK_START)) {
                        c = 6;
                        break;
                    }
                    break;
                case 687136219:
                    if (optString.equals("api.avatarClick")) {
                        c = 4;
                        break;
                    }
                    break;
                case 699317398:
                    if (optString.equals("api.avatarPress")) {
                        c = 5;
                        break;
                    }
                    break;
                case 790870569:
                    if (optString.equals(DMStart.REASON_SEND_TEXT)) {
                        c = '\b';
                        break;
                    }
                    break;
                case 1100434848:
                    if (optString.equals("api.triggerIntent")) {
                        c = '\t';
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    reason = 1;
                    break;
                case 1:
                    reason = 2;
                    break;
                case 2:
                    reason = 6;
                    break;
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    reason = 3;
                    break;
                case '\b':
                    reason = 4;
                    break;
                case '\t':
                    reason = 5;
                    break;
                case '\n':
                    reason = 7;
                    break;
            }
            sessionId = jsonObject.optString("sessionId");
            soundArea = jsonObject.optInt("soundArea");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new WakeupReason(reason, sessionId, soundArea);
    }
}
