package com.xiaopeng.systemui.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.xiaopeng.systemui.Logger;
import com.xiaopeng.systemui.controller.screensaver.ScreensaverManager;
/* loaded from: classes24.dex */
public class ScreensaverSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "SurfaceViewDraw";
    private static ScreensaverSurfaceView sScreensaverSurfaceView = null;

    public ScreensaverSurfaceView(Context context) {
        super(context);
    }

    public ScreensaverSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScreensaverSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == 0) {
            Logger.d(TAG, "onTouchEvent - down");
            ScreensaverManager.get(getContext()).stop();
            ScreensaverManager.get(getContext()).StartDoIdle();
            Logger.d(TAG, "touch stop");
            return true;
        }
        return true;
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder holder) {
        ScreensaverManager.get(getContext()).play();
        Logger.d(TAG, "surface play");
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder holder) {
        ScreensaverManager.get(getContext()).stop();
        Logger.d(TAG, "surface stop");
    }
}
