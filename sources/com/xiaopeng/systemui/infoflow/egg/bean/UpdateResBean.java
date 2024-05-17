package com.xiaopeng.systemui.infoflow.egg.bean;

import android.text.TextUtils;
import androidx.annotation.Keep;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import com.xiaopeng.systemui.infoflow.egg.utils.GsonUtils;
import com.xiaopeng.systemui.infoflow.egg.utils.NumberUtils;
import com.xiaopeng.systemui.infoflow.egg.utils.TimeUtils;
import java.util.HashMap;
@Keep
/* loaded from: classes24.dex */
public class UpdateResBean {
    @SerializedName(SpeechWidget.WIDGET_EXTRA)
    public HashMap<String, Object> extra;
    @Expose(deserialize = false, serialize = false)
    private transient UpdateResExtra mExtra;
    @Expose(deserialize = false, serialize = false)
    private transient boolean mInitExtra;
    @SerializedName(VuiConstants.ELEMENT_PRIORITY)
    public int priority;
    @SerializedName("sign")
    public String sign;
    @SerializedName(SpeechWidget.WIDGET_URL)
    public String url;
    @SerializedName("ver")
    public String ver;

    public String toString() {
        return "{ver = " + this.ver + ", url = " + this.url + ", sign = " + this.sign + ", priority = " + this.priority + ", extra = " + this.extra + "}";
    }

    public String getString(String key) {
        HashMap<String, Object> hashMap = this.extra;
        if (hashMap == null) {
            return null;
        }
        Object object = hashMap.get(key);
        return GsonUtils.obj2String(object);
    }

    private void initExtra() {
        String startTimeStr = getString("startTime");
        long startTime = !TextUtils.isEmpty(startTimeStr) ? TimeUtils.simpleDate2Timestamp(startTimeStr) : 0L;
        String endTimeStr = getString("endTime");
        long endTime = TextUtils.isEmpty(endTimeStr) ? 0L : TimeUtils.simpleDate2Timestamp(endTimeStr);
        String showHourStr = getString("showHour");
        int showHourStart = -1;
        int showHourEnd = -1;
        if (!TextUtils.isEmpty(showHourStr)) {
            String[] hourStrArray = showHourStr.split("-");
            if (hourStrArray.length >= 2) {
                showHourStart = NumberUtils.pause(hourStrArray[0], -1);
                showHourEnd = NumberUtils.pause(hourStrArray[1], -1);
            }
        }
        String uidStr = getString("uid");
        long uid = NumberUtils.pauseLong(uidStr, -1L);
        this.mExtra = new UpdateResExtra(startTime, endTime, showHourStart, showHourEnd, uid);
        this.mInitExtra = true;
    }

    public boolean matchTime(long currentTime, long hour, long uid) {
        if (!this.mInitExtra) {
            initExtra();
        }
        if (this.mExtra.startTime <= 0 || this.mExtra.endTime <= 0) {
            return false;
        }
        if ((this.mExtra.uid == -1 || this.mExtra.uid != uid) && currentTime >= this.mExtra.startTime && currentTime <= this.mExtra.endTime) {
            return this.mExtra.showHourStart < 0 || this.mExtra.showHourEnd < 0 || hour >= ((long) this.mExtra.showHourStart) || hour < ((long) this.mExtra.showHourEnd);
        }
        return false;
    }

    public long getDuration() {
        return this.mExtra.endTime - this.mExtra.startTime;
    }

    public long getStartTime() {
        if (!this.mInitExtra) {
            initExtra();
        }
        return this.mExtra.startTime;
    }

    public long getEndTime() {
        if (!this.mInitExtra) {
            initExtra();
        }
        return this.mExtra.endTime;
    }

    public HashMap<String, Object> getExtra() {
        return this.extra;
    }
}
