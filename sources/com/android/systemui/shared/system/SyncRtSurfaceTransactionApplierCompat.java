package com.android.systemui.shared.system;

import android.graphics.HardwareRenderer;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.os.Trace;
import android.view.Surface;
import android.view.View;
import android.view.ViewRootImpl;
import java.util.function.Consumer;
/* loaded from: classes21.dex */
public class SyncRtSurfaceTransactionApplierCompat {
    private static final int MSG_UPDATE_SEQUENCE_NUMBER = 0;
    private Runnable mAfterApplyCallback;
    private final Handler mApplyHandler;
    private final Surface mTargetSurface;
    private final ViewRootImpl mTargetViewRootImpl;
    private int mSequenceNumber = 0;
    private int mPendingSequenceNumber = 0;

    public SyncRtSurfaceTransactionApplierCompat(View targetView) {
        this.mTargetViewRootImpl = targetView != null ? targetView.getViewRootImpl() : null;
        ViewRootImpl viewRootImpl = this.mTargetViewRootImpl;
        this.mTargetSurface = viewRootImpl != null ? viewRootImpl.mSurface : null;
        this.mApplyHandler = new Handler(new Handler.Callback() { // from class: com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat.1
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message msg) {
                if (msg.what == 0) {
                    SyncRtSurfaceTransactionApplierCompat.this.onApplyMessage(msg.arg1);
                    return true;
                }
                return false;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onApplyMessage(int seqNo) {
        this.mSequenceNumber = seqNo;
        if (this.mSequenceNumber == this.mPendingSequenceNumber && this.mAfterApplyCallback != null) {
            Runnable r = this.mAfterApplyCallback;
            this.mAfterApplyCallback = null;
            r.run();
        }
    }

    public void scheduleApply(final SurfaceParams... params) {
        ViewRootImpl viewRootImpl = this.mTargetViewRootImpl;
        if (viewRootImpl == null || viewRootImpl.getView() == null) {
            return;
        }
        this.mPendingSequenceNumber++;
        final int toApplySeqNo = this.mPendingSequenceNumber;
        this.mTargetViewRootImpl.registerRtFrameCallback(new HardwareRenderer.FrameDrawingCallback() { // from class: com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat.2
            public void onFrameDraw(long frame) {
                if (SyncRtSurfaceTransactionApplierCompat.this.mTargetSurface == null || !SyncRtSurfaceTransactionApplierCompat.this.mTargetSurface.isValid()) {
                    Message.obtain(SyncRtSurfaceTransactionApplierCompat.this.mApplyHandler, 0, toApplySeqNo, 0).sendToTarget();
                    return;
                }
                Trace.traceBegin(8L, "Sync transaction frameNumber=" + frame);
                TransactionCompat t = new TransactionCompat();
                for (int i = params.length + (-1); i >= 0; i--) {
                    SurfaceParams surfaceParams = params[i];
                    SurfaceControlCompat surface = surfaceParams.surface;
                    t.deferTransactionUntil(surface, SyncRtSurfaceTransactionApplierCompat.this.mTargetSurface, frame);
                    SyncRtSurfaceTransactionApplierCompat.applyParams(t, surfaceParams);
                }
                t.setEarlyWakeup();
                t.apply();
                Trace.traceEnd(8L);
                Message.obtain(SyncRtSurfaceTransactionApplierCompat.this.mApplyHandler, 0, toApplySeqNo, 0).sendToTarget();
            }
        });
        this.mTargetViewRootImpl.getView().invalidate();
    }

    public void addAfterApplyCallback(final Runnable afterApplyCallback) {
        if (this.mSequenceNumber == this.mPendingSequenceNumber) {
            afterApplyCallback.run();
        } else if (this.mAfterApplyCallback == null) {
            this.mAfterApplyCallback = afterApplyCallback;
        } else {
            final Runnable oldCallback = this.mAfterApplyCallback;
            this.mAfterApplyCallback = new Runnable() { // from class: com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat.3
                @Override // java.lang.Runnable
                public void run() {
                    afterApplyCallback.run();
                    oldCallback.run();
                }
            };
        }
    }

    public static void applyParams(TransactionCompat t, SurfaceParams params) {
        t.setMatrix(params.surface, params.matrix);
        t.setWindowCrop(params.surface, params.windowCrop);
        t.setAlpha(params.surface, params.alpha);
        t.setLayer(params.surface, params.layer);
        t.setCornerRadius(params.surface, params.cornerRadius);
        t.show(params.surface);
    }

    public static void create(final View targetView, final Consumer<SyncRtSurfaceTransactionApplierCompat> callback) {
        if (targetView == null) {
            callback.accept(null);
        } else if (targetView.getViewRootImpl() != null) {
            callback.accept(new SyncRtSurfaceTransactionApplierCompat(targetView));
        } else {
            targetView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() { // from class: com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat.4
                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewAttachedToWindow(View v) {
                    targetView.removeOnAttachStateChangeListener(this);
                    callback.accept(new SyncRtSurfaceTransactionApplierCompat(targetView));
                }

                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewDetachedFromWindow(View v) {
                }
            });
        }
    }

    /* loaded from: classes21.dex */
    public static class SurfaceParams {
        public final float alpha;
        final float cornerRadius;
        public final int layer;
        public final Matrix matrix;
        public final SurfaceControlCompat surface;
        public final Rect windowCrop;

        public SurfaceParams(SurfaceControlCompat surface, float alpha, Matrix matrix, Rect windowCrop, int layer, float cornerRadius) {
            this.surface = surface;
            this.alpha = alpha;
            this.matrix = new Matrix(matrix);
            this.windowCrop = new Rect(windowCrop);
            this.layer = layer;
            this.cornerRadius = cornerRadius;
        }
    }
}
