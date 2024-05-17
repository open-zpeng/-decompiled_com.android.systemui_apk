package com.xiaopeng.systemui.infoflow.egg.bean;

import android.graphics.drawable.Drawable;
import android.view.View;
import com.google.gson.annotations.Expose;
import com.xiaopeng.systemui.infoflow.egg.view.BaseHolidayView;
import com.xiaopeng.systemui.infoflow.egg.view.HolidayViewFactory;
import java.io.Serializable;
import java.util.List;
/* loaded from: classes24.dex */
public class HolidayLayout implements Serializable {
    private String background;
    private List<BtnBean> btns;
    private TitleBean desc;
    @Expose(deserialize = false, serialize = false)
    private transient HolidayView holidayView;
    private String loading;
    @Expose(deserialize = false, serialize = false)
    private transient BaseHolidayView mHolidayView;
    private TitleBean subTitle;
    private TitleBean tag;
    private TitleBean title;
    private int type;
    private String url;

    /* loaded from: classes24.dex */
    public static class BtnBean implements Serializable {
        public String background;
        public String color;
        public String text;
        public String url;
    }

    /* loaded from: classes24.dex */
    public static class TitleBean implements Serializable {
        public String background;
        public String color;
        public String text;
    }

    public void setHolidayView(HolidayView holidayView) {
        this.holidayView = holidayView;
    }

    public int getType() {
        return this.type;
    }

    public Drawable getBackground() {
        return this.holidayView.getDrawable(this.background);
    }

    public TitleBean getTag() {
        return this.tag;
    }

    public TitleBean getTitle() {
        return this.title;
    }

    public TitleBean getSubTitle() {
        return this.subTitle;
    }

    public TitleBean getDesc() {
        return this.desc;
    }

    public String getUrl() {
        return this.url;
    }

    public List<BtnBean> getBtns() {
        return this.btns;
    }

    public String getTagText() {
        return this.holidayView.getField(this.tag.text);
    }

    public int getTagColor() {
        return this.holidayView.getColor(this.tag.color, 0);
    }

    public int getTagBackgroundColor() {
        return this.holidayView.getColor(this.tag.background, 0);
    }

    public String getTitleText() {
        return this.holidayView.getField(this.title.text);
    }

    public int getTitleColor() {
        return this.holidayView.getColor(this.title.color, 0);
    }

    public String getSubTitleText() {
        return this.holidayView.getField(this.subTitle.text);
    }

    public int getSubTitleColor() {
        return this.holidayView.getColor(this.subTitle.color, 0);
    }

    public String getDescText() {
        return this.holidayView.getField(this.desc.text);
    }

    public int getDescColor() {
        return this.holidayView.getColor(this.desc.color, 0);
    }

    public String getBtnText(int index) {
        return this.holidayView.getField(this.btns.get(index).text);
    }

    public int getBtnTextColor(int index) {
        return this.holidayView.getColor(this.btns.get(index).color, 0);
    }

    public int getBtnColor(int index) {
        return this.holidayView.getColor(this.btns.get(index).background, 0);
    }

    public Drawable getBtnBackground(int index) {
        return this.holidayView.getDrawable(this.btns.get(index).background);
    }

    public String getLoading() {
        return this.loading;
    }

    public View createView() {
        this.mHolidayView = HolidayViewFactory.getHolidayView(getType());
        return this.mHolidayView.createView(this);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.mHolidayView.setOnClickListener(onClickListener);
    }
}
