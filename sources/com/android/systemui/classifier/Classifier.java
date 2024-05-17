package com.android.systemui.classifier;

import android.hardware.SensorEvent;
import android.view.MotionEvent;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes21.dex */
public abstract class Classifier {
    public static final int BOUNCER_UNLOCK = 8;
    public static final int GENERIC = 7;
    public static final int LEFT_AFFORDANCE = 5;
    public static final int NOTIFICATION_DISMISS = 1;
    public static final int NOTIFICATION_DOUBLE_TAP = 3;
    public static final int NOTIFICATION_DRAG_DOWN = 2;
    public static final int PULSE_EXPAND = 9;
    public static final int QUICK_SETTINGS = 0;
    public static final int RIGHT_AFFORDANCE = 6;
    public static final int UNLOCK = 4;
    protected ClassifierData mClassifierData;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes21.dex */
    public @interface InteractionType {
    }

    public abstract String getTag();

    public void onTouchEvent(MotionEvent event) {
    }

    public void onSensorChanged(SensorEvent event) {
    }
}
