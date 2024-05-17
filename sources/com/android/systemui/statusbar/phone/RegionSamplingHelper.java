package com.android.systemui.statusbar.phone;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Handler;
import android.view.CompositionSamplingListener;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewRootImpl;
import android.view.ViewTreeObserver;
import com.android.systemui.R;
/* loaded from: classes21.dex */
public class RegionSamplingHelper implements View.OnAttachStateChangeListener, View.OnLayoutChangeListener {
    private final SamplingCallback mCallback;
    private float mCurrentMedianLuma;
    private boolean mFirstSamplingAfterStart;
    private float mLastMedianLuma;
    private final float mLuminanceChangeThreshold;
    private final float mLuminanceThreshold;
    private final View mSampledView;
    private final CompositionSamplingListener mSamplingListener;
    private boolean mWaitingOnDraw;
    private final Handler mHandler = new Handler();
    private final Runnable mUpdateSamplingListener = new Runnable() { // from class: com.android.systemui.statusbar.phone.-$$Lambda$RegionSamplingHelper$pSsfVRzQ1H9WUV0xYw9vQmpq4Cw
        @Override // java.lang.Runnable
        public final void run() {
            RegionSamplingHelper.this.updateSamplingListener();
        }
    };
    private final Rect mSamplingRequestBounds = new Rect();
    private final Rect mRegisteredSamplingBounds = new Rect();
    private boolean mSamplingEnabled = false;
    private boolean mSamplingListenerRegistered = false;
    private SurfaceControl mRegisteredStopLayer = null;
    private ViewTreeObserver.OnDrawListener mUpdateOnDraw = new ViewTreeObserver.OnDrawListener() { // from class: com.android.systemui.statusbar.phone.RegionSamplingHelper.1
        @Override // android.view.ViewTreeObserver.OnDrawListener
        public void onDraw() {
            RegionSamplingHelper.this.mHandler.post(RegionSamplingHelper.this.mRemoveDrawRunnable);
            RegionSamplingHelper.this.onDraw();
        }
    };
    private Runnable mRemoveDrawRunnable = new Runnable() { // from class: com.android.systemui.statusbar.phone.RegionSamplingHelper.2
        @Override // java.lang.Runnable
        public void run() {
            RegionSamplingHelper.this.mSampledView.getViewTreeObserver().removeOnDrawListener(RegionSamplingHelper.this.mUpdateOnDraw);
        }
    };

    public RegionSamplingHelper(View sampledView, SamplingCallback samplingCallback) {
        this.mSamplingListener = new CompositionSamplingListener(sampledView.getContext().getMainExecutor()) { // from class: com.android.systemui.statusbar.phone.RegionSamplingHelper.3
            public void onSampleCollected(float medianLuma) {
                if (RegionSamplingHelper.this.mSamplingEnabled) {
                    RegionSamplingHelper.this.updateMediaLuma(medianLuma);
                }
            }
        };
        this.mSampledView = sampledView;
        this.mSampledView.addOnAttachStateChangeListener(this);
        this.mSampledView.addOnLayoutChangeListener(this);
        Resources res = sampledView.getResources();
        this.mLuminanceThreshold = res.getFloat(R.dimen.navigation_luminance_threshold);
        this.mLuminanceChangeThreshold = res.getFloat(R.dimen.navigation_luminance_change_threshold);
        this.mCallback = samplingCallback;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDraw() {
        if (this.mWaitingOnDraw) {
            this.mWaitingOnDraw = false;
            updateSamplingListener();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void start(Rect initialSamplingBounds) {
        if (!this.mCallback.isSamplingEnabled()) {
            return;
        }
        if (initialSamplingBounds != null) {
            this.mSamplingRequestBounds.set(initialSamplingBounds);
        }
        this.mSamplingEnabled = true;
        this.mLastMedianLuma = -1.0f;
        this.mFirstSamplingAfterStart = true;
        updateSamplingListener();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void stop() {
        this.mSamplingEnabled = false;
        updateSamplingListener();
    }

    void stopAndDestroy() {
        stop();
        this.mSamplingListener.destroy();
    }

    @Override // android.view.View.OnAttachStateChangeListener
    public void onViewAttachedToWindow(View view) {
        updateSamplingListener();
    }

    @Override // android.view.View.OnAttachStateChangeListener
    public void onViewDetachedFromWindow(View view) {
        stopAndDestroy();
    }

    @Override // android.view.View.OnLayoutChangeListener
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        updateSamplingRect();
    }

    private void postUpdateSamplingListener() {
        this.mHandler.removeCallbacks(this.mUpdateSamplingListener);
        this.mHandler.post(this.mUpdateSamplingListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSamplingListener() {
        boolean isSamplingEnabled = this.mSamplingEnabled && !this.mSamplingRequestBounds.isEmpty() && (this.mSampledView.isAttachedToWindow() || this.mFirstSamplingAfterStart);
        if (isSamplingEnabled) {
            ViewRootImpl viewRootImpl = this.mSampledView.getViewRootImpl();
            SurfaceControl stopLayerControl = null;
            if (viewRootImpl != null) {
                stopLayerControl = viewRootImpl.getSurfaceControl();
            }
            if (stopLayerControl == null || !stopLayerControl.isValid()) {
                if (!this.mWaitingOnDraw) {
                    this.mWaitingOnDraw = true;
                    if (this.mHandler.hasCallbacks(this.mRemoveDrawRunnable)) {
                        this.mHandler.removeCallbacks(this.mRemoveDrawRunnable);
                    } else {
                        this.mSampledView.getViewTreeObserver().addOnDrawListener(this.mUpdateOnDraw);
                    }
                }
                stopLayerControl = null;
            }
            if (!this.mSamplingRequestBounds.equals(this.mRegisteredSamplingBounds) || this.mRegisteredStopLayer != stopLayerControl) {
                unregisterSamplingListener();
                this.mSamplingListenerRegistered = true;
                CompositionSamplingListener.register(this.mSamplingListener, 0, stopLayerControl != null ? stopLayerControl.getHandle() : null, this.mSamplingRequestBounds);
                this.mRegisteredSamplingBounds.set(this.mSamplingRequestBounds);
                this.mRegisteredStopLayer = stopLayerControl;
            }
            this.mFirstSamplingAfterStart = false;
            return;
        }
        unregisterSamplingListener();
    }

    private void unregisterSamplingListener() {
        if (this.mSamplingListenerRegistered) {
            this.mSamplingListenerRegistered = false;
            this.mRegisteredStopLayer = null;
            this.mRegisteredSamplingBounds.setEmpty();
            CompositionSamplingListener.unregister(this.mSamplingListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMediaLuma(float medianLuma) {
        this.mCurrentMedianLuma = medianLuma;
        if (Math.abs(this.mCurrentMedianLuma - this.mLastMedianLuma) > this.mLuminanceChangeThreshold) {
            this.mCallback.onRegionDarknessChanged(medianLuma < this.mLuminanceThreshold);
            this.mLastMedianLuma = medianLuma;
        }
    }

    public void updateSamplingRect() {
        Rect sampledRegion = this.mCallback.getSampledRegion(this.mSampledView);
        if (!this.mSamplingRequestBounds.equals(sampledRegion)) {
            this.mSamplingRequestBounds.set(sampledRegion);
            updateSamplingListener();
        }
    }

    /* loaded from: classes21.dex */
    public interface SamplingCallback {
        Rect getSampledRegion(View view);

        void onRegionDarknessChanged(boolean z);

        default boolean isSamplingEnabled() {
            return true;
        }
    }
}
