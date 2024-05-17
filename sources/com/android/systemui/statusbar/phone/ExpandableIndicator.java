package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class ExpandableIndicator extends ImageView {
    private boolean mExpanded;
    private boolean mIsDefaultDirection;

    public ExpandableIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mIsDefaultDirection = true;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        updateIndicatorDrawable();
        setContentDescription(getContentDescription(this.mExpanded));
    }

    public void setExpanded(boolean expanded) {
        if (expanded == this.mExpanded) {
            return;
        }
        this.mExpanded = expanded;
        int res = getDrawableResourceId(!this.mExpanded);
        AnimatedVectorDrawable avd = (AnimatedVectorDrawable) getContext().getDrawable(res).getConstantState().newDrawable();
        setImageDrawable(avd);
        avd.forceAnimationOnUI();
        avd.start();
        setContentDescription(getContentDescription(expanded));
    }

    public void setDefaultDirection(boolean isDefaultDirection) {
        this.mIsDefaultDirection = isDefaultDirection;
        updateIndicatorDrawable();
    }

    private int getDrawableResourceId(boolean expanded) {
        return this.mIsDefaultDirection ? expanded ? R.drawable.ic_volume_collapse_animation : R.drawable.ic_volume_expand_animation : expanded ? R.drawable.ic_volume_expand_animation : R.drawable.ic_volume_collapse_animation;
    }

    private String getContentDescription(boolean expanded) {
        return expanded ? this.mContext.getString(R.string.accessibility_quick_settings_collapse) : this.mContext.getString(R.string.accessibility_quick_settings_expand);
    }

    private void updateIndicatorDrawable() {
        int res = getDrawableResourceId(this.mExpanded);
        setImageResource(res);
    }
}
