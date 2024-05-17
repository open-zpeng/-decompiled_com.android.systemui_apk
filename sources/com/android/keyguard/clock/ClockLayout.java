package com.android.keyguard.clock;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.MathUtils;
import android.view.View;
import android.widget.FrameLayout;
import com.android.systemui.R;
import com.android.systemui.doze.util.BurnInHelperKt;
/* loaded from: classes19.dex */
public class ClockLayout extends FrameLayout {
    private static final int ANALOG_CLOCK_SHIFT_FACTOR = 3;
    private View mAnalogClock;
    private int mBurnInPreventionOffsetX;
    private int mBurnInPreventionOffsetY;
    private float mDarkAmount;

    public ClockLayout(Context context) {
        this(context, null);
    }

    public ClockLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mAnalogClock = findViewById(R.id.analog_clock);
        Resources resources = getResources();
        this.mBurnInPreventionOffsetX = resources.getDimensionPixelSize(R.dimen.burn_in_prevention_offset_x);
        this.mBurnInPreventionOffsetY = resources.getDimensionPixelSize(R.dimen.burn_in_prevention_offset_y);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        positionChildren();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onTimeChanged() {
        positionChildren();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDarkAmount(float darkAmount) {
        this.mDarkAmount = darkAmount;
        positionChildren();
    }

    private void positionChildren() {
        float offsetX = MathUtils.lerp(0.0f, BurnInHelperKt.getBurnInOffset(this.mBurnInPreventionOffsetX * 2, true) - this.mBurnInPreventionOffsetX, this.mDarkAmount);
        float offsetY = MathUtils.lerp(0.0f, BurnInHelperKt.getBurnInOffset(this.mBurnInPreventionOffsetY * 2, false) - (this.mBurnInPreventionOffsetY * 0.5f), this.mDarkAmount);
        View view = this.mAnalogClock;
        if (view != null) {
            view.setX(Math.max(0.0f, (getWidth() - this.mAnalogClock.getWidth()) * 0.5f) + (offsetX * 3.0f));
            this.mAnalogClock.setY(Math.max(0.0f, (getHeight() - this.mAnalogClock.getHeight()) * 0.5f) + (3.0f * offsetY));
        }
    }
}
