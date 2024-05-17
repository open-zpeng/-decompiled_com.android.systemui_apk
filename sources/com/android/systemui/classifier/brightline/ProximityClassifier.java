package com.android.systemui.classifier.brightline;

import android.provider.DeviceConfig;
import android.view.MotionEvent;
import com.android.systemui.util.ProximitySensor;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes21.dex */
public class ProximityClassifier extends FalsingClassifier {
    private static final float PERCENT_COVERED_THRESHOLD = 0.1f;
    private final DistanceClassifier mDistanceClassifier;
    private long mGestureStartTimeNs;
    private boolean mNear;
    private long mNearDurationNs;
    private final float mPercentCoveredThreshold;
    private float mPercentNear;
    private long mPrevNearTimeNs;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ProximityClassifier(DistanceClassifier distanceClassifier, FalsingDataProvider dataProvider) {
        super(dataProvider);
        this.mDistanceClassifier = distanceClassifier;
        this.mPercentCoveredThreshold = DeviceConfig.getFloat("systemui", "brightline_falsing_proximity_percent_covered_threshold", 0.1f);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.systemui.classifier.brightline.FalsingClassifier
    public void onSessionStarted() {
        this.mPrevNearTimeNs = 0L;
        this.mPercentNear = 0.0f;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.systemui.classifier.brightline.FalsingClassifier
    public void onSessionEnded() {
        this.mPrevNearTimeNs = 0L;
        this.mPercentNear = 0.0f;
    }

    @Override // com.android.systemui.classifier.brightline.FalsingClassifier
    public void onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getActionMasked();
        if (action == 0) {
            this.mGestureStartTimeNs = motionEvent.getEventTimeNano();
            if (this.mPrevNearTimeNs > 0) {
                this.mPrevNearTimeNs = motionEvent.getEventTimeNano();
            }
            logDebug("Gesture start time: " + this.mGestureStartTimeNs);
            this.mNearDurationNs = 0L;
        }
        if (action == 1 || action == 3) {
            update(this.mNear, motionEvent.getEventTimeNano());
            long duration = motionEvent.getEventTimeNano() - this.mGestureStartTimeNs;
            logDebug("Gesture duration, Proximity duration: " + duration + ", " + this.mNearDurationNs);
            if (duration == 0) {
                this.mPercentNear = this.mNear ? 1.0f : 0.0f;
            } else {
                this.mPercentNear = ((float) this.mNearDurationNs) / ((float) duration);
            }
        }
    }

    @Override // com.android.systemui.classifier.brightline.FalsingClassifier
    public void onProximityEvent(ProximitySensor.ProximityEvent proximityEvent) {
        boolean near = proximityEvent.getNear();
        long timestampNs = proximityEvent.getTimestampNs();
        logDebug("Sensor is: " + near + " at time " + timestampNs);
        update(near, timestampNs);
    }

    @Override // com.android.systemui.classifier.brightline.FalsingClassifier
    public boolean isFalseTouch() {
        if (getInteractionType() == 0) {
            return false;
        }
        logInfo("Percent of gesture in proximity: " + this.mPercentNear);
        if (this.mPercentNear > this.mPercentCoveredThreshold) {
            return !this.mDistanceClassifier.isLongSwipe();
        }
        return false;
    }

    private void update(boolean near, long timeStampNs) {
        long j = this.mPrevNearTimeNs;
        if (j != 0 && timeStampNs > j && this.mNear) {
            this.mNearDurationNs += timeStampNs - j;
            logDebug("Updating duration: " + this.mNearDurationNs);
        }
        if (near) {
            logDebug("Set prevNearTimeNs: " + timeStampNs);
            this.mPrevNearTimeNs = timeStampNs;
        }
        this.mNear = near;
    }
}
