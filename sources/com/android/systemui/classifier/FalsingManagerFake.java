package com.android.systemui.classifier;

import android.net.Uri;
import android.view.MotionEvent;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.plugins.FalsingManager;
import java.io.PrintWriter;
/* loaded from: classes21.dex */
public class FalsingManagerFake implements FalsingManager {
    private boolean mIsClassiferEnabled;
    private boolean mIsFalseTouch;
    private boolean mIsReportingEnabled;
    private boolean mIsUnlockingDisabled;
    private boolean mShouldEnforceBouncer;

    @Override // com.android.systemui.plugins.FalsingManager
    public void onSucccessfulUnlock() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onNotificationActive() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void setShowingAod(boolean showingAod) {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onNotificatonStartDraggingDown() {
    }

    @VisibleForTesting
    public void setIsUnlockingDisabled(boolean isUnlockingDisabled) {
        this.mIsUnlockingDisabled = isUnlockingDisabled;
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public boolean isUnlockingDisabled() {
        return this.mIsUnlockingDisabled;
    }

    @VisibleForTesting
    public void setIsFalseTouch(boolean isFalseTouch) {
        this.mIsFalseTouch = isFalseTouch;
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public boolean isFalseTouch() {
        return this.mIsFalseTouch;
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onNotificatonStopDraggingDown() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void setNotificationExpanded() {
    }

    @VisibleForTesting
    public void setIsClassiferEnabled(boolean isClassiferEnabled) {
        this.mIsClassiferEnabled = isClassiferEnabled;
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public boolean isClassiferEnabled() {
        return this.mIsClassiferEnabled;
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onQsDown() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void setQsExpanded(boolean expanded) {
    }

    @VisibleForTesting
    public void setShouldEnforceBouncer(boolean shouldEnforceBouncer) {
        this.mShouldEnforceBouncer = shouldEnforceBouncer;
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public boolean shouldEnforceBouncer() {
        return this.mShouldEnforceBouncer;
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onTrackingStarted(boolean secure) {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onTrackingStopped() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onLeftAffordanceOn() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onCameraOn() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onAffordanceSwipingStarted(boolean rightCorner) {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onAffordanceSwipingAborted() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onStartExpandingFromPulse() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onExpansionFromPulseStopped() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public Uri reportRejectedTouch() {
        return null;
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onScreenOnFromTouch() {
    }

    @VisibleForTesting
    public void setIsReportingEnabled(boolean isReportingEnabled) {
        this.mIsReportingEnabled = isReportingEnabled;
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public boolean isReportingEnabled() {
        return this.mIsReportingEnabled;
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onUnlockHintStarted() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onCameraHintStarted() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onLeftAffordanceHintStarted() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onScreenTurningOn() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onScreenOff() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onNotificatonStopDismissing() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onNotificationDismissed() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onNotificatonStartDismissing() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onNotificationDoubleTap(boolean accepted, float dx, float dy) {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onBouncerShown() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onBouncerHidden() {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void onTouchEvent(MotionEvent ev, int width, int height) {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void dump(PrintWriter pw) {
    }

    @Override // com.android.systemui.plugins.FalsingManager
    public void cleanup() {
    }
}
