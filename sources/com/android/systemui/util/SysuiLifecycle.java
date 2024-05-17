package com.android.systemui.util;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
/* loaded from: classes21.dex */
public class SysuiLifecycle {
    private SysuiLifecycle() {
    }

    public static LifecycleOwner viewAttachLifecycle(View v) {
        return new ViewLifecycle(v);
    }

    /* loaded from: classes21.dex */
    private static class ViewLifecycle implements LifecycleOwner, View.OnAttachStateChangeListener {
        private final LifecycleRegistry mLifecycle = new LifecycleRegistry(this);

        ViewLifecycle(View v) {
            v.addOnAttachStateChangeListener(this);
        }

        @Override // androidx.lifecycle.LifecycleOwner
        @NonNull
        public Lifecycle getLifecycle() {
            return this.mLifecycle;
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View v) {
            this.mLifecycle.markState(Lifecycle.State.RESUMED);
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View v) {
            this.mLifecycle.markState(Lifecycle.State.DESTROYED);
        }
    }
}
