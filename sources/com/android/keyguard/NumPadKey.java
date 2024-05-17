package com.android.keyguard;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.R;
/* loaded from: classes19.dex */
public class NumPadKey extends ViewGroup {
    static String[] sKlondike;
    private int mDigit;
    private TextView mDigitText;
    private boolean mEnableHaptics;
    private TextView mKlondikeText;
    private View.OnClickListener mListener;
    private PowerManager mPM;
    private PasswordTextView mTextView;
    private int mTextViewResId;

    public void userActivity() {
        this.mPM.userActivity(SystemClock.uptimeMillis(), false);
    }

    public NumPadKey(Context context) {
        this(context, null);
    }

    public NumPadKey(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumPadKey(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, R.layout.keyguard_num_pad_key);
    }

    protected NumPadKey(Context context, AttributeSet attrs, int defStyle, int contentResource) {
        super(context, attrs, defStyle);
        this.mDigit = -1;
        this.mListener = new View.OnClickListener() { // from class: com.android.keyguard.NumPadKey.1
            @Override // android.view.View.OnClickListener
            public void onClick(View thisView) {
                View v;
                if (NumPadKey.this.mTextView == null && NumPadKey.this.mTextViewResId > 0 && (v = NumPadKey.this.getRootView().findViewById(NumPadKey.this.mTextViewResId)) != null && (v instanceof PasswordTextView)) {
                    NumPadKey.this.mTextView = (PasswordTextView) v;
                }
                if (NumPadKey.this.mTextView != null && NumPadKey.this.mTextView.isEnabled()) {
                    NumPadKey.this.mTextView.append(Character.forDigit(NumPadKey.this.mDigit, 10));
                }
                NumPadKey.this.userActivity();
            }
        };
        setFocusable(true);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumPadKey);
        try {
            this.mDigit = a.getInt(R.styleable.NumPadKey_digit, this.mDigit);
            this.mTextViewResId = a.getResourceId(R.styleable.NumPadKey_textView, 0);
            a.recycle();
            setOnClickListener(this.mListener);
            setOnHoverListener(new LiftToActivateListener(context));
            this.mEnableHaptics = new LockPatternUtils(context).isTactileFeedbackEnabled();
            this.mPM = (PowerManager) this.mContext.getSystemService("power");
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService("layout_inflater");
            inflater.inflate(contentResource, (ViewGroup) this, true);
            this.mDigitText = (TextView) findViewById(R.id.digit_text);
            this.mDigitText.setText(Integer.toString(this.mDigit));
            this.mKlondikeText = (TextView) findViewById(R.id.klondike_text);
            if (this.mDigit >= 0) {
                if (sKlondike == null) {
                    sKlondike = getResources().getStringArray(R.array.lockscreen_num_pad_klondike);
                }
                String[] strArr = sKlondike;
                if (strArr != null) {
                    int length = strArr.length;
                    int i = this.mDigit;
                    if (length > i) {
                        String klondike = strArr[i];
                        int len = klondike.length();
                        if (len > 0) {
                            this.mKlondikeText.setText(klondike);
                        } else {
                            this.mKlondikeText.setVisibility(4);
                        }
                    }
                }
            }
            TypedArray a2 = context.obtainStyledAttributes(attrs, android.R.styleable.View);
            if (!a2.hasValueOrEmpty(13)) {
                setBackground(this.mContext.getDrawable(R.drawable.ripple_drawable_pin));
            }
            a2.recycle();
            setContentDescription(this.mDigitText.getText().toString());
        } catch (Throwable th) {
            a.recycle();
            throw th;
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == 0) {
            doHapticKeyClick();
        }
        return super.onTouchEvent(event);
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int digitHeight = this.mDigitText.getMeasuredHeight();
        int klondikeHeight = this.mKlondikeText.getMeasuredHeight();
        int totalHeight = digitHeight + klondikeHeight;
        int top = (getHeight() / 2) - (totalHeight / 2);
        int centerX = getWidth() / 2;
        int left = centerX - (this.mDigitText.getMeasuredWidth() / 2);
        int bottom = top + digitHeight;
        TextView textView = this.mDigitText;
        textView.layout(left, top, textView.getMeasuredWidth() + left, bottom);
        int top2 = (int) (bottom - (klondikeHeight * 0.35f));
        int left2 = centerX - (this.mKlondikeText.getMeasuredWidth() / 2);
        TextView textView2 = this.mKlondikeText;
        textView2.layout(left2, top2, textView2.getMeasuredWidth() + left2, top2 + klondikeHeight);
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    public void doHapticKeyClick() {
        if (this.mEnableHaptics) {
            performHapticFeedback(1, 3);
        }
    }
}
