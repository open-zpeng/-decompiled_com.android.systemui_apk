package com.android.systemui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.VectorDrawable;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.MathUtils;
import android.view.DisplayCutout;
import android.view.DisplayInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.VisibleForTesting;
import com.alibaba.fastjson.asm.Opcodes;
import com.android.internal.util.Preconditions;
import com.android.systemui.CameraAvailabilityListener;
import com.android.systemui.RegionInterceptingFrameLayout;
import com.android.systemui.ScreenDecorations;
import com.android.systemui.fragments.FragmentHostManager;
import com.android.systemui.plugins.qs.QS;
import com.android.systemui.qs.SecureSetting;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.statusbar.phone.CollapsedStatusBarFragment;
import com.android.systemui.statusbar.phone.NavigationBarTransitions;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.tuner.TunablePadding;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.util.leak.RotationUtils;
import com.xiaopeng.systemui.helper.WindowHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
/* loaded from: classes21.dex */
public class ScreenDecorations extends SystemUI implements TunerService.Tunable, NavigationBarTransitions.DarkIntensityListener {
    private static final boolean DEBUG = false;
    public static final String PADDING = "sysui_rounded_content_padding";
    public static final String SIZE = "sysui_rounded_size";
    private static final String TAG = "ScreenDecorations";
    private static final boolean VERBOSE = false;
    private boolean mAssistHintVisible;
    private View mBottomOverlay;
    private CameraAvailabilityListener mCameraListener;
    private SecureSetting mColorInversionSetting;
    private DisplayCutoutView mCutoutBottom;
    private DisplayCutoutView mCutoutTop;
    private float mDensity;
    private DisplayManager.DisplayListener mDisplayListener;
    private DisplayManager mDisplayManager;
    private Handler mHandler;
    private boolean mInGesturalMode;
    private boolean mIsRoundedCornerMultipleRadius;
    private View mOverlay;
    private boolean mPendingRotationChange;
    private int mRotation;
    @VisibleForTesting
    protected int mRoundedDefault;
    @VisibleForTesting
    protected int mRoundedDefaultBottom;
    @VisibleForTesting
    protected int mRoundedDefaultTop;
    private WindowManager mWindowManager;
    private static final boolean DEBUG_SCREENSHOT_ROUNDED_CORNERS = SystemProperties.getBoolean("debug.screenshot_rounded_corners", false);
    private static final boolean DEBUG_COLOR = DEBUG_SCREENSHOT_ROUNDED_CORNERS;
    private boolean mAssistHintBlocked = false;
    private boolean mIsReceivingNavBarColor = false;
    private CameraAvailabilityListener.CameraTransitionCallback mCameraTransitionCallback = new CameraAvailabilityListener.CameraTransitionCallback() { // from class: com.android.systemui.ScreenDecorations.1
        @Override // com.android.systemui.CameraAvailabilityListener.CameraTransitionCallback
        public void onApplyCameraProtection(Path protectionPath, Rect bounds) {
            ScreenDecorations.this.mCutoutTop.setProtection(protectionPath, bounds);
            ScreenDecorations.this.mCutoutTop.setShowProtection(true);
            ScreenDecorations.this.mCutoutBottom.setProtection(protectionPath, bounds);
            ScreenDecorations.this.mCutoutBottom.setShowProtection(true);
        }

        @Override // com.android.systemui.CameraAvailabilityListener.CameraTransitionCallback
        public void onHideCameraProtection() {
            ScreenDecorations.this.mCutoutTop.setShowProtection(false);
            ScreenDecorations.this.mCutoutBottom.setShowProtection(false);
        }
    };
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() { // from class: com.android.systemui.ScreenDecorations.5
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.intent.action.USER_SWITCHED")) {
                int newUserId = intent.getIntExtra("android.intent.extra.user_handle", ActivityManager.getCurrentUser());
                ScreenDecorations.this.mColorInversionSetting.setUserId(newUserId);
                ScreenDecorations screenDecorations = ScreenDecorations.this;
                screenDecorations.updateColorInversion(screenDecorations.mColorInversionSetting.getValue());
            }
        }
    };

    public static Region rectsToRegion(List<Rect> rects) {
        Region result = Region.obtain();
        if (rects != null) {
            for (Rect r : rects) {
                if (r != null && !r.isEmpty()) {
                    result.op(r, Region.Op.UNION);
                }
            }
        }
        return result;
    }

    @Override // com.android.systemui.SystemUI
    public void start() {
        this.mHandler = startHandlerThread();
        this.mHandler.post(new Runnable() { // from class: com.android.systemui.-$$Lambda$ScreenDecorations$IfAux2ksmJXT9o9i38WaSEQXJTQ
            @Override // java.lang.Runnable
            public final void run() {
                ScreenDecorations.this.startOnScreenDecorationsThread();
            }
        });
        setupStatusBarPaddingIfNeeded();
        putComponent(ScreenDecorations.class, this);
        this.mInGesturalMode = QuickStepContract.isGesturalMode(((NavigationModeController) Dependency.get(NavigationModeController.class)).addListener(new NavigationModeController.ModeChangedListener() { // from class: com.android.systemui.-$$Lambda$60rw5ghsFNPB4OvLwvmy1hJgGv0
            @Override // com.android.systemui.statusbar.phone.NavigationModeController.ModeChangedListener
            public final void onNavigationModeChanged(int i) {
                ScreenDecorations.this.lambda$handleNavigationModeChange$0$ScreenDecorations(i);
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    /* renamed from: handleNavigationModeChange */
    public void lambda$handleNavigationModeChange$0$ScreenDecorations(final int navigationMode) {
        if (!this.mHandler.getLooper().isCurrentThread()) {
            this.mHandler.post(new Runnable() { // from class: com.android.systemui.-$$Lambda$ScreenDecorations$4F6CKqAgtSx8ZovPRT6WEWEYS4E
                @Override // java.lang.Runnable
                public final void run() {
                    ScreenDecorations.this.lambda$handleNavigationModeChange$0$ScreenDecorations(navigationMode);
                }
            });
            return;
        }
        boolean inGesturalMode = QuickStepContract.isGesturalMode(navigationMode);
        if (this.mInGesturalMode != inGesturalMode) {
            this.mInGesturalMode = inGesturalMode;
            if (this.mInGesturalMode && this.mOverlay == null) {
                setupDecorations();
                if (this.mOverlay != null) {
                    updateLayoutParams();
                }
            }
        }
    }

    Animator getHandleAnimator(View view, float start, float end, boolean isLeft, long durationMs, Interpolator interpolator) {
        float scaleStart = MathUtils.lerp(2.0f, 1.0f, start);
        float scaleEnd = MathUtils.lerp(2.0f, 1.0f, end);
        Animator scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, scaleStart, scaleEnd);
        Animator scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, scaleStart, scaleEnd);
        float translationStart = MathUtils.lerp(0.2f, 0.0f, start);
        float translationEnd = MathUtils.lerp(0.2f, 0.0f, end);
        int xDirection = isLeft ? -1 : 1;
        Animator translateX = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, xDirection * translationStart * view.getWidth(), xDirection * translationEnd * view.getWidth());
        Animator translateY = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, view.getHeight() * translationStart, view.getHeight() * translationEnd);
        AnimatorSet set = new AnimatorSet();
        set.play(scaleX).with(scaleY);
        set.play(scaleX).with(translateX);
        set.play(scaleX).with(translateY);
        set.setDuration(durationMs);
        set.setInterpolator(interpolator);
        return set;
    }

    private void fade(View view, boolean fadeIn, boolean isLeft) {
        if (fadeIn) {
            view.animate().cancel();
            view.setAlpha(1.0f);
            view.setVisibility(0);
            AnimatorSet anim = new AnimatorSet();
            Animator first = getHandleAnimator(view, 0.0f, 1.1f, isLeft, 750L, new PathInterpolator(0.0f, 0.45f, 0.67f, 1.0f));
            Interpolator secondInterpolator = new PathInterpolator(0.33f, 0.0f, 0.67f, 1.0f);
            Animator second = getHandleAnimator(view, 1.1f, 0.97f, isLeft, 400L, secondInterpolator);
            Animator third = getHandleAnimator(view, 0.97f, 1.02f, isLeft, 400L, secondInterpolator);
            Animator fourth = getHandleAnimator(view, 1.02f, 1.0f, isLeft, 400L, secondInterpolator);
            anim.play(first).before(second);
            anim.play(second).before(third);
            anim.play(third).before(fourth);
            anim.start();
            return;
        }
        view.animate().cancel();
        view.animate().setInterpolator(new AccelerateInterpolator(1.5f)).setDuration(250L).alpha(0.0f);
    }

    /* renamed from: setAssistHintVisible */
    public void lambda$setAssistHintVisible$1$ScreenDecorations(final boolean visible) {
        View view;
        if (!this.mHandler.getLooper().isCurrentThread()) {
            this.mHandler.post(new Runnable() { // from class: com.android.systemui.-$$Lambda$ScreenDecorations$v4Vg-fK79EV22k9HdjvuSqrLHx4
                @Override // java.lang.Runnable
                public final void run() {
                    ScreenDecorations.this.lambda$setAssistHintVisible$1$ScreenDecorations(visible);
                }
            });
        } else if ((this.mAssistHintBlocked && visible) || (view = this.mOverlay) == null || this.mBottomOverlay == null) {
        } else {
            if (this.mAssistHintVisible != visible) {
                this.mAssistHintVisible = visible;
                CornerHandleView assistHintTopLeft = (CornerHandleView) view.findViewById(R.id.assist_hint_left);
                CornerHandleView assistHintTopRight = (CornerHandleView) this.mOverlay.findViewById(R.id.assist_hint_right);
                CornerHandleView assistHintBottomLeft = (CornerHandleView) this.mBottomOverlay.findViewById(R.id.assist_hint_left);
                CornerHandleView assistHintBottomRight = (CornerHandleView) this.mBottomOverlay.findViewById(R.id.assist_hint_right);
                int i = this.mRotation;
                if (i == 0) {
                    fade(assistHintBottomLeft, this.mAssistHintVisible, true);
                    fade(assistHintBottomRight, this.mAssistHintVisible, false);
                } else if (i == 1) {
                    fade(assistHintTopRight, this.mAssistHintVisible, true);
                    fade(assistHintBottomRight, this.mAssistHintVisible, false);
                } else if (i == 2) {
                    fade(assistHintTopLeft, this.mAssistHintVisible, false);
                    fade(assistHintBottomLeft, this.mAssistHintVisible, true);
                } else if (i == 3) {
                    fade(assistHintTopLeft, this.mAssistHintVisible, false);
                    fade(assistHintTopRight, this.mAssistHintVisible, true);
                }
            }
            updateWindowVisibilities();
        }
    }

    /* renamed from: setAssistHintBlocked */
    public void lambda$setAssistHintBlocked$2$ScreenDecorations(final boolean blocked) {
        if (!this.mHandler.getLooper().isCurrentThread()) {
            this.mHandler.post(new Runnable() { // from class: com.android.systemui.-$$Lambda$ScreenDecorations$X65dAPl3paBdNr5xrYJHzDmgROE
                @Override // java.lang.Runnable
                public final void run() {
                    ScreenDecorations.this.lambda$setAssistHintBlocked$2$ScreenDecorations(blocked);
                }
            });
            return;
        }
        this.mAssistHintBlocked = blocked;
        if (this.mAssistHintVisible && this.mAssistHintBlocked) {
            hideAssistHandles();
        }
    }

    @VisibleForTesting
    Handler startHandlerThread() {
        HandlerThread thread = new HandlerThread(TAG);
        thread.start();
        return thread.getThreadHandler();
    }

    private boolean shouldHostHandles() {
        return this.mInGesturalMode;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startOnScreenDecorationsThread() {
        this.mRotation = RotationUtils.getExactRotation(this.mContext);
        this.mWindowManager = (WindowManager) this.mContext.getSystemService(WindowManager.class);
        this.mIsRoundedCornerMultipleRadius = this.mContext.getResources().getBoolean(R.bool.config_roundedCornerMultipleRadius);
        updateRoundedCornerRadii();
        if (hasRoundedCorners() || shouldDrawCutout() || shouldHostHandles()) {
            setupDecorations();
            setupCameraListener();
        }
        this.mDisplayListener = new DisplayManager.DisplayListener() { // from class: com.android.systemui.ScreenDecorations.2
            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayAdded(int displayId) {
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayRemoved(int displayId) {
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayChanged(int displayId) {
                int newRotation = RotationUtils.getExactRotation(ScreenDecorations.this.mContext);
                if (ScreenDecorations.this.mOverlay != null && ScreenDecorations.this.mBottomOverlay != null && ScreenDecorations.this.mRotation != newRotation) {
                    ScreenDecorations.this.mPendingRotationChange = true;
                    ViewTreeObserver viewTreeObserver = ScreenDecorations.this.mOverlay.getViewTreeObserver();
                    ScreenDecorations screenDecorations = ScreenDecorations.this;
                    viewTreeObserver.addOnPreDrawListener(new RestartingPreDrawListener(screenDecorations.mOverlay, newRotation));
                    ViewTreeObserver viewTreeObserver2 = ScreenDecorations.this.mBottomOverlay.getViewTreeObserver();
                    ScreenDecorations screenDecorations2 = ScreenDecorations.this;
                    viewTreeObserver2.addOnPreDrawListener(new RestartingPreDrawListener(screenDecorations2.mBottomOverlay, newRotation));
                }
                ScreenDecorations.this.updateOrientation();
            }
        };
        this.mDisplayManager = (DisplayManager) this.mContext.getSystemService("display");
        this.mDisplayManager.registerDisplayListener(this.mDisplayListener, this.mHandler);
        updateOrientation();
    }

    private void setupDecorations() {
        this.mOverlay = LayoutInflater.from(this.mContext).inflate(R.layout.rounded_corners, (ViewGroup) null);
        this.mCutoutTop = new DisplayCutoutView(this.mContext, true, new Runnable() { // from class: com.android.systemui.-$$Lambda$ScreenDecorations$aq1MVJyy_LkZ11q5t5cPVZOqbG0
            @Override // java.lang.Runnable
            public final void run() {
                ScreenDecorations.this.updateWindowVisibilities();
            }
        }, this);
        ((ViewGroup) this.mOverlay).addView(this.mCutoutTop);
        this.mBottomOverlay = LayoutInflater.from(this.mContext).inflate(R.layout.rounded_corners, (ViewGroup) null);
        this.mCutoutBottom = new DisplayCutoutView(this.mContext, false, new Runnable() { // from class: com.android.systemui.-$$Lambda$ScreenDecorations$aq1MVJyy_LkZ11q5t5cPVZOqbG0
            @Override // java.lang.Runnable
            public final void run() {
                ScreenDecorations.this.updateWindowVisibilities();
            }
        }, this);
        ((ViewGroup) this.mBottomOverlay).addView(this.mCutoutBottom);
        this.mOverlay.setSystemUiVisibility(256);
        this.mOverlay.setAlpha(0.0f);
        this.mOverlay.setForceDarkAllowed(false);
        this.mBottomOverlay.setSystemUiVisibility(256);
        this.mBottomOverlay.setAlpha(0.0f);
        this.mBottomOverlay.setForceDarkAllowed(false);
        updateViews();
        this.mWindowManager.addView(this.mOverlay, getWindowLayoutParams());
        this.mWindowManager.addView(this.mBottomOverlay, getBottomLayoutParams());
        DisplayMetrics metrics = new DisplayMetrics();
        this.mWindowManager.getDefaultDisplay().getMetrics(metrics);
        this.mDensity = metrics.density;
        ((Handler) Dependency.get(Dependency.MAIN_HANDLER)).post(new Runnable() { // from class: com.android.systemui.-$$Lambda$ScreenDecorations$80y3DVvpOo5hQVD-W3EJlUKhPsU
            @Override // java.lang.Runnable
            public final void run() {
                ScreenDecorations.this.lambda$setupDecorations$3$ScreenDecorations();
            }
        });
        this.mColorInversionSetting = new SecureSetting(this.mContext, this.mHandler, "accessibility_display_inversion_enabled") { // from class: com.android.systemui.ScreenDecorations.3
            @Override // com.android.systemui.qs.SecureSetting
            protected void handleValueChanged(int value, boolean observedChange) {
                ScreenDecorations.this.updateColorInversion(value);
            }
        };
        this.mColorInversionSetting.setListening(true);
        this.mColorInversionSetting.onChange(false);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.USER_SWITCHED");
        this.mContext.registerReceiver(this.mIntentReceiver, filter, null, this.mHandler);
        this.mOverlay.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: com.android.systemui.ScreenDecorations.4
            @Override // android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                ScreenDecorations.this.mOverlay.removeOnLayoutChangeListener(this);
                ScreenDecorations.this.mOverlay.animate().alpha(1.0f).setDuration(1000L).start();
                ScreenDecorations.this.mBottomOverlay.animate().alpha(1.0f).setDuration(1000L).start();
            }
        });
        this.mOverlay.getViewTreeObserver().addOnPreDrawListener(new ValidatingPreDrawListener(this.mOverlay));
        this.mBottomOverlay.getViewTreeObserver().addOnPreDrawListener(new ValidatingPreDrawListener(this.mBottomOverlay));
    }

    public /* synthetic */ void lambda$setupDecorations$3$ScreenDecorations() {
        ((TunerService) Dependency.get(TunerService.class)).addTunable(this, SIZE);
    }

    private void setupCameraListener() {
        Resources res = this.mContext.getResources();
        boolean enabled = res.getBoolean(R.bool.config_enableDisplayCutoutProtection);
        if (enabled) {
            CameraAvailabilityListener.Factory factory = CameraAvailabilityListener.Factory;
            Context context = this.mContext;
            final Handler handler = this.mHandler;
            Objects.requireNonNull(handler);
            this.mCameraListener = factory.build(context, new Executor() { // from class: com.android.systemui.-$$Lambda$LfzJt661qZfn2w-6SYHFbD3aMy0
                @Override // java.util.concurrent.Executor
                public final void execute(Runnable runnable) {
                    handler.post(runnable);
                }
            });
            this.mCameraListener.addTransitionCallback(this.mCameraTransitionCallback);
            this.mCameraListener.startListening();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateColorInversion(int colorsInvertedValue) {
        int tint = colorsInvertedValue != 0 ? -1 : -16777216;
        if (DEBUG_COLOR) {
            tint = -65536;
        }
        ColorStateList tintList = ColorStateList.valueOf(tint);
        ((ImageView) this.mOverlay.findViewById(R.id.left)).setImageTintList(tintList);
        ((ImageView) this.mOverlay.findViewById(R.id.right)).setImageTintList(tintList);
        ((ImageView) this.mBottomOverlay.findViewById(R.id.left)).setImageTintList(tintList);
        ((ImageView) this.mBottomOverlay.findViewById(R.id.right)).setImageTintList(tintList);
        this.mCutoutTop.setColor(tint);
        this.mCutoutBottom.setColor(tint);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.SystemUI
    public void onConfigurationChanged(Configuration newConfig) {
        this.mHandler.post(new Runnable() { // from class: com.android.systemui.-$$Lambda$ScreenDecorations$ZcXfKc-loCI7zxM2FCddG-g2uzM
            @Override // java.lang.Runnable
            public final void run() {
                ScreenDecorations.this.lambda$onConfigurationChanged$4$ScreenDecorations();
            }
        });
    }

    public /* synthetic */ void lambda$onConfigurationChanged$4$ScreenDecorations() {
        int i = this.mRotation;
        this.mPendingRotationChange = false;
        updateOrientation();
        updateRoundedCornerRadii();
        if (shouldDrawCutout() && this.mOverlay == null) {
            setupDecorations();
        }
        if (this.mOverlay != null) {
            updateLayoutParams();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateOrientation() {
        int newRotation;
        boolean z = this.mHandler.getLooper().getThread() == Thread.currentThread();
        Preconditions.checkState(z, "must call on " + this.mHandler.getLooper().getThread() + ", but was " + Thread.currentThread());
        if (!this.mPendingRotationChange && (newRotation = RotationUtils.getExactRotation(this.mContext)) != this.mRotation) {
            this.mRotation = newRotation;
            if (this.mOverlay != null) {
                updateLayoutParams();
                updateViews();
                if (this.mAssistHintVisible) {
                    hideAssistHandles();
                    lambda$setAssistHintVisible$1$ScreenDecorations(true);
                }
            }
        }
    }

    private void hideAssistHandles() {
        View view = this.mOverlay;
        if (view != null && this.mBottomOverlay != null) {
            view.findViewById(R.id.assist_hint_left).setVisibility(8);
            this.mOverlay.findViewById(R.id.assist_hint_right).setVisibility(8);
            this.mBottomOverlay.findViewById(R.id.assist_hint_left).setVisibility(8);
            this.mBottomOverlay.findViewById(R.id.assist_hint_right).setVisibility(8);
            this.mAssistHintVisible = false;
        }
    }

    private void updateRoundedCornerRadii() {
        int newRoundedDefault = this.mContext.getResources().getDimensionPixelSize(17105412);
        int newRoundedDefaultTop = this.mContext.getResources().getDimensionPixelSize(17105416);
        int newRoundedDefaultBottom = this.mContext.getResources().getDimensionPixelSize(17105414);
        boolean roundedCornersChanged = (this.mRoundedDefault == newRoundedDefault && this.mRoundedDefaultBottom == newRoundedDefaultBottom && this.mRoundedDefaultTop == newRoundedDefaultTop) ? false : true;
        if (roundedCornersChanged) {
            if (this.mIsRoundedCornerMultipleRadius) {
                VectorDrawable d = (VectorDrawable) this.mContext.getDrawable(R.drawable.rounded);
                this.mRoundedDefault = Math.max(d.getIntrinsicWidth(), d.getIntrinsicHeight());
                int i = this.mRoundedDefault;
                this.mRoundedDefaultBottom = i;
                this.mRoundedDefaultTop = i;
            } else {
                this.mRoundedDefault = newRoundedDefault;
                this.mRoundedDefaultTop = newRoundedDefaultTop;
                this.mRoundedDefaultBottom = newRoundedDefaultBottom;
            }
            onTuningChanged(SIZE, null);
        }
    }

    private void updateViews() {
        View topLeft = this.mOverlay.findViewById(R.id.left);
        View topRight = this.mOverlay.findViewById(R.id.right);
        View bottomLeft = this.mBottomOverlay.findViewById(R.id.left);
        View bottomRight = this.mBottomOverlay.findViewById(R.id.right);
        int i = this.mRotation;
        if (i == 0) {
            updateView(topLeft, 51, 0);
            updateView(topRight, 53, 90);
            updateView(bottomLeft, 83, 270);
            updateView(bottomRight, 85, Opcodes.GETFIELD);
        } else if (i == 1) {
            updateView(topLeft, 51, 0);
            updateView(topRight, 83, 270);
            updateView(bottomLeft, 53, 90);
            updateView(bottomRight, 85, Opcodes.GETFIELD);
        } else if (i == 3) {
            updateView(topLeft, 83, 270);
            updateView(topRight, 85, Opcodes.GETFIELD);
            updateView(bottomLeft, 51, 0);
            updateView(bottomRight, 53, 90);
        } else if (i == 2) {
            updateView(topLeft, 85, Opcodes.GETFIELD);
            updateView(topRight, 53, 90);
            updateView(bottomLeft, 83, 270);
            updateView(bottomRight, 51, 0);
        }
        updateAssistantHandleViews();
        this.mCutoutTop.setRotation(this.mRotation);
        this.mCutoutBottom.setRotation(this.mRotation);
        updateWindowVisibilities();
    }

    private void updateAssistantHandleViews() {
        View assistHintTopLeft = this.mOverlay.findViewById(R.id.assist_hint_left);
        View assistHintTopRight = this.mOverlay.findViewById(R.id.assist_hint_right);
        View assistHintBottomLeft = this.mBottomOverlay.findViewById(R.id.assist_hint_left);
        View assistHintBottomRight = this.mBottomOverlay.findViewById(R.id.assist_hint_right);
        int assistHintVisibility = this.mAssistHintVisible ? 0 : 4;
        int i = this.mRotation;
        if (i == 0) {
            assistHintTopLeft.setVisibility(8);
            assistHintTopRight.setVisibility(8);
            assistHintBottomLeft.setVisibility(assistHintVisibility);
            assistHintBottomRight.setVisibility(assistHintVisibility);
            updateView(assistHintBottomLeft, 83, 270);
            updateView(assistHintBottomRight, 85, Opcodes.GETFIELD);
        } else if (i == 1) {
            assistHintTopLeft.setVisibility(8);
            assistHintTopRight.setVisibility(assistHintVisibility);
            assistHintBottomLeft.setVisibility(8);
            assistHintBottomRight.setVisibility(assistHintVisibility);
            updateView(assistHintTopRight, 83, 270);
            updateView(assistHintBottomRight, 85, Opcodes.GETFIELD);
        } else if (i == 3) {
            assistHintTopLeft.setVisibility(assistHintVisibility);
            assistHintTopRight.setVisibility(assistHintVisibility);
            assistHintBottomLeft.setVisibility(8);
            assistHintBottomRight.setVisibility(8);
            updateView(assistHintTopLeft, 83, 270);
            updateView(assistHintTopRight, 85, Opcodes.GETFIELD);
        } else if (i == 2) {
            assistHintTopLeft.setVisibility(assistHintVisibility);
            assistHintTopRight.setVisibility(8);
            assistHintBottomLeft.setVisibility(assistHintVisibility);
            assistHintBottomRight.setVisibility(8);
            updateView(assistHintTopLeft, 85, Opcodes.GETFIELD);
            updateView(assistHintBottomLeft, 83, 270);
        }
    }

    private void updateView(View v, int gravity, int rotation) {
        ((FrameLayout.LayoutParams) v.getLayoutParams()).gravity = gravity;
        v.setRotation(rotation);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateWindowVisibilities() {
        updateWindowVisibility(this.mOverlay);
        updateWindowVisibility(this.mBottomOverlay);
    }

    private void updateWindowVisibility(View overlay) {
        boolean visibleForHandles = true;
        int i = 0;
        boolean visibleForCutout = shouldDrawCutout() && overlay.findViewById(R.id.display_cutout).getVisibility() == 0;
        boolean visibleForRoundedCorners = hasRoundedCorners();
        if (overlay.findViewById(R.id.assist_hint_left).getVisibility() != 0 && overlay.findViewById(R.id.assist_hint_right).getVisibility() != 0) {
            visibleForHandles = false;
        }
        if (!visibleForCutout && !visibleForRoundedCorners && !visibleForHandles) {
            i = 8;
        }
        overlay.setVisibility(i);
    }

    private boolean hasRoundedCorners() {
        return this.mRoundedDefault > 0 || this.mRoundedDefaultBottom > 0 || this.mRoundedDefaultTop > 0 || this.mIsRoundedCornerMultipleRadius;
    }

    private boolean shouldDrawCutout() {
        return shouldDrawCutout(this.mContext);
    }

    static boolean shouldDrawCutout(Context context) {
        return context.getResources().getBoolean(17891458);
    }

    private void setupStatusBarPaddingIfNeeded() {
        int padding = this.mContext.getResources().getDimensionPixelSize(R.dimen.rounded_corner_content_padding);
        if (padding != 0) {
            setupStatusBarPadding(padding);
        }
    }

    private void setupStatusBarPadding(int padding) {
        StatusBar sb = (StatusBar) getComponent(StatusBar.class);
        View statusBar = sb != null ? sb.getStatusBarWindow() : null;
        if (statusBar != null) {
            TunablePadding.addTunablePadding(statusBar.findViewById(R.id.keyguard_header), PADDING, padding, 2);
            FragmentHostManager fragmentHostManager = FragmentHostManager.get(statusBar);
            fragmentHostManager.addTagListener(CollapsedStatusBarFragment.TAG, new TunablePaddingTagListener(padding, R.id.status_bar));
            fragmentHostManager.addTagListener(QS.TAG, new TunablePaddingTagListener(padding, R.id.header));
        }
    }

    @VisibleForTesting
    WindowManager.LayoutParams getWindowLayoutParams() {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(-1, -2, WindowHelper.TYPE_NAVIGATION_BAR_PANEL, 545259816, -3);
        lp.privateFlags |= 80;
        if (!DEBUG_SCREENSHOT_ROUNDED_CORNERS) {
            lp.privateFlags |= 1048576;
        }
        lp.setTitle("ScreenDecorOverlay");
        int i = this.mRotation;
        if (i == 2 || i == 3) {
            lp.gravity = 85;
        } else {
            lp.gravity = 51;
        }
        lp.layoutInDisplayCutoutMode = 1;
        if (isLandscape(this.mRotation)) {
            lp.width = -2;
            lp.height = -1;
        }
        lp.privateFlags |= 16777216;
        return lp;
    }

    private WindowManager.LayoutParams getBottomLayoutParams() {
        WindowManager.LayoutParams lp = getWindowLayoutParams();
        lp.setTitle("ScreenDecorOverlayBottom");
        int i = this.mRotation;
        if (i == 2 || i == 3) {
            lp.gravity = 51;
        } else {
            lp.gravity = 85;
        }
        return lp;
    }

    private void updateLayoutParams() {
        this.mWindowManager.updateViewLayout(this.mOverlay, getWindowLayoutParams());
        this.mWindowManager.updateViewLayout(this.mBottomOverlay, getBottomLayoutParams());
    }

    @Override // com.android.systemui.tuner.TunerService.Tunable
    public void onTuningChanged(final String key, final String newValue) {
        this.mHandler.post(new Runnable() { // from class: com.android.systemui.-$$Lambda$ScreenDecorations$mdf60Bg4ecef-imWHJ4lSsesAIU
            @Override // java.lang.Runnable
            public final void run() {
                ScreenDecorations.this.lambda$onTuningChanged$5$ScreenDecorations(key, newValue);
            }
        });
    }

    public /* synthetic */ void lambda$onTuningChanged$5$ScreenDecorations(String key, String newValue) {
        if (this.mOverlay != null && SIZE.equals(key)) {
            int size = this.mRoundedDefault;
            int sizeTop = this.mRoundedDefaultTop;
            int sizeBottom = this.mRoundedDefaultBottom;
            if (newValue != null) {
                try {
                    size = (int) (Integer.parseInt(newValue) * this.mDensity);
                } catch (Exception e) {
                }
            }
            if (sizeTop == 0) {
                sizeTop = size;
            }
            if (sizeBottom == 0) {
                sizeBottom = size;
            }
            setSize(this.mOverlay.findViewById(R.id.left), sizeTop);
            setSize(this.mOverlay.findViewById(R.id.right), sizeTop);
            setSize(this.mBottomOverlay.findViewById(R.id.left), sizeBottom);
            setSize(this.mBottomOverlay.findViewById(R.id.right), sizeBottom);
        }
    }

    private void setSize(View view, int pixelSize) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = pixelSize;
        params.height = pixelSize;
        view.setLayoutParams(params);
    }

    @Override // com.android.systemui.statusbar.phone.NavigationBarTransitions.DarkIntensityListener
    /* renamed from: onDarkIntensity */
    public void lambda$onDarkIntensity$6$ScreenDecorations(final float darkIntensity) {
        if (!this.mHandler.getLooper().isCurrentThread()) {
            this.mHandler.post(new Runnable() { // from class: com.android.systemui.-$$Lambda$ScreenDecorations$0LxH4_gyyT9LgXM946gQ6FsGA7o
                @Override // java.lang.Runnable
                public final void run() {
                    ScreenDecorations.this.lambda$onDarkIntensity$6$ScreenDecorations(darkIntensity);
                }
            });
            return;
        }
        View view = this.mOverlay;
        if (view != null) {
            CornerHandleView assistHintTopLeft = (CornerHandleView) view.findViewById(R.id.assist_hint_left);
            CornerHandleView assistHintTopRight = (CornerHandleView) this.mOverlay.findViewById(R.id.assist_hint_right);
            assistHintTopLeft.updateDarkness(darkIntensity);
            assistHintTopRight.updateDarkness(darkIntensity);
        }
        View view2 = this.mBottomOverlay;
        if (view2 != null) {
            CornerHandleView assistHintBottomLeft = (CornerHandleView) view2.findViewById(R.id.assist_hint_left);
            CornerHandleView assistHintBottomRight = (CornerHandleView) this.mBottomOverlay.findViewById(R.id.assist_hint_right);
            assistHintBottomLeft.updateDarkness(darkIntensity);
            assistHintBottomRight.updateDarkness(darkIntensity);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @VisibleForTesting
    /* loaded from: classes21.dex */
    public static class TunablePaddingTagListener implements FragmentHostManager.FragmentListener {
        private final int mId;
        private final int mPadding;
        private TunablePadding mTunablePadding;

        public TunablePaddingTagListener(int padding, int id) {
            this.mPadding = padding;
            this.mId = id;
        }

        @Override // com.android.systemui.fragments.FragmentHostManager.FragmentListener
        public void onFragmentViewCreated(String tag, Fragment fragment) {
            TunablePadding tunablePadding = this.mTunablePadding;
            if (tunablePadding != null) {
                tunablePadding.destroy();
            }
            View view = fragment.getView();
            int i = this.mId;
            if (i != 0) {
                view = view.findViewById(i);
            }
            this.mTunablePadding = TunablePadding.addTunablePadding(view, ScreenDecorations.PADDING, this.mPadding, 3);
        }
    }

    /* loaded from: classes21.dex */
    public static class DisplayCutoutView extends View implements DisplayManager.DisplayListener, RegionInterceptingFrameLayout.RegionInterceptableView {
        private static final float HIDDEN_CAMERA_PROTECTION_SCALE = 0.5f;
        private final Path mBoundingPath;
        private final Rect mBoundingRect;
        private final List<Rect> mBounds;
        private ValueAnimator mCameraProtectionAnimator;
        private float mCameraProtectionProgress;
        private int mColor;
        private final ScreenDecorations mDecorations;
        private final DisplayInfo mInfo;
        private final boolean mInitialStart;
        private final int[] mLocation;
        private final Paint mPaint;
        private Path mProtectionPath;
        private Path mProtectionPathOrig;
        private RectF mProtectionRect;
        private RectF mProtectionRectOrig;
        private int mRotation;
        private boolean mShowProtection;
        private boolean mStart;
        private Rect mTotalBounds;
        private final Runnable mVisibilityChangedListener;

        public DisplayCutoutView(Context context, boolean start, Runnable visibilityChangedListener, ScreenDecorations decorations) {
            super(context);
            this.mInfo = new DisplayInfo();
            this.mPaint = new Paint();
            this.mBounds = new ArrayList();
            this.mBoundingRect = new Rect();
            this.mBoundingPath = new Path();
            this.mTotalBounds = new Rect();
            this.mShowProtection = false;
            this.mLocation = new int[2];
            this.mColor = -16777216;
            this.mCameraProtectionProgress = 0.5f;
            this.mInitialStart = start;
            this.mVisibilityChangedListener = visibilityChangedListener;
            this.mDecorations = decorations;
            setId(R.id.display_cutout);
        }

        private /* synthetic */ void lambda$new$0() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.mInitialStart ? "OverlayTop" : "OverlayBottom");
            sb.append(" drawn in rot ");
            sb.append(this.mRotation);
            Log.i(ScreenDecorations.TAG, sb.toString());
        }

        public void setColor(int color) {
            this.mColor = color;
            invalidate();
        }

        @Override // android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            ((DisplayManager) this.mContext.getSystemService(DisplayManager.class)).registerDisplayListener(this, getHandler());
            update();
        }

        @Override // android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            ((DisplayManager) this.mContext.getSystemService(DisplayManager.class)).unregisterDisplayListener(this);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            getLocationOnScreen(this.mLocation);
            int[] iArr = this.mLocation;
            canvas.translate(-iArr[0], -iArr[1]);
            if (!this.mBoundingPath.isEmpty()) {
                this.mPaint.setColor(this.mColor);
                this.mPaint.setStyle(Paint.Style.FILL);
                this.mPaint.setAntiAlias(true);
                canvas.drawPath(this.mBoundingPath, this.mPaint);
            }
            if (this.mCameraProtectionProgress > 0.5f && !this.mProtectionRect.isEmpty()) {
                float f = this.mCameraProtectionProgress;
                canvas.scale(f, f, this.mProtectionRect.centerX(), this.mProtectionRect.centerY());
                canvas.drawPath(this.mProtectionPath, this.mPaint);
            }
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayAdded(int displayId) {
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayRemoved(int displayId) {
        }

        @Override // android.hardware.display.DisplayManager.DisplayListener
        public void onDisplayChanged(int displayId) {
            if (displayId == getDisplay().getDisplayId()) {
                update();
            }
        }

        public void setRotation(int rotation) {
            this.mRotation = rotation;
            update();
        }

        void setProtection(Path protectionPath, Rect pathBounds) {
            if (this.mProtectionPathOrig == null) {
                this.mProtectionPathOrig = new Path();
                this.mProtectionPath = new Path();
            }
            this.mProtectionPathOrig.set(protectionPath);
            if (this.mProtectionRectOrig == null) {
                this.mProtectionRectOrig = new RectF();
                this.mProtectionRect = new RectF();
            }
            this.mProtectionRectOrig.set(pathBounds);
        }

        void setShowProtection(boolean shouldShow) {
            if (this.mShowProtection == shouldShow) {
                return;
            }
            this.mShowProtection = shouldShow;
            updateBoundingPath();
            if (this.mShowProtection) {
                requestLayout();
            }
            ValueAnimator valueAnimator = this.mCameraProtectionAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            float[] fArr = new float[2];
            fArr[0] = this.mCameraProtectionProgress;
            fArr[1] = this.mShowProtection ? 1.0f : 0.5f;
            this.mCameraProtectionAnimator = ValueAnimator.ofFloat(fArr).setDuration(750L);
            this.mCameraProtectionAnimator.setInterpolator(Interpolators.DECELERATE_QUINT);
            this.mCameraProtectionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.systemui.-$$Lambda$ScreenDecorations$DisplayCutoutView$f2Iwcv56BgbHyCi4FYuzR2s0HB4
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    ScreenDecorations.DisplayCutoutView.this.lambda$setShowProtection$1$ScreenDecorations$DisplayCutoutView(valueAnimator2);
                }
            });
            this.mCameraProtectionAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.android.systemui.ScreenDecorations.DisplayCutoutView.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    DisplayCutoutView.this.mCameraProtectionAnimator = null;
                    if (!DisplayCutoutView.this.mShowProtection) {
                        DisplayCutoutView.this.requestLayout();
                    }
                }
            });
            this.mCameraProtectionAnimator.start();
        }

        public /* synthetic */ void lambda$setShowProtection$1$ScreenDecorations$DisplayCutoutView(ValueAnimator animation) {
            this.mCameraProtectionProgress = ((Float) animation.getAnimatedValue()).floatValue();
            invalidate();
        }

        private boolean isStart() {
            int i = this.mRotation;
            boolean flipped = i == 2 || i == 3;
            return flipped ? !this.mInitialStart : this.mInitialStart;
        }

        private void update() {
            int newVisible;
            if (!isAttachedToWindow() || this.mDecorations.mPendingRotationChange) {
                return;
            }
            this.mStart = isStart();
            requestLayout();
            getDisplay().getDisplayInfo(this.mInfo);
            this.mBounds.clear();
            this.mBoundingRect.setEmpty();
            this.mBoundingPath.reset();
            if (ScreenDecorations.shouldDrawCutout(getContext()) && hasCutout()) {
                this.mBounds.addAll(this.mInfo.displayCutout.getBoundingRects());
                localBounds(this.mBoundingRect);
                updateGravity();
                updateBoundingPath();
                invalidate();
                newVisible = 0;
            } else {
                newVisible = 8;
            }
            if (newVisible != getVisibility()) {
                setVisibility(newVisible);
                this.mVisibilityChangedListener.run();
            }
        }

        private void updateBoundingPath() {
            int lw = this.mInfo.logicalWidth;
            int lh = this.mInfo.logicalHeight;
            boolean z = true;
            if (this.mInfo.rotation != 1 && this.mInfo.rotation != 3) {
                z = false;
            }
            boolean flipped = z;
            int dw = flipped ? lh : lw;
            int dh = flipped ? lw : lh;
            this.mBoundingPath.set(DisplayCutout.pathFromResources(getResources(), dw, dh));
            Matrix m = new Matrix();
            transformPhysicalToLogicalCoordinates(this.mInfo.rotation, dw, dh, m);
            this.mBoundingPath.transform(m);
            Path path = this.mProtectionPathOrig;
            if (path != null) {
                this.mProtectionPath.set(path);
                this.mProtectionPath.transform(m);
                m.mapRect(this.mProtectionRect, this.mProtectionRectOrig);
            }
        }

        private static void transformPhysicalToLogicalCoordinates(int rotation, int physicalWidth, int physicalHeight, Matrix out) {
            if (rotation == 0) {
                out.reset();
            } else if (rotation == 1) {
                out.setRotate(270.0f);
                out.postTranslate(0.0f, physicalWidth);
            } else if (rotation == 2) {
                out.setRotate(180.0f);
                out.postTranslate(physicalWidth, physicalHeight);
            } else if (rotation == 3) {
                out.setRotate(90.0f);
                out.postTranslate(physicalHeight, 0.0f);
            } else {
                throw new IllegalArgumentException("Unknown rotation: " + rotation);
            }
        }

        private void updateGravity() {
            ViewGroup.LayoutParams lp = getLayoutParams();
            if (lp instanceof FrameLayout.LayoutParams) {
                FrameLayout.LayoutParams flp = (FrameLayout.LayoutParams) lp;
                int newGravity = getGravity(this.mInfo.displayCutout);
                if (flp.gravity != newGravity) {
                    flp.gravity = newGravity;
                    setLayoutParams(flp);
                }
            }
        }

        private boolean hasCutout() {
            DisplayCutout displayCutout = this.mInfo.displayCutout;
            if (displayCutout == null) {
                return false;
            }
            return this.mStart ? displayCutout.getSafeInsetLeft() > 0 || displayCutout.getSafeInsetTop() > 0 : displayCutout.getSafeInsetRight() > 0 || displayCutout.getSafeInsetBottom() > 0;
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            if (this.mBounds.isEmpty()) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            } else if (this.mShowProtection) {
                this.mTotalBounds.union(this.mBoundingRect);
                this.mTotalBounds.union((int) this.mProtectionRect.left, (int) this.mProtectionRect.top, (int) this.mProtectionRect.right, (int) this.mProtectionRect.bottom);
                setMeasuredDimension(resolveSizeAndState(this.mTotalBounds.width(), widthMeasureSpec, 0), resolveSizeAndState(this.mTotalBounds.height(), heightMeasureSpec, 0));
            } else {
                setMeasuredDimension(resolveSizeAndState(this.mBoundingRect.width(), widthMeasureSpec, 0), resolveSizeAndState(this.mBoundingRect.height(), heightMeasureSpec, 0));
            }
        }

        public static void boundsFromDirection(DisplayCutout displayCutout, int gravity, Rect out) {
            if (gravity == 3) {
                out.set(displayCutout.getBoundingRectLeft());
            } else if (gravity == 5) {
                out.set(displayCutout.getBoundingRectRight());
            } else if (gravity == 48) {
                out.set(displayCutout.getBoundingRectTop());
            } else if (gravity == 80) {
                out.set(displayCutout.getBoundingRectBottom());
            } else {
                out.setEmpty();
            }
        }

        private void localBounds(Rect out) {
            DisplayCutout displayCutout = this.mInfo.displayCutout;
            boundsFromDirection(displayCutout, getGravity(displayCutout), out);
        }

        private int getGravity(DisplayCutout displayCutout) {
            if (this.mStart) {
                if (displayCutout.getSafeInsetLeft() > 0) {
                    return 3;
                }
                if (displayCutout.getSafeInsetTop() > 0) {
                    return 48;
                }
                return 0;
            } else if (displayCutout.getSafeInsetRight() > 0) {
                return 5;
            } else {
                if (displayCutout.getSafeInsetBottom() > 0) {
                    return 80;
                }
                return 0;
            }
        }

        @Override // com.android.systemui.RegionInterceptingFrameLayout.RegionInterceptableView
        public boolean shouldInterceptTouch() {
            return this.mInfo.displayCutout != null && getVisibility() == 0;
        }

        @Override // com.android.systemui.RegionInterceptingFrameLayout.RegionInterceptableView
        public Region getInterceptRegion() {
            if (this.mInfo.displayCutout == null) {
                return null;
            }
            View rootView = getRootView();
            Region cutoutBounds = ScreenDecorations.rectsToRegion(this.mInfo.displayCutout.getBoundingRects());
            rootView.getLocationOnScreen(this.mLocation);
            int[] iArr = this.mLocation;
            cutoutBounds.translate(-iArr[0], -iArr[1]);
            cutoutBounds.op(rootView.getLeft(), rootView.getTop(), rootView.getRight(), rootView.getBottom(), Region.Op.INTERSECT);
            return cutoutBounds;
        }
    }

    private boolean isLandscape(int rotation) {
        return rotation == 1 || rotation == 2;
    }

    /* loaded from: classes21.dex */
    private class RestartingPreDrawListener implements ViewTreeObserver.OnPreDrawListener {
        private final int mTargetRotation;
        private final View mView;

        private RestartingPreDrawListener(View view, int targetRotation) {
            this.mView = view;
            this.mTargetRotation = targetRotation;
        }

        @Override // android.view.ViewTreeObserver.OnPreDrawListener
        public boolean onPreDraw() {
            this.mView.getViewTreeObserver().removeOnPreDrawListener(this);
            if (this.mTargetRotation != ScreenDecorations.this.mRotation) {
                ScreenDecorations.this.mPendingRotationChange = false;
                ScreenDecorations.this.updateOrientation();
                this.mView.invalidate();
                return false;
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes21.dex */
    public class ValidatingPreDrawListener implements ViewTreeObserver.OnPreDrawListener {
        private final View mView;

        public ValidatingPreDrawListener(View view) {
            this.mView = view;
        }

        @Override // android.view.ViewTreeObserver.OnPreDrawListener
        public boolean onPreDraw() {
            int displayRotation = RotationUtils.getExactRotation(ScreenDecorations.this.mContext);
            if (displayRotation != ScreenDecorations.this.mRotation && !ScreenDecorations.this.mPendingRotationChange) {
                this.mView.invalidate();
                return false;
            }
            return true;
        }
    }
}
