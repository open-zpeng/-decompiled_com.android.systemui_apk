package com.android.systemui;

import android.util.EventLog;
/* loaded from: classes21.dex */
public class EventLogTags {
    public static final int SYSUI_FULLSCREEN_NOTIFICATION = 36002;
    public static final int SYSUI_HEADS_UP_ESCALATION = 36003;
    public static final int SYSUI_HEADS_UP_STATUS = 36001;
    public static final int SYSUI_LOCKSCREEN_GESTURE = 36021;
    public static final int SYSUI_NOTIFICATIONPANEL_TOUCH = 36020;
    public static final int SYSUI_PANELBAR_TOUCH = 36010;
    public static final int SYSUI_PANELHOLDER_TOUCH = 36040;
    public static final int SYSUI_QUICKPANEL_TOUCH = 36030;
    public static final int SYSUI_RECENTS_CONNECTION = 36060;
    public static final int SYSUI_SEARCHPANEL_TOUCH = 36050;
    public static final int SYSUI_STATUSBAR_TOUCH = 36000;
    public static final int SYSUI_STATUS_BAR_STATE = 36004;

    private EventLogTags() {
    }

    public static void writeSysuiStatusbarTouch(int type, int x, int y, int disable1, int disable2) {
        EventLog.writeEvent((int) SYSUI_STATUSBAR_TOUCH, Integer.valueOf(type), Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(disable1), Integer.valueOf(disable2));
    }

    public static void writeSysuiHeadsUpStatus(String key, int visible) {
        EventLog.writeEvent((int) SYSUI_HEADS_UP_STATUS, key, Integer.valueOf(visible));
    }

    public static void writeSysuiFullscreenNotification(String key) {
        EventLog.writeEvent((int) SYSUI_FULLSCREEN_NOTIFICATION, key);
    }

    public static void writeSysuiHeadsUpEscalation(String key) {
        EventLog.writeEvent(36003, key);
    }

    public static void writeSysuiStatusBarState(int state, int keyguardshowing, int keyguardoccluded, int bouncershowing, int secure, int currentlyinsecure) {
        EventLog.writeEvent(36004, Integer.valueOf(state), Integer.valueOf(keyguardshowing), Integer.valueOf(keyguardoccluded), Integer.valueOf(bouncershowing), Integer.valueOf(secure), Integer.valueOf(currentlyinsecure));
    }

    public static void writeSysuiPanelbarTouch(int type, int x, int y, int enabled) {
        EventLog.writeEvent(36010, Integer.valueOf(type), Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(enabled));
    }

    public static void writeSysuiNotificationpanelTouch(int type, int x, int y) {
        EventLog.writeEvent((int) SYSUI_NOTIFICATIONPANEL_TOUCH, Integer.valueOf(type), Integer.valueOf(x), Integer.valueOf(y));
    }

    public static void writeSysuiLockscreenGesture(int type, int lengthdp, int velocitydp) {
        EventLog.writeEvent((int) SYSUI_LOCKSCREEN_GESTURE, Integer.valueOf(type), Integer.valueOf(lengthdp), Integer.valueOf(velocitydp));
    }

    public static void writeSysuiQuickpanelTouch(int type, int x, int y) {
        EventLog.writeEvent((int) SYSUI_QUICKPANEL_TOUCH, Integer.valueOf(type), Integer.valueOf(x), Integer.valueOf(y));
    }

    public static void writeSysuiPanelholderTouch(int type, int x, int y) {
        EventLog.writeEvent((int) SYSUI_PANELHOLDER_TOUCH, Integer.valueOf(type), Integer.valueOf(x), Integer.valueOf(y));
    }

    public static void writeSysuiSearchpanelTouch(int type, int x, int y) {
        EventLog.writeEvent(36050, Integer.valueOf(type), Integer.valueOf(x), Integer.valueOf(y));
    }

    public static void writeSysuiRecentsConnection(int type, int user) {
        EventLog.writeEvent((int) SYSUI_RECENTS_CONNECTION, Integer.valueOf(type), Integer.valueOf(user));
    }
}
