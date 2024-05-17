package com.xiaopeng.speech.protocol.node.camera;

import com.alibaba.fastjson.parser.JSONLexer;
import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.CameraEvent;
/* loaded from: classes23.dex */
public class CameraNode_Processor implements ICommandProcessor {
    private CameraNode mTarget;

    public CameraNode_Processor(CameraNode target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -1960069370:
                if (event.equals(CameraEvent.FRONT_TAKE)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -1876486015:
                if (event.equals(CameraEvent.CAMERA_PHOTOALBUM_OPEN)) {
                    c = 27;
                    break;
                }
                c = 65535;
                break;
            case -1734758414:
                if (event.equals(CameraEvent.TOP_ON)) {
                    c = 16;
                    break;
                }
                c = 65535;
                break;
            case -1275719440:
                if (event.equals(CameraEvent.TRANSPARENT_CHASSIS_CMD)) {
                    c = 22;
                    break;
                }
                c = 65535;
                break;
            case -1212502159:
                if (event.equals(CameraEvent.CAMERA_TRANSPARENT_CHASSIS_OFF)) {
                    c = JSONLexer.EOI;
                    break;
                }
                c = 65535;
                break;
            case -1080240309:
                if (event.equals(CameraEvent.RIGHT_ON)) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case -1041501812:
                if (event.equals(CameraEvent.LEFT_ON)) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case -886954978:
                if (event.equals(CameraEvent.FRONT_ON)) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case -655385126:
                if (event.equals(CameraEvent.TOP_TAKE)) {
                    c = 17;
                    break;
                }
                c = 65535;
                break;
            case -499850625:
                if (event.equals(CameraEvent.REAR_OFF)) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case -392525311:
                if (event.equals(CameraEvent.REAR_ON_NEW)) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case -313369279:
                if (event.equals(CameraEvent.REAR_RECORD)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -155721484:
                if (event.equals(CameraEvent.LEFT_TAKE)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case -16124209:
                if (event.equals(CameraEvent.REAR_ON)) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case 184229895:
                if (event.equals(CameraEvent.TOP_ROTATE_LEFT)) {
                    c = 18;
                    break;
                }
                c = 65535;
                break;
            case 184408484:
                if (event.equals(CameraEvent.TOP_ROTATE_REAR)) {
                    c = 21;
                    break;
                }
                c = 65535;
                break;
            case 446223610:
                if (event.equals(CameraEvent.OVERALL_ON)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 621719934:
                if (event.equals(CameraEvent.LEFT_RECORD)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 1207813021:
                if (event.equals(CameraEvent.CAMERA_TRANSPARENT_CHASSIS_ON)) {
                    c = 25;
                    break;
                }
                c = 65535;
                break;
            case 1271288563:
                if (event.equals(CameraEvent.RIGHT_TAKE)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 1411014185:
                if (event.equals(CameraEvent.TOP_ROTATE_FRONT)) {
                    c = 20;
                    break;
                }
                c = 65535;
                break;
            case 1421820444:
                if (event.equals(CameraEvent.TOP_ROTATE_RIGHT)) {
                    c = 19;
                    break;
                }
                c = 65535;
                break;
            case 1584286675:
                if (event.equals(CameraEvent.CAMERA_THREE_D_ON)) {
                    c = 23;
                    break;
                }
                c = 65535;
                break;
            case 1684644215:
                if (event.equals(CameraEvent.REAR_TAKE)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1810189072:
                if (event.equals(CameraEvent.FRONT_RECORD)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 1868246523:
                if (event.equals(CameraEvent.CAMERA_THREE_D_OFF)) {
                    c = 24;
                    break;
                }
                c = 65535;
                break;
            case 1883807677:
                if (event.equals(CameraEvent.RIGHT_RECORD)) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case 2057063868:
                if (event.equals(CameraEvent.TOP_OFF)) {
                    c = 15;
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
                this.mTarget.onOverallOn(event, data);
                return;
            case 1:
                this.mTarget.onRearTake(event, data);
                return;
            case 2:
                this.mTarget.onRearRecord(event, data);
                return;
            case 3:
                this.mTarget.onFrontTake(event, data);
                return;
            case 4:
                this.mTarget.onFrontRecord(event, data);
                return;
            case 5:
                this.mTarget.onLeftTake(event, data);
                return;
            case 6:
                this.mTarget.onLeftRecord(event, data);
                return;
            case 7:
                this.mTarget.onRightTake(event, data);
                return;
            case '\b':
                this.mTarget.onRightRecord(event, data);
                return;
            case '\t':
                this.mTarget.onLeftOn(event, data);
                return;
            case '\n':
                this.mTarget.onRightOn(event, data);
                return;
            case 11:
                this.mTarget.onRearOn(event, data);
                return;
            case '\f':
                this.mTarget.onRearOnNew(event, data);
                return;
            case '\r':
                this.mTarget.onFrontOn(event, data);
                return;
            case 14:
                this.mTarget.onRearOff(event, data);
                return;
            case 15:
                this.mTarget.onTopOff(event, data);
                return;
            case 16:
                this.mTarget.onTopOn(event, data);
                return;
            case 17:
                this.mTarget.onTopTake(event, data);
                return;
            case 18:
                this.mTarget.onTopRotateLeft(event, data);
                return;
            case 19:
                this.mTarget.onTopRotateRight(event, data);
                return;
            case 20:
                this.mTarget.onTopRotateFront(event, data);
                return;
            case 21:
                this.mTarget.onTopRotateRear(event, data);
                return;
            case 22:
                this.mTarget.onTransparentChassisCMD(event, data);
                return;
            case 23:
                this.mTarget.onCameraThreeDOn(event, data);
                return;
            case 24:
                this.mTarget.onCameraThreeDOff(event, data);
                return;
            case 25:
                this.mTarget.onCameraTransparentChassisOn(event, data);
                return;
            case 26:
                this.mTarget.onCameraTransparentChassisOff(event, data);
                return;
            case 27:
                this.mTarget.onCameraPhotoalbumOpen(event, data);
                return;
            default:
                return;
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{CameraEvent.OVERALL_ON, CameraEvent.REAR_TAKE, CameraEvent.REAR_RECORD, CameraEvent.FRONT_TAKE, CameraEvent.FRONT_RECORD, CameraEvent.LEFT_TAKE, CameraEvent.LEFT_RECORD, CameraEvent.RIGHT_TAKE, CameraEvent.RIGHT_RECORD, CameraEvent.LEFT_ON, CameraEvent.RIGHT_ON, CameraEvent.REAR_ON, CameraEvent.REAR_ON_NEW, CameraEvent.FRONT_ON, CameraEvent.REAR_OFF, CameraEvent.TOP_OFF, CameraEvent.TOP_ON, CameraEvent.TOP_TAKE, CameraEvent.TOP_ROTATE_LEFT, CameraEvent.TOP_ROTATE_RIGHT, CameraEvent.TOP_ROTATE_FRONT, CameraEvent.TOP_ROTATE_REAR, CameraEvent.TRANSPARENT_CHASSIS_CMD, CameraEvent.CAMERA_THREE_D_ON, CameraEvent.CAMERA_THREE_D_OFF, CameraEvent.CAMERA_TRANSPARENT_CHASSIS_ON, CameraEvent.CAMERA_TRANSPARENT_CHASSIS_OFF, CameraEvent.CAMERA_PHOTOALBUM_OPEN};
    }
}
