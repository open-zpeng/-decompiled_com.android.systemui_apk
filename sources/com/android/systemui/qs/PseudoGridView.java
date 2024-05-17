package com.android.systemui.qs;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.android.systemui.R;
import java.lang.ref.WeakReference;
/* loaded from: classes21.dex */
public class PseudoGridView extends ViewGroup {
    private int mHorizontalSpacing;
    private int mNumColumns;
    private int mVerticalSpacing;

    public PseudoGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mNumColumns = 3;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PseudoGridView);
        int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.PseudoGridView_numColumns) {
                this.mNumColumns = a.getInt(attr, 3);
            } else if (attr == R.styleable.PseudoGridView_verticalSpacing) {
                this.mVerticalSpacing = a.getDimensionPixelSize(attr, 0);
            } else if (attr == R.styleable.PseudoGridView_horizontalSpacing) {
                this.mHorizontalSpacing = a.getDimensionPixelSize(attr, 0);
            }
        }
        a.recycle();
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (View.MeasureSpec.getMode(widthMeasureSpec) == 0) {
            throw new UnsupportedOperationException("Needs a maximum width");
        }
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int i = this.mNumColumns;
        int childWidth = (width - ((i - 1) * this.mHorizontalSpacing)) / i;
        int i2 = 1073741824;
        int childWidthSpec = View.MeasureSpec.makeMeasureSpec(childWidth, 1073741824);
        int totalHeight = 0;
        int children = getChildCount();
        int i3 = this.mNumColumns;
        int rows = ((children + i3) - 1) / i3;
        int row = 0;
        while (row < rows) {
            int i4 = this.mNumColumns;
            int startOfRow = row * i4;
            int endOfRow = Math.min(i4 + startOfRow, children);
            int maxHeight = 0;
            for (int i5 = startOfRow; i5 < endOfRow; i5++) {
                View child = getChildAt(i5);
                child.measure(childWidthSpec, 0);
                maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
            }
            int maxHeightSpec = View.MeasureSpec.makeMeasureSpec(maxHeight, i2);
            for (int i6 = startOfRow; i6 < endOfRow; i6++) {
                View child2 = getChildAt(i6);
                if (child2.getMeasuredHeight() != maxHeight) {
                    child2.measure(childWidthSpec, maxHeightSpec);
                }
            }
            totalHeight += maxHeight;
            if (row > 0) {
                totalHeight += this.mVerticalSpacing;
            }
            row++;
            i2 = 1073741824;
        }
        setMeasuredDimension(width, resolveSizeAndState(totalHeight, heightMeasureSpec, 0));
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        boolean isRtl = isLayoutRtl();
        int children = getChildCount();
        int i = this.mNumColumns;
        int rows = ((children + i) - 1) / i;
        int y = 0;
        for (int row = 0; row < rows; row++) {
            int x = isRtl ? getWidth() : 0;
            int maxHeight = 0;
            int i2 = this.mNumColumns;
            int startOfRow = row * i2;
            int endOfRow = Math.min(i2 + startOfRow, children);
            for (int i3 = startOfRow; i3 < endOfRow; i3++) {
                View child = getChildAt(i3);
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                if (isRtl) {
                    x -= width;
                }
                child.layout(x, y, x + width, y + height);
                maxHeight = Math.max(maxHeight, height);
                if (isRtl) {
                    x -= this.mHorizontalSpacing;
                } else {
                    x += this.mHorizontalSpacing + width;
                }
            }
            y += maxHeight;
            if (row > 0) {
                y += this.mVerticalSpacing;
            }
        }
    }

    /* loaded from: classes21.dex */
    public static class ViewGroupAdapterBridge extends DataSetObserver {
        private final BaseAdapter mAdapter;
        private boolean mReleased = false;
        private final WeakReference<ViewGroup> mViewGroup;

        public static void link(ViewGroup viewGroup, BaseAdapter adapter) {
            new ViewGroupAdapterBridge(viewGroup, adapter);
        }

        private ViewGroupAdapterBridge(ViewGroup viewGroup, BaseAdapter adapter) {
            this.mViewGroup = new WeakReference<>(viewGroup);
            this.mAdapter = adapter;
            this.mAdapter.registerDataSetObserver(this);
            refresh();
        }

        private void refresh() {
            if (this.mReleased) {
                return;
            }
            ViewGroup viewGroup = this.mViewGroup.get();
            if (viewGroup == null) {
                release();
                return;
            }
            int childCount = viewGroup.getChildCount();
            int adapterCount = this.mAdapter.getCount();
            int N = Math.max(childCount, adapterCount);
            for (int i = 0; i < N; i++) {
                if (i < adapterCount) {
                    View oldView = null;
                    if (i < childCount) {
                        oldView = viewGroup.getChildAt(i);
                    }
                    View newView = this.mAdapter.getView(i, oldView, viewGroup);
                    if (oldView == null) {
                        viewGroup.addView(newView);
                    } else if (oldView != newView) {
                        viewGroup.removeViewAt(i);
                        viewGroup.addView(newView, i);
                    }
                } else {
                    int lastIndex = viewGroup.getChildCount() - 1;
                    viewGroup.removeViewAt(lastIndex);
                }
            }
        }

        @Override // android.database.DataSetObserver
        public void onChanged() {
            refresh();
        }

        @Override // android.database.DataSetObserver
        public void onInvalidated() {
            release();
        }

        private void release() {
            if (!this.mReleased) {
                this.mReleased = true;
                this.mAdapter.unregisterDataSetObserver(this);
            }
        }
    }
}
