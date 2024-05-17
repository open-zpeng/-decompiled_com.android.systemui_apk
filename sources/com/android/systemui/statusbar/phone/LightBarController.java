package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import com.android.internal.colorextraction.ColorExtractor;
import com.android.systemui.Dumpable;
import com.android.systemui.R;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.statusbar.policy.BatteryController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class LightBarController implements BatteryController.BatteryStateChangeCallback, Dumpable {
    private static final float NAV_BAR_INVERSION_SCRIM_ALPHA_THRESHOLD = 0.1f;
    private final BatteryController mBatteryController;
    private BiometricUnlockController mBiometricUnlockController;
    private final Color mDarkModeColor;
    private boolean mDirectReplying;
    private boolean mDockedLight;
    private int mDockedStackVisibility;
    private boolean mForceDarkForScrim;
    private boolean mFullscreenLight;
    private int mFullscreenStackVisibility;
    private boolean mHasLightNavigationBar;
    private int mLastNavigationBarMode;
    private int mLastStatusBarMode;
    private boolean mNavbarColorManagedByIme;
    private LightBarTransitionsController mNavigationBarController;
    private boolean mNavigationLight;
    private boolean mQsCustomizing;
    private final SysuiDarkIconDispatcher mStatusBarIconController;
    private int mSystemUiVisibility;
    private final Rect mLastFullscreenBounds = new Rect();
    private final Rect mLastDockedBounds = new Rect();

    @Inject
    public LightBarController(Context ctx, DarkIconDispatcher darkIconDispatcher, BatteryController batteryController) {
        this.mDarkModeColor = Color.valueOf(ctx.getColor(R.color.dark_mode_icon_color_single_tone));
        this.mStatusBarIconController = (SysuiDarkIconDispatcher) darkIconDispatcher;
        this.mBatteryController = batteryController;
        this.mBatteryController.addCallback(this);
    }

    public void setNavigationBar(LightBarTransitionsController navigationBar) {
        this.mNavigationBarController = navigationBar;
        updateNavigation();
    }

    public void setBiometricUnlockController(BiometricUnlockController biometricUnlockController) {
        this.mBiometricUnlockController = biometricUnlockController;
    }

    public void onSystemUiVisibilityChanged(int fullscreenStackVis, int dockedStackVis, int mask, Rect fullscreenStackBounds, Rect dockedStackBounds, boolean sbModeChanged, int statusBarMode, boolean navbarColorManagedByIme) {
        int oldFullscreen = this.mFullscreenStackVisibility;
        int newFullscreen = ((~mask) & oldFullscreen) | (fullscreenStackVis & mask);
        int diffFullscreen = newFullscreen ^ oldFullscreen;
        int oldDocked = this.mDockedStackVisibility;
        int newDocked = ((~mask) & oldDocked) | (dockedStackVis & mask);
        int diffDocked = newDocked ^ oldDocked;
        if ((diffFullscreen & 8192) != 0 || (diffDocked & 8192) != 0 || sbModeChanged || !this.mLastFullscreenBounds.equals(fullscreenStackBounds) || !this.mLastDockedBounds.equals(dockedStackBounds)) {
            this.mFullscreenLight = isLight(newFullscreen, statusBarMode, 8192);
            this.mDockedLight = isLight(newDocked, statusBarMode, 8192);
            updateStatus(fullscreenStackBounds, dockedStackBounds);
        }
        this.mFullscreenStackVisibility = newFullscreen;
        this.mDockedStackVisibility = newDocked;
        this.mLastStatusBarMode = statusBarMode;
        this.mNavbarColorManagedByIme = navbarColorManagedByIme;
        this.mLastFullscreenBounds.set(fullscreenStackBounds);
        this.mLastDockedBounds.set(dockedStackBounds);
    }

    public void onNavigationVisibilityChanged(int vis, int mask, boolean nbModeChanged, int navigationBarMode, boolean navbarColorManagedByIme) {
        int oldVis = this.mSystemUiVisibility;
        int newVis = ((~mask) & oldVis) | (vis & mask);
        int diffVis = newVis ^ oldVis;
        if ((diffVis & 16) != 0 || nbModeChanged) {
            boolean last = this.mNavigationLight;
            this.mHasLightNavigationBar = isLight(vis, navigationBarMode, 16);
            this.mNavigationLight = this.mHasLightNavigationBar && ((this.mDirectReplying && this.mNavbarColorManagedByIme) || !this.mForceDarkForScrim) && !this.mQsCustomizing;
            if (this.mNavigationLight != last) {
                updateNavigation();
            }
        }
        this.mSystemUiVisibility = newVis;
        this.mLastNavigationBarMode = navigationBarMode;
        this.mNavbarColorManagedByIme = navbarColorManagedByIme;
    }

    private void reevaluate() {
        onSystemUiVisibilityChanged(this.mFullscreenStackVisibility, this.mDockedStackVisibility, 0, this.mLastFullscreenBounds, this.mLastDockedBounds, true, this.mLastStatusBarMode, this.mNavbarColorManagedByIme);
        onNavigationVisibilityChanged(this.mSystemUiVisibility, 0, true, this.mLastNavigationBarMode, this.mNavbarColorManagedByIme);
    }

    public void setQsCustomizing(boolean customizing) {
        if (this.mQsCustomizing == customizing) {
            return;
        }
        this.mQsCustomizing = customizing;
        reevaluate();
    }

    public void setDirectReplying(boolean directReplying) {
        if (this.mDirectReplying == directReplying) {
            return;
        }
        this.mDirectReplying = directReplying;
        reevaluate();
    }

    public void setScrimState(ScrimState scrimState, float scrimBehindAlpha, ColorExtractor.GradientColors scrimInFrontColor) {
        boolean forceDarkForScrimLast = this.mForceDarkForScrim;
        this.mForceDarkForScrim = (scrimState == ScrimState.BOUNCER || scrimState == ScrimState.BOUNCER_SCRIMMED || scrimBehindAlpha < 0.1f || scrimInFrontColor.supportsDarkText()) ? false : true;
        if (this.mHasLightNavigationBar && this.mForceDarkForScrim != forceDarkForScrimLast) {
            reevaluate();
        }
    }

    private boolean isLight(int vis, int barMode, int flag) {
        boolean isTransparentBar = barMode == 4 || barMode == 6;
        boolean light = (vis & flag) != 0;
        return isTransparentBar && light;
    }

    private boolean animateChange() {
        int unlockMode;
        BiometricUnlockController biometricUnlockController = this.mBiometricUnlockController;
        return (biometricUnlockController == null || (unlockMode = biometricUnlockController.getMode()) == 2 || unlockMode == 1) ? false : true;
    }

    private void updateStatus(Rect fullscreenStackBounds, Rect dockedStackBounds) {
        boolean hasDockedStack = !dockedStackBounds.isEmpty();
        if ((this.mFullscreenLight && this.mDockedLight) || (this.mFullscreenLight && !hasDockedStack)) {
            this.mStatusBarIconController.setIconsDarkArea(null);
            this.mStatusBarIconController.getTransitionsController().setIconsDark(true, animateChange());
        } else if ((!this.mFullscreenLight && !this.mDockedLight) || (!this.mFullscreenLight && !hasDockedStack)) {
            this.mStatusBarIconController.getTransitionsController().setIconsDark(false, animateChange());
        } else {
            Rect bounds = this.mFullscreenLight ? fullscreenStackBounds : dockedStackBounds;
            if (bounds.isEmpty()) {
                this.mStatusBarIconController.setIconsDarkArea(null);
            } else {
                this.mStatusBarIconController.setIconsDarkArea(bounds);
            }
            this.mStatusBarIconController.getTransitionsController().setIconsDark(true, animateChange());
        }
    }

    private void updateNavigation() {
        LightBarTransitionsController lightBarTransitionsController = this.mNavigationBarController;
        if (lightBarTransitionsController != null) {
            lightBarTransitionsController.setIconsDark(this.mNavigationLight, animateChange());
        }
    }

    @Override // com.android.systemui.statusbar.policy.BatteryController.BatteryStateChangeCallback
    public void onBatteryLevelChanged(int level, boolean pluggedIn, boolean charging) {
    }

    @Override // com.android.systemui.statusbar.policy.BatteryController.BatteryStateChangeCallback
    public void onPowerSaveChanged(boolean isPowerSave) {
        reevaluate();
    }

    @Override // com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("LightBarController: ");
        pw.print(" mSystemUiVisibility=0x");
        pw.print(Integer.toHexString(this.mSystemUiVisibility));
        pw.print(" mFullscreenStackVisibility=0x");
        pw.print(Integer.toHexString(this.mFullscreenStackVisibility));
        pw.print(" mDockedStackVisibility=0x");
        pw.println(Integer.toHexString(this.mDockedStackVisibility));
        pw.print(" mFullscreenLight=");
        pw.print(this.mFullscreenLight);
        pw.print(" mDockedLight=");
        pw.println(this.mDockedLight);
        pw.print(" mLastFullscreenBounds=");
        pw.print(this.mLastFullscreenBounds);
        pw.print(" mLastDockedBounds=");
        pw.println(this.mLastDockedBounds);
        pw.print(" mNavigationLight=");
        pw.print(this.mNavigationLight);
        pw.print(" mHasLightNavigationBar=");
        pw.println(this.mHasLightNavigationBar);
        pw.print(" mLastStatusBarMode=");
        pw.print(this.mLastStatusBarMode);
        pw.print(" mLastNavigationBarMode=");
        pw.println(this.mLastNavigationBarMode);
        pw.print(" mForceDarkForScrim=");
        pw.print(this.mForceDarkForScrim);
        pw.print(" mQsCustomizing=");
        pw.print(this.mQsCustomizing);
        pw.print(" mDirectReplying=");
        pw.println(this.mDirectReplying);
        pw.print(" mNavbarColorManagedByIme=");
        pw.println(this.mNavbarColorManagedByIme);
        pw.println();
        LightBarTransitionsController transitionsController = this.mStatusBarIconController.getTransitionsController();
        if (transitionsController != null) {
            pw.println(" StatusBarTransitionsController:");
            transitionsController.dump(fd, pw, args);
            pw.println();
        }
        if (this.mNavigationBarController != null) {
            pw.println(" NavigationBarTransitionsController:");
            this.mNavigationBarController.dump(fd, pw, args);
            pw.println();
        }
    }
}
