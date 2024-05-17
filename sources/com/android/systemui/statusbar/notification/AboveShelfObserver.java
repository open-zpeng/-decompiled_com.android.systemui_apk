package com.android.systemui.statusbar.notification;

import android.view.View;
import android.view.ViewGroup;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
/* loaded from: classes21.dex */
public class AboveShelfObserver implements AboveShelfChangedListener {
    private boolean mHasViewsAboveShelf = false;
    private final ViewGroup mHostLayout;
    private HasViewAboveShelfChangedListener mListener;

    /* loaded from: classes21.dex */
    public interface HasViewAboveShelfChangedListener {
        void onHasViewsAboveShelfChanged(boolean z);
    }

    public AboveShelfObserver(ViewGroup hostLayout) {
        this.mHostLayout = hostLayout;
    }

    public void setListener(HasViewAboveShelfChangedListener listener) {
        this.mListener = listener;
    }

    @Override // com.android.systemui.statusbar.notification.AboveShelfChangedListener
    public void onAboveShelfStateChanged(boolean aboveShelf) {
        ViewGroup viewGroup;
        boolean hasViewsAboveShelf = aboveShelf;
        if (!hasViewsAboveShelf && (viewGroup = this.mHostLayout) != null) {
            int n = viewGroup.getChildCount();
            int i = 0;
            while (true) {
                if (i >= n) {
                    break;
                }
                View child = this.mHostLayout.getChildAt(i);
                if (!(child instanceof ExpandableNotificationRow) || !((ExpandableNotificationRow) child).isAboveShelf()) {
                    i++;
                } else {
                    hasViewsAboveShelf = true;
                    break;
                }
            }
        }
        if (this.mHasViewsAboveShelf != hasViewsAboveShelf) {
            this.mHasViewsAboveShelf = hasViewsAboveShelf;
            HasViewAboveShelfChangedListener hasViewAboveShelfChangedListener = this.mListener;
            if (hasViewAboveShelfChangedListener != null) {
                hasViewAboveShelfChangedListener.onHasViewsAboveShelfChanged(hasViewsAboveShelf);
            }
        }
    }

    @VisibleForTesting
    boolean hasViewsAboveShelf() {
        return this.mHasViewsAboveShelf;
    }
}
