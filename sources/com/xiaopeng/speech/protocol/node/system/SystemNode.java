package com.xiaopeng.speech.protocol.node.system;

import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.speech.SpeechNode;
import com.xiaopeng.speech.annotation.SpeechAnnotation;
import com.xiaopeng.speech.protocol.bean.AdjustValue;
import com.xiaopeng.speech.protocol.bean.VolumeValue;
import com.xiaopeng.speech.protocol.bean.stats.SceneSwitchStatisticsBean;
import com.xiaopeng.speech.protocol.event.SystemEvent;
import com.xiaopeng.speech.protocol.utils.StringUtil;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class SystemNode extends SpeechNode<SystemListener> {
    public static final String NAVI_VOICE_DATA = "{\"stream_type\":9}";
    private static final String STREAM_MUSIC = "3";

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.SCREEN_BRIGHTNESS_UP)
    public void onScreenBrightnessUp(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onScreenBrightnessUp();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.SCREEN_BRIGHTNESS_MAX)
    public void onScreenBrightnessMax(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onScreenBrightnessMax();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.SCREEN_BRIGHTNESS_DOWN)
    public void onScreenBrightnessDown(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onScreenBrightnessDown();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.SCREEN_BRIGHTNESS_MIN)
    public void onScreenBrightnessMin(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onScreenBrightnessMin();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.THEME_MODE_AUTO)
    public void onThemeModeAuto(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onThemeModeAuto();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.THEME_MODE_DAY)
    public void onThemeModeDay(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onThemeModeDay();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.THEME_MODE_NIGHT)
    public void onThemeModeNight(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onThemeModeNight();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.SCREEN_MODE_CLEAN)
    public void onScreenModeClean(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onScreenModeClean();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.WIFI_OFF)
    public void onWifiOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onWifiOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.WIFI_ON)
    public void onWifiOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onWifiOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.BLUETOOTH_OFF)
    public void onBluetoothOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onBluetoothOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.BLUETOOTH_ON)
    public void onBluetoothOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onBluetoothOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.VOLUME_UP)
    public void onVolumeUp(String event, String data) {
        VolumeValue volumeValue = VolumeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onVolumeUp(volumeValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.VOLUME_SET)
    public void onVolumeSet(String event, String data) {
        VolumeValue volumeValue = VolumeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onVolumeSet(volumeValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.VOLUME_DOWN)
    public void onVolumeDown(String event, String data) {
        VolumeValue volumeValue = VolumeValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onVolumeDown(volumeValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.VOLUME_MAX)
    public void onVolumeMax(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        String streamType = "3";
        try {
            JSONObject jsonObject = new JSONObject(data);
            streamType = jsonObject.optString("stream_type", "3");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onVolumeMax(Integer.valueOf(StringUtil.isDecimalNumber(streamType) ? streamType : "3").intValue());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.VOLUME_MIN)
    public void onVolumeMin(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        String streamType = "3";
        try {
            JSONObject jsonObject = new JSONObject(data);
            streamType = jsonObject.optString("stream_type", "3");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onVolumeMin(Integer.valueOf(Integer.valueOf(StringUtil.isDecimalNumber(streamType) ? streamType : "3").intValue()).intValue());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.VOLUME_MUTE)
    public void onVolumeMute(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        String streamType = "3";
        try {
            JSONObject jsonObject = new JSONObject(data);
            streamType = jsonObject.optString("stream_type", "3");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onVolumeMute(Integer.valueOf(StringUtil.isDecimalNumber(streamType) ? streamType : "3").intValue());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.VOLUME_UNMUTE)
    public void onVolumeUnmute(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        String streamType = "3";
        try {
            JSONObject jsonObject = new JSONObject(data);
            streamType = jsonObject.optString("stream_type", "3");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onVolumeUnmute(Integer.valueOf(StringUtil.isDecimalNumber(streamType) ? streamType : "3").intValue());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.VOLUME_RESUME)
    public void onVolumeResume(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onVolumeResume();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.BRIGHTNESS_UP_PERCENT)
    public void onBrightnessUpPercent(String event, String data) {
        AdjustValue adjustValue = AdjustValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onBrightnessUpPercent(adjustValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.BRIGHTNESS_SET_PERCENT)
    public void onScreenBrightnessSetPercent(String event, String data) {
        AdjustValue adjustValue = AdjustValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onBrightnessSetPercent(adjustValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.SCREEN_BRIGHTNESS_SET)
    public void onBrightnessSet(String event, String data) {
        AdjustValue adjustValue = AdjustValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onBrightnessSet(adjustValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.ICM_BRIGHTNESS_SET)
    public void onIcmBrightnessSet(String event, String data) {
        AdjustValue adjustValue = AdjustValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onIcmBrightnessSet(adjustValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.ICM_BRIGHTNESS_UP)
    public void onIcmBrightnessUp(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onIcmBrightnessUp();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.ICM_BRIGHTNESS_MAX)
    public void onIcmBrightnessMax(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onIcmBrightnessMax();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.ICM_BRIGHTNESS_DOWN)
    public void onIcmBrightnessDown(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onIcmBrightnessDown();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.ICM_BRIGHTNESS_MIN)
    public void onIcmBrightnessMin(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onIcmBrightnessMin();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.SETTING_PAGE_OPEN)
    public void onOpenSettingPage(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onOpenSettingPage();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.WIFI_SETTING_OPEN)
    public void onOpenWifiPage(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onOpenWifiPage();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.BRIGHTNESS_DOWN_PERCENT)
    public void onBrightnessDownPercent(String event, String data) {
        AdjustValue adjustValue = AdjustValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onBrightnessDownPercent(adjustValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.ICM_BRIGHTNESS_UP_PERCENT)
    public void onIcmBrightnessUpPercent(String event, String data) {
        AdjustValue adjustValue = AdjustValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onIcmBrightnessUpPercent(adjustValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.ICM_BRIGHTNESS_SET_PERCENT)
    public void onIcmBrightnessSetPercent(String event, String data) {
        AdjustValue adjustValue = AdjustValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onIcmBrightnessSetPercent(adjustValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.ICM_BRIGHTNESS_DOWN_PERCENT)
    public void onIcmBrightnessDownPercent(String event, String data) {
        AdjustValue adjustValue = AdjustValue.fromJson(data);
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onIcmBrightnessDownPercent(adjustValue);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.SCREEN_OFF)
    public void onScreenOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onScreenOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.SCREEN_ON)
    public void onScreenOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onScreenOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.SCREEN_BRIGHTNESS_STOP)
    public void onScreenBrightnessStop(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onScreenBrightnessStop();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.SCREEN_BRIGHTNESS_AUTO_ON)
    public void onScreenBrightnessAutoOn(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onScreenBrightnessAutoOn();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.SCREEN_BRIGHTNESS_AUTO_OFF)
    public void onScreenBrightnessAutoOff(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onScreenBrightnessAutoOff();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.VOLUME_NOTIFICATION_MAX)
    public void onVolumeNotificationMax(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onVolumeNotificationMax();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.VOLUME_NOTIFICATION_MIN)
    public void onVolumeNotificationMin(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onVolumeNotificationMin();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.VOLUME_NOTIFICATION_MEDIUM)
    public void onVolumeNotificationMedium(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onVolumeNotificationMedium();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.VOLUME_NOTIFICATION_UP)
    public void onVolumeNotificationUp(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onVolumeNotificationUp();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.VOLUME_NOTIFICATION_DOWN)
    public void onVolumeNotificationDown(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onVolumeNotificationDown();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.SOUND_EFFECT_DIRECTION_SET)
    public void onSoundEffectField(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        int direction = 1;
        try {
            JSONObject jsonObject = new JSONObject(data);
            direction = jsonObject.optInt(VuiConstants.EVENT_VALUE_DIRECTION);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onSoundEffectField(direction);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.SOUND_EFFECT_MODE_SET)
    public void onSoundEffectMode(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        int mode = 1;
        try {
            JSONObject jsonObject = new JSONObject(data);
            mode = jsonObject.optInt("mode");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onSoundEffectMode(mode);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.SOUND_EFFECT_SCENE_SET)
    public void onSoundEffectScene(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        int scene = 1;
        try {
            JSONObject jsonObject = new JSONObject(data);
            scene = jsonObject.optInt(SceneSwitchStatisticsBean.NAME_SCENE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onSoundEffectScene(scene);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.SOUND_EFFECT_STYlE_SET)
    public void onSoundEffectStyle(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        int style = 1;
        try {
            JSONObject jsonObject = new JSONObject(data);
            style = jsonObject.optInt(ThemeManager.AttributeSet.STYLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onSoundEffectStyle(style);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SpeechAnnotation(event = SystemEvent.HEADREST_MODE_SET)
    public void onHeadsetMode(String event, String data) {
        Object[] listenerList = this.mListenerList.collectCallbacks();
        int mode = 1;
        try {
            JSONObject jsonObject = new JSONObject(data);
            mode = jsonObject.optInt("mode");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (listenerList != null) {
            for (Object obj : listenerList) {
                ((SystemListener) obj).onHeadsetMode(mode);
            }
        }
    }
}
