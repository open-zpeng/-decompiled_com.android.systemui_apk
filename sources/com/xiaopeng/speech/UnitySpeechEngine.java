package com.xiaopeng.speech;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.xiaopeng.lib.apirouter.server.ApiPublisherProvider;
import com.xiaopeng.lib.apirouter.server.IManifestHandler;
import com.xiaopeng.lib.apirouter.server.IManifestHelper;
import com.xiaopeng.lib.apirouter.server.ManifestHelper_VuiEngine;
import com.xiaopeng.speech.overall.OverallManager;
import com.xiaopeng.speech.overall.listener.IXpOverallListener;
import com.xiaopeng.speech.vui.VuiEngineImpl;
import com.xiaopeng.speech.vui.listener.IUnityVuiSceneListener;
import com.xiaopeng.speech.vui.model.VuiFeedback;
import com.xiaopeng.speech.vui.model.VuiScene;
import com.xiaopeng.speech.vui.utils.LogUtils;
import com.xiaopeng.speech.vui.utils.VuiUtils;
import com.xiaopeng.vui.commons.model.VuiElement;
import java.util.Arrays;
/* loaded from: classes23.dex */
public class UnitySpeechEngine {
    private static VuiEngineImpl impl = null;
    private static String TAG = "UnitySpeechEngine";

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
        initApiRouter();
        if (impl == null) {
            impl = new VuiEngineImpl(context, false);
        }
        impl.init("ApiRouterUnitySceneService");
        OverallManager.instance().init(context);
    }

    public static void init(Context context, IXpOverallListener listener) {
        LogUtils.logInfo(TAG, "init");
        if (ApiPublisherProvider.CONTEXT == null) {
            ApiPublisherProvider.CONTEXT = context.getApplicationContext();
        }
        initApiRouter();
        if (impl == null) {
            impl = new VuiEngineImpl(context, false);
            impl.init("ApiRouterUnitySceneService");
        }
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

    public static void registerVuiSceneListener(String sceneId, IUnityVuiSceneListener listener) {
        VuiEngineImpl vuiEngineImpl = impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.addVuiSceneListener(sceneId, null, listener, null, true);
        }
        String str = TAG;
        LogUtils.logInfo(str, "registerVuiSceneListener:" + sceneId + ",listener:" + listener);
    }

    public static void unregisterVuiSceneListener(String sceneId, IUnityVuiSceneListener listener) {
        String str = TAG;
        LogUtils.logDebug(str, "unregisterVuiSceneListener:" + sceneId + ",listener:" + listener);
        VuiEngineImpl vuiEngineImpl = impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.removeVuiSceneListener(sceneId, listener, false);
        }
    }

    public static void setBuildElement(String data) {
        String str = TAG;
        LogUtils.logInfo(str, "setBuildElement:" + data);
        if (impl != null) {
            VuiScene scene = VuiUtils.stringConvertToVuiScene(data);
            impl.setBuildElements(scene.getSceneId(), scene.getElements());
        }
    }

    public static void setUpdateElement(String data) {
        if (impl != null) {
            VuiScene scene = VuiUtils.stringConvertToVuiScene(data);
            impl.setUpdateElements(scene.getSceneId(), scene.getElements());
        }
    }

    public static void setUpdateElement(String sceneId, String data) {
        String str = TAG;
        LogUtils.logInfo(str, "setBuildElement:" + sceneId + ",data:" + data);
        if (impl != null) {
            VuiElement element = VuiUtils.stringConvertToVuiElement(data);
            impl.setUpdateElements(sceneId, Arrays.asList(element));
        }
    }

    public static String getStatefulButtonString(int currIndex, String vuilabel, String action, String id, String label) {
        if (!TextUtils.isEmpty(vuilabel)) {
            String[] vuilabels = vuilabel.split(",");
            VuiElement element = VuiUtils.generateStatefulButtonElement(currIndex, vuilabels, action, id, label);
            if (element != null) {
                return VuiUtils.vuiElementGroupConvertToString(Arrays.asList(element));
            }
            return null;
        }
        return null;
    }

    public static void setUpdateElementValue(String sceneId, String elementId, Object value) {
        VuiEngineImpl vuiEngineImpl = impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.setUpdateElementValue(sceneId, elementId, value);
        }
    }

    public static void setUpdateElementVisible(String sceneId, String elementId, boolean visible) {
        VuiEngineImpl vuiEngineImpl = impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.setUpdateElementVisible(sceneId, elementId, visible);
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
        LogUtils.logInfo(str, "enterScene:" + sceneId);
        VuiEngineImpl vuiEngineImpl = impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.enterScene(sceneId, true);
        }
    }

    public static void exitScene(String sceneId) {
        String str = TAG;
        LogUtils.logInfo(str, "exitScene:" + sceneId);
        VuiEngineImpl vuiEngineImpl = impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.exitScene(sceneId, true);
        }
    }

    public static void dispatchOverallEvent(String event, String data) {
        OverallManager.instance().dispatchOverallEvent(event, data);
    }

    public static void dispatchOverallQuery(String event, String data, String callback) {
        OverallManager.instance().dispatchOverallQuery(event, data, callback);
    }

    public void addVuiElement(String sceneId, String sceneData) {
        String str = TAG;
        LogUtils.logInfo(str, "addVuiElement:" + sceneId + ",data:" + sceneData);
        if (impl != null) {
            VuiElement element = VuiUtils.stringConvertToVuiElement(sceneData);
            impl.setUpdateElements(sceneId, Arrays.asList(element));
        }
    }

    public void removeVuiElement(String sceneId, String elementId) {
        String str = TAG;
        LogUtils.logInfo(str, "removeVuiElement:" + sceneId + ",elementId:" + elementId);
        VuiEngineImpl vuiEngineImpl = impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.removeVuiElement(sceneId, elementId);
        }
    }

    public void vuiFeedback(String elementId, String feedbackStr, int state) {
        if (impl != null) {
            String str = TAG;
            Log.i(str, "vuiFeedback elementId:" + elementId + ",feedback," + feedbackStr);
            VuiFeedback feedback = new VuiFeedback.Builder().state(state).content(feedbackStr).build();
            impl.vuiFeedback(elementId, feedback);
        }
    }

    private static void initApiRouter() {
        ApiPublisherProvider.addManifestHandler(new IManifestHandler() { // from class: com.xiaopeng.speech.UnitySpeechEngine.1
            @Override // com.xiaopeng.lib.apirouter.server.IManifestHandler
            public IManifestHelper[] getManifestHelpers() {
                return new IManifestHelper[]{new ManifestHelper_VuiEngine()};
            }
        });
    }
}
