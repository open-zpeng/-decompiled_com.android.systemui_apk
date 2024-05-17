package com.android.systemui.statusbar.phone;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import com.android.systemui.Dependency;
import com.android.systemui.Interpolators;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.statusbar.policy.KeyButtonDrawable;
import java.util.ArrayList;
/* loaded from: classes21.dex */
public class ButtonDispatcher {
    private static final int FADE_DURATION_IN = 150;
    private static final int FADE_DURATION_OUT = 250;
    private View.AccessibilityDelegate mAccessibilityDelegate;
    private Float mAlpha;
    private View.OnClickListener mClickListener;
    private View mCurrentView;
    private Float mDarkIntensity;
    private Boolean mDelayTouchFeedback;
    private ValueAnimator mFadeAnimator;
    private final int mId;
    private KeyButtonDrawable mImageDrawable;
    private View.OnLongClickListener mLongClickListener;
    private Boolean mLongClickable;
    private View.OnHoverListener mOnHoverListener;
    private View.OnTouchListener mTouchListener;
    private boolean mVertical;
    private final ArrayList<View> mViews = new ArrayList<>();
    private Integer mVisibility = 0;
    private final ValueAnimator.AnimatorUpdateListener mAlphaListener = new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$ButtonDispatcher$YQ5xchhAskLzgLUT3UrgvCxrRAQ
        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
            ButtonDispatcher.this.lambda$new$0$ButtonDispatcher(valueAnimator);
        }
    };
    private final AnimatorListenerAdapter mFadeListener = new AnimatorListenerAdapter() { // from class: com.android.systemui.statusbar.phone.ButtonDispatcher.1
        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            ButtonDispatcher.this.mFadeAnimator = null;
            ButtonDispatcher buttonDispatcher = ButtonDispatcher.this;
            buttonDispatcher.setVisibility(buttonDispatcher.getAlpha() == 1.0f ? 0 : 4);
        }
    };
    private final AssistManager mAssistManager = (AssistManager) Dependency.get(AssistManager.class);

    public /* synthetic */ void lambda$new$0$ButtonDispatcher(ValueAnimator animation) {
        setAlpha(((Float) animation.getAnimatedValue()).floatValue(), false, false);
    }

    public ButtonDispatcher(int id) {
        this.mId = id;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clear() {
        this.mViews.clear();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addView(View view) {
        this.mViews.add(view);
        view.setOnClickListener(this.mClickListener);
        view.setOnTouchListener(this.mTouchListener);
        view.setOnLongClickListener(this.mLongClickListener);
        view.setOnHoverListener(this.mOnHoverListener);
        Boolean bool = this.mLongClickable;
        if (bool != null) {
            view.setLongClickable(bool.booleanValue());
        }
        Float f = this.mAlpha;
        if (f != null) {
            view.setAlpha(f.floatValue());
        }
        Integer num = this.mVisibility;
        if (num != null) {
            view.setVisibility(num.intValue());
        }
        View.AccessibilityDelegate accessibilityDelegate = this.mAccessibilityDelegate;
        if (accessibilityDelegate != null) {
            view.setAccessibilityDelegate(accessibilityDelegate);
        }
        if (view instanceof ButtonInterface) {
            ButtonInterface button = (ButtonInterface) view;
            Float f2 = this.mDarkIntensity;
            if (f2 != null) {
                button.setDarkIntensity(f2.floatValue());
            }
            KeyButtonDrawable keyButtonDrawable = this.mImageDrawable;
            if (keyButtonDrawable != null) {
                button.setImageDrawable(keyButtonDrawable);
            }
            Boolean bool2 = this.mDelayTouchFeedback;
            if (bool2 != null) {
                button.setDelayTouchFeedback(bool2.booleanValue());
            }
            button.setVertical(this.mVertical);
        }
    }

    public int getId() {
        return this.mId;
    }

    public int getVisibility() {
        Integer num = this.mVisibility;
        if (num != null) {
            return num.intValue();
        }
        return 0;
    }

    public boolean isVisible() {
        return getVisibility() == 0;
    }

    public float getAlpha() {
        Float f = this.mAlpha;
        if (f != null) {
            return f.floatValue();
        }
        return 1.0f;
    }

    public KeyButtonDrawable getImageDrawable() {
        return this.mImageDrawable;
    }

    public void setImageDrawable(KeyButtonDrawable drawable) {
        this.mImageDrawable = drawable;
        int N = this.mViews.size();
        for (int i = 0; i < N; i++) {
            if (this.mViews.get(i) instanceof ButtonInterface) {
                ((ButtonInterface) this.mViews.get(i)).setImageDrawable(this.mImageDrawable);
            }
        }
        KeyButtonDrawable keyButtonDrawable = this.mImageDrawable;
        if (keyButtonDrawable != null) {
            keyButtonDrawable.setCallback(this.mCurrentView);
        }
    }

    public void setVisibility(int visibility) {
        if (this.mVisibility.intValue() == visibility) {
            return;
        }
        ValueAnimator valueAnimator = this.mFadeAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.mVisibility = Integer.valueOf(visibility);
        int N = this.mViews.size();
        for (int i = 0; i < N; i++) {
            this.mViews.get(i).setVisibility(this.mVisibility.intValue());
        }
    }

    public void abortCurrentGesture() {
        int N = this.mViews.size();
        for (int i = 0; i < N; i++) {
            if (this.mViews.get(i) instanceof ButtonInterface) {
                ((ButtonInterface) this.mViews.get(i)).abortCurrentGesture();
            }
        }
    }

    public void setAlpha(float alpha) {
        setAlpha(alpha, false);
    }

    public void setAlpha(float alpha, boolean animate) {
        setAlpha(alpha, animate, true);
    }

    public void setAlpha(float alpha, boolean animate, long duration) {
        setAlpha(alpha, animate, duration, true);
    }

    public void setAlpha(float alpha, boolean animate, boolean cancelAnimator) {
        setAlpha(alpha, animate, getAlpha() < alpha ? 150L : 250L, cancelAnimator);
    }

    public void setAlpha(float alpha, boolean animate, long duration, boolean cancelAnimator) {
        if (this.mFadeAnimator != null && (cancelAnimator || animate)) {
            this.mFadeAnimator.cancel();
        }
        if (animate) {
            setVisibility(0);
            this.mFadeAnimator = ValueAnimator.ofFloat(getAlpha(), alpha);
            this.mFadeAnimator.setStartDelay(this.mAssistManager.getAssistHandleShowAndGoRemainingDurationMs());
            this.mFadeAnimator.setDuration(duration);
            this.mFadeAnimator.setInterpolator(Interpolators.LINEAR);
            this.mFadeAnimator.addListener(this.mFadeListener);
            this.mFadeAnimator.addUpdateListener(this.mAlphaListener);
            this.mFadeAnimator.start();
            return;
        }
        this.mAlpha = Float.valueOf(alpha);
        int N = this.mViews.size();
        for (int i = 0; i < N; i++) {
            this.mViews.get(i).setAlpha(alpha);
        }
    }

    public void setDarkIntensity(float darkIntensity) {
        this.mDarkIntensity = Float.valueOf(darkIntensity);
        int N = this.mViews.size();
        for (int i = 0; i < N; i++) {
            if (this.mViews.get(i) instanceof ButtonInterface) {
                ((ButtonInterface) this.mViews.get(i)).setDarkIntensity(darkIntensity);
            }
        }
    }

    public void setDelayTouchFeedback(boolean delay) {
        this.mDelayTouchFeedback = Boolean.valueOf(delay);
        int N = this.mViews.size();
        for (int i = 0; i < N; i++) {
            if (this.mViews.get(i) instanceof ButtonInterface) {
                ((ButtonInterface) this.mViews.get(i)).setDelayTouchFeedback(delay);
            }
        }
    }

    public void setOnClickListener(View.OnClickListener clickListener) {
        this.mClickListener = clickListener;
        int N = this.mViews.size();
        for (int i = 0; i < N; i++) {
            this.mViews.get(i).setOnClickListener(this.mClickListener);
        }
    }

    public void setOnTouchListener(View.OnTouchListener touchListener) {
        this.mTouchListener = touchListener;
        int N = this.mViews.size();
        for (int i = 0; i < N; i++) {
            this.mViews.get(i).setOnTouchListener(this.mTouchListener);
        }
    }

    public void setLongClickable(boolean isLongClickable) {
        this.mLongClickable = Boolean.valueOf(isLongClickable);
        int N = this.mViews.size();
        for (int i = 0; i < N; i++) {
            this.mViews.get(i).setLongClickable(this.mLongClickable.booleanValue());
        }
    }

    public void setOnLongClickListener(View.OnLongClickListener longClickListener) {
        this.mLongClickListener = longClickListener;
        int N = this.mViews.size();
        for (int i = 0; i < N; i++) {
            this.mViews.get(i).setOnLongClickListener(this.mLongClickListener);
        }
    }

    public void setOnHoverListener(View.OnHoverListener hoverListener) {
        this.mOnHoverListener = hoverListener;
        int N = this.mViews.size();
        for (int i = 0; i < N; i++) {
            this.mViews.get(i).setOnHoverListener(this.mOnHoverListener);
        }
    }

    public void setAccessibilityDelegate(View.AccessibilityDelegate delegate) {
        this.mAccessibilityDelegate = delegate;
        int N = this.mViews.size();
        for (int i = 0; i < N; i++) {
            this.mViews.get(i).setAccessibilityDelegate(delegate);
        }
    }

    public void setClickable(boolean clickable) {
        abortCurrentGesture();
        int N = this.mViews.size();
        for (int i = 0; i < N; i++) {
            this.mViews.get(i).setClickable(clickable);
        }
    }

    public void setTranslation(int x, int y, int z) {
        int N = this.mViews.size();
        for (int i = 0; i < N; i++) {
            View view = this.mViews.get(i);
            view.setTranslationX(x);
            view.setTranslationY(y);
            view.setTranslationZ(z);
        }
    }

    public ArrayList<View> getViews() {
        return this.mViews;
    }

    public View getCurrentView() {
        return this.mCurrentView;
    }

    public void setCurrentView(View currentView) {
        this.mCurrentView = currentView.findViewById(this.mId);
        KeyButtonDrawable keyButtonDrawable = this.mImageDrawable;
        if (keyButtonDrawable != null) {
            keyButtonDrawable.setCallback(this.mCurrentView);
        }
        View view = this.mCurrentView;
        if (view != null) {
            view.setTranslationX(0.0f);
            this.mCurrentView.setTranslationY(0.0f);
            this.mCurrentView.setTranslationZ(0.0f);
        }
    }

    public void setVertical(boolean vertical) {
        this.mVertical = vertical;
        int N = this.mViews.size();
        for (int i = 0; i < N; i++) {
            View view = this.mViews.get(i);
            if (view instanceof ButtonInterface) {
                ((ButtonInterface) view).setVertical(vertical);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDestroy() {
    }
}
