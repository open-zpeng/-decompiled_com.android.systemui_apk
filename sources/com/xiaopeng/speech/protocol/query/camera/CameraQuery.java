package com.xiaopeng.speech.protocol.query.camera;

import com.xiaopeng.speech.SpeechQuery;
import com.xiaopeng.speech.annotation.QueryAnnotation;
import com.xiaopeng.speech.protocol.event.query.QueryCameraEvent;
/* loaded from: classes23.dex */
public class CameraQuery extends SpeechQuery<ICameraCaller> {
    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCameraEvent.GET_SUPPORT_TOP_STATUS)
    public int getSupportTopStatus(String event, String data) {
        return ((ICameraCaller) this.mQueryCaller).getSupportTopStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCameraEvent.GET_SUPPORT_PANORAMIC_STATUS)
    public int getSupportPanoramicStatus(String event, String data) {
        return ((ICameraCaller) this.mQueryCaller).getSupportPanoramicStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCameraEvent.GET_SUPPORT_REAR_STATUS)
    public int getSupportRearStatus(String event, String data) {
        return ((ICameraCaller) this.mQueryCaller).getSupportRearStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCameraEvent.GET_BUSINESS_CAMERA_STATUS)
    public int getBusinessCameraStatus(String event, String data) {
        return ((ICameraCaller) this.mQueryCaller).getBusinessCameraStatus(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCameraEvent.GET_CAMERA_THREE_D_SUPPORT)
    public int getCameraThreeDSupport(String event, String data) {
        return ((ICameraCaller) this.mQueryCaller).getCameraThreeDSupport();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCameraEvent.GET_CAMERA_TRANSPARENT_CHASSIS_SUPPORT)
    public int getCameraTransparentChassisSupport(String event, String data) {
        return ((ICameraCaller) this.mQueryCaller).getCameraTransparentChassisSupport();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QueryCameraEvent.IS_CAMERA_RECORDING)
    public boolean isCameraRecording(String event, String data) {
        return ((ICameraCaller) this.mQueryCaller).isCameraRecording();
    }
}
