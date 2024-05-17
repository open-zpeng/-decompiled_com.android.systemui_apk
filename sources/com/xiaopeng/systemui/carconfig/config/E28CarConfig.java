package com.xiaopeng.systemui.carconfig.config;

import com.xiaopeng.systemui.carconfig.option.CfcCarOption;
/* loaded from: classes24.dex */
public class E28CarConfig extends CarConfig {
    @Override // com.xiaopeng.systemui.carconfig.config.CarConfig, com.xiaopeng.systemui.carconfig.config.IConfig
    public int isAutoWiperSupport() {
        if (!CfcCarOption.CFG_PACKAGE_3_SUPPORT) {
            return 1;
        }
        return 0;
    }
}
