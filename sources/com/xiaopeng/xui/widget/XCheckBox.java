package com.xiaopeng.xui.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.xiaopeng.xpui.R;
/* loaded from: classes25.dex */
public class XCheckBox extends XCompoundButton {
    public XCheckBox(Context context) {
        this(context, null);
    }

    public XCheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.XCheckBox);
    }

    public XCheckBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setClickable(true);
    }
}
