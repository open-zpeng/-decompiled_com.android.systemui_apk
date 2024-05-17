package com.android.keyguard;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.EditText;
import com.android.systemui.R;
import java.util.ArrayList;
import java.util.Stack;
import kotlin.text.Typography;
/* loaded from: classes19.dex */
public class PasswordTextView extends View {
    private static final long APPEAR_DURATION = 160;
    private static final long DISAPPEAR_DURATION = 160;
    private static char DOT = Typography.bullet;
    private static final long DOT_APPEAR_DURATION_OVERSHOOT = 320;
    private static final long DOT_APPEAR_TEXT_DISAPPEAR_OVERLAP_DURATION = 130;
    private static final float DOT_OVERSHOOT_FACTOR = 1.5f;
    private static final float OVERSHOOT_TIME_POSITION = 0.5f;
    private static final long RESET_DELAY_PER_ELEMENT = 40;
    private static final long RESET_MAX_DELAY = 200;
    private static final long TEXT_REST_DURATION_AFTER_APPEAR = 100;
    private static final long TEXT_VISIBILITY_DURATION = 1300;
    private Interpolator mAppearInterpolator;
    private int mCharPadding;
    private Stack<CharState> mCharPool;
    private Interpolator mDisappearInterpolator;
    private int mDotSize;
    private final Paint mDrawPaint;
    private Interpolator mFastOutSlowInInterpolator;
    private final int mGravity;
    private PowerManager mPM;
    private boolean mShowPassword;
    private String mText;
    private ArrayList<CharState> mTextChars;
    private final int mTextHeightRaw;
    private UserActivityListener mUserActivityListener;

    /* loaded from: classes19.dex */
    public interface UserActivityListener {
        void onUserActivity();
    }

    public PasswordTextView(Context context) {
        this(context, null);
    }

    public PasswordTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PasswordTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PasswordTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mTextChars = new ArrayList<>();
        this.mText = "";
        this.mCharPool = new Stack<>();
        this.mDrawPaint = new Paint();
        setFocusableInTouchMode(true);
        setFocusable(true);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PasswordTextView);
        try {
            this.mTextHeightRaw = a.getInt(R.styleable.PasswordTextView_scaledTextSize, 0);
            this.mGravity = a.getInt(R.styleable.PasswordTextView_android_gravity, 17);
            this.mDotSize = a.getDimensionPixelSize(R.styleable.PasswordTextView_dotSize, getContext().getResources().getDimensionPixelSize(R.dimen.password_dot_size));
            this.mCharPadding = a.getDimensionPixelSize(R.styleable.PasswordTextView_charPadding, getContext().getResources().getDimensionPixelSize(R.dimen.password_char_padding));
            int textColor = a.getColor(R.styleable.PasswordTextView_android_textColor, -1);
            this.mDrawPaint.setColor(textColor);
            a.recycle();
            this.mDrawPaint.setFlags(129);
            this.mDrawPaint.setTextAlign(Paint.Align.CENTER);
            this.mDrawPaint.setTypeface(Typeface.create(context.getString(17039742), 0));
            this.mShowPassword = Settings.System.getInt(this.mContext.getContentResolver(), "show_password", 1) == 1;
            this.mAppearInterpolator = AnimationUtils.loadInterpolator(this.mContext, 17563662);
            this.mDisappearInterpolator = AnimationUtils.loadInterpolator(this.mContext, 17563663);
            this.mFastOutSlowInInterpolator = AnimationUtils.loadInterpolator(this.mContext, 17563661);
            this.mPM = (PowerManager) this.mContext.getSystemService("power");
        } catch (Throwable th) {
            a.recycle();
            throw th;
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        float currentDrawPosition;
        float totalDrawingWidth = getDrawingWidth();
        int i = this.mGravity;
        if ((i & 7) == 3) {
            if ((i & 8388608) != 0 && getLayoutDirection() == 1) {
                currentDrawPosition = (getWidth() - getPaddingRight()) - totalDrawingWidth;
            } else {
                currentDrawPosition = getPaddingLeft();
            }
        } else {
            currentDrawPosition = (getWidth() / 2) - (totalDrawingWidth / 2.0f);
        }
        int length = this.mTextChars.size();
        Rect bounds = getCharBounds();
        int charHeight = bounds.bottom - bounds.top;
        float yPosition = (((getHeight() - getPaddingBottom()) - getPaddingTop()) / 2) + getPaddingTop();
        canvas.clipRect(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        float charLength = bounds.right - bounds.left;
        float currentDrawPosition2 = currentDrawPosition;
        for (int i2 = 0; i2 < length; i2++) {
            CharState charState = this.mTextChars.get(i2);
            float charWidth = charState.draw(canvas, currentDrawPosition2, charHeight, yPosition, charLength);
            currentDrawPosition2 += charWidth;
        }
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    private Rect getCharBounds() {
        float textHeight = this.mTextHeightRaw * getResources().getDisplayMetrics().scaledDensity;
        this.mDrawPaint.setTextSize(textHeight);
        Rect bounds = new Rect();
        this.mDrawPaint.getTextBounds("0", 0, 1, bounds);
        return bounds;
    }

    private float getDrawingWidth() {
        int width = 0;
        int length = this.mTextChars.size();
        Rect bounds = getCharBounds();
        int charLength = bounds.right - bounds.left;
        for (int i = 0; i < length; i++) {
            CharState charState = this.mTextChars.get(i);
            if (i != 0) {
                width = (int) (width + (this.mCharPadding * charState.currentWidthFactor));
            }
            width = (int) (width + (charLength * charState.currentWidthFactor));
        }
        return width;
    }

    public void append(char c) {
        CharState charState;
        int visibleChars = this.mTextChars.size();
        CharSequence textbefore = getTransformedText();
        this.mText += c;
        int newLength = this.mText.length();
        if (newLength > visibleChars) {
            charState = obtainCharState(c);
            this.mTextChars.add(charState);
        } else {
            charState = this.mTextChars.get(newLength - 1);
            charState.whichChar = c;
        }
        charState.startAppearAnimation();
        if (newLength > 1) {
            CharState previousState = this.mTextChars.get(newLength - 2);
            if (previousState.isDotSwapPending) {
                previousState.swapToDotWhenAppearFinished();
            }
        }
        userActivity();
        sendAccessibilityEventTypeViewTextChanged(textbefore, textbefore.length(), 0, 1);
    }

    public void setUserActivityListener(UserActivityListener userActivitiListener) {
        this.mUserActivityListener = userActivitiListener;
    }

    private void userActivity() {
        this.mPM.userActivity(SystemClock.uptimeMillis(), false);
        UserActivityListener userActivityListener = this.mUserActivityListener;
        if (userActivityListener != null) {
            userActivityListener.onUserActivity();
        }
    }

    public void deleteLastChar() {
        int length = this.mText.length();
        CharSequence textbefore = getTransformedText();
        if (length > 0) {
            this.mText = this.mText.substring(0, length - 1);
            CharState charState = this.mTextChars.get(length - 1);
            charState.startRemoveAnimation(0L, 0L);
            sendAccessibilityEventTypeViewTextChanged(textbefore, textbefore.length() - 1, 1, 0);
        }
        userActivity();
    }

    public String getText() {
        return this.mText;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public CharSequence getTransformedText() {
        int textLength = this.mTextChars.size();
        StringBuilder stringBuilder = new StringBuilder(textLength);
        for (int i = 0; i < textLength; i++) {
            CharState charState = this.mTextChars.get(i);
            if (charState.dotAnimator == null || charState.dotAnimationIsGrowing) {
                stringBuilder.append(charState.isCharVisibleForA11y() ? charState.whichChar : DOT);
            }
        }
        return stringBuilder;
    }

    private CharState obtainCharState(char c) {
        CharState charState;
        if (this.mCharPool.isEmpty()) {
            charState = new CharState();
        } else {
            charState = this.mCharPool.pop();
            charState.reset();
        }
        charState.whichChar = c;
        return charState;
    }

    public void reset(boolean animated, boolean announce) {
        int distToMiddle;
        CharSequence textbefore = getTransformedText();
        this.mText = "";
        int length = this.mTextChars.size();
        int middleIndex = (length - 1) / 2;
        for (int i = 0; i < length; i++) {
            CharState charState = this.mTextChars.get(i);
            if (animated) {
                if (i <= middleIndex) {
                    distToMiddle = i * 2;
                } else {
                    int delayIndex = i - middleIndex;
                    distToMiddle = (length - 1) - ((delayIndex - 1) * 2);
                }
                long startDelay = distToMiddle * RESET_DELAY_PER_ELEMENT;
                long startDelay2 = Math.min(startDelay, 200L);
                long maxDelay = (length - 1) * RESET_DELAY_PER_ELEMENT;
                charState.startRemoveAnimation(startDelay2, Math.min(maxDelay, 200L) + 160);
                charState.removeDotSwapCallbacks();
            } else {
                this.mCharPool.push(charState);
            }
        }
        if (!animated) {
            this.mTextChars.clear();
        }
        if (announce) {
            sendAccessibilityEventTypeViewTextChanged(textbefore, 0, textbefore.length(), 0);
        }
    }

    void sendAccessibilityEventTypeViewTextChanged(CharSequence beforeText, int fromIndex, int removedCount, int addedCount) {
        if (AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            if (isFocused() || (isSelected() && isShown())) {
                AccessibilityEvent event = AccessibilityEvent.obtain(16);
                event.setFromIndex(fromIndex);
                event.setRemovedCount(removedCount);
                event.setAddedCount(addedCount);
                event.setBeforeText(beforeText);
                CharSequence transformedText = getTransformedText();
                if (!TextUtils.isEmpty(transformedText)) {
                    event.getText().add(transformedText);
                }
                event.setPassword(true);
                sendAccessibilityEventUnchecked(event);
            }
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(EditText.class.getName());
        event.setPassword(true);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(EditText.class.getName());
        info.setPassword(true);
        info.setText(getTransformedText());
        info.setEditable(true);
        info.setInputType(16);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes19.dex */
    public class CharState {
        float currentDotSizeFactor;
        float currentTextSizeFactor;
        float currentTextTranslationY;
        float currentWidthFactor;
        boolean dotAnimationIsGrowing;
        Animator dotAnimator;
        Animator.AnimatorListener dotFinishListener;
        private ValueAnimator.AnimatorUpdateListener dotSizeUpdater;
        private Runnable dotSwapperRunnable;
        boolean isDotSwapPending;
        Animator.AnimatorListener removeEndListener;
        boolean textAnimationIsGrowing;
        ValueAnimator textAnimator;
        Animator.AnimatorListener textFinishListener;
        private ValueAnimator.AnimatorUpdateListener textSizeUpdater;
        ValueAnimator textTranslateAnimator;
        Animator.AnimatorListener textTranslateFinishListener;
        private ValueAnimator.AnimatorUpdateListener textTranslationUpdater;
        char whichChar;
        boolean widthAnimationIsGrowing;
        ValueAnimator widthAnimator;
        Animator.AnimatorListener widthFinishListener;
        private ValueAnimator.AnimatorUpdateListener widthUpdater;

        private CharState() {
            this.currentTextTranslationY = 1.0f;
            this.removeEndListener = new AnimatorListenerAdapter() { // from class: com.android.keyguard.PasswordTextView.CharState.1
                private boolean mCancelled;

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    this.mCancelled = true;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (!this.mCancelled) {
                        PasswordTextView.this.mTextChars.remove(CharState.this);
                        PasswordTextView.this.mCharPool.push(CharState.this);
                        CharState.this.reset();
                        CharState charState = CharState.this;
                        charState.cancelAnimator(charState.textTranslateAnimator);
                        CharState.this.textTranslateAnimator = null;
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    this.mCancelled = false;
                }
            };
            this.dotFinishListener = new AnimatorListenerAdapter() { // from class: com.android.keyguard.PasswordTextView.CharState.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    CharState.this.dotAnimator = null;
                }
            };
            this.textFinishListener = new AnimatorListenerAdapter() { // from class: com.android.keyguard.PasswordTextView.CharState.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    CharState.this.textAnimator = null;
                }
            };
            this.textTranslateFinishListener = new AnimatorListenerAdapter() { // from class: com.android.keyguard.PasswordTextView.CharState.4
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    CharState.this.textTranslateAnimator = null;
                }
            };
            this.widthFinishListener = new AnimatorListenerAdapter() { // from class: com.android.keyguard.PasswordTextView.CharState.5
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    CharState.this.widthAnimator = null;
                }
            };
            this.dotSizeUpdater = new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.keyguard.PasswordTextView.CharState.6
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator animation) {
                    CharState.this.currentDotSizeFactor = ((Float) animation.getAnimatedValue()).floatValue();
                    PasswordTextView.this.invalidate();
                }
            };
            this.textSizeUpdater = new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.keyguard.PasswordTextView.CharState.7
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator animation) {
                    boolean textVisibleBefore = CharState.this.isCharVisibleForA11y();
                    float beforeTextSizeFactor = CharState.this.currentTextSizeFactor;
                    CharState.this.currentTextSizeFactor = ((Float) animation.getAnimatedValue()).floatValue();
                    if (textVisibleBefore != CharState.this.isCharVisibleForA11y()) {
                        CharState charState = CharState.this;
                        charState.currentTextSizeFactor = beforeTextSizeFactor;
                        CharSequence beforeText = PasswordTextView.this.getTransformedText();
                        CharState.this.currentTextSizeFactor = ((Float) animation.getAnimatedValue()).floatValue();
                        int indexOfThisChar = PasswordTextView.this.mTextChars.indexOf(CharState.this);
                        if (indexOfThisChar >= 0) {
                            PasswordTextView.this.sendAccessibilityEventTypeViewTextChanged(beforeText, indexOfThisChar, 1, 1);
                        }
                    }
                    PasswordTextView.this.invalidate();
                }
            };
            this.textTranslationUpdater = new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.keyguard.PasswordTextView.CharState.8
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator animation) {
                    CharState.this.currentTextTranslationY = ((Float) animation.getAnimatedValue()).floatValue();
                    PasswordTextView.this.invalidate();
                }
            };
            this.widthUpdater = new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.keyguard.PasswordTextView.CharState.9
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator animation) {
                    CharState.this.currentWidthFactor = ((Float) animation.getAnimatedValue()).floatValue();
                    PasswordTextView.this.invalidate();
                }
            };
            this.dotSwapperRunnable = new Runnable() { // from class: com.android.keyguard.PasswordTextView.CharState.10
                @Override // java.lang.Runnable
                public void run() {
                    CharState.this.performSwap();
                    CharState.this.isDotSwapPending = false;
                }
            };
        }

        void reset() {
            this.whichChar = (char) 0;
            this.currentTextSizeFactor = 0.0f;
            this.currentDotSizeFactor = 0.0f;
            this.currentWidthFactor = 0.0f;
            cancelAnimator(this.textAnimator);
            this.textAnimator = null;
            cancelAnimator(this.dotAnimator);
            this.dotAnimator = null;
            cancelAnimator(this.widthAnimator);
            this.widthAnimator = null;
            this.currentTextTranslationY = 1.0f;
            removeDotSwapCallbacks();
        }

        void startRemoveAnimation(long startDelay, long widthDelay) {
            boolean z = true;
            boolean dotNeedsAnimation = (this.currentDotSizeFactor > 0.0f && this.dotAnimator == null) || (this.dotAnimator != null && this.dotAnimationIsGrowing);
            boolean textNeedsAnimation = (this.currentTextSizeFactor > 0.0f && this.textAnimator == null) || (this.textAnimator != null && this.textAnimationIsGrowing);
            if ((this.currentWidthFactor <= 0.0f || this.widthAnimator != null) && (this.widthAnimator == null || !this.widthAnimationIsGrowing)) {
                z = false;
            }
            boolean widthNeedsAnimation = z;
            if (dotNeedsAnimation) {
                startDotDisappearAnimation(startDelay);
            }
            if (textNeedsAnimation) {
                startTextDisappearAnimation(startDelay);
            }
            if (widthNeedsAnimation) {
                startWidthDisappearAnimation(widthDelay);
            }
        }

        void startAppearAnimation() {
            boolean widthNeedsAnimation = true;
            boolean dotNeedsAnimation = !PasswordTextView.this.mShowPassword && (this.dotAnimator == null || !this.dotAnimationIsGrowing);
            boolean textNeedsAnimation = PasswordTextView.this.mShowPassword && (this.textAnimator == null || !this.textAnimationIsGrowing);
            if (this.widthAnimator != null && this.widthAnimationIsGrowing) {
                widthNeedsAnimation = false;
            }
            if (dotNeedsAnimation) {
                startDotAppearAnimation(0L);
            }
            if (textNeedsAnimation) {
                startTextAppearAnimation();
            }
            if (widthNeedsAnimation) {
                startWidthAppearAnimation();
            }
            if (PasswordTextView.this.mShowPassword) {
                postDotSwap(PasswordTextView.TEXT_VISIBILITY_DURATION);
            }
        }

        private void postDotSwap(long delay) {
            removeDotSwapCallbacks();
            PasswordTextView.this.postDelayed(this.dotSwapperRunnable, delay);
            this.isDotSwapPending = true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void removeDotSwapCallbacks() {
            PasswordTextView.this.removeCallbacks(this.dotSwapperRunnable);
            this.isDotSwapPending = false;
        }

        void swapToDotWhenAppearFinished() {
            removeDotSwapCallbacks();
            ValueAnimator valueAnimator = this.textAnimator;
            if (valueAnimator != null) {
                long remainingDuration = valueAnimator.getDuration() - this.textAnimator.getCurrentPlayTime();
                postDotSwap(PasswordTextView.TEXT_REST_DURATION_AFTER_APPEAR + remainingDuration);
                return;
            }
            performSwap();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void performSwap() {
            startTextDisappearAnimation(0L);
            startDotAppearAnimation(30L);
        }

        private void startWidthDisappearAnimation(long widthDelay) {
            cancelAnimator(this.widthAnimator);
            this.widthAnimator = ValueAnimator.ofFloat(this.currentWidthFactor, 0.0f);
            this.widthAnimator.addUpdateListener(this.widthUpdater);
            this.widthAnimator.addListener(this.widthFinishListener);
            this.widthAnimator.addListener(this.removeEndListener);
            this.widthAnimator.setDuration(this.currentWidthFactor * 160.0f);
            this.widthAnimator.setStartDelay(widthDelay);
            this.widthAnimator.start();
            this.widthAnimationIsGrowing = false;
        }

        private void startTextDisappearAnimation(long startDelay) {
            cancelAnimator(this.textAnimator);
            this.textAnimator = ValueAnimator.ofFloat(this.currentTextSizeFactor, 0.0f);
            this.textAnimator.addUpdateListener(this.textSizeUpdater);
            this.textAnimator.addListener(this.textFinishListener);
            this.textAnimator.setInterpolator(PasswordTextView.this.mDisappearInterpolator);
            this.textAnimator.setDuration(this.currentTextSizeFactor * 160.0f);
            this.textAnimator.setStartDelay(startDelay);
            this.textAnimator.start();
            this.textAnimationIsGrowing = false;
        }

        private void startDotDisappearAnimation(long startDelay) {
            cancelAnimator(this.dotAnimator);
            ValueAnimator animator = ValueAnimator.ofFloat(this.currentDotSizeFactor, 0.0f);
            animator.addUpdateListener(this.dotSizeUpdater);
            animator.addListener(this.dotFinishListener);
            animator.setInterpolator(PasswordTextView.this.mDisappearInterpolator);
            long duration = Math.min(this.currentDotSizeFactor, 1.0f) * 160.0f;
            animator.setDuration(duration);
            animator.setStartDelay(startDelay);
            animator.start();
            this.dotAnimator = animator;
            this.dotAnimationIsGrowing = false;
        }

        private void startWidthAppearAnimation() {
            cancelAnimator(this.widthAnimator);
            this.widthAnimator = ValueAnimator.ofFloat(this.currentWidthFactor, 1.0f);
            this.widthAnimator.addUpdateListener(this.widthUpdater);
            this.widthAnimator.addListener(this.widthFinishListener);
            this.widthAnimator.setDuration((1.0f - this.currentWidthFactor) * 160.0f);
            this.widthAnimator.start();
            this.widthAnimationIsGrowing = true;
        }

        private void startTextAppearAnimation() {
            cancelAnimator(this.textAnimator);
            this.textAnimator = ValueAnimator.ofFloat(this.currentTextSizeFactor, 1.0f);
            this.textAnimator.addUpdateListener(this.textSizeUpdater);
            this.textAnimator.addListener(this.textFinishListener);
            this.textAnimator.setInterpolator(PasswordTextView.this.mAppearInterpolator);
            this.textAnimator.setDuration((1.0f - this.currentTextSizeFactor) * 160.0f);
            this.textAnimator.start();
            this.textAnimationIsGrowing = true;
            if (this.textTranslateAnimator == null) {
                this.textTranslateAnimator = ValueAnimator.ofFloat(1.0f, 0.0f);
                this.textTranslateAnimator.addUpdateListener(this.textTranslationUpdater);
                this.textTranslateAnimator.addListener(this.textTranslateFinishListener);
                this.textTranslateAnimator.setInterpolator(PasswordTextView.this.mAppearInterpolator);
                this.textTranslateAnimator.setDuration(160L);
                this.textTranslateAnimator.start();
            }
        }

        private void startDotAppearAnimation(long delay) {
            cancelAnimator(this.dotAnimator);
            if (!PasswordTextView.this.mShowPassword) {
                ValueAnimator overShootAnimator = ValueAnimator.ofFloat(this.currentDotSizeFactor, 1.5f);
                overShootAnimator.addUpdateListener(this.dotSizeUpdater);
                overShootAnimator.setInterpolator(PasswordTextView.this.mAppearInterpolator);
                overShootAnimator.setDuration(160L);
                ValueAnimator settleBackAnimator = ValueAnimator.ofFloat(1.5f, 1.0f);
                settleBackAnimator.addUpdateListener(this.dotSizeUpdater);
                settleBackAnimator.setDuration(PasswordTextView.DOT_APPEAR_DURATION_OVERSHOOT - 160);
                settleBackAnimator.addListener(this.dotFinishListener);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playSequentially(overShootAnimator, settleBackAnimator);
                animatorSet.setStartDelay(delay);
                animatorSet.start();
                this.dotAnimator = animatorSet;
            } else {
                ValueAnimator growAnimator = ValueAnimator.ofFloat(this.currentDotSizeFactor, 1.0f);
                growAnimator.addUpdateListener(this.dotSizeUpdater);
                growAnimator.setDuration((1.0f - this.currentDotSizeFactor) * 160.0f);
                growAnimator.addListener(this.dotFinishListener);
                growAnimator.setStartDelay(delay);
                growAnimator.start();
                this.dotAnimator = growAnimator;
            }
            this.dotAnimationIsGrowing = true;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void cancelAnimator(Animator animator) {
            if (animator != null) {
                animator.cancel();
            }
        }

        public float draw(Canvas canvas, float currentDrawPosition, int charHeight, float yPosition, float charLength) {
            boolean textVisible = this.currentTextSizeFactor > 0.0f;
            boolean dotVisible = this.currentDotSizeFactor > 0.0f;
            float charWidth = this.currentWidthFactor * charLength;
            if (textVisible) {
                float currYPosition = ((charHeight / 2.0f) * this.currentTextSizeFactor) + yPosition + (charHeight * this.currentTextTranslationY * 0.8f);
                canvas.save();
                float centerX = (charWidth / 2.0f) + currentDrawPosition;
                canvas.translate(centerX, currYPosition);
                float f = this.currentTextSizeFactor;
                canvas.scale(f, f);
                canvas.drawText(Character.toString(this.whichChar), 0.0f, 0.0f, PasswordTextView.this.mDrawPaint);
                canvas.restore();
            }
            if (dotVisible) {
                canvas.save();
                float centerX2 = (charWidth / 2.0f) + currentDrawPosition;
                canvas.translate(centerX2, yPosition);
                canvas.drawCircle(0.0f, 0.0f, (PasswordTextView.this.mDotSize / 2) * this.currentDotSizeFactor, PasswordTextView.this.mDrawPaint);
                canvas.restore();
            }
            return (PasswordTextView.this.mCharPadding * this.currentWidthFactor) + charWidth;
        }

        public boolean isCharVisibleForA11y() {
            boolean textIsGrowing = this.textAnimator != null && this.textAnimationIsGrowing;
            return this.currentTextSizeFactor > 0.0f || textIsGrowing;
        }
    }
}
