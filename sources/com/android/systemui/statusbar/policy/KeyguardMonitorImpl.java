package com.android.systemui.statusbar.policy;

import android.content.Context;
import com.android.internal.util.Preconditions;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
import java.util.ArrayList;
import java.util.function.Consumer;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class KeyguardMonitorImpl extends KeyguardUpdateMonitorCallback implements KeyguardMonitor {
    private boolean mBypassFadingAnimation;
    private final ArrayList<KeyguardMonitor.Callback> mCallbacks = new ArrayList<>();
    private final Context mContext;
    private boolean mKeyguardFadingAway;
    private long mKeyguardFadingAwayDelay;
    private long mKeyguardFadingAwayDuration;
    private boolean mKeyguardGoingAway;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private boolean mLaunchTransitionFadingAway;
    private boolean mListening;
    private boolean mOccluded;
    private boolean mSecure;
    private boolean mShowing;

    @Inject
    public KeyguardMonitorImpl(Context context) {
        this.mContext = context;
        this.mKeyguardUpdateMonitor = KeyguardUpdateMonitor.getInstance(this.mContext);
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void addCallback(KeyguardMonitor.Callback callback) {
        Preconditions.checkNotNull(callback, "Callback must not be null. b/128895449");
        this.mCallbacks.add(callback);
        if (this.mCallbacks.size() != 0 && !this.mListening) {
            this.mListening = true;
            this.mKeyguardUpdateMonitor.registerCallback(this);
        }
    }

    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void removeCallback(KeyguardMonitor.Callback callback) {
        Preconditions.checkNotNull(callback, "Callback must not be null. b/128895449");
        if (this.mCallbacks.remove(callback) && this.mCallbacks.size() == 0 && this.mListening) {
            this.mListening = false;
            this.mKeyguardUpdateMonitor.removeCallback(this);
        }
    }

    @Override // com.android.systemui.statusbar.policy.KeyguardMonitor
    public boolean isShowing() {
        return this.mShowing;
    }

    @Override // com.android.systemui.statusbar.policy.KeyguardMonitor
    public boolean isSecure() {
        return this.mSecure;
    }

    @Override // com.android.systemui.statusbar.policy.KeyguardMonitor
    public boolean isOccluded() {
        return this.mOccluded;
    }

    @Override // com.android.systemui.statusbar.policy.KeyguardMonitor
    public void notifyKeyguardState(boolean showing, boolean secure, boolean occluded) {
        if (this.mShowing == showing && this.mSecure == secure && this.mOccluded == occluded) {
            return;
        }
        this.mShowing = showing;
        this.mSecure = secure;
        this.mOccluded = occluded;
        notifyKeyguardChanged();
    }

    @Override // com.android.keyguard.KeyguardUpdateMonitorCallback
    public void onTrustChanged(int userId) {
        notifyKeyguardChanged();
    }

    @Override // com.android.systemui.statusbar.policy.KeyguardMonitor
    public boolean isDeviceInteractive() {
        return this.mKeyguardUpdateMonitor.isDeviceInteractive();
    }

    private void notifyKeyguardChanged() {
        new ArrayList(this.mCallbacks).forEach(new Consumer() { // from class: com.android.systemui.statusbar.policy.-$$Lambda$CusFj6pVztwBZlitsnMLA9Hx95I
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((KeyguardMonitor.Callback) obj).onKeyguardShowingChanged();
            }
        });
    }

    @Override // com.android.systemui.statusbar.policy.KeyguardMonitor
    public void notifyKeyguardFadingAway(long delay, long fadeoutDuration, boolean isBypassFading) {
        this.mKeyguardFadingAwayDelay = delay;
        this.mKeyguardFadingAwayDuration = fadeoutDuration;
        this.mBypassFadingAnimation = isBypassFading;
        setKeyguardFadingAway(true);
    }

    private void setKeyguardFadingAway(boolean keyguardFadingAway) {
        if (this.mKeyguardFadingAway != keyguardFadingAway) {
            this.mKeyguardFadingAway = keyguardFadingAway;
            ArrayList<KeyguardMonitor.Callback> callbacks = new ArrayList<>(this.mCallbacks);
            for (int i = 0; i < callbacks.size(); i++) {
                callbacks.get(i).onKeyguardFadingAwayChanged();
            }
        }
    }

    @Override // com.android.systemui.statusbar.policy.KeyguardMonitor
    public void notifyKeyguardDoneFading() {
        this.mKeyguardGoingAway = false;
        setKeyguardFadingAway(false);
    }

    @Override // com.android.systemui.statusbar.policy.KeyguardMonitor
    public boolean isKeyguardFadingAway() {
        return this.mKeyguardFadingAway;
    }

    @Override // com.android.systemui.statusbar.policy.KeyguardMonitor
    public boolean isKeyguardGoingAway() {
        return this.mKeyguardGoingAway;
    }

    @Override // com.android.systemui.statusbar.policy.KeyguardMonitor
    public boolean isBypassFadingAnimation() {
        return this.mBypassFadingAnimation;
    }

    @Override // com.android.systemui.statusbar.policy.KeyguardMonitor
    public long getKeyguardFadingAwayDelay() {
        return this.mKeyguardFadingAwayDelay;
    }

    @Override // com.android.systemui.statusbar.policy.KeyguardMonitor
    public long getKeyguardFadingAwayDuration() {
        return this.mKeyguardFadingAwayDuration;
    }

    @Override // com.android.systemui.statusbar.policy.KeyguardMonitor
    public long calculateGoingToFullShadeDelay() {
        return this.mKeyguardFadingAwayDelay + this.mKeyguardFadingAwayDuration;
    }

    @Override // com.android.systemui.statusbar.policy.KeyguardMonitor
    public void notifyKeyguardGoingAway(boolean keyguardGoingAway) {
        this.mKeyguardGoingAway = keyguardGoingAway;
    }

    @Override // com.android.systemui.statusbar.policy.KeyguardMonitor
    public void setLaunchTransitionFadingAway(boolean fadingAway) {
        this.mLaunchTransitionFadingAway = fadingAway;
    }

    @Override // com.android.systemui.statusbar.policy.KeyguardMonitor
    public boolean isLaunchTransitionFadingAway() {
        return this.mLaunchTransitionFadingAway;
    }
}
