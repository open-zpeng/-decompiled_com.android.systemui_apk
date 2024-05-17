package com.android.systemui.statusbar.policy;

import android.content.Context;
import com.android.internal.view.RotationPolicy;
import com.android.systemui.statusbar.policy.RotationLockController;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public final class RotationLockControllerImpl implements RotationLockController {
    private final Context mContext;
    private final CopyOnWriteArrayList<RotationLockController.RotationLockControllerCallback> mCallbacks = new CopyOnWriteArrayList<>();
    private final RotationPolicy.RotationPolicyListener mRotationPolicyListener = new RotationPolicy.RotationPolicyListener() { // from class: com.android.systemui.statusbar.policy.RotationLockControllerImpl.1
        public void onChange() {
            RotationLockControllerImpl.this.notifyChanged();
        }
    };

    @Inject
    public RotationLockControllerImpl(Context context) {
        this.mContext = context;
        setListening(true);
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void addCallback(RotationLockController.RotationLockControllerCallback callback) {
        this.mCallbacks.add(callback);
        notifyChanged(callback);
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void removeCallback(RotationLockController.RotationLockControllerCallback callback) {
        this.mCallbacks.remove(callback);
    }

    @Override // com.android.systemui.statusbar.policy.RotationLockController
    public int getRotationLockOrientation() {
        return RotationPolicy.getRotationLockOrientation(this.mContext);
    }

    @Override // com.android.systemui.statusbar.policy.RotationLockController
    public boolean isRotationLocked() {
        return RotationPolicy.isRotationLocked(this.mContext);
    }

    @Override // com.android.systemui.statusbar.policy.RotationLockController
    public void setRotationLocked(boolean locked) {
        RotationPolicy.setRotationLock(this.mContext, locked);
    }

    @Override // com.android.systemui.statusbar.policy.RotationLockController
    public void setRotationLockedAtAngle(boolean locked, int rotation) {
        RotationPolicy.setRotationLockAtAngle(this.mContext, locked, rotation);
    }

    @Override // com.android.systemui.statusbar.policy.RotationLockController
    public boolean isRotationLockAffordanceVisible() {
        return RotationPolicy.isRotationLockToggleVisible(this.mContext);
    }

    @Override // com.android.systemui.statusbar.policy.Listenable
    public void setListening(boolean listening) {
        if (listening) {
            RotationPolicy.registerRotationPolicyListener(this.mContext, this.mRotationPolicyListener, -1);
        } else {
            RotationPolicy.unregisterRotationPolicyListener(this.mContext, this.mRotationPolicyListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyChanged() {
        Iterator<RotationLockController.RotationLockControllerCallback> it = this.mCallbacks.iterator();
        while (it.hasNext()) {
            RotationLockController.RotationLockControllerCallback callback = it.next();
            notifyChanged(callback);
        }
    }

    private void notifyChanged(RotationLockController.RotationLockControllerCallback callback) {
        callback.onRotationLockStateChanged(RotationPolicy.isRotationLocked(this.mContext), RotationPolicy.isRotationLockToggleVisible(this.mContext));
    }
}
