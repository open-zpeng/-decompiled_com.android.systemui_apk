package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Icon;
import android.util.AttributeSet;
import android.view.View;
import androidx.collection.ArrayMap;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.statusbar.AlphaOptimizedFrameLayout;
import com.android.systemui.statusbar.StatusBarIconView;
import com.android.systemui.statusbar.notification.stack.AnimationFilter;
import com.android.systemui.statusbar.notification.stack.AnimationProperties;
import com.android.systemui.statusbar.notification.stack.ViewState;
import java.util.ArrayList;
import java.util.HashMap;
/* loaded from: classes21.dex */
public class NotificationIconContainer extends AlphaOptimizedFrameLayout {
    private static final int CANNED_ANIMATION_DURATION = 100;
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_OVERFLOW = false;
    private static final int MAX_DOTS = 1;
    public static final int MAX_STATIC_ICONS = 4;
    private static final int MAX_VISIBLE_ICONS_ON_LOCK = 5;
    private static final int NO_VALUE = Integer.MIN_VALUE;
    public static final float OVERFLOW_EARLY_AMOUNT = 0.2f;
    private static final String TAG = "NotificationIconContainer";
    private int[] mAbsolutePosition;
    private int mActualLayoutWidth;
    private float mActualPaddingEnd;
    private float mActualPaddingStart;
    private int mAddAnimationStartIndex;
    private boolean mAnimationsEnabled;
    private int mCannedAnimationStartIndex;
    private boolean mChangingViewPositions;
    private boolean mDisallowNextAnimation;
    private int mDotPadding;
    private boolean mDozing;
    private IconState mFirstVisibleIconState;
    private int mIconSize;
    private final HashMap<View, IconState> mIconStates;
    private boolean mIsStaticLayout;
    private StatusBarIconView mIsolatedIcon;
    private View mIsolatedIconForAnimation;
    private Rect mIsolatedIconLocation;
    private IconState mLastVisibleIconState;
    private int mNumDots;
    private boolean mOnLockScreen;
    private float mOpenedAmount;
    private int mOverflowWidth;
    private ArrayMap<String, ArrayList<StatusBarIcon>> mReplacingIcons;
    private int mSpeedBumpIndex;
    private int mStaticDotDiameter;
    private int mStaticDotRadius;
    private float mVisualOverflowStart;
    private static final AnimationProperties DOT_ANIMATION_PROPERTIES = new AnimationProperties() { // from class: com.android.systemui.statusbar.phone.NotificationIconContainer.1
        private AnimationFilter mAnimationFilter = new AnimationFilter().animateX();

        @Override // com.android.systemui.statusbar.notification.stack.AnimationProperties
        public AnimationFilter getAnimationFilter() {
            return this.mAnimationFilter;
        }
    }.setDuration(200);
    private static final AnimationProperties ICON_ANIMATION_PROPERTIES = new AnimationProperties() { // from class: com.android.systemui.statusbar.phone.NotificationIconContainer.2
        private AnimationFilter mAnimationFilter = new AnimationFilter().animateY().animateAlpha().animateScale();

        @Override // com.android.systemui.statusbar.notification.stack.AnimationProperties
        public AnimationFilter getAnimationFilter() {
            return this.mAnimationFilter;
        }
    }.setDuration(100).setCustomInterpolator(View.TRANSLATION_Y, Interpolators.ICON_OVERSHOT);
    private static final AnimationProperties sTempProperties = new AnimationProperties() { // from class: com.android.systemui.statusbar.phone.NotificationIconContainer.3
        private AnimationFilter mAnimationFilter = new AnimationFilter();

        @Override // com.android.systemui.statusbar.notification.stack.AnimationProperties
        public AnimationFilter getAnimationFilter() {
            return this.mAnimationFilter;
        }
    };
    private static final AnimationProperties ADD_ICON_PROPERTIES = new AnimationProperties() { // from class: com.android.systemui.statusbar.phone.NotificationIconContainer.4
        private AnimationFilter mAnimationFilter = new AnimationFilter().animateAlpha();

        @Override // com.android.systemui.statusbar.notification.stack.AnimationProperties
        public AnimationFilter getAnimationFilter() {
            return this.mAnimationFilter;
        }
    }.setDuration(200).setDelay(50);
    private static final AnimationProperties UNISOLATION_PROPERTY_OTHERS = new AnimationProperties() { // from class: com.android.systemui.statusbar.phone.NotificationIconContainer.5
        private AnimationFilter mAnimationFilter = new AnimationFilter().animateAlpha();

        @Override // com.android.systemui.statusbar.notification.stack.AnimationProperties
        public AnimationFilter getAnimationFilter() {
            return this.mAnimationFilter;
        }
    }.setDuration(110);
    private static final AnimationProperties UNISOLATION_PROPERTY = new AnimationProperties() { // from class: com.android.systemui.statusbar.phone.NotificationIconContainer.6
        private AnimationFilter mAnimationFilter = new AnimationFilter().animateX();

        @Override // com.android.systemui.statusbar.notification.stack.AnimationProperties
        public AnimationFilter getAnimationFilter() {
            return this.mAnimationFilter;
        }
    }.setDuration(110);

    public NotificationIconContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mIsStaticLayout = true;
        this.mIconStates = new HashMap<>();
        this.mActualLayoutWidth = Integer.MIN_VALUE;
        this.mActualPaddingEnd = -2.14748365E9f;
        this.mActualPaddingStart = -2.14748365E9f;
        this.mAddAnimationStartIndex = -1;
        this.mCannedAnimationStartIndex = -1;
        this.mSpeedBumpIndex = -1;
        this.mOpenedAmount = 0.0f;
        this.mAnimationsEnabled = true;
        this.mAbsolutePosition = new int[2];
        initDimens();
        setWillNotDraw(true);
    }

    private void initDimens() {
        this.mDotPadding = getResources().getDimensionPixelSize(R.dimen.overflow_icon_dot_padding);
        this.mStaticDotRadius = getResources().getDimensionPixelSize(R.dimen.overflow_dot_radius);
        this.mStaticDotDiameter = this.mStaticDotRadius * 2;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(-65536);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(getActualPaddingStart(), 0.0f, getLayoutEnd(), getHeight(), paint);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initDimens();
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        float centerY = getHeight() / 2.0f;
        this.mIconSize = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            int top = (int) (centerY - (height / 2.0f));
            child.layout(0, top, width, top + height);
            if (i == 0) {
                setIconSize(child.getWidth());
            }
        }
        getLocationOnScreen(this.mAbsolutePosition);
        if (this.mIsStaticLayout) {
            updateState();
        }
    }

    private void setIconSize(int size) {
        this.mIconSize = size;
        this.mOverflowWidth = this.mIconSize + ((this.mStaticDotDiameter + this.mDotPadding) * 0);
    }

    private void updateState() {
        resetViewStates();
        calculateIconTranslations();
        applyIconStates();
    }

    public void applyIconStates() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            ViewState childState = this.mIconStates.get(child);
            if (childState != null) {
                childState.applyToView(child);
            }
        }
        this.mAddAnimationStartIndex = -1;
        this.mCannedAnimationStartIndex = -1;
        this.mDisallowNextAnimation = false;
        this.mIsolatedIconForAnimation = null;
    }

    @Override // android.view.ViewGroup
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        boolean isReplacingIcon = isReplacingIcon(child);
        if (!this.mChangingViewPositions) {
            IconState v = new IconState();
            if (isReplacingIcon) {
                v.justAdded = false;
                v.justReplaced = true;
            }
            this.mIconStates.put(child, v);
        }
        int childIndex = indexOfChild(child);
        if (childIndex < getChildCount() - 1 && !isReplacingIcon && this.mIconStates.get(getChildAt(childIndex + 1)).iconAppearAmount > 0.0f) {
            int i = this.mAddAnimationStartIndex;
            if (i < 0) {
                this.mAddAnimationStartIndex = childIndex;
            } else {
                this.mAddAnimationStartIndex = Math.min(i, childIndex);
            }
        }
        if (child instanceof StatusBarIconView) {
            ((StatusBarIconView) child).setDozing(this.mDozing, false, 0L);
        }
    }

    private boolean isReplacingIcon(View child) {
        if (this.mReplacingIcons != null && (child instanceof StatusBarIconView)) {
            StatusBarIconView iconView = (StatusBarIconView) child;
            Icon sourceIcon = iconView.getSourceIcon();
            String groupKey = iconView.getNotification().getGroupKey();
            ArrayList<StatusBarIcon> statusBarIcons = this.mReplacingIcons.get(groupKey);
            if (statusBarIcons != null) {
                StatusBarIcon replacedIcon = statusBarIcons.get(0);
                if (sourceIcon.sameAs(replacedIcon.icon)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    @Override // android.view.ViewGroup
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        if (child instanceof StatusBarIconView) {
            boolean isReplacingIcon = isReplacingIcon(child);
            final StatusBarIconView icon = (StatusBarIconView) child;
            if (areAnimationsEnabled(icon) && icon.getVisibleState() != 2 && child.getVisibility() == 0 && isReplacingIcon) {
                int animationStartIndex = findFirstViewIndexAfter(icon.getTranslationX());
                int i = this.mAddAnimationStartIndex;
                if (i < 0) {
                    this.mAddAnimationStartIndex = animationStartIndex;
                } else {
                    this.mAddAnimationStartIndex = Math.min(i, animationStartIndex);
                }
            }
            if (!this.mChangingViewPositions) {
                this.mIconStates.remove(child);
                if (areAnimationsEnabled(icon) && !isReplacingIcon) {
                    addTransientView(icon, 0);
                    boolean isIsolatedIcon = child == this.mIsolatedIcon;
                    icon.setVisibleState(2, true, new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NotificationIconContainer$sYOppFQ4vSNRi0SYdFbv716CxNY
                        @Override // java.lang.Runnable
                        public final void run() {
                            NotificationIconContainer.this.lambda$onViewRemoved$0$NotificationIconContainer(icon);
                        }
                    }, isIsolatedIcon ? 110L : 0L);
                }
            }
        }
    }

    public /* synthetic */ void lambda$onViewRemoved$0$NotificationIconContainer(StatusBarIconView icon) {
        removeTransientView(icon);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean areAnimationsEnabled(StatusBarIconView icon) {
        return this.mAnimationsEnabled || icon == this.mIsolatedIcon;
    }

    private int findFirstViewIndexAfter(float translationX) {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view.getTranslationX() > translationX) {
                return i;
            }
        }
        int i2 = getChildCount();
        return i2;
    }

    public void resetViewStates() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            ViewState iconState = this.mIconStates.get(view);
            iconState.initFrom(view);
            StatusBarIconView statusBarIconView = this.mIsolatedIcon;
            iconState.alpha = (statusBarIconView == null || view == statusBarIconView) ? 1.0f : 0.0f;
            iconState.hidden = false;
        }
    }

    public void calculateIconTranslations() {
        int maxVisibleIcons;
        IconState iconState;
        View view;
        int i;
        float f;
        float f2;
        float translationX = getActualPaddingStart();
        int firstOverflowIndex = -1;
        int childCount = getChildCount();
        if (this.mOnLockScreen) {
            maxVisibleIcons = 5;
        } else {
            maxVisibleIcons = this.mIsStaticLayout ? 4 : childCount;
        }
        float layoutEnd = getLayoutEnd();
        float overflowStart = getMaxOverflowStart();
        float f3 = 0.0f;
        this.mVisualOverflowStart = 0.0f;
        this.mFirstVisibleIconState = null;
        int i2 = this.mSpeedBumpIndex;
        int i3 = -1;
        boolean hasAmbient = i2 != -1 && i2 < getChildCount();
        int i4 = 0;
        while (i4 < childCount) {
            View view2 = getChildAt(i4);
            IconState iconState2 = this.mIconStates.get(view2);
            iconState2.xTranslation = translationX;
            if (this.mFirstVisibleIconState == null) {
                this.mFirstVisibleIconState = iconState2;
            }
            int i5 = this.mSpeedBumpIndex;
            boolean forceOverflow = (i5 != i3 && i4 >= i5 && iconState2.iconAppearAmount > f3) || i4 >= maxVisibleIcons;
            boolean noOverflowAfter = i4 == childCount + (-1);
            if (this.mOnLockScreen && (view2 instanceof StatusBarIconView)) {
                f = ((StatusBarIconView) view2).getIconScaleIncreased();
            } else {
                f = 1.0f;
            }
            float drawingScale = f;
            if (this.mOpenedAmount != f3) {
                noOverflowAfter = (!noOverflowAfter || hasAmbient || forceOverflow) ? false : true;
            }
            iconState2.visibleState = 0;
            if (noOverflowAfter) {
                f2 = layoutEnd - this.mIconSize;
            } else {
                f2 = overflowStart - this.mIconSize;
            }
            boolean isOverflowing = translationX > f2;
            if (firstOverflowIndex == -1 && (forceOverflow || isOverflowing)) {
                firstOverflowIndex = (!noOverflowAfter || forceOverflow) ? i4 : i4 - 1;
                this.mVisualOverflowStart = layoutEnd - this.mOverflowWidth;
                if (forceOverflow || this.mIsStaticLayout) {
                    this.mVisualOverflowStart = Math.min(translationX, this.mVisualOverflowStart);
                }
            }
            float f4 = iconState2.iconAppearAmount;
            int firstOverflowIndex2 = firstOverflowIndex;
            int firstOverflowIndex3 = view2.getWidth();
            translationX += f4 * firstOverflowIndex3 * drawingScale;
            i4++;
            firstOverflowIndex = firstOverflowIndex2;
            f3 = 0.0f;
            i3 = -1;
        }
        this.mNumDots = 0;
        if (firstOverflowIndex != -1) {
            translationX = this.mVisualOverflowStart;
            for (int i6 = firstOverflowIndex; i6 < childCount; i6++) {
                IconState iconState3 = this.mIconStates.get(getChildAt(i6));
                int dotWidth = this.mStaticDotDiameter + this.mDotPadding;
                iconState3.xTranslation = translationX;
                int i7 = this.mNumDots;
                if (i7 < 1) {
                    if (i7 == 0 && iconState3.iconAppearAmount < 0.8f) {
                        iconState3.visibleState = 0;
                        i = 1;
                    } else {
                        i = 1;
                        iconState3.visibleState = 1;
                        this.mNumDots++;
                    }
                    translationX += (this.mNumDots == i ? dotWidth * 1 : dotWidth) * iconState3.iconAppearAmount;
                    this.mLastVisibleIconState = iconState3;
                } else {
                    iconState3.visibleState = 2;
                }
            }
        } else if (childCount > 0) {
            View lastChild = getChildAt(childCount - 1);
            this.mLastVisibleIconState = this.mIconStates.get(lastChild);
            this.mFirstVisibleIconState = this.mIconStates.get(getChildAt(0));
        }
        boolean center = this.mOnLockScreen;
        if (center && translationX < getLayoutEnd()) {
            IconState iconState4 = this.mFirstVisibleIconState;
            float initialTranslation = iconState4 == null ? 0.0f : iconState4.xTranslation;
            float contentWidth = 0.0f;
            IconState iconState5 = this.mLastVisibleIconState;
            if (iconState5 != null) {
                float contentWidth2 = iconState5.xTranslation + this.mIconSize;
                contentWidth = Math.min(getWidth(), contentWidth2) - initialTranslation;
            }
            float contentWidth3 = getLayoutEnd();
            float availableSpace = contentWidth3 - getActualPaddingStart();
            float delta = (availableSpace - contentWidth) / 2.0f;
            if (firstOverflowIndex != -1) {
                float deltaIgnoringOverflow = (getLayoutEnd() - this.mVisualOverflowStart) / 2.0f;
                delta = (deltaIgnoringOverflow + delta) / 2.0f;
            }
            int i8 = 0;
            while (i8 < childCount) {
                IconState iconState6 = this.mIconStates.get(getChildAt(i8));
                float translationX2 = translationX;
                float translationX3 = iconState6.xTranslation;
                iconState6.xTranslation = translationX3 + delta;
                i8++;
                translationX = translationX2;
            }
        }
        if (isLayoutRtl()) {
            for (int i9 = 0; i9 < childCount; i9++) {
                IconState iconState7 = this.mIconStates.get(getChildAt(i9));
                iconState7.xTranslation = (getWidth() - iconState7.xTranslation) - view.getWidth();
            }
        }
        StatusBarIconView statusBarIconView = this.mIsolatedIcon;
        if (statusBarIconView != null && (iconState = this.mIconStates.get(statusBarIconView)) != null) {
            iconState.xTranslation = (this.mIsolatedIconLocation.left - this.mAbsolutePosition[0]) - (((1.0f - this.mIsolatedIcon.getIconScale()) * this.mIsolatedIcon.getWidth()) / 2.0f);
            iconState.visibleState = 0;
        }
    }

    private float getLayoutEnd() {
        return getActualWidth() - getActualPaddingEnd();
    }

    private float getActualPaddingEnd() {
        float f = this.mActualPaddingEnd;
        if (f == -2.14748365E9f) {
            return getPaddingEnd();
        }
        return f;
    }

    private float getActualPaddingStart() {
        float f = this.mActualPaddingStart;
        if (f == -2.14748365E9f) {
            return getPaddingStart();
        }
        return f;
    }

    public void setIsStaticLayout(boolean isStaticLayout) {
        this.mIsStaticLayout = isStaticLayout;
    }

    public void setActualLayoutWidth(int actualLayoutWidth) {
        this.mActualLayoutWidth = actualLayoutWidth;
    }

    public void setActualPaddingEnd(float paddingEnd) {
        this.mActualPaddingEnd = paddingEnd;
    }

    public void setActualPaddingStart(float paddingStart) {
        this.mActualPaddingStart = paddingStart;
    }

    public int getActualWidth() {
        int i = this.mActualLayoutWidth;
        if (i == Integer.MIN_VALUE) {
            return getWidth();
        }
        return i;
    }

    public int getFinalTranslationX() {
        if (this.mLastVisibleIconState == null) {
            return 0;
        }
        int translation = (int) (isLayoutRtl() ? getWidth() - this.mLastVisibleIconState.xTranslation : this.mLastVisibleIconState.xTranslation + this.mIconSize);
        return Math.min(getWidth(), translation);
    }

    private float getMaxOverflowStart() {
        return getLayoutEnd() - this.mOverflowWidth;
    }

    public void setChangingViewPositions(boolean changingViewPositions) {
        this.mChangingViewPositions = changingViewPositions;
    }

    public void setDozing(boolean dozing, boolean fade, long delay) {
        this.mDozing = dozing;
        this.mDisallowNextAnimation |= !fade;
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof StatusBarIconView) {
                ((StatusBarIconView) view).setDozing(dozing, fade, delay);
            }
        }
    }

    public IconState getIconState(StatusBarIconView icon) {
        return this.mIconStates.get(icon);
    }

    public void setSpeedBumpIndex(int speedBumpIndex) {
        this.mSpeedBumpIndex = speedBumpIndex;
    }

    public void setOpenedAmount(float expandAmount) {
        this.mOpenedAmount = expandAmount;
    }

    public boolean hasOverflow() {
        return this.mNumDots > 0;
    }

    public boolean hasPartialOverflow() {
        int i = this.mNumDots;
        return i > 0 && i < 1;
    }

    public int getPartialOverflowExtraPadding() {
        if (!hasPartialOverflow()) {
            return 0;
        }
        int partialOverflowAmount = (1 - this.mNumDots) * (this.mStaticDotDiameter + this.mDotPadding);
        int adjustedWidth = getFinalTranslationX() + partialOverflowAmount;
        if (adjustedWidth > getWidth()) {
            return getWidth() - getFinalTranslationX();
        }
        return partialOverflowAmount;
    }

    public int getNoOverflowExtraPadding() {
        if (this.mNumDots != 0) {
            return 0;
        }
        int collapsedPadding = this.mOverflowWidth;
        if (getFinalTranslationX() + collapsedPadding > getWidth()) {
            return getWidth() - getFinalTranslationX();
        }
        return collapsedPadding;
    }

    public int getIconSize() {
        return this.mIconSize;
    }

    public void setAnimationsEnabled(boolean enabled) {
        if (!enabled && this.mAnimationsEnabled) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                ViewState childState = this.mIconStates.get(child);
                if (childState != null) {
                    childState.cancelAnimations(child);
                    childState.applyToView(child);
                }
            }
        }
        this.mAnimationsEnabled = enabled;
    }

    public void setReplacingIcons(ArrayMap<String, ArrayList<StatusBarIcon>> replacingIcons) {
        this.mReplacingIcons = replacingIcons;
    }

    public void showIconIsolated(StatusBarIconView icon, boolean animated) {
        if (animated) {
            this.mIsolatedIconForAnimation = icon != null ? icon : this.mIsolatedIcon;
        }
        this.mIsolatedIcon = icon;
        updateState();
    }

    public void setIsolatedIconLocation(Rect isolatedIconLocation, boolean requireUpdate) {
        this.mIsolatedIconLocation = isolatedIconLocation;
        if (requireUpdate) {
            updateState();
        }
    }

    public void setOnLockScreen(boolean onLockScreen) {
        this.mOnLockScreen = onLockScreen;
    }

    /* loaded from: classes21.dex */
    public class IconState extends ViewState {
        public static final int NO_VALUE = Integer.MIN_VALUE;
        public boolean isLastExpandIcon;
        private boolean justReplaced;
        public boolean needsCannedAnimation;
        public boolean noAnimations;
        public boolean translateContent;
        public boolean useFullTransitionAmount;
        public boolean useLinearTransitionAmount;
        public int visibleState;
        public float iconAppearAmount = 1.0f;
        public float clampedAppearAmount = 1.0f;
        public boolean justAdded = true;
        public int iconColor = 0;
        public int customTransformHeight = Integer.MIN_VALUE;

        public IconState() {
        }

        @Override // com.android.systemui.statusbar.notification.stack.ViewState
        public void applyToView(View view) {
            if (view instanceof StatusBarIconView) {
                StatusBarIconView icon = (StatusBarIconView) view;
                boolean animate = false;
                AnimationProperties animationProperties = null;
                boolean animationsAllowed = (!NotificationIconContainer.this.areAnimationsEnabled(icon) || NotificationIconContainer.this.mDisallowNextAnimation || this.noAnimations) ? false : true;
                if (animationsAllowed) {
                    if (this.justAdded || this.justReplaced) {
                        super.applyToView(icon);
                        if (this.justAdded && this.iconAppearAmount != 0.0f) {
                            icon.setAlpha(0.0f);
                            icon.setVisibleState(2, false);
                            animationProperties = NotificationIconContainer.ADD_ICON_PROPERTIES;
                            animate = true;
                        }
                    } else if (this.visibleState != icon.getVisibleState()) {
                        animationProperties = NotificationIconContainer.DOT_ANIMATION_PROPERTIES;
                        animate = true;
                    }
                    if (!animate && NotificationIconContainer.this.mAddAnimationStartIndex >= 0 && NotificationIconContainer.this.indexOfChild(view) >= NotificationIconContainer.this.mAddAnimationStartIndex && (icon.getVisibleState() != 2 || this.visibleState != 2)) {
                        animationProperties = NotificationIconContainer.DOT_ANIMATION_PROPERTIES;
                        animate = true;
                    }
                    if (this.needsCannedAnimation) {
                        AnimationFilter animationFilter = NotificationIconContainer.sTempProperties.getAnimationFilter();
                        animationFilter.reset();
                        animationFilter.combineFilter(NotificationIconContainer.ICON_ANIMATION_PROPERTIES.getAnimationFilter());
                        NotificationIconContainer.sTempProperties.resetCustomInterpolators();
                        NotificationIconContainer.sTempProperties.combineCustomInterpolators(NotificationIconContainer.ICON_ANIMATION_PROPERTIES);
                        if (animationProperties != null) {
                            animationFilter.combineFilter(animationProperties.getAnimationFilter());
                            NotificationIconContainer.sTempProperties.combineCustomInterpolators(animationProperties);
                        }
                        animationProperties = NotificationIconContainer.sTempProperties;
                        animationProperties.setDuration(100L);
                        animate = true;
                        NotificationIconContainer notificationIconContainer = NotificationIconContainer.this;
                        notificationIconContainer.mCannedAnimationStartIndex = notificationIconContainer.indexOfChild(view);
                    }
                    if (!animate && NotificationIconContainer.this.mCannedAnimationStartIndex >= 0 && NotificationIconContainer.this.indexOfChild(view) > NotificationIconContainer.this.mCannedAnimationStartIndex && (icon.getVisibleState() != 2 || this.visibleState != 2)) {
                        AnimationFilter animationFilter2 = NotificationIconContainer.sTempProperties.getAnimationFilter();
                        animationFilter2.reset();
                        animationFilter2.animateX();
                        NotificationIconContainer.sTempProperties.resetCustomInterpolators();
                        animationProperties = NotificationIconContainer.sTempProperties;
                        animationProperties.setDuration(100L);
                        animate = true;
                    }
                    if (NotificationIconContainer.this.mIsolatedIconForAnimation != null) {
                        if (view == NotificationIconContainer.this.mIsolatedIconForAnimation) {
                            animationProperties = NotificationIconContainer.UNISOLATION_PROPERTY;
                            animationProperties.setDelay(NotificationIconContainer.this.mIsolatedIcon == null ? 0L : 100L);
                        } else {
                            animationProperties = NotificationIconContainer.UNISOLATION_PROPERTY_OTHERS;
                            animationProperties.setDelay(NotificationIconContainer.this.mIsolatedIcon != null ? 0L : 100L);
                        }
                        animate = true;
                    }
                }
                icon.setVisibleState(this.visibleState, animationsAllowed);
                icon.setIconColor(this.iconColor, this.needsCannedAnimation && animationsAllowed);
                if (animate) {
                    animateTo(icon, animationProperties);
                } else {
                    super.applyToView(view);
                }
                boolean inShelf = this.iconAppearAmount == 1.0f;
                icon.setIsInShelf(inShelf);
            }
            this.justAdded = false;
            this.justReplaced = false;
            this.needsCannedAnimation = false;
        }

        public boolean hasCustomTransformHeight() {
            return this.isLastExpandIcon && this.customTransformHeight != Integer.MIN_VALUE;
        }

        @Override // com.android.systemui.statusbar.notification.stack.ViewState
        public void initFrom(View view) {
            super.initFrom(view);
            if (view instanceof StatusBarIconView) {
                this.iconColor = ((StatusBarIconView) view).getStaticDrawableColor();
            }
        }
    }
}
