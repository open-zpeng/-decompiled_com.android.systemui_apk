package com.android.systemui.globalactions;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.HardwareBgDrawable;
import com.android.systemui.MultiListLayout;
import com.android.systemui.R;
import com.android.systemui.util.leak.RotationUtils;
import java.util.Locale;
/* loaded from: classes21.dex */
public abstract class GlobalActionsLayout extends MultiListLayout {
    boolean mBackgroundsSet;

    protected abstract boolean shouldReverseListItems();

    public GlobalActionsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void setBackgrounds() {
        int gridBackgroundColor = getResources().getColor(R.color.global_actions_grid_background, null);
        int separatedBackgroundColor = getResources().getColor(R.color.global_actions_separated_background, null);
        HardwareBgDrawable listBackground = new HardwareBgDrawable(true, true, getContext());
        HardwareBgDrawable separatedBackground = new HardwareBgDrawable(true, true, getContext());
        listBackground.setTint(gridBackgroundColor);
        separatedBackground.setTint(separatedBackgroundColor);
        getListView().setBackground(listBackground);
        getSeparatedView().setBackground(separatedBackground);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getListView() != null && !this.mBackgroundsSet) {
            setBackgrounds();
            this.mBackgroundsSet = true;
        }
    }

    protected void addToListView(View v, boolean reverse) {
        if (reverse) {
            getListView().addView(v, 0);
        } else {
            getListView().addView(v);
        }
    }

    protected void addToSeparatedView(View v, boolean reverse) {
        if (reverse) {
            getSeparatedView().addView(v, 0);
        } else {
            getSeparatedView().addView(v);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @VisibleForTesting
    public int getCurrentLayoutDirection() {
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @VisibleForTesting
    public int getCurrentRotation() {
        return RotationUtils.getRotation(this.mContext);
    }

    @Override // com.android.systemui.MultiListLayout
    public void onUpdateList() {
        super.onUpdateList();
        ViewGroup separatedView = getSeparatedView();
        ViewGroup listView = getListView();
        for (int i = 0; i < this.mAdapter.getCount(); i++) {
            boolean separated = this.mAdapter.shouldBeSeparated(i);
            View v = separated ? this.mAdapter.getView(i, null, separatedView) : this.mAdapter.getView(i, null, listView);
            if (separated) {
                addToSeparatedView(v, false);
            } else {
                addToListView(v, shouldReverseListItems());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.MultiListLayout
    public ViewGroup getSeparatedView() {
        return (ViewGroup) findViewById(R.id.separated_button);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.MultiListLayout
    public ViewGroup getListView() {
        return (ViewGroup) findViewById(16908298);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public View getWrapper() {
        return getChildAt(0);
    }

    @Override // com.android.systemui.MultiListLayout
    public void setDivisionView(View v) {
    }
}
