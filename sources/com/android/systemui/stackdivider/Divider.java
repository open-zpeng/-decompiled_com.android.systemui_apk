package com.android.systemui.stackdivider;

import android.content.res.Configuration;
import android.os.RemoteException;
import android.util.Log;
import android.view.IDockedStackListener;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManagerGlobal;
import com.android.systemui.R;
import com.android.systemui.SystemUI;
import com.android.systemui.recents.Recents;
import com.android.systemui.stackdivider.Divider;
import com.android.systemui.stackdivider.DividerView;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes21.dex */
public class Divider extends SystemUI implements DividerView.DividerCallbacks {
    private static final String TAG = "Divider";
    private DockDividerVisibilityListener mDockDividerVisibilityListener;
    private ForcedResizableInfoActivityController mForcedResizableController;
    private DividerView mView;
    private DividerWindowManager mWindowManager;
    private final DividerState mDividerState = new DividerState();
    private boolean mVisible = false;
    private boolean mMinimized = false;
    private boolean mAdjustedForIme = false;
    private boolean mHomeStackResizable = false;

    @Override // com.android.systemui.SystemUI
    public void start() {
        this.mWindowManager = new DividerWindowManager(this.mContext);
        update(this.mContext.getResources().getConfiguration());
        putComponent(Divider.class, this);
        this.mDockDividerVisibilityListener = new DockDividerVisibilityListener();
        try {
            WindowManagerGlobal.getWindowManagerService().registerDockedStackListener(this.mDockDividerVisibilityListener);
        } catch (Exception e) {
            Log.e(TAG, "Failed to register docked stack listener", e);
        }
        this.mForcedResizableController = new ForcedResizableInfoActivityController(this.mContext);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.SystemUI
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        update(newConfig);
    }

    public DividerView getView() {
        return this.mView;
    }

    public boolean isMinimized() {
        return this.mMinimized;
    }

    public boolean isHomeStackResizable() {
        return this.mHomeStackResizable;
    }

    private void addDivider(Configuration configuration) {
        this.mView = (DividerView) LayoutInflater.from(this.mContext).inflate(R.layout.docked_stack_divider, (ViewGroup) null);
        this.mView.injectDependencies(this.mWindowManager, this.mDividerState, this);
        this.mView.setVisibility(this.mVisible ? 0 : 4);
        this.mView.setMinimizedDockStack(this.mMinimized, this.mHomeStackResizable);
        int size = this.mContext.getResources().getDimensionPixelSize(17105146);
        boolean landscape = configuration.orientation == 2;
        int width = landscape ? size : -1;
        int height = landscape ? -1 : size;
        this.mWindowManager.add(this.mView, width, height);
    }

    private void removeDivider() {
        DividerView dividerView = this.mView;
        if (dividerView != null) {
            dividerView.onDividerRemoved();
        }
        this.mWindowManager.remove();
    }

    private void update(Configuration configuration) {
        removeDivider();
        addDivider(configuration);
        if (this.mMinimized) {
            this.mView.setMinimizedDockStack(true, this.mHomeStackResizable);
            updateTouchable();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateVisibility(final boolean visible) {
        this.mView.post(new Runnable() { // from class: com.android.systemui.stackdivider.Divider.1
            @Override // java.lang.Runnable
            public void run() {
                boolean z = Divider.this.mVisible;
                boolean z2 = visible;
                if (z != z2) {
                    Divider.this.mVisible = z2;
                    Divider.this.mView.setVisibility(visible ? 0 : 4);
                    Divider.this.mView.setMinimizedDockStack(Divider.this.mMinimized, Divider.this.mHomeStackResizable);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMinimizedDockedStack(final boolean minimized, final long animDuration, final boolean isHomeStackResizable) {
        this.mView.post(new Runnable() { // from class: com.android.systemui.stackdivider.Divider.2
            @Override // java.lang.Runnable
            public void run() {
                Divider.this.mHomeStackResizable = isHomeStackResizable;
                boolean z = Divider.this.mMinimized;
                boolean z2 = minimized;
                if (z != z2) {
                    Divider.this.mMinimized = z2;
                    Divider.this.updateTouchable();
                    if (animDuration > 0) {
                        Divider.this.mView.setMinimizedDockStack(minimized, animDuration, isHomeStackResizable);
                    } else {
                        Divider.this.mView.setMinimizedDockStack(minimized, isHomeStackResizable);
                    }
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyDockedStackExistsChanged(final boolean exists) {
        this.mView.post(new Runnable() { // from class: com.android.systemui.stackdivider.Divider.3
            @Override // java.lang.Runnable
            public void run() {
                Divider.this.mForcedResizableController.notifyDockedStackExistsChanged(exists);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTouchable() {
        this.mWindowManager.setTouchable((this.mHomeStackResizable || !this.mMinimized) && !this.mAdjustedForIme);
    }

    public void onRecentsActivityStarting() {
        DividerView dividerView = this.mView;
        if (dividerView != null) {
            dividerView.onRecentsActivityStarting();
        }
    }

    public void onRecentsDrawn() {
        DividerView dividerView = this.mView;
        if (dividerView != null) {
            dividerView.onRecentsDrawn();
        }
    }

    public void onUndockingTask() {
        DividerView dividerView = this.mView;
        if (dividerView != null) {
            dividerView.onUndockingTask();
        }
    }

    public void onDockedFirstAnimationFrame() {
        DividerView dividerView = this.mView;
        if (dividerView != null) {
            dividerView.onDockedFirstAnimationFrame();
        }
    }

    public void onDockedTopTask() {
        DividerView dividerView = this.mView;
        if (dividerView != null) {
            dividerView.onDockedTopTask();
        }
    }

    public void onAppTransitionFinished() {
        this.mForcedResizableController.onAppTransitionFinished();
    }

    @Override // com.android.systemui.stackdivider.DividerView.DividerCallbacks
    public void onDraggingStart() {
        this.mForcedResizableController.onDraggingStart();
    }

    @Override // com.android.systemui.stackdivider.DividerView.DividerCallbacks
    public void onDraggingEnd() {
        this.mForcedResizableController.onDraggingEnd();
    }

    @Override // com.android.systemui.stackdivider.DividerView.DividerCallbacks
    public void growRecents() {
        Recents recents = (Recents) getComponent(Recents.class);
        if (recents != null) {
            recents.growRecents();
        }
    }

    @Override // com.android.systemui.SystemUI
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.print("  mVisible=");
        pw.println(this.mVisible);
        pw.print("  mMinimized=");
        pw.println(this.mMinimized);
        pw.print("  mAdjustedForIme=");
        pw.println(this.mAdjustedForIme);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes21.dex */
    public class DockDividerVisibilityListener extends IDockedStackListener.Stub {
        DockDividerVisibilityListener() {
        }

        public void onDividerVisibilityChanged(boolean visible) throws RemoteException {
            Divider.this.updateVisibility(visible);
        }

        public void onDockedStackExistsChanged(boolean exists) throws RemoteException {
            Divider.this.notifyDockedStackExistsChanged(exists);
        }

        public void onDockedStackMinimizedChanged(boolean minimized, long animDuration, boolean isHomeStackResizable) throws RemoteException {
            Divider.this.mHomeStackResizable = isHomeStackResizable;
            Divider.this.updateMinimizedDockedStack(minimized, animDuration, isHomeStackResizable);
        }

        public void onAdjustedForImeChanged(final boolean adjustedForIme, final long animDuration) throws RemoteException {
            Divider.this.mView.post(new Runnable() { // from class: com.android.systemui.stackdivider.-$$Lambda$Divider$DockDividerVisibilityListener$fZDE4rhC5s3QEgR-7YXeKi_feiY
                @Override // java.lang.Runnable
                public final void run() {
                    Divider.DockDividerVisibilityListener.this.lambda$onAdjustedForImeChanged$0$Divider$DockDividerVisibilityListener(adjustedForIme, animDuration);
                }
            });
        }

        public /* synthetic */ void lambda$onAdjustedForImeChanged$0$Divider$DockDividerVisibilityListener(boolean adjustedForIme, long animDuration) {
            if (Divider.this.mAdjustedForIme != adjustedForIme) {
                Divider.this.mAdjustedForIme = adjustedForIme;
                Divider.this.updateTouchable();
                if (!Divider.this.mMinimized) {
                    if (animDuration > 0) {
                        Divider.this.mView.setAdjustedForIme(adjustedForIme, animDuration);
                    } else {
                        Divider.this.mView.setAdjustedForIme(adjustedForIme);
                    }
                }
            }
        }

        public /* synthetic */ void lambda$onDockSideChanged$1$Divider$DockDividerVisibilityListener(int newDockSide) {
            Divider.this.mView.notifyDockSideChanged(newDockSide);
        }

        public void onDockSideChanged(final int newDockSide) throws RemoteException {
            Divider.this.mView.post(new Runnable() { // from class: com.android.systemui.stackdivider.-$$Lambda$Divider$DockDividerVisibilityListener$cPiHgQdgCDQeKAQTEdGGnGaaM_c
                @Override // java.lang.Runnable
                public final void run() {
                    Divider.DockDividerVisibilityListener.this.lambda$onDockSideChanged$1$Divider$DockDividerVisibilityListener(newDockSide);
                }
            });
        }
    }
}
