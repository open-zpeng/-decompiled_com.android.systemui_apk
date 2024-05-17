package com.android.systemui.statusbar.notification.row;

import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
import com.android.systemui.Dumpable;
import com.android.systemui.statusbar.notification.stack.ExpandableViewState;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes21.dex */
public abstract class ExpandableView extends FrameLayout implements Dumpable {
    public static final float NO_ROUNDNESS = -1.0f;
    private static final String TAG = "ExpandableView";
    private static Rect mClipRect = new Rect();
    private int mActualHeight;
    private boolean mChangingPosition;
    protected int mClipBottomAmount;
    private boolean mClipToActualHeight;
    protected int mClipTopAmount;
    protected float mExtraWidthForClipping;
    private boolean mInShelf;
    private ArrayList<View> mMatchParentViews;
    private int mMinClipTopAmount;
    protected int mMinimumHeightForClipping;
    protected OnHeightChangedListener mOnHeightChangedListener;
    private boolean mTransformingInShelf;
    private ViewGroup mTransientContainer;
    private final ExpandableViewState mViewState;
    private boolean mWillBeGone;

    /* loaded from: classes21.dex */
    public interface OnHeightChangedListener {
        void onHeightChanged(ExpandableView expandableView, boolean z);

        void onReset(ExpandableView expandableView);
    }

    public abstract void performAddAnimation(long j, long j2, boolean z);

    public abstract long performRemoveAnimation(long j, long j2, float f, boolean z, float f2, Runnable runnable, AnimatorListenerAdapter animatorListenerAdapter);

    public ExpandableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mMinimumHeightForClipping = 0;
        this.mExtraWidthForClipping = 0.0f;
        this.mMatchParentViews = new ArrayList<>();
        this.mMinClipTopAmount = 0;
        this.mClipToActualHeight = true;
        this.mChangingPosition = false;
        this.mViewState = createExpandableViewState();
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int makeMeasureSpec;
        int givenSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int viewHorizontalPadding = getPaddingStart() + getPaddingEnd();
        int ownMaxHeight = Integer.MAX_VALUE;
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != 0 && givenSize != 0) {
            ownMaxHeight = Math.min(givenSize, Integer.MAX_VALUE);
        }
        int newHeightSpec = View.MeasureSpec.makeMeasureSpec(ownMaxHeight, Integer.MIN_VALUE);
        int maxChildHeight = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                int childHeightSpec = newHeightSpec;
                ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
                if (layoutParams.height != -1) {
                    if (layoutParams.height >= 0) {
                        if (layoutParams.height > ownMaxHeight) {
                            makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(ownMaxHeight, 1073741824);
                        } else {
                            makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(layoutParams.height, 1073741824);
                        }
                        childHeightSpec = makeMeasureSpec;
                    }
                    child.measure(getChildMeasureSpec(widthMeasureSpec, viewHorizontalPadding, layoutParams.width), childHeightSpec);
                    int childHeight = child.getMeasuredHeight();
                    maxChildHeight = Math.max(maxChildHeight, childHeight);
                } else {
                    this.mMatchParentViews.add(child);
                }
            }
        }
        int ownHeight = heightMode == 1073741824 ? givenSize : Math.min(ownMaxHeight, maxChildHeight);
        int newHeightSpec2 = View.MeasureSpec.makeMeasureSpec(ownHeight, 1073741824);
        Iterator<View> it = this.mMatchParentViews.iterator();
        while (it.hasNext()) {
            View child2 = it.next();
            child2.measure(getChildMeasureSpec(widthMeasureSpec, viewHorizontalPadding, child2.getLayoutParams().width), newHeightSpec2);
        }
        this.mMatchParentViews.clear();
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(width, ownHeight);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateClipping();
    }

    public boolean pointInView(float localX, float localY, float slop) {
        float top = this.mClipTopAmount;
        float bottom = this.mActualHeight;
        return localX >= (-slop) && localY >= top - slop && localX < ((float) (this.mRight - this.mLeft)) + slop && localY < bottom + slop;
    }

    public void setActualHeight(int actualHeight, boolean notifyListeners) {
        this.mActualHeight = actualHeight;
        updateClipping();
        if (notifyListeners) {
            notifyHeightChanged(false);
        }
    }

    public void setDistanceToTopRoundness(float distanceToTopRoundness) {
    }

    public void setActualHeight(int actualHeight) {
        setActualHeight(actualHeight, true);
    }

    public int getActualHeight() {
        return this.mActualHeight;
    }

    public boolean isExpandAnimationRunning() {
        return false;
    }

    public int getMaxContentHeight() {
        return getHeight();
    }

    public int getMinHeight() {
        return getMinHeight(false);
    }

    public int getMinHeight(boolean ignoreTemporaryStates) {
        return getHeight();
    }

    public int getCollapsedHeight() {
        return getHeight();
    }

    public void setDimmed(boolean dimmed, boolean fade) {
    }

    public boolean isRemoved() {
        return false;
    }

    public void setHideSensitiveForIntrinsicHeight(boolean hideSensitive) {
    }

    public void setHideSensitive(boolean hideSensitive, boolean animated, long delay, long duration) {
    }

    public int getIntrinsicHeight() {
        return getHeight();
    }

    public void setClipTopAmount(int clipTopAmount) {
        this.mClipTopAmount = clipTopAmount;
        updateClipping();
    }

    public void setClipBottomAmount(int clipBottomAmount) {
        this.mClipBottomAmount = clipBottomAmount;
        updateClipping();
    }

    public int getClipTopAmount() {
        return this.mClipTopAmount;
    }

    public int getClipBottomAmount() {
        return this.mClipBottomAmount;
    }

    public void setOnHeightChangedListener(OnHeightChangedListener listener) {
        this.mOnHeightChangedListener = listener;
    }

    public boolean isContentExpandable() {
        return false;
    }

    public void notifyHeightChanged(boolean needsAnimation) {
        OnHeightChangedListener onHeightChangedListener = this.mOnHeightChangedListener;
        if (onHeightChangedListener != null) {
            onHeightChangedListener.onHeightChanged(this, needsAnimation);
        }
    }

    public boolean isTransparent() {
        return false;
    }

    public void setBelowSpeedBump(boolean below) {
    }

    public int getPinnedHeadsUpHeight() {
        return getIntrinsicHeight();
    }

    public void setTranslation(float translation) {
        setTranslationX(translation);
    }

    public float getTranslation() {
        return getTranslationX();
    }

    public void onHeightReset() {
        OnHeightChangedListener onHeightChangedListener = this.mOnHeightChangedListener;
        if (onHeightChangedListener != null) {
            onHeightChangedListener.onReset(this);
        }
    }

    @Override // android.view.View
    public void getDrawingRect(Rect outRect) {
        super.getDrawingRect(outRect);
        outRect.left = (int) (outRect.left + getTranslationX());
        outRect.right = (int) (outRect.right + getTranslationX());
        outRect.bottom = (int) (outRect.top + getTranslationY() + getActualHeight());
        outRect.top = (int) (outRect.top + getTranslationY() + getClipTopAmount());
    }

    public void getBoundsOnScreen(Rect outRect, boolean clipToParent) {
        super.getBoundsOnScreen(outRect, clipToParent);
        if (getTop() + getTranslationY() < 0.0f) {
            outRect.top = (int) (outRect.top + getTop() + getTranslationY());
        }
        outRect.bottom = outRect.top + getActualHeight();
        outRect.top += getClipTopAmount();
    }

    public boolean isSummaryWithChildren() {
        return false;
    }

    public boolean areChildrenExpanded() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateClipping() {
        if (this.mClipToActualHeight && shouldClipToActualHeight()) {
            int top = getClipTopAmount();
            int bottom = Math.max(Math.max((getActualHeight() + getExtraBottomPadding()) - this.mClipBottomAmount, top), this.mMinimumHeightForClipping);
            int halfExtraWidth = (int) (this.mExtraWidthForClipping / 2.0f);
            mClipRect.set(-halfExtraWidth, top, getWidth() + halfExtraWidth, bottom);
            setClipBounds(mClipRect);
            return;
        }
        setClipBounds(null);
    }

    public void setMinimumHeightForClipping(int minimumHeightForClipping) {
        this.mMinimumHeightForClipping = minimumHeightForClipping;
        updateClipping();
    }

    public void setExtraWidthForClipping(float extraWidthForClipping) {
        this.mExtraWidthForClipping = extraWidthForClipping;
        updateClipping();
    }

    public float getHeaderVisibleAmount() {
        return 1.0f;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean shouldClipToActualHeight() {
        return true;
    }

    public void setClipToActualHeight(boolean clipToActualHeight) {
        this.mClipToActualHeight = clipToActualHeight;
        updateClipping();
    }

    public boolean willBeGone() {
        return this.mWillBeGone;
    }

    public void setWillBeGone(boolean willBeGone) {
        this.mWillBeGone = willBeGone;
    }

    public int getMinClipTopAmount() {
        return this.mMinClipTopAmount;
    }

    public void setMinClipTopAmount(int minClipTopAmount) {
        this.mMinClipTopAmount = minClipTopAmount;
    }

    @Override // android.view.View
    public void setLayerType(int layerType, Paint paint) {
        if (hasOverlappingRendering()) {
            super.setLayerType(layerType, paint);
        }
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return super.hasOverlappingRendering() && getActualHeight() <= getHeight();
    }

    public float getIncreasedPaddingAmount() {
        return 0.0f;
    }

    public boolean mustStayOnScreen() {
        return false;
    }

    public void setFakeShadowIntensity(float shadowIntensity, float outlineAlpha, int shadowYEnd, int outlineTranslation) {
    }

    public float getOutlineAlpha() {
        return 0.0f;
    }

    public int getOutlineTranslation() {
        return 0;
    }

    public void setChangingPosition(boolean changingPosition) {
        this.mChangingPosition = changingPosition;
    }

    public boolean isChangingPosition() {
        return this.mChangingPosition;
    }

    public void setTransientContainer(ViewGroup transientContainer) {
        this.mTransientContainer = transientContainer;
    }

    public ViewGroup getTransientContainer() {
        return this.mTransientContainer;
    }

    public int getExtraBottomPadding() {
        return 0;
    }

    public boolean isGroupExpansionChanging() {
        return false;
    }

    public boolean isGroupExpanded() {
        return false;
    }

    public void setHeadsUpIsVisible() {
    }

    public boolean showingPulsing() {
        return false;
    }

    public boolean isChildInGroup() {
        return false;
    }

    public void setActualHeightAnimating(boolean animating) {
    }

    protected ExpandableViewState createExpandableViewState() {
        return new ExpandableViewState();
    }

    public ExpandableViewState resetViewState() {
        this.mViewState.height = getIntrinsicHeight();
        this.mViewState.gone = getVisibility() == 8;
        ExpandableViewState expandableViewState = this.mViewState;
        expandableViewState.alpha = 1.0f;
        expandableViewState.notGoneIndex = -1;
        expandableViewState.xTranslation = getTranslationX();
        ExpandableViewState expandableViewState2 = this.mViewState;
        expandableViewState2.hidden = false;
        expandableViewState2.scaleX = getScaleX();
        this.mViewState.scaleY = getScaleY();
        ExpandableViewState expandableViewState3 = this.mViewState;
        expandableViewState3.inShelf = false;
        expandableViewState3.headsUpIsVisible = false;
        if (this instanceof ExpandableNotificationRow) {
            ExpandableNotificationRow row = (ExpandableNotificationRow) this;
            List<ExpandableNotificationRow> children = row.getNotificationChildren();
            if (row.isSummaryWithChildren() && children != null) {
                for (ExpandableNotificationRow childRow : children) {
                    childRow.resetViewState();
                }
            }
        }
        return this.mViewState;
    }

    @Nullable
    public ExpandableViewState getViewState() {
        return this.mViewState;
    }

    public void applyViewState() {
        if (!this.mViewState.gone) {
            this.mViewState.applyToView(this);
        }
    }

    public boolean hasNoContentHeight() {
        return false;
    }

    public void setInShelf(boolean inShelf) {
        this.mInShelf = inShelf;
    }

    public boolean isInShelf() {
        return this.mInShelf;
    }

    public void setTransformingInShelf(boolean transformingInShelf) {
        this.mTransformingInShelf = transformingInShelf;
    }

    public boolean isTransformingIntoShelf() {
        return this.mTransformingInShelf;
    }

    public boolean isAboveShelf() {
        return false;
    }

    public boolean hasExpandingChild() {
        return false;
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
    }
}
