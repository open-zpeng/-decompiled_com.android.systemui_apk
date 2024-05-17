package com.android.systemui.statusbar.phone;

import android.metrics.LogMaker;
import android.util.ArrayMap;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.Dependency;
import com.android.systemui.EventLogConstants;
import com.android.systemui.EventLogTags;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class LockscreenGestureLogger {
    private LogMaker mLogMaker = new LogMaker(0).setType(4);
    private final MetricsLogger mMetricsLogger = (MetricsLogger) Dependency.get(MetricsLogger.class);
    private ArrayMap<Integer, Integer> mLegacyMap = new ArrayMap<>(EventLogConstants.METRICS_GESTURE_TYPE_MAP.length);

    @Inject
    public LockscreenGestureLogger() {
        for (int i = 0; i < EventLogConstants.METRICS_GESTURE_TYPE_MAP.length; i++) {
            this.mLegacyMap.put(Integer.valueOf(EventLogConstants.METRICS_GESTURE_TYPE_MAP[i]), Integer.valueOf(i));
        }
    }

    public void write(int gesture, int length, int velocity) {
        this.mMetricsLogger.write(this.mLogMaker.setCategory(gesture).setType(4).addTaggedData(826, Integer.valueOf(length)).addTaggedData(827, Integer.valueOf(velocity)));
        EventLogTags.writeSysuiLockscreenGesture(safeLookup(gesture), length, velocity);
    }

    public void writeAtFractionalPosition(int category, int xPercent, int yPercent, int rotation) {
        this.mMetricsLogger.write(this.mLogMaker.setCategory(category).setType(4).addTaggedData(1326, Integer.valueOf(xPercent)).addTaggedData(1327, Integer.valueOf(yPercent)).addTaggedData(1329, Integer.valueOf(rotation)));
    }

    private int safeLookup(int gesture) {
        Integer value = this.mLegacyMap.get(Integer.valueOf(gesture));
        if (value == null) {
            return 0;
        }
        return value.intValue();
    }
}
