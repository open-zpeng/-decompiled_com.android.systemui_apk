package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.internal.util.ContrastColorUtil;
import com.android.settingslib.Utils;
import com.android.systemui.Dependency;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.NotificationShelf;
import com.android.systemui.statusbar.StatusBarIconView;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.NotificationUtils;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;
/* loaded from: classes21.dex */
public class NotificationIconAreaController implements DarkIconDispatcher.DarkReceiver, StatusBarStateController.StateListener, NotificationWakeUpCoordinator.WakeUpListener {
    private static final long AOD_ICONS_APPEAR_DURATION = 200;
    public static final String HIGH_PRIORITY = "high_priority";
    private boolean mAnimationsEnabled;
    private int mAodIconAppearTranslation;
    private int mAodIconTint;
    private NotificationIconContainer mAodIcons;
    private boolean mAodIconsVisible;
    private final KeyguardBypassController mBypassController;
    private NotificationIconContainer mCenteredIcon;
    protected View mCenteredIconArea;
    private StatusBarIconView mCenteredIconView;
    private Context mContext;
    private final ContrastColorUtil mContrastColorUtil;
    private final DozeParameters mDozeParameters;
    private boolean mFullyHidden;
    private int mIconHPadding;
    private int mIconSize;
    private boolean mIsPulsing;
    private final NotificationMediaManager mMediaManager;
    protected View mNotificationIconArea;
    private NotificationIconContainer mNotificationIcons;
    private ViewGroup mNotificationScrollLayout;
    private NotificationIconContainer mShelfIcons;
    private StatusBar mStatusBar;
    private final StatusBarStateController mStatusBarStateController;
    private final NotificationWakeUpCoordinator mWakeUpCoordinator;
    private final Runnable mUpdateStatusBarIcons = new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NWCrb8vzuopzf5kAygkNeXndtBo
        @Override // java.lang.Runnable
        public final void run() {
            NotificationIconAreaController.this.updateStatusBarIcons();
        }
    };
    private int mIconTint = -1;
    private int mCenteredIconTint = -1;
    private final Rect mTintArea = new Rect();
    private final NotificationEntryManager mEntryManager = (NotificationEntryManager) Dependency.get(NotificationEntryManager.class);

    public NotificationIconAreaController(Context context, StatusBar statusBar, StatusBarStateController statusBarStateController, NotificationWakeUpCoordinator wakeUpCoordinator, KeyguardBypassController keyguardBypassController, NotificationMediaManager notificationMediaManager) {
        this.mStatusBar = statusBar;
        this.mContrastColorUtil = ContrastColorUtil.getInstance(context);
        this.mContext = context;
        this.mStatusBarStateController = statusBarStateController;
        this.mStatusBarStateController.addCallback(this);
        this.mMediaManager = notificationMediaManager;
        this.mDozeParameters = DozeParameters.getInstance(this.mContext);
        this.mWakeUpCoordinator = wakeUpCoordinator;
        wakeUpCoordinator.addListener(this);
        this.mBypassController = keyguardBypassController;
        initializeNotificationAreaViews(context);
        reloadAodColor();
    }

    protected View inflateIconArea(LayoutInflater inflater) {
        return inflater.inflate(R.layout.notification_icon_area, (ViewGroup) null);
    }

    protected void initializeNotificationAreaViews(Context context) {
        reloadDimens(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        this.mNotificationIconArea = inflateIconArea(layoutInflater);
        this.mNotificationIcons = (NotificationIconContainer) this.mNotificationIconArea.findViewById(R.id.notificationIcons);
        this.mNotificationScrollLayout = this.mStatusBar.getNotificationScrollLayout();
        this.mCenteredIconArea = layoutInflater.inflate(R.layout.center_icon_area, (ViewGroup) null);
        this.mCenteredIcon = (NotificationIconContainer) this.mCenteredIconArea.findViewById(R.id.centeredIcon);
        initAodIcons();
    }

    public void initAodIcons() {
        boolean changed = this.mAodIcons != null;
        if (changed) {
            this.mAodIcons.setAnimationsEnabled(false);
            this.mAodIcons.removeAllViews();
        }
        this.mAodIcons = (NotificationIconContainer) this.mStatusBar.getStatusBarWindow().findViewById(R.id.clock_notification_icon_container);
        this.mAodIcons.setOnLockScreen(true);
        updateAodIconsVisibility(false);
        updateAnimations();
        if (changed) {
            updateAodNotificationIcons();
        }
    }

    public void setupShelf(NotificationShelf shelf) {
        this.mShelfIcons = shelf.getShelfIcons();
        shelf.setCollapsedIcons(this.mNotificationIcons);
    }

    public void onDensityOrFontScaleChanged(Context context) {
        reloadDimens(context);
        FrameLayout.LayoutParams params = generateIconLayoutParams();
        for (int i = 0; i < this.mNotificationIcons.getChildCount(); i++) {
            View child = this.mNotificationIcons.getChildAt(i);
            child.setLayoutParams(params);
        }
        for (int i2 = 0; i2 < this.mShelfIcons.getChildCount(); i2++) {
            View child2 = this.mShelfIcons.getChildAt(i2);
            child2.setLayoutParams(params);
        }
        for (int i3 = 0; i3 < this.mCenteredIcon.getChildCount(); i3++) {
            View child3 = this.mCenteredIcon.getChildAt(i3);
            child3.setLayoutParams(params);
        }
        for (int i4 = 0; i4 < this.mAodIcons.getChildCount(); i4++) {
            View child4 = this.mAodIcons.getChildAt(i4);
            child4.setLayoutParams(params);
        }
    }

    @NonNull
    private FrameLayout.LayoutParams generateIconLayoutParams() {
        return new FrameLayout.LayoutParams(this.mIconSize + (this.mIconHPadding * 2), getHeight());
    }

    private void reloadDimens(Context context) {
        Resources res = context.getResources();
        this.mIconSize = res.getDimensionPixelSize(17105441);
        this.mIconHPadding = res.getDimensionPixelSize(R.dimen.status_bar_icon_padding);
        this.mAodIconAppearTranslation = res.getDimensionPixelSize(R.dimen.shelf_appear_translation);
    }

    public View getNotificationInnerAreaView() {
        return this.mNotificationIconArea;
    }

    public View getCenteredNotificationAreaView() {
        return this.mCenteredIconArea;
    }

    @Override // com.android.systemui.plugins.DarkIconDispatcher.DarkReceiver
    public void onDarkChanged(Rect tintArea, float darkIntensity, int iconTint) {
        if (tintArea == null) {
            this.mTintArea.setEmpty();
        } else {
            this.mTintArea.set(tintArea);
        }
        View view = this.mNotificationIconArea;
        if (view != null) {
            if (DarkIconDispatcher.isInArea(tintArea, view)) {
                this.mIconTint = iconTint;
            }
        } else {
            this.mIconTint = iconTint;
        }
        View view2 = this.mCenteredIconArea;
        if (view2 != null) {
            if (DarkIconDispatcher.isInArea(tintArea, view2)) {
                this.mCenteredIconTint = iconTint;
            }
        } else {
            this.mCenteredIconTint = iconTint;
        }
        applyNotificationIconsTint();
    }

    protected int getHeight() {
        return this.mStatusBar.getStatusBarHeight();
    }

    protected boolean shouldShowNotificationIcon(NotificationEntry entry, boolean showAmbient, boolean hideDismissed, boolean hideRepliedMessages, boolean hideCurrentMedia, boolean hideCenteredIcon, boolean hidePulsing, boolean onlyShowCenteredIcon) {
        boolean isCenteredNotificationIcon = (this.mCenteredIconView == null || entry.centeredIcon == null || !Objects.equals(entry.centeredIcon, this.mCenteredIconView)) ? false : true;
        if (onlyShowCenteredIcon) {
            return isCenteredNotificationIcon;
        }
        if (hideCenteredIcon && isCenteredNotificationIcon && !entry.isRowHeadsUp()) {
            return false;
        }
        if (!this.mEntryManager.getNotificationData().isAmbient(entry.key) || showAmbient) {
            if ((hideCurrentMedia && entry.key.equals(this.mMediaManager.getMediaNotificationKey())) || !entry.isTopLevelChild() || entry.getRow().getVisibility() == 8) {
                return false;
            }
            if (entry.isRowDismissed() && hideDismissed) {
                return false;
            }
            if (hideRepliedMessages && entry.isLastMessageFromReply()) {
                return false;
            }
            if (showAmbient || !entry.shouldSuppressStatusBar()) {
                return (hidePulsing && entry.showingPulsing() && (!this.mWakeUpCoordinator.getNotificationsFullyHidden() || !entry.isPulseSuppressed())) ? false : true;
            }
            return false;
        }
        return false;
    }

    public void updateNotificationIcons() {
        updateStatusBarIcons();
        updateShelfIcons();
        updateCenterIcon();
        updateAodNotificationIcons();
        applyNotificationIconsTint();
    }

    private void updateShelfIcons() {
        updateIconsForLayout(new Function() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NotificationIconAreaController$afpYK1wAP1i0HTFHOa1jb1wzzAQ
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                StatusBarIconView statusBarIconView;
                statusBarIconView = ((NotificationEntry) obj).expandedIcon;
                return statusBarIconView;
            }
        }, this.mShelfIcons, true, false, false, false, false, false, false);
    }

    public void updateStatusBarIcons() {
        updateIconsForLayout(new Function() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NotificationIconAreaController$ujxUr-qwlryo8PHBzga56kRshsA
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                StatusBarIconView statusBarIconView;
                statusBarIconView = ((NotificationEntry) obj).icon;
                return statusBarIconView;
            }
        }, this.mNotificationIcons, false, true, true, false, true, false, false);
    }

    private void updateCenterIcon() {
        updateIconsForLayout(new Function() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NotificationIconAreaController$S6CJ2tXrA2ieNVmUpwBa8v9eeEY
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                StatusBarIconView statusBarIconView;
                statusBarIconView = ((NotificationEntry) obj).centeredIcon;
                return statusBarIconView;
            }
        }, this.mCenteredIcon, false, false, false, false, false, false, true);
    }

    public void updateAodNotificationIcons() {
        updateIconsForLayout(new Function() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NotificationIconAreaController$b7MkWJaTAeTosmR_aU3q7JZNLpI
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                StatusBarIconView statusBarIconView;
                statusBarIconView = ((NotificationEntry) obj).aodIcon;
                return statusBarIconView;
            }
        }, this.mAodIcons, false, true, true, true, true, this.mBypassController.getBypassEnabled(), false);
    }

    private void updateIconsForLayout(Function<NotificationEntry, StatusBarIconView> function, NotificationIconContainer hostLayout, boolean showAmbient, boolean hideDismissed, boolean hideRepliedMessages, boolean hideCurrentMedia, boolean hideCenteredIcon, boolean hidePulsing, boolean onlyShowCenteredIcon) {
        StatusBarIconView iconView;
        ArrayList<StatusBarIconView> toShow = new ArrayList<>(this.mNotificationScrollLayout.getChildCount());
        for (int i = 0; i < this.mNotificationScrollLayout.getChildCount(); i++) {
            View view = this.mNotificationScrollLayout.getChildAt(i);
            if (view instanceof ExpandableNotificationRow) {
                NotificationEntry ent = ((ExpandableNotificationRow) view).getEntry();
                if (shouldShowNotificationIcon(ent, showAmbient, hideDismissed, hideRepliedMessages, hideCurrentMedia, hideCenteredIcon, hidePulsing, onlyShowCenteredIcon) && (iconView = function.apply(ent)) != null) {
                    toShow.add(iconView);
                }
            }
        }
        ArrayMap<String, ArrayList<StatusBarIcon>> replacingIcons = new ArrayMap<>();
        ArrayList<View> toRemove = new ArrayList<>();
        for (int i2 = 0; i2 < hostLayout.getChildCount(); i2++) {
            View child = hostLayout.getChildAt(i2);
            if ((child instanceof StatusBarIconView) && !toShow.contains(child)) {
                boolean iconWasReplaced = false;
                StatusBarIconView removedIcon = (StatusBarIconView) child;
                String removedGroupKey = removedIcon.getNotification().getGroupKey();
                int j = 0;
                while (true) {
                    if (j >= toShow.size()) {
                        break;
                    }
                    StatusBarIconView candidate = toShow.get(j);
                    if (candidate.getSourceIcon().sameAs(removedIcon.getSourceIcon()) && candidate.getNotification().getGroupKey().equals(removedGroupKey)) {
                        if (!iconWasReplaced) {
                            iconWasReplaced = true;
                        } else {
                            iconWasReplaced = false;
                            break;
                        }
                    }
                    j++;
                }
                if (iconWasReplaced) {
                    ArrayList<StatusBarIcon> statusBarIcons = replacingIcons.get(removedGroupKey);
                    if (statusBarIcons == null) {
                        statusBarIcons = new ArrayList<>();
                        replacingIcons.put(removedGroupKey, statusBarIcons);
                    }
                    statusBarIcons.add(removedIcon.getStatusBarIcon());
                }
                toRemove.add(removedIcon);
            }
        }
        ArrayList<String> duplicates = new ArrayList<>();
        for (String key : replacingIcons.keySet()) {
            if (replacingIcons.get(key).size() != 1) {
                duplicates.add(key);
            }
        }
        replacingIcons.removeAll(duplicates);
        hostLayout.setReplacingIcons(replacingIcons);
        int toRemoveCount = toRemove.size();
        for (int i3 = 0; i3 < toRemoveCount; i3++) {
            hostLayout.removeView(toRemove.get(i3));
        }
        FrameLayout.LayoutParams params = generateIconLayoutParams();
        for (int i4 = 0; i4 < toShow.size(); i4++) {
            StatusBarIconView v = toShow.get(i4);
            hostLayout.removeTransientView(v);
            if (v.getParent() == null) {
                if (hideDismissed) {
                    v.setOnDismissListener(this.mUpdateStatusBarIcons);
                }
                hostLayout.addView(v, i4, params);
            }
        }
        hostLayout.setChangingViewPositions(true);
        int childCount = hostLayout.getChildCount();
        for (int i5 = 0; i5 < childCount; i5++) {
            View actual = hostLayout.getChildAt(i5);
            StatusBarIconView expected = toShow.get(i5);
            if (actual != expected) {
                hostLayout.removeView(expected);
                hostLayout.addView(expected, i5);
            }
        }
        hostLayout.setChangingViewPositions(false);
        hostLayout.setReplacingIcons(null);
    }

    private void applyNotificationIconsTint() {
        for (int i = 0; i < this.mNotificationIcons.getChildCount(); i++) {
            final StatusBarIconView iv = (StatusBarIconView) this.mNotificationIcons.getChildAt(i);
            if (iv.getWidth() != 0) {
                updateTintForIcon(iv, this.mIconTint);
            } else {
                iv.executeOnLayout(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NotificationIconAreaController$kEHcYKNlJqRNuom7zI__dD3YiUQ
                    @Override // java.lang.Runnable
                    public final void run() {
                        NotificationIconAreaController.this.lambda$applyNotificationIconsTint$4$NotificationIconAreaController(iv);
                    }
                });
            }
        }
        for (int i2 = 0; i2 < this.mCenteredIcon.getChildCount(); i2++) {
            final StatusBarIconView iv2 = (StatusBarIconView) this.mCenteredIcon.getChildAt(i2);
            if (iv2.getWidth() != 0) {
                updateTintForIcon(iv2, this.mCenteredIconTint);
            } else {
                iv2.executeOnLayout(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NotificationIconAreaController$DNX7QrLi_n7I734CPybT_ZrNpwI
                    @Override // java.lang.Runnable
                    public final void run() {
                        NotificationIconAreaController.this.lambda$applyNotificationIconsTint$5$NotificationIconAreaController(iv2);
                    }
                });
            }
        }
        updateAodIconColors();
    }

    public /* synthetic */ void lambda$applyNotificationIconsTint$4$NotificationIconAreaController(StatusBarIconView iv) {
        updateTintForIcon(iv, this.mIconTint);
    }

    public /* synthetic */ void lambda$applyNotificationIconsTint$5$NotificationIconAreaController(StatusBarIconView iv) {
        updateTintForIcon(iv, this.mCenteredIconTint);
    }

    private void updateTintForIcon(StatusBarIconView v, int tint) {
        boolean isPreL = Boolean.TRUE.equals(v.getTag(R.id.icon_is_pre_L));
        int color = 0;
        boolean colorize = !isPreL || NotificationUtils.isGrayscale(v, this.mContrastColorUtil);
        if (colorize) {
            color = DarkIconDispatcher.getTint(this.mTintArea, v, tint);
        }
        v.setStaticDrawableColor(color);
        v.setDecorColor(tint);
    }

    public void showIconCentered(NotificationEntry entry) {
        StatusBarIconView icon = entry == null ? null : entry.centeredIcon;
        if (!Objects.equals(this.mCenteredIconView, icon)) {
            this.mCenteredIconView = icon;
            updateNotificationIcons();
        }
    }

    public void showIconIsolated(StatusBarIconView icon, boolean animated) {
        this.mNotificationIcons.showIconIsolated(icon, animated);
    }

    public void setIsolatedIconLocation(Rect iconDrawingRect, boolean requireStateUpdate) {
        this.mNotificationIcons.setIsolatedIconLocation(iconDrawingRect, requireStateUpdate);
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onDozingChanged(boolean isDozing) {
        boolean animate = this.mDozeParameters.getAlwaysOn() && !this.mDozeParameters.getDisplayNeedsBlanking();
        this.mAodIcons.setDozing(isDozing, animate, 0L);
    }

    public void setAnimationsEnabled(boolean enabled) {
        this.mAnimationsEnabled = enabled;
        updateAnimations();
    }

    @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
    public void onStateChanged(int newState) {
        updateAodIconsVisibility(false);
        updateAnimations();
    }

    private void updateAnimations() {
        boolean z = true;
        boolean inShade = this.mStatusBarStateController.getState() == 0;
        this.mAodIcons.setAnimationsEnabled(this.mAnimationsEnabled && !inShade);
        this.mCenteredIcon.setAnimationsEnabled(this.mAnimationsEnabled && inShade);
        NotificationIconContainer notificationIconContainer = this.mNotificationIcons;
        if (!this.mAnimationsEnabled || !inShade) {
            z = false;
        }
        notificationIconContainer.setAnimationsEnabled(z);
    }

    public void onThemeChanged() {
        reloadAodColor();
        updateAodIconColors();
    }

    public void appearAodIcons() {
        DozeParameters dozeParameters = DozeParameters.getInstance(this.mContext);
        if (dozeParameters.shouldControlScreenOff()) {
            this.mAodIcons.setTranslationY(-this.mAodIconAppearTranslation);
            this.mAodIcons.setAlpha(0.0f);
            animateInAodIconTranslation();
            this.mAodIcons.animate().alpha(1.0f).setInterpolator(Interpolators.LINEAR).setDuration(200L).start();
        }
    }

    private void animateInAodIconTranslation() {
        this.mAodIcons.animate().setInterpolator(Interpolators.DECELERATE_QUINT).translationY(0.0f).setDuration(200L).start();
    }

    private void reloadAodColor() {
        this.mAodIconTint = Utils.getColorAttrDefaultColor(this.mContext, R.attr.wallpaperTextColor);
    }

    private void updateAodIconColors() {
        for (int i = 0; i < this.mAodIcons.getChildCount(); i++) {
            final StatusBarIconView iv = (StatusBarIconView) this.mAodIcons.getChildAt(i);
            if (iv.getWidth() != 0) {
                updateTintForIcon(iv, this.mAodIconTint);
            } else {
                iv.executeOnLayout(new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NotificationIconAreaController$PUTDTipRCmrDLS4VQZByqHC4HFA
                    @Override // java.lang.Runnable
                    public final void run() {
                        NotificationIconAreaController.this.lambda$updateAodIconColors$6$NotificationIconAreaController(iv);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$updateAodIconColors$6$NotificationIconAreaController(StatusBarIconView iv) {
        updateTintForIcon(iv, this.mAodIconTint);
    }

    @Override // com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator.WakeUpListener
    public void onFullyHiddenChanged(boolean fullyHidden) {
        boolean animate = true;
        if (!this.mBypassController.getBypassEnabled()) {
            boolean animate2 = this.mDozeParameters.getAlwaysOn() && !this.mDozeParameters.getDisplayNeedsBlanking();
            animate = animate2 & fullyHidden;
        }
        updateAodIconsVisibility(animate);
        updateAodNotificationIcons();
    }

    @Override // com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator.WakeUpListener
    public void onPulseExpansionChanged(boolean expandingChanged) {
        if (expandingChanged) {
            updateAodIconsVisibility(true);
        }
    }

    private void updateAodIconsVisibility(boolean animate) {
        boolean visible = this.mBypassController.getBypassEnabled() || this.mWakeUpCoordinator.getNotificationsFullyHidden();
        if (this.mStatusBarStateController.getState() != 1) {
            visible = false;
        }
        if (visible && this.mWakeUpCoordinator.isPulseExpanding()) {
            visible = false;
        }
        if (this.mAodIconsVisible != visible) {
            this.mAodIconsVisible = visible;
            this.mAodIcons.animate().cancel();
            if (!animate) {
                this.mAodIcons.setAlpha(1.0f);
                this.mAodIcons.setTranslationY(0.0f);
                this.mAodIcons.setVisibility(visible ? 0 : 4);
                return;
            }
            boolean wasFullyInvisible = this.mAodIcons.getVisibility() != 0;
            if (this.mAodIconsVisible) {
                if (wasFullyInvisible) {
                    this.mAodIcons.setVisibility(0);
                    this.mAodIcons.setAlpha(1.0f);
                    appearAodIcons();
                    return;
                }
                animateInAodIconTranslation();
                CrossFadeHelper.fadeIn(this.mAodIcons);
                return;
            }
            animateInAodIconTranslation();
            CrossFadeHelper.fadeOut(this.mAodIcons);
        }
    }
}
