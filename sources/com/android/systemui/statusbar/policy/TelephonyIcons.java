package com.android.systemui.statusbar.policy;

import com.android.systemui.R;
import com.android.systemui.statusbar.policy.MobileSignalController;
import java.util.HashMap;
import java.util.Map;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes21.dex */
public class TelephonyIcons {
    static final int FLIGHT_MODE_ICON = R.drawable.stat_sys_airplane_mode;
    static final int ICON_LTE = R.drawable.ic_lte_mobiledata;
    static final int ICON_LTE_PLUS = R.drawable.ic_lte_plus_mobiledata;
    static final int ICON_G = R.drawable.ic_g_mobiledata;
    static final int ICON_E = R.drawable.ic_e_mobiledata;
    static final int ICON_H = R.drawable.ic_h_mobiledata;
    static final int ICON_H_PLUS = R.drawable.ic_h_plus_mobiledata;
    static final int ICON_3G = R.drawable.ic_3g_mobiledata;
    static final int ICON_4G = R.drawable.ic_4g_mobiledata;
    static final int ICON_4G_PLUS = R.drawable.ic_4g_plus_mobiledata;
    static final int ICON_5G_E = R.drawable.ic_5g_e_mobiledata;
    static final int ICON_1X = R.drawable.ic_1x_mobiledata;
    static final int ICON_5G = R.drawable.ic_5g_mobiledata;
    static final int ICON_5G_PLUS = R.drawable.ic_5g_plus_mobiledata;
    static final MobileSignalController.MobileIconGroup CARRIER_NETWORK_CHANGE = new MobileSignalController.MobileIconGroup("CARRIER_NETWORK_CHANGE", null, null, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 0, 0, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], R.string.carrier_network_change_mode, 0, false);
    static final MobileSignalController.MobileIconGroup THREE_G = new MobileSignalController.MobileIconGroup("3G", null, null, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 0, 0, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], R.string.data_connection_3g, ICON_3G, true);
    static final MobileSignalController.MobileIconGroup WFC = new MobileSignalController.MobileIconGroup("WFC", null, null, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 0, 0, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], 0, 0, false);
    static final MobileSignalController.MobileIconGroup UNKNOWN = new MobileSignalController.MobileIconGroup("Unknown", null, null, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 0, 0, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], 0, 0, false);
    static final MobileSignalController.MobileIconGroup E = new MobileSignalController.MobileIconGroup("E", null, null, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 0, 0, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], R.string.data_connection_edge, ICON_E, false);
    static final MobileSignalController.MobileIconGroup ONE_X = new MobileSignalController.MobileIconGroup("1X", null, null, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 0, 0, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], R.string.data_connection_cdma, ICON_1X, true);
    static final MobileSignalController.MobileIconGroup G = new MobileSignalController.MobileIconGroup("G", null, null, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 0, 0, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], R.string.data_connection_gprs, ICON_G, false);
    static final MobileSignalController.MobileIconGroup H = new MobileSignalController.MobileIconGroup("H", null, null, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 0, 0, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], R.string.data_connection_3_5g, ICON_H, false);
    static final MobileSignalController.MobileIconGroup H_PLUS = new MobileSignalController.MobileIconGroup("H+", null, null, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 0, 0, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], R.string.data_connection_3_5g_plus, ICON_H_PLUS, false);
    static final MobileSignalController.MobileIconGroup FOUR_G = new MobileSignalController.MobileIconGroup("4G", null, null, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 0, 0, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], R.string.data_connection_4g, ICON_4G, true);
    static final MobileSignalController.MobileIconGroup FOUR_G_PLUS = new MobileSignalController.MobileIconGroup("4G+", null, null, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 0, 0, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], R.string.data_connection_4g_plus, ICON_4G_PLUS, true);
    static final MobileSignalController.MobileIconGroup LTE = new MobileSignalController.MobileIconGroup("LTE", null, null, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 0, 0, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], R.string.data_connection_lte, ICON_LTE, true);
    static final MobileSignalController.MobileIconGroup LTE_PLUS = new MobileSignalController.MobileIconGroup("LTE+", null, null, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 0, 0, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], R.string.data_connection_lte_plus, ICON_LTE_PLUS, true);
    static final MobileSignalController.MobileIconGroup LTE_CA_5G_E = new MobileSignalController.MobileIconGroup("5Ge", null, null, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 0, 0, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], R.string.data_connection_5ge_html, ICON_5G_E, true);
    static final MobileSignalController.MobileIconGroup NR_5G = new MobileSignalController.MobileIconGroup("5G", null, null, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 0, 0, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], R.string.data_connection_5g, ICON_5G, true);
    static final MobileSignalController.MobileIconGroup NR_5G_PLUS = new MobileSignalController.MobileIconGroup("5G_PLUS", null, null, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 0, 0, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], R.string.data_connection_5g_plus, ICON_5G_PLUS, true);
    static final MobileSignalController.MobileIconGroup DATA_DISABLED = new MobileSignalController.MobileIconGroup("DataDisabled", null, null, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 0, 0, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], R.string.cell_data_off_content_description, 0, false);
    static final MobileSignalController.MobileIconGroup NOT_DEFAULT_DATA = new MobileSignalController.MobileIconGroup("NotDefaultData", null, null, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH, 0, 0, 0, 0, AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH[0], R.string.not_default_data_content_description, 0, false);
    static final Map<String, MobileSignalController.MobileIconGroup> ICON_NAME_TO_ICON = new HashMap();

    TelephonyIcons() {
    }

    static {
        ICON_NAME_TO_ICON.put("carrier_network_change", CARRIER_NETWORK_CHANGE);
        ICON_NAME_TO_ICON.put("3g", THREE_G);
        ICON_NAME_TO_ICON.put("wfc", WFC);
        ICON_NAME_TO_ICON.put("unknown", UNKNOWN);
        ICON_NAME_TO_ICON.put("e", E);
        ICON_NAME_TO_ICON.put("1x", ONE_X);
        ICON_NAME_TO_ICON.put("g", G);
        ICON_NAME_TO_ICON.put("h", H);
        ICON_NAME_TO_ICON.put("h+", H_PLUS);
        ICON_NAME_TO_ICON.put("4g", FOUR_G);
        ICON_NAME_TO_ICON.put("4g+", FOUR_G_PLUS);
        ICON_NAME_TO_ICON.put("5ge", LTE_CA_5G_E);
        ICON_NAME_TO_ICON.put("lte", LTE);
        ICON_NAME_TO_ICON.put("lte+", LTE_PLUS);
        ICON_NAME_TO_ICON.put("5g", NR_5G);
        ICON_NAME_TO_ICON.put("5g_plus", NR_5G_PLUS);
        ICON_NAME_TO_ICON.put("datadisable", DATA_DISABLED);
        ICON_NAME_TO_ICON.put("notdefaultdata", NOT_DEFAULT_DATA);
    }
}
