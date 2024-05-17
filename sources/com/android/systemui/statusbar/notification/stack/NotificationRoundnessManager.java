package com.android.systemui.statusbar.notification.stack;

import android.util.MathUtils;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import java.util.HashSet;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class NotificationRoundnessManager implements OnHeadsUpChangedListener {
    private HashSet<ExpandableView> mAnimatedChildren;
    private float mAppearFraction;
    private final KeyguardBypassController mBypassController;
    private boolean mExpanded;
    private Runnable mRoundingChangedCallback;
    private ExpandableNotificationRow mTrackedHeadsUp;
    private final ActivatableNotificationView[] mFirstInSectionViews = new ActivatableNotificationView[2];
    private final ActivatableNotificationView[] mLastInSectionViews = new ActivatableNotificationView[2];
    private final ActivatableNotificationView[] mTmpFirstInSectionViews = new ActivatableNotificationView[2];
    private final ActivatableNotificationView[] mTmpLastInSectionViews = new ActivatableNotificationView[2];

    /* JADX INFO: Access modifiers changed from: package-private */
    @Inject
    public NotificationRoundnessManager(KeyguardBypassController keyguardBypassController) {
        this.mBypassController = keyguardBypassController;
    }

    @Override // com.android.systemui.statusbar.policy.OnHeadsUpChangedListener
    public void onHeadsUpPinned(NotificationEntry headsUp) {
        updateView(headsUp.getRow(), false);
    }

    @Override // com.android.systemui.statusbar.policy.OnHeadsUpChangedListener
    public void onHeadsUpUnPinned(NotificationEntry headsUp) {
        updateView(headsUp.getRow(), true);
    }

    public void onHeadsupAnimatingAwayChanged(ExpandableNotificationRow row, boolean isAnimatingAway) {
        updateView(row, false);
    }

    @Override // com.android.systemui.statusbar.policy.OnHeadsUpChangedListener
    public void onHeadsUpStateChanged(NotificationEntry entry, boolean isHeadsUp) {
        updateView(entry.getRow(), false);
    }

    private void updateView(ActivatableNotificationView view, boolean animate) {
        boolean changed = updateViewWithoutCallback(view, animate);
        if (changed) {
            this.mRoundingChangedCallback.run();
        }
    }

    private boolean updateViewWithoutCallback(ActivatableNotificationView view, boolean animate) {
        float topRoundness = getRoundness(view, true);
        float bottomRoundness = getRoundness(view, false);
        boolean topChanged = view.setTopRoundness(topRoundness, animate);
        boolean bottomChanged = view.setBottomRoundness(bottomRoundness, animate);
        boolean firstInSection = isFirstInSection(view, false);
        boolean lastInSection = isLastInSection(view, false);
        view.setFirstInSection(firstInSection);
        view.setLastInSection(lastInSection);
        if ((firstInSection || lastInSection) && (topChanged || bottomChanged)) {
            return true;
        }
        return false;
    }

    private boolean isFirstInSection(ActivatableNotificationView view, boolean includeFirstSection) {
        int numNonEmptySections = 0;
        int i = 0;
        while (true) {
            ActivatableNotificationView[] activatableNotificationViewArr = this.mFirstInSectionViews;
            if (i >= activatableNotificationViewArr.length) {
                return false;
            }
            if (view == activatableNotificationViewArr[i]) {
                return includeFirstSection || numNonEmptySections > 0;
            }
            if (activatableNotificationViewArr[i] != null) {
                numNonEmptySections++;
            }
            i++;
        }
    }

    private boolean isLastInSection(ActivatableNotificationView view, boolean includeLastSection) {
        int numNonEmptySections = 0;
        for (int i = this.mLastInSectionViews.length - 1; i >= 0; i--) {
            ActivatableNotificationView[] activatableNotificationViewArr = this.mLastInSectionViews;
            if (view == activatableNotificationViewArr[i]) {
                return includeLastSection || numNonEmptySections > 0;
            }
            if (activatableNotificationViewArr[i] != null) {
                numNonEmptySections++;
            }
        }
        return false;
    }

    private float getRoundness(ActivatableNotificationView view, boolean top) {
        if ((view.isPinned() || view.isHeadsUpAnimatingAway()) && !this.mExpanded) {
            return 1.0f;
        }
        if (isFirstInSection(view, true) && top) {
            return 1.0f;
        }
        if (!isLastInSection(view, true) || top) {
            if (view == this.mTrackedHeadsUp) {
                return MathUtils.saturate(1.0f - this.mAppearFraction);
            }
            return (!view.showingPulsing() || this.mBypassController.getBypassEnabled()) ? 0.0f : 1.0f;
        }
        return 1.0f;
    }

    public void setExpanded(float expandedHeight, float appearFraction) {
        this.mExpanded = expandedHeight != 0.0f;
        this.mAppearFraction = appearFraction;
        ExpandableNotificationRow expandableNotificationRow = this.mTrackedHeadsUp;
        if (expandableNotificationRow != null) {
            updateView(expandableNotificationRow, true);
        }
    }

    public void updateRoundedChildren(NotificationSection[] sections) {
        for (int i = 0; i < 2; i++) {
            ActivatableNotificationView[] activatableNotificationViewArr = this.mTmpFirstInSectionViews;
            ActivatableNotificationView[] activatableNotificationViewArr2 = this.mFirstInSectionViews;
            activatableNotificationViewArr[i] = activatableNotificationViewArr2[i];
            this.mTmpLastInSectionViews[i] = this.mLastInSectionViews[i];
            activatableNotificationViewArr2[i] = sections[i].getFirstVisibleChild();
            this.mLastInSectionViews[i] = sections[i].getLastVisibleChild();
        }
        boolean anyChanged = false | handleRemovedOldViews(sections, this.mTmpFirstInSectionViews, true);
        if (anyChanged | handleRemovedOldViews(sections, this.mTmpLastInSectionViews, false) | handleAddedNewViews(sections, this.mTmpFirstInSectionViews, true) | handleAddedNewViews(sections, this.mTmpLastInSectionViews, false)) {
            this.mRoundingChangedCallback.run();
        }
    }

    private boolean handleRemovedOldViews(NotificationSection[] sections, ActivatableNotificationView[] oldViews, boolean first) {
        boolean anyChanged = false;
        for (ActivatableNotificationView oldView : oldViews) {
            if (oldView != null) {
                boolean isStillPresent = false;
                boolean adjacentSectionChanged = false;
                int length = sections.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    NotificationSection section = sections[i];
                    ActivatableNotificationView newView = first ? section.getFirstVisibleChild() : section.getLastVisibleChild();
                    if (newView != oldView) {
                        i++;
                    } else {
                        isStillPresent = true;
                        if (oldView.isFirstInSection() != isFirstInSection(oldView, false) || oldView.isLastInSection() != isLastInSection(oldView, false)) {
                            adjacentSectionChanged = true;
                        }
                    }
                }
                if (!isStillPresent || adjacentSectionChanged) {
                    anyChanged = true;
                    if (!oldView.isRemoved()) {
                        updateViewWithoutCallback(oldView, oldView.isShown());
                    }
                }
            }
        }
        return anyChanged;
    }

    private boolean handleAddedNewViews(NotificationSection[] sections, ActivatableNotificationView[] oldViews, boolean first) {
        boolean anyChanged = false;
        for (NotificationSection section : sections) {
            ActivatableNotificationView newView = first ? section.getFirstVisibleChild() : section.getLastVisibleChild();
            if (newView != null) {
                boolean wasAlreadyPresent = false;
                int length = oldViews.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    ActivatableNotificationView oldView = oldViews[i];
                    if (oldView != newView) {
                        i++;
                    } else {
                        wasAlreadyPresent = true;
                        break;
                    }
                }
                if (!wasAlreadyPresent) {
                    anyChanged = true;
                    updateViewWithoutCallback(newView, newView.isShown() && !this.mAnimatedChildren.contains(newView));
                }
            }
        }
        return anyChanged;
    }

    public void setAnimatedChildren(HashSet<ExpandableView> animatedChildren) {
        this.mAnimatedChildren = animatedChildren;
    }

    public void setOnRoundingChangedCallback(Runnable roundingChangedCallback) {
        this.mRoundingChangedCallback = roundingChangedCallback;
    }

    public void setTrackingHeadsUp(ExpandableNotificationRow row) {
        ExpandableNotificationRow previous = this.mTrackedHeadsUp;
        this.mTrackedHeadsUp = row;
        if (previous != null) {
            updateView(previous, true);
        }
    }
}
