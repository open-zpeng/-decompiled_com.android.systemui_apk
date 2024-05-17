package com.android.systemui;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import com.android.systemui.util.leak.RotationUtils;
/* loaded from: classes21.dex */
public abstract class MultiListLayout extends LinearLayout {
    protected MultiListAdapter mAdapter;
    protected boolean mHasOutsideTouch;
    protected int mRotation;
    protected RotationListener mRotationListener;

    /* loaded from: classes21.dex */
    public interface RotationListener {
        void onRotate(int i, int i2);
    }

    public abstract float getAnimationOffsetX();

    public abstract float getAnimationOffsetY();

    protected abstract ViewGroup getListView();

    protected abstract ViewGroup getSeparatedView();

    public abstract void setDivisionView(View view);

    public MultiListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mRotation = RotationUtils.getRotation(context);
    }

    public void setListViewAccessibilityDelegate(View.AccessibilityDelegate delegate) {
        getListView().setAccessibilityDelegate(delegate);
    }

    protected void setSeparatedViewVisibility(boolean visible) {
        getSeparatedView().setVisibility(visible ? 0 : 8);
    }

    public void setAdapter(MultiListAdapter adapter) {
        this.mAdapter = adapter;
    }

    public void setOutsideTouchListener(View.OnClickListener onClickListener) {
        this.mHasOutsideTouch = true;
        requestLayout();
        setOnClickListener(onClickListener);
        setClickable(true);
        setFocusable(true);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int newRotation = RotationUtils.getRotation(this.mContext);
        int i = this.mRotation;
        if (newRotation != i) {
            rotate(i, newRotation);
            this.mRotation = newRotation;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void rotate(int from, int to) {
        RotationListener rotationListener = this.mRotationListener;
        if (rotationListener != null) {
            rotationListener.onRotate(from, to);
        }
    }

    public void updateList() {
        if (this.mAdapter == null) {
            throw new IllegalStateException("mAdapter must be set before calling updateList");
        }
        onUpdateList();
    }

    protected void removeAllSeparatedViews() {
        ViewGroup separated = getSeparatedView();
        if (separated != null) {
            separated.removeAllViews();
        }
    }

    protected void removeAllListViews() {
        ViewGroup list = getListView();
        if (list != null) {
            list.removeAllViews();
        }
    }

    protected void removeAllItems() {
        removeAllListViews();
        removeAllSeparatedViews();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onUpdateList() {
        removeAllItems();
        setSeparatedViewVisibility(this.mAdapter.hasSeparatedItems());
    }

    public void setRotationListener(RotationListener listener) {
        this.mRotationListener = listener;
    }

    public static MultiListLayout get(View v) {
        if (v instanceof MultiListLayout) {
            return (MultiListLayout) v;
        }
        if (v.getParent() instanceof View) {
            return get((View) v.getParent());
        }
        return null;
    }

    /* loaded from: classes21.dex */
    public static abstract class MultiListAdapter extends BaseAdapter {
        public abstract int countListItems();

        public abstract int countSeparatedItems();

        public abstract void onClickItem(int i);

        public abstract boolean onLongClickItem(int i);

        public abstract boolean shouldBeSeparated(int i);

        public boolean hasSeparatedItems() {
            return countSeparatedItems() > 0;
        }
    }
}
