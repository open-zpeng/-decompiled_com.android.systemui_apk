package com.xiaopeng.speech.vui.task;

import com.xiaopeng.speech.vui.task.base.BaseTask;
import com.xiaopeng.speech.vui.utils.LogUtils;
import java.util.List;
/* loaded from: classes.dex */
public class TaskDispatcher {
    private String TAG = getClass().getSimpleName();

    /* loaded from: classes.dex */
    public enum TaskType {
        BUILD,
        UPDATE,
        ADD,
        REMOVE,
        UPDATEATTRIBUTE,
        UPDATERECYCLEVIEWITEM
    }

    public void dispatchTask(List<TaskWrapper> viewWrappers) {
        BaseTask task;
        if (viewWrappers == null || viewWrappers.isEmpty()) {
            return;
        }
        for (TaskWrapper viewWrapper : viewWrappers) {
            switch (viewWrapper.getTaskType()) {
                case ADD:
                    task = new AddSceneTask(viewWrapper);
                    break;
                case BUILD:
                    task = new BuildSceneTask(viewWrapper);
                    break;
                case REMOVE:
                    task = new RemoveTask(viewWrapper);
                    break;
                case UPDATE:
                    task = new UpdateSceneTask(viewWrapper);
                    break;
                case UPDATEATTRIBUTE:
                    task = new UpdateSceneAttributeTask(viewWrapper);
                    break;
                case UPDATERECYCLEVIEWITEM:
                    task = new UpdateRecyclerItemTask(viewWrapper);
                    break;
                default:
                    task = new BuildSceneTask(viewWrapper);
                    break;
            }
            submitTask(task);
        }
    }

    public void submitTask(BaseTask task) {
        if (task == null || task.wrapper == null) {
            return;
        }
        try {
            ThreadPoolProxyFactory.getThreadPool().execute(task);
        } catch (Exception e) {
            String str = this.TAG;
            LogUtils.e(str, "submitTask e:" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void removeTask(String sceneId) {
        ThreadPoolProxyFactory.getThreadPool().removeTask(sceneId);
    }
}
