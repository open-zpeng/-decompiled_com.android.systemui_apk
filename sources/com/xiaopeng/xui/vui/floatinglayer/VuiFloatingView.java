package com.xiaopeng.xui.vui.floatinglayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.utils.XLogUtils;
import com.xiaopeng.xui.utils.XuiUtils;
@SuppressLint({"ViewConstructor"})
/* loaded from: classes25.dex */
public class VuiFloatingView extends FrameLayout {
    private static final String TAG = "VuiFloatingView";
    private AlphaAnimation mAlphaAnimation;
    private AnimatedImageDrawable mAnimatedImageDrawable;
    private Callback mCallback;
    private Animatable2.AnimationCallback mCallbackPoxy;
    private Context mContext;
    private Drawable mDrawable;
    private ImageView mFloatingView;
    private boolean mIsNight;
    private boolean mNeedReLoadDrawable;
    private OnTouchListener mOnTouchListener;
    private int mType;
    private VuiFloatingDrawable mVuiDrawable;

    /* loaded from: classes25.dex */
    public interface Callback {
        void onAnimationEnd(VuiFloatingView vuiFloatingView);

        void onAnimationStart(VuiFloatingView vuiFloatingView);
    }

    /* loaded from: classes25.dex */
    public interface OnTouchListener {
        void onTouch(int i);
    }

    public void setLocation(int x, int y) {
        int paddingLeft;
        int paddingTop;
        int sw = XuiUtils.getDisplayWidth();
        int sh = XuiUtils.getDisplayHeight();
        int w = getVisibleWidth();
        int h = getVisibleHeight();
        if (x < 0) {
            paddingLeft = x;
        } else {
            int paddingLeft2 = x + w;
            if (paddingLeft2 > sw) {
                paddingLeft = (x + w) - sw;
            } else {
                paddingLeft = 0;
            }
        }
        if (y < 0) {
            paddingTop = y;
        } else {
            int paddingTop2 = y + h;
            if (paddingTop2 > sh) {
                paddingTop = (y + h) - sh;
            } else {
                paddingTop = 0;
            }
        }
        this.mFloatingView.setX(paddingLeft);
        this.mFloatingView.setY(paddingTop);
        XLogUtils.d(TAG, "setLocation x " + paddingLeft + " , y " + paddingTop);
    }

    public VuiFloatingView(@NonNull Context context, int type) {
        super(context);
        this.mNeedReLoadDrawable = true;
        this.mCallbackPoxy = new Animatable2.AnimationCallback() { // from class: com.xiaopeng.xui.vui.floatinglayer.VuiFloatingView.1
            @Override // android.graphics.drawable.Animatable2.AnimationCallback
            public void onAnimationStart(Drawable drawable) {
                super.onAnimationStart(drawable);
                if (VuiFloatingView.this.mCallback != null) {
                    VuiFloatingView.this.mCallback.onAnimationStart(VuiFloatingView.this);
                }
            }

            @Override // android.graphics.drawable.Animatable2.AnimationCallback
            public void onAnimationEnd(Drawable drawable) {
                super.onAnimationEnd(drawable);
                if (VuiFloatingView.this.mCallback != null) {
                    VuiFloatingView.this.mCallback.onAnimationEnd(VuiFloatingView.this);
                }
                VuiFloatingView.this.unRegisterAnimationCallback();
            }
        };
        this.mContext = context;
        this.mType = type;
        View rootView = LayoutInflater.from(this.mContext).inflate(R.layout.vui_floating, this);
        this.mFloatingView = (ImageView) rootView.findViewById(R.id.floating_view);
        if (VuiImageDecoderUtils.isSupportAlpha(type)) {
            this.mAlphaAnimation = new AlphaAnimation(0.0f, 1.0f);
            this.mAlphaAnimation.setDuration(600L);
        }
    }

    private void checkAnimatorDrawable() {
        if (VuiImageDecoderUtils.isSupportNight(this.mType)) {
            boolean isNight = ThemeManager.isNightMode(getContext());
            if (this.mIsNight != isNight || this.mNeedReLoadDrawable) {
                refreshAnimatorDrawable(VuiImageDecoderUtils.decoderImage(this.mContext, this.mType, isNight));
                XLogUtils.d(TAG, "checkAnimatorDrawable isNight " + isNight + " ,mNeedReLoadDrawable " + this.mNeedReLoadDrawable);
                this.mIsNight = isNight;
                this.mNeedReLoadDrawable = false;
            }
        } else if (this.mNeedReLoadDrawable) {
            refreshAnimatorDrawable(VuiImageDecoderUtils.decoderImage(this.mContext, this.mType));
            XLogUtils.d(TAG, "checkAnimatorDrawable mNeedReLoadDrawable " + this.mNeedReLoadDrawable);
            this.mNeedReLoadDrawable = false;
        }
    }

    @SuppressLint({"NewApi"})
    private void refreshAnimatorDrawable(Drawable drawable) {
        if (drawable == null) {
            XLogUtils.e(TAG, "refreshAnimatorDrawable drawable is null");
            return;
        }
        this.mFloatingView.setImageDrawable(drawable);
        this.mDrawable = drawable;
        if (drawable instanceof AnimatedImageDrawable) {
            this.mAnimatedImageDrawable = (AnimatedImageDrawable) drawable;
            this.mAnimatedImageDrawable.setRepeatCount(0);
        } else if (!(drawable instanceof VuiFloatingDrawable)) {
            XLogUtils.e(TAG, "refreshAnimatorDrawable drawable is not AnimatedImageDrawable");
        } else {
            this.mVuiDrawable = (VuiFloatingDrawable) drawable;
        }
    }

    public void prepare() {
        checkAnimatorDrawable();
    }

    public void requestNeedReLoadDrawable() {
        this.mNeedReLoadDrawable = true;
    }

    public void registerAnimationCallback(Callback callback) {
        this.mCallback = callback;
        AnimatedImageDrawable animatedImageDrawable = this.mAnimatedImageDrawable;
        if (animatedImageDrawable != null) {
            animatedImageDrawable.unregisterAnimationCallback(this.mCallbackPoxy);
            this.mAnimatedImageDrawable.registerAnimationCallback(this.mCallbackPoxy);
        }
        VuiFloatingDrawable vuiFloatingDrawable = this.mVuiDrawable;
        if (vuiFloatingDrawable != null) {
            vuiFloatingDrawable.registerAnimationCallback(this.mCallbackPoxy);
        }
    }

    public void unRegisterAnimationCallback() {
        this.mCallback = null;
        AnimatedImageDrawable animatedImageDrawable = this.mAnimatedImageDrawable;
        if (animatedImageDrawable != null) {
            animatedImageDrawable.unregisterAnimationCallback(this.mCallbackPoxy);
        }
        VuiFloatingDrawable vuiFloatingDrawable = this.mVuiDrawable;
        if (vuiFloatingDrawable != null) {
            vuiFloatingDrawable.unregisterAnimationCallback(this.mCallbackPoxy);
        }
    }

    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.mOnTouchListener = onTouchListener;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent event) {
        OnTouchListener onTouchListener;
        if ((event.getAction() == 0 || event.getAction() == 4) && (onTouchListener = this.mOnTouchListener) != null) {
            onTouchListener.onTouch(this.mType);
            if (this.mAnimatedImageDrawable != null) {
                requestNeedReLoadDrawable();
                return true;
            }
            return true;
        }
        return true;
    }

    public void start() {
        AnimatedImageDrawable animatedImageDrawable = this.mAnimatedImageDrawable;
        if (animatedImageDrawable != null) {
            if (!animatedImageDrawable.isRunning()) {
                this.mAnimatedImageDrawable.start();
            }
            AlphaAnimation alphaAnimation = this.mAlphaAnimation;
            if (alphaAnimation != null) {
                this.mFloatingView.startAnimation(alphaAnimation);
            }
        }
        VuiFloatingDrawable vuiFloatingDrawable = this.mVuiDrawable;
        if (vuiFloatingDrawable != null) {
            vuiFloatingDrawable.start();
        }
    }

    public void stop() {
        AnimatedImageDrawable animatedImageDrawable = this.mAnimatedImageDrawable;
        if (animatedImageDrawable != null && animatedImageDrawable.isRunning()) {
            this.mAnimatedImageDrawable.stop();
        }
        AlphaAnimation alphaAnimation = this.mAlphaAnimation;
        if (alphaAnimation != null) {
            alphaAnimation.cancel();
        }
        VuiFloatingDrawable vuiFloatingDrawable = this.mVuiDrawable;
        if (vuiFloatingDrawable != null) {
            vuiFloatingDrawable.stop();
        }
    }

    public int getVisibleHeight() {
        Drawable drawable = this.mDrawable;
        if (drawable != null) {
            return drawable.getIntrinsicHeight();
        }
        return 0;
    }

    public int getVisibleWidth() {
        Drawable drawable = this.mDrawable;
        if (drawable != null) {
            return drawable.getIntrinsicWidth();
        }
        return 0;
    }

    public void destroy() {
        AnimatedImageDrawable animatedImageDrawable = this.mAnimatedImageDrawable;
        if (animatedImageDrawable != null) {
            animatedImageDrawable.unregisterAnimationCallback(this.mCallbackPoxy);
        }
        VuiFloatingDrawable vuiFloatingDrawable = this.mVuiDrawable;
        if (vuiFloatingDrawable != null) {
            vuiFloatingDrawable.unregisterAnimationCallback(this.mCallbackPoxy);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        XLogUtils.i(TAG, "onAttachedToWindow type : " + this.mType);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        XLogUtils.d(TAG, "onDetachedFromWindow type : " + this.mType);
    }
}
