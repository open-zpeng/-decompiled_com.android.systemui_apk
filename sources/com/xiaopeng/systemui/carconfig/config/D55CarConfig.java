package com.xiaopeng.systemui.carconfig.config;

import com.xiaopeng.systemui.carconfig.option.CfcCarOption;
/* loaded from: classes24.dex */
public class D55CarConfig extends CarConfig {
    @Override // com.xiaopeng.systemui.carconfig.config.CarConfig, com.xiaopeng.systemui.carconfig.config.IConfig
    public int isAutoWiperSupport() {
        return CfcCarOption.CFG_RLS_SUPPORT ? 1 : 0;
    }
}
