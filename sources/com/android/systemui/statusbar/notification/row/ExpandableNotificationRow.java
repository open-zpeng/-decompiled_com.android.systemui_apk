package com.android.systemui.statusbar.notification.row;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.NotificationChannel;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Path;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.service.notification.StatusBarNotification;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.FloatProperty;
import android.util.Log;
import android.util.MathUtils;
import android.util.Property;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.NotificationHeaderView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RemoteViews;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.ContrastColorUtil;
import com.android.internal.widget.CachingIconView;
import com.android.settingslib.Utils;
import com.android.systemui.Dependency;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.StatusBarIconView;
import com.android.systemui.statusbar.notification.AboveShelfChangedListener;
import com.android.systemui.statusbar.notification.ActivityLaunchAnimator;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.logging.NotificationCounters;
import com.android.systemui.statusbar.notification.row.NotificationContentInflater;
import com.android.systemui.statusbar.notification.row.NotificationGuts;
import com.android.systemui.statusbar.notification.row.wrapper.NotificationViewWrapper;
import com.android.systemui.statusbar.notification.stack.AmbientState;
import com.android.systemui.statusbar.notification.stack.AnimationProperties;
import com.android.systemui.statusbar.notification.stack.ExpandableViewState;
import com.android.systemui.statusbar.notification.stack.NotificationChildrenContainer;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.InflatedSmartReplies;
import com.badlogic.gdx.net.HttpStatus;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
/* loaded from: classes21.dex */
public class ExpandableNotificationRow extends ActivatableNotificationView implements PluginListener<NotificationMenuRowPlugin> {
    private static final int COLORED_DIVIDER_ALPHA = 123;
    private static final boolean DEBUG = false;
    private static final int DEFAULT_DIVIDER_ALPHA = 41;
    public static final float DEFAULT_HEADER_VISIBLE_AMOUNT = 1.0f;
    private static final int MENU_VIEW_INDEX = 0;
    private static final String TAG = "ExpandableNotifRow";
    private boolean mAboveShelf;
    private AboveShelfChangedListener mAboveShelfChangedListener;
    private String mAppName;
    private KeyguardBypassController mBypassController;
    private View mChildAfterViewWhenDismissed;
    private boolean mChildIsExpanding;
    private NotificationChildrenContainer mChildrenContainer;
    private ViewStub mChildrenContainerStub;
    private boolean mChildrenExpanded;
    private float mContentTransformationAmount;
    private boolean mDismissed;
    private boolean mEnableNonGroupedNotificationExpand;
    private NotificationEntry mEntry;
    private boolean mExpandAnimationRunning;
    private View.OnClickListener mExpandClickListener;
    private boolean mExpandable;
    private boolean mExpandedWhenPinned;
    private final Runnable mExpireRecentlyAlertedFlag;
    private FalsingManager mFalsingManager;
    private boolean mForceUnlocked;
    private boolean mGroupExpansionChanging;
    private NotificationGroupManager mGroupManager;
    private View mGroupParentWhenDismissed;
    private NotificationGuts mGuts;
    private ViewStub mGutsStub;
    private boolean mHasUserChangedExpansion;
    private float mHeaderVisibleAmount;
    private Consumer<Boolean> mHeadsUpAnimatingAwayListener;
    private HeadsUpManager mHeadsUpManager;
    private boolean mHeadsupDisappearRunning;
    private boolean mHideSensitiveForIntrinsicHeight;
    private boolean mIconAnimationRunning;
    private int mIconTransformContentShift;
    private int mIconTransformContentShiftNoIcon;
    private boolean mIconsVisible;
    private NotificationInlineImageResolver mImageResolver;
    private int mIncreasedPaddingBetweenElements;
    private boolean mIsBlockingHelperShowing;
    private boolean mIsColorized;
    private boolean mIsHeadsUp;
    private boolean mIsLastChild;
    private boolean mIsLowPriority;
    private boolean mIsPinned;
    private boolean mIsSummaryWithChildren;
    private boolean mIsSystemChildExpanded;
    private boolean mIsSystemExpanded;
    private boolean mJustClicked;
    private boolean mKeepInParent;
    private boolean mLastChronometerRunning;
    private LayoutListener mLayoutListener;
    private NotificationContentView[] mLayouts;
    private ExpansionLogger mLogger;
    private String mLoggingKey;
    private LongPressListener mLongPressListener;
    private int mMaxHeadsUpHeight;
    private int mMaxHeadsUpHeightBeforeN;
    private int mMaxHeadsUpHeightBeforeP;
    private int mMaxHeadsUpHeightIncreased;
    private NotificationMediaManager mMediaManager;
    private NotificationMenuRowPlugin mMenuRow;
    private boolean mMustStayOnScreen;
    private boolean mNeedsRedaction;
    private int mNotificationColor;
    private final NotificationContentInflater mNotificationInflater;
    private int mNotificationLaunchHeight;
    private int mNotificationMaxHeight;
    private int mNotificationMinHeight;
    private int mNotificationMinHeightBeforeN;
    private int mNotificationMinHeightBeforeP;
    private int mNotificationMinHeightLarge;
    private int mNotificationMinHeightMedia;
    private ExpandableNotificationRow mNotificationParent;
    private boolean mNotificationTranslationFinished;
    private View.OnClickListener mOnAppOpsClickListener;
    private View.OnClickListener mOnClickListener;
    private Runnable mOnDismissRunnable;
    private OnExpandClickListener mOnExpandClickListener;
    private boolean mOnKeyguard;
    private NotificationContentView mPrivateLayout;
    private NotificationContentView mPublicLayout;
    private boolean mRefocusOnDismiss;
    private boolean mRemoved;
    private BooleanSupplier mSecureStateProvider;
    private boolean mSensitive;
    private boolean mSensitiveHiddenInGeneral;
    private boolean mShowGroupBackgroundWhenExpanded;
    private boolean mShowNoBackground;
    private boolean mShowingPublic;
    private boolean mShowingPublicInitialized;
    private StatusBarNotification mStatusBarNotification;
    private StatusBarStateController mStatusbarStateController;
    private SystemNotificationAsyncTask mSystemNotificationAsyncTask;
    private Animator mTranslateAnim;
    private ArrayList<View> mTranslateableViews;
    private float mTranslationWhenRemoved;
    private boolean mUpdateBackgroundOnUpdate;
    private boolean mUseIncreasedCollapsedHeight;
    private boolean mUseIncreasedHeadsUpHeight;
    private boolean mUserExpanded;
    private boolean mUserLocked;
    private boolean mWasChildInGroupWhenRemoved;
    private static final long RECENTLY_ALERTED_THRESHOLD_MS = TimeUnit.SECONDS.toMillis(30);
    private static final Property<ExpandableNotificationRow, Float> TRANSLATE_CONTENT = new FloatProperty<ExpandableNotificationRow>("translate") { // from class: com.android.systemui.statusbar.notification.row.ExpandableNotificationRow.2
        @Override // android.util.FloatProperty
        public void setValue(ExpandableNotificationRow object, float value) {
            object.setTranslation(value);
        }

        @Override // android.util.Property
        public Float get(ExpandableNotificationRow object) {
            return Float.valueOf(object.getTranslation());
        }
    };

    /* loaded from: classes21.dex */
    public interface ExpansionLogger {
        void logNotificationExpansion(String str, boolean z, boolean z2);
    }

    /* loaded from: classes21.dex */
    public interface LayoutListener {
        void onLayout();
    }

    /* loaded from: classes21.dex */
    public interface LongPressListener {
        boolean onLongPress(View view, int i, int i2, NotificationMenuRowPlugin.MenuItem menuItem);
    }

    /* loaded from: classes21.dex */
    public interface OnAppOpsClickListener {
        boolean onClick(View view, int i, int i2, NotificationMenuRowPlugin.MenuItem menuItem);
    }

    /* loaded from: classes21.dex */
    public interface OnExpandClickListener {
        void onExpandClicked(NotificationEntry notificationEntry, boolean z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Boolean isSystemNotification(Context context, StatusBarNotification statusBarNotification) {
        PackageManager packageManager = StatusBar.getPackageManagerForUser(context, statusBarNotification.getUser().getIdentifier());
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(statusBarNotification.getPackageName(), 64);
            Boolean isSystemNotification = Boolean.valueOf(Utils.isSystemPackage(context.getResources(), packageManager, packageInfo));
            return isSystemNotification;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "cacheIsSystemNotification: Could not find package info");
            return null;
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public boolean isGroupExpansionChanging() {
        if (isChildInGroup()) {
            return this.mNotificationParent.isGroupExpansionChanging();
        }
        return this.mGroupExpansionChanging;
    }

    public void setGroupExpansionChanging(boolean changing) {
        this.mGroupExpansionChanging = changing;
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public void setActualHeightAnimating(boolean animating) {
        NotificationContentView notificationContentView = this.mPrivateLayout;
        if (notificationContentView != null) {
            notificationContentView.setContentHeightAnimating(animating);
        }
    }

    public NotificationContentView getPrivateLayout() {
        return this.mPrivateLayout;
    }

    public NotificationContentView getPublicLayout() {
        return this.mPublicLayout;
    }

    public void setIconAnimationRunning(boolean running) {
        NotificationContentView[] notificationContentViewArr;
        for (NotificationContentView l : this.mLayouts) {
            setIconAnimationRunning(running, l);
        }
        if (this.mIsSummaryWithChildren) {
            setIconAnimationRunningForChild(running, this.mChildrenContainer.getHeaderView());
            setIconAnimationRunningForChild(running, this.mChildrenContainer.getLowPriorityHeaderView());
            List<ExpandableNotificationRow> notificationChildren = this.mChildrenContainer.getNotificationChildren();
            for (int i = 0; i < notificationChildren.size(); i++) {
                ExpandableNotificationRow child = notificationChildren.get(i);
                child.setIconAnimationRunning(running);
            }
        }
        this.mIconAnimationRunning = running;
    }

    private void setIconAnimationRunning(boolean running, NotificationContentView layout) {
        if (layout != null) {
            View contractedChild = layout.getContractedChild();
            View expandedChild = layout.getExpandedChild();
            View headsUpChild = layout.getHeadsUpChild();
            setIconAnimationRunningForChild(running, contractedChild);
            setIconAnimationRunningForChild(running, expandedChild);
            setIconAnimationRunningForChild(running, headsUpChild);
        }
    }

    private void setIconAnimationRunningForChild(boolean running, View child) {
        if (child != null) {
            ImageView icon = (ImageView) child.findViewById(16908294);
            setIconRunning(icon, running);
            ImageView rightIcon = (ImageView) child.findViewById(16909395);
            setIconRunning(rightIcon, running);
        }
    }

    private void setIconRunning(ImageView imageView, boolean running) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AnimationDrawable) {
                AnimationDrawable animationDrawable = (AnimationDrawable) drawable;
                if (running) {
                    animationDrawable.start();
                } else {
                    animationDrawable.stop();
                }
            } else if (drawable instanceof AnimatedVectorDrawable) {
                AnimatedVectorDrawable animationDrawable2 = (AnimatedVectorDrawable) drawable;
                if (running) {
                    animationDrawable2.start();
                } else {
                    animationDrawable2.stop();
                }
            }
        }
    }

    public void setEntry(NotificationEntry entry) {
        this.mEntry = entry;
        this.mStatusBarNotification = entry.notification;
        cacheIsSystemNotification();
    }

    public void inflateViews() {
        this.mNotificationInflater.inflateNotificationViews();
    }

    public void freeContentViewWhenSafe(final int inflationFlag) {
        updateInflationFlag(inflationFlag, false);
        Runnable freeViewRunnable = new Runnable() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$ExpandableNotificationRow$RmUEmS0GEHf9L7pp2cHmxPWsfmA
            @Override // java.lang.Runnable
            public final void run() {
                ExpandableNotificationRow.this.lambda$freeContentViewWhenSafe$0$ExpandableNotificationRow(inflationFlag);
            }
        };
        if (inflationFlag == 4) {
            getPrivateLayout().performWhenContentInactive(2, freeViewRunnable);
        } else if (inflationFlag == 8) {
            getPublicLayout().performWhenContentInactive(0, freeViewRunnable);
        }
    }

    public /* synthetic */ void lambda$freeContentViewWhenSafe$0$ExpandableNotificationRow(int inflationFlag) {
        this.mNotificationInflater.freeNotificationView(inflationFlag);
    }

    public void updateInflationFlag(int flag, boolean shouldInflate) {
        this.mNotificationInflater.updateInflationFlag(flag, shouldInflate);
    }

    public boolean isInflationFlagSet(int flag) {
        return this.mNotificationInflater.isInflationFlagSet(flag);
    }

    private void cacheIsSystemNotification() {
        NotificationEntry notificationEntry = this.mEntry;
        if (notificationEntry != null && notificationEntry.mIsSystemNotification == null && this.mSystemNotificationAsyncTask.getStatus() == AsyncTask.Status.PENDING) {
            this.mSystemNotificationAsyncTask.execute(new Void[0]);
        }
    }

    public boolean getIsNonblockable() {
        NotificationEntry notificationEntry;
        boolean isNonblockable = ((NotificationBlockingHelperManager) Dependency.get(NotificationBlockingHelperManager.class)).isNonblockable(this.mStatusBarNotification.getPackageName(), this.mEntry.channel.getId());
        NotificationEntry notificationEntry2 = this.mEntry;
        if (notificationEntry2 != null && notificationEntry2.mIsSystemNotification == null) {
            this.mSystemNotificationAsyncTask.cancel(true);
            this.mEntry.mIsSystemNotification = isSystemNotification(this.mContext, this.mStatusBarNotification);
        }
        boolean isNonblockable2 = isNonblockable | this.mEntry.channel.isImportanceLockedByOEM() | this.mEntry.channel.isImportanceLockedByCriticalDeviceFunction();
        if (!isNonblockable2 && (notificationEntry = this.mEntry) != null && notificationEntry.mIsSystemNotification != null && this.mEntry.mIsSystemNotification.booleanValue() && this.mEntry.channel != null && !this.mEntry.channel.isBlockableSystem()) {
            return true;
        }
        return isNonblockable2;
    }

    public void onNotificationUpdated() {
        NotificationContentView[] notificationContentViewArr;
        for (NotificationContentView l : this.mLayouts) {
            l.onNotificationUpdated(this.mEntry);
        }
        this.mIsColorized = this.mStatusBarNotification.getNotification().isColorized();
        this.mShowingPublicInitialized = false;
        updateNotificationColor();
        NotificationMenuRowPlugin notificationMenuRowPlugin = this.mMenuRow;
        if (notificationMenuRowPlugin != null) {
            notificationMenuRowPlugin.onNotificationUpdated(this.mStatusBarNotification);
            this.mMenuRow.setAppName(this.mAppName);
        }
        if (this.mIsSummaryWithChildren) {
            this.mChildrenContainer.recreateNotificationHeader(this.mExpandClickListener);
            this.mChildrenContainer.onNotificationUpdated();
        }
        if (this.mIconAnimationRunning) {
            setIconAnimationRunning(true);
        }
        if (this.mLastChronometerRunning) {
            setChronometerRunning(true);
        }
        ExpandableNotificationRow expandableNotificationRow = this.mNotificationParent;
        if (expandableNotificationRow != null) {
            expandableNotificationRow.updateChildrenHeaderAppearance();
        }
        onChildrenCountChanged();
        this.mPublicLayout.updateExpandButtons(true);
        updateLimits();
        updateIconVisibilities();
        updateShelfIconColor();
        updateRippleAllowed();
        if (this.mUpdateBackgroundOnUpdate) {
            this.mUpdateBackgroundOnUpdate = false;
            updateBackgroundColors();
        }
    }

    public void onNotificationRankingUpdated() {
        NotificationMenuRowPlugin notificationMenuRowPlugin = this.mMenuRow;
        if (notificationMenuRowPlugin != null) {
            notificationMenuRowPlugin.onNotificationUpdated(this.mStatusBarNotification);
        }
    }

    @VisibleForTesting
    void updateShelfIconColor() {
        StatusBarIconView expandedIcon = this.mEntry.expandedIcon;
        boolean isPreL = Boolean.TRUE.equals(expandedIcon.getTag(R.id.icon_is_pre_L));
        boolean z = false;
        boolean colorize = !isPreL || NotificationUtils.isGrayscale(expandedIcon, ContrastColorUtil.getInstance(this.mContext));
        int color = 0;
        if (colorize) {
            NotificationHeaderView header = getVisibleNotificationHeader();
            if (header != null) {
                color = header.getOriginalIconColor();
            } else {
                NotificationEntry notificationEntry = this.mEntry;
                Context context = this.mContext;
                if (this.mIsLowPriority && !isExpanded()) {
                    z = true;
                }
                color = notificationEntry.getContrastedColor(context, z, getBackgroundColorWithoutTint());
            }
        }
        expandedIcon.setStaticDrawableColor(color);
    }

    public void setAboveShelfChangedListener(AboveShelfChangedListener aboveShelfChangedListener) {
        this.mAboveShelfChangedListener = aboveShelfChangedListener;
    }

    public void setSecureStateProvider(BooleanSupplier secureStateProvider) {
        this.mSecureStateProvider = secureStateProvider;
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView
    public boolean isDimmable() {
        if (getShowingLayout().isDimmable() && !showingPulsing()) {
            return super.isDimmable();
        }
        return false;
    }

    private void updateLimits() {
        NotificationContentView[] notificationContentViewArr;
        for (NotificationContentView l : this.mLayouts) {
            updateLimitsForView(l);
        }
    }

    private void updateLimitsForView(NotificationContentView layout) {
        int minHeight;
        int headsUpHeight;
        boolean z = true;
        boolean customView = (layout.getContractedChild() == null || layout.getContractedChild().getId() == 16909507) ? false : true;
        boolean beforeN = this.mEntry.targetSdk < 24;
        boolean beforeP = this.mEntry.targetSdk < 28;
        View expandedView = layout.getExpandedChild();
        boolean isMediaLayout = (expandedView == null || expandedView.findViewById(16909197) == null) ? false : true;
        boolean showCompactMediaSeekbar = this.mMediaManager.getShowCompactMediaSeekbar();
        if (customView && beforeP && !this.mIsSummaryWithChildren) {
            minHeight = beforeN ? this.mNotificationMinHeightBeforeN : this.mNotificationMinHeightBeforeP;
        } else if (isMediaLayout && showCompactMediaSeekbar) {
            minHeight = this.mNotificationMinHeightMedia;
        } else if (this.mUseIncreasedCollapsedHeight && layout == this.mPrivateLayout) {
            minHeight = this.mNotificationMinHeightLarge;
        } else {
            minHeight = this.mNotificationMinHeight;
        }
        if (layout.getHeadsUpChild() == null || layout.getHeadsUpChild().getId() == 16909507) {
            z = false;
        }
        boolean headsUpCustom = z;
        if (headsUpCustom && beforeP) {
            headsUpHeight = beforeN ? this.mMaxHeadsUpHeightBeforeN : this.mMaxHeadsUpHeightBeforeP;
        } else if (this.mUseIncreasedHeadsUpHeight && layout == this.mPrivateLayout) {
            headsUpHeight = this.mMaxHeadsUpHeightIncreased;
        } else {
            headsUpHeight = this.mMaxHeadsUpHeight;
        }
        NotificationViewWrapper headsUpWrapper = layout.getVisibleWrapper(2);
        if (headsUpWrapper != null) {
            headsUpHeight = Math.max(headsUpHeight, headsUpWrapper.getMinLayoutHeight());
        }
        layout.setHeights(minHeight, headsUpHeight, this.mNotificationMaxHeight);
    }

    public StatusBarNotification getStatusBarNotification() {
        return this.mStatusBarNotification;
    }

    public NotificationEntry getEntry() {
        return this.mEntry;
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView
    public boolean isHeadsUp() {
        return this.mIsHeadsUp;
    }

    public void setHeadsUp(boolean isHeadsUp) {
        boolean wasAboveShelf = isAboveShelf();
        int intrinsicBefore = getIntrinsicHeight();
        this.mIsHeadsUp = isHeadsUp;
        this.mPrivateLayout.setHeadsUp(isHeadsUp);
        if (this.mIsSummaryWithChildren) {
            this.mChildrenContainer.updateGroupOverflow();
        }
        if (intrinsicBefore != getIntrinsicHeight()) {
            notifyHeightChanged(false);
        }
        if (isHeadsUp) {
            this.mMustStayOnScreen = true;
            setAboveShelf(true);
        } else if (isAboveShelf() != wasAboveShelf) {
            this.mAboveShelfChangedListener.onAboveShelfStateChanged(!wasAboveShelf);
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public boolean showingPulsing() {
        return isHeadsUpState() && (isDozing() || (this.mOnKeyguard && isBypassEnabled()));
    }

    public boolean isHeadsUpState() {
        return this.mIsHeadsUp || this.mHeadsupDisappearRunning;
    }

    public void setGroupManager(NotificationGroupManager groupManager) {
        this.mGroupManager = groupManager;
        this.mPrivateLayout.setGroupManager(groupManager);
    }

    public void setRemoteInputController(RemoteInputController r) {
        this.mPrivateLayout.setRemoteInputController(r);
    }

    public void setAppName(String appName) {
        this.mAppName = appName;
        NotificationMenuRowPlugin notificationMenuRowPlugin = this.mMenuRow;
        if (notificationMenuRowPlugin != null && notificationMenuRowPlugin.getMenuView() != null) {
            this.mMenuRow.setAppName(this.mAppName);
        }
    }

    public void addChildNotification(ExpandableNotificationRow row) {
        addChildNotification(row, -1);
    }

    public void setHeaderVisibleAmount(float headerVisibleAmount) {
        NotificationContentView[] notificationContentViewArr;
        if (this.mHeaderVisibleAmount != headerVisibleAmount) {
            this.mHeaderVisibleAmount = headerVisibleAmount;
            for (NotificationContentView l : this.mLayouts) {
                l.setHeaderVisibleAmount(headerVisibleAmount);
            }
            NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
            if (notificationChildrenContainer != null) {
                notificationChildrenContainer.setHeaderVisibleAmount(headerVisibleAmount);
            }
            notifyHeightChanged(false);
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public float getHeaderVisibleAmount() {
        return this.mHeaderVisibleAmount;
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public void setHeadsUpIsVisible() {
        super.setHeadsUpIsVisible();
        this.mMustStayOnScreen = false;
    }

    public void addChildNotification(ExpandableNotificationRow row, int childIndex) {
        if (this.mChildrenContainer == null) {
            this.mChildrenContainerStub.inflate();
        }
        this.mChildrenContainer.addNotification(row, childIndex);
        onChildrenCountChanged();
        row.setIsChildInGroup(true, this);
    }

    public void removeChildNotification(ExpandableNotificationRow row) {
        NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
        if (notificationChildrenContainer != null) {
            notificationChildrenContainer.removeNotification(row);
        }
        onChildrenCountChanged();
        row.setIsChildInGroup(false, null);
        row.setBottomRoundness(0.0f, false);
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public boolean isChildInGroup() {
        return this.mNotificationParent != null;
    }

    public boolean isOnlyChildInGroup() {
        return this.mGroupManager.isOnlyChildInGroup(getStatusBarNotification());
    }

    public ExpandableNotificationRow getNotificationParent() {
        return this.mNotificationParent;
    }

    public void setIsChildInGroup(boolean isChildInGroup, ExpandableNotificationRow parent) {
        ExpandableNotificationRow expandableNotificationRow;
        boolean childInGroup = StatusBar.ENABLE_CHILD_NOTIFICATIONS && isChildInGroup;
        if (this.mExpandAnimationRunning && !isChildInGroup && (expandableNotificationRow = this.mNotificationParent) != null) {
            expandableNotificationRow.setChildIsExpanding(false);
            this.mNotificationParent.setExtraWidthForClipping(0.0f);
            this.mNotificationParent.setMinimumHeightForClipping(0);
        }
        this.mNotificationParent = childInGroup ? parent : null;
        this.mPrivateLayout.setIsChildInGroup(childInGroup);
        this.mNotificationInflater.setIsChildInGroup(childInGroup);
        resetBackgroundAlpha();
        updateBackgroundForGroupState();
        updateClickAndFocus();
        if (this.mNotificationParent != null) {
            setOverrideTintColor(0, 0.0f);
            setDistanceToTopRoundness(-1.0f);
            this.mNotificationParent.updateBackgroundForGroupState();
        }
        updateIconVisibilities();
        updateBackgroundClipping();
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView, android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() != 0 || !isChildInGroup() || isGroupExpanded()) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView
    protected boolean handleSlideBack() {
        NotificationMenuRowPlugin notificationMenuRowPlugin = this.mMenuRow;
        if (notificationMenuRowPlugin != null && notificationMenuRowPlugin.isMenuVisible()) {
            animateTranslateNotification(0.0f);
            return true;
        }
        return false;
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView
    protected boolean shouldHideBackground() {
        return super.shouldHideBackground() || this.mShowNoBackground;
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public boolean isSummaryWithChildren() {
        return this.mIsSummaryWithChildren;
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public boolean areChildrenExpanded() {
        return this.mChildrenExpanded;
    }

    public List<ExpandableNotificationRow> getNotificationChildren() {
        NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
        if (notificationChildrenContainer == null) {
            return null;
        }
        return notificationChildrenContainer.getNotificationChildren();
    }

    public int getNumberOfNotificationChildren() {
        NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
        if (notificationChildrenContainer == null) {
            return 0;
        }
        return notificationChildrenContainer.getNotificationChildren().size();
    }

    public boolean applyChildOrder(List<ExpandableNotificationRow> childOrder, VisualStabilityManager visualStabilityManager, VisualStabilityManager.Callback callback) {
        NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
        return notificationChildrenContainer != null && notificationChildrenContainer.applyChildOrder(childOrder, visualStabilityManager, callback);
    }

    public void updateChildrenStates(AmbientState ambientState) {
        if (this.mIsSummaryWithChildren) {
            ExpandableViewState parentState = getViewState();
            this.mChildrenContainer.updateState(parentState, ambientState);
        }
    }

    public void applyChildrenState() {
        if (this.mIsSummaryWithChildren) {
            this.mChildrenContainer.applyState();
        }
    }

    public void prepareExpansionChanged() {
        if (this.mIsSummaryWithChildren) {
            this.mChildrenContainer.prepareExpansionChanged();
        }
    }

    public void startChildAnimation(AnimationProperties properties) {
        if (this.mIsSummaryWithChildren) {
            this.mChildrenContainer.startAnimationToState(properties);
        }
    }

    public ExpandableNotificationRow getViewAtPosition(float y) {
        if (!this.mIsSummaryWithChildren || !this.mChildrenExpanded) {
            return this;
        }
        ExpandableNotificationRow view = this.mChildrenContainer.getViewAtPosition(y);
        return view == null ? this : view;
    }

    public NotificationGuts getGuts() {
        return this.mGuts;
    }

    public void setPinned(boolean pinned) {
        int intrinsicHeight = getIntrinsicHeight();
        boolean wasAboveShelf = isAboveShelf();
        this.mIsPinned = pinned;
        if (intrinsicHeight != getIntrinsicHeight()) {
            notifyHeightChanged(false);
        }
        if (pinned) {
            setIconAnimationRunning(true);
            this.mExpandedWhenPinned = false;
        } else if (this.mExpandedWhenPinned) {
            setUserExpanded(true);
        }
        setChronometerRunning(this.mLastChronometerRunning);
        if (isAboveShelf() != wasAboveShelf) {
            this.mAboveShelfChangedListener.onAboveShelfStateChanged(!wasAboveShelf);
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView
    public boolean isPinned() {
        return this.mIsPinned;
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public int getPinnedHeadsUpHeight() {
        return getPinnedHeadsUpHeight(true);
    }

    private int getPinnedHeadsUpHeight(boolean atLeastMinHeight) {
        if (this.mIsSummaryWithChildren) {
            return this.mChildrenContainer.getIntrinsicHeight();
        }
        if (this.mExpandedWhenPinned) {
            return Math.max(getMaxExpandHeight(), getHeadsUpHeight());
        }
        if (atLeastMinHeight) {
            return Math.max(getCollapsedHeight(), getHeadsUpHeight());
        }
        return getHeadsUpHeight();
    }

    public void setJustClicked(boolean justClicked) {
        this.mJustClicked = justClicked;
    }

    public boolean wasJustClicked() {
        return this.mJustClicked;
    }

    public void setChronometerRunning(boolean running) {
        this.mLastChronometerRunning = running;
        setChronometerRunning(running, this.mPrivateLayout);
        setChronometerRunning(running, this.mPublicLayout);
        NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
        if (notificationChildrenContainer != null) {
            List<ExpandableNotificationRow> notificationChildren = notificationChildrenContainer.getNotificationChildren();
            for (int i = 0; i < notificationChildren.size(); i++) {
                ExpandableNotificationRow child = notificationChildren.get(i);
                child.setChronometerRunning(running);
            }
        }
    }

    private void setChronometerRunning(boolean running, NotificationContentView layout) {
        if (layout != null) {
            boolean running2 = running || isPinned();
            View contractedChild = layout.getContractedChild();
            View expandedChild = layout.getExpandedChild();
            View headsUpChild = layout.getHeadsUpChild();
            setChronometerRunningForChild(running2, contractedChild);
            setChronometerRunningForChild(running2, expandedChild);
            setChronometerRunningForChild(running2, headsUpChild);
        }
    }

    private void setChronometerRunningForChild(boolean running, View child) {
        if (child != null) {
            View chronometer = child.findViewById(16908909);
            if (chronometer instanceof Chronometer) {
                ((Chronometer) chronometer).setStarted(running);
            }
        }
    }

    public NotificationHeaderView getNotificationHeader() {
        if (this.mIsSummaryWithChildren) {
            return this.mChildrenContainer.getHeaderView();
        }
        return this.mPrivateLayout.getNotificationHeader();
    }

    public NotificationHeaderView getVisibleNotificationHeader() {
        if (this.mIsSummaryWithChildren && !shouldShowPublic()) {
            return this.mChildrenContainer.getVisibleHeader();
        }
        return getShowingLayout().getVisibleNotificationHeader();
    }

    public NotificationHeaderView getContractedNotificationHeader() {
        if (this.mIsSummaryWithChildren) {
            return this.mChildrenContainer.getHeaderView();
        }
        return this.mPrivateLayout.getContractedNotificationHeader();
    }

    public void setOnExpandClickListener(OnExpandClickListener onExpandClickListener) {
        this.mOnExpandClickListener = onExpandClickListener;
    }

    public void setLongPressListener(LongPressListener longPressListener) {
        this.mLongPressListener = longPressListener;
    }

    @Override // android.view.View
    public void setOnClickListener(View.OnClickListener l) {
        super.setOnClickListener(l);
        this.mOnClickListener = l;
        updateClickAndFocus();
    }

    private void updateClickAndFocus() {
        boolean clickable = false;
        boolean normalChild = !isChildInGroup() || isGroupExpanded();
        if (this.mOnClickListener != null && normalChild) {
            clickable = true;
        }
        if (isFocusable() != normalChild) {
            setFocusable(normalChild);
        }
        if (isClickable() != clickable) {
            setClickable(clickable);
        }
    }

    public void setHeadsUpManager(HeadsUpManager headsUpManager) {
        this.mHeadsUpManager = headsUpManager;
    }

    public HeadsUpManager getHeadsUpManager() {
        return this.mHeadsUpManager;
    }

    public void setGutsView(NotificationMenuRowPlugin.MenuItem item) {
        if (this.mGuts != null && (item.getGutsView() instanceof NotificationGuts.GutsContent)) {
            ((NotificationGuts.GutsContent) item.getGutsView()).setGutsParent(this.mGuts);
            this.mGuts.setGutsContent((NotificationGuts.GutsContent) item.getGutsView());
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mEntry.setInitializationTime(SystemClock.elapsedRealtime());
        ((PluginManager) Dependency.get(PluginManager.class)).addPluginListener((PluginListener) this, NotificationMenuRowPlugin.class, false);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ((PluginManager) Dependency.get(PluginManager.class)).removePluginListener(this);
    }

    @Override // com.android.systemui.plugins.PluginListener
    public void onPluginConnected(NotificationMenuRowPlugin plugin, Context pluginContext) {
        NotificationMenuRowPlugin notificationMenuRowPlugin = this.mMenuRow;
        boolean existed = (notificationMenuRowPlugin == null || notificationMenuRowPlugin.getMenuView() == null) ? false : true;
        if (existed) {
            removeView(this.mMenuRow.getMenuView());
        }
        if (plugin == null) {
            return;
        }
        this.mMenuRow = plugin;
        if (this.mMenuRow.shouldUseDefaultMenuItems()) {
            ArrayList<NotificationMenuRowPlugin.MenuItem> items = new ArrayList<>();
            items.add(NotificationMenuRow.createInfoItem(this.mContext));
            items.add(NotificationMenuRow.createSnoozeItem(this.mContext));
            items.add(NotificationMenuRow.createAppOpsItem(this.mContext));
            this.mMenuRow.setMenuItems(items);
        }
        if (existed) {
            createMenu();
        }
    }

    @Override // com.android.systemui.plugins.PluginListener
    public void onPluginDisconnected(NotificationMenuRowPlugin plugin) {
        boolean existed = this.mMenuRow.getMenuView() != null;
        this.mMenuRow = new NotificationMenuRow(this.mContext);
        if (existed) {
            createMenu();
        }
    }

    public NotificationMenuRowPlugin createMenu() {
        NotificationMenuRowPlugin notificationMenuRowPlugin = this.mMenuRow;
        if (notificationMenuRowPlugin == null) {
            return null;
        }
        if (notificationMenuRowPlugin.getMenuView() == null) {
            this.mMenuRow.createMenu(this, this.mStatusBarNotification);
            this.mMenuRow.setAppName(this.mAppName);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-1, -1);
            addView(this.mMenuRow.getMenuView(), 0, lp);
        }
        return this.mMenuRow;
    }

    public NotificationMenuRowPlugin getProvider() {
        return this.mMenuRow;
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView, com.android.systemui.statusbar.notification.row.ExpandableOutlineView
    public void onDensityOrFontScaleChanged() {
        super.onDensityOrFontScaleChanged();
        initDimens();
        initBackground();
        reInflateViews();
    }

    private void reInflateViews() {
        NotificationContentView[] notificationContentViewArr;
        NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
        if (notificationChildrenContainer != null) {
            notificationChildrenContainer.reInflateViews(this.mExpandClickListener, this.mEntry.notification);
        }
        if (this.mGuts != null) {
            NotificationGuts oldGuts = this.mGuts;
            int index = indexOfChild(oldGuts);
            removeView(oldGuts);
            this.mGuts = (NotificationGuts) LayoutInflater.from(this.mContext).inflate(R.layout.notification_guts, (ViewGroup) this, false);
            this.mGuts.setVisibility(oldGuts.isExposed() ? 0 : 8);
            addView(this.mGuts, index);
        }
        NotificationMenuRowPlugin notificationMenuRowPlugin = this.mMenuRow;
        View oldMenu = notificationMenuRowPlugin == null ? null : notificationMenuRowPlugin.getMenuView();
        if (oldMenu != null) {
            int menuIndex = indexOfChild(oldMenu);
            removeView(oldMenu);
            this.mMenuRow.createMenu(this, this.mStatusBarNotification);
            this.mMenuRow.setAppName(this.mAppName);
            addView(this.mMenuRow.getMenuView(), menuIndex);
        }
        for (NotificationContentView l : this.mLayouts) {
            l.initView();
            l.reInflateViews();
        }
        this.mStatusBarNotification.clearPackageContext();
        this.mNotificationInflater.clearCachesAndReInflate();
    }

    @Override // android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        NotificationMenuRowPlugin notificationMenuRowPlugin = this.mMenuRow;
        if (notificationMenuRowPlugin != null && notificationMenuRowPlugin.getMenuView() != null) {
            this.mMenuRow.onConfigurationChanged();
        }
    }

    public void onUiModeChanged() {
        this.mUpdateBackgroundOnUpdate = true;
        reInflateViews();
        NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
        if (notificationChildrenContainer != null) {
            for (ExpandableNotificationRow child : notificationChildrenContainer.getNotificationChildren()) {
                child.onUiModeChanged();
            }
        }
    }

    public void setContentBackground(int customBackgroundColor, boolean animate, NotificationContentView notificationContentView) {
        if (getShowingLayout() == notificationContentView) {
            setTintColor(customBackgroundColor, animate);
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView
    protected void setBackgroundTintColor(int color) {
        super.setBackgroundTintColor(color);
        NotificationContentView view = getShowingLayout();
        if (view != null) {
            view.setBackgroundTintColor(color);
        }
    }

    public void closeRemoteInput() {
        NotificationContentView[] notificationContentViewArr;
        for (NotificationContentView l : this.mLayouts) {
            l.closeRemoteInput();
        }
    }

    public void setSingleLineWidthIndention(int indention) {
        this.mPrivateLayout.setSingleLineWidthIndention(indention);
    }

    public int getNotificationColor() {
        return this.mNotificationColor;
    }

    private void updateNotificationColor() {
        Configuration currentConfig = getResources().getConfiguration();
        boolean nightMode = (currentConfig.uiMode & 48) == 32;
        this.mNotificationColor = ContrastColorUtil.resolveContrastColor(this.mContext, getStatusBarNotification().getNotification().color, getBackgroundColorWithoutTint(), nightMode);
    }

    public HybridNotificationView getSingleLineView() {
        return this.mPrivateLayout.getSingleLineView();
    }

    public boolean isOnKeyguard() {
        return this.mOnKeyguard;
    }

    public void removeAllChildren() {
        List<ExpandableNotificationRow> notificationChildren = this.mChildrenContainer.getNotificationChildren();
        ArrayList<ExpandableNotificationRow> clonedList = new ArrayList<>(notificationChildren);
        for (int i = 0; i < clonedList.size(); i++) {
            ExpandableNotificationRow row = clonedList.get(i);
            if (!row.keepInParent()) {
                this.mChildrenContainer.removeNotification(row);
                row.setIsChildInGroup(false, null);
            }
        }
        onChildrenCountChanged();
    }

    public void setForceUnlocked(boolean forceUnlocked) {
        this.mForceUnlocked = forceUnlocked;
        if (this.mIsSummaryWithChildren) {
            List<ExpandableNotificationRow> notificationChildren = getNotificationChildren();
            for (ExpandableNotificationRow child : notificationChildren) {
                child.setForceUnlocked(forceUnlocked);
            }
        }
    }

    public void setDismissed(boolean fromAccessibility) {
        List<ExpandableNotificationRow> notificationChildren;
        int i;
        setLongPressListener(null);
        this.mDismissed = true;
        this.mGroupParentWhenDismissed = this.mNotificationParent;
        this.mRefocusOnDismiss = fromAccessibility;
        this.mChildAfterViewWhenDismissed = null;
        this.mEntry.icon.setDismissed();
        if (isChildInGroup() && (i = (notificationChildren = this.mNotificationParent.getNotificationChildren()).indexOf(this)) != -1 && i < notificationChildren.size() - 1) {
            this.mChildAfterViewWhenDismissed = notificationChildren.get(i + 1);
        }
    }

    public boolean isDismissed() {
        return this.mDismissed;
    }

    public boolean keepInParent() {
        return this.mKeepInParent;
    }

    public void setKeepInParent(boolean keepInParent) {
        this.mKeepInParent = keepInParent;
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public boolean isRemoved() {
        return this.mRemoved;
    }

    public void setRemoved() {
        NotificationContentView[] notificationContentViewArr;
        this.mRemoved = true;
        this.mTranslationWhenRemoved = getTranslationY();
        this.mWasChildInGroupWhenRemoved = isChildInGroup();
        if (isChildInGroup()) {
            this.mTranslationWhenRemoved += getNotificationParent().getTranslationY();
        }
        for (NotificationContentView l : this.mLayouts) {
            l.setRemoved();
        }
    }

    public boolean wasChildInGroupWhenRemoved() {
        return this.mWasChildInGroupWhenRemoved;
    }

    public float getTranslationWhenRemoved() {
        return this.mTranslationWhenRemoved;
    }

    public NotificationChildrenContainer getChildrenContainer() {
        return this.mChildrenContainer;
    }

    public void setHeadsUpAnimatingAway(boolean headsUpAnimatingAway) {
        Consumer<Boolean> consumer;
        boolean wasAboveShelf = isAboveShelf();
        boolean changed = headsUpAnimatingAway != this.mHeadsupDisappearRunning;
        this.mHeadsupDisappearRunning = headsUpAnimatingAway;
        this.mPrivateLayout.setHeadsUpAnimatingAway(headsUpAnimatingAway);
        if (changed && (consumer = this.mHeadsUpAnimatingAwayListener) != null) {
            consumer.accept(Boolean.valueOf(headsUpAnimatingAway));
        }
        if (isAboveShelf() != wasAboveShelf) {
            this.mAboveShelfChangedListener.onAboveShelfStateChanged(!wasAboveShelf);
        }
    }

    public void setHeadsUpAnimatingAwayListener(Consumer<Boolean> listener) {
        this.mHeadsUpAnimatingAwayListener = listener;
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView
    public boolean isHeadsUpAnimatingAway() {
        return this.mHeadsupDisappearRunning;
    }

    public View getChildAfterViewWhenDismissed() {
        return this.mChildAfterViewWhenDismissed;
    }

    public View getGroupParentWhenDismissed() {
        return this.mGroupParentWhenDismissed;
    }

    public boolean performDismissWithBlockingHelper(boolean fromAccessibility) {
        NotificationBlockingHelperManager manager = (NotificationBlockingHelperManager) Dependency.get(NotificationBlockingHelperManager.class);
        boolean isBlockingHelperShown = manager.perhapsShowBlockingHelper(this, this.mMenuRow);
        ((MetricsLogger) Dependency.get(MetricsLogger.class)).count(NotificationCounters.NOTIFICATION_DISMISSED, 1);
        performDismiss(fromAccessibility);
        return isBlockingHelperShown;
    }

    public void performDismiss(boolean fromAccessibility) {
        Runnable runnable;
        if (isOnlyChildInGroup()) {
            NotificationEntry groupSummary = this.mGroupManager.getLogicalGroupSummary(getStatusBarNotification());
            if (groupSummary.isClearable()) {
                groupSummary.getRow().performDismiss(fromAccessibility);
            }
        }
        setDismissed(fromAccessibility);
        if (this.mEntry.isClearable() && (runnable = this.mOnDismissRunnable) != null) {
            runnable.run();
        }
    }

    public void setBlockingHelperShowing(boolean isBlockingHelperShowing) {
        this.mIsBlockingHelperShowing = isBlockingHelperShowing;
    }

    public boolean isBlockingHelperShowing() {
        return this.mIsBlockingHelperShowing;
    }

    public boolean isBlockingHelperShowingAndTranslationFinished() {
        return this.mIsBlockingHelperShowing && this.mNotificationTranslationFinished;
    }

    public void setOnDismissRunnable(Runnable onDismissRunnable) {
        this.mOnDismissRunnable = onDismissRunnable;
    }

    public View getNotificationIcon() {
        NotificationHeaderView notificationHeader = getVisibleNotificationHeader();
        if (notificationHeader != null) {
            return notificationHeader.getIcon();
        }
        return null;
    }

    public boolean isShowingIcon() {
        return (areGutsExposed() || getVisibleNotificationHeader() == null) ? false : true;
    }

    public void setContentTransformationAmount(float contentTransformationAmount, boolean isLastChild) {
        boolean changeTransformation = isLastChild != this.mIsLastChild;
        boolean changeTransformation2 = changeTransformation | (this.mContentTransformationAmount != contentTransformationAmount);
        this.mIsLastChild = isLastChild;
        this.mContentTransformationAmount = contentTransformationAmount;
        if (changeTransformation2) {
            updateContentTransformation();
        }
    }

    public void setIconsVisible(boolean iconsVisible) {
        if (iconsVisible != this.mIconsVisible) {
            this.mIconsVisible = iconsVisible;
            updateIconVisibilities();
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView
    protected void onBelowSpeedBumpChanged() {
        updateIconVisibilities();
    }

    private void updateContentTransformation() {
        float contentAlpha;
        NotificationContentView[] notificationContentViewArr;
        if (this.mExpandAnimationRunning) {
            return;
        }
        float f = this.mContentTransformationAmount;
        float translationY = (-f) * this.mIconTransformContentShift;
        if (this.mIsLastChild) {
            float contentAlpha2 = 1.0f - f;
            contentAlpha = Interpolators.ALPHA_OUT.getInterpolation(Math.min(contentAlpha2 / 0.5f, 1.0f));
            translationY *= 0.4f;
        } else {
            contentAlpha = 1.0f;
        }
        for (NotificationContentView l : this.mLayouts) {
            l.setAlpha(contentAlpha);
            l.setTranslationY(translationY);
        }
        NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
        if (notificationChildrenContainer != null) {
            notificationChildrenContainer.setAlpha(contentAlpha);
            this.mChildrenContainer.setTranslationY(translationY);
        }
    }

    private void updateIconVisibilities() {
        NotificationContentView[] notificationContentViewArr;
        boolean visible = isChildInGroup() || this.mIconsVisible;
        for (NotificationContentView l : this.mLayouts) {
            l.setIconsVisible(visible);
        }
        NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
        if (notificationChildrenContainer != null) {
            notificationChildrenContainer.setIconsVisible(visible);
        }
    }

    public int getRelativeTopPadding(View view) {
        int topPadding = 0;
        while (view.getParent() instanceof ViewGroup) {
            topPadding += view.getTop();
            view = (View) view.getParent();
            if (view instanceof ExpandableNotificationRow) {
                return topPadding;
            }
        }
        return topPadding;
    }

    public float getContentTranslation() {
        return this.mPrivateLayout.getTranslationY();
    }

    public void setIsLowPriority(boolean isLowPriority) {
        this.mIsLowPriority = isLowPriority;
        this.mPrivateLayout.setIsLowPriority(isLowPriority);
        this.mNotificationInflater.setIsLowPriority(this.mIsLowPriority);
        NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
        if (notificationChildrenContainer != null) {
            notificationChildrenContainer.setIsLowPriority(isLowPriority);
        }
    }

    public boolean isLowPriority() {
        return this.mIsLowPriority;
    }

    public void setUseIncreasedCollapsedHeight(boolean use) {
        this.mUseIncreasedCollapsedHeight = use;
        this.mNotificationInflater.setUsesIncreasedHeight(use);
    }

    public void setUseIncreasedHeadsUpHeight(boolean use) {
        this.mUseIncreasedHeadsUpHeight = use;
        this.mNotificationInflater.setUsesIncreasedHeadsUpHeight(use);
    }

    public void setRemoteViewClickHandler(RemoteViews.OnClickHandler remoteViewClickHandler) {
        this.mNotificationInflater.setRemoteViewClickHandler(remoteViewClickHandler);
    }

    public void setInflationCallback(NotificationContentInflater.InflationCallback callback) {
        this.mNotificationInflater.setInflationCallback(callback);
    }

    public void setNeedsRedaction(boolean needsRedaction) {
        if (this.mNeedsRedaction != needsRedaction) {
            this.mNeedsRedaction = needsRedaction;
            updateInflationFlag(8, needsRedaction);
            this.mNotificationInflater.updateNeedsRedaction(needsRedaction);
            if (!needsRedaction) {
                freeContentViewWhenSafe(8);
            }
        }
    }

    @VisibleForTesting
    public NotificationContentInflater getNotificationInflater() {
        return this.mNotificationInflater;
    }

    public ExpandableNotificationRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mNotificationTranslationFinished = false;
        this.mHeaderVisibleAmount = 1.0f;
        this.mLastChronometerRunning = true;
        this.mExpandClickListener = new View.OnClickListener() { // from class: com.android.systemui.statusbar.notification.row.ExpandableNotificationRow.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                boolean nowExpanded;
                if (ExpandableNotificationRow.this.shouldShowPublic() || ((ExpandableNotificationRow.this.mIsLowPriority && !ExpandableNotificationRow.this.isExpanded()) || !ExpandableNotificationRow.this.mGroupManager.isSummaryOfGroup(ExpandableNotificationRow.this.mStatusBarNotification))) {
                    if (ExpandableNotificationRow.this.mEnableNonGroupedNotificationExpand) {
                        if (v.isAccessibilityFocused()) {
                            ExpandableNotificationRow.this.mPrivateLayout.setFocusOnVisibilityChange();
                        }
                        if (ExpandableNotificationRow.this.isPinned()) {
                            nowExpanded = !ExpandableNotificationRow.this.mExpandedWhenPinned;
                            ExpandableNotificationRow.this.mExpandedWhenPinned = nowExpanded;
                        } else {
                            nowExpanded = !ExpandableNotificationRow.this.isExpanded();
                            ExpandableNotificationRow.this.setUserExpanded(nowExpanded);
                        }
                        ExpandableNotificationRow.this.notifyHeightChanged(true);
                        ExpandableNotificationRow.this.mOnExpandClickListener.onExpandClicked(ExpandableNotificationRow.this.mEntry, nowExpanded);
                        MetricsLogger.action(ExpandableNotificationRow.this.mContext, (int) HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED, nowExpanded);
                        return;
                    }
                    return;
                }
                ExpandableNotificationRow.this.mGroupExpansionChanging = true;
                boolean wasExpanded = ExpandableNotificationRow.this.mGroupManager.isGroupExpanded(ExpandableNotificationRow.this.mStatusBarNotification);
                boolean nowExpanded2 = ExpandableNotificationRow.this.mGroupManager.toggleGroupExpansion(ExpandableNotificationRow.this.mStatusBarNotification);
                ExpandableNotificationRow.this.mOnExpandClickListener.onExpandClicked(ExpandableNotificationRow.this.mEntry, nowExpanded2);
                MetricsLogger.action(ExpandableNotificationRow.this.mContext, (int) HttpStatus.SC_REQUEST_TIMEOUT, nowExpanded2);
                ExpandableNotificationRow.this.onExpansionChanged(true, wasExpanded);
            }
        };
        this.mIconsVisible = true;
        this.mSystemNotificationAsyncTask = new SystemNotificationAsyncTask();
        this.mExpireRecentlyAlertedFlag = new Runnable() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$ExpandableNotificationRow$i9dza2pC3is4TOlr6JzMO8_yGSI
            @Override // java.lang.Runnable
            public final void run() {
                ExpandableNotificationRow.this.lambda$new$1$ExpandableNotificationRow();
            }
        };
        this.mFalsingManager = (FalsingManager) Dependency.get(FalsingManager.class);
        this.mNotificationInflater = new NotificationContentInflater(this);
        this.mMenuRow = new NotificationMenuRow(this.mContext);
        this.mImageResolver = new NotificationInlineImageResolver(context, new NotificationInlineImageCache());
        this.mMediaManager = (NotificationMediaManager) Dependency.get(NotificationMediaManager.class);
        initDimens();
    }

    public void setBypassController(KeyguardBypassController bypassController) {
        this.mBypassController = bypassController;
    }

    public void setStatusBarStateController(StatusBarStateController statusBarStateController) {
        this.mStatusbarStateController = statusBarStateController;
    }

    private void initDimens() {
        this.mNotificationMinHeightBeforeN = NotificationUtils.getFontScaledHeight(this.mContext, R.dimen.notification_min_height_legacy);
        this.mNotificationMinHeightBeforeP = NotificationUtils.getFontScaledHeight(this.mContext, R.dimen.notification_min_height_before_p);
        this.mNotificationMinHeight = NotificationUtils.getFontScaledHeight(this.mContext, R.dimen.notification_min_height);
        this.mNotificationMinHeightLarge = NotificationUtils.getFontScaledHeight(this.mContext, R.dimen.notification_min_height_increased);
        this.mNotificationMinHeightMedia = NotificationUtils.getFontScaledHeight(this.mContext, R.dimen.notification_min_height_media);
        this.mNotificationMaxHeight = NotificationUtils.getFontScaledHeight(this.mContext, R.dimen.notification_max_height);
        this.mMaxHeadsUpHeightBeforeN = NotificationUtils.getFontScaledHeight(this.mContext, R.dimen.notification_max_heads_up_height_legacy);
        this.mMaxHeadsUpHeightBeforeP = NotificationUtils.getFontScaledHeight(this.mContext, R.dimen.notification_max_heads_up_height_before_p);
        this.mMaxHeadsUpHeight = NotificationUtils.getFontScaledHeight(this.mContext, R.dimen.notification_max_heads_up_height);
        this.mMaxHeadsUpHeightIncreased = NotificationUtils.getFontScaledHeight(this.mContext, R.dimen.notification_max_heads_up_height_increased);
        Resources res = getResources();
        this.mIncreasedPaddingBetweenElements = res.getDimensionPixelSize(R.dimen.notification_divider_height_increased);
        this.mIconTransformContentShiftNoIcon = res.getDimensionPixelSize(R.dimen.notification_icon_transform_content_shift);
        this.mEnableNonGroupedNotificationExpand = res.getBoolean(R.bool.config_enableNonGroupedNotificationExpand);
        this.mShowGroupBackgroundWhenExpanded = res.getBoolean(R.bool.config_showGroupNotificationBgWhenExpanded);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public NotificationInlineImageResolver getImageResolver() {
        return this.mImageResolver;
    }

    public void reset() {
        this.mShowingPublicInitialized = false;
        onHeightReset();
        requestLayout();
    }

    public void showAppOpsIcons(ArraySet<Integer> activeOps) {
        if (this.mIsSummaryWithChildren && this.mChildrenContainer.getHeaderView() != null) {
            this.mChildrenContainer.getHeaderView().showAppOpsIcons(activeOps);
        }
        this.mPrivateLayout.showAppOpsIcons(activeOps);
        this.mPublicLayout.showAppOpsIcons(activeOps);
    }

    public void setLastAudiblyAlertedMs(long lastAudiblyAlertedMs) {
        if (NotificationUtils.useNewInterruptionModel(this.mContext)) {
            long timeSinceAlertedAudibly = System.currentTimeMillis() - lastAudiblyAlertedMs;
            boolean alertedRecently = timeSinceAlertedAudibly < RECENTLY_ALERTED_THRESHOLD_MS;
            applyAudiblyAlertedRecently(alertedRecently);
            removeCallbacks(this.mExpireRecentlyAlertedFlag);
            if (alertedRecently) {
                long timeUntilNoLongerRecent = RECENTLY_ALERTED_THRESHOLD_MS - timeSinceAlertedAudibly;
                postDelayed(this.mExpireRecentlyAlertedFlag, timeUntilNoLongerRecent);
            }
        }
    }

    public /* synthetic */ void lambda$new$1$ExpandableNotificationRow() {
        applyAudiblyAlertedRecently(false);
    }

    private void applyAudiblyAlertedRecently(boolean audiblyAlertedRecently) {
        if (this.mIsSummaryWithChildren && this.mChildrenContainer.getHeaderView() != null) {
            this.mChildrenContainer.getHeaderView().setRecentlyAudiblyAlerted(audiblyAlertedRecently);
        }
        this.mPrivateLayout.setRecentlyAudiblyAlerted(audiblyAlertedRecently);
        this.mPublicLayout.setRecentlyAudiblyAlerted(audiblyAlertedRecently);
    }

    public View.OnClickListener getAppOpsOnClickListener() {
        return this.mOnAppOpsClickListener;
    }

    public void setAppOpsOnClickListener(final OnAppOpsClickListener l) {
        this.mOnAppOpsClickListener = new View.OnClickListener() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$ExpandableNotificationRow$pZQ5iMMRBs0QlgeqJrDmlq2VhEQ
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ExpandableNotificationRow.this.lambda$setAppOpsOnClickListener$2$ExpandableNotificationRow(l, view);
            }
        };
    }

    public /* synthetic */ void lambda$setAppOpsOnClickListener$2$ExpandableNotificationRow(OnAppOpsClickListener l, View v) {
        NotificationMenuRowPlugin.MenuItem menuItem;
        createMenu();
        NotificationMenuRowPlugin provider = getProvider();
        if (provider != null && (menuItem = provider.getAppOpsMenuItem(this.mContext)) != null) {
            l.onClick(this, v.getWidth() / 2, v.getHeight() / 2, menuItem);
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView, android.view.View
    protected void onFinishInflate() {
        NotificationContentView[] notificationContentViewArr;
        super.onFinishInflate();
        this.mPublicLayout = (NotificationContentView) findViewById(R.id.expandedPublic);
        this.mPrivateLayout = (NotificationContentView) findViewById(R.id.expanded);
        this.mLayouts = new NotificationContentView[]{this.mPrivateLayout, this.mPublicLayout};
        for (NotificationContentView l : this.mLayouts) {
            l.setExpandClickListener(this.mExpandClickListener);
            l.setContainingNotification(this);
        }
        this.mGutsStub = (ViewStub) findViewById(R.id.notification_guts_stub);
        this.mGutsStub.setOnInflateListener(new ViewStub.OnInflateListener() { // from class: com.android.systemui.statusbar.notification.row.ExpandableNotificationRow.3
            @Override // android.view.ViewStub.OnInflateListener
            public void onInflate(ViewStub stub, View inflated) {
                ExpandableNotificationRow.this.mGuts = (NotificationGuts) inflated;
                ExpandableNotificationRow.this.mGuts.setClipTopAmount(ExpandableNotificationRow.this.getClipTopAmount());
                ExpandableNotificationRow.this.mGuts.setActualHeight(ExpandableNotificationRow.this.getActualHeight());
                ExpandableNotificationRow.this.mGutsStub = null;
            }
        });
        this.mChildrenContainerStub = (ViewStub) findViewById(R.id.child_container_stub);
        this.mChildrenContainerStub.setOnInflateListener(new ViewStub.OnInflateListener() { // from class: com.android.systemui.statusbar.notification.row.ExpandableNotificationRow.4
            @Override // android.view.ViewStub.OnInflateListener
            public void onInflate(ViewStub stub, View inflated) {
                ExpandableNotificationRow.this.mChildrenContainer = (NotificationChildrenContainer) inflated;
                ExpandableNotificationRow.this.mChildrenContainer.setIsLowPriority(ExpandableNotificationRow.this.mIsLowPriority);
                ExpandableNotificationRow.this.mChildrenContainer.setContainingNotification(ExpandableNotificationRow.this);
                ExpandableNotificationRow.this.mChildrenContainer.onNotificationUpdated();
                if (ExpandableNotificationRow.this.mShouldTranslateContents) {
                    ExpandableNotificationRow.this.mTranslateableViews.add(ExpandableNotificationRow.this.mChildrenContainer);
                }
            }
        });
        if (this.mShouldTranslateContents) {
            this.mTranslateableViews = new ArrayList<>();
            for (int i = 0; i < getChildCount(); i++) {
                this.mTranslateableViews.add(getChildAt(i));
            }
            this.mTranslateableViews.remove(this.mChildrenContainerStub);
            this.mTranslateableViews.remove(this.mGutsStub);
        }
    }

    private void doLongClickCallback() {
        doLongClickCallback(getWidth() / 2, getHeight() / 2);
    }

    public void doLongClickCallback(int x, int y) {
        createMenu();
        NotificationMenuRowPlugin provider = getProvider();
        NotificationMenuRowPlugin.MenuItem menuItem = null;
        if (provider != null) {
            menuItem = provider.getLongpressMenuItem(this.mContext);
        }
        doLongClickCallback(x, y, menuItem);
    }

    private void doLongClickCallback(int x, int y, NotificationMenuRowPlugin.MenuItem menuItem) {
        LongPressListener longPressListener = this.mLongPressListener;
        if (longPressListener != null && menuItem != null) {
            longPressListener.onLongPress(this, x, y, menuItem);
        }
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.isConfirmKey(keyCode)) {
            event.startTracking();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (KeyEvent.isConfirmKey(keyCode)) {
            if (!event.isCanceled()) {
                performClick();
                return true;
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (KeyEvent.isConfirmKey(keyCode)) {
            doLongClickCallback();
            return true;
        }
        return false;
    }

    public void resetTranslation() {
        Animator animator = this.mTranslateAnim;
        if (animator != null) {
            animator.cancel();
        }
        if (!this.mShouldTranslateContents) {
            setTranslationX(0.0f);
        } else if (this.mTranslateableViews != null) {
            for (int i = 0; i < this.mTranslateableViews.size(); i++) {
                this.mTranslateableViews.get(i).setTranslationX(0.0f);
            }
            invalidateOutline();
            getEntry().expandedIcon.setScrollX(0);
        }
        NotificationMenuRowPlugin notificationMenuRowPlugin = this.mMenuRow;
        if (notificationMenuRowPlugin != null) {
            notificationMenuRowPlugin.resetMenu();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onGutsOpened() {
        resetTranslation();
        updateContentAccessibilityImportanceForGuts(false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onGutsClosed() {
        updateContentAccessibilityImportanceForGuts(true);
    }

    private void updateContentAccessibilityImportanceForGuts(boolean isEnabled) {
        NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
        if (notificationChildrenContainer != null) {
            updateChildAccessibilityImportance(notificationChildrenContainer, isEnabled);
        }
        View[] viewArr = this.mLayouts;
        if (viewArr != null) {
            for (View view : viewArr) {
                updateChildAccessibilityImportance(view, isEnabled);
            }
        }
        if (isEnabled) {
            requestAccessibilityFocus();
        }
    }

    private void updateChildAccessibilityImportance(View childView, boolean isEnabled) {
        int i;
        if (isEnabled) {
            i = 0;
        } else {
            i = 4;
        }
        childView.setImportantForAccessibility(i);
    }

    public CharSequence getActiveRemoteInputText() {
        return this.mPrivateLayout.getActiveRemoteInputText();
    }

    public void animateTranslateNotification(float leftTarget) {
        Animator animator = this.mTranslateAnim;
        if (animator != null) {
            animator.cancel();
        }
        this.mTranslateAnim = getTranslateViewAnimator(leftTarget, null);
        Animator animator2 = this.mTranslateAnim;
        if (animator2 != null) {
            animator2.start();
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public void setTranslation(float translationX) {
        if (isBlockingHelperShowingAndTranslationFinished()) {
            this.mGuts.setTranslationX(translationX);
            return;
        }
        if (!this.mShouldTranslateContents) {
            setTranslationX(translationX);
        } else if (this.mTranslateableViews != null) {
            for (int i = 0; i < this.mTranslateableViews.size(); i++) {
                if (this.mTranslateableViews.get(i) != null) {
                    this.mTranslateableViews.get(i).setTranslationX(translationX);
                }
            }
            invalidateOutline();
            getEntry().expandedIcon.setScrollX((int) (-translationX));
        }
        NotificationMenuRowPlugin notificationMenuRowPlugin = this.mMenuRow;
        if (notificationMenuRowPlugin != null && notificationMenuRowPlugin.getMenuView() != null) {
            this.mMenuRow.onParentTranslationUpdate(translationX);
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public float getTranslation() {
        if (!this.mShouldTranslateContents) {
            return getTranslationX();
        }
        if (isBlockingHelperShowingAndCanTranslate()) {
            return this.mGuts.getTranslationX();
        }
        ArrayList<View> arrayList = this.mTranslateableViews;
        if (arrayList != null && arrayList.size() > 0) {
            return this.mTranslateableViews.get(0).getTranslationX();
        }
        return 0.0f;
    }

    private boolean isBlockingHelperShowingAndCanTranslate() {
        return areGutsExposed() && this.mIsBlockingHelperShowing && this.mNotificationTranslationFinished;
    }

    public Animator getTranslateViewAnimator(final float leftTarget, ValueAnimator.AnimatorUpdateListener listener) {
        Animator animator = this.mTranslateAnim;
        if (animator != null) {
            animator.cancel();
        }
        ObjectAnimator translateAnim = ObjectAnimator.ofFloat(this, TRANSLATE_CONTENT, leftTarget);
        if (listener != null) {
            translateAnim.addUpdateListener(listener);
        }
        translateAnim.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.notification.row.ExpandableNotificationRow.5
            boolean cancelled = false;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator anim) {
                this.cancelled = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator anim) {
                if (ExpandableNotificationRow.this.mIsBlockingHelperShowing) {
                    ExpandableNotificationRow.this.mNotificationTranslationFinished = true;
                }
                if (!this.cancelled && leftTarget == 0.0f) {
                    if (ExpandableNotificationRow.this.mMenuRow != null) {
                        ExpandableNotificationRow.this.mMenuRow.resetMenu();
                    }
                    ExpandableNotificationRow.this.mTranslateAnim = null;
                }
            }
        });
        this.mTranslateAnim = translateAnim;
        return translateAnim;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void ensureGutsInflated() {
        if (this.mGuts == null) {
            this.mGutsStub.inflate();
        }
    }

    private void updateChildrenVisibility() {
        NotificationGuts notificationGuts;
        int i = 0;
        boolean hideContentWhileLaunching = this.mExpandAnimationRunning && (notificationGuts = this.mGuts) != null && notificationGuts.isExposed();
        this.mPrivateLayout.setVisibility((this.mShowingPublic || this.mIsSummaryWithChildren || hideContentWhileLaunching) ? 4 : 0);
        NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
        if (notificationChildrenContainer != null) {
            if (this.mShowingPublic || !this.mIsSummaryWithChildren || hideContentWhileLaunching) {
                i = 4;
            }
            notificationChildrenContainer.setVisibility(i);
        }
        updateLimits();
    }

    public boolean onRequestSendAccessibilityEventInternal(View child, AccessibilityEvent event) {
        if (super.onRequestSendAccessibilityEventInternal(child, event)) {
            AccessibilityEvent record = AccessibilityEvent.obtain();
            onInitializeAccessibilityEvent(record);
            dispatchPopulateAccessibilityEvent(record);
            event.appendRecord(record);
            return true;
        }
        return false;
    }

    public void applyExpandAnimationParams(ActivityLaunchAnimator.ExpandAnimationParameters params) {
        if (params == null) {
            return;
        }
        float zProgress = Interpolators.FAST_OUT_SLOW_IN.getInterpolation(params.getProgress(0L, 50L));
        float translationZ = MathUtils.lerp(params.getStartTranslationZ(), this.mNotificationLaunchHeight, zProgress);
        setTranslationZ(translationZ);
        float extraWidthForClipping = (params.getWidth() - getWidth()) + MathUtils.lerp(0.0f, this.mOutlineRadius * 2.0f, params.getProgress());
        setExtraWidthForClipping(extraWidthForClipping);
        int top = params.getTop();
        float interpolation = Interpolators.FAST_OUT_SLOW_IN.getInterpolation(params.getProgress());
        int startClipTopAmount = params.getStartClipTopAmount();
        ExpandableNotificationRow expandableNotificationRow = this.mNotificationParent;
        if (expandableNotificationRow != null) {
            float parentY = expandableNotificationRow.getTranslationY();
            top = (int) (top - parentY);
            this.mNotificationParent.setTranslationZ(translationZ);
            int parentStartClipTopAmount = params.getParentStartClipTopAmount();
            if (startClipTopAmount != 0) {
                int clipTopAmount = (int) MathUtils.lerp(parentStartClipTopAmount, parentStartClipTopAmount - startClipTopAmount, interpolation);
                this.mNotificationParent.setClipTopAmount(clipTopAmount);
            }
            this.mNotificationParent.setExtraWidthForClipping(extraWidthForClipping);
            float clipBottom = Math.max(params.getBottom(), (this.mNotificationParent.getActualHeight() + parentY) - this.mNotificationParent.getClipBottomAmount());
            float clipTop = Math.min(params.getTop(), parentY);
            int minimumHeightForClipping = (int) (clipBottom - clipTop);
            this.mNotificationParent.setMinimumHeightForClipping(minimumHeightForClipping);
        } else if (startClipTopAmount != 0) {
            int clipTopAmount2 = (int) MathUtils.lerp(startClipTopAmount, 0.0f, interpolation);
            setClipTopAmount(clipTopAmount2);
        }
        setTranslationY(top);
        setActualHeight(params.getHeight());
        this.mBackgroundNormal.setExpandAnimationParams(params);
    }

    public void setExpandAnimationRunning(boolean expandAnimationRunning) {
        View contentView;
        if (this.mIsSummaryWithChildren) {
            contentView = this.mChildrenContainer;
        } else {
            contentView = getShowingLayout();
        }
        NotificationGuts notificationGuts = this.mGuts;
        if (notificationGuts != null && notificationGuts.isExposed()) {
            contentView = this.mGuts;
        }
        if (expandAnimationRunning) {
            contentView.animate().alpha(0.0f).setDuration(67L).setInterpolator(Interpolators.ALPHA_OUT);
            setAboveShelf(true);
            this.mExpandAnimationRunning = true;
            getViewState().cancelAnimations(this);
            this.mNotificationLaunchHeight = AmbientState.getNotificationLaunchHeight(getContext());
        } else {
            this.mExpandAnimationRunning = false;
            setAboveShelf(isAboveShelf());
            NotificationGuts notificationGuts2 = this.mGuts;
            if (notificationGuts2 != null) {
                notificationGuts2.setAlpha(1.0f);
            }
            if (contentView != null) {
                contentView.setAlpha(1.0f);
            }
            setExtraWidthForClipping(0.0f);
            ExpandableNotificationRow expandableNotificationRow = this.mNotificationParent;
            if (expandableNotificationRow != null) {
                expandableNotificationRow.setExtraWidthForClipping(0.0f);
                this.mNotificationParent.setMinimumHeightForClipping(0);
            }
        }
        ExpandableNotificationRow expandableNotificationRow2 = this.mNotificationParent;
        if (expandableNotificationRow2 != null) {
            expandableNotificationRow2.setChildIsExpanding(this.mExpandAnimationRunning);
        }
        updateChildrenVisibility();
        updateClipping();
        this.mBackgroundNormal.setExpandAnimationRunning(expandAnimationRunning);
    }

    private void setChildIsExpanding(boolean isExpanding) {
        this.mChildIsExpanding = isExpanding;
        updateClipping();
        invalidate();
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public boolean hasExpandingChild() {
        return this.mChildIsExpanding;
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    protected boolean shouldClipToActualHeight() {
        return super.shouldClipToActualHeight() && !this.mExpandAnimationRunning;
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public boolean isExpandAnimationRunning() {
        return this.mExpandAnimationRunning;
    }

    @Override // android.view.View
    public boolean isSoundEffectsEnabled() {
        BooleanSupplier booleanSupplier;
        StatusBarStateController statusBarStateController = this.mStatusbarStateController;
        boolean mute = (statusBarStateController == null || !statusBarStateController.isDozing() || (booleanSupplier = this.mSecureStateProvider) == null || booleanSupplier.getAsBoolean()) ? false : true;
        return !mute && super.isSoundEffectsEnabled();
    }

    public boolean isExpandable() {
        if (!this.mIsSummaryWithChildren || shouldShowPublic()) {
            return this.mEnableNonGroupedNotificationExpand && this.mExpandable;
        }
        return !this.mChildrenExpanded;
    }

    public void setExpandable(boolean expandable) {
        this.mExpandable = expandable;
        this.mPrivateLayout.updateExpandButtons(isExpandable());
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public void setClipToActualHeight(boolean clipToActualHeight) {
        boolean z = false;
        super.setClipToActualHeight(clipToActualHeight || isUserLocked());
        NotificationContentView showingLayout = getShowingLayout();
        if (clipToActualHeight || isUserLocked()) {
            z = true;
        }
        showingLayout.setClipToActualHeight(z);
    }

    public boolean hasUserChangedExpansion() {
        return this.mHasUserChangedExpansion;
    }

    public boolean isUserExpanded() {
        return this.mUserExpanded;
    }

    public void setUserExpanded(boolean userExpanded) {
        setUserExpanded(userExpanded, false);
    }

    public void setUserExpanded(boolean userExpanded, boolean allowChildExpansion) {
        this.mFalsingManager.setNotificationExpanded();
        if (this.mIsSummaryWithChildren && !shouldShowPublic() && allowChildExpansion && !this.mChildrenContainer.showingAsLowPriority()) {
            boolean wasExpanded = this.mGroupManager.isGroupExpanded(this.mStatusBarNotification);
            this.mGroupManager.setGroupExpanded(this.mStatusBarNotification, userExpanded);
            onExpansionChanged(true, wasExpanded);
        } else if (!userExpanded || this.mExpandable) {
            boolean wasExpanded2 = isExpanded();
            this.mHasUserChangedExpansion = true;
            this.mUserExpanded = userExpanded;
            onExpansionChanged(true, wasExpanded2);
            if (!wasExpanded2 && isExpanded() && getActualHeight() != getIntrinsicHeight()) {
                notifyHeightChanged(true);
            }
        }
    }

    public void resetUserExpansion() {
        boolean wasExpanded = isExpanded();
        this.mHasUserChangedExpansion = false;
        this.mUserExpanded = false;
        if (wasExpanded != isExpanded()) {
            if (this.mIsSummaryWithChildren) {
                this.mChildrenContainer.onExpansionChanged();
            }
            notifyHeightChanged(false);
        }
        updateShelfIconColor();
    }

    public boolean isUserLocked() {
        return this.mUserLocked && !this.mForceUnlocked;
    }

    public void setUserLocked(boolean userLocked) {
        this.mUserLocked = userLocked;
        this.mPrivateLayout.setUserExpanding(userLocked);
        NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
        if (notificationChildrenContainer != null) {
            notificationChildrenContainer.setUserLocked(userLocked);
            if (this.mIsSummaryWithChildren) {
                if (userLocked || !isGroupExpanded()) {
                    updateBackgroundForGroupState();
                }
            }
        }
    }

    public boolean isSystemExpanded() {
        return this.mIsSystemExpanded;
    }

    public void setSystemExpanded(boolean expand) {
        if (expand != this.mIsSystemExpanded) {
            boolean wasExpanded = isExpanded();
            this.mIsSystemExpanded = expand;
            notifyHeightChanged(false);
            onExpansionChanged(false, wasExpanded);
            if (this.mIsSummaryWithChildren) {
                this.mChildrenContainer.updateGroupOverflow();
            }
        }
    }

    public void setOnKeyguard(boolean onKeyguard) {
        if (onKeyguard != this.mOnKeyguard) {
            boolean wasAboveShelf = isAboveShelf();
            boolean wasExpanded = isExpanded();
            this.mOnKeyguard = onKeyguard;
            onExpansionChanged(false, wasExpanded);
            if (wasExpanded != isExpanded()) {
                if (this.mIsSummaryWithChildren) {
                    this.mChildrenContainer.updateGroupOverflow();
                }
                notifyHeightChanged(false);
            }
            if (isAboveShelf() != wasAboveShelf) {
                this.mAboveShelfChangedListener.onAboveShelfStateChanged(!wasAboveShelf);
            }
        }
        updateRippleAllowed();
    }

    private void updateRippleAllowed() {
        boolean allowed = isOnKeyguard() || this.mEntry.notification.getNotification().contentIntent == null;
        setRippleAllowed(allowed);
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public int getIntrinsicHeight() {
        if (isUserLocked()) {
            return getActualHeight();
        }
        NotificationGuts notificationGuts = this.mGuts;
        if (notificationGuts != null && notificationGuts.isExposed()) {
            return this.mGuts.getIntrinsicHeight();
        }
        if (isChildInGroup() && !isGroupExpanded()) {
            return this.mPrivateLayout.getMinHeight();
        }
        if (this.mSensitive && this.mHideSensitiveForIntrinsicHeight) {
            return getMinHeight();
        }
        if (this.mIsSummaryWithChildren) {
            return this.mChildrenContainer.getIntrinsicHeight();
        }
        if (canShowHeadsUp() && isHeadsUpState()) {
            if (isPinned() || this.mHeadsupDisappearRunning) {
                return getPinnedHeadsUpHeight(true);
            }
            if (isExpanded()) {
                return Math.max(getMaxExpandHeight(), getHeadsUpHeight());
            }
            return Math.max(getCollapsedHeight(), getHeadsUpHeight());
        } else if (isExpanded()) {
            return getMaxExpandHeight();
        } else {
            return getCollapsedHeight();
        }
    }

    public boolean canShowHeadsUp() {
        if (this.mOnKeyguard && !isDozing() && !isBypassEnabled()) {
            return false;
        }
        return true;
    }

    private boolean isBypassEnabled() {
        KeyguardBypassController keyguardBypassController = this.mBypassController;
        return keyguardBypassController == null || keyguardBypassController.getBypassEnabled();
    }

    private boolean isDozing() {
        StatusBarStateController statusBarStateController = this.mStatusbarStateController;
        return statusBarStateController != null && statusBarStateController.isDozing();
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public boolean isGroupExpanded() {
        return this.mGroupManager.isGroupExpanded(this.mStatusBarNotification);
    }

    private void onChildrenCountChanged() {
        NotificationChildrenContainer notificationChildrenContainer;
        this.mIsSummaryWithChildren = StatusBar.ENABLE_CHILD_NOTIFICATIONS && (notificationChildrenContainer = this.mChildrenContainer) != null && notificationChildrenContainer.getNotificationChildCount() > 0;
        if (this.mIsSummaryWithChildren && this.mChildrenContainer.getHeaderView() == null) {
            this.mChildrenContainer.recreateNotificationHeader(this.mExpandClickListener);
        }
        getShowingLayout().updateBackgroundColor(false);
        this.mPrivateLayout.updateExpandButtons(isExpandable());
        updateChildrenHeaderAppearance();
        updateChildrenVisibility();
        applyChildrenRoundness();
    }

    public int getNumUniqueChannels() {
        return getUniqueChannels().size();
    }

    public ArraySet<NotificationChannel> getUniqueChannels() {
        ArraySet<NotificationChannel> channels = new ArraySet<>();
        channels.add(this.mEntry.channel);
        if (this.mIsSummaryWithChildren) {
            List<ExpandableNotificationRow> childrenRows = getNotificationChildren();
            int numChildren = childrenRows.size();
            for (int i = 0; i < numChildren; i++) {
                ExpandableNotificationRow childRow = childrenRows.get(i);
                NotificationChannel childChannel = childRow.getEntry().channel;
                StatusBarNotification childSbn = childRow.getStatusBarNotification();
                if (childSbn.getUser().equals(this.mStatusBarNotification.getUser()) && childSbn.getPackageName().equals(this.mStatusBarNotification.getPackageName())) {
                    channels.add(childChannel);
                }
            }
        }
        return channels;
    }

    public void updateChildrenHeaderAppearance() {
        if (this.mIsSummaryWithChildren) {
            this.mChildrenContainer.updateChildrenHeaderAppearance();
        }
    }

    public boolean isExpanded() {
        return isExpanded(false);
    }

    public boolean isExpanded(boolean allowOnKeyguard) {
        return (!this.mOnKeyguard || allowOnKeyguard) && ((!hasUserChangedExpansion() && (isSystemExpanded() || isSystemChildExpanded())) || isUserExpanded());
    }

    private boolean isSystemChildExpanded() {
        return this.mIsSystemChildExpanded;
    }

    public void setSystemChildExpanded(boolean expanded) {
        this.mIsSystemChildExpanded = expanded;
    }

    public void setLayoutListener(LayoutListener listener) {
        this.mLayoutListener = listener;
    }

    public void removeListener() {
        this.mLayoutListener = null;
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView, com.android.systemui.statusbar.notification.row.ExpandableView, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int intrinsicBefore = getIntrinsicHeight();
        super.onLayout(changed, left, top, right, bottom);
        if (intrinsicBefore != getIntrinsicHeight() && intrinsicBefore != 0) {
            notifyHeightChanged(true);
        }
        NotificationMenuRowPlugin notificationMenuRowPlugin = this.mMenuRow;
        if (notificationMenuRowPlugin != null && notificationMenuRowPlugin.getMenuView() != null) {
            this.mMenuRow.onParentHeightUpdate();
        }
        updateContentShiftHeight();
        LayoutListener layoutListener = this.mLayoutListener;
        if (layoutListener != null) {
            layoutListener.onLayout();
        }
    }

    private void updateContentShiftHeight() {
        NotificationHeaderView notificationHeader = getVisibleNotificationHeader();
        if (notificationHeader != null) {
            CachingIconView icon = notificationHeader.getIcon();
            this.mIconTransformContentShift = getRelativeTopPadding(icon) + icon.getHeight();
            return;
        }
        this.mIconTransformContentShift = this.mIconTransformContentShiftNoIcon;
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public void notifyHeightChanged(boolean needsAnimation) {
        super.notifyHeightChanged(needsAnimation);
        getShowingLayout().requestSelectLayout(needsAnimation || isUserLocked());
    }

    public void setSensitive(boolean sensitive, boolean hideSensitive) {
        this.mSensitive = sensitive;
        this.mSensitiveHiddenInGeneral = hideSensitive;
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public void setHideSensitiveForIntrinsicHeight(boolean hideSensitive) {
        this.mHideSensitiveForIntrinsicHeight = hideSensitive;
        if (this.mIsSummaryWithChildren) {
            List<ExpandableNotificationRow> notificationChildren = this.mChildrenContainer.getNotificationChildren();
            for (int i = 0; i < notificationChildren.size(); i++) {
                ExpandableNotificationRow child = notificationChildren.get(i);
                child.setHideSensitiveForIntrinsicHeight(hideSensitive);
            }
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public void setHideSensitive(boolean hideSensitive, boolean animated, long delay, long duration) {
        if (getVisibility() == 8) {
            return;
        }
        boolean oldShowingPublic = this.mShowingPublic;
        this.mShowingPublic = this.mSensitive && hideSensitive;
        if ((this.mShowingPublicInitialized && this.mShowingPublic == oldShowingPublic) || this.mPublicLayout.getChildCount() == 0) {
            return;
        }
        if (!animated) {
            this.mPublicLayout.animate().cancel();
            this.mPrivateLayout.animate().cancel();
            NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
            if (notificationChildrenContainer != null) {
                notificationChildrenContainer.animate().cancel();
                this.mChildrenContainer.setAlpha(1.0f);
            }
            this.mPublicLayout.setAlpha(1.0f);
            this.mPrivateLayout.setAlpha(1.0f);
            this.mPublicLayout.setVisibility(this.mShowingPublic ? 0 : 4);
            updateChildrenVisibility();
        } else {
            animateShowingPublic(delay, duration, this.mShowingPublic);
        }
        NotificationContentView showingLayout = getShowingLayout();
        showingLayout.updateBackgroundColor(animated);
        this.mPrivateLayout.updateExpandButtons(isExpandable());
        updateShelfIconColor();
        this.mShowingPublicInitialized = true;
    }

    private void animateShowingPublic(long delay, long duration, boolean showingPublic) {
        View[] privateViews = this.mIsSummaryWithChildren ? new View[]{this.mChildrenContainer} : new View[]{this.mPrivateLayout};
        View[] publicViews = {this.mPublicLayout};
        View[] hiddenChildren = showingPublic ? privateViews : publicViews;
        View[] shownChildren = showingPublic ? publicViews : privateViews;
        for (final View hiddenView : hiddenChildren) {
            hiddenView.setVisibility(0);
            hiddenView.animate().cancel();
            hiddenView.animate().alpha(0.0f).setStartDelay(delay).setDuration(duration).withEndAction(new Runnable() { // from class: com.android.systemui.statusbar.notification.row.ExpandableNotificationRow.6
                @Override // java.lang.Runnable
                public void run() {
                    hiddenView.setVisibility(4);
                }
            });
        }
        for (View showView : shownChildren) {
            showView.setVisibility(0);
            showView.setAlpha(0.0f);
            showView.animate().cancel();
            showView.animate().alpha(1.0f).setStartDelay(delay).setDuration(duration);
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public boolean mustStayOnScreen() {
        return this.mIsHeadsUp && this.mMustStayOnScreen;
    }

    public boolean canViewBeDismissed() {
        return this.mEntry.isClearable() && !(shouldShowPublic() && this.mSensitiveHiddenInGeneral);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean shouldShowPublic() {
        return this.mSensitive && this.mHideSensitiveForIntrinsicHeight;
    }

    public void makeActionsVisibile() {
        setUserExpanded(true, true);
        if (isChildInGroup()) {
            this.mGroupManager.setGroupExpanded(this.mStatusBarNotification, true);
        }
        notifyHeightChanged(false);
    }

    public void setChildrenExpanded(boolean expanded, boolean animate) {
        this.mChildrenExpanded = expanded;
        NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
        if (notificationChildrenContainer != null) {
            notificationChildrenContainer.setChildrenExpanded(expanded);
        }
        updateBackgroundForGroupState();
        updateClickAndFocus();
    }

    public static void applyTint(View v, int color) {
        int alpha;
        if (color != 0) {
            alpha = 123;
        } else {
            color = -16777216;
            alpha = 41;
        }
        if (v.getBackground() instanceof ColorDrawable) {
            ColorDrawable background = (ColorDrawable) v.getBackground();
            background.mutate();
            background.setColor(color);
            background.setAlpha(alpha);
        }
    }

    public int getMaxExpandHeight() {
        return this.mPrivateLayout.getExpandHeight();
    }

    private int getHeadsUpHeight() {
        return getShowingLayout().getHeadsUpHeight(false);
    }

    public boolean areGutsExposed() {
        NotificationGuts notificationGuts = this.mGuts;
        return notificationGuts != null && notificationGuts.isExposed();
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public boolean isContentExpandable() {
        if (this.mIsSummaryWithChildren && !shouldShowPublic()) {
            return true;
        }
        NotificationContentView showingLayout = getShowingLayout();
        return showingLayout.isContentExpandable();
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView
    protected View getContentView() {
        if (this.mIsSummaryWithChildren && !shouldShowPublic()) {
            return this.mChildrenContainer;
        }
        return getShowingLayout();
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView, com.android.systemui.statusbar.notification.row.ExpandableView
    public long performRemoveAnimation(final long duration, final long delay, final float translationDirection, final boolean isHeadsUpAnimation, final float endLocation, final Runnable onFinishedRunnable, final AnimatorListenerAdapter animationListener) {
        Animator anim;
        NotificationMenuRowPlugin notificationMenuRowPlugin = this.mMenuRow;
        if (notificationMenuRowPlugin != null && notificationMenuRowPlugin.isMenuVisible() && (anim = getTranslateViewAnimator(0.0f, null)) != null) {
            anim.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.notification.row.ExpandableNotificationRow.7
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    ExpandableNotificationRow.super.performRemoveAnimation(duration, delay, translationDirection, isHeadsUpAnimation, endLocation, onFinishedRunnable, animationListener);
                }
            });
            anim.start();
            return anim.getDuration();
        }
        return super.performRemoveAnimation(duration, delay, translationDirection, isHeadsUpAnimation, endLocation, onFinishedRunnable, animationListener);
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView
    protected void onAppearAnimationFinished(boolean wasAppearing) {
        NotificationContentView[] notificationContentViewArr;
        super.onAppearAnimationFinished(wasAppearing);
        if (wasAppearing) {
            NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
            if (notificationChildrenContainer != null) {
                notificationChildrenContainer.setAlpha(1.0f);
                this.mChildrenContainer.setLayerType(0, null);
            }
            for (NotificationContentView l : this.mLayouts) {
                l.setAlpha(1.0f);
                l.setLayerType(0, null);
            }
            return;
        }
        setHeadsUpAnimatingAway(false);
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public int getExtraBottomPadding() {
        if (this.mIsSummaryWithChildren && isGroupExpanded()) {
            return this.mIncreasedPaddingBetweenElements;
        }
        return 0;
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView, com.android.systemui.statusbar.notification.row.ExpandableOutlineView, com.android.systemui.statusbar.notification.row.ExpandableView
    public void setActualHeight(int height, boolean notifyListeners) {
        NotificationContentView[] notificationContentViewArr;
        ViewGroup parent;
        boolean changed = height != getActualHeight();
        super.setActualHeight(height, notifyListeners);
        if (changed && isRemoved() && (parent = (ViewGroup) getParent()) != null) {
            parent.invalidate();
        }
        NotificationGuts notificationGuts = this.mGuts;
        if (notificationGuts != null && notificationGuts.isExposed()) {
            this.mGuts.setActualHeight(height);
            return;
        }
        int contentHeight = Math.max(getMinHeight(), height);
        for (NotificationContentView l : this.mLayouts) {
            l.setContentHeight(contentHeight);
        }
        if (this.mIsSummaryWithChildren) {
            this.mChildrenContainer.setActualHeight(height);
        }
        NotificationGuts notificationGuts2 = this.mGuts;
        if (notificationGuts2 != null) {
            notificationGuts2.setActualHeight(height);
        }
        NotificationMenuRowPlugin notificationMenuRowPlugin = this.mMenuRow;
        if (notificationMenuRowPlugin != null && notificationMenuRowPlugin.getMenuView() != null) {
            this.mMenuRow.onParentHeightUpdate();
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public int getMaxContentHeight() {
        if (this.mIsSummaryWithChildren && !shouldShowPublic()) {
            return this.mChildrenContainer.getMaxContentHeight();
        }
        NotificationContentView showingLayout = getShowingLayout();
        return showingLayout.getMaxHeight();
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public int getMinHeight(boolean ignoreTemporaryStates) {
        NotificationGuts notificationGuts;
        if (!ignoreTemporaryStates && (notificationGuts = this.mGuts) != null && notificationGuts.isExposed()) {
            return this.mGuts.getIntrinsicHeight();
        }
        if (!ignoreTemporaryStates && canShowHeadsUp() && this.mIsHeadsUp && this.mHeadsUpManager.isTrackingHeadsUp()) {
            return getPinnedHeadsUpHeight(false);
        }
        if (this.mIsSummaryWithChildren && !isGroupExpanded() && !shouldShowPublic()) {
            return this.mChildrenContainer.getMinHeight();
        }
        if (!ignoreTemporaryStates && canShowHeadsUp() && this.mIsHeadsUp) {
            return getHeadsUpHeight();
        }
        NotificationContentView showingLayout = getShowingLayout();
        return showingLayout.getMinHeight();
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public int getCollapsedHeight() {
        if (this.mIsSummaryWithChildren && !shouldShowPublic()) {
            return this.mChildrenContainer.getCollapsedHeight();
        }
        return getMinHeight();
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView
    public int getHeadsUpHeightWithoutHeader() {
        if (!canShowHeadsUp() || !this.mIsHeadsUp) {
            return getCollapsedHeight();
        }
        if (this.mIsSummaryWithChildren && !shouldShowPublic()) {
            return this.mChildrenContainer.getCollapsedHeightWithoutHeader();
        }
        return getShowingLayout().getHeadsUpHeight(true);
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView, com.android.systemui.statusbar.notification.row.ExpandableOutlineView, com.android.systemui.statusbar.notification.row.ExpandableView
    public void setClipTopAmount(int clipTopAmount) {
        NotificationContentView[] notificationContentViewArr;
        super.setClipTopAmount(clipTopAmount);
        for (NotificationContentView l : this.mLayouts) {
            l.setClipTopAmount(clipTopAmount);
        }
        NotificationGuts notificationGuts = this.mGuts;
        if (notificationGuts != null) {
            notificationGuts.setClipTopAmount(clipTopAmount);
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView, com.android.systemui.statusbar.notification.row.ExpandableOutlineView, com.android.systemui.statusbar.notification.row.ExpandableView
    public void setClipBottomAmount(int clipBottomAmount) {
        NotificationContentView[] notificationContentViewArr;
        if (this.mExpandAnimationRunning) {
            return;
        }
        if (clipBottomAmount != this.mClipBottomAmount) {
            super.setClipBottomAmount(clipBottomAmount);
            for (NotificationContentView l : this.mLayouts) {
                l.setClipBottomAmount(clipBottomAmount);
            }
            NotificationGuts notificationGuts = this.mGuts;
            if (notificationGuts != null) {
                notificationGuts.setClipBottomAmount(clipBottomAmount);
            }
        }
        NotificationChildrenContainer notificationChildrenContainer = this.mChildrenContainer;
        if (notificationChildrenContainer != null && !this.mChildIsExpanding) {
            notificationChildrenContainer.setClipBottomAmount(clipBottomAmount);
        }
    }

    public NotificationContentView getShowingLayout() {
        return shouldShowPublic() ? this.mPublicLayout : this.mPrivateLayout;
    }

    public View getExpandedContentView() {
        return getPrivateLayout().getExpandedChild();
    }

    public void setLegacy(boolean legacy) {
        NotificationContentView[] notificationContentViewArr;
        for (NotificationContentView l : this.mLayouts) {
            l.setLegacy(legacy);
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView
    protected void updateBackgroundTint() {
        super.updateBackgroundTint();
        updateBackgroundForGroupState();
        if (this.mIsSummaryWithChildren) {
            List<ExpandableNotificationRow> notificationChildren = this.mChildrenContainer.getNotificationChildren();
            for (int i = 0; i < notificationChildren.size(); i++) {
                ExpandableNotificationRow child = notificationChildren.get(i);
                child.updateBackgroundForGroupState();
            }
        }
    }

    public void onFinishedExpansionChange() {
        this.mGroupExpansionChanging = false;
        updateBackgroundForGroupState();
    }

    public void updateBackgroundForGroupState() {
        if (this.mIsSummaryWithChildren) {
            this.mShowNoBackground = (this.mShowGroupBackgroundWhenExpanded || !isGroupExpanded() || isGroupExpansionChanging() || isUserLocked()) ? false : false;
            this.mChildrenContainer.updateHeaderForExpansion(this.mShowNoBackground);
            List<ExpandableNotificationRow> children = this.mChildrenContainer.getNotificationChildren();
            for (int i = 0; i < children.size(); i++) {
                children.get(i).updateBackgroundForGroupState();
            }
        } else if (isChildInGroup()) {
            int childColor = getShowingLayout().getBackgroundColorForExpansionState();
            boolean showBackground = isGroupExpanded() || ((this.mNotificationParent.isGroupExpansionChanging() || this.mNotificationParent.isUserLocked()) && childColor != 0);
            this.mShowNoBackground = showBackground ? false : true;
        } else {
            this.mShowNoBackground = false;
        }
        updateOutline();
        updateBackground();
    }

    public int getPositionOfChild(ExpandableNotificationRow childRow) {
        if (this.mIsSummaryWithChildren) {
            return this.mChildrenContainer.getPositionInLinearLayout(childRow);
        }
        return 0;
    }

    public void setExpansionLogger(ExpansionLogger logger, String key) {
        this.mLogger = logger;
        this.mLoggingKey = key;
    }

    public void onExpandedByGesture(boolean userExpanded) {
        int event = HttpStatus.SC_CONFLICT;
        if (this.mGroupManager.isSummaryOfGroup(getStatusBarNotification())) {
            event = HttpStatus.SC_GONE;
        }
        MetricsLogger.action(this.mContext, event, userExpanded);
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public float getIncreasedPaddingAmount() {
        if (this.mIsSummaryWithChildren) {
            if (isGroupExpanded()) {
                return 1.0f;
            }
            if (isUserLocked()) {
                return this.mChildrenContainer.getIncreasedPaddingAmount();
            }
            return 0.0f;
        } else if (isColorized()) {
            if (!this.mIsLowPriority || isExpanded()) {
                return -1.0f;
            }
            return 0.0f;
        } else {
            return 0.0f;
        }
    }

    private boolean isColorized() {
        return this.mIsColorized && this.mBgTint != 0;
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView
    protected boolean disallowSingleClick(MotionEvent event) {
        if (areGutsExposed()) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        NotificationHeaderView header = getVisibleNotificationHeader();
        if (header != null && header.isInTouchRect(x - getTranslation(), y)) {
            return true;
        }
        if ((!this.mIsSummaryWithChildren || shouldShowPublic()) && getShowingLayout().disallowSingleClick(x, y)) {
            return true;
        }
        return super.disallowSingleClick(event);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onExpansionChanged(boolean userAction, boolean wasExpanded) {
        boolean nowExpanded = isExpanded();
        if (this.mIsSummaryWithChildren && (!this.mIsLowPriority || wasExpanded)) {
            nowExpanded = this.mGroupManager.isGroupExpanded(this.mStatusBarNotification);
        }
        if (nowExpanded != wasExpanded) {
            updateShelfIconColor();
            ExpansionLogger expansionLogger = this.mLogger;
            if (expansionLogger != null) {
                expansionLogger.logNotificationExpansion(this.mLoggingKey, userAction, nowExpanded);
            }
            if (this.mIsSummaryWithChildren) {
                this.mChildrenContainer.onExpansionChanged();
            }
        }
    }

    public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfoInternal(info);
        info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_LONG_CLICK);
        if (canViewBeDismissed()) {
            info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_DISMISS);
        }
        boolean expandable = shouldShowPublic();
        boolean isExpanded = false;
        if (!expandable) {
            if (this.mIsSummaryWithChildren) {
                expandable = true;
                if (!this.mIsLowPriority || isExpanded()) {
                    isExpanded = isGroupExpanded();
                }
            } else {
                expandable = this.mPrivateLayout.isContentExpandable();
                isExpanded = isExpanded();
            }
        }
        if (expandable) {
            if (isExpanded) {
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_COLLAPSE);
            } else {
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_EXPAND);
            }
        }
        NotificationMenuRowPlugin provider = getProvider();
        if (provider != null) {
            NotificationMenuRowPlugin.MenuItem snoozeMenu = provider.getSnoozeMenuItem(getContext());
            if (snoozeMenu != null) {
                AccessibilityNodeInfo.AccessibilityAction action = new AccessibilityNodeInfo.AccessibilityAction(R.id.action_snooze, getContext().getResources().getString(R.string.notification_menu_snooze_action));
                info.addAction(action);
            }
        }
    }

    public boolean performAccessibilityActionInternal(int action, Bundle arguments) {
        if (super.performAccessibilityActionInternal(action, arguments)) {
            return true;
        }
        if (action == 32) {
            doLongClickCallback();
            return true;
        } else if (action == 262144 || action == 524288) {
            this.mExpandClickListener.onClick(this);
            return true;
        } else if (action == 1048576) {
            performDismissWithBlockingHelper(true);
            return true;
        } else if (action == R.id.action_snooze) {
            NotificationMenuRowPlugin provider = getProvider();
            if (provider != null || this.mMenuRow == null) {
                return false;
            }
            NotificationMenuRowPlugin provider2 = createMenu();
            NotificationMenuRowPlugin.MenuItem snoozeMenu = provider2.getSnoozeMenuItem(getContext());
            if (snoozeMenu != null) {
                doLongClickCallback(getWidth() / 2, getHeight() / 2, snoozeMenu);
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean shouldRefocusOnDismiss() {
        return this.mRefocusOnDismiss || isAccessibilityFocused();
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public ExpandableViewState createExpandableViewState() {
        return new NotificationViewState();
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public boolean isAboveShelf() {
        return canShowHeadsUp() && (this.mIsPinned || this.mHeadsupDisappearRunning || ((this.mIsHeadsUp && this.mAboveShelf) || this.mExpandAnimationRunning || this.mChildIsExpanding));
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableOutlineView
    public boolean topAmountNeedsClipping() {
        if (isGroupExpanded() || isGroupExpansionChanging() || getShowingLayout().shouldClipToRounding(true, false)) {
            return true;
        }
        NotificationGuts notificationGuts = this.mGuts;
        return (notificationGuts == null || notificationGuts.getAlpha() == 0.0f) ? false : true;
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView, com.android.systemui.statusbar.notification.row.ExpandableOutlineView
    protected boolean childNeedsClipping(View child) {
        if (child instanceof NotificationContentView) {
            NotificationContentView contentView = (NotificationContentView) child;
            if (isClippingNeeded()) {
                return true;
            }
            if (!hasNoRounding()) {
                if (contentView.shouldClipToRounding(getCurrentTopRoundness() != 0.0f, getCurrentBottomRoundness() != 0.0f)) {
                    return true;
                }
            }
        } else if (child == this.mChildrenContainer) {
            if (isClippingNeeded() || !hasNoRounding()) {
                return true;
            }
        } else if (child instanceof NotificationGuts) {
            return !hasNoRounding();
        }
        return super.childNeedsClipping(child);
    }

    @Override // com.android.systemui.statusbar.notification.row.ActivatableNotificationView, com.android.systemui.statusbar.notification.row.ExpandableOutlineView
    protected void applyRoundness() {
        super.applyRoundness();
        applyChildrenRoundness();
    }

    private void applyChildrenRoundness() {
        if (this.mIsSummaryWithChildren) {
            this.mChildrenContainer.setCurrentBottomRoundness(getCurrentBottomRoundness());
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableOutlineView
    public Path getCustomClipPath(View child) {
        if (child instanceof NotificationGuts) {
            return getClipPath(true);
        }
        return super.getCustomClipPath(child);
    }

    private boolean hasNoRounding() {
        return getCurrentBottomRoundness() == 0.0f && getCurrentTopRoundness() == 0.0f;
    }

    public boolean isMediaRow() {
        return (getExpandedContentView() == null || getExpandedContentView().findViewById(16909197) == null) ? false : true;
    }

    public boolean isTopLevelChild() {
        return getParent() instanceof NotificationStackScrollLayout;
    }

    public boolean isGroupNotFullyVisible() {
        return getClipTopAmount() > 0 || getTranslationY() < 0.0f;
    }

    public void setAboveShelf(boolean aboveShelf) {
        boolean wasAboveShelf = isAboveShelf();
        this.mAboveShelf = aboveShelf;
        if (isAboveShelf() != wasAboveShelf) {
            this.mAboveShelfChangedListener.onAboveShelfStateChanged(!wasAboveShelf);
        }
    }

    public void setDismissRtl(boolean dismissRtl) {
        NotificationMenuRowPlugin notificationMenuRowPlugin = this.mMenuRow;
        if (notificationMenuRowPlugin != null) {
            notificationMenuRowPlugin.setDismissRtl(dismissRtl);
        }
    }

    /* loaded from: classes21.dex */
    private static class NotificationViewState extends ExpandableViewState {
        private NotificationViewState() {
        }

        @Override // com.android.systemui.statusbar.notification.stack.ExpandableViewState, com.android.systemui.statusbar.notification.stack.ViewState
        public void applyToView(View view) {
            if (view instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow row = (ExpandableNotificationRow) view;
                if (row.isExpandAnimationRunning()) {
                    return;
                }
                handleFixedTranslationZ(row);
                super.applyToView(view);
                row.applyChildrenState();
            }
        }

        private void handleFixedTranslationZ(ExpandableNotificationRow row) {
            if (row.hasExpandingChild()) {
                this.zTranslation = row.getTranslationZ();
                this.clipTopAmount = row.getClipTopAmount();
            }
        }

        @Override // com.android.systemui.statusbar.notification.stack.ViewState
        protected void onYTranslationAnimationFinished(View view) {
            super.onYTranslationAnimationFinished(view);
            if (view instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow row = (ExpandableNotificationRow) view;
                if (row.isHeadsUpAnimatingAway()) {
                    row.setHeadsUpAnimatingAway(false);
                }
            }
        }

        @Override // com.android.systemui.statusbar.notification.stack.ExpandableViewState, com.android.systemui.statusbar.notification.stack.ViewState
        public void animateTo(View child, AnimationProperties properties) {
            if (child instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow row = (ExpandableNotificationRow) child;
                if (row.isExpandAnimationRunning()) {
                    return;
                }
                handleFixedTranslationZ(row);
                super.animateTo(child, properties);
                row.startChildAnimation(properties);
            }
        }
    }

    public InflatedSmartReplies.SmartRepliesAndActions getExistingSmartRepliesAndActions() {
        return this.mPrivateLayout.getCurrentSmartRepliesAndActions();
    }

    @VisibleForTesting
    protected void setChildrenContainer(NotificationChildrenContainer childrenContainer) {
        this.mChildrenContainer = childrenContainer;
    }

    @VisibleForTesting
    protected void setPrivateLayout(NotificationContentView privateLayout) {
        this.mPrivateLayout = privateLayout;
    }

    @VisibleForTesting
    protected void setPublicLayout(NotificationContentView publicLayout) {
        this.mPublicLayout = publicLayout;
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView, com.android.systemui.Dumpable
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        super.dump(fd, pw, args);
        pw.println("  Notification: " + getStatusBarNotification().getKey());
        pw.print("    visibility: " + getVisibility());
        pw.print(", alpha: " + getAlpha());
        pw.print(", translation: " + getTranslation());
        pw.print(", removed: " + isRemoved());
        pw.print(", expandAnimationRunning: " + this.mExpandAnimationRunning);
        NotificationContentView showingLayout = getShowingLayout();
        StringBuilder sb = new StringBuilder();
        sb.append(", privateShowing: ");
        sb.append(showingLayout == this.mPrivateLayout);
        pw.print(sb.toString());
        pw.println();
        showingLayout.dump(fd, pw, args);
        pw.print("    ");
        if (getViewState() != null) {
            getViewState().dump(fd, pw, args);
        } else {
            pw.print("no viewState!!!");
        }
        pw.println();
        pw.println();
        if (this.mIsSummaryWithChildren) {
            pw.print("  ChildrenContainer");
            pw.print(" visibility: " + this.mChildrenContainer.getVisibility());
            pw.print(", alpha: " + this.mChildrenContainer.getAlpha());
            pw.print(", translationY: " + this.mChildrenContainer.getTranslationY());
            pw.println();
            List<ExpandableNotificationRow> notificationChildren = getNotificationChildren();
            pw.println("  Children: " + notificationChildren.size());
            pw.println("  {");
            for (ExpandableNotificationRow child : notificationChildren) {
                child.dump(fd, pw, args);
            }
            pw.println("  }");
            pw.println();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class SystemNotificationAsyncTask extends AsyncTask<Void, Void, Boolean> {
        private SystemNotificationAsyncTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Void... voids) {
            return ExpandableNotificationRow.isSystemNotification(ExpandableNotificationRow.this.mContext, ExpandableNotificationRow.this.mStatusBarNotification);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean result) {
            if (ExpandableNotificationRow.this.mEntry != null) {
                ExpandableNotificationRow.this.mEntry.mIsSystemNotification = result;
            }
        }
    }
}
