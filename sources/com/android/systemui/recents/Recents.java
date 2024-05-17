package com.android.systemui.recents;

import android.content.ContentResolver;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.provider.Settings;
import com.android.systemui.R;
import com.android.systemui.SystemUI;
import com.android.systemui.statusbar.CommandQueue;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes21.dex */
public class Recents extends SystemUI implements CommandQueue.Callbacks {
    private RecentsImplementation mImpl;

    @Override // com.android.systemui.SystemUI
    public void start() {
        ((CommandQueue) getComponent(CommandQueue.class)).addCallback((CommandQueue.Callbacks) this);
        putComponent(Recents.class, this);
        this.mImpl = createRecentsImplementationFromConfig();
        this.mImpl.onStart(this.mContext, this);
    }

    @Override // com.android.systemui.SystemUI
    public void onBootCompleted() {
        this.mImpl.onBootCompleted();
    }

    @Override // com.android.systemui.SystemUI
    public void onConfigurationChanged(Configuration newConfig) {
        this.mImpl.onConfigurationChanged(newConfig);
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void appTransitionFinished(int displayId) {
        if (this.mContext.getDisplayId() == displayId) {
            this.mImpl.onAppTransitionFinished();
        }
    }

    public void growRecents() {
        this.mImpl.growRecents();
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void showRecentApps(boolean triggeredFromAltTab) {
        if (!isUserSetup()) {
            return;
        }
        this.mImpl.showRecentApps(triggeredFromAltTab);
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void hideRecentApps(boolean triggeredFromAltTab, boolean triggeredFromHomeKey) {
        if (!isUserSetup()) {
            return;
        }
        this.mImpl.hideRecentApps(triggeredFromAltTab, triggeredFromHomeKey);
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void toggleRecentApps() {
        if (!isUserSetup()) {
            return;
        }
        this.mImpl.toggleRecentApps();
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void preloadRecentApps() {
        if (!isUserSetup()) {
            return;
        }
        this.mImpl.preloadRecentApps();
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void cancelPreloadRecentApps() {
        if (!isUserSetup()) {
            return;
        }
        this.mImpl.cancelPreloadRecentApps();
    }

    public boolean splitPrimaryTask(int stackCreateMode, Rect initialBounds, int metricsDockAction) {
        if (!isUserSetup()) {
            return false;
        }
        return this.mImpl.splitPrimaryTask(stackCreateMode, initialBounds, metricsDockAction);
    }

    private boolean isUserSetup() {
        ContentResolver cr = this.mContext.getContentResolver();
        return (Settings.Global.getInt(cr, "device_provisioned", 0) == 0 || Settings.Secure.getInt(cr, "user_setup_complete", 0) == 0) ? false : true;
    }

    private RecentsImplementation createRecentsImplementationFromConfig() {
        String clsName = this.mContext.getString(R.string.config_recentsComponent);
        if (clsName == null || clsName.length() == 0) {
            throw new RuntimeException("No recents component configured", null);
        }
        try {
            Class<?> cls = this.mContext.getClassLoader().loadClass(clsName);
            try {
                RecentsImplementation impl = (RecentsImplementation) cls.newInstance();
                return impl;
            } catch (Throwable t) {
                throw new RuntimeException("Error creating recents component: " + clsName, t);
            }
        } catch (Throwable t2) {
            throw new RuntimeException("Error loading recents component: " + clsName, t2);
        }
    }

    @Override // com.android.systemui.SystemUI
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        this.mImpl.dump(pw);
    }
}
