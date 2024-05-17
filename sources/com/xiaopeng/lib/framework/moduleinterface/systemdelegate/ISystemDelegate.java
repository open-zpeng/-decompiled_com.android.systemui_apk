package com.xiaopeng.lib.framework.moduleinterface.systemdelegate;

import android.os.RemoteException;
import android.support.annotation.Nullable;
/* loaded from: classes23.dex */
public interface ISystemDelegate {
    @Nullable
    String getCertificate() throws RemoteException;

    void setSystemProperty(String str, String str2) throws RemoteException;
}
