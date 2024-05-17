package com.android.systemui.bubbles;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.android.internal.graphics.ColorUtils;
import com.android.launcher3.icons.DotRenderer;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class BadgedImageView extends ImageView {
    static final int DEFAULT_PATH_SIZE = 100;
    private int mDotColor;
    private DotRenderer mDotRenderer;
    private float mDotScale;
    private DotRenderer.DrawParams mDrawParams;
    private int mIconBitmapSize;
    private boolean mOnLeft;
    private boolean mShowDot;
    private Rect mTempBounds;

    public BadgedImageView(Context context) {
        this(context, null);
    }

    public BadgedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BadgedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public BadgedImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mTempBounds = new Rect();
        this.mDotScale = 0.0f;
        this.mIconBitmapSize = getResources().getDimensionPixelSize(R.dimen.bubble_icon_bitmap_size);
        this.mDrawParams = new DotRenderer.DrawParams();
        TypedArray ta = context.obtainStyledAttributes(new int[]{16844002});
        ta.recycle();
    }

    @Override // android.widget.ImageView, android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!this.mShowDot) {
            return;
        }
        getDrawingRect(this.mTempBounds);
        DotRenderer.DrawParams drawParams = this.mDrawParams;
        drawParams.color = this.mDotColor;
        drawParams.iconBounds = this.mTempBounds;
        drawParams.leftAlign = this.mOnLeft;
        drawParams.scale = this.mDotScale;
        if (this.mDotRenderer == null) {
            Path circlePath = new Path();
            circlePath.addCircle(50.0f, 50.0f, 50.0f, Path.Direction.CW);
            this.mDotRenderer = new DotRenderer(this.mIconBitmapSize, circlePath, 100);
        }
        this.mDotRenderer.draw(canvas, this.mDrawParams);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDotOnLeft(boolean onLeft) {
        this.mOnLeft = onLeft;
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getDotOnLeft() {
        return this.mOnLeft;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setShowDot(boolean showDot) {
        this.mShowDot = showDot;
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isShowingDot() {
        return this.mShowDot;
    }

    public void setDotColor(int color) {
        this.mDotColor = ColorUtils.setAlphaComponent(color, 255);
        invalidate();
    }

    public void drawDot(Path iconPath) {
        this.mDotRenderer = new DotRenderer(this.mIconBitmapSize, iconPath, 100);
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDotScale(float fraction) {
        this.mDotScale = fraction;
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float[] getDotCenter() {
        float[] dotPosition;
        if (this.mOnLeft) {
            dotPosition = this.mDotRenderer.getLeftDotPosition();
        } else {
            dotPosition = this.mDotRenderer.getRightDotPosition();
        }
        getDrawingRect(this.mTempBounds);
        float dotCenterX = this.mTempBounds.width() * dotPosition[0];
        float dotCenterY = this.mTempBounds.height() * dotPosition[1];
        return new float[]{dotCenterX, dotCenterY};
    }
}
