package com.xiaopeng.speech.vui.task;

import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.xiaopeng.speech.vui.VuiEngineImpl;
import com.xiaopeng.speech.vui.VuiSceneManager;
import com.xiaopeng.speech.vui.cache.VuiSceneCache;
import com.xiaopeng.speech.vui.cache.VuiSceneCacheFactory;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.speech.vui.model.VuiScene;
import com.xiaopeng.speech.vui.model.VuiSceneInfo;
import com.xiaopeng.speech.vui.task.base.BaseTask;
import com.xiaopeng.speech.vui.utils.LogUtils;
import com.xiaopeng.speech.vui.utils.VuiUtils;
import com.xiaopeng.vui.commons.IVuiElement;
import com.xiaopeng.vui.commons.IVuiElementChangedListener;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.model.VuiElement;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public class UpdateSceneTask extends BaseTask {
    private String TAG;
    private List<String> allIdList;
    private List<String> bizIds;
    private SoftReference<IVuiElementListener> callback;
    private IVuiElementChangedListener elementChangedListener;
    private List<VuiElement> elements;
    private List<String> idList;
    private List<Integer> ids;
    private VuiSceneInfo info;
    private boolean isRecyclerView;
    private List<String> mainThreadSceneList;
    private String sceneId;
    private long time;
    private SoftReference<View> updateView;
    private List<SoftReference<View>> viewList;
    private TaskWrapper viewWrapper;
    private VuiScene vuiScene;
    private VuiSceneCache vuiSceneCache;

    public UpdateSceneTask(TaskWrapper viewWrapper) {
        super(viewWrapper);
        this.TAG = "VuiEngine_UpdateSceneTask";
        this.vuiScene = null;
        this.elements = new ArrayList();
        this.bizIds = new ArrayList();
        this.time = -1L;
        this.info = null;
        this.isRecyclerView = false;
        this.allIdList = null;
        this.idList = new ArrayList();
        this.vuiSceneCache = null;
        this.mainThreadSceneList = Arrays.asList("MainMusicConcentration");
        this.viewWrapper = viewWrapper;
        this.sceneId = viewWrapper.getSceneId();
        this.viewList = viewWrapper.getViewList();
        this.updateView = viewWrapper.getView();
        this.ids = viewWrapper.getCustomizeIds();
        this.callback = viewWrapper.getElementListener();
    }

    @Override // com.xiaopeng.speech.vui.task.base.Task
    public void execute() {
        updateSceneByElement();
    }

    private void updateSceneByElement() {
        try {
            if ((this.viewList == null && this.updateView == null) || TextUtils.isEmpty(this.sceneId) || !VuiSceneManager.instance().canUpdateScene(this.sceneId)) {
                return;
            }
            this.time = System.currentTimeMillis();
            this.vuiScene = getNewVuiScene(this.sceneId, this.time);
            this.allIdList = VuiSceneManager.instance().getSceneIdList(this.sceneId);
            this.info = VuiSceneManager.instance().getSceneInfo(this.sceneId);
            this.vuiSceneCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
            if (this.info == null) {
                return;
            }
            this.elementChangedListener = this.info.getElementChangedListener();
            if (this.viewList != null) {
                int size = this.viewList.size();
                if (size == 0) {
                    return;
                }
                for (int i = 0; i < size; i++) {
                    SoftReference<View> view = this.viewList.get(i);
                    if (isNeedUpdate(view)) {
                        if (view != null && (view.get() instanceof RecyclerView)) {
                            this.isRecyclerView = true;
                        }
                        String str = this.TAG;
                        LogUtils.logDebug(str, "updateScene updateView" + view.get());
                        buildUpdateView(view);
                    }
                }
                handleUpdateElement();
            } else if (this.updateView != null) {
                String str2 = this.TAG;
                LogUtils.logDebug(str2, "updateScene updateView" + this.updateView.get());
                if (!isNeedUpdate(this.updateView)) {
                    return;
                }
                if (this.updateView != null && (this.updateView.get() instanceof RecyclerView)) {
                    this.isRecyclerView = true;
                }
                String sceneName = this.sceneId.substring(this.sceneId.lastIndexOf("-") + 1);
                if (this.isRecyclerView && this.mainThreadSceneList.contains(sceneName)) {
                    buildUpdateView(this.updateView);
                    handleUpdateElement();
                    return;
                }
                buildUpdateView(this.updateView);
                handleUpdateElement();
            }
        } catch (Exception e) {
            String str3 = this.TAG;
            LogUtils.e(str3, "e:" + e.getMessage());
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

    private boolean isNeedUpdate(SoftReference<View> view) {
        if (view != null && view.get() != null && (view.get() instanceof RecyclerView) && !this.sceneId.equals(VuiEngineImpl.mActiveSceneId)) {
            RecyclerView recyclerView = (RecyclerView) view.get();
            if (recyclerView.getChildCount() == 0) {
                return false;
            }
        }
        return true;
    }

    private boolean isAddToScene(SoftReference<View> view, boolean isRecyclerView, VuiSceneInfo vuiSceneInfo) {
        if (view == null || view.get() == null) {
            return true;
        }
        View rootView = vuiSceneInfo.getRootView();
        if (VuiConstants.MUSIC.equals(VuiSceneManager.instance().getmPackageName())) {
            if (isRecyclerView) {
                if (isRecyclerViewChild((View) view.get().getParent(), rootView)) {
                    String str = this.TAG;
                    LogUtils.logDebug(str, "view:" + view + "isRecyclerView," + isRecyclerView + ",rootView:" + rootView + ",ignore addToScene");
                    return false;
                }
            } else if (isRecyclerViewChild(view.get(), rootView)) {
                String str2 = this.TAG;
                LogUtils.logDebug(str2, "view:" + view + "isRecyclerView," + isRecyclerView + ",rootView:" + rootView + ",ignore addToScene");
                return false;
            }
        }
        return true;
    }

    private boolean isRecyclerViewChild(View view, View rootView) {
        if (rootView == null || view == null) {
            return false;
        }
        if (view instanceof RecyclerView) {
            return true;
        }
        if (view == rootView) {
            return false;
        }
        return isRecyclerViewChild((View) view.getParent(), rootView);
    }

    private void buildUpdateView(SoftReference<View> view) {
        List<VuiElement> list = this.elements;
        List<Integer> list2 = this.ids;
        SoftReference<IVuiElementListener> softReference = this.callback;
        List<String> list3 = this.idList;
        long j = this.time;
        List<String> list4 = this.allIdList;
        List<String> list5 = this.bizIds;
        boolean isVuiLayoutLoadable = (view == null || !(view.get() instanceof IVuiElement)) ? false : ((IVuiElement) view.get()).isVuiLayoutLoadable();
        boolean z = this.isRecyclerView;
        VuiElement element = buildView(view, list, list2, softReference, list3, j, list4, list5, null, 0, isVuiLayoutLoadable, z, z ? null : this.elementChangedListener);
        if (element != null && element.getId() != null) {
            element.setTimestamp(this.time);
            setVuiTag(view, element.getId());
            VuiElement targetElement = this.vuiSceneCache.getVuiElementById(this.sceneId, element.getId());
            if (targetElement == null) {
                if (isAddToScene(view, this.isRecyclerView, this.info)) {
                    this.elements.add(element);
                }
            } else if (!element.equals(targetElement)) {
                this.elements.add(element);
            } else {
                LogUtils.logDebug(this.TAG, "updateScene element same");
            }
        }
        int i = 0;
        while (i < this.elements.size()) {
            VuiElement curElement = this.elements.get(i);
            VuiElement targetElement2 = this.vuiSceneCache.getVuiElementById(this.sceneId, curElement.getId());
            if (targetElement2 != null && curElement.equals(targetElement2)) {
                this.elements.remove(curElement);
            } else {
                i++;
            }
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

    private void handleUpdateElement() {
        if (!this.elements.isEmpty()) {
            boolean isWholeScene = true;
            this.vuiScene.setElements(this.elements);
            VuiSceneInfo vuiSceneInfo = this.info;
            if (vuiSceneInfo != null) {
                isWholeScene = vuiSceneInfo.isWholeScene();
            }
            VuiSceneManager.instance().setSceneIdList(this.sceneId, this.allIdList);
            if (!"user".equals(Build.TYPE) && LogUtils.getLogLevel() <= LogUtils.LOG_DEBUG_LEVEL) {
                String str = this.TAG;
                LogUtils.logDebug(str, "updateScene completed time:" + (System.currentTimeMillis() - this.time) + "," + VuiUtils.vuiUpdateSceneConvertToString(this.vuiScene));
            }
            if (Thread.currentThread().isInterrupted()) {
                LogUtils.logInfo(this.TAG, "取消当前任务");
                return;
            }
            VuiSceneCache buildCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
            if (isWholeScene) {
                setWholeSceneCache(this.sceneId, buildCache, this.elements);
                VuiSceneManager.instance().sendSceneData(VuiSceneManager.TYPE_UPDATE, true, this.vuiScene);
                return;
            }
            if (buildCache != null) {
                List<VuiElement> elementList = buildCache.getFusionCache(this.sceneId, this.elements, false);
                buildCache.setCache(this.sceneId, elementList);
            }
            List<String> wholeSceneIds = null;
            VuiSceneInfo vuiSceneInfo2 = this.info;
            if (vuiSceneInfo2 != null) {
                wholeSceneIds = vuiSceneInfo2.getWholeSceneId();
            }
            int size = wholeSceneIds != null ? wholeSceneIds.size() : 0;
            String str2 = this.TAG;
            LogUtils.logDebug(str2, "updateScene wholeSceneIds:" + wholeSceneIds);
            if (size > 0) {
                if (wholeSceneIds.contains(VuiEngineImpl.mActiveSceneId)) {
                    this.vuiScene = getNewVuiScene(VuiEngineImpl.mActiveSceneId, this.time);
                    this.vuiScene.setElements(this.elements);
                    setWholeSceneCache(VuiEngineImpl.mActiveSceneId, buildCache, this.elements);
                    VuiSceneManager.instance().sendSceneData(VuiSceneManager.TYPE_UPDATE, true, this.vuiScene);
                }
                for (int i = 0; i < size; i++) {
                    String wholeSceneId = wholeSceneIds.get(i);
                    if (!TextUtils.isEmpty(wholeSceneId) && !wholeSceneId.equals(VuiEngineImpl.mActiveSceneId)) {
                        this.vuiScene = getNewVuiScene(wholeSceneId, this.time);
                        this.vuiScene.setElements(this.elements);
                        setWholeSceneCache(wholeSceneId, buildCache, this.elements);
                        VuiSceneManager.instance().sendSceneData(VuiSceneManager.TYPE_UPDATE, true, this.vuiScene);
                    }
                }
            }
        }
    }
}
