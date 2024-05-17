package com.android.systemui.volume;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.VolumePolicy;
import android.os.Bundle;
import com.android.settingslib.applications.InterestingConfigChanges;
import com.android.systemui.Dependency;
import com.android.systemui.SystemUI;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.PluginDependencyProvider;
import com.android.systemui.plugins.VolumeDialog;
import com.android.systemui.plugins.VolumeDialogController;
import com.android.systemui.qs.tiles.DndTile;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.volume.VolumeDialogControllerImpl;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.function.Consumer;
import java.util.function.Supplier;
/* loaded from: classes21.dex */
public class VolumeDialogComponent implements VolumeComponent, TunerService.Tunable, VolumeDialogControllerImpl.UserActivityListener {
    public static final boolean DEFAULT_DO_NOT_DISTURB_WHEN_SILENT = false;
    public static final boolean DEFAULT_VOLUME_DOWN_TO_ENTER_SILENT = false;
    public static final boolean DEFAULT_VOLUME_UP_TO_EXIT_SILENT = false;
    public static final String VOLUME_DOWN_SILENT = "sysui_volume_down_silent";
    public static final String VOLUME_SILENT_DO_NOT_DISTURB = "sysui_do_not_disturb";
    public static final String VOLUME_UP_SILENT = "sysui_volume_up_silent";
    protected final Context mContext;
    private VolumeDialog mDialog;
    private final SystemUI mSysui;
    private final InterestingConfigChanges mConfigChanges = new InterestingConfigChanges(-1073741308);
    private VolumePolicy mVolumePolicy = new VolumePolicy(false, false, false, 400);
    private final VolumeDialog.Callback mVolumeDialogCallback = new VolumeDialog.Callback() { // from class: com.android.systemui.volume.VolumeDialogComponent.1
        @Override // com.android.systemui.plugins.VolumeDialog.Callback
        public void onZenSettingsClicked() {
            VolumeDialogComponent.this.startSettings(ZenModePanel.ZEN_SETTINGS);
        }

        @Override // com.android.systemui.plugins.VolumeDialog.Callback
        public void onZenPrioritySettingsClicked() {
            VolumeDialogComponent.this.startSettings(ZenModePanel.ZEN_PRIORITY_SETTINGS);
        }
    };
    private final VolumeDialogControllerImpl mController = (VolumeDialogControllerImpl) Dependency.get(VolumeDialogController.class);

    public VolumeDialogComponent(SystemUI sysui, Context context) {
        this.mSysui = sysui;
        this.mContext = context;
        this.mController.setUserActivityListener(this);
        ((PluginDependencyProvider) Dependency.get(PluginDependencyProvider.class)).allowPluginDependency(VolumeDialogController.class);
        ((ExtensionController) Dependency.get(ExtensionController.class)).newExtension(VolumeDialog.class).withPlugin(VolumeDialog.class).withDefault(new Supplier() { // from class: com.android.systemui.volume.-$$Lambda$5eQ6FmuY0CORdNfZebXQAtrsfI4
            @Override // java.util.function.Supplier
            public final Object get() {
                return VolumeDialogComponent.this.createDefault();
            }
        }).withCallback(new Consumer() { // from class: com.android.systemui.volume.-$$Lambda$VolumeDialogComponent$vZvGMkdhFGTZ9hLE1BnozIW6Wb0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                VolumeDialogComponent.this.lambda$new$0$VolumeDialogComponent((VolumeDialog) obj);
            }
        }).build();
        applyConfiguration();
        ((TunerService) Dependency.get(TunerService.class)).addTunable(this, VOLUME_DOWN_SILENT, VOLUME_UP_SILENT, VOLUME_SILENT_DO_NOT_DISTURB);
    }

    public /* synthetic */ void lambda$new$0$VolumeDialogComponent(VolumeDialog dialog) {
        VolumeDialog volumeDialog = this.mDialog;
        if (volumeDialog != null) {
            volumeDialog.destroy();
        }
        this.mDialog = dialog;
        this.mDialog.init(2020, this.mVolumeDialogCallback);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public VolumeDialog createDefault() {
        VolumeDialogImpl impl = new VolumeDialogImpl(this.mContext);
        impl.setStreamImportant(1, false);
        impl.setAutomute(true);
        impl.setSilentMode(false);
        return impl;
    }

    @Override // com.android.systemui.tuner.TunerService.Tunable
    public void onTuningChanged(String key, String newValue) {
        boolean volumeDownToEnterSilent = this.mVolumePolicy.volumeDownToEnterSilent;
        boolean volumeUpToExitSilent = this.mVolumePolicy.volumeUpToExitSilent;
        boolean doNotDisturbWhenSilent = this.mVolumePolicy.doNotDisturbWhenSilent;
        if (VOLUME_DOWN_SILENT.equals(key)) {
            volumeDownToEnterSilent = TunerService.parseIntegerSwitch(newValue, false);
        } else if (VOLUME_UP_SILENT.equals(key)) {
            volumeUpToExitSilent = TunerService.parseIntegerSwitch(newValue, false);
        } else if (VOLUME_SILENT_DO_NOT_DISTURB.equals(key)) {
            doNotDisturbWhenSilent = TunerService.parseIntegerSwitch(newValue, false);
        }
        setVolumePolicy(volumeDownToEnterSilent, volumeUpToExitSilent, doNotDisturbWhenSilent, this.mVolumePolicy.vibrateToSilentDebounce);
    }

    private void setVolumePolicy(boolean volumeDownToEnterSilent, boolean volumeUpToExitSilent, boolean doNotDisturbWhenSilent, int vibrateToSilentDebounce) {
        this.mVolumePolicy = new VolumePolicy(volumeDownToEnterSilent, volumeUpToExitSilent, doNotDisturbWhenSilent, vibrateToSilentDebounce);
        this.mController.setVolumePolicy(this.mVolumePolicy);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setEnableDialogs(boolean volumeUi, boolean safetyWarning) {
        this.mController.setEnableDialogs(volumeUi, safetyWarning);
    }

    @Override // com.android.systemui.volume.VolumeDialogControllerImpl.UserActivityListener
    public void onUserActivity() {
        KeyguardViewMediator kvm = (KeyguardViewMediator) this.mSysui.getComponent(KeyguardViewMediator.class);
        if (kvm != null) {
            kvm.userActivity();
        }
    }

    private void applyConfiguration() {
        this.mController.setVolumePolicy(this.mVolumePolicy);
        this.mController.showDndTile(true);
    }

    @Override // com.android.systemui.volume.VolumeComponent
    public void onConfigurationChanged(Configuration newConfig) {
        if (this.mConfigChanges.applyNewConfig(this.mContext.getResources())) {
            this.mController.mCallbacks.onConfigurationChanged();
        }
    }

    @Override // com.android.systemui.volume.VolumeComponent
    public void dismissNow() {
        this.mController.dismiss();
    }

    @Override // com.android.systemui.DemoMode
    public void dispatchDemoCommand(String command, Bundle args) {
    }

    @Override // com.android.systemui.volume.VolumeComponent
    public void register() {
        this.mController.register();
        DndTile.setCombinedIcon(this.mContext, true);
    }

    @Override // com.android.systemui.volume.VolumeComponent
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startSettings(Intent intent) {
        ((ActivityStarter) Dependency.get(ActivityStarter.class)).startActivity(intent, true, true);
    }
}
