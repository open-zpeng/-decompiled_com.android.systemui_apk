package com.android.systemui.dock;

import com.android.systemui.dock.DockManager;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class DockManagerImpl implements DockManager {
    @Override // com.android.systemui.dock.DockManager
    public void addListener(DockManager.DockEventListener callback) {
    }

    @Override // com.android.systemui.dock.DockManager
    public void removeListener(DockManager.DockEventListener callback) {
    }

    @Override // com.android.systemui.dock.DockManager
    public void addAlignmentStateListener(DockManager.AlignmentStateListener listener) {
    }

    @Override // com.android.systemui.dock.DockManager
    public void removeAlignmentStateListener(DockManager.AlignmentStateListener listener) {
    }

    @Override // com.android.systemui.dock.DockManager
    public boolean isDocked() {
        return false;
    }
}
