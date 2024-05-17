package com.android.systemui.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;
/* loaded from: classes21.dex */
public class AutoMarqueeTextView extends TextView {
    private boolean mAggregatedVisible;

    public AutoMarqueeTextView(Context context) {
        super(context);
        this.mAggregatedVisible = false;
    }

    public AutoMarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mAggregatedVisible = false;
    }

    public AutoMarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mAggregatedVisible = false;
    }

    public AutoMarqueeTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mAggregatedVisible = false;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        onVisibilityAggregated(isVisibleToUser());
    }

    @Override // android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setSelected(true);
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setSelected(false);
    }

    @Override // android.view.View
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);
        if (isVisible == this.mAggregatedVisible) {
            return;
        }
        this.mAggregatedVisible = isVisible;
        if (this.mAggregatedVisible) {
            setEllipsize(TextUtils.TruncateAt.MARQUEE);
        } else {
            setEllipsize(TextUtils.TruncateAt.END);
        }
    }
}
