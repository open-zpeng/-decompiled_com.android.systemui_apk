package com.xiaopeng.speech.protocol.node.camera;

import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.protocol.bean.ChangeValue;
import com.xiaopeng.speech.protocol.event.CameraEvent;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class CameraNode extends SpeechNode<CameraListener> {
    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CameraEvent.OVERALL_ON)
    public void onOverallOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        boolean isTTS = true;
        try {
            JSONObject jsonObject = new JSONObject(data);
            isTTS = jsonObject.optBoolean("isTTS", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onOverallOn(isTTS);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CameraEvent.REAR_TAKE)
    public void onRearTake(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        boolean isTTS = true;
        try {
            JSONObject jsonObject = new JSONObject(data);
            isTTS = jsonObject.optBoolean("isTTS", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onRearTake(isTTS);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CameraEvent.REAR_RECORD)
    public void onRearRecord(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        boolean isTTS = true;
        try {
            JSONObject jsonObject = new JSONObject(data);
            isTTS = jsonObject.optBoolean("isTTS", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onRearRecord(isTTS);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CameraEvent.FRONT_TAKE)
    public void onFrontTake(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onFrontTake();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CameraEvent.FRONT_RECORD)
    public void onFrontRecord(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onFrontRecord();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CameraEvent.LEFT_TAKE)
    public void onLeftTake(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onLeftTake();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CameraEvent.LEFT_RECORD)
    public void onLeftRecord(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onLeftRecord();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CameraEvent.RIGHT_TAKE)
    public void onRightTake(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onRightTake();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CameraEvent.RIGHT_RECORD)
    public void onRightRecord(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onRightRecord();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CameraEvent.LEFT_ON)
    public void onLeftOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onLeftOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CameraEvent.RIGHT_ON)
    public void onRightOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onRightOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CameraEvent.REAR_ON)
    public void onRearOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onRearOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CameraEvent.REAR_ON_NEW)
    public void onRearOnNew(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onRearOnNew();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CameraEvent.FRONT_ON)
    public void onFrontOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onFrontOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CameraEvent.REAR_OFF)
    public void onRearOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onRearOff();
            }
        }
    }

    protected void onCarcorderTake(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onCarcorderTake();
            }
        }
    }

    protected void onCarcorderLock(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onCarcorderLock();
            }
        }
    }

    protected void onCarcorderRecord(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onCarcorderRecord();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CameraEvent.TOP_OFF)
    public void onTopOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        boolean isTTS = true;
        try {
            JSONObject jsonObject = new JSONObject(data);
            isTTS = jsonObject.optBoolean("isTTS", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onTopOff(isTTS);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CameraEvent.TOP_ON)
    public void onTopOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        boolean isTTS = true;
        try {
            JSONObject jsonObject = new JSONObject(data);
            isTTS = jsonObject.optBoolean("isTTS", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onTopOn(isTTS);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CameraEvent.TOP_TAKE)
    public void onTopTake(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onTopTake();
            }
        }
    }

    protected void onTopRecord(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onTopRecord();
            }
        }
    }

    protected void onTopRecordEnd(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onTopRecordEnd();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CameraEvent.TOP_ROTATE_LEFT)
    public void onTopRotateLeft(String event, String data) {
        ChangeValue changeValue = ChangeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        boolean isTTS = true;
        try {
            JSONObject jsonObject = new JSONObject(data);
            isTTS = jsonObject.optBoolean("isTTS", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onTopRotateLeft(changeValue, isTTS);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CameraEvent.TOP_ROTATE_RIGHT)
    public void onTopRotateRight(String event, String data) {
        ChangeValue changeValue = ChangeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        boolean isTTS = true;
        try {
            JSONObject jsonObject = new JSONObject(data);
            isTTS = jsonObject.optBoolean("isTTS", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onTopRotateRight(changeValue, isTTS);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CameraEvent.TOP_ROTATE_FRONT)
    public void onTopRotateFront(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        boolean isTTS = true;
        try {
            JSONObject jsonObject = new JSONObject(data);
            isTTS = jsonObject.optBoolean("isTTS", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onTopRotateFront(isTTS);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CameraEvent.TOP_ROTATE_REAR)
    public void onTopRotateRear(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        boolean isTTS = true;
        try {
            JSONObject jsonObject = new JSONObject(data);
            isTTS = jsonObject.optBoolean("isTTS", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onTopRotateRear(isTTS);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CameraEvent.TRANSPARENT_CHASSIS_CMD)
    public void onTransparentChassisCMD(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onTransparentChassisCMD(data);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CameraEvent.CAMERA_THREE_D_ON)
    public void onCameraThreeDOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onCameraThreeDOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CameraEvent.CAMERA_THREE_D_OFF)
    public void onCameraThreeDOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onCameraThreeDOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CameraEvent.CAMERA_TRANSPARENT_CHASSIS_ON)
    public void onCameraTransparentChassisOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onCameraTransparentChassisOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CameraEvent.CAMERA_TRANSPARENT_CHASSIS_OFF)
    public void onCameraTransparentChassisOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onCameraTransparentChassisOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = CameraEvent.CAMERA_PHOTOALBUM_OPEN)
    public void onCameraPhotoalbumOpen(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((CameraListener) obj).onCameraPhotoalbumOpen();
            }
        }
    }
}
