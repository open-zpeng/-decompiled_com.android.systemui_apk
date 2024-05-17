package com.android.systemui.statusbar;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.SystemProperties;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Dependency;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableView;
import com.android.systemui.statusbar.notification.stack.AmbientState;
import com.android.systemui.statusbar.notification.stack.AnimationProperties;
import com.android.systemui.statusbar.notification.stack.ExpandableViewState;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.notification.stack.ViewState;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.NotificationIconContainer;
import javax.inject.Inject;
import javax.inject.Named;
/* loaded from: classes21.dex */
public class NotificationShelf extends ActivatableNotificationView implements View.OnLayoutChangeListener, StatusBarStateController.StateListener {
    private static final String TAG = "NotificationShelf";
    private AmbientState mAmbientState;
    private boolean mAnimationsEnabled;
    private final KeyguardBypassController mBypassController;
    private Rect mClipRect;
    private NotificationIconContainer mCollapsedIcons;
    private int mCutoutHeight;
    private float mFirstElementRoundness;
    private int mGapHeight;
    private boolean mHasItemsInStableShelf;
    private float mHiddenShelfIconSize;
    private boolean mHideBackground;
    private NotificationStackScrollLayout mHostLayout;
    private int mIconAppearTopPadding;
    private int mIconSize;
    private boolean mInteractive;
    private int mMaxLayoutHeight;
    private float mMaxShelfEnd;
    private boolean mNoAnimationsInThisFrame;
    private int mNotGoneIndex;
    private float mOpenedAmount;
    private int mPaddingBetweenElements;
    private int mRelativeOffset;
    private int mScrollFastThreshold;
    private NotificationIconContainer mShelfIcons;
    private boolean mShowNotificationShelf;
    private int mStatusBarHeight;
    private int mStatusBarPaddingStart;
    private int mStatusBarState;
    private int[] mTmp;
    private static final boolean USE_ANIMATIONS_WHEN_OPENING = SystemProperties.getBoolean("debug.icon_opening_animations", true);
    private static final boolean ICON_ANMATIONS_WHILE_SCROLLING = SystemProperties.getBoolean("debug.icon_scroll_animations", true);
    private static final int TAG_CONTINUOUS_CLIPPING = R.id.continuous_clipping_tag;

    @Inject
    public NotificationShelf(@Named("view_context") Context context, AttributeSet attrs, KeyguardBypassController keyguardBypassController) {
        super(context, attrs);
        this.mTmp = new int[2];
        this.mAnimationsEnabled = true;
        this.mClipRect = new Rect();
        this.mBypassController = keyguardBypassController;
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView, android.view.View
    @VisibleForTesting
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mShelfIcons = (NotificationIconContainer) findViewById(R.id.content);
        this.mShelfIcons.setClipChildren(false);
        this.mShelfIcons.setClipToPadding(false);
        setClipToActualHeight(false);
        setClipChildren(false);
        setClipToPadding(false);
        this.mShelfIcons.setIsStaticLayout(false);
        setBottomRoundness(1.0f, false);
        initDimens();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ((SysuiStatusBarStateController) Dependency.get(StatusBarStateController.class)).addCallback(this, 3);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ((StatusBarStateController) Dependency.get(StatusBarStateController.class)).removeCallback(this);
    }

    public void bind(AmbientState ambientState, NotificationStackScrollLayout hostLayout) {
        this.mAmbientState = ambientState;
        this.mHostLayout = hostLayout;
    }

    private void initDimens() {
        Resources res = getResources();
        this.mIconAppearTopPadding = res.getDimensionPixelSize(R.dimen.notification_icon_appear_padding);
        this.mStatusBarHeight = res.getDimensionPixelOffset(R.dimen.status_bar_height);
        this.mStatusBarPaddingStart = res.getDimensionPixelOffset(R.dimen.status_bar_padding_start);
        this.mPaddingBetweenElements = res.getDimensionPixelSize(R.dimen.notification_divider_height);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height = res.getDimensionPixelOffset(R.dimen.notification_shelf_height);
        setLayoutParams(layoutParams);
        int padding = res.getDimensionPixelOffset(R.dimen.shelf_icon_container_padding);
        this.mShelfIcons.setPadding(padding, 0, padding, 0);
        this.mScrollFastThreshold = res.getDimensionPixelOffset(R.dimen.scroll_fast_threshold);
        this.mShowNotificationShelf = res.getBoolean(R.bool.config_showNotificationShelf);
        this.mIconSize = res.getDimensionPixelSize(17105441);
        this.mHiddenShelfIconSize = res.getDimensionPixelOffset(R.dimen.hidden_shelf_icon_size);
        this.mGapHeight = res.getDimensionPixelSize(R.dimen.qs_notification_padding);
        if (!this.mShowNotificationShelf) {
            setVisibility(8);
        }
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initDimens();
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView
    protected View getContentView() {
        return this.mShelfIcons;
    }

    public NotificationIconContainer getShelfIcons() {
        return this.mShelfIcons;
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public ExpandableViewState createExpandableViewState() {
        return new ShelfState();
    }

    public void updateState(AmbientState ambientState) {
        ExpandableView lastView = ambientState.getLastVisibleBackgroundChild();
        ShelfState viewState = (ShelfState) getViewState();
        boolean z = true;
        if (this.mShowNotificationShelf && lastView != null) {
            float maxShelfEnd = ambientState.getInnerHeight() + ambientState.getTopPadding() + ambientState.getStackTranslation();
            ExpandableViewState lastViewState = lastView.getViewState();
            float viewEnd = lastViewState.yTranslation + lastViewState.height;
            viewState.copyFrom(lastViewState);
            viewState.height = getIntrinsicHeight();
            viewState.yTranslation = Math.max(Math.min(viewEnd, maxShelfEnd) - viewState.height, getFullyClosedTranslation());
            viewState.zTranslation = ambientState.getBaseZHeight();
            float openedAmount = (viewState.yTranslation - getFullyClosedTranslation()) / ((getIntrinsicHeight() * 2) + this.mCutoutHeight);
            viewState.openedAmount = Math.min(1.0f, openedAmount);
            viewState.clipTopAmount = 0;
            viewState.alpha = 1.0f;
            viewState.belowSpeedBump = this.mAmbientState.getSpeedBumpIndex() == 0;
            viewState.hideSensitive = false;
            viewState.xTranslation = getTranslationX();
            if (this.mNotGoneIndex != -1) {
                viewState.notGoneIndex = Math.min(viewState.notGoneIndex, this.mNotGoneIndex);
            }
            viewState.hasItemsInStableShelf = lastViewState.inShelf;
            if (this.mAmbientState.isShadeExpanded() && !this.mAmbientState.isQsCustomizerShowing()) {
                z = false;
            }
            viewState.hidden = z;
            viewState.maxShelfEnd = maxShelfEnd;
            return;
        }
        viewState.hidden = true;
        viewState.location = 64;
        viewState.hasItemsInStableShelf = false;
    }

    /* JADX WARN: Removed duplicated region for block: B:101:0x0232  */
    /* JADX WARN: Removed duplicated region for block: B:107:0x025d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateAppearance() {
        /*
            Method dump skipped, instructions count: 1036
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.NotificationShelf.updateAppearance():void");
    }

    private void clipTransientViews() {
        for (int i = 0; i < this.mHostLayout.getTransientViewCount(); i++) {
            View transientView = this.mHostLayout.getTransientView(i);
            if (transientView instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow transientRow = (ExpandableNotificationRow) transientView;
                updateNotificationClipHeight(transientRow, getTranslationY(), -1);
            } else {
                Log.e(TAG, "NotificationShelf.clipTransientViews(): Trying to clip non-row transient view");
            }
        }
    }

    private void setFirstElementRoundness(float firstElementRoundness) {
        if (this.mFirstElementRoundness != firstElementRoundness) {
            this.mFirstElementRoundness = firstElementRoundness;
            setTopRoundness(firstElementRoundness, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateIconClipAmount(ExpandableNotificationRow row) {
        float maxTop = row.getTranslationY();
        if (getClipTopAmount() != 0) {
            maxTop = Math.max(maxTop, getTranslationY() + getClipTopAmount());
        }
        StatusBarIconView icon = row.getEntry().expandedIcon;
        float shelfIconPosition = getTranslationY() + icon.getTop() + icon.getTranslationY();
        if (shelfIconPosition < maxTop && !this.mAmbientState.isFullyHidden()) {
            int top = (int) (maxTop - shelfIconPosition);
            Rect clipRect = new Rect(0, top, icon.getWidth(), Math.max(top, icon.getHeight()));
            icon.setClipBounds(clipRect);
            return;
        }
        icon.setClipBounds(null);
    }

    private void updateContinuousClipping(final ExpandableNotificationRow row) {
        final StatusBarIconView icon = row.getEntry().expandedIcon;
        boolean needsContinuousClipping = ViewState.isAnimatingY(icon) && !this.mAmbientState.isDozing();
        boolean isContinuousClipping = icon.getTag(TAG_CONTINUOUS_CLIPPING) != null;
        if (needsContinuousClipping && !isContinuousClipping) {
            final ViewTreeObserver observer = icon.getViewTreeObserver();
            final ViewTreeObserver.OnPreDrawListener predrawListener = new ViewTreeObserver.OnPreDrawListener() { // from class: com.android.systemui.statusbar.NotificationShelf.1
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    boolean animatingY = ViewState.isAnimatingY(icon);
                    if (animatingY) {
                        NotificationShelf.this.updateIconClipAmount(row);
                        return true;
                    }
                    if (observer.isAlive()) {
                        observer.removeOnPreDrawListener(this);
                    }
                    icon.setTag(NotificationShelf.TAG_CONTINUOUS_CLIPPING, null);
                    return true;
                }
            };
            observer.addOnPreDrawListener(predrawListener);
            icon.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() { // from class: com.android.systemui.statusbar.NotificationShelf.2
                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewAttachedToWindow(View v) {
                }

                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewDetachedFromWindow(View v) {
                    if (v == icon) {
                        if (observer.isAlive()) {
                            observer.removeOnPreDrawListener(predrawListener);
                        }
                        icon.setTag(NotificationShelf.TAG_CONTINUOUS_CLIPPING, null);
                    }
                }
            });
            icon.setTag(TAG_CONTINUOUS_CLIPPING, predrawListener);
        }
    }

    private int updateNotificationClipHeight(ActivatableNotificationView row, float notificationClipEnd, int childIndex) {
        float viewEnd = row.getTranslationY() + row.getActualHeight();
        boolean shouldClipOwnTop = true;
        boolean isPinned = (row.isPinned() || row.isHeadsUpAnimatingAway()) && !this.mAmbientState.isDozingAndNotPulsing(row);
        if (this.mAmbientState.isPulseExpanding()) {
            if (childIndex != 0) {
                shouldClipOwnTop = false;
            }
        } else {
            shouldClipOwnTop = row.showingPulsing();
        }
        if (viewEnd > notificationClipEnd && !shouldClipOwnTop && (this.mAmbientState.isShadeExpanded() || !isPinned)) {
            int clipBottomAmount = (int) (viewEnd - notificationClipEnd);
            if (isPinned) {
                clipBottomAmount = Math.min(row.getIntrinsicHeight() - row.getCollapsedHeight(), clipBottomAmount);
            }
            row.setClipBottomAmount(clipBottomAmount);
        } else {
            row.setClipBottomAmount(0);
        }
        if (shouldClipOwnTop) {
            return (int) (viewEnd - getTranslationY());
        }
        return 0;
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView, com.android.systemui.statusbar.notification.row.ExpandableView
    public void setFakeShadowIntensity(float shadowIntensity, float outlineAlpha, int shadowYEnd, int outlineTranslation) {
        if (!this.mHasItemsInStableShelf) {
            shadowIntensity = 0.0f;
        }
        super.setFakeShadowIntensity(shadowIntensity, outlineAlpha, shadowYEnd, outlineTranslation);
    }

    private float updateIconAppearance(ExpandableNotificationRow row, float expandAmount, boolean scrolling, boolean scrollingFast, boolean expandingAnimated, boolean isLastChild) {
        int fullHeight;
        float iconTransformDistance;
        boolean fullyInOrOut;
        float fullTransitionAmount;
        float iconTransitionAmount;
        StatusBarIconView icon = row.getEntry().expandedIcon;
        NotificationIconContainer.IconState iconState = getIconState(icon);
        if (iconState == null) {
            return 0.0f;
        }
        float viewStart = row.getTranslationY();
        int fullHeight2 = row.getActualHeight() + this.mPaddingBetweenElements;
        float iconTransformDistance2 = Math.min(getIntrinsicHeight() * 1.5f * NotificationUtils.interpolate(1.0f, 1.5f, expandAmount), fullHeight2);
        if (isLastChild) {
            fullHeight2 = Math.min(fullHeight2, row.getMinHeight() - getIntrinsicHeight());
            iconTransformDistance2 = Math.min(iconTransformDistance2, row.getMinHeight() - getIntrinsicHeight());
        }
        float viewEnd = viewStart + fullHeight2;
        if (expandingAnimated && this.mAmbientState.getScrollY() == 0) {
            if (!this.mAmbientState.isOnKeyguard() && !iconState.isLastExpandIcon) {
                float position = this.mAmbientState.getIntrinsicPadding() + this.mHostLayout.getPositionInLinearLayout(row);
                int maxShelfStart = this.mMaxLayoutHeight - getIntrinsicHeight();
                if (position < maxShelfStart && row.getIntrinsicHeight() + position >= maxShelfStart && row.getTranslationY() < position) {
                    iconState.isLastExpandIcon = true;
                    iconState.customTransformHeight = Integer.MIN_VALUE;
                    boolean forceInShelf = ((float) (this.mMaxLayoutHeight - getIntrinsicHeight())) - position < ((float) getIntrinsicHeight());
                    if (!forceInShelf) {
                        iconState.customTransformHeight = (int) ((this.mMaxLayoutHeight - getIntrinsicHeight()) - position);
                    }
                }
            }
        }
        float shelfStart = getTranslationY();
        if (!iconState.hasCustomTransformHeight()) {
            fullHeight = fullHeight2;
            iconTransformDistance = iconTransformDistance2;
        } else {
            fullHeight = iconState.customTransformHeight;
            iconTransformDistance = iconState.customTransformHeight;
        }
        if (viewEnd < shelfStart || ((this.mAmbientState.isUnlockHintRunning() && !row.isInShelf()) || (!this.mAmbientState.isShadeExpanded() && (row.isPinned() || row.isHeadsUpAnimatingAway())))) {
            fullyInOrOut = true;
            fullTransitionAmount = 0.0f;
            iconTransitionAmount = 0.0f;
        } else if (viewStart >= shelfStart) {
            fullyInOrOut = true;
            fullTransitionAmount = 1.0f;
            iconTransitionAmount = 1.0f;
        } else {
            float fullAmount = Math.min(1.0f, (shelfStart - viewStart) / fullHeight);
            float interpolatedAmount = Interpolators.ACCELERATE_DECELERATE.getInterpolation(fullAmount);
            float fullTransitionAmount2 = 1.0f - NotificationUtils.interpolate(interpolatedAmount, fullAmount, expandAmount);
            float iconTransitionAmount2 = (shelfStart - viewStart) / iconTransformDistance;
            fullyInOrOut = false;
            iconTransitionAmount = 1.0f - Math.min(1.0f, iconTransitionAmount2);
            fullTransitionAmount = fullTransitionAmount2;
        }
        if (fullyInOrOut && !expandingAnimated && iconState.isLastExpandIcon) {
            iconState.isLastExpandIcon = false;
            iconState.customTransformHeight = Integer.MIN_VALUE;
        }
        updateIconPositioning(row, iconTransitionAmount, fullTransitionAmount, iconTransformDistance, scrolling, scrollingFast, expandingAnimated, isLastChild);
        return fullTransitionAmount;
    }

    private void updateIconPositioning(ExpandableNotificationRow row, float iconTransitionAmount, float fullTransitionAmount, float iconTransformDistance, boolean scrolling, boolean scrollingFast, boolean expandingAnimated, boolean isLastChild) {
        float transitionAmount;
        float f;
        StatusBarIconView icon = row.getEntry().expandedIcon;
        NotificationIconContainer.IconState iconState = getIconState(icon);
        if (iconState == null) {
            return;
        }
        boolean forceInShelf = iconState.isLastExpandIcon && !iconState.hasCustomTransformHeight();
        float f2 = 0.0f;
        float clampedAmount = iconTransitionAmount > 0.5f ? 1.0f : 0.0f;
        if (clampedAmount == fullTransitionAmount) {
            iconState.noAnimations = (scrollingFast || expandingAnimated) && !forceInShelf;
            iconState.useFullTransitionAmount = iconState.noAnimations || (!ICON_ANMATIONS_WHILE_SCROLLING && fullTransitionAmount == 0.0f && scrolling);
            iconState.useLinearTransitionAmount = (ICON_ANMATIONS_WHILE_SCROLLING || fullTransitionAmount != 0.0f || this.mAmbientState.isExpansionChanging()) ? false : true;
            iconState.translateContent = (((float) this.mMaxLayoutHeight) - getTranslationY()) - ((float) getIntrinsicHeight()) > 0.0f;
        }
        if (!forceInShelf && (scrollingFast || (expandingAnimated && iconState.useFullTransitionAmount && !ViewState.isAnimatingY(icon)))) {
            iconState.cancelAnimations(icon);
            iconState.useFullTransitionAmount = true;
            iconState.noAnimations = true;
        }
        if (iconState.hasCustomTransformHeight()) {
            iconState.useFullTransitionAmount = true;
        }
        if (iconState.isLastExpandIcon) {
            iconState.translateContent = false;
        }
        if (this.mAmbientState.isHiddenAtAll() && !row.isInShelf()) {
            transitionAmount = this.mAmbientState.isFullyHidden() ? 1.0f : 0.0f;
        } else if (isLastChild || !USE_ANIMATIONS_WHEN_OPENING || iconState.useFullTransitionAmount || iconState.useLinearTransitionAmount) {
            transitionAmount = iconTransitionAmount;
        } else {
            iconState.needsCannedAnimation = (iconState.clampedAppearAmount == clampedAmount || this.mNoAnimationsInThisFrame) ? false : true;
            transitionAmount = clampedAmount;
        }
        if (!USE_ANIMATIONS_WHEN_OPENING || iconState.useFullTransitionAmount) {
            f = fullTransitionAmount;
        } else {
            f = transitionAmount;
        }
        iconState.iconAppearAmount = f;
        iconState.clampedAppearAmount = clampedAmount;
        if (!row.isAboveShelf() && !row.showingPulsing() && (isLastChild || iconState.translateContent)) {
            f2 = iconTransitionAmount;
        }
        float contentTransformationAmount = f2;
        row.setContentTransformationAmount(contentTransformationAmount, isLastChild);
        setIconTransformationAmount(row, transitionAmount, iconTransformDistance, clampedAmount != transitionAmount, isLastChild);
    }

    /* JADX WARN: Code restructure failed: missing block: B:54:0x0106, code lost:
        if (r22.getTranslationZ() <= r21.mAmbientState.getBaseZHeight()) goto L46;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void setIconTransformationAmount(com.android.systemui.statusbar.notification.row.ExpandableNotificationRow r22, float r23, float r24, boolean r25, boolean r26) {
        /*
            Method dump skipped, instructions count: 313
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.NotificationShelf.setIconTransformationAmount(com.android.systemui.statusbar.notification.row.ExpandableNotificationRow, float, float, boolean, boolean):void");
    }

    private NotificationIconContainer.IconState getIconState(StatusBarIconView icon) {
        return this.mShelfIcons.getIconState(icon);
    }

    private float getFullyClosedTranslation() {
        return (-(getIntrinsicHeight() - this.mStatusBarHeight)) / 2;
    }

    public int getNotificationMergeSize() {
        return getIntrinsicHeight();
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public boolean hasNoContentHeight() {
        return true;
    }

    private void setHideBackground(boolean hideBackground) {
        if (this.mHideBackground != hideBackground) {
            this.mHideBackground = hideBackground;
            updateBackground();
            updateOutline();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.notification.row.ExpandableOutlineView
    public boolean needsOutline() {
        return !this.mHideBackground && super.needsOutline();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView
    public boolean shouldHideBackground() {
        return super.shouldHideBackground() || this.mHideBackground;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView, com.android.systemui.statusbar.notification.row.ExpandableView, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateRelativeOffset();
        int height = getResources().getDisplayMetrics().heightPixels;
        this.mClipRect.set(0, -height, getWidth(), height);
        this.mShelfIcons.setClipBounds(this.mClipRect);
    }

    private void updateRelativeOffset() {
        this.mCollapsedIcons.getLocationOnScreen(this.mTmp);
        int[] iArr = this.mTmp;
        this.mRelativeOffset = iArr[0];
        getLocationOnScreen(iArr);
        this.mRelativeOffset -= this.mTmp[0];
    }

    @Override // android.view.View
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        WindowInsets ret = super.onApplyWindowInsets(insets);
        DisplayCutout displayCutout = insets.getDisplayCutout();
        this.mCutoutHeight = (displayCutout == null || displayCutout.getSafeInsetTop() < 0) ? 0 : displayCutout.getSafeInsetTop();
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setOpenedAmount(float openedAmount) {
        int collapsedPadding;
        this.mNoAnimationsInThisFrame = openedAmount == 1.0f && this.mOpenedAmount == 0.0f;
        this.mOpenedAmount = openedAmount;
        openedAmount = (!this.mAmbientState.isPanelFullWidth() || this.mAmbientState.isDozing()) ? 1.0f : 1.0f;
        int start = this.mRelativeOffset;
        if (isLayoutRtl()) {
            start = (getWidth() - start) - this.mCollapsedIcons.getWidth();
        }
        int width = (int) NotificationUtils.interpolate(this.mCollapsedIcons.getFinalTranslationX() + start, this.mShelfIcons.getWidth(), Interpolators.FAST_OUT_SLOW_IN_REVERSE.getInterpolation(openedAmount));
        this.mShelfIcons.setActualLayoutWidth(width);
        boolean hasOverflow = this.mCollapsedIcons.hasOverflow();
        int collapsedPadding2 = this.mCollapsedIcons.getPaddingEnd();
        if (!hasOverflow) {
            collapsedPadding = collapsedPadding2 - this.mCollapsedIcons.getNoOverflowExtraPadding();
        } else {
            collapsedPadding = collapsedPadding2 - this.mCollapsedIcons.getPartialOverflowExtraPadding();
        }
        float padding = NotificationUtils.interpolate(collapsedPadding, this.mShelfIcons.getPaddingEnd(), openedAmount);
        this.mShelfIcons.setActualPaddingEnd(padding);
        float paddingStart = NotificationUtils.interpolate(start, this.mShelfIcons.getPaddingStart(), openedAmount);
        this.mShelfIcons.setActualPaddingStart(paddingStart);
        this.mShelfIcons.setOpenedAmount(openedAmount);
    }

    public void setMaxLayoutHeight(int maxLayoutHeight) {
        this.mMaxLayoutHeight = maxLayoutHeight;
    }

    public int getNotGoneIndex() {
        return this.mNotGoneIndex;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setHasItemsInStableShelf(boolean hasItemsInStableShelf) {
        if (this.mHasItemsInStableShelf != hasItemsInStableShelf) {
            this.mHasItemsInStableShelf = hasItemsInStableShelf;
            updateInteractiveness();
        }
    }

    public boolean hasItemsInStableShelf() {
        return this.mHasItemsInStableShelf;
    }

    public void setCollapsedIcons(NotificationIconContainer collapsedIcons) {
        this.mCollapsedIcons = collapsedIcons;
        this.mCollapsedIcons.addOnLayoutChangeListener(this);
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onStateChanged(int newState) {
        this.mStatusBarState = newState;
        updateInteractiveness();
    }

    private void updateInteractiveness() {
        this.mInteractive = this.mStatusBarState == 1 && this.mHasItemsInStableShelf;
        setClickable(this.mInteractive);
        setFocusable(this.mInteractive);
        setImportantForAccessibility(this.mInteractive ? 1 : 4);
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView
    protected boolean isInteractive() {
        return this.mInteractive;
    }

    public void setMaxShelfEnd(float maxShelfEnd) {
        this.mMaxShelfEnd = maxShelfEnd;
    }

    public void setAnimationsEnabled(boolean enabled) {
        this.mAnimationsEnabled = enabled;
        if (!enabled) {
            this.mShelfIcons.setAnimationsEnabled(false);
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView, android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (this.mInteractive) {
            info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_EXPAND);
            AccessibilityNodeInfo.AccessibilityAction unlock = new AccessibilityNodeInfo.AccessibilityAction(16, getContext().getString(R.string.accessibility_overflow_action));
            info.addAction(unlock);
        }
    }

    @Override // android.view.View.OnLayoutChangeListener
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        updateRelativeOffset();
    }

    public void onUiModeChanged() {
        updateBackgroundColors();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class ShelfState extends ExpandableViewState {
        private boolean hasItemsInStableShelf;
        private float maxShelfEnd;
        private float openedAmount;

        private ShelfState() {
        }

        @Override // com.android.systemui.statusbar.notification.stack.ExpandableViewState, com.android.systemui.statusbar.notification.stack.ViewState
        public void applyToView(View view) {
            if (!NotificationShelf.this.mShowNotificationShelf) {
                return;
            }
            super.applyToView(view);
            NotificationShelf.this.setMaxShelfEnd(this.maxShelfEnd);
            NotificationShelf.this.setOpenedAmount(this.openedAmount);
            NotificationShelf.this.updateAppearance();
            NotificationShelf.this.setHasItemsInStableShelf(this.hasItemsInStableShelf);
            NotificationShelf.this.mShelfIcons.setAnimationsEnabled(NotificationShelf.this.mAnimationsEnabled);
        }

        @Override // com.android.systemui.statusbar.notification.stack.ExpandableViewState, com.android.systemui.statusbar.notification.stack.ViewState
        public void animateTo(View child, AnimationProperties properties) {
            if (!NotificationShelf.this.mShowNotificationShelf) {
                return;
            }
            super.animateTo(child, properties);
            NotificationShelf.this.setMaxShelfEnd(this.maxShelfEnd);
            NotificationShelf.this.setOpenedAmount(this.openedAmount);
            NotificationShelf.this.updateAppearance();
            NotificationShelf.this.setHasItemsInStableShelf(this.hasItemsInStableShelf);
            NotificationShelf.this.mShelfIcons.setAnimationsEnabled(NotificationShelf.this.mAnimationsEnabled);
        }
    }
}
