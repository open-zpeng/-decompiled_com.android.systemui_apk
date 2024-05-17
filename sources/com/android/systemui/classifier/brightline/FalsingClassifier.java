package com.android.systemui.classifier.brightline;

import android.view.MotionEvent;
import com.android.systemui.util.ProximitySensor;
import java.util.List;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes21.dex */
public abstract class FalsingClassifier {
    private final FalsingDataProvider mDataProvider;

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract boolean isFalseTouch();

    /* JADX INFO: Access modifiers changed from: package-private */
    public FalsingClassifier(FalsingDataProvider dataProvider) {
        this.mDataProvider = dataProvider;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<MotionEvent> getRecentMotionEvents() {
        return this.mDataProvider.getRecentMotionEvents();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MotionEvent getFirstMotionEvent() {
        return this.mDataProvider.getFirstRecentMotionEvent();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MotionEvent getLastMotionEvent() {
        return this.mDataProvider.getLastMotionEvent();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isHorizontal() {
        return this.mDataProvider.isHorizontal();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isRight() {
        return this.mDataProvider.isRight();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isVertical() {
        return this.mDataProvider.isVertical();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isUp() {
        return this.mDataProvider.isUp();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float getAngle() {
        return this.mDataProvider.getAngle();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getWidthPixels() {
        return this.mDataProvider.getWidthPixels();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getHeightPixels() {
        return this.mDataProvider.getHeightPixels();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float getXdpi() {
        return this.mDataProvider.getXdpi();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float getYdpi() {
        return this.mDataProvider.getYdpi();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int getInteractionType() {
        return this.mDataProvider.getInteractionType();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setInteractionType(int interactionType) {
        this.mDataProvider.setInteractionType(interactionType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onTouchEvent(MotionEvent motionEvent) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onProximityEvent(ProximitySensor.ProximityEvent proximityEvent) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onSessionStarted() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onSessionEnded() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void logDebug(String msg) {
        BrightLineFalsingManager.logDebug(msg);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void logInfo(String msg) {
        BrightLineFalsingManager.logInfo(msg);
    }

    static void logError(String msg) {
        BrightLineFalsingManager.logError(msg);
    }
}
