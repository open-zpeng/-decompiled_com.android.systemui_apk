package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.drawable.XLoadingDrawable;
import com.xiaopeng.xui.graphics.XFpsMonitor;
import com.xiaopeng.xui.view.XView;
/* loaded from: classes25.dex */
public class XLoading extends XView {
    private static final String TAG = "XLoading";
    private boolean isDebug;
    private XLoadingDrawable mDrawable;
    private XFpsMonitor mFpsMonitor;

    public XLoading(Context context) {
        this(context, null);
    }

    public XLoading(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.style.XLoading_XLarge);
    }

    public XLoading(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.XLoading_XLarge);
    }

    public XLoading(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.isDebug = false;
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (Build.VERSION.SDK_INT <= 26) {
            setLayerType(1, null);
        }
        this.mDrawable = new XLoadingDrawable();
        this.mDrawable.setCallback(this);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.XLoading, defStyleAttr, defStyleRes);
        this.mDrawable.setType(ta.getInt(R.styleable.XLoading_loading_type, 3));
        ta.recycle();
        if (this.isDebug) {
            this.mFpsMonitor = new XFpsMonitor(TAG);
        }
    }

    @Override // android.view.View
    protected boolean verifyDrawable(@NonNull Drawable who) {
        return super.verifyDrawable(who) || who == this.mDrawable;
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        if (getLayoutParams().width == -2 && getLayoutParams().height == -2) {
            setMeasuredDimension(216, 216);
        } else if (getLayoutParams().width == -2) {
            setMeasuredDimension(216, heightSize);
        } else if (getLayoutParams().height == -2) {
            setMeasuredDimension(widthSize, 216);
        }
    }

    @Override // android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.mDrawable.setBounds(left, top, right, bottom);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.view.XView, android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mDrawable.onConfigurationChanged(getContext(), newConfig);
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        XFpsMonitor xFpsMonitor;
        XFpsMonitor xFpsMonitor2;
        if (this.isDebug && (xFpsMonitor2 = this.mFpsMonitor) != null) {
            xFpsMonitor2.frameStart();
        }
        super.draw(canvas);
        if (this.isDebug && (xFpsMonitor = this.mFpsMonitor) != null) {
            xFpsMonitor.frameEnd();
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mDrawable.draw(canvas);
    }

    public void setType(int type) {
        this.mDrawable.setType(type);
    }

    public void setDuration(long duration) {
        this.mDrawable.setDuration(duration);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.view.XView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mDrawable.onConfigurationChanged(getContext(), getResources().getConfiguration());
        this.mDrawable.cancelAnimations();
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.view.XView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mDrawable.cancelAnimations();
    }

    public void setDebug(boolean isDebug) {
        this.isDebug = isDebug;
        this.mDrawable.setDebug(isDebug);
    }

    public float getDelayFactor() {
        return this.mDrawable.getDelayFactor();
    }

    @Override // android.view.View
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        this.mDrawable.setVisible(visibility == 0, true);
    }
}
