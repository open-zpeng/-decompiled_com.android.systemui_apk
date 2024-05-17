package com.android.systemui.classifier.brightline;

import android.util.DisplayMetrics;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes21.dex */
public class FalsingDataProvider {
    private static final long MOTION_EVENT_AGE_MS = 1000;
    private static final float THREE_HUNDRED_SIXTY_DEG = 6.2831855f;
    private MotionEvent mFirstActualMotionEvent;
    private MotionEvent mFirstRecentMotionEvent;
    private final int mHeightPixels;
    private int mInteractionType;
    private MotionEvent mLastMotionEvent;
    private final int mWidthPixels;
    private final float mXdpi;
    private final float mYdpi;
    private final TimeLimitedMotionEventBuffer mRecentMotionEvents = new TimeLimitedMotionEventBuffer(1000);
    private boolean mDirty = true;
    private float mAngle = 0.0f;

    public FalsingDataProvider(DisplayMetrics displayMetrics) {
        this.mXdpi = displayMetrics.xdpi;
        this.mYdpi = displayMetrics.ydpi;
        this.mWidthPixels = displayMetrics.widthPixels;
        this.mHeightPixels = displayMetrics.heightPixels;
        FalsingClassifier.logInfo("xdpi, ydpi: " + getXdpi() + ", " + getYdpi());
        FalsingClassifier.logInfo("width, height: " + getWidthPixels() + ", " + getHeightPixels());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onMotionEvent(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0) {
            this.mFirstActualMotionEvent = motionEvent;
        }
        List<MotionEvent> motionEvents = unpackMotionEvent(motionEvent);
        FalsingClassifier.logDebug("Unpacked into: " + motionEvents.size());
        if (motionEvent.getActionMasked() == 0) {
            this.mRecentMotionEvents.clear();
        }
        this.mRecentMotionEvents.addAll(motionEvents);
        FalsingClassifier.logDebug("Size: " + this.mRecentMotionEvents.size());
        this.mDirty = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getWidthPixels() {
        return this.mWidthPixels;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getHeightPixels() {
        return this.mHeightPixels;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float getXdpi() {
        return this.mXdpi;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float getYdpi() {
        return this.mYdpi;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<MotionEvent> getRecentMotionEvents() {
        return this.mRecentMotionEvents;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setInteractionType(int interactionType) {
        this.mInteractionType = interactionType;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final int getInteractionType() {
        return this.mInteractionType;
    }

    MotionEvent getFirstActualMotionEvent() {
        return this.mFirstActualMotionEvent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MotionEvent getFirstRecentMotionEvent() {
        recalculateData();
        return this.mFirstRecentMotionEvent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MotionEvent getLastMotionEvent() {
        recalculateData();
        return this.mLastMotionEvent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float getAngle() {
        recalculateData();
        return this.mAngle;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isHorizontal() {
        recalculateData();
        return !this.mRecentMotionEvents.isEmpty() && Math.abs(this.mFirstRecentMotionEvent.getX() - this.mLastMotionEvent.getX()) > Math.abs(this.mFirstRecentMotionEvent.getY() - this.mLastMotionEvent.getY());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isRight() {
        recalculateData();
        return !this.mRecentMotionEvents.isEmpty() && this.mLastMotionEvent.getX() > this.mFirstRecentMotionEvent.getX();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isVertical() {
        return !isHorizontal();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isUp() {
        recalculateData();
        return !this.mRecentMotionEvents.isEmpty() && this.mLastMotionEvent.getY() < this.mFirstRecentMotionEvent.getY();
    }

    private void recalculateData() {
        if (!this.mDirty) {
            return;
        }
        if (this.mRecentMotionEvents.isEmpty()) {
            this.mFirstRecentMotionEvent = null;
            this.mLastMotionEvent = null;
        } else {
            this.mFirstRecentMotionEvent = this.mRecentMotionEvents.get(0);
            TimeLimitedMotionEventBuffer timeLimitedMotionEventBuffer = this.mRecentMotionEvents;
            this.mLastMotionEvent = timeLimitedMotionEventBuffer.get(timeLimitedMotionEventBuffer.size() - 1);
        }
        calculateAngleInternal();
        this.mDirty = false;
    }

    private void calculateAngleInternal() {
        if (this.mRecentMotionEvents.size() < 2) {
            this.mAngle = Float.MAX_VALUE;
            return;
        }
        float lastX = this.mLastMotionEvent.getX() - this.mFirstRecentMotionEvent.getX();
        float lastY = this.mLastMotionEvent.getY() - this.mFirstRecentMotionEvent.getY();
        this.mAngle = (float) Math.atan2(lastY, lastX);
        while (true) {
            float f = this.mAngle;
            if (f >= 0.0f) {
                break;
            }
            this.mAngle = f + 6.2831855f;
        }
        while (true) {
            float f2 = this.mAngle;
            if (f2 > 6.2831855f) {
                this.mAngle = f2 - 6.2831855f;
            } else {
                return;
            }
        }
    }

    private List<MotionEvent> unpackMotionEvent(MotionEvent motionEvent) {
        List<MotionEvent> motionEvents = new ArrayList<>();
        List<MotionEvent.PointerProperties> pointerPropertiesList = new ArrayList<>();
        int pointerCount = motionEvent.getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            MotionEvent.PointerProperties pointerProperties = new MotionEvent.PointerProperties();
            motionEvent.getPointerProperties(i, pointerProperties);
            pointerPropertiesList.add(pointerProperties);
        }
        MotionEvent.PointerProperties[] pointerPropertiesArray = new MotionEvent.PointerProperties[pointerPropertiesList.size()];
        pointerPropertiesList.toArray(pointerPropertiesArray);
        int historySize = motionEvent.getHistorySize();
        int i2 = 0;
        while (i2 < historySize) {
            List<MotionEvent.PointerCoords> pointerCoordsList = new ArrayList<>();
            for (int j = 0; j < pointerCount; j++) {
                MotionEvent.PointerCoords pointerCoords = new MotionEvent.PointerCoords();
                motionEvent.getHistoricalPointerCoords(j, i2, pointerCoords);
                pointerCoordsList.add(pointerCoords);
            }
            motionEvents.add(MotionEvent.obtain(motionEvent.getDownTime(), motionEvent.getHistoricalEventTime(i2), motionEvent.getAction(), pointerCount, pointerPropertiesArray, (MotionEvent.PointerCoords[]) pointerCoordsList.toArray(new MotionEvent.PointerCoords[0]), motionEvent.getMetaState(), motionEvent.getButtonState(), motionEvent.getXPrecision(), motionEvent.getYPrecision(), motionEvent.getDeviceId(), motionEvent.getEdgeFlags(), motionEvent.getSource(), motionEvent.getFlags()));
            i2++;
            pointerCount = pointerCount;
            historySize = historySize;
            pointerPropertiesArray = pointerPropertiesArray;
        }
        motionEvents.add(MotionEvent.obtainNoHistory(motionEvent));
        return motionEvents;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onSessionEnd() {
        this.mFirstActualMotionEvent = null;
        Iterator<MotionEvent> it = this.mRecentMotionEvents.iterator();
        while (it.hasNext()) {
            MotionEvent ev = it.next();
            ev.recycle();
        }
        this.mRecentMotionEvents.clear();
        this.mDirty = true;
    }
}
