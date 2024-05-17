package com.android.systemui.statusbar.notification.row;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;
/* loaded from: classes21.dex */
public class ButtonLinearLayout extends LinearLayout {
    public ButtonLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return Button.class.getName();
    }
}
