package com.android.systemui.statusbar.notification;

import android.content.Context;
import android.util.ArraySet;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.statusbar.phone.UnlockMethodCache;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import java.util.Iterator;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class DynamicPrivacyController implements UnlockMethodCache.OnUnlockMethodChangedListener {
    private boolean mCacheInvalid;
    private final KeyguardMonitor mKeyguardMonitor;
    private boolean mLastDynamicUnlocked;
    private ArraySet<Listener> mListeners;
    private final NotificationLockscreenUserManager mLockscreenUserManager;
    private final StatusBarStateController mStateController;
    private StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
    private final UnlockMethodCache mUnlockMethodCache;

    /* loaded from: classes21.dex */
    public interface Listener {
        void onDynamicPrivacyChanged();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Inject
    public DynamicPrivacyController(Context context, KeyguardMonitor keyguardMonitor, NotificationLockscreenUserManager notificationLockscreenUserManager, StatusBarStateController stateController) {
        this(notificationLockscreenUserManager, keyguardMonitor, UnlockMethodCache.getInstance(context), stateController);
    }

    @VisibleForTesting
    DynamicPrivacyController(NotificationLockscreenUserManager notificationLockscreenUserManager, KeyguardMonitor keyguardMonitor, UnlockMethodCache unlockMethodCache, StatusBarStateController stateController) {
        this.mListeners = new ArraySet<>();
        this.mLockscreenUserManager = notificationLockscreenUserManager;
        this.mStateController = stateController;
        this.mUnlockMethodCache = unlockMethodCache;
        this.mKeyguardMonitor = keyguardMonitor;
        this.mUnlockMethodCache.addListener(this);
        this.mLastDynamicUnlocked = isDynamicallyUnlocked();
    }

    @Override // com.android.systemui.statusbar.phone.UnlockMethodCache.OnUnlockMethodChangedListener
    public void onUnlockMethodStateChanged() {
        if (isDynamicPrivacyEnabled()) {
            boolean dynamicallyUnlocked = isDynamicallyUnlocked();
            if (dynamicallyUnlocked != this.mLastDynamicUnlocked || this.mCacheInvalid) {
                this.mLastDynamicUnlocked = dynamicallyUnlocked;
                Iterator<Listener> it = this.mListeners.iterator();
                while (it.hasNext()) {
                    Listener listener = it.next();
                    listener.onDynamicPrivacyChanged();
                }
            }
            this.mCacheInvalid = false;
            return;
        }
        this.mCacheInvalid = true;
    }

    private boolean isDynamicPrivacyEnabled() {
        NotificationLockscreenUserManager notificationLockscreenUserManager = this.mLockscreenUserManager;
        return !notificationLockscreenUserManager.shouldHideNotifications(notificationLockscreenUserManager.getCurrentUserId());
    }

    public boolean isDynamicallyUnlocked() {
        return (this.mUnlockMethodCache.canSkipBouncer() || this.mKeyguardMonitor.isKeyguardGoingAway() || this.mKeyguardMonitor.isKeyguardFadingAway()) && isDynamicPrivacyEnabled();
    }

    public void addListener(Listener listener) {
        this.mListeners.add(listener);
    }

    public boolean isInLockedDownShade() {
        if (this.mStatusBarKeyguardViewManager.isShowing() && this.mUnlockMethodCache.isMethodSecure()) {
            int state = this.mStateController.getState();
            return (state == 0 || state == 2) && isDynamicPrivacyEnabled() && !isDynamicallyUnlocked();
        }
        return false;
    }

    public void setStatusBarKeyguardViewManager(StatusBarKeyguardViewManager statusBarKeyguardViewManager) {
        this.mStatusBarKeyguardViewManager = statusBarKeyguardViewManager;
    }
}
