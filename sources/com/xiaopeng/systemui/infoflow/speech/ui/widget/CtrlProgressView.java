package com.xiaopeng.systemui.infoflow.speech.ui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.annotation.Nullable;
import com.android.systemui.R;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.systemui.infoflow.speech.carcontrol.CtrlCardUtils;
import com.xiaopeng.systemui.infoflow.speech.carcontrol.ProgressProperty;
import com.xiaopeng.systemui.infoflow.speech.utils.ColorUtils;
import com.xiaopeng.systemui.infoflow.util.GsonUtil;
/* loaded from: classes24.dex */
public class CtrlProgressView extends View {
    private static final int CARD_WIDTH = 500;
    private static final int INDICATOR_RADIUS = 26;
    private static final int INDICATOR_STROKE_WIDTH = 6;
    private static final int MARGIN_LEFT = 25;
    private static final int MARGIN_RIGHT = 25;
    private static final int PROGRESS_BAR_WIDTH = 415;
    private static final int SEAT_PROGRESS_BAR_CAP_RADIUS = 4;
    private static final int SEAT_PROGRESS_BAR_HEIGHT = 8;
    private static final int SEAT_PROGRESS_BAR_OFFSET = 2;
    private static final int SEAT_PROGRESS_BAR_SPACE = 33;
    private static final int SEAT_PROGRESS_BAR_SPACE2 = 13;
    private static final int SEAT_PROGRESS_BAR_STROKE_WIDTH = 4;
    private static final String TAG = "CtrlProgressView";
    private static final int defaultSizeLong = 288;
    private static final int defaultSizeShort = 48;
    private Path allDottedBgPath;
    private Path allDottedPath;
    private Bitmap bitmapForColor;
    private Bitmap bitmapForIndicator;
    private OnColorPickerChangeListener colorPickerChangeListener;
    private int curX;
    private int curY;
    private Paint dashPaint;
    private Path dottedCasePath;
    private Path firstDottedCasePath;
    private boolean isAttach;
    private boolean isDashMode;
    private boolean isInit;
    private boolean isSeatMode;
    private Path itemBgpath;
    int itemPathWith;
    int itemSpace;
    private Path itempath;
    private LinearGradient linearGradient;
    private int mBottom;
    private int mCtrlType;
    private int mCurrentColor;
    private int mDashNum;
    private int mInColor;
    private int mIndicatorColor;
    private boolean mIndicatorEnable;
    private boolean mIndicatorVisible;
    private boolean mIsChangeInSide;
    private int mLeft;
    private int mOutRingColor;
    private int mProgress;
    private ProgressProperty mProperty;
    private int mRight;
    private int mSeatIndicatorOffInColor;
    private int mSeatIndicatorOffOutColor;
    private int mSeatIndicatorOnInColor;
    private int mSeatOnColor;
    private int[] mSeatProgressbarLineLefts;
    private boolean mSeatProgressbarLineRectInited;
    private RectF[] mSeatProgressbarLineRects;
    private int mSeatProgressbarOffColor;
    private int mSeatWidth;
    private int mSeatWidth2;
    private int mSlitNums;
    private int mTop;
    private boolean needReDrawColorTable;
    private boolean needReDrawIndicator;
    private Orientation orientation;
    private final Paint paint;
    private Paint paintBg;
    private final Paint paintForIndicator;
    private final Rect rect;
    private final Rect rectForIndicator;

    /* loaded from: classes24.dex */
    public interface OnColorPickerChangeListener {
        int onColorChanged(CtrlProgressView ctrlProgressView, int i);

        void onProgress(int i);

        void onStartTrackingTouch(CtrlProgressView ctrlProgressView);

        void onStopTrackingTouch(CtrlProgressView ctrlProgressView);
    }

    /* loaded from: classes24.dex */
    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }

    public CtrlProgressView(Context context) {
        super(context);
        this.rect = new Rect();
        this.rectForIndicator = new Rect();
        this.itemPathWith = 36;
        this.itemSpace = 8;
        this.mIndicatorVisible = true;
        this.needReDrawColorTable = true;
        this.needReDrawIndicator = true;
        this.mSeatProgressbarLineRectInited = false;
        this.isInit = true;
        this.mSlitNums = 100;
        this.mSeatWidth = 80;
        this.mSeatWidth2 = 100;
        setLayerType(1, null);
        this.paint = new Paint();
        this.paintBg = new Paint();
        this.paintBg.setAntiAlias(true);
        this.paint.setAntiAlias(true);
        this.paintForIndicator = new Paint();
        this.paintForIndicator.setAntiAlias(true);
        this.curY = Integer.MAX_VALUE;
        this.curX = Integer.MAX_VALUE;
        this.isAttach = false;
    }

    public CtrlProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CtrlProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.rect = new Rect();
        this.rectForIndicator = new Rect();
        this.itemPathWith = 36;
        this.itemSpace = 8;
        this.mIndicatorVisible = true;
        this.needReDrawColorTable = true;
        this.needReDrawIndicator = true;
        this.mSeatProgressbarLineRectInited = false;
        this.isInit = true;
        this.mSlitNums = 100;
        this.mSeatWidth = 80;
        this.mSeatWidth2 = 100;
        setLayerType(1, null);
        this.paint = new Paint();
        this.paintBg = new Paint();
        this.paintBg.setAntiAlias(true);
        this.paint.setAntiAlias(true);
        this.paintForIndicator = new Paint();
        this.paintForIndicator.setAntiAlias(true);
        this.curY = Integer.MAX_VALUE;
        this.curX = Integer.MAX_VALUE;
        this.isAttach = false;
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CtrlProgressView, defStyleAttr, 0);
        this.mIndicatorColor = array.getColor(0, -1);
        int or = array.getInteger(3, 0);
        this.orientation = or == 0 ? Orientation.HORIZONTAL : Orientation.VERTICAL;
        this.mIndicatorEnable = array.getBoolean(1, true);
        this.mIsChangeInSide = array.getBoolean(2, true);
        array.recycle();
    }

    public int getProgress() {
        return this.mProgress;
    }

    public void setProgress(int value) {
        this.mProgress = value;
        if (this.isDashMode) {
            setDashPostion(value / this.mProperty.getPer());
        }
        this.isAttach = true;
        invalidate();
        initIndicatorOffset(value);
        Log.d(TAG, "setProgress() called with: mProgress = [" + value + "]curx=" + this.curX);
    }

    public void setIndicatorVisible(boolean indicatorVisible) {
        this.mIndicatorVisible = indicatorVisible;
    }

    public void setDashMode(boolean dashMode) {
        this.isDashMode = dashMode;
    }

    public void setSeatMode(boolean seatMode) {
        this.isSeatMode = seatMode;
        if (this.isSeatMode) {
            initSeatProgressbarLineRects();
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) getLayoutParams();
            lp.leftMargin = 0;
            lp.rightMargin = 0;
            setLayoutParams(lp);
        }
    }

    private void initSeatProgressbarLineRects() {
        int i;
        if (this.mSeatProgressbarLineRectInited) {
            return;
        }
        this.mSeatProgressbarLineRects = new RectF[4];
        this.mSeatProgressbarLineLefts = new int[4];
        this.mSeatProgressbarLineRectInited = true;
        float right = this.mSeatWidth - 2;
        this.mSeatProgressbarLineRects[0] = new RectF(2.0f, 2.0f, right, 6.0f);
        this.mSeatProgressbarLineLefts[0] = (this.mSeatWidth / 2) + 25;
        float left = 2.0f + i + 33;
        float right2 = (this.mSeatWidth2 + left) - 4.0f;
        this.mSeatProgressbarLineRects[1] = new RectF(left, 2.0f, right2, 6.0f);
        int[] iArr = this.mSeatProgressbarLineLefts;
        int i2 = this.mSeatWidth2;
        iArr[1] = (int) (left + 25.0f + (i2 / 2));
        float left2 = left + i2 + 13;
        float right3 = (i2 + left2) - 4.0f;
        this.mSeatProgressbarLineRects[2] = new RectF(left2, 2.0f, right3, 6.0f);
        int[] iArr2 = this.mSeatProgressbarLineLefts;
        int i3 = this.mSeatWidth2;
        iArr2[2] = (int) (left2 + 25.0f + (i3 / 2));
        float left3 = left2 + i3 + 13;
        float right4 = (i3 + left3) - 4.0f;
        this.mSeatProgressbarLineRects[3] = new RectF(left3, 2.0f, right4, 6.0f);
        this.mSeatProgressbarLineLefts[3] = (int) (25.0f + left3 + (this.mSeatWidth2 / 2));
    }

    public void setChangeInSide(boolean changeInSide) {
        this.mIsChangeInSide = changeInSide;
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        if (widthMode == 1073741824) {
            width = widthSize;
        } else {
            int width2 = getSuggestedMinimumWidth();
            width = width2 + getPaddingLeft() + getPaddingRight();
        }
        if (heightMode == 1073741824) {
            height = heightSize;
        } else {
            int height2 = getSuggestedMinimumHeight();
            height = height2 + getPaddingTop() + getPaddingBottom();
        }
        Orientation orientation = this.orientation;
        Orientation orientation2 = Orientation.HORIZONTAL;
        int i = defaultSizeLong;
        int width3 = Math.max(width, orientation == orientation2 ? defaultSizeLong : 48);
        if (this.orientation == Orientation.HORIZONTAL) {
            i = 48;
        }
        setMeasuredDimension(width3, Math.max(height, i));
    }

    @Override // android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.mTop = getPaddingTop();
        this.mLeft = getPaddingLeft();
        this.mBottom = getMeasuredHeight() - getPaddingBottom();
        this.mRight = getMeasuredWidth() - getPaddingRight();
        int i = this.curX;
        int i2 = this.curY;
        if (i == i2 || i2 == Integer.MAX_VALUE) {
            this.curX = 25;
            this.curY = getHeight() / 2;
        }
        calculBounds();
        if (!this.isSeatMode) {
            setProgressBarColor();
            createProgressBitmap();
        }
        if (this.isInit) {
            initIndicatorOffset(this.mProgress);
            this.isInit = false;
            Log.d(TAG, "onLayout init curX=" + this.curX);
        }
        if (showIndicator()) {
            this.needReDrawIndicator = true;
        }
    }

    private boolean showIndicator() {
        return this.mIndicatorVisible && this.mIndicatorEnable;
    }

    private void initIndicatorOffset(int progress) {
        if (this.mCtrlType != 11) {
            if (progress == 0) {
                this.curX = 25;
            } else {
                recalculateCurX(progress);
            }
        } else if (progress == 10) {
            this.curX = 25;
        } else {
            recalculateCurX(progress);
        }
    }

    private void recalculateCurX(int progress) {
        this.curX = ((this.rect.width() * progress) / this.mSlitNums) + 25;
        if (isWindCard()) {
            this.curX -= 26;
        } else if (this.mCtrlType == 11) {
            this.curX = (((progress - 10) * this.rect.width()) / 90) + 25 + 11;
        }
    }

    private void setProgressBarColor() {
        int[] progbarColors = ColorUtils.getInstance().createLightBrightnessColor();
        int i = this.mCtrlType;
        if (i == 2) {
            progbarColors = ColorUtils.getInstance().createColdWindColor();
        } else if (i == 3) {
            progbarColors = ColorUtils.getInstance().createHotWindColor();
        } else if (i == 4) {
            progbarColors = ColorUtils.getInstance().createBlowWindColor();
        } else if (i == 16) {
            progbarColors = ColorUtils.getInstance().createBrightnessColor();
        } else if (i != 17) {
            switch (i) {
                case 8:
                    progbarColors = ColorUtils.getInstance().createColdWindColor();
                    break;
                case 9:
                    progbarColors = ColorUtils.getInstance().createHotWindColor();
                    break;
                case 10:
                    progbarColors = ColorUtils.getInstance().createBlowWindColor();
                    break;
                case 11:
                    progbarColors = ColorUtils.getInstance().createLightBrightnessColor();
                    break;
                case 12:
                    progbarColors = ColorUtils.getInstance().createAmbientLightingColor();
                    break;
            }
        } else {
            progbarColors = ColorUtils.getInstance().createBrightnessColor();
        }
        setColors(progbarColors);
    }

    private void createProgressBitmap() {
        int hc = this.rect.height();
        int wc = this.rect.width();
        Bitmap bitmap = this.bitmapForColor;
        if (bitmap != null && !bitmap.isRecycled()) {
            this.bitmapForColor.recycle();
            this.bitmapForColor = null;
        }
        Bitmap bitmap2 = this.bitmapForIndicator;
        if (bitmap2 != null && !bitmap2.isRecycled()) {
            this.bitmapForIndicator.recycle();
            this.bitmapForIndicator = null;
        }
        this.bitmapForColor = Bitmap.createBitmap(wc, hc, Bitmap.Config.ARGB_8888);
        this.bitmapForIndicator = Bitmap.createBitmap(52, 52, Bitmap.Config.ARGB_8888);
    }

    private void calculBounds() {
        int t;
        int b;
        int l;
        int r;
        int h = this.mBottom - this.mTop;
        int w = this.mRight - this.mLeft;
        int size = Math.min(w, h);
        if (this.orientation == Orientation.HORIZONTAL) {
            if (w <= h) {
                size = w / 6;
            }
        } else if (w >= h) {
            size = h / 6;
        }
        int each = size / 16;
        if (this.orientation == Orientation.HORIZONTAL) {
            l = this.mLeft + 25;
            r = (this.mRight - 25) - 10;
            t = (getHeight() / 2) - each;
            b = (getHeight() / 2) + each;
        } else {
            int l2 = this.mTop;
            t = l2 + 26;
            b = this.mBottom - 26;
            l = (getWidth() / 2) - each;
            r = (getWidth() / 2) + each;
        }
        this.rect.set(l, t, r, b);
    }

    public void setColors(int... colors) {
        this.linearGradient = null;
        float[] positions = getColorPositions(colors.length);
        if (this.orientation == Orientation.HORIZONTAL) {
            this.linearGradient = new LinearGradient(this.rect.left, this.rect.top, this.rect.right, this.rect.top, colors, positions, Shader.TileMode.CLAMP);
        } else {
            this.linearGradient = new LinearGradient(this.rect.left, this.rect.top, this.rect.left, this.rect.bottom, colors, positions, Shader.TileMode.CLAMP);
        }
        this.needReDrawColorTable = true;
        invalidate();
    }

    private float[] getColorPositions(int size) {
        if (size == 2) {
            return new float[]{0.0f, 1.0f};
        }
        return new float[]{0.0f, 0.05f, 0.1f, 0.16f, 0.21f, 0.26f, 0.31f, 0.37f, 0.42f, 0.47f, 0.53f, 0.58f, 0.63f, 0.68f, 0.74f, 0.79f, 0.84f, 0.89f, 0.95f, 1.0f};
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.needReDrawColorTable) {
            if (this.isSeatMode) {
                drawSeatModeProgress(canvas);
            } else {
                createColorTableBitmap();
            }
        }
        if (!this.isSeatMode) {
            canvas.drawBitmap(this.bitmapForColor, (Rect) null, this.rect, this.paint);
        }
        if (showIndicator()) {
            if (this.isSeatMode && this.isAttach) {
                int i = this.mProgress;
                if (i >= 0 && i < 25) {
                    this.curX = this.mSeatProgressbarLineLefts[0];
                } else {
                    int i2 = this.mProgress;
                    if (i2 >= 25 && i2 < 50) {
                        this.curX = this.mSeatProgressbarLineLefts[1];
                    } else {
                        int i3 = this.mProgress;
                        if (i3 >= 50 && i3 < 75) {
                            this.curX = this.mSeatProgressbarLineLefts[2];
                        } else {
                            this.curX = getSeatModeRight();
                        }
                    }
                }
            } else if (isWindCard()) {
                int i4 = this.mProgress;
                if (i4 % 5 != 0) {
                    this.mProgress = ((i4 / 5) + 1) * 5;
                }
                recalculateCurX(this.mProgress);
            }
            int i5 = this.curX;
            int left = i5 - 26;
            int i6 = this.curY;
            int top = i6 - 26;
            int right = i5 + 26;
            int bottom = i6 + 26;
            if (left < 25) {
                left = 25;
                right = 25 + 52;
            }
            if (right > 475) {
                right = 475;
                left = 475 - 52;
            }
            if (this.isSeatMode && right > this.mRight) {
                left = getSeatModeRight();
                right = left + 52;
            }
            if (this.needReDrawIndicator) {
                if (this.isSeatMode) {
                    drawSeatModeIndicator(canvas, left, top);
                } else {
                    createIndicatorBitmap(this.mOutRingColor, this.mInColor);
                }
            }
            if (!this.isSeatMode) {
                this.rectForIndicator.set(left, top, right, bottom);
                calcuColor();
                canvas.drawBitmap(this.bitmapForIndicator, (Rect) null, this.rectForIndicator, this.paint);
            }
            this.isAttach = false;
        }
    }

    private boolean isWindCard() {
        int i = this.mCtrlType;
        return i == 4 || i == 2 || i == 3;
    }

    private int getSeatModeRight() {
        return this.mSeatProgressbarLineLefts[3];
    }

    public void createIndicatorBitmap(int outRingColor, int inColor) {
        this.paintForIndicator.setStyle(Paint.Style.STROKE);
        this.paintForIndicator.setAntiAlias(true);
        this.paintForIndicator.setColor(outRingColor);
        this.paintForIndicator.setStrokeWidth(6.0f);
        Canvas c = new Canvas(this.bitmapForIndicator);
        c.drawCircle(26.0f, 26.0f, 23.0f, this.paintForIndicator);
        this.paintForIndicator.reset();
        this.paintForIndicator.setAntiAlias(true);
        this.paintForIndicator.setColor(inColor);
        c.drawCircle(26.0f, 26.0f, 20.0f, this.paintForIndicator);
        this.needReDrawIndicator = false;
    }

    public void drawSeatModeIndicator(Canvas c, int left, int top) {
        c.save();
        c.translate(left, top);
        this.paintForIndicator.setStyle(Paint.Style.STROKE);
        this.paintForIndicator.setStrokeWidth(6.0f);
        this.paintForIndicator.setAntiAlias(true);
        int progress = (int) CtrlCardUtils.getInstance().progress2UnitValue(14, this.mProgress);
        if (progress != 0) {
            this.paintForIndicator.setColor(this.mSeatOnColor);
            c.drawCircle(26.0f, 26.0f, 23.0f, this.paintForIndicator);
            this.paintForIndicator.reset();
            this.paintForIndicator.setAntiAlias(true);
            this.paintForIndicator.setColor(this.mSeatIndicatorOnInColor);
            c.drawCircle(26.0f, 26.0f, 20.0f, this.paintForIndicator);
            this.paintForIndicator.setColor(this.mSeatOnColor);
            this.paintForIndicator.setTextSize(26.0f);
            String familyName = getResources().getString(R.string.font_xpeng_number_bold);
            Typeface typeface = Typeface.create(familyName, 1);
            this.paintForIndicator.setTypeface(typeface);
            this.paintForIndicator.setTextAlign(Paint.Align.CENTER);
            Paint.FontMetrics fontMetrics = this.paintForIndicator.getFontMetrics();
            float distance = (fontMetrics.bottom - fontMetrics.top) / 2.0f;
            float baseline = (distance + 26.0f) - 2.0f;
            c.drawText(progress + "", 26.0f, baseline, this.paintForIndicator);
        } else {
            this.paintForIndicator.setColor(this.mSeatIndicatorOffOutColor);
            c.drawCircle(26.0f, 26.0f, 23.0f, this.paintForIndicator);
            this.paintForIndicator.reset();
            this.paintForIndicator.setAntiAlias(true);
            this.paintForIndicator.setColor(this.mSeatIndicatorOffInColor);
            c.drawCircle(26.0f, 26.0f, 20.0f, this.paintForIndicator);
        }
        c.restore();
    }

    public void setCtrlType(int type) {
        this.mCtrlType = type;
        this.mProperty = CtrlCardUtils.getInstance().getProgressProperty(type);
        ProgressProperty progressProperty = this.mProperty;
        if (progressProperty != null) {
            this.mSlitNums = progressProperty.getPer() * this.mProperty.getSlips();
        }
        Log.d(TAG, "setCtrlType() called with: mCtrlType = [" + type + "]mProperty=" + GsonUtil.toJson(this.mProperty) + "mSlitNums=" + this.mSlitNums);
    }

    public void setSeatProgressbarColor(int progressOffColor, int onColor, int indicatorOffOutColor, int indicatorOffInColor, int indicatorOnInColor) {
        this.mSeatProgressbarOffColor = progressOffColor;
        this.mSeatOnColor = onColor;
        this.mSeatIndicatorOffOutColor = indicatorOffOutColor;
        this.mSeatIndicatorOffInColor = indicatorOffInColor;
        this.mSeatIndicatorOnInColor = indicatorOnInColor;
    }

    public void setThumbColor(int outRingColor, int inColor) {
        this.mOutRingColor = outRingColor;
        this.mInColor = inColor;
    }

    private void createColorTableBitmap() {
        int r;
        Canvas c = new Canvas(this.bitmapForColor);
        RectF rf = new RectF(0.0f, 0.0f, this.bitmapForColor.getWidth(), this.bitmapForColor.getHeight());
        if (this.orientation == Orientation.HORIZONTAL) {
            r = this.bitmapForColor.getHeight() / 2;
        } else {
            r = this.bitmapForColor.getWidth() / 2;
        }
        if (this.isDashMode) {
            dashedProgress(c, rf, r);
        } else if (this.isSeatMode) {
            drawSeatModeProgress(c);
        } else {
            this.paint.setColor(-16777216);
            c.drawRoundRect(rf, r, r, this.paint);
            this.paint.setShader(this.linearGradient);
            c.drawRoundRect(rf, r, r, this.paint);
            this.paint.setShader(null);
            this.needReDrawColorTable = false;
        }
    }

    private void drawSeatModeProgress(Canvas canvas) {
        canvas.save();
        canvas.translate(this.rect.left, this.rect.top);
        this.paint.setColor(this.mSeatProgressbarOffColor);
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeWidth(4.0f);
        canvas.drawRoundRect(this.mSeatProgressbarLineRects[0], 4.0f, 4.0f, this.paint);
        int i = this.mProgress;
        if (i >= 25 && i < 50) {
            this.paint.setColor(this.mSeatOnColor);
        } else {
            this.paint.setColor(this.mSeatProgressbarOffColor);
        }
        canvas.drawRoundRect(this.mSeatProgressbarLineRects[1], 4.0f, 4.0f, this.paint);
        int i2 = this.mProgress;
        if (i2 >= 50 && i2 < 75) {
            this.paint.setColor(this.mSeatOnColor);
        } else {
            this.paint.setColor(this.mSeatProgressbarOffColor);
        }
        canvas.drawRoundRect(this.mSeatProgressbarLineRects[2], 4.0f, 4.0f, this.paint);
        int i3 = this.mProgress;
        if (i3 >= 75 && i3 <= 100) {
            this.paint.setColor(this.mSeatOnColor);
        } else {
            this.paint.setColor(this.mSeatProgressbarOffColor);
        }
        canvas.drawRoundRect(this.mSeatProgressbarLineRects[3], 4.0f, 4.0f, this.paint);
        canvas.restore();
    }

    public void dashedProgress(Canvas canvas, RectF rf, int radius) {
        this.needReDrawColorTable = true;
        createDashedProgBg(canvas, rf, radius);
        createDashedProg(canvas, rf, radius);
    }

    private void createDashedProg(Canvas canvas, RectF rectF, int radius) {
        this.paint.reset();
        Shader shader = this.linearGradient;
        this.paint.setShader(shader);
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Paint.Style.STROKE);
        this.itempath = new Path();
        Path path = this.allDottedPath;
        if (path == null) {
            this.allDottedPath = new Path();
        } else {
            path.reset();
        }
        this.allDottedPath.moveTo(0.0f, 0.0f);
        this.allDottedPath.lineTo((this.itemPathWith + this.itemSpace) * this.mDashNum, 0.0f);
        this.itempath.addRoundRect(new RectF(0.0f, 0.0f, this.itemPathWith, rectF.bottom), radius, radius, Path.Direction.CCW);
        this.paint.setPathEffect(new PathDashPathEffect(this.itempath, this.itemPathWith + this.itemSpace, 0.0f, PathDashPathEffect.Style.TRANSLATE));
        canvas.drawPath(this.allDottedPath, this.paint);
        canvas.save();
    }

    private void createDashedProgBg(Canvas canvas, RectF rectF, int radius) {
        this.paintBg.reset();
        this.paintBg.setColor(-7829368);
        this.paintBg.setAntiAlias(true);
        this.paintBg.setStyle(Paint.Style.STROKE);
        this.itemBgpath = new Path();
        this.allDottedBgPath = new Path();
        this.allDottedBgPath.lineTo((this.itemPathWith + this.itemSpace) * this.mProperty.getSlips(), 0.0f);
        this.itemBgpath.addRoundRect(new RectF(0.0f, 0.0f, this.itemPathWith, rectF.bottom), radius, radius, Path.Direction.CCW);
        this.paintBg.setPathEffect(new PathDashPathEffect(this.itemBgpath, this.itemPathWith + this.itemSpace, 0.0f, PathDashPathEffect.Style.TRANSLATE));
        canvas.drawPath(this.allDottedBgPath, this.paintBg);
        canvas.save();
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        int x;
        int ex = (int) event.getX();
        int ey = (int) event.getY();
        if (ex <= 0) {
            x = 0;
        } else {
            int i = this.mRight;
            if (ex >= (i - 26) + 2) {
                x = (i - 26) + 2;
            } else {
                x = ex;
            }
        }
        int progressbarWidth = this.rect.width();
        if (progressbarWidth == 0) {
            progressbarWidth = 415;
        }
        if (this.mCtrlType == 11) {
            this.mProgress = ((((x - 25) - 11) * 90) / progressbarWidth) + 10;
        } else {
            this.mProgress = ((x - 25) * this.mSlitNums) / progressbarWidth;
        }
        int intValue = this.mProgress / this.mProperty.getPer();
        float flValue = this.mProgress / this.mProperty.getPer();
        int data = 0;
        if (flValue > intValue) {
            data = intValue + 1;
        } else if (flValue == intValue) {
            data = intValue;
        }
        if (this.mDashNum != data) {
            this.mDashNum = data;
            invalidate();
        }
        if (isWindCard() && this.mProgress <= 0) {
            this.mProgress = 1;
        }
        OnColorPickerChangeListener onColorPickerChangeListener = this.colorPickerChangeListener;
        if (onColorPickerChangeListener != null) {
            onColorPickerChangeListener.onProgress(this.mProgress);
        }
        if (inBoundOfColorTable(ex, ey)) {
            if (this.orientation == Orientation.HORIZONTAL) {
                this.curX = ex;
                this.curY = getHeight() / 2;
            } else {
                this.curX = getWidth() / 2;
                this.curY = ey;
            }
            if (event.getActionMasked() == 0) {
                OnColorPickerChangeListener onColorPickerChangeListener2 = this.colorPickerChangeListener;
                if (onColorPickerChangeListener2 != null) {
                    onColorPickerChangeListener2.onStartTrackingTouch(this);
                    this.colorPickerChangeListener.onColorChanged(this, this.mCurrentColor);
                }
            } else if (event.getActionMasked() == 1) {
                this.isAttach = true;
                OnColorPickerChangeListener onColorPickerChangeListener3 = this.colorPickerChangeListener;
                if (onColorPickerChangeListener3 != null) {
                    onColorPickerChangeListener3.onStopTrackingTouch(this);
                    this.colorPickerChangeListener.onColorChanged(this, this.mCurrentColor);
                }
                if (this.isSeatMode) {
                    this.mProgress = CtrlCardUtils.transferSeatModeProgress(this.mProgress);
                    initIndicatorOffset(this.mProgress);
                }
            } else {
                OnColorPickerChangeListener onColorPickerChangeListener4 = this.colorPickerChangeListener;
                if (onColorPickerChangeListener4 != null) {
                    onColorPickerChangeListener4.onColorChanged(this, this.mCurrentColor);
                }
            }
            invalidate();
            return true;
        }
        return true;
    }

    public int getColor() {
        return calcuColor();
    }

    private boolean inBoundOfColorTable(int ex, int ey) {
        if (this.orientation != Orientation.HORIZONTAL) {
            return ey > this.mTop + 26 && ey < this.mBottom + (-26);
        } else if (this.isSeatMode) {
            return true;
        } else {
            return ex > this.mLeft + 26 && ex < this.mRight + (-26);
        }
    }

    private int calcuColor() {
        int x;
        int y;
        int bmpWidth = this.bitmapForColor.getWidth();
        int bmpHeight = this.bitmapForColor.getHeight();
        if (this.orientation == Orientation.HORIZONTAL) {
            y = (this.rect.bottom - this.rect.top) / 2;
            x = this.curX < this.rect.left ? 1 : this.curX > this.rect.right ? bmpWidth - 1 : this.curX - this.rect.left;
        } else {
            x = (this.rect.right - this.rect.left) / 2;
            if (this.curY < this.rect.top) {
                y = 1;
            } else {
                int y2 = this.curY;
                if (y2 > this.rect.bottom) {
                    y = bmpHeight - 1;
                } else {
                    int y3 = this.curY;
                    y = y3 - this.rect.top;
                }
            }
        }
        if (x >= bmpWidth) {
            x = bmpWidth - 1;
        }
        if (y >= bmpHeight) {
            y = bmpHeight - 1;
        }
        int pixel = this.bitmapForColor.getPixel(x, y);
        this.mCurrentColor = pixelToColor(pixel);
        refreshIcon();
        return this.mCurrentColor;
    }

    public void refreshIcon() {
        if (this.isDashMode || this.isSeatMode) {
            return;
        }
        OnColorPickerChangeListener onColorPickerChangeListener = this.colorPickerChangeListener;
        if (onColorPickerChangeListener != null) {
            this.mCurrentColor = onColorPickerChangeListener.onColorChanged(this, this.mCurrentColor);
        }
        if (this.mIsChangeInSide) {
            createIndicatorBitmap(this.mOutRingColor, this.mCurrentColor);
        } else {
            createIndicatorBitmap(this.mCurrentColor, this.mInColor);
        }
    }

    private int pixelToColor(int pixel) {
        int alpha = Color.alpha(pixel);
        int red = Color.red(pixel);
        int green = Color.green(pixel);
        int blue = Color.blue(pixel);
        return Color.argb(alpha, red, green, blue);
    }

    public void setOnColorPickerChangeListener(OnColorPickerChangeListener l) {
        this.colorPickerChangeListener = l;
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        SavedState ss = new SavedState(parcelable);
        Bitmap bitmap = this.bitmapForColor;
        if (bitmap != null) {
            ss.selX = this.curX;
            ss.selY = this.curY;
            ss.color = bitmap;
            if (showIndicator()) {
                ss.indicator = this.bitmapForIndicator;
            }
        }
        return ss;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.curX = ss.selX;
        this.curY = ss.selY;
        this.bitmapForColor = ss.color;
        if (showIndicator()) {
            this.bitmapForIndicator = ss.indicator;
            this.needReDrawIndicator = true;
        }
        this.needReDrawColorTable = true;
    }

    public void setPosition(int x, int y) {
        if (inBoundOfColorTable(x, y)) {
            this.curX = x;
            this.curY = y;
            if (showIndicator()) {
                this.needReDrawIndicator = true;
            }
            invalidate();
        }
    }

    public void setDashPostion(int dashNum) {
        this.mDashNum = dashNum;
    }

    public int getIndicatorColor() {
        return this.mIndicatorColor;
    }

    public void setIndicatorColor(int color) {
        this.mIndicatorColor = color;
        this.needReDrawIndicator = true;
        invalidate();
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
        this.needReDrawIndicator = true;
        this.needReDrawColorTable = true;
        requestLayout();
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (ThemeManager.isThemeChanged(newConfig)) {
            invalidate();
        }
    }

    /* loaded from: classes24.dex */
    private class SavedState extends View.BaseSavedState {
        Bitmap color;
        int[] colors;
        Bitmap indicator;
        int selX;
        int selY;

        SavedState(Parcelable source) {
            super(source);
            this.indicator = null;
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.selX);
            out.writeInt(this.selY);
            out.writeParcelable(this.color, flags);
            out.writeIntArray(this.colors);
            Bitmap bitmap = this.indicator;
            if (bitmap != null) {
                out.writeParcelable(bitmap, flags);
            }
        }
    }
}
