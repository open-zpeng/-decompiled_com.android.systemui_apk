package com.xiaopeng.speech.vui.task;

import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.xiaopeng.speech.vui.VuiEngineImpl;
import com.xiaopeng.speech.vui.VuiSceneManager;
import com.xiaopeng.speech.vui.cache.VuiSceneCache;
import com.xiaopeng.speech.vui.cache.VuiSceneCacheFactory;
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
public class UpdateRecyclerItemTask extends BaseTask {
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
    private SoftReference<RecyclerView> recyclerView;
    private String sceneId;
    private long time;
    private SoftReference<View> updateView;
    private List<SoftReference<View>> viewList;
    private TaskWrapper viewWrapper;
    private VuiScene vuiScene;
    private VuiSceneCache vuiSceneCache;

    public UpdateRecyclerItemTask(TaskWrapper viewWrapper) {
        super(viewWrapper);
        this.TAG = "VuiEngine_UpdateSceneTask";
        this.vuiScene = null;
        this.elements = new ArrayList();
        this.bizIds = new ArrayList();
        this.time = -1L;
        this.info = null;
        this.isRecyclerView = true;
        this.allIdList = null;
        this.idList = new ArrayList();
        this.vuiSceneCache = null;
        this.viewWrapper = viewWrapper;
        this.sceneId = viewWrapper.getSceneId();
        this.viewList = viewWrapper.getViewList();
        this.updateView = viewWrapper.getView();
        this.ids = viewWrapper.getCustomizeIds();
        this.callback = viewWrapper.getElementListener();
        this.recyclerView = viewWrapper.getRecyclerView();
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
                    String str = this.TAG;
                    LogUtils.logInfo(str, "updateScene updateView" + view.get());
                    buildUpdateView(view);
                }
                handleUpdateElement();
            } else if (this.updateView != null) {
                String str2 = this.TAG;
                LogUtils.logInfo(str2, "updateScene updateView" + this.updateView.get());
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

    private void buildUpdateView(SoftReference<View> view) {
        boolean layoutLoadable;
        SoftReference<RecyclerView> softReference = this.recyclerView;
        if (softReference != null && softReference.get() != null && (this.recyclerView.get() instanceof IVuiElement) && ((IVuiElement) this.recyclerView.get()).isVuiLayoutLoadable()) {
            layoutLoadable = true;
        } else {
            layoutLoadable = false;
        }
        VuiElement element = buildView(view, this.elements, this.ids, this.callback, this.idList, this.time, this.allIdList, this.bizIds, null, 0, layoutLoadable, this.isRecyclerView, null);
        if (element != null && element.getId() != null) {
            element.setTimestamp(this.time);
            setVuiTag(view, element.getId());
            VuiElement targetElement = this.vuiSceneCache.getVuiElementById(this.sceneId, element.getId());
            if (targetElement != null) {
                String oldStr = VuiUtils.vuiElementGroupConvertToString(Arrays.asList(targetElement));
                String newStr = VuiUtils.vuiElementGroupConvertToString(Arrays.asList(element));
                if (!oldStr.equals(newStr)) {
                    this.elements.add(element);
                } else {
                    LogUtils.logInfo(this.TAG, "updateScene element same");
                }
            }
        }
    }

    private void handleUpdateElement() {
        if (!this.elements.isEmpty()) {
            this.vuiScene.setElements(this.elements);
            boolean isWholeScene = true;
            String updateStr = VuiUtils.vuiUpdateSceneConvertToString(this.vuiScene);
            VuiSceneInfo vuiSceneInfo = this.info;
            if (vuiSceneInfo != null) {
                isWholeScene = vuiSceneInfo.isWholeScene();
            }
            VuiSceneManager.instance().setSceneIdList(this.sceneId, this.allIdList);
            String str = this.TAG;
            LogUtils.logInfo(str, "updateScene completed time:" + (System.currentTimeMillis() - this.time));
            String str2 = this.TAG;
            LogUtils.logDebug(str2, "updateScene:" + updateStr);
            if (Thread.currentThread().isInterrupted()) {
                LogUtils.logInfo(this.TAG, "取消当前任务");
                return;
            }
            VuiSceneCache buildCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
            if (isWholeScene) {
                VuiSceneManager.instance().sendSceneData(VuiSceneManager.TYPE_UPDATE, true, this.vuiScene);
                setWholeSceneCache(this.sceneId, buildCache, this.elements);
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
            String str3 = this.TAG;
            LogUtils.logDebug(str3, "updateScene wholeSceneIds:" + wholeSceneIds);
            if (size > 0) {
                if (wholeSceneIds.contains(VuiEngineImpl.mActiveSceneId)) {
                    this.vuiScene = getNewVuiScene(VuiEngineImpl.mActiveSceneId, this.time);
                    this.vuiScene.setElements(this.elements);
                    VuiSceneManager.instance().sendSceneData(VuiSceneManager.TYPE_UPDATE, true, this.vuiScene);
                    setWholeSceneCache(VuiEngineImpl.mActiveSceneId, buildCache, this.elements);
                }
                for (int i = 0; i < size; i++) {
                    String wholeSceneId = wholeSceneIds.get(i);
                    if (!VuiEngineImpl.mActiveSceneId.equals(wholeSceneId)) {
                        this.vuiScene = getNewVuiScene(wholeSceneId, this.time);
                        this.vuiScene.setElements(this.elements);
                        VuiSceneManager.instance().sendSceneData(VuiSceneManager.TYPE_UPDATE, true, this.vuiScene);
                        setWholeSceneCache(wholeSceneId, buildCache, this.elements);
                    }
                }
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
                LogUtils.logDebug(str, "updateSceneTask build cache" + VuiUtils.vuiSceneConvertToString(scene));
            }
        }
    }
}
