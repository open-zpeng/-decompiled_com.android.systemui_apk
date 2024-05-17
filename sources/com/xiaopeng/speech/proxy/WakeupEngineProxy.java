package com.xiaopeng.speech.proxy;

import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import com.xiaopeng.speech.ConnectManager;
import com.xiaopeng.speech.ISpeechEngine;
import com.xiaopeng.speech.common.bean.DisableInfoBean;
import com.xiaopeng.speech.common.bean.SuspendInfoBean;
import com.xiaopeng.speech.common.util.IPCRunner;
import com.xiaopeng.speech.common.util.LogUtils;
import com.xiaopeng.speech.common.util.WorkerHandler;
import com.xiaopeng.speech.coreapi.IWakeupEngine;
import com.xiaopeng.speech.jarvisproto.SoundAreaStatus;
import com.xiaopeng.speech.protocol.utils.CarTypeUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/* loaded from: classes23.dex */
public class WakeupEngineProxy extends IWakeupEngine.Stub implements ConnectManager.OnConnectCallback {
    private static final String TAG = "WakeupEngineProxy";
    private IPCRunner<IWakeupEngine> mIpcRunner = new IPCRunner<>(TAG);
    private Map<String, DisableInfoBean> disableInfoCache = new ConcurrentHashMap();
    private Map<String, DisableInfoBean> disableSpeechInfoCache = new ConcurrentHashMap();
    private Map<String, List<SuspendInfoBean>> suspendInfoCache = new ConcurrentHashMap();

    @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
    public void onConnect(ISpeechEngine speechEngine) {
        try {
            this.mIpcRunner.setProxy(speechEngine.getWakeupEngine());
            this.mIpcRunner.fetchAll();
            LogUtils.i(TAG, "reset:   onConnect");
            resumeCarSpeechStatus();
        } catch (RemoteException e) {
            LogUtils.e(this, "onConnect exception ", e);
        }
    }

    @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
    public void onDisconnect() {
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void startDialog() {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.1
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.startDialog();
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void stopDialog() {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.2
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.stopDialog();
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void avatarClick(final String greeting) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.3
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.avatarClick(greeting);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void avatarPress() {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.4
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.avatarPress();
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void avatarRelease() {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.5
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.avatarRelease();
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void enableWakeup() {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.6
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.enableWakeup();
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void disableWakeup() {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.7
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.disableWakeup();
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public String[] getWakeupWords() {
        return (String[]) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, String[]>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.8
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public String[] run(IWakeupEngine proxy) throws RemoteException {
                return proxy.getWakeupWords();
            }
        }, new String[0]);
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void updateMinorWakeupWord(final String word, final String pinyin, final String threshold, final String[] greetings) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.9
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.updateMinorWakeupWord(word, pinyin, threshold, greetings);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public String getMinorWakeupWord() {
        return (String) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, String>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.10
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public String run(IWakeupEngine proxy) throws RemoteException {
                return proxy.getMinorWakeupWord();
            }
        }, null);
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void updateCommandWakeupWord(final String[] actions, final String[] words, final String[] pinyin, final String[] threshold, final String[] greetings) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.11
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.updateCommandWakeupWord(actions, words, pinyin, threshold, greetings);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void clearCommandWakeupWord() {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.12
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.clearCommandWakeupWord();
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void addCommandWakeupWord(final String[] actions, final String[] words, final String[] pinyin, final String[] threshold, final String[] greetings) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.13
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.addCommandWakeupWord(actions, words, pinyin, threshold, greetings);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void removeCommandWakeupWord(final String[] words) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.14
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.removeCommandWakeupWord(words);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void updateShortcutWakeupWord(final String[] words, final String[] pinyin, final String[] threshold) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.15
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.updateShortcutWakeupWord(words, pinyin, threshold);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void clearShortCutWakeupWord() {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.16
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.clearShortCutWakeupWord();
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void addShortcutWakeupWord(final String[] words, final String[] pinyin, final String[] threshold) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.17
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.addShortcutWakeupWord(words, pinyin, threshold);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void removeShortcutWakeupWord(final String[] words) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.18
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.removeShortcutWakeupWord(words);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void pauseDialog() {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.19
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.pauseDialog();
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void resumeDialog() {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.20
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.resumeDialog();
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public boolean isEnableWakeup() {
        return ((Boolean) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Boolean>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.21
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Boolean run(IWakeupEngine proxy) throws RemoteException {
                return Boolean.valueOf(proxy.isEnableWakeup());
            }
        }, false)).booleanValue();
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public boolean isDefaultEnableWakeup() {
        return ((Boolean) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Boolean>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.22
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Boolean run(IWakeupEngine proxy) throws RemoteException {
                return Boolean.valueOf(proxy.isDefaultEnableWakeup());
            }
        }, true)).booleanValue();
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void setDefaultWakeupEnabled(final boolean enable) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.23
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.setDefaultWakeupEnabled(enable);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void enableWakeupEnhance(final IBinder binder) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.24
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                IBinder iBinder = binder;
                if (iBinder == null) {
                    proxy.enableWakeupEnhance(proxy.asBinder());
                    return null;
                }
                proxy.enableWakeupEnhance(iBinder);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void disableWakeupEnhance(final IBinder binder) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.25
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                IBinder iBinder = binder;
                if (iBinder == null) {
                    proxy.disableWakeupEnhance(proxy.asBinder());
                    return null;
                }
                proxy.disableWakeupEnhance(iBinder);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public boolean isWheelWakeupEnabled() {
        return ((Boolean) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Boolean>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.26
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Boolean run(IWakeupEngine proxy) throws RemoteException {
                return Boolean.valueOf(proxy.isWheelWakeupEnabled());
            }
        }, true)).booleanValue();
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void setWheelWakeupEnabled(final IBinder binder, final boolean state) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.27
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                IBinder iBinder = binder;
                if (iBinder == null) {
                    proxy.setWheelWakeupEnabled(proxy.asBinder(), state);
                    return null;
                }
                proxy.setWheelWakeupEnabled(iBinder, state);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public boolean isDefaultEnableOneshot() {
        return ((Boolean) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Boolean>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.28
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Boolean run(IWakeupEngine proxy) throws RemoteException {
                return Boolean.valueOf(proxy.isDefaultEnableOneshot());
            }
        }, false)).booleanValue();
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void setDefaultOneshotEnabled(final boolean enable) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.29
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.setDefaultOneshotEnabled(enable);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void enableOneshot() {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.30
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.enableOneshot();
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void disableOneshot() {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.31
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.disableOneshot();
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public boolean isDefaultEnableFastWake() {
        return ((Boolean) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Boolean>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.32
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Boolean run(IWakeupEngine proxy) throws RemoteException {
                return Boolean.valueOf(proxy.isDefaultEnableFastWake());
            }
        }, false)).booleanValue();
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void setDefaultFastWakeEnabled(final boolean enable) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.33
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.setDefaultFastWakeEnabled(enable);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void enableFastWake() {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.34
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.enableFastWake();
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void disableFastWake() {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.35
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.disableFastWake();
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void stopDialogMessage() {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.36
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.stopDialogMessage();
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void stopDialogReason(final String reason) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.37
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.stopDialogReason(reason);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void enableMainWakeupWord(final IBinder binder) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.38
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                IBinder iBinder = binder;
                if (iBinder == null) {
                    proxy.enableMainWakeupWord(proxy.asBinder());
                    return null;
                }
                proxy.enableMainWakeupWord(iBinder);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void disableMainWakeupWord(final IBinder binder) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.39
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                IBinder iBinder = binder;
                if (iBinder == null) {
                    proxy.disableMainWakeupWord(proxy.asBinder());
                    return null;
                }
                proxy.disableMainWakeupWord(iBinder);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void enableFastWakeEnhance(final IBinder binder) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.40
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                IBinder iBinder = binder;
                if (iBinder == null) {
                    proxy.enableFastWakeEnhance(proxy.asBinder());
                    return null;
                }
                proxy.enableFastWakeEnhance(iBinder);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void disableFastWakeEnhance(final IBinder binder) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.41
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                IBinder iBinder = binder;
                if (iBinder == null) {
                    proxy.disableFastWakeEnhance(proxy.asBinder());
                    return null;
                }
                proxy.disableFastWakeEnhance(iBinder);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void enableInterruptWake(final IBinder binder) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.42
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                IBinder iBinder = binder;
                if (iBinder == null) {
                    proxy.enableInterruptWake(proxy.asBinder());
                    return null;
                }
                proxy.enableInterruptWake(iBinder);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void disableInterruptWake(final IBinder binder) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.43
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                IBinder iBinder = binder;
                if (iBinder == null) {
                    proxy.disableInterruptWake(proxy.asBinder());
                    return null;
                }
                proxy.disableInterruptWake(iBinder);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void startDialogFrom(final String reason) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.44
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.startDialogFrom(reason);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void startDialogWithSoundArea(final String reason, final int soundArea) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.45
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                proxy.startDialogWithSoundArea(reason, soundArea);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void disableWakeupWithInfo(final IBinder binder, final int type, final String byWho, final String info, final int notifyType) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.46
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) {
                try {
                    if (binder == null) {
                        proxy.disableWakeupWithInfo(proxy.asBinder(), type, byWho, info, notifyType);
                    } else {
                        proxy.disableWakeupWithInfo(binder, type, byWho, info, notifyType);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                WakeupEngineProxy wakeupEngineProxy = WakeupEngineProxy.this;
                IBinder iBinder = binder;
                if (iBinder == null) {
                    iBinder = proxy.asBinder();
                }
                wakeupEngineProxy.setDisableInfoCache(iBinder, type, byWho, info, notifyType);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void enableWakeupWithInfo(final IBinder binder, final int type, final String byWho, final int notifyType) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.47
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) {
                try {
                    if (binder == null) {
                        proxy.enableWakeupWithInfo(proxy.asBinder(), type, byWho, notifyType);
                    } else {
                        proxy.enableWakeupWithInfo(binder, type, byWho, notifyType);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                WakeupEngineProxy wakeupEngineProxy = WakeupEngineProxy.this;
                IBinder iBinder = binder;
                if (iBinder == null) {
                    iBinder = proxy.asBinder();
                }
                wakeupEngineProxy.removeDisableInfoCache(iBinder, type, byWho, notifyType);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void disableWheelWakeupWithInfo(final IBinder binder, final String byWho, final String info, final int notifyType) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.48
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) {
                try {
                    if (binder == null) {
                        proxy.disableWheelWakeupWithInfo(proxy.asBinder(), byWho, info, notifyType);
                    } else {
                        proxy.disableWheelWakeupWithInfo(binder, byWho, info, notifyType);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                WakeupEngineProxy wakeupEngineProxy = WakeupEngineProxy.this;
                IBinder iBinder = binder;
                if (iBinder == null) {
                    iBinder = proxy.asBinder();
                }
                wakeupEngineProxy.setDisableInfoCache(iBinder, -1, byWho, info, notifyType);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void enableWheelWakeupWithInfo(final IBinder binder, final String byWho, final int notifyType) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.49
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) {
                try {
                    if (binder == null) {
                        proxy.enableWheelWakeupWithInfo(proxy.asBinder(), byWho, notifyType);
                    } else {
                        proxy.enableWheelWakeupWithInfo(binder, byWho, notifyType);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                WakeupEngineProxy wakeupEngineProxy = WakeupEngineProxy.this;
                IBinder iBinder = binder;
                if (iBinder == null) {
                    iBinder = proxy.asBinder();
                }
                wakeupEngineProxy.removeDisableInfoCache(iBinder, -1, byWho, notifyType);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void suspendDialogWithReason(final IBinder binder, final String byWho, final String reason) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.50
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                IBinder iBinder = binder;
                if (iBinder == null) {
                    proxy.suspendDialogWithReason(proxy.asBinder(), byWho, reason);
                    return null;
                }
                proxy.suspendDialogWithReason(iBinder, byWho, reason);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void resumeDialogWithReason(final IBinder binder, final String byWho, final String reason) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.51
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) throws RemoteException {
                IBinder iBinder = binder;
                if (iBinder == null) {
                    proxy.resumeDialogWithReason(proxy.asBinder(), byWho, reason);
                    return null;
                }
                proxy.resumeDialogWithReason(iBinder, byWho, reason);
                return null;
            }
        });
    }

    public void setHandler(WorkerHandler workerHandler) {
        this.mIpcRunner.setWorkerHandler(workerHandler);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDisableInfoCache(IBinder binder, int type, String byWho, String info, int notifyType) {
        String key = generateKey(binder, type, byWho, notifyType);
        LogUtils.i(TAG, "setDisableInfoCache :  " + key);
        if (!TextUtils.isEmpty(key)) {
            DisableInfoBean mInfoBean = new DisableInfoBean(binder, type, byWho, info, notifyType);
            if (!this.disableInfoCache.containsKey(key)) {
                LogUtils.i(TAG, "put data  :  " + key + ": " + mInfoBean.toString());
                this.disableInfoCache.put(key, mInfoBean);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeDisableInfoCache(IBinder binder, int type, String byWho, int notifyType) {
        String key = generateKey(binder, type, byWho, notifyType);
        LogUtils.i(TAG, "removeDisableInfoCache :  " + key);
        if (!TextUtils.isEmpty(key) && this.disableInfoCache.containsKey(key)) {
            LogUtils.i(TAG, "remove  :  " + key + ": " + this.disableInfoCache.size());
            this.disableInfoCache.remove(key);
            LogUtils.i(TAG, "remove after :  " + key + ": " + this.disableInfoCache.size());
        }
    }

    private String generateKey(IBinder binder, int type, String byWho, int notifyType) {
        String key;
        if (binder != null) {
            String key2 = binder.toString();
            if (type == -1) {
                key = key2 + "_" + byWho;
            } else {
                key = key2 + "_" + type + "_" + byWho;
            }
            if (notifyType != -1) {
                return key + "_" + notifyType;
            }
            return key;
        }
        return null;
    }

    private void resumeCarSpeechStatus() {
        LogUtils.i(TAG, "resumeCarSpeechStatus  disableInfoCache size " + this.disableInfoCache.size() + ",suspendInfoCache size " + this.suspendInfoCache.size() + ",disableSpeechInfoCache size " + this.disableSpeechInfoCache.size());
        if (this.disableInfoCache.size() > 0) {
            for (Map.Entry<String, DisableInfoBean> entry : this.disableInfoCache.entrySet()) {
                DisableInfoBean disableInfoBean = entry.getValue();
                if (disableInfoBean != null) {
                    LogUtils.i(TAG, "disable from cache:    = ====  " + disableInfoBean.toString());
                    if (disableInfoBean.getType() == -1) {
                        disableWheelWakeupWithInfo(disableInfoBean.getBinder(), disableInfoBean.getByWho(), disableInfoBean.getInfo(), disableInfoBean.getNotifyType());
                    } else {
                        disableWakeupWithInfo(disableInfoBean.getBinder(), disableInfoBean.getType(), disableInfoBean.getByWho(), disableInfoBean.getInfo(), disableInfoBean.getNotifyType());
                    }
                }
            }
        }
        if (this.suspendInfoCache.size() > 0) {
            for (Map.Entry<String, List<SuspendInfoBean>> entry2 : this.suspendInfoCache.entrySet()) {
                List<SuspendInfoBean> infoBeans = entry2.getValue();
                for (int i = 0; i < infoBeans.size(); i++) {
                    SuspendInfoBean infoBean = infoBeans.get(i);
                    if (infoBean != null) {
                        LogUtils.i(TAG, "suspend from cache:    = ====  " + infoBean.toString());
                        suspendSpeechWithInfo(infoBean.getBinder(), infoBean.getByWho(), infoBean.getInfo(), infoBean.getNotifyType(), infoBean.isNeedMic(), false);
                    }
                }
            }
        }
        if (this.disableSpeechInfoCache.size() > 0) {
            for (Map.Entry<String, DisableInfoBean> entry3 : this.disableSpeechInfoCache.entrySet()) {
                DisableInfoBean disableInfoBean2 = entry3.getValue();
                if (disableInfoBean2 != null) {
                    LogUtils.i(TAG, "disable speech from cache:    = ====  " + disableInfoBean2.toString());
                    disableSpeechWithInfo(disableInfoBean2.getBinder(), disableInfoBean2.getByWho(), disableInfoBean2.getInfo(), disableInfoBean2.getNotifyType());
                }
            }
        }
    }

    private void suspendSpeechWithInfo(final IBinder binder, final String byWho, final String info, final int notifyType, final boolean needMic, final boolean handleCache) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.52
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) {
                try {
                    if (binder == null) {
                        proxy.suspendSpeechWithInfo(proxy.asBinder(), byWho, info, notifyType, needMic);
                    } else {
                        proxy.suspendSpeechWithInfo(binder, byWho, info, notifyType, needMic);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                if (handleCache) {
                    WakeupEngineProxy wakeupEngineProxy = WakeupEngineProxy.this;
                    IBinder iBinder = binder;
                    if (iBinder == null) {
                        iBinder = proxy.asBinder();
                    }
                    wakeupEngineProxy.setSuspendInfoCache(iBinder, byWho, info, notifyType, needMic);
                    return null;
                }
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void suspendSpeechWithInfo(IBinder binder, String byWho, String info, int notifyType, boolean needMic) {
        suspendSpeechWithInfo(binder, byWho, info, notifyType, needMic, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSuspendInfoCache(IBinder binder, String byWho, String info, int notifyType, boolean needMic) {
        String key = generateKey(binder, notifyType, byWho, -1);
        LogUtils.i(TAG, "setSuspendInfoCache :  " + key);
        if (!TextUtils.isEmpty(key)) {
            SuspendInfoBean mInfoBean = new SuspendInfoBean(binder, byWho, info, notifyType, needMic);
            List<SuspendInfoBean> infoBeans = this.suspendInfoCache.get(key);
            if (infoBeans == null) {
                infoBeans = new ArrayList();
            }
            infoBeans.add(mInfoBean);
            this.suspendInfoCache.put(key, infoBeans);
            LogUtils.i(TAG, "put data  :  " + key + ": " + mInfoBean.toString() + ",suspendInfoCache:" + this.suspendInfoCache.size());
        }
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void resumeSpeechWithInfo(IBinder binder, String byWho) {
        resumeSpeechWithTypeInfo(binder, byWho, -1, false);
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public String getSoundAreaStatus(final int soundArea) {
        if (soundArea < 0) {
            if (CarTypeUtils.isE38ZH()) {
                return new SoundAreaStatus(soundArea, true).getJsonData();
            }
            return new SoundAreaStatus(soundArea, false).getJsonData();
        }
        return (String) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, String>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.53
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public String run(IWakeupEngine proxy) throws RemoteException {
                return proxy.getSoundAreaStatus(soundArea);
            }
        }, new SoundAreaStatus(soundArea, true).getJsonData());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeSuspendInfoCache(IBinder binder, int type, String byWho, boolean removeAll) {
        String key = generateKey(binder, type, byWho, -1);
        LogUtils.i(TAG, "removeSuspendInfoCache :  " + key);
        if (!TextUtils.isEmpty(key) && this.suspendInfoCache.containsKey(key)) {
            LogUtils.i(TAG, "remove  :  " + key + ": " + this.suspendInfoCache.size());
            if (removeAll) {
                this.suspendInfoCache.remove(key);
            } else {
                List<SuspendInfoBean> infoBeans = this.suspendInfoCache.get(key);
                if (infoBeans.size() > 0) {
                    infoBeans.remove(0);
                }
                LogUtils.i(TAG, "remove after :  " + key + ": infoBeans" + infoBeans.size());
                if (infoBeans.size() > 0) {
                    this.suspendInfoCache.put(key, infoBeans);
                } else {
                    this.suspendInfoCache.remove(key);
                }
            }
            LogUtils.i(TAG, "remove after :  " + key + ": " + this.suspendInfoCache.size());
        }
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void disableSpeechWithInfo(final IBinder binder, final String byWho, final String info, final int notifyType) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.54
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) {
                try {
                    if (binder == null) {
                        proxy.disableSpeechWithInfo(proxy.asBinder(), byWho, info, notifyType);
                    } else {
                        proxy.disableSpeechWithInfo(binder, byWho, info, notifyType);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                WakeupEngineProxy wakeupEngineProxy = WakeupEngineProxy.this;
                IBinder iBinder = binder;
                if (iBinder == null) {
                    iBinder = proxy.asBinder();
                }
                wakeupEngineProxy.setDisableSpeechInfoCache(iBinder, byWho, info, notifyType);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void enableSpeechWithInfo(final IBinder binder, final String byWho, final int notifyType) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.55
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) {
                try {
                    if (binder == null) {
                        proxy.enableSpeechWithInfo(proxy.asBinder(), byWho, notifyType);
                    } else {
                        proxy.enableSpeechWithInfo(binder, byWho, notifyType);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                WakeupEngineProxy wakeupEngineProxy = WakeupEngineProxy.this;
                IBinder iBinder = binder;
                if (iBinder == null) {
                    iBinder = proxy.asBinder();
                }
                wakeupEngineProxy.removeDisableSpeechInfoCache(iBinder, byWho, notifyType);
                return null;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDisableSpeechInfoCache(IBinder binder, String byWho, String info, int notifyType) {
        String key = generateKey(binder, notifyType, byWho, -1);
        LogUtils.i(TAG, "setDisableSpeechInfoCache :  " + key);
        if (!TextUtils.isEmpty(key)) {
            DisableInfoBean infoBean = new DisableInfoBean(binder, -1, byWho, info, notifyType);
            if (!this.disableSpeechInfoCache.containsKey(key)) {
                LogUtils.i(TAG, "put data  :  " + key + ": " + infoBean.toString());
                this.disableSpeechInfoCache.put(key, infoBean);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeDisableSpeechInfoCache(IBinder binder, String byWho, int notifyType) {
        String key = generateKey(binder, notifyType, byWho, -1);
        LogUtils.i(TAG, "removeDisableSpeechInfoCache :  " + key);
        if (!TextUtils.isEmpty(key) && this.disableSpeechInfoCache.containsKey(key)) {
            LogUtils.i(TAG, "remove  :  " + key + ": " + this.disableSpeechInfoCache.size());
            this.disableSpeechInfoCache.remove(key);
            LogUtils.i(TAG, "remove after :  " + key + ": " + this.disableSpeechInfoCache.size());
        }
    }

    @Override // com.xiaopeng.speech.coreapi.IWakeupEngine
    public void resumeSpeechWithTypeInfo(final IBinder binder, final String byWho, final int notifyType, final boolean removeAll) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IWakeupEngine, Object>() { // from class: com.xiaopeng.speech.proxy.WakeupEngineProxy.56
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IWakeupEngine proxy) {
                try {
                    if (binder == null) {
                        proxy.resumeSpeechWithTypeInfo(proxy.asBinder(), byWho, notifyType, removeAll);
                    } else {
                        proxy.resumeSpeechWithTypeInfo(binder, byWho, notifyType, removeAll);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                WakeupEngineProxy wakeupEngineProxy = WakeupEngineProxy.this;
                IBinder iBinder = binder;
                if (iBinder == null) {
                    iBinder = proxy.asBinder();
                }
                wakeupEngineProxy.removeSuspendInfoCache(iBinder, notifyType, byWho, removeAll);
                return null;
            }
        });
    }
}
