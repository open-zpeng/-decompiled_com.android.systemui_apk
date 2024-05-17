package com.xiaopeng.systemui.infoflow.effect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
/* loaded from: classes24.dex */
public abstract class ThreadView extends View {
    private static final String TAG = "ThreadView";
    private Paint debugPaint;
    private final FpsMonitor fpsMonitor;
    private boolean isRunning;
    public boolean showDebugInfo;

    protected abstract void onSurfaceDraw(Canvas canvas);

    public ThreadView(Context context) {
        this(context, null);
    }

    public ThreadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ThreadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.fpsMonitor = new FpsMonitor();
        this.showDebugInfo = false;
        this.isRunning = false;
        this.debugPaint = new Paint();
        init();
    }

    protected int getCurrentFps() {
        return this.fpsMonitor.getCurrentFps();
    }

    private void performSurfaceDraw(Canvas canvas) {
        onSurfaceDrawBefore(canvas);
        onSurfaceDraw(canvas);
        onSurfaceDrawAfter(canvas);
    }

    protected void onSurfaceDrawBefore(Canvas canvas) {
        clearDraw(canvas);
    }

    private void clearDraw(Canvas canvas) {
    }

    protected void onSurfaceDrawAfter(Canvas canvas) {
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.fpsMonitor.frameStart();
        performSurfaceDraw(canvas);
        String fpsInfo = this.fpsMonitor.frameEnd();
        if (this.showDebugInfo) {
            canvas.drawText(fpsInfo, 0, fpsInfo.length(), 20.0f, 40.0f, this.debugPaint);
            String ha = "HA: " + canvas.isHardwareAccelerated();
            canvas.drawText(ha, 0, ha.length(), 20.0f, 80.0f, this.debugPaint);
            canvas.drawText("View", 0, "View".length(), 20.0f, 120.0f, this.debugPaint);
        }
        if (this.isRunning) {
            invalidate();
        }
    }

    private void init() {
        if (Build.VERSION.SDK_INT > 26) {
            setLayerType(2, null);
        } else {
            setLayerType(1, null);
        }
        this.debugPaint.setColor(-1);
        this.debugPaint.setTextSize(32.0f);
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void startRender() {
        this.isRunning = true;
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void refreshRender() {
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void stopRender() {
        this.isRunning = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void resumeRender() {
        startRender();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void pauseRender() {
        stopRender();
    }
}
