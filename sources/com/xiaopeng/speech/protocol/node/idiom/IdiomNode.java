package com.xiaopeng.speech.protocol.node.idiom;

import android.os.Binder;
import android.os.Handler;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.common.util.LogUtils;
/* loaded from: classes23.dex */
public class IdiomNode extends SpeechNode<IIdiomListener> {
    private String TAG = "IdiomNode";
    private Binder mBinder = new Binder();
    private Binder mTTSBinder = new Binder();
    private Handler mHandler = new Handler();
    private volatile boolean mIsNeedResetSoundLock = false;

    public void setVadTimeout(long milliseconds) {
        SpeechClient.instance().getASREngine().setVadTimeout(milliseconds);
    }

    public void setVadPauseTime(long milliseconds) {
        SpeechClient.instance().getASREngine().setVadPauseTime(milliseconds);
    }

    public void resetVadTime() {
        SpeechClient.instance().getASREngine().setVadPauseTime(500L);
        SpeechClient.instance().getASREngine().setVadTimeout(7000L);
    }

    public void setSingleTTS(boolean state) {
        SpeechClient.instance().getTTSEngine().setSingleTTS(state, this.mBinder);
    }

    public String speakSingleTTS(String text) {
        return SpeechClient.instance().getTTSEngine().speak(text, 3);
    }

    public void shutupTTS(String ttsId) {
        SpeechClient.instance().getTTSEngine().shutup(ttsId);
    }

    public void startListening() {
        SpeechClient.instance().getASREngine().startListen();
    }

    public void cancelListening() {
        SpeechClient.instance().getASREngine().cancelListen();
    }

    public void disableWakeup() {
        this.mHandler.postDelayed(new Runnable() { // from class: com.xiaopeng.speech.protocol.node.idiom.IdiomNode.1
            @Override // java.lang.Runnable
            public void run() {
                LogUtils.i(IdiomNode.this.TAG, "disable wake up.");
                SpeechClient.instance().getWakeupEngine().stopDialog();
                SpeechClient.instance().getWakeupEngine().disableWakeupEnhance(IdiomNode.this.mBinder);
                SpeechClient.instance().getWakeupEngine().setWheelWakeupEnabled(IdiomNode.this.mBinder, false);
                if (SpeechClient.instance().getSoundLockState().getMode() == 0) {
                    IdiomNode.this.mIsNeedResetSoundLock = true;
                    SpeechClient.instance().getSoundLockState().setMode(1);
                    SpeechClient.instance().getSoundLockState().setDriveSoundLocation(0);
                }
            }
        }, 1000L);
    }

    public void enableWakeup() {
        String str = this.TAG;
        LogUtils.i(str, "enable wake up. Need reset: " + this.mIsNeedResetSoundLock);
        SpeechClient.instance().getWakeupEngine().enableWakeupEnhance(this.mBinder);
        SpeechClient.instance().getWakeupEngine().setWheelWakeupEnabled(this.mBinder, true);
        if (this.mIsNeedResetSoundLock) {
            this.mIsNeedResetSoundLock = false;
            SpeechClient.instance().getSoundLockState().initSoundConfig();
        }
    }
}
