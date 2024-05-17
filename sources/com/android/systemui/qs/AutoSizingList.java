package com.android.systemui.qs;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class AutoSizingList extends LinearLayout {
    private static final String TAG = "AutoSizingList";
    private ListAdapter mAdapter;
    private final Runnable mBindChildren;
    private int mCount;
    private final DataSetObserver mDataObserver;
    private boolean mEnableAutoSizing;
    private final Handler mHandler;
    private final int mItemSize;

    public AutoSizingList(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mBindChildren = new Runnable() { // from class: com.android.systemui.qs.AutoSizingList.1
            @Override // java.lang.Runnable
            public void run() {
                AutoSizingList.this.rebindChildren();
            }
        };
        this.mDataObserver = new DataSetObserver() { // from class: com.android.systemui.qs.AutoSizingList.2
            @Override // android.database.DataSetObserver
            public void onChanged() {
                if (AutoSizingList.this.mCount > AutoSizingList.this.getDesiredCount()) {
                    AutoSizingList autoSizingList = AutoSizingList.this;
                    autoSizingList.mCount = autoSizingList.getDesiredCount();
                }
                AutoSizingList.this.postRebindChildren();
            }

            @Override // android.database.DataSetObserver
            public void onInvalidated() {
                AutoSizingList.this.postRebindChildren();
            }
        };
        this.mHandler = new Handler();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoSizingList);
        this.mItemSize = a.getDimensionPixelSize(R.styleable.AutoSizingList_itemHeight, 0);
        this.mEnableAutoSizing = a.getBoolean(R.styleable.AutoSizingList_enableAutoSizing, true);
        a.recycle();
    }

    public void setAdapter(ListAdapter adapter) {
        ListAdapter listAdapter = this.mAdapter;
        if (listAdapter != null) {
            listAdapter.unregisterDataSetObserver(this.mDataObserver);
        }
        this.mAdapter = adapter;
        if (adapter != null) {
            adapter.registerDataSetObserver(this.mDataObserver);
        }
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count;
        int requestedHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        if (requestedHeight != 0 && this.mCount != (count = getItemCount(requestedHeight))) {
            postRebindChildren();
            this.mCount = count;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int getItemCount(int requestedHeight) {
        int desiredCount = getDesiredCount();
        return this.mEnableAutoSizing ? Math.min(requestedHeight / this.mItemSize, desiredCount) : desiredCount;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getDesiredCount() {
        ListAdapter listAdapter = this.mAdapter;
        if (listAdapter != null) {
            return listAdapter.getCount();
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postRebindChildren() {
        this.mHandler.post(this.mBindChildren);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Incorrect condition in loop: B:19:0x002f */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void rebindChildren() {
        /*
            r3 = this;
            android.widget.ListAdapter r0 = r3.mAdapter
            if (r0 != 0) goto L5
            return
        L5:
            r0 = 0
        L6:
            int r1 = r3.mCount
            if (r0 >= r1) goto L29
            int r1 = r3.getChildCount()
            if (r0 >= r1) goto L15
            android.view.View r1 = r3.getChildAt(r0)
            goto L16
        L15:
            r1 = 0
        L16:
            android.widget.ListAdapter r2 = r3.mAdapter
            android.view.View r2 = r2.getView(r0, r1, r3)
            if (r2 == r1) goto L26
            if (r1 == 0) goto L23
            r3.removeView(r1)
        L23:
            r3.addView(r2, r0)
        L26:
            int r0 = r0 + 1
            goto L6
        L29:
            int r0 = r3.getChildCount()
            int r1 = r3.mCount
            if (r0 <= r1) goto L3b
            int r0 = r3.getChildCount()
            int r0 = r0 + (-1)
            r3.removeViewAt(r0)
            goto L29
        L3b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.qs.AutoSizingList.rebindChildren():void");
    }
}
