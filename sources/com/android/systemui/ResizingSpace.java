package com.android.systemui;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
/* loaded from: classes21.dex */
public class ResizingSpace extends View {
    private final int mHeight;
    private final int mWidth;

    public ResizingSpace(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (getVisibility() == 0) {
            setVisibility(4);
        }
        TypedArray a = context.obtainStyledAttributes(attrs, android.R.styleable.ViewGroup_Layout);
        this.mWidth = a.getResourceId(0, 0);
        this.mHeight = a.getResourceId(1, 0);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        int height;
        int width;
        super.onConfigurationChanged(newConfig);
        ViewGroup.LayoutParams params = getLayoutParams();
        boolean changed = false;
        if (this.mWidth > 0 && (width = getContext().getResources().getDimensionPixelOffset(this.mWidth)) != params.width) {
            params.width = width;
            changed = true;
        }
        if (this.mHeight > 0 && (height = getContext().getResources().getDimensionPixelOffset(this.mHeight)) != params.height) {
            params.height = height;
            changed = true;
        }
        if (changed) {
            setLayoutParams(params);
        }
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
    }

    private static int getDefaultSize2(int size, int measureSpec) {
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);
        if (specMode == Integer.MIN_VALUE) {
            int result = Math.min(size, specSize);
            return result;
        } else if (specMode != 0) {
            if (specMode != 1073741824) {
                return size;
            }
            return specSize;
        } else {
            return size;
        }
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize2(getSuggestedMinimumWidth(), widthMeasureSpec), getDefaultSize2(getSuggestedMinimumHeight(), heightMeasureSpec));
    }
}
