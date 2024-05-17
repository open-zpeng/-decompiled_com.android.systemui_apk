package com.xiaopeng.speech.protocol.bean;

import com.xiaopeng.speech.protocol.utils.StringUtil;
import org.json.JSONObject;
/* loaded from: classes23.dex */
public class VolumeValue {
    private static final String STREAM_MUSIC = "3";
    private AdjustValue adjustValue;
    private int streamType;

    public static VolumeValue fromJson(String data) {
        VolumeValue volume = new VolumeValue();
        try {
            JSONObject jsonObject = new JSONObject(data);
            String streamType = jsonObject.optString("stream_type", "3");
            volume.streamType = Integer.valueOf(StringUtil.isDecimalNumber(streamType) ? streamType : "3").intValue();
            volume.adjustValue = AdjustValue.fromJson(jsonObject.optString("change_value"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return volume;
    }

    public int getStreamType() {
        return this.streamType;
    }

    public void setStreamType(int streamType) {
        this.streamType = streamType;
    }

    public AdjustValue getAdjustValue() {
        return this.adjustValue;
    }

    public void setAdjustValue(AdjustValue adjustValue) {
        this.adjustValue = adjustValue;
    }

    public String toString() {
        return "VolumeValue{streamType=" + this.streamType + ", adjustValue=" + this.adjustValue + '}';
    }
}
