package com.xiaopeng.xui.widget.slider;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import com.xiaopeng.vui.commons.IVuiElementListener;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.theme.XThemeManager;
import com.xiaopeng.xui.widget.XViewGroup;
import java.text.DecimalFormat;
/* loaded from: classes25.dex */
public abstract class AbsSlider extends XViewGroup implements IVuiElementListener {
    protected static final int BG_ITEM_MARGIN = 18;
    protected static final int BG_ITEM_SIZE = 30;
    protected static final int BG_ITEM_SIZE_MIN = 3;
    protected static final int CHILDREN_LAYOUT_HEIGHT = 40;
    protected static final int CHILDREN_LAYOUT_WIDTH = 20;
    private static final int INDICATOR_BALL_RADIUS = 4;
    protected static final int INDICATOR_HOLD_HORIZONTAL = 0;
    protected static final int INDICATOR_HOLD_VERTICAL = 40;
    protected static int INDICATOR_MARGIN = 16;
    private static final int INDICATOR_OUTER = 7;
    private static final int THUMB_PADDING = 7;
    protected float accuracy;
    private LinearGradient barGradient;
    @ColorInt
    int bgBallColor;
    @ColorInt
    int bgDayColor;
    private final Paint bgGradientPaint;
    private final float bgHeight;
    protected float bgItemGap;
    @ColorInt
    int bgLineColorSelect;
    private Paint bgLinePaint;
    @ColorInt
    int bgNightColor;
    private Paint bgPaint;
    protected float bgVertical;
    private Paint bollPaint;
    protected float currentUpdateIndex;
    @ColorInt
    private int customBackground;
    protected int decimal;
    protected DecimalFormat decimalFormat;
    private float desireHeight;
    private final float desireWidth;
    protected int disableAlpha;
    protected boolean dismissPop;
    private final int enableAlpha;
    protected int endColor;
    protected int endIndex;
    private final Paint gradientPaint;
    protected boolean hidePop;
    IndicatorDrawable indicatorDrawable;
    private int indicatorHoldVertical;
    protected float indicatorValue;
    protected float indicatorX;
    protected int initIndex;
    protected boolean isNight;
    private int itemCount;
    protected int leftColor;
    @ColorInt
    int lineColorSelect;
    protected Paint lineSelectPaint;
    protected boolean mIsDragging;
    protected float mScaledTouchSlop;
    protected int mStep;
    private float mThumbSize;
    private float mThumbSizeHalf;
    protected float mTouchDownX;
    protected String prefixUnit;
    protected int rightColor;
    private final float roundRadius;
    protected int startIndex;
    protected BitmapDrawable thumb;
    protected int topColor;
    protected String unit;
    protected int upperLimit;
    protected int workableTotalWidth;

    public AbsSlider(Context context) {
        this(context, null);
    }

    public AbsSlider(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbsSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.gradientPaint = new Paint(1);
        this.bgGradientPaint = new Paint(1);
        this.enableAlpha = 92;
        this.desireWidth = 644.0f;
        this.bgHeight = 40.0f;
        this.roundRadius = 8.0f;
        this.disableAlpha = 40;
        this.initIndex = -1;
        this.upperLimit = Integer.MIN_VALUE;
        this.bgVertical = 16.0f;
        this.accuracy = 1.0f;
        this.dismissPop = false;
        this.endColor = 1555808977;
        this.topColor = 1555808977;
        this.rightColor = -12871169;
        this.leftColor = -12860929;
        this.mStep = 1;
        this.hidePop = false;
        this.bgLineColorSelect = -15945223;
        this.bgNightColor = 1543503872;
        this.bgDayColor = 1560281087;
        this.bgBallColor = -14176402;
        this.lineColorSelect = -1;
        this.customBackground = 0;
        this.desireHeight = 100.0f;
        this.itemCount = 30;
        this.indicatorHoldVertical = 40;
        initView(context, attrs);
        initPaint();
        if (!isInEditMode()) {
            this.isNight = XThemeManager.isNight(getContext());
            initColor();
        }
        float f = this.bgVertical;
        INDICATOR_MARGIN = (int) (0.0f + f);
        this.mThumbSize = (f * 2.0f) - 14.0f;
        this.mThumbSizeHalf = this.mThumbSize / 2.0f;
    }

    private void initColor() {
        this.bgDayColor = getContext().getColor(R.color.x_theme_primary_neutral_normal);
        this.bgBallColor = getContext().getColor(R.color.x_theme_primary_normal);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XViewGroup, android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!isInEditMode()) {
            this.isNight = XThemeManager.isNight(getContext());
            if (XThemeManager.isThemeChanged(newConfig)) {
                IndicatorDrawable indicatorDrawable = this.indicatorDrawable;
                if (indicatorDrawable != null) {
                    indicatorDrawable.refreshTheme(getContext());
                }
                initColor();
                invalidate();
            }
        }
    }

    private void initPaint() {
        if (!this.hidePop) {
            this.indicatorDrawable = new IndicatorDrawable(getContext());
        }
        this.bgPaint = new Paint(1);
        this.bgPaint.setStyle(Paint.Style.FILL);
        this.bgPaint.setColor(this.bgNightColor);
        this.bgLinePaint = new Paint(1);
        this.bgLinePaint.setStyle(Paint.Style.FILL);
        this.bgLinePaint.setStrokeCap(Paint.Cap.ROUND);
        this.bgLinePaint.setColor(this.bgLineColorSelect);
        this.bgLinePaint.setStrokeWidth(16.0f);
        this.bollPaint = new Paint(1);
        this.bollPaint.setStyle(Paint.Style.FILL);
        this.bollPaint.setColor(-1);
        this.lineSelectPaint = new Paint(1);
        this.lineSelectPaint.setStyle(Paint.Style.FILL);
        this.lineSelectPaint.setStrokeCap(Paint.Cap.ROUND);
        this.lineSelectPaint.setStrokeWidth(12.0f);
        this.lineSelectPaint.setColor(this.lineColorSelect);
        setEnabled(true);
        this.thumb = (BitmapDrawable) ContextCompat.getDrawable(getContext(), R.drawable.x_slider_slideblock_night);
    }

    private void initView(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.XSlider);
            this.unit = attributes.getString(R.styleable.XSlider_slider_unit);
            this.startIndex = attributes.getInteger(R.styleable.XSlider_slider_start_index, 0);
            this.mStep = attributes.getInteger(R.styleable.XSlider_slider_step, 1);
            this.initIndex = attributes.getInteger(R.styleable.XSlider_slider_init_index, -1);
            this.endIndex = attributes.getInteger(R.styleable.XSlider_slider_end_index, 100);
            this.upperLimit = attributes.getInteger(R.styleable.XSlider_slider_upper_limit, Integer.MIN_VALUE);
            this.decimal = attributes.getInteger(R.styleable.XSlider_slider_index_decimal, 0);
            this.prefixUnit = attributes.getString(R.styleable.XSlider_slider_unit_prefix);
            this.bgNightColor = attributes.getColor(R.styleable.XSlider_slider_bg_color, this.bgNightColor);
            this.bgLineColorSelect = attributes.getColor(R.styleable.XSlider_slider_bg_line_color, this.bgLineColorSelect);
            this.customBackground = attributes.getColor(R.styleable.XSlider_slider_background, 0);
            this.accuracy = attributes.getFloat(R.styleable.XSlider_slider_accuracy, 0.0f);
            this.hidePop = attributes.getBoolean(R.styleable.XSlider_slider_hide_pop, false);
            this.dismissPop = attributes.getBoolean(R.styleable.XSlider_slider_dismiss_pop, false);
            this.itemCount = attributes.getInteger(R.styleable.XSlider_slider_item_count, 30);
            if (this.initIndex == -1) {
                int i = this.startIndex;
                int i2 = this.endIndex;
                if (i > i2) {
                    i = i2;
                }
                this.initIndex = i;
            }
            int i3 = this.initIndex;
            int i4 = this.startIndex;
            this.indicatorValue = i3 - i4;
            if (this.endIndex == i4) {
                throw new RuntimeException("startIndex = endIndex!!! please check the xml");
            }
            int i5 = this.decimal;
            this.decimalFormat = i5 == 0 ? null : i5 == 1 ? new DecimalFormat("0.0") : new DecimalFormat("0.00");
            if (this.accuracy == 0.0f) {
                int i6 = this.decimal;
                this.accuracy = i6 == 0 ? 1.0f : i6 == 1 ? 0.1f : 0.01f;
            }
            if (this.itemCount < 3) {
                this.itemCount = 3;
            }
            if (this.dismissPop) {
                this.hidePop = true;
                this.desireHeight = 32.0f;
                this.indicatorHoldVertical = 0;
            }
            attributes.recycle();
        }
        setMinimumWidth(80);
        setBackground(new ColorDrawable(this.customBackground));
        this.mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        int[] state = getDrawableState();
        boolean changed = false;
        IndicatorDrawable indicatorDrawable = this.indicatorDrawable;
        if (indicatorDrawable != null && indicatorDrawable.isStateful()) {
            changed = false | this.indicatorDrawable.setState(state);
        }
        if (changed) {
            this.bgDayColor = getContext().getColor(R.color.x_primary_neutral_color_selector);
            this.bgBallColor = getContext().getColor(R.color.x_primary_color_selector);
            invalidate();
        }
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (View.MeasureSpec.getMode(widthMeasureSpec) == Integer.MIN_VALUE) {
            width = 644;
        } else {
            width = getMeasuredWidth();
        }
        setMeasuredDimension(width, (int) this.desireHeight);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.bgPaint.setColor(this.bgDayColor);
        canvas.drawRoundRect(0.0f, (getHeightExIndicator() / 2.0f) - this.bgVertical, getWidthExIndicator(), (getHeightExIndicator() / 2.0f) + this.bgVertical, 8.0f, 8.0f, this.bgPaint);
        this.bgPaint.setColor(this.bgBallColor);
        canvas.drawRoundRect(0.0f, (getHeightExIndicator() / 2.0f) - this.bgVertical, filterValidValue() + INDICATOR_MARGIN, (getHeightExIndicator() / 2.0f) + this.bgVertical, 8.0f, 8.0f, this.bgPaint);
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setPadding(0, 0, 0, 0);
        this.workableTotalWidth = w - (INDICATOR_MARGIN * 2);
        this.bgItemGap = this.workableTotalWidth / (this.itemCount - 1);
        int i = this.initIndex;
        int i2 = this.startIndex;
        this.indicatorX = (Math.abs((i - i2) / (this.endIndex - i2)) * this.workableTotalWidth) + INDICATOR_MARGIN;
        for (int i3 = 0; i3 < this.itemCount; i3++) {
            SlideLineView slideLineView = new SlideLineView(getContext(), this.indicatorX > (this.bgItemGap * ((float) i3)) + ((float) INDICATOR_MARGIN));
            addView(slideLineView);
        }
        this.barGradient = new LinearGradient(INDICATOR_MARGIN, 0.0f, this.workableTotalWidth, 0.0f, new int[]{this.leftColor, this.rightColor}, (float[]) null, Shader.TileMode.CLAMP);
        if (!this.hidePop) {
            this.indicatorDrawable.updateCenter(filterValidValue(), getPopString(), this.isNight, getWidth());
        }
        invalidate();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        float itemGap = (getWidth() - 36) / (this.itemCount - 1);
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            int left = (int) (((i * itemGap) + 18.0f) - 10.0f);
            int top = (((int) getHeightExIndicator()) / 2) - 20;
            int right = (int) ((i * itemGap) + 18.0f + 10.0f);
            int bottom = (((int) getHeightExIndicator()) / 2) + 20;
            childAt.layout(left, top, right, bottom);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void invalidateAll() {
        invalidate();
        if (!this.hidePop) {
            this.indicatorDrawable.updateCenter(filterValidValue(), getPopString(), this.isNight, getWidth());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isInScrollContainer() {
        for (ViewParent p = getParent(); p instanceof ViewGroup; p = p.getParent()) {
            if (((ViewGroup) p).shouldDelayChildPressedState()) {
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void stickIndicator() {
        int i;
        if (this.mStep == 1) {
            return;
        }
        float natureGap = this.workableTotalWidth / (this.endIndex - this.startIndex);
        float f = this.indicatorX;
        int number = (int) (((f - i) / natureGap) + 0.5d);
        this.indicatorX = (number * natureGap) + INDICATOR_MARGIN;
    }

    public float getIndicatorValue() {
        return (this.indicatorValue + this.startIndex) * this.mStep;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        float barCenterX = filterValidValue();
        if (barCenterX == 0.0f) {
            return;
        }
        float barCenterY = getHeightExIndicator() / 2.0f;
        if (!this.hidePop) {
            this.indicatorDrawable.draw(canvas, this.isNight, isEnabled());
        }
        if (!isEnabled()) {
            return;
        }
        float f = this.mThumbSizeHalf;
        canvas.drawRoundRect(barCenterX - f, barCenterY - f, barCenterX + f, barCenterY + f, 4.0f, 4.0f, this.bollPaint);
    }

    public float getHeightExIndicator() {
        return getHeight() + this.indicatorHoldVertical;
    }

    private float getWidthExIndicator() {
        return getWidth() + 0;
    }

    public float getIndicatorLocationX() {
        return this.indicatorX;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float filterValidValue() {
        float f = this.indicatorX;
        int i = INDICATOR_MARGIN;
        if (f < i) {
            return i;
        }
        float maxValue = (getWidth() - INDICATOR_MARGIN) - upperLimitDistance();
        float f2 = this.indicatorX;
        if (f2 > maxValue) {
            return maxValue;
        }
        return f2;
    }

    private float upperLimitDistance() {
        int i;
        int i2;
        int i3 = this.upperLimit;
        if (i3 != Integer.MIN_VALUE && (i = this.startIndex) < (i2 = this.endIndex) && i <= i3 && i3 <= i2) {
            return ((i2 - i3) * this.workableTotalWidth) / (i2 - i);
        }
        return 0.0f;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getPopString() {
        if (this.unit == null) {
            this.unit = "";
        }
        if (this.prefixUnit == null) {
            this.prefixUnit = "";
        }
        if (this.decimalFormat == null) {
            if (this.mStep == 1) {
                return this.prefixUnit + (this.startIndex + ((int) this.indicatorValue)) + this.unit;
            }
            return this.prefixUnit + ((this.startIndex + ((int) (this.indicatorValue + 0.5d))) * this.mStep) + this.unit;
        }
        return this.prefixUnit + this.decimalFormat.format((this.startIndex + this.indicatorValue) * this.mStep) + this.unit;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setAlphaByEnable(boolean enable) {
        this.bgNightColor = resetAlpha(this.bgNightColor, enable ? 92 : this.disableAlpha);
        this.bgDayColor = resetAlpha(this.bgDayColor, enable ? 92 : this.disableAlpha);
        this.bgBallColor = resetAlpha(this.bgBallColor, enable ? 255 : this.disableAlpha);
    }

    private int resetAlpha(@ColorInt int color, int alpha) {
        return (color & 16777215) | (alpha << 24);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XViewGroup, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XViewGroup, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()) {
            this.isNight = XThemeManager.isNight(getContext());
        }
    }
}
