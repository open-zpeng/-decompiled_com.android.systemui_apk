package com.xiaopeng.speech.vui.task.queue;

import android.text.TextUtils;
import android.view.View;
import com.xiaopeng.speech.vui.VuiSceneManager;
import com.xiaopeng.speech.vui.task.TaskDispatcher;
import com.xiaopeng.speech.vui.task.base.BaseTask;
import com.xiaopeng.speech.vui.utils.LogUtils;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class TaskWrapperQueue extends DeDuplicationQueue<String, BaseTask> {
    private String TAG = "TaskWrapperQueue";

    @Override // com.xiaopeng.speech.vui.task.queue.DeDuplicationQueue, java.util.concurrent.BlockingQueue, java.util.Queue
    public boolean offer(BaseTask task) {
        return putTaskToQueue(task, false);
    }

    @Override // com.xiaopeng.speech.vui.task.queue.DeDuplicationQueue, java.util.Queue
    public BaseTask poll() {
        return takeTaskFromQueue(false);
    }

    @Override // com.xiaopeng.speech.vui.task.queue.DeDuplicationQueue, java.util.concurrent.BlockingQueue
    public void put(BaseTask task) {
        putTaskToQueue(task, true);
    }

    @Override // com.xiaopeng.speech.vui.task.queue.DeDuplicationQueue, java.util.concurrent.BlockingQueue
    public BaseTask take() {
        return takeTaskFromQueue(true);
    }

    private boolean putTaskToQueue(BaseTask task, boolean isPut) {
        try {
        } catch (Exception e) {
            String str = this.TAG;
            LogUtils.e(str, "putTaskToQueue e:" + e.getMessage());
        }
        if (task.wrapper == null) {
            return false;
        }
        String str2 = this.TAG;
        LogUtils.d(str2, "队列缓存大小: offer:" + this.queue.size() + ",data" + this.queue.toString() + "," + Thread.currentThread().getName());
        String uniqueKey = task.wrapper.getVid();
        if (!TextUtils.isEmpty(uniqueKey) && this.map.containsKey(uniqueKey)) {
            LogUtils.i(this.TAG, "发现队列中相同id有数据");
            BaseTask currTask = (BaseTask) this.map.get(uniqueKey);
            if (currTask != null && currTask.wrapper != null) {
                if (currTask.wrapper.getTaskType() == task.wrapper.getTaskType()) {
                    this.queue.remove(currTask);
                    this.map.remove(uniqueKey);
                } else if (currTask.wrapper.getTaskType() == TaskDispatcher.TaskType.ADD && task.wrapper.getTaskType() == TaskDispatcher.TaskType.REMOVE) {
                    this.queue.remove(currTask);
                    this.map.remove(uniqueKey);
                    return true;
                }
                removeTaskFromSceneMap(currTask);
            }
            this.map.put(uniqueKey, task);
        } else {
            SoftReference<View> view = task.wrapper.getView();
            if (view != null && this.viewMap.containsKey(view)) {
                LogUtils.i(this.TAG, "发现队列中有相同view数据");
                BaseTask currTask2 = (BaseTask) this.viewMap.get(view);
                if (currTask2 != null && currTask2.wrapper != null) {
                    if (currTask2.wrapper.getTaskType() == task.wrapper.getTaskType()) {
                        this.queue.remove(currTask2);
                        this.viewMap.remove(view);
                    } else if (currTask2.wrapper.getTaskType() == TaskDispatcher.TaskType.ADD && task.wrapper.getTaskType() == TaskDispatcher.TaskType.REMOVE) {
                        this.queue.remove(currTask2);
                        this.viewMap.remove(view);
                        return true;
                    }
                    removeTaskFromSceneMap(currTask2);
                }
                this.viewMap.put(view, task);
            }
        }
        List<BaseTask> taskList = this.sceneMap.get(task.wrapper.getSceneId());
        if (taskList == null) {
            taskList = new ArrayList<>();
        }
        taskList.add(task);
        this.sceneMap.put(task.wrapper.getSceneId(), taskList);
        if (isPut) {
            this.queue.put(task);
        } else {
            this.queue.offer(task);
        }
        return true;
    }

    private BaseTask takeTaskFromQueue(boolean isTake) {
        BaseTask task = null;
        try {
            if (isTake) {
                task = (BaseTask) this.queue.take();
            } else {
                task = (BaseTask) this.queue.poll();
            }
            if (task != null && task.wrapper != null) {
                String uniqueKey = task.wrapper.getVid();
                if (!TextUtils.isEmpty(uniqueKey)) {
                    this.map.remove(uniqueKey);
                }
                SoftReference<View> view = task.wrapper.getView();
                if (view != null && this.viewMap.containsKey(view)) {
                    this.viewMap.remove(view);
                }
                removeTaskFromSceneMap(task);
                if (task.wrapper.getTaskType() != TaskDispatcher.TaskType.BUILD && !VuiSceneManager.instance().canUpdateScene(task.wrapper.getSceneId())) {
                    this.queue.remove(task);
                    String str = this.TAG;
                    LogUtils.d(str, "takeTaskFromQueue no cache:" + task.wrapper.getSceneId());
                    return null;
                }
            }
        } catch (Exception e) {
            String str2 = this.TAG;
            LogUtils.e(str2, "takeTaskFromQueue e:" + e.getMessage());
        }
        return task;
    }
}
