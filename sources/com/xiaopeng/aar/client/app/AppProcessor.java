package com.xiaopeng.aar.client.app;

import androidx.annotation.RestrictTo;
import com.xiaopeng.aar.client.ApiListener;
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes22.dex */
public interface AppProcessor {
    String call(String str, String str2, String str3, String str4, byte[] bArr);

    void onReceived(ApiListener apiListener, long j, String str, String str2, String str3, String str4, byte[] bArr);

    void onServerStatus(ApiListener apiListener, String str, int i);
}
