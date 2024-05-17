package com.android.systemui;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.os.SystemProperties;
import android.support.v4.media.MediaPlayer2;
import android.util.Slog;
import com.android.internal.os.BinderInternal;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.shared.plugins.PluginManagerImpl;
import com.xiaopeng.speech.protocol.bean.recommend.RecommendBean;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes21.dex */
public class SystemUIService extends Service {
    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        ((SystemUIApplication) getApplication()).startServicesIfNeeded();
        if (Build.IS_DEBUGGABLE && SystemProperties.getBoolean("debug.crash_sysui", false)) {
            throw new RuntimeException();
        }
        if (Build.IS_DEBUGGABLE) {
            BinderInternal.nSetBinderProxyCountEnabled(true);
            BinderInternal.nSetBinderProxyCountWatermarks(1000, (int) MediaPlayer2.MEDIA_INFO_TIMED_TEXT_ERROR);
            BinderInternal.setBinderProxyCountCallback(new BinderInternal.BinderProxyLimitListener() { // from class: com.android.systemui.SystemUIService.1
                public void onLimitReached(int uid) {
                    Slog.w(SystemUIApplication.TAG, "uid " + uid + " sent too many Binder proxies to uid " + Process.myUid());
                }
            }, (Handler) Dependency.get(Dependency.MAIN_HANDLER));
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    protected void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (args != null && args.length > 0 && args[0].equals("--config")) {
            dumpConfig(pw);
            return;
        }
        dumpServices(((SystemUIApplication) getApplication()).getServices(), fd, pw, args);
        dumpConfig(pw);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void dumpServices(SystemUI[] services, FileDescriptor fd, PrintWriter pw, String[] args) {
        int i = 0;
        if (args == null || args.length == 0) {
            pw.println("dumping service: " + Dependency.class.getName());
            Dependency.staticDump(fd, pw, args);
            int length = services.length;
            while (i < length) {
                SystemUI ui = services[i];
                pw.println("dumping service: " + ui.getClass().getName());
                ui.dump(fd, pw, args);
                i++;
            }
            if (Build.IS_DEBUGGABLE) {
                pw.println("dumping plugins:");
                ((PluginManagerImpl) Dependency.get(PluginManager.class)).dump(fd, pw, args);
                return;
            }
            return;
        }
        String svc = args[0].toLowerCase();
        if (Dependency.class.getName().endsWith(svc)) {
            Dependency.staticDump(fd, pw, args);
        }
        int length2 = services.length;
        while (i < length2) {
            SystemUI ui2 = services[i];
            String name = ui2.getClass().getName().toLowerCase();
            if (name.endsWith(svc)) {
                ui2.dump(fd, pw, args);
            }
            i++;
        }
    }

    private void dumpConfig(PrintWriter pw) {
        pw.println("SystemUiServiceComponents configuration:");
        pw.print("vendor component: ");
        pw.println(getResources().getString(R.string.config_systemUIVendorServiceComponent));
        dumpConfig(pw, RecommendBean.SHOW_TIME_GLOBAL, R.array.config_systemUIServiceComponents);
        dumpConfig(pw, "per-user", R.array.config_systemUIServiceComponentsPerUser);
    }

    private void dumpConfig(PrintWriter pw, String type, int resId) {
        String[] services = getResources().getStringArray(resId);
        pw.print(type);
        pw.print(": ");
        if (services == null) {
            pw.println("N/A");
            return;
        }
        pw.print(services.length);
        pw.println(" services");
        for (int i = 0; i < services.length; i++) {
            pw.print("  ");
            pw.print(i);
            pw.print(": ");
            pw.println(services[i]);
        }
    }
}
