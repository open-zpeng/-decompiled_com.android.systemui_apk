package com.android.systemui.screenshot;

import android.app.Notification;
import android.content.ComponentName;
import android.graphics.Bitmap;
import android.util.Log;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
/* loaded from: classes21.dex */
public class ScreenshotNotificationSmartActionsProvider {
    public static final String ACTION_TYPE = "action_type";
    public static final String DEFAULT_ACTION_TYPE = "Smart Action";
    private static final String TAG = "ScreenshotActions";

    /* loaded from: classes21.dex */
    protected enum ScreenshotOp {
        OP_UNKNOWN,
        RETRIEVE_SMART_ACTIONS,
        REQUEST_SMART_ACTIONS,
        WAIT_FOR_SMART_ACTIONS
    }

    /* loaded from: classes21.dex */
    protected enum ScreenshotOpStatus {
        OP_STATUS_UNKNOWN,
        SUCCESS,
        ERROR,
        TIMEOUT
    }

    public CompletableFuture<List<Notification.Action>> getActions(String screenshotId, Bitmap bitmap, ComponentName componentName, boolean isManagedProfile) {
        Log.d(TAG, "Returning empty smart action list.");
        return CompletableFuture.completedFuture(Collections.emptyList());
    }

    public void notifyOp(String screenshotId, ScreenshotOp op, ScreenshotOpStatus status, long durationMs) {
        Log.d(TAG, "Return without notify.");
    }

    public void notifyAction(String screenshotId, String action, boolean isSmartAction) {
        Log.d(TAG, "Return without notify.");
    }
}
