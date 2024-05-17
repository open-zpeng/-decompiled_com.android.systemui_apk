package com.xiaopeng.aar.server.ipc;

import android.content.Context;
import androidx.annotation.RestrictTo;
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes22.dex */
public interface IpcServer {

    /* loaded from: classes22.dex */
    public interface OnClientListener {
        String onCall(int i, String str, String str2, String str3, byte[] bArr);

        void onClientDied();
    }

    String onCall(int i, String str, String str2, String str3, byte[] bArr);

    void send(int i, String str, String str2, String str3, byte[] bArr);

    void setAppId(String str);

    void setOnClientListener(OnClientListener onClientListener);

    void setTargetPackage(String str);

    default void keepAlive(Context context) {
    }
}
