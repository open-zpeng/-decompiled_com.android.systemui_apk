package com.xiaopeng.systemui.ui.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import com.android.systemui.R;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.controller.CarController;
/* loaded from: classes24.dex */
public class TemperatureTextView extends AlphaOptimizedLinearLayout {
    private static final String TAG = "TemperatureTextView";
    private AnimatedImageView mImageView;
    private boolean mShowImage;
    private float mText1Size;
    private AnimatedTextView mText1View;
    private float mText2Size;
    private AnimatedTextView mText2View;
    private int mTextColor;
    private ColorStateList mTextColorStateList;
    private AnimatedTextView mUnitView;

    public TemperatureTextView(Context context) {
        super(context);
        this.mText1Size = 20.0f;
        this.mText2Size = 20.0f;
        this.mShowImage = false;
        this.mTextColor = R.color.color_temperature_text_selector;
        init(context, null);
    }

    public TemperatureTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mText1Size = 20.0f;
        this.mText2Size = 20.0f;
        this.mShowImage = false;
        this.mTextColor = R.color.color_temperature_text_selector;
        init(context, attrs);
    }

    public TemperatureTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mText1Size = 20.0f;
        this.mText2Size = 20.0f;
        this.mShowImage = false;
        this.mTextColor = R.color.color_temperature_text_selector;
        init(context, attrs);
    }

    public TemperatureTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mText1Size = 20.0f;
        this.mText2Size = 20.0f;
        this.mShowImage = false;
        this.mTextColor = R.color.color_temperature_text_selector;
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.view_temperature_text, this);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.TemperatureTextView, 0, 0);
        this.mShowImage = attributes.getBoolean(0, false);
        this.mText1Size = attributes.getDimensionPixelSize(1, 20);
        this.mText2Size = attributes.getDimensionPixelSize(2, 20);
        this.mTextColor = attributes.getResourceId(3, R.color.color_temperature_text_selector);
        this.mTextColorStateList = this.mContext.getColorStateList(this.mTextColor);
        attributes.recycle();
    }

    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
        this.mTextColorStateList = this.mContext.getColorStateList(textColor);
        updateTextColor();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mUnitView = (AnimatedTextView) findViewById(R.id.unit_view);
        this.mText1View = (AnimatedTextView) findViewById(R.id.text1_view);
        this.mText2View = (AnimatedTextView) findViewById(R.id.text2_view);
        this.mImageView = (AnimatedImageView) findViewById(R.id.image_view);
        this.mUnitView.setText(CarController.getTemperatureUnit(getContext()));
        initAttributeSet();
    }

    private void initAttributeSet() {
        this.mUnitView.setTextSize(this.mText2Size);
        this.mText1View.setTextSize(this.mText1Size);
        this.mText2View.setTextSize(this.mText1Size);
        updateTextColor();
    }

    public void setText(float text) {
        String[] arrayText;
        boolean isMin = text == 18.0f;
        boolean isMax = text == 32.0f;
        if ((isMin || isMax) && this.mShowImage) {
            int res = isMin ? R.drawable.ic_navbar_hvac_temperature_lo : R.drawable.ic_navbar_hvac_temperature_hi;
            this.mImageView.setImageResource(res);
            this.mImageView.setVisibility(this.mShowImage ? 0 : 8);
            this.mText1View.setVisibility(this.mShowImage ? 8 : 0);
            this.mText2View.setVisibility(this.mShowImage ? 8 : 0);
            this.mText1View.setText(isMin ? "L" : "H");
            this.mText2View.setText(isMin ? "o" : "i");
        } else {
            this.mImageView.setVisibility(8);
            String stringText = Float.toString(text);
            Logger.d(TAG, "setText stringText=" + stringText);
            if (!TextUtils.isEmpty(stringText) && stringText.contains(".") && (arrayText = stringText.split("\\.")) != null && arrayText.length == 2) {
                Logger.d(TAG, "setText text0=" + arrayText[0]);
                this.mText1View.setText(arrayText[0]);
                AnimatedTextView animatedTextView = this.mText2View;
                animatedTextView.setText("." + arrayText[1]);
            }
        }
        Logger.d(TAG, "setText : showText = " + ((Object) this.mText1View.getText()) + ((Object) this.mText2View.getText()));
    }

    private float spToPx(float sp) {
        return TypedValue.applyDimension(2, sp, getResources().getDisplayMetrics());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.systemui.ui.widget.AlphaOptimizedLinearLayout, android.view.View
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (ThemeManager.isThemeChanged(newConfig)) {
            this.mTextColorStateList = this.mContext.getColorStateList(this.mTextColor);
            updateTextColor();
        }
    }

    private void updateTextColor() {
        this.mText1View.setTextColor(this.mTextColorStateList);
        this.mText2View.setTextColor(this.mTextColorStateList);
        this.mUnitView.setTextColor(this.mTextColorStateList);
    }
}
