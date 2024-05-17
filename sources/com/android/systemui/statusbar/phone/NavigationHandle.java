package com.android.systemui.statusbar.phone;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import com.android.settingslib.Utils;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class NavigationHandle extends View implements ButtonInterface {
    private final int mBottom;
    private final int mDarkColor;
    private float mDarkIntensity;
    private final int mLightColor;
    private final Paint mPaint;
    private final int mRadius;

    public NavigationHandle(Context context) {
        this(context, null);
    }

    public NavigationHandle(Context context, AttributeSet attr) {
        super(context, attr);
        this.mDarkIntensity = -1.0f;
        this.mPaint = new Paint();
        Resources res = context.getResources();
        this.mRadius = res.getDimensionPixelSize(R.dimen.navigation_handle_radius);
        this.mBottom = res.getDimensionPixelSize(R.dimen.navigation_handle_bottom);
        int dualToneDarkTheme = Utils.getThemeAttr(context, R.attr.darkIconTheme);
        int dualToneLightTheme = Utils.getThemeAttr(context, R.attr.lightIconTheme);
        Context lightContext = new ContextThemeWrapper(context, dualToneLightTheme);
        Context darkContext = new ContextThemeWrapper(context, dualToneDarkTheme);
        this.mLightColor = Utils.getColorAttrDefaultColor(lightContext, R.attr.homeHandleColor);
        this.mDarkColor = Utils.getColorAttrDefaultColor(darkContext, R.attr.homeHandleColor);
        this.mPaint.setAntiAlias(true);
        setFocusable(false);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int navHeight = getHeight();
        int height = this.mRadius * 2;
        int width = getWidth();
        int y = (navHeight - this.mBottom) - height;
        int i = this.mRadius;
        canvas.drawRoundRect(0.0f, y, width, y + height, i, i, this.mPaint);
    }

    @Override // com.android.systemui.statusbar.phone.ButtonInterface
    public void setImageDrawable(Drawable drawable) {
    }

    @Override // com.android.systemui.statusbar.phone.ButtonInterface
    public void abortCurrentGesture() {
    }

    @Override // com.android.systemui.statusbar.phone.ButtonInterface
    public void setVertical(boolean vertical) {
    }

    @Override // com.android.systemui.statusbar.phone.ButtonInterface
    public void setDarkIntensity(float intensity) {
        if (this.mDarkIntensity != intensity) {
            this.mPaint.setColor(((Integer) ArgbEvaluator.getInstance().evaluate(intensity, Integer.valueOf(this.mLightColor), Integer.valueOf(this.mDarkColor))).intValue());
            this.mDarkIntensity = intensity;
            invalidate();
        }
    }

    @Override // com.android.systemui.statusbar.phone.ButtonInterface
    public void setDelayTouchFeedback(boolean shouldDelay) {
    }
}
