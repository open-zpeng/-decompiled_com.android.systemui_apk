package com.xiaopeng.speech.vui.task;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.xiaopeng.speech.vui.VuiSceneManager;
import com.xiaopeng.speech.vui.model.VuiSceneInfo;
import com.xiaopeng.speech.vui.task.base.BaseTask;
import com.xiaopeng.speech.vui.utils.LogUtils;
import com.xiaopeng.vui.commons.IVuiElement;
import com.xiaopeng.vui.commons.model.VuiElement;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class RemoveTask extends BaseTask {
    private String TAG;
    private String sceneId;
    private TaskWrapper viewWrapper;

    public RemoveTask(TaskWrapper viewWrapper) {
        super(viewWrapper);
        this.TAG = "VuiEngine_RemoveTask";
        this.viewWrapper = viewWrapper;
        this.sceneId = viewWrapper.getSceneId();
    }

    @Override // com.xiaopeng.speech.vui.task.base.Task
    public void execute() {
        RemoveTask removeTask;
        int i;
        List<SoftReference<View>> rootViewList;
        List<String> removeIdList;
        RemoveTask removeTask2;
        RemoveTask removeTask3;
        boolean z;
        String str;
        RemoveTask removeTask4 = this;
        try {
            String str2 = ",";
            boolean z2 = true;
            removeTask = null;
            try {
                if (removeTask4.viewWrapper.getElementGroupId() == null) {
                    VuiSceneInfo info = VuiSceneManager.instance().getSceneInfo(removeTask4.sceneId);
                    if (info != null) {
                        info.setLastAddStr(null);
                    }
                    if (removeTask4.viewWrapper.getViewList() == null) {
                        LogUtils.logInfo(removeTask4.TAG, "RemoveTask: sceneId" + removeTask4.sceneId);
                        if (info != null && info.isContainNotChildrenView()) {
                            List<String> removeIdList2 = info.getNotChildrenViewIdList();
                            if (removeIdList2 != null) {
                                for (int i2 = 0; i2 < removeIdList2.size(); i2++) {
                                    String id = removeIdList2.get(i2);
                                    VuiSceneManager.instance().sendSceneData(VuiSceneManager.TYPE_REMOVE, true, removeTask4.sceneId + "," + id);
                                }
                            }
                            info.setContainNotChildrenView(false);
                            info.setNotChildrenViewIdList(null);
                            info.setNotChildrenViewList(null);
                        }
                    } else {
                        LogUtils.logDebug(removeTask4.TAG, "RemoveTask: view list" + removeTask4.sceneId);
                        List<String> idList = new ArrayList<>();
                        long time = System.currentTimeMillis();
                        List<String> allIdList = VuiSceneManager.instance().getSceneIdList(removeTask4.sceneId);
                        List<String> bizIds = new ArrayList<>();
                        List<VuiElement> elements = new ArrayList<>();
                        if (info != null && info.isContainNotChildrenView()) {
                            List<String> removeIdList3 = info.getNotChildrenViewIdList();
                            List<SoftReference<View>> rootViewList2 = info.getNotChildrenViewList();
                            int i3 = 0;
                            while (i3 < removeTask4.viewWrapper.getViewList().size()) {
                                SoftReference<View> view = removeTask4.viewWrapper.getViewList().get(i3);
                                if (!rootViewList2.contains(view)) {
                                    i = i3;
                                    rootViewList = rootViewList2;
                                    removeIdList = removeIdList3;
                                    RemoveTask removeTask5 = removeTask4;
                                    removeTask2 = removeTask;
                                    removeTask3 = removeTask5;
                                    String str3 = str2;
                                    z = z2;
                                    str = str3;
                                } else if (view == null) {
                                    i = i3;
                                    rootViewList = rootViewList2;
                                    removeIdList = removeIdList3;
                                    RemoveTask removeTask6 = removeTask4;
                                    removeTask2 = removeTask;
                                    removeTask3 = removeTask6;
                                    String str4 = str2;
                                    z = z2;
                                    str = str4;
                                } else if (!(view.get() instanceof IVuiElement)) {
                                    i = i3;
                                    rootViewList = rootViewList2;
                                    removeIdList = removeIdList3;
                                    RemoveTask removeTask7 = removeTask4;
                                    removeTask2 = removeTask;
                                    removeTask3 = removeTask7;
                                    String str5 = str2;
                                    z = z2;
                                    str = str5;
                                } else {
                                    i = i3;
                                    List<SoftReference<View>> rootViewList3 = rootViewList2;
                                    List<String> removeIdList4 = removeIdList3;
                                    String str6 = str2;
                                    try {
                                        VuiElement element = buildView(view, elements, null, null, idList, time, allIdList, bizIds, null, 0, view.get() instanceof IVuiElement ? ((IVuiElement) view.get()).isVuiLayoutLoadable() : false, view.get() instanceof RecyclerView ? z2 : false, view.get() instanceof RecyclerView ? null : info.getElementChangedListener());
                                        if (element == null || element.getId() == null) {
                                            z = true;
                                            removeTask3 = this;
                                            rootViewList = rootViewList3;
                                            removeIdList = removeIdList4;
                                            str = str6;
                                        } else {
                                            removeIdList = removeIdList4;
                                            if (!removeIdList.contains(element.getId())) {
                                                z = true;
                                                removeTask3 = this;
                                                rootViewList = rootViewList3;
                                                str = str6;
                                            } else {
                                                VuiSceneManager instance = VuiSceneManager.instance();
                                                int i4 = VuiSceneManager.TYPE_REMOVE;
                                                StringBuilder sb = new StringBuilder();
                                                removeTask3 = this;
                                                sb.append(removeTask3.sceneId);
                                                str = str6;
                                                sb.append(str);
                                                sb.append(element.getId());
                                                z = true;
                                                instance.sendSceneData(i4, true, sb.toString());
                                                removeIdList.remove(element.getId());
                                                rootViewList = rootViewList3;
                                                rootViewList.remove(view);
                                            }
                                        }
                                        if (removeIdList.isEmpty() && rootViewList.isEmpty()) {
                                            info.setContainNotChildrenView(false);
                                            removeTask2 = null;
                                            info.setNotChildrenViewIdList(null);
                                            info.setNotChildrenViewList(null);
                                        } else {
                                            removeTask2 = null;
                                            info.setNotChildrenViewIdList(removeIdList);
                                            info.setNotChildrenViewList(rootViewList);
                                        }
                                    } catch (Exception e) {
                                        e = e;
                                        removeTask = this;
                                        LogUtils.e(removeTask.TAG, "e:" + e.getMessage());
                                        return;
                                    }
                                }
                                removeIdList3 = removeIdList;
                                rootViewList2 = rootViewList;
                                i3 = i + 1;
                                RemoveTask removeTask8 = removeTask2;
                                removeTask4 = removeTask3;
                                removeTask = removeTask8;
                                boolean z3 = z;
                                str2 = str;
                                z2 = z3;
                            }
                        }
                    }
                    return;
                }
                String subSceneId = removeTask4.sceneId + "_" + removeTask4.viewWrapper.getElementGroupId();
                LogUtils.logInfo(removeTask4.TAG, "RemoveTask: subSceneid" + subSceneId);
                List<String> allIdList2 = VuiSceneManager.instance().getSceneIdList(removeTask4.sceneId);
                List<String> subSceneIdList = VuiSceneManager.instance().getSceneIdList(subSceneId);
                if (subSceneIdList != null) {
                    allIdList2.removeAll(subSceneIdList);
                    VuiSceneManager.instance().setSceneIdList(removeTask4.sceneId, allIdList2);
                    VuiSceneManager.instance().removeSubSceneIds(removeTask4.sceneId, subSceneId);
                    VuiSceneManager.instance().removeVuiSceneListener(subSceneId, false, false, null);
                }
                VuiSceneManager.instance().sendSceneData(VuiSceneManager.TYPE_REMOVE, true, removeTask4.sceneId + "," + removeTask4.viewWrapper.getElementGroupId());
            } catch (Exception e2) {
                e = e2;
            }
        } catch (Exception e3) {
            e = e3;
            removeTask = removeTask4;
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
