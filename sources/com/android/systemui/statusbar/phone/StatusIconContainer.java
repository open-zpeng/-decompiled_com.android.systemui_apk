package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import com.android.keyguard.AlphaOptimizedLinearLayout;
import com.android.systemui.R;
import com.android.systemui.statusbar.StatusIconDisplayable;
import com.android.systemui.statusbar.notification.stack.AnimationFilter;
import com.android.systemui.statusbar.notification.stack.AnimationProperties;
import com.android.systemui.statusbar.notification.stack.ViewState;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes21.dex */
public class StatusIconContainer extends AlphaOptimizedLinearLayout {
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_OVERFLOW = false;
    private static final int MAX_DOTS = 1;
    private static final int MAX_ICONS = 7;
    private static final String TAG = "StatusIconContainer";
    private int mDotPadding;
    private int mIconDotFrameWidth;
    private ArrayList<String> mIgnoredSlots;
    private ArrayList<StatusIconState> mLayoutStates;
    private ArrayList<View> mMeasureViews;
    private boolean mNeedsUnderflow;
    private boolean mShouldRestrictIcons;
    private int mStaticDotDiameter;
    private int mUnderflowStart;
    private int mUnderflowWidth;
    private static final AnimationProperties ADD_ICON_PROPERTIES = new AnimationProperties() { // from class: com.android.systemui.statusbar.phone.StatusIconContainer.1
        private AnimationFilter mAnimationFilter = new AnimationFilter().animateAlpha();

        @Override // com.android.systemui.statusbar.notification.stack.AnimationProperties
        public AnimationFilter getAnimationFilter() {
            return this.mAnimationFilter;
        }
    }.setDuration(200).setDelay(50);
    private static final AnimationProperties X_ANIMATION_PROPERTIES = new AnimationProperties() { // from class: com.android.systemui.statusbar.phone.StatusIconContainer.2
        private AnimationFilter mAnimationFilter = new AnimationFilter().animateX();

        @Override // com.android.systemui.statusbar.notification.stack.AnimationProperties
        public AnimationFilter getAnimationFilter() {
            return this.mAnimationFilter;
        }
    }.setDuration(200);
    private static final AnimationProperties ANIMATE_ALL_PROPERTIES = new AnimationProperties() { // from class: com.android.systemui.statusbar.phone.StatusIconContainer.3
        private AnimationFilter mAnimationFilter = new AnimationFilter().animateX().animateY().animateAlpha().animateScale();

        @Override // com.android.systemui.statusbar.notification.stack.AnimationProperties
        public AnimationFilter getAnimationFilter() {
            return this.mAnimationFilter;
        }
    }.setDuration(200);

    public StatusIconContainer(Context context) {
        this(context, null);
    }

    public StatusIconContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mUnderflowStart = 0;
        this.mShouldRestrictIcons = true;
        this.mLayoutStates = new ArrayList<>();
        this.mMeasureViews = new ArrayList<>();
        this.mIgnoredSlots = new ArrayList<>();
        initDimens();
        setWillNotDraw(true);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setShouldRestrictIcons(boolean should) {
        this.mShouldRestrictIcons = should;
    }

    public boolean isRestrictingIcons() {
        return this.mShouldRestrictIcons;
    }

    private void initDimens() {
        this.mIconDotFrameWidth = getResources().getDimensionPixelSize(17105441);
        this.mDotPadding = getResources().getDimensionPixelSize(R.dimen.overflow_icon_dot_padding);
        int radius = getResources().getDimensionPixelSize(R.dimen.overflow_dot_radius);
        this.mStaticDotDiameter = radius * 2;
        this.mUnderflowWidth = this.mIconDotFrameWidth + ((this.mStaticDotDiameter + this.mDotPadding) * 0);
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        float midY = getHeight() / 2.0f;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            int top = (int) (midY - (height / 2.0f));
            child.layout(0, top, width, top + height);
        }
        resetViewStates();
        calculateIconTranslations();
        applyIconStates();
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.mMeasureViews.clear();
        int mode = View.MeasureSpec.getMode(widthMeasureSpec);
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            StatusIconDisplayable icon = (StatusIconDisplayable) getChildAt(i);
            if (icon.isIconVisible() && !icon.isIconBlocked() && !this.mIgnoredSlots.contains(icon.getSlot())) {
                this.mMeasureViews.add((View) icon);
            }
        }
        int visibleCount = this.mMeasureViews.size();
        int maxVisible = visibleCount <= 7 ? 7 : 6;
        int totalWidth = this.mPaddingLeft + this.mPaddingRight;
        boolean trackWidth = true;
        boolean z = false;
        int childWidthSpec = View.MeasureSpec.makeMeasureSpec(width, 0);
        if (this.mShouldRestrictIcons && visibleCount > 7) {
            z = true;
        }
        this.mNeedsUnderflow = z;
        for (int i2 = 0; i2 < this.mMeasureViews.size(); i2++) {
            View child = this.mMeasureViews.get((visibleCount - i2) - 1);
            measureChild(child, childWidthSpec, heightMeasureSpec);
            if (this.mShouldRestrictIcons) {
                if (i2 < maxVisible && trackWidth) {
                    totalWidth += getViewTotalMeasuredWidth(child);
                } else if (trackWidth) {
                    totalWidth += this.mUnderflowWidth;
                    trackWidth = false;
                }
            } else {
                totalWidth += getViewTotalMeasuredWidth(child);
            }
        }
        if (mode == 1073741824) {
            if (!this.mNeedsUnderflow && totalWidth > width) {
                this.mNeedsUnderflow = true;
            }
            setMeasuredDimension(width, View.MeasureSpec.getSize(heightMeasureSpec));
            return;
        }
        if (mode == Integer.MIN_VALUE && totalWidth > width) {
            this.mNeedsUnderflow = true;
            totalWidth = width;
        }
        setMeasuredDimension(totalWidth, View.MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override // android.view.ViewGroup
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        StatusIconState vs = new StatusIconState();
        vs.justAdded = true;
        child.setTag(R.id.status_bar_view_state_tag, vs);
    }

    @Override // android.view.ViewGroup
    public void onViewRemoved(View child) {
        super.onViewRemoved(child);
        child.setTag(R.id.status_bar_view_state_tag, null);
    }

    public void addIgnoredSlot(String slotName) {
        addIgnoredSlotInternal(slotName);
        requestLayout();
    }

    public void addIgnoredSlots(List<String> slots) {
        for (String slot : slots) {
            addIgnoredSlotInternal(slot);
        }
        requestLayout();
    }

    private void addIgnoredSlotInternal(String slotName) {
        if (!this.mIgnoredSlots.contains(slotName)) {
            this.mIgnoredSlots.add(slotName);
        }
    }

    public void removeIgnoredSlot(String slotName) {
        if (this.mIgnoredSlots.contains(slotName)) {
            this.mIgnoredSlots.remove(slotName);
        }
        requestLayout();
    }

    public void setIgnoredSlots(List<String> slots) {
        this.mIgnoredSlots.clear();
        addIgnoredSlots(slots);
    }

    private void calculateIconTranslations() {
        View child;
        this.mLayoutStates.clear();
        float width = getWidth();
        float translationX = width - getPaddingEnd();
        float contentStart = getPaddingStart();
        int childCount = getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View child2 = getChildAt(i);
            StatusIconDisplayable iconView = (StatusIconDisplayable) child2;
            StatusIconState childState = getViewStateFromChild(child2);
            if (!iconView.isIconVisible() || iconView.isIconBlocked() || this.mIgnoredSlots.contains(iconView.getSlot())) {
                childState.visibleState = 2;
            } else {
                childState.visibleState = 0;
                childState.xTranslation = translationX - getViewTotalWidth(child2);
                this.mLayoutStates.add(0, childState);
                translationX -= getViewTotalWidth(child2);
            }
        }
        int totalVisible = this.mLayoutStates.size();
        int maxVisible = totalVisible > 7 ? 6 : 7;
        this.mUnderflowStart = 0;
        int visible = 0;
        int firstUnderflowIndex = -1;
        for (int i2 = totalVisible - 1; i2 >= 0; i2--) {
            StatusIconState state = this.mLayoutStates.get(i2);
            if ((!this.mNeedsUnderflow || state.xTranslation >= this.mUnderflowWidth + contentStart) && (!this.mShouldRestrictIcons || visible < maxVisible)) {
                this.mUnderflowStart = (int) Math.max(contentStart, state.xTranslation - this.mUnderflowWidth);
                visible++;
            } else {
                firstUnderflowIndex = i2;
                break;
            }
        }
        if (firstUnderflowIndex != -1) {
            int totalDots = 0;
            int dotWidth = this.mStaticDotDiameter + this.mDotPadding;
            int dotOffset = (this.mUnderflowStart + this.mUnderflowWidth) - this.mIconDotFrameWidth;
            for (int i3 = firstUnderflowIndex; i3 >= 0; i3--) {
                StatusIconState state2 = this.mLayoutStates.get(i3);
                if (totalDots < 1) {
                    state2.xTranslation = dotOffset;
                    state2.visibleState = 1;
                    dotOffset -= dotWidth;
                    totalDots++;
                } else {
                    state2.visibleState = 2;
                }
            }
        }
        if (isLayoutRtl()) {
            for (int i4 = 0; i4 < childCount; i4++) {
                StatusIconState state3 = getViewStateFromChild(getChildAt(i4));
                state3.xTranslation = (width - state3.xTranslation) - child.getWidth();
            }
        }
    }

    private void applyIconStates() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            StatusIconState vs = getViewStateFromChild(child);
            if (vs != null) {
                vs.applyToView(child);
            }
        }
    }

    private void resetViewStates() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            StatusIconState vs = getViewStateFromChild(child);
            if (vs != null) {
                vs.initFrom(child);
                vs.alpha = 1.0f;
                vs.hidden = false;
            }
        }
    }

    private static StatusIconState getViewStateFromChild(View child) {
        return (StatusIconState) child.getTag(R.id.status_bar_view_state_tag);
    }

    private static int getViewTotalMeasuredWidth(View child) {
        return child.getMeasuredWidth() + child.getPaddingStart() + child.getPaddingEnd();
    }

    private static int getViewTotalWidth(View child) {
        return child.getWidth() + child.getPaddingStart() + child.getPaddingEnd();
    }

    /* loaded from: classes21.dex */
    public static class StatusIconState extends ViewState {
        public int visibleState = 0;
        public boolean justAdded = true;
        float distanceToViewEnd = -1.0f;

        @Override // com.android.systemui.statusbar.notification.stack.ViewState
        public void applyToView(View view) {
            float parentWidth = 0.0f;
            if (view.getParent() instanceof View) {
                parentWidth = ((View) view.getParent()).getWidth();
            }
            float currentDistanceToEnd = parentWidth - this.xTranslation;
            if (!(view instanceof StatusIconDisplayable)) {
                return;
            }
            StatusIconDisplayable icon = (StatusIconDisplayable) view;
            AnimationProperties animationProperties = null;
            boolean animateVisibility = true;
            if (this.justAdded || (icon.getVisibleState() == 2 && this.visibleState == 0)) {
                super.applyToView(view);
                view.setAlpha(0.0f);
                icon.setVisibleState(2);
                animationProperties = StatusIconContainer.ADD_ICON_PROPERTIES;
            } else {
                int visibleState = icon.getVisibleState();
                int i = this.visibleState;
                if (visibleState != i) {
                    if (icon.getVisibleState() != 0 || this.visibleState != 2) {
                        animationProperties = StatusIconContainer.ANIMATE_ALL_PROPERTIES;
                    } else {
                        animateVisibility = false;
                    }
                } else if (i != 2 && this.distanceToViewEnd != currentDistanceToEnd) {
                    animationProperties = StatusIconContainer.X_ANIMATION_PROPERTIES;
                }
            }
            icon.setVisibleState(this.visibleState, animateVisibility);
            if (animationProperties != null) {
                animateTo(view, animationProperties);
            } else {
                super.applyToView(view);
            }
            this.justAdded = false;
            this.distanceToViewEnd = currentDistanceToEnd;
        }
    }
}
