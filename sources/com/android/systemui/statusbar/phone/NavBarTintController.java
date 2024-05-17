package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.view.CompositionSamplingListener;
import android.view.View;
import com.android.systemui.R;
import com.android.systemui.shared.system.QuickStepContract;
import com.xiaopeng.speech.protocol.event.OOBEEvent;
import java.io.PrintWriter;
/* loaded from: classes21.dex */
public class NavBarTintController implements View.OnAttachStateChangeListener, View.OnLayoutChangeListener {
    public static final int DEFAULT_COLOR_ADAPT_TRANSITION_TIME = 1700;
    public static final int MIN_COLOR_ADAPT_TRANSITION_TIME = 400;
    private float mCurrentMedianLuma;
    private float mLastMedianLuma;
    private final LightBarTransitionsController mLightBarController;
    private final float mLuminanceChangeThreshold;
    private final float mLuminanceThreshold;
    private final int mNavBarHeight;
    private final int mNavColorSampleMargin;
    private final NavigationBarView mNavigationBarView;
    private final CompositionSamplingListener mSamplingListener;
    private boolean mUpdateOnNextDraw;
    private boolean mWindowVisible;
    private final Handler mHandler = new Handler();
    private int mNavBarMode = 0;
    private final Runnable mUpdateSamplingListener = new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$NavBarTintController$2EUeUMrCltge35I4yojwTXFosWM
        @Override // java.lang.Runnable
        public final void run() {
            NavBarTintController.this.updateSamplingListener();
        }
    };
    private final Rect mSamplingBounds = new Rect();
    private boolean mSamplingEnabled = false;
    private boolean mSamplingListenerRegistered = false;

    public NavBarTintController(NavigationBarView navigationBarView, LightBarTransitionsController lightBarController) {
        this.mSamplingListener = new CompositionSamplingListener(navigationBarView.getContext().getMainExecutor()) { // from class: com.android.systemui.statusbar.phone.NavBarTintController.1
            public void onSampleCollected(float medianLuma) {
                NavBarTintController.this.updateTint(medianLuma);
            }
        };
        this.mNavigationBarView = navigationBarView;
        this.mNavigationBarView.addOnAttachStateChangeListener(this);
        this.mNavigationBarView.addOnLayoutChangeListener(this);
        this.mLightBarController = lightBarController;
        Resources res = navigationBarView.getResources();
        this.mNavBarHeight = res.getDimensionPixelSize(R.dimen.navigation_bar_height);
        this.mNavColorSampleMargin = res.getDimensionPixelSize(R.dimen.navigation_handle_sample_horizontal_margin);
        this.mLuminanceThreshold = res.getFloat(R.dimen.navigation_luminance_threshold);
        this.mLuminanceChangeThreshold = res.getFloat(R.dimen.navigation_luminance_change_threshold);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onDraw() {
        if (this.mUpdateOnNextDraw) {
            this.mUpdateOnNextDraw = false;
            requestUpdateSamplingListener();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void start() {
        if (!isEnabled(this.mNavigationBarView.getContext(), this.mNavBarMode)) {
            return;
        }
        this.mSamplingEnabled = true;
        requestUpdateSamplingListener();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void stop() {
        this.mSamplingEnabled = false;
        requestUpdateSamplingListener();
    }

    void stopAndDestroy() {
        stop();
        this.mSamplingListener.destroy();
    }

    @Override // android.view.View.OnAttachStateChangeListener
    public void onViewAttachedToWindow(View view) {
        requestUpdateSamplingListener();
    }

    @Override // android.view.View.OnAttachStateChangeListener
    public void onViewDetachedFromWindow(View view) {
        stopAndDestroy();
    }

    @Override // android.view.View.OnLayoutChangeListener
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        this.mSamplingBounds.setEmpty();
        View view = this.mNavigationBarView.getHomeHandle().getCurrentView();
        if (view != null) {
            int[] pos = new int[2];
            view.getLocationOnScreen(pos);
            Point displaySize = new Point();
            view.getContext().getDisplay().getRealSize(displaySize);
            Rect samplingBounds = new Rect(pos[0] - this.mNavColorSampleMargin, displaySize.y - this.mNavBarHeight, pos[0] + view.getWidth() + this.mNavColorSampleMargin, displaySize.y);
            if (!samplingBounds.equals(this.mSamplingBounds)) {
                this.mSamplingBounds.set(samplingBounds);
                requestUpdateSamplingListener();
            }
        }
    }

    private void requestUpdateSamplingListener() {
        this.mHandler.removeCallbacks(this.mUpdateSamplingListener);
        this.mHandler.post(this.mUpdateSamplingListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSamplingListener() {
        if (this.mSamplingListenerRegistered) {
            this.mSamplingListenerRegistered = false;
            CompositionSamplingListener.unregister(this.mSamplingListener);
        }
        if (this.mSamplingEnabled && this.mWindowVisible && !this.mSamplingBounds.isEmpty() && this.mNavigationBarView.isAttachedToWindow()) {
            if (!this.mNavigationBarView.getViewRootImpl().getSurfaceControl().isValid()) {
                this.mUpdateOnNextDraw = true;
                return;
            }
            this.mSamplingListenerRegistered = true;
            CompositionSamplingListener.register(this.mSamplingListener, 0, this.mNavigationBarView.getViewRootImpl().getSurfaceControl().getHandle(), this.mSamplingBounds);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTint(float medianLuma) {
        this.mLastMedianLuma = medianLuma;
        if (Math.abs(this.mCurrentMedianLuma - this.mLastMedianLuma) > this.mLuminanceChangeThreshold) {
            if (medianLuma > this.mLuminanceThreshold) {
                this.mLightBarController.setIconsDark(true, true);
            } else {
                this.mLightBarController.setIconsDark(false, true);
            }
            this.mCurrentMedianLuma = medianLuma;
        }
    }

    public void setWindowVisible(boolean visible) {
        this.mWindowVisible = visible;
        requestUpdateSamplingListener();
    }

    public void onNavigationModeChanged(int mode) {
        this.mNavBarMode = mode;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dump(PrintWriter pw) {
        Object obj;
        pw.println("NavBarTintController:");
        pw.println("  navBar isAttached: " + this.mNavigationBarView.isAttachedToWindow());
        StringBuilder sb = new StringBuilder();
        sb.append("  navBar isScValid: ");
        if (this.mNavigationBarView.isAttachedToWindow()) {
            obj = Boolean.valueOf(this.mNavigationBarView.getViewRootImpl().getSurfaceControl().isValid());
        } else {
            obj = OOBEEvent.STRING_FALSE;
        }
        sb.append(obj);
        pw.println(sb.toString());
        pw.println("  mSamplingListenerRegistered: " + this.mSamplingListenerRegistered);
        pw.println("  mSamplingBounds: " + this.mSamplingBounds);
        pw.println("  mLastMedianLuma: " + this.mLastMedianLuma);
        pw.println("  mCurrentMedianLuma: " + this.mCurrentMedianLuma);
        pw.println("  mWindowVisible: " + this.mWindowVisible);
    }

    public static boolean isEnabled(Context context, int navBarMode) {
        return context.getDisplayId() == 0 && QuickStepContract.isGesturalMode(navBarMode);
    }
}
