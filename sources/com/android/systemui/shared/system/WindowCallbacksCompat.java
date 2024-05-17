package com.android.systemui.shared.system;

import android.graphics.Canvas;
import android.graphics.RecordingCanvas;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewRootImpl;
import android.view.WindowCallbacks;
/* loaded from: classes21.dex */
public class WindowCallbacksCompat {
    private final View mView;
    private final WindowCallbacks mWindowCallbacks = new WindowCallbacks() { // from class: com.android.systemui.shared.system.WindowCallbacksCompat.1
        public void onWindowSizeIsChanging(Rect newBounds, boolean fullscreen, Rect systemInsets, Rect stableInsets) {
            WindowCallbacksCompat.this.onWindowSizeIsChanging(newBounds, fullscreen, systemInsets, stableInsets);
        }

        public void onWindowDragResizeStart(Rect initialBounds, boolean fullscreen, Rect systemInsets, Rect stableInsets, int resizeMode) {
            WindowCallbacksCompat.this.onWindowDragResizeStart(initialBounds, fullscreen, systemInsets, stableInsets, resizeMode);
        }

        public void onWindowDragResizeEnd() {
            WindowCallbacksCompat.this.onWindowDragResizeEnd();
        }

        public boolean onContentDrawn(int offsetX, int offsetY, int sizeX, int sizeY) {
            return WindowCallbacksCompat.this.onContentDrawn(offsetX, offsetY, sizeX, sizeY);
        }

        public void onRequestDraw(boolean reportNextDraw) {
            WindowCallbacksCompat.this.onRequestDraw(reportNextDraw);
        }

        public void onPostDraw(RecordingCanvas canvas) {
            WindowCallbacksCompat.this.onPostDraw(canvas);
        }
    };

    public WindowCallbacksCompat(View view) {
        this.mView = view;
    }

    public void onWindowSizeIsChanging(Rect newBounds, boolean fullscreen, Rect systemInsets, Rect stableInsets) {
    }

    public void onWindowDragResizeStart(Rect initialBounds, boolean fullscreen, Rect systemInsets, Rect stableInsets, int resizeMode) {
    }

    public void onWindowDragResizeEnd() {
    }

    public boolean onContentDrawn(int offsetX, int offsetY, int sizeX, int sizeY) {
        return false;
    }

    public void onRequestDraw(boolean reportNextDraw) {
        if (reportNextDraw) {
            reportDrawFinish();
        }
    }

    public void onPostDraw(Canvas canvas) {
    }

    public void reportDrawFinish() {
        this.mView.getViewRootImpl().reportDrawFinish();
    }

    public boolean attach() {
        ViewRootImpl root = this.mView.getViewRootImpl();
        if (root != null) {
            root.addWindowCallbacks(this.mWindowCallbacks);
            root.requestInvalidateRootRenderNode();
            return true;
        }
        return false;
    }

    public void detach() {
        ViewRootImpl root = this.mView.getViewRootImpl();
        if (root != null) {
            root.removeWindowCallbacks(this.mWindowCallbacks);
        }
    }
}
