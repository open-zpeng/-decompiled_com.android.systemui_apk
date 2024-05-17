package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class AccessibilityManagerWrapper implements CallbackController<AccessibilityManager.AccessibilityServicesStateChangeListener> {
    private final AccessibilityManager mAccessibilityManager;

    @Inject
    public AccessibilityManagerWrapper(Context context) {
        this.mAccessibilityManager = (AccessibilityManager) context.getSystemService(AccessibilityManager.class);
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void addCallback(AccessibilityManager.AccessibilityServicesStateChangeListener listener) {
        this.mAccessibilityManager.addAccessibilityServicesStateChangeListener(listener, null);
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void removeCallback(AccessibilityManager.AccessibilityServicesStateChangeListener listener) {
        this.mAccessibilityManager.removeAccessibilityServicesStateChangeListener(listener);
    }

    public void addAccessibilityStateChangeListener(AccessibilityManager.AccessibilityStateChangeListener listener) {
        this.mAccessibilityManager.addAccessibilityStateChangeListener(listener);
    }

    public void removeAccessibilityStateChangeListener(AccessibilityManager.AccessibilityStateChangeListener listener) {
        this.mAccessibilityManager.removeAccessibilityStateChangeListener(listener);
    }

    public boolean isEnabled() {
        return this.mAccessibilityManager.isEnabled();
    }

    public void sendAccessibilityEvent(AccessibilityEvent event) {
        this.mAccessibilityManager.sendAccessibilityEvent(event);
    }

    public int getRecommendedTimeoutMillis(int originalTimeout, int uiContentFlags) {
        return this.mAccessibilityManager.getRecommendedTimeoutMillis(originalTimeout, uiContentFlags);
    }
}
