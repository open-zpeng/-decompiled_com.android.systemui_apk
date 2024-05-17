package com.android.systemui.volume;

import android.content.Context;
import android.media.AudioSystem;
import android.util.Log;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.plugins.VolumeDialogController;
import com.xiaopeng.speech.protocol.node.navi.bean.NaviPreferenceBean;
import java.util.Arrays;
/* loaded from: classes21.dex */
public class Events {
    public static final int DISMISS_REASON_DONE_CLICKED = 6;
    public static final int DISMISS_REASON_OUTPUT_CHOOSER = 8;
    public static final int DISMISS_REASON_SCREEN_OFF = 4;
    public static final int DISMISS_REASON_SETTINGS_CLICKED = 5;
    public static final int DISMISS_REASON_TIMEOUT = 3;
    public static final int DISMISS_REASON_TOUCH_OUTSIDE = 1;
    public static final int DISMISS_REASON_UNKNOWN = 0;
    public static final int DISMISS_REASON_USB_OVERHEAD_ALARM_CHANGED = 9;
    public static final int DISMISS_REASON_VOLUME_CONTROLLER = 2;
    public static final int DISMISS_STREAM_GONE = 7;
    public static final int EVENT_ACTIVE_STREAM_CHANGED = 2;
    public static final int EVENT_COLLECTION_STARTED = 5;
    public static final int EVENT_COLLECTION_STOPPED = 6;
    public static final int EVENT_DISMISS_DIALOG = 1;
    public static final int EVENT_DISMISS_USB_OVERHEAT_ALARM = 20;
    public static final int EVENT_EXPAND = 3;
    public static final int EVENT_EXTERNAL_RINGER_MODE_CHANGED = 12;
    public static final int EVENT_ICON_CLICK = 7;
    public static final int EVENT_INTERNAL_RINGER_MODE_CHANGED = 11;
    public static final int EVENT_KEY = 4;
    public static final int EVENT_LEVEL_CHANGED = 10;
    public static final int EVENT_MUTE_CHANGED = 15;
    public static final int EVENT_ODI_CAPTIONS_CLICK = 21;
    public static final int EVENT_ODI_CAPTIONS_TOOLTIP_CLICK = 22;
    public static final int EVENT_RINGER_TOGGLE = 18;
    public static final int EVENT_SETTINGS_CLICK = 8;
    public static final int EVENT_SHOW_DIALOG = 0;
    public static final int EVENT_SHOW_USB_OVERHEAT_ALARM = 19;
    public static final int EVENT_SUPPRESSOR_CHANGED = 14;
    public static final int EVENT_TOUCH_LEVEL_CHANGED = 9;
    public static final int EVENT_TOUCH_LEVEL_DONE = 16;
    public static final int EVENT_ZEN_CONFIG_CHANGED = 17;
    public static final int EVENT_ZEN_MODE_CHANGED = 13;
    public static final int ICON_STATE_MUTE = 2;
    public static final int ICON_STATE_UNKNOWN = 0;
    public static final int ICON_STATE_UNMUTE = 1;
    public static final int ICON_STATE_VIBRATE = 3;
    public static final int SHOW_REASON_REMOTE_VOLUME_CHANGED = 2;
    public static final int SHOW_REASON_UNKNOWN = 0;
    public static final int SHOW_REASON_USB_OVERHEAD_ALARM_CHANGED = 3;
    public static final int SHOW_REASON_VOLUME_CHANGED = 1;
    public static Callback sCallback;
    private static final String TAG = Util.logTag(Events.class);
    private static final String[] EVENT_TAGS = {"show_dialog", "dismiss_dialog", "active_stream_changed", "expand", "key", "collection_started", "collection_stopped", "icon_click", "settings_click", "touch_level_changed", "level_changed", "internal_ringer_mode_changed", "external_ringer_mode_changed", "zen_mode_changed", "suppressor_changed", "mute_changed", "touch_level_done", "zen_mode_config_changed", "ringer_toggle", "show_usb_overheat_alarm", "dismiss_usb_overheat_alarm", "odi_captions_click", "odi_captions_tooltip_click"};
    public static final String[] DISMISS_REASONS = {"unknown", "touch_outside", "volume_controller", "timeout", "screen_off", "settings_clicked", "done_clicked", "a11y_stream_changed", "output_chooser", "usb_temperature_below_threshold"};
    public static final String[] SHOW_REASONS = {"unknown", "volume_changed", "remote_volume_changed", "usb_temperature_above_threshold"};

    /* loaded from: classes21.dex */
    public interface Callback {
        void writeEvent(long j, int i, Object[] objArr);

        void writeState(long j, VolumeDialogController.State state);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static void writeEvent(Context context, int tag, Object... list) {
        MetricsLogger logger = new MetricsLogger();
        long time = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder("writeEvent ").append(EVENT_TAGS[tag]);
        if (list != null && list.length > 0) {
            sb.append(" ");
            switch (tag) {
                case 0:
                    MetricsLogger.visible(context, 207);
                    MetricsLogger.histogram(context, "volume_from_keyguard", ((Boolean) list[1]).booleanValue() ? 1 : 0);
                    sb.append(SHOW_REASONS[((Integer) list[0]).intValue()]);
                    sb.append(" keyguard=");
                    sb.append(list[1]);
                    break;
                case 1:
                    MetricsLogger.hidden(context, 207);
                    sb.append(DISMISS_REASONS[((Integer) list[0]).intValue()]);
                    break;
                case 2:
                    MetricsLogger.action(context, (int) NaviPreferenceBean.PATH_PREF_UNPAVED, ((Integer) list[0]).intValue());
                    sb.append(AudioSystem.streamToString(((Integer) list[0]).intValue()));
                    break;
                case 3:
                    MetricsLogger.visibility(context, (int) NaviPreferenceBean.PATH_PREF_HIGHWAY, ((Boolean) list[0]).booleanValue());
                    sb.append(list[0]);
                    break;
                case 4:
                    MetricsLogger.action(context, (int) NaviPreferenceBean.PATH_PREF_AVOID_UNPAVED, ((Integer) list[0]).intValue());
                    sb.append(AudioSystem.streamToString(((Integer) list[0]).intValue()));
                    sb.append(' ');
                    sb.append(list[1]);
                    break;
                case 5:
                case 6:
                case 17:
                default:
                    sb.append(Arrays.asList(list));
                    break;
                case 7:
                    MetricsLogger.action(context, (int) NaviPreferenceBean.PATH_PREF_COUNTRY_BORDER, ((Integer) list[0]).intValue());
                    sb.append(AudioSystem.streamToString(((Integer) list[0]).intValue()));
                    sb.append(' ');
                    sb.append(iconStateToString(((Integer) list[1]).intValue()));
                    break;
                case 8:
                    logger.action(1386);
                    break;
                case 9:
                case 10:
                case 15:
                    sb.append(AudioSystem.streamToString(((Integer) list[0]).intValue()));
                    sb.append(' ');
                    sb.append(list[1]);
                    break;
                case 11:
                    sb.append(ringerModeToString(((Integer) list[0]).intValue()));
                    break;
                case 12:
                    MetricsLogger.action(context, (int) NaviPreferenceBean.PATH_PREF_AVOID_COUNTRY_BORDER, ((Integer) list[0]).intValue());
                    sb.append(ringerModeToString(((Integer) list[0]).intValue()));
                    break;
                case 13:
                    sb.append(zenModeToString(((Integer) list[0]).intValue()));
                    break;
                case 14:
                    sb.append(list[0]);
                    sb.append(' ');
                    sb.append(list[1]);
                    break;
                case 16:
                    MetricsLogger.action(context, (int) NaviPreferenceBean.PATH_PREF_AVOID_HIGHWAY, ((Integer) list[1]).intValue());
                    sb.append(AudioSystem.streamToString(((Integer) list[0]).intValue()));
                    sb.append(' ');
                    sb.append(list[1]);
                    break;
                case 18:
                    logger.action(1385, ((Integer) list[0]).intValue());
                    break;
                case 19:
                    MetricsLogger.visible(context, 1457);
                    MetricsLogger.histogram(context, "show_usb_overheat_alarm", ((Boolean) list[1]).booleanValue() ? 1 : 0);
                    sb.append(SHOW_REASONS[((Integer) list[0]).intValue()]);
                    sb.append(" keyguard=");
                    sb.append(list[1]);
                    break;
                case 20:
                    MetricsLogger.hidden(context, 1457);
                    MetricsLogger.histogram(context, "dismiss_usb_overheat_alarm", ((Boolean) list[1]).booleanValue() ? 1 : 0);
                    sb.append(DISMISS_REASONS[((Integer) list[0]).intValue()]);
                    sb.append(" keyguard=");
                    sb.append(list[1]);
                    break;
            }
        }
        Log.i(TAG, sb.toString());
        Callback callback = sCallback;
        if (callback != null) {
            callback.writeEvent(time, tag, list);
        }
    }

    public static void writeState(long time, VolumeDialogController.State state) {
        Callback callback = sCallback;
        if (callback != null) {
            callback.writeState(time, state);
        }
    }

    private static String iconStateToString(int iconState) {
        if (iconState != 1) {
            if (iconState != 2) {
                if (iconState == 3) {
                    return "vibrate";
                }
                return "unknown_state_" + iconState;
            }
            return "mute";
        }
        return "unmute";
    }

    private static String ringerModeToString(int ringerMode) {
        if (ringerMode != 0) {
            if (ringerMode != 1) {
                if (ringerMode == 2) {
                    return "normal";
                }
                return "unknown";
            }
            return "vibrate";
        }
        return "silent";
    }

    private static String zenModeToString(int zenMode) {
        if (zenMode != 0) {
            if (zenMode != 1) {
                if (zenMode != 2) {
                    if (zenMode == 3) {
                        return "alarms";
                    }
                    return "unknown";
                }
                return "no_interruptions";
            }
            return "important_interruptions";
        }
        return "off";
    }
}
