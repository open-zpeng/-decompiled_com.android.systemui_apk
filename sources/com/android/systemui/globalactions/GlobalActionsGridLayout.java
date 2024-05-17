package com.android.systemui.globalactions;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class GlobalActionsGridLayout extends GlobalActionsLayout {
    public GlobalActionsGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @VisibleForTesting
    protected void setupListView() {
        ListGridLayout listView = getListView();
        listView.setExpectedCount(this.mAdapter.countListItems());
        listView.setReverseSublists(shouldReverseSublists());
        listView.setReverseItems(shouldReverseListItems());
        listView.setSwapRowsAndColumns(shouldSwapRowsAndColumns());
    }

    @Override // com.android.systemui.globalactions.GlobalActionsLayout, com.android.systemui.MultiListLayout
    public void onUpdateList() {
        setupListView();
        super.onUpdateList();
        updateSeparatedItemSize();
    }

    @VisibleForTesting
    protected void updateSeparatedItemSize() {
        ViewGroup separated = getSeparatedView();
        if (separated.getChildCount() == 0) {
            return;
        }
        View firstChild = separated.getChildAt(0);
        ViewGroup.LayoutParams childParams = firstChild.getLayoutParams();
        if (separated.getChildCount() == 1) {
            childParams.width = -1;
            childParams.height = -1;
            return;
        }
        childParams.width = -2;
        childParams.height = -2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.globalactions.GlobalActionsLayout, com.android.systemui.MultiListLayout
    public ListGridLayout getListView() {
        return (ListGridLayout) super.getListView();
    }

    @Override // com.android.systemui.MultiListLayout
    protected void removeAllListViews() {
        ListGridLayout list = getListView();
        if (list != null) {
            list.removeAllItems();
        }
    }

    @Override // com.android.systemui.globalactions.GlobalActionsLayout
    protected void addToListView(View v, boolean reverse) {
        ListGridLayout list = getListView();
        if (list != null) {
            list.addItem(v);
        }
    }

    @Override // com.android.systemui.MultiListLayout
    public void removeAllItems() {
        ViewGroup separatedList = getSeparatedView();
        ListGridLayout list = getListView();
        if (separatedList != null) {
            separatedList.removeAllViews();
        }
        if (list != null) {
            list.removeAllItems();
        }
    }

    @VisibleForTesting
    protected boolean shouldReverseSublists() {
        if (getCurrentRotation() == 2) {
            return true;
        }
        return false;
    }

    @VisibleForTesting
    protected boolean shouldSwapRowsAndColumns() {
        if (getCurrentRotation() == 0) {
            return false;
        }
        return true;
    }

    @Override // com.android.systemui.globalactions.GlobalActionsLayout
    protected boolean shouldReverseListItems() {
        int rotation = getCurrentRotation();
        boolean reverse = false;
        if (rotation == 0 || rotation == 2) {
            reverse = !false;
        }
        if (getCurrentLayoutDirection() == 1) {
            boolean reverse2 = reverse ? false : true;
            return reverse2;
        }
        return reverse;
    }

    @VisibleForTesting
    protected float getAnimationDistance() {
        int rows = getListView().getRowCount();
        float gridItemSize = getContext().getResources().getDimension(R.dimen.global_actions_grid_item_height);
        return (rows * gridItemSize) / 2.0f;
    }

    @Override // com.android.systemui.MultiListLayout
    public float getAnimationOffsetX() {
        int currentRotation = getCurrentRotation();
        if (currentRotation != 1) {
            if (currentRotation == 2) {
                return -getAnimationDistance();
            }
            return 0.0f;
        }
        return getAnimationDistance();
    }

    @Override // com.android.systemui.MultiListLayout
    public float getAnimationOffsetY() {
        if (getCurrentRotation() == 0) {
            return getAnimationDistance();
        }
        return 0.0f;
    }
}
