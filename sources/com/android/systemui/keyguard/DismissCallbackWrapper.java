package com.android.systemui.keyguard;

import android.os.RemoteException;
import android.util.Log;
import com.android.internal.policy.IKeyguardDismissCallback;
/* loaded from: classes21.dex */
public class DismissCallbackWrapper {
    private static final String TAG = "DismissCallbackWrapper";
    private IKeyguardDismissCallback mCallback;

    public DismissCallbackWrapper(IKeyguardDismissCallback callback) {
        this.mCallback = callback;
    }

    public void notifyDismissError() {
        try {
            this.mCallback.onDismissError();
        } catch (RemoteException e) {
            Log.i(TAG, "Failed to call callback", e);
        }
    }

    public void notifyDismissCancelled() {
        try {
            this.mCallback.onDismissCancelled();
        } catch (RemoteException e) {
            Log.i(TAG, "Failed to call callback", e);
        }
    }

    public void notifyDismissSucceeded() {
        try {
            this.mCallback.onDismissSucceeded();
        } catch (RemoteException e) {
            Log.i(TAG, "Failed to call callback", e);
        }
    }
}
