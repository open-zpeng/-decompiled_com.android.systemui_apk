package com.android.systemui.screenshot;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.UserManager;
import android.util.Log;
import java.util.function.Consumer;
/* loaded from: classes21.dex */
public class TakeScreenshotService extends Service {
    private static final String TAG = "TakeScreenshotService";
    private static GlobalScreenshot mScreenshot;
    private Handler mHandler = new AnonymousClass1();

    /* renamed from: com.android.systemui.screenshot.TakeScreenshotService$1  reason: invalid class name */
    /* loaded from: classes21.dex */
    class AnonymousClass1 extends Handler {
        AnonymousClass1() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            final Messenger callback = msg.replyTo;
            final Consumer<Uri> finisher = new Consumer<Uri>() { // from class: com.android.systemui.screenshot.TakeScreenshotService.1.1
                @Override // java.util.function.Consumer
                public void accept(Uri uri) {
                    Message reply = Message.obtain(null, 1, uri);
                    try {
                        callback.send(reply);
                    } catch (RemoteException e) {
                    }
                }
            };
            if (((UserManager) TakeScreenshotService.this.getSystemService(UserManager.class)).isUserUnlocked()) {
                if (TakeScreenshotService.mScreenshot == null) {
                    GlobalScreenshot unused = TakeScreenshotService.mScreenshot = new GlobalScreenshot(TakeScreenshotService.this);
                }
                int i = msg.what;
                if (i == 1) {
                    TakeScreenshotService.mScreenshot.takeScreenshot(finisher, msg.arg1 > 0, msg.arg2 > 0);
                    return;
                } else if (i == 2) {
                    TakeScreenshotService.mScreenshot.takeScreenshotPartial(finisher, msg.arg1 > 0, msg.arg2 > 0);
                    return;
                } else {
                    Log.d(TakeScreenshotService.TAG, "Invalid screenshot option: " + msg.what);
                    return;
                }
            }
            Log.w(TakeScreenshotService.TAG, "Skipping screenshot because storage is locked!");
            post(new Runnable() { // from class: com.android.systemui.screenshot.-$$Lambda$TakeScreenshotService$1$tdU3nLt8HPDjvS_ioHO5v5FVcoU
                @Override // java.lang.Runnable
                public final void run() {
                    finisher.accept(null);
                }
            });
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return new Messenger(this.mHandler).getBinder();
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        GlobalScreenshot globalScreenshot = mScreenshot;
        if (globalScreenshot != null) {
            globalScreenshot.stopScreenshot();
            return true;
        }
        return true;
    }
}
