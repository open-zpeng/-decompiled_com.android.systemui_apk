package com.xiaopeng.systemui.infoflow.effect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
/* loaded from: classes24.dex */
public abstract class ThreadSurfaceView extends SurfaceView {
    private static final String TAG = "ThreadSurfaceView";
    private final FpsMonitor mFpsMonitor;
    private Thread mRenderThread;
    private boolean mRenderWait;
    private final Object mRenderWaitLock;
    private boolean mRunning;
    public boolean showDebugInfo;

    protected abstract void onSurfaceDraw(Canvas canvas);

    public ThreadSurfaceView(Context context) {
        this(context, null);
    }

    public ThreadSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThreadSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mFpsMonitor = new FpsMonitor(TAG);
        this.mRenderWaitLock = new Object();
        this.showDebugInfo = false;
        this.mRunning = false;
        this.mRenderWait = false;
        init();
    }

    private void log(Object obj) {
        Log.d(TAG, String.valueOf(obj));
    }

    private Thread createRenderThread() {
        return new Thread() { // from class: com.xiaopeng.systemui.infoflow.effect.ThreadSurfaceView.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                Canvas canvas;
                super.run();
                Paint paint = new Paint();
                paint.setColor(-1);
                paint.setTextSize(32.0f);
                SurfaceHolder holder = ThreadSurfaceView.this.getHolder();
                holder.setFormat(-2);
                while (ThreadSurfaceView.this.mRunning) {
                    if (ThreadSurfaceView.this.mRenderWait) {
                        synchronized (ThreadSurfaceView.this.mRenderWaitLock) {
                            try {
                                ThreadSurfaceView.this.mRenderWaitLock.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (holder.getSurface().isValid()) {
                        ThreadSurfaceView.this.mFpsMonitor.frameStart();
                        if (Build.VERSION.SDK_INT > 26) {
                            canvas = holder.lockHardwareCanvas();
                        } else {
                            Canvas canvas2 = holder.lockCanvas();
                            canvas = canvas2;
                        }
                        if (canvas == null) {
                            ThreadSurfaceView.this.mFpsMonitor.frameEnd();
                        } else {
                            ThreadSurfaceView.this.performSurfaceDraw(canvas);
                            String fpsInfo = ThreadSurfaceView.this.mFpsMonitor.frameEnd();
                            if (ThreadSurfaceView.this.showDebugInfo) {
                                canvas.drawText(fpsInfo, 0, fpsInfo.length(), 20.0f, 40.0f, paint);
                                String ha = "HA: " + canvas.isHardwareAccelerated();
                                Canvas canvas3 = canvas;
                                canvas3.drawText(ha, 0, ha.length(), 20.0f, 80.0f, paint);
                                canvas3.drawText("SurfaceView", 0, "SurfaceView".length(), 20.0f, 120.0f, paint);
                            }
                            holder.unlockCanvasAndPost(canvas);
                        }
                    }
                }
            }
        };
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

    protected void refreshRender() {
    }

    @Override // android.view.SurfaceView, android.view.View
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility != 0) {
            pauseRender();
        }
    }

    protected void onSurfaceDrawAfter(Canvas canvas) {
    }

    private void init() {
    }

    public boolean isRunning() {
        return this.mRunning && !this.mRenderWait;
    }

    protected void startRender() {
        this.mRunning = true;
        this.mRenderThread = createRenderThread();
        this.mRenderThread.start();
    }

    protected void resumeRender() {
        this.mRenderWait = false;
        synchronized (this.mRenderWaitLock) {
            this.mRenderWaitLock.notifyAll();
        }
    }

    public boolean isRendering() {
        return !this.mRenderWait;
    }

    protected void pauseRender() {
        this.mRenderWait = true;
    }

    protected void stopRender() {
        this.mRunning = false;
        try {
            this.mRenderThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.mRenderThread = null;
    }
}
