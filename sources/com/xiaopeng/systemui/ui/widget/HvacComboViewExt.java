package com.xiaopeng.systemui.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.android.systemui.R;
/* loaded from: classes24.dex */
public class HvacComboViewExt extends HvacComboView {
    public HvacComboViewExt(Context context) {
        super(context);
    }

    public HvacComboViewExt(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HvacComboViewExt(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.ui.widget.HvacComboView
    public void initView() {
        super.initView();
        this.mArrowBottom.setImageResource(R.drawable.ic_navbar_hvac_arrow_bottom_ext);
        this.mArrowTop.setImageResource(R.drawable.ic_navbar_hvac_arrow_top_ext);
        this.mHvacTemperature.setTextColor(R.color.color_hvac_selector_ext);
        this.mHvacAir.setVisibility(8);
    }
}
