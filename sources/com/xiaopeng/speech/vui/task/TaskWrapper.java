package com.xiaopeng.speech.vui.task;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.xiaopeng.speech.vui.ISceneCallbackHandler;
import com.xiaopeng.speech.vui.task.TaskDispatcher;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.vui.commons.IVuiSceneListener;
import com.xiaopeng.vui.commons.VuiPriority;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class TaskWrapper {
    private List<Integer> customizeIds;
    private String elementGroupId;
    private SoftReference<IVuiElementListener> elementListener;
    private boolean isContainNotChildrenView;
    private boolean isWholeScene;
    private IVuiSceneListener listener;
    private ISceneCallbackHandler mSceneCallbackHandler;
    private String parentElementId;
    private VuiPriority priority;
    private SoftReference<RecyclerView> recyclerView;
    private String sceneId;
    private List<String> subSceneIds;
    private TaskDispatcher.TaskType taskType;
    private String vid;
    private SoftReference<View> view;
    private List<SoftReference<View>> viewList;

    public boolean isContainNotChildrenView() {
        return this.isContainNotChildrenView;
    }

    public boolean isWholeScene() {
        return this.isWholeScene;
    }

    public List<SoftReference<View>> getViewList() {
        return this.viewList;
    }

    public List<String> getSubSceneIds() {
        return this.subSceneIds;
    }

    public TaskWrapper() {
        this.isWholeScene = true;
        this.isContainNotChildrenView = false;
    }

    public TaskWrapper(String sceneId, TaskDispatcher.TaskType taskType, List<View> viewList) {
        this.isWholeScene = true;
        this.isContainNotChildrenView = false;
        this.sceneId = sceneId;
        this.taskType = taskType;
        revertViewListToSoftReference(viewList);
    }

    public TaskWrapper(String sceneId, TaskDispatcher.TaskType taskType, List<View> viewList, RecyclerView recyclerView) {
        this.isWholeScene = true;
        this.isContainNotChildrenView = false;
        this.sceneId = sceneId;
        this.taskType = taskType;
        revertViewListToSoftReference(viewList);
        this.recyclerView = new SoftReference<>(recyclerView);
    }

    private void revertViewListToSoftReference(List<View> viewList) {
        if (viewList != null) {
            this.viewList = new ArrayList();
            for (int i = 0; i < viewList.size(); i++) {
                SoftReference<View> curView = new SoftReference<>(viewList.get(i));
                this.viewList.add(curView);
            }
        }
    }

    public TaskWrapper(String sceneId, TaskDispatcher.TaskType taskType, View view) {
        this.isWholeScene = true;
        this.isContainNotChildrenView = false;
        this.sceneId = sceneId;
        this.taskType = taskType;
        this.view = new SoftReference<>(view);
    }

    public TaskWrapper(String sceneId, TaskDispatcher.TaskType taskType, View view, RecyclerView recyclerView) {
        this.isWholeScene = true;
        this.isContainNotChildrenView = false;
        this.sceneId = sceneId;
        this.taskType = taskType;
        this.view = new SoftReference<>(view);
        this.recyclerView = new SoftReference<>(recyclerView);
    }

    public TaskWrapper(View view, int vid, String sceneId, TaskDispatcher.TaskType taskType) {
        this.isWholeScene = true;
        this.isContainNotChildrenView = false;
        this.sceneId = sceneId;
        this.taskType = taskType;
        this.view = new SoftReference<>(view);
        this.vid = String.valueOf(vid);
    }

    public TaskWrapper(View view, int vid, String sceneId, TaskDispatcher.TaskType taskType, RecyclerView recyclerView) {
        this.isWholeScene = true;
        this.isContainNotChildrenView = false;
        this.sceneId = sceneId;
        this.taskType = taskType;
        this.view = new SoftReference<>(view);
        this.vid = String.valueOf(vid);
        this.recyclerView = new SoftReference<>(recyclerView);
    }

    public TaskWrapper(String sceneId, VuiPriority priority, TaskDispatcher.TaskType taskType, List<View> viewList, boolean containNotChildrenView) {
        this.isWholeScene = true;
        this.isContainNotChildrenView = false;
        revertViewListToSoftReference(viewList);
        this.sceneId = sceneId;
        this.priority = priority;
        this.taskType = taskType;
        this.isContainNotChildrenView = containNotChildrenView;
    }

    public TaskWrapper(View view, String sceneId, int vid, TaskDispatcher.TaskType taskType, VuiPriority priority, IVuiSceneListener listener) {
        this.isWholeScene = true;
        this.isContainNotChildrenView = false;
        this.view = new SoftReference<>(view);
        this.sceneId = sceneId;
        this.vid = String.valueOf(vid);
        this.taskType = taskType;
        this.priority = priority;
        this.listener = listener;
    }

    public TaskWrapper(View view, String sceneId, int vid, TaskDispatcher.TaskType taskType, String parentElementId) {
        this.isWholeScene = true;
        this.isContainNotChildrenView = false;
        this.view = new SoftReference<>(view);
        this.sceneId = sceneId;
        this.vid = String.valueOf(vid);
        this.taskType = taskType;
        this.parentElementId = parentElementId;
    }

    public TaskWrapper(View view, String sceneId, int vid, TaskDispatcher.TaskType taskType, List<Integer> ids, IVuiElementListener callback) {
        this.isWholeScene = true;
        this.isContainNotChildrenView = false;
        this.view = new SoftReference<>(view);
        this.sceneId = sceneId;
        this.vid = String.valueOf(vid);
        this.taskType = taskType;
        this.customizeIds = ids;
        this.elementListener = new SoftReference<>(callback);
    }

    public TaskWrapper(View view, String sceneId, TaskDispatcher.TaskType taskType, List<Integer> ids, IVuiElementListener callback) {
        this.isWholeScene = true;
        this.isContainNotChildrenView = false;
        this.view = new SoftReference<>(view);
        this.sceneId = sceneId;
        this.taskType = taskType;
        this.customizeIds = ids;
        this.elementListener = new SoftReference<>(callback);
    }

    public TaskWrapper(List<View> viewList, String sceneId, TaskDispatcher.TaskType taskType, List<Integer> ids, IVuiElementListener callback) {
        this.isWholeScene = true;
        this.isContainNotChildrenView = false;
        revertViewListToSoftReference(viewList);
        this.sceneId = sceneId;
        this.taskType = taskType;
        this.elementListener = new SoftReference<>(callback);
        this.customizeIds = ids;
    }

    public TaskWrapper(View view, String sceneId, int vid, TaskDispatcher.TaskType taskType, List<Integer> customizeIds, IVuiElementListener elementListener, List<String> subSceneIds, boolean isWholeScene, ISceneCallbackHandler mSceneCallbackHandler) {
        this.isWholeScene = true;
        this.isContainNotChildrenView = false;
        this.view = new SoftReference<>(view);
        this.sceneId = sceneId;
        this.vid = String.valueOf(vid);
        this.taskType = taskType;
        this.customizeIds = customizeIds;
        this.elementListener = new SoftReference<>(elementListener);
        this.subSceneIds = subSceneIds;
        this.isWholeScene = isWholeScene;
        this.mSceneCallbackHandler = mSceneCallbackHandler;
    }

    public TaskWrapper(List<View> viewList, String sceneId, TaskDispatcher.TaskType taskType, List<Integer> customizeIds, IVuiElementListener elementListener, List<String> subSceneIds, boolean isWholeScene, ISceneCallbackHandler mSceneCallbackHandler) {
        this.isWholeScene = true;
        this.isContainNotChildrenView = false;
        revertViewListToSoftReference(viewList);
        this.sceneId = sceneId;
        this.vid = String.valueOf(this.vid);
        this.taskType = taskType;
        this.customizeIds = customizeIds;
        this.elementListener = new SoftReference<>(elementListener);
        this.subSceneIds = subSceneIds;
        this.isWholeScene = isWholeScene;
        this.mSceneCallbackHandler = mSceneCallbackHandler;
    }

    public TaskWrapper(View view, String sceneId, TaskDispatcher.TaskType taskType, IVuiSceneListener listener, String elementGroupId) {
        this.isWholeScene = true;
        this.isContainNotChildrenView = false;
        this.view = new SoftReference<>(view);
        this.sceneId = sceneId;
        this.vid = elementGroupId;
        this.listener = listener;
        this.taskType = taskType;
        this.elementGroupId = elementGroupId;
    }

    public TaskDispatcher.TaskType getTaskType() {
        return this.taskType;
    }

    public List<Integer> getCustomizeIds() {
        return this.customizeIds;
    }

    public VuiPriority getPriority() {
        return this.priority;
    }

    public void setPriority(VuiPriority priority) {
        this.priority = priority;
    }

    public IVuiSceneListener getListener() {
        return this.listener;
    }

    public void setListener(IVuiSceneListener listener) {
        this.listener = listener;
    }

    public String getParentElementId() {
        return this.parentElementId;
    }

    public String getElementGroupId() {
        return this.elementGroupId;
    }

    public void setElementGroupId(String elementGroupId) {
        this.elementGroupId = elementGroupId;
    }

    public void setParentElementId(String parentElementId) {
        this.parentElementId = parentElementId;
    }

    public void setCustomizeIds(List<Integer> customizeIds) {
        this.customizeIds = customizeIds;
    }

    public SoftReference<IVuiElementListener> getElementListener() {
        return this.elementListener;
    }

    public void setElementListener(IVuiElementListener elementListener) {
        this.elementListener = new SoftReference<>(elementListener);
    }

    public void setTaskType(TaskDispatcher.TaskType taskType) {
        this.taskType = taskType;
    }

    public SoftReference<View> getView() {
        return this.view;
    }

    public void setView(View view) {
        this.view = new SoftReference<>(view);
    }

    public String getSceneId() {
        return this.sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }

    public String getVid() {
        return this.vid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public ISceneCallbackHandler getSceneCallbackHandler() {
        return this.mSceneCallbackHandler;
    }

    public void setReturnCallBack(ISceneCallbackHandler mSceneCallbackHandler) {
        this.mSceneCallbackHandler = mSceneCallbackHandler;
    }

    public SoftReference<RecyclerView> getRecyclerView() {
        return this.recyclerView;
    }
}
