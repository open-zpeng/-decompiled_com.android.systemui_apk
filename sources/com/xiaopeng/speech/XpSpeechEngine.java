package com.xiaopeng.speech;

import android.content.Context;
import androidx.lifecycle.Lifecycle;
import com.xiaopeng.lib.apirouter.server.ApiPublisherProvider;
import com.xiaopeng.speech.overall.OverallManager;
import com.xiaopeng.speech.overall.listener.IXpOverallListener;
import com.xiaopeng.speech.overall.listener.IXpRecordListener;
import com.xiaopeng.speech.vui.VuiEngineImpl;
import com.xiaopeng.speech.vui.listener.IXpVuiSceneListener;
import com.xiaopeng.speech.vui.model.VuiScene;
import com.xiaopeng.speech.vui.observer.XpVuiElementChangedObserver;
import com.xiaopeng.speech.vui.observer.XpVuiLifecycleObserver;
import com.xiaopeng.speech.vui.utils.LogUtils;
import com.xiaopeng.vui.commons.model.VuiElement;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes23.dex */
public class XpSpeechEngine {
    private static VuiEngineImpl impl = null;
    private static String TAG = "XpSpeechEngine";

    public static void setLoglevel(int loglevel) {
        VuiEngineImpl vuiEngineImpl = impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.setLoglevel(loglevel);
        }
    }

    public static boolean isVuiFeatureDisabled() {
        VuiEngineImpl vuiEngineImpl = impl;
        if (vuiEngineImpl != null) {
            return vuiEngineImpl.isVuiFeatureDisabled();
        }
        return true;
    }

    public static boolean isInSpeech() {
        VuiEngineImpl vuiEngineImpl = impl;
        if (vuiEngineImpl != null) {
            return vuiEngineImpl.isInSpeech();
        }
        return false;
    }

    public static void setProcessName(String processName) {
        VuiEngineImpl vuiEngineImpl = impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.setProcessName(processName);
        }
    }

    public static void dispatchVuiEvent(String vuiEvent, String data) {
        VuiEngineImpl vuiEngineImpl = impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.dispatchVuiEvent(vuiEvent, data);
        }
    }

    public static String getElementState(String sceneId, String elementId) {
        VuiEngineImpl vuiEngineImpl = impl;
        if (vuiEngineImpl != null) {
            return vuiEngineImpl.getElementState(sceneId, elementId);
        }
        return null;
    }

    public static void init(Context context) {
        if (ApiPublisherProvider.CONTEXT == null) {
            ApiPublisherProvider.CONTEXT = context.getApplicationContext();
        }
        if (impl == null) {
            impl = new VuiEngineImpl(context, false);
        }
        impl.init("ApiRouterSceneService");
        OverallManager.instance().init(context);
    }

    public static void init(Context context, IXpOverallListener listener) {
        if (ApiPublisherProvider.CONTEXT == null) {
            ApiPublisherProvider.CONTEXT = context.getApplicationContext();
        }
        if (impl == null) {
            impl = new VuiEngineImpl(context, false);
        }
        impl.init("");
        OverallManager.instance().init(context, listener);
    }

    public static void subScribeOverallCommand(String[] events, IXpOverallListener listener) {
        OverallManager.instance().addObserverEvents(events, null, listener);
    }

    public static void subScribeOverallCommand(String[] events, String[] querys, IXpOverallListener listener) {
        OverallManager.instance().addObserverEvents(events, querys, listener);
    }

    public static void setOverallListener(IXpOverallListener listener) {
        String str = TAG;
        LogUtils.logDebug(str, "setOverallListener:" + listener);
        OverallManager.instance().setOverallListener(listener);
    }

    public static void addOverallListener(IXpOverallListener listener) {
        String str = TAG;
        LogUtils.logDebug(str, "setOverallListener:" + listener);
        OverallManager.instance().addOverallListener(listener);
    }

    public static void removeOverallListener(IXpOverallListener listener) {
        String str = TAG;
        LogUtils.logDebug(str, "removeOverallListener:" + listener);
        OverallManager.instance().removeOverallListener(listener);
    }

    public static void registerDupVuiSceneListener(String sceneId, IXpVuiSceneListener listener) {
        VuiEngineImpl vuiEngineImpl = impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.addDupVuiSceneListener(sceneId, null, listener, null, true);
        }
        String str = TAG;
        LogUtils.logDebug(str, "registerVuiSceneListener:" + sceneId + ",listener:" + listener);
        XpVuiElementChangedObserver elementChangedObserver = new XpVuiElementChangedObserver(listener);
        listener.onInitCompleted(elementChangedObserver);
    }

    public static void unregisterDupVuiSceneListener(String sceneId, IXpVuiSceneListener listener) {
        String str = TAG;
        LogUtils.logDebug(str, "unregisterVuiSceneListener:" + sceneId + ",listener:" + listener);
        VuiEngineImpl vuiEngineImpl = impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.removeDupVuiSceneListener(sceneId, listener, false);
        }
    }

    public static void registerVuiSceneListener(String sceneId, IXpVuiSceneListener listener) {
        VuiEngineImpl vuiEngineImpl = impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.addVuiSceneListener(sceneId, null, listener, null, true);
        }
        String str = TAG;
        LogUtils.logDebug(str, "registerVuiSceneListener:" + sceneId + ",listener:" + listener);
        XpVuiElementChangedObserver elementChangedObserver = new XpVuiElementChangedObserver();
        listener.onInitCompleted(elementChangedObserver);
    }

    public static void unregisterVuiSceneListener(String sceneId, IXpVuiSceneListener listener) {
        String str = TAG;
        LogUtils.logDebug(str, "unregisterVuiSceneListener:" + sceneId + ",listener:" + listener);
        VuiEngineImpl vuiEngineImpl = impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.removeVuiSceneListener(sceneId, listener, false);
        }
    }

    public static void setBuildElement(String sceneId, VuiElement element) {
        VuiEngineImpl vuiEngineImpl = impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.setBuildElements(sceneId, Arrays.asList(element));
        }
    }

    public static void setBuildElement(String sceneId, List<VuiElement> elements) {
        VuiEngineImpl vuiEngineImpl = impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.setBuildElements(sceneId, elements);
        }
    }

    public static void setUpdateElement(String sceneId, VuiElement element) {
        VuiEngineImpl vuiEngineImpl = impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.setUpdateElements(sceneId, Arrays.asList(element));
        }
    }

    public static void setUpdateElement(String sceneId, List<VuiElement> elements) {
        VuiEngineImpl vuiEngineImpl = impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.setUpdateElements(sceneId, elements);
        }
    }

    public static VuiElement getVuiElement(String sceneId, String id) {
        VuiEngineImpl vuiEngineImpl = impl;
        if (vuiEngineImpl != null) {
            return vuiEngineImpl.getVuiElement(sceneId, id);
        }
        return null;
    }

    public static VuiScene getVuiScene(String sceneId) {
        VuiEngineImpl vuiEngineImpl = impl;
        if (vuiEngineImpl != null) {
            return vuiEngineImpl.getVuiScene(sceneId);
        }
        return null;
    }

    public static void enterScene(String sceneId) {
        String str = TAG;
        LogUtils.logDebug(str, "enterScene:" + sceneId);
        VuiEngineImpl vuiEngineImpl = impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.enterScene(sceneId, true);
        }
    }

    public static void enterDupScene(String sceneId, IXpVuiSceneListener listener) {
        String str = TAG;
        LogUtils.logDebug(str, "enterScene:" + sceneId);
        VuiEngineImpl vuiEngineImpl = impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.enterDupScene(sceneId, true, listener);
        }
    }

    public static void exitScene(String sceneId) {
        String str = TAG;
        LogUtils.logDebug(str, "exitScene:" + sceneId);
        VuiEngineImpl vuiEngineImpl = impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.exitScene(sceneId, true);
        }
    }

    public static void exitDupScene(String sceneId, IXpVuiSceneListener listener) {
        String str = TAG;
        LogUtils.logDebug(str, "exitScene:" + sceneId);
        VuiEngineImpl vuiEngineImpl = impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.exitDupScene(sceneId, true, listener);
        }
    }

    public static void initScene(Lifecycle lifecycle, String sceneId, IXpVuiSceneListener listener) {
        XpVuiLifecycleObserver observer = new XpVuiLifecycleObserver(sceneId, listener, lifecycle);
        lifecycle.addObserver(observer);
        XpVuiElementChangedObserver elementChangedObserver = new XpVuiElementChangedObserver();
        listener.onInitCompleted(elementChangedObserver);
    }

    public static void dispatchOverallEvent(String event, String data) {
        OverallManager.instance().dispatchOverallEvent(event, data);
    }

    public static void dispatchOverallQuery(String event, String data, String callback) {
        OverallManager.instance().dispatchOverallQuery(event, data, callback);
    }

    public static boolean isSupportRecord() {
        return OverallManager.instance().isSupportRecord();
    }

    public static void initRecord(Context context, String param, IXpRecordListener listener) {
        OverallManager.instance().initRecord(context, param, listener);
    }

    public static void initRecord(Context context, IXpRecordListener listener) {
        OverallManager.instance().initRecord(context, null, listener);
    }

    public static void startRecord(String param) {
        OverallManager.instance().startRecord(param);
    }

    public static void startRecord() {
        OverallManager.instance().startRecord(null);
    }

    public static void stopRecord() {
        OverallManager.instance().stopRecord();
    }

    public static void destroyRecord(IXpRecordListener listener) {
        OverallManager.instance().destroyRecord(listener);
    }

    public static void speak(String tts) {
        OverallManager.instance().speak(tts);
    }
}
