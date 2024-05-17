package com.android.systemui.statusbar.notification.stack;

import android.content.Context;
import android.util.MathUtils;
import android.view.View;
import com.android.systemui.R;
import com.android.systemui.statusbar.NotificationShelf;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.notification.stack.StackScrollAlgorithm;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import java.util.ArrayList;
/* loaded from: classes21.dex */
public class AmbientState {
    private static final float MAX_PULSE_HEIGHT = 100000.0f;
    private ActivatableNotificationView mActivatedChild;
    private int mAnchorViewIndex;
    private int mAnchorViewY;
    private boolean mAppearing;
    private int mBaseZHeight;
    private float mCurrentScrollVelocity;
    private boolean mDimmed;
    private boolean mDismissAllInProgress;
    private boolean mDozing;
    private int mExpandAnimationTopChange;
    private ExpandableNotificationRow mExpandingNotification;
    private float mExpandingVelocity;
    private boolean mExpansionChanging;
    private HeadsUpManager mHeadUpManager;
    private float mHideAmount;
    private boolean mHideSensitive;
    private int mIntrinsicPadding;
    private ActivatableNotificationView mLastVisibleBackgroundChild;
    private int mLayoutHeight;
    private int mLayoutMinHeight;
    private float mMaxHeadsUpTranslation;
    private int mMaxLayoutHeight;
    private Runnable mOnPulseHeightChangedListener;
    private float mOverScrollBottomAmount;
    private float mOverScrollTopAmount;
    private boolean mPanelFullWidth;
    private boolean mPanelTracking;
    private boolean mPulsing;
    private boolean mQsCustomizerShowing;
    private int mScrollY;
    private final StackScrollAlgorithm.SectionProvider mSectionProvider;
    private boolean mShadeExpanded;
    private NotificationShelf mShelf;
    private float mStackTranslation;
    private int mStatusBarState;
    private int mTopPadding;
    private boolean mUnlockHintRunning;
    private int mZDistanceBetweenElements;
    private ArrayList<ExpandableView> mDraggedViews = new ArrayList<>();
    private int mSpeedBumpIndex = -1;
    private float mPulseHeight = MAX_PULSE_HEIGHT;
    private float mDozeAmount = 0.0f;

    public AmbientState(Context context, StackScrollAlgorithm.SectionProvider sectionProvider, HeadsUpManager headsUpManager) {
        this.mSectionProvider = sectionProvider;
        this.mHeadUpManager = headsUpManager;
        reload(context);
    }

    public void reload(Context context) {
        this.mZDistanceBetweenElements = getZDistanceBetweenElements(context);
        this.mBaseZHeight = getBaseHeight(this.mZDistanceBetweenElements);
    }

    private static int getZDistanceBetweenElements(Context context) {
        return Math.max(1, context.getResources().getDimensionPixelSize(R.dimen.z_distance_between_notifications));
    }

    private static int getBaseHeight(int zdistanceBetweenElements) {
        return zdistanceBetweenElements * 4;
    }

    public static int getNotificationLaunchHeight(Context context) {
        int zDistance = getZDistanceBetweenElements(context);
        return getBaseHeight(zDistance) * 2;
    }

    public int getBaseZHeight() {
        return this.mBaseZHeight;
    }

    public int getZDistanceBetweenElements() {
        return this.mZDistanceBetweenElements;
    }

    public int getScrollY() {
        return this.mScrollY;
    }

    public void setScrollY(int scrollY) {
        this.mScrollY = scrollY;
    }

    public int getAnchorViewIndex() {
        return this.mAnchorViewIndex;
    }

    public void setAnchorViewIndex(int anchorViewIndex) {
        this.mAnchorViewIndex = anchorViewIndex;
    }

    public int getAnchorViewY() {
        return this.mAnchorViewY;
    }

    public void setAnchorViewY(int anchorViewY) {
        this.mAnchorViewY = anchorViewY;
    }

    public void onBeginDrag(ExpandableView view) {
        this.mDraggedViews.add(view);
    }

    public void onDragFinished(View view) {
        this.mDraggedViews.remove(view);
    }

    public ArrayList<ExpandableView> getDraggedViews() {
        return this.mDraggedViews;
    }

    public void setDimmed(boolean dimmed) {
        this.mDimmed = dimmed;
    }

    public void setDozing(boolean dozing) {
        this.mDozing = dozing;
    }

    public void setHideAmount(float hidemount) {
        if (hidemount == 1.0f && this.mHideAmount != hidemount) {
            setPulseHeight(MAX_PULSE_HEIGHT);
        }
        this.mHideAmount = hidemount;
    }

    public float getHideAmount() {
        return this.mHideAmount;
    }

    public void setHideSensitive(boolean hideSensitive) {
        this.mHideSensitive = hideSensitive;
    }

    public void setActivatedChild(ActivatableNotificationView activatedChild) {
        this.mActivatedChild = activatedChild;
    }

    public boolean isDimmed() {
        return this.mDimmed && !(isPulseExpanding() && this.mDozeAmount == 1.0f);
    }

    public boolean isDozing() {
        return this.mDozing;
    }

    public boolean isHideSensitive() {
        return this.mHideSensitive;
    }

    public ActivatableNotificationView getActivatedChild() {
        return this.mActivatedChild;
    }

    public void setOverScrollAmount(float amount, boolean onTop) {
        if (onTop) {
            this.mOverScrollTopAmount = amount;
        } else {
            this.mOverScrollBottomAmount = amount;
        }
    }

    public float getOverScrollAmount(boolean top) {
        return top ? this.mOverScrollTopAmount : this.mOverScrollBottomAmount;
    }

    public int getSpeedBumpIndex() {
        return this.mSpeedBumpIndex;
    }

    public void setSpeedBumpIndex(int shelfIndex) {
        this.mSpeedBumpIndex = shelfIndex;
    }

    public StackScrollAlgorithm.SectionProvider getSectionProvider() {
        return this.mSectionProvider;
    }

    public float getStackTranslation() {
        return this.mStackTranslation;
    }

    public void setStackTranslation(float stackTranslation) {
        this.mStackTranslation = stackTranslation;
    }

    public void setLayoutHeight(int layoutHeight) {
        this.mLayoutHeight = layoutHeight;
    }

    public float getTopPadding() {
        return this.mTopPadding;
    }

    public void setTopPadding(int topPadding) {
        this.mTopPadding = topPadding;
    }

    public int getInnerHeight() {
        return getInnerHeight(false);
    }

    public int getInnerHeight(boolean ignorePulseHeight) {
        if (this.mDozeAmount == 1.0f && !isPulseExpanding()) {
            return this.mShelf.getHeight();
        }
        int height = Math.max(this.mLayoutMinHeight, Math.min(this.mLayoutHeight, this.mMaxLayoutHeight) - this.mTopPadding);
        if (ignorePulseHeight) {
            return height;
        }
        float pulseHeight = Math.min(this.mPulseHeight, height);
        return (int) MathUtils.lerp(height, pulseHeight, this.mDozeAmount);
    }

    public boolean isPulseExpanding() {
        return (this.mPulseHeight == MAX_PULSE_HEIGHT || this.mDozeAmount == 0.0f || this.mHideAmount == 1.0f) ? false : true;
    }

    public boolean isShadeExpanded() {
        return this.mShadeExpanded;
    }

    public void setShadeExpanded(boolean shadeExpanded) {
        this.mShadeExpanded = shadeExpanded;
    }

    public void setMaxHeadsUpTranslation(float maxHeadsUpTranslation) {
        this.mMaxHeadsUpTranslation = maxHeadsUpTranslation;
    }

    public float getMaxHeadsUpTranslation() {
        return this.mMaxHeadsUpTranslation;
    }

    public void setDismissAllInProgress(boolean dismissAllInProgress) {
        this.mDismissAllInProgress = dismissAllInProgress;
    }

    public boolean isDismissAllInProgress() {
        return this.mDismissAllInProgress;
    }

    public void setLayoutMinHeight(int layoutMinHeight) {
        this.mLayoutMinHeight = layoutMinHeight;
    }

    public void setShelf(NotificationShelf shelf) {
        this.mShelf = shelf;
    }

    public NotificationShelf getShelf() {
        return this.mShelf;
    }

    public void setLayoutMaxHeight(int maxLayoutHeight) {
        this.mMaxLayoutHeight = maxLayoutHeight;
    }

    public void setLastVisibleBackgroundChild(ActivatableNotificationView lastVisibleBackgroundChild) {
        this.mLastVisibleBackgroundChild = lastVisibleBackgroundChild;
    }

    public ActivatableNotificationView getLastVisibleBackgroundChild() {
        return this.mLastVisibleBackgroundChild;
    }

    public void setCurrentScrollVelocity(float currentScrollVelocity) {
        this.mCurrentScrollVelocity = currentScrollVelocity;
    }

    public float getCurrentScrollVelocity() {
        return this.mCurrentScrollVelocity;
    }

    public boolean isOnKeyguard() {
        return this.mStatusBarState == 1;
    }

    public void setStatusBarState(int statusBarState) {
        this.mStatusBarState = statusBarState;
    }

    public void setExpandingVelocity(float expandingVelocity) {
        this.mExpandingVelocity = expandingVelocity;
    }

    public void setExpansionChanging(boolean expansionChanging) {
        this.mExpansionChanging = expansionChanging;
    }

    public boolean isExpansionChanging() {
        return this.mExpansionChanging;
    }

    public float getExpandingVelocity() {
        return this.mExpandingVelocity;
    }

    public void setPanelTracking(boolean panelTracking) {
        this.mPanelTracking = panelTracking;
    }

    public boolean hasPulsingNotifications() {
        HeadsUpManager headsUpManager;
        return this.mPulsing && (headsUpManager = this.mHeadUpManager) != null && headsUpManager.hasNotifications();
    }

    public void setPulsing(boolean hasPulsing) {
        this.mPulsing = hasPulsing;
    }

    public boolean isPulsing() {
        return this.mPulsing;
    }

    public boolean isPulsing(NotificationEntry entry) {
        HeadsUpManager headsUpManager;
        if (!this.mPulsing || (headsUpManager = this.mHeadUpManager) == null) {
            return false;
        }
        return headsUpManager.isAlerting(entry.key);
    }

    public boolean isPanelTracking() {
        return this.mPanelTracking;
    }

    public boolean isPanelFullWidth() {
        return this.mPanelFullWidth;
    }

    public void setPanelFullWidth(boolean panelFullWidth) {
        this.mPanelFullWidth = panelFullWidth;
    }

    public void setUnlockHintRunning(boolean unlockHintRunning) {
        this.mUnlockHintRunning = unlockHintRunning;
    }

    public boolean isUnlockHintRunning() {
        return this.mUnlockHintRunning;
    }

    public boolean isQsCustomizerShowing() {
        return this.mQsCustomizerShowing;
    }

    public void setQsCustomizerShowing(boolean qsCustomizerShowing) {
        this.mQsCustomizerShowing = qsCustomizerShowing;
    }

    public void setIntrinsicPadding(int intrinsicPadding) {
        this.mIntrinsicPadding = intrinsicPadding;
    }

    public int getIntrinsicPadding() {
        return this.mIntrinsicPadding;
    }

    public boolean isDozingAndNotPulsing(ExpandableView view) {
        if (view instanceof ExpandableNotificationRow) {
            return isDozingAndNotPulsing((ExpandableNotificationRow) view);
        }
        return false;
    }

    public boolean isDozingAndNotPulsing(ExpandableNotificationRow row) {
        return isDozing() && !isPulsing(row.getEntry());
    }

    public void setExpandAnimationTopChange(int expandAnimationTopChange) {
        this.mExpandAnimationTopChange = expandAnimationTopChange;
    }

    public void setExpandingNotification(ExpandableNotificationRow row) {
        this.mExpandingNotification = row;
    }

    public ExpandableNotificationRow getExpandingNotification() {
        return this.mExpandingNotification;
    }

    public int getExpandAnimationTopChange() {
        return this.mExpandAnimationTopChange;
    }

    public boolean isFullyHidden() {
        return this.mHideAmount == 1.0f;
    }

    public boolean isHiddenAtAll() {
        return this.mHideAmount != 0.0f;
    }

    public void setAppearing(boolean appearing) {
        this.mAppearing = appearing;
    }

    public boolean isAppearing() {
        return this.mAppearing;
    }

    public void setPulseHeight(float height) {
        if (height != this.mPulseHeight) {
            this.mPulseHeight = height;
            Runnable runnable = this.mOnPulseHeightChangedListener;
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    public float getPulseHeight() {
        float f = this.mPulseHeight;
        if (f == MAX_PULSE_HEIGHT) {
            return 0.0f;
        }
        return f;
    }

    public void setDozeAmount(float dozeAmount) {
        if (dozeAmount != this.mDozeAmount) {
            this.mDozeAmount = dozeAmount;
            if (dozeAmount == 0.0f || dozeAmount == 1.0f) {
                setPulseHeight(MAX_PULSE_HEIGHT);
            }
        }
    }

    public boolean isFullyAwake() {
        return this.mDozeAmount == 0.0f;
    }

    public void setOnPulseHeightChangedListener(Runnable onPulseHeightChangedListener) {
        this.mOnPulseHeightChangedListener = onPulseHeightChangedListener;
    }

    public Runnable getOnPulseHeightChangedListener() {
        return this.mOnPulseHeightChangedListener;
    }
}
