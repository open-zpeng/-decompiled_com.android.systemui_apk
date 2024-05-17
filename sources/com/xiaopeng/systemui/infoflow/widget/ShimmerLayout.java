package com.xiaopeng.systemui.infoflow.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.android.systemui.R;
import com.xiaopeng.systemui.infoflow.helper.XBesselCurve3Interpolator;
import com.xiaopeng.systemui.infoflow.util.Logger;
import com.xiaopeng.systemui.infoflow.util.ThreadUtils;
import com.xiaopeng.systemui.infoflow.widget.IFocusView;
/* loaded from: classes24.dex */
public class ShimmerLayout extends RelativeLayout implements IFocusView {
    private static final int ANIMATION_COVER_INNER_ALPHA_FROM = 58;
    private static final int ANIMATION_COVER_INNER_ALPHA_TO = 0;
    private static final int ANIMATION_COVER_OUTER_ALPHA_FROM = 0;
    private static final int ANIMATION_COVER_OUTER_ALPHA_TO = 255;
    private static final int ANIMATION_COVER_OUTER_DURATION = 500;
    private static final int ANIMATION_COVER_OUTER_REVERSE_DURATION = 350;
    private static final int ANIMATION_HIDE_COVER_INNER_DURATION = 400;
    private static final int ANIMATION_PREFOCUS_ALPHA_FROM = 0;
    private static final int ANIMATION_PREFOCUS_ALPHA_TO = 153;
    private static final int ANIMATION_PREFOCUS_DURATION = 400;
    private static final int ANIMATION_START_OFFSET_THRESHOLD = 5;
    private static final String TAG = "ShimmerLayout";
    public static final int TYPE_BUTTON = 1;
    public static final int TYPE_DEFAULT = 0;
    private Bitmap mButtonFocusBitmap;
    private int mButtonFocusHeight;
    private int mButtonFocusMarginLeft;
    private int mButtonFocusMarginTop;
    private int mButtonFocusWidth;
    private int mCoverInnerAlpha;
    private Bitmap mCoverInnerBackgroundBitmap;
    private Bitmap mCoverInnerBitmap;
    private Paint mCoverInnerPaint;
    private int mCoverOuterAlpha;
    private ValueAnimator mCoverOuterAlphaAnimator;
    private Bitmap mCoverOuterBitmap;
    private ValueAnimator mCoverOuterOffsetAnimator;
    private Paint mCoverOuterPaint;
    protected boolean mFocused;
    private int mHeight;
    private ValueAnimator mHideCoverInnerAnimator;
    private boolean mIsAnimationReversed;
    private boolean mIsAnimationStarted;
    private boolean mIsHideCoverInnerAnimationStarted;
    private boolean mIsPrefocusAnimationReversed;
    private boolean mIsPrefocusAnimationStarted;
    private int mMarginLeft;
    private int mMarginTop;
    private int mOffsetX;
    protected IFocusView.OnFocusChangedListener mOnFocusChangedListener;
    private int mPrefocusAlpha;
    private ValueAnimator mPrefocusAlphaAnimator;
    private Bitmap mPrefocusBitmap;
    private Paint mPrefocusPaint;
    private int mStrokeWidth;
    private int mType;
    private int mWidth;

    public ShimmerLayout(Context context) {
        this(context, null);
    }

    public ShimmerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShimmerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mType = 0;
        this.mCoverInnerAlpha = 58;
        this.mCoverOuterAlpha = 0;
        this.mPrefocusAlpha = 0;
        this.mIsAnimationReversed = false;
        this.mIsPrefocusAnimationReversed = false;
        setWillNotDraw(false);
        createShimmerPaint();
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ShimmerLayout, 0, 0);
        try {
            Resources resources = getResources();
            this.mWidth = a.getDimensionPixelSize(4, resources.getDimensionPixelSize(R.dimen.infoflow_card_shimmer_width));
            this.mHeight = a.getDimensionPixelSize(0, resources.getDimensionPixelSize(R.dimen.infoflow_card_shimmer_height));
            this.mMarginLeft = a.getDimensionPixelSize(1, resources.getDimensionPixelSize(R.dimen.infoflow_card_shimmer_margin_left));
            this.mMarginTop = a.getDimensionPixelSize(2, resources.getDimensionPixelSize(R.dimen.infoflow_card_shimmer_margin_top));
            this.mType = a.getInt(3, 0);
            this.mButtonFocusWidth = resources.getDimensionPixelSize(R.dimen.infoflow_card_button_focus_width);
            this.mButtonFocusHeight = resources.getDimensionPixelSize(R.dimen.infoflow_card_button_focus_height);
            this.mButtonFocusMarginLeft = resources.getDimensionPixelSize(R.dimen.infoflow_card_button_focus_margin_left);
            this.mButtonFocusMarginTop = resources.getDimensionPixelSize(R.dimen.infoflow_card_button_focus_margin_top);
            a.recycle();
            this.mStrokeWidth = getResources().getDimensionPixelSize(R.dimen.infoflow_card_shimmer_stroke_width);
            enableForcedSoftwareLayerIfNeeded();
        } catch (Throwable th) {
            a.recycle();
            throw th;
        }
    }

    public void setType(int type) {
        this.mType = type;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        resetShimmering();
        super.onDetachedFromWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        if ((!this.mIsAnimationStarted && !this.mIsHideCoverInnerAnimationStarted && !this.mIsPrefocusAnimationStarted) || getWidth() <= 0 || getHeight() <= 0) {
            super.dispatchDraw(canvas);
        } else {
            dispatchDrawShimmer(canvas);
        }
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility != 0) {
            stopShimmerAnimation();
        }
    }

    public void startShimmerAnimation() {
        if (this.mIsAnimationStarted) {
            return;
        }
        String viewTag = getViewTag();
        Logger.d(TAG, viewTag, "startShimmerAnimation : mIsAnimationReversed = " + this.mIsAnimationReversed);
        this.mCoverInnerAlpha = 58;
        this.mCoverOuterAlpha = 0;
        Animator animator = getShimmerAnimation();
        animator.start();
        this.mIsAnimationStarted = true;
        Animator coverOuterAlphaAnimation = getCoverOuterAlphaAnimation();
        coverOuterAlphaAnimation.start();
        ThreadUtils.runOnMainThreadDelay(new Runnable() { // from class: com.xiaopeng.systemui.infoflow.widget.-$$Lambda$ShimmerLayout$i-Rsv0TRhbAK1ci7FW5AmqKX8Sc
            @Override // java.lang.Runnable
            public final void run() {
                ShimmerLayout.this.lambda$startShimmerAnimation$0$ShimmerLayout();
            }
        }, animator.getDuration());
    }

    public /* synthetic */ void lambda$startShimmerAnimation$0$ShimmerLayout() {
        if (!this.mIsAnimationReversed && this.mIsAnimationStarted) {
            startHideCoverInnerAnimation();
        }
    }

    public void startHideCoverInnerAnimation() {
        if (this.mIsHideCoverInnerAnimationStarted) {
            return;
        }
        Logger.d(TAG, getViewTag(), "startHideCoverInnerAnimation");
        Animator animator = getHideCoverInnerAnimation();
        animator.start();
        this.mIsHideCoverInnerAnimationStarted = true;
    }

    private Animator getHideCoverInnerAnimation() {
        ValueAnimator valueAnimator = this.mHideCoverInnerAnimator;
        if (valueAnimator != null) {
            return valueAnimator;
        }
        this.mHideCoverInnerAnimator = ValueAnimator.ofInt(58, 0);
        this.mHideCoverInnerAnimator.setDuration(400L);
        this.mHideCoverInnerAnimator.setInterpolator(new XBesselCurve3Interpolator());
        this.mHideCoverInnerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.systemui.infoflow.widget.-$$Lambda$ShimmerLayout$4W70iEOmsMd_VtbYaHpUKGGCPjE
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                ShimmerLayout.this.lambda$getHideCoverInnerAnimation$1$ShimmerLayout(valueAnimator2);
            }
        });
        this.mHideCoverInnerAnimator.addListener(new Animator.AnimatorListener() { // from class: com.xiaopeng.systemui.infoflow.widget.ShimmerLayout.1
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                ShimmerLayout.this.mIsHideCoverInnerAnimationStarted = false;
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animator) {
            }
        });
        return this.mHideCoverInnerAnimator;
    }

    public /* synthetic */ void lambda$getHideCoverInnerAnimation$1$ShimmerLayout(ValueAnimator animation) {
        this.mCoverInnerAlpha = ((Integer) animation.getAnimatedValue()).intValue();
        String viewTag = getViewTag();
        Logger.d(TAG, viewTag, "update alpha : " + this.mCoverInnerAlpha);
        if (this.mCoverInnerAlpha >= 0) {
            invalidate();
        }
    }

    public void stopShimmerAnimation() {
        resetShimmering();
    }

    public void setAnimationReversed(boolean animationReversed) {
        this.mIsAnimationReversed = animationReversed;
        resetIfStarted();
    }

    public void showPrefocusBackground(boolean showPrefocusBackground) {
        String viewTag = getViewTag();
        Logger.d(TAG, viewTag, "showPrefocusBackground : showPrefocusBackground = " + showPrefocusBackground);
        resetPrefocusAnimationIfStarted();
        this.mIsPrefocusAnimationReversed = showPrefocusBackground ^ true;
        startPrefocusAnimation();
    }

    public void resetPrefocusAnimationIfStarted() {
        String viewTag = getViewTag();
        Logger.d(TAG, viewTag, "resetPrefocusAnimationIfStarted:" + this.mIsPrefocusAnimationStarted);
        if (this.mIsPrefocusAnimationStarted) {
            ValueAnimator valueAnimator = this.mPrefocusAlphaAnimator;
            if (valueAnimator != null) {
                valueAnimator.end();
                this.mPrefocusAlphaAnimator.removeAllListeners();
                this.mPrefocusAlphaAnimator.removeAllUpdateListeners();
                this.mPrefocusAlphaAnimator = null;
            }
            releasePrefocusBitmap();
            this.mIsPrefocusAnimationStarted = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releasePrefocusBitmap() {
        Logger.d(TAG, getViewTag(), "releasePrefocusBitmap");
        Bitmap bitmap = this.mPrefocusBitmap;
        if (bitmap != null) {
            bitmap.recycle();
            this.mPrefocusBitmap = null;
        }
    }

    private void startPrefocusAnimation() {
        if (this.mIsPrefocusAnimationStarted) {
            return;
        }
        this.mPrefocusAlpha = 0;
        Animator alphaAnimator = getPrefocusAlphaAnimator();
        alphaAnimator.start();
        this.mIsPrefocusAnimationStarted = true;
    }

    private void resetIfStarted() {
        if (this.mIsAnimationStarted) {
            resetShimmering();
            startShimmerAnimation();
        }
    }

    private void dispatchDrawShimmer(Canvas canvas) {
        Logger.d(TAG, getViewTag(), "dispatchDrawShimmer");
        super.dispatchDraw(canvas);
        canvas.save();
        drawCover(canvas);
        canvas.restore();
    }

    private void drawCover(Canvas canvas) {
        if (this.mIsAnimationStarted && this.mOffsetX > 5) {
            drawCoverOuter(canvas);
        }
        if (this.mType == 0) {
            String viewTag = getViewTag();
            Logger.d(TAG, viewTag, "drawCover : mIsPrefocusAnimationStarted = " + this.mIsPrefocusAnimationStarted);
            if (this.mIsPrefocusAnimationStarted && this.mType == 0) {
                drawPrefocusBackground(canvas);
            }
            if ((this.mIsAnimationStarted || this.mIsHideCoverInnerAnimationStarted) && !this.mIsAnimationReversed) {
                drawCoverInner(canvas);
                drawCoverInnerBackground(canvas);
            }
        }
    }

    private void drawCoverInnerBackground(Canvas canvas) {
        createCoverInnerBackgroundBitmap();
        Bitmap bitmap = this.mCoverInnerBackgroundBitmap;
        Rect rect = new Rect(0, 0, this.mOffsetX, this.mHeight);
        int i = this.mMarginLeft;
        int i2 = this.mMarginTop;
        canvas.drawBitmap(bitmap, rect, new Rect(i, i2, this.mOffsetX + i, this.mHeight + i2), this.mCoverInnerPaint);
    }

    private void drawPrefocusBackground(Canvas canvas) {
        String viewTag = getViewTag();
        Logger.d(TAG, viewTag, "drawPrefocusBackground : mPrefocusAlpha = " + this.mPrefocusAlpha + " mIsPrefocusAnimationReversed = " + this.mIsPrefocusAnimationReversed);
        createPrefocusBitmap();
        int width = this.mPrefocusBitmap.getWidth();
        int height = this.mPrefocusBitmap.getHeight();
        this.mPrefocusPaint.setAlpha(this.mPrefocusAlpha);
        Bitmap bitmap = this.mPrefocusBitmap;
        Rect rect = new Rect(0, 0, width, height);
        int i = this.mMarginLeft;
        int i2 = this.mMarginTop;
        canvas.drawBitmap(bitmap, rect, new Rect(i, i2, i + width, i2 + height), this.mPrefocusPaint);
    }

    private void createCoverInnerBackgroundBitmap() {
        if (this.mCoverInnerBackgroundBitmap == null) {
            this.mCoverInnerBackgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_infoflow_cover_inner_background);
        }
    }

    private void createPrefocusBitmap() {
        if (this.mPrefocusBitmap == null) {
            this.mPrefocusBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_infoflow_prefocus);
        }
    }

    private void createButtonFocusBitmap() {
        if (this.mButtonFocusBitmap == null) {
            Drawable drawable = getResources().getDrawable(R.drawable.infoflow_card_button_focus_outer, null);
            this.mButtonFocusBitmap = createBitmap(this.mButtonFocusWidth, this.mButtonFocusHeight);
            Canvas canvas = new Canvas(this.mButtonFocusBitmap);
            drawable.setBounds(0, 0, this.mButtonFocusWidth, this.mButtonFocusHeight);
            drawable.draw(canvas);
        }
    }

    private void drawCoverOuter(Canvas canvas) {
        String viewTag = getViewTag();
        Logger.d(TAG, viewTag, "drawCoverOuter : mCoverOuterAlpha = " + this.mCoverOuterAlpha + " mOffsetX = " + this.mOffsetX + " mType = " + this.mType);
        if (this.mType == 1) {
            createButtonFocusBitmap();
            this.mCoverOuterPaint.setAlpha(this.mCoverOuterAlpha);
            canvas.drawBitmap(this.mButtonFocusBitmap, this.mButtonFocusMarginLeft, this.mButtonFocusMarginTop, this.mCoverOuterPaint);
            return;
        }
        createCoverOuterBitmap();
        this.mCoverOuterPaint.setAlpha(this.mCoverOuterAlpha);
        Bitmap bitmap = this.mCoverOuterBitmap;
        Rect rect = new Rect(0, 0, this.mOffsetX, this.mHeight);
        int i = this.mMarginLeft;
        int i2 = this.mMarginTop;
        canvas.drawBitmap(bitmap, rect, new Rect(i, i2, this.mOffsetX + i, this.mHeight + i2), this.mCoverOuterPaint);
    }

    private void drawCoverInner(Canvas canvas) {
        String viewTag = getViewTag();
        Logger.d(TAG, viewTag, "drawCoverInner : mCoverInnerAlpha = " + this.mCoverInnerAlpha);
        createCoverInnerBitmap();
        int height = this.mCoverInnerBitmap.getHeight();
        int width = this.mCoverInnerBitmap.getWidth();
        int left = this.mMarginLeft + this.mStrokeWidth;
        this.mCoverInnerPaint.setAlpha(this.mCoverInnerAlpha);
        int i = this.mOffsetX;
        int i2 = this.mStrokeWidth;
        if (i > i2 && i < this.mWidth - i2) {
            Bitmap bitmap = this.mCoverInnerBitmap;
            Rect rect = new Rect(0, 0, i - i2, height);
            int i3 = this.mMarginTop;
            int i4 = this.mStrokeWidth;
            canvas.drawBitmap(bitmap, rect, new Rect(left, i3 + i4, (this.mOffsetX + left) - i4, i4 + height + i3), this.mCoverInnerPaint);
        } else if (this.mOffsetX > this.mStrokeWidth) {
            Bitmap bitmap2 = this.mCoverInnerBitmap;
            Rect rect2 = new Rect(0, 0, width, height);
            int i5 = this.mMarginTop;
            int i6 = this.mStrokeWidth;
            canvas.drawBitmap(bitmap2, rect2, new Rect(left, i5 + i6, left + width, i6 + height + i5), this.mCoverInnerPaint);
        }
    }

    private void resetShimmering() {
        ValueAnimator valueAnimator = this.mCoverOuterOffsetAnimator;
        if (valueAnimator != null) {
            valueAnimator.end();
            this.mCoverOuterOffsetAnimator.removeAllUpdateListeners();
            this.mCoverOuterOffsetAnimator.removeAllListeners();
            this.mCoverOuterOffsetAnimator = null;
        }
        ValueAnimator valueAnimator2 = this.mCoverOuterAlphaAnimator;
        if (valueAnimator2 != null) {
            valueAnimator2.end();
            this.mCoverOuterAlphaAnimator.removeAllUpdateListeners();
            this.mCoverOuterAlphaAnimator.removeAllListeners();
            this.mCoverOuterAlphaAnimator = null;
        }
        ValueAnimator valueAnimator3 = this.mHideCoverInnerAnimator;
        if (valueAnimator3 != null) {
            valueAnimator3.end();
            this.mHideCoverInnerAnimator.removeAllUpdateListeners();
            this.mHideCoverInnerAnimator.removeAllListeners();
            this.mHideCoverInnerAnimator = null;
        }
        this.mIsAnimationStarted = false;
        this.mIsHideCoverInnerAnimationStarted = false;
        releaseBitMaps();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseBitMaps() {
        Logger.d(TAG, getViewTag(), "releaseBitMaps");
        Bitmap bitmap = this.mCoverOuterBitmap;
        if (bitmap != null) {
            bitmap.recycle();
            this.mCoverOuterBitmap = null;
        }
        Bitmap bitmap2 = this.mCoverInnerBitmap;
        if (bitmap2 != null) {
            bitmap2.recycle();
            this.mCoverInnerBitmap = null;
        }
        Bitmap bitmap3 = this.mCoverInnerBackgroundBitmap;
        if (bitmap3 != null) {
            bitmap3.recycle();
            this.mCoverInnerBackgroundBitmap = null;
        }
        Bitmap bitmap4 = this.mButtonFocusBitmap;
        if (bitmap4 != null) {
            bitmap4.recycle();
            this.mButtonFocusBitmap = null;
        }
    }

    private void createCoverOuterBitmap() {
        if (this.mCoverOuterBitmap == null) {
            Drawable drawable = getResources().getDrawable(R.drawable.infoflow_card_cover_outer, null);
            this.mCoverOuterBitmap = createBitmap(this.mWidth, this.mHeight);
            Canvas canvas = new Canvas(this.mCoverOuterBitmap);
            drawable.setBounds(0, 0, this.mWidth - 1, this.mHeight);
            drawable.draw(canvas);
        }
    }

    private void createCoverInnerBitmap() {
        if (this.mCoverInnerBitmap == null) {
            Drawable drawable = getResources().getDrawable(R.drawable.infoflow_card_cover_inner, null);
            int i = this.mWidth;
            int i2 = this.mStrokeWidth;
            int width = i - (i2 * 2);
            int height = this.mHeight - (i2 * 2);
            this.mCoverInnerBitmap = createBitmap(width, height);
            Canvas canvas = new Canvas(this.mCoverInnerBitmap);
            drawable.setBounds(0, 0, width, height);
            drawable.draw(canvas);
        }
    }

    private void createShimmerPaint() {
        if (this.mCoverOuterPaint == null) {
            this.mCoverOuterPaint = new Paint();
            this.mCoverOuterPaint.setAntiAlias(true);
        }
        if (this.mCoverInnerPaint == null) {
            this.mCoverInnerPaint = new Paint();
            this.mCoverInnerPaint.setAntiAlias(true);
        }
        if (this.mPrefocusPaint == null) {
            this.mPrefocusPaint = new Paint();
            this.mPrefocusPaint.setAntiAlias(true);
        }
    }

    private Animator getShimmerAnimation() {
        ValueAnimator valueAnimator = this.mCoverOuterOffsetAnimator;
        if (valueAnimator != null) {
            return valueAnimator;
        }
        this.mCoverOuterOffsetAnimator = this.mIsAnimationReversed ? ValueAnimator.ofInt(this.mWidth, 0) : ValueAnimator.ofInt(0, this.mWidth);
        if (!this.mIsAnimationReversed) {
            this.mCoverOuterOffsetAnimator.setDuration(500L);
        } else {
            this.mCoverOuterOffsetAnimator.setDuration(350L);
        }
        this.mCoverOuterOffsetAnimator.setInterpolator(new XBesselCurve3Interpolator());
        this.mCoverOuterOffsetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.systemui.infoflow.widget.-$$Lambda$ShimmerLayout$sAKXkpIh7AKTRHMqLuEhG7h4hFQ
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                ShimmerLayout.this.lambda$getShimmerAnimation$2$ShimmerLayout(valueAnimator2);
            }
        });
        this.mCoverOuterOffsetAnimator.addListener(new Animator.AnimatorListener() { // from class: com.xiaopeng.systemui.infoflow.widget.ShimmerLayout.2
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                ShimmerLayout.this.releaseBitMaps();
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animator) {
            }
        });
        return this.mCoverOuterOffsetAnimator;
    }

    public /* synthetic */ void lambda$getShimmerAnimation$2$ShimmerLayout(ValueAnimator animation) {
        this.mOffsetX = ((Integer) animation.getAnimatedValue()).intValue();
        if (this.mOffsetX >= 0) {
            invalidate();
        }
    }

    private Animator getCoverOuterAlphaAnimation() {
        ValueAnimator valueAnimator = this.mCoverOuterAlphaAnimator;
        if (valueAnimator != null) {
            return valueAnimator;
        }
        this.mCoverOuterAlphaAnimator = this.mIsAnimationReversed ? ValueAnimator.ofInt(255, 0) : ValueAnimator.ofInt(0, 255);
        if (!this.mIsAnimationReversed) {
            this.mCoverOuterAlphaAnimator.setDuration(500L);
        } else {
            this.mCoverOuterAlphaAnimator.setDuration(350L);
        }
        this.mCoverOuterAlphaAnimator.setInterpolator(new XBesselCurve3Interpolator());
        this.mCoverOuterAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.systemui.infoflow.widget.-$$Lambda$ShimmerLayout$yn2FiQbq_zPh3KUU8VeT6I1YKMc
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                ShimmerLayout.this.lambda$getCoverOuterAlphaAnimation$3$ShimmerLayout(valueAnimator2);
            }
        });
        return this.mCoverOuterAlphaAnimator;
    }

    public /* synthetic */ void lambda$getCoverOuterAlphaAnimation$3$ShimmerLayout(ValueAnimator animation) {
        this.mCoverOuterAlpha = ((Integer) animation.getAnimatedValue()).intValue();
        if (this.mCoverOuterAlpha >= 0) {
            invalidate();
        }
    }

    private Animator getPrefocusAlphaAnimator() {
        ValueAnimator valueAnimator = this.mPrefocusAlphaAnimator;
        if (valueAnimator != null) {
            return valueAnimator;
        }
        this.mPrefocusAlphaAnimator = this.mIsPrefocusAnimationReversed ? ValueAnimator.ofInt(153, 0) : ValueAnimator.ofInt(0, 153);
        this.mPrefocusAlphaAnimator.setDuration(400L);
        this.mPrefocusAlphaAnimator.setInterpolator(new XBesselCurve3Interpolator());
        this.mPrefocusAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.systemui.infoflow.widget.-$$Lambda$ShimmerLayout$xuEo79yGY4z3-z0pVo1LEJkSUfU
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                ShimmerLayout.this.lambda$getPrefocusAlphaAnimator$4$ShimmerLayout(valueAnimator2);
            }
        });
        this.mPrefocusAlphaAnimator.addListener(new Animator.AnimatorListener() { // from class: com.xiaopeng.systemui.infoflow.widget.ShimmerLayout.3
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                ShimmerLayout.this.releasePrefocusBitmap();
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animator) {
            }
        });
        return this.mPrefocusAlphaAnimator;
    }

    public /* synthetic */ void lambda$getPrefocusAlphaAnimator$4$ShimmerLayout(ValueAnimator animation) {
        this.mPrefocusAlpha = ((Integer) animation.getAnimatedValue()).intValue();
        if (this.mPrefocusAlpha >= 0) {
            invalidate();
        }
    }

    private Bitmap createBitmap(int width, int height) {
        try {
            return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError e) {
            System.gc();
            return null;
        }
    }

    private void enableForcedSoftwareLayerIfNeeded() {
        if (Build.VERSION.SDK_INT <= 16) {
            setLayerType(1, null);
        }
    }

    public void setFocused(boolean focused) {
        setFocused(focused, true);
    }

    public void setFocused(boolean focused, boolean triggerListener) {
        IFocusView.OnFocusChangedListener onFocusChangedListener;
        if (this.mFocused == focused) {
            return;
        }
        this.mFocused = focused;
        showPrefocusBackground(false);
        setAnimationReversed(!focused);
        startShimmerAnimation();
        if (triggerListener && (onFocusChangedListener = this.mOnFocusChangedListener) != null) {
            onFocusChangedListener.onFocusedChanged(focused);
        }
    }

    @Override // com.xiaopeng.systemui.infoflow.widget.IFocusView
    public void setPreFocused(boolean preFocused) {
        showPrefocusBackground(preFocused);
    }

    @Override // com.xiaopeng.systemui.infoflow.widget.IFocusView
    public void setOnFocusChangedListener(IFocusView.OnFocusChangedListener focusChangedListener) {
        this.mOnFocusChangedListener = focusChangedListener;
    }

    private String getViewTag() {
        if (getTag() != null) {
            return getTag().toString();
        }
        return "";
    }
}
