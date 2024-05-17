package com.android.systemui.qs;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import com.android.settingslib.Utils;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class DataUsageGraph extends View {
    private long mLimitLevel;
    private final int mMarkerWidth;
    private long mMaxLevel;
    private final int mOverlimitColor;
    private final Paint mTmpPaint;
    private final RectF mTmpRect;
    private final int mTrackColor;
    private final int mUsageColor;
    private long mUsageLevel;
    private final int mWarningColor;
    private long mWarningLevel;

    public DataUsageGraph(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTmpRect = new RectF();
        this.mTmpPaint = new Paint();
        Resources res = context.getResources();
        this.mTrackColor = Utils.getColorStateListDefaultColor(context, R.color.data_usage_graph_track);
        this.mWarningColor = Utils.getColorStateListDefaultColor(context, R.color.data_usage_graph_warning);
        this.mUsageColor = Utils.getColorAccentDefaultColor(context);
        this.mOverlimitColor = Utils.getColorErrorDefaultColor(context);
        this.mMarkerWidth = res.getDimensionPixelSize(R.dimen.data_usage_graph_marker_width);
    }

    public void setLevels(long limitLevel, long warningLevel, long usageLevel) {
        this.mLimitLevel = Math.max(0L, limitLevel);
        this.mWarningLevel = Math.max(0L, warningLevel);
        this.mUsageLevel = Math.max(0L, usageLevel);
        this.mMaxLevel = Math.max(Math.max(Math.max(this.mLimitLevel, this.mWarningLevel), this.mUsageLevel), 1L);
        postInvalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF r = this.mTmpRect;
        Paint p = this.mTmpPaint;
        int w = getWidth();
        int h = getHeight();
        long j = this.mLimitLevel;
        boolean overLimit = j > 0 && this.mUsageLevel > j;
        long j2 = this.mMaxLevel;
        float usageRight = w * (((float) this.mUsageLevel) / ((float) j2));
        if (!overLimit) {
            r.set(0.0f, 0.0f, w, h);
            p.setColor(this.mTrackColor);
            canvas.drawRect(r, p);
        } else {
            int i = this.mMarkerWidth;
            float usageRight2 = (w * (((float) this.mLimitLevel) / ((float) j2))) - (i / 2);
            float usageRight3 = i;
            usageRight = Math.min(Math.max(usageRight2, usageRight3), w - (this.mMarkerWidth * 2));
            r.set(this.mMarkerWidth + usageRight, 0.0f, w, h);
            p.setColor(this.mOverlimitColor);
            canvas.drawRect(r, p);
        }
        r.set(0.0f, 0.0f, usageRight, h);
        p.setColor(this.mUsageColor);
        canvas.drawRect(r, p);
        float warningLeft = Math.min(Math.max((w * (((float) this.mWarningLevel) / ((float) this.mMaxLevel))) - (this.mMarkerWidth / 2), 0.0f), w - this.mMarkerWidth);
        r.set(warningLeft, 0.0f, this.mMarkerWidth + warningLeft, h);
        p.setColor(this.mWarningColor);
        canvas.drawRect(r, p);
    }
}
