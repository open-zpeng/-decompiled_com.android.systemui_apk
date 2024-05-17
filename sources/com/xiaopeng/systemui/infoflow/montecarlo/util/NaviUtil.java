package com.xiaopeng.systemui.infoflow.montecarlo.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import androidx.annotation.VisibleForTesting;
import com.android.systemui.R;
import com.android.systemui.util.wakelock.WakeLock;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.DecimalFormat;
/* loaded from: classes24.dex */
public class NaviUtil {
    private static final int BETWEEN_1KM_AND_100KM_INTEGRE = 2;
    private static final int BETWEEN_1KM_AND_100KM_REMAINER = 3;
    public static final int CAR_BATTERY_INVALID = -1;
    public static final int CAR_BATTERY_NORMAL = 0;
    public static final int CAR_BATTERY_RED_WARNING = 2;
    public static final int CAR_BATTERY_WARNING = 1;
    public static final int DISTANCE_120KM = 120000;
    public static final int DISTANCE_1KM = 1000;
    public static final int DISTANCE_20KM = 20000;
    public static final int DISTANCE_30KM = 30000;
    public static final int DISTANCE_60KM = 60000;
    @VisibleForTesting
    protected static final int DISTANCE_FORMAT_TYPE_BIG_NUM = 4;
    @VisibleForTesting
    protected static final int DISTANCE_FORMAT_TYPE_HEADER_ABOUT = 1;
    @VisibleForTesting
    protected static final int DISTANCE_FORMAT_TYPE_NO_HEADER = 2;
    @VisibleForTesting
    protected static final int DISTANCE_FORMAT_TYPE_NO_HEADER_ARRAY = 3;
    @VisibleForTesting
    protected static final int DISTANCE_FORMAT_TYPE_NO_SPACE = 5;
    @VisibleForTesting
    protected static final int DISTANCE_TYPE_ARRIVED_REMAIN = 2;
    @VisibleForTesting
    protected static final int DISTANCE_TYPE_NAV = 1;
    public static final int EXIT_INFO_TYPE_ENTRANCE = 2;
    private static final int LESS_THAN_1KM = 4;
    private static final int MORE_THAN_100KM = 1;
    private static final int ORANGE_REMAIN_DISTANCE = 30000;
    public static final int STATUS_DISTANCE_INVALID = -1;
    public static final int STATUS_DISTANCE_LOW_POWER = 2;
    public static final int STATUS_DISTANCE_REACHABLE = 1;
    public static final int STATUS_DISTANCE_UNREACHABLE = 0;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes24.dex */
    public @interface DistanceFormatType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes24.dex */
    public @interface DistanceRange {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes24.dex */
    public @interface DistanceType {
    }

    public static String getDistanceString(Context context, long distance) {
        return getDistanceString(context, distance);
    }

    public static String getDistanceString(Context context, String distanceDisplay, int distanceDisplayUnit) {
        return context.getResources().getString(distanceDisplayUnit == 0 ? R.string.meter : R.string.kilometer, distanceDisplay);
    }

    public static String getDistanceStringInfo(Context context, String dist, int unit) {
        if (unit == 0) {
            String distInfo = String.format(context.getString(R.string.meter), dist);
            return distInfo;
        } else if (unit != 1) {
            return "";
        } else {
            String distInfo2 = String.format(context.getString(R.string.kilometer), dist);
            return distInfo2;
        }
    }

    public static String getDistanceString(Context context, double distance) {
        if (distance < 1000.0d) {
            return context.getResources().getString(R.string.meter, String.format("%.0f", Double.valueOf(distance)));
        }
        double distancekm = distance / 1000.0d;
        if (distance < 100.0d) {
            return context.getResources().getString(R.string.kilometer, String.format("%.1f", Double.valueOf(distancekm)));
        }
        return context.getResources().getString(R.string.kilometer, String.format("%.0f", Double.valueOf(distancekm)));
    }

    public static CharSequence getDistanceSpannableStringWithUnit(Context context, String distance, int unitType, int numSize, int unitSize) {
        SpannableString disSpannableString = new SpannableString(distance);
        disSpannableString.setSpan(new AbsoluteSizeSpan(numSize), 0, distance.length(), 17);
        String unitString = unitType == 0 ? context.getString(R.string.meter_unit) : context.getString(R.string.kilometer_unit);
        SpannableString unitSpannableString = new SpannableString(unitString);
        unitSpannableString.setSpan(new AbsoluteSizeSpan(unitSize), 0, unitString.length(), 17);
        return TextUtils.concat(disSpannableString, unitSpannableString);
    }

    public static SpannableString getDistanceSpannableString(String distance, int numSize, int unitSize) {
        int textLen = distance.length();
        int unitLen = 0;
        if (distance.endsWith("米")) {
            unitLen = 2;
        } else if (distance.endsWith("公里")) {
            unitLen = 3;
        }
        SpannableString spannableString = new SpannableString(distance);
        spannableString.setSpan(new AbsoluteSizeSpan(numSize), 0, textLen - unitLen, 17);
        spannableString.setSpan(new TypefaceSpan("xpeng-fonts-number"), 0, textLen - unitLen, 17);
        spannableString.setSpan(new AbsoluteSizeSpan(unitSize), textLen - unitLen, distance.length(), 17);
        return spannableString;
    }

    public static SpannableString getDistanceSpannableStringWithColor(String distance, int numSize, int unitSize, String colorString) {
        int textLen = distance.length();
        int unitLen = 0;
        if (distance.endsWith("米")) {
            unitLen = 2;
        } else if (distance.endsWith("公里")) {
            unitLen = 3;
        }
        SpannableString spannableString = new SpannableString(distance);
        spannableString.setSpan(new AbsoluteSizeSpan(numSize), 0, textLen - unitLen, 17);
        spannableString.setSpan(new TypefaceSpan("xpeng-fonts-number"), 0, textLen - unitLen, 17);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor(colorString)), 0, textLen - unitLen, 17);
        spannableString.setSpan(new AbsoluteSizeSpan(unitSize), textLen - unitLen, distance.length(), 17);
        return spannableString;
    }

    public static String getTimeString(Context context, double time) {
        double time2;
        if (time >= 0.0d) {
            time2 = time;
        } else {
            time2 = 0.0d;
        }
        long hours = (long) (time2 / 3600.0d);
        long min = (long) ((time2 % 3600.0d) / 60.0d);
        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(context.getString(R.string.hour, String.valueOf(hours)));
        }
        if (time2 >= 0.0d && min == 0 && hours == 0) {
            min = 1;
        }
        if (min > 0) {
            if (hours > 0) {
                sb.append(" ");
            }
            sb.append(context.getString(R.string.minute, String.valueOf(min)));
        }
        return sb.toString();
    }

    public static SpannableString getTimeSpannableString(String time, int numSize, int unitSize) {
        SpannableString spannableString = new SpannableString(time);
        int length = time.length();
        int hourIndex = time.indexOf("小时");
        if (time.endsWith("分")) {
            spannableString.setSpan(new AbsoluteSizeSpan(unitSize), length - 1, length, 17);
            if (hourIndex != -1) {
                spannableString.setSpan(new AbsoluteSizeSpan(numSize), 0, hourIndex, 17);
                spannableString.setSpan(new TypefaceSpan("xpeng-fonts-number"), 0, hourIndex, 17);
                spannableString.setSpan(new AbsoluteSizeSpan(unitSize), hourIndex, hourIndex + 2, 17);
                spannableString.setSpan(new AbsoluteSizeSpan(numSize), hourIndex + 2, length - 1, 17);
                spannableString.setSpan(new TypefaceSpan("xpeng-fonts-number"), hourIndex + 2, length - 1, 17);
            } else {
                spannableString.setSpan(new AbsoluteSizeSpan(numSize), 0, length - 1, 17);
                spannableString.setSpan(new TypefaceSpan("xpeng-fonts-number"), 0, length - 1, 17);
            }
        } else if (time.endsWith("小时")) {
            spannableString.setSpan(new AbsoluteSizeSpan(numSize), 0, length - 2, 17);
            spannableString.setSpan(new TypefaceSpan("xpeng-fonts-number"), 0, length - 2, 17);
            spannableString.setSpan(new AbsoluteSizeSpan(unitSize), length - 2, length, 17);
        }
        return spannableString;
    }

    public static SpannableString getNextManeuverSpannableString(String distance) {
        StringBuilder sb = new StringBuilder();
        sb.append(ContextUtils.getContext().getString(R.string.next));
        sb.append(distance);
        SpannableString spannableString = new SpannableString(sb.toString());
        sb.toString().length();
        if (!distance.endsWith("米") && distance.endsWith("公里")) {
        }
        return spannableString;
    }

    public static int checkDistanceReachableStatus(long carRemainDis, int remainDis) throws DistanceStatusIllegalArgumentException {
        if (carRemainDis < remainDis) {
            throw new DistanceStatusIllegalArgumentException("carRemainDis is greater than remainDis ");
        }
        if (carRemainDis > WakeLock.DEFAULT_MAX_TIMEOUT) {
            if (remainDis <= 1000) {
                return 0;
            }
            return 1;
        }
        return 2;
    }

    public static ColorStateList checkColorStatus(long carRemainDis, int remainDis) {
        Resources resources = ContextUtils.getContext().getResources();
        return resources.getColorStateList(getRemainDisColorStatusResId(carRemainDis, remainDis));
    }

    public static int getRemainDisColorStatusResId(long carRemainDis, int remainDis) {
        int status = 2;
        try {
            status = checkDistanceReachableStatus(carRemainDis, remainDis);
        } catch (DistanceStatusIllegalArgumentException e) {
            Log.d("ActivityThread", "INVALID remain dis and leftDistance remain dis: " + carRemainDis + " leftDis: " + remainDis);
        }
        int colorResId = ContextUtils.getColor(R.color.batter_status_normal);
        if (status != 0) {
            if (status == 1) {
                if (remainDis <= 30000) {
                    return R.color.batter_status_warning;
                }
                return R.color.color_infoflow_route_element_selector;
            } else if (status == 2) {
                if (remainDis <= 1000) {
                    return R.color.batter_status_low_power;
                }
                return R.color.batter_status_warning;
            } else {
                return colorResId;
            }
        }
        return R.color.batter_status_unreachable;
    }

    public static int checkBatteryStatus(long carRemainDis, int remainDis) {
        int status = 2;
        try {
            status = checkDistanceReachableStatus(carRemainDis, remainDis);
        } catch (DistanceStatusIllegalArgumentException e) {
            Log.e("ActivityThread", "INVALID remain dis and leftDistance remain dis: " + carRemainDis + " leftDis: " + remainDis);
        }
        if (status != 0) {
            if (status != 1) {
                if (status != 2) {
                    return 0;
                }
                return -1;
            } else if (remainDis <= 30000) {
                return 1;
            } else {
                return 0;
            }
        }
        return 2;
    }

    public static String getDistanceString(double distance) {
        return getDistanceString(distance, 2)[0];
    }

    public static String[] getDistanceString(double distance, int formatType) {
        return getDistanceString(distance, formatType, 1);
    }

    private static int getDistanceState(double distance, int distanceType) {
        if (distance >= 1000.0d) {
            if (distance >= 100000.0d) {
                return 1;
            }
            if (distanceType == 2) {
                return 2;
            }
            double f = distance / 1000.0d;
            if (((((float) Math.round(f * 10.0d)) / 10.0f) * 10.0d) % 10.0d == 0.0d) {
                return 2;
            }
            return 3;
        }
        return 4;
    }

    private static String[] getStringMoreOneKM(double f, int formatType) {
        if (formatType == 1) {
            return new String[]{ContextUtils.getString(R.string.about_km100, Integer.valueOf((int) f))};
        }
        if (formatType == 2) {
            return new String[]{ContextUtils.getString(R.string.km100, Integer.valueOf((int) f))};
        }
        if (formatType == 4) {
            return new String[]{ContextUtils.getString(R.string.km100_bignum, Integer.valueOf((int) f))};
        }
        if (formatType != 3) {
            return formatType == 5 ? new String[]{ContextUtils.getString(R.string.km100_no_space, Integer.valueOf((int) f))} : new String[]{""};
        }
        return new String[]{"" + ((int) f), ContextUtils.getString(R.string.unit_km)};
    }

    public static String[] getDistanceString(double distance, int formatType, int distanceType) {
        int state = getDistanceState(distance, distanceType);
        if (state == 4) {
            if (formatType == 1) {
                return new String[]{ContextUtils.getString(R.string.about_meters, Integer.valueOf((int) distance))};
            }
            if (formatType == 2) {
                return new String[]{ContextUtils.getString(R.string.meters, Integer.valueOf((int) distance))};
            }
            if (formatType == 4) {
                return new String[]{ContextUtils.getString(R.string.meters_bignum, Integer.valueOf((int) distance))};
            }
            if (formatType == 3) {
                return new String[]{"" + ((int) distance), ContextUtils.getString(R.string.unit_metre)};
            } else if (formatType == 5) {
                return new String[]{ContextUtils.getString(R.string.meters_no_space, Integer.valueOf((int) distance))};
            }
        } else {
            double f = ((float) Math.round(10.0d * (distance / 1000.0d))) / 10.0f;
            if (state == 1 || state == 2) {
                return getStringMoreOneKM(f, formatType);
            }
            if (state == 3) {
                if (formatType == 1) {
                    return new String[]{ContextUtils.getString(R.string.about_km, Double.valueOf(f))};
                }
                if (formatType == 2) {
                    return new String[]{ContextUtils.getString(R.string.km, Double.valueOf(f))};
                }
                if (formatType == 4) {
                    return new String[]{ContextUtils.getString(R.string.km_bignum, Double.valueOf(f))};
                }
                if (formatType == 3) {
                    return new String[]{new DecimalFormat("#.0").format(f), ContextUtils.getString(R.string.unit_km)};
                }
                if (formatType == 5) {
                    return new String[]{ContextUtils.getString(R.string.km_no_space, Double.valueOf(f))};
                }
            }
        }
        return new String[]{""};
    }

    public static SpannableString getDistanceSizedSpannedString(double distance, float valueSize, float unitSize) {
        String disString = getDistanceString(distance);
        if (TextUtils.isEmpty(disString)) {
            return null;
        }
        RelativeSizeSpan sizeSpan1 = new RelativeSizeSpan(unitSize / valueSize);
        RelativeSizeSpan sizeSpan2 = new RelativeSizeSpan(unitSize / valueSize);
        SpannableString spannableString = new SpannableString(disString);
        int kmStart = disString.indexOf("公里");
        int kmEnd = "公里".length() + kmStart;
        int metStart = disString.indexOf("米");
        int metEnd = "米".length() + metStart;
        if (kmStart > -1 && kmEnd <= disString.length()) {
            spannableString.setSpan(sizeSpan1, kmStart, kmEnd, 17);
        }
        if (metStart > -1 && metEnd <= disString.length()) {
            spannableString.setSpan(sizeSpan2, metStart, metEnd, 17);
        }
        return spannableString;
    }

    public static String getTimeXmlString(double time, int valueSize, int unitSize) {
        long hours = (long) (time / 3600.0d);
        long min = (long) ((time % 3600.0d) / 60.0d);
        StringBuilder sb = new StringBuilder();
        sb.append("<font>");
        if (hours > 0) {
            sb.append(String.format("<font><xpface value = " + ContextUtils.getString(R.string.xp_font_number) + "><xpsize value=\"" + valueSize + "\">%1$d</xpsize></xpface></font><font><xpface value = " + ContextUtils.getString(R.string.xp_font_medium) + "><xpsize value=\"" + unitSize + "\">&nbsp;" + ContextUtils.getString(R.string.hour_forxml) + "&nbsp;</xpsize></xpface></font>", Long.valueOf(hours)));
        }
        if (time > 0.0d && min == 0 && hours == 0) {
            min = 1;
        }
        if (min > 0 && hours < 100) {
            sb.append(String.format("<font><xpface value = " + ContextUtils.getString(R.string.xp_font_number) + "><xpsize value=\"" + valueSize + "\">%1$d</xpsize></xpface></font><font><xpface value = " + ContextUtils.getString(R.string.xp_font_medium) + "><xpsize value=\"" + unitSize + "\">&nbsp;" + ContextUtils.getString(R.string.minute_forxml) + "&nbsp;</xpsize></xpface></font>", Long.valueOf(min)));
        }
        sb.append("</font>");
        return sb.toString();
    }

    /* loaded from: classes24.dex */
    public static class DistanceStatusIllegalArgumentException extends Exception {
        public DistanceStatusIllegalArgumentException(String message) {
            super(message);
        }
    }
}
