package com.android.systemui;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import com.android.settingslib.Utils;
/* loaded from: classes21.dex */
public class CornerHandleView extends View {
    private static final float ARC_LENGTH_DP = 31.0f;
    private static final int FALLBACK_RADIUS_DP = 15;
    private static final float MARGIN_DP = 8.0f;
    private static final int MAX_ARC_DEGREES = 90;
    private static final float STROKE_DP_LARGE = 2.0f;
    private static final float STROKE_DP_SMALL = 1.95f;
    private int mDarkColor;
    private int mLightColor;
    private Paint mPaint;
    private Path mPath;

    public CornerHandleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mPaint.setStrokeWidth(getStrokePx());
        int dualToneDarkTheme = Utils.getThemeAttr(this.mContext, R.attr.darkIconTheme);
        int dualToneLightTheme = Utils.getThemeAttr(this.mContext, R.attr.lightIconTheme);
        Context lightContext = new ContextThemeWrapper(this.mContext, dualToneLightTheme);
        Context darkContext = new ContextThemeWrapper(this.mContext, dualToneDarkTheme);
        this.mLightColor = Utils.getColorAttrDefaultColor(lightContext, R.attr.singleToneColor);
        this.mDarkColor = Utils.getColorAttrDefaultColor(darkContext, R.attr.singleToneColor);
        updatePath();
    }

    private void updatePath() {
        this.mPath = new Path();
        float marginPx = getMarginPx();
        float radiusPx = getInnerRadiusPx();
        float halfStrokePx = getStrokePx() / STROKE_DP_LARGE;
        float angle = getAngle();
        float startAngle = ((90.0f - angle) / STROKE_DP_LARGE) + 180.0f;
        RectF circle = new RectF(marginPx + halfStrokePx, marginPx + halfStrokePx, ((radiusPx * STROKE_DP_LARGE) + marginPx) - halfStrokePx, ((radiusPx * STROKE_DP_LARGE) + marginPx) - halfStrokePx);
        if (angle >= 90.0f) {
            float innerCircumferenceDp = convertPixelToDp(radiusPx * STROKE_DP_LARGE * 3.1415927f, this.mContext);
            float arcDp = (getAngle() * innerCircumferenceDp) / 360.0f;
            float lineLengthPx = convertDpToPixel(((ARC_LENGTH_DP - arcDp) - MARGIN_DP) / STROKE_DP_LARGE, this.mContext);
            this.mPath.moveTo(marginPx + halfStrokePx, marginPx + radiusPx + lineLengthPx);
            this.mPath.lineTo(marginPx + halfStrokePx, marginPx + radiusPx);
            this.mPath.arcTo(circle, startAngle, angle);
            this.mPath.moveTo(marginPx + radiusPx, marginPx + halfStrokePx);
            this.mPath.lineTo(marginPx + radiusPx + lineLengthPx, marginPx + halfStrokePx);
            return;
        }
        this.mPath.arcTo(circle, startAngle, angle);
    }

    public void updateDarkness(float darkIntensity) {
        this.mPaint.setColor(((Integer) ArgbEvaluator.getInstance().evaluate(darkIntensity, Integer.valueOf(this.mLightColor), Integer.valueOf(this.mDarkColor))).intValue());
        if (getVisibility() == 0) {
            invalidate();
        }
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(this.mPath, this.mPaint);
    }

    private static float convertDpToPixel(float dp, Context context) {
        return (context.getResources().getDisplayMetrics().densityDpi / 160.0f) * dp;
    }

    private static float convertPixelToDp(float px, Context context) {
        return (160.0f * px) / context.getResources().getDisplayMetrics().densityDpi;
    }

    private float getAngle() {
        float circumferenceDp = convertPixelToDp(getOuterRadiusPx() * STROKE_DP_LARGE * 3.1415927f, this.mContext);
        float angleDeg = (ARC_LENGTH_DP / circumferenceDp) * 360.0f;
        if (angleDeg > 90.0f) {
            return 90.0f;
        }
        return angleDeg;
    }

    private float getMarginPx() {
        return convertDpToPixel(MARGIN_DP, this.mContext);
    }

    private float getInnerRadiusPx() {
        return getOuterRadiusPx() - getMarginPx();
    }

    private float getOuterRadiusPx() {
        int radius = getResources().getDimensionPixelSize(R.dimen.config_rounded_mask_size_bottom);
        if (radius == 0) {
            radius = getResources().getDimensionPixelSize(R.dimen.config_rounded_mask_size);
        }
        if (radius == 0) {
            radius = getResources().getDimensionPixelSize(R.dimen.config_rounded_mask_size_top);
        }
        if (radius == 0) {
            radius = (int) convertDpToPixel(15.0f, this.mContext);
        }
        return radius;
    }

    private float getStrokePx() {
        return convertDpToPixel(getAngle() < 90.0f ? STROKE_DP_LARGE : STROKE_DP_SMALL, getContext());
    }
}
