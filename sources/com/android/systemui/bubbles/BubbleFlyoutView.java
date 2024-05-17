package com.android.systemui.bubbles;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.android.systemui.R;
import com.android.systemui.recents.TriangleShape;
/* loaded from: classes21.dex */
public class BubbleFlyoutView extends FrameLayout {
    private static final float DOT_SCALE = 1.0f;
    private static final float FLYOUT_MAX_WIDTH_PERCENT = 0.6f;
    private static final float SIZE_PERCENTAGE = 0.228f;
    private final ArgbEvaluator mArgbEvaluator;
    private boolean mArrowPointingLeft;
    private final Paint mBgPaint;
    private final RectF mBgRect;
    private float mBgTranslationX;
    private float mBgTranslationY;
    private final int mBubbleElevation;
    private final int mBubbleIconBitmapSize;
    private final float mBubbleIconTopPadding;
    private final int mBubbleSize;
    private final float mCornerRadius;
    private float[] mDotCenter;
    private int mDotColor;
    private final int mFloatingBackgroundColor;
    private final int mFlyoutElevation;
    private final int mFlyoutPadding;
    private final int mFlyoutSpaceFromBubble;
    private final TextView mFlyoutText;
    private final ViewGroup mFlyoutTextContainer;
    private float mFlyoutToDotHeightDelta;
    private float mFlyoutToDotWidthDelta;
    private final ShapeDrawable mLeftTriangleShape;
    private final float mNewDotRadius;
    private final float mNewDotSize;
    @Nullable
    private Runnable mOnHide;
    private final float mOriginalDotSize;
    private float mPercentStillFlyout;
    private float mPercentTransitionedToDot;
    private final int mPointerSize;
    private float mRestingTranslationX;
    private final ShapeDrawable mRightTriangleShape;
    private float mTranslationXWhenDot;
    private float mTranslationYWhenDot;
    private final Outline mTriangleOutline;

    public BubbleFlyoutView(Context context) {
        super(context);
        this.mBgPaint = new Paint(3);
        this.mArgbEvaluator = new ArgbEvaluator();
        this.mArrowPointingLeft = true;
        this.mTriangleOutline = new Outline();
        this.mBgRect = new RectF();
        this.mPercentTransitionedToDot = 1.0f;
        this.mPercentStillFlyout = 0.0f;
        this.mFlyoutToDotWidthDelta = 0.0f;
        this.mFlyoutToDotHeightDelta = 0.0f;
        this.mTranslationXWhenDot = 0.0f;
        this.mTranslationYWhenDot = 0.0f;
        this.mRestingTranslationX = 0.0f;
        LayoutInflater.from(context).inflate(R.layout.bubble_flyout, (ViewGroup) this, true);
        this.mFlyoutTextContainer = (ViewGroup) findViewById(R.id.bubble_flyout_text_container);
        this.mFlyoutText = (TextView) this.mFlyoutTextContainer.findViewById(R.id.bubble_flyout_text);
        Resources res = getResources();
        this.mFlyoutPadding = res.getDimensionPixelSize(R.dimen.bubble_flyout_padding_x);
        this.mFlyoutSpaceFromBubble = res.getDimensionPixelSize(R.dimen.bubble_flyout_space_from_bubble);
        this.mPointerSize = res.getDimensionPixelSize(R.dimen.bubble_flyout_pointer_size);
        this.mBubbleSize = res.getDimensionPixelSize(R.dimen.individual_bubble_size);
        this.mBubbleIconBitmapSize = res.getDimensionPixelSize(R.dimen.bubble_icon_bitmap_size);
        this.mBubbleIconTopPadding = (this.mBubbleSize - this.mBubbleIconBitmapSize) / 2.0f;
        this.mBubbleElevation = res.getDimensionPixelSize(R.dimen.bubble_elevation);
        this.mFlyoutElevation = res.getDimensionPixelSize(R.dimen.bubble_flyout_elevation);
        this.mOriginalDotSize = this.mBubbleIconBitmapSize * SIZE_PERCENTAGE;
        this.mNewDotRadius = (this.mOriginalDotSize * 1.0f) / 2.0f;
        this.mNewDotSize = this.mNewDotRadius * 2.0f;
        TypedArray ta = this.mContext.obtainStyledAttributes(new int[]{16844002, 16844145});
        this.mFloatingBackgroundColor = ta.getColor(0, -1);
        this.mCornerRadius = ta.getDimensionPixelSize(1, 0);
        ta.recycle();
        int i = this.mPointerSize;
        setPadding(i, 0, i, 0);
        setWillNotDraw(false);
        setClipChildren(false);
        setTranslationZ(this.mFlyoutElevation);
        setOutlineProvider(new ViewOutlineProvider() { // from class: com.android.systemui.bubbles.BubbleFlyoutView.1
            @Override // android.view.ViewOutlineProvider
            public void getOutline(View view, Outline outline) {
                BubbleFlyoutView.this.getOutline(outline);
            }
        });
        this.mBgPaint.setColor(this.mFloatingBackgroundColor);
        int i2 = this.mPointerSize;
        this.mLeftTriangleShape = new ShapeDrawable(TriangleShape.createHorizontal(i2, i2, true));
        ShapeDrawable shapeDrawable = this.mLeftTriangleShape;
        int i3 = this.mPointerSize;
        shapeDrawable.setBounds(0, 0, i3, i3);
        this.mLeftTriangleShape.getPaint().setColor(this.mFloatingBackgroundColor);
        int i4 = this.mPointerSize;
        this.mRightTriangleShape = new ShapeDrawable(TriangleShape.createHorizontal(i4, i4, false));
        ShapeDrawable shapeDrawable2 = this.mRightTriangleShape;
        int i5 = this.mPointerSize;
        shapeDrawable2.setBounds(0, 0, i5, i5);
        this.mRightTriangleShape.getPaint().setColor(this.mFloatingBackgroundColor);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        renderBackground(canvas);
        invalidateOutline();
        super.onDraw(canvas);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setupFlyoutStartingAsDot(CharSequence updateMessage, final PointF stackPos, float parentWidth, boolean arrowPointingLeft, int dotColor, @Nullable final Runnable onLayoutComplete, @Nullable Runnable onHide, float[] dotCenter) {
        this.mArrowPointingLeft = arrowPointingLeft;
        this.mDotColor = dotColor;
        this.mOnHide = onHide;
        this.mDotCenter = dotCenter;
        setCollapsePercent(1.0f);
        this.mFlyoutText.setMaxWidth(((int) (0.6f * parentWidth)) - (this.mFlyoutPadding * 2));
        this.mFlyoutText.setText(updateMessage);
        post(new Runnable() { // from class: com.android.systemui.bubbles.-$$Lambda$BubbleFlyoutView$uiE_eB5rfJqPQTRnM4IXJFU-4Tg
            @Override // java.lang.Runnable
            public final void run() {
                BubbleFlyoutView.this.lambda$setupFlyoutStartingAsDot$0$BubbleFlyoutView(stackPos, onLayoutComplete);
            }
        });
    }

    public /* synthetic */ void lambda$setupFlyoutStartingAsDot$0$BubbleFlyoutView(PointF stackPos, Runnable onLayoutComplete) {
        float restingTranslationY;
        float width;
        if (this.mFlyoutText.getLineCount() > 1) {
            restingTranslationY = stackPos.y + this.mBubbleIconTopPadding;
        } else {
            float restingTranslationY2 = stackPos.y;
            restingTranslationY = restingTranslationY2 + ((this.mBubbleSize - this.mFlyoutTextContainer.getHeight()) / 2.0f);
        }
        setTranslationY(restingTranslationY);
        if (this.mArrowPointingLeft) {
            width = stackPos.x + this.mBubbleSize + this.mFlyoutSpaceFromBubble;
        } else {
            width = (stackPos.x - getWidth()) - this.mFlyoutSpaceFromBubble;
        }
        this.mRestingTranslationX = width;
        this.mFlyoutToDotWidthDelta = getWidth() - this.mNewDotSize;
        this.mFlyoutToDotHeightDelta = getHeight() - this.mNewDotSize;
        float dotPositionX = (stackPos.x + this.mDotCenter[0]) - (this.mOriginalDotSize / 2.0f);
        float dotPositionY = (stackPos.y + this.mDotCenter[1]) - (this.mOriginalDotSize / 2.0f);
        float distanceFromFlyoutLeftToDotCenterX = this.mRestingTranslationX - dotPositionX;
        float distanceFromLayoutTopToDotCenterY = restingTranslationY - dotPositionY;
        this.mTranslationXWhenDot = -distanceFromFlyoutLeftToDotCenterX;
        this.mTranslationYWhenDot = -distanceFromLayoutTopToDotCenterY;
        if (onLayoutComplete != null) {
            onLayoutComplete.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void hideFlyout() {
        Runnable runnable = this.mOnHide;
        if (runnable != null) {
            runnable.run();
            this.mOnHide = null;
        }
        setVisibility(8);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCollapsePercent(float percentCollapsed) {
        if (Float.isNaN(percentCollapsed)) {
            return;
        }
        this.mPercentTransitionedToDot = Math.max(0.0f, Math.min(percentCollapsed, 1.0f));
        this.mPercentStillFlyout = 1.0f - this.mPercentTransitionedToDot;
        this.mFlyoutText.setTranslationX((this.mArrowPointingLeft ? -getWidth() : getWidth()) * this.mPercentTransitionedToDot);
        this.mFlyoutText.setAlpha(clampPercentage((this.mPercentStillFlyout - 0.75f) / 0.25f));
        int i = this.mFlyoutElevation;
        setTranslationZ(i - ((i - this.mBubbleElevation) * this.mPercentTransitionedToDot));
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public float getRestingTranslationX() {
        return this.mRestingTranslationX;
    }

    private float clampPercentage(float percent) {
        return Math.min(1.0f, Math.max(0.0f, percent));
    }

    private void renderBackground(Canvas canvas) {
        float width = getWidth() - (this.mFlyoutToDotWidthDelta * this.mPercentTransitionedToDot);
        float f = this.mFlyoutToDotHeightDelta;
        float f2 = this.mPercentTransitionedToDot;
        float height = getHeight() - (f * f2);
        float interpolatedRadius = (this.mNewDotRadius * f2) + (this.mCornerRadius * (1.0f - f2));
        this.mBgTranslationX = this.mTranslationXWhenDot * f2;
        this.mBgTranslationY = this.mTranslationYWhenDot * f2;
        RectF rectF = this.mBgRect;
        int i = this.mPointerSize;
        float f3 = this.mPercentStillFlyout;
        rectF.set(i * f3, 0.0f, width - (i * f3), height);
        this.mBgPaint.setColor(((Integer) this.mArgbEvaluator.evaluate(this.mPercentTransitionedToDot, Integer.valueOf(this.mFloatingBackgroundColor), Integer.valueOf(this.mDotColor))).intValue());
        canvas.save();
        canvas.translate(this.mBgTranslationX, this.mBgTranslationY);
        renderPointerTriangle(canvas, width, height);
        canvas.drawRoundRect(this.mBgRect, interpolatedRadius, interpolatedRadius, this.mBgPaint);
        canvas.restore();
    }

    private void renderPointerTriangle(Canvas canvas, float currentFlyoutWidth, float currentFlyoutHeight) {
        float arrowTranslationX;
        canvas.save();
        int i = this.mArrowPointingLeft ? 1 : -1;
        float f = this.mPercentTransitionedToDot;
        int i2 = this.mPointerSize;
        float retractionTranslationX = i * f * i2 * 2.0f;
        if (this.mArrowPointingLeft) {
            arrowTranslationX = retractionTranslationX;
        } else {
            arrowTranslationX = (currentFlyoutWidth - i2) + retractionTranslationX;
        }
        float arrowTranslationY = (currentFlyoutHeight / 2.0f) - (this.mPointerSize / 2.0f);
        ShapeDrawable relevantTriangle = this.mArrowPointingLeft ? this.mLeftTriangleShape : this.mRightTriangleShape;
        canvas.translate(arrowTranslationX, arrowTranslationY);
        relevantTriangle.setAlpha((int) (this.mPercentStillFlyout * 255.0f));
        relevantTriangle.draw(canvas);
        relevantTriangle.getOutline(this.mTriangleOutline);
        this.mTriangleOutline.offset((int) arrowTranslationX, (int) arrowTranslationY);
        canvas.restore();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getOutline(Outline outline) {
        if (!this.mTriangleOutline.isEmpty()) {
            Path rectPath = new Path();
            float f = this.mNewDotRadius;
            float f2 = this.mPercentTransitionedToDot;
            float interpolatedRadius = (f * f2) + (this.mCornerRadius * (1.0f - f2));
            rectPath.addRoundRect(this.mBgRect, interpolatedRadius, interpolatedRadius, Path.Direction.CW);
            outline.setConvexPath(rectPath);
            if (this.mPercentStillFlyout > 0.5f) {
                outline.mPath.addPath(this.mTriangleOutline.mPath);
            }
            Matrix outlineMatrix = new Matrix();
            outlineMatrix.postTranslate(getLeft() + this.mBgTranslationX, getTop() + this.mBgTranslationY);
            float f3 = this.mPercentTransitionedToDot;
            if (f3 > 0.98f) {
                float percentBetween99and100 = (f3 - 0.98f) / 0.02f;
                float percentShadowVisible = 1.0f - percentBetween99and100;
                float f4 = this.mNewDotRadius;
                outlineMatrix.postTranslate(f4 * percentBetween99and100, f4 * percentBetween99and100);
                outlineMatrix.preScale(percentShadowVisible, percentShadowVisible);
            }
            outline.mPath.transform(outlineMatrix);
        }
    }
}
