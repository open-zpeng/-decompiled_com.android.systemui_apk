package com.android.systemui.volume;

import android.view.View;
/* loaded from: classes21.dex */
class Util extends com.android.settingslib.volume.Util {
    Util() {
    }

    public static String logTag(Class<?> c) {
        String tag = "vol." + c.getSimpleName();
        return tag.length() < 23 ? tag : tag.substring(0, 23);
    }

    public static String ringerModeToString(int ringerMode) {
        if (ringerMode != 0) {
            if (ringerMode != 1) {
                if (ringerMode == 2) {
                    return "RINGER_MODE_NORMAL";
                }
                return "RINGER_MODE_UNKNOWN_" + ringerMode;
            }
            return "RINGER_MODE_VIBRATE";
        }
        return "RINGER_MODE_SILENT";
    }

    public static final void setVisOrGone(View v, boolean vis) {
        if (v != null) {
            if ((v.getVisibility() == 0) == vis) {
                return;
            }
            v.setVisibility(vis ? 0 : 8);
        }
    }
}
