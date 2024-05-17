package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.content.res.Configuration;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.SysUiServiceProvider;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.policy.ConfigurationController;
import javax.inject.Inject;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class RemoteInputQuickSettingsDisabler implements ConfigurationController.ConfigurationListener {
    @VisibleForTesting
    CommandQueue mCommandQueue;
    private Context mContext;
    private int mLastOrientation;
    @VisibleForTesting
    boolean mRemoteInputActive;
    @VisibleForTesting
    boolean misLandscape;

    @Inject
    public RemoteInputQuickSettingsDisabler(Context context, ConfigurationController configController) {
        this.mContext = context;
        this.mCommandQueue = (CommandQueue) SysUiServiceProvider.getComponent(context, CommandQueue.class);
        this.mLastOrientation = this.mContext.getResources().getConfiguration().orientation;
        configController.addCallback(this);
    }

    public int adjustDisableFlags(int state) {
        if (this.mRemoteInputActive && this.misLandscape) {
            return state | 1;
        }
        return state;
    }

    public void setRemoteInputActive(boolean active) {
        if (this.mRemoteInputActive != active) {
            this.mRemoteInputActive = active;
            recomputeDisableFlags();
        }
    }

    @Override // com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener
    public void onConfigChanged(Configuration newConfig) {
        if (newConfig.orientation != this.mLastOrientation) {
            this.misLandscape = newConfig.orientation == 2;
            this.mLastOrientation = newConfig.orientation;
            recomputeDisableFlags();
        }
    }

    private void recomputeDisableFlags() {
        this.mCommandQueue.recomputeDisableFlags(this.mContext.getDisplayId(), true);
    }
}
