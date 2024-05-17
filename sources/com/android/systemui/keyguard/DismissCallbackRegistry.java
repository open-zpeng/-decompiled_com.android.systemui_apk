package com.android.systemui.keyguard;

import com.android.internal.policy.IKeyguardDismissCallback;
import com.android.systemui.Dependency;
import com.android.systemui.UiOffloadThread;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes21.dex */
public class DismissCallbackRegistry {
    private final ArrayList<DismissCallbackWrapper> mDismissCallbacks = new ArrayList<>();
    private final UiOffloadThread mUiOffloadThread = (UiOffloadThread) Dependency.get(UiOffloadThread.class);

    public void addCallback(IKeyguardDismissCallback callback) {
        this.mDismissCallbacks.add(new DismissCallbackWrapper(callback));
    }

    public void notifyDismissCancelled() {
        for (int i = this.mDismissCallbacks.size() - 1; i >= 0; i--) {
            final DismissCallbackWrapper callback = this.mDismissCallbacks.get(i);
            UiOffloadThread uiOffloadThread = this.mUiOffloadThread;
            Objects.requireNonNull(callback);
            uiOffloadThread.submit(new Runnable() { // from class: com.android.systemui.keyguard.-$$Lambda$zM6bayhThdtgvBghgFXo519LeO0
                @Override // java.lang.Runnable
                public final void run() {
                    DismissCallbackWrapper.this.notifyDismissCancelled();
                }
            });
        }
        this.mDismissCallbacks.clear();
    }

    public void notifyDismissSucceeded() {
        for (int i = this.mDismissCallbacks.size() - 1; i >= 0; i--) {
            final DismissCallbackWrapper callback = this.mDismissCallbacks.get(i);
            UiOffloadThread uiOffloadThread = this.mUiOffloadThread;
            Objects.requireNonNull(callback);
            uiOffloadThread.submit(new Runnable() { // from class: com.android.systemui.keyguard.-$$Lambda$2j_lq_QeR0jp4UUzPHOB_8BlctI
                @Override // java.lang.Runnable
                public final void run() {
                    DismissCallbackWrapper.this.notifyDismissSucceeded();
                }
            });
        }
        this.mDismissCallbacks.clear();
    }
}
