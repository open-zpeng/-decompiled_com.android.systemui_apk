package com.xiaopeng.systemui.statusbar;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.xiaopeng.systemui.controller.NetworkController;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes24.dex */
public class CallbackHandler extends Handler implements NetworkController.SignalCallback {
    private static final int MSG_LISTEN_SIGNAL = 1;
    private final ArrayList<NetworkController.SignalCallback> mSignalCallbacks;

    public CallbackHandler() {
        super(Looper.getMainLooper());
        this.mSignalCallbacks = new ArrayList<>();
    }

    public CallbackHandler(Looper looper) {
        super(looper);
        this.mSignalCallbacks = new ArrayList<>();
    }

    @Override // android.os.Handler
    public void handleMessage(Message msg) {
        if (msg.what == 1) {
            if (msg.arg1 != 0) {
                this.mSignalCallbacks.add((NetworkController.SignalCallback) msg.obj);
            } else {
                this.mSignalCallbacks.remove((NetworkController.SignalCallback) msg.obj);
            }
        }
    }

    @Override // com.xiaopeng.systemui.controller.NetworkController.SignalCallback
    public void setWifiLevel(final int level) {
        post(new Runnable() { // from class: com.xiaopeng.systemui.statusbar.CallbackHandler.1
            @Override // java.lang.Runnable
            public void run() {
                Iterator it = CallbackHandler.this.mSignalCallbacks.iterator();
                while (it.hasNext()) {
                    NetworkController.SignalCallback callback = (NetworkController.SignalCallback) it.next();
                    callback.setWifiLevel(level);
                }
            }
        });
    }

    public void setListening(NetworkController.SignalCallback listener, boolean listening) {
        obtainMessage(1, listening ? 1 : 0, 0, listener).sendToTarget();
    }

    @Override // com.xiaopeng.systemui.controller.NetworkController.SignalCallback
    public void setWifiState(final int wifiState) {
        post(new Runnable() { // from class: com.xiaopeng.systemui.statusbar.CallbackHandler.2
            @Override // java.lang.Runnable
            public void run() {
                Iterator it = CallbackHandler.this.mSignalCallbacks.iterator();
                while (it.hasNext()) {
                    NetworkController.SignalCallback callback = (NetworkController.SignalCallback) it.next();
                    callback.setWifiState(wifiState);
                }
            }
        });
    }

    @Override // com.xiaopeng.systemui.controller.NetworkController.SignalCallback
    public void setWifiConnectionState(final boolean wifiConnectionState) {
        post(new Runnable() { // from class: com.xiaopeng.systemui.statusbar.CallbackHandler.3
            @Override // java.lang.Runnable
            public void run() {
                Iterator it = CallbackHandler.this.mSignalCallbacks.iterator();
                while (it.hasNext()) {
                    NetworkController.SignalCallback callback = (NetworkController.SignalCallback) it.next();
                    callback.setWifiConnectionState(wifiConnectionState);
                }
            }
        });
    }
}
