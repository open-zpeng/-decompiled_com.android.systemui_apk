package com.xiaopeng.speech.protocol.node.dialog;

import android.text.TextUtils;
import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.jarvisproto.DMActive;
import com.xiaopeng.speech.jarvisproto.DMExit;
import com.xiaopeng.speech.jarvisproto.DMPrepare;
import com.xiaopeng.speech.jarvisproto.DMWait;
import com.xiaopeng.speech.jarvisproto.DialogSoundAreaStatus;
import com.xiaopeng.speech.jarvisproto.SoundAreaStatus;
import com.xiaopeng.speech.protocol.event.DialogEvent;
import com.xiaopeng.speech.protocol.node.dialog.bean.DialogActiveReason;
import com.xiaopeng.speech.protocol.node.dialog.bean.DialogEndReason;
import com.xiaopeng.speech.protocol.node.dialog.bean.DialogExitReason;
import com.xiaopeng.speech.protocol.node.dialog.bean.PrepareReason;
import com.xiaopeng.speech.protocol.node.dialog.bean.WakeupReason;
/* loaded from: classes23.dex */
public class DialogNode extends SpeechNode<DialogListener> {
    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "jarvis.dm.start")
    public void onDialogStart(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        WakeupReason wakeupReason = WakeupReason.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((DialogListener) obj).onDialogStart(wakeupReason);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = DMActive.EVENT)
    public void onDialogActive(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        DialogActiveReason activeReason = DialogActiveReason.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((DialogListener) obj).onDialogActive(activeReason);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = DMPrepare.EVENT)
    public void onDialogPrepare(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        PrepareReason activeReason = PrepareReason.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((DialogListener) obj).onDialogPrepare(activeReason);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = DialogEvent.DIALOG_ERROR)
    public void onDialogError(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((DialogListener) obj).onDialogError();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = DMExit.EVENT)
    public void onDialogExit(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        DialogExitReason endReason = DialogExitReason.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((DialogListener) obj).onDialogExit(endReason);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = "jarvis.dm.end")
    public void onDialogEnd(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        DialogEndReason endReason = DialogEndReason.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((DialogListener) obj).onDialogEnd(endReason);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = DMWait.EVENT)
    public void onDialogWait(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        DMWait waitReason = new DMWait();
        if (!TextUtils.isEmpty(data)) {
            waitReason = DMWait.fromJson(data);
        }
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((DialogListener) obj).onDialogWait(waitReason);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = DialogEvent.DIALOG_CONTINUE)
    public void onDialogContinue(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((DialogListener) obj).onDialogContinue();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = DialogEvent.WAKEUP_RESULT)
    public void onWakeupResult(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((DialogListener) obj).onWakeupResult();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = DialogEvent.VAD_BEGIN)
    public void onVadBegin(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((DialogListener) obj).onVadBegin();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = DialogEvent.VAD_END)
    public void onVadEnd(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((DialogListener) obj).onVadEnd();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = DialogEvent.DIALOG_STATUS_CHANGED)
    public void onDialogStatusChanged(String event, String status) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((DialogListener) obj).onDialogStatusChanged(status);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SoundAreaStatus.EVENT)
    public void onSoundAreaStatusChanged(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        SoundAreaStatus soundAreaStatus = SoundAreaStatus.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((DialogListener) obj).onSoundAreaStatusChanged(soundAreaStatus);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = DialogSoundAreaStatus.EVENT)
    public void onDialogState(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        DialogSoundAreaStatus state = DialogSoundAreaStatus.fromJson(data);
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((DialogListener) obj).onDialogSoundAreaStatusChanged(state);
            }
        }
    }
}
