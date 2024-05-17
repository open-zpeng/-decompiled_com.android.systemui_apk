package com.android.keyguard;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.text.method.SingleLineTransformationMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.android.keyguard.CarrierTextController;
import com.android.systemui.R;
import java.util.Locale;
/* loaded from: classes19.dex */
public class CarrierText extends TextView {
    private static final boolean DEBUG = false;
    private static final String TAG = "CarrierText";
    private static CharSequence mSeparator;
    private CarrierTextController.CarrierTextCallback mCarrierTextCallback;
    private CarrierTextController mCarrierTextController;
    private boolean mShouldMarquee;
    private boolean mShowAirplaneMode;
    private boolean mShowMissingSim;

    public CarrierText(Context context) {
        this(context, null);
    }

    public CarrierText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mCarrierTextCallback = new CarrierTextController.CarrierTextCallback() { // from class: com.android.keyguard.CarrierText.1
            @Override // com.android.keyguard.CarrierTextController.CarrierTextCallback
            public void updateCarrierInfo(CarrierTextController.CarrierTextCallbackInfo info) {
                CarrierText.this.setText(info.carrierText);
            }

            @Override // com.android.keyguard.CarrierTextController.CarrierTextCallback
            public void startedGoingToSleep() {
                CarrierText.this.setSelected(false);
            }

            @Override // com.android.keyguard.CarrierTextController.CarrierTextCallback
            public void finishedWakingUp() {
                CarrierText.this.setSelected(true);
            }
        };
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CarrierText, 0, 0);
        try {
            boolean useAllCaps = a.getBoolean(R.styleable.CarrierText_allCaps, false);
            this.mShowAirplaneMode = a.getBoolean(R.styleable.CarrierText_showAirplaneMode, false);
            this.mShowMissingSim = a.getBoolean(R.styleable.CarrierText_showMissingSim, false);
            a.recycle();
            setTransformationMethod(new CarrierTextTransformationMethod(this.mContext, useAllCaps));
        } catch (Throwable th) {
            a.recycle();
            throw th;
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        mSeparator = getResources().getString(17040227);
        this.mCarrierTextController = new CarrierTextController(this.mContext, mSeparator, this.mShowAirplaneMode, this.mShowMissingSim);
        this.mShouldMarquee = KeyguardUpdateMonitor.getInstance(this.mContext).isDeviceInteractive();
        setSelected(this.mShouldMarquee);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mCarrierTextController.setListening(this.mCarrierTextCallback);
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mCarrierTextController.setListening(null);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == 0) {
            setEllipsize(TextUtils.TruncateAt.MARQUEE);
        } else {
            setEllipsize(TextUtils.TruncateAt.END);
        }
    }

    /* loaded from: classes19.dex */
    private class CarrierTextTransformationMethod extends SingleLineTransformationMethod {
        private final boolean mAllCaps;
        private final Locale mLocale;

        public CarrierTextTransformationMethod(Context context, boolean allCaps) {
            this.mLocale = context.getResources().getConfiguration().locale;
            this.mAllCaps = allCaps;
        }

        @Override // android.text.method.ReplacementTransformationMethod, android.text.method.TransformationMethod
        public CharSequence getTransformation(CharSequence source, View view) {
            CharSequence source2 = super.getTransformation(source, view);
            if (this.mAllCaps && source2 != null) {
                return source2.toString().toUpperCase(this.mLocale);
            }
            return source2;
        }
    }
}
