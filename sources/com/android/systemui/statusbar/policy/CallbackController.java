package com.android.systemui.statusbar.policy;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
/* loaded from: classes21.dex */
public interface CallbackController<T> {
    void addCallback(T t);

    void removeCallback(T t);

    default T observe(LifecycleOwner owner, T listener) {
        return observe(owner.getLifecycle(), (Lifecycle) listener);
    }

    default T observe(Lifecycle lifecycle, final T listener) {
        lifecycle.addObserver(new LifecycleEventObserver() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$CallbackController$TlIH8GpCbmJQdNzMgf9ko_xLlUk
            @Override // androidx.lifecycle.LifecycleEventObserver
            public final void onStateChanged(LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
                CallbackController.lambda$observe$0(CallbackController.this, listener, lifecycleOwner, event);
            }
        });
        return listener;
    }

    static /* synthetic */ void lambda$observe$0(CallbackController _this, Object listener, LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_RESUME) {
            _this.addCallback(listener);
        } else if (event == Lifecycle.Event.ON_PAUSE) {
            _this.removeCallback(listener);
        }
    }
}
