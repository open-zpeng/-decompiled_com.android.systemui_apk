package com.xiaopeng.systemui.infoflow.util;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateFormat;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import com.xiaopeng.systemui.utils.Utils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
/* loaded from: classes24.dex */
public class TimeUtils {
    public static String longToString(long elapsedTime) {
        String time = "";
        long hour = elapsedTime / com.xiaopeng.systemui.infoflow.egg.utils.TimeUtils.TIME_ONE_HOUR;
        long minutes = elapsedTime % com.xiaopeng.systemui.infoflow.egg.utils.TimeUtils.TIME_ONE_HOUR;
        long minute = minutes / com.xiaopeng.systemui.infoflow.egg.utils.TimeUtils.TIME_ONE_MINUTE;
        long seconds = minutes % com.xiaopeng.systemui.infoflow.egg.utils.TimeUtils.TIME_ONE_MINUTE;
        long second = Math.round(((float) seconds) / 1000.0f);
        if (hour > 0) {
            if (hour < 10) {
                time = "0";
            }
            time = time + hour + NavigationBarInflaterView.KEY_IMAGE_DELIM;
        }
        if (minute < 10) {
            time = time + "0";
        }
        String time2 = time + minute + NavigationBarInflaterView.KEY_IMAGE_DELIM;
        if (second < 10) {
            time2 = time2 + "0";
        }
        return time2 + second;
    }

    public static String getCurrentDate() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(d);
    }

    public static long dateCompare(String s1) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date callLogTime = null;
        try {
            callLogTime = sdf.parse(s1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date today = null;
        try {
            today = sdf.parse(getCurrentDate());
        } catch (ParseException e2) {
            e2.printStackTrace();
        }
        long diff = calculateDiff(today, callLogTime);
        return diff;
    }

    public static long calculateDiff(Date date1, Date date2) {
        if (date2 == null) {
            return -1000L;
        }
        long diff = Math.abs((date2.getTime() - date1.getTime()) / com.xiaopeng.systemui.infoflow.egg.utils.TimeUtils.TIME_ONE_DAY);
        return diff;
    }

    public static String getWeeks(Resources resources, String dateTime) {
        int index = getWeeksIndex(dateTime);
        switch (index) {
            case 1:
                return resources.getString(R.string.sunday);
            case 2:
                return resources.getString(R.string.monday);
            case 3:
                return resources.getString(R.string.tuesday);
            case 4:
                return resources.getString(R.string.wednesday);
            case 5:
                return resources.getString(R.string.thursday);
            case 6:
                return resources.getString(R.string.friday);
            case 7:
                return resources.getString(R.string.saturday);
            default:
                return "";
        }
    }

    public static int getWeeksIndex(String dateTime) {
        Date date;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            date = sdf.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
            date = null;
        }
        if (date != null) {
            cal.setTime(new Date(date.getTime()));
        }
        return cal.get(7);
    }

    public static String getFormatTimeString(Context context, Date date) {
        Locale locale;
        boolean isChineseVersion = Utils.isChineseLanguage();
        if (isChineseVersion) {
            locale = Locale.getDefault();
        } else {
            locale = Locale.US;
        }
        boolean is24HourFormat = DateFormat.is24HourFormat(context);
        String timeFormat = is24HourFormat ? "HH:mm" : !isChineseVersion ? "h:mm aa" : "aah:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, locale);
        return sdf.format(date);
    }
}
