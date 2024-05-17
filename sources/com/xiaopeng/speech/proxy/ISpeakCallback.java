package com.xiaopeng.speech.proxy;
/* loaded from: classes23.dex */
public interface ISpeakCallback {
    void beginning(String str, String str2);

    void end(String str, int i, String str2);

    void error(String str, String str2);

    void received(byte[] bArr);
}
