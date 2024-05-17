package com.xiaopeng.systemui.server;

import com.android.systemui.SystemUI;
import com.xiaopeng.systemui.Logger;
/* loaded from: classes24.dex */
public class ServerManager extends SystemUI {
    @Override // com.android.systemui.SystemUI
    public void start() {
        Logger.d("ServerManager", "ServerManager---start");
        try {
            SystemUIServer.get().init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.SystemUI
    public void onBootCompleted() {
        super.onBootCompleted();
    }
}
