package com.xiaopeng.xui.widget.pageindicator;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.widget.XImageView;
/* loaded from: classes25.dex */
public class XDotPagerIndicator extends XViewPagerIndicatorAbs {
    private int mDotSize;

    public XDotPagerIndicator(Context context) {
        super(context);
    }

    public XDotPagerIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public XDotPagerIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public XDotPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.pageindicator.XViewPagerIndicatorAbs
    public void init() {
        super.init();
        this.mDotSize = getContext().getResources().getDimensionPixelSize(R.dimen.x_pager_indicator_view_size);
        int padding = getContext().getResources().getDimensionPixelOffset(R.dimen.x_pager_indicator_dot_padding);
        setPadding(padding, 0, padding, 0);
    }

    @Override // com.xiaopeng.xui.widget.pageindicator.XViewPagerIndicatorAbs
    public void populateData(int count) {
        if (count > 0) {
            removeAllViews();
            for (int i = 0; i < count; i++) {
                View view = generateIndicator();
                int i2 = this.mDotSize;
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(i2, i2);
                addView(view, lp);
            }
        }
    }

    protected View generateIndicator() {
        ImageView view = new XImageView(getContext());
        view.setImageResource(R.drawable.x_dot_indicator_bg);
        view.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        return view;
    }

    @Override // com.xiaopeng.xui.widget.pageindicator.XViewPagerIndicatorAbs
    public void select(int position) {
        int childCount = getChildCount();
        if (childCount > 0 && position < childCount) {
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (i == position) {
                    child.setActivated(true);
                } else {
                    child.setActivated(false);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XLinearLayout, android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (ThemeManager.isThemeChanged(newConfig)) {
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                if (child instanceof ImageView) {
                    ImageView imageView = (ImageView) child;
                    imageView.setImageResource(R.drawable.x_dot_indicator_bg);
                }
            }
        }
    }
}
