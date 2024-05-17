package com.android.systemui;

import android.util.Log;
import com.xiaopeng.lib.apirouter.server.IServicePublisher;
import com.xiaopeng.lib.apirouter.server.Publish;
/* loaded from: classes21.dex */
public class ApiRouterRemoteService implements IServicePublisher {
    private static final String TAG = "ApiRouterRemoteService";

    @Publish
    public void updateEvent(String event, String packageName) {
        Log.d("IServicePublisher", "event:" + event);
        ApiRouterListener apiRouterListener = ApiRouterHelper.getInstance().getAPICallbackListener();
        if (apiRouterListener != null) {
            apiRouterListener.updateEvent(event, packageName);
        }
    }
}
