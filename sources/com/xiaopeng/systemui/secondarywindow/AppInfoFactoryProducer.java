package com.xiaopeng.systemui.secondarywindow;

import com.xiaopeng.systemui.utils.Utils;
/* loaded from: classes24.dex */
public class AppInfoFactoryProducer {
    public static AbstractAppsFactory getFactory() {
        if (Utils.isChineseLanguage()) {
            return new AppsFactoryCN();
        }
        return new AppsFactoryEN();
    }
}
