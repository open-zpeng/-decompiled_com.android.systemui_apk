package com.android.systemui.statusbar;

import android.app.Fragment;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.IWindowManager;
import android.view.View;
import android.view.WindowManagerGlobal;
import androidx.annotation.Nullable;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.statusbar.RegisterStatusBarResult;
import com.android.systemui.Dependency;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.fragments.FragmentHostManager;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.phone.AutoHideController;
import com.android.systemui.statusbar.phone.LightBarController;
import com.android.systemui.statusbar.phone.NavigationBarFragment;
import com.android.systemui.statusbar.phone.NavigationBarView;
import com.android.systemui.statusbar.policy.BatteryController;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class NavigationBarController implements CommandQueue.Callbacks {
    private static final String TAG = NavigationBarController.class.getSimpleName();
    private final Context mContext;
    private final DisplayManager mDisplayManager;
    private final Handler mHandler;
    @VisibleForTesting
    SparseArray<NavigationBarFragment> mNavigationBars = new SparseArray<>();

    @Inject
    public NavigationBarController(Context context, @Named("main_handler") Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
        this.mDisplayManager = (DisplayManager) this.mContext.getSystemService("display");
        CommandQueue commandQueue = (CommandQueue) SysUiServiceProvider.getComponent(this.mContext, CommandQueue.class);
        if (commandQueue != null) {
            commandQueue.addCallback((CommandQueue.Callbacks) this);
        }
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void onDisplayRemoved(int displayId) {
        removeNavigationBar(displayId);
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void onDisplayReady(int displayId) {
        Display display = this.mDisplayManager.getDisplay(displayId);
        createNavigationBar(display, null);
    }

    public void createNavigationBars(boolean includeDefaultDisplay, RegisterStatusBarResult result) {
        Display[] displays = this.mDisplayManager.getDisplays();
        for (Display display : displays) {
            if (includeDefaultDisplay || display.getDisplayId() != 0) {
                createNavigationBar(display, result);
            }
        }
    }

    @VisibleForTesting
    void createNavigationBar(final Display display, final RegisterStatusBarResult result) {
        Context createDisplayContext;
        if (display == null) {
            return;
        }
        final int displayId = display.getDisplayId();
        final boolean isOnDefaultDisplay = displayId == 0;
        IWindowManager wms = WindowManagerGlobal.getWindowManagerService();
        try {
            if (!wms.hasNavigationBar(displayId)) {
                return;
            }
            if (isOnDefaultDisplay) {
                createDisplayContext = this.mContext;
            } else {
                createDisplayContext = this.mContext.createDisplayContext(display);
            }
            final Context context = createDisplayContext;
            NavigationBarFragment.create(context, new FragmentHostManager.FragmentListener() { // from class: com.android.systemui.statusbar.-$$Lambda$NavigationBarController$oyTONslWMHHQSXiga3Vs0njIek8
                @Override // com.android.systemui.fragments.FragmentHostManager.FragmentListener
                public final void onFragmentViewCreated(String str, Fragment fragment) {
                    NavigationBarController.this.lambda$createNavigationBar$0$NavigationBarController(isOnDefaultDisplay, context, displayId, result, display, str, fragment);
                }
            });
        } catch (RemoteException e) {
            Log.w(TAG, "Cannot get WindowManager.");
        }
    }

    public /* synthetic */ void lambda$createNavigationBar$0$NavigationBarController(boolean isOnDefaultDisplay, Context context, int displayId, RegisterStatusBarResult result, Display display, String tag, Fragment fragment) {
        LightBarController lightBarController;
        AutoHideController autoHideController;
        NavigationBarFragment navBar = (NavigationBarFragment) fragment;
        if (isOnDefaultDisplay) {
            lightBarController = (LightBarController) Dependency.get(LightBarController.class);
        } else {
            lightBarController = new LightBarController(context, (DarkIconDispatcher) Dependency.get(DarkIconDispatcher.class), (BatteryController) Dependency.get(BatteryController.class));
        }
        LightBarController lightBarController2 = lightBarController;
        navBar.setLightBarController(lightBarController2);
        if (isOnDefaultDisplay) {
            autoHideController = (AutoHideController) Dependency.get(AutoHideController.class);
        } else {
            autoHideController = new AutoHideController(context, this.mHandler);
        }
        AutoHideController autoHideController2 = autoHideController;
        navBar.setAutoHideController(autoHideController2);
        navBar.restoreSystemUiVisibilityState();
        this.mNavigationBars.append(displayId, navBar);
        if (result != null) {
            navBar.setImeWindowStatus(display.getDisplayId(), result.mImeToken, result.mImeWindowVis, result.mImeBackDisposition, result.mShowImeSwitcher);
        }
    }

    private void removeNavigationBar(int displayId) {
        NavigationBarFragment navBar = this.mNavigationBars.get(displayId);
        if (navBar != null) {
            View navigationWindow = navBar.getView().getRootView();
            WindowManagerGlobal.getInstance().removeView(navigationWindow, true);
            this.mNavigationBars.remove(displayId);
        }
    }

    public void checkNavBarModes(int displayId) {
        NavigationBarFragment navBar = this.mNavigationBars.get(displayId);
        if (navBar != null) {
            navBar.checkNavBarModes();
        }
    }

    public void finishBarAnimations(int displayId) {
        NavigationBarFragment navBar = this.mNavigationBars.get(displayId);
        if (navBar != null) {
            navBar.finishBarAnimations();
        }
    }

    public void touchAutoDim(int displayId) {
        NavigationBarFragment navBar = this.mNavigationBars.get(displayId);
        if (navBar != null) {
            navBar.touchAutoDim();
        }
    }

    public void transitionTo(int displayId, int barMode, boolean animate) {
        NavigationBarFragment navBar = this.mNavigationBars.get(displayId);
        if (navBar != null) {
            navBar.transitionTo(barMode, animate);
        }
    }

    public void disableAnimationsDuringHide(int displayId, long delay) {
        NavigationBarFragment navBar = this.mNavigationBars.get(displayId);
        if (navBar != null) {
            navBar.disableAnimationsDuringHide(delay);
        }
    }

    @Nullable
    public NavigationBarView getDefaultNavigationBarView() {
        return getNavigationBarView(0);
    }

    @Nullable
    public NavigationBarView getNavigationBarView(int displayId) {
        NavigationBarFragment navBar = this.mNavigationBars.get(displayId);
        if (navBar == null) {
            return null;
        }
        return (NavigationBarView) navBar.getView();
    }

    public NavigationBarFragment getDefaultNavigationBarFragment() {
        return this.mNavigationBars.get(0);
    }
}
