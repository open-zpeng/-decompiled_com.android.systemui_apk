package com.android.keyguard;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionListenerAdapter;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.AttributeSet;
import android.util.Log;
import android.util.MathUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import androidx.annotation.VisibleForTesting;
import com.android.internal.colorextraction.ColorExtractor;
import com.android.keyguard.KeyguardClockSwitch;
import com.android.keyguard.clock.ClockManager;
import com.android.systemui.Interpolators;
import com.android.systemui.R;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.plugins.ClockPlugin;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.util.wakelock.KeepAwakeAnimationListener;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.TimeZone;
import javax.inject.Inject;
import javax.inject.Named;
/* loaded from: classes19.dex */
public class KeyguardClockSwitch extends RelativeLayout {
    private static final boolean CUSTOM_CLOCKS_ENABLED = false;
    private static final String TAG = "KeyguardClockSwitch";
    private static final float TO_BOLD_TRANSITION_FRACTION = 0.7f;
    private ViewGroup mBigClockContainer;
    private final ClockVisibilityTransition mBoldClockTransition;
    private ClockManager.ClockChangedListener mClockChangedListener;
    private final ClockManager mClockManager;
    private ClockPlugin mClockPlugin;
    private final ClockVisibilityTransition mClockTransition;
    private TextClock mClockView;
    private TextClock mClockViewBold;
    private int[] mColorPalette;
    private final ColorExtractor.OnColorsChangedListener mColorsListener;
    private float mDarkAmount;
    private boolean mHasVisibleNotifications;
    private View mKeyguardStatusArea;
    private boolean mShowingHeader;
    private FrameLayout mSmallClockFrame;
    private final StatusBarStateController.StateListener mStateListener;
    private int mStatusBarState;
    private final StatusBarStateController mStatusBarStateController;
    private boolean mSupportsDarkText;
    private final SysuiColorExtractor mSysuiColorExtractor;
    private final Transition mTransition;

    public /* synthetic */ void lambda$new$0$KeyguardClockSwitch(ColorExtractor extractor, int which) {
        if ((which & 2) != 0) {
            updateColors();
        }
    }

    @Inject
    public KeyguardClockSwitch(@Named("view_context") Context context, AttributeSet attrs, StatusBarStateController statusBarStateController, SysuiColorExtractor colorExtractor, ClockManager clockManager) {
        super(context, attrs);
        this.mStateListener = new StatusBarStateController.StateListener() { // from class: com.android.keyguard.KeyguardClockSwitch.1
            @Override // com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener
            public void onStateChanged(int newState) {
                KeyguardClockSwitch.this.mStatusBarState = newState;
                KeyguardClockSwitch.this.updateBigClockVisibility();
            }
        };
        this.mClockChangedListener = new ClockManager.ClockChangedListener() { // from class: com.android.keyguard.-$$Lambda$KeyguardClockSwitch$H31kNGqlEfE-tZQZgrBtirdKZKc
            @Override // com.android.keyguard.clock.ClockManager.ClockChangedListener
            public final void onClockChanged(ClockPlugin clockPlugin) {
                KeyguardClockSwitch.this.setClockPlugin(clockPlugin);
            }
        };
        this.mColorsListener = new ColorExtractor.OnColorsChangedListener() { // from class: com.android.keyguard.-$$Lambda$KeyguardClockSwitch$1K4q2TFTethGttjK4WWfYw-lPoo
            public final void onColorsChanged(ColorExtractor colorExtractor2, int i) {
                KeyguardClockSwitch.this.lambda$new$0$KeyguardClockSwitch(colorExtractor2, i);
            }
        };
        this.mStatusBarStateController = statusBarStateController;
        this.mStatusBarState = this.mStatusBarStateController.getState();
        this.mSysuiColorExtractor = colorExtractor;
        this.mClockManager = clockManager;
        this.mClockTransition = new ClockVisibilityTransition().setCutoff(0.3f);
        this.mClockTransition.addTarget(R.id.default_clock_view);
        this.mBoldClockTransition = new ClockVisibilityTransition().setCutoff(0.7f);
        this.mBoldClockTransition.addTarget(R.id.default_clock_view_bold);
        this.mTransition = new TransitionSet().setOrdering(0).addTransition(this.mClockTransition).addTransition(this.mBoldClockTransition).setDuration(275L).setInterpolator((TimeInterpolator) Interpolators.LINEAR_OUT_SLOW_IN);
    }

    public boolean hasCustomClock() {
        return this.mClockPlugin != null;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mClockView = (TextClock) findViewById(R.id.default_clock_view);
        this.mClockViewBold = (TextClock) findViewById(R.id.default_clock_view_bold);
        this.mSmallClockFrame = (FrameLayout) findViewById(R.id.clock_view);
        this.mKeyguardStatusArea = findViewById(R.id.keyguard_status_area);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mStatusBarStateController.addCallback(this.mStateListener);
        this.mSysuiColorExtractor.addOnColorsChangedListener(this.mColorsListener);
        updateColors();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mStatusBarStateController.removeCallback(this.mStateListener);
        this.mSysuiColorExtractor.removeOnColorsChangedListener(this.mColorsListener);
        setClockPlugin(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setClockPlugin(ClockPlugin plugin) {
        ViewGroup viewGroup;
        ClockPlugin clockPlugin = this.mClockPlugin;
        if (clockPlugin != null) {
            View smallClockView = clockPlugin.getView();
            if (smallClockView != null) {
                ViewParent parent = smallClockView.getParent();
                FrameLayout frameLayout = this.mSmallClockFrame;
                if (parent == frameLayout) {
                    frameLayout.removeView(smallClockView);
                }
            }
            ViewGroup viewGroup2 = this.mBigClockContainer;
            if (viewGroup2 != null) {
                viewGroup2.removeAllViews();
                updateBigClockVisibility();
            }
            this.mClockPlugin.onDestroyView();
            this.mClockPlugin = null;
        }
        if (plugin == null) {
            if (this.mShowingHeader) {
                this.mClockView.setVisibility(8);
                this.mClockViewBold.setVisibility(0);
            } else {
                this.mClockView.setVisibility(0);
                this.mClockViewBold.setVisibility(4);
            }
            this.mKeyguardStatusArea.setVisibility(0);
            return;
        }
        View smallClockView2 = plugin.getView();
        if (smallClockView2 != null) {
            this.mSmallClockFrame.addView(smallClockView2, -1, new ViewGroup.LayoutParams(-1, -2));
            this.mClockView.setVisibility(8);
            this.mClockViewBold.setVisibility(8);
        }
        View bigClockView = plugin.getBigClockView();
        if (bigClockView != null && (viewGroup = this.mBigClockContainer) != null) {
            viewGroup.addView(bigClockView);
            updateBigClockVisibility();
        }
        if (!plugin.shouldShowStatusArea()) {
            this.mKeyguardStatusArea.setVisibility(8);
        }
        this.mClockPlugin = plugin;
        this.mClockPlugin.setStyle(getPaint().getStyle());
        this.mClockPlugin.setTextColor(getCurrentTextColor());
        this.mClockPlugin.setDarkAmount(this.mDarkAmount);
        int[] iArr = this.mColorPalette;
        if (iArr != null) {
            this.mClockPlugin.setColorPalette(this.mSupportsDarkText, iArr);
        }
    }

    public void setBigClockContainer(ViewGroup container) {
        View bigClockView;
        ClockPlugin clockPlugin = this.mClockPlugin;
        if (clockPlugin != null && container != null && (bigClockView = clockPlugin.getBigClockView()) != null) {
            container.addView(bigClockView);
        }
        this.mBigClockContainer = container;
        updateBigClockVisibility();
    }

    public void setStyle(Paint.Style style) {
        this.mClockView.getPaint().setStyle(style);
        this.mClockViewBold.getPaint().setStyle(style);
        ClockPlugin clockPlugin = this.mClockPlugin;
        if (clockPlugin != null) {
            clockPlugin.setStyle(style);
        }
    }

    public void setTextColor(int color) {
        this.mClockView.setTextColor(color);
        this.mClockViewBold.setTextColor(color);
        ClockPlugin clockPlugin = this.mClockPlugin;
        if (clockPlugin != null) {
            clockPlugin.setTextColor(color);
        }
    }

    public void setShowCurrentUserTime(boolean showCurrentUserTime) {
        this.mClockView.setShowCurrentUserTime(showCurrentUserTime);
        this.mClockViewBold.setShowCurrentUserTime(showCurrentUserTime);
    }

    public void setTextSize(int unit, float size) {
        this.mClockView.setTextSize(unit, size);
    }

    public void setFormat12Hour(CharSequence format) {
        this.mClockView.setFormat12Hour(format);
        this.mClockViewBold.setFormat12Hour(format);
    }

    public void setFormat24Hour(CharSequence format) {
        this.mClockView.setFormat24Hour(format);
        this.mClockViewBold.setFormat24Hour(format);
    }

    public void setDarkAmount(float darkAmount) {
        this.mDarkAmount = darkAmount;
        ClockPlugin clockPlugin = this.mClockPlugin;
        if (clockPlugin != null) {
            clockPlugin.setDarkAmount(darkAmount);
        }
        updateBigClockAlpha();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setHasVisibleNotifications(boolean hasVisibleNotifications) {
        ViewGroup viewGroup;
        if (hasVisibleNotifications == this.mHasVisibleNotifications) {
            return;
        }
        this.mHasVisibleNotifications = hasVisibleNotifications;
        if (this.mDarkAmount == 0.0f && (viewGroup = this.mBigClockContainer) != null) {
            TransitionManager.beginDelayedTransition(viewGroup, new Fade().setDuration(275L).addTarget(this.mBigClockContainer));
        }
        updateBigClockAlpha();
    }

    public Paint getPaint() {
        return this.mClockView.getPaint();
    }

    public int getCurrentTextColor() {
        return this.mClockView.getCurrentTextColor();
    }

    public float getTextSize() {
        return this.mClockView.getTextSize();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getPreferredY(int totalHeight) {
        ClockPlugin clockPlugin = this.mClockPlugin;
        if (clockPlugin != null) {
            return clockPlugin.getPreferredY(totalHeight);
        }
        return totalHeight / 2;
    }

    public void refresh() {
        this.mClockView.refresh();
        this.mClockViewBold.refresh();
        ClockPlugin clockPlugin = this.mClockPlugin;
        if (clockPlugin != null) {
            clockPlugin.onTimeTick();
        }
        if (Build.IS_DEBUGGABLE) {
            Log.d(TAG, "Updating clock: " + ((Object) this.mClockView.getText()));
        }
    }

    public void onTimeZoneChanged(TimeZone timeZone) {
        ClockPlugin clockPlugin = this.mClockPlugin;
        if (clockPlugin != null) {
            clockPlugin.onTimeZoneChanged(timeZone);
        }
    }

    private void updateColors() {
        ColorExtractor.GradientColors colors = this.mSysuiColorExtractor.getColors(2);
        this.mSupportsDarkText = colors.supportsDarkText();
        this.mColorPalette = colors.getColorPalette();
        ClockPlugin clockPlugin = this.mClockPlugin;
        if (clockPlugin != null) {
            clockPlugin.setColorPalette(this.mSupportsDarkText, this.mColorPalette);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateBigClockVisibility() {
        if (this.mBigClockContainer == null) {
            return;
        }
        int i = this.mStatusBarState;
        int visibility = 0;
        boolean z = true;
        if (i != 1 && i != 2) {
            z = false;
        }
        boolean inDisplayState = z;
        visibility = (!inDisplayState || this.mBigClockContainer.getChildCount() == 0) ? 8 : 8;
        if (this.mBigClockContainer.getVisibility() != visibility) {
            this.mBigClockContainer.setVisibility(visibility);
        }
    }

    private void updateBigClockAlpha() {
        if (this.mBigClockContainer != null) {
            float alpha = this.mHasVisibleNotifications ? this.mDarkAmount : 1.0f;
            this.mBigClockContainer.setAlpha(alpha);
            if (alpha == 0.0f) {
                this.mBigClockContainer.setVisibility(4);
            } else if (this.mBigClockContainer.getVisibility() == 4) {
                this.mBigClockContainer.setVisibility(0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setKeyguardShowingHeader(boolean hasHeader) {
        if (this.mShowingHeader == hasHeader) {
            return;
        }
        this.mShowingHeader = hasHeader;
        if (hasCustomClock()) {
            return;
        }
        float smallFontSize = this.mContext.getResources().getDimensionPixelSize(R.dimen.widget_small_font_size);
        float bigFontSize = this.mContext.getResources().getDimensionPixelSize(R.dimen.widget_big_font_size);
        this.mClockTransition.setScale(smallFontSize / bigFontSize);
        this.mBoldClockTransition.setScale(bigFontSize / smallFontSize);
        TransitionManager.endTransitions((ViewGroup) this.mClockView.getParent());
        if (hasHeader) {
            this.mTransition.addListener(new TransitionListenerAdapter() { // from class: com.android.keyguard.KeyguardClockSwitch.2
                @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
                public void onTransitionEnd(Transition transition) {
                    super.onTransitionEnd(transition);
                    if (KeyguardClockSwitch.this.mShowingHeader) {
                        KeyguardClockSwitch.this.mClockView.setVisibility(8);
                    }
                    transition.removeListener(this);
                }
            });
        }
        TransitionManager.beginDelayedTransition((ViewGroup) this.mClockView.getParent(), this.mTransition);
        this.mClockView.setVisibility(hasHeader ? 4 : 0);
        this.mClockViewBold.setVisibility(hasHeader ? 0 : 4);
        int paddingBottom = this.mContext.getResources().getDimensionPixelSize(hasHeader ? R.dimen.widget_vertical_padding_clock : R.dimen.title_clock_padding);
        TextClock textClock = this.mClockView;
        textClock.setPadding(textClock.getPaddingLeft(), this.mClockView.getPaddingTop(), this.mClockView.getPaddingRight(), paddingBottom);
        TextClock textClock2 = this.mClockViewBold;
        textClock2.setPadding(textClock2.getPaddingLeft(), this.mClockViewBold.getPaddingTop(), this.mClockViewBold.getPaddingRight(), paddingBottom);
    }

    @VisibleForTesting(otherwise = 5)
    ClockManager.ClockChangedListener getClockChangedListener() {
        return this.mClockChangedListener;
    }

    @VisibleForTesting(otherwise = 5)
    StatusBarStateController.StateListener getStateListener() {
        return this.mStateListener;
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("KeyguardClockSwitch:");
        pw.println("  mClockPlugin: " + this.mClockPlugin);
        pw.println("  mClockView: " + this.mClockView);
        pw.println("  mClockViewBold: " + this.mClockViewBold);
        pw.println("  mSmallClockFrame: " + this.mSmallClockFrame);
        pw.println("  mBigClockContainer: " + this.mBigClockContainer);
        pw.println("  mKeyguardStatusArea: " + this.mKeyguardStatusArea);
        pw.println("  mDarkAmount: " + this.mDarkAmount);
        pw.println("  mShowingHeader: " + this.mShowingHeader);
        pw.println("  mSupportsDarkText: " + this.mSupportsDarkText);
        pw.println("  mColorPalette: " + Arrays.toString(this.mColorPalette));
    }

    /* loaded from: classes19.dex */
    private class ClockVisibilityTransition extends Visibility {
        private static final String PROPNAME_VISIBILITY = "systemui:keyguard:visibility";
        private float mCutoff;
        private float mScale;

        ClockVisibilityTransition() {
            setCutoff(1.0f);
            setScale(1.0f);
        }

        public ClockVisibilityTransition setCutoff(float cutoff) {
            this.mCutoff = cutoff;
            return this;
        }

        public ClockVisibilityTransition setScale(float scale) {
            this.mScale = scale;
            return this;
        }

        @Override // android.transition.Visibility, android.transition.Transition
        public void captureStartValues(TransitionValues transitionValues) {
            super.captureStartValues(transitionValues);
            captureVisibility(transitionValues);
        }

        @Override // android.transition.Visibility, android.transition.Transition
        public void captureEndValues(TransitionValues transitionValues) {
            super.captureStartValues(transitionValues);
            captureVisibility(transitionValues);
        }

        private void captureVisibility(TransitionValues transitionValues) {
            transitionValues.values.put(PROPNAME_VISIBILITY, Integer.valueOf(transitionValues.view.getVisibility()));
        }

        @Override // android.transition.Visibility
        public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
            if (!sceneRoot.isShown()) {
                return null;
            }
            float cutoff = this.mCutoff;
            int endVisibility = ((Integer) endValues.values.get(PROPNAME_VISIBILITY)).intValue();
            float startScale = this.mScale;
            return createAnimator(view, cutoff, 4, endVisibility, startScale, 1.0f);
        }

        @Override // android.transition.Visibility
        public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
            if (!sceneRoot.isShown()) {
                return null;
            }
            float cutoff = 1.0f - this.mCutoff;
            int endVisibility = ((Integer) endValues.values.get(PROPNAME_VISIBILITY)).intValue();
            float endScale = this.mScale;
            return createAnimator(view, cutoff, 0, endVisibility, 1.0f, endScale);
        }

        private Animator createAnimator(final View view, final float cutoff, final int startVisibility, final int endVisibility, final float startScale, final float endScale) {
            view.setPivotY(view.getHeight() - view.getPaddingBottom());
            ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.keyguard.-$$Lambda$KeyguardClockSwitch$ClockVisibilityTransition$0YYk1dKss121y1dzD6OuOcSJduA
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    KeyguardClockSwitch.ClockVisibilityTransition.lambda$createAnimator$0(cutoff, view, endVisibility, startScale, endScale, valueAnimator);
                }
            });
            animator.addListener(new KeepAwakeAnimationListener(KeyguardClockSwitch.this.getContext()) { // from class: com.android.keyguard.KeyguardClockSwitch.ClockVisibilityTransition.1
                @Override // com.android.systemui.util.wakelock.KeepAwakeAnimationListener, android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    view.setVisibility(startVisibility);
                }

                @Override // com.android.systemui.util.wakelock.KeepAwakeAnimationListener, android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animation.removeListener(this);
                }
            });
            addListener(new TransitionListenerAdapter() { // from class: com.android.keyguard.KeyguardClockSwitch.ClockVisibilityTransition.2
                @Override // android.transition.TransitionListenerAdapter, android.transition.Transition.TransitionListener
                public void onTransitionEnd(Transition transition) {
                    view.setVisibility(endVisibility);
                    view.setScaleX(1.0f);
                    view.setScaleY(1.0f);
                    transition.removeListener(this);
                }
            });
            return animator;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$createAnimator$0(float cutoff, View view, int endVisibility, float startScale, float endScale, ValueAnimator animation) {
            float fraction = animation.getAnimatedFraction();
            if (fraction > cutoff) {
                view.setVisibility(endVisibility);
            }
            float scale = MathUtils.lerp(startScale, endScale, fraction);
            view.setScaleX(scale);
            view.setScaleY(scale);
        }
    }
}
