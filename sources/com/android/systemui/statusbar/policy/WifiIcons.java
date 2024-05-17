package com.android.systemui.statusbar.policy;

import com.android.systemui.R;
/* loaded from: classes21.dex */
public class WifiIcons {
    public static final int QS_WIFI_DISABLED = 17302852;
    public static final int QS_WIFI_NO_NETWORK = 17302852;
    static final int WIFI_NO_NETWORK = 17302852;
    static final int[] WIFI_FULL_ICONS = {17302852, 17302853, 17302854, 17302855, 17302856};
    private static final int[] WIFI_NO_INTERNET_ICONS = {R.drawable.ic_qs_wifi_0, R.drawable.ic_qs_wifi_1, R.drawable.ic_qs_wifi_2, R.drawable.ic_qs_wifi_3, R.drawable.ic_qs_wifi_4};
    public static final int[][] QS_WIFI_SIGNAL_STRENGTH = {WIFI_NO_INTERNET_ICONS, WIFI_FULL_ICONS};
    static final int[][] WIFI_SIGNAL_STRENGTH = QS_WIFI_SIGNAL_STRENGTH;
    static final int WIFI_LEVEL_COUNT = WIFI_SIGNAL_STRENGTH[0].length;
}
