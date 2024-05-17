package com.android.systemui.tuner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.widget.ScrollView;
/* loaded from: classes21.dex */
public class AutoScrollView extends ScrollView {
    private static final float SCROLL_PERCENT = 0.1f;

    public AutoScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.view.View
    public boolean onDragEvent(DragEvent event) {
        if (event.getAction() == 2) {
            int y = (int) event.getY();
            int height = getHeight();
            int scrollPadding = (int) (height * 0.1f);
            if (y < scrollPadding) {
                scrollBy(0, y - scrollPadding);
            } else if (y > height - scrollPadding) {
                scrollBy(0, (y - height) + scrollPadding);
            }
        }
        return false;
    }
}
