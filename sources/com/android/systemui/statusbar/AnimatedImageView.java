package com.android.systemui.statusbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.RemotableViewMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import com.android.systemui.R;
@RemoteViews.RemoteView
/* loaded from: classes21.dex */
public class AnimatedImageView extends ImageView {
    private boolean mAllowAnimation;
    AnimationDrawable mAnim;
    boolean mAttached;
    int mDrawableId;
    private final boolean mHasOverlappingRendering;

    public AnimatedImageView(Context context) {
        this(context, null);
    }

    public AnimatedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mAllowAnimation = true;
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AnimatedImageView, 0, 0);
        try {
            this.mHasOverlappingRendering = a.getBoolean(R.styleable.AnimatedImageView_hasOverlappingRendering, true);
        } finally {
            a.recycle();
        }
    }

    public void setAllowAnimation(boolean allowAnimation) {
        AnimationDrawable animationDrawable;
        if (this.mAllowAnimation != allowAnimation) {
            this.mAllowAnimation = allowAnimation;
            updateAnim();
            if (!this.mAllowAnimation && (animationDrawable = this.mAnim) != null) {
                animationDrawable.setVisible(getVisibility() == 0, true);
            }
        }
    }

    private void updateAnim() {
        AnimationDrawable animationDrawable;
        Drawable drawable = getDrawable();
        if (this.mAttached && (animationDrawable = this.mAnim) != null) {
            animationDrawable.stop();
        }
        if (drawable instanceof AnimationDrawable) {
            this.mAnim = (AnimationDrawable) drawable;
            if (isShown() && this.mAllowAnimation) {
                this.mAnim.start();
                return;
            }
            return;
        }
        this.mAnim = null;
    }

    @Override // android.widget.ImageView
    public void setImageDrawable(Drawable drawable) {
        if (drawable != null) {
            if (this.mDrawableId == drawable.hashCode()) {
                return;
            }
            this.mDrawableId = drawable.hashCode();
        } else {
            this.mDrawableId = 0;
        }
        super.setImageDrawable(drawable);
        updateAnim();
    }

    @Override // android.widget.ImageView
    @RemotableViewMethod
    public void setImageResource(int resid) {
        if (this.mDrawableId == resid) {
            return;
        }
        this.mDrawableId = resid;
        super.setImageResource(resid);
        updateAnim();
    }

    @Override // android.widget.ImageView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mAttached = true;
        updateAnim();
    }

    @Override // android.widget.ImageView, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AnimationDrawable animationDrawable = this.mAnim;
        if (animationDrawable != null) {
            animationDrawable.stop();
        }
        this.mAttached = false;
    }

    @Override // android.view.View
    protected void onVisibilityChanged(View changedView, int vis) {
        super.onVisibilityChanged(changedView, vis);
        if (this.mAnim != null) {
            if (isShown() && this.mAllowAnimation) {
                this.mAnim.start();
            } else {
                this.mAnim.stop();
            }
        }
    }

    @Override // android.widget.ImageView, android.view.View
    public boolean hasOverlappingRendering() {
        return this.mHasOverlappingRendering;
    }
}
