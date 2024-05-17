package com.android.systemui.statusbar;
/* loaded from: classes21.dex */
public class StatusBarState {
    public static final int FULLSCREEN_USER_SWITCHER = 3;
    public static final int KEYGUARD = 1;
    public static final int SHADE = 0;
    public static final int SHADE_LOCKED = 2;

    public static String toShortString(int x) {
        if (x != 0) {
            if (x != 1) {
                if (x != 2) {
                    if (x == 3) {
                        return "FS_USRSW";
                    }
                    return "bad_value_" + x;
                }
                return "SHD_LCK";
            }
            return "KGRD";
        }
        return "SHD";
    }
}
