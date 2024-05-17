package com.android.systemui.shared.recents.view;

import android.app.ActivityOptions;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Bundle;
import android.os.Handler;
import android.os.IRemoteCallback;
import android.os.RemoteException;
import android.view.View;
import java.util.function.Consumer;
/* loaded from: classes21.dex */
public class RecentsTransition {
    public static ActivityOptions createAspectScaleAnimation(Context context, Handler handler, boolean scaleUp, AppTransitionAnimationSpecsFuture animationSpecsFuture, final Runnable animationStartCallback) {
        ActivityOptions.OnAnimationStartedListener animStartedListener = new ActivityOptions.OnAnimationStartedListener() { // from class: com.android.systemui.shared.recents.view.RecentsTransition.1
            private boolean mHandled;

            public void onAnimationStarted() {
                if (this.mHandled) {
                    return;
                }
                this.mHandled = true;
                Runnable runnable = animationStartCallback;
                if (runnable != null) {
                    runnable.run();
                }
            }
        };
        ActivityOptions opts = ActivityOptions.makeMultiThumbFutureAspectScaleAnimation(context, handler, animationSpecsFuture != null ? animationSpecsFuture.getFuture() : null, animStartedListener, scaleUp);
        return opts;
    }

    public static IRemoteCallback wrapStartedListener(final Handler handler, final Runnable animationStartCallback) {
        if (animationStartCallback == null) {
            return null;
        }
        return new IRemoteCallback.Stub() { // from class: com.android.systemui.shared.recents.view.RecentsTransition.2
            public void sendResult(Bundle data) throws RemoteException {
                handler.post(animationStartCallback);
            }
        };
    }

    public static Bitmap drawViewIntoHardwareBitmap(int width, int height, final View view, final float scale, final int eraseColor) {
        return createHardwareBitmap(width, height, new Consumer<Canvas>() { // from class: com.android.systemui.shared.recents.view.RecentsTransition.3
            @Override // java.util.function.Consumer
            public void accept(Canvas c) {
                float f = scale;
                c.scale(f, f);
                int i = eraseColor;
                if (i != 0) {
                    c.drawColor(i);
                }
                View view2 = view;
                if (view2 != null) {
                    view2.draw(c);
                }
            }
        });
    }

    public static Bitmap createHardwareBitmap(int width, int height, Consumer<Canvas> consumer) {
        Picture picture = new Picture();
        Canvas canvas = picture.beginRecording(width, height);
        consumer.accept(canvas);
        picture.endRecording();
        return Bitmap.createBitmap(picture);
    }
}
