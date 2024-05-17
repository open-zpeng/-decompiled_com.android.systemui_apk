package com.xiaopeng.speech.protocol.query.speech;

import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.speech.SpeechQueryEvent;
/* loaded from: classes23.dex */
public class SpeechSensorQuery_Processor implements IQueryProcessor {
    private SpeechSensorQuery mTarget;

    public SpeechSensorQuery_Processor(SpeechSensorQuery target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -1914865600:
                if (event.equals(SpeechQueryEvent.SOUND_LOCATION)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -1900477815:
                if (event.equals(SpeechQueryEvent.SUPPORT_MULTI_SPEECH)) {
                    c = 17;
                    break;
                }
                c = 65535;
                break;
            case -1584155906:
                if (event.equals(SpeechQueryEvent.GET_SCENE_SWITCH_STATUS)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -1563492784:
                if (event.equals(SpeechQueryEvent.IS_USEREXPRESSION_OPENED)) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case -442423409:
                if (event.equals(SpeechQueryEvent.IS_NAPA_TOP)) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case -414885244:
                if (event.equals(SpeechQueryEvent.GET_CURRENT_MODE)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -323965442:
                if (event.equals(SpeechQueryEvent.IS_WAKEUP_ENABLE)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -263260335:
                if (event.equals(SpeechQueryEvent.IS_NAVI_TOP)) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case -225376174:
                if (event.equals(SpeechQueryEvent.GET_CAR_LEVEL)) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case -146831651:
                if (event.equals(SpeechQueryEvent.IS_SCREEN_ON)) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case -145453996:
                if (event.equals(SpeechQueryEvent.SUPPORT_WAIT_AWAKE)) {
                    c = 16;
                    break;
                }
                c = 65535;
                break;
            case -63943572:
                if (event.equals(SpeechQueryEvent.SUPPORT_FAST_SPEECH)) {
                    c = 15;
                    break;
                }
                c = 65535;
                break;
            case 386767985:
                if (event.equals(SpeechQueryEvent.GET_CAR_PLATFORM)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 907076707:
                if (event.equals(SpeechQueryEvent.CURRENT_TTS_ENGINE)) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case 1205362780:
                if (event.equals(SpeechQueryEvent.IS_ACCOUNT_LOGIN)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1352584367:
                if (event.equals(SpeechQueryEvent.IS_APP_FOREGROUND)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1717410403:
                if (event.equals(SpeechQueryEvent.APP_IS_INSTALLED)) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case 1965905439:
                if (event.equals(SpeechQueryEvent.IS_SUPPROT_XSPORT)) {
                    c = 18;
                    break;
                }
                c = 65535;
                break;
            case 1990811260:
                if (event.equals(SpeechQueryEvent.GET_FIRST_SPEECH_STATUS)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return Integer.valueOf(this.mTarget.getSoundLocation(event, data));
            case 1:
                return Boolean.valueOf(this.mTarget.isAppForeground(event, data));
            case 2:
                return Boolean.valueOf(this.mTarget.isAccountLogin(event, data));
            case 3:
                return Boolean.valueOf(this.mTarget.isEnableGlobalWakeup(event, data));
            case 4:
                return Integer.valueOf(this.mTarget.getCurrentMode(event, data));
            case 5:
                return this.mTarget.getCarPlatform(event, data);
            case 6:
                return Integer.valueOf(this.mTarget.getVuiSceneSwitchStatus(event, data));
            case 7:
                return Integer.valueOf(this.mTarget.getFirstSpeechState(event, data));
            case '\b':
                return Integer.valueOf(this.mTarget.getCurrentTtsEngine(event, data));
            case '\t':
                return Boolean.valueOf(this.mTarget.appIsInstalled(event, data));
            case '\n':
                return Boolean.valueOf(this.mTarget.isUserExpressionOpened(event, data));
            case 11:
                return Boolean.valueOf(this.mTarget.isNapaTop(event, data));
            case '\f':
                return Boolean.valueOf(this.mTarget.isNaviTop(event, data));
            case '\r':
                return Boolean.valueOf(this.mTarget.is(event, data));
            case 14:
                return Integer.valueOf(this.mTarget.getCfcVehicleLevel());
            case 15:
                return Integer.valueOf(this.mTarget.ifSupportFastSpeech());
            case 16:
                return Integer.valueOf(this.mTarget.ifSupportWaitAwake());
            case 17:
                return Integer.valueOf(this.mTarget.ifSupportMulti());
            case 18:
                return Boolean.valueOf(this.mTarget.isSupportXSport());
            default:
                return null;
        }
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{SpeechQueryEvent.SOUND_LOCATION, SpeechQueryEvent.IS_APP_FOREGROUND, SpeechQueryEvent.IS_ACCOUNT_LOGIN, SpeechQueryEvent.IS_WAKEUP_ENABLE, SpeechQueryEvent.GET_CURRENT_MODE, SpeechQueryEvent.GET_CAR_PLATFORM, SpeechQueryEvent.GET_SCENE_SWITCH_STATUS, SpeechQueryEvent.GET_FIRST_SPEECH_STATUS, SpeechQueryEvent.CURRENT_TTS_ENGINE, SpeechQueryEvent.APP_IS_INSTALLED, SpeechQueryEvent.IS_USEREXPRESSION_OPENED, SpeechQueryEvent.IS_NAPA_TOP, SpeechQueryEvent.IS_NAVI_TOP, SpeechQueryEvent.IS_SCREEN_ON, SpeechQueryEvent.GET_CAR_LEVEL, SpeechQueryEvent.SUPPORT_FAST_SPEECH, SpeechQueryEvent.SUPPORT_WAIT_AWAKE, SpeechQueryEvent.SUPPORT_MULTI_SPEECH, SpeechQueryEvent.IS_SUPPROT_XSPORT};
    }
}
