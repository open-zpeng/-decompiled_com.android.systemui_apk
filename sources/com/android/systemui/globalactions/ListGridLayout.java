package com.android.systemui.globalactions;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.android.internal.annotations.VisibleForTesting;
/* loaded from: classes21.dex */
public class ListGridLayout extends LinearLayout {
    private static final String TAG = "ListGridLayout";
    private final int[][] mConfigs;
    private int mCurrentCount;
    private int mExpectedCount;
    private boolean mReverseItems;
    private boolean mReverseSublists;
    private boolean mSwapRowsAndColumns;

    public ListGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCurrentCount = 0;
        this.mConfigs = new int[][]{new int[]{0, 0}, new int[]{1, 1}, new int[]{1, 2}, new int[]{1, 3}, new int[]{2, 2}, new int[]{2, 3}, new int[]{2, 3}, new int[]{3, 3}, new int[]{3, 3}, new int[]{3, 3}};
    }

    public void setSwapRowsAndColumns(boolean swap) {
        this.mSwapRowsAndColumns = swap;
    }

    public void setReverseSublists(boolean reverse) {
        this.mReverseSublists = reverse;
    }

    public void setReverseItems(boolean reverse) {
        this.mReverseItems = reverse;
    }

    public void removeAllItems() {
        for (int i = 0; i < getChildCount(); i++) {
            ViewGroup subList = getSublist(i);
            if (subList != null) {
                subList.removeAllViews();
                subList.setVisibility(8);
            }
        }
        this.mCurrentCount = 0;
    }

    public void addItem(View item) {
        ViewGroup parent = getParentView(this.mCurrentCount, this.mReverseSublists, this.mSwapRowsAndColumns);
        if (this.mReverseItems) {
            parent.addView(item, 0);
        } else {
            parent.addView(item);
        }
        parent.setVisibility(0);
        this.mCurrentCount++;
    }

    @VisibleForTesting
    protected ViewGroup getParentView(int index, boolean reverseSublists, boolean swapRowsAndColumns) {
        if (getRowCount() == 0 || index < 0) {
            return null;
        }
        int targetIndex = Math.min(index, getMaxElementCount() - 1);
        int row = getParentViewIndex(targetIndex, reverseSublists, swapRowsAndColumns);
        return getSublist(row);
    }

    @VisibleForTesting
    protected ViewGroup getSublist(int index) {
        return (ViewGroup) getChildAt(index);
    }

    private int reverseSublistIndex(int index) {
        return getChildCount() - (index + 1);
    }

    private int getParentViewIndex(int index, boolean reverseSublists, boolean swapRowsAndColumns) {
        int sublistIndex;
        int rows = getRowCount();
        if (swapRowsAndColumns) {
            sublistIndex = (int) Math.floor(index / rows);
        } else {
            sublistIndex = index % rows;
        }
        if (reverseSublists) {
            return reverseSublistIndex(sublistIndex);
        }
        return sublistIndex;
    }

    public void setExpectedCount(int count) {
        this.mExpectedCount = count;
    }

    private int getMaxElementCount() {
        return this.mConfigs.length - 1;
    }

    private int[] getConfig() {
        if (this.mExpectedCount < 0) {
            return this.mConfigs[0];
        }
        int targetElements = Math.min(getMaxElementCount(), this.mExpectedCount);
        return this.mConfigs[targetElements];
    }

    public int getRowCount() {
        return getConfig()[0];
    }

    public int getColumnCount() {
        return getConfig()[1];
    }
}
