package com.android.systemui;

import android.content.res.Configuration;
import android.util.Log;
import com.android.systemui.statusbar.phone.StatusBar;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes21.dex */
public class SystemBars extends SystemUI {
    private static final boolean DEBUG = false;
    private static final String TAG = "SystemBars";
    private static final int WAIT_FOR_BARS_TO_DIE = 500;
    private SystemUI mStatusBar;

    @Override // com.android.systemui.SystemUI
    public void start() {
        createStatusBarFromConfig();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.SystemUI
    public void onBootCompleted() {
        super.onBootCompleted();
        SystemUI systemUI = this.mStatusBar;
        if (systemUI != null) {
            systemUI.onBootCompleted();
        }
    }

    @Override // com.android.systemui.SystemUI
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        SystemUI systemUI = this.mStatusBar;
        if (systemUI != null) {
            systemUI.dump(fd, pw, args);
        }
    }

    private void createStatusBarFromConfig() {
        String clsName = this.mContext.getString(R.string.config_carStatusBarComponent);
        if (clsName == null || clsName.length() == 0) {
            throw andLog("No status bar component configured", null);
        }
        try {
            Class<?> cls = this.mContext.getClassLoader().loadClass(clsName);
            try {
                this.mStatusBar = (SystemUI) cls.newInstance();
                this.mStatusBar.mContext = this.mContext;
                this.mStatusBar.mComponents = this.mComponents;
                if (this.mStatusBar instanceof StatusBar) {
                    SystemUIFactory.getInstance().getRootComponent().getStatusBarInjector().createStatusBar((StatusBar) this.mStatusBar);
                }
                this.mStatusBar.start();
            } catch (Throwable t) {
                throw andLog("Error creating status bar component: " + clsName, t);
            }
        } catch (Throwable t2) {
            throw andLog("Error loading status bar component: " + clsName, t2);
        }
    }

    private RuntimeException andLog(String msg, Throwable t) {
        Log.w(TAG, msg, t);
        throw new RuntimeException(msg, t);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.SystemUI
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        SystemUI systemUI = this.mStatusBar;
        if (systemUI != null) {
            systemUI.onConfigurationChanged(newConfig);
        }
    }
}
