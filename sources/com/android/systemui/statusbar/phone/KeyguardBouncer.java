package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.os.UserManager;
import android.util.Log;
import android.util.MathUtils;
import android.util.Slog;
import android.util.StatsLog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardHostView;
import com.android.keyguard.KeyguardSecurityModel;
import com.android.keyguard.KeyguardSecurityView;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.keyguard.ViewMediatorCallback;
import com.android.systemui.DejankUtils;
import com.android.systemui.R;
import com.android.systemui.keyguard.DismissCallbackRegistry;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import java.io.PrintWriter;
/* loaded from: classes21.dex */
public class KeyguardBouncer {
    static final float ALPHA_EXPANSION_THRESHOLD = 0.95f;
    static final long BOUNCER_FACE_DELAY = 1200;
    static final float EXPANSION_HIDDEN = 1.0f;
    static final float EXPANSION_VISIBLE = 0.0f;
    private static final String TAG = "KeyguardBouncer";
    private int mBouncerPromptReason;
    protected final ViewMediatorCallback mCallback;
    protected final ViewGroup mContainer;
    protected final Context mContext;
    private final DismissCallbackRegistry mDismissCallbackRegistry;
    private final BouncerExpansionCallback mExpansionCallback;
    private final FalsingManager mFalsingManager;
    private final Handler mHandler;
    private boolean mIsAnimatingAway;
    private boolean mIsScrimmed;
    private final KeyguardBypassController mKeyguardBypassController;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    protected KeyguardHostView mKeyguardView;
    private ViewGroup mLockIconContainer;
    protected final LockPatternUtils mLockPatternUtils;
    protected ViewGroup mRoot;
    private boolean mShowingSoon;
    private int mStatusBarHeight;
    private final UnlockMethodCache mUnlockMethodCache;
    private final KeyguardUpdateMonitorCallback mUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() { // from class: com.android.systemui.statusbar.phone.KeyguardBouncer.1
        @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
        public void onStrongAuthStateChanged(int userId) {
            KeyguardBouncer keyguardBouncer = KeyguardBouncer.this;
            keyguardBouncer.mBouncerPromptReason = keyguardBouncer.mCallback.getBouncerPromptReason();
        }
    };
    private final Runnable mRemoveViewRunnable = new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$iQsniWdIxLGqyYwRi09kQ-Ah02M
        @Override // java.lang.Runnable
        public final void run() {
            KeyguardBouncer.this.removeView();
        }
    };
    private final Runnable mResetRunnable = new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$KeyguardBouncer$Y9Hvfk0n3yPK2FQ39O1Z5j49gj0
        @Override // java.lang.Runnable
        public final void run() {
            KeyguardBouncer.this.lambda$new$0$KeyguardBouncer();
        }
    };
    private float mExpansion = 1.0f;
    private final Runnable mShowRunnable = new Runnable() { // from class: com.android.systemui.statusbar.phone.KeyguardBouncer.2
        @Override // java.lang.Runnable
        public void run() {
            KeyguardBouncer.this.mRoot.setVisibility(0);
            KeyguardBouncer keyguardBouncer = KeyguardBouncer.this;
            keyguardBouncer.showPromptReason(keyguardBouncer.mBouncerPromptReason);
            CharSequence customMessage = KeyguardBouncer.this.mCallback.consumeCustomMessage();
            if (customMessage != null) {
                KeyguardBouncer.this.mKeyguardView.showErrorMessage(customMessage);
            }
            if (KeyguardBouncer.this.mKeyguardView.getHeight() != 0 && KeyguardBouncer.this.mKeyguardView.getHeight() != KeyguardBouncer.this.mStatusBarHeight) {
                KeyguardBouncer.this.mKeyguardView.startAppearAnimation();
            } else {
                KeyguardBouncer.this.mKeyguardView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: com.android.systemui.statusbar.phone.KeyguardBouncer.2.1
                    @Override // android.view.ViewTreeObserver.OnPreDrawListener
                    public boolean onPreDraw() {
                        KeyguardBouncer.this.mKeyguardView.getViewTreeObserver().removeOnPreDrawListener(this);
                        KeyguardBouncer.this.mKeyguardView.startAppearAnimation();
                        return true;
                    }
                });
                KeyguardBouncer.this.mKeyguardView.requestLayout();
            }
            KeyguardBouncer.this.mShowingSoon = false;
            if (KeyguardBouncer.this.mExpansion == 0.0f) {
                KeyguardBouncer.this.mKeyguardView.onResume();
                KeyguardBouncer.this.mKeyguardView.resetSecurityContainer();
            }
            StatsLog.write(63, 2);
        }
    };

    /* loaded from: classes21.dex */
    public interface BouncerExpansionCallback {
        void onFullyHidden();

        void onFullyShown();

        void onStartingToHide();

        void onStartingToShow();
    }

    public /* synthetic */ void lambda$new$0$KeyguardBouncer() {
        KeyguardHostView keyguardHostView = this.mKeyguardView;
        if (keyguardHostView != null) {
            keyguardHostView.resetSecurityContainer();
        }
    }

    public KeyguardBouncer(Context context, ViewMediatorCallback callback, LockPatternUtils lockPatternUtils, ViewGroup container, DismissCallbackRegistry dismissCallbackRegistry, FalsingManager falsingManager, BouncerExpansionCallback expansionCallback, UnlockMethodCache unlockMethodCache, KeyguardUpdateMonitor keyguardUpdateMonitor, KeyguardBypassController keyguardBypassController, Handler handler) {
        this.mContext = context;
        this.mCallback = callback;
        this.mLockPatternUtils = lockPatternUtils;
        this.mContainer = container;
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        this.mFalsingManager = falsingManager;
        this.mDismissCallbackRegistry = dismissCallbackRegistry;
        this.mExpansionCallback = expansionCallback;
        this.mHandler = handler;
        this.mUnlockMethodCache = unlockMethodCache;
        this.mKeyguardUpdateMonitor.registerCallback(this.mUpdateMonitorCallback);
        this.mKeyguardBypassController = keyguardBypassController;
    }

    public void show(boolean resetSecuritySelection) {
        show(resetSecuritySelection, true);
    }

    public void show(boolean resetSecuritySelection, boolean isScrimmed) {
        int keyguardUserId = KeyguardUpdateMonitor.getCurrentUser();
        if (keyguardUserId == 0 && UserManager.isSplitSystemUser()) {
            return;
        }
        ensureView();
        this.mIsScrimmed = isScrimmed;
        if (isScrimmed) {
            setExpansion(0.0f);
        }
        if (resetSecuritySelection) {
            showPrimarySecurityScreen();
        }
        if (this.mRoot.getVisibility() == 0 || this.mShowingSoon) {
            return;
        }
        int activeUserId = KeyguardUpdateMonitor.getCurrentUser();
        boolean allowDismissKeyguard = false;
        boolean isSystemUser = UserManager.isSplitSystemUser() && activeUserId == 0;
        if (!isSystemUser && activeUserId == keyguardUserId) {
            allowDismissKeyguard = true;
        }
        if (allowDismissKeyguard && this.mKeyguardView.dismiss(activeUserId)) {
            return;
        }
        if (!allowDismissKeyguard) {
            Slog.w(TAG, "User can't dismiss keyguard: " + activeUserId + " != " + keyguardUserId);
        }
        this.mShowingSoon = true;
        DejankUtils.removeCallbacks(this.mResetRunnable);
        if (this.mUnlockMethodCache.isFaceAuthEnabled() && !needsFullscreenBouncer() && !this.mKeyguardUpdateMonitor.userNeedsStrongAuth() && !this.mKeyguardBypassController.getBypassEnabled()) {
            this.mHandler.postDelayed(this.mShowRunnable, BOUNCER_FACE_DELAY);
        } else {
            DejankUtils.postAfterTraversal(this.mShowRunnable);
        }
        this.mCallback.onBouncerVisiblityChanged(true);
        this.mExpansionCallback.onStartingToShow();
    }

    public boolean isScrimmed() {
        return this.mIsScrimmed;
    }

    public ViewGroup getLockIconContainer() {
        ViewGroup viewGroup = this.mRoot;
        if (viewGroup == null || viewGroup.getVisibility() != 0) {
            return null;
        }
        return this.mLockIconContainer;
    }

    private void onFullyShown() {
        this.mFalsingManager.onBouncerShown();
        KeyguardHostView keyguardHostView = this.mKeyguardView;
        if (keyguardHostView == null) {
            Log.wtf(TAG, "onFullyShown when view was null");
        } else {
            keyguardHostView.onResume();
        }
    }

    private void onFullyHidden() {
        cancelShowRunnable();
        ViewGroup viewGroup = this.mRoot;
        if (viewGroup != null) {
            viewGroup.setVisibility(4);
        }
        this.mFalsingManager.onBouncerHidden();
        DejankUtils.postAfterTraversal(this.mResetRunnable);
    }

    public void showPromptReason(int reason) {
        KeyguardHostView keyguardHostView = this.mKeyguardView;
        if (keyguardHostView != null) {
            keyguardHostView.showPromptReason(reason);
        } else {
            Log.w(TAG, "Trying to show prompt reason on empty bouncer");
        }
    }

    public void showMessage(String message, ColorStateList colorState) {
        KeyguardHostView keyguardHostView = this.mKeyguardView;
        if (keyguardHostView != null) {
            keyguardHostView.showMessage(message, colorState);
        } else {
            Log.w(TAG, "Trying to show message on empty bouncer");
        }
    }

    private void cancelShowRunnable() {
        DejankUtils.removeCallbacks(this.mShowRunnable);
        this.mHandler.removeCallbacks(this.mShowRunnable);
        this.mShowingSoon = false;
    }

    public void showWithDismissAction(ActivityStarter.OnDismissAction r, Runnable cancelAction) {
        ensureView();
        this.mKeyguardView.setOnDismissAction(r, cancelAction);
        show(false);
    }

    public void hide(boolean destroyView) {
        if (isShowing()) {
            StatsLog.write(63, 1);
            this.mDismissCallbackRegistry.notifyDismissCancelled();
        }
        this.mIsScrimmed = false;
        this.mFalsingManager.onBouncerHidden();
        this.mCallback.onBouncerVisiblityChanged(false);
        cancelShowRunnable();
        KeyguardHostView keyguardHostView = this.mKeyguardView;
        if (keyguardHostView != null) {
            keyguardHostView.cancelDismissAction();
            this.mKeyguardView.cleanUp();
        }
        this.mIsAnimatingAway = false;
        ViewGroup viewGroup = this.mRoot;
        if (viewGroup != null) {
            viewGroup.setVisibility(4);
            if (destroyView) {
                this.mHandler.postDelayed(this.mRemoveViewRunnable, 50L);
            }
        }
    }

    public void startPreHideAnimation(Runnable runnable) {
        this.mIsAnimatingAway = true;
        KeyguardHostView keyguardHostView = this.mKeyguardView;
        if (keyguardHostView != null) {
            keyguardHostView.startDisappearAnimation(runnable);
        } else if (runnable != null) {
            runnable.run();
        }
    }

    public void reset() {
        cancelShowRunnable();
        inflateView();
        this.mFalsingManager.onBouncerHidden();
    }

    public void onScreenTurnedOff() {
        ViewGroup viewGroup;
        if (this.mKeyguardView != null && (viewGroup = this.mRoot) != null && viewGroup.getVisibility() == 0) {
            this.mKeyguardView.onPause();
        }
    }

    public boolean isShowing() {
        ViewGroup viewGroup;
        return (this.mShowingSoon || ((viewGroup = this.mRoot) != null && viewGroup.getVisibility() == 0)) && this.mExpansion == 0.0f && !isAnimatingAway();
    }

    public boolean inTransit() {
        if (!this.mShowingSoon) {
            float f = this.mExpansion;
            if (f == 1.0f || f == 0.0f) {
                return false;
            }
        }
        return true;
    }

    public boolean isAnimatingAway() {
        return this.mIsAnimatingAway;
    }

    public void prepare() {
        boolean wasInitialized = this.mRoot != null;
        ensureView();
        if (wasInitialized) {
            showPrimarySecurityScreen();
        }
        this.mBouncerPromptReason = this.mCallback.getBouncerPromptReason();
    }

    private void showPrimarySecurityScreen() {
        this.mKeyguardView.showPrimarySecurityScreen();
        KeyguardSecurityView keyguardSecurityView = this.mKeyguardView.getCurrentSecurityView();
        if (keyguardSecurityView != null) {
            this.mLockIconContainer = (ViewGroup) ((ViewGroup) keyguardSecurityView).findViewById(R.id.lock_icon_container);
        }
    }

    public void setExpansion(float fraction) {
        float oldExpansion = this.mExpansion;
        this.mExpansion = fraction;
        if (this.mKeyguardView != null && !this.mIsAnimatingAway) {
            float alpha = MathUtils.map((float) ALPHA_EXPANSION_THRESHOLD, 1.0f, 1.0f, 0.0f, fraction);
            this.mKeyguardView.setAlpha(MathUtils.constrain(alpha, 0.0f, 1.0f));
            KeyguardHostView keyguardHostView = this.mKeyguardView;
            keyguardHostView.setTranslationY(keyguardHostView.getHeight() * fraction);
        }
        if (fraction == 0.0f && oldExpansion != 0.0f) {
            onFullyShown();
            this.mExpansionCallback.onFullyShown();
        } else if (fraction == 1.0f && oldExpansion != 1.0f) {
            onFullyHidden();
            this.mExpansionCallback.onFullyHidden();
        } else if (fraction != 0.0f && oldExpansion == 0.0f) {
            this.mExpansionCallback.onStartingToHide();
        }
    }

    public boolean willDismissWithAction() {
        KeyguardHostView keyguardHostView = this.mKeyguardView;
        return keyguardHostView != null && keyguardHostView.hasDismissActions();
    }

    public int getTop() {
        KeyguardHostView keyguardHostView = this.mKeyguardView;
        if (keyguardHostView == null) {
            return 0;
        }
        int top = keyguardHostView.getTop();
        if (this.mKeyguardView.getCurrentSecurityMode() == KeyguardSecurityModel.SecurityMode.Password) {
            View messageArea = this.mKeyguardView.findViewById(R.id.keyguard_message_area);
            return top + messageArea.getTop();
        }
        return top;
    }

    protected void ensureView() {
        boolean forceRemoval = this.mHandler.hasCallbacks(this.mRemoveViewRunnable);
        if (this.mRoot == null || forceRemoval) {
            inflateView();
        }
    }

    protected void inflateView() {
        removeView();
        this.mHandler.removeCallbacks(this.mRemoveViewRunnable);
        this.mRoot = (ViewGroup) LayoutInflater.from(this.mContext).inflate(R.layout.keyguard_bouncer, (ViewGroup) null);
        this.mKeyguardView = (KeyguardHostView) this.mRoot.findViewById(R.id.keyguard_host_view);
        this.mKeyguardView.setLockPatternUtils(this.mLockPatternUtils);
        this.mKeyguardView.setViewMediatorCallback(this.mCallback);
        ViewGroup viewGroup = this.mContainer;
        viewGroup.addView(this.mRoot, viewGroup.getChildCount());
        this.mStatusBarHeight = this.mRoot.getResources().getDimensionPixelOffset(R.dimen.status_bar_height);
        this.mRoot.setVisibility(4);
        this.mRoot.setAccessibilityPaneTitle(this.mKeyguardView.getAccessibilityTitleForCurrentMode());
        WindowInsets rootInsets = this.mRoot.getRootWindowInsets();
        if (rootInsets != null) {
            this.mRoot.dispatchApplyWindowInsets(rootInsets);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void removeView() {
        ViewGroup viewGroup = this.mRoot;
        if (viewGroup != null) {
            ViewParent parent = viewGroup.getParent();
            ViewGroup viewGroup2 = this.mContainer;
            if (parent == viewGroup2) {
                viewGroup2.removeView(this.mRoot);
                this.mRoot = null;
            }
        }
    }

    public boolean onBackPressed() {
        KeyguardHostView keyguardHostView = this.mKeyguardView;
        return keyguardHostView != null && keyguardHostView.handleBackKey();
    }

    public boolean needsFullscreenBouncer() {
        ensureView();
        KeyguardHostView keyguardHostView = this.mKeyguardView;
        if (keyguardHostView != null) {
            KeyguardSecurityModel.SecurityMode mode = keyguardHostView.getSecurityMode();
            return mode == KeyguardSecurityModel.SecurityMode.SimPin || mode == KeyguardSecurityModel.SecurityMode.SimPuk;
        }
        return false;
    }

    public boolean isFullscreenBouncer() {
        KeyguardHostView keyguardHostView = this.mKeyguardView;
        if (keyguardHostView != null) {
            KeyguardSecurityModel.SecurityMode mode = keyguardHostView.getCurrentSecurityMode();
            return mode == KeyguardSecurityModel.SecurityMode.SimPin || mode == KeyguardSecurityModel.SecurityMode.SimPuk;
        }
        return false;
    }

    public boolean isSecure() {
        KeyguardHostView keyguardHostView = this.mKeyguardView;
        return keyguardHostView == null || keyguardHostView.getSecurityMode() != KeyguardSecurityModel.SecurityMode.None;
    }

    public boolean shouldDismissOnMenuPressed() {
        return this.mKeyguardView.shouldEnableMenuKey();
    }

    public boolean interceptMediaKey(KeyEvent event) {
        ensureView();
        return this.mKeyguardView.interceptMediaKey(event);
    }

    public void notifyKeyguardAuthenticated(boolean strongAuth) {
        ensureView();
        this.mKeyguardView.finish(strongAuth, KeyguardUpdateMonitor.getCurrentUser());
    }

    public void dump(PrintWriter pw) {
        pw.println(TAG);
        pw.println("  isShowing(): " + isShowing());
        pw.println("  mStatusBarHeight: " + this.mStatusBarHeight);
        pw.println("  mExpansion: " + this.mExpansion);
        pw.println("  mKeyguardView; " + this.mKeyguardView);
        pw.println("  mShowingSoon: " + this.mKeyguardView);
        pw.println("  mBouncerPromptReason: " + this.mBouncerPromptReason);
        pw.println("  mIsAnimatingAway: " + this.mIsAnimatingAway);
    }
}
