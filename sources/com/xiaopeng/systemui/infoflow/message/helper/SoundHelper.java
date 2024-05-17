package com.xiaopeng.systemui.infoflow.message.helper;

import android.media.AudioAttributes;
import android.media.SoundPool;
import com.xiaopeng.systemui.infoflow.util.Logger;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes24.dex */
public class SoundHelper {
    private static final int DEFAULT_INVALID_STREAM_ID = 0;
    private static final String TAG = "SoundHelper";
    private static Map<String, Integer> sSoundData;
    private static SoundPool sSoundPool;
    private static int sStreamId;
    public static final String PATH_WHEEL_TIP_1 = "/system/media/audio/xiaopeng/cdu/wav/CDU_wheel_tip_1.wav";
    public static final String PATH_INFOFLOW_NEWCARD = "/system/media/audio/xiaopeng/cdu/wav/CDU_infoflow_newcard.wav";
    public static final String PATH_INFOFLOW_TRANSFER = "/system/media/audio/xiaopeng/cdu/wav/CDU_infoflow_transfer.wav";
    public static final String PATH_WHEEL_SCROLL = "/system/media/audio/xiaopeng/cdu/wav/CDU_wheel_scroll_7.wav";
    public static final String PATH_WHEEL_SCROLL_LEFT = "/system/media/audio/xiaopeng/cdu/wav/CDU_wheel_scroll_left.wav";
    public static final String PATH_WHEEL_SCROLL_RIGHT = "/system/media/audio/xiaopeng/cdu/wav/CDU_wheel_scroll_right.wav";
    public static final String PATH_WHEEL_OK = "/system/media/audio/xiaopeng/cdu/wav/CDU_wheel_ok.wav";
    public static final String PATH_TOUCH_DISABLE = "/system/media/audio/xiaopeng/cdu/wav/CDU_touch_disable.wav";
    private static final String[] PATH_ARRAY = {PATH_WHEEL_TIP_1, PATH_INFOFLOW_NEWCARD, PATH_INFOFLOW_TRANSFER, PATH_WHEEL_SCROLL, PATH_WHEEL_SCROLL_LEFT, PATH_WHEEL_SCROLL_RIGHT, PATH_WHEEL_OK, PATH_TOUCH_DISABLE};

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes24.dex */
    @interface SoundType {
    }

    static {
        String[] strArr;
        AudioAttributes.Builder builder = new AudioAttributes.Builder();
        builder.setLegacyStreamType(5);
        sSoundPool = new SoundPool.Builder().setAudioAttributes(builder.build()).setMaxStreams(10).build();
        sSoundData = new HashMap();
        for (String path : PATH_ARRAY) {
            sSoundData.put(path, Integer.valueOf(sSoundPool.load(path, 1)));
        }
        sStreamId = 0;
    }

    public static void play(String soundType) {
        Logger.d(TAG, "play sound effect " + soundType);
        stop();
        int soundId = 0;
        Integer value = sSoundData.get(soundType);
        if (value != null) {
            soundId = value.intValue();
        }
        SoundPool soundPool = sSoundPool;
        if (soundPool != null && soundId != 0) {
            sStreamId = soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f);
            Logger.d(TAG, "play sound effect sStreamId = " + sStreamId);
        }
    }

    public static void stop() {
        int i;
        Logger.d(TAG, "stop last sound effect sStreamId = " + sStreamId);
        SoundPool soundPool = sSoundPool;
        if (soundPool != null && (i = sStreamId) != 0) {
            soundPool.stop(i);
        }
    }

    public static void pause() {
        int i;
        SoundPool soundPool = sSoundPool;
        if (soundPool != null && (i = sStreamId) != 0) {
            soundPool.pause(i);
        }
    }

    public static void resume() {
        int i;
        SoundPool soundPool = sSoundPool;
        if (soundPool != null && (i = sStreamId) != 0) {
            soundPool.resume(i);
        }
    }
}
