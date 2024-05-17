package com.android.systemui.stackdivider;

import android.app.ActivityTaskManager;
import android.graphics.Rect;
import android.os.RemoteException;
import android.util.Log;
import android.view.WindowManagerGlobal;
import com.android.internal.annotations.GuardedBy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/* loaded from: classes21.dex */
public class WindowManagerProxy {
    private static final String TAG = "WindowManagerProxy";
    private static final WindowManagerProxy sInstance = new WindowManagerProxy();
    private float mDimLayerAlpha;
    private int mDimLayerTargetWindowingMode;
    private boolean mDimLayerVisible;
    @GuardedBy({"mDockedRect"})
    private final Rect mDockedRect = new Rect();
    private final Rect mTempDockedTaskRect = new Rect();
    private final Rect mTempDockedInsetRect = new Rect();
    private final Rect mTempOtherTaskRect = new Rect();
    private final Rect mTempOtherInsetRect = new Rect();
    private final Rect mTmpRect1 = new Rect();
    private final Rect mTmpRect2 = new Rect();
    private final Rect mTmpRect3 = new Rect();
    private final Rect mTmpRect4 = new Rect();
    private final Rect mTmpRect5 = new Rect();
    @GuardedBy({"mDockedRect"})
    private final Rect mTouchableRegion = new Rect();
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private final Runnable mResizeRunnable = new Runnable() { // from class: com.android.systemui.stackdivider.WindowManagerProxy.1
        @Override // java.lang.Runnable
        public void run() {
            synchronized (WindowManagerProxy.this.mDockedRect) {
                WindowManagerProxy.this.mTmpRect1.set(WindowManagerProxy.this.mDockedRect);
                WindowManagerProxy.this.mTmpRect2.set(WindowManagerProxy.this.mTempDockedTaskRect);
                WindowManagerProxy.this.mTmpRect3.set(WindowManagerProxy.this.mTempDockedInsetRect);
                WindowManagerProxy.this.mTmpRect4.set(WindowManagerProxy.this.mTempOtherTaskRect);
                WindowManagerProxy.this.mTmpRect5.set(WindowManagerProxy.this.mTempOtherInsetRect);
            }
            try {
                ActivityTaskManager.getService().resizeDockedStack(WindowManagerProxy.this.mTmpRect1, WindowManagerProxy.this.mTmpRect2.isEmpty() ? null : WindowManagerProxy.this.mTmpRect2, WindowManagerProxy.this.mTmpRect3.isEmpty() ? null : WindowManagerProxy.this.mTmpRect3, WindowManagerProxy.this.mTmpRect4.isEmpty() ? null : WindowManagerProxy.this.mTmpRect4, WindowManagerProxy.this.mTmpRect5.isEmpty() ? null : WindowManagerProxy.this.mTmpRect5);
            } catch (RemoteException e) {
                Log.w(WindowManagerProxy.TAG, "Failed to resize stack: " + e);
            }
        }
    };
    private final Runnable mDismissRunnable = new Runnable() { // from class: com.android.systemui.stackdivider.WindowManagerProxy.2
        @Override // java.lang.Runnable
        public void run() {
            try {
                ActivityTaskManager.getService().dismissSplitScreenMode(false);
            } catch (RemoteException e) {
                Log.w(WindowManagerProxy.TAG, "Failed to remove stack: " + e);
            }
        }
    };
    private final Runnable mMaximizeRunnable = new Runnable() { // from class: com.android.systemui.stackdivider.WindowManagerProxy.3
        @Override // java.lang.Runnable
        public void run() {
            try {
                ActivityTaskManager.getService().dismissSplitScreenMode(true);
            } catch (RemoteException e) {
                Log.w(WindowManagerProxy.TAG, "Failed to resize stack: " + e);
            }
        }
    };
    private final Runnable mDimLayerRunnable = new Runnable() { // from class: com.android.systemui.stackdivider.WindowManagerProxy.4
        @Override // java.lang.Runnable
        public void run() {
            try {
                WindowManagerGlobal.getWindowManagerService().setResizeDimLayer(WindowManagerProxy.this.mDimLayerVisible, WindowManagerProxy.this.mDimLayerTargetWindowingMode, WindowManagerProxy.this.mDimLayerAlpha);
            } catch (RemoteException e) {
                Log.w(WindowManagerProxy.TAG, "Failed to resize stack: " + e);
            }
        }
    };
    private final Runnable mSetTouchableRegionRunnable = new Runnable() { // from class: com.android.systemui.stackdivider.WindowManagerProxy.5
        @Override // java.lang.Runnable
        public void run() {
            try {
                synchronized (WindowManagerProxy.this.mDockedRect) {
                    WindowManagerProxy.this.mTmpRect1.set(WindowManagerProxy.this.mTouchableRegion);
                }
                WindowManagerGlobal.getWindowManagerService().setDockedStackDividerTouchRegion(WindowManagerProxy.this.mTmpRect1);
            } catch (RemoteException e) {
                Log.w(WindowManagerProxy.TAG, "Failed to set touchable region: " + e);
            }
        }
    };

    private WindowManagerProxy() {
    }

    public static WindowManagerProxy getInstance() {
        return sInstance;
    }

    public void resizeDockedStack(Rect docked, Rect tempDockedTaskRect, Rect tempDockedInsetRect, Rect tempOtherTaskRect, Rect tempOtherInsetRect) {
        synchronized (this.mDockedRect) {
            this.mDockedRect.set(docked);
            if (tempDockedTaskRect != null) {
                this.mTempDockedTaskRect.set(tempDockedTaskRect);
            } else {
                this.mTempDockedTaskRect.setEmpty();
            }
            if (tempDockedInsetRect != null) {
                this.mTempDockedInsetRect.set(tempDockedInsetRect);
            } else {
                this.mTempDockedInsetRect.setEmpty();
            }
            if (tempOtherTaskRect != null) {
                this.mTempOtherTaskRect.set(tempOtherTaskRect);
            } else {
                this.mTempOtherTaskRect.setEmpty();
            }
            if (tempOtherInsetRect != null) {
                this.mTempOtherInsetRect.set(tempOtherInsetRect);
            } else {
                this.mTempOtherInsetRect.setEmpty();
            }
        }
        this.mExecutor.execute(this.mResizeRunnable);
    }

    public void dismissDockedStack() {
        this.mExecutor.execute(this.mDismissRunnable);
    }

    public void maximizeDockedStack() {
        this.mExecutor.execute(this.mMaximizeRunnable);
    }

    public void setResizing(final boolean resizing) {
        this.mExecutor.execute(new Runnable() { // from class: com.android.systemui.stackdivider.WindowManagerProxy.6
            @Override // java.lang.Runnable
            public void run() {
                try {
                    ActivityTaskManager.getService().setSplitScreenResizing(resizing);
                } catch (RemoteException e) {
                    Log.w(WindowManagerProxy.TAG, "Error calling setDockedStackResizing: " + e);
                }
            }
        });
    }

    public int getDockSide() {
        try {
            return WindowManagerGlobal.getWindowManagerService().getDockedStackSide();
        } catch (RemoteException e) {
            Log.w(TAG, "Failed to get dock side: " + e);
            return -1;
        }
    }

    public void setResizeDimLayer(boolean visible, int targetWindowingMode, float alpha) {
        this.mDimLayerVisible = visible;
        this.mDimLayerTargetWindowingMode = targetWindowingMode;
        this.mDimLayerAlpha = alpha;
        this.mExecutor.execute(this.mDimLayerRunnable);
    }

    public void setTouchRegion(Rect region) {
        synchronized (this.mDockedRect) {
            this.mTouchableRegion.set(region);
        }
        this.mExecutor.execute(this.mSetTouchableRegionRunnable);
    }
}
