package com.android.systemui.globalactions;

import android.os.RemoteException;
import android.os.ServiceManager;
import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.Dependency;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.SystemUI;
import com.android.systemui.plugins.GlobalActions;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.policy.ExtensionController;
import java.util.function.Consumer;
import java.util.function.Supplier;
/* loaded from: classes21.dex */
public class GlobalActionsComponent extends SystemUI implements CommandQueue.Callbacks, GlobalActions.GlobalActionsManager {
    private IStatusBarService mBarService;
    private ExtensionController.Extension<GlobalActions> mExtension;
    private GlobalActions mPlugin;

    @Override // com.android.systemui.SystemUI
    public void start() {
        this.mBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
        this.mExtension = ((ExtensionController) Dependency.get(ExtensionController.class)).newExtension(GlobalActions.class).withPlugin(GlobalActions.class).withDefault(new Supplier() { // from class: com.android.systemui.globalactions.-$$Lambda$GlobalActionsComponent$YD1kfcxpItFZ4AniRUv_gcXk_Mo
            @Override // java.util.function.Supplier
            public final Object get() {
                return GlobalActionsComponent.this.lambda$start$0$GlobalActionsComponent();
            }
        }).withCallback(new Consumer() { // from class: com.android.systemui.globalactions.-$$Lambda$GlobalActionsComponent$bGplH0pcKhfpL1pOMBpgWKJntvw
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                GlobalActionsComponent.this.onExtensionCallback((GlobalActions) obj);
            }
        }).build();
        this.mPlugin = this.mExtension.get();
        ((CommandQueue) SysUiServiceProvider.getComponent(this.mContext, CommandQueue.class)).addCallback((CommandQueue.Callbacks) this);
    }

    public /* synthetic */ GlobalActions lambda$start$0$GlobalActionsComponent() {
        return new GlobalActionsImpl(this.mContext);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onExtensionCallback(GlobalActions newPlugin) {
        GlobalActions globalActions = this.mPlugin;
        if (globalActions != null) {
            globalActions.destroy();
        }
        this.mPlugin = newPlugin;
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void handleShowShutdownUi(boolean isReboot, String reason) {
        this.mExtension.get().showShutdownUi(isReboot, reason);
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void handleShowGlobalActionsMenu() {
        this.mExtension.get().showGlobalActions(this);
    }

    @Override // com.android.systemui.plugins.GlobalActions.GlobalActionsManager
    public void onGlobalActionsShown() {
        try {
            this.mBarService.onGlobalActionsShown();
        } catch (RemoteException e) {
        }
    }

    @Override // com.android.systemui.plugins.GlobalActions.GlobalActionsManager
    public void onGlobalActionsHidden() {
        try {
            this.mBarService.onGlobalActionsHidden();
        } catch (RemoteException e) {
        }
    }

    @Override // com.android.systemui.plugins.GlobalActions.GlobalActionsManager
    public void shutdown() {
        try {
            this.mBarService.shutdown();
        } catch (RemoteException e) {
        }
    }

    @Override // com.android.systemui.plugins.GlobalActions.GlobalActionsManager
    public void reboot(boolean safeMode) {
        try {
            this.mBarService.reboot(safeMode);
        } catch (RemoteException e) {
        }
    }
}
