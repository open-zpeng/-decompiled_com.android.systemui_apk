package com.xiaopeng.speech.vui.task;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.xiaopeng.speech.vui.VuiEngineImpl;
import com.xiaopeng.speech.vui.VuiSceneManager;
import com.xiaopeng.speech.vui.cache.VuiSceneCache;
import com.xiaopeng.speech.vui.cache.VuiSceneCacheFactory;
import com.xiaopeng.speech.vui.model.VuiScene;
import com.xiaopeng.speech.vui.model.VuiSceneInfo;
import com.xiaopeng.speech.vui.task.base.BaseTask;
import com.xiaopeng.speech.vui.utils.LogUtils;
import com.xiaopeng.speech.vui.utils.SceneMergeUtils;
import com.xiaopeng.speech.vui.utils.VuiUtils;
import com.xiaopeng.speech.vui.vuiengine.R;
import com.xiaopeng.vui.commons.IVuiElement;
import com.xiaopeng.vui.commons.IVuiElementChangedListener;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.IVuiSceneListener;
import com.xiaopeng.vui.commons.model.VuiElement;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
/* loaded from: classes.dex */
public class BuildSceneTask extends BaseTask {
    private String TAG;
    private List<String> bizIds;
    private IVuiElementChangedListener elementChangedListener;
    private List<VuiElement> elements;
    private VuiSceneInfo info;
    private boolean isWholeScene;
    private List<String> mIdList;
    private List<String> mainThreadSceneList;
    private SoftReference<View> rootView;
    private String sceneId;
    private List<SoftReference<View>> viewList;
    private TaskWrapper viewWrapper;

    @Override // com.xiaopeng.vui.commons.IVuiElementBuilder
    public List<VuiElement> build(int fatherElementId, View view) {
        return build(fatherElementId, view);
    }

    @Override // com.xiaopeng.vui.commons.IVuiElementBuilder
    public List<VuiElement> build(int fatherElementId, List<View> list) {
        return build(fatherElementId, list);
    }

    public BuildSceneTask(TaskWrapper viewWrapper) {
        super(viewWrapper);
        this.isWholeScene = true;
        this.TAG = "VuiEngine_BuildSceneTask";
        this.mIdList = new ArrayList();
        this.elements = new ArrayList();
        this.mainThreadSceneList = Arrays.asList("MainNetRadioConcentration", "MainProgramEditorChoice", "MainMusicConcentration", "MainMineCollect", "MainMineHistory", "MainMineVip", "MainMinePlaylist");
        this.info = null;
        this.bizIds = new ArrayList();
        this.elementChangedListener = null;
        this.viewWrapper = viewWrapper;
        this.rootView = viewWrapper.getView();
        this.viewList = viewWrapper.getViewList();
        this.sceneId = viewWrapper.getSceneId();
        this.isWholeScene = viewWrapper.isWholeScene();
    }

    @Override // com.xiaopeng.speech.vui.task.base.Task
    public void execute() {
        BuildSceneTask buildSceneTask;
        long time;
        BuildSceneTask buildSceneTask2 = this;
        try {
            final long time2 = System.currentTimeMillis();
            if (buildSceneTask2.isWholeScene && !buildSceneTask2.sceneId.equals(VuiEngineImpl.mActiveSceneId)) {
                return;
            }
            String str = buildSceneTask2.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("buildScene-------------- sceneId:");
            sb.append(buildSceneTask2.sceneId);
            sb.append(",view:");
            sb.append(buildSceneTask2.rootView != null ? buildSceneTask2.rootView.get() : null);
            sb.append(",viewList:");
            sb.append(buildSceneTask2.viewList);
            LogUtils.logDebug(str, sb.toString());
            buildSceneTask2.info = VuiSceneManager.instance().getSceneInfo(buildSceneTask2.sceneId);
            boolean z = true;
            String sceneName = buildSceneTask2.sceneId.substring(buildSceneTask2.sceneId.lastIndexOf("-") + 1);
            if (buildSceneTask2.info != null) {
                buildSceneTask2.info.setBuild(true);
                buildSceneTask2.elementChangedListener = buildSceneTask2.info.getElementChangedListener();
            }
            if (buildSceneTask2.rootView != null && buildSceneTask2.rootView.get() != null) {
                if (buildSceneTask2.mainThreadSceneList.contains(sceneName)) {
                    SoftReference<View> recyclerView = VuiUtils.findRecyclerView(buildSceneTask2.rootView);
                    if (recyclerView != null && buildSceneTask2.isWholeScene) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.xiaopeng.speech.vui.task.BuildSceneTask.1
                            @Override // java.lang.Runnable
                            public void run() {
                                BuildSceneTask.this.buildView(time2);
                                BuildSceneTask.this.handleBuildElement(time2);
                            }
                        });
                    } else {
                        buildSceneTask2.buildView(time2);
                        buildSceneTask2.handleBuildElement(time2);
                    }
                    return;
                }
                buildSceneTask2.buildView(time2);
                buildSceneTask2.handleBuildElement(time2);
            } else if (buildSceneTask2.viewList != null) {
                int i = 0;
                while (i < buildSceneTask2.viewList.size()) {
                    SoftReference<View> view = buildSceneTask2.viewList.get(i);
                    LogUtils.logDebug(buildSceneTask2.TAG, "buildScene-------------- sceneId:" + buildSceneTask2.sceneId + ",第" + i + "个View" + view);
                    int i2 = i;
                    String sceneName2 = sceneName;
                    boolean z2 = z;
                    long time3 = time2;
                    try {
                        VuiElement element = buildView(view, buildSceneTask2.elements, buildSceneTask2.viewWrapper.getCustomizeIds(), buildSceneTask2.viewWrapper.getElementListener(), buildSceneTask2.mIdList, time2, null, buildSceneTask2.bizIds, null, 0, (view == null || !(view.get() instanceof IVuiElement)) ? false : ((IVuiElement) view.get()).isVuiLayoutLoadable(), (view == null || !(view.get() instanceof RecyclerView)) ? false : z, (view == null || !(view.get() instanceof RecyclerView)) ? buildSceneTask2.elementChangedListener : null);
                        if (element == null || element.getId() == null) {
                            buildSceneTask = this;
                            time = time3;
                        } else {
                            time = time3;
                            element.setTimestamp(time);
                            buildSceneTask = this;
                            try {
                                buildSceneTask.elements.add(element);
                                buildSceneTask.setVuiTag(view, element.getId());
                            } catch (Exception e) {
                                e = e;
                                LogUtils.e(buildSceneTask.TAG, "e:" + e.getMessage());
                                return;
                            }
                        }
                        i = i2 + 1;
                        time2 = time;
                        buildSceneTask2 = buildSceneTask;
                        z = z2;
                        sceneName = sceneName2;
                    } catch (Exception e2) {
                        e = e2;
                        buildSceneTask = this;
                        LogUtils.e(buildSceneTask.TAG, "e:" + e.getMessage());
                        return;
                    }
                }
                buildSceneTask = buildSceneTask2;
                buildSceneTask.handleBuildElement(time2);
            }
        } catch (Exception e3) {
            e = e3;
            buildSceneTask = buildSceneTask2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void buildView(long time) {
        VuiSceneInfo vuiSceneInfo;
        if (!this.isWholeScene && (vuiSceneInfo = this.info) != null && vuiSceneInfo.isBuildComplete()) {
            VuiSceneCache buildCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
            if (buildCache != null) {
                this.elements = buildCache.getCache(this.sceneId);
            }
            return;
        }
        SoftReference<View> recyclerView = VuiUtils.findRecyclerView(this.rootView);
        SoftReference<View> softReference = this.rootView;
        List<VuiElement> list = this.elements;
        List<Integer> customizeIds = this.viewWrapper.getCustomizeIds();
        SoftReference<IVuiElementListener> elementListener = this.viewWrapper.getElementListener();
        List<String> list2 = this.mIdList;
        List<String> list3 = this.bizIds;
        SoftReference<View> softReference2 = this.rootView;
        boolean isVuiLayoutLoadable = (softReference2 == null || !(softReference2.get() instanceof IVuiElement)) ? false : ((IVuiElement) this.rootView.get()).isVuiLayoutLoadable();
        SoftReference<View> softReference3 = this.rootView;
        VuiElement element = buildView(softReference, list, customizeIds, elementListener, list2, time, null, list3, null, 0, isVuiLayoutLoadable, softReference3 != null && (softReference3.get() instanceof RecyclerView), recyclerView == null ? this.elementChangedListener : null);
        if (this.elements.size() == 0 && element != null && element.getId() != null) {
            element.setTimestamp(time);
            element.setVisible(null);
            this.elements.add(element);
            setVuiTag(this.rootView, element.getId());
        }
        if (recyclerView != null && !recyclerView.equals(this.rootView)) {
            addVuiElementChangedListener();
        }
    }

    private void addVuiElementChangedListener() {
        if ("com.android.systemui".equals(VuiSceneManager.instance().getmPackageName()) || this.elementChangedListener == null) {
            return;
        }
        Queue<SoftReference<View>> queue = new LinkedList<>();
        queue.offer(this.rootView);
        while (!queue.isEmpty()) {
            SoftReference<View> view = queue.poll();
            String tag = (view == null || view.get() == null) ? null : (String) view.get().getTag(R.id.vuiElementId);
            if (tag != null && this.mIdList.contains(tag) && (view.get() instanceof IVuiElement)) {
                IVuiElement vuiElement = (IVuiElement) view.get();
                if (vuiElement.getVuiElementId() == null) {
                    vuiElement.setVuiElementId(tag);
                }
                vuiElement.setVuiElementChangedListener(this.elementChangedListener);
            } else if (view != null && view.get() != null && (view instanceof IVuiElement)) {
                String ownerTag = (String) view.get().getTag();
                IVuiElement vuiElement2 = (IVuiElement) view.get();
                if (ownerTag != null && ownerTag.startsWith("4657")) {
                    vuiElement2.setVuiElementChangedListener(this.elementChangedListener);
                }
            }
            if (view != null && !(view.get() instanceof RecyclerView) && (view.get() instanceof ViewGroup)) {
                SoftReference<ViewGroup> group = new SoftReference<>((ViewGroup) view.get());
                for (int i = 0; group.get() != null && i < group.get().getChildCount(); i++) {
                    SoftReference<View> child = new SoftReference<>(group.get().getChildAt(i));
                    queue.offer(child);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleBuildElement(long time) {
        boolean z;
        VuiSceneInfo vuiSceneInfo;
        VuiSceneInfo vuiSceneInfo2;
        VuiSceneManager.instance().setSceneIdList(this.sceneId, this.mIdList);
        List<String> subSceneIds = this.viewWrapper.getSubSceneIds();
        List<String> buildSubSceneList = new ArrayList<>();
        if (subSceneIds != null) {
            int size = subSceneIds.size();
            for (int i = 0; i < size; i++) {
                String subSceneId = subSceneIds.get(i);
                IVuiSceneListener listener = VuiSceneManager.instance().getVuiSceneListener(subSceneId);
                if (listener == null) {
                    VuiSceneInfo vuiSceneInfo3 = this.info;
                    if (vuiSceneInfo3 != null) {
                        vuiSceneInfo3.updateAddSubSceneNum();
                    }
                    VuiSceneManager.instance().initSubSceneInfo(subSceneId, this.sceneId);
                } else {
                    VuiSceneCache buildCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
                    if (buildCache != null) {
                        List<VuiElement> subElements = buildCache.getCache(subSceneId);
                        if (subElements != null) {
                            this.elements.addAll(i, subElements);
                            VuiSceneInfo vuiSceneInfo4 = this.info;
                            if (vuiSceneInfo4 != null) {
                                vuiSceneInfo4.updateAddSubSceneNum();
                            }
                            VuiSceneManager.instance().setWholeSceneId(subSceneId, this.sceneId);
                        } else {
                            buildSubSceneList.add(subSceneId);
                        }
                    } else {
                        buildSubSceneList.add(subSceneId);
                    }
                }
            }
            VuiSceneManager.instance().addSubSceneIds(this.sceneId, subSceneIds);
        }
        VuiSceneManager.instance().setIsWholeScene(this.sceneId, this.isWholeScene);
        if (this.elements.size() > 0) {
            VuiScene vuiScene = getNewVuiScene(this.sceneId, time);
            vuiScene.setElements(this.elements);
            VuiSceneCache buildCache2 = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
            String str = this.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("buildScene completed:");
            sb.append(this.sceneId);
            sb.append(",time:");
            sb.append(System.currentTimeMillis() - time);
            sb.append(this.isWholeScene ? "full scene build completed" : "");
            LogUtils.logInfo(str, sb.toString());
            boolean z2 = false;
            boolean z3 = true;
            if (this.isWholeScene && (vuiSceneInfo2 = this.info) != null && vuiSceneInfo2.isFull()) {
                setWholeSceneCache(this.sceneId, buildCache2, this.elements);
                VuiSceneInfo vuiSceneInfo5 = this.info;
                if (vuiSceneInfo5 != null) {
                    vuiSceneInfo5.setBuildComplete(true);
                }
                VuiSceneManager.instance().sendSceneData(VuiSceneManager.TYPE_BUILD, false, vuiScene);
            } else {
                if (!this.isWholeScene && (vuiSceneInfo = this.info) != null && !vuiSceneInfo.isBuildComplete()) {
                    if (buildCache2 != null) {
                        buildCache2.setCache(this.sceneId, this.elements);
                    }
                    this.info.setBuildComplete(true);
                } else if (this.isWholeScene) {
                    setWholeSceneCache(this.sceneId, buildCache2, this.elements);
                }
                List<String> wholeSceneIds = null;
                VuiSceneInfo vuiSceneInfo6 = this.info;
                if (vuiSceneInfo6 != null) {
                    wholeSceneIds = vuiSceneInfo6.getWholeSceneId();
                }
                if (wholeSceneIds != null) {
                    int size2 = wholeSceneIds.size();
                    int i2 = 0;
                    while (i2 < size2) {
                        String wholeSceneId = wholeSceneIds.get(i2);
                        VuiSceneCache buildCache3 = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
                        VuiSceneInfo wholeSceneInfo = VuiSceneManager.instance().getSceneInfo(wholeSceneId);
                        if (wholeSceneInfo != null && wholeSceneInfo.isFull()) {
                            vuiScene = getNewVuiScene(wholeSceneId, time);
                            vuiScene.setElements(this.elements);
                            setSubSceneElementToCache(wholeSceneId, buildCache3, this.elements);
                            LogUtils.logInfo(this.TAG, "main scene update");
                            VuiSceneManager.instance().sendSceneData(VuiSceneManager.TYPE_UPDATE, z3, vuiScene);
                            z = z2;
                        } else if (wholeSceneInfo == null) {
                            z = z2;
                        } else if (wholeSceneInfo.isFull()) {
                            z = z2;
                        } else {
                            wholeSceneInfo.updateAddSubSceneNum();
                            List<VuiElement> wholeElements = setSubSceneElementToCache(wholeSceneId, buildCache3, this.elements);
                            if (!wholeSceneInfo.isFull()) {
                                z = z2;
                            } else {
                                wholeSceneInfo.setBuildComplete(z3);
                                String str2 = this.TAG;
                                LogUtils.logInfo(str2, wholeSceneId + " full scene build completed ");
                                if (!wholeSceneId.equals(VuiEngineImpl.mActiveSceneId)) {
                                    z = false;
                                } else {
                                    vuiScene = getNewVuiScene(wholeSceneId, time);
                                    vuiScene.setElements(wholeElements);
                                    z = false;
                                    VuiSceneManager.instance().sendSceneData(VuiSceneManager.TYPE_BUILD, false, vuiScene);
                                }
                            }
                        }
                        i2++;
                        z2 = z;
                        z3 = true;
                    }
                }
                buildSubScenes(buildSubSceneList);
            }
            if (this.viewWrapper.getSceneCallbackHandler() != null) {
                this.viewWrapper.getSceneCallbackHandler().onBuildFinished(vuiScene);
            }
        } else if (this.isWholeScene && buildSubSceneList.size() > 0) {
            buildSubScenes(buildSubSceneList);
        } else {
            LogUtils.e(this.TAG, "请确认此场景是否包含支持VUI操作的控件");
        }
    }

    private void setWholeSceneCache(String sceneId, VuiSceneCache buildCache, List<VuiElement> elements) {
        if (buildCache != null) {
            List<VuiElement> cache = buildCache.getUpdateFusionCache(sceneId, elements, false);
            buildCache.setCache(sceneId, cache);
        }
    }

    private List<VuiElement> setSubSceneElementToCache(String sceneId, VuiSceneCache buildCache, List<VuiElement> elements) {
        List<VuiElement> wholeElements = buildCache.getCache(sceneId);
        if (wholeElements == null) {
            wholeElements = new ArrayList();
        }
        if (wholeElements.contains(elements.get(0))) {
            return wholeElements;
        }
        SceneMergeUtils.merge(wholeElements, elements, false);
        buildCache.setCache(sceneId, wholeElements);
        if (!"user".equals(Build.TYPE) && LogUtils.getLogLevel() <= LogUtils.LOG_DEBUG_LEVEL && !sceneId.equals(VuiEngineImpl.mActiveSceneId)) {
            VuiScene vuiScene = getNewVuiScene(sceneId, System.currentTimeMillis());
            vuiScene.setElements(wholeElements);
            String str = this.TAG;
            LogUtils.logDebug(str, "buildScene full_scene_info:" + VuiUtils.vuiSceneConvertToString(vuiScene));
        }
        return wholeElements;
    }

    private void buildSubScenes(List<String> buildSubSceneList) {
        int buildSize = buildSubSceneList.size();
        if (buildSize > 0) {
            for (int i = 0; i < buildSize; i++) {
                String subSceneId = buildSubSceneList.get(i);
                IVuiSceneListener listener = VuiSceneManager.instance().getVuiSceneListener(subSceneId);
                if (listener != null) {
                    listener.onBuildScene();
                }
                VuiSceneManager.instance().setWholeSceneId(subSceneId, this.sceneId);
            }
        }
    }
}
