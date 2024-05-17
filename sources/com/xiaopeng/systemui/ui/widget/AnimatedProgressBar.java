package com.xiaopeng.systemui.ui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.MathUtils;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import com.android.systemui.R;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.infoflow.util.OrientationUtil;
/* loaded from: classes24.dex */
public class AnimatedProgressBar extends ProgressBar {
    public static final long CLICK_DIST_THRESHOLD = 20;
    public static final long DEFAULT_LONG_CLICK_DELAY = 200;
    public static final int HORIZONTAL = 0;
    private static final String TAG = "AnimatedProgressBar";
    public static final int VERTICAL = 1;
    private boolean mImageEnable;
    private int mImageMargin;
    private Bitmap mMaxImageBitmap;
    private int mMaxImageResource;
    private Bitmap mMinImageBitmap;
    private int mMinImageResource;
    private int mOrientation;
    private int mProgress;
    private OnProgressListener mProgressListener;
    private String mText;
    private boolean mTextEnable;
    private int mTextSize;
    private ThemeViewModel mThemeViewModel;
    private long mTouchMillis;
    private float mTouchStartX;
    private float mTouchStartY;
    public boolean mTouchTracking;
    private float mTouchX;
    private float mTouchY;
    private boolean mTouchable;

    /* loaded from: classes24.dex */
    public interface OnProgressListener {
        void onProgressChanged(AnimatedProgressBar animatedProgressBar, int i, boolean z);

        void onStartTrackingTouch(AnimatedProgressBar animatedProgressBar);

        void onStopTrackingTouch(AnimatedProgressBar animatedProgressBar);
    }

    private void updateProgress() {
        setProgress(this.mProgress);
        onProgressChanged(true);
    }

    public AnimatedProgressBar(Context context) {
        super(context);
        this.mTouchMillis = 0L;
        this.mOrientation = 1;
        this.mText = "";
        this.mTextSize = 24;
        this.mTextEnable = false;
        this.mMinImageResource = -1;
        this.mMaxImageResource = -1;
        this.mImageMargin = 20;
        this.mImageEnable = false;
        this.mTouchable = false;
        this.mTouchTracking = false;
        init(context, null, 0, 0);
    }

    public AnimatedProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTouchMillis = 0L;
        this.mOrientation = 1;
        this.mText = "";
        this.mTextSize = 24;
        this.mTextEnable = false;
        this.mMinImageResource = -1;
        this.mMaxImageResource = -1;
        this.mImageMargin = 20;
        this.mImageEnable = false;
        this.mTouchable = false;
        this.mTouchTracking = false;
        init(context, attrs, 0, 0);
    }

    public AnimatedProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mTouchMillis = 0L;
        this.mOrientation = 1;
        this.mText = "";
        this.mTextSize = 24;
        this.mTextEnable = false;
        this.mMinImageResource = -1;
        this.mMaxImageResource = -1;
        this.mImageMargin = 20;
        this.mImageEnable = false;
        this.mTouchable = false;
        this.mTouchTracking = false;
        init(context, attrs, defStyleAttr, 0);
    }

    public AnimatedProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mTouchMillis = 0L;
        this.mOrientation = 1;
        this.mText = "";
        this.mTextSize = 24;
        this.mTextEnable = false;
        this.mMinImageResource = -1;
        this.mMaxImageResource = -1;
        this.mImageMargin = 20;
        this.mImageEnable = false;
        this.mTouchable = false;
        this.mTouchTracking = false;
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setProgressListener(OnProgressListener listener) {
        this.mProgressListener = listener;
    }

    public void destroy() {
        this.mTouchTracking = false;
    }

    public void setTouchable(boolean touchable) {
        this.mTouchable = touchable;
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this.mThemeViewModel = ThemeViewModel.create(context, attrs, defStyleAttr, defStyleRes);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.AnimatedProgressBar, 0, 0);
        this.mImageMargin = attributes.getDimensionPixelSize(1, 0);
        this.mImageEnable = attributes.getBoolean(0, false);
        this.mText = attributes.getString(5);
        this.mTextSize = attributes.getDimensionPixelSize(7, 20);
        this.mTextEnable = attributes.getBoolean(6, false);
        this.mOrientation = attributes.getInt(4, 1);
        this.mTouchable = attributes.getBoolean(8, true);
        attributes.recycle();
    }

    @Override // android.widget.ProgressBar
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        this.mProgress = progress;
        Logger.d(TAG, "setProgress progress=" + progress);
    }

    public void setText(String text) {
        this.mText = text;
        postInvalidate();
    }

    public boolean isTouchTracking() {
        return this.mTouchTracking;
    }

    @Override // android.widget.ProgressBar, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mThemeViewModel.onAttachedToWindow(this);
    }

    @Override // android.widget.ProgressBar, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mThemeViewModel.onDetachedFromWindow(this);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mThemeViewModel.onConfigurationChanged(this, newConfig);
    }

    @Override // android.widget.ProgressBar, android.view.View
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawContent(canvas);
    }

    @Override // android.view.View
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (this.mTouchable) {
            handleTouchEvent(event);
            return true;
        }
        return super.onTouchEvent(event);
    }

    private Paint createPaint() {
        Paint paint = new Paint();
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(sp2px(this.mContext, this.mTextSize));
        paint.setTypeface(Typeface.DEFAULT);
        return paint;
    }

    private void drawContent(Canvas canvas) {
        try {
            Paint paint = createPaint();
            Logger.d(TAG, "drawContent textEnable=" + this.mTextEnable + " imageEnable=" + this.mImageEnable + " text=" + this.mText + " paint=" + paint);
            if (this.mTextEnable && !TextUtils.isEmpty(this.mText)) {
                Rect bounds = new Rect();
                String text = this.mText;
                paint.getTextBounds(text, 0, text.length(), bounds);
                float textX = (getWidth() / 2) - bounds.centerX();
                float textY = (getHeight() / 2) - bounds.centerY();
                canvas.drawText(text, textX, textY, paint);
            }
            if (this.mImageEnable) {
                int i = this.mOrientation;
                if (i == 0) {
                    if (this.mMinImageBitmap != null) {
                        float left = this.mImageMargin;
                        float top = (getHeight() / 2) - (this.mMinImageBitmap.getHeight() / 2);
                        canvas.drawBitmap(this.mMinImageBitmap, left, top, paint);
                    }
                    if (this.mMaxImageBitmap != null) {
                        float left2 = (getWidth() - this.mMaxImageBitmap.getWidth()) - this.mImageMargin;
                        float top2 = (getHeight() / 2) - (this.mMaxImageBitmap.getHeight() / 2);
                        canvas.drawBitmap(this.mMaxImageBitmap, left2, top2, paint);
                    }
                } else if (i == 1) {
                    if (this.mMinImageBitmap != null) {
                        float left3 = (getWidth() / 2) - (this.mMinImageBitmap.getWidth() / 2);
                        float top3 = (getHeight() - this.mMinImageBitmap.getHeight()) - this.mImageMargin;
                        canvas.drawBitmap(this.mMinImageBitmap, left3, top3, paint);
                    }
                    if (this.mMaxImageBitmap != null) {
                        float left4 = (getWidth() / 2) - (this.mMaxImageBitmap.getWidth() / 2);
                        float top4 = this.mImageMargin;
                        canvas.drawBitmap(this.mMaxImageBitmap, left4, top4, paint);
                    }
                }
            }
        } catch (Exception e) {
            Logger.d(TAG, "drawContent e=" + e);
        }
    }

    private void handleTouchEvent(MotionEvent event) {
        boolean visible = isVisibleToUser();
        Logger.d(TAG, "onTouchEvent visible=" + visible + " event=" + event);
        if (event != null && visible) {
            int action = event.getAction();
            if (action == 0) {
                this.mTouchX = event.getX();
                this.mTouchY = event.getY();
                this.mTouchStartX = this.mTouchX;
                this.mTouchStartY = this.mTouchY;
                this.mTouchMillis = System.currentTimeMillis();
                onStartTrackingTouch();
            } else if (action != 1) {
                if (action == 2) {
                    if (!this.mTouchTracking) {
                        this.mTouchX = event.getX();
                        this.mTouchY = event.getY();
                    }
                    trackTouch(event.getX(), event.getY());
                    onStartTrackingTouch();
                }
            } else {
                float touchY = event.getY();
                event.getX();
                long touchMillis = System.currentTimeMillis();
                float moveY = Math.abs(touchY - this.mTouchStartY);
                Logger.d(TAG, "onTouchEvent moveY=" + moveY);
                if (moveY < 20.0f && touchMillis - this.mTouchMillis <= 200) {
                    clickTouch(event.getRawX(), event.getRawY());
                }
                onStopTrackingTouch();
            }
        }
    }

    private void clickTouch(float touchX, float touchY) {
        int MIN = getMin();
        int MAX = getMax();
        int WIDTH = getWidth();
        int HEIGHT = getHeight();
        int[] location = new int[2];
        getLocationOnScreen(location);
        int i = this.mOrientation;
        if (i == 0) {
            float offset = touchX - location[0];
            this.mProgress = ((int) ((MAX - MIN) * (offset / WIDTH))) + MIN;
        } else if (i == 1) {
            float offset2 = (location[1] + HEIGHT) - touchY;
            this.mProgress = ((int) ((MAX - MIN) * (offset2 / HEIGHT))) + MIN;
        }
        this.mProgress = MathUtils.constrain(this.mProgress, MIN, MAX);
        Logger.d(TAG, "clickTouch : touchY = " + touchY + " mProgress = " + this.mProgress);
        updateProgress();
    }

    private void trackTouch(float touchX, float touchY) {
        float scale;
        int MIN = getMin();
        int MAX = getMax();
        int WIDTH = getWidth();
        int HEIGHT = getHeight();
        int PROGRESS = getProgress();
        float unit = 0.0f;
        float offset = 0.0f;
        int i = this.mOrientation;
        if (i == 0) {
            unit = WIDTH / (MAX - MIN);
            offset = touchX - this.mTouchX;
        } else if (i == 1) {
            unit = HEIGHT / (MAX - MIN);
            offset = -(touchY - this.mTouchY);
        }
        if (unit == 0.0f) {
            scale = 0.0f;
        } else {
            scale = offset / unit;
        }
        if (Math.abs(scale) >= 1.0f) {
            this.mTouchX = touchX;
            this.mTouchY = touchY;
            this.mProgress = ((int) scale) + PROGRESS;
            this.mProgress = MathUtils.constrain(this.mProgress, MIN, MAX);
            updateProgress();
        }
    }

    private void onProgressChanged(boolean fromUser) {
        OnProgressListener onProgressListener = this.mProgressListener;
        if (onProgressListener != null) {
            onProgressListener.onProgressChanged(this, this.mProgress, fromUser);
        }
    }

    private void onStartTrackingTouch() {
        OnProgressListener onProgressListener = this.mProgressListener;
        if (onProgressListener != null && !this.mTouchTracking) {
            onProgressListener.onStartTrackingTouch(this);
        }
        this.mTouchTracking = true;
    }

    private void onStopTrackingTouch() {
        Logger.d(TAG, "onStopTrackingTouch");
        OnProgressListener onProgressListener = this.mProgressListener;
        if (onProgressListener != null && this.mTouchTracking) {
            onProgressListener.onStopTrackingTouch(this);
        }
        this.mTouchTracking = false;
    }

    @Override // android.view.View
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility != 0) {
            this.mTouchTracking = false;
        }
    }

    private static void setProgressBarDrawable(ProgressBar bar, Drawable drawable) {
        if (bar != null && drawable != null) {
            try {
                LayerDrawable layerDrawable = (LayerDrawable) bar.getProgressDrawable();
                if (layerDrawable != null) {
                    int layers = layerDrawable.getNumberOfLayers();
                    Drawable[] drawables = new Drawable[layers];
                    if (drawables.length > 0) {
                        for (int i = 0; i < layers; i++) {
                            int id = layerDrawable.getId(i);
                            if (id == 16908288) {
                                drawables[i] = drawable;
                                drawables[i].setBounds(layerDrawable.getDrawable(0).getBounds());
                                layerDrawable.setDrawable(i, drawables[i]);
                            } else if (id == 16908301) {
                                drawables[i] = drawable;
                                drawables[i].setBounds(layerDrawable.getDrawable(0).getBounds());
                                layerDrawable.setDrawable(i, drawables[i]);
                            }
                        }
                        bar.setProgressDrawable(layerDrawable);
                        bar.invalidate();
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    @Override // android.view.View
    public void setActivated(boolean activated) {
        super.setActivated(activated);
        if (!OrientationUtil.isLandscapeScreen(this.mContext)) {
            Logger.d(TAG, "setActivated : " + activated);
            setProgressDrawable(this.mContext.getDrawable(activated ? R.drawable.progress_horizontal : R.drawable.progress_horizontal_inactivated));
        }
    }

    private static boolean linear(float start, float middle, float end) {
        if (start == middle || middle == end) {
            return true;
        }
        if (middle < start) {
            if (middle > end) {
                return true;
            }
        } else if (middle < end) {
            return true;
        }
        return false;
    }

    private static float sp2px(Context context, float sp) {
        return TypedValue.applyDimension(2, sp, context.getResources().getDisplayMetrics());
    }
}
