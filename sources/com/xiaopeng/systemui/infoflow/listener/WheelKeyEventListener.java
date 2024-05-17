package com.xiaopeng.systemui.infoflow.listener;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.RemoteException;
import android.view.KeyEvent;
import com.xiaopeng.IXPKeyListener;
import com.xiaopeng.systemui.infoflow.util.Logger;
/* loaded from: classes24.dex */
public class WheelKeyEventListener extends IXPKeyListener.Stub {
    private static final String TAG = "WheelKeyEventListener";
    private Context mContext;
    private InputManager mInputManager = InputManager.getInstance();
    private WheelKeyListener mListener;

    /* loaded from: classes24.dex */
    public interface WheelKeyListener {
        void onWheelKeyEvent(KeyEvent keyEvent);
    }

    public WheelKeyEventListener(Context context, WheelKeyListener listener) {
        this.mContext = context;
        this.mListener = listener;
    }

    public void register() {
        this.mInputManager.registerListener(this, this.mContext.getPackageName(), true);
        Logger.d(TAG, "register wheel event listener to input manager");
    }

    public int notify(KeyEvent keyEvent, String s) throws RemoteException {
        if (this.mListener != null) {
            Logger.d(TAG, "notify onWheelKeyEvent");
            this.mListener.onWheelKeyEvent(keyEvent);
            return 0;
        }
        return 0;
    }
}
