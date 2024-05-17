package com.android.systemui.statusbar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
/* loaded from: classes21.dex */
public final class KeyboardShortcutKeysLayout extends ViewGroup {
    private final Context mContext;
    private int mLineHeight;

    public KeyboardShortcutKeysLayout(Context context) {
        super(context);
        this.mContext = context;
    }

    public KeyboardShortcutKeysLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childHeightMeasureSpec;
        int width = (View.MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()) - getPaddingRight();
        int childCount = getChildCount();
        int height = (View.MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop()) - getPaddingBottom();
        int lineHeight = 0;
        int xPos = getPaddingLeft();
        int yPos = getPaddingTop();
        if (View.MeasureSpec.getMode(heightMeasureSpec) == Integer.MIN_VALUE) {
            childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE);
        } else {
            childHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
        }
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
                child.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), childHeightMeasureSpec);
                int childWidth = child.getMeasuredWidth();
                lineHeight = Math.max(lineHeight, child.getMeasuredHeight() + layoutParams.mVerticalSpacing);
                if (xPos + childWidth > width) {
                    xPos = getPaddingLeft();
                    yPos += lineHeight;
                }
                xPos += layoutParams.mHorizontalSpacing + childWidth;
            }
        }
        this.mLineHeight = lineHeight;
        if (View.MeasureSpec.getMode(heightMeasureSpec) == 0) {
            height = yPos + lineHeight;
        } else if (View.MeasureSpec.getMode(heightMeasureSpec) == Integer.MIN_VALUE && yPos + lineHeight < height) {
            height = yPos + lineHeight;
        }
        setMeasuredDimension(width, height);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        int spacing = getHorizontalVerticalSpacing();
        return new LayoutParams(spacing, spacing);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        int spacing = getHorizontalVerticalSpacing();
        return new LayoutParams(spacing, spacing, layoutParams);
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int xPos;
        LayoutParams lp;
        int i;
        int paddingLeft;
        int childCount = getChildCount();
        int fullRowWidth = r - l;
        if (isRTL()) {
            xPos = fullRowWidth - getPaddingRight();
        } else {
            xPos = getPaddingLeft();
        }
        int yPos = getPaddingTop();
        int xPos2 = xPos;
        int yPos2 = yPos;
        int lastHorizontalSpacing = 0;
        int rowStartIdx = 0;
        for (int i2 = 0; i2 < childCount; i2++) {
            View currentChild = getChildAt(i2);
            if (currentChild.getVisibility() != 8) {
                int currentChildWidth = currentChild.getMeasuredWidth();
                LayoutParams lp2 = (LayoutParams) currentChild.getLayoutParams();
                boolean z = false;
                if (isRTL()) {
                    if ((xPos2 - getPaddingLeft()) - currentChildWidth < 0) {
                        z = true;
                    }
                } else if (xPos2 + currentChildWidth > fullRowWidth) {
                    z = true;
                }
                boolean childDoesNotFitOnRow = z;
                if (childDoesNotFitOnRow) {
                    lp = lp2;
                    layoutChildrenOnRow(rowStartIdx, i2, fullRowWidth, xPos2, yPos2, lastHorizontalSpacing);
                    if (isRTL()) {
                        paddingLeft = fullRowWidth - getPaddingRight();
                    } else {
                        paddingLeft = getPaddingLeft();
                    }
                    xPos2 = paddingLeft;
                    yPos2 += this.mLineHeight;
                    rowStartIdx = i2;
                } else {
                    lp = lp2;
                }
                if (isRTL()) {
                    i = (xPos2 - currentChildWidth) - lp.mHorizontalSpacing;
                } else {
                    i = xPos2 + currentChildWidth + lp.mHorizontalSpacing;
                }
                xPos2 = i;
                lastHorizontalSpacing = lp.mHorizontalSpacing;
            }
        }
        if (rowStartIdx < childCount) {
            layoutChildrenOnRow(rowStartIdx, childCount, fullRowWidth, xPos2, yPos2, lastHorizontalSpacing);
        }
    }

    private int getHorizontalVerticalSpacing() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(1, 4.0f, displayMetrics);
    }

    private void layoutChildrenOnRow(int startIndex, int endIndex, int fullRowWidth, int xPos, int yPos, int lastHorizontalSpacing) {
        int nextChildWidth;
        if (!isRTL()) {
            xPos = ((getPaddingLeft() + fullRowWidth) - xPos) + lastHorizontalSpacing;
        }
        for (int j = startIndex; j < endIndex; j++) {
            View currentChild = getChildAt(j);
            int currentChildWidth = currentChild.getMeasuredWidth();
            LayoutParams lp = (LayoutParams) currentChild.getLayoutParams();
            if (isRTL() && j == startIndex) {
                xPos = (((fullRowWidth - xPos) - getPaddingRight()) - currentChildWidth) - lp.mHorizontalSpacing;
            }
            currentChild.layout(xPos, yPos, xPos + currentChildWidth, currentChild.getMeasuredHeight() + yPos);
            if (isRTL()) {
                if (j < endIndex - 1) {
                    nextChildWidth = getChildAt(j + 1).getMeasuredWidth();
                } else {
                    nextChildWidth = 0;
                }
                xPos -= lp.mHorizontalSpacing + nextChildWidth;
            } else {
                xPos += lp.mHorizontalSpacing + currentChildWidth;
            }
        }
    }

    private boolean isRTL() {
        return this.mContext.getResources().getConfiguration().getLayoutDirection() == 1;
    }

    /* loaded from: classes21.dex */
    public static class LayoutParams extends ViewGroup.LayoutParams {
        public final int mHorizontalSpacing;
        public final int mVerticalSpacing;

        public LayoutParams(int horizontalSpacing, int verticalSpacing, ViewGroup.LayoutParams viewGroupLayout) {
            super(viewGroupLayout);
            this.mHorizontalSpacing = horizontalSpacing;
            this.mVerticalSpacing = verticalSpacing;
        }

        public LayoutParams(int mHorizontalSpacing, int verticalSpacing) {
            super(0, 0);
            this.mHorizontalSpacing = mHorizontalSpacing;
            this.mVerticalSpacing = verticalSpacing;
        }
    }
}
