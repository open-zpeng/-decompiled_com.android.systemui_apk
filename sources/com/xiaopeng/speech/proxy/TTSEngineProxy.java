package com.xiaopeng.speech.proxy;

import android.os.IBinder;
import android.os.RemoteException;
import com.xiaopeng.speech.ConnectManager;
import com.xiaopeng.speech.ISpeechEngine;
import com.xiaopeng.speech.common.SpeechConstant;
import com.xiaopeng.speech.common.util.IPCRunner;
import com.xiaopeng.speech.common.util.LogUtils;
import com.xiaopeng.speech.common.util.WorkerHandler;
import com.xiaopeng.speech.coreapi.ITTSCallback;
import com.xiaopeng.speech.coreapi.ITTSEngine;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
/* loaded from: classes23.dex */
public class TTSEngineProxy extends ITTSEngine.Stub implements ConnectManager.OnConnectCallback {
    private WorkerHandler mWorkerHandler;
    private Map<String, ISpeakCallback> mSpeakCallbackMap = new ConcurrentHashMap();
    private volatile boolean mHadSetCallback = false;
    private IPCRunner<ITTSEngine> mIpcRunner = new IPCRunner<>("TTSEngineProxy");
    private ITTSCallback mTTSCallback = new ITTSCallback.Stub() { // from class: com.xiaopeng.speech.proxy.TTSEngineProxy.1
        @Override // com.xiaopeng.speech.coreapi.ITTSCallback
        public void beginning(final String ttsId, final String flag) {
            TTSEngineProxy.this.mWorkerHandler.optPost(new Runnable() { // from class: com.xiaopeng.speech.proxy.TTSEngineProxy.1.1
                @Override // java.lang.Runnable
                public void run() {
                    ISpeakCallback speakCallback = (ISpeakCallback) TTSEngineProxy.this.mSpeakCallbackMap.get(ttsId);
                    if (speakCallback != null) {
                        speakCallback.beginning(ttsId, flag);
                    }
                }
            });
        }

        @Override // com.xiaopeng.speech.coreapi.ITTSCallback
        public void received(final byte[] data) {
            TTSEngineProxy.this.mWorkerHandler.optPost(new Runnable() { // from class: com.xiaopeng.speech.proxy.TTSEngineProxy.1.2
                @Override // java.lang.Runnable
                public void run() {
                    for (Map.Entry<String, ISpeakCallback> entry : TTSEngineProxy.this.mSpeakCallbackMap.entrySet()) {
                        entry.getValue().received(data);
                    }
                }
            });
        }

        @Override // com.xiaopeng.speech.coreapi.ITTSCallback
        public void end(final String ttsId, final int status, final String flag) {
            TTSEngineProxy.this.mWorkerHandler.optPost(new Runnable() { // from class: com.xiaopeng.speech.proxy.TTSEngineProxy.1.3
                @Override // java.lang.Runnable
                public void run() {
                    ISpeakCallback speakCallback = (ISpeakCallback) TTSEngineProxy.this.mSpeakCallbackMap.get(ttsId);
                    if (speakCallback != null) {
                        speakCallback.end(ttsId, status, flag);
                        TTSEngineProxy.this.mSpeakCallbackMap.remove(ttsId);
                    }
                }
            });
        }

        @Override // com.xiaopeng.speech.coreapi.ITTSCallback
        public void error(final String error, final String flag) {
            TTSEngineProxy.this.mWorkerHandler.optPost(new Runnable() { // from class: com.xiaopeng.speech.proxy.TTSEngineProxy.1.4
                @Override // java.lang.Runnable
                public void run() {
                    Iterator<Map.Entry<String, ISpeakCallback>> iterator = TTSEngineProxy.this.mSpeakCallbackMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        iterator.next().getValue().error(error, flag);
                        iterator.remove();
                    }
                }
            });
        }
    };

    public void setHandler(WorkerHandler workerHandler) {
        this.mWorkerHandler = workerHandler;
        this.mIpcRunner.setWorkerHandler(this.mWorkerHandler);
    }

    public String speak(final String text) {
        this.mWorkerHandler.optPostDelay(new Runnable() { // from class: com.xiaopeng.speech.proxy.TTSEngineProxy.2
            @Override // java.lang.Runnable
            public void run() {
                TTSEngineProxy.this.speak(text, 1, 3, -1, null);
            }
        }, 50L);
        return "";
    }

    public String speak(String text, int priority) {
        return speak(text, priority, 3);
    }

    public String speak(String text, int priority, int audioFocus) {
        return speak(text, priority, audioFocus, (ISpeakCallback) null);
    }

    public String speak(String text, ISpeakCallback speakCallback) {
        return speak(text, 1, speakCallback);
    }

    public String speak(String text, int priority, ISpeakCallback speakCallback) {
        return speak(text, priority, 3, speakCallback);
    }

    public String speak(String text, int priority, int audioFocus, ISpeakCallback callback) {
        return speak(text, priority, audioFocus, -1, callback);
    }

    public String speak(String text, int priority, int audioFocus, int streamType, ISpeakCallback callback) {
        String ttsId = UUID.randomUUID().toString();
        int ttsMode = SpeechConstant.TTS_DEFAULT_MODEL;
        speakEnhance(text, priority, ttsId, audioFocus, streamType, ttsMode, SpeechConstant.TTS_TIMEOUT);
        addTSSListener();
        if (callback != null) {
            this.mSpeakCallbackMap.put(ttsId, callback);
            LogUtils.i("speak callback size:" + this.mSpeakCallbackMap.size());
        }
        return ttsId;
    }

    public void removeSpeakCallback(String ttsId) {
        this.mSpeakCallbackMap.remove(ttsId);
    }

    @Override // com.xiaopeng.speech.coreapi.ITTSEngine
    public boolean speakEnhance(final String text, final int priority, final String ttsId, final int audioFocus, final int streamType, final int ttsMode, final long timeout) {
        return ((Boolean) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<ITTSEngine, Boolean>() { // from class: com.xiaopeng.speech.proxy.TTSEngineProxy.3
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Boolean run(ITTSEngine proxy) throws RemoteException {
                try {
                    return Boolean.valueOf(proxy.speakEnhance(text, priority, ttsId, audioFocus, streamType, ttsMode, timeout));
                } catch (Throwable e) {
                    LogUtils.e("IPC Excption:" + e.getMessage());
                    return false;
                }
            }
        }, false)).booleanValue();
    }

    @Override // com.xiaopeng.speech.coreapi.ITTSEngine
    public boolean speak(final String text, final int priority, final String ttsId, final int audioFocus) {
        return ((Boolean) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<ITTSEngine, Boolean>() { // from class: com.xiaopeng.speech.proxy.TTSEngineProxy.4
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Boolean run(ITTSEngine proxy) throws RemoteException {
                try {
                    return Boolean.valueOf(proxy.speak(text, priority, ttsId, audioFocus));
                } catch (Throwable e) {
                    LogUtils.e("IPC Excption:" + e.getMessage());
                    return false;
                }
            }
        }, false)).booleanValue();
    }

    @Override // com.xiaopeng.speech.coreapi.ITTSEngine
    public void setSpeaker(final String speaker) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<ITTSEngine, Object>() { // from class: com.xiaopeng.speech.proxy.TTSEngineProxy.5
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(ITTSEngine proxy) throws RemoteException {
                try {
                    proxy.setSpeaker(speaker);
                    return null;
                } catch (Throwable e) {
                    LogUtils.e("IPC Excption:" + e.getMessage());
                    return null;
                }
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.ITTSEngine
    public String getSpeaker() {
        return (String) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<ITTSEngine, String>() { // from class: com.xiaopeng.speech.proxy.TTSEngineProxy.6
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public String run(ITTSEngine proxy) throws RemoteException {
                try {
                    return proxy.getSpeaker();
                } catch (Throwable e) {
                    LogUtils.e("IPC Excption:" + e.getMessage());
                    return null;
                }
            }
        }, null);
    }

    @Override // com.xiaopeng.speech.coreapi.ITTSEngine
    public void setSpeed(final float speed) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<ITTSEngine, Object>() { // from class: com.xiaopeng.speech.proxy.TTSEngineProxy.7
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(ITTSEngine proxy) throws RemoteException {
                try {
                    proxy.setSpeed(speed);
                    return null;
                } catch (Throwable e) {
                    LogUtils.e("IPC Excption:" + e.getMessage());
                    return null;
                }
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.ITTSEngine
    public float getSpeed() {
        return ((Float) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<ITTSEngine, Float>() { // from class: com.xiaopeng.speech.proxy.TTSEngineProxy.8
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Float run(ITTSEngine proxy) throws RemoteException {
                try {
                    return Float.valueOf(proxy.getSpeed());
                } catch (Throwable e) {
                    LogUtils.e("IPC Excption:" + e.getMessage());
                    return Float.valueOf(0.0f);
                }
            }
        }, Float.valueOf(0.0f))).floatValue();
    }

    @Override // com.xiaopeng.speech.coreapi.ITTSEngine
    public void setVolume(final int volume) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<ITTSEngine, Object>() { // from class: com.xiaopeng.speech.proxy.TTSEngineProxy.9
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(ITTSEngine proxy) throws RemoteException {
                try {
                    proxy.setVolume(volume);
                    return null;
                } catch (Throwable e) {
                    LogUtils.e("IPC Excption:" + e.getMessage());
                    return null;
                }
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.ITTSEngine
    public int getVolume() {
        return ((Integer) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<ITTSEngine, Integer>() { // from class: com.xiaopeng.speech.proxy.TTSEngineProxy.10
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Integer run(ITTSEngine proxy) throws RemoteException {
                try {
                    return Integer.valueOf(proxy.getVolume());
                } catch (Throwable e) {
                    LogUtils.e("IPC Excption:" + e.getMessage());
                    return 0;
                }
            }
        }, 0)).intValue();
    }

    @Override // com.xiaopeng.speech.coreapi.ITTSEngine
    public void setMode(final int mode) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<ITTSEngine, Object>() { // from class: com.xiaopeng.speech.proxy.TTSEngineProxy.11
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(ITTSEngine proxy) throws RemoteException {
                try {
                    proxy.setMode(mode);
                    return null;
                } catch (Throwable e) {
                    LogUtils.e("IPC Excption:" + e.getMessage());
                    return null;
                }
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.ITTSEngine
    public void setSoloMode(final boolean enable) throws RemoteException {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<ITTSEngine, Object>() { // from class: com.xiaopeng.speech.proxy.TTSEngineProxy.12
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(ITTSEngine proxy) throws RemoteException {
                try {
                    proxy.setSoloMode(enable);
                    return null;
                } catch (Throwable e) {
                    LogUtils.e("IPC Excption:" + e.getMessage());
                    return null;
                }
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.ITTSEngine
    public void shutupByReason(final String ttsid, final String reason) throws RemoteException {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<ITTSEngine, Object>() { // from class: com.xiaopeng.speech.proxy.TTSEngineProxy.13
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(ITTSEngine proxy) throws RemoteException {
                try {
                    proxy.shutupByReason(ttsid, reason);
                    return null;
                } catch (Throwable e) {
                    LogUtils.e("IPC Excption:" + e.getMessage());
                    return null;
                }
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.ITTSEngine
    public boolean speakEnhanceByVoicePosition(final String text, final int priority, final String ttsId, final int audioFocus, final int streamType, final int ttsMode, final long timeout, final int position) throws RemoteException {
        return ((Boolean) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<ITTSEngine, Boolean>() { // from class: com.xiaopeng.speech.proxy.TTSEngineProxy.14
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Boolean run(ITTSEngine proxy) throws RemoteException {
                try {
                    return Boolean.valueOf(proxy.speakEnhanceByVoicePosition(text, priority, ttsId, audioFocus, streamType, ttsMode, timeout, position));
                } catch (Throwable e) {
                    LogUtils.e("IPC Excption:" + e.getMessage());
                    return false;
                }
            }
        }, false)).booleanValue();
    }

    @Override // com.xiaopeng.speech.coreapi.ITTSEngine
    public boolean speakParam(final String text, final String param) throws RemoteException {
        return ((Boolean) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<ITTSEngine, Boolean>() { // from class: com.xiaopeng.speech.proxy.TTSEngineProxy.15
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Boolean run(ITTSEngine proxy) throws RemoteException {
                try {
                    return Boolean.valueOf(proxy.speakParam(text, param));
                } catch (Throwable e) {
                    LogUtils.e("IPC Excption:" + e.getMessage());
                    return false;
                }
            }
        }, false)).booleanValue();
    }

    @Override // com.xiaopeng.speech.coreapi.ITTSEngine
    public void setSoloModeByChannel(final int channel, final boolean enable) throws RemoteException {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<ITTSEngine, Object>() { // from class: com.xiaopeng.speech.proxy.TTSEngineProxy.16
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(ITTSEngine proxy) throws RemoteException {
                try {
                    proxy.setSoloModeByChannel(channel, enable);
                    return null;
                } catch (Throwable e) {
                    LogUtils.e("IPC Excption:" + e.getMessage());
                    return null;
                }
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.ITTSEngine
    public void shutup(final String ttsid) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<ITTSEngine, Object>() { // from class: com.xiaopeng.speech.proxy.TTSEngineProxy.17
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(ITTSEngine proxy) throws RemoteException {
                try {
                    proxy.shutup(ttsid);
                    return null;
                } catch (Throwable e) {
                    LogUtils.e("IPC Excption:" + e.getMessage());
                    return null;
                }
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.ITTSEngine
    public void addListener(final ITTSCallback callback) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<ITTSEngine, Object>() { // from class: com.xiaopeng.speech.proxy.TTSEngineProxy.18
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(ITTSEngine proxy) throws RemoteException {
                try {
                    proxy.addListener(callback);
                    return null;
                } catch (Throwable e) {
                    LogUtils.e("IPC Excption:" + e.getMessage());
                    return null;
                }
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.ITTSEngine
    public void removeListener(final ITTSCallback callback) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<ITTSEngine, Object>() { // from class: com.xiaopeng.speech.proxy.TTSEngineProxy.19
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(ITTSEngine proxy) throws RemoteException {
                try {
                    proxy.removeListener(callback);
                    return null;
                } catch (Throwable e) {
                    LogUtils.e("IPC Excption:" + e.getMessage());
                    return null;
                }
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.ITTSEngine
    public void setSingleTTS(final boolean state, final IBinder binder) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<ITTSEngine, Object>() { // from class: com.xiaopeng.speech.proxy.TTSEngineProxy.20
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(ITTSEngine proxy) throws RemoteException {
                try {
                    if (binder == null) {
                        proxy.setSingleTTS(state, proxy.asBinder());
                    } else {
                        proxy.setSingleTTS(state, binder);
                    }
                    return null;
                } catch (Throwable e) {
                    LogUtils.e("IPC Excption:" + e.getMessage());
                    return null;
                }
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.ITTSEngine
    public boolean isSingleTTS() {
        return ((Boolean) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<ITTSEngine, Boolean>() { // from class: com.xiaopeng.speech.proxy.TTSEngineProxy.21
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Boolean run(ITTSEngine proxy) throws RemoteException {
                try {
                    return Boolean.valueOf(proxy.isSingleTTS());
                } catch (Throwable e) {
                    LogUtils.e("IPC Excption:" + e.getMessage());
                    return false;
                }
            }
        }, false)).booleanValue();
    }

    @Override // com.xiaopeng.speech.coreapi.ITTSEngine
    public boolean isTTSSupportSpell() {
        return ((Boolean) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<ITTSEngine, Boolean>() { // from class: com.xiaopeng.speech.proxy.TTSEngineProxy.22
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Boolean run(ITTSEngine proxy) throws RemoteException {
                try {
                    return Boolean.valueOf(proxy.isTTSSupportSpell());
                } catch (Throwable e) {
                    LogUtils.e("IPC Excption:" + e.getMessage());
                    return false;
                }
            }
        }, false)).booleanValue();
    }

    private synchronized void addTSSListener() {
        if (this.mHadSetCallback) {
            return;
        }
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<ITTSEngine, Object>() { // from class: com.xiaopeng.speech.proxy.TTSEngineProxy.23
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(ITTSEngine proxy) throws RemoteException {
                try {
                    proxy.addListener(TTSEngineProxy.this.mTTSCallback);
                    TTSEngineProxy.this.mHadSetCallback = true;
                    return null;
                } catch (Throwable e) {
                    LogUtils.e("IPC Excption:" + e.getMessage());
                    return null;
                }
            }
        });
    }

    @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
    public void onConnect(ISpeechEngine speechEngine) {
        try {
            this.mIpcRunner.setProxy(speechEngine.getTTSEngine());
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NullPointerException e2) {
            e2.printStackTrace();
        }
        this.mIpcRunner.fetchAll();
    }

    @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
    public void onDisconnect() {
        this.mHadSetCallback = false;
        this.mIpcRunner.setProxy(null);
    }

    public String speak(String text, String param, ISpeakCallback callback) throws RemoteException {
        String ttsId = UUID.randomUUID().toString();
        int i = SpeechConstant.TTS_DEFAULT_MODEL;
        speakParam(text, param);
        addTSSListener();
        if (callback != null) {
            this.mSpeakCallbackMap.put(ttsId, callback);
            LogUtils.i("speak callback size:" + this.mSpeakCallbackMap.size());
        }
        return ttsId;
    }
}
