package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.android.systemui.Gefingerpoken;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableView;
/* loaded from: classes21.dex */
public class HeadsUpTouchHelper implements Gefingerpoken {
    private Callback mCallback;
    private boolean mCollapseSnoozes;
    private HeadsUpManagerPhone mHeadsUpManager;
    private float mInitialTouchX;
    private float mInitialTouchY;
    private NotificationPanelView mPanel;
    private ExpandableNotificationRow mPickedChild;
    private float mTouchSlop;
    private boolean mTouchingHeadsUpView;
    private boolean mTrackingHeadsUp;
    private int mTrackingPointer;

    /* loaded from: classes21.dex */
    public interface Callback {
        ExpandableView getChildAtRawPosition(float f, float f2);

        Context getContext();

        boolean isExpanded();
    }

    public HeadsUpTouchHelper(HeadsUpManagerPhone headsUpManager, Callback callback, NotificationPanelView notificationPanelView) {
        this.mHeadsUpManager = headsUpManager;
        this.mCallback = callback;
        this.mPanel = notificationPanelView;
        Context context = this.mCallback.getContext();
        ViewConfiguration configuration = ViewConfiguration.get(context);
        this.mTouchSlop = configuration.getScaledTouchSlop();
    }

    public boolean isTrackingHeadsUp() {
        return this.mTrackingHeadsUp;
    }

    @Override // com.android.systemui.Gefingerpoken
    public boolean onInterceptTouchEvent(MotionEvent event) {
        NotificationEntry topEntry;
        int upPointer;
        if (this.mTouchingHeadsUpView || event.getActionMasked() == 0) {
            int pointerIndex = event.findPointerIndex(this.mTrackingPointer);
            if (pointerIndex < 0) {
                pointerIndex = 0;
                this.mTrackingPointer = event.getPointerId(0);
            }
            float x = event.getX(pointerIndex);
            float y = event.getY(pointerIndex);
            int actionMasked = event.getActionMasked();
            boolean z = true;
            if (actionMasked == 0) {
                this.mInitialTouchY = y;
                this.mInitialTouchX = x;
                setTrackingHeadsUp(false);
                ExpandableView child = this.mCallback.getChildAtRawPosition(x, y);
                this.mTouchingHeadsUpView = false;
                if (child instanceof ExpandableNotificationRow) {
                    this.mPickedChild = (ExpandableNotificationRow) child;
                    if (this.mCallback.isExpanded() || !this.mPickedChild.isHeadsUp() || !this.mPickedChild.isPinned()) {
                        z = false;
                    }
                    this.mTouchingHeadsUpView = z;
                } else if (child == null && !this.mCallback.isExpanded() && (topEntry = this.mHeadsUpManager.getTopEntry()) != null && topEntry.isRowPinned()) {
                    this.mPickedChild = topEntry.getRow();
                    this.mTouchingHeadsUpView = true;
                }
            } else {
                if (actionMasked != 1) {
                    if (actionMasked == 2) {
                        float h = y - this.mInitialTouchY;
                        if (this.mTouchingHeadsUpView && Math.abs(h) > this.mTouchSlop && Math.abs(h) > Math.abs(x - this.mInitialTouchX)) {
                            setTrackingHeadsUp(true);
                            float f = 0.0f;
                            this.mCollapseSnoozes = h < 0.0f;
                            this.mInitialTouchX = x;
                            this.mInitialTouchY = y;
                            int startHeight = (int) (this.mPickedChild.getActualHeight() + this.mPickedChild.getTranslationY());
                            float maxPanelHeight = this.mPanel.getMaxPanelHeight();
                            NotificationPanelView notificationPanelView = this.mPanel;
                            if (maxPanelHeight > 0.0f) {
                                f = startHeight / maxPanelHeight;
                            }
                            notificationPanelView.setPanelScrimMinFraction(f);
                            this.mPanel.startExpandMotion(x, y, true, startHeight);
                            this.mPanel.startExpandingFromPeek();
                            this.mHeadsUpManager.unpinAll(true);
                            this.mPanel.clearNotificationEffects();
                            endMotion();
                            return true;
                        }
                    } else if (actionMasked != 3) {
                        if (actionMasked == 6 && this.mTrackingPointer == (upPointer = event.getPointerId(event.getActionIndex()))) {
                            int newIndex = event.getPointerId(0) != upPointer ? 0 : 1;
                            this.mTrackingPointer = event.getPointerId(newIndex);
                            this.mInitialTouchX = event.getX(newIndex);
                            this.mInitialTouchY = event.getY(newIndex);
                        }
                    }
                }
                ExpandableNotificationRow expandableNotificationRow = this.mPickedChild;
                if (expandableNotificationRow != null && this.mTouchingHeadsUpView && this.mHeadsUpManager.shouldSwallowClick(expandableNotificationRow.getStatusBarNotification().getKey())) {
                    endMotion();
                    return true;
                }
                endMotion();
            }
            return false;
        }
        return false;
    }

    private void setTrackingHeadsUp(boolean tracking) {
        this.mTrackingHeadsUp = tracking;
        this.mHeadsUpManager.setTrackingHeadsUp(tracking);
        this.mPanel.setTrackedHeadsUp(tracking ? this.mPickedChild : null);
    }

    public void notifyFling(boolean collapse) {
        if (collapse && this.mCollapseSnoozes) {
            this.mHeadsUpManager.snooze();
        }
        this.mCollapseSnoozes = false;
    }

    @Override // com.android.systemui.Gefingerpoken
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mTrackingHeadsUp) {
            int actionMasked = event.getActionMasked();
            if (actionMasked == 1 || actionMasked == 3) {
                endMotion();
                setTrackingHeadsUp(false);
            }
            return true;
        }
        return false;
    }

    private void endMotion() {
        this.mTrackingPointer = -1;
        this.mPickedChild = null;
        this.mTouchingHeadsUpView = false;
    }
}
