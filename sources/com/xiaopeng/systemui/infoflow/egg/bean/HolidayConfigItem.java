package com.xiaopeng.systemui.infoflow.egg.bean;

import android.text.TextUtils;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.xiaopeng.lib.apirouter.ClientConstants;
import com.xiaopeng.lib.utils.LogUtils;
import com.xiaopeng.systemui.infoflow.egg.HolidayEventManager;
import com.xiaopeng.systemui.infoflow.egg.utils.TimeUtils;
import java.io.File;
import java.io.Serializable;
/* loaded from: classes24.dex */
public class HolidayConfigItem implements Serializable {
    public static final String FACTOR_BOTH = "3";
    public static final String FACTOR_OFF = "2";
    public static final String FACTOR_ON = "1";
    @Expose(deserialize = false, serialize = false)
    protected transient String configKey;
    @SerializedName("endTime")
    public String endTime;
    @SerializedName("factor")
    public String factor;
    @Expose(deserialize = false, serialize = false)
    protected transient MatchTimeFile matchTimeFile;
    @SerializedName("name")
    public String name;
    @SerializedName(ClientConstants.ALIAS.PATH)
    public String path;
    @SerializedName("showHourEnd")
    public int showHourEnd;
    @SerializedName("showHourStart")
    public int showHourStart;
    @SerializedName("startTime")
    public String startTime;

    public String getConfigKey() {
        return this.configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public MatchTimeFile getMatchTimeFile() {
        return this.matchTimeFile;
    }

    public void setMatchTimeFile(MatchTimeFile matchTimeFile) {
        this.matchTimeFile = matchTimeFile;
    }

    public String wrapPath(String filePath) {
        return this.configKey + "/" + filePath;
    }

    public File getFile(String fileName) {
        File holidayDir = HolidayEventManager.getHolidayFile();
        String filePath = this.configKey + "/" + this.path + "/" + fileName;
        File file = new File(holidayDir, filePath);
        LogUtils.i("HolidayConfigItem", "filePath:" + filePath);
        return file;
    }

    public String getFactor() {
        if (TextUtils.isEmpty(this.factor)) {
            this.factor = "1";
        }
        return this.factor;
    }

    public boolean matchTime(long currentTime, long hour) {
        int i;
        long startTimeL = TimeUtils.simpleDate2Timestamp(this.startTime);
        long endTimeL = TimeUtils.simpleDate2Timestamp(this.endTime);
        if (currentTime < startTimeL || currentTime > endTimeL) {
            return false;
        }
        int i2 = this.showHourStart;
        if (i2 < 0 || (i = this.showHourEnd) < 0) {
            return true;
        }
        return hour >= ((long) i2) && hour < ((long) i);
    }
}
