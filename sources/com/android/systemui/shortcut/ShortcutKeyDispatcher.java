package com.android.systemui.shortcut;

import android.os.RemoteException;
import android.util.Log;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;
import com.android.internal.policy.DividerSnapAlgorithm;
import com.android.systemui.SystemUI;
import com.android.systemui.recents.Recents;
import com.android.systemui.shortcut.ShortcutKeyServiceProxy;
import com.android.systemui.stackdivider.Divider;
import com.android.systemui.stackdivider.DividerView;
/* loaded from: classes21.dex */
public class ShortcutKeyDispatcher extends SystemUI implements ShortcutKeyServiceProxy.Callbacks {
    private static final String TAG = "ShortcutKeyDispatcher";
    private ShortcutKeyServiceProxy mShortcutKeyServiceProxy = new ShortcutKeyServiceProxy(this);
    private IWindowManager mWindowManagerService = WindowManagerGlobal.getWindowManagerService();
    protected final long META_MASK = 281474976710656L;
    protected final long ALT_MASK = 8589934592L;
    protected final long CTRL_MASK = 17592186044416L;
    protected final long SHIFT_MASK = 4294967296L;
    protected final long SC_DOCK_LEFT = 281474976710727L;
    protected final long SC_DOCK_RIGHT = 281474976710728L;

    public void registerShortcutKey(long shortcutCode) {
        try {
            this.mWindowManagerService.registerShortcutKey(shortcutCode, this.mShortcutKeyServiceProxy);
        } catch (RemoteException e) {
        }
    }

    @Override // com.android.systemui.shortcut.ShortcutKeyServiceProxy.Callbacks
    public void onShortcutKeyPressed(long shortcutCode) {
        int orientation = this.mContext.getResources().getConfiguration().orientation;
        if ((shortcutCode == 281474976710727L || shortcutCode == 281474976710728L) && orientation == 2) {
            handleDockKey(shortcutCode);
        }
    }

    @Override // com.android.systemui.SystemUI
    public void start() {
        registerShortcutKey(281474976710727L);
        registerShortcutKey(281474976710728L);
    }

    private void handleDockKey(long shortcutCode) {
        DividerSnapAlgorithm.SnapTarget target;
        try {
            int dockSide = this.mWindowManagerService.getDockedStackSide();
            int i = 0;
            if (dockSide == -1) {
                Recents recents = (Recents) getComponent(Recents.class);
                if (shortcutCode != 281474976710727L) {
                    i = 1;
                }
                recents.splitPrimaryTask(i, null, -1);
                return;
            }
            DividerView dividerView = ((Divider) getComponent(Divider.class)).getView();
            DividerSnapAlgorithm snapAlgorithm = dividerView.getSnapAlgorithm();
            int dividerPosition = dividerView.getCurrentPosition();
            DividerSnapAlgorithm.SnapTarget currentTarget = snapAlgorithm.calculateNonDismissingSnapTarget(dividerPosition);
            if (shortcutCode == 281474976710727L) {
                target = snapAlgorithm.getPreviousTarget(currentTarget);
            } else {
                target = snapAlgorithm.getNextTarget(currentTarget);
            }
            dividerView.startDragging(true, false);
            dividerView.stopDragging(target.position, 0.0f, false, true);
        } catch (RemoteException e) {
            Log.e(TAG, "handleDockKey() failed.");
        }
    }
}
