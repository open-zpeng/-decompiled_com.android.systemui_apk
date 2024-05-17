package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.view.accessibility.AccessibilityManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class AccessibilityController implements AccessibilityManager.AccessibilityStateChangeListener, AccessibilityManager.TouchExplorationStateChangeListener {
    private boolean mAccessibilityEnabled;
    private final ArrayList<AccessibilityStateChangedCallback> mChangeCallbacks = new ArrayList<>();
    private boolean mTouchExplorationEnabled;

    /* loaded from: classes21.dex */
    public interface AccessibilityStateChangedCallback {
        void onStateChanged(boolean z, boolean z2);
    }

    @Inject
    public AccessibilityController(Context context) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService("accessibility");
        am.addTouchExplorationStateChangeListener(this);
        am.addAccessibilityStateChangeListener(this);
        this.mAccessibilityEnabled = am.isEnabled();
        this.mTouchExplorationEnabled = am.isTouchExplorationEnabled();
    }

    public boolean isAccessibilityEnabled() {
        return this.mAccessibilityEnabled;
    }

    public boolean isTouchExplorationEnabled() {
        return this.mTouchExplorationEnabled;
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("AccessibilityController state:");
        pw.print("  mAccessibilityEnabled=");
        pw.println(this.mAccessibilityEnabled);
        pw.print("  mTouchExplorationEnabled=");
        pw.println(this.mTouchExplorationEnabled);
    }

    public void addStateChangedCallback(AccessibilityStateChangedCallback cb) {
        this.mChangeCallbacks.add(cb);
        cb.onStateChanged(this.mAccessibilityEnabled, this.mTouchExplorationEnabled);
    }

    public void removeStateChangedCallback(AccessibilityStateChangedCallback cb) {
        this.mChangeCallbacks.remove(cb);
    }

    private void fireChanged() {
        int N = this.mChangeCallbacks.size();
        for (int i = 0; i < N; i++) {
            this.mChangeCallbacks.get(i).onStateChanged(this.mAccessibilityEnabled, this.mTouchExplorationEnabled);
        }
    }

    @Override // android.view.accessibility.AccessibilityManager.AccessibilityStateChangeListener
    public void onAccessibilityStateChanged(boolean enabled) {
        this.mAccessibilityEnabled = enabled;
        fireChanged();
    }

    @Override // android.view.accessibility.AccessibilityManager.TouchExplorationStateChangeListener
    public void onTouchExplorationStateChanged(boolean enabled) {
        this.mTouchExplorationEnabled = enabled;
        fireChanged();
    }
}
