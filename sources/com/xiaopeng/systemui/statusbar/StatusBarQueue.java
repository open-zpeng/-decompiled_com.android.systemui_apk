package com.xiaopeng.systemui.statusbar;

import android.graphics.Rect;
import android.os.RemoteException;
import android.os.ServiceManager;
import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.SystemUIApplication;
import com.xiaopeng.systemui.statusbar.CommandQueue;
import java.util.ArrayList;
/* loaded from: classes24.dex */
public class StatusBarQueue {
    private CommandQueue mCommandQueue = new CommandQueue(SystemUIApplication.getContext());
    protected IStatusBarService mBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));

    public void init() {
        try {
            int[] iArr = new int[9];
            new ArrayList();
            new ArrayList();
            new ArrayList();
            new Rect();
            new Rect();
            this.mBarService.registerStatusBar(this.mCommandQueue);
        } catch (RemoteException e) {
        }
    }

    public void addCallbacks(CommandQueue.Callbacks callbacks) {
        this.mCommandQueue.addCallback(callbacks);
    }

    public void removeCallbacks(CommandQueue.Callbacks callbacks) {
        this.mCommandQueue.removeCallback(callbacks);
    }
}
