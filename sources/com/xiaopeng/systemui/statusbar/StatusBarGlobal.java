package com.xiaopeng.systemui.statusbar;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.SystemProperties;
import android.view.WindowManager;
import com.xiaopeng.systemui.controller.AccountController;
import com.xiaopeng.systemui.controller.CarController;
import com.xiaopeng.systemui.controller.OsdController;
import com.xiaopeng.systemui.controller.UsbController;
import com.xiaopeng.systemui.controller.brightness.BrightnessManager;
import com.xiaopeng.systemui.controller.screensaver.ScreensaverManager;
import com.xiaopeng.systemui.helper.WindowHelper;
import com.xiaopeng.systemui.navigationbar.INavigation;
import com.xiaopeng.systemui.statusbar.CommandQueue;
/* loaded from: classes24.dex */
public final class StatusBarGlobal {
    public static final int KEYCODE_NAVIGATION_HIDE = 2002;
    public static final int KEYCODE_NAVIGATION_SHOW = 2001;
    private static final String TAG = "StatusBarGlobal";
    private static StatusBarGlobal sStatusBarGlobal;
    private boolean mBootCompleted;
    private Context mContext;
    private INavigation mNavigation;
    private StatusBarQueue mStatusBarQueue;
    private WindowManager mWindowManager;
    private Rect mStatusBarRect = null;
    private Rect mNavigationBarRect = null;
    private boolean mClickNetworkButtonToShowNoDataPage = false;
    private CommandQueue.Callbacks mCommandCallbacks = new CommandQueue.Callbacks() { // from class: com.xiaopeng.systemui.statusbar.StatusBarGlobal.1
        public void setSystemUiVisibility(int vis, int fullscreenStackVis, int dockedStackVis, int mask, Rect fullscreenStackBounds, Rect dockedStackBounds) {
        }

        @Override // com.xiaopeng.systemui.statusbar.CommandQueue.Callbacks
        public void handleSystemKey(int keycode) {
            StatusBarGlobal.this.handleNavigationVisibility(keycode);
        }
    };

    private StatusBarGlobal(Context context) {
        this.mContext = context;
    }

    public void initialize() {
        this.mWindowManager = (WindowManager) this.mContext.getSystemService("window");
        this.mBootCompleted = "1".equals(SystemProperties.get("sys.boot_completed"));
        initStatusBarCallback();
        UsbController.getInstance(this.mContext).init();
        AccountController.getInstance(this.mContext).init();
        BrightnessManager.get(this.mContext).init();
        ScreensaverManager.get(this.mContext).init();
    }

    public static StatusBarGlobal getInstance(Context context) {
        StatusBarGlobal statusBarGlobal;
        synchronized (StatusBarGlobal.class) {
            if (sStatusBarGlobal == null) {
                sStatusBarGlobal = new StatusBarGlobal(context);
            }
            statusBarGlobal = sStatusBarGlobal;
        }
        return statusBarGlobal;
    }

    public WindowManager getWindowManager() {
        return this.mWindowManager;
    }

    public void initStatusBarCallback() {
        this.mStatusBarQueue = new StatusBarQueue();
        this.mStatusBarQueue.init();
        this.mStatusBarQueue.addCallbacks(this.mCommandCallbacks);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        CarController.getInstance(this.mContext).onConfigurationChanged(newConfig);
        OsdController.getInstance(this.mContext).onConfigurationChanged(newConfig);
        BrightnessManager.get(this.mContext).onConfigurationChanged(newConfig);
    }

    public void onBootCompleted() {
        this.mBootCompleted = true;
    }

    public void setNavigation(INavigation navigation) {
        this.mNavigation = navigation;
    }

    public Rect getStatusBarRect() {
        if (this.mStatusBarRect == null) {
            this.mStatusBarRect = WindowHelper.getStatusBarRect(this.mContext);
        }
        return this.mStatusBarRect;
    }

    public Rect getNavigationBarRect() {
        if (this.mNavigationBarRect == null) {
            this.mNavigationBarRect = WindowHelper.getNavigationBarRect(this.mContext);
        }
        return this.mNavigationBarRect;
    }

    public boolean isBootCompleted() {
        return this.mBootCompleted;
    }

    public void setClickNetworkButtonToShowOutOfDataPage(boolean b) {
        this.mClickNetworkButtonToShowNoDataPage = b;
    }

    public boolean clickNetworkButtonToShowOutOfDataPage() {
        return this.mClickNetworkButtonToShowNoDataPage;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleNavigationVisibility(int keycode) {
        INavigation iNavigation = this.mNavigation;
        if (iNavigation != null && keycode > 2000) {
            if (keycode == 2001) {
                iNavigation.showNavigationBar();
            } else if (keycode == 2002) {
                iNavigation.hideNavigationBar();
            }
        }
    }

    /* loaded from: classes24.dex */
    public static class StatusBarUser {
        public boolean userLogin = false;
        public boolean userActive = false;
        public boolean userVisible = false;
        public boolean userWelcome = false;
        public boolean userActivated = false;
        public boolean userDoorClosed = true;
        public boolean userDoorChanged = false;
        public boolean userActiveChanged = false;

        public void setUserLogin(boolean login) {
            this.userLogin = login;
        }

        public void setUserActive(boolean active) {
            this.userActive = active;
            this.userActivated = active;
        }

        public void setUserVisible(boolean visible) {
            this.userVisible = visible;
        }

        public void setUserWelcome(boolean welcome) {
            this.userWelcome = welcome;
        }

        public void setUserDoorClosed(boolean closed) {
            this.userDoorClosed = closed;
        }

        public void setUserDoorChanged(boolean changed) {
            this.userDoorChanged = changed;
        }

        public void setUserActiveChanged(boolean changed) {
            this.userActiveChanged = changed;
        }

        public static StatusBarUser clone(StatusBarUser user) {
            StatusBarUser sbu = new StatusBarUser();
            if (user != null) {
                sbu.userLogin = user.userLogin;
                sbu.userActive = user.userActive;
                sbu.userVisible = user.userVisible;
                sbu.userWelcome = user.userWelcome;
                sbu.userActivated = user.userActivated;
                sbu.userDoorClosed = user.userDoorClosed;
                sbu.userDoorChanged = user.userDoorChanged;
                sbu.userActiveChanged = user.userActiveChanged;
            }
            return sbu;
        }

        public String toString() {
            StringBuffer buffer = new StringBuffer("");
            buffer.append("StatusBarUser");
            buffer.append(" userLogin=" + this.userLogin);
            buffer.append(" userActive=" + this.userActive);
            buffer.append(" userVisible=" + this.userVisible);
            buffer.append(" userWelcome=" + this.userWelcome);
            buffer.append(" userActivated=" + this.userActivated);
            buffer.append(" userDoorClosed=" + this.userDoorClosed);
            buffer.append(" userDoorChanged=" + this.userDoorChanged);
            buffer.append(" userActiveChanged=" + this.userActiveChanged);
            return buffer.toString();
        }
    }
}
