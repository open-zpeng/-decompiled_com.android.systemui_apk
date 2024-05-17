package com.android.keyguard.clock;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes19.dex */
public final class ViewPreviewer {
    private static final String TAG = "ViewPreviewer";
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());

    /* JADX INFO: Access modifiers changed from: package-private */
    public Bitmap createPreview(final View view, final int width, final int height) {
        if (view == null) {
            return null;
        }
        FutureTask<Bitmap> task = new FutureTask<>(new Callable<Bitmap>() { // from class: com.android.keyguard.clock.ViewPreviewer.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.concurrent.Callable
            public Bitmap call() {
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                canvas.drawColor(-16777216);
                ViewPreviewer.this.dispatchVisibilityAggregated(view, true);
                view.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(height, 1073741824));
                view.layout(0, 0, width, height);
                view.draw(canvas);
                return bitmap;
            }
        });
        if (Looper.myLooper() == Looper.getMainLooper()) {
            task.run();
        } else {
            this.mMainHandler.post(task);
        }
        try {
            return task.get();
        } catch (Exception e) {
            Log.e(TAG, "Error completing task", e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchVisibilityAggregated(View view, boolean isVisible) {
        boolean z = false;
        boolean thisVisible = view.getVisibility() == 0;
        if (thisVisible || !isVisible) {
            view.onVisibilityAggregated(isVisible);
        }
        if (view instanceof ViewGroup) {
            if (thisVisible && isVisible) {
                z = true;
            }
            boolean isVisible2 = z;
            ViewGroup vg = (ViewGroup) view;
            int count = vg.getChildCount();
            for (int i = 0; i < count; i++) {
                dispatchVisibilityAggregated(vg.getChildAt(i), isVisible2);
            }
        }
    }
}
