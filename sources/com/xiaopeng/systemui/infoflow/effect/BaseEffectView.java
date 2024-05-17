package com.xiaopeng.systemui.infoflow.effect;

import android.content.Context;
import android.util.AttributeSet;
/* loaded from: classes24.dex */
public abstract class BaseEffectView extends ThreadView {
    public BaseEffectView(Context context) {
        super(context);
    }

    public BaseEffectView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseEffectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void start() {
        if (!isRunning()) {
            startRender();
        }
    }

    public void stop() {
        if (isRunning()) {
            stopRender();
        }
    }

    protected void postInRenderThread(Runnable runnable) {
        runnable.run();
    }

    protected void postInRenderThreadDelayed(Runnable runnable, long delayed) {
    }

    public void invalidateEffect() {
        refreshRender();
    }

    public void pause() {
        pauseRender();
    }

    public void resume() {
        resumeRender();
    }
}
