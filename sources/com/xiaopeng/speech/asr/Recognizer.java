package com.xiaopeng.speech.asr;

import android.os.RemoteException;
import com.xiaopeng.speech.ConnectManager;
import com.xiaopeng.speech.ISpeechEngine;
import com.xiaopeng.speech.SpeechClient;
import com.xiaopeng.speech.common.util.IPCRunner;
import com.xiaopeng.speech.common.util.LogUtils;
import com.xiaopeng.speech.common.util.WorkerHandler;
/* loaded from: classes23.dex */
public class Recognizer {
    public static final String ASR_BUFFER = "otp_asr_buffer";
    public static final String AUDIO_EXTERNAL = "otp_external_audio";
    public static final String AUDIO_FORMAT = "otp_audio_format";
    public static final String AUDIO_SAVE_PATH = "otp_audio_save_path";
    public static final String BOS = "otp_bos";
    public static final String DISABLE_ASR = "otp_disable_asr";
    public static final String DISABLE_VAD = "otp_disable_vad";
    public static final String ENABLE_ASR_PUNCT = "otp_enable_punct";
    public static final String EOS = "otp_eos";
    public static final int EXT_ASR_AUDIO = 2;
    public static final int EXT_VOL = 1;
    public static final int FMT_AMR = 1;
    public static final int FMT_PCM = 0;
    public static final String KEEP_AUDIO_RECORD = "otp_keep_audio_record";
    public static final String MAX_ACTIVE_TIME = "otp_max_time";
    public static final int STATE_ASR_END = 5;
    public static final int STATE_BOS = 1;
    public static final int STATE_BOS_TIMEOUT = 3;
    public static final int STATE_END = 6;
    public static final int STATE_EOS = 2;
    public static final int STATE_RECORD_END = 4;
    public static final int STATE_RECORD_START = 0;
    private final WorkerHandler handler;
    private volatile IRecognizer iRecognizer;
    private volatile RecognizeListenerImpl listenerImpl;
    private final String TAG = "Recognizer";
    private IPCRunner<IRecognizer> ipcRunner = new IPCRunner<>("IRecognizerProxy");
    private final ConnectManager.OnConnectCallback connectCallback = new ConnectManager.OnConnectCallback() { // from class: com.xiaopeng.speech.asr.Recognizer.8
        @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
        public void onConnect(ISpeechEngine speechEngine) {
            LogUtils.i("Recognizer", "on connect");
            try {
                IRecognizer iRecog = speechEngine.getRecognizer();
                Recognizer.this.ipcRunner.setProxy(iRecog);
                Recognizer.this.iRecognizer = iRecog;
            } catch (Throwable th) {
                LogUtils.e("Recognizer", "on connect error");
            }
        }

        @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
        public void onDisconnect() {
            Recognizer.this.ipcRunner.setProxy(null);
            Recognizer.this.iRecognizer = null;
        }
    };

    public Recognizer(WorkerHandler handler) {
        this.handler = handler;
        this.ipcRunner.setWorkerHandler(handler);
    }

    public void startListening(final RecognizeListener listener) {
        this.ipcRunner.runFunc(new IPCRunner.IIPCFunc<IRecognizer, Object>() { // from class: com.xiaopeng.speech.asr.Recognizer.1
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IRecognizer iRecognizer) throws RemoteException {
                if (SpeechClient.instance().getSpeechState().isDMStarted()) {
                    SpeechClient.instance().getWakeupEngine().stopDialog();
                }
                Recognizer.this._startListening(listener);
                return null;
            }
        });
    }

    public void stopListening() {
        this.ipcRunner.runFunc(new IPCRunner.IIPCFunc<IRecognizer, Object>() { // from class: com.xiaopeng.speech.asr.Recognizer.2
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IRecognizer iRecognizer) throws RemoteException {
                Recognizer.this._stopListening();
                return null;
            }
        });
    }

    public boolean isListening() {
        try {
            return _isListening();
        } catch (Throwable e) {
            LogUtils.e("Recognizer", "error: ", e);
            return false;
        }
    }

    public void cancel() {
        this.ipcRunner.runFunc(new IPCRunner.IIPCFunc<IRecognizer, Object>() { // from class: com.xiaopeng.speech.asr.Recognizer.3
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IRecognizer iRecognizer) throws RemoteException {
                Recognizer.this._cancel();
                return null;
            }
        });
    }

    public void writeData(byte[] data, int offset, int length) {
        try {
            _writeData(data, offset, length);
        } catch (RemoteException e) {
            LogUtils.e("Recognizer", "write data error: ", e);
        }
    }

    public void setString(final String key, final String value) {
        this.ipcRunner.runFunc(new IPCRunner.IIPCFunc<IRecognizer, Object>() { // from class: com.xiaopeng.speech.asr.Recognizer.4
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IRecognizer iRecognizer) throws RemoteException {
                Recognizer.this._setString(key, value);
                return null;
            }
        });
    }

    public void setInt(final String key, final int value) {
        this.ipcRunner.runFunc(new IPCRunner.IIPCFunc<IRecognizer, Object>() { // from class: com.xiaopeng.speech.asr.Recognizer.5
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IRecognizer iRecognizer) throws RemoteException {
                Recognizer.this._setInt(key, value);
                return null;
            }
        });
    }

    public void setBool(final String key, final boolean value) {
        this.ipcRunner.runFunc(new IPCRunner.IIPCFunc<IRecognizer, Object>() { // from class: com.xiaopeng.speech.asr.Recognizer.6
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IRecognizer iRecognizer) throws RemoteException {
                Recognizer.this._setBool(key, value);
                return null;
            }
        });
    }

    public void setDouble(final String key, final double value) {
        this.ipcRunner.runFunc(new IPCRunner.IIPCFunc<IRecognizer, Object>() { // from class: com.xiaopeng.speech.asr.Recognizer.7
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IRecognizer iRecognizer) throws RemoteException {
                Recognizer.this._setDouble(key, value);
                return null;
            }
        });
    }

    public ConnectManager.OnConnectCallback getConnectCallback() {
        return this.connectCallback;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _startListening(RecognizeListener listener) throws RemoteException {
        LogUtils.i("Recognizer", "startListening with RecognizeListener");
        IRecognizer recognizer = this.iRecognizer;
        if (recognizer == null) {
            LogUtils.e("Recognizer", "service is disconnected");
            listener.onError(2003, "service is disconnected");
        } else if (isListening()) {
            LogUtils.e("Recognizer", "last asr still running");
            listener.onError(2003, "last asr still running");
        } else {
            this.listenerImpl = new RecognizeListenerImpl(listener);
            recognizer.startListening(this.listenerImpl);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _stopListening() throws RemoteException {
        LogUtils.i("Recognizer", "stop listening");
        IRecognizer recognizer = this.iRecognizer;
        if (recognizer != null && isListening()) {
            recognizer.stopListening();
        }
    }

    private boolean _isListening() throws RemoteException {
        IRecognizer recognizer = this.iRecognizer;
        return recognizer != null && recognizer.isListening();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _cancel() throws RemoteException {
        LogUtils.i("Recognizer", "cancel");
        IRecognizer recognizer = this.iRecognizer;
        if (recognizer != null && isListening()) {
            recognizer.cancel();
        }
    }

    private void _writeData(byte[] data, int offset, int length) throws RemoteException {
        IRecognizer recognizer = this.iRecognizer;
        if (recognizer != null && isListening()) {
            recognizer.writeData(data, offset, length);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _setString(String key, String value) throws RemoteException {
        IRecognizer recognizer = this.iRecognizer;
        if (recognizer != null) {
            recognizer.setString(key, value);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _setInt(String key, int value) throws RemoteException {
        IRecognizer recognizer = this.iRecognizer;
        if (recognizer != null) {
            recognizer.setInt(key, value);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _setBool(String key, boolean value) throws RemoteException {
        IRecognizer recognizer = this.iRecognizer;
        if (recognizer != null) {
            recognizer.setBool(key, value);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void _setDouble(String key, double value) throws RemoteException {
        IRecognizer recognizer = this.iRecognizer;
        if (recognizer != null) {
            recognizer.setDouble(key, value);
        }
    }
}
