package com.android.systemui.statusbar.notification.row;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin;
import com.android.systemui.statusbar.AlphaOptimizedImageView;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.NotificationGuts;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/* loaded from: classes21.dex */
public class NotificationMenuRow implements NotificationMenuRowPlugin, View.OnClickListener, ExpandableNotificationRow.LayoutListener {
    private static final boolean DEBUG = false;
    private static final int ICON_ALPHA_ANIM_DURATION = 200;
    private static final long SHOW_MENU_DELAY = 60;
    private static final float SWIPED_BACK_ENOUGH_TO_COVER_FRACTION = 0.2f;
    private static final float SWIPED_FAR_ENOUGH_MENU_FRACTION = 0.25f;
    private static final float SWIPED_FAR_ENOUGH_MENU_UNCLEARABLE_FRACTION = 0.15f;
    private static final String TAG = "swipe";
    private float mAlpha;
    private boolean mAnimating;
    private NotificationMenuRowPlugin.MenuItem mAppOpsItem;
    private CheckForDrag mCheckForDrag;
    private Context mContext;
    private boolean mDismissRtl;
    private boolean mDismissing;
    private ValueAnimator mFadeAnimator;
    private Handler mHandler;
    private int mHorizSpaceForIcon;
    private int[] mIconLocation;
    private int mIconPadding;
    private boolean mIconsPlaced;
    private NotificationMenuItem mInfoItem;
    private boolean mIsForeground;
    private boolean mIsUserTouching;
    private final boolean mIsUsingBidirectionalSwipe;
    private ArrayList<NotificationMenuRowPlugin.MenuItem> mLeftMenuItems;
    private FrameLayout mMenuContainer;
    private boolean mMenuFadedIn;
    private final Map<View, NotificationMenuRowPlugin.MenuItem> mMenuItemsByView;
    private NotificationMenuRowPlugin.OnMenuEventListener mMenuListener;
    private boolean mMenuSnapped;
    private boolean mMenuSnappedOnLeft;
    private boolean mOnLeft;
    private ExpandableNotificationRow mParent;
    private int[] mParentLocation;
    private ArrayList<NotificationMenuRowPlugin.MenuItem> mRightMenuItems;
    private boolean mShouldShowMenu;
    private int mSidePadding;
    private boolean mSnapping;
    private NotificationMenuRowPlugin.MenuItem mSnoozeItem;
    private float mTranslation;
    private int mVertSpaceForIcons;

    public NotificationMenuRow(Context context) {
        this(context, false);
    }

    @VisibleForTesting
    NotificationMenuRow(Context context, boolean isUsingBidirectionalSwipe) {
        this.mMenuItemsByView = new ArrayMap();
        this.mIconLocation = new int[2];
        this.mParentLocation = new int[2];
        this.mHorizSpaceForIcon = -1;
        this.mVertSpaceForIcons = -1;
        this.mIconPadding = -1;
        this.mAlpha = 0.0f;
        this.mContext = context;
        this.mShouldShowMenu = context.getResources().getBoolean(R.bool.config_showNotificationGear);
        this.mHandler = new Handler(Looper.getMainLooper());
        this.mLeftMenuItems = new ArrayList<>();
        this.mRightMenuItems = new ArrayList<>();
        this.mIsUsingBidirectionalSwipe = isUsingBidirectionalSwipe;
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public ArrayList<NotificationMenuRowPlugin.MenuItem> getMenuItems(Context context) {
        return this.mOnLeft ? this.mLeftMenuItems : this.mRightMenuItems;
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public NotificationMenuRowPlugin.MenuItem getLongpressMenuItem(Context context) {
        return this.mInfoItem;
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public NotificationMenuRowPlugin.MenuItem getAppOpsMenuItem(Context context) {
        return this.mAppOpsItem;
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public NotificationMenuRowPlugin.MenuItem getSnoozeMenuItem(Context context) {
        return this.mSnoozeItem;
    }

    @VisibleForTesting
    protected ExpandableNotificationRow getParent() {
        return this.mParent;
    }

    @VisibleForTesting
    protected boolean isMenuOnLeft() {
        return this.mOnLeft;
    }

    @VisibleForTesting
    protected boolean isMenuSnappedOnLeft() {
        return this.mMenuSnappedOnLeft;
    }

    @VisibleForTesting
    protected boolean isMenuSnapped() {
        return this.mMenuSnapped;
    }

    @VisibleForTesting
    protected boolean isDismissing() {
        return this.mDismissing;
    }

    @VisibleForTesting
    protected boolean isSnapping() {
        return this.mSnapping;
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public void setMenuClickListener(NotificationMenuRowPlugin.OnMenuEventListener listener) {
        this.mMenuListener = listener;
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public void createMenu(ViewGroup parent, StatusBarNotification sbn) {
        this.mParent = (ExpandableNotificationRow) parent;
        createMenuViews(true, (sbn == null || (sbn.getNotification().flags & 64) == 0) ? false : true);
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public boolean isMenuVisible() {
        return this.mAlpha > 0.0f;
    }

    @VisibleForTesting
    protected boolean isUserTouching() {
        return this.mIsUserTouching;
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public boolean shouldShowMenu() {
        return this.mShouldShowMenu;
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public View getMenuView() {
        return this.mMenuContainer;
    }

    @VisibleForTesting
    protected float getTranslation() {
        return this.mTranslation;
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public void resetMenu() {
        resetState(true);
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public void onTouchEnd() {
        this.mIsUserTouching = false;
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public void onNotificationUpdated(StatusBarNotification sbn) {
        if (this.mMenuContainer == null) {
            return;
        }
        createMenuViews(!isMenuVisible(), (sbn.getNotification().flags & 64) != 0);
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public void onConfigurationChanged() {
        this.mParent.setLayoutListener(this);
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableNotificationRow.LayoutListener
    public void onLayout() {
        this.mIconsPlaced = false;
        setMenuLocation();
        this.mParent.removeListener();
    }

    private void createMenuViews(boolean resetState, boolean isForeground) {
        this.mIsForeground = isForeground;
        Resources res = this.mContext.getResources();
        this.mHorizSpaceForIcon = res.getDimensionPixelSize(R.dimen.notification_menu_icon_size);
        this.mVertSpaceForIcons = res.getDimensionPixelSize(R.dimen.notification_min_height);
        this.mLeftMenuItems.clear();
        this.mRightMenuItems.clear();
        boolean showSnooze = Settings.Secure.getInt(this.mContext.getContentResolver(), "show_notification_snooze", 0) == 1;
        if (!isForeground && showSnooze) {
            this.mSnoozeItem = createSnoozeItem(this.mContext);
        }
        this.mAppOpsItem = createAppOpsItem(this.mContext);
        if (this.mIsUsingBidirectionalSwipe) {
            this.mInfoItem = createInfoItem(this.mContext, true ^ this.mParent.getEntry().isHighPriority());
        } else {
            this.mInfoItem = createInfoItem(this.mContext);
        }
        if (!this.mIsUsingBidirectionalSwipe) {
            if (!isForeground && showSnooze) {
                this.mRightMenuItems.add(this.mSnoozeItem);
            }
            this.mRightMenuItems.add(this.mInfoItem);
            this.mRightMenuItems.add(this.mAppOpsItem);
            this.mLeftMenuItems.addAll(this.mRightMenuItems);
        } else {
            ArrayList<NotificationMenuRowPlugin.MenuItem> menuItems = this.mDismissRtl ? this.mLeftMenuItems : this.mRightMenuItems;
            menuItems.add(this.mInfoItem);
        }
        populateMenuViews();
        if (resetState) {
            resetState(false);
            return;
        }
        this.mIconsPlaced = false;
        setMenuLocation();
        if (!this.mIsUserTouching) {
            onSnapOpen();
        }
    }

    private void populateMenuViews() {
        FrameLayout frameLayout = this.mMenuContainer;
        if (frameLayout != null) {
            frameLayout.removeAllViews();
            this.mMenuItemsByView.clear();
        } else {
            this.mMenuContainer = new FrameLayout(this.mContext);
        }
        List<NotificationMenuRowPlugin.MenuItem> menuItems = this.mOnLeft ? this.mLeftMenuItems : this.mRightMenuItems;
        for (int i = 0; i < menuItems.size(); i++) {
            addMenuView(menuItems.get(i), this.mMenuContainer);
        }
    }

    private void resetState(boolean notify) {
        setMenuAlpha(0.0f);
        this.mIconsPlaced = false;
        this.mMenuFadedIn = false;
        this.mAnimating = false;
        this.mSnapping = false;
        this.mDismissing = false;
        this.mMenuSnapped = false;
        setMenuLocation();
        NotificationMenuRowPlugin.OnMenuEventListener onMenuEventListener = this.mMenuListener;
        if (onMenuEventListener != null && notify) {
            onMenuEventListener.onMenuReset(this.mParent);
        }
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public void onTouchMove(float delta) {
        this.mSnapping = false;
        if (!isTowardsMenu(delta) && isMenuLocationChange()) {
            this.mMenuSnapped = false;
            if (!this.mHandler.hasCallbacks(this.mCheckForDrag)) {
                this.mCheckForDrag = null;
            } else {
                setMenuAlpha(0.0f);
                setMenuLocation();
            }
        }
        if (this.mShouldShowMenu && !NotificationStackScrollLayout.isPinnedHeadsUp(getParent()) && !this.mParent.areGutsExposed() && !this.mParent.showingPulsing()) {
            CheckForDrag checkForDrag = this.mCheckForDrag;
            if (checkForDrag == null || !this.mHandler.hasCallbacks(checkForDrag)) {
                this.mCheckForDrag = new CheckForDrag();
                this.mHandler.postDelayed(this.mCheckForDrag, 60L);
            }
        }
    }

    @VisibleForTesting
    protected void beginDrag() {
        this.mSnapping = false;
        ValueAnimator valueAnimator = this.mFadeAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.mHandler.removeCallbacks(this.mCheckForDrag);
        this.mCheckForDrag = null;
        this.mIsUserTouching = true;
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public void onTouchStart() {
        beginDrag();
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public void onSnapOpen() {
        ExpandableNotificationRow expandableNotificationRow;
        this.mMenuSnapped = true;
        this.mMenuSnappedOnLeft = isMenuOnLeft();
        if (this.mAlpha == 0.0f && (expandableNotificationRow = this.mParent) != null) {
            fadeInMenu(expandableNotificationRow.getWidth());
        }
        NotificationMenuRowPlugin.OnMenuEventListener onMenuEventListener = this.mMenuListener;
        if (onMenuEventListener != null) {
            onMenuEventListener.onMenuShown(getParent());
        }
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public void onSnapClosed() {
        cancelDrag();
        this.mMenuSnapped = false;
        this.mSnapping = true;
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public void onDismiss() {
        cancelDrag();
        this.mMenuSnapped = false;
        this.mDismissing = true;
    }

    @VisibleForTesting
    protected void cancelDrag() {
        ValueAnimator valueAnimator = this.mFadeAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.mHandler.removeCallbacks(this.mCheckForDrag);
    }

    @VisibleForTesting
    protected float getMinimumSwipeDistance() {
        float multiplier;
        if (getParent().canViewBeDismissed()) {
            multiplier = 0.25f;
        } else {
            multiplier = SWIPED_FAR_ENOUGH_MENU_UNCLEARABLE_FRACTION;
        }
        return this.mHorizSpaceForIcon * multiplier;
    }

    @VisibleForTesting
    protected float getMaximumSwipeDistance() {
        return this.mHorizSpaceForIcon * 0.2f;
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public boolean isTowardsMenu(float movement) {
        return isMenuVisible() && ((isMenuOnLeft() && movement <= 0.0f) || (!isMenuOnLeft() && movement >= 0.0f));
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public void setAppName(String appName) {
        if (appName == null) {
            return;
        }
        setAppName(appName, this.mLeftMenuItems);
        setAppName(appName, this.mRightMenuItems);
    }

    private void setAppName(String appName, ArrayList<NotificationMenuRowPlugin.MenuItem> menuItems) {
        Resources res = this.mContext.getResources();
        int count = menuItems.size();
        for (int i = 0; i < count; i++) {
            NotificationMenuRowPlugin.MenuItem item = menuItems.get(i);
            String description = String.format(res.getString(R.string.notification_menu_accessibility), appName, item.getContentDescription());
            View menuView = item.getMenuView();
            if (menuView != null) {
                menuView.setContentDescription(description);
            }
        }
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public void onParentHeightUpdate() {
        float translationY;
        if (this.mParent != null) {
            if ((this.mLeftMenuItems.isEmpty() && this.mRightMenuItems.isEmpty()) || this.mMenuContainer == null) {
                return;
            }
            int parentHeight = this.mParent.getActualHeight();
            int i = this.mVertSpaceForIcons;
            if (parentHeight < i) {
                translationY = (parentHeight / 2) - (this.mHorizSpaceForIcon / 2);
            } else {
                translationY = (i - this.mHorizSpaceForIcon) / 2;
            }
            this.mMenuContainer.setTranslationY(translationY);
        }
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public void onParentTranslationUpdate(float translation) {
        float desiredAlpha;
        this.mTranslation = translation;
        if (this.mAnimating || !this.mMenuFadedIn) {
            return;
        }
        float fadeThreshold = this.mParent.getWidth() * 0.3f;
        float absTrans = Math.abs(translation);
        if (absTrans == 0.0f) {
            desiredAlpha = 0.0f;
        } else if (absTrans <= fadeThreshold) {
            desiredAlpha = 1.0f;
        } else {
            desiredAlpha = 1.0f - ((absTrans - fadeThreshold) / (this.mParent.getWidth() - fadeThreshold));
        }
        setMenuAlpha(desiredAlpha);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (this.mMenuListener == null) {
            return;
        }
        v.getLocationOnScreen(this.mIconLocation);
        this.mParent.getLocationOnScreen(this.mParentLocation);
        int centerX = this.mHorizSpaceForIcon / 2;
        int centerY = v.getHeight() / 2;
        int[] iArr = this.mIconLocation;
        int i = iArr[0];
        int[] iArr2 = this.mParentLocation;
        int x = (i - iArr2[0]) + centerX;
        int y = (iArr[1] - iArr2[1]) + centerY;
        if (this.mMenuItemsByView.containsKey(v)) {
            this.mMenuListener.onMenuClicked(this.mParent, x, y, this.mMenuItemsByView.get(v));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isMenuLocationChange() {
        boolean onLeft = this.mTranslation > ((float) this.mIconPadding);
        boolean onRight = this.mTranslation < ((float) (-this.mIconPadding));
        return (isMenuOnLeft() && onRight) || (!isMenuOnLeft() && onLeft);
    }

    private void setMenuLocation() {
        FrameLayout frameLayout;
        boolean showOnLeft = this.mTranslation > 0.0f;
        if ((this.mIconsPlaced && showOnLeft == isMenuOnLeft()) || isSnapping() || (frameLayout = this.mMenuContainer) == null || !frameLayout.isAttachedToWindow()) {
            return;
        }
        boolean wasOnLeft = this.mOnLeft;
        this.mOnLeft = showOnLeft;
        if (wasOnLeft != showOnLeft) {
            populateMenuViews();
        }
        int count = this.mMenuContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = this.mMenuContainer.getChildAt(i);
            float left = this.mHorizSpaceForIcon * i;
            float right = this.mParent.getWidth() - (this.mHorizSpaceForIcon * (i + 1));
            v.setX(showOnLeft ? left : right);
        }
        this.mIconsPlaced = true;
    }

    @VisibleForTesting
    protected void setMenuAlpha(float alpha) {
        this.mAlpha = alpha;
        FrameLayout frameLayout = this.mMenuContainer;
        if (frameLayout == null) {
            return;
        }
        if (alpha == 0.0f) {
            this.mMenuFadedIn = false;
            frameLayout.setVisibility(4);
        } else {
            frameLayout.setVisibility(0);
        }
        int count = this.mMenuContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            this.mMenuContainer.getChildAt(i).setAlpha(this.mAlpha);
        }
    }

    @VisibleForTesting
    protected int getSpaceForMenu() {
        return this.mHorizSpaceForIcon * this.mMenuContainer.getChildCount();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public final class CheckForDrag implements Runnable {
        private CheckForDrag() {
        }

        @Override // java.lang.Runnable
        public void run() {
            float absTransX = Math.abs(NotificationMenuRow.this.mTranslation);
            float bounceBackToMenuWidth = NotificationMenuRow.this.getSpaceForMenu();
            float notiThreshold = NotificationMenuRow.this.mParent.getWidth() * 0.4f;
            if ((!NotificationMenuRow.this.isMenuVisible() || NotificationMenuRow.this.isMenuLocationChange()) && absTransX >= bounceBackToMenuWidth * 0.4d && absTransX < notiThreshold) {
                NotificationMenuRow.this.fadeInMenu(notiThreshold);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fadeInMenu(final float notiThreshold) {
        if (this.mDismissing || this.mAnimating) {
            return;
        }
        if (isMenuLocationChange()) {
            setMenuAlpha(0.0f);
        }
        final float transX = this.mTranslation;
        final boolean fromLeft = this.mTranslation > 0.0f;
        setMenuLocation();
        this.mFadeAnimator = ValueAnimator.ofFloat(this.mAlpha, 1.0f);
        this.mFadeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.notification.row.NotificationMenuRow.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                float absTrans = Math.abs(transX);
                boolean pastMenu = (fromLeft && transX <= notiThreshold) || (!fromLeft && absTrans <= notiThreshold);
                if (pastMenu && !NotificationMenuRow.this.mMenuFadedIn) {
                    NotificationMenuRow.this.setMenuAlpha(((Float) animation.getAnimatedValue()).floatValue());
                }
            }
        });
        this.mFadeAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.notification.row.NotificationMenuRow.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                NotificationMenuRow.this.mAnimating = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                NotificationMenuRow.this.setMenuAlpha(0.0f);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                NotificationMenuRow.this.mAnimating = false;
                NotificationMenuRow notificationMenuRow = NotificationMenuRow.this;
                notificationMenuRow.mMenuFadedIn = notificationMenuRow.mAlpha == 1.0f;
            }
        });
        this.mFadeAnimator.setInterpolator(Interpolators.ALPHA_IN);
        this.mFadeAnimator.setDuration(200L);
        this.mFadeAnimator.start();
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public void setMenuItems(ArrayList<NotificationMenuRowPlugin.MenuItem> items) {
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public boolean shouldShowGutsOnSnapOpen() {
        return this.mIsUsingBidirectionalSwipe;
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public NotificationMenuRowPlugin.MenuItem menuItemToExposeOnSnap() {
        if (this.mIsUsingBidirectionalSwipe) {
            return this.mInfoItem;
        }
        return null;
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public Point getRevealAnimationOrigin() {
        View v = this.mInfoItem.getMenuView();
        int menuX = v.getLeft() + v.getPaddingLeft() + (v.getWidth() / 2);
        int menuY = v.getTop() + v.getPaddingTop() + (v.getHeight() / 2);
        if (isMenuOnLeft()) {
            return new Point(menuX, menuY);
        }
        return new Point(this.mParent.getRight() - menuX, menuY);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static NotificationMenuRowPlugin.MenuItem createSnoozeItem(Context context) {
        Resources res = context.getResources();
        NotificationSnooze content = (NotificationSnooze) LayoutInflater.from(context).inflate(R.layout.notification_snooze, (ViewGroup) null, false);
        String snoozeDescription = res.getString(R.string.notification_menu_snooze_description);
        NotificationMenuRowPlugin.MenuItem snooze = new NotificationMenuItem(context, snoozeDescription, content, R.drawable.ic_snooze);
        return snooze;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static NotificationMenuItem createInfoItem(Context context) {
        Resources res = context.getResources();
        String infoDescription = res.getString(R.string.notification_menu_gear_description);
        NotificationInfo infoContent = (NotificationInfo) LayoutInflater.from(context).inflate(R.layout.notification_info, (ViewGroup) null, false);
        return new NotificationMenuItem(context, infoDescription, infoContent, R.drawable.ic_settings);
    }

    static NotificationMenuItem createInfoItem(Context context, boolean isCurrentlySilent) {
        int iconResId;
        Resources res = context.getResources();
        String infoDescription = res.getString(R.string.notification_menu_gear_description);
        NotificationInfo infoContent = (NotificationInfo) LayoutInflater.from(context).inflate(R.layout.notification_info, (ViewGroup) null, false);
        if (isCurrentlySilent) {
            iconResId = R.drawable.ic_notifications_silence;
        } else {
            iconResId = R.drawable.ic_notifications_alert;
        }
        return new NotificationMenuItem(context, infoDescription, infoContent, iconResId);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static NotificationMenuRowPlugin.MenuItem createAppOpsItem(Context context) {
        AppOpsInfo appOpsContent = (AppOpsInfo) LayoutInflater.from(context).inflate(R.layout.app_ops_info, (ViewGroup) null, false);
        NotificationMenuRowPlugin.MenuItem info = new NotificationMenuItem(context, null, appOpsContent, -1);
        return info;
    }

    private void addMenuView(NotificationMenuRowPlugin.MenuItem item, ViewGroup parent) {
        View menuView = item.getMenuView();
        if (menuView != null) {
            menuView.setAlpha(this.mAlpha);
            parent.addView(menuView);
            menuView.setOnClickListener(this);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) menuView.getLayoutParams();
            int i = this.mHorizSpaceForIcon;
            lp.width = i;
            lp.height = i;
            menuView.setLayoutParams(lp);
        }
        this.mMenuItemsByView.put(menuView, item);
    }

    @VisibleForTesting
    protected float getSnapBackThreshold() {
        return getSpaceForMenu() - getMaximumSwipeDistance();
    }

    @VisibleForTesting
    protected float getDismissThreshold() {
        return getParent().getWidth() * 0.6f;
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public boolean isWithinSnapMenuThreshold() {
        float translation = getTranslation();
        float snapBackThreshold = getSnapBackThreshold();
        float targetRight = getDismissThreshold();
        return isMenuOnLeft() ? translation > snapBackThreshold && translation < targetRight : translation < (-snapBackThreshold) && translation > (-targetRight);
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public boolean isSwipedEnoughToShowMenu() {
        float minimumSwipeDistance = getMinimumSwipeDistance();
        float translation = getTranslation();
        return isMenuVisible() && (!isMenuOnLeft() ? translation >= (-minimumSwipeDistance) : translation <= minimumSwipeDistance);
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public int getMenuSnapTarget() {
        return isMenuOnLeft() ? getSpaceForMenu() : -getSpaceForMenu();
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public boolean shouldSnapBack() {
        float translation = getTranslation();
        float targetLeft = getSnapBackThreshold();
        if (isMenuOnLeft()) {
            if (translation < targetLeft) {
                return true;
            }
        } else if (translation > (-targetLeft)) {
            return true;
        }
        return false;
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public boolean isSnappedAndOnSameSide() {
        return isMenuSnapped() && isMenuVisible() && isMenuSnappedOnLeft() == isMenuOnLeft();
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public boolean canBeDismissed() {
        return getParent().canViewBeDismissed();
    }

    @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin
    public void setDismissRtl(boolean dismissRtl) {
        this.mDismissRtl = dismissRtl;
        if (this.mMenuContainer != null) {
            createMenuViews(true, this.mIsForeground);
        }
    }

    /* loaded from: classes21.dex */
    public static class NotificationMenuItem implements NotificationMenuRowPlugin.MenuItem {
        String mContentDescription;
        NotificationGuts.GutsContent mGutsContent;
        View mMenuView;

        public NotificationMenuItem(Context context, String contentDescription, NotificationGuts.GutsContent content, int iconResId) {
            Resources res = context.getResources();
            int padding = res.getDimensionPixelSize(R.dimen.notification_menu_icon_padding);
            int tint = res.getColor(R.color.notification_gear_color);
            if (iconResId >= 0) {
                AlphaOptimizedImageView iv = new AlphaOptimizedImageView(context);
                iv.setPadding(padding, padding, padding, padding);
                Drawable icon = context.getResources().getDrawable(iconResId);
                iv.setImageDrawable(icon);
                iv.setColorFilter(tint);
                iv.setAlpha(1.0f);
                this.mMenuView = iv;
            }
            this.mContentDescription = contentDescription;
            this.mGutsContent = content;
        }

        @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin.MenuItem
        public View getMenuView() {
            return this.mMenuView;
        }

        @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin.MenuItem
        public View getGutsView() {
            return this.mGutsContent.getContentView();
        }

        @Override // com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin.MenuItem
        public String getContentDescription() {
            return this.mContentDescription;
        }
    }
}
