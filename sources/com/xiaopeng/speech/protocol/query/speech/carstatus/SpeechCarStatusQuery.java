package com.xiaopeng.speech.protocol.query.speech.carstatus;

import com.xiaopeng.speech.SpeechQuery;
import com.xiaopeng.speech.annotation.QueryAnnotation;
import com.xiaopeng.speech.protocol.event.query.speech.SpeechCarStatusEvent;
/* loaded from: classes23.dex */
public class SpeechCarStatusQuery extends SpeechQuery<ISpeechCarstatusQueryCaller> {
    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechCarStatusEvent.STATUS_CUR_MODE)
    public int getCurrentMode(String event, String data) {
        return ((ISpeechCarstatusQueryCaller) this.mQueryCaller).getCurrentMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechCarStatusEvent.STATUS_AC_POWER)
    public boolean getAcPowerStatus(String event, String data) {
        return ((ISpeechCarstatusQueryCaller) this.mQueryCaller).getAcPowerStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechCarStatusEvent.STATUS_AC_QUALITYPURGE)
    public boolean getAcQualityPurgeStatus(String event, String data) {
        return ((ISpeechCarstatusQueryCaller) this.mQueryCaller).getAcQualityPurgeStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechCarStatusEvent.STATUS_AC_TEMPDRIVERVALUE)
    public float getAcTempDriverValue(String event, String data) {
        return ((ISpeechCarstatusQueryCaller) this.mQueryCaller).getAcTempDriverValue();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechCarStatusEvent.STATUS_AC_TEMPPSNVALUE)
    public float getAcTempPsnValue(String event, String data) {
        return ((ISpeechCarstatusQueryCaller) this.mQueryCaller).getAcTempPsnValue();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechCarStatusEvent.STATUS_AC_WINDSPEEDLEVEL)
    public int getAcWindSpeedLevel(String event, String data) {
        return ((ISpeechCarstatusQueryCaller) this.mQueryCaller).getAcWindSpeedLevel();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechCarStatusEvent.STATUS_AC_WINDBLOWMODE)
    public int getAcWindBlowMode(String event, String data) {
        return ((ISpeechCarstatusQueryCaller) this.mQueryCaller).getAcWindBlowMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechCarStatusEvent.STATUS_AC_CIRCULATIONMODE)
    public int getAcCirculationMode(String event, String data) {
        return ((ISpeechCarstatusQueryCaller) this.mQueryCaller).getAcCirculationMode();
    }
}
