package com.android.systemui.statusbar.phone;

import android.graphics.Rect;
import android.os.Handler;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.SparseArray;
import android.view.IWallpaperVisibilityListener;
import android.view.IWindowManager;
import android.view.View;
import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.LightBarTransitionsController;
import com.android.systemui.statusbar.phone.NavigationBarTransitions;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes21.dex */
public final class NavigationBarTransitions extends BarTransitions implements LightBarTransitionsController.DarkIntensityApplier {
    private final boolean mAllowAutoDimWallpaperNotVisible;
    private boolean mAutoDim;
    private final IStatusBarService mBarService;
    private List<DarkIntensityListener> mDarkIntensityListeners;
    private final Handler mHandler;
    private final LightBarTransitionsController mLightTransitionsController;
    private boolean mLightsOut;
    private int mNavBarMode;
    private View mNavButtons;
    private final NavigationBarView mView;
    private final IWallpaperVisibilityListener mWallpaperVisibilityListener;
    private boolean mWallpaperVisible;

    /* loaded from: classes21.dex */
    public interface DarkIntensityListener {
        void onDarkIntensity(float f);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.systemui.statusbar.phone.NavigationBarTransitions$1  reason: invalid class name */
    /* loaded from: classes21.dex */
    public class AnonymousClass1 extends IWallpaperVisibilityListener.Stub {
        AnonymousClass1() {
        }

        public void onWallpaperVisibilityChanged(boolean newVisibility, int displayId) throws RemoteException {
            NavigationBarTransitions.this.mWallpaperVisible = newVisibility;
            NavigationBarTransitions.this.mHandler.post(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NavigationBarTransitions$1$5foY_Yygo1gW25-mVBRpPSQRb_g
                @Override // java.lang.Runnable
                public final void run() {
                    NavigationBarTransitions.AnonymousClass1.this.lambda$onWallpaperVisibilityChanged$0$NavigationBarTransitions$1();
                }
            });
        }

        public /* synthetic */ void lambda$onWallpaperVisibilityChanged$0$NavigationBarTransitions$1() {
            NavigationBarTransitions.this.applyLightsOut(true, false);
        }
    }

    public NavigationBarTransitions(NavigationBarView view) {
        super(view, R.drawable.nav_background);
        this.mNavBarMode = 0;
        this.mHandler = Handler.getMain();
        this.mWallpaperVisibilityListener = new AnonymousClass1();
        this.mView = view;
        this.mBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
        this.mLightTransitionsController = new LightBarTransitionsController(view.getContext(), this);
        this.mAllowAutoDimWallpaperNotVisible = view.getContext().getResources().getBoolean(R.bool.config_navigation_bar_enable_auto_dim_no_visible_wallpaper);
        this.mDarkIntensityListeners = new ArrayList();
        IWindowManager windowManagerService = (IWindowManager) Dependency.get(IWindowManager.class);
        try {
            this.mWallpaperVisible = windowManagerService.registerWallpaperVisibilityListener(this.mWallpaperVisibilityListener, 0);
        } catch (RemoteException e) {
        }
        this.mView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NavigationBarTransitions$XJcD0ZRW4UO2juvu7uZcSTj_ILk
            @Override // android.view.View.OnLayoutChangeListener
            public final void onLayoutChange(View view2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                NavigationBarTransitions.this.lambda$new$0$NavigationBarTransitions(view2, i, i2, i3, i4, i5, i6, i7, i8);
            }
        });
        View currentView = this.mView.getCurrentView();
        if (currentView != null) {
            this.mNavButtons = currentView.findViewById(R.id.nav_buttons);
        }
    }

    public /* synthetic */ void lambda$new$0$NavigationBarTransitions(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        View currentView = this.mView.getCurrentView();
        if (currentView != null) {
            this.mNavButtons = currentView.findViewById(R.id.nav_buttons);
            applyLightsOut(false, true);
        }
    }

    public void init() {
        applyModeBackground(-1, getMode(), false);
        applyLightsOut(false, true);
    }

    @Override // com.android.systemui.statusbar.phone.BarTransitions
    public void destroy() {
        IWindowManager windowManagerService = (IWindowManager) Dependency.get(IWindowManager.class);
        try {
            windowManagerService.unregisterWallpaperVisibilityListener(this.mWallpaperVisibilityListener, 0);
        } catch (RemoteException e) {
        }
    }

    @Override // com.android.systemui.statusbar.phone.BarTransitions
    public void setAutoDim(boolean autoDim) {
        if ((autoDim && NavBarTintController.isEnabled(this.mView.getContext(), this.mNavBarMode)) || this.mAutoDim == autoDim) {
            return;
        }
        this.mAutoDim = autoDim;
        applyLightsOut(true, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setBackgroundFrame(Rect frame) {
        this.mBarBackground.setFrame(frame);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.phone.BarTransitions
    public boolean isLightsOut(int mode) {
        return super.isLightsOut(mode) || (this.mAllowAutoDimWallpaperNotVisible && this.mAutoDim && !this.mWallpaperVisible && mode != 5);
    }

    public LightBarTransitionsController getLightTransitionsController() {
        return this.mLightTransitionsController;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.phone.BarTransitions
    public void onTransition(int oldMode, int newMode, boolean animate) {
        super.onTransition(oldMode, newMode, animate);
        applyLightsOut(animate, false);
        this.mView.onBarTransition(newMode);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyLightsOut(boolean animate, boolean force) {
        applyLightsOut(isLightsOut(getMode()), animate, force);
    }

    private void applyLightsOut(boolean lightsOut, boolean animate, boolean force) {
        if (force || lightsOut != this.mLightsOut) {
            this.mLightsOut = lightsOut;
            View view = this.mNavButtons;
            if (view == null) {
                return;
            }
            view.animate().cancel();
            float darkBump = this.mLightTransitionsController.getCurrentDarkIntensity() / 10.0f;
            float navButtonsAlpha = lightsOut ? 0.6f + darkBump : 1.0f;
            if (!animate) {
                this.mNavButtons.setAlpha(navButtonsAlpha);
                return;
            }
            int duration = lightsOut ? BarTransitions.LIGHTS_OUT_DURATION : 250;
            this.mNavButtons.animate().alpha(navButtonsAlpha).setDuration(duration).start();
        }
    }

    public void reapplyDarkIntensity() {
        applyDarkIntensity(this.mLightTransitionsController.getCurrentDarkIntensity());
    }

    @Override // com.android.systemui.statusbar.phone.LightBarTransitionsController.DarkIntensityApplier
    public void applyDarkIntensity(float darkIntensity) {
        SparseArray<ButtonDispatcher> buttonDispatchers = this.mView.getButtonDispatchers();
        for (int i = buttonDispatchers.size() - 1; i >= 0; i--) {
            buttonDispatchers.valueAt(i).setDarkIntensity(darkIntensity);
        }
        this.mView.getRotationButtonController().setDarkIntensity(darkIntensity);
        for (DarkIntensityListener listener : this.mDarkIntensityListeners) {
            listener.onDarkIntensity(darkIntensity);
        }
        if (this.mAutoDim) {
            applyLightsOut(false, true);
        }
    }

    @Override // com.android.systemui.statusbar.phone.LightBarTransitionsController.DarkIntensityApplier
    public int getTintAnimationDuration() {
        if (NavBarTintController.isEnabled(this.mView.getContext(), this.mNavBarMode)) {
            return Math.max((int) NavBarTintController.DEFAULT_COLOR_ADAPT_TRANSITION_TIME, 400);
        }
        return 120;
    }

    public void onNavigationModeChanged(int mode) {
        this.mNavBarMode = mode;
    }

    public float addDarkIntensityListener(DarkIntensityListener listener) {
        this.mDarkIntensityListeners.add(listener);
        return this.mLightTransitionsController.getCurrentDarkIntensity();
    }

    public void removeDarkIntensityListener(DarkIntensityListener listener) {
        this.mDarkIntensityListeners.remove(listener);
    }
}
