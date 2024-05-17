package com.android.systemui.pip;

import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.UserManager;
import com.android.systemui.SystemUI;
import com.android.systemui.pip.tv.PipManager;
import com.android.systemui.statusbar.CommandQueue;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes21.dex */
public class PipUI extends SystemUI implements CommandQueue.Callbacks {
    private BasePipManager mPipManager;
    private boolean mSupportsPip;

    @Override // com.android.systemui.SystemUI
    public void start() {
        BasePipManager pipManager;
        PackageManager pm = this.mContext.getPackageManager();
        this.mSupportsPip = pm.hasSystemFeature("android.software.picture_in_picture");
        if (!this.mSupportsPip) {
            return;
        }
        int processUser = UserManager.get(this.mContext).getUserHandle();
        if (processUser != 0) {
            throw new IllegalStateException("Non-primary Pip component not currently supported.");
        }
        if (pm.hasSystemFeature("android.software.leanback_only")) {
            pipManager = PipManager.getInstance();
        } else {
            pipManager = com.android.systemui.pip.phone.PipManager.getInstance();
        }
        this.mPipManager = pipManager;
        this.mPipManager.initialize(this.mContext);
        ((CommandQueue) getComponent(CommandQueue.class)).addCallback((CommandQueue.Callbacks) this);
        putComponent(PipUI.class, this);
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void showPictureInPictureMenu() {
        this.mPipManager.showPictureInPictureMenu();
    }

    public void expandPip() {
        this.mPipManager.expandPip();
    }

    public void hidePipMenu(Runnable onStartCallback, Runnable onEndCallback) {
        this.mPipManager.hidePipMenu(onStartCallback, onEndCallback);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.SystemUI
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        BasePipManager basePipManager = this.mPipManager;
        if (basePipManager == null) {
            return;
        }
        basePipManager.onConfigurationChanged(newConfig);
    }

    @Override // com.android.systemui.SystemUI
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        BasePipManager basePipManager = this.mPipManager;
        if (basePipManager == null) {
            return;
        }
        basePipManager.dump(pw);
    }
}
