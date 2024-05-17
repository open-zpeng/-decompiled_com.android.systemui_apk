package com.xiaopeng.aar.client.ipc;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes22.dex */
public interface Ipc {

    /* loaded from: classes22.dex */
    public interface OnServerListener {
        void onEvent(int i, String str, String str2, String str3, String str4, byte[] bArr);
    }

    /* loaded from: classes22.dex */
    public interface OnServerStatusListener {
        public static final int STATUS_RESTART = 2;
        public static final int STATUS_START = 1;

        void onServerStatus(String str, int i);
    }

    void addOnServerStatusListener(@NonNull OnServerStatusListener onServerStatusListener);

    String call(String str, String str2, String str3, String str4, byte[] bArr);

    void onReceived(int i, String str, String str2, String str3, String str4, byte[] bArr);

    void removeOnServerStatusListener(@NonNull OnServerStatusListener onServerStatusListener);

    void setOnServerListener(OnServerListener onServerListener);

    boolean subscribe(String str, String str2);

    boolean subscribes(String str, String str2);

    void unSubscribe(String str, String str2);

    void unSubscribes(String str, String str2);
}
