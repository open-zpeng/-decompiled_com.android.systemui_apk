package com.xiaopeng.systemui.helper;

import android.content.Context;
import android.support.v4.media.subtitle.Cea708CCParser;
import com.android.systemui.R;
/* loaded from: classes24.dex */
public class TelephonyHelper {
    public static final int NETWORK_TYPE_EDGE = 2;
    public static final int NETWORK_TYPE_GPRS = 1;
    public static final int NETWORK_TYPE_HSPA = 3;
    public static final int NETWORK_TYPE_LTE = 4;
    public static final int NETWORK_TYPE_NR = 5;
    public static final int NETWORK_TYPE_OUT_OF_DATA = -2;
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    public static final int SIGNAL_LEVEL_0 = 0;
    public static final int SIGNAL_LEVEL_1 = 1;
    public static final int SIGNAL_LEVEL_2 = 2;
    public static final int SIGNAL_LEVEL_3 = 3;
    public static final int SIGNAL_LEVEL_4 = 4;
    public static final int SIGNAL_LEVEL_5 = 5;
    public static final int SIGNAL_LEVEL_INVALID = -1;

    public static int getSignalLevelByRssi(int apn1, int rssi) {
        int level;
        if (1 == 0) {
            return 0;
        }
        if (rssi <= -109 || rssi == -1) {
            level = 0;
        } else if (rssi >= -89) {
            level = 5;
        } else if (rssi >= -97) {
            level = 4;
        } else if (rssi >= -103) {
            level = 3;
        } else if (rssi >= -109) {
            level = 2;
        } else {
            level = 1;
        }
        if (apn1 == 0) {
            return -1;
        }
        return level;
    }

    public static String getNetworkTypeLabel(Context context, int type) {
        int resid = R.string.sysbar_network_type_unknown;
        if (type == 0) {
            resid = R.string.sysbar_network_type_unknown;
        } else if (type == 1) {
            resid = R.string.sysbar_network_type_gprs;
        } else if (type == 2) {
            resid = R.string.sysbar_network_type_edge;
        } else if (type == 3) {
            resid = R.string.sysbar_network_type_hspa;
        } else if (type == 4) {
            resid = R.string.sysbar_network_type_lte;
        }
        return context.getText(resid).toString();
    }

    public static int getNetworkTypeImgResourceId(int type) {
        if (type != -2) {
            if (type != 0) {
                if (type == 1 || type == 2) {
                    return R.drawable.ic_sysbar_signal_e;
                }
                if (type != 3) {
                    if (type != 4) {
                        if (type != 5) {
                            return R.drawable.ic_sysbar_signal_noservice;
                        }
                        return R.drawable.ic_sysbar_signal_5g;
                    }
                    return R.drawable.ic_sysbar_signal_4g;
                }
                return R.drawable.ic_sysbar_signal_3g;
            }
            return R.drawable.ic_sysbar_signal_noservice;
        }
        return R.drawable.ic_sysbar_out_of_data;
    }

    public static boolean isNetworkTypeValid(int type) {
        if (type == 1 || type == 2 || type == 3 || type == 4) {
            return true;
        }
        return false;
    }

    public static int getSignalLevelByRsrp(int apn1, int rsrp) {
        int level = 0;
        if (1 == 0) {
            return 0;
        }
        if (rsrp > -44) {
            level = 0;
        } else if (rsrp >= -85) {
            level = 5;
        } else if (rsrp >= -95) {
            level = 4;
        } else if (rsrp >= -105) {
            level = 3;
        } else if (rsrp >= -115) {
            level = 2;
        } else if (rsrp >= -140) {
            level = 1;
        }
        if (apn1 == 0) {
            return -1;
        }
        return level;
    }

    public int getGsmDbm(int asu) {
        int asu2 = asu == 99 ? -1 : asu;
        if (asu2 != -1) {
            int dBm = (asu2 * 2) - 113;
            return dBm;
        }
        return -1;
    }

    public int getLteAsuLevel(int dbm) {
        if (dbm == Integer.MAX_VALUE) {
            return 255;
        }
        int lteAsuLevel = dbm + Cea708CCParser.Const.CODE_C1_DLW;
        return lteAsuLevel;
    }
}
