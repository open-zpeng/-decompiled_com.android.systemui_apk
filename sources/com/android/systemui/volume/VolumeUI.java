package com.android.systemui.volume;

import android.content.res.Configuration;
import android.os.Handler;
import android.util.Log;
import com.android.systemui.R;
import com.android.systemui.SystemUI;
import com.android.systemui.SystemUIFactory;
import com.android.systemui.qs.tiles.DndTile;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes21.dex */
public class VolumeUI extends SystemUI {
    private boolean mEnabled;
    private final Handler mHandler = new Handler();
    private VolumeDialogComponent mVolumeComponent;
    private static final String TAG = "VolumeUI";
    private static boolean LOGD = Log.isLoggable(TAG, 3);

    @Override // com.android.systemui.SystemUI
    public void start() {
        boolean enableVolumeUi = this.mContext.getResources().getBoolean(R.bool.enable_volume_ui);
        boolean enableSafetyWarning = this.mContext.getResources().getBoolean(R.bool.enable_safety_warning);
        this.mEnabled = enableVolumeUi || enableSafetyWarning;
        if (this.mEnabled) {
            this.mVolumeComponent = SystemUIFactory.getInstance().createVolumeDialogComponent(this, this.mContext);
            this.mVolumeComponent.setEnableDialogs(enableVolumeUi, enableSafetyWarning);
            putComponent(VolumeComponent.class, getVolumeComponent());
            setDefaultVolumeController();
        }
    }

    private VolumeComponent getVolumeComponent() {
        return this.mVolumeComponent;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.systemui.SystemUI
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.mEnabled) {
            getVolumeComponent().onConfigurationChanged(newConfig);
        }
    }

    @Override // com.android.systemui.SystemUI
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.print("mEnabled=");
        pw.println(this.mEnabled);
        if (this.mEnabled) {
            getVolumeComponent().dump(fd, pw, args);
        }
    }

    private void setDefaultVolumeController() {
        DndTile.setVisible(this.mContext, true);
        if (LOGD) {
            Log.d(TAG, "Registering default volume controller");
        }
        getVolumeComponent().register();
    }
}
