package com.xiaopeng.systemui.infoflow.egg.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;
/* loaded from: classes24.dex */
public class HolidayConfig implements Serializable {
    @Expose(deserialize = false, serialize = false)
    public String configKey;
    @SerializedName("data")
    public List<HolidayConfigItem> data;

    public String getConfigKey() {
        return this.configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
        List<HolidayConfigItem> list = this.data;
        if (list != null && !list.isEmpty()) {
            for (HolidayConfigItem configItem : this.data) {
                configItem.setConfigKey(configKey);
            }
        }
    }

    public HolidayConfigItem getHolidayConfigItem() {
        List<HolidayConfigItem> list = this.data;
        if (list == null || list.isEmpty()) {
            return null;
        }
        return this.data.get(0);
    }

    public boolean matchTime(long currentTime, long hour) {
        if (getHolidayConfigItem() != null) {
            return getHolidayConfigItem().matchTime(currentTime, hour);
        }
        return false;
    }
}
