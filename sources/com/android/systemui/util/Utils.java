package com.android.systemui.util;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.statusbar.CommandQueue;
import java.util.List;
import java.util.function.Consumer;
/* loaded from: classes21.dex */
public class Utils {
    public static <T> void safeForeach(List<T> list, Consumer<T> c) {
        for (int i = list.size() - 1; i >= 0; i--) {
            T item = list.get(i);
            if (item != null) {
                c.accept(item);
            }
        }
    }

    /* loaded from: classes21.dex */
    public static class DisableStateTracker implements CommandQueue.Callbacks, View.OnAttachStateChangeListener {
        private boolean mDisabled;
        private final int mMask1;
        private final int mMask2;
        private View mView;

        public DisableStateTracker(int disableMask, int disable2Mask) {
            this.mMask1 = disableMask;
            this.mMask2 = disable2Mask;
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View v) {
            this.mView = v;
            ((CommandQueue) SysUiServiceProvider.getComponent(v.getContext(), CommandQueue.class)).addCallback((CommandQueue.Callbacks) this);
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View v) {
            ((CommandQueue) SysUiServiceProvider.getComponent(this.mView.getContext(), CommandQueue.class)).removeCallback((CommandQueue.Callbacks) this);
            this.mView = null;
        }

        @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
        public void disable(int displayId, int state1, int state2, boolean animate) {
            if (displayId != this.mView.getDisplay().getDisplayId()) {
                return;
            }
            boolean disabled = ((this.mMask1 & state1) == 0 && (this.mMask2 & state2) == 0) ? false : true;
            if (disabled == this.mDisabled) {
                return;
            }
            this.mDisabled = disabled;
            this.mView.setVisibility(disabled ? 8 : 0);
        }

        public boolean isDisabled() {
            return this.mDisabled;
        }
    }

    public static boolean isHeadlessRemoteDisplayProvider(PackageManager pm, String packageName) {
        if (pm.checkPermission("android.permission.REMOTE_DISPLAY_PROVIDER", packageName) != 0) {
            return false;
        }
        Intent homeIntent = new Intent("android.intent.action.MAIN");
        homeIntent.addCategory("android.intent.category.LAUNCHER");
        homeIntent.setPackage(packageName);
        return pm.queryIntentActivities(homeIntent, 0).isEmpty();
    }
}
