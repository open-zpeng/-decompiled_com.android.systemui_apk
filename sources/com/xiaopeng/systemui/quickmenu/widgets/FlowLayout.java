package com.xiaopeng.systemui.quickmenu.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.R;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes24.dex */
public class FlowLayout extends ViewGroup {
    private static final int DEFAULT_CHILD_SPACING = 0;
    private static final int DEFAULT_CHILD_SPACING_FOR_LAST_ROW = -65538;
    private static final boolean DEFAULT_FLOW = true;
    private static final int DEFAULT_MAX_ROWS = Integer.MAX_VALUE;
    private static final float DEFAULT_ROW_SPACING = 0.0f;
    private static final boolean DEFAULT_RTL = false;
    private static final String LOG_TAG = FlowLayout.class.getSimpleName();
    private static final int ROW_VERTICAL_GRAVITY_AUTO = -65536;
    public static final int SPACING_ALIGN = -65537;
    public static final int SPACING_AUTO = -65536;
    private static final int SPACING_UNDEFINED = -65538;
    private static final int UNSPECIFIED_GRAVITY = -1;
    private float mAdjustedRowSpacing;
    private List<Integer> mChildNumForRow;
    private int mChildSpacing;
    private int mChildSpacingForLastRow;
    private int mExactMeasuredHeight;
    private boolean mFlow;
    private int mGravity;
    private List<Integer> mHeightForRow;
    private List<Float> mHorizontalSpacingForRow;
    private int mMaxRows;
    private int mMinChildSpacing;
    private float mRowSpacing;
    private int mRowVerticalGravity;
    private boolean mRtl;
    private List<Integer> mWidthForRow;

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mFlow = true;
        this.mChildSpacing = 0;
        this.mMinChildSpacing = 0;
        this.mChildSpacingForLastRow = -65538;
        this.mRowSpacing = 0.0f;
        this.mAdjustedRowSpacing = 0.0f;
        this.mRtl = false;
        this.mMaxRows = Integer.MAX_VALUE;
        this.mGravity = -1;
        this.mRowVerticalGravity = -65536;
        this.mHorizontalSpacingForRow = new ArrayList();
        this.mHeightForRow = new ArrayList();
        this.mWidthForRow = new ArrayList();
        this.mChildNumForRow = new ArrayList();
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FlowLayout, 0, 0);
        try {
            this.mFlow = a.getBoolean(3, true);
            this.mChildSpacing = getDimensionOrInt(a, 1, (int) dpToPx(0.0f));
            this.mMinChildSpacing = getDimensionOrInt(a, 5, (int) dpToPx(0.0f));
            this.mChildSpacingForLastRow = getDimensionOrInt(a, 2, -65538);
            this.mRowSpacing = getDimensionOrInt(a, 6, (int) dpToPx(0.0f));
            this.mMaxRows = a.getInt(4, Integer.MAX_VALUE);
            this.mRtl = a.getBoolean(8, false);
            this.mGravity = a.getInt(0, -1);
            this.mRowVerticalGravity = a.getInt(7, -65536);
        } finally {
            a.recycle();
        }
    }

    private int getDimensionOrInt(TypedArray a, int index, int defValue) {
        TypedValue tv = new TypedValue();
        a.getValue(index, tv);
        if (tv.type == 5) {
            return a.getDimensionPixelSize(index, defValue);
        }
        return a.getInt(index, defValue);
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childSpacing;
        int measuredWidth;
        float rowSpacing;
        int heightSize;
        int min;
        int maxChildHeightInRow;
        int rowTotalChildWidth;
        int i;
        int heightMode;
        int childCount;
        int childCount2;
        View child;
        int childNumInRow;
        int widthMode;
        int rowWidth;
        int heightSize2;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int widthMode2 = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightSize3 = View.MeasureSpec.getSize(heightMeasureSpec);
        int heightMode2 = View.MeasureSpec.getMode(heightMeasureSpec);
        this.mHorizontalSpacingForRow.clear();
        this.mHeightForRow.clear();
        this.mWidthForRow.clear();
        this.mChildNumForRow.clear();
        int childCount3 = getChildCount();
        int rowSize = (widthSize - getPaddingLeft()) - getPaddingRight();
        boolean allowFlow = widthMode2 != 0 && this.mFlow;
        if (this.mChildSpacing != -65536 || widthMode2 != 0) {
            childSpacing = this.mChildSpacing;
        } else {
            childSpacing = 0;
        }
        float tmpSpacing = childSpacing == -65536 ? this.mMinChildSpacing : childSpacing;
        int measuredHeight = 0;
        int maxChildHeightInRow2 = 0;
        int rowWidth2 = 0;
        int rowWidth3 = 0;
        int maxChildHeightInRow3 = 0;
        int childNumInRow2 = 0;
        int rowTotalChildWidth2 = 0;
        while (rowTotalChildWidth2 < childCount3) {
            int childNumInRow3 = maxChildHeightInRow3;
            View child2 = getChildAt(rowTotalChildWidth2);
            int measuredWidth2 = maxChildHeightInRow2;
            int rowWidth4 = rowWidth2;
            if (child2.getVisibility() == 8) {
                i = rowTotalChildWidth2;
                heightMode = heightMode2;
                childCount = childCount3;
                maxChildHeightInRow3 = childNumInRow3;
                maxChildHeightInRow2 = measuredWidth2;
                rowWidth2 = rowWidth4;
                childNumInRow = widthMode2;
                rowWidth = heightSize3;
            } else {
                ViewGroup.LayoutParams childParams = child2.getLayoutParams();
                int horizontalMargin = 0;
                int verticalMargin = 0;
                if (childParams instanceof ViewGroup.MarginLayoutParams) {
                    childCount = childCount3;
                    childCount2 = measuredWidth2;
                    rowWidth = heightSize3;
                    heightSize2 = rowWidth4;
                    maxChildHeightInRow = rowWidth3;
                    heightMode = heightMode2;
                    child = child2;
                    childNumInRow = widthMode2;
                    widthMode = childNumInRow3;
                    rowTotalChildWidth = childNumInRow2;
                    i = rowTotalChildWidth2;
                    measureChildWithMargins(child2, widthMeasureSpec, 0, heightMeasureSpec, measuredHeight);
                    ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) childParams;
                    horizontalMargin = marginParams.leftMargin + marginParams.rightMargin;
                    verticalMargin = marginParams.topMargin + marginParams.bottomMargin;
                } else {
                    maxChildHeightInRow = rowWidth3;
                    rowTotalChildWidth = childNumInRow2;
                    i = rowTotalChildWidth2;
                    heightMode = heightMode2;
                    childCount = childCount3;
                    childCount2 = measuredWidth2;
                    child = child2;
                    childNumInRow = widthMode2;
                    widthMode = childNumInRow3;
                    rowWidth = heightSize3;
                    heightSize2 = rowWidth4;
                    measureChild(child, widthMeasureSpec, heightMeasureSpec);
                }
                int childWidth = child.getMeasuredWidth() + horizontalMargin;
                int childHeight = child.getMeasuredHeight() + verticalMargin;
                if (allowFlow && heightSize2 + childWidth > rowSize) {
                    this.mHorizontalSpacingForRow.add(Float.valueOf(getSpacingForRow(childSpacing, rowSize, rowTotalChildWidth, widthMode)));
                    this.mChildNumForRow.add(Integer.valueOf(widthMode));
                    this.mHeightForRow.add(Integer.valueOf(maxChildHeightInRow));
                    this.mWidthForRow.add(Integer.valueOf(heightSize2 - ((int) tmpSpacing)));
                    if (this.mHorizontalSpacingForRow.size() <= this.mMaxRows) {
                        measuredHeight += maxChildHeightInRow;
                    }
                    int measuredWidth3 = Math.max(childCount2, heightSize2);
                    int rowWidth5 = ((int) tmpSpacing) + childWidth;
                    rowWidth3 = childHeight;
                    maxChildHeightInRow2 = measuredWidth3;
                    childNumInRow2 = childWidth;
                    maxChildHeightInRow3 = 1;
                    rowWidth2 = rowWidth5;
                } else {
                    int childNumInRow4 = widthMode + 1;
                    rowWidth2 = (int) (heightSize2 + childWidth + tmpSpacing);
                    int rowTotalChildWidth3 = rowTotalChildWidth + childWidth;
                    rowWidth3 = Math.max(maxChildHeightInRow, childHeight);
                    maxChildHeightInRow3 = childNumInRow4;
                    childNumInRow2 = rowTotalChildWidth3;
                    maxChildHeightInRow2 = childCount2;
                }
            }
            rowTotalChildWidth2 = i + 1;
            widthMode2 = childNumInRow;
            heightSize3 = rowWidth;
            childCount3 = childCount;
            heightMode2 = heightMode;
        }
        int widthMode3 = widthMode2;
        int heightSize4 = heightSize3;
        int heightMode3 = heightMode2;
        int childCount4 = maxChildHeightInRow2;
        int heightSize5 = rowWidth2;
        int measuredWidth4 = rowWidth3;
        int widthMode4 = maxChildHeightInRow3;
        int i2 = childNumInRow2;
        int rowWidth6 = this.mChildSpacingForLastRow;
        if (rowWidth6 == -65537) {
            if (this.mHorizontalSpacingForRow.size() >= 1) {
                List<Float> list = this.mHorizontalSpacingForRow;
                list.add(list.get(list.size() - 1));
            } else {
                this.mHorizontalSpacingForRow.add(Float.valueOf(getSpacingForRow(childSpacing, rowSize, i2, widthMode4)));
            }
        } else if (rowWidth6 != -65538) {
            this.mHorizontalSpacingForRow.add(Float.valueOf(getSpacingForRow(rowWidth6, rowSize, i2, widthMode4)));
        } else {
            this.mHorizontalSpacingForRow.add(Float.valueOf(getSpacingForRow(childSpacing, rowSize, i2, widthMode4)));
        }
        this.mChildNumForRow.add(Integer.valueOf(widthMode4));
        this.mHeightForRow.add(Integer.valueOf(measuredWidth4));
        this.mWidthForRow.add(Integer.valueOf(heightSize5 - ((int) tmpSpacing)));
        if (this.mHorizontalSpacingForRow.size() <= this.mMaxRows) {
            measuredHeight += measuredWidth4;
        }
        int measuredWidth5 = Math.max(childCount4, heightSize5);
        if (childSpacing == -65536) {
            measuredWidth = widthSize;
        } else if (widthMode3 == 0) {
            measuredWidth = getPaddingLeft() + measuredWidth5 + getPaddingRight();
        } else {
            measuredWidth = Math.min(getPaddingLeft() + measuredWidth5 + getPaddingRight(), widthSize);
        }
        int measuredHeight2 = measuredHeight + getPaddingTop() + getPaddingBottom();
        int rowNum = Math.min(this.mHorizontalSpacingForRow.size(), this.mMaxRows);
        if (this.mRowSpacing != -65536.0f || heightMode3 != 0) {
            rowSpacing = this.mRowSpacing;
        } else {
            rowSpacing = 0.0f;
        }
        if (rowSpacing == -65536.0f) {
            if (rowNum > 1) {
                this.mAdjustedRowSpacing = (heightSize4 - measuredHeight2) / (rowNum - 1);
            } else {
                this.mAdjustedRowSpacing = 0.0f;
            }
            measuredHeight2 = heightSize4;
            heightSize = heightSize4;
        } else {
            this.mAdjustedRowSpacing = rowSpacing;
            if (rowNum > 1) {
                if (heightMode3 == 0) {
                    int maxChildHeightInRow4 = rowNum - 1;
                    min = (int) (measuredHeight2 + (this.mAdjustedRowSpacing * maxChildHeightInRow4));
                    heightSize = heightSize4;
                } else {
                    heightSize = heightSize4;
                    min = Math.min((int) (measuredHeight2 + (this.mAdjustedRowSpacing * (rowNum - 1))), heightSize);
                }
                measuredHeight2 = min;
            } else {
                heightSize = heightSize4;
            }
        }
        this.mExactMeasuredHeight = measuredHeight2;
        setMeasuredDimension(widthMode3 == 1073741824 ? widthSize : measuredWidth, heightMode3 == 1073741824 ? heightSize : measuredHeight2);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int horizontalGravity;
        int verticalGravity;
        int rowHeight;
        int y;
        int verticalRowGravity;
        int rowCount;
        int marginRight;
        int tt;
        int horizontalGravity2;
        int y2;
        float spacing;
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int childIdx = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int x = this.mRtl ? getWidth() - paddingRight : paddingLeft;
        int y3 = childIdx;
        int i = this.mGravity;
        int verticalGravity2 = i & 112;
        int horizontalGravity3 = i & 7;
        if (verticalGravity2 != 16) {
            if (verticalGravity2 == 80) {
                y3 += (((b - t) - childIdx) - paddingBottom) - this.mExactMeasuredHeight;
            }
        } else {
            y3 += ((((b - t) - childIdx) - paddingBottom) - this.mExactMeasuredHeight) / 2;
        }
        int offset = paddingLeft + paddingRight;
        int layoutWidth = r - l;
        int x2 = x + getHorizontalGravityOffsetForRow(horizontalGravity3, layoutWidth, offset, 0);
        int verticalRowGravity2 = this.mRowVerticalGravity & 112;
        int rowCount2 = this.mChildNumForRow.size();
        int childIdx2 = 0;
        int y4 = y3;
        int y5 = x2;
        int x3 = 0;
        while (x3 < Math.min(rowCount2, this.mMaxRows)) {
            int childNum = this.mChildNumForRow.get(x3).intValue();
            int rowHeight2 = this.mHeightForRow.get(x3).intValue();
            int paddingLeft2 = paddingLeft;
            float spacing2 = this.mHorizontalSpacingForRow.get(x3).floatValue();
            int paddingBottom2 = paddingBottom;
            int x4 = y5;
            int x5 = 0;
            int paddingTop = childIdx;
            int paddingTop2 = childIdx2;
            while (true) {
                if (x5 >= childNum) {
                    horizontalGravity = horizontalGravity3;
                    verticalGravity = verticalGravity2;
                    rowHeight = rowHeight2;
                    y = y4;
                    verticalRowGravity = verticalRowGravity2;
                    rowCount = rowCount2;
                    break;
                }
                verticalGravity = verticalGravity2;
                if (paddingTop2 >= getChildCount()) {
                    horizontalGravity = horizontalGravity3;
                    rowHeight = rowHeight2;
                    y = y4;
                    verticalRowGravity = verticalRowGravity2;
                    rowCount = rowCount2;
                    break;
                }
                int childIdx3 = paddingTop2 + 1;
                View child = getChildAt(paddingTop2);
                int childNum2 = childNum;
                if (child.getVisibility() == 8) {
                    paddingTop2 = childIdx3;
                    verticalGravity2 = verticalGravity;
                    childNum = childNum2;
                } else {
                    int i2 = x5 + 1;
                    ViewGroup.LayoutParams childParams = child.getLayoutParams();
                    int marginLeft = 0;
                    int marginTop = 0;
                    int marginBottom = 0;
                    if (childParams instanceof ViewGroup.MarginLayoutParams) {
                        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) childParams;
                        marginLeft = marginParams.leftMargin;
                        int marginRight2 = marginParams.rightMargin;
                        int marginRight3 = marginParams.topMargin;
                        marginTop = marginRight3;
                        int marginTop2 = marginParams.bottomMargin;
                        marginBottom = marginTop2;
                        marginRight = marginRight2;
                    } else {
                        marginRight = 0;
                    }
                    int childWidth = child.getMeasuredWidth();
                    int childHeight = child.getMeasuredHeight();
                    int tt2 = y4 + marginTop;
                    int rowCount3 = rowCount2;
                    if (verticalRowGravity2 == 80) {
                        int tt3 = ((y4 + rowHeight2) - marginBottom) - childHeight;
                        tt = tt3;
                    } else if (verticalRowGravity2 != 16) {
                        tt = tt2;
                    } else {
                        int tt4 = y4 + marginTop + ((((rowHeight2 - marginTop) - marginBottom) - childHeight) / 2);
                        tt = tt4;
                    }
                    int tt5 = verticalRowGravity2;
                    int verticalRowGravity3 = tt + childHeight;
                    int rowHeight3 = rowHeight2;
                    if (this.mRtl) {
                        int l1 = (x4 - marginRight) - childWidth;
                        y2 = y4;
                        child.layout(l1, tt, x4 - marginRight, verticalRowGravity3);
                        horizontalGravity2 = horizontalGravity3;
                        x4 = (int) (x4 - (((childWidth + spacing2) + marginLeft) + marginRight));
                        spacing = spacing2;
                    } else {
                        horizontalGravity2 = horizontalGravity3;
                        y2 = y4;
                        int r2 = x4 + marginLeft + childWidth;
                        child.layout(x4 + marginLeft, tt, r2, verticalRowGravity3);
                        float f = childWidth + spacing2;
                        spacing = spacing2;
                        float spacing3 = marginLeft;
                        x4 = (int) (x4 + f + spacing3 + marginRight);
                    }
                    paddingTop2 = childIdx3;
                    verticalGravity2 = verticalGravity;
                    childNum = childNum2;
                    x5 = i2;
                    verticalRowGravity2 = tt5;
                    rowCount2 = rowCount3;
                    rowHeight2 = rowHeight3;
                    y4 = y2;
                    spacing2 = spacing;
                    horizontalGravity3 = horizontalGravity2;
                }
            }
            int x6 = this.mRtl ? getWidth() - paddingRight : paddingLeft2;
            horizontalGravity3 = horizontalGravity;
            y5 = x6 + getHorizontalGravityOffsetForRow(horizontalGravity3, layoutWidth, offset, x3 + 1);
            int x7 = y;
            y4 = (int) (x7 + rowHeight + this.mAdjustedRowSpacing);
            x3++;
            childIdx2 = paddingTop2;
            paddingLeft = paddingLeft2;
            childIdx = paddingTop;
            paddingBottom = paddingBottom2;
            verticalGravity2 = verticalGravity;
            verticalRowGravity2 = verticalRowGravity;
            rowCount2 = rowCount;
        }
        for (int i3 = childIdx2; i3 < getChildCount(); i3++) {
            View child2 = getChildAt(i3);
            if (child2.getVisibility() != 8) {
                child2.layout(0, 0, 0, 0);
            }
        }
    }

    private int getHorizontalGravityOffsetForRow(int horizontalGravity, int parentWidth, int horizontalPadding, int row) {
        if (this.mChildSpacing == -65536 || row >= this.mWidthForRow.size() || row >= this.mChildNumForRow.size() || this.mChildNumForRow.get(row).intValue() <= 0) {
            return 0;
        }
        if (horizontalGravity == 1) {
            int offset = ((parentWidth - horizontalPadding) - this.mWidthForRow.get(row).intValue()) / 2;
            return offset;
        } else if (horizontalGravity != 5) {
            return 0;
        } else {
            int offset2 = (parentWidth - horizontalPadding) - this.mWidthForRow.get(row).intValue();
            return offset2;
        }
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new ViewGroup.MarginLayoutParams(p);
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new ViewGroup.MarginLayoutParams(getContext(), attrs);
    }

    public boolean isFlow() {
        return this.mFlow;
    }

    public void setFlow(boolean flow) {
        this.mFlow = flow;
        requestLayout();
    }

    public int getChildSpacing() {
        return this.mChildSpacing;
    }

    public void setChildSpacing(int childSpacing) {
        this.mChildSpacing = childSpacing;
        requestLayout();
    }

    public int getChildSpacingForLastRow() {
        return this.mChildSpacingForLastRow;
    }

    public void setChildSpacingForLastRow(int childSpacingForLastRow) {
        this.mChildSpacingForLastRow = childSpacingForLastRow;
        requestLayout();
    }

    public float getRowSpacing() {
        return this.mRowSpacing;
    }

    public void setRowSpacing(float rowSpacing) {
        this.mRowSpacing = rowSpacing;
        requestLayout();
    }

    public int getMaxRows() {
        return this.mMaxRows;
    }

    public void setMaxRows(int maxRows) {
        this.mMaxRows = maxRows;
        requestLayout();
    }

    public void setGravity(int gravity) {
        if (this.mGravity != gravity) {
            this.mGravity = gravity;
            requestLayout();
        }
    }

    public void setRowVerticalGravity(int rowVerticalGravity) {
        if (this.mRowVerticalGravity != rowVerticalGravity) {
            this.mRowVerticalGravity = rowVerticalGravity;
            requestLayout();
        }
    }

    public boolean isRtl() {
        return this.mRtl;
    }

    public void setRtl(boolean rtl) {
        this.mRtl = rtl;
        requestLayout();
    }

    public int getMinChildSpacing() {
        return this.mMinChildSpacing;
    }

    public void setMinChildSpacing(int minChildSpacing) {
        this.mMinChildSpacing = minChildSpacing;
        requestLayout();
    }

    public int getRowsCount() {
        return this.mChildNumForRow.size();
    }

    private float getSpacingForRow(int spacingAttribute, int rowSize, int usedSize, int childNum) {
        if (spacingAttribute == -65536) {
            if (childNum > 1) {
                float spacing = (rowSize - usedSize) / (childNum - 1);
                return spacing;
            }
            return 0.0f;
        }
        float spacing2 = spacingAttribute;
        return spacing2;
    }

    private float dpToPx(float dp) {
        return TypedValue.applyDimension(1, dp, getResources().getDisplayMetrics());
    }
}
