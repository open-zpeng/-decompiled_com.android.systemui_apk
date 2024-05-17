package com.android.systemui.globalactions;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class GlobalActionsColumnLayout extends GlobalActionsLayout {
    private boolean mLastSnap;

    public GlobalActionsColumnLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        post(new Runnable() { // from class: com.android.systemui.globalactions.-$$Lambda$GlobalActionsColumnLayout$iug9piEk-yt27o1Db7MoL30coo4
            @Override // java.lang.Runnable
            public final void run() {
                GlobalActionsColumnLayout.this.lambda$onLayout$0$GlobalActionsColumnLayout();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.globalactions.GlobalActionsLayout, android.widget.LinearLayout, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override // com.android.systemui.globalactions.GlobalActionsLayout
    @VisibleForTesting
    protected boolean shouldReverseListItems() {
        int rotation = getCurrentRotation();
        if (rotation == 0) {
            return false;
        }
        return getCurrentLayoutDirection() == 1 ? rotation == 1 : rotation == 2;
    }

    @Override // com.android.systemui.globalactions.GlobalActionsLayout, com.android.systemui.MultiListLayout
    public void onUpdateList() {
        super.onUpdateList();
        updateChildOrdering();
    }

    private void updateChildOrdering() {
        if (shouldReverseListItems()) {
            getListView().bringToFront();
        } else {
            getSeparatedView().bringToFront();
        }
    }

    @VisibleForTesting
    protected void snapToPowerButton() {
        int offset = getPowerButtonOffsetDistance();
        int currentRotation = getCurrentRotation();
        if (currentRotation == 1) {
            setPadding(offset, 0, 0, 0);
            setGravity(51);
        } else if (currentRotation == 2) {
            setPadding(0, 0, offset, 0);
            setGravity(85);
        } else {
            setPadding(0, offset, 0, 0);
            setGravity(53);
        }
    }

    @VisibleForTesting
    protected void centerAlongEdge() {
        int currentRotation = getCurrentRotation();
        if (currentRotation == 1) {
            setPadding(0, 0, 0, 0);
            setGravity(49);
        } else if (currentRotation == 2) {
            setPadding(0, 0, 0, 0);
            setGravity(81);
        } else {
            setPadding(0, 0, 0, 0);
            setGravity(21);
        }
    }

    @VisibleForTesting
    protected int getPowerButtonOffsetDistance() {
        return Math.round(getContext().getResources().getDimension(R.dimen.global_actions_top_padding));
    }

    @VisibleForTesting
    protected boolean shouldSnapToPowerButton() {
        int dialogSize;
        int screenSize;
        int offsetSize = getPowerButtonOffsetDistance();
        View wrapper = getWrapper();
        int rotation = getCurrentRotation();
        if (rotation == 0) {
            dialogSize = wrapper.getMeasuredHeight();
            screenSize = getMeasuredHeight();
        } else {
            dialogSize = wrapper.getMeasuredWidth();
            screenSize = getMeasuredWidth();
        }
        return dialogSize + offsetSize < screenSize;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @VisibleForTesting
    /* renamed from: updateSnap */
    public void lambda$onLayout$0$GlobalActionsColumnLayout() {
        boolean snap = shouldSnapToPowerButton();
        if (snap != this.mLastSnap) {
            if (snap) {
                snapToPowerButton();
            } else {
                centerAlongEdge();
            }
        }
        this.mLastSnap = snap;
    }

    @VisibleForTesting
    protected float getGridItemSize() {
        return getContext().getResources().getDimension(R.dimen.global_actions_grid_item_height);
    }

    @VisibleForTesting
    protected float getAnimationDistance() {
        return getGridItemSize() / 2.0f;
    }

    @Override // com.android.systemui.MultiListLayout
    public float getAnimationOffsetX() {
        if (getCurrentRotation() == 0) {
            return getAnimationDistance();
        }
        return 0.0f;
    }

    @Override // com.android.systemui.MultiListLayout
    public float getAnimationOffsetY() {
        int currentRotation = getCurrentRotation();
        if (currentRotation != 1) {
            if (currentRotation == 2) {
                return getAnimationDistance();
            }
            return 0.0f;
        }
        return -getAnimationDistance();
    }
}
