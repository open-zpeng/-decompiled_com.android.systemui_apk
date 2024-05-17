package com.xiaopeng.aar.client;
/* loaded from: classes22.dex */
public interface ApiListener {
    void onCallResult(String str, String str2, String str3, String str4, String str5);

    void onReceived(String str, String str2, String str3, String str4, ByteWrapper byteWrapper);

    default void onServerStatus(String appId, int status) {
    }
}
