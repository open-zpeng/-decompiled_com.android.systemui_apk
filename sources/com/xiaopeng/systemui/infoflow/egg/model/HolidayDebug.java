package com.xiaopeng.systemui.infoflow.egg.model;

import android.os.SystemProperties;
/* loaded from: classes24.dex */
public class HolidayDebug {
    public static boolean sDebugNoRename = SystemProperties.getBoolean("ersist.sys.assistant.holiday.debug", false);
    public static boolean sDebugSequenceData = sDebugNoRename;
}
