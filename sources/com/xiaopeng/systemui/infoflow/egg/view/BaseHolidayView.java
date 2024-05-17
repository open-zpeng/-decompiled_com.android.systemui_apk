package com.xiaopeng.systemui.infoflow.egg.view;

import android.view.View;
import com.xiaopeng.systemui.infoflow.egg.bean.HolidayLayout;
/* loaded from: classes24.dex */
public abstract class BaseHolidayView {
    protected View.OnClickListener mOnClickListener;

    public abstract View createView(HolidayLayout holidayLayout);

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }
}
