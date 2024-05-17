package com.xiaopeng.xui.widget;

import android.content.Context;
import android.util.AttributeSet;
/* loaded from: classes25.dex */
public class XRadioButton extends XAppCompatRadioButton {
    public XRadioButton(Context context) {
        this(context, null);
    }

    public XRadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, 16842878);
    }

    public XRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
    }
}
