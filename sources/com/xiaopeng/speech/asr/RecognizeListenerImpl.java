package com.xiaopeng.speech.asr;

import android.os.RemoteException;
import com.xiaopeng.speech.asr.IRecognizeListener;
/* loaded from: classes23.dex */
class RecognizeListenerImpl extends IRecognizeListener.Stub {
    private final RecognizeListener listener;

    public RecognizeListenerImpl(RecognizeListener listener) {
        this.listener = listener;
    }

    @Override // com.xiaopeng.speech.asr.IRecognizeListener
    public void onResult(String result, boolean last) throws RemoteException {
        this.listener.onResult(result, last);
    }

    @Override // com.xiaopeng.speech.asr.IRecognizeListener
    public void onError(int code, String extraInfo) throws RemoteException {
        this.listener.onError(code, extraInfo);
    }

    @Override // com.xiaopeng.speech.asr.IRecognizeListener
    public void onState(int state, int extra) throws RemoteException {
        this.listener.onState(state, extra);
    }

    @Override // com.xiaopeng.speech.asr.IRecognizeListener
    public void onExtra(int type, int arg1, int arg2, String info, byte[] data) throws RemoteException {
        this.listener.onExtra(type, arg1, arg2, info, data);
    }
}
