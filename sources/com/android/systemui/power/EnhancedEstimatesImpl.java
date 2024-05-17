package com.android.systemui.power;

import com.android.settingslib.fuelgauge.Estimate;
import javax.inject.Singleton;
@Singleton
/* loaded from: classes21.dex */
public class EnhancedEstimatesImpl implements EnhancedEstimates {
    @Override // com.android.systemui.power.EnhancedEstimates
    public boolean isHybridNotificationEnabled() {
        return false;
    }

    @Override // com.android.systemui.power.EnhancedEstimates
    public Estimate getEstimate() {
        return null;
    }

    @Override // com.android.systemui.power.EnhancedEstimates
    public long getLowWarningThreshold() {
        return 0L;
    }

    @Override // com.android.systemui.power.EnhancedEstimates
    public long getSevereWarningThreshold() {
        return 0L;
    }

    @Override // com.android.systemui.power.EnhancedEstimates
    public boolean getLowWarningEnabled() {
        return true;
    }
}
