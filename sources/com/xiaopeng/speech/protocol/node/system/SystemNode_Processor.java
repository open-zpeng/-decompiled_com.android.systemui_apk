package com.xiaopeng.speech.protocol.node.system;

import com.alibaba.fastjson.parser.JSONLexer;
import com.xiaopeng.speech.annotation.ICommandProcessor;
import com.xiaopeng.speech.protocol.event.SystemEvent;
import kotlin.text.Typography;
/* loaded from: classes23.dex */
public class SystemNode_Processor implements ICommandProcessor {
    private SystemNode mTarget;

    public SystemNode_Processor(SystemNode target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public void performCommand(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -1919368988:
                if (event.equals(SystemEvent.VOLUME_NOTIFICATION_MEDIUM)) {
                    c = ')';
                    break;
                }
                c = 65535;
                break;
            case -1597026812:
                if (event.equals(SystemEvent.ICM_BRIGHTNESS_UP_PERCENT)) {
                    c = 31;
                    break;
                }
                c = 65535;
                break;
            case -1344595949:
                if (event.equals(SystemEvent.SCREEN_BRIGHTNESS_AUTO_ON)) {
                    c = '%';
                    break;
                }
                c = 65535;
                break;
            case -1017521433:
                if (event.equals(SystemEvent.ICM_BRIGHTNESS_SET_PERCENT)) {
                    c = ' ';
                    break;
                }
                c = 65535;
                break;
            case -1008260193:
                if (event.equals(SystemEvent.BLUETOOTH_OFF)) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case -931863087:
                if (event.equals(SystemEvent.VOLUME_NOTIFICATION_DOWN)) {
                    c = '+';
                    break;
                }
                c = 65535;
                break;
            case -601542604:
                if (event.equals(SystemEvent.ICM_BRIGHTNESS_DOWN)) {
                    c = JSONLexer.EOI;
                    break;
                }
                c = 65535;
                break;
            case -537146671:
                if (event.equals(SystemEvent.WIFI_SETTING_OPEN)) {
                    c = 29;
                    break;
                }
                c = 65535;
                break;
            case -453867971:
                if (event.equals(SystemEvent.BRIGHTNESS_UP_PERCENT)) {
                    c = 20;
                    break;
                }
                c = 65535;
                break;
            case -147779855:
                if (event.equals(SystemEvent.VOLUME_RESUME)) {
                    c = 19;
                    break;
                }
                c = 65535;
                break;
            case -95607791:
                if (event.equals(SystemEvent.SCREEN_ON)) {
                    c = '#';
                    break;
                }
                c = 65535;
                break;
            case -70327096:
                if (event.equals(SystemEvent.WIFI_ON)) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case -53759242:
                if (event.equals(SystemEvent.VOLUME_UNMUTE)) {
                    c = 18;
                    break;
                }
                c = 65535;
                break;
            case 60664270:
                if (event.equals(SystemEvent.BRIGHTNESS_SET_PERCENT)) {
                    c = 21;
                    break;
                }
                c = 65535;
                break;
            case 108495445:
                if (event.equals(SystemEvent.VOLUME_NOTIFICATION_MAX)) {
                    c = '\'';
                    break;
                }
                c = 65535;
                break;
            case 108495683:
                if (event.equals(SystemEvent.VOLUME_NOTIFICATION_MIN)) {
                    c = '(';
                    break;
                }
                c = 65535;
                break;
            case 195917088:
                if (event.equals(SystemEvent.VOLUME_MAX)) {
                    c = 15;
                    break;
                }
                c = 65535;
                break;
            case 195917326:
                if (event.equals(SystemEvent.VOLUME_MIN)) {
                    c = 16;
                    break;
                }
                c = 65535;
                break;
            case 195922974:
                if (event.equals(SystemEvent.VOLUME_SET)) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case 448747494:
                if (event.equals(SystemEvent.SCREEN_BRIGHTNESS_UP)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 483297957:
                if (event.equals(SystemEvent.THEME_MODE_AUTO)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 546271995:
                if (event.equals(SystemEvent.SOUND_EFFECT_DIRECTION_SET)) {
                    c = ',';
                    break;
                }
                c = 65535;
                break;
            case 605044356:
                if (event.equals(SystemEvent.BRIGHTNESS_DOWN_PERCENT)) {
                    c = 30;
                    break;
                }
                c = 65535;
                break;
            case 660212143:
                if (event.equals(SystemEvent.BLUETOOTH_ON)) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case 662217111:
                if (event.equals(SystemEvent.SETTING_PAGE_OPEN)) {
                    c = 28;
                    break;
                }
                c = 65535;
                break;
            case 759114577:
                if (event.equals(SystemEvent.SOUND_EFFECT_SCENE_SET)) {
                    c = '.';
                    break;
                }
                c = 65535;
                break;
            case 968795136:
                if (event.equals(SystemEvent.SOUND_EFFECT_STYlE_SET)) {
                    c = '/';
                    break;
                }
                c = 65535;
                break;
            case 979070195:
                if (event.equals(SystemEvent.HEADREST_MODE_SET)) {
                    c = '0';
                    break;
                }
                c = 65535;
                break;
            case 985423846:
                if (event.equals(SystemEvent.THEME_MODE_DAY)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 1026262393:
                if (event.equals(SystemEvent.SCREEN_BRIGHTNESS_MAX)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1026262631:
                if (event.equals(SystemEvent.SCREEN_BRIGHTNESS_MIN)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 1026268279:
                if (event.equals(SystemEvent.SCREEN_BRIGHTNESS_SET)) {
                    c = 22;
                    break;
                }
                c = 65535;
                break;
            case 1209375344:
                if (event.equals(SystemEvent.SCREEN_MODE_CLEAN)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 1267198395:
                if (event.equals(SystemEvent.SCREEN_BRIGHTNESS_AUTO_OFF)) {
                    c = Typography.amp;
                    break;
                }
                c = 65535;
                break;
            case 1331125629:
                if (event.equals(SystemEvent.SCREEN_OFF)) {
                    c = Typography.quote;
                    break;
                }
                c = 65535;
                break;
            case 1447822712:
                if (event.equals(SystemEvent.SOUND_EFFECT_MODE_SET)) {
                    c = '-';
                    break;
                }
                c = 65535;
                break;
            case 1541025931:
                if (event.equals(SystemEvent.ICM_BRIGHTNESS_DOWN_PERCENT)) {
                    c = '!';
                    break;
                }
                c = 65535;
                break;
            case 1749108525:
                if (event.equals(SystemEvent.SCREEN_BRIGHTNESS_DOWN)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 1749559949:
                if (event.equals(SystemEvent.SCREEN_BRIGHTNESS_STOP)) {
                    c = Typography.dollar;
                    break;
                }
                c = 65535;
                break;
            case 1778207846:
                if (event.equals(SystemEvent.VOLUME_DOWN)) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case 1778481629:
                if (event.equals(SystemEvent.VOLUME_MUTE)) {
                    c = 17;
                    break;
                }
                c = 65535;
                break;
            case 1781718930:
                if (event.equals(SystemEvent.ICM_BRIGHTNESS_MAX)) {
                    c = 25;
                    break;
                }
                c = 65535;
                break;
            case 1781719168:
                if (event.equals(SystemEvent.ICM_BRIGHTNESS_MIN)) {
                    c = 27;
                    break;
                }
                c = 65535;
                break;
            case 1781724816:
                if (event.equals(SystemEvent.ICM_BRIGHTNESS_SET)) {
                    c = 23;
                    break;
                }
                c = 65535;
                break;
            case 1858590381:
                if (event.equals(SystemEvent.ICM_BRIGHTNESS_UP)) {
                    c = 24;
                    break;
                }
                c = 65535;
                break;
            case 1943162762:
                if (event.equals(SystemEvent.VOLUME_NOTIFICATION_UP)) {
                    c = '*';
                    break;
                }
                c = 65535;
                break;
            case 1945982815:
                if (event.equals(SystemEvent.VOLUME_UP)) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case 2108970466:
                if (event.equals(SystemEvent.THEME_MODE_NIGHT)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 2114827174:
                if (event.equals(SystemEvent.WIFI_OFF)) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                this.mTarget.onScreenBrightnessUp(event, data);
                return;
            case 1:
                this.mTarget.onScreenBrightnessMax(event, data);
                return;
            case 2:
                this.mTarget.onScreenBrightnessDown(event, data);
                return;
            case 3:
                this.mTarget.onScreenBrightnessMin(event, data);
                return;
            case 4:
                this.mTarget.onThemeModeAuto(event, data);
                return;
            case 5:
                this.mTarget.onThemeModeDay(event, data);
                return;
            case 6:
                this.mTarget.onThemeModeNight(event, data);
                return;
            case 7:
                this.mTarget.onScreenModeClean(event, data);
                return;
            case '\b':
                this.mTarget.onWifiOff(event, data);
                return;
            case '\t':
                this.mTarget.onWifiOn(event, data);
                return;
            case '\n':
                this.mTarget.onBluetoothOff(event, data);
                return;
            case 11:
                this.mTarget.onBluetoothOn(event, data);
                return;
            case '\f':
                this.mTarget.onVolumeUp(event, data);
                return;
            case '\r':
                this.mTarget.onVolumeSet(event, data);
                return;
            case 14:
                this.mTarget.onVolumeDown(event, data);
                return;
            case 15:
                this.mTarget.onVolumeMax(event, data);
                return;
            case 16:
                this.mTarget.onVolumeMin(event, data);
                return;
            case 17:
                this.mTarget.onVolumeMute(event, data);
                return;
            case 18:
                this.mTarget.onVolumeUnmute(event, data);
                return;
            case 19:
                this.mTarget.onVolumeResume(event, data);
                return;
            case 20:
                this.mTarget.onBrightnessUpPercent(event, data);
                return;
            case 21:
                this.mTarget.onScreenBrightnessSetPercent(event, data);
                return;
            case 22:
                this.mTarget.onBrightnessSet(event, data);
                return;
            case 23:
                this.mTarget.onIcmBrightnessSet(event, data);
                return;
            case 24:
                this.mTarget.onIcmBrightnessUp(event, data);
                return;
            case 25:
                this.mTarget.onIcmBrightnessMax(event, data);
                return;
            case 26:
                this.mTarget.onIcmBrightnessDown(event, data);
                return;
            case 27:
                this.mTarget.onIcmBrightnessMin(event, data);
                return;
            case 28:
                this.mTarget.onOpenSettingPage(event, data);
                return;
            case 29:
                this.mTarget.onOpenWifiPage(event, data);
                return;
            case 30:
                this.mTarget.onBrightnessDownPercent(event, data);
                return;
            case 31:
                this.mTarget.onIcmBrightnessUpPercent(event, data);
                return;
            case ' ':
                this.mTarget.onIcmBrightnessSetPercent(event, data);
                return;
            case '!':
                this.mTarget.onIcmBrightnessDownPercent(event, data);
                return;
            case '\"':
                this.mTarget.onScreenOff(event, data);
                return;
            case '#':
                this.mTarget.onScreenOn(event, data);
                return;
            case '$':
                this.mTarget.onScreenBrightnessStop(event, data);
                return;
            case '%':
                this.mTarget.onScreenBrightnessAutoOn(event, data);
                return;
            case '&':
                this.mTarget.onScreenBrightnessAutoOff(event, data);
                return;
            case '\'':
                this.mTarget.onVolumeNotificationMax(event, data);
                return;
            case '(':
                this.mTarget.onVolumeNotificationMin(event, data);
                return;
            case ')':
                this.mTarget.onVolumeNotificationMedium(event, data);
                return;
            case '*':
                this.mTarget.onVolumeNotificationUp(event, data);
                return;
            case '+':
                this.mTarget.onVolumeNotificationDown(event, data);
                return;
            case ',':
                this.mTarget.onSoundEffectField(event, data);
                return;
            case '-':
                this.mTarget.onSoundEffectMode(event, data);
                return;
            case '.':
                this.mTarget.onSoundEffectScene(event, data);
                return;
            case '/':
                this.mTarget.onSoundEffectStyle(event, data);
                return;
            case '0':
                this.mTarget.onHeadsetMode(event, data);
                return;
            default:
                return;
        }
    }

    @Override // com.xiaopeng.speech.annotation.ICommandProcessor
    public String[] getSubscribeEvents() {
        return new String[]{SystemEvent.SCREEN_BRIGHTNESS_UP, SystemEvent.SCREEN_BRIGHTNESS_MAX, SystemEvent.SCREEN_BRIGHTNESS_DOWN, SystemEvent.SCREEN_BRIGHTNESS_MIN, SystemEvent.THEME_MODE_AUTO, SystemEvent.THEME_MODE_DAY, SystemEvent.THEME_MODE_NIGHT, SystemEvent.SCREEN_MODE_CLEAN, SystemEvent.WIFI_OFF, SystemEvent.WIFI_ON, SystemEvent.BLUETOOTH_OFF, SystemEvent.BLUETOOTH_ON, SystemEvent.VOLUME_UP, SystemEvent.VOLUME_SET, SystemEvent.VOLUME_DOWN, SystemEvent.VOLUME_MAX, SystemEvent.VOLUME_MIN, SystemEvent.VOLUME_MUTE, SystemEvent.VOLUME_UNMUTE, SystemEvent.VOLUME_RESUME, SystemEvent.BRIGHTNESS_UP_PERCENT, SystemEvent.BRIGHTNESS_SET_PERCENT, SystemEvent.SCREEN_BRIGHTNESS_SET, SystemEvent.ICM_BRIGHTNESS_SET, SystemEvent.ICM_BRIGHTNESS_UP, SystemEvent.ICM_BRIGHTNESS_MAX, SystemEvent.ICM_BRIGHTNESS_DOWN, SystemEvent.ICM_BRIGHTNESS_MIN, SystemEvent.SETTING_PAGE_OPEN, SystemEvent.WIFI_SETTING_OPEN, SystemEvent.BRIGHTNESS_DOWN_PERCENT, SystemEvent.ICM_BRIGHTNESS_UP_PERCENT, SystemEvent.ICM_BRIGHTNESS_SET_PERCENT, SystemEvent.ICM_BRIGHTNESS_DOWN_PERCENT, SystemEvent.SCREEN_OFF, SystemEvent.SCREEN_ON, SystemEvent.SCREEN_BRIGHTNESS_STOP, SystemEvent.SCREEN_BRIGHTNESS_AUTO_ON, SystemEvent.SCREEN_BRIGHTNESS_AUTO_OFF, SystemEvent.VOLUME_NOTIFICATION_MAX, SystemEvent.VOLUME_NOTIFICATION_MIN, SystemEvent.VOLUME_NOTIFICATION_MEDIUM, SystemEvent.VOLUME_NOTIFICATION_UP, SystemEvent.VOLUME_NOTIFICATION_DOWN, SystemEvent.SOUND_EFFECT_DIRECTION_SET, SystemEvent.SOUND_EFFECT_MODE_SET, SystemEvent.SOUND_EFFECT_SCENE_SET, SystemEvent.SOUND_EFFECT_STYlE_SET, SystemEvent.HEADREST_MODE_SET};
    }
}
