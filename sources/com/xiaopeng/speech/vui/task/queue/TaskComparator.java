package com.xiaopeng.speech.vui.task.queue;

import android.text.TextUtils;
import com.xiaopeng.speech.vui.VuiEngineImpl;
import com.xiaopeng.speech.vui.task.base.BaseTask;
import com.xiaopeng.speech.vui.utils.LogUtils;
import java.util.Comparator;
/* loaded from: classes.dex */
public class TaskComparator implements Comparator<BaseTask> {
    private static final String TAG = "TaskComparator";

    @Override // java.util.Comparator
    public int compare(BaseTask currentTask, BaseTask queueTask) {
        String activeSceneId = VuiEngineImpl.mActiveSceneId;
        if (currentTask == null || queueTask == null) {
            return 0;
        }
        LogUtils.logDebug(TAG, "Comparator: activeScene: " + activeSceneId + " --  currentTask:" + currentTask.getSceneId() + " -- queueTask : " + queueTask.getSceneId());
        if (TextUtils.isEmpty(activeSceneId)) {
            LogUtils.logDebug(TAG, "compare: activeScene is null");
            return 0;
        } else if (currentTask.getSceneId().equals(queueTask.getSceneId())) {
            LogUtils.logDebug(TAG, "compare: currentTask == queueTask");
            return 0;
        } else if (activeSceneId.equals(currentTask.getSceneId())) {
            LogUtils.logDebug(TAG, "compare: currentTask is activeScene");
            return -1;
        } else if (!activeSceneId.equals(queueTask.getSceneId())) {
            return 0;
        } else {
            LogUtils.logDebug(TAG, "compare: queueTask is activeScene");
            return 1;
        }
    }
}
