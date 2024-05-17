package com.android.systemui;

import android.app.Notification;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Map;
import java.util.function.Function;
/* loaded from: classes21.dex */
public abstract class SystemUI implements SysUiServiceProvider {
    public Map<Class<?>, Object> mComponents;
    public Context mContext;

    /* loaded from: classes21.dex */
    public interface Injector extends Function<Context, SystemUI> {
    }

    public abstract void start();

    /* JADX INFO: Access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration newConfig) {
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onBootCompleted() {
    }

    @Override // com.android.systemui.SysUiServiceProvider
    public <T> T getComponent(Class<T> interfaceType) {
        Map<Class<?>, Object> map = this.mComponents;
        if (map != null) {
            return (T) map.get(interfaceType);
        }
        return null;
    }

    public <T, C extends T> void putComponent(Class<T> interfaceType, C component) {
        Map<Class<?>, Object> map = this.mComponents;
        if (map != null) {
            map.put(interfaceType, component);
        }
    }

    public static void overrideNotificationAppName(Context context, Notification.Builder n, boolean system) {
        String appName;
        Bundle extras = new Bundle();
        if (system) {
            appName = context.getString(17040490);
        } else {
            appName = context.getString(17040489);
        }
        extras.putString("android.substName", appName);
        n.addExtras(extras);
    }
}
