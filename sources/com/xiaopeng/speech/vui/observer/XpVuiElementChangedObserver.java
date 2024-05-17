package com.xiaopeng.speech.vui.observer;

import android.text.TextUtils;
import android.view.View;
import com.xiaopeng.speech.XpSpeechEngine;
import com.xiaopeng.speech.vui.VuiEngineImpl;
import com.xiaopeng.speech.vui.cache.VuiSceneCache;
import com.xiaopeng.speech.vui.cache.VuiSceneCacheFactory;
import com.xiaopeng.speech.vui.listener.IXpVuiElementChanged;
import com.xiaopeng.speech.vui.utils.LogUtils;
import com.xiaopeng.speech.vui.utils.VuiUtils;
import com.xiaopeng.vui.commons.IVuiSceneListener;
import com.xiaopeng.vui.commons.VuiElementType;
import com.xiaopeng.vui.commons.model.VuiElement;
import java.util.List;
/* loaded from: classes.dex */
public class XpVuiElementChangedObserver implements IXpVuiElementChanged {
    private IVuiSceneListener mListener;

    public XpVuiElementChangedObserver(IVuiSceneListener listener) {
        this.mListener = null;
        this.mListener = listener;
    }

    public XpVuiElementChangedObserver() {
        this.mListener = null;
    }

    @Override // com.xiaopeng.speech.vui.listener.IXpVuiElementChanged
    public void onVuiElementChanged(String sceneId, View view) {
        onVuiElementChanged(sceneId, view, null, null, -1);
    }

    @Override // com.xiaopeng.speech.vui.listener.IXpVuiElementChanged
    public void onVuiElementChanged(String sceneId, View view, String[] vuiLabels, int curState) {
        onVuiElementChanged(sceneId, view, null, vuiLabels, curState);
    }

    @Override // com.xiaopeng.speech.vui.listener.IXpVuiElementChanged
    public void onVuiElementChanged(String sceneId, View view, List<VuiElement> elementList) {
        onVuiElementChanged(sceneId, view, elementList, null, -1);
    }

    private void onVuiElementChanged(String sceneId, View view, List<VuiElement> elementList, String[] vuiLabels, int curState) {
        LogUtils.logDebug("XpVuiElementChangedObserver", "onVuiElementChanged:" + sceneId + ",elementList:" + elementList);
        if (this.mListener != null && !TextUtils.isEmpty(sceneId)) {
            sceneId = this.mListener.toString() + "-" + sceneId;
        }
        if (view != null) {
            VuiElement element = XpSpeechEngine.getVuiElement(sceneId, "" + view.getId());
            if (element != null) {
                if (VuiElementType.RADIOBUTTON.getType().equals(element.getType()) || VuiElementType.CHECKBOX.getType().equals(element.getType())) {
                    VuiUtils.setValueAttribute(view, element);
                } else if (VuiElementType.RECYCLEVIEW.getType().equals(element.getType())) {
                    VuiUtils.addScrollProps(element, view);
                    element.setElements(elementList);
                } else if (VuiElementType.STATEFULBUTTON.getType().equals(element.getType())) {
                    VuiUtils.setStatefulButtonValues(curState, vuiLabels, element);
                }
                element.setVisible(view.getVisibility() == 0 ? null : false);
                XpSpeechEngine.setUpdateElement(sceneId, element);
                return;
            }
            XpSpeechEngine.setBuildElement(sceneId, elementList);
            return;
        }
        VuiSceneCache cache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
        if (cache != null) {
            List<VuiElement> cacheList = cache.getCache(VuiEngineImpl.mSceneIdPrefix + "-" + sceneId);
            if (cacheList != null && !cacheList.isEmpty()) {
                XpSpeechEngine.setUpdateElement(sceneId, elementList);
                return;
            }
            LogUtils.logInfo("XpVuiElementChangedObserver", "cacheList is empty");
            XpSpeechEngine.setBuildElement(sceneId, elementList);
        }
    }
}
