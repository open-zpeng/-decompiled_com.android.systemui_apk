package com.android.systemui;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes21.dex */
public class SystemUISecondaryUserService extends Service {
    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        ((SystemUIApplication) getApplication()).startSecondaryUserServicesIfNeeded();
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        SystemUIService.dumpServices(((SystemUIApplication) getApplication()).getServices(), fd, pw, args);
    }
}
