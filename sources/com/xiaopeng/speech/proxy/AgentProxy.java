package com.xiaopeng.speech.proxy;

import android.os.IBinder;
import android.os.RemoteException;
import com.xiaopeng.speech.ConnectManager;
import com.xiaopeng.speech.ISpeechEngine;
import com.xiaopeng.speech.common.bean.SliceData;
import com.xiaopeng.speech.common.util.IPCRunner;
import com.xiaopeng.speech.common.util.LogUtils;
import com.xiaopeng.speech.common.util.WorkerHandler;
import com.xiaopeng.speech.coreapi.IAgent;
/* loaded from: classes23.dex */
public class AgentProxy extends IAgent.Stub implements ConnectManager.OnConnectCallback {
    private volatile IAgent agent;
    private IPCRunner<IAgent> mIpcRunner = new IPCRunner<>("AgentProxy");
    private final String TAG = "AgentProxy";

    @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
    public void onConnect(ISpeechEngine speechEngine) {
        try {
            IAgent agent = speechEngine.getAgent();
            this.agent = agent;
            this.mIpcRunner.setProxy(agent);
            this.mIpcRunner.fetchAll();
        } catch (RemoteException e) {
            LogUtils.e(this, "onConnect exception ", e);
        }
    }

    @Override // com.xiaopeng.speech.ConnectManager.OnConnectCallback
    public void onDisconnect() {
        this.agent = null;
        this.mIpcRunner.setProxy(null);
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public void sendText(final String text) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, Object>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.1
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IAgent proxy) throws RemoteException {
                proxy.sendText(text);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public void sendTextWithSoundArea(final String text, final int soundArea) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, Object>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.2
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IAgent proxy) throws RemoteException {
                proxy.sendTextWithSoundArea(text, soundArea);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public void sendEvent(final String event, final String data) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, Object>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.3
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IAgent proxy) throws RemoteException {
                proxy.sendEvent(event, data);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public void feedbackResult(final String event, final String data) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, Object>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.4
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IAgent proxy) throws RemoteException {
                proxy.feedbackResult(event, data);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public void updateDeviceInfo(final String deviceJson) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, Object>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.5
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IAgent proxy) throws RemoteException {
                proxy.updateDeviceInfo(deviceJson);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public void triggerIntent(final String skill, final String task, final String intent, final String slots) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, Object>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.6
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IAgent proxy) throws RemoteException {
                proxy.triggerIntent(skill, task, intent, slots);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public void updateVocab(final String vocabName, final String[] contents, final boolean addOrDelete) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, Object>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.7
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IAgent proxy) throws RemoteException {
                proxy.updateVocab(vocabName, contents, addOrDelete);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public void sendUIEvent(final String event, final String data) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, Object>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.8
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IAgent proxy) throws RemoteException {
                proxy.sendUIEvent(event, data);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    @Deprecated
    public void setASRInterruptEnabled(final boolean enable) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, Object>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.9
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IAgent proxy) throws RemoteException {
                proxy.setASRInterruptEnabled(enable);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    @Deprecated
    public boolean isEnableASRInterrupt() {
        return ((Boolean) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, Boolean>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.10
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Boolean run(IAgent proxy) throws RemoteException {
                return Boolean.valueOf(proxy.isEnableASRInterrupt());
            }
        }, false)).booleanValue();
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public void sendScript(final String script) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, Object>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.11
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IAgent proxy) throws RemoteException {
                proxy.sendScript(script);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    @Deprecated
    public boolean isDefaultEnableASRInterrupt() {
        return ((Boolean) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, Boolean>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.12
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Boolean run(IAgent proxy) throws RemoteException {
                return Boolean.valueOf(proxy.isDefaultEnableASRInterrupt());
            }
        }, false)).booleanValue();
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    @Deprecated
    public void setDefaultASRInterruptEnabled(final boolean enable) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, Object>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.13
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IAgent proxy) throws RemoteException {
                proxy.setDefaultASRInterruptEnabled(enable);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public void setDefaultWelcomeEnabled(final boolean enable) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, Object>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.14
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IAgent proxy) throws RemoteException {
                proxy.setDefaultWelcomeEnabled(enable);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public boolean isDefaultEnableWelcome() {
        return ((Boolean) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, Boolean>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.15
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Boolean run(IAgent proxy) throws RemoteException {
                return Boolean.valueOf(proxy.isDefaultEnableWelcome());
            }
        }, false)).booleanValue();
    }

    public void setHandler(WorkerHandler workerHandler) {
        this.mIpcRunner.setWorkerHandler(workerHandler);
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public void sendThirdCMD(final String cmd) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, Object>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.16
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IAgent proxy) throws RemoteException {
                proxy.sendThirdCMD(cmd);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public void triggerEvent(final String event, final String data) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, Object>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.17
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IAgent proxy) throws RemoteException {
                proxy.triggerEvent(event, data);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public void setUseWheelVoiceButton(final IBinder binder, final boolean useVoiceButton) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, String>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.18
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public String run(IAgent proxy) throws RemoteException {
                proxy.setUseWheelVoiceButton(binder, useVoiceButton);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public boolean isWheelVoiceButtonEnable() {
        IAgent agent = this.agent;
        if (agent != null) {
            try {
                return agent.isWheelVoiceButtonEnable();
            } catch (Throwable e) {
                LogUtils.e("AgentProxy", "remote error: ", e);
                return false;
            }
        }
        return false;
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public String getRecommendData(final String type) {
        return (String) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, String>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.19
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public String run(IAgent proxy) throws RemoteException {
                return proxy.getRecommendData(type);
            }
        }, "");
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public String getSkillData(final String type) {
        return (String) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, String>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.20
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public String run(IAgent proxy) throws RemoteException {
                return proxy.getSkillData(type);
            }
        }, "");
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public void setSkillData(final String data) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, String>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.21
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public String run(IAgent proxy) throws RemoteException {
                proxy.setSkillData(data);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public String getConfigData(final String key) {
        return (String) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, String>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.22
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public String run(IAgent proxy) throws RemoteException {
                return proxy.getConfigData(key);
            }
        }, "");
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public void setConfigData(final String key, final String data) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, String>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.23
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public String run(IAgent proxy) throws RemoteException {
                proxy.setConfigData(key, data);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public void sendInfoFlowStatData(final int eventId, final String data) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, Object>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.24
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IAgent proxy) throws RemoteException {
                proxy.sendInfoFlowStatData(eventId, data);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public void sendApiRoute(final String type, final String data) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, String>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.25
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public String run(IAgent proxy) throws RemoteException {
                proxy.sendApiRoute(type, data);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public String getMessageBoxData(final String type, final String data) {
        return (String) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, String>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.26
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public String run(IAgent proxy) throws RemoteException {
                return proxy.getMessageBoxData(type, data);
            }
        }, "");
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public void sendSceneData(final String sceneId, final String appName, final String appVersion, final String sceneData, final String type) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, String>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.27
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public String run(IAgent proxy) throws RemoteException {
                proxy.sendSceneData(sceneId, appName, appVersion, sceneData, type);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public void sendInfoFlowCardState(final String type, final int state) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, String>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.28
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public String run(IAgent proxy) throws RemoteException {
                proxy.sendInfoFlowCardState(type, state);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public void uploadContacts(final String vocabName, final String contents, final int operation) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, String>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.29
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public String run(IAgent proxy) throws RemoteException {
                proxy.uploadContacts(vocabName, contents, operation);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public void uploadContact(final String vocabName, final SliceData contents, final int operation) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, String>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.30
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public String run(IAgent proxy) throws RemoteException {
                proxy.uploadContact(vocabName, contents, operation);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public void triggerIntentWithBinder(final IBinder binder, final String skill, final String task, final String intent, final String slots) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, Object>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.31
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IAgent proxy) throws RemoteException {
                proxy.triggerIntentWithBinder(binder, skill, task, intent, slots);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public void triggerDialog(final int triggerID, final int[] soundArea, final String data) {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, Object>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.32
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IAgent proxy) throws RemoteException {
                proxy.triggerDialog(triggerID, soundArea, data);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public void leaveTrigger() {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, Object>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.33
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IAgent proxy) throws RemoteException {
                proxy.leaveTrigger();
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public void leaveTriggerWithID(final int triggerID) throws RemoteException {
        this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, Object>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.34
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IAgent proxy) throws RemoteException {
                proxy.leaveTriggerWithID(triggerID);
                return null;
            }
        });
    }

    @Override // com.xiaopeng.speech.coreapi.IAgent
    public int newTriggerDialog(final int triggerID, final int[] soundArea, final String data) throws RemoteException {
        return ((Integer) this.mIpcRunner.runFunc(new IPCRunner.IIPCFunc<IAgent, Object>() { // from class: com.xiaopeng.speech.proxy.AgentProxy.35
            @Override // com.xiaopeng.speech.common.util.IPCRunner.IIPCFunc
            public Object run(IAgent proxy) throws RemoteException {
                return Integer.valueOf(proxy.newTriggerDialog(triggerID, soundArea, data));
            }
        }, 100000)).intValue();
    }
}
