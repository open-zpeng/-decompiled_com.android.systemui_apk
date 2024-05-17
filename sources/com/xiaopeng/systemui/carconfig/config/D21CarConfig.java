package com.xiaopeng.systemui.carconfig.config;

import com.xiaopeng.systemui.carconfig.option.CfcCarOption;
/* loaded from: classes24.dex */
public class D21CarConfig extends CarConfig {
    @Override // com.xiaopeng.systemui.carconfig.config.CarConfig, com.xiaopeng.systemui.carconfig.config.IConfig
    public boolean isAirCleanSupport() {
        return CfcCarOption.CFG_CONFIG_CODE_VALUE > 1;
    }
}
