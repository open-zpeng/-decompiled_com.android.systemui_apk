package com.xiaopeng.speech.protocol.query.speech.camera;

import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.speech.SpeechCameraEvent;
/* loaded from: classes23.dex */
public class SpeechCameraQuery_Processor implements IQueryProcessor {
    private SpeechCameraQuery mTarget;

    public SpeechCameraQuery_Processor(SpeechCameraQuery target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -2010416950:
                if (event.equals(SpeechCameraEvent.CAMERA_ANGLE)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 137950515:
                if (event.equals(SpeechCameraEvent.CAMERA_HEIGHT_STATE)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 753266552:
                if (event.equals(SpeechCameraEvent.CAMERA_DISPLAY_MODE)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1558434298:
                if (event.equals(SpeechCameraEvent.CAMERA_ROOF_POSITION)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 1886102880:
                if (event.equals(SpeechCameraEvent.CAMERA_ROOF_STATE)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 1936425711:
                if (event.equals(SpeechCameraEvent.CAMERA_HAS_PANO)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 1936498769:
                if (event.equals(SpeechCameraEvent.CAMERA_HAS_ROOF)) {
                    c = 4;
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
                return Integer.valueOf(this.mTarget.getCameraAngle(event, data));
            case 1:
                return Boolean.valueOf(this.mTarget.getCameraHeight(event, data));
            case 2:
                return Integer.valueOf(this.mTarget.getCameraDisplayMode(event, data));
            case 3:
                return Boolean.valueOf(this.mTarget.getHasPanoCamera(event, data));
            case 4:
                return Boolean.valueOf(this.mTarget.getHasRoofCamera(event, data));
            case 5:
                return Integer.valueOf(this.mTarget.getRoofCameraState(event, data));
            case 6:
                return Integer.valueOf(this.mTarget.getRoofCameraPosition(event, data));
            default:
                return null;
        }
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{SpeechCameraEvent.CAMERA_ANGLE, SpeechCameraEvent.CAMERA_HEIGHT_STATE, SpeechCameraEvent.CAMERA_DISPLAY_MODE, SpeechCameraEvent.CAMERA_HAS_PANO, SpeechCameraEvent.CAMERA_HAS_ROOF, SpeechCameraEvent.CAMERA_ROOF_STATE, SpeechCameraEvent.CAMERA_ROOF_POSITION};
    }
}
