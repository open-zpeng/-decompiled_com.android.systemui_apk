package com.xiaopeng.systemui.infoflow.egg.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.speech.protocol.bean.stats.SceneSwitchStatisticsBean;
import com.xiaopeng.speech.speechwidget.SpeechWidget;
import java.io.Serializable;
import java.util.HashMap;
/* loaded from: classes24.dex */
public class HolidayMessage implements Serializable {
    @SerializedName(ThemeManager.AttributeSet.BUTTON)
    public String button;
    @SerializedName("buttonContent")
    public HashMap<String, Object> buttonContent;
    @Expose(deserialize = false, serialize = false)
    private transient HolidayView holidayView;
    @SerializedName(SceneSwitchStatisticsBean.NAME_SCENE)
    public int scene;
    @SerializedName(SpeechWidget.WIDGET_SUBTITLE)
    public String subTitle;
    @SerializedName(SpeechWidget.WIDGET_TITLE)
    public String title;

    public void setHolidayView(HolidayView holidayView) {
        this.holidayView = holidayView;
        parse();
    }

    public void parse() {
        this.title = this.holidayView.getField(this.title);
        this.subTitle = this.holidayView.getField(this.title);
    }
}
