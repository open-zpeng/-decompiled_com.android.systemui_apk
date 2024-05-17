package com.android.systemui.statusbar.notification.row;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.ViewGroup;
import com.android.systemui.statusbar.AlphaOptimizedButton;
/* loaded from: classes21.dex */
public class FooterViewButton extends AlphaOptimizedButton {
    public FooterViewButton(Context context) {
        this(context, null);
    }

    public FooterViewButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FooterViewButton(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public FooterViewButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override // android.view.View
    public void getDrawingRect(Rect outRect) {
        super.getDrawingRect(outRect);
        float translationX = ((ViewGroup) this.mParent).getTranslationX();
        float translationY = ((ViewGroup) this.mParent).getTranslationY();
        outRect.left = (int) (outRect.left + translationX);
        outRect.right = (int) (outRect.right + translationX);
        outRect.top = (int) (outRect.top + translationY);
        outRect.bottom = (int) (outRect.bottom + translationY);
    }
}
