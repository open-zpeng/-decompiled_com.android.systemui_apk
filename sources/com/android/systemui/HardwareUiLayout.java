package com.android.systemui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.util.leak.RotationUtils;
/* loaded from: classes21.dex */
public class HardwareUiLayout extends MultiListLayout implements TunerService.Tunable {
    private static final String EDGE_BLEED = "sysui_hwui_edge_bleed";
    private static final String ROUNDED_DIVIDER = "sysui_hwui_rounded_divider";
    private boolean mAnimating;
    private AnimatorSet mAnimation;
    private Animator mAnimator;
    private boolean mCollapse;
    private View mDivision;
    private boolean mEdgeBleed;
    private int mEndPoint;
    private final ViewTreeObserver.OnComputeInternalInsetsListener mInsetsListener;
    private ViewGroup mList;
    private HardwareBgDrawable mListBackground;
    private int mOldHeight;
    private boolean mRotatedBackground;
    private boolean mRoundedDivider;
    private ViewGroup mSeparatedView;
    private HardwareBgDrawable mSeparatedViewBackground;
    private boolean mSwapOrientation;
    private final int[] mTmp2;

    public HardwareUiLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTmp2 = new int[2];
        this.mSwapOrientation = true;
        this.mInsetsListener = new ViewTreeObserver.OnComputeInternalInsetsListener() { // from class: com.android.systemui.-$$Lambda$HardwareUiLayout$Wopid983i_OFN_0DzaqL8EnwZHc
            public final void onComputeInternalInsets(ViewTreeObserver.InternalInsetsInfo internalInsetsInfo) {
                HardwareUiLayout.this.lambda$new$5$HardwareUiLayout(internalInsetsInfo);
            }
        };
        this.mRotation = 0;
        updateSettings();
    }

    @Override // com.android.systemui.MultiListLayout
    protected ViewGroup getSeparatedView() {
        return (ViewGroup) findViewById(R.id.separated_button);
    }

    @Override // com.android.systemui.MultiListLayout
    protected ViewGroup getListView() {
        return (ViewGroup) findViewById(16908298);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateSettings();
        ((TunerService) Dependency.get(TunerService.class)).addTunable(this, EDGE_BLEED, ROUNDED_DIVIDER);
        getViewTreeObserver().addOnComputeInternalInsetsListener(this.mInsetsListener);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnComputeInternalInsetsListener(this.mInsetsListener);
        ((TunerService) Dependency.get(TunerService.class)).removeTunable(this);
    }

    @Override // com.android.systemui.tuner.TunerService.Tunable
    public void onTuningChanged(String key, String newValue) {
        updateSettings();
    }

    private void updateSettings() {
        this.mEdgeBleed = Settings.Secure.getInt(getContext().getContentResolver(), EDGE_BLEED, 0) != 0;
        this.mRoundedDivider = Settings.Secure.getInt(getContext().getContentResolver(), ROUNDED_DIVIDER, 0) != 0;
        updateEdgeMargin(this.mEdgeBleed ? 0 : getEdgePadding());
        this.mListBackground = new HardwareBgDrawable(this.mRoundedDivider, !this.mEdgeBleed, getContext());
        this.mSeparatedViewBackground = new HardwareBgDrawable(this.mRoundedDivider, true ^ this.mEdgeBleed, getContext());
        ViewGroup viewGroup = this.mList;
        if (viewGroup != null) {
            viewGroup.setBackground(this.mListBackground);
            this.mSeparatedView.setBackground(this.mSeparatedViewBackground);
            requestLayout();
        }
    }

    private void updateEdgeMargin(int edge) {
        ViewGroup viewGroup = this.mList;
        if (viewGroup != null) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) viewGroup.getLayoutParams();
            if (this.mRotation == 1) {
                params.topMargin = edge;
            } else if (this.mRotation == 2) {
                params.bottomMargin = edge;
            } else {
                params.rightMargin = edge;
            }
            this.mList.setLayoutParams(params);
        }
        ViewGroup viewGroup2 = this.mSeparatedView;
        if (viewGroup2 != null) {
            ViewGroup.MarginLayoutParams params2 = (ViewGroup.MarginLayoutParams) viewGroup2.getLayoutParams();
            if (this.mRotation == 1) {
                params2.topMargin = edge;
            } else if (this.mRotation == 2) {
                params2.bottomMargin = edge;
            } else {
                params2.rightMargin = edge;
            }
            this.mSeparatedView.setLayoutParams(params2);
        }
    }

    private int getEdgePadding() {
        return getContext().getResources().getDimensionPixelSize(R.dimen.edge_margin);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.mList == null) {
            if (getChildCount() != 0) {
                this.mList = getListView();
                this.mList.setBackground(this.mListBackground);
                this.mSeparatedView = getSeparatedView();
                this.mSeparatedView.setBackground(this.mSeparatedViewBackground);
                updateEdgeMargin(this.mEdgeBleed ? 0 : getEdgePadding());
                this.mOldHeight = this.mList.getMeasuredHeight();
                updateRotation();
            } else {
                return;
            }
        }
        int newHeight = this.mList.getMeasuredHeight();
        int i = this.mOldHeight;
        if (newHeight != i) {
            animateChild(i, newHeight);
        }
        post(new Runnable() { // from class: com.android.systemui.-$$Lambda$HardwareUiLayout$e7QpWmSxwKfxOfM1Q3hNoq7i9r0
            @Override // java.lang.Runnable
            public final void run() {
                HardwareUiLayout.this.lambda$onMeasure$0$HardwareUiLayout();
            }
        });
        post(new Runnable() { // from class: com.android.systemui.-$$Lambda$HardwareUiLayout$zQ_qVVlFo_33izIMpEk2X8p8Su8
            @Override // java.lang.Runnable
            public final void run() {
                HardwareUiLayout.this.lambda$onMeasure$1$HardwareUiLayout();
            }
        });
    }

    public void setSwapOrientation(boolean swapOrientation) {
        this.mSwapOrientation = swapOrientation;
    }

    private void updateRotation() {
        int rotation = RotationUtils.getRotation(getContext());
        if (rotation != this.mRotation) {
            rotate(this.mRotation, rotation);
            this.mRotation = rotation;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.MultiListLayout
    public void rotate(int from, int to) {
        super.rotate(from, to);
        if (from != 0 && to != 0) {
            rotate(from, 0);
            rotate(0, to);
            return;
        }
        if (from == 1 || to == 2) {
            rotateRight();
        } else {
            rotateLeft();
        }
        if (this.mAdapter.hasSeparatedItems()) {
            if (from == 2 || to == 2) {
                swapLeftAndTop(this.mSeparatedView);
            } else if (from == 1) {
                rotateRight(this.mSeparatedView);
            } else {
                rotateLeft(this.mSeparatedView);
            }
        }
        if (to != 0) {
            if (this.mList instanceof LinearLayout) {
                this.mRotatedBackground = true;
                this.mListBackground.setRotatedBackground(true);
                this.mSeparatedViewBackground.setRotatedBackground(true);
                LinearLayout linearLayout = (LinearLayout) this.mList;
                if (this.mSwapOrientation) {
                    linearLayout.setOrientation(0);
                    setOrientation(0);
                }
                swapDimens(this.mList);
                swapDimens(this.mSeparatedView);
            }
        } else if (this.mList instanceof LinearLayout) {
            this.mRotatedBackground = false;
            this.mListBackground.setRotatedBackground(false);
            this.mSeparatedViewBackground.setRotatedBackground(false);
            LinearLayout linearLayout2 = (LinearLayout) this.mList;
            if (this.mSwapOrientation) {
                linearLayout2.setOrientation(1);
                setOrientation(1);
            }
            swapDimens(this.mList);
            swapDimens(this.mSeparatedView);
        }
    }

    @Override // com.android.systemui.MultiListLayout
    public void onUpdateList() {
        ViewGroup parent;
        super.onUpdateList();
        for (int i = 0; i < this.mAdapter.getCount(); i++) {
            boolean separated = this.mAdapter.shouldBeSeparated(i);
            if (separated) {
                parent = getSeparatedView();
            } else {
                parent = getListView();
            }
            View v = this.mAdapter.getView(i, null, parent);
            parent.addView(v);
        }
    }

    private void rotateRight() {
        rotateRight(this);
        rotateRight(this.mList);
        swapDimens(this);
        LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) this.mList.getLayoutParams();
        p.gravity = rotateGravityRight(p.gravity);
        this.mList.setLayoutParams(p);
        LinearLayout.LayoutParams separatedViewLayoutParams = (LinearLayout.LayoutParams) this.mSeparatedView.getLayoutParams();
        separatedViewLayoutParams.gravity = rotateGravityRight(separatedViewLayoutParams.gravity);
        this.mSeparatedView.setLayoutParams(separatedViewLayoutParams);
        setGravity(rotateGravityRight(getGravity()));
    }

    private void swapDimens(View v) {
        ViewGroup.LayoutParams params = v.getLayoutParams();
        int h = params.width;
        params.width = params.height;
        params.height = h;
        v.setLayoutParams(params);
    }

    private int rotateGravityRight(int gravity) {
        int retGravity;
        int layoutDirection = getLayoutDirection();
        int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
        int verticalGravity = gravity & 112;
        int i = absoluteGravity & 7;
        if (i == 1) {
            retGravity = 0 | 16;
        } else if (i == 5) {
            retGravity = 0 | 80;
        } else {
            retGravity = 0 | 48;
        }
        if (verticalGravity != 16) {
            if (verticalGravity == 80) {
                return retGravity | 3;
            }
            return retGravity | 5;
        }
        return retGravity | 1;
    }

    private void rotateLeft() {
        rotateLeft(this);
        rotateLeft(this.mList);
        swapDimens(this);
        LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) this.mList.getLayoutParams();
        p.gravity = rotateGravityLeft(p.gravity);
        this.mList.setLayoutParams(p);
        LinearLayout.LayoutParams separatedViewLayoutParams = (LinearLayout.LayoutParams) this.mSeparatedView.getLayoutParams();
        separatedViewLayoutParams.gravity = rotateGravityLeft(separatedViewLayoutParams.gravity);
        this.mSeparatedView.setLayoutParams(separatedViewLayoutParams);
        setGravity(rotateGravityLeft(getGravity()));
    }

    private int rotateGravityLeft(int gravity) {
        int retGravity;
        if (gravity == -1) {
            gravity = 8388659;
        }
        int layoutDirection = getLayoutDirection();
        int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
        int verticalGravity = gravity & 112;
        int i = absoluteGravity & 7;
        if (i == 1) {
            retGravity = 0 | 16;
        } else if (i == 5) {
            retGravity = 0 | 48;
        } else {
            retGravity = 0 | 80;
        }
        if (verticalGravity != 16) {
            if (verticalGravity == 80) {
                return retGravity | 5;
            }
            return retGravity | 3;
        }
        return retGravity | 1;
    }

    private void rotateLeft(View v) {
        v.setPadding(v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom(), v.getPaddingLeft());
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        params.setMargins(params.topMargin, params.rightMargin, params.bottomMargin, params.leftMargin);
        v.setLayoutParams(params);
    }

    private void rotateRight(View v) {
        v.setPadding(v.getPaddingBottom(), v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight());
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        params.setMargins(params.bottomMargin, params.leftMargin, params.topMargin, params.rightMargin);
        v.setLayoutParams(params);
    }

    private void swapLeftAndTop(View v) {
        v.setPadding(v.getPaddingTop(), v.getPaddingLeft(), v.getPaddingBottom(), v.getPaddingRight());
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        params.setMargins(params.topMargin, params.leftMargin, params.bottomMargin, params.rightMargin);
        v.setLayoutParams(params);
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        post(new Runnable() { // from class: com.android.systemui.-$$Lambda$HardwareUiLayout$cC2d-RGmOoAkRlNqsTu1n43qy3A
            @Override // java.lang.Runnable
            public final void run() {
                HardwareUiLayout.this.lambda$onLayout$2$HardwareUiLayout();
            }
        });
    }

    private void animateChild(int oldHeight, int newHeight) {
    }

    /* renamed from: com.android.systemui.HardwareUiLayout$1  reason: invalid class name */
    /* loaded from: classes21.dex */
    class AnonymousClass1 extends AnimatorListenerAdapter {
        AnonymousClass1() {
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            HardwareUiLayout.this.mAnimating = false;
        }
    }

    private /* synthetic */ void lambda$animateChild$3(ValueAnimator animation) {
        this.mListBackground.invalidateSelf();
    }

    @Override // com.android.systemui.MultiListLayout
    public void setDivisionView(View v) {
        this.mDivision = v;
        View view = this.mDivision;
        if (view != null) {
            view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: com.android.systemui.-$$Lambda$HardwareUiLayout$2j9eMBfPQJX3xgwLvM_hUGNd8jc
                @Override // android.view.View.OnLayoutChangeListener
                public final void onLayoutChange(View view2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                    HardwareUiLayout.this.lambda$setDivisionView$4$HardwareUiLayout(view2, i, i2, i3, i4, i5, i6, i7, i8);
                }
            });
        }
        lambda$onMeasure$1$HardwareUiLayout();
    }

    public /* synthetic */ void lambda$setDivisionView$4$HardwareUiLayout(View v1, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        lambda$onMeasure$1$HardwareUiLayout();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: updatePosition */
    public void lambda$onMeasure$1$HardwareUiLayout() {
        if (this.mList == null) {
            return;
        }
        boolean separated = this.mAdapter.hasSeparatedItems();
        this.mListBackground.setRotatedBackground(separated);
        this.mSeparatedViewBackground.setRotatedBackground(separated);
        View view = this.mDivision;
        if (view != null && view.getVisibility() == 0) {
            int index = !this.mRotatedBackground ? 1 : 0;
            this.mDivision.getLocationOnScreen(this.mTmp2);
            float trans = this.mRotatedBackground ? this.mDivision.getTranslationX() : this.mDivision.getTranslationY();
            int[] iArr = this.mTmp2;
            int viewTop = (int) (iArr[index] + trans);
            this.mList.getLocationOnScreen(iArr);
            setCutPoint(viewTop - this.mTmp2[index]);
            return;
        }
        setCutPoint(this.mList.getMeasuredHeight());
    }

    private void setCutPoint(int point) {
        int curPoint = this.mListBackground.getCutPoint();
        if (curPoint == point) {
            return;
        }
        if (getAlpha() == 0.0f || curPoint == 0) {
            this.mListBackground.setCutPoint(point);
            return;
        }
        Animator animator = this.mAnimator;
        if (animator != null) {
            if (this.mEndPoint == point) {
                return;
            }
            animator.cancel();
        }
        this.mEndPoint = point;
        this.mAnimator = ObjectAnimator.ofInt(this.mListBackground, "cutPoint", curPoint, point);
        if (this.mCollapse) {
            this.mAnimator.setStartDelay(300L);
            this.mCollapse = false;
        }
        this.mAnimator.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: updatePaddingAndGravityIfTooTall */
    public void lambda$onMeasure$0$HardwareUiLayout() {
        int defaultTopPadding;
        int viewsTotalHeight;
        int separatedViewTopMargin;
        int screenHeight;
        int targetGravity;
        boolean separated = this.mAdapter.hasSeparatedItems();
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) this.mSeparatedView.getLayoutParams();
        int rotation = RotationUtils.getRotation(getContext());
        if (rotation == 1) {
            defaultTopPadding = getPaddingLeft();
            viewsTotalHeight = this.mList.getMeasuredWidth() + this.mSeparatedView.getMeasuredWidth();
            separatedViewTopMargin = separated ? params.leftMargin : 0;
            screenHeight = getMeasuredWidth();
            targetGravity = 49;
        } else if (rotation == 2) {
            defaultTopPadding = getPaddingRight();
            viewsTotalHeight = this.mList.getMeasuredWidth() + this.mSeparatedView.getMeasuredWidth();
            separatedViewTopMargin = separated ? params.leftMargin : 0;
            screenHeight = getMeasuredWidth();
            targetGravity = 81;
        } else {
            defaultTopPadding = getPaddingTop();
            viewsTotalHeight = this.mList.getMeasuredHeight() + this.mSeparatedView.getMeasuredHeight();
            separatedViewTopMargin = separated ? params.topMargin : 0;
            screenHeight = getMeasuredHeight();
            targetGravity = 21;
        }
        int totalHeight = defaultTopPadding + viewsTotalHeight + separatedViewTopMargin;
        if (totalHeight >= screenHeight) {
            setPadding(0, 0, 0, 0);
            setGravity(targetGravity);
        }
    }

    @Override // android.view.View
    public ViewOutlineProvider getOutlineProvider() {
        return super.getOutlineProvider();
    }

    public void setCollapse() {
        this.mCollapse = true;
    }

    public /* synthetic */ void lambda$new$5$HardwareUiLayout(ViewTreeObserver.InternalInsetsInfo inoutInfo) {
        if (this.mHasOutsideTouch || this.mList == null) {
            inoutInfo.setTouchableInsets(0);
            return;
        }
        inoutInfo.setTouchableInsets(1);
        inoutInfo.contentInsets.set(this.mList.getLeft(), this.mList.getTop(), 0, getBottom() - this.mList.getBottom());
    }

    private float getAnimationDistance() {
        return getContext().getResources().getDimension(R.dimen.global_actions_panel_width) / 2.0f;
    }

    @Override // com.android.systemui.MultiListLayout
    public float getAnimationOffsetX() {
        if (RotationUtils.getRotation(this.mContext) == 0) {
            return getAnimationDistance();
        }
        return 0.0f;
    }

    @Override // com.android.systemui.MultiListLayout
    public float getAnimationOffsetY() {
        int rotation = RotationUtils.getRotation(getContext());
        if (rotation != 1) {
            if (rotation == 2) {
                return getAnimationDistance();
            }
            return 0.0f;
        }
        return -getAnimationDistance();
    }
}
