package com.android.systemui.globalactions;

import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.internal.colorextraction.ColorExtractor;
import com.android.internal.colorextraction.drawable.ScrimDrawable;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.settingslib.Utils;
import com.android.systemui.Dependency;
import com.android.systemui.R;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.plugins.GlobalActions;
import com.android.systemui.plugins.GlobalActionsPanelPlugin;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.statusbar.policy.KeyguardMonitor;
/* loaded from: classes21.dex */
public class GlobalActionsImpl implements GlobalActions, CommandQueue.Callbacks {
    private static final float SHUTDOWN_SCRIM_ALPHA = 0.95f;
    private final Context mContext;
    private boolean mDisabled;
    private GlobalActionsDialog mGlobalActions;
    private final ExtensionController.Extension<GlobalActionsPanelPlugin> mPanelExtension;
    private final KeyguardMonitor mKeyguardMonitor = (KeyguardMonitor) Dependency.get(KeyguardMonitor.class);
    private final DeviceProvisionedController mDeviceProvisionedController = (DeviceProvisionedController) Dependency.get(DeviceProvisionedController.class);

    public GlobalActionsImpl(Context context) {
        this.mContext = context;
        ((CommandQueue) SysUiServiceProvider.getComponent(context, CommandQueue.class)).addCallback((CommandQueue.Callbacks) this);
        this.mPanelExtension = ((ExtensionController) Dependency.get(ExtensionController.class)).newExtension(GlobalActionsPanelPlugin.class).withPlugin(GlobalActionsPanelPlugin.class).build();
    }

    @Override // com.android.systemui.plugins.GlobalActions
    public void destroy() {
        ((CommandQueue) SysUiServiceProvider.getComponent(this.mContext, CommandQueue.class)).removeCallback((CommandQueue.Callbacks) this);
        GlobalActionsDialog globalActionsDialog = this.mGlobalActions;
        if (globalActionsDialog != null) {
            globalActionsDialog.destroy();
            this.mGlobalActions = null;
        }
    }

    @Override // com.android.systemui.plugins.GlobalActions
    public void showGlobalActions(GlobalActions.GlobalActionsManager manager) {
        if (this.mDisabled) {
            return;
        }
        if (this.mGlobalActions == null) {
            this.mGlobalActions = new GlobalActionsDialog(this.mContext, manager);
        }
        this.mGlobalActions.showDialog(this.mKeyguardMonitor.isShowing(), this.mDeviceProvisionedController.isDeviceProvisioned(), this.mPanelExtension.get());
        KeyguardUpdateMonitor.getInstance(this.mContext).requestFaceAuth();
    }

    @Override // com.android.systemui.plugins.GlobalActions
    public void showShutdownUi(boolean isReboot, String reason) {
        Drawable scrimDrawable = new ScrimDrawable();
        scrimDrawable.setAlpha(242);
        Dialog d = new Dialog(this.mContext, R.style.Theme_SystemUI_Dialog_GlobalActions);
        Window window = d.getWindow();
        window.requestFeature(1);
        window.getAttributes().systemUiVisibility |= 1792;
        window.getDecorView();
        window.getAttributes().width = -1;
        window.getAttributes().height = -1;
        window.getAttributes().layoutInDisplayCutoutMode = 1;
        window.setType(2020);
        window.clearFlags(2);
        window.addFlags(17629472);
        window.setBackgroundDrawable(scrimDrawable);
        window.setWindowAnimations(16973828);
        d.setContentView(17367292);
        d.setCancelable(false);
        int color = Utils.getColorAttrDefaultColor(this.mContext, R.attr.wallpaperTextColor);
        ((KeyguardManager) this.mContext.getSystemService(KeyguardManager.class)).isKeyguardLocked();
        ProgressBar bar = (ProgressBar) d.findViewById(16908301);
        bar.getIndeterminateDrawable().setTint(color);
        TextView message = (TextView) d.findViewById(16908308);
        message.setTextColor(color);
        if (isReboot) {
            message.setText(17040912);
        }
        ColorExtractor.GradientColors colors = ((SysuiColorExtractor) Dependency.get(SysuiColorExtractor.class)).getNeutralColors();
        scrimDrawable.setColor(colors.getMainColor(), false);
        d.show();
    }

    @Override // com.android.systemui.statusbar.CommandQueue.Callbacks
    public void disable(int displayId, int state1, int state2, boolean animate) {
        GlobalActionsDialog globalActionsDialog;
        boolean disabled = (state2 & 8) != 0;
        if (displayId != this.mContext.getDisplayId() || disabled == this.mDisabled) {
            return;
        }
        this.mDisabled = disabled;
        if (disabled && (globalActionsDialog = this.mGlobalActions) != null) {
            globalActionsDialog.dismissDialog();
        }
    }
}
