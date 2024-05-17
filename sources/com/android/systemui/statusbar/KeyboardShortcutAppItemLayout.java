package com.android.systemui.statusbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class KeyboardShortcutAppItemLayout extends RelativeLayout {
    private static final double MAX_WIDTH_PERCENT_FOR_KEYWORDS = 0.7d;

    public KeyboardShortcutAppItemLayout(Context context) {
        super(context);
    }

    public KeyboardShortcutAppItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.widget.RelativeLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (View.MeasureSpec.getMode(widthMeasureSpec) == 1073741824) {
            ImageView shortcutIcon = (ImageView) findViewById(R.id.keyboard_shortcuts_icon);
            TextView shortcutKeyword = (TextView) findViewById(R.id.keyboard_shortcuts_keyword);
            int totalMeasuredWidth = View.MeasureSpec.getSize(widthMeasureSpec);
            int totalPadding = getPaddingLeft() + getPaddingRight();
            int availableWidth = totalMeasuredWidth - totalPadding;
            if (shortcutIcon.getVisibility() == 0) {
                availableWidth -= shortcutIcon.getMeasuredWidth();
            }
            shortcutKeyword.setMaxWidth((int) Math.round(availableWidth * MAX_WIDTH_PERCENT_FOR_KEYWORDS));
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
