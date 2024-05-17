package com.android.systemui.statusbar.notification.row;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import com.android.settingslib.Utils;
import com.android.systemui.R;
import com.android.systemui.statusbar.notification.AnimatableProperty;
import com.android.systemui.statusbar.notification.PropertyAnimator;
import com.android.systemui.statusbar.notification.stack.AnimationProperties;
import java.util.function.BiConsumer;
import java.util.function.Function;
/* loaded from: classes21.dex */
public abstract class ExpandableOutlineView extends ExpandableView {
    private boolean mAlwaysRoundBothCorners;
    private int mBackgroundTop;
    private float mBottomRoundness;
    private final Path mClipPath;
    private float mCurrentBottomRoundness;
    private float mCurrentTopRoundness;
    private boolean mCustomOutline;
    private float mDistanceToTopRoundness;
    private float mOutlineAlpha;
    protected float mOutlineRadius;
    private final Rect mOutlineRect;
    private final ViewOutlineProvider mProvider;
    protected boolean mShouldTranslateContents;
    private Path mTmpPath;
    private boolean mTopAmountRounded;
    private float mTopRoundness;
    private static final AnimatableProperty TOP_ROUNDNESS = AnimatableProperty.from("topRoundness", new BiConsumer() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$ExpandableOutlineView$lgIjKBD4iaJhFeEZ5izPzOddhds
        @Override // java.util.function.BiConsumer
        public final void accept(Object obj, Object obj2) {
            ((ExpandableOutlineView) obj).setTopRoundnessInternal(((Float) obj2).floatValue());
        }
    }, new Function() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$iDWyu_PNFZfGQr9kcCLSWoFYxpI
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Float.valueOf(((ExpandableOutlineView) obj).getCurrentTopRoundness());
        }
    }, R.id.top_roundess_animator_tag, R.id.top_roundess_animator_end_tag, R.id.top_roundess_animator_start_tag);
    private static final AnimatableProperty BOTTOM_ROUNDNESS = AnimatableProperty.from("bottomRoundness", new BiConsumer() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$ExpandableOutlineView$ZLqiUGCQzNj3P4m8kfbTwbzfyaI
        @Override // java.util.function.BiConsumer
        public final void accept(Object obj, Object obj2) {
            ((ExpandableOutlineView) obj).setBottomRoundnessInternal(((Float) obj2).floatValue());
        }
    }, new Function() { // from class: com.android.systemui.statusbar.notification.row.-$$Lambda$RLFq7_ULx7AWbuaAJNsAxNrN1PI
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return Float.valueOf(((ExpandableOutlineView) obj).getCurrentBottomRoundness());
        }
    }, R.id.bottom_roundess_animator_tag, R.id.bottom_roundess_animator_end_tag, R.id.bottom_roundess_animator_start_tag);
    private static final AnimationProperties ROUNDNESS_PROPERTIES = new AnimationProperties().setDuration(360);
    private static final Path EMPTY_PATH = new Path();

    /* JADX INFO: Access modifiers changed from: protected */
    public Path getClipPath(boolean ignoreTranslation) {
        int left;
        int top;
        int right;
        int translation;
        float topRoundness = this.mAlwaysRoundBothCorners ? this.mOutlineRadius : getCurrentBackgroundRadiusTop();
        if (!this.mCustomOutline) {
            int translation2 = (!this.mShouldTranslateContents || ignoreTranslation) ? 0 : (int) getTranslation();
            int halfExtraWidth = (int) (this.mExtraWidthForClipping / 2.0f);
            left = Math.max(translation2, 0) - halfExtraWidth;
            top = this.mClipTopAmount + this.mBackgroundTop;
            right = getWidth() + halfExtraWidth + Math.min(translation2, 0);
            translation = Math.max(this.mMinimumHeightForClipping, Math.max(getActualHeight() - this.mClipBottomAmount, (int) (top + topRoundness)));
        } else {
            left = this.mOutlineRect.left;
            top = this.mOutlineRect.top;
            right = this.mOutlineRect.right;
            translation = this.mOutlineRect.bottom;
        }
        int height = translation - top;
        if (height == 0) {
            return EMPTY_PATH;
        }
        float bottomRoundness = this.mAlwaysRoundBothCorners ? this.mOutlineRadius : getCurrentBackgroundRadiusBottom();
        if (topRoundness + bottomRoundness > height) {
            float overShoot = (topRoundness + bottomRoundness) - height;
            float f = this.mCurrentTopRoundness;
            float f2 = this.mCurrentBottomRoundness;
            topRoundness -= (overShoot * f) / (f + f2);
            bottomRoundness -= (overShoot * f2) / (f + f2);
        }
        getRoundedRectPath(left, top, right, translation, topRoundness, bottomRoundness, this.mTmpPath);
        return this.mTmpPath;
    }

    public static void getRoundedRectPath(int left, int top, int right, int bottom, float topRoundness, float bottomRoundness, Path outPath) {
        outPath.reset();
        int width = right - left;
        float topRoundnessX = Math.min(width / 2, topRoundness);
        float bottomRoundnessX = Math.min(width / 2, bottomRoundness);
        if (topRoundness > 0.0f) {
            outPath.moveTo(left, top + topRoundness);
            outPath.quadTo(left, top, left + topRoundnessX, top);
            outPath.lineTo(right - topRoundnessX, top);
            outPath.quadTo(right, top, right, top + topRoundness);
        } else {
            outPath.moveTo(left, top);
            outPath.lineTo(right, top);
        }
        if (bottomRoundness > 0.0f) {
            outPath.lineTo(right, bottom - bottomRoundness);
            outPath.quadTo(right, bottom, right - bottomRoundnessX, bottom);
            outPath.lineTo(left + bottomRoundnessX, bottom);
            outPath.quadTo(left, bottom, left, bottom - bottomRoundness);
        } else {
            outPath.lineTo(right, bottom);
            outPath.lineTo(left, bottom);
        }
        outPath.close();
    }

    public ExpandableOutlineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mOutlineRect = new Rect();
        this.mClipPath = new Path();
        this.mOutlineAlpha = -1.0f;
        this.mTmpPath = new Path();
        this.mDistanceToTopRoundness = -1.0f;
        this.mProvider = new ViewOutlineProvider() { // from class: com.android.systemui.statusbar.notification.row.ExpandableOutlineView.1
            @Override // android.view.ViewOutlineProvider
            public void getOutline(View view, Outline outline) {
                if (!ExpandableOutlineView.this.mCustomOutline && ExpandableOutlineView.this.mCurrentTopRoundness == 0.0f && ExpandableOutlineView.this.mCurrentBottomRoundness == 0.0f && !ExpandableOutlineView.this.mAlwaysRoundBothCorners && !ExpandableOutlineView.this.mTopAmountRounded) {
                    int translation = ExpandableOutlineView.this.mShouldTranslateContents ? (int) ExpandableOutlineView.this.getTranslation() : 0;
                    int left = Math.max(translation, 0);
                    int top = ExpandableOutlineView.this.mClipTopAmount + ExpandableOutlineView.this.mBackgroundTop;
                    int right = ExpandableOutlineView.this.getWidth() + Math.min(translation, 0);
                    int bottom = Math.max(ExpandableOutlineView.this.getActualHeight() - ExpandableOutlineView.this.mClipBottomAmount, top);
                    outline.setRect(left, top, right, bottom);
                } else {
                    Path clipPath = ExpandableOutlineView.this.getClipPath(false);
                    if (clipPath != null && clipPath.isConvex()) {
                        outline.setConvexPath(clipPath);
                    }
                }
                outline.setAlpha(ExpandableOutlineView.this.mOutlineAlpha);
            }
        };
        setOutlineProvider(this.mProvider);
        initDimens();
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        canvas.save();
        Path intersectPath = null;
        if (this.mTopAmountRounded && topAmountNeedsClipping()) {
            int left = (int) ((-this.mExtraWidthForClipping) / 2.0f);
            int top = (int) (this.mClipTopAmount - this.mDistanceToTopRoundness);
            int right = getWidth() + ((int) (this.mExtraWidthForClipping + left));
            int bottom = (int) Math.max(this.mMinimumHeightForClipping, Math.max(getActualHeight() - this.mClipBottomAmount, top + this.mOutlineRadius));
            getRoundedRectPath(left, top, right, bottom, this.mOutlineRadius, 0.0f, this.mClipPath);
            intersectPath = this.mClipPath;
        }
        boolean clipped = false;
        if (childNeedsClipping(child)) {
            Path clipPath = getCustomClipPath(child);
            if (clipPath == null) {
                clipPath = getClipPath(false);
            }
            if (clipPath != null) {
                if (intersectPath != null) {
                    clipPath.op(intersectPath, Path.Op.INTERSECT);
                }
                canvas.clipPath(clipPath);
                clipped = true;
            }
        }
        if (!clipped && intersectPath != null) {
            canvas.clipPath(intersectPath);
        }
        boolean result = super.drawChild(canvas, child, drawingTime);
        canvas.restore();
        return result;
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public void setExtraWidthForClipping(float extraWidthForClipping) {
        super.setExtraWidthForClipping(extraWidthForClipping);
        invalidate();
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public void setMinimumHeightForClipping(int minimumHeightForClipping) {
        super.setMinimumHeightForClipping(minimumHeightForClipping);
        invalidate();
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public void setDistanceToTopRoundness(float distanceToTopRoundness) {
        super.setDistanceToTopRoundness(distanceToTopRoundness);
        if (distanceToTopRoundness != this.mDistanceToTopRoundness) {
            this.mTopAmountRounded = distanceToTopRoundness >= 0.0f;
            this.mDistanceToTopRoundness = distanceToTopRoundness;
            applyRoundness();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean childNeedsClipping(View child) {
        return false;
    }

    public boolean topAmountNeedsClipping() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isClippingNeeded() {
        return this.mAlwaysRoundBothCorners || this.mCustomOutline || getTranslation() != 0.0f;
    }

    private void initDimens() {
        Resources res = getResources();
        this.mShouldTranslateContents = res.getBoolean(R.bool.config_translateNotificationContentsOnSwipe);
        this.mOutlineRadius = res.getDimension(R.dimen.notification_shadow_radius);
        this.mAlwaysRoundBothCorners = res.getBoolean(R.bool.config_clipNotificationsToOutline);
        if (!this.mAlwaysRoundBothCorners) {
            this.mOutlineRadius = res.getDimensionPixelSize(Utils.getThemeAttr(this.mContext, 16844145));
        }
        setClipToOutline(this.mAlwaysRoundBothCorners);
    }

    public boolean setTopRoundness(float topRoundness, boolean animate) {
        if (this.mTopRoundness != topRoundness) {
            this.mTopRoundness = topRoundness;
            PropertyAnimator.setProperty(this, TOP_ROUNDNESS, topRoundness, ROUNDNESS_PROPERTIES, animate);
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void applyRoundness() {
        invalidateOutline();
        invalidate();
    }

    public float getCurrentBackgroundRadiusTop() {
        if (this.mTopAmountRounded) {
            return this.mOutlineRadius;
        }
        return this.mCurrentTopRoundness * this.mOutlineRadius;
    }

    public float getCurrentTopRoundness() {
        return this.mCurrentTopRoundness;
    }

    public float getCurrentBottomRoundness() {
        return this.mCurrentBottomRoundness;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getCurrentBackgroundRadiusBottom() {
        return this.mCurrentBottomRoundness * this.mOutlineRadius;
    }

    public boolean setBottomRoundness(float bottomRoundness, boolean animate) {
        if (this.mBottomRoundness != bottomRoundness) {
            this.mBottomRoundness = bottomRoundness;
            PropertyAnimator.setProperty(this, BOTTOM_ROUNDNESS, bottomRoundness, ROUNDNESS_PROPERTIES, animate);
            return true;
        }
        return false;
    }

    protected void setBackgroundTop(int backgroundTop) {
        if (this.mBackgroundTop != backgroundTop) {
            this.mBackgroundTop = backgroundTop;
            invalidateOutline();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTopRoundnessInternal(float topRoundness) {
        this.mCurrentTopRoundness = topRoundness;
        applyRoundness();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setBottomRoundnessInternal(float bottomRoundness) {
        this.mCurrentBottomRoundness = bottomRoundness;
        applyRoundness();
    }

    public void onDensityOrFontScaleChanged() {
        initDimens();
        applyRoundness();
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public void setActualHeight(int actualHeight, boolean notifyListeners) {
        int previousHeight = getActualHeight();
        super.setActualHeight(actualHeight, notifyListeners);
        if (previousHeight != actualHeight) {
            applyRoundness();
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public void setClipTopAmount(int clipTopAmount) {
        int previousAmount = getClipTopAmount();
        super.setClipTopAmount(clipTopAmount);
        if (previousAmount != clipTopAmount) {
            applyRoundness();
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public void setClipBottomAmount(int clipBottomAmount) {
        int previousAmount = getClipBottomAmount();
        super.setClipBottomAmount(clipBottomAmount);
        if (previousAmount != clipBottomAmount) {
            applyRoundness();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setOutlineAlpha(float alpha) {
        if (alpha != this.mOutlineAlpha) {
            this.mOutlineAlpha = alpha;
            applyRoundness();
        }
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public float getOutlineAlpha() {
        return this.mOutlineAlpha;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setOutlineRect(RectF rect) {
        if (rect != null) {
            setOutlineRect(rect.left, rect.top, rect.right, rect.bottom);
            return;
        }
        this.mCustomOutline = false;
        applyRoundness();
    }

    @Override // com.android.systemui.statusbar.notification.row.ExpandableView
    public int getOutlineTranslation() {
        return this.mCustomOutline ? this.mOutlineRect.left : (int) getTranslation();
    }

    public void updateOutline() {
        if (this.mCustomOutline) {
            return;
        }
        boolean hasOutline = needsOutline();
        setOutlineProvider(hasOutline ? this.mProvider : null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean needsOutline() {
        if (isChildInGroup()) {
            return isGroupExpanded() && !isGroupExpansionChanging();
        } else if (isSummaryWithChildren()) {
            return !isGroupExpanded() || isGroupExpansionChanging();
        } else {
            return true;
        }
    }

    public boolean isOutlineShowing() {
        ViewOutlineProvider op = getOutlineProvider();
        return op != null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setOutlineRect(float left, float top, float right, float bottom) {
        this.mCustomOutline = true;
        this.mOutlineRect.set((int) left, (int) top, (int) right, (int) bottom);
        Rect rect = this.mOutlineRect;
        rect.bottom = (int) Math.max(top, rect.bottom);
        Rect rect2 = this.mOutlineRect;
        rect2.right = (int) Math.max(left, rect2.right);
        applyRoundness();
    }

    public Path getCustomClipPath(View child) {
        return null;
    }
}
