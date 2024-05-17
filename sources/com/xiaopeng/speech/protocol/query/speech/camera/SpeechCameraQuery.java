package com.xiaopeng.speech.protocol.query.speech.camera;

import com.xiaopeng.speech.SpeechQuery;
import com.xiaopeng.speech.annotation.QueryAnnotation;
import com.xiaopeng.speech.protocol.event.query.speech.SpeechCameraEvent;
/* loaded from: classes23.dex */
public class SpeechCameraQuery extends SpeechQuery<ISpeechCameraQueryCaller> {
    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechCameraEvent.CAMERA_ANGLE)
    public int getCameraAngle(String event, String data) {
        return ((ISpeechCameraQueryCaller) this.mQueryCaller).getCameraAngle();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechCameraEvent.CAMERA_HEIGHT_STATE)
    public boolean getCameraHeight(String event, String data) {
        return ((ISpeechCameraQueryCaller) this.mQueryCaller).getCameraHeight();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechCameraEvent.CAMERA_DISPLAY_MODE)
    public int getCameraDisplayMode(String event, String data) {
        return ((ISpeechCameraQueryCaller) this.mQueryCaller).getCameraDisplayMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechCameraEvent.CAMERA_HAS_PANO)
    public boolean getHasPanoCamera(String event, String data) {
        return ((ISpeechCameraQueryCaller) this.mQueryCaller).getHasPanoCamera();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechCameraEvent.CAMERA_HAS_ROOF)
    public boolean getHasRoofCamera(String event, String data) {
        return ((ISpeechCameraQueryCaller) this.mQueryCaller).getHasRoofCamera();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechCameraEvent.CAMERA_ROOF_STATE)
    public int getRoofCameraState(String event, String data) {
        return ((ISpeechCameraQueryCaller) this.mQueryCaller).getRoofCameraState();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = SpeechCameraEvent.CAMERA_ROOF_POSITION)
    public int getRoofCameraPosition(String event, String data) {
        return ((ISpeechCameraQueryCaller) this.mQueryCaller).getRoofCameraPosition();
    }
}
