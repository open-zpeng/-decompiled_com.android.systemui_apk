package com.xiaopeng.systemui.infoflow.message.util;

import android.content.Context;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.egg.utils.TimeUtils;
/* loaded from: classes24.dex */
public class TimeUtil {
    public static String getCardElapsedTimeDes(Context context, long elapsedTimeMills) {
        long hour = elapsedTimeMills / TimeUtils.TIME_ONE_HOUR;
        long minutes = elapsedTimeMills % TimeUtils.TIME_ONE_HOUR;
        long minute = minutes / TimeUtils.TIME_ONE_MINUTE;
        if (hour > 0) {
            return context.getString(R.string.passed_time_hour, Integer.valueOf((int) hour));
        }
        if (minute > 0) {
            return context.getString(R.string.passed_time_minute, Integer.valueOf((int) minute));
        }
        return context.getString(R.string.passed_time_just_now);
    }
}
