package com.humrousz.sequence;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.rastermill.FrescoSequence;
import android.util.Log;
import com.sequence.BaseAnimationSequence;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import kotlin.jvm.internal.LongCompanionObject;
/* loaded from: classes21.dex */
public class AnimationSequenceDrawable extends Drawable implements Animatable, Runnable {
    private static final long DEFAULT_DELAY_MS = 100;
    public static final int LOOP_DEFAULT = 3;
    public static final int LOOP_FINITE = 1;
    public static final int LOOP_INF = 2;
    private static final long MIN_DELAY_MS = 20;
    private static final int STATE_DECODING = 2;
    private static final int STATE_READY_TO_SWAP = 4;
    private static final int STATE_SCHEDULED = 1;
    private static final int STATE_WAITING_TO_SWAP = 3;
    private static final String TAG = "BaseAnimationSequence";
    private static HandlerThread sDecodingThread;
    private static Handler sDecodingThreadHandler;
    private final BaseAnimationSequence mAnimationSequence;
    private Bitmap mBackBitmap;
    private BitmapShader mBackBitmapShader;
    private final BitmapProvider mBitmapProvider;
    private boolean mCircleMaskEnabled;
    private int mCurrentLoop;
    private Runnable mDecodeRunnable;
    private boolean mDestroyed;
    private Runnable mFinishedCallbackRunnable;
    private Bitmap mFrontBitmap;
    private BitmapShader mFrontBitmapShader;
    private long mLastSwap;
    private final Object mLock;
    private int mLoopBehavior;
    private int mLoopCount;
    private int mNextFrameToDecode;
    private long mNextSwap;
    private OnFinishedListener mOnFinishedListener;
    private final Paint mPaint;
    private final Rect mSrcRect;
    private int mState;
    private RectF mTempRectF;
    private static final Object S_LOCK = new Object();
    private static BitmapProvider sAllocatingBitmapProvider = new BitmapProvider() { // from class: com.humrousz.sequence.AnimationSequenceDrawable.1
        @Override // com.humrousz.sequence.AnimationSequenceDrawable.BitmapProvider
        public Bitmap acquireBitmap(int minWidth, int minHeight) {
            return Bitmap.createBitmap(minWidth, minHeight, Bitmap.Config.ARGB_8888);
        }

        @Override // com.humrousz.sequence.AnimationSequenceDrawable.BitmapProvider
        public void releaseBitmap(Bitmap bitmap) {
        }
    };

    /* loaded from: classes21.dex */
    public interface BitmapProvider {
        Bitmap acquireBitmap(int i, int i2);

        void releaseBitmap(Bitmap bitmap);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes21.dex */
    public @interface LoopBehavior {
    }

    /* loaded from: classes21.dex */
    public interface OnFinishedListener {
        void onFinished(AnimationSequenceDrawable animationSequenceDrawable);
    }

    private static void initializeDecodingThread() {
        synchronized (S_LOCK) {
            if (sDecodingThread != null) {
                return;
            }
            sDecodingThread = new HandlerThread("BaseAnimationSequence decoding thread", 10);
            sDecodingThread.start();
            sDecodingThreadHandler = new Handler(sDecodingThread.getLooper());
        }
    }

    public void setOnFinishedListener(OnFinishedListener onFinishedListener) {
        this.mOnFinishedListener = onFinishedListener;
    }

    public void setLoopBehavior(int loopBehavior) {
        this.mLoopBehavior = loopBehavior;
    }

    public void setLoopCount(int loopCount) {
        this.mLoopCount = loopCount;
    }

    private static Bitmap acquireAndValidateBitmap(BitmapProvider bitmapProvider, int minWidth, int minHeight) {
        Bitmap bitmap = bitmapProvider.acquireBitmap(minWidth, minHeight);
        if (bitmap.getWidth() < minWidth || bitmap.getHeight() < minHeight || bitmap.getConfig() != Bitmap.Config.ARGB_8888) {
            throw new IllegalArgumentException("Invalid bitmap provided");
        }
        return bitmap;
    }

    public AnimationSequenceDrawable(BaseAnimationSequence sequence) {
        this(sequence, sAllocatingBitmapProvider);
    }

    public AnimationSequenceDrawable(InputStream inputStream) {
        this(FrescoSequence.decodeStream(inputStream));
    }

    public AnimationSequenceDrawable(BaseAnimationSequence sequence, BitmapProvider bitmapProvider) {
        this.mLock = new Object();
        this.mDestroyed = false;
        this.mLoopBehavior = 3;
        this.mLoopCount = 1;
        this.mTempRectF = new RectF();
        this.mDecodeRunnable = new Runnable() { // from class: com.humrousz.sequence.AnimationSequenceDrawable.2
            @Override // java.lang.Runnable
            public void run() {
                Exception e;
                synchronized (AnimationSequenceDrawable.this.mLock) {
                    if (AnimationSequenceDrawable.this.mDestroyed) {
                        return;
                    }
                    int nextFrame = AnimationSequenceDrawable.this.mNextFrameToDecode;
                    if (nextFrame < 0) {
                        return;
                    }
                    Bitmap bitmap = AnimationSequenceDrawable.this.mBackBitmap;
                    AnimationSequenceDrawable.this.mState = 2;
                    int lastFrame = nextFrame - 2;
                    long invalidateTimeMs = 0;
                    try {
                        invalidateTimeMs = AnimationSequenceDrawable.this.mAnimationSequence.getFrame(nextFrame, bitmap, lastFrame);
                        e = null;
                    } catch (Exception e2) {
                        Log.e(AnimationSequenceDrawable.TAG, "exception during decode: " + e2);
                        e = 1;
                    }
                    if (invalidateTimeMs < 20) {
                        invalidateTimeMs = AnimationSequenceDrawable.DEFAULT_DELAY_MS;
                    }
                    boolean schedule = false;
                    Bitmap bitmapToRelease = null;
                    synchronized (AnimationSequenceDrawable.this.mLock) {
                        if (AnimationSequenceDrawable.this.mDestroyed) {
                            bitmapToRelease = AnimationSequenceDrawable.this.mBackBitmap;
                            AnimationSequenceDrawable.this.mBackBitmap = null;
                        } else if (AnimationSequenceDrawable.this.mNextFrameToDecode >= 0 && AnimationSequenceDrawable.this.mState == 2) {
                            schedule = true;
                            AnimationSequenceDrawable.this.mNextSwap = e != null ? LongCompanionObject.MAX_VALUE : AnimationSequenceDrawable.this.mLastSwap + invalidateTimeMs;
                            AnimationSequenceDrawable.this.mState = 3;
                        }
                    }
                    if (schedule) {
                        AnimationSequenceDrawable animationSequenceDrawable = AnimationSequenceDrawable.this;
                        animationSequenceDrawable.scheduleSelf(animationSequenceDrawable, animationSequenceDrawable.mNextSwap);
                    }
                    if (bitmapToRelease != null) {
                        AnimationSequenceDrawable.this.mBitmapProvider.releaseBitmap(bitmapToRelease);
                    }
                }
            }
        };
        this.mFinishedCallbackRunnable = new Runnable() { // from class: com.humrousz.sequence.AnimationSequenceDrawable.3
            @Override // java.lang.Runnable
            public void run() {
                synchronized (AnimationSequenceDrawable.this.mLock) {
                    AnimationSequenceDrawable.this.mNextFrameToDecode = -1;
                    AnimationSequenceDrawable.this.mState = 0;
                }
                if (AnimationSequenceDrawable.this.mOnFinishedListener != null) {
                    AnimationSequenceDrawable.this.mOnFinishedListener.onFinished(AnimationSequenceDrawable.this);
                }
            }
        };
        int width = sequence.getWidth();
        int height = sequence.getHeight();
        this.mBitmapProvider = bitmapProvider;
        this.mFrontBitmap = acquireAndValidateBitmap(bitmapProvider, width, height);
        this.mBackBitmap = acquireAndValidateBitmap(bitmapProvider, width, height);
        this.mSrcRect = new Rect(0, 0, width, height);
        this.mPaint = new Paint();
        this.mPaint.setFilterBitmap(true);
        this.mFrontBitmapShader = new BitmapShader(this.mFrontBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        this.mBackBitmapShader = new BitmapShader(this.mBackBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        this.mLastSwap = 0L;
        this.mNextFrameToDecode = -1;
        this.mAnimationSequence = sequence;
        this.mAnimationSequence.getFrame(0, this.mFrontBitmap, -1);
        initializeDecodingThread();
    }

    public final void setCircleMaskEnabled(boolean circleMaskEnabled) {
        if (this.mCircleMaskEnabled != circleMaskEnabled) {
            this.mCircleMaskEnabled = circleMaskEnabled;
            this.mPaint.setAntiAlias(circleMaskEnabled);
            invalidateSelf();
        }
    }

    public final boolean getCircleMaskEnabled() {
        return this.mCircleMaskEnabled;
    }

    private void checkDestroyedLocked() {
        if (this.mDestroyed) {
            throw new IllegalStateException("Cannot perform operation on recycled drawable");
        }
    }

    public boolean isDestroyed() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mDestroyed;
        }
        return z;
    }

    public void destroy() {
        Bitmap bitmapToReleaseA;
        if (this.mBitmapProvider == null) {
            throw new IllegalStateException("BitmapProvider must be non-null");
        }
        Bitmap bitmapToReleaseB = null;
        synchronized (this.mLock) {
            checkDestroyedLocked();
            bitmapToReleaseA = this.mFrontBitmap;
            this.mFrontBitmap = null;
            if (this.mState != 2) {
                bitmapToReleaseB = this.mBackBitmap;
                this.mBackBitmap = null;
            }
            this.mDestroyed = true;
        }
        this.mBitmapProvider.releaseBitmap(bitmapToReleaseA);
        if (bitmapToReleaseB != null) {
            this.mBitmapProvider.releaseBitmap(bitmapToReleaseB);
        }
        BaseAnimationSequence baseAnimationSequence = this.mAnimationSequence;
        if (baseAnimationSequence != null) {
            baseAnimationSequence.destroy();
        }
    }

    @Override // android.graphics.drawable.Animatable
    public void start() {
        if (!isRunning()) {
            synchronized (this.mLock) {
                checkDestroyedLocked();
                if (this.mState == 1) {
                    return;
                }
                this.mCurrentLoop = 0;
                scheduleDecodeLocked();
            }
        }
    }

    @Override // android.graphics.drawable.Animatable
    public void stop() {
        if (isRunning()) {
            unscheduleSelf(this);
        }
    }

    @Override // android.graphics.drawable.Animatable
    public boolean isRunning() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mNextFrameToDecode > -1 && !this.mDestroyed;
        }
        return z;
    }

    @Override // android.graphics.drawable.Drawable
    public void unscheduleSelf(Runnable what) {
        synchronized (this.mLock) {
            this.mNextFrameToDecode = -1;
            this.mState = 0;
        }
        super.unscheduleSelf(what);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setVisible(boolean visible, boolean restart) {
        boolean changed = super.setVisible(visible, restart);
        if (!visible) {
            stop();
        } else if (restart || changed) {
            stop();
            start();
        } else if (!isRunning() && visible) {
            start();
        }
        return changed;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(@NonNull Canvas canvas) {
        synchronized (this.mLock) {
            checkDestroyedLocked();
            if (this.mState == 3 && this.mNextSwap - SystemClock.uptimeMillis() <= 0) {
                this.mState = 4;
            }
            if (isRunning() && this.mState == 4) {
                Bitmap tmp = this.mBackBitmap;
                this.mBackBitmap = this.mFrontBitmap;
                this.mFrontBitmap = tmp;
                BitmapShader tmpShader = this.mBackBitmapShader;
                this.mBackBitmapShader = this.mFrontBitmapShader;
                this.mFrontBitmapShader = tmpShader;
                this.mLastSwap = SystemClock.uptimeMillis();
                boolean continueLooping = true;
                boolean z = true;
                if (this.mNextFrameToDecode == this.mAnimationSequence.getFrameCount() - 1) {
                    this.mCurrentLoop++;
                    if ((this.mLoopBehavior != 1 || this.mCurrentLoop != this.mLoopCount) && (this.mLoopBehavior != 3 || this.mCurrentLoop != this.mAnimationSequence.getDefaultLoopCount())) {
                        z = false;
                    }
                    boolean stopLooping = z;
                    if (stopLooping) {
                        continueLooping = false;
                    }
                }
                if (continueLooping) {
                    scheduleDecodeLocked();
                } else {
                    scheduleSelf(this.mFinishedCallbackRunnable, 0L);
                }
            }
        }
        if (this.mCircleMaskEnabled) {
            Rect bounds = getBounds();
            int bitmapWidth = getIntrinsicWidth();
            int bitmapHeight = getIntrinsicHeight();
            float scaleX = (bounds.width() * 1.0f) / bitmapWidth;
            float scaleY = (bounds.height() * 1.0f) / bitmapHeight;
            canvas.save();
            canvas.translate(bounds.left, bounds.top);
            canvas.scale(scaleX, scaleY);
            float unscaledCircleDiameter = Math.min(bounds.width(), bounds.height());
            float scaledDiameterX = unscaledCircleDiameter / scaleX;
            float scaledDiameterY = unscaledCircleDiameter / scaleY;
            this.mTempRectF.set((bitmapWidth - scaledDiameterX) / 2.0f, (bitmapHeight - scaledDiameterY) / 2.0f, (bitmapWidth + scaledDiameterX) / 2.0f, (bitmapHeight + scaledDiameterY) / 2.0f);
            this.mPaint.setShader(this.mFrontBitmapShader);
            canvas.drawOval(this.mTempRectF, this.mPaint);
            canvas.restore();
            return;
        }
        this.mPaint.setShader(null);
        canvas.drawBitmap(this.mFrontBitmap, this.mSrcRect, getBounds(), this.mPaint);
    }

    private void scheduleDecodeLocked() {
        this.mState = 1;
        this.mNextFrameToDecode = (this.mNextFrameToDecode + 1) % this.mAnimationSequence.getFrameCount();
        sDecodingThreadHandler.post(this.mDecodeRunnable);
    }

    @Override // android.graphics.drawable.Drawable
    public void setFilterBitmap(boolean filter) {
        this.mPaint.setFilterBitmap(filter);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.mPaint.setAlpha(alpha);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mAnimationSequence.getWidth();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mAnimationSequence.getHeight();
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return this.mAnimationSequence.isOpaque() ? -1 : -2;
    }

    @Override // java.lang.Runnable
    public void run() {
        boolean invalidate = false;
        synchronized (this.mLock) {
            if (this.mNextFrameToDecode >= 0 && this.mState == 3) {
                this.mState = 4;
                invalidate = true;
            }
        }
        if (invalidate) {
            invalidateSelf();
        }
    }
}
