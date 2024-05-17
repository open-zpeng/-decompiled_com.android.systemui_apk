package com.xiaopeng.speech.vui.task;

import android.os.Build;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
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
import com.xiaopeng.vui.commons.IVuiSceneListener;
import com.xiaopeng.vui.commons.VuiPriority;
import com.xiaopeng.vui.commons.model.VuiElement;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public class AddSceneTask extends BaseTask {
    private String TAG;
    private TaskWrapper viewWrapper;

    public AddSceneTask(TaskWrapper viewWrapper) {
        super(viewWrapper);
        this.TAG = "VuiEngine_AddSceneTask";
        this.viewWrapper = viewWrapper;
    }

    @Override // com.xiaopeng.speech.vui.task.base.Task
    public void execute() {
        if (this.viewWrapper.isContainNotChildrenView()) {
            addNotChildViewToScene(this.viewWrapper.getViewList(), this.viewWrapper.getSceneId(), this.viewWrapper.getPriority());
        } else if (this.viewWrapper.getPriority() != null) {
            addSceneElementGroup(this.viewWrapper.getView(), this.viewWrapper.getSceneId(), this.viewWrapper.getPriority(), this.viewWrapper.getListener());
        } else {
            addSceneElement(this.viewWrapper.getView(), this.viewWrapper.getParentElementId(), this.viewWrapper.getSceneId());
        }
    }

    private void addNotChildViewToScene(List<SoftReference<View>> viewList, String sceneId, VuiPriority priority) {
        AddSceneTask addSceneTask;
        List<String> idList;
        boolean z;
        long time;
        List<String> allIdList;
        VuiSceneInfo info;
        VuiScene vuiScene;
        boolean z2;
        List<SoftReference<View>> notChildViewList;
        VuiSceneCache vuiSceneCache;
        List<VuiElement> elements;
        long time2;
        String str;
        int i;
        List<String> rootViewIdList;
        AddSceneTask addSceneTask2 = this;
        List<SoftReference<View>> list = viewList;
        String str2 = sceneId;
        if (list != null && str2 != null) {
            try {
                if (!VuiSceneManager.instance().canUpdateScene(str2)) {
                    return;
                }
                List<String> idList2 = new ArrayList<>();
                long time3 = System.currentTimeMillis();
                VuiScene vuiScene2 = addSceneTask2.getNewVuiScene(str2, time3);
                List<VuiElement> elements2 = new ArrayList<>();
                List<String> allIdList2 = VuiSceneManager.instance().getSceneIdList(str2);
                List<String> bizIds = new ArrayList<>();
                List<String> rootViewIdList2 = new ArrayList<>();
                VuiSceneInfo info2 = VuiSceneManager.instance().getSceneInfo(str2);
                if (info2 == null) {
                    return;
                }
                IVuiElementChangedListener elementChangedListener = info2.getElementChangedListener();
                info2.setContainNotChildrenView(true);
                List<SoftReference<View>> notChildViewList2 = new ArrayList<>();
                boolean isRecyclerView = false;
                VuiSceneCache vuiSceneCache2 = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
                List<String> rootViewIdList3 = rootViewIdList2;
                int i2 = 0;
                while (i2 < viewList.size()) {
                    SoftReference<View> view = list.get(i2);
                    if (view != null) {
                        List<SoftReference<View>> notChildViewList3 = notChildViewList2;
                        if (!(view.get() instanceof IVuiElement)) {
                            info = info2;
                            vuiScene = vuiScene2;
                            z2 = false;
                            long j = time3;
                            vuiSceneCache = vuiSceneCache2;
                            elements = elements2;
                            time2 = j;
                            str = str2;
                            notChildViewList = notChildViewList3;
                            List<String> list2 = rootViewIdList3;
                            i = i2;
                            addSceneTask = addSceneTask2;
                            rootViewIdList = list2;
                        } else {
                            if (view.get() instanceof RecyclerView) {
                                isRecyclerView = true;
                            }
                            z2 = false;
                            VuiSceneCache vuiSceneCache3 = vuiSceneCache2;
                            info = info2;
                            List<String> rootViewIdList4 = rootViewIdList3;
                            i = i2;
                            vuiScene = vuiScene2;
                            List<VuiElement> elements3 = elements2;
                            long time4 = time3;
                            try {
                                VuiElement element = buildView(view, elements2, null, null, idList2, time3, allIdList2, bizIds, null, 0, view.get() instanceof IVuiElement ? ((IVuiElement) view.get()).isVuiLayoutLoadable() : false, isRecyclerView, isRecyclerView ? null : elementChangedListener);
                                if (element == null || element.getId() == null) {
                                    addSceneTask = this;
                                    str = sceneId;
                                    notChildViewList = notChildViewList3;
                                    vuiSceneCache = vuiSceneCache3;
                                    rootViewIdList = rootViewIdList4;
                                    elements = elements3;
                                    time2 = time4;
                                } else {
                                    time2 = time4;
                                    element.setTimestamp(time2);
                                    addSceneTask = this;
                                    try {
                                        addSceneTask.setVuiTag(view, element.getId());
                                        str = sceneId;
                                        vuiSceneCache = vuiSceneCache3;
                                    } catch (Exception e) {
                                        e = e;
                                        LogUtils.e(addSceneTask.TAG, "addNotChildViewToScene e:" + e.getMessage());
                                        return;
                                    }
                                    try {
                                        VuiElement targetElement = vuiSceneCache.getVuiElementById(str, element.getId());
                                        if (targetElement != null && element.equals(targetElement)) {
                                            notChildViewList = notChildViewList3;
                                            rootViewIdList = rootViewIdList4;
                                            elements = elements3;
                                        } else {
                                            elements = elements3;
                                            elements.add(element);
                                            notChildViewList = notChildViewList3;
                                            notChildViewList.add(view);
                                            rootViewIdList = rootViewIdList4;
                                            rootViewIdList.add(element.getId());
                                        }
                                    } catch (Exception e2) {
                                        e = e2;
                                        LogUtils.e(addSceneTask.TAG, "addNotChildViewToScene e:" + e.getMessage());
                                        return;
                                    }
                                }
                            } catch (Exception e3) {
                                e = e3;
                                addSceneTask = this;
                            }
                        }
                    } else {
                        info = info2;
                        vuiScene = vuiScene2;
                        z2 = false;
                        String str3 = str2;
                        notChildViewList = notChildViewList2;
                        long j2 = time3;
                        vuiSceneCache = vuiSceneCache2;
                        elements = elements2;
                        time2 = j2;
                        str = str3;
                        List<String> list3 = rootViewIdList3;
                        i = i2;
                        addSceneTask = addSceneTask2;
                        rootViewIdList = list3;
                    }
                    int i3 = i + 1;
                    list = viewList;
                    rootViewIdList3 = rootViewIdList;
                    info2 = info;
                    vuiScene2 = vuiScene;
                    addSceneTask2 = addSceneTask;
                    i2 = i3;
                    List<SoftReference<View>> list4 = notChildViewList;
                    str2 = str;
                    elements2 = elements;
                    vuiSceneCache2 = vuiSceneCache;
                    time3 = time2;
                    notChildViewList2 = list4;
                }
                VuiSceneInfo info3 = info2;
                VuiScene vuiScene3 = vuiScene2;
                String str4 = str2;
                List<SoftReference<View>> notChildViewList4 = notChildViewList2;
                long j3 = time3;
                VuiSceneCache vuiSceneCache4 = vuiSceneCache2;
                List<VuiElement> elements4 = elements2;
                long time5 = j3;
                List<String> rootViewIdList5 = rootViewIdList3;
                AddSceneTask addSceneTask3 = addSceneTask2;
                int i4 = 0;
                while (i4 < elements4.size()) {
                    VuiElement curElement = elements4.get(i4);
                    VuiElement targetElement2 = vuiSceneCache4.getVuiElementById(str4, curElement.getId());
                    if (targetElement2 != null && curElement.equals(targetElement2)) {
                        elements4.remove(curElement);
                    } else {
                        i4++;
                    }
                }
                int i5 = elements4.size();
                if (i5 > 0) {
                    vuiScene3.setElements(elements4);
                    if ("user".equals(Build.TYPE) || LogUtils.getLogLevel() > LogUtils.LOG_DEBUG_LEVEL) {
                        idList = allIdList2;
                    } else {
                        String str5 = addSceneTask3.TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("addNotChildViewToScene completed time:");
                        idList = allIdList2;
                        sb.append(System.currentTimeMillis() - time5);
                        sb.append(",");
                        sb.append(VuiUtils.vuiUpdateSceneConvertToString(vuiScene3));
                        LogUtils.logDebug(str5, sb.toString());
                    }
                    if (info3.getNotChildrenViewList() != null && info3.getNotChildrenViewIdList() != null) {
                        List<String> removeList = new ArrayList<>();
                        if (notChildViewList4.size() < info3.getNotChildrenViewList().size()) {
                            int i6 = 0;
                            while (i6 < info3.getNotChildrenViewIdList().size()) {
                                String id = info3.getNotChildrenViewIdList().get(i6);
                                if (rootViewIdList5.contains(id)) {
                                    time = time5;
                                    allIdList = idList;
                                } else {
                                    removeList.add(id);
                                    time = time5;
                                    allIdList = idList;
                                    VuiSceneManager.instance().sendSceneData(VuiSceneManager.TYPE_REMOVE, true, str4 + "," + id);
                                }
                                i6++;
                                idList = allIdList;
                                time5 = time;
                            }
                            z = true;
                        } else {
                            z = true;
                        }
                        if (removeList.size() == info3.getNotChildrenViewIdList().size()) {
                            VuiSceneManager.instance().sendSceneData(VuiSceneManager.TYPE_ADD, z, vuiScene3);
                        } else {
                            VuiSceneCache buildCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
                            if (buildCache != null) {
                                List<VuiElement> elementList = buildCache.getFusionCache(str4, elements4, false);
                                buildCache.setCache(str4, elementList);
                                if (!"user".equals(Build.TYPE) && LogUtils.getLogLevel() <= LogUtils.LOG_DEBUG_LEVEL) {
                                    VuiScene scene = addSceneTask3.getNewVuiScene(str4, System.currentTimeMillis());
                                    scene.setElements(elementList);
                                    LogUtils.logDebug(addSceneTask3.TAG, "addNotChildViewToScene full_scene_info" + VuiUtils.vuiSceneConvertToString(scene));
                                }
                            }
                            VuiSceneManager.instance().sendSceneData(VuiSceneManager.TYPE_UPDATE, z, vuiScene3);
                        }
                        info3.setNotChildrenViewList(notChildViewList4);
                        info3.setNotChildrenViewIdList(rootViewIdList5);
                        return;
                    }
                    info3.setNotChildrenViewList(notChildViewList4);
                    info3.setNotChildrenViewIdList(rootViewIdList5);
                    VuiSceneManager.instance().sendSceneData(VuiSceneManager.TYPE_ADD, true, vuiScene3);
                }
            } catch (Exception e4) {
                e = e4;
                addSceneTask = addSceneTask2;
            }
        }
    }

    public void addSceneElementGroup(SoftReference<View> rootView, String sceneId, VuiPriority priority, IVuiSceneListener listener) {
        AddSceneTask addSceneTask;
        VuiSceneInfo info;
        SoftReference<View> softReference;
        String str;
        long time;
        List<VuiElement> elements;
        try {
            List<String> idList = new ArrayList<>();
            if (!(rootView instanceof IVuiElement) || !VuiSceneManager.instance().canUpdateScene(sceneId) || (info = VuiSceneManager.instance().getSceneInfo(sceneId)) == null) {
                return;
            }
            ((IVuiElement) rootView).setVuiPriority(priority);
            long time2 = System.currentTimeMillis();
            VuiScene vuiScene = getNewVuiScene(sceneId, time2);
            List<VuiElement> elements2 = new ArrayList<>();
            List<String> allIdList = VuiSceneManager.instance().getSceneIdList(sceneId);
            List<String> bizIds = new ArrayList<>();
            VuiSceneCache vuiSceneCache = VuiSceneCacheFactory.instance().getSceneCache(VuiSceneCacheFactory.CacheType.BUILD.getType());
            try {
                VuiElement element = buildView(rootView, elements2, null, null, idList, time2, allIdList, bizIds, null, 0, (rootView == null || !(rootView.get() instanceof IVuiElement)) ? false : ((IVuiElement) rootView.get()).isVuiLayoutLoadable(), rootView != null && (rootView.get() instanceof RecyclerView), (rootView == null || !(rootView.get() instanceof RecyclerView)) ? info.getElementChangedListener() : null);
                if (element == null || element.getId() == null) {
                    addSceneTask = this;
                    softReference = rootView;
                    str = sceneId;
                    time = time2;
                    elements = elements2;
                } else {
                    time = time2;
                    element.setTimestamp(time);
                    addSceneTask = this;
                    softReference = rootView;
                    try {
                        addSceneTask.setVuiTag(softReference, element.getId());
                        str = sceneId;
                        try {
                            VuiElement targetElement = vuiSceneCache.getVuiElementById(str, element.getId());
                            if (targetElement != null) {
                                if (element.equals(targetElement)) {
                                    LogUtils.logDebug(addSceneTask.TAG, "addNotChildViewToScene element same");
                                    elements = elements2;
                                } else {
                                    elements = elements2;
                                    elements.add(element);
                                }
                            } else {
                                elements = elements2;
                                elements.add(element);
                            }
                        } catch (Exception e) {
                            e = e;
                            LogUtils.e(addSceneTask.TAG, "addSceneElementGroup e:" + e.getMessage());
                            return;
                        }
                    } catch (Exception e2) {
                        e = e2;
                        LogUtils.e(addSceneTask.TAG, "addSceneElementGroup e:" + e.getMessage());
                        return;
                    }
                }
                if (elements.size() > 0) {
                    vuiScene.setElements(elements);
                    LogUtils.logInfo(addSceneTask.TAG, "addSceneElementGroup completed time:" + (System.currentTimeMillis() - time));
                    if (!"user".equals(Build.TYPE) && LogUtils.getLogLevel() <= LogUtils.LOG_DEBUG_LEVEL) {
                        LogUtils.logDebug(addSceneTask.TAG, "addSceneElementGroup:" + VuiUtils.vuiUpdateSceneConvertToString(vuiScene));
                    }
                    int id = (softReference == null || rootView.get() == null) ? -1 : rootView.get().getId();
                    String subSceneid = str + "_" + id;
                    VuiSceneManager.instance().setSceneIdList(subSceneid, idList);
                    VuiSceneManager.instance().addSubSceneIds(str, Arrays.asList(subSceneid));
                    if (softReference != null && rootView.get() != null && listener != null) {
                        VuiSceneManager.instance().setSceneIdList(str, allIdList);
                        VuiSceneManager.instance().addVuiSceneListener(subSceneid, rootView.get(), listener, null, true);
                    }
                    VuiSceneManager.instance().sendSceneData(VuiSceneManager.TYPE_ADD, true, vuiScene);
                }
            } catch (Exception e3) {
                e = e3;
                addSceneTask = this;
            }
        } catch (Exception e4) {
            e = e4;
            addSceneTask = this;
        }
    }

    public void addSceneElement(SoftReference<View> view, String parentElementId, String sceneId) {
        VuiSceneInfo info;
        List<VuiElement> elements;
        long time;
        List<String> idList = new ArrayList<>();
        if ((view instanceof IVuiElement) && VuiSceneManager.instance().canUpdateScene(sceneId) && (info = VuiSceneManager.instance().getSceneInfo(sceneId)) != null) {
            long time2 = System.currentTimeMillis();
            VuiScene vuiScene = getNewVuiScene(sceneId, time2);
            List<VuiElement> elements2 = new ArrayList<>();
            List<String> allIdList = VuiSceneManager.instance().getSceneIdList(sceneId);
            List<String> bizIds = new ArrayList<>();
            boolean z = false;
            boolean isVuiLayoutLoadable = (view == null || !(view.get() instanceof IVuiElement)) ? false : ((IVuiElement) view.get()).isVuiLayoutLoadable();
            if (view != null && (view.get() instanceof RecyclerView)) {
                z = true;
            }
            VuiElement element = buildView(view, elements2, null, null, idList, time2, allIdList, bizIds, null, 0, isVuiLayoutLoadable, z, (view == null || !(view.get() instanceof RecyclerView)) ? info.getElementChangedListener() : null);
            if (element == null || element.getId() == null) {
                elements = elements2;
                time = time2;
            } else {
                time = time2;
                element.setTimestamp(time);
                elements = elements2;
                elements.add(element);
            }
            vuiScene.setElements(elements);
            VuiSceneManager.instance().setSceneIdList(sceneId, allIdList);
            if (!"user".equals(Build.TYPE) && LogUtils.getLogLevel() <= LogUtils.LOG_DEBUG_LEVEL) {
                String str = this.TAG;
                LogUtils.logDebug(str, "addSceneElement:" + VuiUtils.vuiSceneConvertToString(vuiScene) + "time:" + (System.currentTimeMillis() - time));
            }
            VuiSceneManager.instance().addSceneElement(vuiScene, parentElementId);
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
}
