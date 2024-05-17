package com.xiaopeng.xui.vui.floatinglayer;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.Drawable;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.xiaopeng.xui.utils.XLogUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
/* loaded from: classes25.dex */
class VuiFloatingDrawable extends Drawable implements Animatable2 {
    private static final int PADDING = 0;
    private ValueAnimator mAlphaAnimatorIn;
    private ValueAnimator mAlphaAnimatorOut;
    private ArrayList<Animatable2.AnimationCallback> mAnimationCallbacks;
    private AnimatorSet mAnimatorSet;
    private ArrayList<Animator> mAnimators;
    private Bitmap mBitmap;
    private Animator.AnimatorListener mEndListener;
    private Matrix mMatrix;
    private Paint mPaint;
    private PaintFlagsDrawFilter mPaintFlagsDrawFilter;
    private boolean mRunning;
    private ValueAnimator[] mScaleAnimator;
    private boolean mStarting;
    private ValueAnimator.AnimatorUpdateListener mUpdateListenerAlpha;
    private ValueAnimator.AnimatorUpdateListener mUpdateListenerScale;

    private VuiFloatingDrawable() {
        this.mUpdateListenerAlpha = new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.xui.vui.floatinglayer.VuiFloatingDrawable.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                int v = ((Integer) animation.getAnimatedValue()).intValue();
                VuiFloatingDrawable.this.mPaint.setAlpha(v);
                VuiFloatingDrawable.this.invalidateSelf();
            }
        };
        this.mUpdateListenerScale = new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.xui.vui.floatinglayer.VuiFloatingDrawable.2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = ((Float) animation.getAnimatedValue()).floatValue();
                VuiFloatingDrawable.this.mMatrix.setScale(v, v, VuiFloatingDrawable.this.mBitmap.getWidth() / 2, VuiFloatingDrawable.this.mBitmap.getHeight() / 2);
                VuiFloatingDrawable.this.invalidateSelf();
            }
        };
        this.mEndListener = new Animator.AnimatorListener() { // from class: com.xiaopeng.xui.vui.floatinglayer.VuiFloatingDrawable.3
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                VuiFloatingDrawable.this.log("onAnimationEnd ");
                VuiFloatingDrawable.this.stop();
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animation) {
            }
        };
    }

    public VuiFloatingDrawable(Bitmap bitmap) {
        this.mUpdateListenerAlpha = new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.xui.vui.floatinglayer.VuiFloatingDrawable.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                int v = ((Integer) animation.getAnimatedValue()).intValue();
                VuiFloatingDrawable.this.mPaint.setAlpha(v);
                VuiFloatingDrawable.this.invalidateSelf();
            }
        };
        this.mUpdateListenerScale = new ValueAnimator.AnimatorUpdateListener() { // from class: com.xiaopeng.xui.vui.floatinglayer.VuiFloatingDrawable.2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = ((Float) animation.getAnimatedValue()).floatValue();
                VuiFloatingDrawable.this.mMatrix.setScale(v, v, VuiFloatingDrawable.this.mBitmap.getWidth() / 2, VuiFloatingDrawable.this.mBitmap.getHeight() / 2);
                VuiFloatingDrawable.this.invalidateSelf();
            }
        };
        this.mEndListener = new Animator.AnimatorListener() { // from class: com.xiaopeng.xui.vui.floatinglayer.VuiFloatingDrawable.3
            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                VuiFloatingDrawable.this.log("onAnimationEnd ");
                VuiFloatingDrawable.this.stop();
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
            }

            @Override // android.animation.Animator.AnimatorListener
            public void onAnimationRepeat(Animator animation) {
            }
        };
        this.mBitmap = bitmap;
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mMatrix = new Matrix();
        this.mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0, 3);
        this.mAlphaAnimatorIn = ValueAnimator.ofInt(0, 255);
        this.mAlphaAnimatorIn.setDuration(500L);
        this.mAlphaAnimatorIn.setInterpolator(new DecelerateInterpolator());
        this.mScaleAnimator = new ValueAnimator[4];
        Interpolator interpolator = new DecelerateInterpolator();
        int i = 0;
        while (true) {
            ValueAnimator[] valueAnimatorArr = this.mScaleAnimator;
            if (i < valueAnimatorArr.length) {
                if (i % 2 == 0) {
                    valueAnimatorArr[i] = ValueAnimator.ofFloat(1.0f, 1.05f);
                } else {
                    valueAnimatorArr[i] = ValueAnimator.ofFloat(1.05f, 1.0f);
                }
                this.mScaleAnimator[i].setDuration(1000L);
                this.mScaleAnimator[i].setInterpolator(interpolator);
                i++;
            } else {
                this.mAlphaAnimatorOut = ValueAnimator.ofInt(255, 0);
                this.mAlphaAnimatorOut.setDuration(250L);
                this.mAlphaAnimatorOut.setInterpolator(new AccelerateInterpolator());
                this.mAnimatorSet = new AnimatorSet();
                this.mAnimators = new ArrayList<>();
                this.mAnimators.add(this.mAlphaAnimatorIn);
                this.mAnimators.addAll(Arrays.asList(this.mScaleAnimator));
                this.mAnimators.add(this.mAlphaAnimatorOut);
                this.mAnimatorSet.playSequentially(this.mAnimators);
                return;
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setVisible(boolean visible, boolean restart) {
        if (!visible) {
            log("setVisible false ");
            stop();
        }
        return super.setVisible(visible, restart);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        Bitmap bitmap = this.mBitmap;
        if (bitmap != null) {
            return bitmap.getWidth() + 0;
        }
        return super.getIntrinsicWidth();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        Bitmap bitmap = this.mBitmap;
        if (bitmap != null) {
            return bitmap.getHeight() + 0;
        }
        return super.getIntrinsicHeight();
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(@NonNull Canvas canvas) {
        if (!isVisible()) {
            return;
        }
        if (this.mStarting) {
            this.mStarting = false;
            this.mRunning = true;
            postOnAnimationStart();
        }
        if (!this.mRunning) {
            return;
        }
        canvas.setDrawFilter(this.mPaintFlagsDrawFilter);
        canvas.translate(0.0f, 0.0f);
        canvas.drawBitmap(this.mBitmap, this.mMatrix, this.mPaint);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Animatable2
    public void registerAnimationCallback(@NonNull Animatable2.AnimationCallback callback) {
        if (this.mAnimationCallbacks == null) {
            this.mAnimationCallbacks = new ArrayList<>();
        }
        if (!this.mAnimationCallbacks.contains(callback)) {
            this.mAnimationCallbacks.add(callback);
        }
    }

    @Override // android.graphics.drawable.Animatable2
    public boolean unregisterAnimationCallback(@NonNull Animatable2.AnimationCallback callback) {
        ArrayList<Animatable2.AnimationCallback> arrayList = this.mAnimationCallbacks;
        if (arrayList == null || !arrayList.remove(callback)) {
            return false;
        }
        if (this.mAnimationCallbacks.isEmpty()) {
            clearAnimationCallbacks();
            return true;
        }
        return true;
    }

    @Override // android.graphics.drawable.Animatable2
    public void clearAnimationCallbacks() {
        if (this.mAnimationCallbacks != null) {
            this.mAnimationCallbacks = null;
        }
    }

    @Override // android.graphics.drawable.Animatable
    public void start() {
        ValueAnimator[] valueAnimatorArr;
        if (this.mRunning) {
            stop();
        }
        this.mStarting = true;
        this.mPaint.setAlpha(0);
        for (ValueAnimator animator : this.mScaleAnimator) {
            animator.addUpdateListener(this.mUpdateListenerScale);
        }
        this.mAlphaAnimatorIn.addUpdateListener(this.mUpdateListenerAlpha);
        this.mAlphaAnimatorOut.addUpdateListener(this.mUpdateListenerAlpha);
        this.mAlphaAnimatorOut.addListener(this.mEndListener);
        this.mAnimatorSet.start();
    }

    @Override // android.graphics.drawable.Animatable
    public void stop() {
        this.mAnimatorSet.cancel();
        ArrayList<Animator> arrayList = this.mAnimators;
        if (arrayList != null) {
            Iterator<Animator> it = arrayList.iterator();
            while (it.hasNext()) {
                Animator anim = it.next();
                if (anim instanceof ValueAnimator) {
                    ((ValueAnimator) anim).removeAllUpdateListeners();
                }
                anim.removeAllListeners();
            }
        }
        this.mMatrix.reset();
        if (this.mRunning) {
            this.mRunning = false;
            postOnAnimationEnd();
        }
    }

    private void postOnAnimationStart() {
        ArrayList<Animatable2.AnimationCallback> arrayList = this.mAnimationCallbacks;
        if (arrayList == null) {
            return;
        }
        Iterator<Animatable2.AnimationCallback> it = arrayList.iterator();
        while (it.hasNext()) {
            Animatable2.AnimationCallback callback = it.next();
            callback.onAnimationStart(this);
        }
    }

    private void postOnAnimationEnd() {
        log("postOnAnimationEnd ");
        ArrayList<Animatable2.AnimationCallback> arrayList = this.mAnimationCallbacks;
        if (arrayList == null) {
            return;
        }
        Iterator<Animatable2.AnimationCallback> it = arrayList.iterator();
        while (it.hasNext()) {
            Animatable2.AnimationCallback callback = it.next();
            callback.onAnimationEnd(this);
        }
    }

    @Override // android.graphics.drawable.Animatable
    public boolean isRunning() {
        return this.mRunning;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log(String msg) {
        XLogUtils.d("VuiFloatingDrawable", msg);
    }
}
