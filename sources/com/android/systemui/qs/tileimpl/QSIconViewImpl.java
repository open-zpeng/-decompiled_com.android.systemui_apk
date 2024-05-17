package com.android.systemui.qs.tileimpl;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import com.android.systemui.R;
import com.android.systemui.plugins.qs.QSIconView;
import com.android.systemui.plugins.qs.QSTile;
import com.android.systemui.qs.AlphaControlledSignalTileView;
import com.android.systemui.statusbar.phone.NavigationBarInflaterView;
import java.util.Objects;
/* loaded from: classes21.dex */
public class QSIconViewImpl extends QSIconView {
    public static final long QS_ANIM_LENGTH = 350;
    private boolean mAnimationEnabled;
    protected final View mIcon;
    protected final int mIconSizePx;
    private QSTile.Icon mLastIcon;
    private int mState;
    private int mTint;

    public QSIconViewImpl(Context context) {
        super(context);
        this.mAnimationEnabled = true;
        this.mState = -1;
        Resources res = context.getResources();
        this.mIconSizePx = res.getDimensionPixelSize(R.dimen.qs_tile_icon_size);
        this.mIcon = createIcon();
        addView(this.mIcon);
    }

    @Override // com.android.systemui.plugins.qs.QSIconView
    public void disableAnimation() {
        this.mAnimationEnabled = false;
    }

    @Override // com.android.systemui.plugins.qs.QSIconView
    public View getIconView() {
        return this.mIcon;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = View.MeasureSpec.getSize(widthMeasureSpec);
        int iconSpec = exactly(this.mIconSizePx);
        this.mIcon.measure(View.MeasureSpec.makeMeasureSpec(w, getIconMeasureMode()), iconSpec);
        setMeasuredDimension(w, this.mIcon.getMeasuredHeight());
    }

    @Override // android.view.View
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append('[');
        sb.append("state=" + this.mState);
        sb.append(", tint=" + this.mTint);
        if (this.mLastIcon != null) {
            sb.append(", lastIcon=" + this.mLastIcon.toString());
        }
        sb.append(NavigationBarInflaterView.SIZE_MOD_END);
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int w = getMeasuredWidth();
        int iconLeft = (w - this.mIcon.getMeasuredWidth()) / 2;
        layout(this.mIcon, iconLeft, 0);
    }

    @Override // com.android.systemui.plugins.qs.QSIconView
    public void setIcon(QSTile.State state, boolean allowAnimations) {
        setIcon((ImageView) this.mIcon, state, allowAnimations);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* renamed from: updateIcon */
    public void lambda$setIcon$0$QSIconViewImpl(ImageView iv, QSTile.State state, boolean allowAnimations) {
        Drawable d;
        QSTile.Icon icon = state.iconSupplier != null ? state.iconSupplier.get() : state.icon;
        if (!Objects.equals(icon, iv.getTag(R.id.qs_icon_tag)) || !Objects.equals(state.slash, iv.getTag(R.id.qs_slash_tag))) {
            boolean shouldAnimate = allowAnimations && shouldAnimate(iv);
            this.mLastIcon = icon;
            if (icon == null) {
                d = null;
            } else {
                d = shouldAnimate ? icon.getDrawable(this.mContext) : icon.getInvisibleDrawable(this.mContext);
            }
            int padding = icon != null ? icon.getPadding() : 0;
            if (d != null) {
                d.setAutoMirrored(false);
                d.setLayoutDirection(getLayoutDirection());
            }
            if (iv instanceof SlashImageView) {
                ((SlashImageView) iv).setAnimationEnabled(shouldAnimate);
                ((SlashImageView) iv).setState(null, d);
            } else {
                iv.setImageDrawable(d);
            }
            iv.setTag(R.id.qs_icon_tag, icon);
            iv.setTag(R.id.qs_slash_tag, state.slash);
            iv.setPadding(0, padding, 0, padding);
            if (d instanceof Animatable2) {
                final Animatable2 a = (Animatable2) d;
                a.start();
                if (state.isTransient) {
                    a.registerAnimationCallback(new Animatable2.AnimationCallback() { // from class: com.android.systemui.qs.tileimpl.QSIconViewImpl.1
                        @Override // android.graphics.drawable.Animatable2.AnimationCallback
                        public void onAnimationEnd(Drawable drawable) {
                            a.start();
                        }
                    });
                }
            }
        }
    }

    private boolean shouldAnimate(ImageView iv) {
        return this.mAnimationEnabled && iv.isShown() && iv.getDrawable() != null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setIcon(final ImageView iv, final QSTile.State state, final boolean allowAnimations) {
        if (state.disabledByPolicy) {
            iv.setColorFilter(getContext().getColor(R.color.qs_tile_disabled_color));
        } else {
            iv.clearColorFilter();
        }
        if (state.state != this.mState) {
            int color = getColor(state.state);
            this.mState = state.state;
            if (this.mTint != 0 && allowAnimations && shouldAnimate(iv)) {
                animateGrayScale(this.mTint, color, iv, new Runnable() { // from class: com.android.systemui.qs.tileimpl.-$$Lambda$QSIconViewImpl$xTIBDrD33UKSYZv6_hT3f3X3znk
                    @Override // java.lang.Runnable
                    public final void run() {
                        QSIconViewImpl.this.lambda$setIcon$0$QSIconViewImpl(iv, state, allowAnimations);
                    }
                });
                this.mTint = color;
                return;
            }
            if (iv instanceof AlphaControlledSignalTileView.AlphaControlledSlashImageView) {
                ((AlphaControlledSignalTileView.AlphaControlledSlashImageView) iv).setFinalImageTintList(ColorStateList.valueOf(color));
            } else {
                setTint(iv, color);
            }
            this.mTint = color;
            lambda$setIcon$0$QSIconViewImpl(iv, state, allowAnimations);
            return;
        }
        lambda$setIcon$0$QSIconViewImpl(iv, state, allowAnimations);
    }

    protected int getColor(int state) {
        return QSTileImpl.getColorForState(getContext(), state);
    }

    private void animateGrayScale(int fromColor, int toColor, final ImageView iv, final Runnable endRunnable) {
        if (iv instanceof AlphaControlledSignalTileView.AlphaControlledSlashImageView) {
            ((AlphaControlledSignalTileView.AlphaControlledSlashImageView) iv).setFinalImageTintList(ColorStateList.valueOf(toColor));
        }
        if (this.mAnimationEnabled && ValueAnimator.areAnimatorsEnabled()) {
            final float fromAlpha = Color.alpha(fromColor);
            final float toAlpha = Color.alpha(toColor);
            final float fromChannel = Color.red(fromColor);
            final float toChannel = Color.red(toColor);
            ValueAnimator anim = ValueAnimator.ofFloat(0.0f, 1.0f);
            anim.setDuration(350L);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.qs.tileimpl.-$$Lambda$QSIconViewImpl$CeqSBPdIhNYTow_6QM6a9ZwQyb8
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    QSIconViewImpl.lambda$animateGrayScale$1(fromAlpha, toAlpha, fromChannel, toChannel, iv, valueAnimator);
                }
            });
            anim.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.qs.tileimpl.QSIconViewImpl.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    endRunnable.run();
                }
            });
            anim.start();
            return;
        }
        setTint(iv, toColor);
        endRunnable.run();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$animateGrayScale$1(float fromAlpha, float toAlpha, float fromChannel, float toChannel, ImageView iv, ValueAnimator animation) {
        float fraction = animation.getAnimatedFraction();
        int alpha = (int) (((toAlpha - fromAlpha) * fraction) + fromAlpha);
        int channel = (int) (((toChannel - fromChannel) * fraction) + fromChannel);
        setTint(iv, Color.argb(alpha, channel, channel, channel));
    }

    public static void setTint(ImageView iv, int color) {
        iv.setImageTintList(ColorStateList.valueOf(color));
    }

    protected int getIconMeasureMode() {
        return 1073741824;
    }

    protected View createIcon() {
        ImageView icon = new SlashImageView(this.mContext);
        icon.setId(16908294);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        return icon;
    }

    protected final int exactly(int size) {
        return View.MeasureSpec.makeMeasureSpec(size, 1073741824);
    }

    protected final void layout(View child, int left, int top) {
        child.layout(left, top, child.getMeasuredWidth() + left, child.getMeasuredHeight() + top);
    }
}
