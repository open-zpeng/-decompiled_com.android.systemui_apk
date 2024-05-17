package com.xiaopeng.systemui.infoflow.egg.bean;

import androidx.annotation.Keep;
import com.google.gson.annotations.SerializedName;
import com.xiaopeng.speech.vui.constants.VuiConstants;
import java.util.Collections;
import java.util.List;
@Keep
/* loaded from: classes24.dex */
public class UpdateHolBean {
    @SerializedName("holidayList")
    public List<UpdateResBean> holidayList;
    @SerializedName(VuiConstants.TIMESTAMP)
    public long timestamp;

    public static UpdateHolBean empty() {
        UpdateHolBean updateHolBean = new UpdateHolBean();
        updateHolBean.holidayList = Collections.emptyList();
        updateHolBean.timestamp = System.currentTimeMillis();
        return updateHolBean;
    }
}
