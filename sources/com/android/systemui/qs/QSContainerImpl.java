package com.android.systemui.qs;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.android.systemui.R;
import com.android.systemui.qs.customize.QSCustomizer;
/* loaded from: classes21.dex */
public class QSContainerImpl extends FrameLayout {
    private View mBackground;
    private View mBackgroundGradient;
    private QuickStatusBarHeader mHeader;
    private int mHeightOverride;
    private QSCustomizer mQSCustomizer;
    private View mQSDetail;
    private View mQSFooter;
    private QSPanel mQSPanel;
    private boolean mQsDisabled;
    private float mQsExpansion;
    private int mSideMargins;
    private final Point mSizePoint;
    private View mStatusBarBackground;

    public QSContainerImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mSizePoint = new Point();
        this.mHeightOverride = -1;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mQSPanel = (QSPanel) findViewById(R.id.quick_settings_panel);
        this.mQSDetail = findViewById(R.id.qs_detail);
        this.mHeader = (QuickStatusBarHeader) findViewById(R.id.header);
        this.mQSCustomizer = (QSCustomizer) findViewById(R.id.qs_customize);
        this.mQSFooter = findViewById(R.id.qs_footer);
        this.mBackground = findViewById(R.id.quick_settings_background);
        this.mStatusBarBackground = findViewById(R.id.quick_settings_status_bar_background);
        this.mBackgroundGradient = findViewById(R.id.quick_settings_gradient_view);
        this.mSideMargins = getResources().getDimensionPixelSize(R.dimen.notification_side_paddings);
        setImportantForAccessibility(2);
        setMargins();
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setBackgroundGradientVisibility(newConfig);
        updateResources();
        this.mSizePoint.set(0, 0);
    }

    @Override // android.view.View
    public boolean performClick() {
        return true;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Configuration config = getResources().getConfiguration();
        boolean navBelow = config.smallestScreenWidthDp >= 600 || config.orientation != 2;
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) this.mQSPanel.getLayoutParams();
        int maxQs = ((getDisplayHeight() - layoutParams.topMargin) - layoutParams.bottomMargin) - getPaddingBottom();
        if (navBelow) {
            maxQs -= getResources().getDimensionPixelSize(R.dimen.navigation_bar_height);
        }
        this.mQSPanel.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(maxQs, 1073741824));
        int width = this.mQSPanel.getMeasuredWidth();
        int height = layoutParams.topMargin + layoutParams.bottomMargin + this.mQSPanel.getMeasuredHeight() + getPaddingBottom();
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(height, 1073741824));
        this.mQSCustomizer.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(getDisplayHeight(), 1073741824));
    }

    @Override // android.view.ViewGroup
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        if (child != this.mQSPanel) {
            super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
        }
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateExpansion();
    }

    public void disable(int state1, int state2, boolean animate) {
        boolean disabled = (state2 & 1) != 0;
        if (disabled == this.mQsDisabled) {
            return;
        }
        this.mQsDisabled = disabled;
        setBackgroundGradientVisibility(getResources().getConfiguration());
        this.mBackground.setVisibility(this.mQsDisabled ? 8 : 0);
    }

    private void updateResources() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mQSPanel.getLayoutParams();
        layoutParams.topMargin = this.mContext.getResources().getDimensionPixelSize(17105399);
        this.mQSPanel.setLayoutParams(layoutParams);
    }

    public void setHeightOverride(int heightOverride) {
        this.mHeightOverride = heightOverride;
        updateExpansion();
    }

    public void updateExpansion() {
        int height = calculateContainerHeight();
        setBottom(getTop() + height);
        this.mQSDetail.setBottom(getTop() + height);
        View view = this.mQSFooter;
        view.setTranslationY(height - view.getHeight());
        this.mBackground.setTop(this.mQSPanel.getTop());
        this.mBackground.setBottom(height);
    }

    protected int calculateContainerHeight() {
        int heightOverride = this.mHeightOverride;
        if (heightOverride == -1) {
            heightOverride = getMeasuredHeight();
        }
        return this.mQSCustomizer.isCustomizing() ? this.mQSCustomizer.getHeight() : Math.round(this.mQsExpansion * (heightOverride - this.mHeader.getHeight())) + this.mHeader.getHeight();
    }

    private void setBackgroundGradientVisibility(Configuration newConfig) {
        if (newConfig.orientation == 2) {
            this.mBackgroundGradient.setVisibility(4);
            this.mStatusBarBackground.setVisibility(4);
            return;
        }
        this.mBackgroundGradient.setVisibility(this.mQsDisabled ? 4 : 0);
        this.mStatusBarBackground.setVisibility(0);
    }

    public void setExpansion(float expansion) {
        this.mQsExpansion = expansion;
        updateExpansion();
    }

    private void setMargins() {
        setMargins(this.mQSDetail);
        setMargins(this.mBackground);
        setMargins(this.mQSFooter);
        this.mQSPanel.setMargins(this.mSideMargins);
        this.mHeader.setMargins(this.mSideMargins);
    }

    private void setMargins(View view) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) view.getLayoutParams();
        int i = this.mSideMargins;
        lp.rightMargin = i;
        lp.leftMargin = i;
    }

    private int getDisplayHeight() {
        if (this.mSizePoint.y == 0) {
            getDisplay().getRealSize(this.mSizePoint);
        }
        return this.mSizePoint.y;
    }
}
