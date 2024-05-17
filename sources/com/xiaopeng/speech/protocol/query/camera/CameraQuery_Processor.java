package com.xiaopeng.speech.protocol.query.camera;

import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.QueryCameraEvent;
/* loaded from: classes23.dex */
public class CameraQuery_Processor implements IQueryProcessor {
    private CameraQuery mTarget;

    public CameraQuery_Processor(CameraQuery target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -1268994260:
                if (event.equals(QueryCameraEvent.GET_CAMERA_THREE_D_SUPPORT)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -504669293:
                if (event.equals(QueryCameraEvent.GET_SUPPORT_TOP_STATUS)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -94871978:
                if (event.equals(QueryCameraEvent.IS_CAMERA_RECORDING)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -9645022:
                if (event.equals(QueryCameraEvent.GET_CAMERA_TRANSPARENT_CHASSIS_SUPPORT)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 180040983:
                if (event.equals(QueryCameraEvent.GET_BUSINESS_CAMERA_STATUS)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 1273362324:
                if (event.equals(QueryCameraEvent.GET_SUPPORT_REAR_STATUS)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1391541314:
                if (event.equals(QueryCameraEvent.GET_SUPPORT_PANORAMIC_STATUS)) {
                    c = 1;
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
                return Integer.valueOf(this.mTarget.getSupportTopStatus(event, data));
            case 1:
                return Integer.valueOf(this.mTarget.getSupportPanoramicStatus(event, data));
            case 2:
                return Integer.valueOf(this.mTarget.getSupportRearStatus(event, data));
            case 3:
                return Integer.valueOf(this.mTarget.getBusinessCameraStatus(event, data));
            case 4:
                return Integer.valueOf(this.mTarget.getCameraThreeDSupport(event, data));
            case 5:
                return Integer.valueOf(this.mTarget.getCameraTransparentChassisSupport(event, data));
            case 6:
                return Boolean.valueOf(this.mTarget.isCameraRecording(event, data));
            default:
                return null;
        }
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{QueryCameraEvent.GET_SUPPORT_TOP_STATUS, QueryCameraEvent.GET_SUPPORT_PANORAMIC_STATUS, QueryCameraEvent.GET_SUPPORT_REAR_STATUS, QueryCameraEvent.GET_BUSINESS_CAMERA_STATUS, QueryCameraEvent.GET_CAMERA_THREE_D_SUPPORT, QueryCameraEvent.GET_CAMERA_TRANSPARENT_CHASSIS_SUPPORT, QueryCameraEvent.IS_CAMERA_RECORDING};
    }
}
