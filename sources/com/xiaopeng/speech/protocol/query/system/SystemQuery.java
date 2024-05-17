package com.xiaopeng.speech.protocol.query.system;

import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.speech.SpeechQuery;
import com.xiaopeng.speech.annotation.QueryAnnotation;
import com.xiaopeng.speech.protocol.bean.stats.SceneSwitchStatisticsBean;
import com.xiaopeng.speech.protocol.event.query.QuerySystemEvent;
import com.xiaopeng.speech.protocol.utils.StringUtil;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class SystemQuery extends SpeechQuery<ISystemCaller> {
    private static final int SOUND_EFFECT_MODE = 2;
    private static final String STREAM_MUSIC = "3";

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QuerySystemEvent.GET_CUR_SCREEN_BRIGHTNESS)
    public int getCurScreenBrightness(String event, String data) {
        return ((ISystemCaller) this.mQueryCaller).getCurScreenBrightness();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QuerySystemEvent.GET_MAX_SCREEN_BRIGHTNESS_VALUE)
    public int getMaxScreenBrightnessValue(String event, String data) {
        return ((ISystemCaller) this.mQueryCaller).getMaxScreenBrightnessValue();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QuerySystemEvent.GET_MIN_SCREEN_BRIGHTNESS_VALUE)
    public int getMinScreenBrightnessValue(String event, String data) {
        return ((ISystemCaller) this.mQueryCaller).getMinScreenBrightnessValue();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QuerySystemEvent.GET_CUR_ICM_BRIGHTNESS)
    public int getCurIcmBrightness(String event, String data) {
        return ((ISystemCaller) this.mQueryCaller).getCurIcmBrightness();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QuerySystemEvent.GET_MAX_ICM_BRIGHTNESS_VALUE)
    public int getMaxIcmBrightnessValue(String event, String data) {
        return ((ISystemCaller) this.mQueryCaller).getMaxIcmBrightnessValue();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QuerySystemEvent.GET_MIN_ICM_BRIGHTNESS_VALUE)
    public int getMinIcmBrightnessValue(String event, String data) {
        return ((ISystemCaller) this.mQueryCaller).getMinIcmBrightnessValue();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QuerySystemEvent.GET_CUR_VOLUME)
    public int getCurVolume(String event, String data) {
        String streamType = "3";
        try {
            JSONObject obj = new JSONObject(data);
            streamType = obj.optString("stream_type", "3");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ((ISystemCaller) this.mQueryCaller).getCurVolume(Integer.valueOf(StringUtil.isDecimalNumber(streamType) ? streamType : "3").intValue());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QuerySystemEvent.GET_MAX_VOLUME_VALUE)
    public int getMaxVolumeValue(String event, String data) {
        String streamType = "3";
        try {
            JSONObject obj = new JSONObject(data);
            streamType = obj.optString("stream_type", "3");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ((ISystemCaller) this.mQueryCaller).getMaxVolumeValue(Integer.valueOf(StringUtil.isDecimalNumber(streamType) ? streamType : "3").intValue());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QuerySystemEvent.GET_MIN_VOLUME_VALUE)
    public int getMinVolumeValue(String event, String data) {
        String streamType = "3";
        try {
            JSONObject obj = new JSONObject(data);
            streamType = obj.optString("stream_type", "3");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ((ISystemCaller) this.mQueryCaller).getMinVolumeValue(Integer.valueOf(StringUtil.isDecimalNumber(streamType) ? streamType : "3").intValue());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QuerySystemEvent.GET_TIPS_VOLUME)
    public int getTipsVolume(String event, String data) {
        return ((ISystemCaller) this.mQueryCaller).getTipsVolume();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QuerySystemEvent.GET_PAGE_OPEN_STATUS)
    public int getGuiPageOpenState(String event, String data) {
        return ((ISystemCaller) this.mQueryCaller).getGuiPageOpenState(data);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QuerySystemEvent.GET_SYSTEM_MUSIC_ISPLAYING)
    public int getCurrentMusicActive(String event, String data) {
        return ((ISystemCaller) this.mQueryCaller).getCurrentMusicActive();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QuerySystemEvent.GET_SOUND_EFFECT_MODEL)
    public int getCurrentSoundEffectMode(String event, String data) {
        return ((ISystemCaller) this.mQueryCaller).getCurrentSoundEffectMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QuerySystemEvent.ISSUPPORT_SOUND_EFFECT_DYNAUDIO)
    public int isSupportSoundEffectMode(String event, String data) {
        int soundmode = 2;
        try {
            JSONObject obj = new JSONObject(data);
            soundmode = obj.optInt("mode");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ((ISystemCaller) this.mQueryCaller).isSupportSoundEffectMode(soundmode);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QuerySystemEvent.ISSUPPORT_SOUND_EFFECT_STYLE)
    public int isSupportSoundEffectStyle(String event, String data) {
        int style = 0;
        try {
            JSONObject obj = new JSONObject(data);
            style = obj.optInt(ThemeManager.AttributeSet.STYLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ((ISystemCaller) this.mQueryCaller).isSupportSoundEffectStyle(style);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QuerySystemEvent.GET_CURRENT_SOUND_EFFECT_STYLE)
    public int getCurrentSoundEffectStyle(String event, String data) {
        return ((ISystemCaller) this.mQueryCaller).getCurrentSoundEffectStyle();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QuerySystemEvent.ISSUPPORT_SOUND_SCENE)
    public int isSupportSoundEffectScene(String event, String data) {
        int scene = 1;
        try {
            JSONObject obj = new JSONObject(data);
            scene = obj.optInt(SceneSwitchStatisticsBean.NAME_SCENE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ((ISystemCaller) this.mQueryCaller).isSupportSoundEffectScene(scene);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QuerySystemEvent.GET_CURRENT_SOUND_SCENE)
    public int getCurrentSoundEffectScene(String event, String data) {
        return ((ISystemCaller) this.mQueryCaller).getCurrentSoundEffectScene();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QuerySystemEvent.ISSUPPORT_HEADREST_MODE)
    public int isSupportHeadsetMode(String event, String data) {
        return ((ISystemCaller) this.mQueryCaller).isSupportHeadsetMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QuerySystemEvent.GET_CURRENT_HEADREST_MODE)
    public int getCurrentHeadsetMode(String event, String data) {
        return ((ISystemCaller) this.mQueryCaller).getCurrentHeadsetMode();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QuerySystemEvent.ISUPPORT_SOUND_POSTION)
    public int isSupportSoundEffectField(String event, String data) {
        int direction = 1;
        try {
            JSONObject obj = new JSONObject(data);
            direction = obj.optInt(VuiConstants.EVENT_VALUE_DIRECTION);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ((ISystemCaller) this.mQueryCaller).isSupportSoundEffectField(direction);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QuerySystemEvent.GET_CURRENT_SOUND_POSITON)
    public int getCurrentSoundEffectField(String event, String data) {
        return ((ISystemCaller) this.mQueryCaller).getCurrentSoundEffectField();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QuerySystemEvent.GET_AUTO_SCREEN_STATUS)
    public int getAutoScreenStatus(String event, String data) {
        return ((ISystemCaller) this.mQueryCaller).getAutoScreenStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QuerySystemEvent.GET_CURRENT_NEDC_STATUS)
    public int getCurrentNedcStatus(String event, String data) {
        return ((ISystemCaller) this.mQueryCaller).getCurrentNedcStatus();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @QueryAnnotation(event = QuerySystemEvent.GET_INTELLIGENT_DARK_LIGHT_ADAPT_STATUS)
    public int getIntelligentDarkLightAdaptStatus(String event, String data) {
        return ((ISystemCaller) this.mQueryCaller).getIntelligentDarkLightAdaptStatus();
    }
}
