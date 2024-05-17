package com.android.systemui.classifier.brightline;

import android.provider.DeviceConfig;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import java.util.List;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes21.dex */
public class DistanceClassifier extends FalsingClassifier {
    private static final float HORIZONTAL_FLING_THRESHOLD_DISTANCE_IN = 1.0f;
    private static final float HORIZONTAL_SWIPE_THRESHOLD_DISTANCE_IN = 3.0f;
    private static final float SCREEN_FRACTION_MAX_DISTANCE = 0.8f;
    private static final float VELOCITY_TO_DISTANCE = 80.0f;
    private static final float VERTICAL_FLING_THRESHOLD_DISTANCE_IN = 1.0f;
    private static final float VERTICAL_SWIPE_THRESHOLD_DISTANCE_IN = 3.0f;
    private DistanceVectors mCachedDistance;
    private boolean mDistanceDirty;
    private final float mHorizontalFlingThresholdPx;
    private final float mHorizontalSwipeThresholdPx;
    private final float mVelocityToDistanceMultiplier;
    private final float mVerticalFlingThresholdPx;
    private final float mVerticalSwipeThresholdPx;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DistanceClassifier(FalsingDataProvider dataProvider) {
        super(dataProvider);
        this.mVelocityToDistanceMultiplier = DeviceConfig.getFloat("systemui", "brightline_falsing_distance_velcoity_to_distance", (float) VELOCITY_TO_DISTANCE);
        float horizontalFlingThresholdIn = DeviceConfig.getFloat("systemui", "brightline_falsing_distance_horizontal_fling_threshold_in", 1.0f);
        float verticalFlingThresholdIn = DeviceConfig.getFloat("systemui", "brightline_falsing_distance_vertical_fling_threshold_in", 1.0f);
        float horizontalSwipeThresholdIn = DeviceConfig.getFloat("systemui", "brightline_falsing_distance_horizontal_swipe_threshold_in", 3.0f);
        float verticalSwipeThresholdIn = DeviceConfig.getFloat("systemui", "brightline_falsing_distance_horizontal_swipe_threshold_in", 3.0f);
        float screenFractionMaxDistance = DeviceConfig.getFloat("systemui", "brightline_falsing_distance_screen_fraction_max_distance", 0.8f);
        this.mHorizontalFlingThresholdPx = Math.min(getWidthPixels() * screenFractionMaxDistance, getXdpi() * horizontalFlingThresholdIn);
        this.mVerticalFlingThresholdPx = Math.min(getHeightPixels() * screenFractionMaxDistance, getYdpi() * verticalFlingThresholdIn);
        this.mHorizontalSwipeThresholdPx = Math.min(getWidthPixels() * screenFractionMaxDistance, getXdpi() * horizontalSwipeThresholdIn);
        this.mVerticalSwipeThresholdPx = Math.min(getHeightPixels() * screenFractionMaxDistance, getYdpi() * verticalSwipeThresholdIn);
        this.mDistanceDirty = true;
    }

    private DistanceVectors getDistances() {
        if (this.mDistanceDirty) {
            this.mCachedDistance = calculateDistances();
            this.mDistanceDirty = false;
        }
        return this.mCachedDistance;
    }

    private DistanceVectors calculateDistances() {
        VelocityTracker velocityTracker = VelocityTracker.obtain();
        List<MotionEvent> motionEvents = getRecentMotionEvents();
        if (motionEvents.size() < 3) {
            logDebug("Only " + motionEvents.size() + " motion events recorded.");
            return new DistanceVectors(0.0f, 0.0f, 0.0f, 0.0f);
        }
        for (MotionEvent motionEvent : motionEvents) {
            velocityTracker.addMovement(motionEvent);
        }
        velocityTracker.computeCurrentVelocity(1);
        float vX = velocityTracker.getXVelocity();
        float vY = velocityTracker.getYVelocity();
        velocityTracker.recycle();
        float dX = getLastMotionEvent().getX() - getFirstMotionEvent().getX();
        float dY = getLastMotionEvent().getY() - getFirstMotionEvent().getY();
        logInfo("dX: " + dX + " dY: " + dY + " xV: " + vX + " yV: " + vY);
        return new DistanceVectors(dX, dY, vX, vY);
    }

    @Override // com.android.systemui.classifier.brightline.FalsingClassifier
    public void onTouchEvent(MotionEvent motionEvent) {
        this.mDistanceDirty = true;
    }

    @Override // com.android.systemui.classifier.brightline.FalsingClassifier
    public boolean isFalseTouch() {
        return !getDistances().getPassedFlingThreshold();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isLongSwipe() {
        boolean longSwipe = getDistances().getPassedDistanceThreshold();
        logDebug("Is longSwipe? " + longSwipe);
        return longSwipe;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class DistanceVectors {
        final float mDx;
        final float mDy;
        private final float mVx;
        private final float mVy;

        DistanceVectors(float dX, float dY, float vX, float vY) {
            this.mDx = dX;
            this.mDy = dY;
            this.mVx = vX;
            this.mVy = vY;
        }

        boolean getPassedDistanceThreshold() {
            if (DistanceClassifier.this.isHorizontal()) {
                FalsingClassifier.logDebug("Horizontal swipe distance: " + Math.abs(this.mDx));
                FalsingClassifier.logDebug("Threshold: " + DistanceClassifier.this.mHorizontalSwipeThresholdPx);
                return Math.abs(this.mDx) >= DistanceClassifier.this.mHorizontalSwipeThresholdPx;
            }
            FalsingClassifier.logDebug("Vertical swipe distance: " + Math.abs(this.mDy));
            FalsingClassifier.logDebug("Threshold: " + DistanceClassifier.this.mVerticalSwipeThresholdPx);
            return Math.abs(this.mDy) >= DistanceClassifier.this.mVerticalSwipeThresholdPx;
        }

        boolean getPassedFlingThreshold() {
            float dX = this.mDx + (this.mVx * DistanceClassifier.this.mVelocityToDistanceMultiplier);
            float dY = this.mDy + (this.mVy * DistanceClassifier.this.mVelocityToDistanceMultiplier);
            if (DistanceClassifier.this.isHorizontal()) {
                FalsingClassifier.logDebug("Horizontal swipe and fling distance: " + this.mDx + ", " + (this.mVx * DistanceClassifier.this.mVelocityToDistanceMultiplier));
                StringBuilder sb = new StringBuilder();
                sb.append("Threshold: ");
                sb.append(DistanceClassifier.this.mHorizontalFlingThresholdPx);
                FalsingClassifier.logDebug(sb.toString());
                return Math.abs(dX) >= DistanceClassifier.this.mHorizontalFlingThresholdPx;
            }
            FalsingClassifier.logDebug("Vertical swipe and fling distance: " + this.mDy + ", " + (this.mVy * DistanceClassifier.this.mVelocityToDistanceMultiplier));
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Threshold: ");
            sb2.append(DistanceClassifier.this.mVerticalFlingThresholdPx);
            FalsingClassifier.logDebug(sb2.toString());
            return Math.abs(dY) >= DistanceClassifier.this.mVerticalFlingThresholdPx;
        }
    }
}
