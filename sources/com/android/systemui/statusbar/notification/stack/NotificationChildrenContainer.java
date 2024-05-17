package com.android.systemui.statusbar.notification.stack;

import android.app.Notification;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.service.notification.StatusBarNotification;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.NotificationHeaderView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.R;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.NotificationHeaderUtil;
import com.android.systemui.statusbar.TransformableView;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.HybridGroupManager;
import com.android.systemui.statusbar.notification.row.HybridNotificationView;
import com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes21.dex */
public class NotificationChildrenContainer extends ViewGroup {
    private static final AnimationProperties ALPHA_FADE_IN = new AnimationProperties() { // from class: com.android.systemui.statusbar.notification.stack.NotificationChildrenContainer.1
        private AnimationFilter mAnimationFilter = new AnimationFilter().animateAlpha();

        @Override // com.android.systemui.statusbar.notification.stack.AnimationProperties
        public AnimationFilter getAnimationFilter() {
            return this.mAnimationFilter;
        }
    }.setDuration(200);
    @VisibleForTesting
    static final int NUMBER_OF_CHILDREN_WHEN_CHILDREN_EXPANDED = 8;
    @VisibleForTesting
    static final int NUMBER_OF_CHILDREN_WHEN_COLLAPSED = 2;
    @VisibleForTesting
    static final int NUMBER_OF_CHILDREN_WHEN_SYSTEM_EXPANDED = 5;
    private int mActualHeight;
    private int mChildPadding;
    private final List<ExpandableNotificationRow> mChildren;
    private boolean mChildrenExpanded;
    private int mClipBottomAmount;
    private float mCollapsedBottompadding;
    private ExpandableNotificationRow mContainingNotification;
    private ViewGroup mCurrentHeader;
    private int mCurrentHeaderTranslation;
    private float mDividerAlpha;
    private int mDividerHeight;
    private final List<View> mDividers;
    private boolean mEnableShadowOnChildNotifications;
    private ViewState mGroupOverFlowState;
    private View.OnClickListener mHeaderClickListener;
    private int mHeaderHeight;
    private NotificationHeaderUtil mHeaderUtil;
    private ViewState mHeaderViewState;
    private float mHeaderVisibleAmount;
    private boolean mHideDividersDuringExpand;
    private final HybridGroupManager mHybridGroupManager;
    private boolean mIsLowPriority;
    private boolean mNeverAppliedGroupState;
    private NotificationHeaderView mNotificationHeader;
    private NotificationHeaderView mNotificationHeaderLowPriority;
    private int mNotificationHeaderMargin;
    private NotificationViewWrapper mNotificationHeaderWrapper;
    private NotificationViewWrapper mNotificationHeaderWrapperLowPriority;
    private int mNotificatonTopPadding;
    private TextView mOverflowNumber;
    private int mRealHeight;
    private boolean mShowDividersWhenExpanded;
    private int mTranslationForHeader;
    private boolean mUserLocked;

    public NotificationChildrenContainer(Context context) {
        this(context, null);
    }

    public NotificationChildrenContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NotificationChildrenContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public NotificationChildrenContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mDividers = new ArrayList();
        this.mChildren = new ArrayList();
        this.mCurrentHeaderTranslation = 0;
        this.mHeaderVisibleAmount = 1.0f;
        this.mHybridGroupManager = new HybridGroupManager(getContext(), this);
        initDimens();
        setClipChildren(false);
    }

    private void initDimens() {
        Resources res = getResources();
        this.mChildPadding = res.getDimensionPixelSize(R.dimen.notification_children_padding);
        this.mDividerHeight = res.getDimensionPixelSize(R.dimen.notification_children_container_divider_height);
        this.mDividerAlpha = res.getFloat(R.dimen.notification_divider_alpha);
        this.mNotificationHeaderMargin = res.getDimensionPixelSize(R.dimen.notification_children_container_margin_top);
        this.mNotificatonTopPadding = res.getDimensionPixelSize(R.dimen.notification_children_container_top_padding);
        this.mHeaderHeight = this.mNotificationHeaderMargin + this.mNotificatonTopPadding;
        this.mCollapsedBottompadding = res.getDimensionPixelSize(17105310);
        this.mEnableShadowOnChildNotifications = res.getBoolean(R.bool.config_enableShadowOnChildNotifications);
        this.mShowDividersWhenExpanded = res.getBoolean(R.bool.config_showDividersWhenGroupNotificationExpanded);
        this.mHideDividersDuringExpand = res.getBoolean(R.bool.config_hideDividersDuringExpand);
        this.mTranslationForHeader = res.getDimensionPixelSize(17105310) - this.mNotificationHeaderMargin;
        this.mHybridGroupManager.initDimens();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = Math.min(this.mChildren.size(), 8);
        for (int i = 0; i < childCount; i++) {
            View child = this.mChildren.get(i);
            child.layout(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
            this.mDividers.get(i).layout(0, 0, getWidth(), this.mDividerHeight);
        }
        if (this.mOverflowNumber != null) {
            boolean isRtl = getLayoutDirection() == 1;
            int left = isRtl ? 0 : getWidth() - this.mOverflowNumber.getMeasuredWidth();
            int right = this.mOverflowNumber.getMeasuredWidth() + left;
            TextView textView = this.mOverflowNumber;
            textView.layout(left, 0, right, textView.getMeasuredHeight());
        }
        NotificationHeaderView notificationHeaderView = this.mNotificationHeader;
        if (notificationHeaderView != null) {
            notificationHeaderView.layout(0, 0, notificationHeaderView.getMeasuredWidth(), this.mNotificationHeader.getMeasuredHeight());
        }
        NotificationHeaderView notificationHeaderView2 = this.mNotificationHeaderLowPriority;
        if (notificationHeaderView2 != null) {
            notificationHeaderView2.layout(0, 0, notificationHeaderView2.getMeasuredWidth(), this.mNotificationHeaderLowPriority.getMeasuredHeight());
        }
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        TextView textView;
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        boolean hasFixedHeight = heightMode == 1073741824;
        boolean isHeightLimited = heightMode == Integer.MIN_VALUE;
        int size = View.MeasureSpec.getSize(heightMeasureSpec);
        int newHeightSpec = heightMeasureSpec;
        if (hasFixedHeight || isHeightLimited) {
            newHeightSpec = View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE);
        }
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        TextView textView2 = this.mOverflowNumber;
        if (textView2 != null) {
            textView2.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), newHeightSpec);
        }
        int dividerHeightSpec = View.MeasureSpec.makeMeasureSpec(this.mDividerHeight, 1073741824);
        int height = this.mNotificationHeaderMargin + this.mNotificatonTopPadding;
        int childCount = Math.min(this.mChildren.size(), 8);
        int collapsedChildren = getMaxAllowedVisibleChildren(true);
        int overflowIndex = childCount > collapsedChildren ? collapsedChildren - 1 : -1;
        int height2 = height;
        int height3 = 0;
        while (height3 < childCount) {
            ExpandableNotificationRow child = this.mChildren.get(height3);
            int overflowIndex2 = overflowIndex;
            boolean isOverflow = height3 == overflowIndex2;
            child.setSingleLineWidthIndention((!isOverflow || (textView = this.mOverflowNumber) == null) ? 0 : textView.getMeasuredWidth());
            child.measure(widthMeasureSpec, newHeightSpec);
            View divider = this.mDividers.get(height3);
            divider.measure(widthMeasureSpec, dividerHeightSpec);
            int overflowIndex3 = child.getVisibility();
            boolean hasFixedHeight2 = hasFixedHeight;
            if (overflowIndex3 != 8) {
                height2 += child.getMeasuredHeight() + this.mDividerHeight;
            }
            height3++;
            overflowIndex = overflowIndex2;
            hasFixedHeight = hasFixedHeight2;
        }
        this.mRealHeight = height2;
        if (heightMode != 0) {
            height2 = Math.min(height2, size);
        }
        int headerHeightSpec = View.MeasureSpec.makeMeasureSpec(this.mHeaderHeight, 1073741824);
        NotificationHeaderView notificationHeaderView = this.mNotificationHeader;
        if (notificationHeaderView != null) {
            notificationHeaderView.measure(widthMeasureSpec, headerHeightSpec);
        }
        if (this.mNotificationHeaderLowPriority != null) {
            int headerHeightSpec2 = View.MeasureSpec.makeMeasureSpec(this.mHeaderHeight, 1073741824);
            this.mNotificationHeaderLowPriority.measure(widthMeasureSpec, headerHeightSpec2);
        }
        setMeasuredDimension(width, height2);
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    public boolean pointInView(float localX, float localY, float slop) {
        return localX >= (-slop) && localY >= (-slop) && localX < ((float) (this.mRight - this.mLeft)) + slop && localY < ((float) this.mRealHeight) + slop;
    }

    public void addNotification(ExpandableNotificationRow row, int childIndex) {
        int newIndex = childIndex < 0 ? this.mChildren.size() : childIndex;
        this.mChildren.add(newIndex, row);
        addView(row);
        row.setUserLocked(this.mUserLocked);
        View divider = inflateDivider();
        addView(divider);
        this.mDividers.add(newIndex, divider);
        updateGroupOverflow();
        row.setContentTransformationAmount(0.0f, false);
        ExpandableViewState viewState = row.getViewState();
        if (viewState != null) {
            viewState.cancelAnimations(row);
            row.cancelAppearDrawing();
        }
    }

    public void removeNotification(ExpandableNotificationRow row) {
        int childIndex = this.mChildren.indexOf(row);
        this.mChildren.remove(row);
        removeView(row);
        final View divider = this.mDividers.remove(childIndex);
        removeView(divider);
        getOverlay().add(divider);
        CrossFadeHelper.fadeOut(divider, new Runnable() { // from class: com.android.systemui.statusbar.notification.stack.NotificationChildrenContainer.2
            @Override // java.lang.Runnable
            public void run() {
                NotificationChildrenContainer.this.getOverlay().remove(divider);
            }
        });
        row.setSystemChildExpanded(false);
        row.setUserLocked(false);
        updateGroupOverflow();
        if (!row.isRemoved()) {
            this.mHeaderUtil.restoreNotificationHeader(row);
        }
    }

    public int getNotificationChildCount() {
        return this.mChildren.size();
    }

    public void recreateNotificationHeader(View.OnClickListener listener) {
        this.mHeaderClickListener = listener;
        StatusBarNotification notification = this.mContainingNotification.getStatusBarNotification();
        Notification.Builder builder = Notification.Builder.recoverBuilder(getContext(), notification.getNotification());
        RemoteViews header = builder.makeNotificationHeader();
        if (this.mNotificationHeader == null) {
            this.mNotificationHeader = header.apply(getContext(), this);
            View expandButton = this.mNotificationHeader.findViewById(16908998);
            expandButton.setVisibility(0);
            this.mNotificationHeader.setOnClickListener(this.mHeaderClickListener);
            this.mNotificationHeaderWrapper = NotificationViewWrapper.wrap(getContext(), this.mNotificationHeader, this.mContainingNotification);
            addView((View) this.mNotificationHeader, 0);
            invalidate();
        } else {
            header.reapply(getContext(), this.mNotificationHeader);
        }
        this.mNotificationHeaderWrapper.onContentUpdated(this.mContainingNotification);
        recreateLowPriorityHeader(builder);
        updateHeaderVisibility(false);
        updateChildrenHeaderAppearance();
    }

    private void recreateLowPriorityHeader(Notification.Builder builder) {
        StatusBarNotification notification = this.mContainingNotification.getStatusBarNotification();
        if (this.mIsLowPriority) {
            if (builder == null) {
                builder = Notification.Builder.recoverBuilder(getContext(), notification.getNotification());
            }
            RemoteViews header = builder.makeLowPriorityContentView(true);
            if (this.mNotificationHeaderLowPriority == null) {
                this.mNotificationHeaderLowPriority = header.apply(getContext(), this);
                View expandButton = this.mNotificationHeaderLowPriority.findViewById(16908998);
                expandButton.setVisibility(0);
                this.mNotificationHeaderLowPriority.setOnClickListener(this.mHeaderClickListener);
                this.mNotificationHeaderWrapperLowPriority = NotificationViewWrapper.wrap(getContext(), this.mNotificationHeaderLowPriority, this.mContainingNotification);
                addView((View) this.mNotificationHeaderLowPriority, 0);
                invalidate();
            } else {
                header.reapply(getContext(), this.mNotificationHeaderLowPriority);
            }
            this.mNotificationHeaderWrapperLowPriority.onContentUpdated(this.mContainingNotification);
            resetHeaderVisibilityIfNeeded(this.mNotificationHeaderLowPriority, calculateDesiredHeader());
            return;
        }
        removeView(this.mNotificationHeaderLowPriority);
        this.mNotificationHeaderLowPriority = null;
        this.mNotificationHeaderWrapperLowPriority = null;
    }

    public void updateChildrenHeaderAppearance() {
        this.mHeaderUtil.updateChildrenHeaderAppearance();
    }

    public void updateGroupOverflow() {
        int childCount = this.mChildren.size();
        int maxAllowedVisibleChildren = getMaxAllowedVisibleChildren(true);
        if (childCount > maxAllowedVisibleChildren) {
            int number = childCount - maxAllowedVisibleChildren;
            this.mOverflowNumber = this.mHybridGroupManager.bindOverflowNumber(this.mOverflowNumber, number);
            if (this.mGroupOverFlowState == null) {
                this.mGroupOverFlowState = new ViewState();
                this.mNeverAppliedGroupState = true;
                return;
            }
            return;
        }
        TextView textView = this.mOverflowNumber;
        if (textView != null) {
            removeView(textView);
            if (isShown() && isAttachedToWindow()) {
                final View removedOverflowNumber = this.mOverflowNumber;
                addTransientView(removedOverflowNumber, getTransientViewCount());
                CrossFadeHelper.fadeOut(removedOverflowNumber, new Runnable() { // from class: com.android.systemui.statusbar.notification.stack.NotificationChildrenContainer.3
                    @Override // java.lang.Runnable
                    public void run() {
                        NotificationChildrenContainer.this.removeTransientView(removedOverflowNumber);
                    }
                });
            }
            this.mOverflowNumber = null;
            this.mGroupOverFlowState = null;
        }
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateGroupOverflow();
    }

    private View inflateDivider() {
        return LayoutInflater.from(this.mContext).inflate(R.layout.notification_children_divider, (ViewGroup) this, false);
    }

    public List<ExpandableNotificationRow> getNotificationChildren() {
        return this.mChildren;
    }

    public boolean applyChildOrder(List<ExpandableNotificationRow> childOrder, VisualStabilityManager visualStabilityManager, VisualStabilityManager.Callback callback) {
        if (childOrder == null) {
            return false;
        }
        boolean result = false;
        for (int i = 0; i < this.mChildren.size() && i < childOrder.size(); i++) {
            ExpandableNotificationRow child = this.mChildren.get(i);
            ExpandableNotificationRow desiredChild = childOrder.get(i);
            if (child != desiredChild) {
                if (visualStabilityManager.canReorderNotification(desiredChild)) {
                    this.mChildren.remove(desiredChild);
                    this.mChildren.add(i, desiredChild);
                    result = true;
                } else {
                    visualStabilityManager.addReorderingAllowedCallback(callback);
                }
            }
        }
        updateExpansionStates();
        return result;
    }

    private void updateExpansionStates() {
        if (this.mChildrenExpanded || this.mUserLocked) {
            return;
        }
        int size = this.mChildren.size();
        for (int i = 0; i < size; i++) {
            ExpandableNotificationRow child = this.mChildren.get(i);
            boolean z = true;
            if (i != 0 || size != 1) {
                z = false;
            }
            child.setSystemChildExpanded(z);
        }
    }

    public int getIntrinsicHeight() {
        int maxAllowedVisibleChildren = getMaxAllowedVisibleChildren();
        return getIntrinsicHeight(maxAllowedVisibleChildren);
    }

    private int getIntrinsicHeight(float maxAllowedVisibleChildren) {
        int i;
        int intrinsicHeight;
        if (showingAsLowPriority()) {
            return this.mNotificationHeaderLowPriority.getHeight();
        }
        int intrinsicHeight2 = this.mNotificationHeaderMargin + this.mCurrentHeaderTranslation;
        int visibleChildren = 0;
        int childCount = this.mChildren.size();
        boolean firstChild = true;
        float expandFactor = 0.0f;
        if (this.mUserLocked) {
            expandFactor = getGroupExpandFraction();
        }
        boolean childrenExpanded = this.mChildrenExpanded;
        for (int i2 = 0; i2 < childCount && visibleChildren < maxAllowedVisibleChildren; i2++) {
            if (!firstChild) {
                if (this.mUserLocked) {
                    intrinsicHeight = (int) (intrinsicHeight2 + NotificationUtils.interpolate(this.mChildPadding, this.mDividerHeight, expandFactor));
                } else {
                    intrinsicHeight = intrinsicHeight2 + (childrenExpanded ? this.mDividerHeight : this.mChildPadding);
                }
            } else {
                if (this.mUserLocked) {
                    intrinsicHeight = (int) (intrinsicHeight2 + NotificationUtils.interpolate(0.0f, this.mNotificatonTopPadding + this.mDividerHeight, expandFactor));
                } else {
                    if (childrenExpanded) {
                        i = this.mNotificatonTopPadding + this.mDividerHeight;
                    } else {
                        i = 0;
                    }
                    intrinsicHeight = intrinsicHeight2 + i;
                }
                firstChild = false;
            }
            ExpandableNotificationRow child = this.mChildren.get(i2);
            intrinsicHeight2 = intrinsicHeight + child.getIntrinsicHeight();
            visibleChildren++;
        }
        if (this.mUserLocked) {
            return (int) (intrinsicHeight2 + NotificationUtils.interpolate(this.mCollapsedBottompadding, 0.0f, expandFactor));
        }
        if (!childrenExpanded) {
            return (int) (intrinsicHeight2 + this.mCollapsedBottompadding);
        }
        return intrinsicHeight2;
    }

    public void updateState(ExpandableViewState parentState, AmbientState ambientState) {
        float f;
        int yPosition;
        boolean firstChild;
        float f2;
        int maxAllowedVisibleChildren;
        int childCount = this.mChildren.size();
        int yPosition2 = this.mNotificationHeaderMargin + this.mCurrentHeaderTranslation;
        boolean firstChild2 = true;
        int maxAllowedVisibleChildren2 = getMaxAllowedVisibleChildren();
        int lastVisibleIndex = maxAllowedVisibleChildren2 - 1;
        int firstOverflowIndex = lastVisibleIndex + 1;
        float expandFactor = 0.0f;
        boolean expandingToExpandedGroup = this.mUserLocked && !showingAsLowPriority();
        if (this.mUserLocked) {
            expandFactor = getGroupExpandFraction();
            firstOverflowIndex = getMaxAllowedVisibleChildren(true);
        }
        boolean childrenExpandedAndNotAnimating = this.mChildrenExpanded && !this.mContainingNotification.isGroupExpansionChanging();
        int launchTransitionCompensation = 0;
        int i = 0;
        while (i < childCount) {
            ExpandableNotificationRow child = this.mChildren.get(i);
            if (!firstChild2) {
                if (expandingToExpandedGroup) {
                    yPosition = (int) (yPosition2 + NotificationUtils.interpolate(this.mChildPadding, this.mDividerHeight, expandFactor));
                    firstChild = firstChild2;
                } else {
                    boolean firstChild3 = firstChild2;
                    boolean firstChild4 = this.mChildrenExpanded;
                    yPosition = yPosition2 + (firstChild4 ? this.mDividerHeight : this.mChildPadding);
                    firstChild = firstChild3;
                }
            } else {
                if (expandingToExpandedGroup) {
                    yPosition = (int) (yPosition2 + NotificationUtils.interpolate(0.0f, this.mNotificatonTopPadding + this.mDividerHeight, expandFactor));
                } else {
                    yPosition = yPosition2 + (this.mChildrenExpanded ? this.mDividerHeight + this.mNotificatonTopPadding : 0);
                }
                firstChild = false;
            }
            ExpandableViewState childState = child.getViewState();
            int intrinsicHeight = child.getIntrinsicHeight();
            childState.height = intrinsicHeight;
            boolean firstChild5 = firstChild;
            childState.yTranslation = yPosition + launchTransitionCompensation;
            childState.hidden = false;
            if (childrenExpandedAndNotAnimating && this.mEnableShadowOnChildNotifications) {
                f2 = parentState.zTranslation;
            } else {
                f2 = 0.0f;
            }
            childState.zTranslation = f2;
            childState.dimmed = parentState.dimmed;
            childState.hideSensitive = parentState.hideSensitive;
            childState.belowSpeedBump = parentState.belowSpeedBump;
            childState.clipTopAmount = 0;
            childState.alpha = 0.0f;
            if (i < firstOverflowIndex) {
                childState.alpha = showingAsLowPriority() ? expandFactor : 1.0f;
                maxAllowedVisibleChildren = maxAllowedVisibleChildren2;
            } else if (expandFactor != 1.0f || i > lastVisibleIndex) {
                maxAllowedVisibleChildren = maxAllowedVisibleChildren2;
            } else {
                maxAllowedVisibleChildren = maxAllowedVisibleChildren2;
                childState.alpha = (this.mActualHeight - childState.yTranslation) / childState.height;
                childState.alpha = Math.max(0.0f, Math.min(1.0f, childState.alpha));
            }
            childState.location = parentState.location;
            childState.inShelf = parentState.inShelf;
            yPosition2 = yPosition + intrinsicHeight;
            if (child.isExpandAnimationRunning()) {
                launchTransitionCompensation = -ambientState.getExpandAnimationTopChange();
            }
            i++;
            firstChild2 = firstChild5;
            maxAllowedVisibleChildren2 = maxAllowedVisibleChildren;
        }
        if (this.mOverflowNumber == null) {
            f = 0.0f;
        } else {
            ExpandableNotificationRow overflowView = this.mChildren.get(Math.min(getMaxAllowedVisibleChildren(true), childCount) - 1);
            this.mGroupOverFlowState.copyFrom(overflowView.getViewState());
            if (!this.mChildrenExpanded) {
                HybridNotificationView alignView = overflowView.getSingleLineView();
                if (alignView != null) {
                    View mirrorView = alignView.getTextView();
                    if (mirrorView.getVisibility() == 8) {
                        mirrorView = alignView.getTitleView();
                    }
                    if (mirrorView.getVisibility() == 8) {
                        mirrorView = alignView;
                    }
                    this.mGroupOverFlowState.alpha = mirrorView.getAlpha();
                    this.mGroupOverFlowState.yTranslation += NotificationUtils.getRelativeYOffset(mirrorView, overflowView);
                }
                f = 0.0f;
            } else {
                this.mGroupOverFlowState.yTranslation += this.mNotificationHeaderMargin;
                f = 0.0f;
                this.mGroupOverFlowState.alpha = 0.0f;
            }
        }
        if (this.mNotificationHeader != null) {
            if (this.mHeaderViewState == null) {
                this.mHeaderViewState = new ViewState();
            }
            this.mHeaderViewState.initFrom(this.mNotificationHeader);
            ViewState viewState = this.mHeaderViewState;
            if (childrenExpandedAndNotAnimating) {
                f = parentState.zTranslation;
            }
            viewState.zTranslation = f;
            ViewState viewState2 = this.mHeaderViewState;
            viewState2.yTranslation = this.mCurrentHeaderTranslation;
            viewState2.alpha = this.mHeaderVisibleAmount;
            viewState2.hidden = false;
        }
    }

    private boolean updateChildStateForExpandedGroup(ExpandableNotificationRow child, int parentHeight, ExpandableViewState childState, int yPosition) {
        boolean z;
        int top = child.getClipTopAmount() + yPosition;
        int intrinsicHeight = child.getIntrinsicHeight();
        int bottom = top + intrinsicHeight;
        int newHeight = intrinsicHeight;
        if (bottom >= parentHeight) {
            newHeight = Math.max(parentHeight - top, 0);
        }
        if (newHeight != 0) {
            z = false;
        } else {
            z = true;
        }
        childState.hidden = z;
        childState.height = newHeight;
        if (childState.height == intrinsicHeight || childState.hidden) {
            return false;
        }
        return true;
    }

    @VisibleForTesting
    int getMaxAllowedVisibleChildren() {
        return getMaxAllowedVisibleChildren(false);
    }

    @VisibleForTesting
    int getMaxAllowedVisibleChildren(boolean likeCollapsed) {
        if (!likeCollapsed && ((this.mChildrenExpanded || this.mContainingNotification.isUserLocked()) && !showingAsLowPriority())) {
            return 8;
        }
        if (!this.mIsLowPriority) {
            if (this.mContainingNotification.isOnKeyguard() || !this.mContainingNotification.isExpanded()) {
                if (this.mContainingNotification.isHeadsUpState() && this.mContainingNotification.canShowHeadsUp()) {
                    return 5;
                }
                return 2;
            }
            return 5;
        }
        return 5;
    }

    public void applyState() {
        int childCount = this.mChildren.size();
        ViewState tmpState = new ViewState();
        float expandFraction = 0.0f;
        if (this.mUserLocked) {
            expandFraction = getGroupExpandFraction();
        }
        boolean dividersVisible = (this.mUserLocked && !showingAsLowPriority()) || (this.mChildrenExpanded && this.mShowDividersWhenExpanded) || (this.mContainingNotification.isGroupExpansionChanging() && !this.mHideDividersDuringExpand);
        for (int i = 0; i < childCount; i++) {
            ExpandableNotificationRow child = this.mChildren.get(i);
            ExpandableViewState viewState = child.getViewState();
            viewState.applyToView(child);
            View divider = this.mDividers.get(i);
            tmpState.initFrom(divider);
            tmpState.yTranslation = viewState.yTranslation - this.mDividerHeight;
            float alpha = (!this.mChildrenExpanded || viewState.alpha == 0.0f) ? 0.0f : this.mDividerAlpha;
            if (this.mUserLocked && !showingAsLowPriority() && viewState.alpha != 0.0f) {
                alpha = NotificationUtils.interpolate(0.0f, 0.5f, Math.min(viewState.alpha, expandFraction));
            }
            tmpState.hidden = !dividersVisible;
            tmpState.alpha = alpha;
            tmpState.applyToView(divider);
            child.setFakeShadowIntensity(0.0f, 0.0f, 0, 0);
        }
        ViewState viewState2 = this.mGroupOverFlowState;
        if (viewState2 != null) {
            viewState2.applyToView(this.mOverflowNumber);
            this.mNeverAppliedGroupState = false;
        }
        ViewState viewState3 = this.mHeaderViewState;
        if (viewState3 != null) {
            viewState3.applyToView(this.mNotificationHeader);
        }
        updateChildrenClipping();
    }

    private void updateChildrenClipping() {
        if (this.mContainingNotification.hasExpandingChild()) {
            return;
        }
        int childCount = this.mChildren.size();
        int layoutEnd = this.mContainingNotification.getActualHeight() - this.mClipBottomAmount;
        for (int i = 0; i < childCount; i++) {
            ExpandableNotificationRow child = this.mChildren.get(i);
            if (child.getVisibility() != 8) {
                float childTop = child.getTranslationY();
                float childBottom = child.getActualHeight() + childTop;
                boolean visible = true;
                int clipBottomAmount = 0;
                if (childTop > layoutEnd) {
                    visible = false;
                } else if (childBottom > layoutEnd) {
                    clipBottomAmount = (int) (childBottom - layoutEnd);
                }
                boolean isVisible = child.getVisibility() == 0;
                if (visible != isVisible) {
                    child.setVisibility(visible ? 0 : 4);
                }
                child.setClipBottomAmount(clipBottomAmount);
            }
        }
    }

    public void prepareExpansionChanged() {
    }

    public void startAnimationToState(AnimationProperties properties) {
        int childCount = this.mChildren.size();
        ViewState tmpState = new ViewState();
        float expandFraction = getGroupExpandFraction();
        boolean dividersVisible = (this.mUserLocked && !showingAsLowPriority()) || (this.mChildrenExpanded && this.mShowDividersWhenExpanded) || (this.mContainingNotification.isGroupExpansionChanging() && !this.mHideDividersDuringExpand);
        for (int i = childCount - 1; i >= 0; i--) {
            ExpandableNotificationRow child = this.mChildren.get(i);
            ExpandableViewState viewState = child.getViewState();
            viewState.animateTo(child, properties);
            View divider = this.mDividers.get(i);
            tmpState.initFrom(divider);
            tmpState.yTranslation = viewState.yTranslation - this.mDividerHeight;
            float alpha = (!this.mChildrenExpanded || viewState.alpha == 0.0f) ? 0.0f : 0.5f;
            if (this.mUserLocked && !showingAsLowPriority() && viewState.alpha != 0.0f) {
                alpha = NotificationUtils.interpolate(0.0f, 0.5f, Math.min(viewState.alpha, expandFraction));
            }
            tmpState.hidden = !dividersVisible;
            tmpState.alpha = alpha;
            tmpState.animateTo(divider, properties);
            child.setFakeShadowIntensity(0.0f, 0.0f, 0, 0);
        }
        if (this.mOverflowNumber != null) {
            if (this.mNeverAppliedGroupState) {
                float alpha2 = this.mGroupOverFlowState.alpha;
                ViewState viewState2 = this.mGroupOverFlowState;
                viewState2.alpha = 0.0f;
                viewState2.applyToView(this.mOverflowNumber);
                this.mGroupOverFlowState.alpha = alpha2;
                this.mNeverAppliedGroupState = false;
            }
            this.mGroupOverFlowState.animateTo(this.mOverflowNumber, properties);
        }
        View view = this.mNotificationHeader;
        if (view != null) {
            this.mHeaderViewState.applyToView(view);
        }
        updateChildrenClipping();
    }

    public ExpandableNotificationRow getViewAtPosition(float y) {
        int count = this.mChildren.size();
        for (int childIdx = 0; childIdx < count; childIdx++) {
            ExpandableNotificationRow slidingChild = this.mChildren.get(childIdx);
            float childTop = slidingChild.getTranslationY();
            float top = slidingChild.getClipTopAmount() + childTop;
            float bottom = slidingChild.getActualHeight() + childTop;
            if (y >= top && y <= bottom) {
                return slidingChild;
            }
        }
        return null;
    }

    public void setChildrenExpanded(boolean childrenExpanded) {
        this.mChildrenExpanded = childrenExpanded;
        updateExpansionStates();
        NotificationHeaderView notificationHeaderView = this.mNotificationHeader;
        if (notificationHeaderView != null) {
            notificationHeaderView.setExpanded(childrenExpanded);
        }
        int count = this.mChildren.size();
        for (int childIdx = 0; childIdx < count; childIdx++) {
            ExpandableNotificationRow child = this.mChildren.get(childIdx);
            child.setChildrenExpanded(childrenExpanded, false);
        }
        updateHeaderTouchability();
    }

    public void setContainingNotification(ExpandableNotificationRow parent) {
        this.mContainingNotification = parent;
        this.mHeaderUtil = new NotificationHeaderUtil(this.mContainingNotification);
    }

    public ExpandableNotificationRow getContainingNotification() {
        return this.mContainingNotification;
    }

    public NotificationHeaderView getHeaderView() {
        return this.mNotificationHeader;
    }

    public NotificationHeaderView getLowPriorityHeaderView() {
        return this.mNotificationHeaderLowPriority;
    }

    @VisibleForTesting
    public ViewGroup getCurrentHeaderView() {
        return this.mCurrentHeader;
    }

    private void updateHeaderVisibility(boolean animate) {
        ViewGroup currentHeader = this.mCurrentHeader;
        ViewGroup desiredHeader = calculateDesiredHeader();
        if (currentHeader == desiredHeader) {
            return;
        }
        if (animate) {
            if (desiredHeader != null && currentHeader != null) {
                currentHeader.setVisibility(0);
                desiredHeader.setVisibility(0);
                TransformableView visibleWrapper = getWrapperForView(desiredHeader);
                NotificationViewWrapper hiddenWrapper = getWrapperForView(currentHeader);
                visibleWrapper.transformFrom(hiddenWrapper);
                hiddenWrapper.transformTo(visibleWrapper, new Runnable() { // from class: com.android.systemui.statusbar.notification.stack.-$$Lambda$NotificationChildrenContainer$yaCA55rJjaS5fwWl4gZlw69MJ2w
                    @Override // java.lang.Runnable
                    public final void run() {
                        NotificationChildrenContainer.this.lambda$updateHeaderVisibility$0$NotificationChildrenContainer();
                    }
                });
                startChildAlphaAnimations(desiredHeader == this.mNotificationHeader);
            } else {
                animate = false;
            }
        }
        if (!animate) {
            if (desiredHeader != null) {
                getWrapperForView(desiredHeader).setVisible(true);
                desiredHeader.setVisibility(0);
            }
            if (currentHeader != null) {
                NotificationViewWrapper wrapper = getWrapperForView(currentHeader);
                if (wrapper != null) {
                    wrapper.setVisible(false);
                }
                currentHeader.setVisibility(4);
            }
        }
        resetHeaderVisibilityIfNeeded(this.mNotificationHeader, desiredHeader);
        resetHeaderVisibilityIfNeeded(this.mNotificationHeaderLowPriority, desiredHeader);
        this.mCurrentHeader = desiredHeader;
    }

    public /* synthetic */ void lambda$updateHeaderVisibility$0$NotificationChildrenContainer() {
        updateHeaderVisibility(false);
    }

    private void resetHeaderVisibilityIfNeeded(View header, View desiredHeader) {
        if (header == null) {
            return;
        }
        if (header != this.mCurrentHeader && header != desiredHeader) {
            getWrapperForView(header).setVisible(false);
            header.setVisibility(4);
        }
        if (header == desiredHeader && header.getVisibility() != 0) {
            getWrapperForView(header).setVisible(true);
            header.setVisibility(0);
        }
    }

    private ViewGroup calculateDesiredHeader() {
        if (showingAsLowPriority()) {
            return this.mNotificationHeaderLowPriority;
        }
        return this.mNotificationHeader;
    }

    private void startChildAlphaAnimations(boolean toVisible) {
        float target = toVisible ? 1.0f : 0.0f;
        float start = 1.0f - target;
        int childCount = this.mChildren.size();
        for (int i = 0; i < childCount && i < 5; i++) {
            ExpandableNotificationRow child = this.mChildren.get(i);
            child.setAlpha(start);
            ViewState viewState = new ViewState();
            viewState.initFrom(child);
            viewState.alpha = target;
            ALPHA_FADE_IN.setDelay(i * 50);
            viewState.animateTo(child, ALPHA_FADE_IN);
        }
    }

    private void updateHeaderTransformation() {
        if (this.mUserLocked && showingAsLowPriority()) {
            float fraction = getGroupExpandFraction();
            this.mNotificationHeaderWrapper.transformFrom(this.mNotificationHeaderWrapperLowPriority, fraction);
            this.mNotificationHeader.setVisibility(0);
            this.mNotificationHeaderWrapperLowPriority.transformTo(this.mNotificationHeaderWrapper, fraction);
        }
    }

    private NotificationViewWrapper getWrapperForView(View visibleHeader) {
        if (visibleHeader == this.mNotificationHeader) {
            return this.mNotificationHeaderWrapper;
        }
        return this.mNotificationHeaderWrapperLowPriority;
    }

    public void updateHeaderForExpansion(boolean expanded) {
        NotificationHeaderView notificationHeaderView = this.mNotificationHeader;
        if (notificationHeaderView != null) {
            if (expanded) {
                ColorDrawable cd = new ColorDrawable();
                cd.setColor(this.mContainingNotification.calculateBgColor());
                this.mNotificationHeader.setHeaderBackgroundDrawable(cd);
                return;
            }
            notificationHeaderView.setHeaderBackgroundDrawable((Drawable) null);
        }
    }

    public int getMaxContentHeight() {
        int minHeight;
        if (showingAsLowPriority()) {
            return getMinHeight(5, true);
        }
        int maxContentHeight = this.mNotificationHeaderMargin + this.mCurrentHeaderTranslation + this.mNotificatonTopPadding;
        int visibleChildren = 0;
        int childCount = this.mChildren.size();
        for (int i = 0; i < childCount && visibleChildren < 8; i++) {
            ExpandableNotificationRow child = this.mChildren.get(i);
            if (child.isExpanded(true)) {
                minHeight = child.getMaxExpandHeight();
            } else {
                minHeight = child.getShowingLayout().getMinHeight(true);
            }
            float childHeight = minHeight;
            maxContentHeight = (int) (maxContentHeight + childHeight);
            visibleChildren++;
        }
        if (visibleChildren > 0) {
            return maxContentHeight + (this.mDividerHeight * visibleChildren);
        }
        return maxContentHeight;
    }

    public void setActualHeight(int actualHeight) {
        float childHeight;
        if (!this.mUserLocked) {
            return;
        }
        this.mActualHeight = actualHeight;
        float fraction = getGroupExpandFraction();
        boolean showingLowPriority = showingAsLowPriority();
        updateHeaderTransformation();
        int maxAllowedVisibleChildren = getMaxAllowedVisibleChildren(true);
        int childCount = this.mChildren.size();
        for (int i = 0; i < childCount; i++) {
            ExpandableNotificationRow child = this.mChildren.get(i);
            if (showingLowPriority) {
                childHeight = child.getShowingLayout().getMinHeight(false);
            } else if (!child.isExpanded(true)) {
                childHeight = child.getShowingLayout().getMinHeight(true);
            } else {
                childHeight = child.getMaxExpandHeight();
            }
            if (i < maxAllowedVisibleChildren) {
                float singleLineHeight = child.getShowingLayout().getMinHeight(false);
                child.setActualHeight((int) NotificationUtils.interpolate(singleLineHeight, childHeight, fraction), false);
            } else {
                child.setActualHeight((int) childHeight, false);
            }
        }
    }

    public float getGroupExpandFraction() {
        int visibleChildrenExpandedHeight = showingAsLowPriority() ? getMaxContentHeight() : getVisibleChildrenExpandHeight();
        int minExpandHeight = getCollapsedHeight();
        float factor = (this.mActualHeight - minExpandHeight) / (visibleChildrenExpandedHeight - minExpandHeight);
        return Math.max(0.0f, Math.min(1.0f, factor));
    }

    private int getVisibleChildrenExpandHeight() {
        int minHeight;
        int intrinsicHeight = this.mNotificationHeaderMargin + this.mCurrentHeaderTranslation + this.mNotificatonTopPadding + this.mDividerHeight;
        int visibleChildren = 0;
        int childCount = this.mChildren.size();
        int maxAllowedVisibleChildren = getMaxAllowedVisibleChildren(true);
        for (int i = 0; i < childCount && visibleChildren < maxAllowedVisibleChildren; i++) {
            ExpandableNotificationRow child = this.mChildren.get(i);
            if (!child.isExpanded(true)) {
                minHeight = child.getShowingLayout().getMinHeight(true);
            } else {
                minHeight = child.getMaxExpandHeight();
            }
            float childHeight = minHeight;
            intrinsicHeight = (int) (intrinsicHeight + childHeight);
            visibleChildren++;
        }
        return intrinsicHeight;
    }

    public int getMinHeight() {
        return getMinHeight(2, false);
    }

    public int getCollapsedHeight() {
        return getMinHeight(getMaxAllowedVisibleChildren(true), false);
    }

    public int getCollapsedHeightWithoutHeader() {
        return getMinHeight(getMaxAllowedVisibleChildren(true), false, 0);
    }

    private int getMinHeight(int maxAllowedVisibleChildren, boolean likeHighPriority) {
        return getMinHeight(maxAllowedVisibleChildren, likeHighPriority, this.mCurrentHeaderTranslation);
    }

    private int getMinHeight(int maxAllowedVisibleChildren, boolean likeHighPriority, int headerTranslation) {
        if (!likeHighPriority && showingAsLowPriority()) {
            return this.mNotificationHeaderLowPriority.getHeight();
        }
        int minExpandHeight = this.mNotificationHeaderMargin + headerTranslation;
        int visibleChildren = 0;
        boolean firstChild = true;
        int childCount = this.mChildren.size();
        for (int i = 0; i < childCount && visibleChildren < maxAllowedVisibleChildren; i++) {
            if (!firstChild) {
                minExpandHeight += this.mChildPadding;
            } else {
                firstChild = false;
            }
            ExpandableNotificationRow child = this.mChildren.get(i);
            minExpandHeight += child.getSingleLineView().getHeight();
            visibleChildren++;
        }
        return (int) (minExpandHeight + this.mCollapsedBottompadding);
    }

    public boolean showingAsLowPriority() {
        return this.mIsLowPriority && !this.mContainingNotification.isExpanded();
    }

    public void reInflateViews(View.OnClickListener listener, StatusBarNotification notification) {
        NotificationHeaderView notificationHeaderView = this.mNotificationHeader;
        if (notificationHeaderView != null) {
            removeView(notificationHeaderView);
            this.mNotificationHeader = null;
        }
        NotificationHeaderView notificationHeaderView2 = this.mNotificationHeaderLowPriority;
        if (notificationHeaderView2 != null) {
            removeView(notificationHeaderView2);
            this.mNotificationHeaderLowPriority = null;
        }
        recreateNotificationHeader(listener);
        initDimens();
        for (int i = 0; i < this.mDividers.size(); i++) {
            View prevDivider = this.mDividers.get(i);
            int index = indexOfChild(prevDivider);
            removeView(prevDivider);
            View divider = inflateDivider();
            addView(divider, index);
            this.mDividers.set(i, divider);
        }
        removeView(this.mOverflowNumber);
        this.mOverflowNumber = null;
        this.mGroupOverFlowState = null;
        updateGroupOverflow();
    }

    public void setUserLocked(boolean userLocked) {
        this.mUserLocked = userLocked;
        if (!this.mUserLocked) {
            updateHeaderVisibility(false);
        }
        int childCount = this.mChildren.size();
        for (int i = 0; i < childCount; i++) {
            ExpandableNotificationRow child = this.mChildren.get(i);
            child.setUserLocked(userLocked && !showingAsLowPriority());
        }
        updateHeaderTouchability();
    }

    private void updateHeaderTouchability() {
        NotificationHeaderView notificationHeaderView = this.mNotificationHeader;
        if (notificationHeaderView != null) {
            notificationHeaderView.setAcceptAllTouches(this.mChildrenExpanded || this.mUserLocked);
        }
    }

    public void onNotificationUpdated() {
        this.mHybridGroupManager.setOverflowNumberColor(this.mOverflowNumber, this.mContainingNotification.getNotificationColor());
    }

    public int getPositionInLinearLayout(View childInGroup) {
        int position = this.mNotificationHeaderMargin + this.mCurrentHeaderTranslation + this.mNotificatonTopPadding;
        int i = 0;
        while (true) {
            if (i >= this.mChildren.size()) {
                return 0;
            }
            ExpandableNotificationRow child = this.mChildren.get(i);
            boolean notGone = child.getVisibility() != 8;
            if (notGone) {
                position += this.mDividerHeight;
            }
            if (child == childInGroup) {
                return position;
            }
            if (notGone) {
                position += child.getIntrinsicHeight();
            }
            i++;
        }
    }

    public void setIconsVisible(boolean iconsVisible) {
        NotificationHeaderView header;
        NotificationHeaderView header2;
        NotificationViewWrapper notificationViewWrapper = this.mNotificationHeaderWrapper;
        if (notificationViewWrapper != null && (header2 = notificationViewWrapper.getNotificationHeader()) != null) {
            header2.getIcon().setForceHidden(!iconsVisible);
        }
        NotificationViewWrapper notificationViewWrapper2 = this.mNotificationHeaderWrapperLowPriority;
        if (notificationViewWrapper2 != null && (header = notificationViewWrapper2.getNotificationHeader()) != null) {
            header.getIcon().setForceHidden(!iconsVisible);
        }
    }

    public void setClipBottomAmount(int clipBottomAmount) {
        this.mClipBottomAmount = clipBottomAmount;
        updateChildrenClipping();
    }

    public void setIsLowPriority(boolean isLowPriority) {
        this.mIsLowPriority = isLowPriority;
        if (this.mContainingNotification != null) {
            recreateLowPriorityHeader(null);
            updateHeaderVisibility(false);
        }
        boolean z = this.mUserLocked;
        if (z) {
            setUserLocked(z);
        }
    }

    public NotificationHeaderView getVisibleHeader() {
        NotificationHeaderView header = this.mNotificationHeader;
        if (showingAsLowPriority()) {
            NotificationHeaderView header2 = this.mNotificationHeaderLowPriority;
            return header2;
        }
        return header;
    }

    public void onExpansionChanged() {
        if (this.mIsLowPriority) {
            boolean z = this.mUserLocked;
            if (z) {
                setUserLocked(z);
            }
            updateHeaderVisibility(true);
        }
    }

    public float getIncreasedPaddingAmount() {
        if (showingAsLowPriority()) {
            return 0.0f;
        }
        return getGroupExpandFraction();
    }

    @VisibleForTesting
    public boolean isUserLocked() {
        return this.mUserLocked;
    }

    public void setCurrentBottomRoundness(float currentBottomRoundness) {
        boolean last = true;
        for (int i = this.mChildren.size() - 1; i >= 0; i--) {
            ExpandableNotificationRow child = this.mChildren.get(i);
            if (child.getVisibility() != 8) {
                float bottomRoundness = last ? currentBottomRoundness : 0.0f;
                child.setBottomRoundness(bottomRoundness, isShown());
                last = false;
            }
        }
    }

    public void setHeaderVisibleAmount(float headerVisibleAmount) {
        this.mHeaderVisibleAmount = headerVisibleAmount;
        this.mCurrentHeaderTranslation = (int) ((1.0f - headerVisibleAmount) * this.mTranslationForHeader);
    }
}
