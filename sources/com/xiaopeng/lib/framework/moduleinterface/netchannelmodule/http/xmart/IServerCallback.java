package com.xiaopeng.lib.framework.moduleinterface.netchannelmodule.http.xmart;
/* loaded from: classes23.dex */
public interface IServerCallback {
    public static final int CODE_SUCCESS = 200;

    void onFailure(IXmartResponse iXmartResponse);

    void onSuccess(IXmartResponse iXmartResponse);
}
