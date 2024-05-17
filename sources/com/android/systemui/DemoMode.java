package com.android.systemui;

import android.os.Bundle;
/* loaded from: classes21.dex */
public interface DemoMode {
    public static final String ACTION_DEMO = "com.android.systemui.demo";
    public static final String COMMAND_BARS = "bars";
    public static final String COMMAND_BATTERY = "battery";
    public static final String COMMAND_CLOCK = "clock";
    public static final String COMMAND_ENTER = "enter";
    public static final String COMMAND_EXIT = "exit";
    public static final String COMMAND_NETWORK = "network";
    public static final String COMMAND_NOTIFICATIONS = "notifications";
    public static final String COMMAND_OPERATOR = "operator";
    public static final String COMMAND_STATUS = "status";
    public static final String COMMAND_VOLUME = "volume";
    public static final String DEMO_MODE_ALLOWED = "sysui_demo_allowed";
    public static final String EXTRA_COMMAND = "command";

    void dispatchDemoCommand(String str, Bundle bundle);
}
