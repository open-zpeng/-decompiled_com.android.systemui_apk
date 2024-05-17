package com.android.systemui.doze;

import android.content.Context;
import android.os.PowerManager;
import android.os.SystemClock;
import android.service.dreams.DreamService;
import android.util.Log;
import com.android.systemui.Dependency;
import com.android.systemui.doze.DozeMachine;
import com.android.systemui.plugins.DozeServicePlugin;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.shared.plugins.PluginManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import javax.inject.Inject;
/* loaded from: classes21.dex */
public class DozeService extends DreamService implements DozeMachine.Service, DozeServicePlugin.RequestDoze, PluginListener<DozeServicePlugin> {
    private DozeMachine mDozeMachine;
    private DozeServicePlugin mDozePlugin;
    private PluginManager mPluginManager;
    private static final String TAG = "DozeService";
    static final boolean DEBUG = Log.isLoggable(TAG, 3);

    @Inject
    public DozeService() {
        setDebug(DEBUG);
    }

    @Override // android.service.dreams.DreamService, android.app.Service
    public void onCreate() {
        super.onCreate();
        setWindowless(true);
        if (DozeFactory.getHost(this) == null) {
            finish();
            return;
        }
        this.mPluginManager = (PluginManager) Dependency.get(PluginManager.class);
        this.mPluginManager.addPluginListener((PluginListener) this, DozeServicePlugin.class, false);
        this.mDozeMachine = new DozeFactory().assembleMachine(this, (FalsingManager) Dependency.get(FalsingManager.class));
    }

    @Override // android.service.dreams.DreamService, android.app.Service
    public void onDestroy() {
        PluginManager pluginManager = this.mPluginManager;
        if (pluginManager != null) {
            pluginManager.removePluginListener(this);
        }
        super.onDestroy();
        this.mDozeMachine = null;
    }

    @Override // com.android.systemui.plugins.PluginListener
    public void onPluginConnected(DozeServicePlugin plugin, Context pluginContext) {
        this.mDozePlugin = plugin;
        this.mDozePlugin.setDozeRequester(this);
    }

    @Override // com.android.systemui.plugins.PluginListener
    public void onPluginDisconnected(DozeServicePlugin plugin) {
        DozeServicePlugin dozeServicePlugin = this.mDozePlugin;
        if (dozeServicePlugin != null) {
            dozeServicePlugin.onDreamingStopped();
            this.mDozePlugin = null;
        }
    }

    @Override // android.service.dreams.DreamService
    public void onDreamingStarted() {
        super.onDreamingStarted();
        this.mDozeMachine.requestState(DozeMachine.State.INITIALIZED);
        startDozing();
        DozeServicePlugin dozeServicePlugin = this.mDozePlugin;
        if (dozeServicePlugin != null) {
            dozeServicePlugin.onDreamingStarted();
        }
    }

    @Override // android.service.dreams.DreamService
    public void onDreamingStopped() {
        super.onDreamingStopped();
        this.mDozeMachine.requestState(DozeMachine.State.FINISH);
        DozeServicePlugin dozeServicePlugin = this.mDozePlugin;
        if (dozeServicePlugin != null) {
            dozeServicePlugin.onDreamingStopped();
        }
    }

    protected void dumpOnHandler(FileDescriptor fd, PrintWriter pw, String[] args) {
        super.dumpOnHandler(fd, pw, args);
        DozeMachine dozeMachine = this.mDozeMachine;
        if (dozeMachine != null) {
            dozeMachine.dump(pw);
        }
    }

    @Override // com.android.systemui.doze.DozeMachine.Service
    public void requestWakeUp() {
        PowerManager pm = (PowerManager) getSystemService(PowerManager.class);
        pm.wakeUp(SystemClock.uptimeMillis(), 4, "com.android.systemui:NODOZE");
    }

    @Override // com.android.systemui.plugins.DozeServicePlugin.RequestDoze
    public void onRequestShowDoze() {
        DozeMachine dozeMachine = this.mDozeMachine;
        if (dozeMachine != null) {
            dozeMachine.requestState(DozeMachine.State.DOZE_AOD);
        }
    }

    @Override // com.android.systemui.plugins.DozeServicePlugin.RequestDoze
    public void onRequestHideDoze() {
        DozeMachine dozeMachine = this.mDozeMachine;
        if (dozeMachine != null) {
            dozeMachine.requestState(DozeMachine.State.DOZE);
        }
    }
}
