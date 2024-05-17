package com.xiaopeng.speech.protocol.query.system;

import com.xiaopeng.speech.annotation.IQueryProcessor;
import com.xiaopeng.speech.protocol.event.query.QuerySystemEvent;
/* loaded from: classes23.dex */
public class SystemQuery_Processor implements IQueryProcessor {
    private SystemQuery mTarget;

    public SystemQuery_Processor(SystemQuery target) {
        this.mTarget = target;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public Object querySensor(String event, String data) {
        char c;
        switch (event.hashCode()) {
            case -2146663530:
                if (event.equals(QuerySystemEvent.GET_CUR_SCREEN_BRIGHTNESS)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -2111346675:
                if (event.equals(QuerySystemEvent.GET_MAX_SCREEN_BRIGHTNESS_VALUE)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -1976339039:
                if (event.equals(QuerySystemEvent.ISUPPORT_SOUND_POSTION)) {
                    c = 20;
                    break;
                }
                c = 65535;
                break;
            case -1613537166:
                if (event.equals(QuerySystemEvent.GET_MAX_VOLUME_VALUE)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case -1541034698:
                if (event.equals(QuerySystemEvent.GET_CURRENT_NEDC_STATUS)) {
                    c = 23;
                    break;
                }
                c = 65535;
                break;
            case -1469262005:
                if (event.equals(QuerySystemEvent.ISSUPPORT_SOUND_SCENE)) {
                    c = 16;
                    break;
                }
                c = 65535;
                break;
            case -1423951429:
                if (event.equals(QuerySystemEvent.GET_PAGE_OPEN_STATUS)) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case -1338868101:
                if (event.equals(QuerySystemEvent.GET_MIN_SCREEN_BRIGHTNESS_VALUE)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -965726840:
                if (event.equals(QuerySystemEvent.GET_CURRENT_SOUND_POSITON)) {
                    c = 21;
                    break;
                }
                c = 65535;
                break;
            case -884498389:
                if (event.equals(QuerySystemEvent.ISSUPPORT_HEADREST_MODE)) {
                    c = 18;
                    break;
                }
                c = 65535;
                break;
            case -853478350:
                if (event.equals(QuerySystemEvent.GET_CURRENT_SOUND_SCENE)) {
                    c = 17;
                    break;
                }
                c = 65535;
                break;
            case -841058592:
                if (event.equals(QuerySystemEvent.GET_MIN_VOLUME_VALUE)) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case -813486830:
                if (event.equals(QuerySystemEvent.GET_CURRENT_HEADREST_MODE)) {
                    c = 19;
                    break;
                }
                c = 65535;
                break;
            case -701436316:
                if (event.equals(QuerySystemEvent.GET_MAX_ICM_BRIGHTNESS_VALUE)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -668448824:
                if (event.equals(QuerySystemEvent.GET_INTELLIGENT_DARK_LIGHT_ADAPT_STATUS)) {
                    c = 24;
                    break;
                }
                c = 65535;
                break;
            case -540074940:
                if (event.equals(QuerySystemEvent.GET_CUR_VOLUME)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -288490975:
                if (event.equals(QuerySystemEvent.GET_AUTO_SCREEN_STATUS)) {
                    c = 22;
                    break;
                }
                c = 65535;
                break;
            case -183670776:
                if (event.equals(QuerySystemEvent.GET_CURRENT_SOUND_EFFECT_STYLE)) {
                    c = 15;
                    break;
                }
                c = 65535;
                break;
            case 71042258:
                if (event.equals(QuerySystemEvent.GET_MIN_ICM_BRIGHTNESS_VALUE)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 148702545:
                if (event.equals(QuerySystemEvent.GET_TIPS_VOLUME)) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case 1473884338:
                if (event.equals(QuerySystemEvent.GET_SYSTEM_MUSIC_ISPLAYING)) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case 1521446945:
                if (event.equals(QuerySystemEvent.ISSUPPORT_SOUND_EFFECT_STYLE)) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case 1587476973:
                if (event.equals(QuerySystemEvent.GET_SOUND_EFFECT_MODEL)) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case 1606123190:
                if (event.equals(QuerySystemEvent.GET_CUR_ICM_BRIGHTNESS)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 1832379462:
                if (event.equals(QuerySystemEvent.ISSUPPORT_SOUND_EFFECT_DYNAUDIO)) {
                    c = '\r';
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
                return Integer.valueOf(this.mTarget.getCurScreenBrightness(event, data));
            case 1:
                return Integer.valueOf(this.mTarget.getMaxScreenBrightnessValue(event, data));
            case 2:
                return Integer.valueOf(this.mTarget.getMinScreenBrightnessValue(event, data));
            case 3:
                return Integer.valueOf(this.mTarget.getCurIcmBrightness(event, data));
            case 4:
                return Integer.valueOf(this.mTarget.getMaxIcmBrightnessValue(event, data));
            case 5:
                return Integer.valueOf(this.mTarget.getMinIcmBrightnessValue(event, data));
            case 6:
                return Integer.valueOf(this.mTarget.getCurVolume(event, data));
            case 7:
                return Integer.valueOf(this.mTarget.getMaxVolumeValue(event, data));
            case '\b':
                return Integer.valueOf(this.mTarget.getMinVolumeValue(event, data));
            case '\t':
                return Integer.valueOf(this.mTarget.getTipsVolume(event, data));
            case '\n':
                return Integer.valueOf(this.mTarget.getGuiPageOpenState(event, data));
            case 11:
                return Integer.valueOf(this.mTarget.getCurrentMusicActive(event, data));
            case '\f':
                return Integer.valueOf(this.mTarget.getCurrentSoundEffectMode(event, data));
            case '\r':
                return Integer.valueOf(this.mTarget.isSupportSoundEffectMode(event, data));
            case 14:
                return Integer.valueOf(this.mTarget.isSupportSoundEffectStyle(event, data));
            case 15:
                return Integer.valueOf(this.mTarget.getCurrentSoundEffectStyle(event, data));
            case 16:
                return Integer.valueOf(this.mTarget.isSupportSoundEffectScene(event, data));
            case 17:
                return Integer.valueOf(this.mTarget.getCurrentSoundEffectScene(event, data));
            case 18:
                return Integer.valueOf(this.mTarget.isSupportHeadsetMode(event, data));
            case 19:
                return Integer.valueOf(this.mTarget.getCurrentHeadsetMode(event, data));
            case 20:
                return Integer.valueOf(this.mTarget.isSupportSoundEffectField(event, data));
            case 21:
                return Integer.valueOf(this.mTarget.getCurrentSoundEffectField(event, data));
            case 22:
                return Integer.valueOf(this.mTarget.getAutoScreenStatus(event, data));
            case 23:
                return Integer.valueOf(this.mTarget.getCurrentNedcStatus(event, data));
            case 24:
                return Integer.valueOf(this.mTarget.getIntelligentDarkLightAdaptStatus(event, data));
            default:
                return null;
        }
    }

    @Override // com.xiaopeng.speech.annotation.IQueryProcessor
    public String[] getQueryEvents() {
        return new String[]{QuerySystemEvent.GET_CUR_SCREEN_BRIGHTNESS, QuerySystemEvent.GET_MAX_SCREEN_BRIGHTNESS_VALUE, QuerySystemEvent.GET_MIN_SCREEN_BRIGHTNESS_VALUE, QuerySystemEvent.GET_CUR_ICM_BRIGHTNESS, QuerySystemEvent.GET_MAX_ICM_BRIGHTNESS_VALUE, QuerySystemEvent.GET_MIN_ICM_BRIGHTNESS_VALUE, QuerySystemEvent.GET_CUR_VOLUME, QuerySystemEvent.GET_MAX_VOLUME_VALUE, QuerySystemEvent.GET_MIN_VOLUME_VALUE, QuerySystemEvent.GET_TIPS_VOLUME, QuerySystemEvent.GET_PAGE_OPEN_STATUS, QuerySystemEvent.GET_SYSTEM_MUSIC_ISPLAYING, QuerySystemEvent.GET_SOUND_EFFECT_MODEL, QuerySystemEvent.ISSUPPORT_SOUND_EFFECT_DYNAUDIO, QuerySystemEvent.ISSUPPORT_SOUND_EFFECT_STYLE, QuerySystemEvent.GET_CURRENT_SOUND_EFFECT_STYLE, QuerySystemEvent.ISSUPPORT_SOUND_SCENE, QuerySystemEvent.GET_CURRENT_SOUND_SCENE, QuerySystemEvent.ISSUPPORT_HEADREST_MODE, QuerySystemEvent.GET_CURRENT_HEADREST_MODE, QuerySystemEvent.ISUPPORT_SOUND_POSTION, QuerySystemEvent.GET_CURRENT_SOUND_POSITON, QuerySystemEvent.GET_AUTO_SCREEN_STATUS, QuerySystemEvent.GET_CURRENT_NEDC_STATUS, QuerySystemEvent.GET_INTELLIGENT_DARK_LIGHT_ADAPT_STATUS};
    }
}
