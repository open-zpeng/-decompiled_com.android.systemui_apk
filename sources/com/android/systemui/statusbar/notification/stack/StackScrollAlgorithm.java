package com.android.systemui.statusbar.notification.stack;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.R;
import com.android.systemui.statusbar.EmptyShadeView;
import com.android.systemui.statusbar.NotificationShelf;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.notification.row.FooterView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/* loaded from: classes21.dex */
public class StackScrollAlgorithm {
    static final boolean ANCHOR_SCROLLING = false;
    private static final String LOG_TAG = "StackScrollAlgorithm";
    private boolean mClipNotificationScrollToTop;
    private int mCollapsedSize;
    private int mGapHeight;
    private float mHeadsUpInset;
    private final ViewGroup mHostView;
    private int mIncreasedPaddingBetweenElements;
    private boolean mIsExpanded;
    private int mPaddingBetweenElements;
    private int mPinnedZTranslationExtra;
    private int mStatusBarHeight;
    private StackScrollAlgorithmState mTempAlgorithmState = new StackScrollAlgorithmState();

    /* loaded from: classes21.dex */
    public interface SectionProvider {
        boolean beginsSection(View view);
    }

    public StackScrollAlgorithm(Context context, ViewGroup hostView) {
        this.mHostView = hostView;
        initView(context);
    }

    public void initView(Context context) {
        initConstants(context);
    }

    private void initConstants(Context context) {
        Resources res = context.getResources();
        this.mPaddingBetweenElements = res.getDimensionPixelSize(R.dimen.notification_divider_height);
        this.mIncreasedPaddingBetweenElements = res.getDimensionPixelSize(R.dimen.notification_divider_height_increased);
        this.mCollapsedSize = res.getDimensionPixelSize(R.dimen.notification_min_height);
        this.mStatusBarHeight = res.getDimensionPixelSize(R.dimen.status_bar_height);
        this.mClipNotificationScrollToTop = res.getBoolean(R.bool.config_clipNotificationScrollToTop);
        this.mHeadsUpInset = this.mStatusBarHeight + res.getDimensionPixelSize(R.dimen.heads_up_status_bar_padding);
        this.mPinnedZTranslationExtra = res.getDimensionPixelSize(R.dimen.heads_up_pinned_elevation);
        this.mGapHeight = res.getDimensionPixelSize(R.dimen.notification_section_divider_height);
    }

    public void resetViewStates(AmbientState ambientState) {
        StackScrollAlgorithmState algorithmState = this.mTempAlgorithmState;
        resetChildViewStates();
        initAlgorithmState(this.mHostView, algorithmState, ambientState);
        updatePositionsForState(algorithmState, ambientState);
        updateZValuesForState(algorithmState, ambientState);
        updateHeadsUpStates(algorithmState, ambientState);
        updatePulsingStates(algorithmState, ambientState);
        updateDimmedActivatedHideSensitive(ambientState, algorithmState);
        updateClipping(algorithmState, ambientState);
        updateSpeedBumpState(algorithmState, ambientState);
        updateShelfState(ambientState);
        getNotificationChildrenStates(algorithmState, ambientState);
    }

    private void resetChildViewStates() {
        int numChildren = this.mHostView.getChildCount();
        for (int i = 0; i < numChildren; i++) {
            ExpandableView child = (ExpandableView) this.mHostView.getChildAt(i);
            child.resetViewState();
        }
    }

    private void getNotificationChildrenStates(StackScrollAlgorithmState algorithmState, AmbientState ambientState) {
        int childCount = algorithmState.visibleChildren.size();
        for (int i = 0; i < childCount; i++) {
            ExpandableView v = algorithmState.visibleChildren.get(i);
            if (v instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow row = (ExpandableNotificationRow) v;
                row.updateChildrenStates(ambientState);
            }
        }
    }

    private void updateSpeedBumpState(StackScrollAlgorithmState algorithmState, AmbientState ambientState) {
        int childCount = algorithmState.visibleChildren.size();
        int belowSpeedBump = ambientState.getSpeedBumpIndex();
        int i = 0;
        while (i < childCount) {
            ExpandableView child = algorithmState.visibleChildren.get(i);
            ExpandableViewState childViewState = child.getViewState();
            childViewState.belowSpeedBump = i >= belowSpeedBump;
            i++;
        }
    }

    private void updateShelfState(AmbientState ambientState) {
        NotificationShelf shelf = ambientState.getShelf();
        if (shelf != null) {
            shelf.updateState(ambientState);
        }
    }

    private void updateClipping(StackScrollAlgorithmState algorithmState, AmbientState ambientState) {
        float drawStart;
        if (!ambientState.isOnKeyguard()) {
            drawStart = ambientState.getTopPadding() + ambientState.getStackTranslation() + ambientState.getExpandAnimationTopChange();
        } else {
            drawStart = 0.0f;
        }
        float clipStart = 0.0f;
        int childCount = algorithmState.visibleChildren.size();
        boolean firstHeadsUp = true;
        for (int i = 0; i < childCount; i++) {
            ExpandableView child = algorithmState.visibleChildren.get(i);
            ExpandableViewState state = child.getViewState();
            if (!child.mustStayOnScreen() || state.headsUpIsVisible) {
                clipStart = Math.max(drawStart, clipStart);
            }
            float newYTranslation = state.yTranslation;
            float newHeight = state.height;
            float newNotificationEnd = newYTranslation + newHeight;
            boolean isHeadsUp = (child instanceof ExpandableNotificationRow) && ((ExpandableNotificationRow) child).isPinned();
            if (this.mClipNotificationScrollToTop && ((!state.inShelf || (isHeadsUp && !firstHeadsUp)) && newYTranslation < clipStart)) {
                float overlapAmount = clipStart - newYTranslation;
                state.clipTopAmount = (int) overlapAmount;
            } else {
                state.clipTopAmount = 0;
            }
            if (isHeadsUp) {
                firstHeadsUp = false;
            }
            if (!child.isTransparent()) {
                clipStart = Math.max(clipStart, isHeadsUp ? newYTranslation : newNotificationEnd);
            }
        }
    }

    public static boolean canChildBeDismissed(View v) {
        if (v instanceof ExpandableNotificationRow) {
            ExpandableNotificationRow row = (ExpandableNotificationRow) v;
            if (row.isBlockingHelperShowingAndTranslationFinished()) {
                return true;
            }
            if (row.areGutsExposed() || !row.getEntry().hasFinishedInitialization()) {
                return false;
            }
            return row.canViewBeDismissed();
        }
        return false;
    }

    private void updateDimmedActivatedHideSensitive(AmbientState ambientState, StackScrollAlgorithmState algorithmState) {
        boolean dimmed = ambientState.isDimmed();
        boolean hideSensitive = ambientState.isHideSensitive();
        View activatedChild = ambientState.getActivatedChild();
        int childCount = algorithmState.visibleChildren.size();
        for (int i = 0; i < childCount; i++) {
            ExpandableView child = algorithmState.visibleChildren.get(i);
            ExpandableViewState childViewState = child.getViewState();
            childViewState.dimmed = dimmed;
            childViewState.hideSensitive = hideSensitive;
            boolean isActivatedChild = activatedChild == child;
            if (dimmed && isActivatedChild) {
                childViewState.zTranslation += ambientState.getZDistanceBetweenElements() * 2.0f;
            }
        }
    }

    private void initAlgorithmState(ViewGroup hostView, StackScrollAlgorithmState state, AmbientState ambientState) {
        int i;
        int firstHiddenIndex;
        StackScrollAlgorithm stackScrollAlgorithm = this;
        int firstHiddenIndex2 = 0;
        float bottomOverScroll = ambientState.getOverScrollAmount(false);
        int scrollY = ambientState.getScrollY();
        state.scrollY = (int) (Math.max(0, scrollY) + bottomOverScroll);
        int childCount = hostView.getChildCount();
        state.visibleChildren.clear();
        state.visibleChildren.ensureCapacity(childCount);
        state.paddingMap.clear();
        int notGoneIndex = 0;
        ExpandableView lastView = null;
        if (ambientState.isDozing()) {
            if (ambientState.hasPulsingNotifications()) {
                firstHiddenIndex2 = 1;
            }
        } else {
            firstHiddenIndex2 = childCount;
        }
        int i2 = 0;
        while (i2 < childCount) {
            ExpandableView v = (ExpandableView) hostView.getChildAt(i2);
            if (v.getVisibility() == 8) {
                firstHiddenIndex = firstHiddenIndex2;
            } else if (v == ambientState.getShelf()) {
                firstHiddenIndex = firstHiddenIndex2;
            } else {
                if (i2 >= firstHiddenIndex2) {
                    lastView = null;
                }
                notGoneIndex = stackScrollAlgorithm.updateNotGoneIndex(state, notGoneIndex, v);
                float increasedPadding = v.getIncreasedPaddingAmount();
                if (increasedPadding != 0.0f) {
                    state.paddingMap.put(v, Float.valueOf(increasedPadding));
                    if (lastView == null) {
                        firstHiddenIndex = firstHiddenIndex2;
                    } else {
                        Float prevValue = state.paddingMap.get(lastView);
                        float newValue = stackScrollAlgorithm.getPaddingForValue(Float.valueOf(increasedPadding));
                        if (prevValue == null) {
                            firstHiddenIndex = firstHiddenIndex2;
                        } else {
                            firstHiddenIndex = firstHiddenIndex2;
                            float prevPadding = stackScrollAlgorithm.getPaddingForValue(prevValue);
                            if (increasedPadding <= 0.0f) {
                                if (prevValue.floatValue() > 0.0f) {
                                    newValue = NotificationUtils.interpolate(newValue, prevPadding, prevValue.floatValue());
                                }
                            } else {
                                newValue = NotificationUtils.interpolate(prevPadding, newValue, increasedPadding);
                            }
                        }
                        state.paddingMap.put(lastView, Float.valueOf(newValue));
                    }
                } else {
                    firstHiddenIndex = firstHiddenIndex2;
                    if (lastView != null) {
                        state.paddingMap.put(lastView, Float.valueOf(stackScrollAlgorithm.getPaddingForValue(state.paddingMap.get(lastView))));
                    }
                }
                if (v instanceof ExpandableNotificationRow) {
                    ExpandableNotificationRow row = (ExpandableNotificationRow) v;
                    List<ExpandableNotificationRow> children = row.getNotificationChildren();
                    if (row.isSummaryWithChildren() && children != null) {
                        for (ExpandableNotificationRow childRow : children) {
                            ExpandableNotificationRow row2 = row;
                            if (childRow.getVisibility() != 8) {
                                ExpandableViewState childState = childRow.getViewState();
                                childState.notGoneIndex = notGoneIndex;
                                notGoneIndex++;
                            }
                            row = row2;
                        }
                    }
                }
                lastView = v;
            }
            i2++;
            stackScrollAlgorithm = this;
            firstHiddenIndex2 = firstHiddenIndex;
        }
        ExpandableNotificationRow expandingNotification = ambientState.getExpandingNotification();
        if (expandingNotification != null) {
            if (expandingNotification.isChildInGroup()) {
                i = state.visibleChildren.indexOf(expandingNotification.getNotificationParent());
            } else {
                i = state.visibleChildren.indexOf(expandingNotification);
            }
        } else {
            i = -1;
        }
        state.indexOfExpandingNotification = i;
    }

    private float getPaddingForValue(Float increasedPadding) {
        if (increasedPadding == null) {
            return this.mPaddingBetweenElements;
        }
        if (increasedPadding.floatValue() >= 0.0f) {
            return NotificationUtils.interpolate(this.mPaddingBetweenElements, this.mIncreasedPaddingBetweenElements, increasedPadding.floatValue());
        }
        return NotificationUtils.interpolate(0.0f, this.mPaddingBetweenElements, increasedPadding.floatValue() + 1.0f);
    }

    private int updateNotGoneIndex(StackScrollAlgorithmState state, int notGoneIndex, ExpandableView v) {
        ExpandableViewState viewState = v.getViewState();
        viewState.notGoneIndex = notGoneIndex;
        state.visibleChildren.add(v);
        return notGoneIndex + 1;
    }

    private void updatePositionsForState(StackScrollAlgorithmState algorithmState, AmbientState ambientState) {
        float currentYPosition = -algorithmState.scrollY;
        int childCount = algorithmState.visibleChildren.size();
        for (int i = 0; i < childCount; i++) {
            currentYPosition = updateChild(i, algorithmState, ambientState, currentYPosition, false);
        }
    }

    protected float updateChild(int i, StackScrollAlgorithmState algorithmState, AmbientState ambientState, float currentYPosition, boolean reverse) {
        float currentYPosition2;
        float currentYPosition3;
        ExpandableView child = algorithmState.visibleChildren.get(i);
        boolean applyGapHeight = childNeedsGapHeight(ambientState.getSectionProvider(), algorithmState, i, child);
        ExpandableViewState childViewState = child.getViewState();
        childViewState.location = 0;
        if (applyGapHeight && !reverse) {
            currentYPosition2 = currentYPosition + this.mGapHeight;
        } else {
            currentYPosition2 = currentYPosition;
        }
        int paddingAfterChild = getPaddingAfterChild(algorithmState, child);
        int childHeight = getMaxAllowedChildHeight(child);
        if (reverse) {
            childViewState.yTranslation = currentYPosition2 - (childHeight + paddingAfterChild);
            if (currentYPosition2 <= 0.0f) {
                childViewState.location = 2;
            }
        } else {
            childViewState.yTranslation = currentYPosition2;
        }
        boolean isFooterView = child instanceof FooterView;
        boolean isEmptyShadeView = child instanceof EmptyShadeView;
        childViewState.location = 4;
        float inset = ambientState.getTopPadding() + ambientState.getStackTranslation();
        if (i <= algorithmState.getIndexOfExpandingNotification()) {
            inset += ambientState.getExpandAnimationTopChange();
        }
        if (child.mustStayOnScreen() && childViewState.yTranslation >= 0.0f) {
            float end = childViewState.yTranslation + childViewState.height + inset;
            childViewState.headsUpIsVisible = end < ambientState.getMaxHeadsUpTranslation();
        }
        if (isFooterView) {
            childViewState.yTranslation = Math.min(childViewState.yTranslation, ambientState.getInnerHeight() - childHeight);
        } else if (!isEmptyShadeView) {
            clampPositionToShelf(child, childViewState, ambientState);
        } else {
            childViewState.yTranslation = (ambientState.getInnerHeight() - childHeight) + (ambientState.getStackTranslation() * 0.25f);
        }
        if (reverse) {
            currentYPosition3 = childViewState.yTranslation;
            if (applyGapHeight) {
                currentYPosition3 -= this.mGapHeight;
            }
        } else {
            currentYPosition3 = childViewState.yTranslation + childHeight + paddingAfterChild;
            if (currentYPosition3 <= 0.0f) {
                childViewState.location = 2;
            }
        }
        if (childViewState.location == 0) {
            Log.wtf(LOG_TAG, "Failed to assign location for child " + i);
        }
        childViewState.yTranslation += inset;
        return currentYPosition3;
    }

    private boolean childNeedsGapHeight(SectionProvider sectionProvider, StackScrollAlgorithmState algorithmState, int visibleIndex, View child) {
        return sectionProvider.beginsSection(child) && visibleIndex > 0;
    }

    protected int getPaddingAfterChild(StackScrollAlgorithmState algorithmState, ExpandableView child) {
        return algorithmState.getPaddingAfterChild(child);
    }

    private void updatePulsingStates(StackScrollAlgorithmState algorithmState, AmbientState ambientState) {
        int childCount = algorithmState.visibleChildren.size();
        for (int i = 0; i < childCount; i++) {
            View child = algorithmState.visibleChildren.get(i);
            if (child instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow row = (ExpandableNotificationRow) child;
                if (row.showingPulsing() && (i != 0 || !ambientState.isPulseExpanding())) {
                    ExpandableViewState viewState = row.getViewState();
                    viewState.hidden = false;
                }
            }
        }
    }

    private void updateHeadsUpStates(StackScrollAlgorithmState algorithmState, AmbientState ambientState) {
        int childCount = algorithmState.visibleChildren.size();
        ExpandableNotificationRow topHeadsUpEntry = null;
        for (int i = 0; i < childCount; i++) {
            View child = algorithmState.visibleChildren.get(i);
            if (child instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow row = (ExpandableNotificationRow) child;
                if (row.isHeadsUp()) {
                    ExpandableViewState childState = row.getViewState();
                    boolean isTopEntry = true;
                    if (topHeadsUpEntry == null && row.mustStayOnScreen() && !childState.headsUpIsVisible) {
                        topHeadsUpEntry = row;
                        childState.location = 1;
                    }
                    if (topHeadsUpEntry != row) {
                        isTopEntry = false;
                    }
                    float unmodifiedEndLocation = childState.yTranslation + childState.height;
                    if (this.mIsExpanded && row.mustStayOnScreen() && !childState.headsUpIsVisible && !row.showingPulsing()) {
                        clampHunToTop(ambientState, row, childState);
                        if (i == 0 && row.isAboveShelf()) {
                            clampHunToMaxTranslation(ambientState, row, childState);
                            childState.hidden = false;
                        }
                    }
                    if (row.isPinned()) {
                        childState.yTranslation = Math.max(childState.yTranslation, this.mHeadsUpInset);
                        childState.height = Math.max(row.getIntrinsicHeight(), childState.height);
                        childState.hidden = false;
                        ExpandableViewState topState = topHeadsUpEntry == null ? null : topHeadsUpEntry.getViewState();
                        if (topState != null && !isTopEntry && (!this.mIsExpanded || unmodifiedEndLocation > topState.yTranslation + topState.height)) {
                            childState.height = row.getIntrinsicHeight();
                            childState.yTranslation = Math.min((topState.yTranslation + topState.height) - childState.height, childState.yTranslation);
                        }
                        if (!this.mIsExpanded && isTopEntry && ambientState.getScrollY() > 0) {
                            childState.yTranslation -= ambientState.getScrollY();
                        }
                    }
                    if (row.isHeadsUpAnimatingAway()) {
                        childState.hidden = false;
                    }
                }
            }
        }
    }

    private void clampHunToTop(AmbientState ambientState, ExpandableNotificationRow row, ExpandableViewState childState) {
        float newTranslation = Math.max(ambientState.getTopPadding() + ambientState.getStackTranslation(), childState.yTranslation);
        childState.height = (int) Math.max(childState.height - (newTranslation - childState.yTranslation), row.getCollapsedHeight());
        childState.yTranslation = newTranslation;
    }

    private void clampHunToMaxTranslation(AmbientState ambientState, ExpandableNotificationRow row, ExpandableViewState childState) {
        float maxHeadsUpTranslation = ambientState.getMaxHeadsUpTranslation();
        float maxShelfPosition = ambientState.getInnerHeight() + ambientState.getTopPadding() + ambientState.getStackTranslation();
        float maxHeadsUpTranslation2 = Math.min(maxHeadsUpTranslation, maxShelfPosition);
        float bottomPosition = maxHeadsUpTranslation2 - row.getCollapsedHeight();
        float newTranslation = Math.min(childState.yTranslation, bottomPosition);
        childState.height = (int) Math.min(childState.height, maxHeadsUpTranslation2 - newTranslation);
        childState.yTranslation = newTranslation;
    }

    private void clampPositionToShelf(ExpandableView child, ExpandableViewState childViewState, AmbientState ambientState) {
        if (ambientState.getShelf() == null) {
            return;
        }
        int shelfStart = ambientState.getInnerHeight() - ambientState.getShelf().getIntrinsicHeight();
        if (ambientState.isAppearing() && !child.isAboveShelf()) {
            childViewState.yTranslation = Math.max(childViewState.yTranslation, shelfStart);
        }
        childViewState.yTranslation = Math.min(childViewState.yTranslation, shelfStart);
        if (childViewState.yTranslation >= shelfStart) {
            childViewState.hidden = (child.isExpandAnimationRunning() || child.hasExpandingChild()) ? false : true;
            childViewState.inShelf = true;
            childViewState.headsUpIsVisible = false;
        }
    }

    protected int getMaxAllowedChildHeight(View child) {
        if (!(child instanceof ExpandableView)) {
            return child == null ? this.mCollapsedSize : child.getHeight();
        }
        ExpandableView expandableView = (ExpandableView) child;
        return expandableView.getIntrinsicHeight();
    }

    private void updateZValuesForState(StackScrollAlgorithmState algorithmState, AmbientState ambientState) {
        int childCount = algorithmState.visibleChildren.size();
        float childrenOnTop = 0.0f;
        for (int i = childCount - 1; i >= 0; i--) {
            childrenOnTop = updateChildZValue(i, childrenOnTop, algorithmState, ambientState);
        }
    }

    protected float updateChildZValue(int i, float childrenOnTop, StackScrollAlgorithmState algorithmState, AmbientState ambientState) {
        ExpandableView child = algorithmState.visibleChildren.get(i);
        ExpandableViewState childViewState = child.getViewState();
        int zDistanceBetweenElements = ambientState.getZDistanceBetweenElements();
        float baseZ = ambientState.getBaseZHeight();
        if (child.mustStayOnScreen() && !childViewState.headsUpIsVisible && !ambientState.isDozingAndNotPulsing(child) && childViewState.yTranslation < ambientState.getTopPadding() + ambientState.getStackTranslation()) {
            if (childrenOnTop != 0.0f) {
                childrenOnTop += 1.0f;
            } else {
                float overlap = (ambientState.getTopPadding() + ambientState.getStackTranslation()) - childViewState.yTranslation;
                childrenOnTop += Math.min(1.0f, overlap / childViewState.height);
            }
            float overlap2 = zDistanceBetweenElements;
            childViewState.zTranslation = (overlap2 * childrenOnTop) + baseZ;
        } else if (i == 0 && (child.isAboveShelf() || child.showingPulsing())) {
            int shelfHeight = ambientState.getShelf() == null ? 0 : ambientState.getShelf().getIntrinsicHeight();
            float shelfStart = (ambientState.getInnerHeight() - shelfHeight) + ambientState.getTopPadding() + ambientState.getStackTranslation();
            float notificationEnd = childViewState.yTranslation + child.getPinnedHeadsUpHeight() + this.mPaddingBetweenElements;
            if (shelfStart > notificationEnd) {
                childViewState.zTranslation = baseZ;
            } else {
                float factor = (notificationEnd - shelfStart) / shelfHeight;
                childViewState.zTranslation = (zDistanceBetweenElements * Math.min(factor, 1.0f)) + baseZ;
            }
        } else {
            childViewState.zTranslation = baseZ;
        }
        childViewState.zTranslation += (1.0f - child.getHeaderVisibleAmount()) * this.mPinnedZTranslationExtra;
        return childrenOnTop;
    }

    public void setIsExpanded(boolean isExpanded) {
        this.mIsExpanded = isExpanded;
    }

    /* loaded from: classes21.dex */
    public class StackScrollAlgorithmState {
        public int anchorViewIndex;
        public int anchorViewY;
        private int indexOfExpandingNotification;
        public int scrollY;
        public final ArrayList<ExpandableView> visibleChildren = new ArrayList<>();
        public final HashMap<ExpandableView, Float> paddingMap = new HashMap<>();

        public StackScrollAlgorithmState() {
        }

        public int getPaddingAfterChild(ExpandableView child) {
            Float padding = this.paddingMap.get(child);
            if (padding == null) {
                return StackScrollAlgorithm.this.mPaddingBetweenElements;
            }
            return (int) padding.floatValue();
        }

        public int getIndexOfExpandingNotification() {
            return this.indexOfExpandingNotification;
        }
    }
}
