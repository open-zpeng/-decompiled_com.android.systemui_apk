package com.xiaopeng.speech.vui.task;

import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import com.google.gson.JsonObject;
import com.xiaopeng.speech.vui.VuiEngineImpl;
import com.xiaopeng.speech.vui.VuiSceneManager;
import com.xiaopeng.speech.vui.cache.VuiSceneCache;
import com.xiaopeng.speech.vui.cache.VuiSceneCacheFactory;
import com.xiaopeng.speech.vui.model.VuiScene;
import com.xiaopeng.speech.vui.model.VuiSceneInfo;
import com.xiaopeng.speech.vui.task.base.BaseTask;
import com.xiaopeng.speech.vui.utils.LogUtils;
import com.xiaopeng.speech.vui.utils.VuiUtils;
import com.xiaopeng.speech.vui.vuiengine.R;
import com.xiaopeng.vui.commons.IVuiElement;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.VuiElementType;
import com.xiaopeng.vui.commons.model.VuiElement;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class UpdateSceneAttributeTask extends BaseTask {
    private String TAG;
    private String sceneId;
    private SoftReference<View> updateView;
    private List<SoftReference<View>> viewList;
    private TaskWrapper viewWrapper;

    public UpdateSceneAttributeTask(TaskWrapper viewWrapper) {
        super(viewWrapper);
        this.TAG = "VuiEngine_UpdateSceneAttributeTask";
        this.viewWrapper = viewWrapper;
        this.sceneId = viewWrapper.getSceneId();
        this.updateView = viewWrapper.getView();
        this.viewList = viewWrapper.getViewList();
    }

    @Override // com.xiaopeng.speech.vui.task.base.Task
    public void execute() {
        updateScene();
    }

    private void updateScene() {
        try {
            if ((this.updateView == null && this.viewList == null) || TextUtils.isEmpty(this.sceneId) || !VuiSceneManager.instance().canUpdateScene(this.sceneId)) {
                return;
            }
            long time = System.currentTimeMillis();
            VuiSceneCache vuiSceneCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
            List<VuiElement> elements = new ArrayList<>();
            int size = 0;
            if (this.updateView != null) {
                LogUtils.d(this.TAG, "updateView：" + this.updateView.get());
                VuiElement element = buildElement(vuiSceneCache, this.updateView, time);
                if (element != null) {
                    elements.add(element);
                }
            } else if (this.viewList != null) {
                for (int i = 0; i < this.viewList.size(); i++) {
                    SoftReference<View> view = this.viewList.get(i);
                    VuiElement element2 = buildElement(vuiSceneCache, view, time);
                    if (element2 != null) {
                        elements.add(element2);
                    }
                }
            }
            if (elements.size() > 0) {
                VuiScene vuiScene = getNewVuiScene(this.sceneId, time);
                vuiScene.setElements(elements);
                VuiSceneInfo info = VuiSceneManager.instance().getSceneInfo(this.sceneId);
                boolean isWholeScene = true;
                if (info != null) {
                    isWholeScene = info.isWholeScene();
                }
                VuiSceneCache buildCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
                if (isWholeScene) {
                    setWholeSceneCache(this.sceneId, buildCache, elements);
                    VuiSceneManager.instance().sendSceneData(VuiSceneManager.TYPE_UPDATE, true, vuiScene);
                    return;
                }
                if (buildCache != null) {
                    List<VuiElement> elementList = buildCache.getFusionCache(this.sceneId, elements, true);
                    buildCache.setCache(this.sceneId, elementList);
                }
                List<String> wholeSceneIds = null;
                if (info != null) {
                    wholeSceneIds = info.getWholeSceneId();
                }
                if (wholeSceneIds != null) {
                    size = wholeSceneIds.size();
                }
                LogUtils.logDebug(this.TAG, "UpdateSceneAttributeTask wholeSceneIds:" + wholeSceneIds);
                if (size > 0) {
                    if (wholeSceneIds.contains(VuiEngineImpl.mActiveSceneId)) {
                        VuiScene vuiScene2 = getNewVuiScene(VuiEngineImpl.mActiveSceneId, time);
                        vuiScene2.setElements(elements);
                        setWholeSceneCache(VuiEngineImpl.mActiveSceneId, buildCache, elements);
                        VuiSceneManager.instance().sendSceneData(VuiSceneManager.TYPE_UPDATE, true, vuiScene2);
                    }
                    for (int i2 = 0; i2 < size; i2++) {
                        String wholeSceneId = wholeSceneIds.get(i2);
                        if (!VuiEngineImpl.mActiveSceneId.equals(wholeSceneId)) {
                            VuiScene vuiScene3 = getNewVuiScene(wholeSceneId, time);
                            vuiScene3.setElements(elements);
                            setWholeSceneCache(wholeSceneId, buildCache, elements);
                            VuiSceneManager.instance().sendSceneData(VuiSceneManager.TYPE_UPDATE, true, vuiScene3);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.e(this.TAG, "e:" + e.getMessage());
        }
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementBuilder
    public List<VuiElement> build(int fatherElementId, View view) {
        return build(fatherElementId, view);
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementBuilder
    public List<VuiElement> build(int fatherElementId, List<View> list) {
        return build(fatherElementId, list);
    }

    private String getElementId(SoftReference<View> view) {
        if (view == null || view.get() == null || !(view.get() instanceof IVuiElement)) {
            return null;
        }
        String elementId = (String) view.get().getTag(R.id.vuiElementId);
        if (TextUtils.isEmpty(elementId)) {
            if (view.get().getTag() != null && (view.get().getTag() instanceof String)) {
                String tag = (String) view.get().getTag();
                if (tag.startsWith("4657")) {
                    return tag;
                }
            }
            IVuiElement vuiElement = (IVuiElement) view.get();
            String elementId2 = vuiElement.getVuiElementId();
            if (TextUtils.isEmpty(elementId2)) {
                LogUtils.e(this.TAG, "update 元素的属性时必须是build过的元素");
                return null;
            }
            return elementId2;
        }
        return elementId;
    }

    private VuiElement buildElement(VuiSceneCache vuiSceneCache, SoftReference<View> view, long time) {
        VuiElement element;
        String elementId = getElementId(view);
        if (TextUtils.isEmpty(elementId)) {
            return null;
        }
        VuiElement targetElement = vuiSceneCache.getVuiElementById(this.sceneId, elementId);
        if (targetElement == null) {
            LogUtils.e(this.TAG, "缓存中没有此元素");
            return null;
        } else if (view != null && (view.get() instanceof IVuiElementListener) && (element = ((IVuiElementListener) view.get()).onBuildVuiElement(elementId, this)) != null) {
            return element;
        } else {
            VuiElement element2 = buildVuiElementAttr(view);
            if (element2 == null || view == null) {
                return null;
            }
            element2.setId(elementId);
            IVuiElement vuiFriendlyView = (IVuiElement) view.get();
            if (vuiFriendlyView == null) {
                return null;
            }
            JSONObject vuiPropsJson = vuiFriendlyView.getVuiProps();
            if (vuiPropsJson != null) {
                if (VuiElementType.STATEFULBUTTON.getType().equals(element2.getType())) {
                    createElementByProps(view, element2, vuiPropsJson, time, false, false);
                } else if (vuiPropsJson.keys().hasNext()) {
                    element2.setProps((JsonObject) this.mGson.fromJson(vuiPropsJson.toString(), (Class<Object>) JsonObject.class));
                }
            }
            if (targetElement.isLayoutLoadable()) {
                element2.setLayoutLoadable(true);
            }
            element2.setTimestamp(time);
            if (element2.equals(targetElement)) {
                LogUtils.logDebug(this.TAG, "updateScene same");
                return null;
            }
            if (!VuiElementType.STATEFULBUTTON.getType().equals(element2.getType()) && targetElement.getElements() != null && targetElement.getElements().size() > 0) {
                element2.setElements(targetElement.getElements());
            }
            return element2;
        }
    }

    private void setWholeSceneCache(String sceneId, VuiSceneCache buildCache, List<VuiElement> elements) {
        if (buildCache != null) {
            List<VuiElement> elementList = buildCache.getFusionCache(sceneId, elements, false);
            buildCache.setCache(sceneId, elementList);
            if (!"user".equals(Build.TYPE) && LogUtils.getLogLevel() <= LogUtils.LOG_DEBUG_LEVEL) {
                VuiScene scene = getNewVuiScene(sceneId, System.currentTimeMillis());
                scene.setElements(elementList);
                String str = this.TAG;
                LogUtils.logDebug(str, "updateSceneTask full_scene_info" + VuiUtils.vuiSceneConvertToString(scene));
            }
        }
    }
}
