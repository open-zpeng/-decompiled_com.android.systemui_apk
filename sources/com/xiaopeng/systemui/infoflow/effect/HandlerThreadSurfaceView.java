package com.xiaopeng.systemui.infoflow.effect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
/* loaded from: classes24.dex */
public abstract class HandlerThreadSurfaceView extends SurfaceView {
    private static final String TAG = "ThreadSurfaceView";
    private Paint mDebugPaint;
    private final FpsMonitor mFpsMonitor;
    private Handler mRenderHandler;
    private HandlerThread mRenderHandlerThread;
    private Runnable mRenderRunnable;
    private boolean mRunning;
    public boolean showDebugInfo;

    protected abstract void onSurfaceDraw(Canvas canvas);

    public HandlerThreadSurfaceView(Context context) {
        this(context, null);
    }

    public HandlerThreadSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HandlerThreadSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mFpsMonitor = new FpsMonitor(TAG);
        this.showDebugInfo = false;
        this.mRunning = false;
        this.mRenderRunnable = new Runnable() { // from class: com.xiaopeng.systemui.infoflow.effect.HandlerThreadSurfaceView.1
            @Override // java.lang.Runnable
            public void run() {
                Canvas canvas;
                SurfaceHolder holder = HandlerThreadSurfaceView.this.getHolder();
                if (holder.getSurface().isValid()) {
                    HandlerThreadSurfaceView.this.mFpsMonitor.frameStart();
                    if (Build.VERSION.SDK_INT > 26) {
                        canvas = holder.lockHardwareCanvas();
                    } else {
                        canvas = holder.lockCanvas();
                    }
                    if (canvas == null) {
                        HandlerThreadSurfaceView.this.mFpsMonitor.frameEnd();
                        return;
                    }
                    HandlerThreadSurfaceView.this.performSurfaceDraw(canvas);
                    String fpsInfo = HandlerThreadSurfaceView.this.mFpsMonitor.frameEnd();
                    if (HandlerThreadSurfaceView.this.showDebugInfo) {
                        if (HandlerThreadSurfaceView.this.mDebugPaint == null) {
                            HandlerThreadSurfaceView.this.mDebugPaint = new Paint();
                            HandlerThreadSurfaceView.this.mDebugPaint.setColor(-1);
                            HandlerThreadSurfaceView.this.mDebugPaint.setTextSize(32.0f);
                        }
                        canvas.drawText(fpsInfo, 0, fpsInfo.length(), 20.0f, 40.0f, HandlerThreadSurfaceView.this.mDebugPaint);
                        String ha = "HA: " + canvas.isHardwareAccelerated();
                        canvas.drawText(ha, 0, ha.length(), 20.0f, 80.0f, HandlerThreadSurfaceView.this.mDebugPaint);
                        canvas.drawText("SurfaceView", 0, "SurfaceView".length(), 20.0f, 120.0f, HandlerThreadSurfaceView.this.mDebugPaint);
                    }
                    holder.unlockCanvasAndPost(canvas);
                }
                if (HandlerThreadSurfaceView.this.mRunning) {
                    HandlerThreadSurfaceView.this.mRenderHandler.post(this);
                }
            }
        };
        init();
    }

    private void log(Object obj) {
        Log.d(TAG, String.valueOf(obj));
    }

    protected int getCurrentFps() {
        return this.mFpsMonitor.getCurrentFps();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performSurfaceDraw(Canvas canvas) {
        onSurfaceDrawBefore(canvas);
        onSurfaceDraw(canvas);
        onSurfaceDrawAfter(canvas);
    }

    protected void onSurfaceDrawBefore(Canvas canvas) {
        clearDraw(canvas);
    }

    private void clearDraw(Canvas canvas) {
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
    }

    protected void runInRenderThreadDelayed(Runnable runnable, long delay) {
        Handler handler = this.mRenderHandler;
        if (handler != null) {
            handler.postDelayed(runnable, delay);
        }
    }

    protected void runInRenderThread(Runnable runnable) {
        Handler handler = this.mRenderHandler;
        if (handler != null) {
            handler.post(runnable);
        }
    }

    @Override // android.view.SurfaceView, android.view.View
    public void setVisibility(int visibility) {
        Handler handler;
        super.setVisibility(visibility);
        if (visibility == 0 && (handler = this.mRenderHandler) != null) {
            handler.post(this.mRenderRunnable);
        }
    }

    protected void onSurfaceDrawAfter(Canvas canvas) {
    }

    private void init() {
        setZOrderOnTop(true);
        getHolder().setFormat(-2);
    }

    public boolean isRunning() {
        return this.mRunning;
    }

    protected void startRender() {
        if (this.mRunning) {
            return;
        }
        this.mRunning = true;
        this.mRenderHandlerThread = new HandlerThread("HandlerThreadSurfaceView");
        this.mRenderHandlerThread.start();
        this.mRenderHandler = new Handler(this.mRenderHandlerThread.getLooper());
        this.mRenderHandler.post(this.mRenderRunnable);
    }

    protected void refreshRender() {
        Handler handler = this.mRenderHandler;
        if (handler != null) {
            handler.post(this.mRenderRunnable);
        }
    }

    protected void resumeRender() {
        if (this.mRunning) {
            return;
        }
        this.mRunning = true;
        this.mRenderHandler.post(this.mRenderRunnable);
    }

    public boolean isRendering() {
        return this.mRunning;
    }

    protected void pauseRender() {
        this.mRunning = false;
    }

    protected void stopRender() {
        if (!this.mRunning) {
            return;
        }
        this.mRunning = false;
        this.mRenderHandlerThread.quit();
        this.mRenderHandlerThread = null;
        this.mRenderHandler.removeCallbacksAndMessages(null);
        this.mRenderHandler = null;
    }
}
