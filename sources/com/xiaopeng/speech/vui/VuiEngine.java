package com.xiaopeng.speech.vui;

import android.content.Context;
import android.view.View;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import com.xiaopeng.speech.vui.constants.Foo;
import com.xiaopeng.speech.vui.listener.IVuiEventListener;
import com.xiaopeng.speech.vui.model.VuiFeedback;
import com.xiaopeng.speech.vui.model.VuiScene;
import com.xiaopeng.speech.vui.observer.VuiLifecycleObserver;
import com.xiaopeng.speech.vui.utils.VuiUtils;
import com.xiaopeng.vui.commons.IVuiElement;
import com.xiaopeng.vui.commons.IVuiElementChangedListener;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.IVuiEngine;
import com.xiaopeng.vui.commons.IVuiSceneListener;
import com.xiaopeng.vui.commons.VuiAction;
import com.xiaopeng.vui.commons.VuiPriority;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public class VuiEngine implements IVuiEngine {
    private static volatile VuiEngine instance = null;
    private VuiEngineImpl impl;

    private VuiEngine(Context context) {
        this.impl = null;
        if (!VuiUtils.canUseVuiFeature()) {
            return;
        }
        if (VuiUtils.is3DUIPlatForm() && "com.xiaopeng.napa".equals(context.getApplicationInfo().packageName)) {
            return;
        }
        this.impl = new VuiEngineImpl(context, true);
    }

    public static VuiEngine getInstance(Context context) {
        if (instance == null) {
            synchronized (VuiEngine.class) {
                if (instance == null) {
                    instance = new VuiEngine(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public void enterScene(String sceneId, int flag) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            if (flag == 0) {
                vuiEngineImpl.enterScene(sceneId, true);
            } else if (flag == 2) {
                vuiEngineImpl.enterScene(sceneId, false);
            }
        }
    }

    public void exitScene(String sceneId, int flag) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            if (flag == 0) {
                vuiEngineImpl.exitScene(sceneId, true);
            } else if (flag == 2) {
                vuiEngineImpl.exitScene(sceneId, false);
            }
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiEngine
    public void enterScene(String sceneId) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.enterScene(sceneId, true);
        }
    }

    public void enterScene(String sceneId, boolean isWholeScene) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.enterScene(sceneId, isWholeScene);
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiEngine
    public void exitScene(String sceneId) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.exitScene(sceneId, true);
        }
    }

    public void exitScene(String sceneId, boolean isWholeScene) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.exitScene(sceneId, isWholeScene);
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiEngine
    public void buildScene(String sceneId, View rootView) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.buildScene(sceneId, rootView, (List<Integer>) null, (IVuiElementListener) null, (List<String>) null, true, (ISceneCallbackHandler) null);
        }
    }

    public void buildScene(String sceneId, View rootView, boolean isWholeScene) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.buildScene(sceneId, rootView, (List<Integer>) null, (IVuiElementListener) null, (List<String>) null, isWholeScene, (ISceneCallbackHandler) null);
        }
    }

    public void buildScene(String sceneId, View rootView, ISceneCallbackHandler mSceneCallbackHandler) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.buildScene(sceneId, rootView, (List<Integer>) null, (IVuiElementListener) null, (List<String>) null, true, mSceneCallbackHandler);
        }
    }

    public void buildScene(String sceneId, View rootView, List<String> subSceneIdList, boolean isWholeScene) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.buildScene(sceneId, rootView, (List<Integer>) null, (IVuiElementListener) null, subSceneIdList, isWholeScene, (ISceneCallbackHandler) null);
        }
    }

    public void buildScene(String sceneId, View rootView, List<String> subSceneIdList, ISceneCallbackHandler mSceneCallbackHandler) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.buildScene(sceneId, rootView, (List<Integer>) null, (IVuiElementListener) null, subSceneIdList, true, mSceneCallbackHandler);
        }
    }

    public void buildScene(String sceneId, View rootView, List<Integer> customizeIds, IVuiElementListener viewVuiHandler, List<String> subSceneIdList, boolean isWholeScene, ISceneCallbackHandler sceneCallbackHandler) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.buildScene(sceneId, rootView, customizeIds, (IVuiElementListener) null, subSceneIdList, isWholeScene, sceneCallbackHandler);
        }
    }

    public void buildScene(String sceneId, List<View> viewList, List<Integer> customizeIds, IVuiElementListener viewVuiHandler, List<String> subSceneIdList, boolean isWholeScene, ISceneCallbackHandler mSceneCallbackHandler) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.buildScene(sceneId, viewList, customizeIds, viewVuiHandler, subSceneIdList, isWholeScene, mSceneCallbackHandler);
        }
    }

    public void buildScene(String sceneId, List<View> viewList, List<String> subSceneIdList, boolean isWholeScene) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.buildScene(sceneId, viewList, (List<Integer>) null, (IVuiElementListener) null, subSceneIdList, isWholeScene, (ISceneCallbackHandler) null);
        }
    }

    public void buildScene(String sceneId, List<View> viewList, boolean isWholeScene) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.buildScene(sceneId, viewList, (List<Integer>) null, (IVuiElementListener) null, (List<String>) null, isWholeScene, (ISceneCallbackHandler) null);
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiEngine
    public void buildScene(String sceneId, View rootView, List<Integer> customizeIds, IVuiElementListener viewVuiHandler) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.buildScene(sceneId, rootView, customizeIds, viewVuiHandler, (List<String>) null, true, (ISceneCallbackHandler) null);
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiEngine
    public void buildScene(String sceneId, List<View> list) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.buildScene(sceneId, list, (List<Integer>) null, (IVuiElementListener) null, (List<String>) null, true, (ISceneCallbackHandler) null);
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiEngine
    public void buildScene(String sceneId, List<View> list, List<Integer> customizeIds, IVuiElementListener viewVuiHandler) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.buildScene(sceneId, list, customizeIds, viewVuiHandler, (List<String>) null, true, (ISceneCallbackHandler) null);
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiEngine
    public void updateScene(String sceneId, List<View> viewList) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.updateScene(sceneId, viewList);
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiEngine
    public void updateElementAttribute(String sceneId, View view) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.updateElementAttribute(sceneId, Arrays.asList(view));
        }
    }

    public void updateElementAttribute(String sceneId, List<View> views) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.updateElementAttribute(sceneId, views);
        }
    }

    public void updateRecyclerViewItemView(String sceneId, View view, RecyclerView recyclerView) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.updateRecyclerViewItemView(sceneId, Arrays.asList(view), recyclerView);
        }
    }

    public void updateRecyclerViewItemView(String sceneId, List<View> viewList, RecyclerView recyclerView) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.updateRecyclerViewItemView(sceneId, viewList, recyclerView);
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiEngine
    public void updateScene(String sceneId, View view) {
        if (this.impl != null) {
            if ((view instanceof IVuiElement) && ((IVuiElement) view).getVuiElementChangedListener() != null) {
                this.impl.updateScene(sceneId, view);
            } else {
                this.impl.updateScene(sceneId, Arrays.asList(view));
            }
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiEngine
    public void updateScene(String sceneId, List<View> viewList, List<Integer> ids, IVuiElementListener vuiHandler) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.updateScene(sceneId, viewList, ids, vuiHandler);
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiEngine
    public void updateScene(String sceneId, View view, List<Integer> ids, IVuiElementListener callback) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.updateScene(sceneId, view, ids, callback);
        }
    }

    public void handleNewRootviewToScene(String sceneId, List<View> views) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.handleNewRootviewToScene(sceneId, views, VuiPriority.LEVEL2);
        }
    }

    public void handleNewRootviewToScene(String sceneId, View view) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.handleNewRootviewToScene(sceneId, Arrays.asList(view), VuiPriority.LEVEL2);
        }
    }

    public void handleNewRootviewToScene(String sceneId, View view, VuiPriority vuiPriority) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.handleNewRootviewToScene(sceneId, Arrays.asList(view), vuiPriority);
        }
    }

    public void handleNewRootviewToScene(String sceneId, List<View> views, VuiPriority vuiPriority) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.handleNewRootviewToScene(sceneId, views, vuiPriority);
        }
    }

    public void removeOtherRootViewFromScene(String sceneId, View view) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.removeOtherRootViewFromScene(sceneId, Arrays.asList(view));
        }
    }

    public void removeOtherRootViewFromScene(String sceneId, List<View> viewList) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.removeOtherRootViewFromScene(sceneId, viewList);
        }
    }

    public void removeOtherRootViewFromScene(String sceneId) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.removeOtherRootViewFromScene(sceneId);
        }
    }

    public void addSceneElementGroup(View rootView, String sceneId, VuiPriority priority, IVuiSceneListener listener) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.addSceneElementGroup(rootView, sceneId, priority, listener);
        }
    }

    public void addSceneElement(View view, String parentElementId, String sceneId) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.addSceneElement(view, parentElementId, sceneId);
        }
    }

    public void removeSceneElementGroup(String elementGroupId, String sceneId, IVuiSceneListener listener) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.removeSceneElementGroup(elementGroupId, sceneId, listener);
        }
    }

    public void dispatchVuiEvent(String vuiEvent, String data) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.dispatchVuiEvent(vuiEvent, data);
        }
    }

    public String getElementState(String sceneId, String elementId) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            return vuiEngineImpl.getElementState(sceneId, elementId);
        }
        return null;
    }

    public void vuiFeedback(View view, VuiFeedback vuiResult) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.vuiFeedback(view, vuiResult);
        }
    }

    public void subscribe(String observer) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.subscribe(observer);
        }
    }

    public void subscribeVuiFeature() {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.subscribeVuiFeature();
        }
    }

    public void unSubscribeVuiFeature() {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.unSubscribeVuiFeature();
        }
    }

    public void unSubscribe() {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.unSubscribe();
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiEngine
    public void addVuiSceneListener(String sceneId, View rootView, IVuiSceneListener listener) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.addVuiSceneListener(sceneId, rootView, listener, null, true);
        }
    }

    public void addVuiSceneListener(String sceneId, View rootView, IVuiSceneListener listener, boolean needBuild) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.addVuiSceneListener(sceneId, rootView, listener, null, needBuild);
        }
    }

    public void addVuiSceneListener(String sceneId, View rootView, IVuiSceneListener listener, IVuiElementChangedListener elementChangedListener, boolean needBuild) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.addVuiSceneListener(sceneId, rootView, listener, elementChangedListener, needBuild);
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiEngine
    public void addVuiSceneListener(String sceneId, View rootView, IVuiSceneListener listener, IVuiElementChangedListener elementChangedListener) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.addVuiSceneListener(sceneId, rootView, listener, elementChangedListener, true);
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiEngine
    public void removeVuiSceneListener(String sceneId) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.removeVuiSceneListener(sceneId, null, false);
        }
    }

    public void removeVuiSceneListener(String sceneId, IVuiSceneListener listener) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.removeVuiSceneListener(sceneId, listener, false);
        }
    }

    public void removeVuiSceneListener(String sceneId, boolean keepCache) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.removeVuiSceneListener(sceneId, null, keepCache);
        }
    }

    public void removeVuiSceneListener(String sceneId, IVuiSceneListener listener, boolean keepCache) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.removeVuiSceneListener(sceneId, listener, keepCache);
        }
    }

    public void setVuiElementTag(View tagView, String tag) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.setVuiElementTag(tagView, tag);
        }
    }

    public String getVuiElementTag(View view) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            return vuiEngineImpl.getVuiElementTag(view);
        }
        return null;
    }

    public void setVuiElementUnSupportTag(View tagView, boolean isUnSupport) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.setVuiElementUnSupportTag(tagView, isUnSupport);
        }
    }

    public void setVuiElementUnStandardSwitch(View tagView) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.setVuiElementDefaultAction(tagView, VuiAction.SETCHECK.getName(), true);
        }
    }

    public void setVuiElementDefaultAction(View tagView, String action, Object value) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.setVuiElementDefaultAction(tagView, action, value);
        }
    }

    public void setVuiCustomDisableControlTag(View tagView) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.setVuiCustomDisableControlTag(tagView);
        }
    }

    public void setVuiCustomDisableFeedbackTag(View tagView) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.setVuiCustomDisableFeedbackTag(tagView);
        }
    }

    public void setVuiStatfulButtonClick(View tagView) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.setVuiStatefulButtonClick(tagView);
        }
    }

    public void disableChildVuiAttrWhenInvisible(View tagView) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.disableChildVuiAttrWhenInvisible(tagView);
        }
    }

    public void setVuiLabelUnSupportText(View... tagView) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.setVuiLabelUnSupportText(tagView);
        }
    }

    public void setVuiElementVisibleTag(View tagView, boolean isVisible) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.setVuiElementVisibleTag(tagView, isVisible);
        }
    }

    public Boolean getVuiElementVisibleTag(View tagView) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            return vuiEngineImpl.getVuiElementVisibleTag(tagView);
        }
        return null;
    }

    public void disableVuiFeature() {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.disableVuiFeature();
        }
    }

    public void enableVuiFeature() {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.enableVuiFeature();
        }
    }

    public boolean isVuiFeatureDisabled() {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            return vuiEngineImpl.isVuiFeatureDisabled();
        }
        return true;
    }

    public boolean isInSpeech() {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            return vuiEngineImpl.isInSpeech();
        }
        return false;
    }

    public String getVuiElementId(String fatherId, int position, String viewId) {
        if (fatherId != null) {
            viewId = fatherId + "_" + viewId;
        }
        if (position != -1) {
            return viewId + "_" + position;
        }
        return viewId;
    }

    public VuiScene createVuiScene(String sceneId, long time) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            return vuiEngineImpl.createVuiScene(sceneId, time);
        }
        return null;
    }

    public void setLoglevel(int loglevel) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.setLoglevel(loglevel);
        }
    }

    public void addVuiEventListener(String sceneId, IVuiEventListener listener) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.addVuiEventListener(sceneId, listener);
        }
    }

    public void disableViewVuiMode() {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.disableViewVuiMode();
        }
    }

    public void setExecuteVirtualTag(View tagView) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.setExecuteVirtualTag(tagView, null);
        }
    }

    public void setExecuteVirtualTag(View tagView, String action) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.setExecuteVirtualTag(tagView, action);
        }
    }

    public void setVirtualResourceNameTag(View tagView, String name) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.setVirtualResourceNameTag(tagView, name);
        }
    }

    public void setCustomDoActionTag(View tagView) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.setCustomDoActionTag(tagView);
        }
    }

    public void setProcessName(String processName) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.setProcessName(processName);
        }
    }

    public void initScene(Lifecycle lifecycle, String sceneId, View view, IVuiSceneListener listener) {
        initScene(lifecycle, sceneId, view, listener, null, true, false);
    }

    public void initScene(Lifecycle lifecycle, String sceneId, View view, IVuiSceneListener listener, IVuiElementChangedListener elementChangedListener) {
        initScene(lifecycle, sceneId, view, listener, elementChangedListener, true, false);
    }

    public void initScene(Lifecycle lifecycle, String sceneId, View view, IVuiSceneListener listener, IVuiElementChangedListener elementChangedListener, boolean needBuild, boolean keepCache) {
        VuiLifecycleObserver observer = new VuiLifecycleObserver(Foo.getContext(), lifecycle, sceneId, view, listener, elementChangedListener, needBuild, keepCache);
        lifecycle.addObserver(observer);
    }

    public boolean isSpeechShowNumber() {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            return vuiEngineImpl.isSpeechShowNumber();
        }
        return false;
    }

    public String getActiveSceneId() {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            return vuiEngineImpl.getActiveSceneId();
        }
        return null;
    }

    public void setHasFeedBackTxtByViewDisable(View tagView, String tts) {
        VuiEngineImpl vuiEngineImpl = this.impl;
        if (vuiEngineImpl != null) {
            vuiEngineImpl.setHasFeedBackTxtByViewDisable(tagView, tts);
        }
    }
}
