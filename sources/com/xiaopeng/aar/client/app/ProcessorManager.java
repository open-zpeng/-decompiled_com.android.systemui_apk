package com.xiaopeng.aar.client.app;

import androidx.annotation.RestrictTo;
import com.xiaopeng.aar.Apps;
import com.xiaopeng.aar.utils.LogUtils;
import java.util.HashMap;
import java.util.Set;
@RestrictTo({RestrictTo.Scope.LIBRARY})
/* loaded from: classes22.dex */
public class ProcessorManager {
    private final HashMap<String, AppProcessor> mApps;

    /* loaded from: classes22.dex */
    private static class SingletonHolder {
        private static final ProcessorManager INSTANCE = new ProcessorManager();

        private SingletonHolder() {
        }
    }

    public static ProcessorManager get() {
        return SingletonHolder.INSTANCE;
    }

    private ProcessorManager() {
        this.mApps = new HashMap<>();
        long time = System.currentTimeMillis();
        loadProcessor();
        LogUtils.i("Processor", "mApps : " + this.mApps.size() + " , time : " + (System.currentTimeMillis() - time));
    }

    private void loadProcessor() {
        Set<String> apps = Apps.getApps();
        for (String app : apps) {
            if (!this.mApps.containsKey(app)) {
                this.mApps.put(app, new AppProcessorImpl(app));
            }
        }
    }

    public AppProcessor getAppProcessor(String appId) {
        return this.mApps.get(appId);
    }
}
