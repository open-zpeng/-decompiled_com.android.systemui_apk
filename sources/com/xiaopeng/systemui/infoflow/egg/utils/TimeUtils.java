package com.xiaopeng.systemui.infoflow.egg.utils;

import android.text.TextUtils;
import com.xiaopeng.lib.utils.LogUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
/* loaded from: classes24.dex */
public class TimeUtils {
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
    public static final long TIME_DAY_TO_HOUR = 24;
    public static final long TIME_HOUR_TO_MINUTE = 60;
    public static final long TIME_MINUTE_TO_SECOND = 60;
    public static final long TIME_ONE_DAY = 86400000;
    public static final long TIME_ONE_HOUR = 3600000;
    public static final long TIME_ONE_MINUTE = 60000;
    public static final long TIME_ONE_SECOND = 1000;

    public static final String timestamp2SimpleDate(long timestamp) {
        return SIMPLE_DATE_FORMAT.format(new Date(timestamp));
    }

    public static final long simpleDate2Timestamp(String date) {
        if (TextUtils.isEmpty(date)) {
            return 0L;
        }
        try {
            return SIMPLE_DATE_FORMAT.parse(date).getTime();
        } catch (ParseException e) {
            LogUtils.e("TimeUtils", "simpleDate2Timestamp:" + date, e);
            return 0L;
        }
    }

    public static boolean isSameDay(long currentMillisTime, long designatedTime) {
        String currentDate = SIMPLE_DATE_FORMAT.format(new Date(currentMillisTime));
        String designatedDate = SIMPLE_DATE_FORMAT.format(new Date(designatedTime));
        return currentDate.equals(designatedDate);
    }

    public static boolean isSameDay(long designatedTime) {
        long currentMillisTime = System.currentTimeMillis();
        return isSameDay(currentMillisTime, designatedTime);
    }
}
