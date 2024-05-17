package com.xiaopeng.systemui.infoflow.speech.ui.widget.seekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.media.MediaPlayer2;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import com.android.systemui.R;
import java.io.PrintStream;
/* loaded from: classes24.dex */
public class ArcSeekBar extends View {
    private static final float CIRCLE_ANGLE = 360.0f;
    private static final int DEFAULT_ARC_WIDTH = 40;
    private static final int DEFAULT_BORDER_COLOR = 268435455;
    private static final int DEFAULT_BORDER_WIDTH = 0;
    private static final int DEFAULT_EDGE_LENGTH = 260;
    private static final int DEFAULT_MAX_VALUE = 100;
    private static final int DEFAULT_MIN_VALUE = 0;
    private static final float DEFAULT_OPEN_ANGLE = 120.0f;
    private static final float DEFAULT_ROTATE_ANGLE = 90.0f;
    private static final int DEFAULT_SHADOW_RADIUS = 0;
    private static final int DEFAULT_THUMB_COLOR = -1;
    private static final int DEFAULT_THUMB_RADIUS = 15;
    private static final int DEFAULT_THUMB_SHADOW_COLOR = -16777216;
    private static final int DEFAULT_THUMB_SHADOW_RADIUS = 0;
    private static final int DEFAULT_THUMB_WIDTH = 2;
    private static final String KEY_PROGRESS_PRESENT = "PRESENT";
    private static final int THUMB_MODE_FILL = 1;
    private static final int THUMB_MODE_FILL_STROKE = 2;
    private static final int THUMB_MODE_STROKE = 0;
    private int lastProgress;
    private boolean mAllowTouchSkip;
    private int[] mArcColors;
    private Paint mArcPaint;
    private Region mArcRegion;
    private float mArcWidth;
    private int mBorderColor;
    private Paint mBorderPaint;
    private Path mBorderPath;
    private int mBorderWidth;
    private boolean mCanDrag;
    private float mCenterX;
    private float mCenterY;
    private GestureDetector mDetector;
    private Matrix mInvertMatrix;
    private int mMaxValue;
    private int mMinValue;
    private OnProgressChangeListener mOnProgressChangeListener;
    private float mOpenAngle;
    private float mProgressPresent;
    private float mRotateAngle;
    private Path mSeekPath;
    private PathMeasure mSeekPathMeasure;
    private Paint mShadowPaint;
    private int mShadowRadius;
    private float[] mTempPos;
    private float[] mTempTan;
    private int mThumbColor;
    private int mThumbMode;
    private Paint mThumbPaint;
    private float mThumbRadius;
    private int mThumbShadowColor;
    private float mThumbShadowRadius;
    private float mThumbWidth;
    private float mThumbX;
    private float mThumbY;
    private boolean moved;

    /* loaded from: classes24.dex */
    public interface OnProgressChangeListener {
        void onProgressChanged(ArcSeekBar arcSeekBar, int i, boolean z);

        void onStartTrackingTouch(ArcSeekBar arcSeekBar);

        void onStopTrackingTouch(ArcSeekBar arcSeekBar);
    }

    public ArcSeekBar(Context context) {
        this(context, null);
    }

    public ArcSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mProgressPresent = 0.0f;
        this.mCanDrag = false;
        this.mAllowTouchSkip = false;
        this.moved = false;
        this.lastProgress = -1;
        setSaveEnabled(true);
        setLayerType(1, null);
        initAttrs(context, attrs);
        initData();
        initPaint();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ArcSeekBar);
        this.mArcColors = getArcColors(context, ta);
        this.mArcWidth = ta.getDimensionPixelSize(15, dp2px(40));
        this.mOpenAngle = ta.getFloat(5, DEFAULT_OPEN_ANGLE);
        this.mRotateAngle = ta.getFloat(7, DEFAULT_ROTATE_ANGLE);
        this.mMaxValue = ta.getInt(3, 100);
        this.mMinValue = ta.getInt(4, 0);
        if (this.mMaxValue <= this.mMinValue) {
            this.mMaxValue = 100;
            this.mMinValue = 0;
        }
        int progress = ta.getInt(6, this.mMinValue);
        setProgress(progress);
        this.mBorderWidth = ta.getDimensionPixelSize(1, dp2px(0));
        this.mBorderColor = ta.getColor(0, DEFAULT_BORDER_COLOR);
        this.mThumbColor = ta.getColor(9, -1);
        this.mThumbRadius = ta.getDimensionPixelSize(11, dp2px(15));
        this.mThumbShadowRadius = ta.getDimensionPixelSize(13, dp2px(0));
        this.mThumbShadowColor = ta.getColor(12, -16777216);
        this.mThumbWidth = ta.getDimensionPixelSize(14, dp2px(2));
        this.mThumbMode = ta.getInt(10, 0);
        this.mShadowRadius = ta.getDimensionPixelSize(8, dp2px(0));
        ta.recycle();
    }

    private void initData() {
        this.mSeekPath = new Path();
        this.mBorderPath = new Path();
        this.mSeekPathMeasure = new PathMeasure();
        this.mTempPos = new float[2];
        this.mTempTan = new float[2];
        this.mDetector = new GestureDetector(getContext(), new OnClickListener());
        this.mInvertMatrix = new Matrix();
        this.mArcRegion = new Region();
    }

    private void initPaint() {
        initArcPaint();
        initThumbPaint();
        initBorderPaint();
        initShadowPaint();
    }

    private void initArcPaint() {
        this.mArcPaint = new Paint();
        this.mArcPaint.setAntiAlias(true);
        this.mArcPaint.setStrokeWidth(this.mArcWidth);
        this.mArcPaint.setStyle(Paint.Style.STROKE);
        this.mArcPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void initThumbPaint() {
        this.mThumbPaint = new Paint();
        this.mThumbPaint.setAntiAlias(true);
        this.mThumbPaint.setColor(this.mThumbColor);
        this.mThumbPaint.setStrokeWidth(this.mThumbWidth);
        this.mThumbPaint.setStrokeCap(Paint.Cap.ROUND);
        int i = this.mThumbMode;
        if (i == 1) {
            this.mThumbPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        } else if (i == 2) {
            this.mThumbPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        } else {
            this.mThumbPaint.setStyle(Paint.Style.STROKE);
        }
        this.mThumbPaint.setTextSize(56.0f);
    }

    private void initBorderPaint() {
        this.mBorderPaint = new Paint();
        this.mBorderPaint.setAntiAlias(true);
        this.mBorderPaint.setColor(this.mBorderColor);
        this.mBorderPaint.setStrokeWidth(this.mBorderWidth);
        this.mBorderPaint.setStyle(Paint.Style.STROKE);
    }

    private void initShadowPaint() {
        this.mShadowPaint = new Paint();
        this.mShadowPaint.setAntiAlias(true);
        this.mShadowPaint.setStrokeWidth(this.mBorderWidth);
        this.mShadowPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    private int[] getArcColors(Context context, TypedArray ta) {
        int resId = ta.getResourceId(2, 0);
        if (resId == 0) {
            resId = R.array.arc_colors_default;
        }
        int[] ret = getColorsByArrayResId(context, resId);
        return ret;
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putFloat(KEY_PROGRESS_PRESENT, this.mProgressPresent);
        return bundle;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.mProgressPresent = bundle.getFloat(KEY_PROGRESS_PRESENT);
            state = bundle.getParcelable("superState");
        }
        OnProgressChangeListener onProgressChangeListener = this.mOnProgressChangeListener;
        if (onProgressChangeListener != null) {
            onProgressChangeListener.onProgressChanged(this, getProgress(), false);
        }
        super.onRestoreInstanceState(state);
    }

    private int[] getColorsByArrayResId(Context context, int resId) {
        TypedArray colorArray = context.getResources().obtainTypedArray(resId);
        int[] ret = new int[colorArray.length()];
        for (int i = 0; i < colorArray.length(); i++) {
            ret[i] = colorArray.getColor(i, 0);
        }
        return ret;
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int ws = View.MeasureSpec.getSize(widthMeasureSpec);
        int wm = View.MeasureSpec.getMode(widthMeasureSpec);
        int hs = View.MeasureSpec.getSize(heightMeasureSpec);
        int hm = View.MeasureSpec.getMode(heightMeasureSpec);
        if (wm == 0) {
            dp2px(260);
        } else if (wm == Integer.MIN_VALUE) {
            Math.min(dp2px(260), ws);
        }
        if (hm == 0) {
            dp2px(260);
        } else if (hm == Integer.MIN_VALUE) {
            Math.min(dp2px(260), hs);
        }
        setMeasuredDimension(dp2px(MediaPlayer2.MEDIA_INFO_VIDEO_TRACK_LAGGING), dp2px(MediaPlayer2.MEDIA_INFO_VIDEO_TRACK_LAGGING));
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        float edgeLength;
        float startX;
        float startY;
        super.onSizeChanged(w, h, oldw, oldh);
        int safeW = (w - getPaddingLeft()) - getPaddingRight();
        int safeH = (h - getPaddingTop()) - getPaddingBottom();
        float fix = (this.mArcWidth / 2.0f) + this.mBorderWidth + (this.mShadowRadius * 2);
        if (safeW < safeH) {
            edgeLength = safeW - fix;
            startX = getPaddingLeft();
            startY = ((safeH - safeW) / 2.0f) + getPaddingTop();
        } else {
            float edgeLength2 = safeH;
            edgeLength = edgeLength2 - fix;
            startX = ((safeW - safeH) / 2.0f) + getPaddingLeft();
            startY = getPaddingTop();
        }
        RectF content = new RectF(startX + fix, startY + fix, startX + edgeLength, startY + edgeLength);
        this.mCenterX = content.centerX();
        this.mCenterY = content.centerY();
        this.mSeekPath.reset();
        Path path = this.mSeekPath;
        float f = this.mOpenAngle;
        path.addArc(content, f / 2.0f, CIRCLE_ANGLE - f);
        this.mSeekPathMeasure.setPath(this.mSeekPath, false);
        computeThumbPos(this.mProgressPresent);
        resetShaderColor();
        this.mInvertMatrix.reset();
        this.mInvertMatrix.preRotate(-this.mRotateAngle, this.mCenterX, this.mCenterY);
        this.mArcPaint.getFillPath(this.mSeekPath, this.mBorderPath);
        this.mBorderPath.close();
        this.mArcRegion.setPath(this.mBorderPath, new Region(0, 0, w, h));
    }

    private void resetShaderColor() {
        float f = this.mOpenAngle;
        float startPos = (f / 2.0f) / CIRCLE_ANGLE;
        float stopPos = (CIRCLE_ANGLE - (f / 2.0f)) / CIRCLE_ANGLE;
        int[] iArr = this.mArcColors;
        int len = iArr.length - 1;
        float distance = (stopPos - startPos) / len;
        float[] pos = new float[iArr.length];
        int i = 0;
        while (true) {
            int[] iArr2 = this.mArcColors;
            if (i < iArr2.length) {
                pos[i] = (i * distance) + startPos;
                i++;
            } else {
                SweepGradient gradient = new SweepGradient(this.mCenterX, this.mCenterY, iArr2, pos);
                this.mArcPaint.setShader(gradient);
                return;
            }
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.rotate(this.mRotateAngle, this.mCenterX, this.mCenterY);
        this.mShadowPaint.setShadowLayer(this.mShadowRadius * 2, 0.0f, 0.0f, getColor());
        canvas.drawPath(this.mBorderPath, this.mShadowPaint);
        canvas.drawPath(this.mSeekPath, this.mArcPaint);
        if (this.mBorderWidth > 0) {
            canvas.drawPath(this.mBorderPath, this.mBorderPaint);
        }
        float f = this.mThumbShadowRadius;
        if (f > 0.0f) {
            this.mThumbPaint.setShadowLayer(f, 0.0f, 0.0f, this.mThumbShadowColor);
            canvas.drawCircle(this.mThumbX, this.mThumbY, this.mThumbRadius, this.mThumbPaint);
            this.mThumbPaint.clearShadowLayer();
        }
        canvas.drawCircle(this.mThumbX, this.mThumbY, this.mThumbRadius, this.mThumbPaint);
        canvas.restore();
    }

    /* JADX WARN: Code restructure failed: missing block: B:8:0x0010, code lost:
        if (r0 != 3) goto L8;
     */
    @Override // android.view.View
    @android.annotation.SuppressLint({"ClickableViewAccessibility"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouchEvent(android.view.MotionEvent r6) {
        /*
            r5 = this;
            super.onTouchEvent(r6)
            int r0 = r6.getActionMasked()
            r1 = 1
            if (r0 == 0) goto L68
            if (r0 == r1) goto L5c
            r2 = 2
            if (r0 == r2) goto L13
            r2 = 3
            if (r0 == r2) goto L5c
            goto L75
        L13:
            boolean r2 = r5.mCanDrag
            if (r2 != 0) goto L18
            goto L75
        L18:
            float r2 = r6.getX()
            float r3 = r6.getY()
            float r2 = r5.getCurrentProgress(r2, r3)
            boolean r3 = r5.mAllowTouchSkip
            if (r3 != 0) goto L37
            float r3 = r5.mProgressPresent
            float r3 = r2 - r3
            float r3 = java.lang.Math.abs(r3)
            r4 = 1056964608(0x3f000000, float:0.5)
            int r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
            if (r3 <= 0) goto L37
            goto L75
        L37:
            r5.mProgressPresent = r2
            float r3 = r5.mProgressPresent
            r5.computeThumbPos(r3)
            com.xiaopeng.systemui.infoflow.speech.ui.widget.seekbar.ArcSeekBar$OnProgressChangeListener r3 = r5.mOnProgressChangeListener
            if (r3 == 0) goto L59
            int r3 = r5.getProgress()
            int r4 = r5.lastProgress
            if (r3 == r4) goto L59
            com.xiaopeng.systemui.infoflow.speech.ui.widget.seekbar.ArcSeekBar$OnProgressChangeListener r3 = r5.mOnProgressChangeListener
            int r4 = r5.getProgress()
            r3.onProgressChanged(r5, r4, r1)
            int r3 = r5.getProgress()
            r5.lastProgress = r3
        L59:
            r5.moved = r1
            goto L75
        L5c:
            com.xiaopeng.systemui.infoflow.speech.ui.widget.seekbar.ArcSeekBar$OnProgressChangeListener r2 = r5.mOnProgressChangeListener
            if (r2 == 0) goto L75
            boolean r3 = r5.moved
            if (r3 == 0) goto L75
            r2.onStopTrackingTouch(r5)
            goto L75
        L68:
            r2 = 0
            r5.moved = r2
            r5.judgeCanDrag(r6)
            com.xiaopeng.systemui.infoflow.speech.ui.widget.seekbar.ArcSeekBar$OnProgressChangeListener r2 = r5.mOnProgressChangeListener
            if (r2 == 0) goto L75
            r2.onStartTrackingTouch(r5)
        L75:
            android.view.GestureDetector r2 = r5.mDetector
            r2.onTouchEvent(r6)
            r5.invalidate()
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.systemui.infoflow.speech.ui.widget.seekbar.ArcSeekBar.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void judgeCanDrag(MotionEvent event) {
        float[] pos = {event.getX(), event.getY()};
        this.mInvertMatrix.mapPoints(pos);
        if (getDistance(pos[0], pos[1]) <= this.mThumbRadius * 1.5d) {
            this.mCanDrag = true;
        } else {
            this.mCanDrag = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes24.dex */
    public class OnClickListener extends GestureDetector.SimpleOnGestureListener {
        private OnClickListener() {
        }

        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
        public boolean onSingleTapUp(MotionEvent e) {
            if (ArcSeekBar.this.isInArcProgress(e.getX(), e.getY())) {
                ArcSeekBar arcSeekBar = ArcSeekBar.this;
                arcSeekBar.mProgressPresent = arcSeekBar.getCurrentProgress(e.getX(), e.getY());
                ArcSeekBar arcSeekBar2 = ArcSeekBar.this;
                arcSeekBar2.computeThumbPos(arcSeekBar2.mProgressPresent);
                if (ArcSeekBar.this.mOnProgressChangeListener != null) {
                    OnProgressChangeListener onProgressChangeListener = ArcSeekBar.this.mOnProgressChangeListener;
                    ArcSeekBar arcSeekBar3 = ArcSeekBar.this;
                    onProgressChangeListener.onProgressChanged(arcSeekBar3, arcSeekBar3.getProgress(), true);
                    ArcSeekBar.this.mOnProgressChangeListener.onStopTrackingTouch(ArcSeekBar.this);
                }
                return true;
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isInArcProgress(float px, float py) {
        float[] pos = {px, py};
        this.mInvertMatrix.mapPoints(pos);
        return this.mArcRegion.contains((int) pos[0], (int) pos[1]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getCurrentProgress(float px, float py) {
        float diffAngle = getDiffAngle(px, py);
        float progress = diffAngle / (CIRCLE_ANGLE - this.mOpenAngle);
        if (progress < 0.0f) {
            progress = 0.0f;
        }
        if (progress > 1.0f) {
            return 1.0f;
        }
        return progress;
    }

    private float getDiffAngle(float px, float py) {
        float angle = getAngle(px, py);
        float diffAngle = angle - this.mRotateAngle;
        if (diffAngle < 0.0f) {
            diffAngle = (diffAngle + CIRCLE_ANGLE) % CIRCLE_ANGLE;
        }
        return diffAngle - (this.mOpenAngle / 2.0f);
    }

    private float getAngle(float px, float py) {
        float angle = (float) ((Math.atan2(py - this.mCenterY, px - this.mCenterX) * 180.0d) / 3.140000104904175d);
        if (angle < 0.0f) {
            return angle + CIRCLE_ANGLE;
        }
        return angle;
    }

    private float getDistance(float px, float py) {
        float f = this.mThumbX;
        float f2 = (px - f) * (px - f);
        float f3 = this.mThumbY;
        return (float) Math.sqrt(f2 + ((py - f3) * (py - f3)));
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(1, dp, getContext().getResources().getDisplayMetrics());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void computeThumbPos(float present) {
        if (present < 0.0f) {
            present = 0.0f;
        }
        if (present > 1.0f) {
            present = 1.0f;
        }
        PathMeasure pathMeasure = this.mSeekPathMeasure;
        if (pathMeasure == null) {
            return;
        }
        float distance = pathMeasure.getLength() * present;
        this.mSeekPathMeasure.getPosTan(distance, this.mTempPos, this.mTempTan);
        float[] fArr = this.mTempPos;
        this.mThumbX = fArr[0];
        this.mThumbY = fArr[1];
    }

    public int getColor() {
        return getColor(this.mProgressPresent);
    }

    private int getColor(float radio) {
        int[] iArr = this.mArcColors;
        float diatance = 1.0f / (iArr.length - 1);
        if (radio >= 1.0f) {
            return iArr[iArr.length - 1];
        }
        int i = 0;
        while (true) {
            int[] iArr2 = this.mArcColors;
            if (i < iArr2.length) {
                if (radio > i * diatance) {
                    i++;
                } else if (i == 0) {
                    return iArr2[0];
                } else {
                    int startColor = iArr2[i - 1];
                    int endColor = iArr2[i];
                    float areaRadio = getAreaRadio(radio, (i - 1) * diatance, i * diatance);
                    return getColorFrom(startColor, endColor, areaRadio);
                }
            } else {
                return -1;
            }
        }
    }

    private float getAreaRadio(float radio, float startPosition, float endPosition) {
        return (radio - startPosition) / (endPosition - startPosition);
    }

    private int getColorFrom(int startColor, int endColor, float radio) {
        int redStart = Color.red(startColor);
        int blueStart = Color.blue(startColor);
        int greenStart = Color.green(startColor);
        int redEnd = Color.red(endColor);
        int blueEnd = Color.blue(endColor);
        int greenEnd = Color.green(endColor);
        int red = (int) (redStart + ((redEnd - redStart) * radio) + 0.5d);
        int greed = (int) (greenStart + ((greenEnd - greenStart) * radio) + 0.5d);
        int blue = (int) (blueStart + ((blueEnd - blueStart) * radio) + 0.5d);
        return Color.argb(255, red, greed, blue);
    }

    public void setProgress(int progress) {
        PrintStream printStream = System.out;
        printStream.println("setProgress = " + progress);
        if (progress > this.mMaxValue) {
            progress = this.mMaxValue;
        }
        if (progress < this.mMinValue) {
            progress = this.mMinValue;
        }
        int i = this.mMinValue;
        this.mProgressPresent = ((progress - i) * 1.0f) / (this.mMaxValue - i);
        PrintStream printStream2 = System.out;
        printStream2.println("setProgress present = " + this.mProgressPresent);
        OnProgressChangeListener onProgressChangeListener = this.mOnProgressChangeListener;
        if (onProgressChangeListener != null) {
            onProgressChangeListener.onProgressChanged(this, progress, false);
        }
        computeThumbPos(this.mProgressPresent);
        postInvalidate();
    }

    public int getProgress() {
        float f = this.mProgressPresent;
        int i = this.mMaxValue;
        int i2 = this.mMinValue;
        return ((int) (f * (i - i2))) + i2;
    }

    public void setArcColors(int[] colors) {
        this.mArcColors = colors;
        resetShaderColor();
        postInvalidate();
    }

    public void setMaxValue(int max) {
        this.mMaxValue = max;
    }

    public void setMinValue(int min) {
        this.mMinValue = min;
    }

    public void setArcColors(int colorArrayRes) {
        setArcColors(getColorsByArrayResId(getContext(), colorArrayRes));
    }

    public void setOnProgressChangeListener(OnProgressChangeListener onProgressChangeListener) {
        this.mOnProgressChangeListener = onProgressChangeListener;
    }
}
