package com.xiaopeng.speech.protocol.query.speech.radar;

import com.xiaopeng.speech.SpeechQuery;
import com.xiaopeng.speech.annotation.QueryAnnotation;
import com.xiaopeng.speech.protocol.event.query.speech.SpeechRadarEvent;
/* loaded from: classes23.dex */
public class SpeechRadarQuery extends SpeechQuery<ISpeechRadarQueryCaller> {
    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechRadarEvent.RADAR_FRONT_DATA)
    public float[] getFrontRadarData(String event, String data) {
        return ((ISpeechRadarQueryCaller) this.mQueryCaller).getFrontRadarData();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechRadarEvent.RADAR_TAIL_DATA)
    public float[] getTailRadarData(String event, String data) {
        return ((ISpeechRadarQueryCaller) this.mQueryCaller).getTailRadarData();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechRadarEvent.RADAR_FRONT_LV)
    public int[] getFrontRadarLevel(String event, String data) {
        return ((ISpeechRadarQueryCaller) this.mQueryCaller).getFrontRadarLevel();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechRadarEvent.RADAR_TAIL_LV)
    public int[] getTailRadarLevel(String event, String data) {
        return ((ISpeechRadarQueryCaller) this.mQueryCaller).getTailRadarLevel();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechRadarEvent.RADAR_FRONT_FAULT)
    public int[] getFrontRadarFaultSt(String event, String data) {
        return ((ISpeechRadarQueryCaller) this.mQueryCaller).getFrontRadarFaultSt();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechRadarEvent.RADAR_TAIL_FAULT)
    public int[] getTailRadarFaultSt(String event, String data) {
        return ((ISpeechRadarQueryCaller) this.mQueryCaller).getTailRadarFaultSt();
    }
}
