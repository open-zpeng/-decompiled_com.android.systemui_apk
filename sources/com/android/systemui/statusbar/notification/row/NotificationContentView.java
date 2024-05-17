package com.android.systemui.statusbar.notification.row;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Rect;
import android.service.notification.StatusBarNotification;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.NotificationHeaderView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ContrastColorUtil;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.statusbar.MediaTransferManager;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.SmartReplyController;
import com.android.systemui.statusbar.TransformableView;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.wrapper.NotificationCustomViewWrapper;
import com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.policy.InflatedSmartReplies;
import com.android.systemui.statusbar.policy.RemoteInputView;
import com.android.systemui.statusbar.policy.SmartReplyConstants;
import com.android.systemui.statusbar.policy.SmartReplyView;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.function.Consumer;
/* loaded from: classes21.dex */
public class NotificationContentView extends FrameLayout {
    public static final int UNDEFINED = -1;
    public static final int VISIBLE_TYPE_CONTRACTED = 0;
    public static final int VISIBLE_TYPE_EXPANDED = 1;
    public static final int VISIBLE_TYPE_HEADSUP = 2;
    private static final int VISIBLE_TYPE_SINGLELINE = 3;
    private boolean mAnimate;
    private int mAnimationStartVisibleType;
    private boolean mBeforeN;
    private RemoteInputView mCachedExpandedRemoteInput;
    private RemoteInputView mCachedHeadsUpRemoteInput;
    private int mClipBottomAmount;
    private final Rect mClipBounds;
    private boolean mClipToActualHeight;
    private int mClipTopAmount;
    private ExpandableNotificationRow mContainingNotification;
    private int mContentHeight;
    private int mContentHeightAtAnimationStart;
    private View mContractedChild;
    private NotificationViewWrapper mContractedWrapper;
    private InflatedSmartReplies.SmartRepliesAndActions mCurrentSmartRepliesAndActions;
    private final ViewTreeObserver.OnPreDrawListener mEnableAnimationPredrawListener;
    private View.OnClickListener mExpandClickListener;
    private boolean mExpandable;
    private View mExpandedChild;
    private InflatedSmartReplies mExpandedInflatedSmartReplies;
    private RemoteInputView mExpandedRemoteInput;
    private SmartReplyView mExpandedSmartReplyView;
    private Runnable mExpandedVisibleListener;
    private NotificationViewWrapper mExpandedWrapper;
    private boolean mFocusOnVisibilityChange;
    private boolean mForceSelectNextLayout;
    private NotificationGroupManager mGroupManager;
    private boolean mHeadsUpAnimatingAway;
    private View mHeadsUpChild;
    private int mHeadsUpHeight;
    private InflatedSmartReplies mHeadsUpInflatedSmartReplies;
    private RemoteInputView mHeadsUpRemoteInput;
    private SmartReplyView mHeadsUpSmartReplyView;
    private NotificationViewWrapper mHeadsUpWrapper;
    private HybridGroupManager mHybridGroupManager;
    private boolean mIconsVisible;
    private boolean mIsChildInGroup;
    private boolean mIsContentExpandable;
    private boolean mIsHeadsUp;
    private boolean mIsLowPriority;
    private boolean mLegacy;
    private MediaTransferManager mMediaTransferManager;
    private int mMinContractedHeight;
    private int mNotificationContentMarginEnd;
    private int mNotificationMaxHeight;
    private final ArrayMap<View, Runnable> mOnContentViewInactiveListeners;
    private PendingIntent mPreviousExpandedRemoteInputIntent;
    private PendingIntent mPreviousHeadsUpRemoteInputIntent;
    private RemoteInputController mRemoteInputController;
    private boolean mRemoteInputVisible;
    private HybridNotificationView mSingleLineView;
    private int mSingleLineWidthIndention;
    private int mSmallHeight;
    private SmartReplyConstants mSmartReplyConstants;
    private SmartReplyController mSmartReplyController;
    private StatusBarNotification mStatusBarNotification;
    private int mTransformationStartVisibleType;
    private int mUnrestrictedContentHeight;
    private boolean mUserExpanding;
    private int mVisibleType;
    private static final String TAG = "NotificationContentView";
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);

    public NotificationContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mClipBounds = new Rect();
        this.mVisibleType = 0;
        this.mOnContentViewInactiveListeners = new ArrayMap<>();
        this.mEnableAnimationPredrawListener = new ViewTreeObserver.OnPreDrawListener() { // from class: com.android.systemui.statusbar.notification.row.NotificationContentView.1
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                NotificationContentView.this.post(new Runnable() { // from class: com.android.systemui.statusbar.notification.row.NotificationContentView.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        NotificationContentView.this.mAnimate = true;
                    }
                });
                NotificationContentView.this.getViewTreeObserver().removeOnPreDrawListener(this);
                return true;
            }
        };
        this.mClipToActualHeight = true;
        this.mAnimationStartVisibleType = -1;
        this.mForceSelectNextLayout = true;
        this.mContentHeightAtAnimationStart = -1;
        this.mHybridGroupManager = new HybridGroupManager(getContext(), this);
        this.mMediaTransferManager = new MediaTransferManager(getContext());
        this.mSmartReplyConstants = (SmartReplyConstants) Dependency.get(SmartReplyConstants.class);
        this.mSmartReplyController = (SmartReplyController) Dependency.get(SmartReplyController.class);
        initView();
    }

    public void initView() {
        this.mMinContractedHeight = getResources().getDimensionPixelSize(R.dimen.min_notification_layout_height);
        this.mNotificationContentMarginEnd = getResources().getDimensionPixelSize(17105312);
    }

    public void setHeights(int smallHeight, int headsUpMaxHeight, int maxHeight) {
        this.mSmallHeight = smallHeight;
        this.mHeadsUpHeight = headsUpMaxHeight;
        this.mNotificationMaxHeight = maxHeight;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxSize;
        int size;
        boolean useExactly;
        boolean useExactly2;
        int size2;
        int heightSpec;
        int measuredHeight;
        int size3;
        boolean useExactly3;
        int i;
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        boolean hasFixedHeight = heightMode == 1073741824;
        boolean isHeightLimited = heightMode == Integer.MIN_VALUE;
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        if (!hasFixedHeight && !isHeightLimited) {
            maxSize = 1073741823;
        } else {
            int maxSize2 = View.MeasureSpec.getSize(heightMeasureSpec);
            maxSize = maxSize2;
        }
        int maxChildHeight = 0;
        if (this.mExpandedChild != null) {
            int notificationMaxHeight = this.mNotificationMaxHeight;
            SmartReplyView smartReplyView = this.mExpandedSmartReplyView;
            if (smartReplyView != null) {
                notificationMaxHeight += smartReplyView.getHeightUpperLimit();
            }
            int notificationMaxHeight2 = notificationMaxHeight + this.mExpandedWrapper.getExtraMeasureHeight();
            ViewGroup.LayoutParams layoutParams = this.mExpandedChild.getLayoutParams();
            if (layoutParams.height >= 0) {
                int size4 = Math.min(notificationMaxHeight2, layoutParams.height);
                size3 = size4;
                useExactly3 = true;
            } else {
                size3 = notificationMaxHeight2;
                useExactly3 = false;
            }
            if (useExactly3) {
                i = 1073741824;
            } else {
                i = Integer.MIN_VALUE;
            }
            int spec = View.MeasureSpec.makeMeasureSpec(size3, i);
            measureChildWithMargins(this.mExpandedChild, widthMeasureSpec, 0, spec, 0);
            maxChildHeight = Math.max(0, this.mExpandedChild.getMeasuredHeight());
        }
        View view = this.mContractedChild;
        if (view != null) {
            int size5 = this.mSmallHeight;
            ViewGroup.LayoutParams layoutParams2 = view.getLayoutParams();
            if (layoutParams2.height < 0) {
                useExactly2 = false;
                size2 = size5;
            } else {
                useExactly2 = true;
                size2 = Math.min(size5, layoutParams2.height);
            }
            boolean useExactly4 = shouldContractedBeFixedSize();
            if (useExactly4 || useExactly2) {
                int heightSpec2 = View.MeasureSpec.makeMeasureSpec(size2, 1073741824);
                heightSpec = heightSpec2;
            } else {
                heightSpec = View.MeasureSpec.makeMeasureSpec(size2, Integer.MIN_VALUE);
            }
            measureChildWithMargins(this.mContractedChild, widthMeasureSpec, 0, heightSpec, 0);
            int measuredHeight2 = this.mContractedChild.getMeasuredHeight();
            int i2 = this.mMinContractedHeight;
            if (measuredHeight2 >= i2) {
                measuredHeight = measuredHeight2;
            } else {
                heightSpec = View.MeasureSpec.makeMeasureSpec(i2, 1073741824);
                measuredHeight = measuredHeight2;
                measureChildWithMargins(this.mContractedChild, widthMeasureSpec, 0, heightSpec, 0);
            }
            maxChildHeight = Math.max(maxChildHeight, measuredHeight);
            if (updateContractedHeaderWidth()) {
                measureChildWithMargins(this.mContractedChild, widthMeasureSpec, 0, heightSpec, 0);
            }
            if (this.mExpandedChild != null && this.mContractedChild.getMeasuredHeight() > this.mExpandedChild.getMeasuredHeight()) {
                int heightSpec3 = View.MeasureSpec.makeMeasureSpec(this.mContractedChild.getMeasuredHeight(), 1073741824);
                measureChildWithMargins(this.mExpandedChild, widthMeasureSpec, 0, heightSpec3, 0);
            }
        }
        if (this.mHeadsUpChild != null) {
            int maxHeight = this.mHeadsUpHeight;
            SmartReplyView smartReplyView2 = this.mHeadsUpSmartReplyView;
            if (smartReplyView2 != null) {
                maxHeight += smartReplyView2.getHeightUpperLimit();
            }
            int maxHeight2 = maxHeight + this.mHeadsUpWrapper.getExtraMeasureHeight();
            ViewGroup.LayoutParams layoutParams3 = this.mHeadsUpChild.getLayoutParams();
            if (layoutParams3.height >= 0) {
                int size6 = Math.min(maxHeight2, layoutParams3.height);
                size = size6;
                useExactly = true;
            } else {
                size = maxHeight2;
                useExactly = false;
            }
            measureChildWithMargins(this.mHeadsUpChild, widthMeasureSpec, 0, View.MeasureSpec.makeMeasureSpec(size, useExactly ? 1073741824 : Integer.MIN_VALUE), 0);
            maxChildHeight = Math.max(maxChildHeight, this.mHeadsUpChild.getMeasuredHeight());
        }
        if (this.mSingleLineView != null) {
            int singleLineWidthSpec = widthMeasureSpec;
            if (this.mSingleLineWidthIndention != 0 && View.MeasureSpec.getMode(widthMeasureSpec) != 0) {
                singleLineWidthSpec = View.MeasureSpec.makeMeasureSpec((width - this.mSingleLineWidthIndention) + this.mSingleLineView.getPaddingEnd(), 1073741824);
            }
            this.mSingleLineView.measure(singleLineWidthSpec, View.MeasureSpec.makeMeasureSpec(this.mNotificationMaxHeight, Integer.MIN_VALUE));
            maxChildHeight = Math.max(maxChildHeight, this.mSingleLineView.getMeasuredHeight());
        }
        int ownHeight = Math.min(maxChildHeight, maxSize);
        setMeasuredDimension(width, ownHeight);
    }

    private int getExtraRemoteInputHeight(RemoteInputView remoteInput) {
        if (remoteInput != null) {
            if (remoteInput.isActive() || remoteInput.isSending()) {
                return getResources().getDimensionPixelSize(17105310);
            }
            return 0;
        }
        return 0;
    }

    private boolean updateContractedHeaderWidth() {
        int paddingLeft;
        int i;
        NotificationHeaderView contractedHeader = this.mContractedWrapper.getNotificationHeader();
        if (contractedHeader != null) {
            if (this.mExpandedChild != null && this.mExpandedWrapper.getNotificationHeader() != null) {
                NotificationHeaderView expandedHeader = this.mExpandedWrapper.getNotificationHeader();
                int headerTextMargin = expandedHeader.getHeaderTextMarginEnd();
                if (headerTextMargin != contractedHeader.getHeaderTextMarginEnd()) {
                    contractedHeader.setHeaderTextMarginEnd(headerTextMargin);
                    return true;
                }
            } else {
                int paddingEnd = this.mNotificationContentMarginEnd;
                if (contractedHeader.getPaddingEnd() != paddingEnd) {
                    if (contractedHeader.isLayoutRtl()) {
                        paddingLeft = paddingEnd;
                    } else {
                        paddingLeft = contractedHeader.getPaddingLeft();
                    }
                    int paddingTop = contractedHeader.getPaddingTop();
                    if (contractedHeader.isLayoutRtl()) {
                        i = contractedHeader.getPaddingLeft();
                    } else {
                        i = paddingEnd;
                    }
                    contractedHeader.setPadding(paddingLeft, paddingTop, i, contractedHeader.getPaddingBottom());
                    contractedHeader.setShowWorkBadgeAtEnd(false);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean shouldContractedBeFixedSize() {
        return this.mBeforeN && (this.mContractedWrapper instanceof NotificationCustomViewWrapper);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int previousHeight = 0;
        View view = this.mExpandedChild;
        if (view != null) {
            previousHeight = view.getHeight();
        }
        super.onLayout(changed, left, top, right, bottom);
        if (previousHeight != 0 && this.mExpandedChild.getHeight() != previousHeight) {
            this.mContentHeightAtAnimationStart = previousHeight;
        }
        updateClipping();
        invalidateOutline();
        selectLayout(false, this.mForceSelectNextLayout);
        this.mForceSelectNextLayout = false;
        updateExpandButtons(this.mExpandable);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateVisibility();
    }

    public View getContractedChild() {
        return this.mContractedChild;
    }

    public View getExpandedChild() {
        return this.mExpandedChild;
    }

    public View getHeadsUpChild() {
        return this.mHeadsUpChild;
    }

    public void setContractedChild(View child) {
        View view = this.mContractedChild;
        if (view != null) {
            view.animate().cancel();
            removeView(this.mContractedChild);
        }
        if (child == null) {
            this.mContractedChild = null;
            this.mContractedWrapper = null;
            if (this.mTransformationStartVisibleType == 0) {
                this.mTransformationStartVisibleType = -1;
                return;
            }
            return;
        }
        addView(child);
        this.mContractedChild = child;
        this.mContractedWrapper = NotificationViewWrapper.wrap(getContext(), child, this.mContainingNotification);
    }

    private NotificationViewWrapper getWrapperForView(View child) {
        if (child == this.mContractedChild) {
            return this.mContractedWrapper;
        }
        if (child == this.mExpandedChild) {
            return this.mExpandedWrapper;
        }
        if (child == this.mHeadsUpChild) {
            return this.mHeadsUpWrapper;
        }
        return null;
    }

    public void setExpandedChild(View child) {
        if (this.mExpandedChild != null) {
            this.mPreviousExpandedRemoteInputIntent = null;
            RemoteInputView remoteInputView = this.mExpandedRemoteInput;
            if (remoteInputView != null) {
                remoteInputView.onNotificationUpdateOrReset();
                if (this.mExpandedRemoteInput.isActive()) {
                    this.mPreviousExpandedRemoteInputIntent = this.mExpandedRemoteInput.getPendingIntent();
                    RemoteInputView remoteInputView2 = this.mExpandedRemoteInput;
                    this.mCachedExpandedRemoteInput = remoteInputView2;
                    remoteInputView2.dispatchStartTemporaryDetach();
                    ((ViewGroup) this.mExpandedRemoteInput.getParent()).removeView(this.mExpandedRemoteInput);
                }
            }
            this.mExpandedChild.animate().cancel();
            removeView(this.mExpandedChild);
            this.mExpandedRemoteInput = null;
        }
        if (child == null) {
            this.mExpandedChild = null;
            this.mExpandedWrapper = null;
            if (this.mTransformationStartVisibleType == 1) {
                this.mTransformationStartVisibleType = -1;
            }
            if (this.mVisibleType == 1) {
                selectLayout(false, true);
                return;
            }
            return;
        }
        addView(child);
        this.mExpandedChild = child;
        this.mExpandedWrapper = NotificationViewWrapper.wrap(getContext(), child, this.mContainingNotification);
    }

    public void setHeadsUpChild(View child) {
        if (this.mHeadsUpChild != null) {
            this.mPreviousHeadsUpRemoteInputIntent = null;
            RemoteInputView remoteInputView = this.mHeadsUpRemoteInput;
            if (remoteInputView != null) {
                remoteInputView.onNotificationUpdateOrReset();
                if (this.mHeadsUpRemoteInput.isActive()) {
                    this.mPreviousHeadsUpRemoteInputIntent = this.mHeadsUpRemoteInput.getPendingIntent();
                    RemoteInputView remoteInputView2 = this.mHeadsUpRemoteInput;
                    this.mCachedHeadsUpRemoteInput = remoteInputView2;
                    remoteInputView2.dispatchStartTemporaryDetach();
                    ((ViewGroup) this.mHeadsUpRemoteInput.getParent()).removeView(this.mHeadsUpRemoteInput);
                }
            }
            this.mHeadsUpChild.animate().cancel();
            removeView(this.mHeadsUpChild);
            this.mHeadsUpRemoteInput = null;
        }
        if (child == null) {
            this.mHeadsUpChild = null;
            this.mHeadsUpWrapper = null;
            if (this.mTransformationStartVisibleType == 2) {
                this.mTransformationStartVisibleType = -1;
            }
            if (this.mVisibleType == 2) {
                selectLayout(false, true);
                return;
            }
            return;
        }
        addView(child);
        this.mHeadsUpChild = child;
        this.mHeadsUpWrapper = NotificationViewWrapper.wrap(getContext(), child, this.mContainingNotification);
    }

    @Override // android.view.ViewGroup
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        child.setTag(R.id.row_tag_for_content_view, this.mContainingNotification);
    }

    @Override // android.view.View
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        updateVisibility();
        if (visibility != 0) {
            for (Runnable r : this.mOnContentViewInactiveListeners.values()) {
                r.run();
            }
            this.mOnContentViewInactiveListeners.clear();
        }
    }

    private void updateVisibility() {
        setVisible(isShown());
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnPreDrawListener(this.mEnableAnimationPredrawListener);
    }

    private void setVisible(boolean isVisible) {
        if (isVisible) {
            getViewTreeObserver().removeOnPreDrawListener(this.mEnableAnimationPredrawListener);
            getViewTreeObserver().addOnPreDrawListener(this.mEnableAnimationPredrawListener);
            return;
        }
        getViewTreeObserver().removeOnPreDrawListener(this.mEnableAnimationPredrawListener);
        this.mAnimate = false;
    }

    private void focusExpandButtonIfNecessary() {
        ImageView expandButton;
        if (this.mFocusOnVisibilityChange) {
            NotificationHeaderView header = getVisibleNotificationHeader();
            if (header != null && (expandButton = header.getExpandButton()) != null) {
                expandButton.requestAccessibilityFocus();
            }
            this.mFocusOnVisibilityChange = false;
        }
    }

    public void setContentHeight(int contentHeight) {
        this.mUnrestrictedContentHeight = Math.max(contentHeight, getMinHeight());
        int maxContentHeight = (this.mContainingNotification.getIntrinsicHeight() - getExtraRemoteInputHeight(this.mExpandedRemoteInput)) - getExtraRemoteInputHeight(this.mHeadsUpRemoteInput);
        this.mContentHeight = Math.min(this.mUnrestrictedContentHeight, maxContentHeight);
        selectLayout(this.mAnimate, false);
        if (this.mContractedChild == null) {
            return;
        }
        int minHeightHint = getMinContentHeightHint();
        NotificationViewWrapper wrapper = getVisibleWrapper(this.mVisibleType);
        if (wrapper != null) {
            wrapper.setContentHeight(this.mUnrestrictedContentHeight, minHeightHint);
        }
        NotificationViewWrapper wrapper2 = getVisibleWrapper(this.mTransformationStartVisibleType);
        if (wrapper2 != null) {
            wrapper2.setContentHeight(this.mUnrestrictedContentHeight, minHeightHint);
        }
        updateClipping();
        invalidateOutline();
    }

    private int getMinContentHeightHint() {
        int hint;
        int i;
        if (this.mIsChildInGroup && isVisibleOrTransitioning(3)) {
            return this.mContext.getResources().getDimensionPixelSize(17105301);
        }
        if (this.mHeadsUpChild != null && this.mExpandedChild != null) {
            boolean transitioningBetweenHunAndExpanded = isTransitioningFromTo(2, 1) || isTransitioningFromTo(1, 2);
            boolean pinned = !isVisibleOrTransitioning(0) && (this.mIsHeadsUp || this.mHeadsUpAnimatingAway) && this.mContainingNotification.canShowHeadsUp();
            if (transitioningBetweenHunAndExpanded || pinned) {
                return Math.min(getViewHeight(2), getViewHeight(1));
            }
        }
        if (this.mVisibleType == 1 && (i = this.mContentHeightAtAnimationStart) >= 0 && this.mExpandedChild != null) {
            return Math.min(i, getViewHeight(1));
        }
        if (this.mHeadsUpChild != null && isVisibleOrTransitioning(2)) {
            hint = getViewHeight(2);
        } else if (this.mExpandedChild != null) {
            hint = getViewHeight(1);
        } else {
            int hint2 = getViewHeight(0);
            hint = hint2 + this.mContext.getResources().getDimensionPixelSize(17105301);
        }
        if (this.mExpandedChild != null && isVisibleOrTransitioning(1)) {
            return Math.min(hint, getViewHeight(1));
        }
        return hint;
    }

    private boolean isTransitioningFromTo(int from, int to) {
        return (this.mTransformationStartVisibleType == from || this.mAnimationStartVisibleType == from) && this.mVisibleType == to;
    }

    private boolean isVisibleOrTransitioning(int type) {
        return this.mVisibleType == type || this.mTransformationStartVisibleType == type || this.mAnimationStartVisibleType == type;
    }

    private void updateContentTransformation() {
        int visibleType = calculateVisibleType();
        int i = this.mVisibleType;
        if (visibleType != i) {
            this.mTransformationStartVisibleType = i;
            TransformableView shownView = getTransformableViewForVisibleType(visibleType);
            TransformableView hiddenView = getTransformableViewForVisibleType(this.mTransformationStartVisibleType);
            shownView.transformFrom(hiddenView, 0.0f);
            getViewForVisibleType(visibleType).setVisibility(0);
            hiddenView.transformTo(shownView, 0.0f);
            this.mVisibleType = visibleType;
            updateBackgroundColor(true);
        }
        if (this.mForceSelectNextLayout) {
            forceUpdateVisibilities();
        }
        int i2 = this.mTransformationStartVisibleType;
        if (i2 != -1 && this.mVisibleType != i2 && getViewForVisibleType(i2) != null) {
            TransformableView shownView2 = getTransformableViewForVisibleType(this.mVisibleType);
            TransformableView hiddenView2 = getTransformableViewForVisibleType(this.mTransformationStartVisibleType);
            float transformationAmount = calculateTransformationAmount();
            shownView2.transformFrom(hiddenView2, transformationAmount);
            hiddenView2.transformTo(shownView2, transformationAmount);
            updateBackgroundTransformation(transformationAmount);
            return;
        }
        updateViewVisibilities(visibleType);
        updateBackgroundColor(false);
    }

    private void updateBackgroundTransformation(float transformationAmount) {
        int endColor = getBackgroundColor(this.mVisibleType);
        int startColor = getBackgroundColor(this.mTransformationStartVisibleType);
        if (endColor != startColor) {
            if (startColor == 0) {
                startColor = this.mContainingNotification.getBackgroundColorWithoutTint();
            }
            if (endColor == 0) {
                endColor = this.mContainingNotification.getBackgroundColorWithoutTint();
            }
            endColor = NotificationUtils.interpolateColors(startColor, endColor, transformationAmount);
        }
        this.mContainingNotification.updateBackgroundAlpha(transformationAmount);
        this.mContainingNotification.setContentBackground(endColor, false, this);
    }

    private float calculateTransformationAmount() {
        int startHeight = getViewHeight(this.mTransformationStartVisibleType);
        int endHeight = getViewHeight(this.mVisibleType);
        int progress = Math.abs(this.mContentHeight - startHeight);
        int totalDistance = Math.abs(endHeight - startHeight);
        if (totalDistance == 0) {
            Log.wtf(TAG, "the total transformation distance is 0\n StartType: " + this.mTransformationStartVisibleType + " height: " + startHeight + "\n VisibleType: " + this.mVisibleType + " height: " + endHeight + "\n mContentHeight: " + this.mContentHeight);
            return 1.0f;
        }
        float amount = progress / totalDistance;
        return Math.min(1.0f, amount);
    }

    public int getContentHeight() {
        return this.mContentHeight;
    }

    public int getMaxHeight() {
        if (this.mExpandedChild != null) {
            return getViewHeight(1) + getExtraRemoteInputHeight(this.mExpandedRemoteInput);
        }
        if (this.mIsHeadsUp && this.mHeadsUpChild != null && this.mContainingNotification.canShowHeadsUp()) {
            return getViewHeight(2) + getExtraRemoteInputHeight(this.mHeadsUpRemoteInput);
        }
        if (this.mContractedChild != null) {
            return getViewHeight(0);
        }
        return this.mNotificationMaxHeight;
    }

    private int getViewHeight(int visibleType) {
        return getViewHeight(visibleType, false);
    }

    private int getViewHeight(int visibleType, boolean forceNoHeader) {
        View view = getViewForVisibleType(visibleType);
        int height = view.getHeight();
        NotificationViewWrapper viewWrapper = getWrapperForView(view);
        if (viewWrapper != null) {
            return height + viewWrapper.getHeaderTranslation(forceNoHeader);
        }
        return height;
    }

    public int getMinHeight() {
        return getMinHeight(false);
    }

    public int getMinHeight(boolean likeGroupExpanded) {
        if (likeGroupExpanded || !this.mIsChildInGroup || isGroupExpanded()) {
            return this.mContractedChild != null ? getViewHeight(0) : this.mMinContractedHeight;
        }
        return this.mSingleLineView.getHeight();
    }

    private boolean isGroupExpanded() {
        return this.mGroupManager.isGroupExpanded(this.mStatusBarNotification);
    }

    public void setClipTopAmount(int clipTopAmount) {
        this.mClipTopAmount = clipTopAmount;
        updateClipping();
    }

    public void setClipBottomAmount(int clipBottomAmount) {
        this.mClipBottomAmount = clipBottomAmount;
        updateClipping();
    }

    @Override // android.view.View
    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
        updateClipping();
    }

    private void updateClipping() {
        if (this.mClipToActualHeight) {
            int top = (int) (this.mClipTopAmount - getTranslationY());
            int bottom = (int) ((this.mUnrestrictedContentHeight - this.mClipBottomAmount) - getTranslationY());
            this.mClipBounds.set(0, top, getWidth(), Math.max(top, bottom));
            setClipBounds(this.mClipBounds);
            return;
        }
        setClipBounds(null);
    }

    public void setClipToActualHeight(boolean clipToActualHeight) {
        this.mClipToActualHeight = clipToActualHeight;
        updateClipping();
    }

    private void selectLayout(boolean animate, boolean force) {
        if (this.mContractedChild == null) {
            return;
        }
        if (this.mUserExpanding) {
            updateContentTransformation();
            return;
        }
        int visibleType = calculateVisibleType();
        boolean changedType = visibleType != this.mVisibleType;
        if (changedType || force) {
            View visibleView = getViewForVisibleType(visibleType);
            if (visibleView != null) {
                visibleView.setVisibility(0);
                transferRemoteInputFocus(visibleType);
            }
            if (animate && ((visibleType == 1 && this.mExpandedChild != null) || ((visibleType == 2 && this.mHeadsUpChild != null) || ((visibleType == 3 && this.mSingleLineView != null) || visibleType == 0)))) {
                animateToVisibleType(visibleType);
            } else {
                updateViewVisibilities(visibleType);
            }
            this.mVisibleType = visibleType;
            if (changedType) {
                focusExpandButtonIfNecessary();
            }
            NotificationViewWrapper visibleWrapper = getVisibleWrapper(visibleType);
            if (visibleWrapper != null) {
                visibleWrapper.setContentHeight(this.mUnrestrictedContentHeight, getMinContentHeightHint());
            }
            updateBackgroundColor(animate);
        }
    }

    private void forceUpdateVisibilities() {
        forceUpdateVisibility(0, this.mContractedChild, this.mContractedWrapper);
        forceUpdateVisibility(1, this.mExpandedChild, this.mExpandedWrapper);
        forceUpdateVisibility(2, this.mHeadsUpChild, this.mHeadsUpWrapper);
        HybridNotificationView hybridNotificationView = this.mSingleLineView;
        forceUpdateVisibility(3, hybridNotificationView, hybridNotificationView);
        fireExpandedVisibleListenerIfVisible();
        this.mAnimationStartVisibleType = -1;
    }

    private void fireExpandedVisibleListenerIfVisible() {
        if (this.mExpandedVisibleListener != null && this.mExpandedChild != null && isShown() && this.mExpandedChild.getVisibility() == 0) {
            Runnable listener = this.mExpandedVisibleListener;
            this.mExpandedVisibleListener = null;
            listener.run();
        }
    }

    private void forceUpdateVisibility(int type, View view, TransformableView wrapper) {
        if (view == null) {
            return;
        }
        boolean visible = this.mVisibleType == type || this.mTransformationStartVisibleType == type;
        if (!visible) {
            view.setVisibility(4);
        } else {
            wrapper.setVisible(true);
        }
    }

    public void updateBackgroundColor(boolean animate) {
        int customBackgroundColor = getBackgroundColor(this.mVisibleType);
        this.mContainingNotification.resetBackgroundAlpha();
        this.mContainingNotification.setContentBackground(customBackgroundColor, animate, this);
    }

    public void setBackgroundTintColor(int color) {
        SmartReplyView smartReplyView = this.mExpandedSmartReplyView;
        if (smartReplyView != null) {
            smartReplyView.setBackgroundTintColor(color);
        }
        SmartReplyView smartReplyView2 = this.mHeadsUpSmartReplyView;
        if (smartReplyView2 != null) {
            smartReplyView2.setBackgroundTintColor(color);
        }
    }

    public int getVisibleType() {
        return this.mVisibleType;
    }

    public int getBackgroundColorForExpansionState() {
        int visibleType;
        if (this.mContainingNotification.isGroupExpanded() || this.mContainingNotification.isUserLocked()) {
            visibleType = calculateVisibleType();
        } else {
            visibleType = getVisibleType();
        }
        return getBackgroundColor(visibleType);
    }

    public int getBackgroundColor(int visibleType) {
        NotificationViewWrapper currentVisibleWrapper = getVisibleWrapper(visibleType);
        if (currentVisibleWrapper == null) {
            return 0;
        }
        int customBackgroundColor = currentVisibleWrapper.getCustomBackgroundColor();
        return customBackgroundColor;
    }

    private void updateViewVisibilities(int visibleType) {
        updateViewVisibility(visibleType, 0, this.mContractedChild, this.mContractedWrapper);
        updateViewVisibility(visibleType, 1, this.mExpandedChild, this.mExpandedWrapper);
        updateViewVisibility(visibleType, 2, this.mHeadsUpChild, this.mHeadsUpWrapper);
        HybridNotificationView hybridNotificationView = this.mSingleLineView;
        updateViewVisibility(visibleType, 3, hybridNotificationView, hybridNotificationView);
        fireExpandedVisibleListenerIfVisible();
        this.mAnimationStartVisibleType = -1;
    }

    private void updateViewVisibility(int visibleType, int type, View view, TransformableView wrapper) {
        if (view != null) {
            wrapper.setVisible(visibleType == type);
        }
    }

    private void animateToVisibleType(int visibleType) {
        TransformableView shownView = getTransformableViewForVisibleType(visibleType);
        final TransformableView hiddenView = getTransformableViewForVisibleType(this.mVisibleType);
        if (shownView == hiddenView || hiddenView == null) {
            shownView.setVisible(true);
            return;
        }
        this.mAnimationStartVisibleType = this.mVisibleType;
        shownView.transformFrom(hiddenView);
        getViewForVisibleType(visibleType).setVisibility(0);
        hiddenView.transformTo(shownView, new Runnable() { // from class: com.android.systemui.statusbar.notification.row.NotificationContentView.2
            @Override // java.lang.Runnable
            public void run() {
                TransformableView transformableView = hiddenView;
                NotificationContentView notificationContentView = NotificationContentView.this;
                if (transformableView != notificationContentView.getTransformableViewForVisibleType(notificationContentView.mVisibleType)) {
                    hiddenView.setVisible(false);
                }
                NotificationContentView.this.mAnimationStartVisibleType = -1;
            }
        });
        fireExpandedVisibleListenerIfVisible();
    }

    private void transferRemoteInputFocus(int visibleType) {
        RemoteInputView remoteInputView;
        RemoteInputView remoteInputView2;
        if (visibleType == 2 && this.mHeadsUpRemoteInput != null && (remoteInputView2 = this.mExpandedRemoteInput) != null && remoteInputView2.isActive()) {
            this.mHeadsUpRemoteInput.stealFocusFrom(this.mExpandedRemoteInput);
        }
        if (visibleType == 1 && this.mExpandedRemoteInput != null && (remoteInputView = this.mHeadsUpRemoteInput) != null && remoteInputView.isActive()) {
            this.mExpandedRemoteInput.stealFocusFrom(this.mHeadsUpRemoteInput);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public TransformableView getTransformableViewForVisibleType(int visibleType) {
        if (visibleType != 1) {
            if (visibleType != 2) {
                if (visibleType == 3) {
                    return this.mSingleLineView;
                }
                return this.mContractedWrapper;
            }
            return this.mHeadsUpWrapper;
        }
        return this.mExpandedWrapper;
    }

    private View getViewForVisibleType(int visibleType) {
        if (visibleType != 1) {
            if (visibleType != 2) {
                if (visibleType == 3) {
                    return this.mSingleLineView;
                }
                return this.mContractedChild;
            }
            return this.mHeadsUpChild;
        }
        return this.mExpandedChild;
    }

    public NotificationViewWrapper getVisibleWrapper(int visibleType) {
        if (visibleType != 0) {
            if (visibleType != 1) {
                if (visibleType == 2) {
                    return this.mHeadsUpWrapper;
                }
                return null;
            }
            return this.mExpandedWrapper;
        }
        return this.mContractedWrapper;
    }

    public int calculateVisibleType() {
        int height;
        int collapsedVisualType;
        if (this.mUserExpanding) {
            if (!this.mIsChildInGroup || isGroupExpanded() || this.mContainingNotification.isExpanded(true)) {
                height = this.mContainingNotification.getMaxContentHeight();
            } else {
                height = this.mContainingNotification.getShowingLayout().getMinHeight();
            }
            if (height == 0) {
                height = this.mContentHeight;
            }
            int expandedVisualType = getVisualTypeForHeight(height);
            if (this.mIsChildInGroup && !isGroupExpanded()) {
                collapsedVisualType = 3;
            } else {
                collapsedVisualType = getVisualTypeForHeight(this.mContainingNotification.getCollapsedHeight());
            }
            if (this.mTransformationStartVisibleType == collapsedVisualType) {
                return expandedVisualType;
            }
            return collapsedVisualType;
        }
        int intrinsicHeight = this.mContainingNotification.getIntrinsicHeight();
        int viewHeight = this.mContentHeight;
        if (intrinsicHeight != 0) {
            viewHeight = Math.min(this.mContentHeight, intrinsicHeight);
        }
        return getVisualTypeForHeight(viewHeight);
    }

    private int getVisualTypeForHeight(float viewHeight) {
        boolean noExpandedChild = this.mExpandedChild == null;
        if (noExpandedChild || viewHeight != getViewHeight(1)) {
            if (this.mUserExpanding || !this.mIsChildInGroup || isGroupExpanded()) {
                return ((this.mIsHeadsUp || this.mHeadsUpAnimatingAway) && this.mHeadsUpChild != null && this.mContainingNotification.canShowHeadsUp()) ? (viewHeight <= ((float) getViewHeight(2)) || noExpandedChild) ? 2 : 1 : (noExpandedChild || !(this.mContractedChild == null || viewHeight > ((float) getViewHeight(0)) || (this.mIsChildInGroup && !isGroupExpanded() && this.mContainingNotification.isExpanded(true)))) ? 0 : 1;
            }
            return 3;
        }
        return 1;
    }

    public boolean isContentExpandable() {
        return this.mIsContentExpandable;
    }

    public void setHeadsUp(boolean headsUp) {
        this.mIsHeadsUp = headsUp;
        selectLayout(false, true);
        updateExpandButtons(this.mExpandable);
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    public void setLegacy(boolean legacy) {
        this.mLegacy = legacy;
        updateLegacy();
    }

    private void updateLegacy() {
        if (this.mContractedChild != null) {
            this.mContractedWrapper.setLegacy(this.mLegacy);
        }
        if (this.mExpandedChild != null) {
            this.mExpandedWrapper.setLegacy(this.mLegacy);
        }
        if (this.mHeadsUpChild != null) {
            this.mHeadsUpWrapper.setLegacy(this.mLegacy);
        }
    }

    public void setIsChildInGroup(boolean isChildInGroup) {
        this.mIsChildInGroup = isChildInGroup;
        if (this.mContractedChild != null) {
            this.mContractedWrapper.setIsChildInGroup(this.mIsChildInGroup);
        }
        if (this.mExpandedChild != null) {
            this.mExpandedWrapper.setIsChildInGroup(this.mIsChildInGroup);
        }
        if (this.mHeadsUpChild != null) {
            this.mHeadsUpWrapper.setIsChildInGroup(this.mIsChildInGroup);
        }
        updateAllSingleLineViews();
    }

    public void onNotificationUpdated(NotificationEntry entry) {
        this.mStatusBarNotification = entry.notification;
        this.mOnContentViewInactiveListeners.clear();
        this.mBeforeN = entry.targetSdk < 24;
        updateAllSingleLineViews();
        ExpandableNotificationRow row = entry.getRow();
        if (this.mContractedChild != null) {
            this.mContractedWrapper.onContentUpdated(row);
        }
        if (this.mExpandedChild != null) {
            this.mExpandedWrapper.onContentUpdated(row);
        }
        if (this.mHeadsUpChild != null) {
            this.mHeadsUpWrapper.onContentUpdated(row);
        }
        applyRemoteInputAndSmartReply(entry);
        applyMediaTransfer(entry);
        updateLegacy();
        this.mForceSelectNextLayout = true;
        this.mPreviousExpandedRemoteInputIntent = null;
        this.mPreviousHeadsUpRemoteInputIntent = null;
    }

    private void updateAllSingleLineViews() {
        updateSingleLineView();
    }

    private void updateSingleLineView() {
        if (this.mIsChildInGroup) {
            boolean isNewView = this.mSingleLineView == null;
            this.mSingleLineView = this.mHybridGroupManager.bindFromNotification(this.mSingleLineView, this.mStatusBarNotification.getNotification());
            if (isNewView) {
                int i = this.mVisibleType;
                HybridNotificationView hybridNotificationView = this.mSingleLineView;
                updateViewVisibility(i, 3, hybridNotificationView, hybridNotificationView);
                return;
            }
            return;
        }
        View view = this.mSingleLineView;
        if (view != null) {
            removeView(view);
            this.mSingleLineView = null;
        }
    }

    private void applyMediaTransfer(NotificationEntry entry) {
        View bigContentView = this.mExpandedChild;
        if (bigContentView == null || !entry.isMediaNotification()) {
            return;
        }
        View mediaActionContainer = bigContentView.findViewById(16909197);
        if (!(mediaActionContainer instanceof LinearLayout)) {
            return;
        }
        this.mMediaTransferManager.applyMediaTransferView((ViewGroup) mediaActionContainer, entry);
    }

    private void applyRemoteInputAndSmartReply(NotificationEntry entry) {
        InflatedSmartReplies.SmartRepliesAndActions smartRepliesAndActions;
        if (this.mRemoteInputController == null) {
            return;
        }
        applyRemoteInput(entry, InflatedSmartReplies.hasFreeformRemoteInput(entry));
        if (this.mExpandedInflatedSmartReplies == null && this.mHeadsUpInflatedSmartReplies == null) {
            if (DEBUG) {
                Log.d(TAG, "Both expanded, and heads-up InflatedSmartReplies are null, don't add smart replies.");
                return;
            }
            return;
        }
        InflatedSmartReplies inflatedSmartReplies = this.mExpandedInflatedSmartReplies;
        if (inflatedSmartReplies != null) {
            smartRepliesAndActions = inflatedSmartReplies.getSmartRepliesAndActions();
        } else {
            smartRepliesAndActions = this.mHeadsUpInflatedSmartReplies.getSmartRepliesAndActions();
        }
        this.mCurrentSmartRepliesAndActions = smartRepliesAndActions;
        if (DEBUG) {
            Object[] objArr = new Object[3];
            objArr[0] = entry.notification.getKey();
            objArr[1] = Integer.valueOf(this.mCurrentSmartRepliesAndActions.smartActions == null ? 0 : this.mCurrentSmartRepliesAndActions.smartActions.actions.size());
            objArr[2] = Integer.valueOf(this.mCurrentSmartRepliesAndActions.smartReplies != null ? this.mCurrentSmartRepliesAndActions.smartReplies.choices.length : 0);
            Log.d(TAG, String.format("Adding suggestions for %s, %d actions, and %d replies.", objArr));
        }
        applySmartReplyView(this.mCurrentSmartRepliesAndActions, entry);
    }

    private void applyRemoteInput(NotificationEntry entry, boolean hasFreeformRemoteInput) {
        View bigContentView = this.mExpandedChild;
        if (bigContentView != null) {
            this.mExpandedRemoteInput = applyRemoteInput(bigContentView, entry, hasFreeformRemoteInput, this.mPreviousExpandedRemoteInputIntent, this.mCachedExpandedRemoteInput, this.mExpandedWrapper);
        } else {
            this.mExpandedRemoteInput = null;
        }
        RemoteInputView remoteInputView = this.mCachedExpandedRemoteInput;
        if (remoteInputView != null && remoteInputView != this.mExpandedRemoteInput) {
            remoteInputView.dispatchFinishTemporaryDetach();
        }
        this.mCachedExpandedRemoteInput = null;
        View headsUpContentView = this.mHeadsUpChild;
        if (headsUpContentView != null) {
            this.mHeadsUpRemoteInput = applyRemoteInput(headsUpContentView, entry, hasFreeformRemoteInput, this.mPreviousHeadsUpRemoteInputIntent, this.mCachedHeadsUpRemoteInput, this.mHeadsUpWrapper);
        } else {
            this.mHeadsUpRemoteInput = null;
        }
        RemoteInputView remoteInputView2 = this.mCachedHeadsUpRemoteInput;
        if (remoteInputView2 != null && remoteInputView2 != this.mHeadsUpRemoteInput) {
            remoteInputView2.dispatchFinishTemporaryDetach();
        }
        this.mCachedHeadsUpRemoteInput = null;
    }

    private RemoteInputView applyRemoteInput(View view, NotificationEntry entry, boolean hasRemoteInput, PendingIntent existingPendingIntent, RemoteInputView cachedView, NotificationViewWrapper wrapper) {
        View actionContainerCandidate = view.findViewById(16908794);
        if (actionContainerCandidate instanceof FrameLayout) {
            RemoteInputView existing = (RemoteInputView) view.findViewWithTag(RemoteInputView.VIEW_TAG);
            if (existing != null) {
                existing.onNotificationUpdateOrReset();
            }
            if (existing == null && hasRemoteInput) {
                ViewGroup actionContainer = (FrameLayout) actionContainerCandidate;
                if (cachedView == null) {
                    RemoteInputView riv = RemoteInputView.inflate(this.mContext, actionContainer, entry, this.mRemoteInputController);
                    riv.setVisibility(4);
                    actionContainer.addView(riv, new FrameLayout.LayoutParams(-1, -1));
                    existing = riv;
                } else {
                    actionContainer.addView(cachedView);
                    cachedView.dispatchFinishTemporaryDetach();
                    cachedView.requestFocus();
                    existing = cachedView;
                }
            }
            if (hasRemoteInput) {
                int color = entry.notification.getNotification().color;
                if (color == 0) {
                    color = this.mContext.getColor(R.color.default_remote_input_background);
                }
                existing.setBackgroundColor(ContrastColorUtil.ensureTextBackgroundColor(color, this.mContext.getColor(R.color.remote_input_text_enabled), this.mContext.getColor(R.color.remote_input_hint)));
                existing.setWrapper(wrapper);
                existing.setOnVisibilityChangedListener(new Consumer() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$GC_EXjlJWjwU2u0y95DlTq2QVf0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        NotificationContentView.this.setRemoteInputVisible(((Boolean) obj).booleanValue());
                    }
                });
                if (existingPendingIntent != null || existing.isActive()) {
                    Notification.Action[] actions = entry.notification.getNotification().actions;
                    if (existingPendingIntent != null) {
                        existing.setPendingIntent(existingPendingIntent);
                    }
                    if (existing.updatePendingIntentFromActions(actions)) {
                        if (!existing.isActive()) {
                            existing.focus();
                        }
                    } else if (existing.isActive()) {
                        existing.close();
                    }
                }
            }
            return existing;
        }
        return null;
    }

    private void applySmartReplyView(InflatedSmartReplies.SmartRepliesAndActions smartRepliesAndActions, NotificationEntry entry) {
        boolean fromAssistant;
        View view = this.mExpandedChild;
        if (view != null) {
            this.mExpandedSmartReplyView = applySmartReplyView(view, smartRepliesAndActions, entry, this.mExpandedInflatedSmartReplies);
            if (this.mExpandedSmartReplyView != null && (smartRepliesAndActions.smartReplies != null || smartRepliesAndActions.smartActions != null)) {
                boolean z = false;
                int numSmartReplies = smartRepliesAndActions.smartReplies == null ? 0 : smartRepliesAndActions.smartReplies.choices.length;
                int numSmartActions = smartRepliesAndActions.smartActions == null ? 0 : smartRepliesAndActions.smartActions.actions.size();
                if (smartRepliesAndActions.smartReplies == null) {
                    fromAssistant = smartRepliesAndActions.smartActions.fromAssistant;
                } else {
                    fromAssistant = smartRepliesAndActions.smartReplies.fromAssistant;
                }
                if (smartRepliesAndActions.smartReplies != null && this.mSmartReplyConstants.getEffectiveEditChoicesBeforeSending(smartRepliesAndActions.smartReplies.remoteInput.getEditChoicesBeforeSending())) {
                    z = true;
                }
                boolean editBeforeSending = z;
                this.mSmartReplyController.smartSuggestionsAdded(entry, numSmartReplies, numSmartActions, fromAssistant, editBeforeSending);
            }
        }
        if (this.mHeadsUpChild != null && this.mSmartReplyConstants.getShowInHeadsUp()) {
            this.mHeadsUpSmartReplyView = applySmartReplyView(this.mHeadsUpChild, smartRepliesAndActions, entry, this.mHeadsUpInflatedSmartReplies);
        }
    }

    private SmartReplyView applySmartReplyView(View view, InflatedSmartReplies.SmartRepliesAndActions smartRepliesAndActions, NotificationEntry entry, InflatedSmartReplies inflatedSmartReplyView) {
        View smartReplyContainerCandidate = view.findViewById(16909474);
        if (smartReplyContainerCandidate instanceof LinearLayout) {
            LinearLayout smartReplyContainer = (LinearLayout) smartReplyContainerCandidate;
            if (!InflatedSmartReplies.shouldShowSmartReplyView(entry, smartRepliesAndActions)) {
                smartReplyContainer.setVisibility(8);
                return null;
            }
            SmartReplyView smartReplyView = null;
            if (smartReplyContainer.getChildCount() == 1 && (smartReplyContainer.getChildAt(0) instanceof SmartReplyView)) {
                smartReplyContainer.removeAllViews();
            }
            if (smartReplyContainer.getChildCount() == 0 && inflatedSmartReplyView != null && inflatedSmartReplyView.getSmartReplyView() != null) {
                smartReplyView = inflatedSmartReplyView.getSmartReplyView();
                smartReplyContainer.addView(smartReplyView);
            }
            if (smartReplyView != null) {
                smartReplyView.resetSmartSuggestions(smartReplyContainer);
                smartReplyView.addPreInflatedButtons(inflatedSmartReplyView.getSmartSuggestionButtons());
                smartReplyView.setBackgroundTintColor(entry.getRow().getCurrentBackgroundTint());
                smartReplyContainer.setVisibility(0);
            }
            return smartReplyView;
        }
        return null;
    }

    public void setExpandedInflatedSmartReplies(InflatedSmartReplies inflatedSmartReplies) {
        this.mExpandedInflatedSmartReplies = inflatedSmartReplies;
        if (inflatedSmartReplies == null) {
            this.mExpandedSmartReplyView = null;
        }
    }

    public void setHeadsUpInflatedSmartReplies(InflatedSmartReplies inflatedSmartReplies) {
        this.mHeadsUpInflatedSmartReplies = inflatedSmartReplies;
        if (inflatedSmartReplies == null) {
            this.mHeadsUpSmartReplyView = null;
        }
    }

    public InflatedSmartReplies.SmartRepliesAndActions getCurrentSmartRepliesAndActions() {
        return this.mCurrentSmartRepliesAndActions;
    }

    public void closeRemoteInput() {
        RemoteInputView remoteInputView = this.mHeadsUpRemoteInput;
        if (remoteInputView != null) {
            remoteInputView.close();
        }
        RemoteInputView remoteInputView2 = this.mExpandedRemoteInput;
        if (remoteInputView2 != null) {
            remoteInputView2.close();
        }
    }

    public void setGroupManager(NotificationGroupManager groupManager) {
        this.mGroupManager = groupManager;
    }

    public void setRemoteInputController(RemoteInputController r) {
        this.mRemoteInputController = r;
    }

    public void setExpandClickListener(View.OnClickListener expandClickListener) {
        this.mExpandClickListener = expandClickListener;
    }

    public void updateExpandButtons(boolean expandable) {
        this.mExpandable = expandable;
        View view = this.mExpandedChild;
        if (view != null && view.getHeight() != 0) {
            if ((!this.mIsHeadsUp && !this.mHeadsUpAnimatingAway) || this.mHeadsUpChild == null || !this.mContainingNotification.canShowHeadsUp()) {
                if (this.mExpandedChild.getHeight() <= this.mContractedChild.getHeight()) {
                    expandable = false;
                }
            } else if (this.mExpandedChild.getHeight() <= this.mHeadsUpChild.getHeight()) {
                expandable = false;
            }
        }
        if (this.mExpandedChild != null) {
            this.mExpandedWrapper.updateExpandability(expandable, this.mExpandClickListener);
        }
        if (this.mContractedChild != null) {
            this.mContractedWrapper.updateExpandability(expandable, this.mExpandClickListener);
        }
        if (this.mHeadsUpChild != null) {
            this.mHeadsUpWrapper.updateExpandability(expandable, this.mExpandClickListener);
        }
        this.mIsContentExpandable = expandable;
    }

    public NotificationHeaderView getNotificationHeader() {
        NotificationHeaderView header = null;
        if (this.mContractedChild != null) {
            header = this.mContractedWrapper.getNotificationHeader();
        }
        if (header == null && this.mExpandedChild != null) {
            header = this.mExpandedWrapper.getNotificationHeader();
        }
        if (header == null && this.mHeadsUpChild != null) {
            NotificationHeaderView header2 = this.mHeadsUpWrapper.getNotificationHeader();
            return header2;
        }
        return header;
    }

    public void showAppOpsIcons(ArraySet<Integer> activeOps) {
        if (this.mContractedChild != null && this.mContractedWrapper.getNotificationHeader() != null) {
            this.mContractedWrapper.getNotificationHeader().showAppOpsIcons(activeOps);
        }
        if (this.mExpandedChild != null && this.mExpandedWrapper.getNotificationHeader() != null) {
            this.mExpandedWrapper.getNotificationHeader().showAppOpsIcons(activeOps);
        }
        if (this.mHeadsUpChild != null && this.mHeadsUpWrapper.getNotificationHeader() != null) {
            this.mHeadsUpWrapper.getNotificationHeader().showAppOpsIcons(activeOps);
        }
    }

    public void setRecentlyAudiblyAlerted(boolean audiblyAlerted) {
        if (this.mContractedChild != null && this.mContractedWrapper.getNotificationHeader() != null) {
            this.mContractedWrapper.getNotificationHeader().setRecentlyAudiblyAlerted(audiblyAlerted);
        }
        if (this.mExpandedChild != null && this.mExpandedWrapper.getNotificationHeader() != null) {
            this.mExpandedWrapper.getNotificationHeader().setRecentlyAudiblyAlerted(audiblyAlerted);
        }
        if (this.mHeadsUpChild != null && this.mHeadsUpWrapper.getNotificationHeader() != null) {
            this.mHeadsUpWrapper.getNotificationHeader().setRecentlyAudiblyAlerted(audiblyAlerted);
        }
    }

    public NotificationHeaderView getContractedNotificationHeader() {
        if (this.mContractedChild != null) {
            return this.mContractedWrapper.getNotificationHeader();
        }
        return null;
    }

    public NotificationHeaderView getVisibleNotificationHeader() {
        NotificationViewWrapper wrapper = getVisibleWrapper(this.mVisibleType);
        if (wrapper == null) {
            return null;
        }
        return wrapper.getNotificationHeader();
    }

    public void setContainingNotification(ExpandableNotificationRow containingNotification) {
        this.mContainingNotification = containingNotification;
    }

    public void requestSelectLayout(boolean needsAnimation) {
        selectLayout(needsAnimation, false);
    }

    public void reInflateViews() {
        HybridNotificationView hybridNotificationView;
        if (this.mIsChildInGroup && (hybridNotificationView = this.mSingleLineView) != null) {
            removeView(hybridNotificationView);
            this.mSingleLineView = null;
            updateAllSingleLineViews();
        }
    }

    public void setUserExpanding(boolean userExpanding) {
        this.mUserExpanding = userExpanding;
        if (userExpanding) {
            this.mTransformationStartVisibleType = this.mVisibleType;
            return;
        }
        this.mTransformationStartVisibleType = -1;
        this.mVisibleType = calculateVisibleType();
        updateViewVisibilities(this.mVisibleType);
        updateBackgroundColor(false);
    }

    public void setSingleLineWidthIndention(int singleLineWidthIndention) {
        if (singleLineWidthIndention != this.mSingleLineWidthIndention) {
            this.mSingleLineWidthIndention = singleLineWidthIndention;
            this.mContainingNotification.forceLayout();
            forceLayout();
        }
    }

    public HybridNotificationView getSingleLineView() {
        return this.mSingleLineView;
    }

    public void setRemoved() {
        RemoteInputView remoteInputView = this.mExpandedRemoteInput;
        if (remoteInputView != null) {
            remoteInputView.setRemoved();
        }
        RemoteInputView remoteInputView2 = this.mHeadsUpRemoteInput;
        if (remoteInputView2 != null) {
            remoteInputView2.setRemoved();
        }
        NotificationViewWrapper notificationViewWrapper = this.mExpandedWrapper;
        if (notificationViewWrapper != null) {
            notificationViewWrapper.setRemoved();
        }
        NotificationViewWrapper notificationViewWrapper2 = this.mContractedWrapper;
        if (notificationViewWrapper2 != null) {
            notificationViewWrapper2.setRemoved();
        }
        NotificationViewWrapper notificationViewWrapper3 = this.mHeadsUpWrapper;
        if (notificationViewWrapper3 != null) {
            notificationViewWrapper3.setRemoved();
        }
    }

    public void setContentHeightAnimating(boolean animating) {
        if (!animating) {
            this.mContentHeightAtAnimationStart = -1;
        }
    }

    @VisibleForTesting
    boolean isAnimatingVisibleType() {
        return this.mAnimationStartVisibleType != -1;
    }

    public void setHeadsUpAnimatingAway(boolean headsUpAnimatingAway) {
        this.mHeadsUpAnimatingAway = headsUpAnimatingAway;
        selectLayout(false, true);
    }

    public void setFocusOnVisibilityChange() {
        this.mFocusOnVisibilityChange = true;
    }

    public void setIconsVisible(boolean iconsVisible) {
        this.mIconsVisible = iconsVisible;
        updateIconVisibilities();
    }

    private void updateIconVisibilities() {
        NotificationHeaderView header;
        NotificationHeaderView header2;
        NotificationHeaderView header3;
        NotificationViewWrapper notificationViewWrapper = this.mContractedWrapper;
        if (notificationViewWrapper != null && (header3 = notificationViewWrapper.getNotificationHeader()) != null) {
            header3.getIcon().setForceHidden(!this.mIconsVisible);
        }
        NotificationViewWrapper notificationViewWrapper2 = this.mHeadsUpWrapper;
        if (notificationViewWrapper2 != null && (header2 = notificationViewWrapper2.getNotificationHeader()) != null) {
            header2.getIcon().setForceHidden(!this.mIconsVisible);
        }
        NotificationViewWrapper notificationViewWrapper3 = this.mExpandedWrapper;
        if (notificationViewWrapper3 != null && (header = notificationViewWrapper3.getNotificationHeader()) != null) {
            header.getIcon().setForceHidden(!this.mIconsVisible);
        }
    }

    @Override // android.view.View
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);
        if (isVisible) {
            fireExpandedVisibleListenerIfVisible();
        }
    }

    public void setOnExpandedVisibleListener(Runnable r) {
        this.mExpandedVisibleListener = r;
        fireExpandedVisibleListenerIfVisible();
    }

    public void performWhenContentInactive(int visibleType, Runnable listener) {
        View view = getViewForVisibleType(visibleType);
        if (view == null || isContentViewInactive(visibleType)) {
            listener.run();
        } else {
            this.mOnContentViewInactiveListeners.put(view, listener);
        }
    }

    public boolean isContentViewInactive(int visibleType) {
        View view = getViewForVisibleType(visibleType);
        return isContentViewInactive(view);
    }

    private boolean isContentViewInactive(View view) {
        if (view != null && isShown()) {
            return (view.getVisibility() == 0 || getViewForVisibleType(this.mVisibleType) == view) ? false : true;
        }
        return true;
    }

    protected void onChildVisibilityChanged(View child, int oldVisibility, int newVisibility) {
        Runnable listener;
        super.onChildVisibilityChanged(child, oldVisibility, newVisibility);
        if (isContentViewInactive(child) && (listener = this.mOnContentViewInactiveListeners.remove(child)) != null) {
            listener.run();
        }
    }

    public void setIsLowPriority(boolean isLowPriority) {
        this.mIsLowPriority = isLowPriority;
    }

    public boolean isDimmable() {
        NotificationViewWrapper notificationViewWrapper = this.mContractedWrapper;
        return notificationViewWrapper != null && notificationViewWrapper.isDimmable();
    }

    public boolean disallowSingleClick(float x, float y) {
        NotificationViewWrapper visibleWrapper = getVisibleWrapper(getVisibleType());
        if (visibleWrapper != null) {
            return visibleWrapper.disallowSingleClick(x, y);
        }
        return false;
    }

    public boolean shouldClipToRounding(boolean topRounded, boolean bottomRounded) {
        boolean needsPaddings = shouldClipToRounding(getVisibleType(), topRounded, bottomRounded);
        if (this.mUserExpanding) {
            return needsPaddings | shouldClipToRounding(this.mTransformationStartVisibleType, topRounded, bottomRounded);
        }
        return needsPaddings;
    }

    private boolean shouldClipToRounding(int visibleType, boolean topRounded, boolean bottomRounded) {
        NotificationViewWrapper visibleWrapper = getVisibleWrapper(visibleType);
        if (visibleWrapper == null) {
            return false;
        }
        return visibleWrapper.shouldClipToRounding(topRounded, bottomRounded);
    }

    public CharSequence getActiveRemoteInputText() {
        RemoteInputView remoteInputView = this.mExpandedRemoteInput;
        if (remoteInputView != null && remoteInputView.isActive()) {
            return this.mExpandedRemoteInput.getText();
        }
        RemoteInputView remoteInputView2 = this.mHeadsUpRemoteInput;
        if (remoteInputView2 != null && remoteInputView2.isActive()) {
            return this.mHeadsUpRemoteInput.getText();
        }
        return null;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float y = ev.getY();
        RemoteInputView riv = getRemoteInputForView(getViewForVisibleType(this.mVisibleType));
        if (riv != null && riv.getVisibility() == 0) {
            int inputStart = this.mUnrestrictedContentHeight - riv.getHeight();
            if (y <= this.mUnrestrictedContentHeight && y >= inputStart) {
                ev.offsetLocation(0.0f, -inputStart);
                return riv.dispatchTouchEvent(ev);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public boolean pointInView(float localX, float localY, float slop) {
        float top = this.mClipTopAmount;
        float bottom = this.mUnrestrictedContentHeight;
        return localX >= (-slop) && localY >= top - slop && localX < ((float) (this.mRight - this.mLeft)) + slop && localY < bottom + slop;
    }

    private RemoteInputView getRemoteInputForView(View child) {
        if (child == this.mExpandedChild) {
            return this.mExpandedRemoteInput;
        }
        if (child == this.mHeadsUpChild) {
            return this.mHeadsUpRemoteInput;
        }
        return null;
    }

    public int getExpandHeight() {
        int viewType = 1;
        if (this.mExpandedChild == null) {
            viewType = 0;
        }
        return getViewHeight(viewType) + getExtraRemoteInputHeight(this.mExpandedRemoteInput);
    }

    public int getHeadsUpHeight(boolean forceNoHeader) {
        int viewType = 2;
        if (this.mHeadsUpChild == null) {
            viewType = 0;
        }
        return getViewHeight(viewType, forceNoHeader) + getExtraRemoteInputHeight(this.mHeadsUpRemoteInput) + getExtraRemoteInputHeight(this.mExpandedRemoteInput);
    }

    public void setRemoteInputVisible(boolean remoteInputVisible) {
        this.mRemoteInputVisible = remoteInputVisible;
        setClipChildren(!remoteInputVisible);
    }

    @Override // android.view.ViewGroup
    public void setClipChildren(boolean clipChildren) {
        boolean clipChildren2 = clipChildren && !this.mRemoteInputVisible;
        super.setClipChildren(clipChildren2);
    }

    public void setHeaderVisibleAmount(float headerVisibleAmount) {
        NotificationViewWrapper notificationViewWrapper = this.mContractedWrapper;
        if (notificationViewWrapper != null) {
            notificationViewWrapper.setHeaderVisibleAmount(headerVisibleAmount);
        }
        NotificationViewWrapper notificationViewWrapper2 = this.mHeadsUpWrapper;
        if (notificationViewWrapper2 != null) {
            notificationViewWrapper2.setHeaderVisibleAmount(headerVisibleAmount);
        }
        NotificationViewWrapper notificationViewWrapper3 = this.mExpandedWrapper;
        if (notificationViewWrapper3 != null) {
            notificationViewWrapper3.setHeaderVisibleAmount(headerVisibleAmount);
        }
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.print("    ");
        pw.print("contentView visibility: " + getVisibility());
        pw.print(", alpha: " + getAlpha());
        pw.print(", clipBounds: " + getClipBounds());
        pw.print(", contentHeight: " + this.mContentHeight);
        pw.print(", visibleType: " + this.mVisibleType);
        View view = getViewForVisibleType(this.mVisibleType);
        pw.print(", visibleView ");
        if (view != null) {
            pw.print(" visibility: " + view.getVisibility());
            pw.print(", alpha: " + view.getAlpha());
            pw.print(", clipBounds: " + view.getClipBounds());
        } else {
            pw.print("null");
        }
        pw.println();
    }

    public RemoteInputView getExpandedRemoteInput() {
        return this.mExpandedRemoteInput;
    }
}
